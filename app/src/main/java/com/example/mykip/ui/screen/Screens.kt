package com.example.mykip.ui.screen

import android.app.Activity
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mykip.BuildConfig
import com.example.mykip.data.Mahasiswa
import com.example.mykip.R
import com.example.mykip.data.FeatureItem
import com.example.mykip.data.RiwayatDana
import com.example.mykip.data.contohAnak
import com.example.mykip.ui.viewModel.UserViewModel
import com.example.mykip.ui.screen.DaftarAnakScreen
import com.example.mykip.viewmodel.MahasiswaViewModel
import com.example.mykip.viewmodel.OrangTuaViewModel
import com.example.mykip.viewmodel.RiwayatDanaViewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.res.stringResource
import com.example.mykip.MyKIPApp
import com.example.mykip.data.LanguagePreference
import com.example.mykip.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale



@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: UserViewModel,
    orangTuaViewModel: OrangTuaViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    riwayatViewModel: RiwayatDanaViewModel,
) {
    val user = viewModel.loggedInUser

    val isOrtu = user?.role == "orangTua"
    val isAdmin = user?.role == "admin"
    val isMahasiswa = user?.role == "mahasiswa"

    // ======================================
    // LOAD DATA
    // ======================================
    var mahasiswaList by remember { mutableStateOf(emptyList<Mahasiswa>()) }
    var riwayatList by remember { mutableStateOf(emptyList<RiwayatDana>()) }
    var currentMahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }
    var userList by remember { mutableStateOf(emptyList<User>()) }

    LaunchedEffect(Unit) {
        viewModel.getAllUsers { userList = it }   // pastikan kamu punya fungsi ini
    }

    LaunchedEffect(Unit) {
        // ambil semua mahasiswa
        mahasiswaViewModel.getAll { mahasiswaList = it }

        // Mahasiswa → ambil berdasarkan nim user
        mahasiswaViewModel.getByNim(user!!.nim) { mhs ->
            currentMahasiswa = mhs
        }

        // Ambil riwayat berdasarkan user
        riwayatViewModel.listenRiwayatByNim(user!!.nim) { list ->
            riwayatList = list
        }
    }

    val jumlahAnak = mahasiswaList.size
    val transaksiMasuk = riwayatList.count { it.goingIn }
    val transaksiKeluar = riwayatList.count { !it.goingIn }

    val displayedNama = when {
        isMahasiswa -> currentMahasiswa?.nama ?: "-"
        isOrtu -> user?.nama ?: "-"
        isAdmin -> user?.nama ?: "-"
        else -> "-"
    }

    val displayedEmail = user?.email ?: "-"

    val displayedNim = when {
        isMahasiswa -> user?.nim ?: "-"
        isAdmin -> user?.nim ?: "-"
        else -> "-"
    }

    val displayedNimAnak = when {
        isOrtu -> currentMahasiswa?.nim ?: "-"
        else -> "-"
    }

    val displayedRole = when {
        isOrtu -> "Orang Tua"
        isAdmin -> "Admin"
        else -> "Mahasiswa"
    }

    val totalSaldo =
        if (isMahasiswa) "Rp. ${user!!.balance}" else {
            viewModel.getAnakUser(user!!)
            if (viewModel.userAnak != null) {
                "Rp. ${viewModel.userAnak!!.balance}"
            } else {
                "Rp. ---"
            }
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "KIPKu",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // ============================
        // CARD SALDO
        // ============================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF304FFE),
                                    Color(0xFF00BCD4),
                                    Color(0xFF4CAF50)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = displayedNama,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        val finalRoleText = if (displayedRole.lowercase() == "mahasiswa") {
                            "${displayedRole} ${currentMahasiswa?.jenjang ?: "-"}"
                        } else {
                            displayedRole
                        }

                        Text(
                            text = finalRoleText,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text("Total Saldo", color = Color.White)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = totalSaldo,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // ============================
        // DASHBOARD ADMIN (NEW)
        // ============================
        if (isAdmin) {
            item {
                AdminSummaryDashboard(
                    mahasiswaList = mahasiswaList,
                    riwayatList = riwayatList,
                    userList = userList
                )
            }
        }

        // ============================
        // GRID MENU
        // ============================
        item {
            FeatureGrid(navController, userRole = user.role)
            Spacer(modifier = Modifier.height(12.dp))
        }


        // ============================
        // TITLE RIWAYAT
        // ============================
        item {
            Text(
                text = "Riwayat Transaksi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
            )
        }

        // ============================
        // LIST RIWAYAT
        // ============================
        items(riwayatList) { r ->
            RiwayatItemStyled(r)
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
fun AdminSummaryDashboard(
    mahasiswaList: List<Mahasiswa>,
    riwayatList: List<RiwayatDana>,
    userList: List<User>
) {
    val totalMahasiswa = mahasiswaList.size
    val totalTransaksi = riwayatList.size

    val totalDanaMasuk = riwayatList.filter { it.goingIn }.sumOf { it.jumlah }
    val totalDanaKeluar = riwayatList.filter { !it.goingIn }.sumOf { it.jumlah }

    // saldo dari tabel user
    val totalSaldoMahasiswa = userList
        .filter { it.role == "mahasiswa" }
        .sumOf { it.balance }

    val averageSaldo = if (totalMahasiswa > 0) {
        totalSaldoMahasiswa / totalMahasiswa
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                "Dashboard Ringkasan Admin",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(16.dp))

            SummaryRow("Total Mahasiswa", totalMahasiswa.toString())
            SummaryRow("Total Transaksi", totalTransaksi.toString())
            SummaryRow("Total Dana Masuk", "Rp. $totalDanaMasuk")
            SummaryRow("Total Dana Keluar", "Rp. $totalDanaKeluar")
            SummaryRow("Total Saldo Mahasiswa", "Rp. $totalSaldoMahasiswa")
            SummaryRow("Rata-rata Saldo", "Rp. $averageSaldo")
        }
    }
}


@Composable
fun SummaryRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontWeight = FontWeight.Medium)
        Text(value, fontWeight = FontWeight.Bold)
    }
}




