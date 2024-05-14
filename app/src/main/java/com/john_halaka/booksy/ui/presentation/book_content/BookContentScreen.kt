package com.john_halaka.booksy.ui.presentation.book_content

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.ui.presentation.book_content.componants.BookContent
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
//@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun BookContentScreen(
    viewModel: BookContentViewModel = hiltViewModel(),
    navController: NavController,
) {
    val context = LocalContext.current
    val book = Book(
        id = viewModel.state.value.bookId,
        title = viewModel.state.value.bookTitle,
        content = viewModel.state.value.bookContent,
        category = viewModel.state.value.bookCategory,
        author = viewModel.state.value.bookAuthor,
        imageUrl = viewModel.state.value.imageUrl,
        description = viewModel.state.value.bookDescription
    )
    val fontSize by viewModel.fontSize.collectAsState()
    val fontColor by viewModel.fontColor.collectAsState()
    val fontWeight by viewModel.fontWeight.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()


    val (showFontSlider, setShowFontSlider) = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is BookContentViewModel.UiEvent.NavigateBack -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(backgroundColor),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {
                },

                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onEvent(BookContentEvent.BackButtonClick)
                    })
                    {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        setShowFontSlider(true)
                    })
                    {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_font_size),
                            contentDescription = stringResource(R.string.adjust_font_size)
                        )
                    }
                }
            )
        }
    ) { values ->
        BookContent(
            book = book,
            modifier = Modifier.padding(values),
            backgroundColor = backgroundColor,
            fontSize = fontSize,
            fontColor = fontColor,
            fontWeight = fontWeight,
            viewModel = viewModel,
            context = context
        )
        if (showFontSlider) {
            AlertDialog(
                onDismissRequest = { setShowFontSlider(false) },
                title = { Text(stringResource(id = R.string.adjust_font_size)) },
                text = {
                    Slider(
                        value = fontSize,
                        onValueChange = { newSize -> viewModel.saveFontSize(newSize) },
                        valueRange = 12f..42f,
                        steps = 24
                    )
                },
                confirmButton = {
                    Button(onClick = { setShowFontSlider(false) }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        }
    }
}

fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}
