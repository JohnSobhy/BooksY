package com.john_halaka.booksy.feature_search.domain.model

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4
@Entity(tableName = "booksFts")
data class BookFts(

    @PrimaryKey
    val rowid: Int,
    val title: String,
    val content: String
)


data class BookWithSnippet(
    val book: BookFts,
    val snippet: String
)