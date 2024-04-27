package com.john_halaka.booksy.ui.presentation.book_content.componants

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Highlights
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.ui.presentation.book_content.showToast



@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun XMLView(
    viewModel: BookContentViewModel,
    context: Context
){

    val book = viewModel.openedBook.value
    val fontSize by viewModel.fontSize.collectAsState()
    val fontColor by viewModel.fontColor.collectAsState()
    val fontWeight by viewModel.fontWeight.collectAsState()

    val yellowPaint = Paint().apply {
        color = Color.Yellow.toArgb()
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


    AndroidView(
        factory = { currentContext ->
            // Inflate the XML layout
            LayoutInflater.from(currentContext)
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
                                            .addRange(yellowPaint, start, end)
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
                                    viewModel.addHighlight(start, end)
                                    return true
                                }
                            }

                            R.id.action_remove_highlight -> {

                            }

                            R.id.action_bookmark -> {
                                val start = textView.selectionStart
                                val end = textView.selectionEnd

                                if (start != end) {
                                    // Check if the selected text range is already bookmarked
                                    val existingBookmark =
                                        viewModel.bookBookmarks.value?.firstOrNull { bookmark ->
                                            bookmark.start == start && bookmark.end == end
                                        }

                                    if (existingBookmark == null) {
                                        // Create a new bookmark and insert it into the database
                                        viewModel.addBookmark(start, end)
                                        // Provide feedback to the user (e.g., toast message)
                                        showToast(context = context, "Bookmark added successfully!")
                                    } else {
                                        // The selected text range is already bookmarked
                                        showToast(
                                            context = context,
                                            "This text is already bookmarked."
                                        )
                                    }
                                }
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
            textView.setTextColor(fontColor)
            if (fontWeight == FontWeight.Normal.weight) {
                textView.typeface = Typeface.DEFAULT
            } else textView.typeface = Typeface.DEFAULT_BOLD
        }
    )
}