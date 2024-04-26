package com.john_halaka.booksy.feature_bookmark.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Int, // ID of the book associated with this bookmark
    val start: Int, // Start position of the selected text
    val end: Int // End position of the selected text
    // Add any other relevant properties here
)
