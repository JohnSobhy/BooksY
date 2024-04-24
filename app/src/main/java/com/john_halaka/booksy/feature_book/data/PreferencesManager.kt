package com.john_halaka.booksy.feature_book.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("BooksY", Context.MODE_PRIVATE)

    fun saveFontSize(fontSize: Float) {
        with(sharedPreferences.edit()) {
            putFloat("fontSize", fontSize)
            apply()
        }
    }

    fun loadFontSize(): Float {
        return sharedPreferences.getFloat("fontSize", 16f)
    }
}
