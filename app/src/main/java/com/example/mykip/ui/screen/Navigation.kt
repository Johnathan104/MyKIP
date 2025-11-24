

// Navigation.kt
package com.example.mykip.ui.screen


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mykip.ui.screen.LoginScreen
import com.example.mykip.ui.screen.RegisterScreen
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.MahasiswaViewModel


@Composable
fun AppNavigation(navController: NavHostController, viewModel: UserViewModel, mahasiswaViewModel: MahasiswaViewModel) {
    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            RegisterScreen(viewModel, mahasiswaViewModel = mahasiswaViewModel, onNavigateToLogin = {
                navController.navigate("login")
            })
        }
        composable("login") {
            LoginScreen(viewModel, onLoginSuccess = {}, onNavigateToRegister = {
                navController.navigate("register")
            })
        }
    }
}