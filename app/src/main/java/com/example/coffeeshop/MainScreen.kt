package com.example.coffeeshop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshop.presentation.cart.CartScreen
import com.example.coffeeshop.presentation.favorite.FavoritesScreen
import com.example.coffeeshop.presentation.home.HomeScreen
import com.example.coffeeshop.presentation.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) },

    ) {
        paddingValues ->
        NavHost(navController, startDestination = "home",modifier = Modifier.padding(paddingValues)) {
            composable("home") { HomeScreen() }
            //composable("favorites") { FavoritesScreen() }
            composable("cart") { CartScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFFF5F5F5)) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
            label = { Text("Ana Sayfa") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        /*
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoriler") },
            label = { Text("Favoriler") },
            selected = false,
            onClick = { navController.navigate("favorites") }
        )

         */
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Sepet") },
            label = { Text("Sepet") },
            selected = false,
            onClick = { navController.navigate("cart") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            selected = false,
            onClick = { navController.navigate("profile") }
        )
    }
}