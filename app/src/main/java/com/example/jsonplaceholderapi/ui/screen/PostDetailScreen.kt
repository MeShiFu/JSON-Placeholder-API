package com.example.jsonplaceholderapi.ui.screen

import androidx.compose.foundation.layout.*
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

@Composable
fun PostDetailScreen(navController: NavController, postId: Int) {
    val context = LocalContext.current
    val viewModel: PostDetailViewModel = viewModel(
        factory = PostDetailViewModelFactory(MainRepository(RetrofitInstance.api))
    )
    val post by viewModel.post.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    LaunchedEffect(postId) {
        viewModel.fetchPost(postId)
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        post?.let {
            Column(Modifier.padding(16.dp).systemBarsPadding()) {
                Text(text = it.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(text = it.body)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "User ID: ${post?.userId ?: "Brak danych"}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Button(onClick = { navController.popBackStack() }) {
                    Text("Wróć")
                }
            }
        } ?: Text("Nie znaleziono posta.")
    }
}