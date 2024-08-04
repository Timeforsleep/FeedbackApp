package com.example.feedbackapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.feedbackapp.bean.TypeBean

class MainViewModel:ViewModel() {

    val isFucError : MutableLiveData<Boolean> = MutableLiveData(true)

    val questionType:MutableLiveData<Int> = MutableLiveData(0)

    val questionSceneList:MutableList<TypeBean> = mutableListOf()

    val questionSelectedScene:MutableLiveData<TypeBean> = MutableLiveData()

    val isAgreeIssue = false

    val feedbackContent:MutableLiveData<String> = MutableLiveData("")

    val relationNumber:MutableLiveData<String> = MutableLiveData()

    val startTime:MutableLiveData<String> =MutableLiveData("8")

    val endTime: MutableLiveData<String> = MutableLiveData("22")

}
