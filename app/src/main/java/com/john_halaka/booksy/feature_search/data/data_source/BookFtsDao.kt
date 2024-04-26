package com.john_halaka.booksy.feature_search.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.john_halaka.booksy.feature_search.domain.model.BookFts
import kotlinx.coroutines.flow.Flow

@Dao
interface BookFtsDao {
    @Query("SELECT rowid, * FROM booksFts WHERE booksFts MATCH :query")
    fun searchBooks(query: String): Flow<List<BookFts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFts(booksFts: List<BookFts>)
}
