package com.example.mykip.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mykip.data.RiwayatDana

@Composable
fun RiwayatItem(item: RiwayatDana) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = item.tanggal,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Rp ${item.jumlah}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = if (item.goingIn) "Dana Masuk" else "Dana Keluar",
                color = if (item.goingIn) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.SemiBold
            )

            if (item.keterangan.isNotEmpty()) {
                Text(
                    text = item.keterangan,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}