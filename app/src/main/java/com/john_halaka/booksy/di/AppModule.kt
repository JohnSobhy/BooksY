package com.john_halaka.booksy.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.john_halaka.booksy.feature_book.data.PreferencesManager
import com.john_halaka.booksy.feature_book.data.data_source.BookDao
import com.john_halaka.booksy.feature_book.data.data_source.BooksYDatabase
import com.john_halaka.booksy.feature_book.data.repository.BookRepositoryImpl
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import com.john_halaka.booksy.feature_book.network.ImageFetcher
import com.john_halaka.booksy.feature_book.network.JsonFetcher
import com.john_halaka.booksy.feature_book.use_cases.BookUseCases
import com.john_halaka.booksy.feature_book.use_cases.GetAllBooks
import com.john_halaka.booksy.feature_book.use_cases.GetBookById
import dagger.Binds
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
    fun provideBookRepository(
        db: BooksYDatabase,
        context: Context,
        imageFetcher: ImageFetcher,
        jsonFetcher: JsonFetcher
    ): BookRepository{
        return BookRepositoryImpl(db.bookDao, jsonFetcher)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application) : Context = application.applicationContext

    @Provides
    @Singleton
    fun provideBookUseCases(repository: BookRepository): BookUseCases {
        return BookUseCases(
            getAllBooks = GetAllBooks(repository),
            getBookById = GetBookById(repository)
        )
    }

    @Provides
    @Singleton
    fun provideImageFetcher(@ApplicationContext context: Context): ImageFetcher {
        return ImageFetcher(context)
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