package com.example.feedbackapp.bean

import com.example.feedbackapp.EMERGENCY_NORMAL
import com.example.feedbackapp.FUC_ERROR
import com.example.feedbackapp.REPLY_TYPE_FEEDBACK

// 请求参数数据类
data class FeedbackRequest(
    val targetId: Int = 0,
    val targetType: Int = REPLY_TYPE_FEEDBACK,
    val userId: Int,
    val deviceId: String,
    val status: Int= EMERGENCY_NORMAL,
    val category: Int = FUC_ERROR,
    val tagId: Int,
    val tagName: String,
    val content: String,
    val startTime:String = "0",
    val endTime:String = "24",
    val photos: List<String>?,
    val relation: String?,
    val video:String?
)