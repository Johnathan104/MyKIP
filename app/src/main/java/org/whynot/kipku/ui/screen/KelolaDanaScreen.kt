package org.whynot.kipku.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import org.whynot.kipku.data.Mahasiswa
import org.whynot.kipku.data.RiwayatDana
import org.whynot.kipku.ui.viewModel.UserViewModel
import com.example.mykip.util.exportTransaksiPdf
import org.whynot.kipku.ui.viewModel.MahasiswaViewModel
import org.whynot.kipku.ui.viewModel.RiwayatDanaViewModel

import org.whynot.kipku.R
@Composable
fun KelolaDanaScreen(
    userViewModel: UserViewModel,
    riwayatViewModel: RiwayatDanaViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    navController: NavController
) {
    val scrollState = rememberScrollState()

    val user = userViewModel.loggedInUser
    val isMahasiswa = user?.role == "mahasiswa"
    val isOrtu = user?.role == "orangTua"

    var riwayatList by remember { mutableStateOf<List<RiwayatDana>>(emptyList()) }
    var currentMahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }

    LaunchedEffect(Unit) {
        if (isMahasiswa || isOrtu) {
            riwayatViewModel.getByNim(user!!.nim) {
                riwayatList = it
            }
        }
    }

    LaunchedEffect(user?.nim) {
        if (user != null) {
            mahasiswaViewModel.getMahasiswaByNim(user.nim) { mhs ->
                currentMahasiswa = mhs
            }
        }
    }

    val totalMasuk = riwayatList
        .filter { it.jenis == "Transfer kepada Mahasiswa" }
        .sumOf { it.jumlah }
    val totalKeluar = riwayatList
        .filter { it.jenis == "Transfer oleh Mahasiswa" }
        .sumOf { it.jumlah }

    // --- WRAP HALAMAN DALAM SCROLLABLE COLUMN ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Laporan Transaksi",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    exportTransaksiPdf(
                        context = navController.context,
                        namaUser = user?.nama ?: "-",
                        nim = user?.nim ?: "-",
                        jenjang = currentMahasiswa?.jenjang ?: "-",
                        jurusan = currentMahasiswa?.jurusan ?: "-",
                        kuliah = currentMahasiswa?.kuliah ?: "-",
                        semester = currentMahasiswa?.semester ?: 0,
                        totalMasuk = totalMasuk,
                        totalKeluar = totalKeluar,
                        saldo = user?.balance ?: 0,
                        riwayat = riwayatList
                    )
                },
                modifier = Modifier
                    .height(32.dp)
                    .wrapContentWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pdf),
                        contentDescription = "PDF",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Export PDF", fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF1FF))
        ) {
            Column(Modifier.padding(16.dp)) {
                InfoItem("Nama", user?.nama ?: "-")
                InfoItem("NIM", user?.nim ?: "-")
                InfoItem("Jenjang", currentMahasiswa?.jenjang ?: "-")
                InfoItem("Jurusan", currentMahasiswa?.jurusan ?: "-")
                InfoItem("Kuliah", currentMahasiswa?.kuliah ?: "-")
                InfoItem("Semester", currentMahasiswa?.semester?.toString() ?: "-")

                Spacer(Modifier.height(10.dp))

                InfoItem("Saldo Saat Ini", "Rp ${user?.balance}")
                InfoItem("Total Masuk", "Rp $totalMasuk")
                InfoItem("Total Keluar", "Rp $totalKeluar")
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Riwayat Transaksi", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        // DAFTAR JUGA DIMASUKKAN DALAM COLUMN SCROLL
        riwayatList.forEach { riwayat ->
            RiwayatItemStyled(
                r = riwayat,
                onClick = {}
            )
            Spacer(Modifier.height(8.dp))
        }

    }
}



