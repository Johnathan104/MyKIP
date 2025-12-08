package com.example.mykip.ui.screen

import DepositDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mykip.data.*
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel
import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAnakScreen(
    anakNim: String,
    navController: NavController,
    userViewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    riwayatViewModel: RiwayatDanaViewModel
) {

    fun getTodayDate(): Timestamp = Timestamp(Date())

    var mahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }
    var riwayatList by remember { mutableStateOf<List<RiwayatDana>>(emptyList()) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var userList by remember { mutableStateOf(emptyList<User>()) }
    // Load data
    LaunchedEffect(anakNim) {
        userViewModel.getAllUsers { userList = it }
        mahasiswaViewModel.getByNim(anakNim) { mahasiswa = it }
        riwayatViewModel.getByNim(anakNim) { riwayatList = it }
    }
    var selectedRiwayat by remember { mutableStateOf<RiwayatDana?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detail Mahasiswa",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(id = com.example.mykip.R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            mahasiswa?.let { mhs ->

                // ------------------ PROFILE CARD -------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            mhs.nama,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("NIM: ${mhs.nim}", style = MaterialTheme.typography.bodyMedium)
                        Text("Jurusan: ${mhs.jurusan}", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ------------------ TITLE SECTION -------------------
                // Hitung nilai dana
                val tiedUser = userList.find { it.nim == mhs.nim && it.role == "mahasiswa" }

                val danaMasuk = riwayatList
                    .filter { it.jenis == "Transfer kepada Mahasiswa" }
                    .sumOf { it.jumlah }

                val danaTerpakai = riwayatList
                    .filter { it.jenis == "Transfer oleh Mahasiswa" }
                    .sumOf { it.jumlah }

                val danaTersisa = tiedUser?.balance ?: 0

                val formatter = NumberFormat.getInstance(Locale("id", "ID"))

                Spacer(Modifier.height(16.dp))

// ------------------ SUMMARY DANA -------------------
                Text(
                    "Ringkasan Dana",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Dana Masuk
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Dana Masuk", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            Text("Rp $danaMasuk", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        // Dana Terpakai
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Dana Terpakai", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            Text("Rp $danaTerpakai", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        // Dana Tersisa
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Dana Tersisa", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            Text("Rp $danaTersisa", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))


                Spacer(Modifier.height(12.dp))

                // ======= BUTTON BERIKAN DANA =======
                Button(
                    onClick = { showWithdrawDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "Beri Dana",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ------------------ RIWAYAT LIST -------------------
                if (riwayatList.isEmpty()) {
                    Text(
                        "Belum ada riwayat dana.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(riwayatList.sortedByDescending { it.timestamp }) { item ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedRiwayat = item
                                        showDetailSheet = true
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                val isIncoming = item.jenis == "Transfer kepada Mahasiswa"

                                Column(modifier = Modifier.padding(16.dp)) {

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("id"))
                                                .format(Date(item.timestamp)),
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                        )

                                        Surface(
                                            color = if (isIncoming) Color(0xFFDEF8E6) else Color(0xFFFFE3E3),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = if (isIncoming) "Pemasukan" else "Pengeluaran",
                                                color = if (isIncoming) Color(0xFF1AAE6F) else Color(0xFFD82020),
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    // Status Badge
                                    Row {
                                        val statusColor = when (item.status) {
                                            "approved" -> Color(0xFF2ECC71)
                                            "rejected" -> Color(0xFFE74C3C)
                                            else -> Color(0xFFB0B0B0)
                                        }

                                        Surface(
                                            color = statusColor.copy(alpha = 0.14f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = item.status.uppercase(),
                                                color = statusColor,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    Text("Jumlah Dana", fontWeight = FontWeight.Medium)
                                    Text(
                                        "Rp ${item.jumlah}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = if (isIncoming) Color(0xFF1AAE6F) else Color(0xFFD82020),
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    if (item.keterangan.isNotEmpty()) {
                                        Text("Keterangan", fontWeight = FontWeight.Medium)
                                        Text(item.keterangan)
                                    }

                                    Spacer(Modifier.height(10.dp))
                                }

                                //  Buttons Fixed Bottom Layout
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            riwayatViewModel.delete(item)
                                            riwayatList = riwayatList.filter { it.id != item.id }
                                        },
                                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Hapus")
                                    }

                                    TextButton(
                                        onClick = {
                                            riwayatViewModel.statusRiwayatGanti(item.id, "approved", "admin")
                                        }
                                    ) {
                                        Text("Terima")
                                    }

                                    TextButton(
                                        onClick = {
                                            riwayatViewModel.statusRiwayatGanti(item.id, "rejected", "admin")
                                        }
                                    ) {
                                        Text("Tolak")
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    if (showDetailSheet && selectedRiwayat != null) {
        DetailRiwayatBottomSheet(
            riwayat = selectedRiwayat!!,
            onDismiss = {
                showDetailSheet = false
                selectedRiwayat = null
            }
        )
    }


    // --------------------------------- ADD DANA DIALOG ---------------------------------
    if (showWithdrawDialog) {
        DepositDialog(
            onDismiss = { showWithdrawDialog = false },
            onSubmit = { jumlah, keterangan ->
                // Tetap simpan jumlah asli (Int) ke database
                userViewModel.penyetoran(
                    nim = anakNim,
                    jumlah = jumlah,
                    keterangan = keterangan,
                    riwayatViewModel = riwayatViewModel
                )

                // Tambahkan ke riwayatList lokal
                riwayatList = riwayatList + RiwayatDana(
                    nim = anakNim,
                    jumlah = jumlah, // jumlah asli tetap
                    keterangan = keterangan,
                    goingIn = true,
                    tanggal = getTodayDate()
                )

                // Tutup dialog
                showWithdrawDialog = false
            }
        )
    }

}

