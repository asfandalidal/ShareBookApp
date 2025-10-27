package myauthapp.azeemi.sharebook.ui.screen.book

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import myauthapp.azeemi.sharebook.navigation.Screen
import myauthapp.azeemi.sharebook.ui.viewmodel.BookViewModel
import myauthapp.azeemi.sharebook.utils.ImageUriHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    bookId: String,
    bookViewModel: BookViewModel = hiltViewModel()
) {
    val selectedBook by bookViewModel.selectedBook.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()
    val errorMessage by bookViewModel.errorMessage.collectAsState()
    
    val context = LocalContext.current
    
    LaunchedEffect(bookId) {
        bookViewModel.getBookById(bookId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { bookViewModel.getBookById(bookId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            selectedBook == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Book not found", fontSize = 16.sp)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                                )
                            )
                        )
                ) {
                    // Book Cover Image
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    ) {
                        Box(modifier = Modifier.height(350.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageUriHelper.getDisplayUri(
                                        context,
                                        selectedBook!!.coverImageUrl,
                                        selectedBook!!.localCoverImagePath
                                    )
                                ),
                                contentDescription = selectedBook!!.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                                        )
                                    )
                            )
                        }
                    }
                    
                    // Book Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title and Author
                        Text(
                            text = selectedBook!!.title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = "by ${selectedBook!!.author}",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        
                        Divider()
                        
                        // Genre and ISBN
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Genre", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(selectedBook!!.genre.ifEmpty { "Not specified" }, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                if (selectedBook!!.isbn.isNotEmpty()) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("ISBN", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(selectedBook!!.isbn, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        
                        // Description
                        Column {
                            Text("Description", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = selectedBook!!.description.ifEmpty { "No description available for this book." },
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                        
                        Divider()
                        
                        // Owner Information
                        Text("Contact Information", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Name", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        Text("Asfand Ali", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Email", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        Text("asfand.azeemi@gmail.com", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Location", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        Text("Available for pick up or delivery", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                        
                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    try {
                                        val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("https://wa.me/923001234567") // Replace with actual number
                                            setPackage("com.whatsapp")
                                        }
                                        context.startActivity(whatsappIntent)
                                    } catch (e: Exception) {
                                        // WhatsApp not installed, open in browser
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/923001234567"))
                                        context.startActivity(webIntent)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF25D366)
                                )
                            ) {
                                Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("WhatsApp")
                            }
                            
                            Button(
                                onClick = {
                                    val emailIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_EMAIL, arrayOf("asfand.azeemi@gmail.com"))
                                        putExtra(Intent.EXTRA_SUBJECT, "Interested in: ${selectedBook!!.title}")
                                        putExtra(Intent.EXTRA_TEXT, "Hi, I'm interested in your book '${selectedBook!!.title}' by ${selectedBook!!.author}. Could you please provide more details?")
                                    }
                                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Email")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
