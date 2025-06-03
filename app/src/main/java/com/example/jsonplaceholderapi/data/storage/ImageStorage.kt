package com.example.jsonplaceholderapi.data.storage

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object ImageStorage {
    fun saveImage(context: Context, bitmap: Bitmap, fileName: String): String {
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return file.absolutePath
    }
}