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
    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    val user = userViewModel.loggedInUser
    val nim = user?.nim ?: return
    var riwayatList by remember { mutableStateOf<List<RiwayatDana>>(emptyList()) }
    val (transaksiMasuk, transaksiKeluar) = remember(riwayatList) {
        var masuk = 0
        var keluar = 0
        for (r in riwayatList) {
            if (r.goingIn) masuk += r.jumlah else keluar += r.jumlah
        }
        masuk to keluar
    }



    // LOAD RIWAYAT USER
    LaunchedEffect(nim) {
        riwayatViewModel.getByNim(nim){ riwayatList = it}
    }



    var showWithdrawDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {

        Text(
            text = "Manajemen Dana KIP - $nim",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9F0FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoItem("Total Saldo", "Rp ${user?.balance}")
                InfoItem("Transaksi Masuk", transaksiMasuk.toString())
                InfoItem("Transaksi Keluar", transaksiKeluar.toString())

                Button(
                    onClick = { showWithdrawDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tarik Dana", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Riwayat Transaksi",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(riwayatList) { r ->
                RiwayatItem(r)
            }
        }


        Spacer(modifier = Modifier.height(30.dp))


    }

    if (showWithdrawDialog) {
        WithdrawDialog(
            userViewModel = userViewModel,
            onDismiss = { showWithdrawDialog = false },
            onSubmit = { jumlah, keterangan ->
                userViewModel.penarikan(
                    nim = nim,
                    jumlah = jumlah,
                    keterangan = keterangan,
                    riwayatViewModel = riwayatViewModel,
                    onError = {}
                )

                riwayatList = riwayatList+RiwayatDana(
                    nim = nim,
                    jumlah = jumlah,
                    keterangan = keterangan,
                    goingIn = false,
                    tanggal = getTodayDate()
                )
            }
        )
    }
}
