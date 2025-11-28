

// Navigation.kt
package com.example.mykip.ui.screen


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mykip.data.UserDatabase
import com.example.mykip.repository.OrangTuaRepository
import com.example.mykip.ui.screen.LoginScreen
import com.example.mykip.ui.screen.RegisterScreen
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.OrangTuaViewModel
import com.example.mykip.viewmodel.OrangTuaViewModelFactory


@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel
) {
    // INIT DATABASE
    val context = navController.context
    val database = UserDatabase.getDatabase(context)

    // INIT REPOSITORY
    val orangTuaRepository = OrangTuaRepository(database.orangTuaDao())

    // INIT VIEWMODEL
    val orangTuaViewModel: OrangTuaViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = OrangTuaViewModelFactory(orangTuaRepository)
    )

    NavHost(navController = navController, startDestination = "register") {

        composable("register") {
            RegisterScreen(
                userViewModel = viewModel,
                mahasiswaViewModel = mahasiswaViewModel,
                orangTuaViewModel = orangTuaViewModel,  // ← WAJIB
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel,
                onLoginSuccess = {},
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
    }
}
