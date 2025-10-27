package myauthapp.azeemi.sharebook.ui.screen.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import myauthapp.azeemi.sharebook.navigation.Screen
import myauthapp.azeemi.sharebook.ui.components.BookCard
import myauthapp.azeemi.sharebook.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    bookViewModel: BookViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    // Simple list of genres instead of enum
    val genres = listOf(
        "Fiction", "Non-Fiction", "Mystery", "Romance", "Science Fiction",
        "Fantasy", "Biography", "History", "Self-Help", "Business"
    )
    
    val books by bookViewModel.filteredBooks.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()
    val errorMessage by bookViewModel.errorMessage.collectAsState()
    
    LaunchedEffect(selectedCategory) {
        selectedCategory?.let { category ->
            bookViewModel.loadBooksByGenre(category)
        } ?: bookViewModel.loadAllBooks()
    }
    
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            bookViewModel.searchBooks(searchQuery)
        } else {
            selectedCategory?.let { category ->
                bookViewModel.loadBooksByGenre(category)
            } ?: bookViewModel.loadAllBooks()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Search Books",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search books...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { 
                            selectedCategory = null
                            bookViewModel.loadAllBooks()
                        },
                        label = { Text("All") }
                    )
                }
                
                items(genres.take(10)) { genre ->
                    FilterChip(
                        selected = selectedCategory == genre,
                        onClick = { 
                            selectedCategory = genre
                            bookViewModel.loadBooksByGenre(genre)
                        },
                        label = { Text(genre) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error: $errorMessage",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { 
                                    selectedCategory?.let { category ->
                                        bookViewModel.loadBooksByGenre(category)
                                    } ?: bookViewModel.loadAllBooks()
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                books.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No books found",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(books) { book ->
                            BookCard(
                                book = book,
                                onClick = {
                                    navController.navigate(Screen.BookDetail.createRoute(book.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
