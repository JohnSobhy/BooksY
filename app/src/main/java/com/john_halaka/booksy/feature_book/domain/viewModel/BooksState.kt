package com.john_halaka.booksy.feature_book.domain.viewModel

import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark

data class BooksState(
    val allBooks: List<Book> = emptyList(),
    val favoriteBooks: List<Book> = emptyList(),
    val bookmarks: List<Bookmark> = emptyList(),
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
