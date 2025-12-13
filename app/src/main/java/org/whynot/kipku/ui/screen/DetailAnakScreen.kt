package org.whynot.kipku.ui.screen

import DepositDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.whynot.kipku.data.*
import org.whynot.kipku.ui.viewModel.UserViewModel
import org.whynot.kipku.ui.viewModel.MahasiswaViewModelFactory
import org.whynot.kipku.ui.viewModel.*
import com.google.firebase.Timestamp
import org.whynot.kipku.data.Mahasiswa
import org.whynot.kipku.data.RiwayatDana
import org.whynot.kipku.data.User
import java.text.NumberFormat
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

    fun getTodayDate(): Timestamp = Timestamp(Date())

    val uiState = userViewModel.uiState
    var mahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }
    var riwayatList by remember { mutableStateOf<List<RiwayatDana>>(emptyList()) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var userList by remember { mutableStateOf(emptyList<User>()) }

    LaunchedEffect(anakNim) {
        userViewModel.getAllUsers { userList = it }
        mahasiswaViewModel.getByNim(anakNim) { mahasiswa = it }
        riwayatViewModel.getByNim(anakNim) { riwayatList = it }
    }

    var selectedRiwayat by remember { mutableStateOf<RiwayatDana?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detail Mahasiswa",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(id = org.whynot.kipku.R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        // 🔥 GANTI COLUMN MENJADI LazyColumn AGAR SCROLL SATU HALAMAN
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                mahasiswa?.let { mhs ->

                    Spacer(Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                "Informasi Siswa",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            Spacer(Modifier.height(12.dp))

                            DetailRow("NIM", mhs.nim)
                            DetailRow("Nama", mhs.nama)
                            DetailRow("Email Wali", mhs.emailWali ?: "-")
                            DetailRow("Alamat", mhs.alamat ?: "-")
                            DetailRow("Jenjang", mhs.jenjang ?: "-")
                            DetailRow("Kuliah", mhs.kuliah ?: "-")
                            DetailRow("Jurusan", mhs.jurusan)
                            DetailRow("Semester", mhs.semester.toString())

                            DetailRow(
                                "Tanggal Lahir",
                                mhs.tanggalLahir?.let {
                                    SimpleDateFormat("dd MMM yyyy", Locale("id")).format(it.toDate())
                                } ?: "-"
                            )
                        }
                    }

                    val tiedUser = userList.find { it.nim == mhs.nim && it.role == "mahasiswa" }

                    val danaMasuk = riwayatList
                        .filter { it.jenis == "Transfer kepada Mahasiswa" }
                        .sumOf { it.jumlah }

                    val danaTerpakai = riwayatList
                        .filter { it.jenis == "Transfer oleh Mahasiswa" }
                        .sumOf { it.jumlah }

                    val danaTersisa = tiedUser?.balance ?: 0

                    val formatter = NumberFormat.getInstance(Locale("id", "ID"))

                    Text(
                        "Ringkasan Dana",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Dana Masuk", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                Text("Rp $danaMasuk", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Dana Terpakai", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                Text("Rp $danaTerpakai", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Dana Tersisa", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                Text("Rp $danaTersisa", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    if(uiState.message.isNotBlank()){
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center,

                        ){
                            Text(uiState.message, color = MaterialTheme.colorScheme.error )
                        }
                    }
                    Button(
                        onClick = { showWithdrawDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            "Beri Dana",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                }
            }

            if (riwayatList.isEmpty()) {
                item {
                    Text(
                        "Belum ada riwayat dana.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                    )
                }
            } else {
                items(riwayatList.sortedByDescending { it.timestamp }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedRiwayat = item
                                showDetailSheet = true
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {

                        val isIncoming = item.jenis == "Transfer kepada Mahasiswa"

                        Column(modifier = Modifier.padding(16.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("id"))
                                        .format(Date(item.timestamp)),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                )

                                Surface(
                                    color = if (isIncoming) Color(0xFFDEF8E6) else Color(0xFFFFE3E3),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = if (isIncoming) "Pemasukan" else "Pengeluaran",
                                        color = if (isIncoming) Color(0xFF1AAE6F) else Color(0xFFD82020),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Row {
                                val statusColor = when (item.status) {
                                    "approved" -> Color(0xFF2ECC71)
                                    "rejected" -> Color(0xFFE74C3C)
                                    else -> Color(0xFFB0B0B0)
                                }

                                Surface(
                                    color = statusColor.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = item.status.uppercase(),
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Text("Jumlah Dana", fontWeight = FontWeight.Medium)
                            Text(
                                "Rp ${item.jumlah}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = if (isIncoming) Color(0xFF1AAE6F) else Color(0xFFD82020),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )

                            Spacer(Modifier.height(6.dp))

                            if (item.keterangan.isNotEmpty()) {
                                Text("Keterangan", fontWeight = FontWeight.Medium)
                                Text(item.keterangan)
                            }

                            Spacer(Modifier.height(10.dp))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    riwayatViewModel.delete(item)
                                    riwayatList = riwayatList.filter { it.id != item.id }
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Hapus")
                            }

                            TextButton(
                                onClick = {
                                    riwayatViewModel.statusRiwayatGanti(item.id, "approved", "admin")
                                }
                            ) {
                                Text("Terima")
                            }

                            TextButton(
                                onClick = {
                                    riwayatViewModel.statusRiwayatGanti(item.id, "rejected", "admin")
                                }
                            ) {
                                Text("Tolak")
                            }
                        }

                    }
                }
            }
        }
    }

    if (showDetailSheet && selectedRiwayat != null) {
        DetailRiwayatBottomSheet(
            riwayat = selectedRiwayat!!,
            onDismiss = {
                showDetailSheet = false
                selectedRiwayat = null
            }
        )
    }

    if (showWithdrawDialog) {
        DepositDialog(
            onDismiss = { showWithdrawDialog = false },
            onSubmit = { jumlah, keterangan, semester ->
                userViewModel.penyetoran(
                    nim = anakNim,
                    jumlah = jumlah,
                    keterangan = keterangan,
                    riwayatViewModel = riwayatViewModel,
                    semester = semester
                )
                showWithdrawDialog = false
            }
        )
    }

}
