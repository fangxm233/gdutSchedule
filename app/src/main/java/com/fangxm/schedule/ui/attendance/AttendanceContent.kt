package com.fangxm.schedule.ui.attendance

import java.util.*

class AttendanceContent {
    val type: String
    var title: String? = null
    var ano: String? = null
    var attedanceType: String? = null
    var courseName: String? = null
    var raiseTime: String? = null
    var duration: Int? = null
    var checkedNum: String? = null
    var totalNum: String? = null
    var ended: Boolean = false

    var checked: Boolean = false

    constructor(title: String) {
        type = "button"
        this.title = title
    }

    constructor(atype: String, ano: String, cname: String, raiseTime: String,
                duration: Int, checkedNum: String, totalNum: String, ended: Boolean) {
        type = "teacher"
        this.ano = ano
        attedanceType = atype
        courseName = cname
        this.raiseTime = raiseTime
        this.duration = duration
        this.checkedNum = checkedNum
        this.totalNum = totalNum
        this.ended = ended
    }

    constructor(atype: String, ano: String, cname: String, raiseTime: String,
                duration: Int, checked: Boolean, ended: Boolean) {
        type = "student"
        this.ano = ano
        attedanceType = atype
        courseName = cname
        this.raiseTime = raiseTime
        this.duration = duration
        this.checked = checked
        this.ended = ended
    }
}