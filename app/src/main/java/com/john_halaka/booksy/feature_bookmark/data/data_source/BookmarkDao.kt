package com.john_halaka.booksy.feature_bookmark.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert
    suspend fun insertBookmark(bookmark: Bookmark)
    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId")
     fun getBookmarksForBook(bookId: Int): Flow<List<Bookmark>>
    @Query("SELECT * FROM bookmarks")
     fun getAllBookmarks(): Flow<List<Bookmark>>
}
