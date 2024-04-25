package com.john_halaka.booksy.feature_book.domain.repository

import com.john_halaka.booksy.feature_book.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooksFromJson(): Flow<List<Book>>
    suspend fun getBookById(id: Int): Book
    suspend fun updateBookContent(book: Book)
}