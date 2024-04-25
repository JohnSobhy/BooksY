package com.john_halaka.booksy.feature_book.domain.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.john_halaka.booksy.feature_book.use_cases.BookUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookUseCases: BookUseCases
) : ViewModel(){
    private val _state = mutableStateOf(BooksState())
    val state: State<BooksState> = _state

    private var getAllBooksJob: Job? = null

    init {
        viewModelScope.launch {
            getAllBooks()
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
            e.printStackTrace()
        }
    }
}