package com.example.feedbackapp.util

import android.app.Activity

@Suppress("DEPRECATION")
object ScreenUtil {
    fun getWindowHeight(context: Activity): Int {
        return context.windowManager.defaultDisplay.height
    }

    fun getWindowWidth(context: Activity): Int {
        return context.windowManager.defaultDisplay.width
    }
}