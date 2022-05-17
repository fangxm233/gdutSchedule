package com.fangxm.schedule.data

import org.json.JSONArray
import java.util.*
import kotlin.collections.HashMap

object TermsManager {
    var terms: HashMap<String, TermContent> = hashMapOf()
    val db = LocalDatabase()

    fun setTermCoursesFromJson(termId: String, json: JSONArray) {
        if (!terms.contains(termId)) {
            terms[termId] = TermContent(termId)
            terms[termId]!!.courses = hashMapOf()
        }

        val coursesMap = terms[termId]!!.courses

        val colorQueue = ColorQueue()

        for (i in 0 until json.length()) {
            val courseData = json.getJSONObject(i)
            val title = courseData.getString("kcmc")
            val classes = courseData.getString("jxbmc")
            val section = courseData.getString("jcdm2").split(",").map { it.toInt() }
            val weeks = courseData.getString("zcs").split(",").map { it.toInt() }
            val weekDate = courseData.getString("xq").toInt()
            val classroom = courseData.getString("jxcdmcs")
            val teacherName = courseData.getString("teaxms")
            val length = section.last() - section.first() + 1
            val color = if (coursesMap.contains(title)) {
                val normalClasses = coursesMap[title]!!.classes.filter {
                    it.type == "class"
                }
                if (normalClasses.isEmpty()) {
                    colorQueue.next()
                } else {
                    normalClasses.first().color
                }
            } else {
                colorQueue.next()
            }

            val classList = mutableListOf<ClassContent>()
            weeks.forEach {
                val classContent = ClassContent(
                    "class", title, classes,
                    section.first(), length, it,
                    weekDate, classroom, teacherName, color
                )
                classList.add(classContent)
            }

            if (coursesMap.contains(title)) {
                coursesMap[title]!!.merge(classList)
            } else {
                coursesMap[title] = CourseContent(title, classList)
                println(title + color)
            }
        }

        db.SaveTermData(termId, coursesMap)
        println("添加课程 $termId")
    }

    fun setTermExamsFromJson(termId: String, json: JSONArray) {
        if (!terms.contains(termId)) {
            terms[termId] = TermContent(termId)
            terms[termId]!!.courses = hashMapOf()
        }

        val coursesMap = terms[termId]!!.courses

        val color = ColorQueue.examColor

        for (i in 0 until json.length()) {
            val courseData = json.getJSONObject(i)
            val title = courseData.getString("kcmc")
            val classes = ""
            val section = courseData.getString("jcdm2").split(",").map { it.toInt() }
            val weeks = courseData.getString("zc").split(",").map { it.toInt() }
            val weekDate = courseData.getString("xq").toInt()
            val classroom = courseData.getString("kscdmc")
            val teacherName = courseData.getString("jkteaxms")
            val length = section.last() - section.first() + 1

            val classList = mutableListOf<ClassContent>()
            weeks.forEach {
                val classContent = ClassContent(
                    "exam", title, classes,
                    section.first(), length, it,
                    weekDate, classroom, teacherName, color
                )
                classList.add(classContent)
            }

            if (coursesMap.contains(title)) {
                coursesMap[title]!!.merge(classList)
            } else {
                coursesMap[title] = CourseContent(title, classList)
            }
        }

        db.SaveTermData(termId, coursesMap)
        println("添加考试安排 $termId")
    }

    fun setTermStartDate(termId: String, date: Calendar) {
        if (!terms.contains(termId)) {
            terms[termId] = TermContent(termId)
            terms[termId]!!.courses = hashMapOf()
        }
        terms[termId]!!.startDate = date
    }

    fun clearTermData(termId: String) {
        terms.remove(termId)
    }

    fun hasTermData(termId: String): Boolean {
        return terms.contains(termId) || db.HasTermData(termId)
    }

    fun getTermData(termId: String): TermContent? {
        if (terms.containsKey(termId)) return terms[termId]
        if (db.HasTermData(termId)) {
            terms[termId] = db.GetTermData(termId)
            return terms[termId]
        }
        return null
    }

    fun addClass(termId: String, content: ClassContent) {
        val coursesMap = terms[termId]!!.courses

        if (coursesMap.contains(content.title)) {
            coursesMap[content.title]!!.merge(mutableListOf(content))
        } else {
            coursesMap[content.title] = CourseContent(content.title, mutableListOf(content))
        }
    }

    fun getWeekCount(termId: String): Int {
        val coursesMap = getTermData(termId)!!.courses

        return coursesMap.maxOf {
            it.value.classes.maxOf {
                it.weekNum
            }
        }
    }

    fun getWeekClasses(termId: String, weekNum: Int): Array<ClassContent> {
        val coursesMap = getTermData(termId)!!.courses

        return coursesMap.flatMap {
            it.value.classes
        }.filter {
            it.weekNum == weekNum
        }.toTypedArray()
    }

    fun getTermCoursesByWeek(termId: String): Array<Array<ClassContent>> {
        return (1..getWeekCount(termId)).map {
            getWeekClasses(termId, it)
        }.toTypedArray()
    }

    fun getTermStartDate(termId: String): Calendar {
        return terms[termId]!!.startDate
    }
}