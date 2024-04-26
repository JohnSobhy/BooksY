package com.john_halaka.booksy

import android.os.Build
import androidx.annotation.RequiresApi

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.john_halaka.booksy.feature_book.domain.model.Book
import com.john_halaka.booksy.ui.Screen
import com.john_halaka.booksy.ui.presentation.book_content.BookContentScreen
import com.john_halaka.booksy.ui.presentation.bookmarks.BookmarksScreen
import com.john_halaka.booksy.ui.presentation.home.HomeScreen
import com.john_halaka.booksy.ui.presentation.search.SearchScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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
            BookContentScreen(navController = navController)
        }
        composable(
            route = Screen.BooksScreen.route
        ) {
            BookmarksScreen()
        }
        composable(
            route = Screen.SearchScreen.route
        ) {
            SearchScreen(navController = navController, context = LocalContext.current)
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

@Composable
fun NavigationDrawer(
    navController: NavController,
    drawerState: DrawerState,
    content: @Composable () -> Unit,
    scope: CoroutineScope
) {

    val currentRoute = navController.currentDestination?.route
    val itemList: List<NavigationItem> = listOf(
        NavigationItem(
            title = stringResource(R.string.home),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = Screen.HomeScreen.route
        ),

        NavigationItem(
            title = stringResource(R.string.favorites),
            selectedIcon = Icons.Default.Favorite,
            unselectedIcon = Icons.Default.FavoriteBorder,
            route = Screen.FavoriteBooksScreen.route
        ),
        NavigationItem(
            title = stringResource(R.string.bookmarks),
            selectedIcon = Icons.Default.Star,
            unselectedIcon = Icons.Outlined.Star,
            route = Screen.BookmarksScreen.route
        ),
        NavigationItem(
            title = stringResource(R.string.settings),
            selectedIcon = Icons.Default.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = Screen.SettingsScreen.route
        )
    )
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                itemList.forEach { navigationItem ->
                    NavigationDrawerItem(
                        label = { Text(text = navigationItem.title) },
                        selected = currentRoute == navigationItem.route,
                        onClick = {
                            navController.navigate(navigationItem.route)
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == navigationItem.route) {
                                    navigationItem.selectedIcon
                                } else
                                    navigationItem.unselectedIcon,
                                contentDescription = navigationItem.title
                            )
                        },
                        //   badge = {},
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .width(intrinsicSize = IntrinsicSize.Min)

                    )
                }
            }
        },
        drawerState = drawerState,
        content = content
    )
}
@Composable
fun NavigationItemsBar(
    navController: NavController,
) {
    val currentRoute = navController.currentDestination?.route
    val itemList: List<NavigationItem> = listOf(

        NavigationItem(
            title = stringResource(R.string.home),
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = Screen.HomeScreen.route
        ),
        NavigationItem(
            title = stringResource(R.string.bookmarks),
            selectedIcon = Icons.Default.Star,
            unselectedIcon = Icons.Outlined.Star,
            route = Screen.BookmarksScreen.route
        ),

        NavigationItem(
            title = stringResource(R.string.search),
            selectedIcon = Icons.Default.Search,
            unselectedIcon = Icons.Outlined.Search,
            route = Screen.SearchScreen.route
        ),
        NavigationItem(
            title = stringResource(R.string.settings),
            selectedIcon = Icons.Default.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = Screen.SettingsScreen.route
        )
    )

    NavigationBar {
        itemList.forEach { navigationItem ->
            NavigationBarItem(
                selected = currentRoute == navigationItem.route,
                onClick = {
                    navController.navigate(navigationItem.route)
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == navigationItem.route) {
                            navigationItem.selectedIcon
                        } else navigationItem.unselectedIcon,
                        contentDescription = navigationItem.title
                    )
                },
                label = {
                    Text(text = navigationItem.title)
                }
            )
        }

    }

}


    data class NavigationItem(
        val title: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val route: String
    )