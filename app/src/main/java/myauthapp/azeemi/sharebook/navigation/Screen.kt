package myauthapp.azeemi.sharebook.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Main : Screen("main")
    object BookList : Screen("book_list")
    object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }
    object AddBook : Screen("add_book")
    object MyBooks : Screen("my_books")
    object Search : Screen("search")
    object Profile : Screen("profile")
}
