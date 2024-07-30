package com.example.feedbackapp.bean

// 请求参数数据类
data class FeedbackRequest(
    val targetId: Int,
    val targetType: Int,
    val userId: Int,
    val deviceId: String,
    val status: Int,
    val category: Int,
    val tagId: Int,
    val tagName: String,
    val content: String,
    val photos: List<String>,
    val relation: String
)