package com.john_halaka.booksy.feature_highlight.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.john_halaka.booksy.feature_highlight.domain.model.Highlight
import kotlinx.coroutines.flow.Flow

@Dao
interface HighlightDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(highlight: Highlight)

    @Delete
    suspend fun delete(highlight: Highlight)

    @Query("SELECT * FROM highlights WHERE bookId = :bookId")
    fun getHighlightsForBook(bookId: Int): Flow<List<Highlight>>
}