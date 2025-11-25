package com.example.mykip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mykip.ui.theme.MyKIPTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mykip.data.Mahasiswa
import com.example.mykip.R
import com.example.mykip.data.contohAnak
import com.example.mykip.data.riwayatUntuk
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.ui.screen.DaftarAnakScreen


@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_ukrida),
                contentDescription = "Logo Ukrida",
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Nama aplikasi
            Text(
                text = "Sistem Manajemen Dana KIP",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Deskripsi singkat aplikasi
            Text(
                text = "Aplikasi ini membantu mahasiswa penerima KIP dalam mengelola pencairan dana, memantau penggunaan dana, serta memastikan transparansi dan akuntabilitas.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Card fitur
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Fitur Utama",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureItem("• Melihat jadwal pencairan dana KIP")
                    FeatureItem("• Melihat riwayat transaksi dana KIP")
                    FeatureItem("• Melacak penggunaan dana per semester")
                    FeatureItem("• Mengunggah bukti penggunaan dana")
                    FeatureItem("• Notifikasi informasi pencairan terbaru")
                }
            }
        }
    }
}

// Item bullet point
@Composable
fun FeatureItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}


@Composable
fun ProfileScreen(
    viewModel: UserViewModel,
    navController: NavController,
    onLogout: () -> Unit
) {
    val state = viewModel.uiState
    val user = viewModel.loggedInUser
    // --- DATA DUMMY (nanti bisa diganti ViewModel) ---
    val totalSaldo = "Rp."+user?.balance.toString()
    val jumlahAnak = 38
    val transaksiMasuk = 14
    val transaksiKeluar = 9

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // FOTO DAN INFO UKRIDA
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(150.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_ukrida),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(6.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Universitas Kristen Krida Wacana",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // DATA USER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.width(60.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "NIM", fontWeight = FontWeight.Medium)
                    Text(text = "Email", fontWeight = FontWeight.Medium)
                }

                Column(
                    modifier = Modifier.padding(start = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row {
                        Text(text = ":", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = user?.nim ?: "-",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row {
                        Text(text = ":", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = user?.email ?: "-",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ========================
        // PANEL ADMIN DANA KIP
        // ========================
        Text(
            text = "Informasi Dana KIP",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9F0FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(label = "Total Saldo KIP", value = totalSaldo)
                InfoItem(label = "Jumlah Anak Terdaftar", value = "$jumlahAnak anak")
                InfoItem(label = "Transaksi Masuk", value = "$transaksiMasuk transaksi")
                InfoItem(label = "Transaksi Keluar", value = "$transaksiKeluar transaksi")

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        // TODO: NAVIGASI KE HALAMAN ADMIN
                        navController.navigate("kelolaDana")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kelola Dana KIP", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // LOGOUT
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                }
            ) {
                Text(text = "Logout", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// COMPONENT UNTUK ITEM INFO
@Composable
fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}



@Composable
fun SearchScreen() {
    MyKIPTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DaftarMahasiswa(mhs = contohMhs())
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DaftarAnakScreen(nav: NavHostController) {
//
//    var query by rememberSaveable { mutableStateOf("") }
//    val anakList = contohAnak()
//
//    val filtered = anakList.filter {
//        it.nama.contains(query, ignoreCase = true) ||
//                it.kelas.contains(query, ignoreCase = true)
//    }
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Daftar Anak") }
//            )
//        }
//    ) { inner ->
//        Column(modifier = Modifier.padding(inner)) {
//
//            OutlinedTextField(
//                value = query,
//                onValueChange = { query = it },
//                label = { Text("Cari anak...") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            )
//
//            LazyColumn(
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(filtered) { anak ->
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                nav.navigate("detailAnak/${anak.id}")
//                            },
//                        elevation = CardDefaults.cardElevation(4.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier.padding(12.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Image(
//                                painter = painterResource(id = anak.photoResId),
//                                contentDescription = anak.nama,
//                                modifier = Modifier
//                                    .size(70.dp)
//                                    .clip(RoundedCornerShape(8.dp))
//                            )
//
//                            Spacer(modifier = Modifier.width(12.dp))
//
//                            Column {
//                                Text(anak.nama, fontWeight = FontWeight.Bold)
//                                Text("Kelas: ${anak.kelas}")
//                                Text("Tersisa: Rp ${anak.danaTersisa}")
//                                Text("Terpakai: Rp ${anak.danaTerpakai}")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAnakScreen(anakId: String, nav: NavHostController) {

    val anak = contohAnak().find { it.id == anakId }
    val riwayat = riwayatUntuk(anakId)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Anak") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { inner ->

        if (anak == null) {
            Text("Data tidak ditemukan", modifier = Modifier.padding(16.dp))
            return@Scaffold
        }

        Column(modifier = Modifier.padding(inner).padding(16.dp)) {

            // HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = anak.photoResId),
                    contentDescription = anak.nama,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(anak.nama, style = MaterialTheme.typography.titleLarge)
                    Text("Kelas: ${anak.kelas}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DANA
            Text("Dana Tersisa: Rp ${anak.danaTersisa}")
            Text("Dana Terpakai: Rp ${anak.danaTerpakai}")

            Spacer(modifier = Modifier.height(20.dp))

            Text("Riwayat Pemakaian Dana", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))

            riwayat.forEach {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(it.tanggal, fontWeight = FontWeight.Bold)
                        Text("Rp ${it.jumlah}")
                        Text(it.keterangan)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarMahasiswa(mhs: List<Mahasiswa>) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }

    val filtered = remember(mhs, query) {
        if (query.isBlank()) mhs
        else {
            val q = query.trim().lowercase()
            mhs.filter { s ->
                s.nama.lowercase().contains(q) ||
                s.nim.lowercase().contains(q) ||
                s.jurusan.lowercase().contains(q)
            }
        }
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Mahasiswa") }
            )
        }
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

            // ✅ Teks hasil pencarian
            Text(
                text = if (query.isBlank())
                    "Menampilkan semua (${mhs.size}) mahasiswa"
                else
                    "Menampilkan ${filtered.size} dari ${mhs.size} mahasiswa",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(filtered) { data ->
                    CardMahasiswa(data)
                }
            }
        }
    }
}

@Composable
fun CardMahasiswa(mhs: Mahasiswa) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = mhs.photoResId),
                contentDescription = "Foto ${mhs.nama}",
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 12.dp)
            )

            Column {
                Text(
                    text = mhs.nama,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "NIM: ${mhs.nim}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Jurusan: ${mhs.jurusan}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun contohMhs(): List<Mahasiswa> = listOf(
    Mahasiswa(
        nim = "200201001",
        nama = "Blessy Jeniffer",
        jurusan = "Informatika",
        photoResId = R.drawable.avatar1
    ),
    Mahasiswa(
        nim = "200201002",
        nama = "Vanda Christie",
        jurusan = "Sistem Informasi",
        photoResId = R.drawable.avatar2
    ),
    Mahasiswa(
        nim = "200201003",
        nama = "Brino Alfaro",
        jurusan = "Informatika",
        photoResId = R.drawable.avatar1
    ),
    Mahasiswa(
        nim = "200201004",
        nama = "Wurrie Anneta",
        jurusan = "Teknik Komputer",
        photoResId = R.drawable.avatar2
    ),
    Mahasiswa(
        nim = "200201005",
        nama = "Prasti Haruko",
        jurusan = "Informatika",
        photoResId = R.drawable.avatar1
    ),
    Mahasiswa(
        nim = "200201006",
        nama = "Molly Travella",
        jurusan = "Teknik Komputer",
        photoResId = R.drawable.avatar2
    )
)

@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}



@Preview(showBackground = true)
@Composable
fun SearchScreenPreview(){
    SearchScreen()
}