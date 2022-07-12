package com.fangxm.schedule.data

class ClassContent {
    val type: String

    val title: String
    val startNum: Int
    val length: Int
    val weekNum: Int
    val weekDate: Int
    val classroom: String
    val teacherName: String
    val color: String

    val classes: String
    val description: String

    val time: String
    val date: String
    val examForm: String

    constructor(title: String, startNum: Int, length: Int, weekNum: Int, weekDate: Int,
                classroom: String, teacherName: String, color: String, classes: String, description: String
    ) {
        this.type = "class"
        this.classes = classes
        this.title = title
        this.startNum = startNum
        this.length = length
        this.weekNum = weekNum
        this.weekDate = weekDate
        this.classroom = classroom
        this.teacherName = teacherName
        this.description = description
        this.color = color

        time = ""
        date = ""
        examForm = ""
    }

    constructor(title: String, startNum: Int, length: Int, weekNum: Int, weekDate: Int,
                classroom: String, teacherName: String, color: String, date: String,
                time: String, examForm: String
                ) {
        this.type = "exam"
        this.title = title
        this.startNum = startNum
        this.length = length
        this.weekNum = weekNum
        this.weekDate = weekDate
        this.time = time
        this.classroom = classroom
        this.teacherName = teacherName
        this.examForm = examForm
        this.color = color
        this.date = date

        classes = ""
        description = ""
    }

    constructor(type: String, title: String, classes: String, startNum: Int, length: Int, weekNum: Int,
                weekDate: Int, time: String, date: String, classroom: String, description: String,
                teacherName: String, examForm: String, color: String
    ) {
        this.type = type
        this.title = title
        this.startNum = startNum
        this.length = length
        this.weekNum = weekNum
        this.weekDate = weekDate
        this.time = time
        this.classroom = classroom
        this.teacherName = teacherName
        this.examForm = examForm
        this.color = color
        this.date = date
        this.classes = classes
        this.description = description
    }
}
