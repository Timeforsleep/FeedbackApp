package com.example.feedbackapp.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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


    fun createMultipartBodyPart(uri: Uri, contentResolver: ContentResolver): MultipartBody.Part {
        val file = File(getFilePathFromUri(uri, contentResolver))
        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaType(), file)
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    // 辅助函数获取文件路径
    fun getFilePathFromUri(uri: Uri, contentResolver: ContentResolver): String {
        val cursor = contentResolver.query(uri, arrayOf("_data"), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndexOrThrow("_data")
                return it.getString(index)
            }
        }
        throw IllegalArgumentException("Invalid URI")
    }


//    fun getFilePathFromUri(context: Context, uri: Uri): String? {
//        var filePath: String? = null
//        val cursor: Cursor? = context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                filePath = it.getString(columnIndex)
//            }
//        }
//        return filePath
//    }
//    fun createMultipartBodyParts(uris: List<Uri>, contentResolver: ContentResolver): List<MultipartBody.Part> {
//        return uris.map { uri ->
//            val file = File(getFilePathFromUri(uri, contentResolver))
//            val requestFile = RequestBody.create("multipart/form-data".toMediaType(), file)
//            MultipartBody.Part.createFormData("file", file.name, requestFile)
//        }
//    }
//
//    fun compressImage(uri: Uri, contentResolver: ContentResolver, outputFile: File, maxWidth: Int = 800, maxHeight: Int = 800, quality: Int = 80): File {
//        val inputStream = contentResolver.openInputStream(uri)
//        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
//        BitmapFactory.decodeStream(inputStream, null, options)
//
//        val (srcWidth, srcHeight) = options.outWidth to options.outHeight
//        var scaleFactor = 1
//
//        if (srcWidth > maxWidth || srcHeight > maxHeight) {
//            scaleFactor = Math.min(srcWidth / maxWidth, srcHeight / maxHeight)
//        }
//
//        val finalOptions = BitmapFactory.Options().apply { inSampleSize = scaleFactor }
//        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, finalOptions)
//
//        FileOutputStream(outputFile).use { out ->
//            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, out)
//        }
//
//        return outputFile
//    }

//    fun compressVideo(inputFilePath: String, outputFilePath: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
//        val command = arrayOf("-i", inputFilePath, "-vcodec", "libx264", "-crf", "28", outputFilePath)
//
//        FFmpeg.executeAsync(command) { _, returnCode ->
//            if (returnCode == com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS) {
//                onSuccess()
//            } else {
//                val error = com.arthenica.mobileffmpeg.Config.getLastCommandOutput()
//                onFailure(error ?: "Unknown error occurred")
//            }
//        }
//    }

    fun compressVideo(context: Context, inputFilePath: String, outputFilePath: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val command = arrayOf("-i", inputFilePath, "-vf", "boxblur=5:1", "-preset", "superfast",outputFilePath)

        RxFFmpegInvoke.getInstance().runCommandRxJava(command).subscribe(object : RxFFmpegSubscriber() {
            override fun onFinish() {
                onSuccess()
            }

            override fun onProgress(progress: Int, progressTime: Long) {
                // 更新进度
            }

            override fun onCancel() {
                onFailure("Compression canceled")
            }

            override fun onError(message: String) {
                onFailure(message)
            }
        })
    }

    suspend fun compressMedia(context: Context, inputFilePath: String, outputFilePath: String): Boolean = suspendCancellableCoroutine { continuation ->
        val command = arrayOf("ffmpeg", "-y", "-i", inputFilePath, "-vcodec", "libx264", "-crf", "28", outputFilePath)
        Log.w("gyk", "compressMedia: $inputFilePath $outputFilePath")

        RxFFmpegInvoke.getInstance().runCommandRxJava(command).subscribe(object : RxFFmpegSubscriber() {
            override fun onFinish() {
                continuation.resume(true)
            }

            override fun onProgress(progress: Int, progressTime: Long) {
                Log.w("gykprocess", "onProgress: $progress")
            }

            override fun onCancel() {
                continuation.resume(false) // Compression canceled
            }

            override fun onError(message: String) {
                continuation.resumeWithException(Exception(message))
            }
        })
    }

    @Throws(IOException::class)
    fun createTempFile(context: Context, prefix: String?, suffix: String?): File {
        // 获取缓存目录
        val cacheDir = context.cacheDir


        // 创建临时文件
        val tempFile = File.createTempFile(prefix, suffix, cacheDir)

        return tempFile
    }

    fun removeFileExtension(filePath: String): String {
        val lastIndexOfDot = filePath.lastIndexOf('.')
        return if (lastIndexOfDot != -1) {
            filePath.substring(0, lastIndexOfDot)
        } else {
            filePath // 如果没有找到点（即没有后缀），则返回原始字符串
        }
    }

}