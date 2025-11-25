package com.example.mykip.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@Composable
fun DepositDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var jumlahText by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Setor Dana") },
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
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
