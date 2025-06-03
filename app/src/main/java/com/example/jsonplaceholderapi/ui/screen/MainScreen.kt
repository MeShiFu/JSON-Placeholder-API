package com.example.jsonplaceholderapi.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jsonplaceholderapi.ui.viewmodel.MainViewModel
import com.example.jsonplaceholderapi.data.repository.MainRepository
import com.example.jsonplaceholderapi.data.network.RetrofitInstance
import com.example.jsonplaceholderapi.ui.viewmodel.MainViewModelFactory

@Composable
fun MainScreen(navController: NavController) {
    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory())
    val posts by viewModel.posts.collectAsState()
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }

    if (isLoading) {
        CircularProgressIndicator()
    } else if (error != null) {
        Text("Błąd: $error")
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("myprofile") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Moje konto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Filtruj po użytkowniku:")
            Box {
                Button(onClick = { expanded = true }) {
                    Text(text = selectedUserId?.let { id ->
                        users.find { it.id == id }?.name ?: "Wszyscy"
                    } ?: "Wszyscy")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Wszyscy") },
                        onClick = {
                            selectedUserId = null
                            expanded = false
                        }
                    )
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                selectedUserId = user.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                val filteredPosts = selectedUserId?.let { id ->
                    posts.filter { it.userId == id }
                } ?: posts

                items(filteredPosts.size) { index ->
                    val post = filteredPosts[index]
                    val user = users.find { it.id == post.userId }

                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("postDetail/${post.id}")
                            }
                    ) {
                        Text(text = post.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "by ${user?.name ?: "Unknown"}",
                            modifier = Modifier.clickable {
                                navController.navigate("userDetail/${post.userId}")
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}