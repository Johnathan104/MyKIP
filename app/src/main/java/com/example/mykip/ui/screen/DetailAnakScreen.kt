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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAnakScreen(
    anakId: String,
    navController: NavController? = null
) {
    val anak = contohAnak().find { it.id == anakId }
    val riwayat = riwayatUntuk(anakId)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Anak") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
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

            if (anak != null) {

                // DATA ANAK
                Row {
                    Image(
                        painter = painterResource(id = anak.photoResId),
                        contentDescription = anak.nama,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 16.dp)
                    )

                    Column {
                        Text(
                            anak.nama,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Kelas: ${anak.kelas}")
                        Spacer(Modifier.height(6.dp))
                        Text("Dana Tersisa: Rp ${anak.danaTersisa}")
                        Text("Dana Terpakai: Rp ${anak.danaTerpakai}")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // TITLE RIWAYAT
                Text(
                    "Riwayat Penggunaan Dana",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // LIST RIWAYAT
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(riwayat) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    item.tanggal,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Rp ${item.jumlah}")
                                Text(item.keterangan)
                            }
                        }
                    }
                }
            }
        }
    }
}
