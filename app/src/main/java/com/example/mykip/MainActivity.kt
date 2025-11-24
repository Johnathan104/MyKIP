package com.example.mykip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.mykip.data.UserDatabase
import com.example.mykip.repository.UserRepository
import com.example.mykip.ui.screen.*
import com.example.mykip.ui.theme.MyKIPTheme
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.ui.viewModel.UserViewModelFactory

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : BottomNavScreen("home", "Home", Icons.Default.Home)
    object Profile : BottomNavScreen("profile", "Profile", Icons.Default.Person)
    object Search : BottomNavScreen("search", "Search", Icons.Default.Search)
    object Login : BottomNavScreen("login", "Login")
    object Register : BottomNavScreen("register", "Register")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyKIPTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val database = UserDatabase.getDatabase(context)
    val viewModel: UserViewModel =
        viewModel(factory = UserViewModelFactory(UserRepository(database.userDao())))

    val bottomItems = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Profile,
        BottomNavScreen.Search
    )

    Scaffold(
        bottomBar = {
            val route = navController.currentBackStackEntryAsState().value?.destination?.route

            if (
                route !in listOf(
                    BottomNavScreen.Login.route,
                    BottomNavScreen.Register.route
                ) &&
                route?.startsWith("detailAnak/") == false
            ) {
                BottomBar(navController, bottomItems)
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Login.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(BottomNavScreen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(BottomNavScreen.Home.route) {
                            popUpTo(BottomNavScreen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(BottomNavScreen.Register.route)
                    }
                )
            }

            composable(BottomNavScreen.Register.route) {
                RegisterScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable(BottomNavScreen.Home.route) {
                HomeScreen()
            }

            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(BottomNavScreen.Login.route) {
                            popUpTo(BottomNavScreen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(BottomNavScreen.Search.route) {
                DaftarAnakScreen(navController)
            }

            composable("daftarAnak") {
                DaftarAnakScreen(navController)
            }

            composable(
                "detailAnak/{anakId}",
                arguments = listOf(navArgument("anakId") { type = NavType.StringType })
            ) { backStackEntry ->
                val anakId = backStackEntry.arguments?.getString("anakId") ?: ""
                DetailAnakScreen(anakId = anakId, navController = navController)
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, items: List<BottomNavScreen>) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
