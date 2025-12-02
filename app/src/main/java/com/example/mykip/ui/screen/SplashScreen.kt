package com.example.mykip.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.mykip.BottomNavScreen
import com.example.mykip.data.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(sessionManager: SessionManager, navController: NavController) {
    // 1. Start with a 'null' initial state to represent "loading" or "undetermined".
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = null)

    // 2. The LaunchedEffect will now re-trigger when 'isLoggedIn' changes from null to a Boolean.
    LaunchedEffect(isLoggedIn) {
        // 3. Wait until 'isLoggedIn' has a real value (is not null).
        if (isLoggedIn != null) {
            // Optional delay for branding
            delay(1500)

            // 4. Now we can safely check the actual value.
            if (isLoggedIn == true) {
                // User is logged in, go to the main screen
                navController.navigate(BottomNavScreen.Home.route) {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                // User is not logged in, go to the login screen
                navController.navigate(BottomNavScreen.Login.route) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    // Your splash screen UI (e.g., a logo in the center)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // This indicator will show while isLoggedIn is null.
        CircularProgressIndicator()
    }
}
