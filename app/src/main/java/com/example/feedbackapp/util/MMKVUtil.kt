package com.example.feedbackapp.util

import com.tencent.mmkv.MMKV

object MMKVUtil {

    private val mmkv: MMKV by lazy {
        MMKV.defaultMMKV()
    }

    // 存储字符串
    fun putString(key: String, value: String) {
        mmkv.encode(key, value)
    }

    // 读取字符串
    fun getString(key: String, defaultValue: String = ""): String {
        return mmkv.decodeString(key, defaultValue) ?: defaultValue
    }

    // 存储整型
    fun putInt(key: String, value: Int) {
        mmkv.encode(key, value)
    }

    // 读取整型
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return mmkv.decodeInt(key, defaultValue)
    }

    // 存储布尔值
    fun putBoolean(key: String, value: Boolean) {
        mmkv.encode(key, value)
    }

    // 读取布尔值
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return mmkv.decodeBool(key, defaultValue)
    }

    // 存储长整型
    fun putLong(key: String, value: Long) {
        mmkv.encode(key, value)
    }

    // 读取长整型
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return mmkv.decodeLong(key, defaultValue)
    }

    // 存储浮点数
    fun putFloat(key: String, value: Float) {
        mmkv.encode(key, value)
    }

    // 读取浮点数
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return mmkv.decodeFloat(key, defaultValue)
    }

    // 删除数据
    fun removeKey(key: String) {
        mmkv.removeValueForKey(key)
    }

    // 清空所有数据
    fun clearAll() {
        mmkv.clearAll()
    }
}