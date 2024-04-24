package com.john_halaka.booksy.feature_book.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: Int,
    val title: String,
    val author: String,
    val category: String,
    val imageUrl: String,
    val description: String,
    val content: String
)
