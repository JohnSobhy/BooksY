package com.john_halaka.booksy.feature_book.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_highlight.data.data_source.HighlightDao
import com.john_halaka.booksy.feature_highlight.domain.model.Highlight

@Database(
    entities = [Book::class, Highlight::class],
    version = 1,
    exportSchema = false)
abstract class BooksYDatabase : RoomDatabase() {
    abstract val bookDao: BookDao
    abstract val highlightDao: HighlightDao

    companion object {
        const val DATABASE_NAME = "booksy_db"
    }
}
