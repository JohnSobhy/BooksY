package com.john_halaka.booksy.feature_book.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

const val jsonUrl = "https://raw.githubusercontent.com/JohnSobhy/BooksY/master/app/src/main/booksy_api/Booksy.json"

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