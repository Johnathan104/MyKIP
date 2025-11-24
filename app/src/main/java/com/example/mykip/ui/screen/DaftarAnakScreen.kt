package com.example.mykip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mykip.data.Anak
import com.example.mykip.data.contohAnak
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel

data class AnakUI(
    val nim: String,
    val nama: String,
    val jurusan: String,
    val danaTersisa: Int,
    val danaTerpakai: Int,
    val photoResId: Int
)@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarAnakScreen(
    navController: NavController,
    mahasiswaViewModel: MahasiswaViewModel,
    riwayatViewModel: RiwayatDanaViewModel
) {
    var query by remember { mutableStateOf("") }
    var anakList by remember { mutableStateOf<List<AnakUI>>(emptyList()) }

    // collect riwayat flow
    val riwayatList by riwayatViewModel.riwayatList.collectAsState()

    // Load Mahasiswa once
    LaunchedEffect(Unit) {
        mahasiswaViewModel.getAll { mahasiswaList ->

            // Load semua riwayat dari DB
            riwayatViewModel.getAll()

            // setiap kali riwayatList berubah → recompute daftar anak
            anakList = mahasiswaList.map { mhs ->

                val riwayatMhs = riwayatList.filter { it.nim == mhs.nim }

                val totalMasuk = riwayatMhs.filter { it.goingIn }.sumOf { it.jumlah }
                val totalKeluar = riwayatMhs.filter { !it.goingIn }.sumOf { it.jumlah }

                AnakUI(
                    nim = mhs.nim,
                    nama = mhs.nama,
                    jurusan = mhs.jurusan,
                    danaTersisa = totalMasuk - totalKeluar,
                    danaTerpakai = totalKeluar,
                    photoResId = mhs.photoResId
                )
            }
        }
    }

    val filtered = remember(query, anakList) {
        anakList.filter {
            val q = query.lowercase()
            it.nama.lowercase().contains(q) ||
                    it.jurusan.lowercase().contains(q) ||
                    it.nim.contains(q)
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Daftar Mahasiswa KIP") }) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Cari mahasiswa...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Text(
                text = "Menampilkan ${filtered.size} mahasiswa",
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { anak ->
                    CardAnakUI(anak) {
                        navController.navigate("detailAnak/${anak.nim}")
                    }
                }
            }
        }
    }
}


@Composable
fun CardAnakUI(anak: AnakUI, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Row(modifier = Modifier.padding(12.dp)) {

            Image(
                painter = painterResource(id = anak.photoResId),
                contentDescription = anak.nama,
                modifier = Modifier.size(70.dp)
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(anak.nama, style = MaterialTheme.typography.titleMedium)
                Text("Jurusan: ${anak.jurusan}")
                Text("Dana Tersisa: Rp ${anak.danaTersisa}")
                Text("Dana Terpakai: Rp ${anak.danaTerpakai}")
            }
        }
    }
}
