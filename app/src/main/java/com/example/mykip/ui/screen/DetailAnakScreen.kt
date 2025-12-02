package com.example.mykip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mykip.data.*
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel
import com.google.firebase.Timestamp
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

    var mahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }
    var riwayatList by remember { mutableStateOf<List<RiwayatDana>>(emptyList()) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    // Load data
    LaunchedEffect(anakNim) {
        mahasiswaViewModel.getByNim(anakNim) { mahasiswa = it }
        riwayatViewModel.getByNim(anakNim) { riwayatList = it }
    }

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
                .padding(20.dp)
                .fillMaxSize()
        ) {

            mahasiswa?.let { mhs ->

                // ------------------ PROFILE CARD -------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Image(
                            painter = painterResource(id = mhs.photoResId),
                            contentDescription = mhs.nama,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(end = 20.dp)
                                .clip(RoundedCornerShape(20.dp))
                        )

                        Column {
                            Text(
                                mhs.nama,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("NIM: ${mhs.nim}", style = MaterialTheme.typography.bodyMedium)
                            Text("Jurusan: ${mhs.jurusan}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ------------------ TITLE SECTION -------------------
                Text(
                    "Riwayat Penggunaan Dana",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(12.dp))

                // ======= BUTTON BERIKAN DANA =======
                Button(
                    onClick = { showWithdrawDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Beri Dana", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(18.dp))

                // ------------------ RIWAYAT LIST -------------------
                if (riwayatList.isEmpty()) {
                    Text(
                        "Belum ada riwayat dana.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(riwayatList) { item ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                elevation = CardDefaults.cardElevation(3.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {

                                    // HEADER ROW
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            SimpleDateFormat(
                                                "dd MMM yyyy • HH:mm",
                                                Locale("id")
                                            ).format(item.tanggal.toDate()),
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        // Badge pemasukan/pengeluaran
                                        val badgeColor = if (item.goingIn)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.15f)

                                        val textColor = if (item.goingIn)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.error

                                        Surface(
                                            color = badgeColor,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = if (item.goingIn) "Pemasukan" else "Pengeluaran",
                                                color = textColor,
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp,
                                                    vertical = 4.dp
                                                ),
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    Text("Jumlah: Rp ${item.jumlah}", fontWeight = FontWeight.Bold)
                                    Text("Keterangan: ${item.keterangan}")

                                    Spacer(Modifier.height(10.dp))

                                    // DELETE BUTTON RIGHT ALIGNED
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = {
                                                riwayatViewModel.delete(item)
                                                riwayatList = riwayatList.filter { it.id != item.id }
                                            },
                                            colors = ButtonDefaults.textButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text("Hapus")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --------------------------------- ADD DANA DIALOG ---------------------------------
    if (showWithdrawDialog) {
        DepositDialog(
            onDismiss = { showWithdrawDialog = false },
            onSubmit = { jumlah, keterangan ->
                userViewModel.penyetoran(
                    nim = anakNim,
                    jumlah = jumlah,
                    keterangan = keterangan,
                    riwayatViewModel = riwayatViewModel
                )
                riwayatList = riwayatList + RiwayatDana(
                    nim = anakNim,
                    jumlah = jumlah,
                    keterangan = keterangan,
                    goingIn = true,
                    tanggal = getTodayDate()
                )
                showWithdrawDialog = false
            }
        )
    }
}

