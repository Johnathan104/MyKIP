package com.example.mykip.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mykip.ui.viewModel.UserViewModel

import com.example.mykip.viewmodel.*

@Composable
fun TransferScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    orangTuaViewModel: OrangTuaViewModel,
    riwayatViewModel: RiwayatDanaViewModel
) {
    val user = userViewModel.loggedInUser ?: return

    val isOrtu = user.role == "orangTua"
    val isMahasiswa = user.role == "mahasiswa"

    var jumlah by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Text("Transfer Dana", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = jumlah,
            onValueChange = { jumlah = it },
            label = { Text("Jumlah") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = keterangan,
            onValueChange = { keterangan = it },
            label = { Text("Keterangan") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val nominal = jumlah.toIntOrNull() ?: return@Button

                if (isMahasiswa) {
//                    mahasiswaViewModel.transferMahasiswa(
//                        nim = user.nim,
//                        jumlah = nominal,
//                        keterangan = keterangan,
//                        riwayatViewModel = riwayatViewModel
//                    )
                    userViewModel.penarikan(
                        nim = user.nim,
                        jumlah = nominal,
                        keterangan = keterangan,
                        riwayatViewModel = riwayatViewModel)
                }

                if (isOrtu) {
                    orangTuaViewModel.transferKeAnak(
                        nimAnak = user.nim,
                        jumlah = nominal,
                        keterangan = keterangan,
                        riwayatViewModel = riwayatViewModel
                    )
                }

                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Transfer")
        }
    }
}

