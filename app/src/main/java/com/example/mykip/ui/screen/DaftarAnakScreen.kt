package com.example.mykip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mykip.data.Anak
import com.example.mykip.data.User
import com.example.mykip.data.contohAnak
import com.example.mykip.repository.UserRepository
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel

data class AnakUI(
    val nim: String,
    val nama: String,
    val jurusan: String,
    val danaTersisa: Int,
    val danaTerpakai: Int,
    val photoResId: Int
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarAnakScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    riwayatViewModel: RiwayatDanaViewModel
) {
    var query by remember { mutableStateOf("") }

    var anakList by remember { mutableStateOf<List<AnakUI>>(emptyList()) }
    var mahasiswaList by remember { mutableStateOf(emptyList<com.example.mykip.data.Mahasiswa>()) }
    var userList by remember { mutableStateOf(emptyList<User>()) }

    val riwayatList by riwayatViewModel.riwayatList.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getAllUsers { userList = it }
        mahasiswaViewModel.getAll { mhsList ->
            mahasiswaList = mhsList
            riwayatViewModel.getAll()
        }
    }

    LaunchedEffect(mahasiswaList, userList, riwayatList) {
        if (mahasiswaList.isNotEmpty() && userList.isNotEmpty()) {
            val mahasiswaNonAdmin = mahasiswaList.filter { mhs ->
                val tiedUser = userList.find { it.nim == mhs.nim }
                tiedUser?.role == "mahasiswa"
            }

            anakList = mahasiswaNonAdmin.map { mhs ->
                val tiedUser = userList.find { it.nim == mhs.nim }
                val danaMasuk = tiedUser?.balance ?: 0
                val riwayatMhs = riwayatList.filter { it.nim == mhs.nim }
                val danaKeluar = riwayatMhs.filter { !it.goingIn }.sumOf { it.jumlah }

                AnakUI(
                    nim = mhs.nim,
                    nama = mhs.nama,
                    jurusan = mhs.jurusan,
                    danaTersisa = danaMasuk,
                    danaTerpakai = danaKeluar,
                    photoResId = mhs.photoResId
                )
            }
        }
    }

    val filteredList = anakList.filter {
        it.nama.contains(query, true) ||
                it.jurusan.contains(query, true) ||
                it.nim.contains(query)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Daftar Mahasiswa KIP",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // Search Bar modern
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Cari mahasiswa...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp)
            )

            Text(
                text = "Menampilkan ${filteredList.size} mahasiswa",
                modifier = Modifier.padding(start = 20.dp, bottom = 4.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )

            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { anak ->
                    CardMahasiswaUI(anak) {
                        navController.navigate("detailAnak/${anak.nim}")
                    }
                }
            }
        }
    }
}


@Composable
fun CardMahasiswaUI(anak: AnakUI, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {

            // Avatar bulat
            Image(
                painter = painterResource(id = anak.photoResId),
                contentDescription = anak.nama,
                modifier = Modifier
                    .size(68.dp)
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(50))
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    anak.nama,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    anak.jurusan,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Row dana
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DanaBadge(label = "Tersisa", value = anak.danaTersisa)
                    DanaBadge(label = "Terpakai", value = anak.danaTerpakai)
                }
            }
        }
    }
}

@Composable
fun DanaBadge(label: String, value: Int) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                "Rp $value",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

