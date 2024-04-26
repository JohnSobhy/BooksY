package com.john_halaka.booksy.ui.presentation.all_books

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.john_halaka.booksy.feature_book.domain.viewModel.BookViewModel
import com.john_halaka.booksy.ui.Screen
import com.john_halaka.booksy.ui.presentation.all_books.components.BookItem

@Composable
fun AllBooksScreen(
    navController: NavController,
    viewModel: BookViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val state by viewModel.state
    val booksList = state.allBooks



    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(16.dp)
    ) {
        items(booksList) { book ->
            BookItem(book, navController, modifier)

        }
    }
}


