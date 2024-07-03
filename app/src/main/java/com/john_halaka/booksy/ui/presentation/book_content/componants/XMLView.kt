package com.john_halaka.booksy.ui.presentation.book_content.componants

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable

import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel

import com.john_halaka.booksy.ui.presentation.book_content.showToast


@Composable
fun XMLView(
    viewModel: BookContentViewModel,
    context: Context
) {

    val state by viewModel.state
    val fontSize by viewModel.fontSize.collectAsState()
    val fontColor by viewModel.fontColor.collectAsState()
    val fontWeight by viewModel.fontWeight.collectAsState()


    // Create a SpannableString from the book content
    val spannableString = remember {
        mutableStateOf(state.spannableContent)
    }
    // Apply dynamic highlight spans
    // spannableString.value = applyHighlightSpans(bookContent, bookHighlights)


    AndroidView(

        factory = { currentContext ->
            // Inflate the XML layout
            val view = LayoutInflater.from(currentContext)
                .inflate(R.layout.text_view_layout, null, false)
            return@AndroidView view

        },
        update = { view ->
            // Get the TextView from the inflated layout
            val textView = view.findViewById<TextView>(R.id.bookTextView)

            spannableString.value = state.spannableContent

            textView.text = spannableString.value

            updateTextViewStyle(textView, fontSize, fontColor, fontWeight)


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
                        menu.removeItem(android.R.id.selectAll)

                        return true
                    }

                    override fun onActionItemClicked(
                        mode: ActionMode,
                        item: MenuItem
                    ): Boolean {
                        when (item.itemId) {
                            android.R.id.copy -> {
                                val selectedText =
                                    textView.text.subSequence(
                                        textView.selectionStart,
                                        textView.selectionEnd
                                    )
                                Log.d("BookContentScreen", "Selected text length: ${selectedText.length}")

                                val truncatedText = selectedText.take(150)
                                Log.d("BookContentScreen", "truncated text length: ${truncatedText.length}")
                                // Handle copying or sharing the truncatedText
                                copyTextWithSignature(truncatedText, context)

                                return true

                            }

                            R.id.action_highlight -> {
                                // Get the start and end positions of the selected text
                                val start = textView.selectionStart
                                val end = textView.selectionEnd
                                if (start != end) {
                                    // Add the highlight to the TextView
                                    spannableString.value.setSpan(
                                        BackgroundColorSpan(Color.Yellow.toArgb()),
                                        start,
                                        end,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )

                                    textView.text = spannableString.value
                                    viewModel.addHighlight(start, end)
                                    return true
                                }
                            }
//                            R.id.action_remove_highlight->{
//                                val start = textView.selectionStart
//                                val end = textView.selectionEnd
//                                if (start != end) {
//                                    viewModel.removeHighlight(start,end)
//                                    textView.text = spannableString.value
//                                }
//                                return true
//                            }

                            R.id.action_bookmark -> {
                                val start = textView.selectionStart
                                val end = textView.selectionEnd

                                if (start != end) {
                                    // Check if the selected text range is already bookmarked
                                    val existingBookmark =
                                        state.bookmarks.firstOrNull { bookmark ->
                                            bookmark.start == start && bookmark.end == end
                                        }

                                    if (existingBookmark == null) {
                                        // Create a new bookmark and insert it into the database
                                        viewModel.addBookmark(start, end)
                                        // Provide feedback to the user (e.g., toast message)
                                        showToast(
                                            context = context,
                                            context.getString(R.string.bookmark_added_successfully)
                                        )
                                    } else {
                                        // The selected text range is already bookmarked
                                        showToast(
                                            context = context,
                                            context.getString(R.string.this_text_is_already_bookmarked)
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
        }
    )


}

//private fun applyHighlightSpans(
//    bookContent: String,
//    highlights: List<Highlight>
//): SpannableString {
//    val spannableString = SpannableString(bookContent)
//    highlights.forEach { highlight ->
//        spannableString.setSpan(
//            BackgroundColorSpan(Color.Yellow.toArgb()),
//            highlight.start,
//            highlight.end,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//    }
//    return spannableString
//}

private fun updateTextViewStyle(
    textView: TextView,
    fontSize: Float,
    fontColor: Int,
    fontWeight: Int
) {
    textView.textSize = fontSize
    textView.setTextColor(fontColor)
    textView.setTypeface(
        null,
        if (fontWeight == FontWeight.Normal.weight) Typeface.NORMAL else Typeface.BOLD
    )
}

// Custom copy action
fun copyTextWithSignature(text: CharSequence, context: Context) {
    val signature = " ©BooksY"
    val modifiedText = "$text$signature"
    Log.d("BookContentScreen", "the copy function is called with text length ${text.length}")

    // Set the modified text as the primary clip
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Modified Text", modifiedText)
    clipboardManager.setPrimaryClip(clipData)

     // Show a toast if the selected text exceeds 150 characters
    if (text.length < 150) {
        showToast(context, context.getString(R.string.text_copied_to_clipboard))
    }else {
        showToast(context, context.getString(R.string.cannot_copy_more_than_150_characters))
    }


}
