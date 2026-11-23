from fastapi import WebSocket, WebSocketDisconnect
import jwt
from sqlalchemy.orm import Session

from app.database import SessionLocal
from app.models.user import User
from app.security import decode_access_token


class ConnectionManager:
    def __init__(self):
        self.active_connections: dict[int, WebSocket] = {}

    async def connect(self, user_id: int, websocket: WebSocket):
        self.active_connections[user_id] = websocket

    def disconnect(self, user_id: int):
        if user_id in self.active_connections:
            del self.active_connections[user_id]

    async def send_to_user(self, user_id: int, message: dict):
        if user_id in self.active_connections:
            websocket = self.active_connections[user_id]
            try:
                await websocket.send_json(message)
                return True
            except Exception:
                self.disconnect(user_id)
        return False

    async def authenticate(self, websocket: WebSocket) -> int | None:
        await websocket.accept()
        try:
            data = await websocket.receive_json()
            if data.get("type") != "auth":
                await websocket.close(code=4000, reason="expected auth message")
                return None

            token = data.get("token")
            if not token:
                await websocket.close(code=4000, reason="missing token")
                return None

            payload = decode_access_token(token)
            user_id = int(payload["sub"])
            return user_id
        except (jwt.PyJWTError, KeyError, TypeError, ValueError):
            await websocket.close(code=4001, reason="invalid token")
            return None
        except Exception:
            await websocket.close(code=4000, reason="authentication failed")
            return None


manager = ConnectionManager()
