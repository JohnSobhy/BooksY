package com.john_halaka.booksy.feature_book.use_cases

import android.util.Log
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository

class InsertAllBooks (
    private val repository: BookRepository
) {
    suspend operator fun invoke(books: List<Book>)  {
        Log.d("GetBookById", "Invoking")
       val insertedBooks = repository.insertAll(books)
        Log.d("InsertAllBooks", "books Inserted to database $insertedBooks")

    }
}
