package com.example.jsonplaceholderapi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jsonplaceholderapi.ui.screen.MainScreen
import com.example.jsonplaceholderapi.ui.screen.PostDetailScreen
import com.example.jsonplaceholderapi.ui.screen.UserDetailScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = "main"
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("main") {
            MainScreen(navController)
        }
        composable("postDetail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")?.toInt() ?: 0
            PostDetailScreen(navController, postId)
        }
        composable("userDetail/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: 0
            UserDetailScreen(navController, userId)
        }
    }
}