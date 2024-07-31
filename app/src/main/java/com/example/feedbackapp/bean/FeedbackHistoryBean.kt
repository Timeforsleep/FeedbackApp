package com.example.feedbackapp.bean

import com.google.gson.annotations.SerializedName

data class FeedbackHistoryBean(
    val id: Int,
    val targetId: Int,
    val targetType: Int,
    val userId: Long,
    val deviceId: String,
    val status: Int?,
    val category: Int,
    val tagId: Int,
    val tagName: String,
    val content: String,
    val relation: String,
    val photos: String?,
    val video: String?,
    @SerializedName("createdStime")
    val createdTime: String,
    @SerializedName("modifiedStime")
    val modifiedTime: String,
    val isDel: Int,
    val photoList: List<String>,
    val children: List<FeedbackHistoryBean>?
) {
}