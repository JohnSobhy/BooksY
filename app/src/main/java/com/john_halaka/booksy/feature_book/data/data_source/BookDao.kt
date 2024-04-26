package com.john_halaka.booksy.feature_book.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_search.domain.model.BookFts
import kotlinx.coroutines.flow.Flow


@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAll(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :bookId")
   suspend fun getBookById(bookId: Int): Book

    @Transaction
   suspend fun getOriginalBook(bookFts: BookFts): Book {
        return getBookById(bookFts.rowid)
   }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<Book>)

    @Update
    suspend fun updateBookContent(book: Book)

}