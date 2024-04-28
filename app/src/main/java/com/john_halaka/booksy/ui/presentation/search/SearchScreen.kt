package com.john_halaka.booksy.ui.presentation.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.ui.presentation.all_books.components.BookItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: BookContentViewModel = hiltViewModel()
) {
    val searchQuery = remember { mutableStateOf(TextFieldValue()) }
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val onValueChange = { newValue: TextFieldValue ->
        searchQuery.value = newValue
        viewModel.searchBooks(newValue.text)
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.search))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    })
                    {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { values ->
        Column(modifier = Modifier.padding(values)) {
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
                            BookItem(
                                book = it,
                                navController = navController,
                                modifier = Modifier.weight(2f)
                            )

                            Text(
                                text = context.getString(R.string.text, result.snippet),
                                modifier = Modifier
                                    .weight(3f)
                                    .padding(16.dp),
                                fontSize = 20.sp
                            )
                        }
                    }

                }
            }
        }
    }
}
