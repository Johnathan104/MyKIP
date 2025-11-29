package com.example.mykip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mykip.R
import com.example.mykip.datastore.OnboardingDataStore
import kotlinx.coroutines.launch

@Composable
fun LandingPage(
    dataStore: OnboardingDataStore,
    onFinished: () -> Unit
) {

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3A86FF),
                        Color(0xFF7B2CBF)
                    )
                )
            )
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.card_illustration),
                contentDescription = null,
                modifier = Modifier
                    .height(220.dp)
                    .width(250.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Jane Cooper",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        }

        // Tombol panah
        Button(
            onClick = {
                scope.launch {
                    dataStore.setCompleted(true)
                    onFinished()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(65.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "Next",
                tint = Color.Black
            )
        }
    }
}

