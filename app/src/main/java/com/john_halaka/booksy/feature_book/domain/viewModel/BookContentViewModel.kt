package com.john_halaka.booksy.feature_book.domain.viewModel

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.john_halaka.booksy.feature_book.data.PreferencesManager
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.use_cases.BookUseCases
import com.john_halaka.booksy.feature_book_view.domain.repository.LinkRepository
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import com.john_halaka.booksy.feature_bookmark.use_cases.BookmarkUseCases
import com.john_halaka.booksy.feature_highlight.domain.model.Highlight
import com.john_halaka.booksy.feature_highlight.use_cases.HighlightUseCases
import com.john_halaka.booksy.feature_search.domain.model.BookFts
import com.john_halaka.booksy.feature_search.domain.model.BookWithSnippet
import com.john_halaka.booksy.ui.presentation.book_content.BookContentEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookContentViewModel @Inject constructor(
    private val bookUseCases: BookUseCases,
    private val highlightUseCases: HighlightUseCases,
    private val preferencesManager: PreferencesManager,
    private val bookmarkUseCases: BookmarkUseCases,
    val linkRepository: LinkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(BookDetailsState())
    val state: State<BookDetailsState> = _state

    private val _searchResults = MutableLiveData<List<BookWithSnippet>>()
    val searchResults = _searchResults

    private val _fontSize = MutableStateFlow(preferencesManager.getFontSize())
    val fontSize: StateFlow<Float> = _fontSize

    private val _fontColor = MutableStateFlow(preferencesManager.getFontColor())
    val fontColor = _fontColor

    private val _fontWeight = MutableStateFlow(preferencesManager.getFontWeight())
    val fontWeight = _fontWeight

    private val _backgroundColor = MutableStateFlow(preferencesManager.getBackgroundColor())
    val backgroundColor = _backgroundColor

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var getAllHighlightsJob: Job? = null
    private var getAllBookmarksJob: Job? = null


    init {
        savedStateHandle.get<Int>("bookId")?.let { bookId ->
            if (bookId != -1) {
                viewModelScope.launch {
                    bookUseCases.getBookById(bookId).also { book ->
                        _state.value = state.value.copy(
                            bookId = book.id,
                            bookTitle = book.title,
                            bookAuthor = book.author,
                            bookCategory = book.category,
                            bookContent = book.content,
                            bookDescription = book.description,
                            imageUrl = book.imageUrl,
                            spannableContent = SpannableString(book.content)
                        )
                        getHighlightsForBook(bookId)
                        getBookmarksForBook(state.value.bookId)
                        Log.d("BookContentViewModel", "spannable content = ${state.value.spannableContent.length}")
                    }
                }
            }
        }
    }

    fun onEvent(event: BookContentEvent) {
        when (event) {
            is BookContentEvent.BackButtonClick -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.NavigateBack)
                }
            }
        }
    }

    fun saveFontSize(fontSize: Float) {
        preferencesManager.setFontSize(fontSize)
        _fontSize.value = fontSize
    }

    fun saveFontColor(color: Int) {
        preferencesManager.setFontColor(color)
        _fontColor.value = color
    }

    fun saveFontWeight(fontWeight: Int) {
        preferencesManager.setFontWeight(fontWeight)
        _fontWeight.value = fontWeight
    }

    fun saveBackgroundColor(backgroundColor: Int) {
        preferencesManager.setBackgroundColor(backgroundColor)
        _backgroundColor.value = backgroundColor
    }

    fun addHighlight(start: Int, end: Int) {
        viewModelScope.launch {
            Log.d("BookContentViewModel", "addHighlight is invoked from {$start} to {$end}")
            val bookId = _state.value.bookId
            val highlight = Highlight(bookId = bookId, start = start, end = end)
            highlightUseCases.addHighlight(highlight)
            Log.d("BookContentViewModel", "highlight is saved from {$start} to {$end}")

            getHighlightsForBook(bookId)
            Log.d("BookContentViewModel", "getHighlightsForBook is invoked")
        }
    }

    fun addBookmark(start: Int, end: Int) {
        val bookId = _state.value.bookId
        val newBookmark = Bookmark(bookId = bookId, start = start, end = end)

        viewModelScope.launch {
            bookmarkUseCases.addBookmark(newBookmark)
            getBookmarksForBook(bookId)
        }
    }


//    fun removeHighlight(start: Int, end: Int) {
//        val bookId = _state.value.bookId
//        val highlight = Highlight(bookId = bookId, start = start, end = end)
//        viewModelScope.launch {
//            Log.d("BookContentViewModel", "Removing highlight from {$start} to {$end}")
//            highlightUseCases.removeHighlight(highlight)
//            getHighlightsForBook(bookId)
//            Log.d("BookContentViewModel", "Updated highlights fetched")
//        }
//    }

    private suspend fun getHighlightsForBook(bookId: Int) {
        getAllHighlightsJob?.cancel()
        try {
            Log.d("BookContentViewModel", "getHighlightsForBook is called")
            getAllHighlightsJob = highlightUseCases.getBookHighlights(bookId).onEach { highlights ->
                _state.value = state.value.copy(
                    bookHighlights = highlights
                )
                Log.d("BookContentViewModel", "getHighlightsForBook is successful $highlights")
            }.launchIn(viewModelScope)
            delay(200)
            _state.value.bookHighlights.forEach { highlight ->
                _state.value.spannableContent.setSpan(
                    BackgroundColorSpan(Color.Yellow.toArgb()),
                    highlight.start,
                    highlight.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            Log.d("BookContentViewModel", "HighlightsForBook is spanned")

        } catch (e: Exception) {
            Log.e("BookContentViewModel", "Error getting Book highlights: ${e.message}")
        }
    }

    private suspend fun getBookmarksForBook(bookId: Int) {
        getAllBookmarksJob?.cancel()
        try {
            Log.d("BookContentViewModel", "getBookmarksForBook is called")
            getAllBookmarksJob = bookmarkUseCases.getBookmarksForBook(bookId).onEach { bookmarks ->
                _state.value = state.value.copy(
                    bookmarks = bookmarks
                )
                Log.d("BookContentViewModel", "getBookmarksForBook is successful $bookmarks")
            }
                .launchIn(viewModelScope)
        } catch (e: Exception) {
            Log.e("BookContentViewModel", "Error getting Book bookmarks: ${e.message}")
        }
    }

    fun getOriginalBook(bookFts: BookFts): LiveData<Book> {
        val result = MutableLiveData<Book>()
        viewModelScope.launch {
            result.value = bookUseCases.getOriginalBook(bookFts)
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("BookContentViewModel", "onCleared is called")
            linkRepository.deleteAllData() // Call a function to delete all data
        }
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

    sealed class UiEvent {
        object NavigateBack : UiEvent()
    }
}



