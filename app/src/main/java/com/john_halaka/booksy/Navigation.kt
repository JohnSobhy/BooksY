package com.john_halaka.booksy

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.ui.Screen
import com.john_halaka.booksy.ui.presentation.book_content.BookContentScreen
import com.john_halaka.booksy.ui.presentation.home.HomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(
            route = Screen.HomeScreen.route
        ) {
            HomeScreen(navController = navController, context = LocalContext.current)
        }
        composable(
            route = Screen.BookContentScreen.route + "?bookId={bookId}",
            arguments = listOf(
                navArgument(
                    name = "bookId"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {

            BookContentScreen()
        }
        composable(
            route = Screen.BooksScreen.route
        ) {

        }
        composable(
            route = Screen.SearchScreen.route
        ) {

        }
        composable(
            route = Screen.FavoriteBooksScreen.route
        ) {

        }
        composable(
            route = Screen.SettingsScreen.route
        ) {

        }
    }
}
