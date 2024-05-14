package com.john_halaka.booksy.feature_book.use_cases

import android.util.Log
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import com.john_halaka.booksy.feature_book.network.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetAllBooks(
    private val repository: BookRepository,
    private val connectivityObserver: ConnectivityObserver
) {
    suspend operator fun invoke(): Flow<List<Book>> {
        Log.d("GetAllBooks", "Invoking getAllBooks")

        val booksFlow =
            if (connectivityObserver.observe().first() == ConnectivityObserver.Status.Available) {
                Log.d("GetAllBooks", "connection available so fetching from json")
                repository.getBooksFromJson()

            } else {
                Log.d("GetAllBooks", "connection not available so fetching from db")
                repository.getBooksFromDb()
            }
        Log.d("GetAllBooks", "Received books flow: $booksFlow")
        return booksFlow
    }
}