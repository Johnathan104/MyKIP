package com.example.mykip.utils

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageConverter {
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
