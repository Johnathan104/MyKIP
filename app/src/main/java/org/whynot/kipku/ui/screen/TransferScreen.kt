package org.whynot.kipku.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.whynot.kipku.ui.viewModel.UserViewModel
import kotlinx.coroutines.delay
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import org.whynot.kipku.utils.ImageConverter
import org.whynot.kipku.R
import org.whynot.kipku.data.Mahasiswa

import org.whynot.kipku.ui.viewModel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    orangTuaViewModel: OrangTuaViewModel,
    riwayatViewModel: RiwayatDanaViewModel
) {
    val context = LocalContext.current
    val user = userViewModel.loggedInUser ?: return

    val isOrtu = user.role == "orangTua"
    val isMahasiswa = user.role == "mahasiswa"
    var currentMhs by remember { mutableStateOf<Mahasiswa?>(null) }

    var selectedSemester by remember { mutableStateOf<Int?>(null) }
    var semesterExpanded by remember { mutableStateOf(false) }
    var showImageSourceSheet by remember { mutableStateOf(false) }


    val semesterOptions = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8"
    )
    var jumlah by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }

    // Animasi tombol
    var buttonPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (buttonPressed) 0.95f else 1f)

    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }

    var triggerTransfer by remember { mutableStateOf(false) }

    // ⭐ Launcher pilih gambar
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val stream = context.contentResolver.openInputStream(uri)
                val bytes = stream?.readBytes()
                selectedImage = bytes

                if (bytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    base64Image = ImageConverter.bitmapToBase64(bitmap)
                    Log.d("BASE64_RESULT", base64Image.toString().take(100)) // cek 100 karakter pertama
                }
            }
        }
    )

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            selectedImage = ImageConverter.bitmapToByteArray(it)
            base64Image = ImageConverter.bitmapToBase64(it)
        }
    }

    LaunchedEffect(Unit){
        mahasiswaViewModel.getByNim(user.nim){
            currentMhs = it
        }
        if(isMahasiswa){
            mahasiswaViewModel.getByNim(user.nim){
                currentMhs = it
            }
        }
    }

    LaunchedEffect(triggerTransfer) {
        if (triggerTransfer) {
            delay(150)
            buttonPressed = false
            triggerTransfer = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {

        // HEADER GRADIENT MODERN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFF4C8CFF),
                            Color(0xFF6EA8FF),
                            Color(0xFF82B6FF)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Text(
                text = "Transfer Dana",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 16.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-32).dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            // ScrollState to handle scrolling
            val scrollState = rememberScrollState()

            // Wrap the Column inside the Modifier.verticalScroll
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState) // Enable scrolling
            ) {
                // Input for Amount (Jumlah)
                Text(
                    "Jumlah Transfer",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = jumlah,
                    onValueChange = { jumlah = it },
                    label = { Text("Masukan jumlah transfer") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_money),
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(20.dp))

                // Input for Description (Keterangan)
                Text(
                    "Keterangan",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Contoh: Uang makan, keperluan kampus") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_note),
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(14.dp)
                )

                if (currentMhs != null) {
                    Text(
                        "Semester",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(4.dp))

                    // Semester Dropdown
                    ExposedDropdownMenuBox(
                        expanded = semesterExpanded,
                        onExpandedChange = { semesterExpanded = !semesterExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSemester?.let { "Semester $it" } ?: "Pilih Semester",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Semester") },
                            trailingIcon = {
                                TrailingIcon(expanded = semesterExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = semesterExpanded,
                            onDismissRequest = { semesterExpanded = false }
                        ) {
                            (1..currentMhs!!.semester).forEach { semester ->
                                DropdownMenuItem(
                                    text = { Text(semester.toString()) },
                                    onClick = {
                                        selectedSemester = semester
                                        semesterExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                }

                // Image Upload (Bukti Transfer)
                Text(
                    "Bukti Transfer",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(6.dp))

                // Card Upload Gambar (Image Upload Box)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (selectedImage == null) 160.dp else 200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF2F4F7))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFBDBDBD),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { showImageSourceSheet = true }
                ) {
                    if (selectedImage == null) {
                        // Placeholder when no image is uploaded
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Upload,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Klik untuk upload bukti transfer",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        // Display the uploaded image
                        AsyncImage(
                            model = selectedImage,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.1f)),
                            contentDescription = "Preview Bukti",
                            contentScale = ContentScale.Fit
                        )

                        // Remove Image Button
                        IconButton(
                            onClick = { selectedImage = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(28.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Hapus gambar",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Transfer Button with Scale Animation
                Button(
                    onClick = {
                        val nominal = jumlah.toIntOrNull() ?: run {
                            Toast.makeText(context, "Jumlah tidak valid!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (base64Image.isNullOrEmpty()) {
                            Toast.makeText(context, "Silakan upload bukti transfer!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (keterangan.isEmpty()) {
                            Toast.makeText(context, "Isi keterangan terlebih dahulu!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Submit transfer request
                        if (isMahasiswa) {
                            userViewModel.penarikan(
                                nim = user.nim,
                                jumlah = nominal,
                                keterangan = keterangan,
                                buktiTransfer = base64Image!!,
                                riwayatViewModel = riwayatViewModel,
                                semester =selectedSemester!!
                            )
                        }

                        Toast.makeText(context, "Berhasil mengajukan transfer", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Transfer")
                }

                Spacer(Modifier.height(8.dp))
            }
        }
        if (showImageSourceSheet) {
            ModalBottomSheet(
                onDismissRequest = { showImageSourceSheet = false },
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Text(
                        "Pilih Sumber Gambar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // 📷 Kamera
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageSourceSheet = false
                                takePhotoLauncher.launch(null)
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Ambil dari Kamera")
                    }

                    Divider()

                    // 🖼 Galeri
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageSourceSheet = false
                                pickImageLauncher.launch("image/*")
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Pilih dari Galeri")
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }
        }

    }
}


