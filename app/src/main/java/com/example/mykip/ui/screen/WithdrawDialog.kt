package com.example.mykip.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.mykip.ui.viewModel.UiState
import com.example.mykip.ui.viewModel.UserViewModel

@Composable
fun WithdrawDialog(
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var jumlahText by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var uiState = userViewModel.uiState
    if (uiState.isSuccess) {
        onDismiss()
        userViewModel.resetState()
    }
    if (uiState.isLoading) {
        uiState = UiState(isLoading = false)
    }
    LaunchedEffect(key1 = Unit) {
        userViewModel.resetState()
    }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Tarik Dana") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = jumlahText,
                    onValueChange = { jumlahText = it },
                    label = { Text("Jumlah (Rp)") }
                )

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan") }
                )

                // 🔥 SHOW ERROR / MESSAGE WHEN NOT EMPTY
                if (uiState.message.isNotEmpty()) {
                    Text(
                        text = uiState.message,
                        color = androidx.compose.ui.graphics.Color.Red
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val jumlahInt = jumlahText.toIntOrNull() ?: 0
                onSubmit(jumlahInt, keterangan)
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Batal")
            }
        }
    )
}
