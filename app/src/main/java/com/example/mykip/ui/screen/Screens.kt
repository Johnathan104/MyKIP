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
import com.example.mykip.BuildConfig
import com.example.mykip.data.Mahasiswa
import com.example.mykip.R
import com.example.mykip.data.RiwayatDana
import com.example.mykip.data.contohAnak
import com.example.mykip.data.riwayatUntuk
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.ui.screen.DaftarAnakScreen
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.OrangTuaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel


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
    orangTuaViewModel: OrangTuaViewModel,
    navController: NavController,
    mahasiswaViewModel: MahasiswaViewModel,
    riwayatViewModel: RiwayatDanaViewModel,
    onLogout: () -> Unit
) {
    val user = viewModel.loggedInUser
    val orangTua = orangTuaViewModel.loggedInOrtu

    // ======================================
    // DETECT ROLE
    // ======================================
    val isOrtu = orangTua != null
    val isAdmin = user?.isAdmin == true
    val isMahasiswa = user != null && !isAdmin

    // ======================================
    // LOAD DATA
    // ======================================
    var mahasiswaList by remember { mutableStateOf(emptyList<Mahasiswa>()) }
    var riwayatList by remember { mutableStateOf(emptyList<RiwayatDana>()) }

    LaunchedEffect(Unit) {
        mahasiswaViewModel.getAll { mahasiswaList = it }

        if (isMahasiswa) {
            riwayatViewModel.getByNim(user!!.nim) {
                riwayatList = it
            }
        }
        if (isOrtu) {
            riwayatViewModel.getByNim(orangTua!!.anakNim) {
                riwayatList = it
            }
        }
    }

    val jumlahAnak = mahasiswaList.size
    val transaksiMasuk = riwayatList.count { it.goingIn }
    val transaksiKeluar = riwayatList.count { !it.goingIn }

    val displayedNim =
        when {
            isMahasiswa -> user!!.nim
            isOrtu -> orangTua!!.anakNim
            else -> "-"
        }

    val displayedEmail =
        when {
            isMahasiswa -> user!!.email
            isOrtu -> orangTua!!.email
            else -> "-"
        }

    val displayedRole =
        when {
            isOrtu -> "Orang Tua"
            isAdmin -> "Admin"
            else -> "Mahasiswa"
        }

    val totalSaldo =
        if (isMahasiswa) "Rp. ${user!!.balance}" else "-"


    // ======================================
    // UI
    // ======================================
    Column(
        Modifier.fillMaxSize().padding(18.dp)
    ) {

        // --------------------------------------
        // FOTO & HEADER
        // --------------------------------------
        Row(verticalAlignment = Alignment.Top) {
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
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // --------------------------------------
            // DATA USER / ORANGTUA
            // --------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.width(60.dp)) {
                    Text("NIM", fontWeight = FontWeight.Medium)
                    Text("Email", fontWeight = FontWeight.Medium)
                    Text("Role", fontWeight = FontWeight.Medium)
                }

                Column(modifier = Modifier.padding(start = 2.dp)) {

                    // NIM
                    Row {
                        Text(":", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = displayedNim,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // EMAIL
                    Row {
                        Text(":", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = displayedEmail,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // ROLE
                    Row {
                        Text(":", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = displayedRole,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAdmin) Color(0xFF1565C0) else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --------------------------------------
        // PANEL INFORMASI
        // --------------------------------------
        Text(
            text = "Informasi Dana KIP",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9F0FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {

            Column(Modifier.padding(16.dp)) {

                // Mahasiswa only
                if (isMahasiswa) {
                    InfoItem("Total Saldo KIP", totalSaldo)
                    InfoItem("Transaksi Masuk", "$transaksiMasuk transaksi")
                    InfoItem("Transaksi Keluar", "$transaksiKeluar transaksi")

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { navController.navigate("kelolaDana") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kelola Dana KIP", fontWeight = FontWeight.Bold)
                    }
                    // ========================
                    // DEBUG: TOGGLE ADMIN
                    // ========================
                    if (BuildConfig.DEBUG) {

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                user?.let {
                                    val updated = it.copy(isAdmin = !it.isAdmin)
                                    viewModel.updateUser(updated)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray
                            )
                        ) {
                            Text(
                                text = if (user?.isAdmin == true)
                                    "Switch to User (Logout first to see effect)"
                                else
                                    "Switch to Admin  (Logout first to see effect)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                }

                // Admin only
                if (isAdmin) {
                    InfoItem("Jumlah Mahasiswa Terdaftar", "$jumlahAnak mahasiswa")
                }

                // Orang Tua only
                if (isOrtu) {
                    InfoItem("Nama Anak", orangTua!!.anakNim)
                    InfoItem("Transaksi Masuk", "$transaksiMasuk transaksi")
                    InfoItem("Transaksi Keluar", "$transaksiKeluar transaksi")
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --------------------------------------
        // LOGOUT
        // --------------------------------------
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                    viewModel.logout()
                    orangTuaViewModel.logout()
                    onLogout()
                }
            ) {
                Text("Logout", fontWeight = FontWeight.Bold)
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