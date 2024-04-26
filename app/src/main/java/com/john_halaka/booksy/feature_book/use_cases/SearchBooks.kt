package com.john_halaka.booksy.feature_book.use_cases

import android.util.Log
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import com.john_halaka.booksy.feature_search.domain.model.BookFts
import kotlinx.coroutines.flow.Flow

class SearchBooks (
    private val repository: BookRepository
    ) {
        suspend operator fun invoke(query: String) : Flow<List<BookFts>> {

            val result = repository.searchBooks(query)
            Log.d("searchBooks", "books found: $result")
            return result
        }
    }