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
import java.text.SimpleDateFormat
import java.util.Locale



@Composable
fun HomeScreen(
    navController:NavController,
    viewModel: UserViewModel,
    orangTuaViewModel: OrangTuaViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    riwayatViewModel: RiwayatDanaViewModel,)
{
    val user = viewModel.loggedInUser

    val isOrtu = user?.role == "orangTua"
    val isAdmin = user?.role == "admin"
    val isMahasiswa = user?.role =="mahasiswa"

    // ======================================
    // LOAD DATA
    // ======================================
    var mahasiswaList by remember { mutableStateOf(emptyList<Mahasiswa>()) }
    var riwayatList by remember { mutableStateOf(emptyList<RiwayatDana>()) }
    var currentMahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }

    LaunchedEffect(Unit) {
        // ambil semua mahasiswa
        mahasiswaViewModel.getAll { mahasiswaList = it }
        // Mahasiswa → ambil berdasarkan nim user
        mahasiswaViewModel.getByNim(user!!.nim) { mhs ->
            currentMahasiswa = mhs
        }

        riwayatViewModel.getByNim(user!!.nim) {
            riwayatList = it
        }

    }

    val jumlahAnak = mahasiswaList.size
    val transaksiMasuk = riwayatList.count { it.goingIn }
    val transaksiKeluar = riwayatList.count { !it.goingIn }

    val displayedNama = when {
        isMahasiswa -> currentMahasiswa?.nama ?: "-"
        isOrtu -> user?.nama ?: "-"       // Nama orang tua
        isAdmin -> user?.nama ?: "-"
        else -> "-"
    }

    val displayedEmail = user?.email ?: "-"

    val displayedNim = when {
        isMahasiswa -> user?.nim ?: "-"   // NIM mahasiswa
        isAdmin -> user?.nim ?: "-"       // Admin boleh melihat NIM user
        else -> "-"                       // Ortu tidak pakai NIM pribadi
    }

    val displayedNimAnak = when {
        isOrtu -> currentMahasiswa?.nim ?: "-"   // NIM anak
        else -> "-"                              // Mahasiswa & admin tidak tampil NIM anak
    }

    val displayedRole = when {
        isOrtu -> "Orang Tua"
        isAdmin -> "Admin"
        else -> "Mahasiswa"
    }

    val totalSaldo =
        if (isMahasiswa) "Rp. ${user!!.balance}" else {
            viewModel.getAnakUser(user!!)
            if(viewModel.userAnak != null){
                "Rp. ${viewModel.userAnak!!.balance}"

            }else{
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
                        Text(
                            text = "$displayedRole ${currentMahasiswa?.jenjang ?: "-"}",
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

        // 🔹 Item pertama: Grid
        item {
            FeatureGrid(navController)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 🔹 Item berikutnya: Judul
        item {
            Text(
                text = "Riwayat Transaksi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
            )
        }

        // 🔹 Semua item riwayat
        items(riwayatList) { r ->
            RiwayatItemStyled(r)
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}



@Composable
fun FeatureGrid(navController:NavController) {
    val features = listOf(
        FeatureItem("Transfer", R.drawable.ic_transfer, Color(0xFFFF3366), destination = "transfer"),
        FeatureItem("Transaction report", R.drawable.ic_report, Color(0xFF6C5CE7), destination = "kelolaDana")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 2000.dp),   // ✔ menetapkan batas tinggi
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
                    .clickable{
                        if(item.destination != ""){
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
                text = if (r.goingIn) "Pemasukan" else "Pengeluaran",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = r.keterangan,
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
            text = (if (r.goingIn) "+Rp " else "-Rp ") + r.jumlah.toString(),
            color = if (r.goingIn) Color(0xFF2ECC71) else Color(0xFFE74C3C),
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
    val isMahasiswa = user?.role =="mahasiswa"

    // ======================================
    // LOAD DATA
    // ======================================
    var mahasiswaList by remember { mutableStateOf(emptyList<Mahasiswa>()) }
//    var riwayatList by remember { mutableStateOf(emptyList<RiwayatDana>()) }
    var currentMahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }

    LaunchedEffect(Unit) {
        // ambil semua mahasiswa
        mahasiswaViewModel.getAll { mahasiswaList = it }
        // Mahasiswa → ambil berdasarkan nim user
        mahasiswaViewModel.getByNim(user!!.nim) { mhs ->
            currentMahasiswa = mhs
        }

//        riwayatViewModel.getByNim(user!!.nim) {
//            riwayatList = it
//        }

    }


    val jumlahAnak = mahasiswaList.size
//    val transaksiMasuk = riwayatList.count { it.goingIn }
//    val transaksiKeluar = riwayatList.count { !it.goingIn }

    val displayedNama = when {
        isMahasiswa -> currentMahasiswa?.nama ?: "-"
        isOrtu -> user?.nama ?: "-"       // Nama orang tua
        isAdmin -> user?.nama ?: "-"
        else -> "-"
    }

    val displayedEmail = user?.email ?: "-"

    val displayedNim = when {
        isMahasiswa -> user?.nim ?: "-"   // NIM mahasiswa
        isAdmin -> user?.nim ?: "-"       // Admin boleh melihat NIM user
        else -> "-"                       // Ortu tidak pakai NIM pribadi
    }

    val displayedNimAnak = when {
        isOrtu -> currentMahasiswa?.nim ?: "-"   // NIM anak
        else -> "-"                              // Mahasiswa & admin tidak tampil NIM anak
    }

    val displayedRole = when {
        isOrtu -> "Orang Tua"
        isAdmin -> "Admin"
        else -> "Mahasiswa"
    }


    val totalSaldo =
        if (isMahasiswa) "Rp. ${user!!.balance}" else "-"


    val displayedJenjang = currentMahasiswa?.jenjang ?: "-"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ================================
        // HEADER ORANYE
        // ================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6A00)) // oranye SeaBank
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Foto Profil
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = displayedNama,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "$displayedRole $displayedJenjang",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================================
        // MENU LIST
        // ================================
        SettingItem(
            icon = R.drawable.ic_profile,
            text = "Profil Saya",
            onClick = { navController.navigate("profileDetail") }
        )

        SettingItem(
            icon = R.drawable.ic_setting,
            text = "Pengaturan Umum",
            onClick = { navController.navigate("pengaturanUmum") }
        )

        SettingItem(
            icon = R.drawable.ic_help,
            text = "Pusat Bantuan",
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ================================
        // LOGOUT
        // ================================
        Button(
            onClick = {
                viewModel.logout()
                orangTuaViewModel.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6A00)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
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

    // Editable fields
    var editableNama by remember { mutableStateOf("") }
    var editableNim by remember { mutableStateOf("") }
    var anakNama by remember{mutableStateOf("Kosong/ salah nim")}
    LaunchedEffect(Unit) {
        if (isMahasiswa || isOrtu) {
            if(isOrtu){
                editableNama = user.nama
                editableNim = user.nim
            }
            mahasiswaViewModel.getByNim(user!!.nim) { mhs ->
                currentMahasiswa = mhs
                if (mhs != null) {

                    if(isOrtu)
                    {
                        anakNama = mhs.nama

                    }else{
                        editableNama = mhs.nama
                        editableNim = user.nim
                    }
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

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // HEADER
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            // BUTTON EDIT / SELESAI
            TextButton(onClick = {
                if (isEditing) {
                    // SAVE
                    if (isMahasiswa) {
                        currentMahasiswa?.let { mhs ->
                            val updated = mhs.copy(
                                nama = editableNama,
                            )
                            val updatedUser = user!!.copy(
                                nama = editableNama,
                            )
                            mahasiswaViewModel.update(updated)
                            userViewModel.update(updatedUser)
                        }
                    }
                    if(isOrtu){
                        val updated = user.copy(
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "DETAIL INFORMATION",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))

        // NAME
        Text("Name")
        EditableField(
            value = editableNama,
            enabled = isEditing,
            onChange = { editableNama = it }
        )
        Spacer(Modifier.height(14.dp))

        // EMAIL (selalu readonly)
        Text("E-mail")
        ReadOnlyField(displayedEmail)
        Spacer(Modifier.height(14.dp))
        if(isOrtu) {
            Text("Nama Anak")
            ReadOnlyField(anakNama)
            Spacer(Modifier.height(14.dp))
        }
        // NIM
        Text(if (isOrtu) "NIM Anak" else "NIM")
        EditableField(
            value = editableNim,
            enabled = isEditing && isOrtu, // anak tidak boleh ganti nim sendiri sementar ortu bisa ganti nim sesuai dengan nim anaknya
            onChange = { editableNim = it }
        )
        Spacer(Modifier.height(14.dp))

        // ROLE (readonly)
        Text("Role")
        ReadOnlyField(displayedRole)
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

@Composable
fun PengaturanUmumScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    orangTuaViewModel: OrangTuaViewModel,
    riwayatViewModel: RiwayatDanaViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Pengaturan Umum",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("• Notifikasi Aplikasi")
        Spacer(modifier = Modifier.height(8.dp))

        Text("• Kebijakan Privasi")
        Spacer(modifier = Modifier.height(8.dp))

        Text("• Tentang Aplikasi")
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("Kembali")
        }
    }
}
