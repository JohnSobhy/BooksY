package com.john_halaka.booksy.ui.presentation.bookmarks


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.viewModel.BookViewModel
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import com.john_halaka.booksy.ui.presentation.all_books.components.BookItem
import com.john_halaka.booksy.ui.presentation.book_content.showToast
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun BookmarksScreen(
    navController: NavController,
    viewModel: BookViewModel = hiltViewModel(),
) {
    val state by viewModel.state
    val bookmarks = state.bookmarks
    val (showDeleteBookmarkDialog, setShowDeleteBookmarkDialog) = remember { mutableStateOf(false) }
    val (bookmarkToDelete, setBookmarkToDelete) = remember { mutableStateOf<Bookmark?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = stringResource(R.string.bookmarks_icon)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.bookmarks))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    })
                    {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { values ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {

            // Show a loading indicator while fetching bookmarks
            // If no bookmarks are saved, display a message
            if (bookmarks.isEmpty()) {
                var showProgress by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(1000)
                    showProgress = false
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (showProgress) {
                        CircularProgressIndicator(color = Color.Blue)
                    } else
                        Text(
                            text = stringResource(R.string.you_have_no_bookmarks_yet),
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                }

            } else {

                LazyColumn {
                    items(bookmarks) { bookmark ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            val textSnippet by viewModel.getTextSnippetForBookmark(bookmark)
                                .observeAsState()
                            val book by viewModel.getBookById(bookmark.bookId).observeAsState()
                            book?.let {
                                Row(
                                    Modifier.fillMaxWidth()
                                ) {
                                    BookItem(
                                        it,
                                        navController,
                                        Modifier.weight(2f)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .weight(3f)
                                            .fillMaxSize()

                                    ) {
                                        // Display the bookmarked text
                                        textSnippet?.let { text ->
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    text = text,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp),
                                                    textAlign = TextAlign.Right
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        setBookmarkToDelete(bookmark)
                                                        setShowDeleteBookmarkDialog(true)
                                                    }, modifier = Modifier.align(BottomEnd)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = stringResource(R.string.delete_bookmark)
                                                    )

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showDeleteBookmarkDialog) {
            AlertDialog(
                onDismissRequest = { setShowDeleteBookmarkDialog(false) },
                title = { Text(stringResource(R.string.warning)) },
                text = { Text(stringResource(R.string.delete_bookmark)) },
                confirmButton = {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            bookmarkToDelete?.let { viewModel.removeBookmark(it) }
                            setShowDeleteBookmarkDialog(false)
                            setBookmarkToDelete(null)
                            showToast(
                                context = context,
                                context.getString(R.string.the_bookmark_is_deleted)
                            )
                        }) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            setShowDeleteBookmarkDialog(false)
                        }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

    }
}