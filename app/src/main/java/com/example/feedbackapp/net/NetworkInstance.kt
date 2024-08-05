package com.example.feedbackapp.net

import android.util.Log
import com.example.feedbackapp.bean.FeedbackHistoryBean
import com.example.feedbackapp.bean.FeedbackRequest
import com.example.feedbackapp.bean.ScoreBean
import com.example.feedbackapp.common.BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.withTimeout
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
        withTimeout(5000) {
            val response = apiService.getProblemScene()
            emit(response)
        }
    }.flowOn(Dispatchers.IO)
        .retry(retries = 1) { e ->
            e is IOException
        }.catch { e ->
            Log.e("mini", "Error occurred: ${e.message}")
            emit(ApiResponse(
                returnCode = -1,
                message = "Error occurred: ${e.message}",
                result = emptyMap()
            ))
        }

    fun getFeedbackId(): Flow<ApiResponse<Int>> = flow {
        withTimeout(5000) {
            val response = apiService.getFeedbackId()
            emit(response)
        }
    }.flowOn(Dispatchers.IO)
        .retry(retries = 1) { e ->
            e is IOException
        }.catch { e ->
            Log.e("mini", "Error occurred: ${e.message}")
            emit(ApiResponse(
                returnCode = -1,
                message = "Error occurred: ${e.message}",
                result = -1
            ))
        }

    fun addScore(scoreBean: ScoreBean): Flow<ApiResponse<Int>> = flow {
        withTimeout(5000) {
            val response = apiService.addScore(scoreBean.userId, scoreBean.score)
            Log.w("addScore", "addScore: ${scoreBean}")
            emit(response)
        }
    }.flowOn(Dispatchers.IO)
        .retry(retries = 1) { e ->
            e is IOException
        }.catch { e ->
            Log.e("FeedbackViewModel", "Error occurred: ${e.message}")
            emit(ApiResponse(
                returnCode = -1,
                message = "Error occurred: ${e.message}",
                result = -1
            ))
        }

    fun submitFeedback(feedbackRequest: FeedbackRequest): Flow<ApiResponse<Int>> = flow {
        withTimeout(5000) {
            val response = apiService.addFeedback(feedbackRequest)
            Log.w("FeedbackViewModel", "submitFeedback: $response")
            emit(response)
        }
    }.flowOn(Dispatchers.IO)
        .retry(retries = 1) { e ->
            e is IOException
        }.catch { e ->
            Log.e("FeedbackViewModel", "Error occurred: ${e.message}")
            emit(ApiResponse(
                returnCode = -1,
                message = "Error occurred: ${e.message}",
                result = -1
            ))
        }

    fun getFeedbackHistory(userId: Int): Flow<ApiResponse<List<FeedbackHistoryBean>>> = flow {
        withTimeout(5000) {
            val response = apiService.getFeedbackHistory(userId)
            Log.w("FeedbackViewModel", "submitFeedback: $response")
            emit(response)
        }
    }.flowOn(Dispatchers.IO)
        .retry(retries = 1) { e ->
            e is IOException
        }.catch { e ->
            Log.e("FeedbackViewModel", "Error occurred: ${e.message}")
            emit(ApiResponse(
                returnCode = -1,
                message = "Error occurred: ${e.message}",
                result = emptyList()
            ))
        }

    fun uploadFiles(id: Int, filePaths: List<String>): Flow<ApiResponse<Int>> = flow {
        withTimeout(50000) {
            val parts = filePaths.map { filePath ->
                Log.w("gyk", "uploadFiles: 调用")
                val file = File(filePath)
                if (!file.exists()) {
                    Log.w("gyk", "uploadFiles: 文件为空")
                }
                val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            }
            Log.w("gyk", "uploadFiles: 调用2")
            val response = apiService.uploadFiles(id, parts)
            emit(response)
        }
    }.flowOn(Dispatchers.IO)
        .retry(retries = 1) { e ->
            e is IOException
        }.catch { e ->
            Log.e("uploadFiles", "Error occurred: ${e.message}")
            emit(ApiResponse(
                returnCode = -1,
                message = "Error occurred: ${e.message}",
                result = -1
            ))
        }


}