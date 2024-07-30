package com.example.feedbackapp.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

object CommonUtil {
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                it.getString(columnIndex)
            } else {
                null
            }
        }
    }

    fun createImageUri(context: Context): Uri? {
        val file = try {
            createImageFile(context)
        } catch (ex: IOException) {
            null
        }
        return file?.let {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it)
        }
    }

    private fun createImageFile(context: Context): File {
        val timeStamp = System.currentTimeMillis().toString()
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
}