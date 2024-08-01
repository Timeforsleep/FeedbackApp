package com.example.feedbackapp.util

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import kotlin.math.roundToInt

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

    // 将 Base64 字符串转换为 Bitmap 并显示在 ImageView 中
    fun loadBase64Image(base64String: String, imageView: ImageView) {
        // 解码 Base64 字符串为字节数组
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

        // 将字节数组转换为 Bitmap
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        // 将 Bitmap 设置到 ImageView
        imageView.setImageBitmap(bitmap)
    }

    fun uriToBase64(uri: Uri, activity: Activity, imageView: ImageView): String? {
        var inputStream: InputStream? = activity.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            // Set inJustDecodeBounds to true to get the dimensions of the image
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)
            inputStream?.close()

            // Calculate the sample size
            val imageWidth = outWidth
            val imageHeight = outHeight
            val viewWidth = imageView.width
            val viewHeight = imageView.height
            inSampleSize = calculateInSampleSize(this, viewWidth, viewHeight)

            // Decode the image with inSampleSize set
            inJustDecodeBounds = false
            inputStream = activity.contentResolver.openInputStream(uri)
        }

        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun uriToBase64(uri: Uri, activity: Activity, reqWidth:Int, reqHeight:Int): String? {
        var inputStream: InputStream? = activity.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            // Set inJustDecodeBounds to true to get the dimensions of the image
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)
            inputStream?.close()

            // Calculate the sample size
//            val imageWidth = outWidth
//            val imageHeight = outHeight
//            val viewWidth = reqWidth
//            val viewHeight = reqHeight
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode the image with inSampleSize set
            inJustDecodeBounds = false
            inputStream = activity.contentResolver.openInputStream(uri)
        }

        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.outHeight to options.outWidth

        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

//    fun createImageUri(context: Context): Uri? {
//        val file = try {
//            createImageFile(context)
//        } catch (ex: IOException) {
//            null
//        }
//        return file?.let {
//            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it)
//        }
//    }

    private fun createImageFile(context: Context): File {
        val timeStamp = System.currentTimeMillis().toString()
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun setBackGroundGray(view: View) {
        view.setBackgroundColor(Color.parseColor("#f0f0f0"))
    }

    fun setBackGroundBlue(view: View) {
        view.setBackgroundColor(Color.parseColor("#EBF4FF"))
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return (dp * (context.resources.displayMetrics.densityDpi / 160f)).roundToInt()
    }
}