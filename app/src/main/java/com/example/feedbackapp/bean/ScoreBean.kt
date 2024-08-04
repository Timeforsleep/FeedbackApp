package com.example.feedbackapp.bean

import com.example.feedbackapp.common.USER_ID
import com.google.gson.annotations.SerializedName

data class ScoreBean(
    @SerializedName("usrId")
    val userId:Int = USER_ID,
    val score:Double
)
