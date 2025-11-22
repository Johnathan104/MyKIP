// RegisterScreen.kt
package com.example.mykip.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mykip.ui.viewModel.UserViewModel


@Composable
fun RegisterScreen(viewModel: UserViewModel, onNavigateToLogin: () -> Unit) {
    var nim by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    val state = viewModel.uiState


    Column(
        Modifier.padding(20.dp). fillMaxSize().fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
            ,modifier = Modifier.fillMaxWidth()

        )
        Spacer(modifier = Modifier.height(32.dp))


        OutlinedTextField(value = nim,

            onValueChange = { nim = it },
            label = { Text("NIM") },
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp).fillMaxWidth())


        OutlinedTextField(value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))


        Button(onClick =
            { viewModel.register(nim, email, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }


        if (state.isLoading) {
            CircularProgressIndicator()
        }


        if (state.message.isNotEmpty()) {
            Text(state.message)
        }


        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login")
        }
    }
}