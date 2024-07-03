package com.john_halaka.booksy.feature_book_view.domain.repository

import com.john_halaka.booksy.feature_book_view.domain.model.LinkSource
import com.john_halaka.booksy.feature_book_view.domain.model.LinkTarget

interface LinkRepository {
    suspend fun insertSource(linkSource: LinkSource)
    suspend fun insertTarget(linkTarget: LinkTarget)
    suspend fun findSourcesByKey(bookId: Int, key: String): List<LinkSource>
    suspend fun findTargetByKey(bookId: Int, key: String): LinkTarget?
    suspend fun deleteSourcesByBookId(bookId: Int)
    suspend fun deleteTargetsByBookId(bookId: Int)

    suspend fun deleteAllData()
}
