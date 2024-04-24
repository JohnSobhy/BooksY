package com.john_halaka.booksy.feature_book.data.repository
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.john_halaka.booksy.feature_book.data.data_source.BookDao
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import com.john_halaka.booksy.feature_book.network.ImageFetcher
import com.john_halaka.booksy.feature_book.network.JsonFetcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream



class BookRepositoryImpl (
    private val bookDao: BookDao,
    private val jsonFetcher: JsonFetcher
) : BookRepository {

    override suspend fun getBooksFromJson(): Flow<List<Book>> {
        Log.d("BookRepositoryImpl", "Fetching JSON")
        val json =
            jsonFetcher.fetchJson("https://raw.githubusercontent.com/JohnSobhy/My-Todo/master/Booksy.json")
        Log.d("BookRepositoryImpl", "Received JSON: $json")

        val books = parseJson(json)
            Log.d("BookRepositoryImpl", "Parsed books: $books")

        try {
            bookDao.insertAll(books)
            Log.d("BookRepositoryImpl", "Inserted books into database")
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error inserting books into database", e)
        }

        return bookDao.getAll()
    }

    override suspend fun getBookById(id: Int): Book {
        return bookDao.loadBookById(id)
    }

    private fun parseJson(json: String?): List<Book> {
        // Parse the JSON into a list of Book objects
        val gson = Gson()
        val bookType = object : TypeToken<List<Book>>() {}.type
        return gson.fromJson(json, bookType)
    }
}
