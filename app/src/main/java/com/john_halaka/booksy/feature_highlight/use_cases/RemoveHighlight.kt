package com.john_halaka.booksy.feature_highlight.use_cases

import android.util.Log
import com.john_halaka.booksy.feature_highlight.domain.model.Highlight
import com.john_halaka.booksy.feature_highlight.domain.repository.HighlightRepository

class RemoveHighlight (
    private val repository: HighlightRepository
) {
    suspend operator fun invoke(highlight: Highlight) {
        Log.d("removeHighlightUseCase", "removeHighlightUseCase is invoked")

        repository.deleteHighlight(highlight)
    }
}
