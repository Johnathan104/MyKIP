package com.example.mykip.ui.screen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
            anakList = mahasiswaList.mapNotNull { mhs ->
                val tiedUser = userList.find { it.nim == mhs.nim && it.role == "mahasiswa" }
                if (tiedUser != null) {
                    val riwayatMhs = riwayatList.filter { it.nim == mhs.nim }

                    val danaMasuk = riwayatMhs
                        .filter { it.jenis == "Transfer kepada Mahasiswa" }
                        .sumOf { it.jumlah }

                    val danaTerpakai = riwayatMhs
                        .filter { it.jenis == "Transfer oleh Mahasiswa" }
                        .sumOf { it.jumlah }

                    val danaTersisa = tiedUser.balance

                    AnakUI(
                        nim = mhs.nim,
                        nama = mhs.nama,
                        jurusan = mhs.jurusan,
                        danaTersisa = danaTersisa,
                        danaTerpakai = danaTerpakai,
                        photoResId = mhs.photoResId
                    )

                } else null
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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

    val danaTotal = anak.danaTersisa + anak.danaTerpakai
    val percentageUsed = if (danaTotal == 0) 0f else anak.danaTerpakai.toFloat() / danaTotal

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        // Header Highlight
        Box(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(14.dp)
        ) {
            Text(
                anak.nama,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Column(Modifier.padding(16.dp)) {

            Text(
                anak.nim,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Text(
                anak.jurusan,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "Dana Tersisa",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF27AE60)
                )
            )

            Text(
                "Rp ${"%,.0f".format(anak.danaTersisa.toFloat())}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF27AE60)
                )
            )

            Spacer(Modifier.height(8.dp))

            Divider(thickness = 1.dp, color = Color(0xFFE0E0E0))

            Spacer(Modifier.height(10.dp))

            Text(
                "Dana Terpakai",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFFE74C3C)
                )
            )

            Text(
                "Rp ${"%,.0f".format(anak.danaTerpakai.toFloat())}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE74C3C)
                )
            )

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = percentageUsed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(0xFFE74C3C),
                trackColor = Color(0xFFB9E5D3)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "${"%.1f".format(percentageUsed * 100)}% dana digunakan",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
            )
        }
    }
}



@Composable
fun DanaBadge(label: String, value: Int, isPositive: Boolean) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 600)
    )

    val bgColor = if (isPositive) Color(0xFFD1F2EB) else Color(0xFFFADBD8)
    val textColor = if (isPositive) Color(0xFF16A085) else Color(0xFFC0392B)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = textColor
                )
            )

            Text(
                "Rp ${"%,.0f".format(animatedValue.toFloat())}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }
    }
}



