package com.john_halaka.booksy.ui.presentation.book_content

import android.graphics.Paint
import android.os.Build
import android.text.Highlights
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.john_halaka.booksy.NavigationDrawer
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun BookContentScreen(
    viewModel: BookContentViewModel = hiltViewModel(),
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val book = viewModel.openedBook.value
    val fontSize by viewModel.fontSize.collectAsState()
    val yellowPaint = Paint().apply {
        color = Color.Yellow.toArgb()
    }
    val transparentPaint = Paint().apply {
        color = Color.Transparent.toArgb()
    }
    val bookHighlights by viewModel.bookHighlights.observeAsState(emptyList())
    val savedHighlightBuilder = remember {
        mutableStateOf(Highlights.Builder())
    }

    LaunchedEffect(bookHighlights) {
        // Create the highlights
        Log.d("BookContentScreen", "savedHighlights is observed $bookHighlights")
        val builder = Highlights.Builder()
        bookHighlights.forEach { highlight ->
            builder.addRange(yellowPaint, highlight.start, highlight.end)
        }
        savedHighlightBuilder.value = builder
    }

    val (showFontSlider, setShowFontSlider) = remember {
        mutableStateOf(false)
    }

    NavigationDrawer(
        navController = navController,
        drawerState = drawerState,
        scope = scope,
        content = {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(

                        title = {
                        },

                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            })
                            {
                                Icon(
                                    Icons.Filled.Menu,
                                    contentDescription = stringResource(R.string.menu)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                 setShowFontSlider(true)
                            })
                            {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = stringResource(R.string.adjust_font_size)
                                )
                            }

                        }
                    )
                }
            ) { values ->

                Column(
                    modifier = Modifier.padding(values)
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
                                    AndroidView(
                                        factory = { context ->
                                            // Inflate the XML layout
                                            LayoutInflater.from(context)
                                                .inflate(R.layout.text_view_layout, null, false)
                                        },
                                        update = { view ->
                                            // Get the TextView from the inflated layout
                                            val textView =
                                                view.findViewById<TextView>(R.id.text_view)

                                            // Apply the highlights
                                            textView.highlights =
                                                savedHighlightBuilder.value.build()

                                            // Set a custom selection action mode callback
                                            textView.customSelectionActionModeCallback =
                                                object : ActionMode.Callback {
                                                    override fun onCreateActionMode(
                                                        mode: ActionMode,
                                                        menu: Menu
                                                    ): Boolean {
                                                        // Inflate your custom menu
                                                        mode.menuInflater.inflate(
                                                            R.menu.custom_action_menu,
                                                            menu
                                                        )
                                                        // Get the start and end positions of the selected text
                                                        val start = textView.selectionStart
                                                        val end = textView.selectionEnd

                                                        // Check if the selected text overlaps with any highlights
                                                        val isHighlighted =
                                                            bookHighlights.any { highlight ->
                                                                (highlight.start <= start && highlight.end >= start) ||
                                                                        (highlight.start <= end && highlight.end >= end)
                                                            }

                                                        // Change the action_highlight menu item to action_remove_highlight if the text is highlighted
                                                        if (isHighlighted) {
                                                            menu.findItem(R.id.action_highlight).isVisible =
                                                                false
                                                            menu.findItem(R.id.action_remove_highlight).isVisible =
                                                                true
                                                        } else {
                                                            menu.findItem(R.id.action_highlight).isVisible =
                                                                true
                                                            menu.findItem(R.id.action_remove_highlight).isVisible =
                                                                false
                                                        }
                                                        return true
                                                    }

                                                    override fun onPrepareActionMode(
                                                        mode: ActionMode,
                                                        menu: Menu
                                                    ): Boolean {
                                                        // Remove default menu items
                                                        menu.removeItem(android.R.id.cut)
                                                        menu.removeItem(android.R.id.shareText)
                                                        menu.removeItem(android.R.id.paste)
                                                        return true
                                                    }

                                                    override fun onActionItemClicked(
                                                        mode: ActionMode,
                                                        item: MenuItem
                                                    ): Boolean {
                                                        when (item.itemId) {
                                                            R.id.action_highlight -> {
                                                                // Get the start and end positions of the selected text
                                                                val start = textView.selectionStart
                                                                val end = textView.selectionEnd

                                                                if (start != end) {
                                                                    // Add the highlight to the TextView

                                                                    val highLightBuilder =
                                                                        Highlights.Builder()
                                                                            .addRange(
                                                                                yellowPaint,
                                                                                start,
                                                                                end
                                                                            )
                                                                    val highlights =
                                                                        highLightBuilder.build()
                                                                    Log.d(
                                                                        "BookContentScreen",
                                                                        "showing the highlight from {$start} to {$end}"
                                                                    )
                                                                    textView.highlights = highlights
                                                                    // Save the highlight to the database
                                                                    Log.d(
                                                                        "BookContentScreen",
                                                                        "saving the highlight to db from {$start} to {$end}"
                                                                    )
                                                                    viewModel.addHighlight(
                                                                        start,
                                                                        end
                                                                    )
                                                                    return true
                                                                }
                                                            }

                                                            R.id.action_remove_highlight -> {

                                                            }

                                                            R.id.action_bookmark -> {
                                                                // action
//                                                val start = textView.selectionStart
//                                                val end = textView.selectionEnd
//                                                if (start != end) {
//                                                    val bookmark = textView.text.subSequence(start, end).toString()
//                                                    viewModel.addBookmark(bookmark)
                                                                return true
                                                            }
                                                        }
                                                        return false
                                                    }

                                                    override fun onDestroyActionMode(mode: ActionMode) {
                                                        // Cleanup if necessary
                                                    }
                                                }
                                            // Update text and text size
                                            textView.text = book.content
                                            textView.textSize = fontSize
                                        }
                                    )
                                }
                            }
                        }
                        if (showFontSlider) {
                            AlertDialog(
                                onDismissRequest = { setShowFontSlider(false) },
                                title = { Text("Adjust Font Size") },
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
                                        Text("OK")
                                    }
                                }
                            )
                        }
                    }
                }
            }
            }
        }
    )
}