@Composable
fun FeatureGrid(
    navController: NavController,
    userRole: String // <-- tambahkan ini
) {

    // ❌ Jika bukan mahasiswa → tidak tampil apa pun
    if (userRole != "mahasiswa") {
        return
    }

    val features = listOf(
        FeatureItem("Transfer", R.drawable.ic_transfer, Color(0xFFFF3366), destination = "transfer"),
        FeatureItem("Transaction report", R.drawable.ic_report, Color(0xFF6C5CE7), destination = "kelolaDana")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 2000.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(features) { item ->

            Column(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(18.dp))
                    .padding(vertical = 18.dp, horizontal = 12.dp)
                    .fillMaxWidth()
                    .clickable {
                        if (item.destination.isNotEmpty()) {
                            navController.navigate(item.destination)
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(item.color.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        tint = item.color,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }
        }
    }
}


@Composable
fun RiwayatItemStyled(r: RiwayatDana) {

    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val tanggalFormatted = dateFormat.format(r.tanggal.toDate())

    // Menentukan jenis transaksi berdasarkan field 'jenis'
    val jenisTransaksi = when (r.jenis) {
        "Transfer kepada Mahasiswa" -> "Pemasukan"
        "Transfer oleh Mahasiswa" -> "Pengeluaran"
        else -> if (r.goingIn) "Pemasukan" else "Pengeluaran"
    }

    val colorJumlah = if (jenisTransaksi == "Pemasukan") Color(0xFF2ECC71) else Color(0xFFE74C3C)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ICON SHOPEE STYLE
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Color(0xFFFF6F00).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_report),
                contentDescription = "",
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = jenisTransaksi,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = r.keterangan, // tetap menampilkan keterangan asli
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = tanggalFormatted,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Text(
            text = (if (jenisTransaksi == "Pemasukan") "+Rp " else "-Rp ") + r.jumlah.toString(),
            color = colorJumlah,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}


// Item bullet point
@Composable
fun FeatureItem(text: String, destination:String = "") {
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

    // ======================================
    // DETECT ROLE
    // ======================================
    val isOrtu = user?.role == "orangTua"
    val isAdmin = user?.role == "admin"
    val isMahasiswa = user?.role == "mahasiswa"

    var mahasiswaList by remember { mutableStateOf(emptyList<Mahasiswa>()) }
    var currentMahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }

    LaunchedEffect(Unit) {
        mahasiswaViewModel.getAll { mahasiswaList = it }
        mahasiswaViewModel.getByNim(user!!.nim) { mhs -> currentMahasiswa = mhs }
    }

    val displayedNama = when {
        isMahasiswa -> currentMahasiswa?.nama ?: "-"
        isOrtu -> user?.nama ?: "-"
        isAdmin -> user?.nama ?: "-"
        else -> "-"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {

        Spacer(Modifier.height(100.dp))

        // ============================
        // FOTO PROFIL DI TENGAH
        // ============================
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(Modifier.height(10.dp))

        // NAMA DI TENGAH
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayedNama,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A4CE1)
            )
        }

        Spacer(Modifier.height(30.dp))

        // ============================
        // MENU ORIGINAL (TIDAK DIUBAH)
        // ============================
        SettingItem(
            icon = R.drawable.ic_profile,
            text = stringResource(id = R.string.setting_profile),
            onClick = { navController.navigate("profileDetail") }
        )

        SettingItem(
            icon = R.drawable.ic_setting,
            text = stringResource(id = R.string.setting_general),
            onClick = { navController.navigate("pengaturanUmum") }
        )

        SettingItem(
            icon = R.drawable.ic_help,
            text = stringResource(id = R.string.setting_help),
            onClick = { /*TODO*/ }
        )

        Spacer(Modifier.height(20.dp))

        // ============================
        // LOGOUT BUTTON
        // ============================
        Button(
            onClick = {
                viewModel.logout()
                orangTuaViewModel.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.setting_logout), color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(40.dp))
    }
}



