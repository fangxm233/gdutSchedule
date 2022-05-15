package com.fangxm.schedule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fangxm.schedule.ui.third.Gzip
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.InputStream
import java.io.OutputStreamWriter

import java.net.URL
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

import javax.net.ssl.HttpsURLConnection
import kotlin.collections.HashMap

class JwAPI {
    companion object {
        var cookie: String? = "JSESSIONID=E4F8C59F827795F334868EFB5360C566;"

        fun fetchUrl(url: String, callback: (Result<InputStream>) -> Unit) {
            val url = URL(url)
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("accept", ",*/*")
            conn.setRequestProperty("accept-encoding", "gzip, deflate, br")

            val h =
                Thread.UncaughtExceptionHandler { _, ex -> println("Uncaught exception: $ex") }
            var result: Result<InputStream>? = null
            val thread = Thread {
                if (conn.responseCode == 200) {
                    println("success get from ${conn.url}")
                    conn.headerFields.forEach {
                        if (it.key == "Set-Cookie") {
                            cookie = it.value[0].slice(IntRange(0, 43))
                            println(cookie)
                        }
                    }
                    result = Result.success(conn.inputStream)
                } else {
                    result = Result.failure(java.net.ConnectException())
                }
                CallbackHandler.runOnUI {
                    callback(result!!)
                }
            }
            thread.uncaughtExceptionHandler = h
            thread.start()
//            callback(result!!)
        }

        fun postUrl(
            url: String,
            content: Map<String, String>?,
            callback: (Result<InputStream>) -> Unit
        ) {
            val url = URL(url)
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("accept", ",*/*")
            conn.setRequestProperty("accept-encoding", "gzip, deflate, br")
            conn.setRequestProperty("Cookie", cookie)
            conn.useCaches = false
            conn.doInput = true
            conn.doOutput = true

            val param = if (content != null) map2Url(content) else ""
            val h =
                Thread.UncaughtExceptionHandler { _, ex -> println("Uncaught exception: $ex") }
            var result: Result<InputStream>? = null
            val thread = Thread {
                conn.connect()
                val writer = OutputStreamWriter(conn.outputStream, "utf-8")
                writer.write(param.toString())
                writer.flush()

                result = if (conn.responseCode == 200) {
                    println("success post from ${conn.url}")
                    Result.success(conn.inputStream)
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

        fun map2Url(paramToMap: Map<String, String>): String? {
            val url = StringBuffer()
            var isfist = true
            for ((key, value) in paramToMap) {
                if (isfist) {
                    isfist = false
                } else {
                    url.append("&")
                }
                url.append(key).append("=")
                url.append(value)
            }
            return url.toString()
        }

        fun getResponseBody(stream: InputStream): JSONObject {
            val reader = stream.reader(Charset.forName("utf-8"))
            val tokener = JSONTokener(reader.readText())
            val json = JSONObject(tokener)
            return json
        }

        fun decodeResourceToString(stream: InputStream): String {
            return Gzip.uncompressToString(stream.readBytes(), "utf-8")
        }

        fun getResponseContent(stream: InputStream): String {
            return stream.reader(Charset.forName("utf-8")).readText()
        }

        private fun getVerifyCodeImgUrl(): String {
            val time = System.currentTimeMillis()
            println(time)
            return "https://jxfw.gdut.edu.cn/yzm?d=$time"
        }

        private fun getLoginUrl(): String {
            return "https://jxfw.gdut.edu.cn/new/login"
        }

        private fun getCurseUrl(): String {
            return "https://jxfw.gdut.edu.cn/xsgrkbcx!xsAllKbList.action"
        }

        private fun getExamUrl(): String {
            return "https://jxfw.gdut.edu.cn/xsksap!getDataList.action"
        }

        private fun getScoreUrl(): String {
            return "https://jxfw.gdut.edu.cn/xskccjxx!getDataList.action"
        }

        private fun getCourseTaskUrl(): String {
            return "https://jxfw.gdut.edu.cn/xskktzd!getDataList.action"
        }

        private fun getCourseDataUrl(): String {
            return "https://jxfw.gdut.edu.cn/xsgrkbcx!getSkxxDataList.action"
        }

        private fun getTermStartDateUrl(termId: String): String {
            return "https://jxfw.gdut.edu.cn/xsgrkbcx!getKbRq.action?xnxqdm=$termId&zc=1"
        }

        fun getVerifyCodeImg(callback: (Result<Bitmap>) -> Unit) {
            fetchUrl(getVerifyCodeImgUrl()) {
                if (it.isSuccess) {
                    val result = BitmapFactory.decodeStream(it.getOrThrow())
                    callback(Result.success(result!!))
                } else {
                    callback(Result.failure(it.exceptionOrNull()!!))
                }
            }
        }

        fun login(
            number: String,
            password: String,
            verifyCode: String,
            callback: (Result<String>) -> Unit
        ) {
            val body = HashMap<String, String>()
            body["account"] = number
            body["pwd"] = password
            body["verifycode"] = verifyCode
            postUrl(getLoginUrl(), body) {
                if (it.isSuccess) {
                    val content = getResponseBody(it.getOrThrow())
                    if (content["code"] == 0) {
                        callback(Result.success(content["message"] as String))
                    } else {
                        callback(Result.failure(Exception(content["message"] as String)))
                    }
                }
            }
        }

        fun getCoursesData(termId: String, callback: (Result<JSONArray>) -> Unit) {
            val body = HashMap<String, String>()
            body["xnxqdm"] = termId

            postUrl(getCurseUrl(), body) {
                if (it.isSuccess) {
                    val content = decodeResourceToString(it.getOrThrow())
                    if (content.contains("请输入学号或工号")) {
                        println("登录信息过期")
                        callback(Result.failure(Exception("登录信息过期")))
                        return@postUrl
                    }

                    val start = content.indexOf("<script type=\"text/javascript\">")

                    if (start == -1) {
                        println("解析失败")
                        callback(Result.failure(Exception("解析失败")))
                        return@postUrl
                    }
                    val script = content.slice(start until content.length)

                    val pattern = Pattern.compile("\\[.*\\];")
                    val matcher = pattern.matcher(script)
                    if (matcher.find()) {
                        val data = matcher.group(0)
                        val tokener = JSONTokener(data)
                        val json = JSONArray(tokener)
                        println(json)
                        callback(Result.success(json))
                        return@postUrl
                    } else {
                        println("解析失败")
                        callback(Result.failure(Exception("解析失败")))
                        return@postUrl
                    }
                }
            }
        }

        fun getExamData(termId: String, callback: (Result<JSONArray>) -> Unit) {
            val body = HashMap<String, String>()
            body["xnxqdm"] = termId
            body["page"] = "1"
            body["rows"] = "30"

            postUrl(getExamUrl(), body) {
                if (it.isSuccess) {
                    val content = decodeResourceToString(it.getOrThrow())
                    if (content.contains("请输入学号或工号")) {
                        println("登录信息过期")
                        callback(Result.failure(Exception("登录信息过期")))
                        return@postUrl
                    }

                    var json: JSONObject? = null
                    try {
                        val tokener = JSONTokener(content)
                        json = JSONObject(tokener)
                    } catch (e: java.lang.Exception) {
                        println("解析失败")
                        callback(Result.failure(Exception("解析失败")))
                    }

                    if (!json!!.has("rows")) {
                        println("解析失败")
                        callback(Result.failure(Exception("解析失败")))
                        return@postUrl
                    }

                    println(json.getJSONArray("rows"))
                    callback(Result.success(json.getJSONArray("rows")))
                }
            }
        }

        fun getScoreData(termId: String, callback: (Result<JSONArray>) -> Unit) {
            val body = HashMap<String, String>()
            body["xnxqdm"] = termId
            body["page"] = "1"
            body["rows"] = "30"

            //post gzip
        }

        fun getCourseTaskData(termId: String, callback: (Result<JSONArray>) -> Unit) {
            val body = HashMap<String, String>()
            body["xnxqdm"] = termId
            body["page"] = "1"
            body["rows"] = "30"

            //post gzip
        }

        fun getCourseData(courseId: String, callback: (Result<JSONArray>) -> Unit) {
            val body = HashMap<String, String>()
            body["kcrwdm"] = courseId
            body["page"] = "1"
            body["rows"] = "100"

            // post gzip
        }

        fun getTermStartDate(termId: String, callback: (Result<Calendar>) -> Unit) {
            fetchUrl(getTermStartDateUrl(termId)) {
                if (it.isSuccess) {
                    val content = decodeResourceToString(it.getOrThrow())
                    if (content.contains("请输入学号或工号")) {
                        println("登录信息过期")
                        callback(Result.failure(Exception("登录信息过期")))
                        return@fetchUrl
                    }

                    var json: JSONArray? = null
                    try {
                        val tokener = JSONTokener(content)
                        json = JSONArray(tokener)
                    } catch (e: java.lang.Exception) {
                        println("解析失败")
                        callback(Result.failure(Exception("解析失败")))
                    }

                    val date = json!!.getJSONArray(1)
                        .getJSONObject(0).getString("rq").split("-").map {
                            it.toInt()
                        }
                    val calendar = Calendar.getInstance()
                    calendar.set(date[0], date[1], date[2])
                    println("开学日期：$calendar")
                    callback(Result.success(calendar))
                }
            }
        }
    }
}
