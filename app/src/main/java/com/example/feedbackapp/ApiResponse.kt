package com.example.feedbackapp

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("returncode")
    val returnCode: Int,
    val message: String,
    val result: T
)