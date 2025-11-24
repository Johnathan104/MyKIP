package com.example.mykip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mykip.data.*
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAnakScreen(
    anakNim: String,
    navController: NavController,
    mahasiswaViewModel: MahasiswaViewModel,
    riwayatViewModel: RiwayatDanaViewModel
) {
    var mahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }
    var riwayatList by remember { mutableStateOf<List<RiwayatDana>>(emptyList()) }

    // Load data from Room
    LaunchedEffect(anakNim) {
        mahasiswaViewModel.getByNim(anakNim) { mahasiswa = it }
        riwayatViewModel.getByNim(anakNim) { riwayatList = it }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Mahasiswa") },
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
                                Column(Modifier.padding(12.dp)) {
                                    Text(item.tanggal, fontWeight = FontWeight.Bold)
                                    Text("Jumlah: Rp ${item.jumlah}")
                                    Text("Tipe: ${if (item.goingIn) "Pemasukan" else "Pengeluaran"}")
                                    Text("Keterangan: ${item.keterangan}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
