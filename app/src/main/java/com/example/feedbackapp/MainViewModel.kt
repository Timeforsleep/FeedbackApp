package com.example.feedbackapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.feedbackapp.bean.TypeBean

class MainViewModel:ViewModel() {
    // 定义一个 LiveData 或其他状态管理器来保存结果
    val typeBeans = MutableLiveData<List<TypeBean>>()
}