package myauthapp.azeemi.sharebook.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import myauthapp.azeemi.sharebook.ui.screen.MainScreen
import myauthapp.azeemi.sharebook.ui.screen.auth.LoginScreen
import myauthapp.azeemi.sharebook.ui.screen.auth.SignUpScreen
import myauthapp.azeemi.sharebook.ui.screen.book.AddBookScreen
import myauthapp.azeemi.sharebook.ui.screen.book.BookDetailScreen
import myauthapp.azeemi.sharebook.ui.screen.book.HomeScreen
import myauthapp.azeemi.sharebook.ui.screen.book.MyBooksScreen
import myauthapp.azeemi.sharebook.ui.screen.profile.ProfileScreen
import myauthapp.azeemi.sharebook.ui.screen.search.SearchScreen

@Composable
fun ShareBookNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }
        
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
        
        composable(Screen.BookList.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.BookDetail.route) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailScreen(
                navController = navController,
                bookId = bookId
            )
        }
        
        composable(Screen.AddBook.route) {
            AddBookScreen(navController = navController)
        }
        
        composable(Screen.MyBooks.route) {
            MyBooksScreen(navController = navController)
        }
        
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
