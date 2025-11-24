package com.example.mykip.data

import com.example.mykip.R

fun contohAnak(): List<Anak> = listOf(
    Anak(
        id = "1",
        nama = "Brino Alfaro",
        kelas = "12 IPA",
        danaTersisa = 1500000,
        danaTerpakai = 500000,
        photoResId = R.drawable.avatar1
    ),
    Anak(
        id = "2",
        nama = "Vanda Christie",
        kelas = "11 IPS",
        danaTersisa = 1300000,
        danaTerpakai = 700000,
        photoResId = R.drawable.avatar2
    )
)

fun riwayatUntuk(id: String): List<RiwayatDana> = listOf(
    RiwayatDana(0, "412022005", "2025-01-02",  false,150000, "Beli susu"),
    RiwayatDana(1, "1234", "2025-01-04",  true,200000, "1"),
    RiwayatDana(2, "2424", "2025-01-10",  false,150000, "2")
)
