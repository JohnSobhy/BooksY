package com.john_halaka.booksy.ui.presentation.all_books.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.ui.Screen

@Composable
fun BookItem(
    book: Book,
    navController:NavController,
    modifier: Modifier
) {
    val onClick = {
        navController.navigate(
            Screen.BookContentScreen.route + "?bookId=${book.id}"
        )
    }
    Box(
        modifier = modifier
            .padding(16.dp)
            .clickable (
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(model = book.imageUrl, contentDescription = book.title)
            Text(text = book.title, textAlign = TextAlign.Center)
        }

    }
}