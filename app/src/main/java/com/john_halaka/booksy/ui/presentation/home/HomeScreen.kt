package com.john_halaka.booksy.ui.presentation.home

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.john_halaka.booksy.ui.presentation.all_books.AllBooksScreen
import com.john_halaka.booksy.ui.presentation.home.widgets.AllBooksWidget
import com.john_halaka.booksy.ui.presentation.home.widgets.RecentReadsWidget
import com.john_halaka.booksy.ui.presentation.home.widgets.TrendingBooksWidget


@Composable
fun HomeScreen (navController: NavController, context: Context){

    AllBooksScreen(navController = navController)
//    RecentReadsWidget()
//    TrendingBooksWidget()
//    AllBooksWidget()
}




