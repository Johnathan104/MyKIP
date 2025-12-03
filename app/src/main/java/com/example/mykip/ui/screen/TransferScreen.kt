package com.example.mykip.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.R
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay


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

    // Animasi tombol
    var buttonPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (buttonPressed) 0.95f else 1f)

    var triggerTransfer by remember { mutableStateOf(false) }

    LaunchedEffect(triggerTransfer) {
        if (triggerTransfer) {
            delay(150)
            buttonPressed = false
            triggerTransfer = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {

        // HEADER GRADIENT MODERN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFF4C8CFF),
                            Color(0xFF6EA8FF),
                            Color(0xFF82B6FF)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Text(
                text = "Transfer Dana",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 16.dp)
            )
        }

        // CARD CONTAINER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-32).dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {

                // INPUT JUMLAH
                Text(
                    "Jumlah Transfer",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = jumlah,
                    onValueChange = { jumlah = it },
                    label = { Text("Masukan jumlah transfer") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_money),
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(20.dp))

                // INPUT KETERANGAN
                Text(
                    "Keterangan",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Contoh: Uang makan, keperluan kampus") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_note),
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(28.dp))

                // BUTTON TRANSFER + ANIMASI SCALE
                Button(
                    onClick = {
                        buttonPressed = true
                        val nominal = jumlah.toIntOrNull() ?: return@Button

                        if (isMahasiswa) {
                            userViewModel.penarikan(
                                nim = user.nim,
                                jumlah = nominal,
                                keterangan = keterangan,
                                riwayatViewModel = riwayatViewModel
                            )
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
                        triggerTransfer = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Transfer", fontSize = 16.sp)
                }


                Spacer(Modifier.height(8.dp))
            }
        }
    }
}


