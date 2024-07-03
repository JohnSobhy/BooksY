package com.john_halaka.booksy.feature_book_view.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.john_halaka.booksy.feature_book_view.domain.model.LinkTarget

@Dao
interface LinkTargetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(linkTarget: LinkTarget)

    @Query("SELECT * FROM link_targets WHERE bookId = :bookId AND tag = :tag")
    suspend fun findTargetByKey(bookId: Int, tag: String): LinkTarget?

    @Query("DELETE FROM link_targets WHERE bookId = :bookId")
    suspend fun deleteByBookId(bookId: Int)


    @Query("DELETE FROM link_targets")
    suspend fun deleteAllTargets()

}