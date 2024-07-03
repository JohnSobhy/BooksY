package com.john_halaka.booksy.feature_book.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book_view.data.LinkSourceDao
import com.john_halaka.booksy.feature_book_view.data.LinkTargetDao
import com.john_halaka.booksy.feature_book_view.domain.model.LinkSource
import com.john_halaka.booksy.feature_book_view.domain.model.LinkTarget
import com.john_halaka.booksy.feature_bookmark.data.data_source.BookmarkDao
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import com.john_halaka.booksy.feature_highlight.data.data_source.HighlightDao
import com.john_halaka.booksy.feature_highlight.domain.model.Highlight
import com.john_halaka.booksy.feature_search.data.data_source.BookFtsDao
import com.john_halaka.booksy.feature_search.domain.model.BookFts

@Database(
    entities = [
        Book::class,
        Highlight::class,
        BookFts::class,
        Bookmark::class,
        LinkSource::class,
        LinkTarget::class
               ],
    version = 1,
    exportSchema = false)
abstract class BooksYDatabase : RoomDatabase() {
    abstract val bookDao: BookDao
    abstract val highlightDao: HighlightDao
    abstract val bookFtsDao: BookFtsDao
    abstract val bookmarkDao: BookmarkDao
    abstract val linkSourceDao: LinkSourceDao
    abstract val linkTargetDao: LinkTargetDao

    companion object {
        const val DATABASE_NAME = "booksy_db"
    }
}
