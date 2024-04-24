package com.john_halaka.booksy.ui

sealed class Screen(val route: String){
    object HomeScreen: Screen("home_screen")
    object BookContentScreen: Screen("book_content_screen")
    object BooksScreen: Screen("books_screen")
    object FavoriteBooksScreen: Screen("fav_books_screen")
    object BookmarksScreen: Screen("bookmarks_screen")
    object RecentReadsScreen: Screen("recent_reads_screen")
    object TrendingBooksScreen: Screen("trending_books_screen")
    object SearchScreen: Screen("search_screen")
    object SettingsScreen: Screen("settings_screen")
    object ProfileScreen: Screen("profile_screen")

}