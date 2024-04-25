package com.john_halaka.booksy.feature_highlight.data.repository

import com.john_halaka.booksy.feature_highlight.data.data_source.HighlightDao
import com.john_halaka.booksy.feature_highlight.domain.model.Highlight
import com.john_halaka.booksy.feature_highlight.domain.repository.HighlightRepository
import kotlinx.coroutines.flow.Flow

class HighlightRepositoryImpl (
   private val highlightDao: HighlightDao
) : HighlightRepository {
    override suspend fun insertHighlight(highlight: Highlight) {
        highlightDao.insert(highlight)
    }

    override suspend fun removeHighlight(highlight: Highlight) {
        highlightDao.delete(highlight)
    }

    override suspend fun getBookHighlights(bookId: Int): Flow<List<Highlight>> {
        return highlightDao.getHighlightsForBook(bookId)
    }

}