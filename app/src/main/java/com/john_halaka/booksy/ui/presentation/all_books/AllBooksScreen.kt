package com.john_halaka.booksy.ui.presentation.all_books

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.john_halaka.booksy.R
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
    val context = LocalContext.current
    val showNetworkError by viewModel.showNetworkError.observeAsState(initial = false)
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        modifier = modifier
    ) { values ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(values)
        ) {
            items(booksList) { book ->
                BookItem(book, navController, modifier)

            }
        }

        // Show the Snackbar when showNetworkError is true
        LaunchedEffect(showNetworkError) {
            if (showNetworkError) {
                val result = snackbarHostState.showSnackbar(
                    message = context.getString(R.string.please_check_your_network_connection),
                    actionLabel = context.getString(R.string.retry)
                )
                if (result == SnackbarResult.ActionPerformed) {
                    navController.navigate(Screen.HomeScreen.route)
                }
            }
        }
    }
}



