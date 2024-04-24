package com.john_halaka.booksy.feature_book.domain.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.john_halaka.booksy.feature_book.data.PreferencesManager
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.use_cases.BookUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookContentViewModel @Inject constructor(
    private val bookUseCases: BookUseCases,
    private val preferencesManager: PreferencesManager,
    savedStateHandle: SavedStateHandle

) : ViewModel() {

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
    init {
        savedStateHandle.get<Int>("bookId")?.let {bookId->
            if (bookId != -1) {
                viewModelScope.launch {
                    bookUseCases.getBookById(bookId).also {book ->
                        _openedBook.value = book
                    }
                }
            }
        }
    }







    fun saveFontSize(fontSize: Float) {
        preferencesManager.saveFontSize(fontSize)
        _fontSize.value = fontSize
    }
}
