package com.john_halaka.booksy.feature_book.domain.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.use_cases.BookUseCases
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import com.john_halaka.booksy.feature_bookmark.use_cases.BookmarkUseCases
import com.john_halaka.booksy.feature_search.domain.model.BookFts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookUseCases: BookUseCases,
    private val bookmarkUseCases: BookmarkUseCases
) : ViewModel(){
    private val _state = mutableStateOf(BooksState())
    val state: State<BooksState> = _state

    private var getAllBooksJob: Job? = null
    private var getAllBookmarksJob: Job? = null

    init {
        viewModelScope.launch {
            getAllBooks()
            getAllBookmarks()
        }

    }






    fun getBookById(bookId: Int): LiveData<Book> {
        val result = MutableLiveData<Book>()
        viewModelScope.launch {
            result.value = bookUseCases.getBookById(bookId)
        }
        return result
    }
    fun getTextSnippetForBookmark(bookmark: Bookmark): LiveData<String> {
        val result = MutableLiveData<String>()
        viewModelScope.launch {
            val book = bookUseCases.getBookById(bookmark.bookId)
            val bookContent = book.content

            // Extract the text snippet based on bookmark start and end positions
            val snippet = bookContent.substring(bookmark.start, bookmark.end)
            result.value = snippet
        }
        return result
    }
    fun removeBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            Log.d("BookViewModel", "removeBookmark is called $bookmark")
            bookmarkUseCases.deleteBookmark(bookmark)
            getAllBookmarks()
        }
    }


            private suspend fun getAllBooks(){
        getAllBooksJob?.cancel()

        try {
            getAllBooksJob = bookUseCases.getAllBooks().
            onEach {books->
                _state.value = state.value.copy(
                    allBooks = books
                )
            }
                .launchIn(viewModelScope)
        }catch (e: Exception) {
            Log.e("BookViewModel", "Error getting Books: ${e.message}")
        }
    }

    private suspend fun getAllBookmarks(){
        getAllBookmarksJob?.cancel()

        try {
            Log.e("BookViewModel", "getBookmarks is called")
            getAllBookmarksJob = bookmarkUseCases.getAllBookmarks().
            onEach {bookmarks->
                _state.value = state.value.copy(
                    bookmarks = bookmarks
                )
                Log.e("BookViewModel", "bookmarks are $bookmarks ")
            }
                .launchIn(viewModelScope)

        }catch (e: Exception) {
            Log.e("BookViewModel", "Error getting Bookmarks: ${e.message}")
        }
    }
}