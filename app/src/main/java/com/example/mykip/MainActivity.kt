// MainActivity.kt
package com.example.mykip


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mykip.data.UserDatabase
import com.example.mykip.repository.UserRepository
import com.example.mykip.ui.screen.AppNavigation
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.ui.theme.MyKIPTheme
import com.example.mykip.ui.viewModel.UserViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyKIPTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val context = this
                    val db = UserDatabase.getDatabase(context)
                    val repo = UserRepository(db.userDao())
                    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repo))


                    val navController = rememberNavController()
                    AppNavigation(navController, viewModel)
                }
            }
        }
    }
}