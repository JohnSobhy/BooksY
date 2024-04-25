package com.john_halaka.booksy.feature_highlight.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "highlights")

data class Highlight(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val start: Int,
    val end: Int
)

