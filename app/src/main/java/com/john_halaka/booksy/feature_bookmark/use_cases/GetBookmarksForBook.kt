package com.john_halaka.booksy.feature_bookmark.use_cases

import android.util.Log
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import com.john_halaka.booksy.feature_bookmark.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

class GetBookmarksForBook (
    private val repository: BookmarkRepository
){
    suspend operator fun invoke(bookId: Int) : Flow<List<Bookmark>>{
        Log.d("GetBookmarksForBookUseCase", "GetBookmarksForBookUseCase is invoked")

        return repository.getBookmarksForBook(bookId)
    }
}