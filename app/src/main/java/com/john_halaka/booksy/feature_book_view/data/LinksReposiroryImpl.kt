package com.john_halaka.booksy.feature_book_view.data

import android.util.Log
import com.john_halaka.booksy.feature_book_view.domain.model.LinkSource
import com.john_halaka.booksy.feature_book_view.domain.model.LinkTarget
import com.john_halaka.booksy.feature_book_view.domain.repository.LinkRepository

class LinkRepositoryImpl(
    private val linkSourceDao: LinkSourceDao,
    private val linkTargetDao: LinkTargetDao
) : LinkRepository {
    override suspend fun insertSource(linkSource: LinkSource) {
        linkSourceDao.insert(linkSource)
    }

    override suspend fun insertTarget(linkTarget: LinkTarget) {
        linkTargetDao.insert(linkTarget)
    }

    override suspend fun findSourcesByKey(bookId: Int, key: String): List<LinkSource> {
        return linkSourceDao.findSourcesByKey(bookId, key)
    }

    override suspend fun findTargetByKey(bookId: Int, key: String): LinkTarget? {
        Log.d("LinkRepositoryImpl", "findTargetByKey is called, key is $key")
        return linkTargetDao.findTargetByKey(bookId, key)
    }

    override suspend fun deleteSourcesByBookId(bookId: Int) {
        linkSourceDao.deleteByBookId(bookId)
    }

    override suspend fun deleteTargetsByBookId(bookId: Int) {
        linkTargetDao.deleteByBookId(bookId)
    }
    override suspend fun deleteAllData() {
        linkSourceDao.deleteAllSources()
        linkTargetDao.deleteAllTargets()
    }

}



//suspend fun parseBookAndInsertLinks(bookId: Int, bookContent: String, linkRepository: LinkRepository) {
//    // Parse the book content here and find the link sources and targets
//    // This is just a placeholder and needs to be replaced with your actual parsing logic
//    val linkSourcesAndTargets = parseBookContent(bookContent)
//
//    for ((tag, linkSourceText, linkTargetText) in linkSourcesAndTargets) {
//        val linkSource = LinkSource(0, bookId, linkSourceText, 0, tag)
//        val linkTarget = LinkTarget(0, bookId, linkTargetText, tag)
//
//        val sourceId = linkRepository.insertLinkSource(linkSource).toInt()
//        val targetId = linkRepository.insertLinkTarget(linkTarget).toInt()
//
//        linkRepository.updateLinkSource(sourceId, targetId)
//    }
//}
