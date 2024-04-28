package com.john_halaka.booksy.feature_bookmark.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Int,
    val start: Int,
    val end: Int
)
