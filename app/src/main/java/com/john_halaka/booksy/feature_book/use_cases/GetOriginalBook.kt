package com.john_halaka.booksy.feature_book.use_cases

import android.util.Log
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import com.john_halaka.booksy.feature_search.domain.model.BookFts

class GetOriginalBook (
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookFts: BookFts) : Book {

        val book = repository.getOriginalBook(bookFts)
        Log.d("GetOriginalBook", "Received original book: $book")
        return book
    }
}
