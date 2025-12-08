package com.example.mykip.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.format

@Composable
fun RiwayatItem(item: RiwayatDana) {

    // Format date (e.g., 15 January 2025, 13:22)
    val formatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    val formattedDate = formatter.format(Date(item.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // LEFT SIDE — Description + Status
            Column(modifier = Modifier.weight(1f)) {

                // Keterangan as title
                Text(
                    text = if (item.keterangan.isNotEmpty()) item.keterangan else "No description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                // Show "Successfully" only if goingOut (expense)
                if (!item.goingIn) {
                    Text(
                        text = "Successfully",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Date below
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // RIGHT SIDE — Amount
            val amountColor =
                if (item.goingIn) Color(0xFF304FFE)      // Blue like your screenshot
                else Color(0xFFC62828)                   // Red

            Text(
                text = (if (item.goingIn) "+ " else "- ") + "Rp ${item.jumlah}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}
