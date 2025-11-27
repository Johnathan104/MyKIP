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
import com.example.mykip.repository.MahasiswaRepository
import com.example.mykip.repository.RiwayatDanaRepository
import com.example.mykip.repository.RiwayatDanaViewModelFactory
import com.example.mykip.repository.UserRepository
import com.example.mykip.ui.screen.*
import com.example.mykip.ui.theme.MyKIPTheme
import com.example.mykip.ui.viewModel.MahasiswaViewModelFactory
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.ui.viewModel.UserViewModelFactory
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel
import com.example.mykip.ui.screen.KelolaDanaScreen

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
    val mahasiswaViewModel: MahasiswaViewModel = viewModel(
        factory = MahasiswaViewModelFactory(
            MahasiswaRepository(database.mahasiswaDao())
        )
    )

    val riwayatViewModel: RiwayatDanaViewModel = viewModel(
        factory = RiwayatDanaViewModelFactory(
            RiwayatDanaRepository(database.riwayatDanaDao())
        )
    )
    val user = viewModel.loggedInUser
    var bottomItems = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Profile,
    )
    if(user?.isAdmin == true){
        bottomItems = listOf(
            BottomNavScreen.Home,
            BottomNavScreen.Profile,
            BottomNavScreen.Search
        )
    }


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
                     viewModel,
                    mahasiswaViewModel,

                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable(BottomNavScreen.Home.route) {
                HomeScreen()
            }

            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    navController,
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(BottomNavScreen.Login.route) {
                            popUpTo(BottomNavScreen.Home.route) { inclusive = true }
                        }
                    },
                    mahasiswaViewModel = mahasiswaViewModel,
                    riwayatViewModel = riwayatViewModel
                )
            }

            composable(BottomNavScreen.Search.route) {
                DaftarAnakScreen(
                    navController = navController,
                    userViewModel = viewModel,
                    mahasiswaViewModel = mahasiswaViewModel,
                    riwayatViewModel = riwayatViewModel
                )
            }

            composable("daftarAnak") {

                val user = viewModel.loggedInUser

                // If user is NOT ADMIN → block access
                if (user?.isAdmin != true) {
                    // Option A: go back
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                    Text("Anda tidak memiliki akses.")  // optional placeholder
                    return@composable
                }
                DaftarAnakScreen(
                    navController = navController,
                    userViewModel = viewModel,
                    mahasiswaViewModel = mahasiswaViewModel,
                    riwayatViewModel = riwayatViewModel
                )
            }
            composable("kelolaDana"){

                val user = viewModel.loggedInUser

                // If user is NOT ADMIN → block access
                if (user?.isAdmin != true) {
                    // Option A: go back
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                    Text("Anda tidak memiliki akses.")  // optional placeholder
                    return@composable
                }
                KelolaDanaScreen(
                     viewModel,
                    riwayatViewModel,
                    navController
                )

            }
            composable(
                "detailAnak/{anakId}",
                arguments = listOf(navArgument("anakId") { type = NavType.StringType })
            ) { backStackEntry ->
                val anakId = backStackEntry.arguments?.getString("anakId") ?: ""
                DetailAnakScreen(anakId , navController,userViewModel = viewModel, mahasiswaViewModel, riwayatViewModel)
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
