package com.example.mykip.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mykip.R
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

    var nim by remember { mutableStateOf("") }
    var mhsPassword by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }
    var ortuPassword by remember { mutableStateOf("") }

    val userState = viewModel.uiState
    val ortuState = orangTuaViewModel.uiState

    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // 🔥 Handling success / error & animation
    LaunchedEffect(userState.isSuccess, userState.error, ortuState.isSuccess, ortuState.error) {

        // Loading
        isLoading = userState.isLoading || ortuState.isLoading

        // Success
        if (userState.isSuccess || ortuState.isSuccess) {
            isLoading = false
            showSuccess = true
            showError = false

            kotlinx.coroutines.delay(1200)
            onLoginSuccess()
        }

        // Error
        if (userState.error != null || ortuState.error != null) {
            isLoading = false
            showSuccess = false
            showError = true

            kotlinx.coroutines.delay(1500)
            showError = false
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

            // =============== SUCCESS BANNER ===================
            AnimatedVisibility(
                visible = showSuccess,
                enter = fadeIn(animationSpec = tween(400)) + slideInVertically { -40 },
                exit = fadeOut(animationSpec = tween(400)) + slideOutVertically { -40 }
            ) {
                SuccessBanner()
            }

            // =============== ERROR BANNER ===================
            AnimatedVisibility(
                visible = showError,
                enter = fadeIn(animationSpec = tween(400)) + slideInVertically { -40 },
                exit = fadeOut(animationSpec = tween(400)) + slideOutVertically { -40 }
            ) {
                ErrorBanner()
            }

            // =============== TITLE ===================
            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3A3A3A)
            )

            Text(
                text = "Hello there, sign in to continue",
                fontSize = 15.sp,
                color = Color.Gray
            )

            // 🔒 ICON
            Image(
                painter = painterResource(R.drawable.padlock),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(5.dp)
            )

            Spacer(Modifier.height(16.dp))

            val tabs = listOf("Mahasiswa/i", "Orang Tua")

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFF1F1F1),
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        selectedContentColor = Color(0xFF7B2CBF),
                        unselectedContentColor = Color.DarkGray,
                        text = {
                            Text(
                                title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(25.dp))


            // ================= FORM ======================
            AnimatedContent(
                targetState = selectedTab,
                label = "loginTabs",
                transitionSpec = {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                }
            ) { tab ->

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    val isMahasiswaValid = nim.isNotBlank() && mhsPassword.isNotBlank()
                    val isOrtuValid = email.isNotBlank() && ortuPassword.isNotBlank()

                    if (tab == 0) {
                        // ================= MAHASISWA FORM =====================
                        OutlinedTextField(
                            value = nim,
                            onValueChange = { nim = it },
                            label = { Text("NIM") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp)
                        )
                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = mhsPassword,
                            onValueChange = { mhsPassword = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Spacer(Modifier.height(15.dp))

                        Text(
                            "Forgot your password ?",
                            modifier = Modifier.align(Alignment.End),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { if (isMahasiswaValid) viewModel.login(nim, mhsPassword) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = isMahasiswaValid && !isLoading,
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7B2CBF),
                                disabledContainerColor = Color(0xFFBDBDBD)
                            )
                        ) {
                            if (isLoading)
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            else
                                Text("Sign In", fontSize = 16.sp, color = Color.White)
                        }

                    } else {
                        // ================== ORANG TUA FORM ======================
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp)
                        )
                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = ortuPassword,
                            onValueChange = { ortuPassword = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = { if (isOrtuValid) orangTuaViewModel.login(email, ortuPassword) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = isOrtuValid && !isLoading,
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7B2CBF),
                                disabledContainerColor = Color(0xFFBDBDBD)
                            )
                        ) {
                            if (isLoading)
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            else
                                Text("Sign In", fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))


            // ===================== REGISTER LINK ====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
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