@Composable
fun SettingItem(icon: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }

    Divider(
        color = Color.LightGray.copy(alpha = 0.3f),
        thickness = 1.dp,
        modifier = Modifier.padding(start = 20.dp)
    )
}
@Composable
fun ProfileDetailScreen(
    navController: NavController,
    viewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    userViewModel: UserViewModel
) {
    val user = viewModel.loggedInUser

    val isOrtu = user?.role == "orangTua"
    val isAdmin = user?.role == "admin"
    val isMahasiswa = user?.role == "mahasiswa"

    var currentMahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    // ---------------- Editable fields ----------------
    var editableNama by remember { mutableStateOf("") }
    var editableNim by remember { mutableStateOf("") }

    // Mahasiswa-only fields
    var editableAlamat by remember { mutableStateOf("") }
    var editableEmailWali by remember { mutableStateOf("") }
    var editableTanggalLahir by remember { mutableStateOf("") } // string for UI display

    var anakNama by remember { mutableStateOf("Kosong / Salah NIM") }

    // ---------------- Load initial data ----------------
    LaunchedEffect(Unit) {
        if (isMahasiswa || isOrtu) {

            if (isOrtu) {
                editableNama = user!!.nama
                editableNim = user.nim
            }

            mahasiswaViewModel.getByNim(user!!.nim) { mhs ->
                currentMahasiswa = mhs
                if (mhs != null) {

                    if (isMahasiswa) {
                        editableNama = mhs.nama
                        editableNim = user.nim
                        if(mhs.alamat != null)
                        editableAlamat = mhs.alamat
                        if (mhs.emailWali != null)
                        editableEmailWali = mhs.emailWali

                        // convert timestamp → yyyy-MM-dd
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        if (mhs.tanggalLahir != null)
                        editableTanggalLahir = sdf.format(mhs.tanggalLahir?.toDate())
                    }

                    if (isOrtu) anakNama = mhs.nama
                }
            }
        } else {
            editableNama = user?.nama ?: ""
            editableNim = user?.nim ?: ""
        }
    }

    val displayedEmail = user?.email ?: "-"
    val displayedRole = when {
        isOrtu -> "Orang Tua"
        isAdmin -> "Admin"
        else -> "Mahasiswa"
    }

    // ================== UI ======================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
    ) {

        // ---------- HEADER ----------
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            // -------- SAVE / EDIT BUTTON --------
            TextButton(onClick = {

                if (isEditing) {
                    // =============================
                    //           SAVE LOGIC
                    // =============================

                    if (isMahasiswa && currentMahasiswa != null) {

                        // Convert string → Timestamp
                        val timestampTanggal = try {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val date = sdf.parse(editableTanggalLahir)
                            if (date != null) com.google.firebase.Timestamp(date)
                            else com.google.firebase.Timestamp.now()
                        } catch (e: Exception) {
                            com.google.firebase.Timestamp.now()
                        }

                        val updatedMhs = currentMahasiswa!!.copy(
                            nama = editableNama,
                            alamat = editableAlamat,
                            emailWali = editableEmailWali,
                            tanggalLahir = timestampTanggal
                        )

                        val updatedUser = user!!.copy(nama = editableNama)

                        mahasiswaViewModel.update(updatedMhs)
                        userViewModel.update(updatedUser)
                    }

                    if (isOrtu) {
                        val updated = user!!.copy(
                            nama = editableNama,
                            nim = editableNim
                        )
                        userViewModel.update(updated)
                    }
                }

                isEditing = !isEditing
            }) {
                Text(if (isEditing) "Selesai" else "Edit")
            }
        }

        // ---------- CONTENT ----------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "DETAIL INFORMATION",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(20.dp))

            // ===============================
            //           NAMA
            // ===============================
            Text("Nama")
            EditableField(editableNama, isEditing, { editableNama = it })
            Spacer(Modifier.height(14.dp))

            // ===============================
            //           EMAIL (readonly)
            // ===============================
            Text("Email")
            ReadOnlyField(displayedEmail)
            Spacer(Modifier.height(14.dp))

            if (isOrtu) {
                Text("Nama Anak")
                ReadOnlyField(anakNama)
                Spacer(Modifier.height(14.dp))
            }

            // ===============================
            //           NIM
            // ===============================
            Text(if (isOrtu) "NIM Anak" else "NIM")
            EditableField(
                editableNim,
                enabled = isEditing && isOrtu,
                onChange = { editableNim = it }
            )
            Spacer(Modifier.height(14.dp))

            // =========================================================
            //               MAHASISWA-ONLY EXTRA FIELDS
            // =========================================================
            if (isMahasiswa) {

                // ---------- Alamat ----------
                Text("Alamat")
                EditableField(editableAlamat, isEditing) { editableAlamat = it }
                Spacer(Modifier.height(14.dp))

                // ---------- Email Wali ----------
                Text("Email Wali")
                EditableField(editableEmailWali, isEditing) { editableEmailWali = it }
                Spacer(Modifier.height(14.dp))

                // ---------- Tanggal Lahir ----------
                Text("Tanggal Lahir (yyyy-MM-dd)")
                EditableField(editableTanggalLahir, isEditing) { editableTanggalLahir = it }
                Spacer(Modifier.height(14.dp))
            }

            // ===============================
            //         ROLE (read only)
            // ===============================
            Text("Role")
            ReadOnlyField(displayedRole)
        }
    }
}


