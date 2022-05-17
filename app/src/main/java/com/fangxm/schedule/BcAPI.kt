package com.fangxm.schedule

import org.json.JSONArray
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

class BcAPI {
    companion object {
        var no: String? = null
        var loggedinType: String? = null
        const val host = "10.0.2.2:8888"

        fun fetchUrl(url: String, callback: (Result<ByteArray>) -> Unit) {
            val url = URL(url)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("accept", ",*/*")
            conn.setRequestProperty("accept-encoding", "gzip, deflate, br")

            val h =
                Thread.UncaughtExceptionHandler { _, ex -> println("Uncaught exception: $ex") }
            var result: Result<ByteArray>? = null
            val thread = Thread {
                if (conn.responseCode == 200) {
                    println("success get from ${conn.url}")
                    result = Result.success(conn.inputStream.readBytes())
                } else {
                    result = Result.failure(java.net.ConnectException())
                }
                CallbackHandler.runOnUI {
                    callback(result!!)
                }
            }
            thread.uncaughtExceptionHandler = h
            thread.start()
        }

        fun postUrl(
            url: String,
            content: Map<String, String>?,
            callback: (Result<ByteArray>) -> Unit
        ) {
            val url = URL(url)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("accept", ",*/*")
            conn.setRequestProperty("accept-encoding", "gzip, deflate, br")
            conn.useCaches = false
            conn.doInput = true
            conn.doOutput = true

            val param = if (content != null) JwAPI.map2Url(content) else ""
            val h =
                Thread.UncaughtExceptionHandler { _, ex -> println("Uncaught exception: $ex") }
            var result: Result<ByteArray>? = null
            val thread = Thread {
                conn.connect()
                val writer = OutputStreamWriter(conn.outputStream, "utf-8")
                writer.write(param.toString())
                writer.flush()

                println(conn.responseCode)
                result = if (conn.responseCode == 200) {
                    println("success post from ${conn.url}")
                    Result.success(conn.inputStream.readBytes())
                } else {
                    Result.failure(java.net.ConnectException())
                }

                CallbackHandler.runOnUI {
                    callback(result!!)
                }
            }

            thread.uncaughtExceptionHandler = h
            thread.start()
        }

        private fun getRaiseAttendanceUrl(): String {
            return "http://$host/raise_attendance"
        }

        private fun getLoginUrl(): String {
            return "http://$host/login"
        }

        private fun getTeacherAttendancesUrl(tno: String): String {
            return "http://$host/teacher_attendances?tno=$tno"
        }

        private fun getStudentAttendancesUrl(sno: String): String {
            return "http://$host/student_attendances?sno=$sno"
        }

        private fun attendanceDetailUrl(ano: String): String {
            return "http://$host/attendance_detail?ano=$ano"
        }

        private fun teachingCoursesUrl(tno: String): String {
            return "http://$host/teach_courses?tno=$tno"
        }

        private fun checkinUrl(): String {
            return "http://$host/checkin"
        }

        private fun endAttendanceUrl(): String {
            return "http://$host/end_attendance"
        }

        fun raiseAttendance(type: String, course: String, duration: String,
                            long: String?, lat: String?, callback: (Result<Unit>) -> Unit) {
            val body = HashMap<String, String>()
            body["type"] = type
            body["course"] = course
            body["duration"] = duration
            if (long != null) {
                body["long"] = long
                body["lat"] = lat!!
            }
            body["tno"] = no!!

            postUrl(getRaiseAttendanceUrl(), body) {
                if (it.isSuccess) {
                    callback(Result.success(Unit))
                } else {
                    callback(Result.failure(java.lang.Exception("未知错误")))
                }
            }
        }

        fun login(type: String, no: String, pwd: String, callback: (Result<Unit>) -> Unit) {
            val body = HashMap<String, String>()
            body["type"] = type
            body["no"] = no
            body["pwd"] = pwd

            postUrl(getLoginUrl(), body) {
                if (it.isSuccess) {
                    val content = JwAPI.getResponseContent(it.getOrThrow())
                    if (content == "账号或密码不正确") {
                        callback(Result.failure(Exception(content)))
                        return@postUrl
                    }
                    loggedinType = if (type == "t") "teacher" else "student"
                    this.no = no
                    callback(Result.success(Unit))
                    return@postUrl
                }
                callback(Result.failure(java.lang.Exception("unknown")))
            }
        }

        fun getTeacherAttendances(callback: (Result<JSONArray>) -> Unit) {
            fetchUrl(getTeacherAttendancesUrl(this.no!!)) {
                if (it.isSuccess) {
                    val content = JwAPI.getResponseBody(it.getOrThrow()).getJSONArray("content")
                    callback(Result.success(content))
                } else {
                    callback(Result.failure(java.lang.Exception("unknown")))
                }
            }
        }

        fun getAttendanceDetail(ano: String, callback: (Result<JSONArray>) -> Unit) {
            fetchUrl(attendanceDetailUrl(ano)) {
                if (it.isSuccess) {
                    val content = JwAPI.getResponseBody(it.getOrThrow()).getJSONArray("content")
                    callback(Result.success(content))
                } else {
                    callback(Result.failure(java.lang.Exception("unknown")))
                }
            }
        }

        fun getStudentAttendances(callback: (Result<JSONArray>) -> Unit) {
            fetchUrl(getStudentAttendancesUrl(this.no!!)) {
                if (it.isSuccess) {
                    val content = JwAPI.getResponseBody(it.getOrThrow()).getJSONArray("content")
                    callback(Result.success(content))
                } else {
                    callback(Result.failure(java.lang.Exception("unknown")))
                }
            }
        }

        fun getTeachingCourses(callback: (Result<JSONArray>) -> Unit) {
            fetchUrl(teachingCoursesUrl(this.no!!)) {
                if (it.isSuccess) {
                    val content = JwAPI.getResponseBody(it.getOrThrow()).getJSONArray("content")
                    callback(Result.success(content))
                } else {
                    callback(Result.failure(java.lang.Exception("unknown")))
                }
            }
        }

        fun checkin(ano: String, callback: (Result<Unit>) -> Unit) {
            val body = HashMap<String, String>()
            body["sno"] = no!!
            body["ano"] = ano

            postUrl(checkinUrl(), body) {
                if (it.isSuccess) {
                    val content = JwAPI.getResponseContent(it.getOrThrow())
                    if (content != "签到成功") {
                        callback(Result.failure(Exception(content)))
                        return@postUrl
                    }
                    callback(Result.success(Unit))
                    return@postUrl
                }
                callback(Result.failure(java.lang.Exception("未知错误")))
            }
        }

        fun endAttendance(ano: String, callback: (Result<Unit>) -> Unit) {
            val body = HashMap<String, String>()
            body["ano"] = ano

            postUrl(endAttendanceUrl(), body) {
                if (it.isSuccess) {
                    callback(Result.success(Unit))
                    return@postUrl
                }
                callback(Result.failure(java.lang.Exception("未知错误")))
            }
        }
    }
}