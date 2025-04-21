package com.example.coffeeshop.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.compose.*
import com.example.coffeeshop.MainScreen
import com.example.coffeeshop.presentation.login.LoginScreen
import com.example.coffeeshop.presentation.register.RegisterScreen

// presentation/navigation/NavGraph.kt
@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("home") {
            MainScreen()
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
    }
}

// presentation/home/HomeScreen.kt
