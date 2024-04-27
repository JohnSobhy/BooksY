package com.john_halaka.booksy.feature_book.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.john_halaka.booksy.R

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("BooksY", Context.MODE_PRIVATE)


    fun setFontSize(fontSize: Float) {
        with(sharedPreferences.edit()) {
            putFloat("fontSize", fontSize)
            apply()
        }
    }

    fun getFontSize(): Float {
        return sharedPreferences.getFloat("fontSize", 16f)
    }

    fun getFontColor(): Int {
        return sharedPreferences.getInt("font_color", Color.Black.toArgb()) // Default font color: Black
    }

    fun setFontColor(color: Int) {
        sharedPreferences.edit().putInt("font_color", color).apply()
    }

    fun getFontWeight() :Int{
        return sharedPreferences.getInt("font_weight", FontWeight.Normal.weight)
    }

    fun setFontWeight(fontWeight: Int){
        sharedPreferences.edit().putInt("font_weight", fontWeight).apply()

    }


    fun getBackgroundColor(): Int {
        return sharedPreferences.getInt("background_color", Color.White.toArgb()) // Default background color: White
    }

    fun setBackgroundColor(color: Int) {
        sharedPreferences.edit().putInt("background_color", color).apply()
    }

}

