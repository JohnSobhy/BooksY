package com.john_halaka.booksy.feature_book.data.repository
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.john_halaka.booksy.feature_book.data.data_source.BookDao
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import com.john_halaka.booksy.feature_book.network.JsonFetcher
import com.john_halaka.booksy.feature_book.network.jsonUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


class BookRepositoryImpl (
    private val bookDao: BookDao,
    private val jsonFetcher: JsonFetcher
) : BookRepository {

    override suspend fun getBooksFromJson(): Flow<List<Book>> {
        Log.d("BookRepositoryImpl", "Fetching JSON")
        val json =
            jsonFetcher.fetchJson(jsonUrl)
        Log.d("BookRepositoryImpl", "Received JSON: $json")

        val booksFromJson = parseJson(json)
        Log.d("BookRepositoryImpl", "Parsed books: $booksFromJson")

        val booksFromDb = bookDao.getAll().first()

        val newBooks = booksFromJson.filter { jsonBook ->
            booksFromDb.none { dbBook -> dbBook.id == jsonBook.id }
        }

        //if you need to update the existing books
//        val existingBooks = booksFromJson.filter { jsonBook ->
//            booksFromDb.any { dbBook -> dbBook.id == jsonBook.id }
//        }

        if (newBooks.isNotEmpty()) {
            try {
                bookDao.insertAll(booksFromJson)
                Log.d("BookRepositoryImpl", "Inserted books into database")
            } catch (e: Exception) {
                Log.e("BookRepositoryImpl", "Error inserting books into database", e)
            }
        }

        //update the books here
//        if (existingBooks.isNotEmpty()) {
//            try {
//                existingBooks.forEach { bookDao.updateBookContent(it) }
//                Log.d("BookRepositoryImpl", "Updated existing books in database")
//            } catch (e: Exception) {
//                Log.e("BookRepositoryImpl", "Error updating existing books in database", e)
//            }
//        }
            return bookDao.getAll()
        }

    override suspend fun getBookById(id: Int): Book {
        return bookDao.loadBookById(id)
    }

    override suspend fun updateBookContent(book: Book) {
        bookDao.updateBookContent(book)
    }

    private fun parseJson(json: String?): List<Book> {
        // Parse the JSON into a list of Book objects
        val gson = Gson()
        val bookType = object : TypeToken<List<Book>>() {}.type
        return gson.fromJson(json, bookType)
    }
}
