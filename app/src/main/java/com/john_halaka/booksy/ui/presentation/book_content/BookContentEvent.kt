package com.john_halaka.booksy.ui.presentation.book_content

sealed class BookContentEvent {
    object BackButtonClick : BookContentEvent()
}