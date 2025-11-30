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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.example.mykip.datastore.OnboardingDataStore
import com.example.mykip.repository.*
import com.example.mykip.ui.screen.*
import com.example.mykip.ui.theme.MyKIPTheme
import com.example.mykip.ui.viewModel.*
import com.example.mykip.viewmodel.*
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
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

    // 🔥 ONBOARDING DATASTORE
    val onboardingDataStore = remember { OnboardingDataStore(context) }
    val isOnboardingCompleted by onboardingDataStore.isCompleted.collectAsState(initial = false)

    val db = Firebase.firestore

    val userViewModel: UserViewModel =
        viewModel(factory = UserViewModelFactory(UserRepository()))

    val mahasiswaViewModel: MahasiswaViewModel =
        viewModel(factory = MahasiswaViewModelFactory(MahasiswaRepository(db)))

    val riwayatViewModel: RiwayatDanaViewModel =
        viewModel(factory = RiwayatDanaViewModelFactory(RiwayatDanaRepository(db)))

    val orangTuaViewModel: OrangTuaViewModel =
        viewModel(factory = OrangTuaViewModelFactory(OrangTuaRepository()))

    val user = userViewModel.loggedInUser

    var bottomItems = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Profile,
    )
    val isAdmin = user?.role=="admin"
    if (isAdmin == true) {
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
                    BottomNavScreen.Register.route,
                    "landing"
                ) &&
                route?.startsWith("detailAnak/") == false
            ) {
                BottomBar(navController, bottomItems)
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = if (isOnboardingCompleted) {
                BottomNavScreen.Login.route
            } else {
                "landing"
            },
            modifier = Modifier.padding(padding)
        ) {

            // ⭐ LANDING PAGE / ONBOARDING
            composable("landing") {
                LandingPage(
                    dataStore = onboardingDataStore,
                    onFinished = {
                        navController.navigate(BottomNavScreen.Login.route) {
                            popUpTo("landing") { inclusive = true }
                        }
                    }
                )
            }


            // ⭐ LOGIN
            composable(BottomNavScreen.Login.route) {
                LoginScreen(
                    viewModel = userViewModel,
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

            // ⭐ REGISTER
            composable(BottomNavScreen.Register.route) {
                RegisterScreen(
                    userViewModel = userViewModel,
                    mahasiswaViewModel = mahasiswaViewModel,
                    orangTuaViewModel = orangTuaViewModel,
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            // ⭐ HOME
            composable(BottomNavScreen.Home.route) {
                HomeScreen(viewModel = userViewModel,
                    orangTuaViewModel = orangTuaViewModel,
                    navController = navController,
                    mahasiswaViewModel = mahasiswaViewModel,
                    riwayatViewModel = riwayatViewModel)
            }

            // ⭐ PROFILE
            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(
                    viewModel = userViewModel,
                    orangTuaViewModel = orangTuaViewModel,
                    navController = navController,
                    onLogout = {
                        userViewModel.logout()
                        navController.navigate(BottomNavScreen.Login.route) {
                            popUpTo(BottomNavScreen.Home.route) { inclusive = true }
                        }
                    },
                    mahasiswaViewModel = mahasiswaViewModel,
                    riwayatViewModel = riwayatViewModel
                )
            }

            // ⭐ SEARCH (khusus admin)
            composable(BottomNavScreen.Search.route) {
                DaftarAnakScreen(
                    navController = navController,
                    userViewModel = userViewModel,
                    mahasiswaViewModel = mahasiswaViewModel,
                    riwayatViewModel = riwayatViewModel
                )
            }

            // ⭐ KELOLA DANA
            composable("kelolaDana") {
                KelolaDanaScreen(
                    userViewModel = userViewModel,
                    riwayatViewModel = riwayatViewModel,
                    navController = navController
                )
            }

            // ⭐ DETAIL ANAK
            composable(
                route = "detailAnak/{anakId}",
                arguments = listOf(navArgument("anakId") { type = NavType.StringType })
            ) { backStackEntry ->
                val anakId = backStackEntry.arguments?.getString("anakId") ?: ""
                DetailAnakScreen(
                    anakId,
                    navController,
                    userViewModel = userViewModel,
                    mahasiswaViewModel = mahasiswaViewModel,
                    riwayatViewModel = riwayatViewModel
                )
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, items: List<BottomNavScreen>) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 12.dp
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route


        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        screen.icon ?: Icons.Default.Home,
                        contentDescription = screen.title,
                        tint = if (currentRoute == screen.route) Color(0xFF4F6EF7) else Color.Gray
                    )
                },
                label = {
                    Text(
                        screen.title,
                        color = if (currentRoute == screen.route) Color(0xFF4F6EF7) else Color.Gray
                    )
                }
            )
        }
    }
}
