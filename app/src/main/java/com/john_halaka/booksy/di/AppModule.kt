package com.john_halaka.booksy.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.john_halaka.booksy.feature_book.data.PreferencesManager
import com.john_halaka.booksy.feature_book.data.data_source.BookDao
import com.john_halaka.booksy.feature_book.data.data_source.BooksYDatabase
import com.john_halaka.booksy.feature_book.data.repository.BookRepositoryImpl
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import com.john_halaka.booksy.feature_book.network.ConnectivityObserver
import com.john_halaka.booksy.feature_book.network.ConnectivityObserverImpl
import com.john_halaka.booksy.feature_book.network.JsonFetcher
import com.john_halaka.booksy.feature_book.use_cases.BookUseCases
import com.john_halaka.booksy.feature_book.use_cases.GetAllBooks
import com.john_halaka.booksy.feature_book.use_cases.GetBookById
import com.john_halaka.booksy.feature_book.use_cases.GetOriginalBook
import com.john_halaka.booksy.feature_book.use_cases.InsertAllBooks
import com.john_halaka.booksy.feature_book.use_cases.SearchBooks
import com.john_halaka.booksy.feature_book_view.data.LinkRepositoryImpl
import com.john_halaka.booksy.feature_book_view.data.LinkSourceDao
import com.john_halaka.booksy.feature_book_view.data.LinkTargetDao
import com.john_halaka.booksy.feature_book_view.domain.repository.LinkRepository
import com.john_halaka.booksy.feature_bookmark.data.repository.BookmarkRepositoryImpl
import com.john_halaka.booksy.feature_bookmark.domain.repository.BookmarkRepository
import com.john_halaka.booksy.feature_bookmark.use_cases.AddBookmark
import com.john_halaka.booksy.feature_bookmark.use_cases.BookmarkUseCases
import com.john_halaka.booksy.feature_bookmark.use_cases.DeleteBookmark
import com.john_halaka.booksy.feature_bookmark.use_cases.GetAllBookmarks
import com.john_halaka.booksy.feature_bookmark.use_cases.GetBookmarksForBook
import com.john_halaka.booksy.feature_highlight.data.data_source.HighlightDao
import com.john_halaka.booksy.feature_highlight.data.repository.HighlightRepositoryImpl
import com.john_halaka.booksy.feature_highlight.domain.repository.HighlightRepository
import com.john_halaka.booksy.feature_highlight.use_cases.AddHighlight
import com.john_halaka.booksy.feature_highlight.use_cases.GetBookHighlights
import com.john_halaka.booksy.feature_highlight.use_cases.HighlightUseCases
import com.john_halaka.booksy.feature_highlight.use_cases.RemoveHighlight
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BooksYDatabase {
        return Room.databaseBuilder(
            context,
            BooksYDatabase::class.java, "database-name"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBookDao(database: BooksYDatabase): BookDao {
        return database.bookDao
    }

    @Provides
    @Singleton
    fun highlightDao(database: BooksYDatabase): HighlightDao {
        return database.highlightDao
    }

    @Provides
    @Singleton
    fun provideBookRepository(
        db: BooksYDatabase,
        jsonFetcher: JsonFetcher,

        ): BookRepository {
        return BookRepositoryImpl(db.bookDao, db.bookFtsDao, jsonFetcher)
    }

    @Provides
    @Singleton
    fun provideHighlightRepository(db: BooksYDatabase): HighlightRepository {
        return HighlightRepositoryImpl(db.highlightDao)
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(db: BooksYDatabase): BookmarkRepository {
        return BookmarkRepositoryImpl(db.bookmarkDao)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideBookUseCases(
        repository: BookRepository,
        connectivityObserver: ConnectivityObserver
        ): BookUseCases {
        return BookUseCases(
            getAllBooks = GetAllBooks(repository, connectivityObserver),
            getBookById = GetBookById(repository),
            insertAllBooks = InsertAllBooks(repository),
            searchBooks = SearchBooks(repository),
            getOriginalBook = GetOriginalBook(repository)
        )
    }

    @Provides
    @Singleton
    fun provideBookmarkUseCases(repository: BookmarkRepository): BookmarkUseCases {
        return BookmarkUseCases(
            getAllBookmarks = GetAllBookmarks(repository),
            getBookmarksForBook = GetBookmarksForBook(repository),
            addBookmark = AddBookmark(repository),
            deleteBookmark = DeleteBookmark(repository)
        )
    }

    @Provides
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
        return ConnectivityObserverImpl(context)
    }

    @Provides
    @Singleton
    fun provideHighlightUseCases(repository: HighlightRepository): HighlightUseCases {
        return HighlightUseCases(
            addHighlight = AddHighlight(repository),
            getBookHighlights = GetBookHighlights(repository),
            removeHighlight = RemoveHighlight(repository)

        )
    }


    @Provides
    @Singleton
    fun provideLinkSourceDao(database: BooksYDatabase): LinkSourceDao {
        return database.linkSourceDao
    }

    @Provides
    @Singleton
    fun provideLinkTargetDao(database: BooksYDatabase): LinkTargetDao {
        return database.linkTargetDao
    }

    @Provides
    @Singleton
    fun provideLinkRepository(
        linkSourceDao: LinkSourceDao,
        linkTargetDao: LinkTargetDao
    ): LinkRepository {
        return LinkRepositoryImpl(linkSourceDao, linkTargetDao)
    }

    @Provides
    @Singleton
    fun provideJsonFetcher(): JsonFetcher {
        return JsonFetcher()
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

}