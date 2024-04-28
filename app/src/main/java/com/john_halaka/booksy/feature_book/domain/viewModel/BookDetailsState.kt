package com.john_halaka.booksy.feature_book.domain.viewModel

import android.text.SpannableString
import com.john_halaka.booksy.feature_bookmark.domain.model.Bookmark
import com.john_halaka.booksy.feature_highlight.domain.model.Highlight

data class BookDetailsState (
    val bookId: Int= -1,
    val bookTitle : String = "",
    val bookAuthor: String = "",
    val bookCategory: String = "",
    val imageUrl: String = "",
    val bookDescription : String = "",
    val bookContent: String ="",
    val bookHighlights: List<Highlight> = emptyList(),
    val bookmarks: List<Bookmark> = emptyList(),
    val spannableContent: SpannableString = SpannableString(bookContent)


)