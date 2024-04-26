package com.john_halaka.booksy.feature_book.use_cases

data class BookUseCases (
    val getAllBooks: GetAllBooks,
    val getBookById: GetBookById,
    val getOriginalBook: GetOriginalBook,
    val insertAllBooks: InsertAllBooks,
    val searchBooks: SearchBooks
)