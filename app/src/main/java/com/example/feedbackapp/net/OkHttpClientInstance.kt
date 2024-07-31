package com.example.feedbackapp.net

import com.example.feedbackapp.net.HttpDns
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttpClientInstance {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(5,5, TimeUnit.MINUTES))
        .dns(HttpDns())//添加okhttpdns
        .build()
}