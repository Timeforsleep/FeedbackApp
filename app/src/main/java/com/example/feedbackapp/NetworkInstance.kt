package com.example.feedbackapp

import android.util.Log
import com.example.feedbackapp.bean.FeedbackRequest
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object NetworkInstance {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClientInstance.okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun getUser(): Flow<ApiResponse<Map<String, String>>> = flow {
        val response = apiService.getUser()
        Log.w("gyk", "getUser:${response} ", )
        emit(response)
    }.flowOn(Dispatchers.IO).retry(retries = 1) { e ->
        // 仅在特定条件下重试，例如网络问题
        e is IOException
    }.catch { e ->
        // 处理异常，例如记录日志或发出错误通知
        Log.e("mini", "Error occurred: ${e.message}" )
    }

    fun submitFeedback(feedbackRequest: FeedbackRequest): Flow<ApiResponse<Int>> = flow {
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

}