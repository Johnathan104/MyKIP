import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String, Int) -> Unit
) {
    var jumlahText by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var isPressed by remember { mutableStateOf(false) }
// Semester state
    var selectedSemester by remember { mutableStateOf("") }
    var semesterExpanded by remember { mutableStateOf(false) }
    val semesterOptions = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8"
    )
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    // Fungsi format ribuan untuk tampilkan di UI
    fun formatRupiah(value: String): String {
        val num = value.replace(".", "").toIntOrNull() ?: 0
        return if (num == 0) "" else NumberFormat.getNumberInstance(Locale("id", "ID")).format(num)
    }

    // Ambil nilai Int dari input yang sudah diformat
    fun parseInput(value: String): Int = value.replace(".", "").toIntOrNull() ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Setor Dana", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = formatRupiah(jumlahText),
                    onValueChange = { jumlahText = it },
                    label = { Text("Jumlah (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ===== Semester Dropdown =====
                ExposedDropdownMenuBox(
                    expanded = semesterExpanded,
                    onExpandedChange = { semesterExpanded = !semesterExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSemester,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Semester") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = semesterExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = semesterExpanded,
                        onDismissRequest = { semesterExpanded = false }
                    ) {
                        semesterOptions.forEach { semester ->
                            DropdownMenuItem(
                                text = { Text(semester) },
                                onClick = {
                                    selectedSemester = semester
                                    semesterExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(parseInput(jumlahText, ), keterangan, parseInput(selectedSemester) ) },
                modifier = Modifier
                    .scale(scale)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .scale(scale)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Batal")
            }
        }
    )
}
