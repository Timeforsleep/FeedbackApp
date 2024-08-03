package com.example.feedbackapp.util

import com.example.feedbackapp.bean.FeedbackRequest
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object NetUtil {
    fun createMultipartBodyParts(files: List<File>): List<MultipartBody.Part> {
        return files.map { file ->
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photoFiles", file.name, requestFile)
        }
    }

    fun createRequestBody(feedbackRequest: FeedbackRequest): RequestBody {
        val gson = Gson()
        val jsonString = gson.toJson(feedbackRequest)
        return jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

}