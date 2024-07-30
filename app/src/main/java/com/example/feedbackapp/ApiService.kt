package com.example.feedbackapp

import com.example.feedbackapp.bean.FeedbackRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("category/list")
    suspend fun getUser(): ApiResponse<Map<String, String>>

    @POST("add")
    suspend fun addFeedback(@Body feedback: FeedbackRequest): ApiResponse<Int>
}