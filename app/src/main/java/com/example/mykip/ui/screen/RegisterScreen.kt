// RegisterScreen.kt
package com.example.mykip.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.*
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mykip.R
import com.example.mykip.data.Mahasiswa
import com.example.mykip.data.OrangTua
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RegisterScreen(
    userViewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    orangTuaViewModel: OrangTuaViewModel,    // ← HARUS ADA INI
    onNavigateToLogin: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    // Field universal + mahasiswa
    var nim by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var jurusan by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val jurusanList = listOf("Kedokteran", "Informatika")

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state = userViewModel.uiState

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // ------------------ TAB ------------------
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

        // ------------------ ANIMATED FORM ------------------
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                slideInHorizontally { fullWidth -> fullWidth } + fadeIn() togetherWith
                        slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
            },
            label = "animatedForm"
        ) { tab ->

            Column {

                // ======== FORM MAHASISWA ========
                if (tab == 0) {
                    OutlinedTextField(
                        value = nim,
                        onValueChange = { nim = it },
                        label = { Text("NIM") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nama,
                        onValueChange = { nama = it },
                        label = { Text("Nama") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    Box {
                        OutlinedTextField(
                            value = jurusan,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jurusan") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            jurusanList.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        jurusan = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                }

                // ======== FORM ORANG TUA ========
                if (tab == 1) {
                    OutlinedTextField(
                        value = nama,
                        onValueChange = { nama = it },
                        label = { Text("Nama Orang Tua") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // ======== FIELD YANG SAMA UNTUK KEDUA USER ========
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ------------------ SUBMIT ------------------
        Button(
            onClick = {
                val isMahasiswa = selectedTab == 0

                userViewModel.register(
                    nim = nim,
                    email = email,
                    password = password,
                    isMahasiswa = isMahasiswa
                )

                if (isMahasiswa) {
                    mahasiswaViewModel.insert(
                        Mahasiswa(
                            nim = nim,
                            nama = nama,
                            jurusan = jurusan,
                            photoResId = R.drawable.avatar1
                        )
                    )
                } else {
                    orangTuaViewModel.insert(
                        OrangTua(
                            nama = nama,
                            email = email,
                            anakNim = "" // nanti bisa dihubungkan dengan mahasiswa
                        )
                    )
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        if (state.isLoading) CircularProgressIndicator()
        if (state.message.isNotEmpty()) Text(state.message)

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login")
        }
    }
}


