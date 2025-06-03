package com.example.jsonplaceholderapi.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jsonplaceholderapi.ui.viewmodel.UserDetailViewModel
import com.example.jsonplaceholderapi.data.repository.MainRepository
import com.example.jsonplaceholderapi.data.network.RetrofitInstance
import com.example.jsonplaceholderapi.ui.viewmodel.UserDetailViewModelFactory
import androidx.compose.ui.platform.LocalContext
import com.example.jsonplaceholderapi.data.model.User
import com.example.jsonplaceholderapi.data.network.ApiService
import com.example.jsonplaceholderapi.ui.viewmodel.PostDetailViewModel
import com.example.jsonplaceholderapi.ui.viewmodel.PostDetailViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition

@Composable
fun UserDetailScreen(navController: NavController, userId: Int) {
    val context = LocalContext.current
    val viewModel: UserDetailViewModel = viewModel(
        factory = UserDetailViewModelFactory(MainRepository(RetrofitInstance.api))
    )
    val user by viewModel.user.collectAsState()
    val todos by viewModel.todos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(userId) {
        viewModel.fetchUserData(userId)
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        user?.let {
            Column(
                Modifier
                    .padding(16.dp)
                    .systemBarsPadding()
            ) {
                Text(text = it.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "@${it.username}")
                Text(text = "📧 ${it.email}")
                Text(text = "📞 ${it.phone}")
                Text(text = "🌐 ${it.website}")
                Text(text = "🏢 ${it.company.name}")
                Text(text = "📍 ${it.address.street}, ${it.address.city}")

                Spacer(Modifier.height(16.dp))

                val lat = it.address.geo.lat.toDoubleOrNull() ?: 0.0
                val lng = it.address.geo.lng.toDoubleOrNull() ?: 0.0
                val userLocation = LatLng(lat, lng)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(userLocation, 12f)
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = userLocation),
                        title = it.name
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(onClick = { navController.popBackStack() }) {
                    Text("Wróć")
                }

                Spacer(Modifier.height(16.dp))

                Text("Zadania:", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(todos.size) { index ->
                        val todo = todos[index]
                        Text(text = "${if (todo.completed) "✅" else "❌"} ${todo.title}")
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        } ?: Text("Nie znaleziono użytkownika.")
    }
}