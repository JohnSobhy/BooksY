package com.john_halaka.booksy.feature_bookmark.use_cases

import android.util.Log
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import com.john_halaka.booksy.feature_bookmark.domain.repository.BookmarkRepository

class AddBookmark (
    private val repository: BookmarkRepository
){
    suspend operator fun invoke(bookmark: Bookmark) {
        Log.d("addBookmarkUseCase", "addBookmarkUseCase is invoked")

        repository.insertBookmark(bookmark)
    }
}