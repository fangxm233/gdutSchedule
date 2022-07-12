package com.fangxm.schedule.data

import android.content.Context
import android.widget.Toast
import com.fangxm.schedule.JwAPI
import org.json.JSONArray
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.ceil

object TermsManager {
    var terms: HashMap<String, TermContent> = hashMapOf()
    lateinit var db: LocalDatabase

    fun init(context: Context) {
        db = LocalDatabase(context)
    }

    fun setTermCourses(termId: String, courses: List<Pair<String, JSONArray>>) {
        if (!terms.contains(termId)) {
            terms[termId] = TermContent(termId)
            terms[termId]!!.courses = hashMapOf()
            terms[termId]!!.startDate = Calendar.getInstance()
        }

        val coursesMap = terms[termId]!!.courses

        val colorQueue = ColorQueue()

        for (coursePair in courses) {
            val courseData = coursePair.second
            if (courseData.length() == 0) continue

            val classList = mutableListOf<ClassContent>()

            println(courseData)
            val title = courseData.getJSONObject(0).getString("kcmc")
            val teacherName = coursePair.first
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

            for (i in 0 until courseData.length()) {
                val classData = courseData.getJSONObject(i)
                if (classData.getString("xq") == "") continue
                val weekDate = classData.getString("xq").toInt()
                val classes = classData.getString("jxbmc")
                val section = classData.getString("jcdm2").split(",").map { it.toInt() }
                val week = classData.getString("zc").toInt()
                val classroom = classData.getString("jxcdmc")
                val description = classData.getString("sknrjj")
                val length = section.last() - section.first() + 1

                val classContent = ClassContent(
                    title, section.first(), length,
                    week, weekDate, classroom,
                    teacherName, color, classes, description
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

        db.SaveTermData(termId, terms[termId]!!)
        println("添加课程 $termId")
    }

    fun setTermExamsFromJson(termId: String, json: JSONArray) {
        if (!terms.contains(termId)) {
            terms[termId] = TermContent(termId)
            terms[termId]!!.courses = hashMapOf()
            terms[termId]!!.startDate = Calendar.getInstance()
        }

        val coursesMap = terms[termId]!!.courses

        val color = ColorQueue.examColor

        for (i in 0 until json.length()) {
            val courseData = json.getJSONObject(i)
            val title = courseData.getString("kcmc")
            val section = courseData.getString("jcdm2").split(",").map { it.toInt() }
            val weeks = courseData.getString("zc").split(",").map { it.toInt() }
            val weekDate = courseData.getString("xq").toInt()
            val time = courseData.getString("kssj")
            val classroom = courseData.getString("kscdmc")
            val teacherName = courseData.getString("jkteaxms")
            val date = courseData.getString("ksrq")
            val examForm = if (courseData.getString("ksxs") == "1") "开卷" else "闭卷"
            val length = section.last() - section.first() + 1

            val classList = mutableListOf<ClassContent>()
            weeks.forEach {
                val classContent = ClassContent(
                    title, section.first(), length,
                    it, weekDate, classroom,
                    teacherName, color, date, time, examForm
                )
                classList.add(classContent)
            }

            if (coursesMap.contains(title)) {
                coursesMap[title]!!.merge(classList)
            } else {
                coursesMap[title] = CourseContent(title, classList)
            }
        }

        db.SaveTermData(termId, terms[termId]!!)
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
            if (it.value.classes.size == 0) 0
            else it.value.classes.maxOf {
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

    fun getTeacherWeekClasses(termId: String, weekNum: Int, name: String): Array<ClassContent> {
        val coursesMap = getTermData(termId)!!.courses

        return coursesMap.flatMap {
            it.value.classes
        }.filter {
            it.weekNum == weekNum && it.teacherName == name
        }.toTypedArray()
    }

    fun getTermCoursesByWeek(termId: String): Array<Array<ClassContent>> {
        return (1..getWeekCount(termId)).map {
            getWeekClasses(termId, it)
        }.toTypedArray()
    }

    fun getTeacherTermCoursesByWeek(termId: String, name: String): Array<Array<ClassContent>> {
        return (1..getWeekCount(termId)).map {
            getTeacherWeekClasses(termId, it, name)
        }.toTypedArray()
    }

    fun getTermStartDate(termId: String): Calendar {
        return terms[termId]!!.startDate
    }

    fun currentWeek(termId: String): Int {
        val diff = (Calendar.getInstance().timeInMillis - getTermStartDate(termId).timeInMillis)
        val day = diff / 1000.0 / 3600 / 24 / 7;
        return ceil(day).toInt()
    }

    fun fetchTermInfo(termId: String, resultCallback: (Result<Unit>) -> Unit) {
        clearTermData(termId)

        var result: Result<Unit>? = null
        var succeed = 0
        val total = 3

        val checkFailure = { it: Result<Any> -> Boolean
            when {
                result != null -> true
                it.isSuccess -> false
                else -> {
                    val message = it.exceptionOrNull()!!.message!!
                    result = when (message) {
                        "登录信息过期" -> Result.failure(Exception("登录信息过期"))
                        "被禁止访问教务处" -> Result.failure(Exception("被禁止访问教务处"))
                        else -> Result.failure(Exception("未知错误: $message"))
                    }
                    resultCallback(result!!)
                    true
                }
            }
        }

        val increaseSucceed = {
            succeed++
            if (succeed == total) {
                resultCallback(Result.success(Unit))
            }
        }

        JwAPI.getCourseTaskData(termId) {
            if (checkFailure(it)) return@getCourseTaskData

            val json = it.getOrThrow()
            val coursesList = mutableListOf<Pair<String, JSONArray>>()

            for (i in 0 until json.length()) {
                val classJson = json.getJSONObject(i)
                JwAPI.getCourseData(classJson.getString("kcrwdm")) {
                    if (checkFailure(it)) return@getCourseData

                    coursesList.add(Pair(classJson.getString("teaxm"), it.getOrThrow()))
                    if (coursesList.size == json.length()) {
                        setTermCourses(termId, coursesList)
                        increaseSucceed()
                    }
                }
            }
        }
        JwAPI.getExamData(termId) {
            if (checkFailure(it)) return@getExamData

            println(it.getOrThrow())
            setTermExamsFromJson(termId, it.getOrThrow())
            increaseSucceed()
        }
        JwAPI.getTermStartDate(termId) {
            if (checkFailure(it)) return@getTermStartDate

            setTermStartDate(termId, it.getOrThrow())
            increaseSucceed()
        }
    }
}