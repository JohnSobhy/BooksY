package com.john_halaka.booksy.ui.presentation.book_content

import android.graphics.Paint
import android.os.Build
import android.text.Highlights
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
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.john_halaka.booksy.R

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun BookContentScreen(
    viewModel: BookContentViewModel = hiltViewModel(),
) {
    val book = viewModel.openedBook.value
    val fontSize by viewModel.fontSize.collectAsState()

    val (showFontSlider, setShowFontSlider) = remember {
        mutableStateOf(false)
    }

    val greenPaint = Paint().apply {
        color = Color.Green.toArgb()
    }
    val redPaint = Paint().apply {
        color = Color.Red.toArgb()
    }

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
                Text(text = book.title,
                    fontSize = fontSize.sp,
                    lineHeight = fontSize.sp * 1.5f)
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
                                LayoutInflater.from(context).inflate(R.layout.text_view_layout, null, false)
                            },
                            update = { view ->
                                // Get the TextView from the inflated layout
                                val textView = view.findViewById<TextView>(R.id.text_view)

                                val builder = Highlights.Builder()
                                    .addRange(greenPaint, 1, 3)
                                    .addRange(redPaint, 3, 5)
                               // val highlights = builder.build()

                                //textView.highlights = highlights

                                // Set a custom selection action mode callback
                                textView.customSelectionActionModeCallback = object : ActionMode.Callback {
                                    override fun onCreateActionMode(
                                        mode: ActionMode,
                                        menu: Menu
                                    ): Boolean {
                                        // Inflate your custom menu
                                        mode.menuInflater.inflate(R.menu.custom_action_menu, menu)
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
                                        // Handle your menu item clicks here
                                        when (item.itemId) {
                                            R.id.action_highlight -> {
                                                // Perform your action
                                                // Get the start and end positions of the selected text
                                                val start = textView.selectionStart
                                                val end = textView.selectionEnd

                                                if (start != end) {
                                                    // Add the highlight to the TextView
                                                    val builder = Highlights.Builder()
                                                        .addRange(greenPaint, start, end)
                                                    val highlights = builder.build()

                                                    textView.highlights = highlights
                                                    return true
                                                }
                                            }

                                            R.id.action_bookmark -> {
                                                // Perform your action
                                                return true
                                            }
                                        }
                                        return false
                                    }

                                    override fun onDestroyActionMode(mode: ActionMode) {
                                        // Cleanup if necessary
                                    }
                                }



                                    // Update the TextView's text and text size
                                textView.text = book.content
                                textView.textSize = fontSize
                            }
                        )
                    }
                }

                // Add a button that opens a dialog for adjusting the font size
                Button(onClick = { setShowFontSlider(true) }) {
                    Text("Adjust Font Size")
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

