// RegisterScreen.kt
package com.example.mykip.ui.screen

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
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.MahasiswaViewModel

@Composable
fun RegisterScreen(
    userViewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    onNavigateToLogin: () -> Unit
) {
    var nim by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var jurusan by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }   // dropdown
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val jurusanList = listOf("Kedokteran", "Informatika")

    val state = userViewModel.uiState

    Column(
        Modifier
            .padding(20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // NIM
        OutlinedTextField(
            value = nim,
            onValueChange = { nim = it },
            label = { Text("NIM") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // NAMA
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // DROPDOWN JURUSAN (TANPA DEPRECATED)
        Box {
            OutlinedTextField(
                value = jurusan,
                onValueChange = {jurusan = it},
                readOnly = false,
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

        Spacer(Modifier.height(8.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // PASSWORD (Masked)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password) ,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // SUBMIT
        Button(
            onClick = {
                userViewModel.register(nim, email, password)

                mahasiswaViewModel.insert(
                    Mahasiswa(
                        nim = nim,
                        nama = nama,
                        jurusan = jurusan,
                        photoResId = R.drawable.avatar1
                    )
                )
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
