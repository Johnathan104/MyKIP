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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarAnakScreen(navController: NavController) {

    val anakList = remember { contohAnak() }
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        val q = query.lowercase()
        anakList.filter {
            it.nama.lowercase().contains(q) ||
                    it.kelas.lowercase().contains(q)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Anak KIP") }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            // Search Box
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Cari anak...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Text(
                text = "Menampilkan ${filtered.size} anak",
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { anak ->
                    CardAnak(anak) {
                        navController.navigate("detailAnak/${anak.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun CardAnak(anak: Anak, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Row(
            modifier = Modifier.padding(12.dp)
        ) {

            Image(
                painter = painterResource(id = anak.photoResId),
                contentDescription = anak.nama,
                modifier = Modifier.size(70.dp)
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(anak.nama, style = MaterialTheme.typography.titleMedium)
                Text("Kelas: ${anak.kelas}")
                Text("Dana Tersisa: Rp ${anak.danaTersisa}")
                Text("Dana Terpakai: Rp ${anak.danaTerpakai}")
            }
        }
    }
}
