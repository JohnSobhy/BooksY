package com.john_halaka.booksy.ui.presentation.book_content.componants

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel


@Composable
fun BookContent(
    book: Book,
    modifier: Modifier,
    backgroundColor: Int,
    fontSize: Float,
    fontColor: Int,
    fontWeight: Int,
    viewModel: BookContentViewModel,
    context: Context
) {
    Column(
        modifier
            .background(Color(backgroundColor))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier.size(200.dp)
                )
                SelectionContainer {
                    Text(
                        text = book.title,
                        fontSize = fontSize.sp,
                        color = Color(fontColor),
                        fontWeight = FontWeight(fontWeight),
                        lineHeight = fontSize.sp * 1.5f
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        item {
                            XMLView(viewModel, context = context)
                        }
                    }
                }

            }
        }
    }
}