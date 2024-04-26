package com.john_halaka.booksy.feature_bookmark.domain.repository


import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    suspend fun insertBookmark(bookmark: Bookmark)

    suspend fun deleteBookmark(bookmark: Bookmark)

     fun getBookmarksForBook(bookId: Int): Flow<List<Bookmark>>

     fun getAllBookmarks(): Flow<List<Bookmark>>
}