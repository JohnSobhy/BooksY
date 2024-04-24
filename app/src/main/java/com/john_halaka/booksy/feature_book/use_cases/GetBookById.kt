package com.john_halaka.booksy.feature_book.use_cases

import android.util.Log
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class GetBookById (
    private val repository: BookRepository
) {
    suspend operator fun invoke(id: Int) : Book {
        Log.d("GetBookById", "Invoking")
        val book = repository.getBookById(id)
        Log.d("GetBookById", "Received book: $book")
        return book
    }
}
