package com.fangxm.schedule.data

data class CourseContent(
    val title: String,
    val classes: MutableList<ClassContent>
) {
    fun merge(course: CourseContent) {
        classes.addAll(course.classes)
    }

    fun merge(extraClasses: MutableList<ClassContent>) {
        extraClasses.forEach {
            val exist = classes.find { it2 ->
                it2.weekNum == it.weekNum && it2.weekDate == it.weekDate
            }
            if (exist != null) {
                if (exist.type == "class" && it.type == "exam") {
                    classes.remove(exist)
                    classes.add(it)
                }
                return@forEach
            }
            classes.add(it)
        }
    }
}
