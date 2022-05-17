package com.fangxm.schedule.data

import java.util.*

data class TermContent(val termId: String) {
    lateinit var startDate: Calendar
    lateinit var courses: HashMap<String, CourseContent>

    fun merge(course: CourseContent) {
        if (courses.contains(course.title))
            courses[course.title]!!.merge(course)
        else courses[course.title] = course
    }

    fun merge(extraCourses: Map<String, CourseContent>) {
        extraCourses.forEach {
            if (courses.contains(it.key)) {
                courses[it.key]!!.merge(it.value)
            } else {
                courses[it.key] = it.value
            }
        }
    }
}
