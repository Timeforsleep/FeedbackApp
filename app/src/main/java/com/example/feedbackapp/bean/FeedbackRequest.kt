package com.example.feedbackapp.bean

import com.example.feedbackapp.common.EMERGENCY_NORMAL
import com.example.feedbackapp.common.FUC_ERROR
import com.example.feedbackapp.common.REPLY_TYPE_FEEDBACK

// 请求参数数据类
data class FeedbackRequest(
    var id:Int = 0,
    var targetId: Int = 0,
    var targetType: Int = REPLY_TYPE_FEEDBACK,
    var userId: Int,
    var deviceId: String,
    var status: Int= EMERGENCY_NORMAL,
    var category: Int = FUC_ERROR,
    var tagId: Int,
    var tagName: String,
    var content: String,
    var startTime:Int?,
    var endTime:Int?,
    var relation: String?,
)/*{
    var photoFiles: List<File>?=null
}*/