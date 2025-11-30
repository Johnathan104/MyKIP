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
import androidx.compose.material.icons.filled.Visibility


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
        if (isMahasiswa) "Rp. ${user!!.balance}" else "-"

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
                            text = displayedRole,
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

        item {
            // Grid tidak scroll sendiri → aman
            FeatureGrid(navController)
        }
    }
}



@Composable
fun FeatureGrid(navController:NavController) {
    val features = listOf(
        FeatureItem("Account and Card", R.drawable.ic_account, Color(0xFF3E57FF)),
        FeatureItem("Transfer", R.drawable.ic_transfer, Color(0xFFFF3366)),
        FeatureItem("Withdraw", R.drawable.ic_atm, Color(0xFF008CFF)),
        FeatureItem("Mobile recharge", R.drawable.ic_mobile, Color(0xFFFFA833)),
        FeatureItem("Pay the bill", R.drawable.ic_bill, Color(0xFF00B894)),
        FeatureItem("Credit card", R.drawable.ic_creditcard, Color(0xFFFF6B3D)),
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


    // ======================================
    // UI
    // ======================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        // ============================
        // HEADER
        // ============================
        Text(
            text = "Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ============================
        // CARD GRADIENT (seperti gambar)
        // ============================
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF43A3F3),
                                Color(0xFF6A5AF9)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {

                Column {

                    // NOMOR KARTU
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "12345678912356",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text(
                                text = "Card Holder Name",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = displayedEmail,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }

                        Column {
                            Text(
                                text = "Expiry date",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "02/30",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Switch(
                                    checked = true,
                                    onCheckedChange = {},
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color.White.copy(alpha = 0.4f)
                                    )
                                )
                            }
                        }

                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ============================
        // DETAIL INFORMATION TITLE
        // ============================
        Text(
            text = "DETAIL INFORMATION",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = Color(0xFF9E9E9E)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isMahasiswa) {
            Text("Name"); ReadOnlyField(displayedNama)
            Spacer(Modifier.height(14.dp))

            Text("E-mail"); ReadOnlyField(displayedEmail)
            Spacer(Modifier.height(14.dp))

            Text("NIM"); ReadOnlyField(displayedNim)
            Spacer(Modifier.height(14.dp))

            Text("Role"); ReadOnlyField(displayedRole)
        }

        if (isOrtu) {
            Text("Name"); ReadOnlyField(displayedNama)
            Spacer(Modifier.height(14.dp))

            Text("E-mail"); ReadOnlyField(displayedEmail)
            Spacer(Modifier.height(14.dp))

            Text("NIM Anak"); ReadOnlyField(displayedNimAnak)
            Spacer(Modifier.height(14.dp))

            Text("Role"); ReadOnlyField(displayedRole)
        }

        if (isAdmin) {
            Text("Name"); ReadOnlyField(displayedNama)
            Spacer(Modifier.height(14.dp))

            Text("E-mail"); ReadOnlyField(displayedEmail)
            Spacer(Modifier.height(14.dp))

            Text("NIM"); ReadOnlyField(displayedNim)
            Spacer(Modifier.height(14.dp))

            Text("Role"); ReadOnlyField(displayedRole)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Spacer(modifier = Modifier.height(32.dp))

        Divider(
            color = Color.LightGray.copy(alpha = 0.4f),
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ============================
        // LOGOUT BUTTON (tetap)
        // ============================
        Button(
            onClick = {
                viewModel.logout()
                orangTuaViewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Logout", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

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