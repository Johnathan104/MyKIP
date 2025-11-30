package com.example.mykip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mykip.data.*
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel
import com.google.firebase.Timestamp
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
    fun getTodayDate(): Timestamp {
        return Timestamp(Date()) // Create a new Timestamp from the current Date
    }
    var mahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }
    var riwayatList by remember { mutableStateOf<List<RiwayatDana>>(emptyList()) }
    var showWithdrawDialouge by remember {mutableStateOf(false)}

    // Load data from Room
    LaunchedEffect(anakNim) {
        mahasiswaViewModel.getByNim(anakNim) { mahasiswa = it }
        riwayatViewModel.getByNim(anakNim) { riwayatList = it }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Mahasiswa (Admin View)") },
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
                .padding(16.dp)
        ) {

            mahasiswa?.let { mhs ->

                // === PROFILE DATA ===
                Row {
                    Image(
                        painter = painterResource(id = mhs.photoResId),
                        contentDescription = mhs.nama,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 16.dp)
                    )

                    Column {
                        Text(
                            mhs.nama,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text("NIM: ${mhs.nim}")
                        Text("Jurusan: ${mhs.jurusan}")
                    }
                }

                Spacer(Modifier.height(20.dp))

                // === TITLE ===
                Text(
                    "Riwayat Penggunaan Dana",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { showWithdrawDialouge = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Beri Dana", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))

                // === LIST RIWAYAT ===
                if (riwayatList.isEmpty()) {
                    Text("Belum ada riwayat dana.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(riwayatList) { item ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.tanggal.toDate().toString(), fontWeight = FontWeight.Bold)
                                        Text("Jumlah: Rp ${item.jumlah}")
                                        Text("Tipe: ${if (item.goingIn) "Pemasukan" else "Pengeluaran"}")
                                        Text("Keterangan: ${item.keterangan}")
                                    }

                                    // ======== DELETE BUTTON ========
                                    Button(
                                        onClick = {
                                            riwayatViewModel.delete(item)

                                            // Remove from local UI list
                                            riwayatList = riwayatList.filter { it.id != item.id }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        ),
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .align(Alignment.CenterVertically)
                                    ) {
                                        Text("Hapus")
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

    }
    if (showWithdrawDialouge) {
        DepositDialog(
            onDismiss = { showWithdrawDialouge = false },
            onSubmit = { jumlah, keterangan ->
                userViewModel.penyetoran(
                    nim = anakNim,
                    jumlah = jumlah,
                    keterangan = keterangan,
                    riwayatViewModel = riwayatViewModel
                )
                riwayatList = riwayatList+RiwayatDana(
                    nim = anakNim,
                    jumlah = jumlah,
                    keterangan = keterangan,
                    goingIn = false,
                    tanggal = getTodayDate()
                )


                showWithdrawDialouge = false
            }
        )
    }

}
