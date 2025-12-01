package com.example.mykip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.mykip.R
import com.example.mykip.data.RiwayatDana
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun KelolaDanaScreen(
    userViewModel: UserViewModel,
    riwayatViewModel: RiwayatDanaViewModel,
    navController: NavController
) {
    val user = userViewModel.loggedInUser
    val isMahasiswa = user?.role == "mahasiswa"
    val isOrtu = user?.role == "orangTua"

    var riwayatList by remember { mutableStateOf<List<RiwayatDana>>(emptyList()) }

    // ambil transaksi sesuai role
    LaunchedEffect(Unit) {
        if (isMahasiswa) {
            riwayatViewModel.getByNim(user!!.nim) { riwayatList = it }
        }

        if (isOrtu) {
            riwayatViewModel.getByNim(user!!.nim) { riwayatList = it }
        }
    }

    val totalMasuk = riwayatList.filter { it.goingIn }.sumOf { it.jumlah }
    val totalKeluar = riwayatList.filter { !it.goingIn }.sumOf { it.jumlah }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Laporan Transaksi",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF1FF))
        ) {
            Column(Modifier.padding(16.dp)) {
                InfoItem("Saldo Saat Ini", "Rp ${user?.balance}")
                InfoItem("Total Masuk", "Rp $totalMasuk")
                InfoItem("Total Keluar", "Rp $totalKeluar")
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Riwayat Transaksi", fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(riwayatList) { riwayat ->
                RiwayatItemStyled(riwayat)
            }
        }
    }
}

