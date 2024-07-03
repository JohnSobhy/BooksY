package com.john_halaka.booksy.feature_book_view.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "link_sources")
data class LinkSource(
    //@PrimaryKey(autoGenerate = true) val id: Int = 0,
    val start: Int,
    val end: Int,
    @PrimaryKey
    val tag: String,
    val bookId: Int
)
