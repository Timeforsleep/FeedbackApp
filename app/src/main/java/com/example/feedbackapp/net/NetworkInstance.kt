package com.example.feedbackapp.net

import android.util.Log
import com.example.feedbackapp.bean.FeedbackHistoryBean
import com.example.feedbackapp.bean.FeedbackRequest
import com.example.feedbackapp.common.BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException

object NetworkInstance {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClientInstance.okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun getProblemScene(): Flow<ApiResponse<Map<String, String>>> = flow {
        val response = apiService.getProblemScene()
        emit(response)
    }.flowOn(Dispatchers.IO).retry(retries = 1) { e ->
        // 仅在特定条件下重试，例如网络问题
        e is IOException
    }.catch { e ->
        // 处理异常，例如记录日志或发出错误通知
        Log.e("mini", "Error occurred: ${e.message}" )
    }

    fun getFeedbackId(): Flow<ApiResponse<Int>> = flow {
        val response = apiService.getFeedbackId()
        emit(response)
    }.flowOn(Dispatchers.IO).catch { e ->
            // 处理异常，例如记录日志或发出错误通知
            Log.e("mini", "Error occurred: ${e.message}")
        }




    fun submitFeedback(feedbackRequest: FeedbackRequest): Flow<ApiResponse<Int>> = flow {
//        var photoAndVideoParts:List<MultipartBody.Part>? = null
//        feedbackRequest.photoFiles?.let {
//            photoAndVideoParts = NetUtil.createMultipartBodyParts(it)
//        }
//        val photoAndVideoParts = NetUtil.createMultipartBodyParts(feedbackRequest.photoFiles)
        val response = apiService.addFeedback(feedbackRequest)
        Log.w("FeedbackViewModel", "submitFeedback: $response")
        emit(response)
    }.flowOn(Dispatchers.IO)
        .retry(retries = 1) { e ->
            // 仅在特定条件下重试，例如网络问题
            e is IOException
        }
        .catch { e ->
            // 处理异常，例如记录日志或发出错误通知
            Log.e("FeedbackViewModel", "Error occurred: ${e.message}")
            // 这里也可以通过 emit 发送一个错误结果，视需求而定
        }

    fun getFeedbackHistory(userId:Int): Flow<ApiResponse<List<FeedbackHistoryBean>>> = flow {
        val response = apiService.getFeedbackHistory(userId)
        Log.w("FeedbackViewModel", "submitFeedback: $response")
        emit(response)
    }.flowOn(Dispatchers.IO)
        .retry(retries = 1) { e ->
            // 仅在特定条件下重试，例如网络问题
            e is IOException
        }
        .catch { e ->
            // 处理异常，例如记录日志或发出错误通知
            Log.e("FeedbackViewModel", "Error occurred: ${e.message}")
            // 这里也可以通过 emit 发送一个错误结果，视需求而定
        }


//    fun uploadFile(filePath:String): Flow<ApiResponse<Int>> = flow {
//        val file = File(filePath)
//        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
//        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//        val response = apiService.uploadFile(body)
//        emit(response)
//    }.flowOn(Dispatchers.IO)
//    .retry(retries = 1) { e ->
//        // 仅在特定条件下重试，例如网络问题
//        e is IOException
//    }
//    .catch { e ->
//        // 处理异常，例如记录日志或发出错误通知
//        Log.e("NetworkInstance", "Error occurred: ${e.message}")
//        // 可以通过emit发送错误结果，视需求而定
//    }

    fun uploadFiles(id:Int,filePaths: List<String>): Flow<ApiResponse<Int>> = flow {
        val parts = filePaths.map { filePath ->
            val file = File(filePath)
            if (!file.exists()) {
                Log.w("gyk", "uploadFiles: 文件为空", )
            }
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        }
        val response = apiService.uploadFiles(id,parts)
        emit(response)
    }.flowOn(Dispatchers.IO)
        .retry(retries = 1) { e ->
            // 仅在特定条件下重试，例如网络问题
            e is IOException
        }
        .catch { e ->
            // 处理异常，例如记录日志或发出错误通知
            Log.e("uploadFiles", "Error occurred: ${e.message}")
            // 可以通过emit发送错误结果，视需求而定
        }


}