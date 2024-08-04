package com.example.feedbackapp.net

import com.example.feedbackapp.bean.FeedbackHistoryBean
import com.example.feedbackapp.bean.FeedbackRequest
import com.example.feedbackapp.bean.ScoreBean
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {
    @GET("advice/category/list")
    suspend fun getProblemScene(): ApiResponse<Map<String, String>>

    @GET("advice/getid")
    suspend fun getFeedbackId():ApiResponse<Int>

    @POST("advice/add")
    suspend fun addFeedback(
        @Body feedback: FeedbackRequest): ApiResponse<Int>

    @POST("/advice/score")
    suspend fun addScore(
        @Body scoreBean: ScoreBean
    ):ApiResponse<Int>

    @GET("advice/list")
    suspend fun getFeedbackHistory(@Query("userId") userId: Int): ApiResponse<List<FeedbackHistoryBean>>

    @Multipart
    @POST("files/upload")
    suspend fun uploadFiles(@Part("id") id: Int,@Part files: List<MultipartBody.Part>): ApiResponse<Int>
}

