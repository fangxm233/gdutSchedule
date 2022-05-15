package com.fangxm.schedule.data

data class CourseContent(
    val title: String,
    val classes: MutableList<ClassContent>
) {
    fun merge(course: CourseContent) {
        classes.addAll(course.classes)
    }

    fun merge(extraClasses: MutableList<ClassContent>) {
        classes.addAll(extraClasses)
    }
}
