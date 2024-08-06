package com.example.feedbackapp.net

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttpClientInstance {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(50, TimeUnit.SECONDS)
        .readTimeout(50, TimeUnit.SECONDS)
        .writeTimeout(50, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(5,5, TimeUnit.MINUTES))
        .dns(HttpDns())//添加okhttpdns
        .build()
}