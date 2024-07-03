package com.john_halaka.booksy.feature_book_view.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "link_targets")
data class LinkTarget(
    @PrimaryKey
    val tag: String,
    val destination: Int,
    val bookId: Int
)