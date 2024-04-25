package com.john_halaka.booksy.feature_highlight.domain.repository

import com.john_halaka.booksy.feature_highlight.domain.model.Highlight
import kotlinx.coroutines.flow.Flow

interface HighlightRepository {
    suspend fun insertHighlight(highlight: Highlight)
    suspend fun removeHighlight(highlight: Highlight)

    suspend fun getBookHighlights(bookId: Int): Flow<List<Highlight>>
}