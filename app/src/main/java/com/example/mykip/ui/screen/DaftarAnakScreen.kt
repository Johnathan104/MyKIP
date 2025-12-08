package com.example.mykip.ui.screen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mykip.data.User
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel
import com.example.mykip.ui.viewModel.UserViewModel
import kotlinx.coroutines.launch

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

    // Lists
    var anakList by remember { mutableStateOf<List<AnakUI>>(emptyList()) }
    var mahasiswaList by remember { mutableStateOf(emptyList<com.example.mykip.data.Mahasiswa>()) }
    var userList by remember { mutableStateOf(emptyList<User>()) }
    val riwayatList by riwayatViewModel.riwayatList.collectAsState()

    // Dialog states
    var showDialog by remember { mutableStateOf(false) }
    var selectedMahasiswa by remember { mutableStateOf<com.example.mykip.data.Mahasiswa?>(null) }
    var waliEmail by remember { mutableStateOf("") }
    var originalWaliEmail by remember { mutableStateOf("") }

    // ViewModel UI state
    val uiState = mahasiswaViewModel.uiState

    // Snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutine = rememberCoroutineScope()

    // Load initial data
    LaunchedEffect(Unit) {
        userViewModel.getAllUsers { userList = it }
        mahasiswaViewModel.getAll { mhsList ->
            mahasiswaList = mhsList
            riwayatViewModel.getAll()
        }
    }

    // Map Mahasiswa to AnakUI
    LaunchedEffect(mahasiswaList, userList, riwayatList) {
        if (mahasiswaList.isNotEmpty() && userList.isNotEmpty()) {
            anakList = mahasiswaList.mapNotNull { mhs ->
                val tiedUser = userList.find { it.nim == mhs.nim && it.role == "mahasiswa" }
                if (tiedUser != null) {
                    val riwayatMhs = riwayatList.filter { it.nim == mhs.nim }
                    val danaTerpakai = riwayatMhs
                        .filter { it.jenis == "Transfer oleh Mahasiswa" }
                        .sumOf { it.jumlah }

                    AnakUI(
                        nim = mhs.nim,
                        nama = mhs.nama,
                        jurusan = mhs.jurusan,
                        danaTersisa = tiedUser.balance,
                        danaTerpakai = danaTerpakai,
                        photoResId = mhs.photoResId
                    )
                } else null
            }
        }
    }

    // React to update success / error
    LaunchedEffect(uiState.isSuccess, uiState.error) {
        if (uiState.isSuccess) {
            coroutine.launch {
                snackbarHostState.showSnackbar("Email wali berhasil diperbarui")
            }
            showDialog = false
        }

        uiState.error?.let { err ->
            coroutine.launch {
                snackbarHostState.showSnackbar("Error: $err")
            }
        }
    }

    // Search Filter
    val filteredList = anakList.filter {
        it.nama.contains(query, true) ||
                it.jurusan.contains(query, true) ||
                it.nim.contains(query)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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

                    CardMahasiswaUI(
                        anak = anak,
                        onClick = {
                            navController.navigate("detailAnak/${anak.nim}")
                        },
                        onKasihEmail = {
                            val mhs = mahasiswaList.find { it.nim == anak.nim }
                            selectedMahasiswa = mhs
                            waliEmail = mhs?.emailWali ?: ""
                            originalWaliEmail = waliEmail
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    // ===========================================================
    //               UPDATE EMAIL WALI DIALOG
    // ===========================================================
    if (showDialog && selectedMahasiswa != null) {
        AlertDialog(
            onDismissRequest = {
                if (!uiState.isLoading) showDialog = false
            },
            title = { Text("Email Wali untuk ${selectedMahasiswa!!.nama}") },
            text = {
                Column {
                    OutlinedTextField(
                        value = waliEmail,
                        onValueChange = { waliEmail = it },
                        label = { Text("Email Wali") },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (uiState.isLoading) {
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = selectedMahasiswa!!.copy(emailWali = waliEmail)
                        mahasiswaViewModel.update(updated)
                    },
                    enabled = waliEmail != originalWaliEmail && !uiState.isLoading
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false },
                    enabled = !uiState.isLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun CardMahasiswaUI(
    anak: AnakUI,
    onClick: () -> Unit,
    onKasihEmail: () -> Unit
) {
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

            Text(anak.nim, color = Color.Gray)
            Text(anak.jurusan, color = Color.Gray)

            Spacer(Modifier.height(18.dp))

            Text("Dana Tersisa", color = Color(0xFF27AE60))
            Text(
                "Rp ${"%,.0f".format(anak.danaTersisa.toFloat())}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF27AE60)
                )
            )

            Spacer(Modifier.height(8.dp))
            Divider()

            Spacer(Modifier.height(10.dp))

            Text("Dana Terpakai", color = Color(0xFFE74C3C))
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
                color = Color.DarkGray
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { onKasihEmail() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Kasih Email Wali")
            }
        }
    }
}
