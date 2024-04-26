package com.john_halaka.booksy.feature_bookmark.use_cases

data class BookmarkUseCases(
    val addBookmark: AddBookmark,
    val deleteBookmark: DeleteBookmark,
    val getBookmarksForBook: GetBookmarksForBook,
    val getAllBookmarks: GetAllBookmarks
)
