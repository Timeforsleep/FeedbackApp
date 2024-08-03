package com.example.feedbackapp.bean

import com.google.gson.annotations.SerializedName

data class FeedbackHistoryBean(
    var id: Int,
    var targetId: Int,
    var targetType: Int,
    var userId: Int,
    var deviceId: String,
    var status: Int,
    var category: Int,
    var tagId: Int,
    var tagName: String,
    var content: String,
    var relation: String?,
    var schedule:Int?,
//    var photos: String?,
    var video: String?,
    @SerializedName("createdStime")
    var createdTime: String,
    @SerializedName("modifiedStime")
    var modifiedTime: String,
    var isDel: Int,
    @SerializedName("localFile")
    var localFile: List<String>?,
    var children: List<FeedbackHistoryBean>?
) {
}