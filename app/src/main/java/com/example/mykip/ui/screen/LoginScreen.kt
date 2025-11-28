package com.example.mykip.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.OrangTuaViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    orangTuaViewModel: OrangTuaViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    // mahasiswa login fields
    var nim by remember { mutableStateOf("") }
    var mhsPassword by remember { mutableStateOf("") }

    // orang tua login fields
    var email by remember { mutableStateOf("") }
    var ortuPassword by remember { mutableStateOf("") }

    val userState = viewModel.uiState
    val ortuState = orangTuaViewModel.uiState

    // Navigate when login success
    LaunchedEffect(userState.isSuccess, ortuState.isSuccess) {
        if (userState.isSuccess || ortuState.isSuccess) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        val tabs = listOf("Mahasiswa/i", "Orang Tua")

        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            },
            label = "loginTabs"
        ) { tab ->

            Column {

                if (tab == 0) {
                    // ------------------ MAHASISWA LOGIN ------------------
                    OutlinedTextField(
                        value = nim,
                        onValueChange = { nim = it },
                        label = { Text("NIM") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = mhsPassword,
                        onValueChange = { mhsPassword = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.login(nim, mhsPassword) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Login Mahasiswa") }

                    if (userState.isLoading) CircularProgressIndicator()
                    if (userState.message.isNotEmpty()) Text(userState.message)

                } else {
                    // ------------------ ORANG TUA LOGIN ------------------
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ortuPassword,
                        onValueChange = { ortuPassword = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { orangTuaViewModel.login(email, ortuPassword) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Login Orang Tua") }

                    if (ortuState.isLoading) CircularProgressIndicator()
                    if (ortuState.message.isNotEmpty()) Text(ortuState.message)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister, modifier = Modifier.fillMaxWidth()) {
            Text("Don't have an account? Register")
        }
    }
}
