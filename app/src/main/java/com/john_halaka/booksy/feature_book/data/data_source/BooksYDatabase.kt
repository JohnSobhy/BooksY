package com.john_halaka.booksy.feature_book.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.john_halaka.booksy.feature_book.domain.model.Book

@Database(entities = [Book::class], version = 1, exportSchema = false)
abstract class BooksYDatabase : RoomDatabase() {
    abstract val bookDao: BookDao

    companion object {
        const val DATABASE_NAME = "booksy_db"
    }
}
