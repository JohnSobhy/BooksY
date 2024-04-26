package com.john_halaka.booksy.feature_book.data.repository
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.john_halaka.booksy.feature_book.data.data_source.BookDao
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.repository.BookRepository
import com.john_halaka.booksy.feature_book.network.JsonFetcher
import com.john_halaka.booksy.feature_book.network.jsonUrl
import com.john_halaka.booksy.feature_search.data.data_source.BookFtsDao
import com.john_halaka.booksy.feature_search.domain.model.BookFts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


class BookRepositoryImpl (
    private val bookDao: BookDao,
    private val bookFtsDao: BookFtsDao,
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

        if (newBooks.isNotEmpty()) {
            try {
                bookDao.insertAll(booksFromJson)
                Log.d("BookRepositoryImpl", "Inserted books into database")
            } catch (e: Exception) {
                Log.e("BookRepositoryImpl", "Error inserting books into database", e)
            }
            try {
                val booksFts: List<BookFts> = booksFromJson.map {book->
                    BookFts(
                        book.id,
                        book.title,
                        book.content
                    )
                }
                bookFtsDao.insertFts(booksFts)
                Log.d("BookRepositoryImpl", "Inserted books into fts")
            } catch (e: Exception) {
                Log.e("BookRepositoryImpl", "Error inserting books into fts", e)
            }
        }
            return bookDao.getAll()
        }

    override suspend fun insertAll(books: List<Book>) {
        TODO("Not yet implemented")
    }

    override suspend fun getBookById(id: Int): Book {
        return bookDao.getBookById(id)
    }

    override suspend fun getOriginalBook(bookFts: BookFts): Book {
        return bookDao.getOriginalBook(bookFts)
    }

    override suspend fun updateBookContent(book: Book) {
        bookDao.updateBookContent(book)
    }

    override suspend fun searchBooks(query: String): Flow<List<BookFts>> {
        val booksFts= bookFtsDao.searchBooks(query)
        return booksFts
    }

    private fun parseJson(json: String?): List<Book> {
        // Parse the JSON into a list of Book objects
        val gson = Gson()
        val bookType = object : TypeToken<List<Book>>() {}.type
        return gson.fromJson(json, bookType)
    }
}
