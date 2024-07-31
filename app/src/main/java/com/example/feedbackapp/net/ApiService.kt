package com.example.feedbackapp.net

import com.example.feedbackapp.bean.FeedbackRequest
import com.example.feedbackapp.bean.FeedbackHistoryBean
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("category/list")
    suspend fun getProblemScene(): ApiResponse<Map<String, String>>

    @POST("add")
    suspend fun addFeedback(@Body feedback: FeedbackRequest): ApiResponse<Int>

    @GET("list")
    suspend fun getFeedbackHistory(@Query("userId") userId: Int): ApiResponse<List<FeedbackHistoryBean>>

}