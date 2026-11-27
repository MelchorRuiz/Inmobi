package com.example.inmobi.core.util

import com.example.inmobi.BuildConfig

object UrlResolver {
    fun resolveImageUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "${BuildConfig.BASE_URL}$url"
        }
    }
}
