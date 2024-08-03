package com.example.feedbackapp.bean

import java.io.File

data class UploadBean(val file: File,var isVideo:Boolean = false) {
//    var isVideo = isVideo
}