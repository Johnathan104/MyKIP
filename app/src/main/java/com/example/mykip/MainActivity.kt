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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.mykip.data.AppDatabase
import com.example.mykip.data.UserDatabase
import com.example.mykip.repository.UserRepository
import com.example.mykip.ui.screen.*
import com.example.mykip.ui.theme.MyKIPTheme
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.ui.viewModel.UserViewModelFactory

// Sealed class untuk bottom navigation
sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null // icon nullable, Login tidak butuh icon
) {
    object Home: BottomNavScreen("home","Home", Icons.Default.Home)
    object Profile: BottomNavScreen("profile","Profile", Icons.Default.Person)
    object Search: BottomNavScreen("search","Search", Icons.Default.Search)
    object Login: BottomNavScreen("login","Login") // Login tanpa icon
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
    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Profile,
        BottomNavScreen.Search
    )

    val context = LocalContext.current
    val database = UserDatabase.getDatabase(context)
    val userDao = database.userDao()
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepository(userDao)))


    Scaffold(
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != BottomNavScreen.Login.route) {
                BottomBar(navController = navController, items = items)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Login Screen
            composable(BottomNavScreen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(BottomNavScreen.Home.route) {
                            popUpTo(BottomNavScreen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { /* register */ }
                )
            }

            // Home Screen
            composable(BottomNavScreen.Home.route){
                HomeScreen()
            }

            // Profile Screen
            composable(BottomNavScreen.Profile.route){
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

            // Search Screen
            composable(BottomNavScreen.Search.route){
                SearchScreen()
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, items: List<BottomNavScreen>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route){
                        popUpTo(navController.graph.findStartDestination().id){
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

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyKIPTheme {
        MyApp()
    }
}
