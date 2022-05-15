package com.fangxm.schedule.data

data class ClassContent(
    val type: String,
    val title: String,
    val classes: String,
    val startNum: Int,
    val length: Int,
    val weekNum: Int,
    val weekDate: Int,
    val classroom: String,
    val teacherName: String,
    val color: String
)