@Composable
fun PengaturanUmumScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    orangTuaViewModel: OrangTuaViewModel,
    riwayatViewModel: RiwayatDanaViewModel
) {
    val context = LocalContext.current
    // PAKAI SINGLETON DARI APPLICATION — JANGAN BIKIN INSTANCE BARU
    val languagePreference = MyKIPApp.languagePreference

    val coroutineScope = rememberCoroutineScope()
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageDialog(
            onDismiss = { showLanguageDialog = false },
            onSelect = { lang ->
                showLanguageDialog = false

                // Simpan pilihan bahasa di IO dispatcher
                coroutineScope.launch(Dispatchers.IO) {
                    languagePreference.setLanguage(lang)
                }

                // Restart activity agar perubahan bahasa langsung terlihat
                (context as? Activity)?.recreate()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = stringResource(id = R.string.general_settings),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ========== PENGATURAN APLIKASI ==========

        SettingRow(
            text = stringResource(id = R.string.language),
            onClick = { showLanguageDialog = true }
        )

        SettingRow(
            text = stringResource(id = R.string.privacy_policy),
            onClick = { /* TODO */ }
        )

        SettingRow(
            text = stringResource(id = R.string.about_app),
            onClick = { /* TODO */ }
        )

        SettingRowWithEndText(
            text = stringResource(id = R.string.version),
            endText = "1.0.0",
            onClick = {}
        )
    }
}

@Composable
fun LanguageDialog(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.choose_language)) },
        text = {
            Column {
                Text(
                    stringResource(id = R.string.indonesian),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect("id") }
                        .padding(12.dp)
                )
                Text(
                    stringResource(id = R.string.english),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect("en") }
                        .padding(12.dp)
                )
            }
        },
        confirmButton = {}
    )
}


@Composable
fun SettingRow(text: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, fontSize = 16.sp)
            Spacer(Modifier.weight(1f))
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
        Divider(color = Color.LightGray.copy(alpha = 0.3f))
    }
}

@Composable
fun SettingRowWithEndText(text: String, endText: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, fontSize = 16.sp)
            Spacer(Modifier.weight(1f))
            Text(endText, fontSize = 14.sp, color = Color.Gray)
        }
        Divider(color = Color.LightGray.copy(alpha = 0.3f))
    }
}


@Composable
fun EditableField(
    value: String,
    enabled: Boolean = false,
    onChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = { if (enabled) onChange(it) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true
    )
}



@Composable
fun ReadOnlyField(value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .background(
                Color(0xFFF2F2F2),
                RoundedCornerShape(10.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
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


