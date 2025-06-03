package com.example.jsonplaceholderapi.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jsonplaceholderapi.ui.viewmodel.PostDetailViewModel
import com.example.jsonplaceholderapi.data.repository.MainRepository
import com.example.jsonplaceholderapi.data.network.RetrofitInstance
import com.example.jsonplaceholderapi.ui.viewmodel.PostDetailViewModelFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.jsonplaceholderapi.data.local.DataStoreManager
import com.example.jsonplaceholderapi.ui.viewmodel.MainViewModel
import com.example.jsonplaceholderapi.ui.viewmodel.MainViewModelFactory
import androidx.compose.ui.Alignment

@Composable
fun PostDetailScreen(navController: NavController, postId: Int) {
    val context = LocalContext.current
    val postViewModel: PostDetailViewModel = viewModel(
        factory = PostDetailViewModelFactory(MainRepository(RetrofitInstance.api))
    )
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory())
    val post by postViewModel.post.collectAsState()
    val isLoading by postViewModel.isLoading.collectAsState()
    val comments by mainViewModel.comments.collectAsState()

    val dataStoreManager = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()

    var commentText by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        name = dataStoreManager.getName() ?: ""
        surname = dataStoreManager.getSurname() ?: ""
        imagePath = dataStoreManager.getImagePath() ?: ""
    }

    LaunchedEffect(postId) {
        postViewModel.fetchPost(postId)
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        post?.let {
            Column(
                Modifier
                    .padding(16.dp)
                    .systemBarsPadding()
            ) {
                Text(text = it.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(text = it.body)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "User ID: ${it.userId}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(24.dp))

                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Dodaj komentarz") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            mainViewModel.addComment(
                                postId,
                                name,
                                surname,
                                imagePath,
                                commentText
                            )
                            commentText = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Dodaj")
                }

                Text("Komentarze:")

                val postComments = comments.filter { c -> c.postId == postId }

                LazyColumn() {
                    items(postComments.size) { index ->
                        val comment = postComments[index]
                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    "${comment.authorName ?: ""} ${comment.authorSurname ?: ""}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(comment.content ?: "")
                            }
                        }
                    }
                }

                Button(onClick = { navController.popBackStack() }) {
                    Text("Wróć")
                }
            }
        } ?: Text("Nie znaleziono posta.")
    }
}