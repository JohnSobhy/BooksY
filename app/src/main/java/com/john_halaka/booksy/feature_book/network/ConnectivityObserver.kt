package com.john_halaka.booksy.feature_book.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe() : Flow<Status>

    enum class Status{
        Available, Unavailable, Lost
    }
}