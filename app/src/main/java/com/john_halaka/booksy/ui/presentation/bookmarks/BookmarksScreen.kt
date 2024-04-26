package com.john_halaka.booksy.ui.presentation.bookmarks

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.john_halaka.booksy.feature_book.domain.viewModel.BookViewModel
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import com.john_halaka.booksy.ui.presentation.all_books.components.BookItem
import com.john_halaka.booksy.ui.presentation.book_content.showToast
import kotlinx.coroutines.delay

@Composable

fun BookmarksScreen(
    navController: NavController,
    viewModel: BookViewModel = hiltViewModel(),
    context: Context
) {
    val state by viewModel.state
    val bookmarks = state.bookmarks
    val (showDeleteBookmarkDialog, setShowDeleteBookmarkDialog) = remember { mutableStateOf(false) }
    val (bookmarkToDelete, setBookmarkToDelete) = remember { mutableStateOf<Bookmark?>(null) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                        text = "You have no bookmarks yet.",
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
                        val textSnippet by viewModel.getTextSnippetForBookmark(bookmark).observeAsState()
                        val book by viewModel.getBookById(bookmark.bookId).observeAsState()
                        book?.let {
                            Row (
                                Modifier.fillMaxWidth()
                            ){
                            BookItem(
                                it,
                                navController,
                                Modifier.weight(2f)
                            )
                            Column (
                                modifier = Modifier.weight(3f)
                                    .fillMaxSize()

                            ){
                                // Display the bookmarked text
                                textSnippet?.let { text ->
                                    Row (modifier = Modifier.fillMaxWidth()) {
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
                                            }, modifier = Modifier.align(Alignment.BottomEnd)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete Bookmark"
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
            title = { Text("Warning") },
            text = { Text("Delete bookmark?") },
            confirmButton = {
                Button(onClick = {
                    bookmarkToDelete?.let { viewModel.removeBookmark(it) }
                    setShowDeleteBookmarkDialog(false)
                    setBookmarkToDelete(null)
                    showToast(context = context, "The bookmark is deleted")
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { setShowDeleteBookmarkDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }


}