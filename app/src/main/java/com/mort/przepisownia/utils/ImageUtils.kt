package com.mort.przepisownia.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()

        fileName
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}