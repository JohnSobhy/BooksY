package com.john_halaka.booksy.feature_book.use_cases

import android.util.Log
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class GetAllBooks (
    private val repository: BookRepository
) {
    suspend operator fun invoke() : Flow<List<Book>> {
        Log.d("GetAllBooks", "Invoking getAllBooks")
        val booksFlow = repository.getBooksFromJson()
        Log.d("GetAllBooks", "Received books flow: $booksFlow")
        return booksFlow
    }
}