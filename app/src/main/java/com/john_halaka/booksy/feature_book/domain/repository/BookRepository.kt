package com.john_halaka.booksy.feature_book.domain.repository

import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_search.domain.model.BookFts
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooksFromJson(): Flow<List<Book>>
    suspend fun getBooksFromDb(): Flow<List<Book>>
    suspend fun insertAll(books: List<Book>)
    suspend fun getBookById(id: Int): Book
    suspend fun getOriginalBook(bookFts: BookFts) : Book
    suspend fun updateBookContent(book: Book)
    suspend fun searchBooks(query: String): Flow<List<BookFts>>
}