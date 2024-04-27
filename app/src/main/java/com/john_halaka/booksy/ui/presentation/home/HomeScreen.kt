package com.john_halaka.booksy.ui.presentation.home

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.john_halaka.booksy.NavigationDrawer
import com.john_halaka.booksy.NavigationItemsBar
import com.john_halaka.booksy.R
import com.john_halaka.booksy.ui.presentation.all_books.AllBooksScreen
import com.john_halaka.booksy.ui.presentation.home.widgets.AllBooksWidget
import com.john_halaka.booksy.ui.presentation.home.widgets.RecentReadsWidget
import com.john_halaka.booksy.ui.presentation.home.widgets.TrendingBooksWidget
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (
    navController: NavController,
)
    {

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    NavigationDrawer(
        navController = navController,
        drawerState = drawerState,
        scope = scope,
        content = {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(

                        title = {
                        },

                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            })
                            {
                                Icon(
                                    Icons.Filled.Menu,
                                    contentDescription = stringResource(R.string.menu)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                            })
                            {
                                Icon(
                                    Icons.Filled.AccountCircle,
                                    contentDescription = stringResource(R.string.profile)
                                )
                            }
                        }
                    )


                },
                bottomBar = {
                    NavigationItemsBar(navController = navController)
                },

                ) { values ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)

                ) {
                    AllBooksScreen(navController = navController, modifier = Modifier)
//    RecentReadsWidget()
//    TrendingBooksWidget()
//    AllBooksWidget()
                }
            }
        }
    )
}





