package com.example.mykip.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.example.mykip.data.RiwayatDana
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun exportTransaksiPdf(
    context: Context,
    namaUser: String,
    nim: String,
    jenjang: String,
    jurusan: String,
    kuliah: String,
    semester: Int,
    totalMasuk: Int,
    totalKeluar: Int,
    saldo: Int,
    riwayat: List<RiwayatDana>
) {
    try {
        val dirPath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
        val file = File(dirPath, "laporan_transaksi_${System.currentTimeMillis()}.pdf")

        val writer = PdfWriter(file)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc)

        // Judul
        document.add(
            Paragraph("Laporan Dana Mahasiswa")
                .setBold()
                .setFontSize(20f)
        )

        // Info Mahasiswa
        document.add(Paragraph("Nama: $namaUser"))
        document.add(Paragraph("NIM: $nim"))
        document.add(Paragraph("Jenjang: $jenjang"))
        document.add(Paragraph("Jurusan: $jurusan"))
        document.add(Paragraph("Kuliah: $kuliah"))
        document.add(Paragraph("Semester: $semester"))

        document.add(Paragraph("\n"))

        // Ringkasan Dana
        document.add(Paragraph("Saldo Saat Ini: Rp $saldo"))
        document.add(Paragraph("Total Dana Masuk: Rp $totalMasuk"))
        document.add(Paragraph("Total Dana Keluar: Rp $totalKeluar"))

        document.add(Paragraph("\n"))
        document.add(
            Paragraph("Riwayat Transaksi")
                .setBold()
                .setFontSize(16f)
        )

        val format = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

        riwayat.forEach { r ->
            val tanggal = format.format(Date(r.tanggal.seconds * 1000))
            val jenis = if (r.goingIn) "Dana Masuk" else "Dana Keluar"

            document.add(
                Paragraph("$tanggal  |  Rp ${r.jumlah}  |  $jenis  |  ${r.keterangan}")
            )
        }

        document.close()

        Toast.makeText(context, "PDF berhasil dibuat: ${file.absolutePath}", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Gagal membuat PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
