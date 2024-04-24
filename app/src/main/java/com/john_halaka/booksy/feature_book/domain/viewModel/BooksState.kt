package com.john_halaka.booksy.feature_book.domain.viewModel

import com.john_halaka.booksy.feature_book.domain.model.Book

data class BooksState(
    val allBooks: List<Book> = emptyList(),
    val favoriteBook: List<Book> = emptyList(),
    val book: Book = Book(
        id = -1,
        title = "",
        description = "",
        author = "",
        content = "",
        imageUrl = "",
        category = ""
    )
)
