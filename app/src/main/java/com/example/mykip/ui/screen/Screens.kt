package com.example.mykip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.mykip.data.Mahasiswa
import com.example.mykip.R
import com.example.mykip.ui.viewModel.UserViewModel


@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_ukrida),
                contentDescription = "Logo Ukrida",
                modifier = Modifier
                    .size(150.dp) // ukuran logo bisa disesuaikan
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tanjung Duren",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: UserViewModel,
    onLogout: () -> Unit // callback untuk navigasi setelah logout
) {
    val state = viewModel.uiState
    val user = viewModel.loggedInUser // asumsi ViewModel punya properti user yang login

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // Kolom kiri: gambar + teks
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

            // Kolom label & value
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.LightGray),
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
                    // NIM
                    Row {
                        Text(text = ":", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = user?.nim ?: "-", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    // Email
                    Row {
                        Text(text = ":", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = user?.email ?: "-", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Tombol Logout
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    viewModel.logout() // pastikan ada fungsi logout di ViewModel
                    onLogout()
                }
            ) {
                Text(text = "Logout", fontWeight = FontWeight.Bold)
            }
        }
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