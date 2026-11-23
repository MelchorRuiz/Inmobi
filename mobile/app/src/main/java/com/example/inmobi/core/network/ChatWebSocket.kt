package com.example.inmobi.core.network

import android.content.Context
import android.util.Log
import com.example.inmobi.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

object ChatWebSocket {

    enum class ConnectionState {
        DISCONNECTED, CONNECTING, CONNECTED, RECONNECTING
    }

    private const val TAG = "ChatWebSocket"
    private const val MAX_RECONNECT_DELAY = 30_000L
    private const val BASE_RECONNECT_DELAY = 1_000L

    private lateinit var appContext: Context
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<InboundWsMessage>(extraBufferCapacity = 64)
    val incomingMessages: SharedFlow<InboundWsMessage> = _incomingMessages.asSharedFlow()

    private val _inAppNotifications = MutableSharedFlow<InAppNotification>(extraBufferCapacity = 16)
    val inAppNotifications: SharedFlow<InAppNotification> = _inAppNotifications.asSharedFlow()

    private val senderCache = mutableMapOf<Int, SenderInfo>()
    private val chatToOtherUser = mutableMapOf<Int, SenderInfo>()

    private val reconnectAttempts = AtomicInteger(0)
    private val shouldReconnect = AtomicBoolean(false)

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun connect() {
        if (_connectionState.value == ConnectionState.CONNECTED || _connectionState.value == ConnectionState.CONNECTING) {
            return
        }

        val token = TokenManager.getAccessToken()
        if (token == null) {
            Log.w(TAG, "No access token available, skipping WebSocket connection")
            return
        }

        shouldReconnect.set(true)
        reconnectAttempts.set(0)
        doConnect(token)
    }

    private fun doConnect(token: String) {
        _connectionState.value = if (reconnectAttempts.get() > 0) {
            ConnectionState.RECONNECTING
        } else {
            ConnectionState.CONNECTING
        }

        val wsUrl = buildWsUrl()
        Log.d(TAG, "Attempting to connect to WebSocket at: $wsUrl")
        Log.d(TAG, "Token available: ${token.take(10)}...")
        
        val request = Request.Builder()
            .url(wsUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected successfully, sending auth")
                val authMessage = WsAuthMessage(type = "auth", token = token)
                val authJson = moshi.adapter(WsAuthMessage::class.java).toJson(authMessage)
                webSocket.send(authJson)
                _connectionState.value = ConnectionState.CONNECTED
                reconnectAttempts.set(0)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received: $text")
                handleMessage(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code $reason")
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code $reason")
                _connectionState.value = ConnectionState.DISCONNECTED
                scheduleReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}", t)
                response?.let {
                    Log.e(TAG, "Response code: ${it.code}")
                    Log.e(TAG, "Response message: ${it.message}")
                    Log.e(TAG, "Response headers: ${it.headers}")
                }
                _connectionState.value = ConnectionState.DISCONNECTED
                scheduleReconnect()
            }
        })
    }

    fun disconnect() {
        shouldReconnect.set(false)
        webSocket?.close(1000, "User logged out")
        webSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
        senderCache.clear()
        chatToOtherUser.clear()
    }

    fun updateSenderCache(chatId: Int, otherUserId: Int, otherUserName: String?, otherUserAvatar: String?) {
        chatToOtherUser[chatId] = SenderInfo(otherUserName, otherUserAvatar)
        senderCache[otherUserId] = SenderInfo(otherUserName, otherUserAvatar)
    }

    private fun handleMessage(text: String) {
        try {
            val jsonAdapter = moshi.adapter(Map::class.java)
            val map = jsonAdapter.fromJson(text) as? Map<*, *> ?: return
            val type = map["type"] as? String ?: return

            when (type) {
                "new_message" -> {
                    val payloadAdapter = moshi.adapter(WsNewMessagePayload::class.java)
                    val payload = payloadAdapter.fromJson(text) ?: return
                    val message = InboundWsMessage.NewMessage(payload)
                    _incomingMessages.tryEmit(message)
                    emitInAppNotification(payload)
                }
                else -> {
                    _incomingMessages.tryEmit(InboundWsMessage.Unknown(text))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing WebSocket message", e)
        }
    }

    private fun emitInAppNotification(payload: WsNewMessagePayload) {
        val senderInfo = senderCache[payload.senderId] ?: chatToOtherUser[payload.chatId]
        val notification = InAppNotification(
            chatId = payload.chatId,
            senderName = senderInfo?.fullName ?: "Usuario",
            senderAvatarUrl = senderInfo?.avatarUrl,
            preview = payload.content
        )
        _inAppNotifications.tryEmit(notification)
    }

    private fun scheduleReconnect() {
        if (!shouldReconnect.get() || TokenManager.getAccessToken() == null) {
            return
        }

        val attempt = reconnectAttempts.getAndIncrement()
        val delayMs = (BASE_RECONNECT_DELAY * (1 shl attempt)).coerceAtMost(MAX_RECONNECT_DELAY)

        Log.d(TAG, "Scheduling reconnect in ${delayMs}ms (attempt $attempt)")
        _connectionState.value = ConnectionState.RECONNECTING

        scope.launch {
            delay(delayMs)
            if (shouldReconnect.get() && TokenManager.getAccessToken() != null) {
                doConnect(TokenManager.getAccessToken()!!)
            }
        }
    }

    private fun buildWsUrl(): String {
        val base = BuildConfig.BASE_URL
        return base
            .replaceFirst("https://", "wss://")
            .replaceFirst("http://", "ws://")
            .trimEnd('/') + "/chats/ws/"
    }
}
