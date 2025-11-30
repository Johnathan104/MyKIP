package com.example.mykip.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mykip.R
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.OrangTuaViewModel
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    // orangTuaViewModel: OrangTuaViewModel, // REMOVED
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // --- State for the unified form ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // --- State from ViewModel ---
    val userState = viewModel.uiState
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // 🔥 Handling success / error & animation (now simplified)
    LaunchedEffect(userState) {
        isLoading = userState.isLoading

        if (userState.isSuccess) {
            isLoading = false
            showSuccess = true
            showError = false
            kotlinx.coroutines.delay(1200)
            onLoginSuccess()
            viewModel.resetState() // Reset state after success
        }

        if (userState.error != null) {
            isLoading = false
            showSuccess = false
            showError = true
            kotlinx.coroutines.delay(2000)
            showError = false
            viewModel.resetState() // Reset state to allow retry
        }
    }

    // ================================
    // 💜 UI START
    // ================================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3A86FF), Color(0xFF7B2CBF))
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White)
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Banners and Title remain the same...
            AnimatedVisibility(visible = showSuccess, enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically()) { SuccessBanner() }
            AnimatedVisibility(visible = showError, enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically()) { ErrorBanner() }
            Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3A3A3A))
            Text("Hello there, sign in to continue", fontSize = 15.sp, color = Color.Gray)
            Image(painter = painterResource(R.drawable.padlock), contentDescription = null, modifier = Modifier
                .size(120.dp)
                .padding(5.dp))
            Spacer(Modifier.height(16.dp))

            // --- REMOVED TAB ROW ---

            Spacer(Modifier.height(25.dp))

            // ================= UNIFIED LOGIN FORM ======================
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val isFormValid = email.isNotBlank() && password.isNotBlank()

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    }
                )

                Spacer(Modifier.height(15.dp))
                Text("Forgot your password ?", modifier = Modifier.align(Alignment.End), fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(16.dp))

                // Login Button
                Button(
                    onClick = { if (isFormValid) viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isFormValid && !isLoading,
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7B2CBF),
                        disabledContainerColor = Color(0xFFBDBDBD)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Sign In", fontSize = 16.sp, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ===================== REGISTER LINK ====================
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "Don't have an account? ", color = Color.Gray)
                Text(
                    text = "Sign Up",
                    color = Color(0xFF7B2CBF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}



// ======================================================
// SUCCESS BANNER
// ======================================================
@Composable
fun SuccessBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF4CAF50))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_success),
            contentDescription = null,
            tint = Color.White
        )
        Spacer(Modifier.width(10.dp))
        Text("Login Berhasil!", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

// ======================================================
// ERROR BANNER
// ======================================================
@Composable
fun ErrorBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE53935))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = Color.White
        )
        Spacer(Modifier.width(10.dp))
        Text("Login gagal, periksa kembali!", color = Color.White, fontWeight = FontWeight.Bold)
    }
}
