package com.john_halaka.booksy.ui.presentation.search

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.ui.presentation.all_books.components.BookItem

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: BookContentViewModel = hiltViewModel(),
    context: Context
) {
    val searchQuery = remember { mutableStateOf(TextFieldValue()) }
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val onValueChange = { newValue: TextFieldValue ->
        searchQuery.value = newValue
        viewModel.searchBooks(newValue.text)
    }

    Column {
        TextField(
            value = searchQuery.value,
            onValueChange = onValueChange,
            label = { Text(text = stringResource(id = R.string.search)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            },

            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        LazyColumn {


            items(searchResults) { result ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(5f)
                ) {
                    val book by viewModel.getOriginalBook(result.book).observeAsState()
                    book?.let {
                        Text(
                            text = context.getString(R.string.text, result.snippet),
                            modifier = Modifier
                                .weight(3f)
                                .padding(16.dp),
                            fontSize = 20.sp
                        )

                        BookItem(
                            book = it,
                            navController = navController,
                            modifier = Modifier.weight(2f)
                        )
                    }
                }

            }
        }
    }
}
