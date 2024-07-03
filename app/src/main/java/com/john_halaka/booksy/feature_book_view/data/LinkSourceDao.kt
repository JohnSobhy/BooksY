package com.john_halaka.booksy.feature_book_view.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.john_halaka.booksy.feature_book_view.domain.model.LinkSource

@Dao
interface LinkSourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(linkSource: LinkSource)

    @Query("SELECT * FROM link_sources WHERE bookId = :bookId AND tag = :tag")
    suspend fun findSourcesByKey(bookId: Int, tag: String): List<LinkSource>

    @Query("DELETE FROM link_sources WHERE bookId = :bookId")
    suspend fun deleteByBookId(bookId: Int)

    @Query("DELETE FROM link_sources")
    suspend fun deleteAllSources()
}