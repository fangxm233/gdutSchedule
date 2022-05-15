package com.fangxm.schedule.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AttendanceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "这是考勤界面"
    }
    val text: LiveData<String> = _text
}