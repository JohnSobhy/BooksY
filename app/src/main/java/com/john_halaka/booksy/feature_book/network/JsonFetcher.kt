package com.john_halaka.booksy.feature_book.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class JsonFetcher {
    private val client = OkHttpClient()

    suspend fun fetchJson(url: String): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                return@withContext response.body?.string()
            }
        }
    }
}