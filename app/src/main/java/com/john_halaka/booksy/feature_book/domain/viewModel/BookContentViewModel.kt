package com.john_halaka.booksy.feature_book.domain.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.john_halaka.booksy.feature_book.data.PreferencesManager
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.use_cases.BookUseCases
import com.john_halaka.booksy.feature_highlight.domain.model.Highlight
import com.john_halaka.booksy.feature_highlight.use_cases.HighlightUseCases
import com.john_halaka.booksy.feature_search.domain.model.BookFts
import com.john_halaka.booksy.feature_search.domain.model.BookWithSnippet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookContentViewModel @Inject constructor(
    private val bookUseCases: BookUseCases,
    private val highlightUseCases: HighlightUseCases,
    private val preferencesManager: PreferencesManager,

    savedStateHandle: SavedStateHandle

) : ViewModel() {

    private val _searchResults = MutableLiveData<List<BookWithSnippet>>()
    val searchResults = _searchResults

    private val _fontSize = MutableStateFlow(preferencesManager.loadFontSize())
    val fontSize: StateFlow<Float> = _fontSize

    private val _openedBook = mutableStateOf(
        Book(
            id = -1,
            title = "Unknown",
            content = "Unknown",
            author = "unknown",
            category = "unknown",
            imageUrl = "Unknown",
            description = "Unknown"
        )
    )
    val openedBook = _openedBook
    private val _bookHighlights = MutableLiveData<List<Highlight>>()
    val bookHighlights = _bookHighlights

    init {
        savedStateHandle.get<Int>("bookId")?.let { bookId ->
            if (bookId != -1) {
                viewModelScope.launch {
                    bookUseCases.getBookById(bookId).also { book ->
                        _openedBook.value = book
                        getHighlightsForBook(openedBook.value.id)
                    }
                }
            }
        }
    }

    fun saveFontSize(fontSize: Float) {
        preferencesManager.saveFontSize(fontSize)
        _fontSize.value = fontSize
    }

    fun addHighlight(start: Int, end: Int) {

        viewModelScope.launch {
            Log.d("BookContentViewModel", "addHighlight is invoked from {$start} to {$end}")
            val bookId = _openedBook.value.id
            val highlight = Highlight(bookId = bookId, start = start, end = end)
            highlightUseCases.addHighlight(highlight)
            Log.d("BookContentViewModel", "highlight is saved from {$start} to {$end}")

            getHighlightsForBook(bookId)
            Log.d("BookContentViewModel", "getHighlightsForBook is invoked")
        }
    }

    fun removeHighlight(start: Int, end: Int) {
        val bookId = _openedBook.value.id
        val highlight = Highlight(bookId = bookId, start = start, end = end)
        viewModelScope.launch {
            highlightUseCases.removeHighlight(highlight)
            getHighlightsForBook(bookId)
        }
    }

    private fun getHighlightsForBook(bookId: Int) {
        viewModelScope.launch {
            highlightUseCases.getBookHighlights(bookId).collect { bookHighlights ->
                _bookHighlights.value = bookHighlights

            }
        }
    }

    fun getOriginalBook(bookFts: BookFts): LiveData<Book> {
        val result = MutableLiveData<Book>()
        viewModelScope.launch {
            result.value = bookUseCases.getOriginalBook(bookFts)
        }
        return result
    }


    fun searchBooks(query: String) {
        Log.d("BookContentViewModel", "searchBooks invoked")
        viewModelScope.launch {
            bookUseCases.searchBooks(query).collect { books ->
                val resultsWithSnippets = books.map { book ->
                    // Find the position of the query in the book's content
                    val index = book.content.indexOf(query)

                    // Extract a snippet of text around this position
                    val start = (index - 50).coerceAtLeast(0)
                    val end = (index + 50).coerceAtMost(book.content.length)
                    val snippet = book.content.substring(start, end)

                    // Create a new data class that includes the book and the snippet
                    BookWithSnippet(book, snippet)
                }

                _searchResults.value = resultsWithSnippets
                Log.d("BookContentViewModel", "searchBooks result is $resultsWithSnippets")
            }
        }
    }
}


