// RegisterScreen.kt
package org.whynot.kipku.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.whynot.kipku.R
import org.whynot.kipku.data.Mahasiswa
import org.whynot.kipku.ui.viewModel.UserViewModel
import org.whynot.kipku.ui.viewModel.*
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RegisterScreen(
    userViewModel: UserViewModel,
    mahasiswaViewModel: MahasiswaViewModel,
    orangTuaViewModel: OrangTuaViewModel,
    onNavigateToLogin: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    // Field universal + mahasiswa
    var nim by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var jurusan by remember { mutableStateOf("") }
    var jenjang by remember { mutableStateOf("") }
    var kuliah by remember { mutableStateOf("") }
    var tanggalLahirString by remember { mutableStateOf("") }

    val jenjangOptions = listOf("S1", "D4", "D3")

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAgree by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val state = userViewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ----------------------------------------------------
    //  VALIDASI ENABLING BUTTON
    // ----------------------------------------------------
    val allFieldsFilled =
        if (selectedTab == 0) {
            // mahasiswa
            nama.isNotEmpty() &&
                    nim.isNotEmpty() &&
                    jurusan.isNotEmpty() &&
                    kuliah.isNotEmpty() &&
                    jenjang.isNotEmpty() &&
                    tanggalLahirString.isNotEmpty() && // 2. ADD TO VALIDATION
                    email.isNotEmpty() &&
                    password.isNotEmpty() &&
                    isAgree
        } else {
            // orang tua
            nama.isNotEmpty() &&
            email.isNotEmpty() &&
            password.isNotEmpty() &&
            isAgree

        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3A86FF), Color(0xFF7B2CBF))
                )
            )
    ) {

        // BACKGROUND CONTENT (Blur if loading)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(if (state.isLoading) 10.dp else 0.dp)
        ) {
            Spacer(Modifier.height(40.dp))

            // WHITE CARD
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
            ) {

                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Welcome to us,",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3A1B8C)
                    )

                    Text(
                        text = "Hello there, create New account",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    Image(
                        painter = painterResource(id = R.drawable.card_illustration),
                        contentDescription = null,
                        modifier = Modifier.size(180.dp)
                    )

                    Spacer(Modifier.height(30.dp))

                    // TABS
                    val tabs = listOf("Mahasiswa/i", "Orang Tua")

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFFF3F3F3),
                        modifier = Modifier.height(50.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        color = if (selectedTab == index) Color(0xFF7B2CBF) else Color.Gray
                                    )
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // FORM
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        }
                    ) { tab ->

                        Column {

                            if (tab == 0) {
                                RoundedInput("Name", nama) { nama = it }
                                RoundedInput("NIM", nim) { nim = it }
                                RoundedInput("Tanggal Lahir (yyyy-mm-dd)", tanggalLahirString) { tanggalLahirString = it }
                                RoundedInput("Jurusan", jurusan) { jurusan = it }
                                RoundedInput("Kuliah", kuliah) { kuliah = it }

                                Spacer(Modifier.height(10.dp))
                                Text("Pilih Jenjang", fontWeight = FontWeight.Bold)

                                jenjangOptions.forEach { option ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    ) {
                                        RadioButton(
                                            selected = option == jenjang,
                                            onClick = { jenjang = option }
                                        )
                                        Text(option)
                                    }
                                }

                                Spacer(Modifier.height(10.dp))
                            }

                            if (tab == 1) {
                                RoundedInput("Nama Orang Tua", nama) { nama = it }
                                Spacer(Modifier.height(10.dp))
                            }

                            RoundedInput("Email", email) { email = it }
                            RoundedPassword("Password", password) { password = it }
                        }
                    }

                    Spacer(Modifier.height(15.dp))

                    // TERMS
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(checked = isAgree, onCheckedChange = { isAgree = it })

                        Text(
                            buildAnnotatedString {
                                append("By creating an account your agree to our ")

                                pushStyle(
                                    SpanStyle(
                                        color = Color(0xFF7B2CBF),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                append("Terms and Conditions")
                                pop()
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }


                    Spacer(Modifier.height(20.dp))

                    // SIGN UP BUTTON
                    Button(
                        onClick = {
                            val isMahasiswa = selectedTab == 0
                            val timestampTanggalLahir = try {
                                val sdf = SimpleDateFormat("yyyy-MM-dd")
                                sdf.isLenient = false  // Prevents wrong dates like 2024-13-99

                                val date = sdf.parse(tanggalLahirString)
                                if (date != null) {
                                    Timestamp(date)
                                } else {
                                    Timestamp.now()
                                }
                            } catch (e: Exception) {
                                Log.e("RegisterScreen", "Date parsing error: ${e.message}")
                                Timestamp.now() // fallback
                            }

                            if (isMahasiswa) {
                                userViewModel.register(
                                    nim = nim,
                                    nama = nama,
                                    email = email,
                                    password = password,
                                    role = "mahasiswa" )

                                mahasiswaViewModel.insert(
                                    Mahasiswa(
                                        nim = nim,
                                        nama = nama,
                                        jurusan = jurusan,
                                        jenjang = jenjang,
                                        kuliah = kuliah,
                                        semester = 1,
                                        tanggalLahir = timestampTanggalLahir,
                                        photoResId = R.drawable.avatar1
                                    )
                                )
                            } else {
                                userViewModel.register(
                                    nim = nim,
                                    nama = nama,  // sebenarnya nama orang tua
                                    email = email,
                                    password = password,
                                    role = "orangTua"
                                )

                            }

                            scope.launch {
                                snackbarHostState.showSnackbar("Register Success!")
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (allFieldsFilled)
                                Color(0xFF7B2CBF)
                            else
                                Color(0xFFDAD7F3),
                            contentColor = Color.White
                        ),
                        enabled = allFieldsFilled
                    ) {
                        Text(
                            text = "Sign up",
                            fontSize = 18.sp
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Row {
                        Text("Have an account?", color = Color.DarkGray)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Sign In",
                            color = Color(0xFF7B2CBF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        }

        // ----------------------------------------------------
        // LOADING OVERLAY
        // ----------------------------------------------------
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // SNACKBAR HOST
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
fun RoundedInput(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF7B2CBF),
            unfocusedBorderColor = Color(0xFFE0E0E0)
        )
    )
}

@Composable
fun RoundedPassword(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF7B2CBF),
            unfocusedBorderColor = Color(0xFFE0E0E0)
        )
    )
}
