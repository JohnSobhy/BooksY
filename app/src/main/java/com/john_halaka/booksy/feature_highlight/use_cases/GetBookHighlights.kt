package com.john_halaka.booksy.feature_highlight.use_cases

import com.john_halaka.booksy.feature_highlight.domain.model.Highlight
import com.john_halaka.booksy.feature_highlight.domain.repository.HighlightRepository
import kotlinx.coroutines.flow.Flow

class GetBookHighlights (
   private val repository: HighlightRepository
) {
    suspend operator fun invoke(bookId: Int): Flow<List<Highlight>> {
        return repository.getBookHighlights(bookId)
    }
}