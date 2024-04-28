package com.john_halaka.booksy.feature_bookmark.data.repository

import com.john_halaka.booksy.feature_bookmark.data.data_source.BookmarkDao
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import com.john_halaka.booksy.feature_bookmark.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

class BookmarkRepositoryImpl(
  private val dao: BookmarkDao
) : BookmarkRepository {
    override suspend fun insertBookmark(bookmark: Bookmark) {
        dao.insertBookmark(bookmark)
    }
    override suspend fun deleteBookmark(bookmark: Bookmark) {
        dao.deleteBookmark(bookmark)
    }
    override  fun getBookmarksForBook(bookId: Int): Flow<List<Bookmark>> {
      return  dao.getBookmarksForBook(bookId)
    }
    override  fun getAllBookmarks(): Flow<List<Bookmark>> {
       return dao.getAllBookmarks()
    }
}