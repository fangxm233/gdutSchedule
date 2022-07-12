package com.fangxm.schedule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fangxm.schedule.data.TermsManager
import com.fangxm.schedule.ui.third.Gzip
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter

import java.net.URL
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

import javax.net.ssl.HttpsURLConnection
import kotlin.collections.HashMap

class JwAPI {
    companion object {
        fun fetchUrl(url: String, callback: (Result<ByteArray>) -> Unit) {
            val url = URL(url)
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("accept", ",*/*")
            conn.setRequestProperty("accept-encoding", "gzip, deflate, br")
            conn.setRequestProperty("Cookie", TermsManager.db.cookie)

            val h =
                Thread.UncaughtExceptionHandler { _, ex -> println("Uncaught exception: $ex") }
            var result: Result<ByteArray>? = null
            val thread = Thread {
                if (conn.responseCode == 200) {
                    println("success get from ${conn.url}")
                    conn.headerFields.forEach {
                        if (it.key == "Set-Cookie") {
                            val cookie = it.value[0].slice(IntRange(0, 43))
                            TermsManager.db.cookie = cookie
                            println(cookie)
                        }
                    }
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
//            callback(result!!)
        }

        fun postUrl(
            url: String,
            content: Map<String, String>?,
            callback: (Result<ByteArray>) -> Unit
        ) {
            val url = URL(url)
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("accept", ",*/*")
            conn.setRequestProperty("accept-encoding", "gzip, deflate, br")
            conn.setRequestProperty("Cookie", TermsManager.db.cookie)
            conn.useCaches = false
            conn.doInput = true
            conn.doOutput = true

            val param = if (content != null) map2Url(content) else ""
            val h =
                Thread.UncaughtExceptionHandler { _, ex -> println("Uncaught exception: $ex") }
            var result: Result<ByteArray>? = null
            val thread = Thread {
                conn.connect()
                val writer = OutputStreamWriter(conn.outputStream, "utf-8")
                writer.write(param.toString())
                writer.flush()

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

        fun getResponseObject(stream: ByteArray): JSONObject {
            val content = decodeResourceToString(stream)
            val tokenizer = JSONTokener(content)
            return JSONObject(tokenizer)
        }

        fun getResponseArray(stream: ByteArray): JSONArray {
            val content = decodeResourceToString(stream)
            val tokenizer = JSONTokener(content)
            return JSONArray(tokenizer)
        }

        fun checkAndGetResponseObject(stream: ByteArray): Result<JSONObject> {
            val content = decodeResourceToString(stream)
            if (content.contains("请输入学号或工号")) {
                println("登录信息过期")
                return Result.failure(Exception("登录信息过期"))
            }
            if (content.contains("禁止访问")) {
                println("被禁止访问")
                return Result.failure(Exception("被禁止访问教务处"))
            }
            return try {
                val tokenizer = JSONTokener(content)
                val json = JSONObject(tokenizer)
                Result.success(json)
            } catch (e: java.lang.Exception) {
                println("解析失败")
                Result.failure(Exception("解析失败"))
            }
        }

        fun checkAndGetResponseArray(stream: ByteArray): Result<JSONArray> {
            val content = decodeResourceToString(stream)
            if (content.contains("请输入学号或工号")) {
                println("登录信息过期")
                return Result.failure(Exception("登录信息过期"))
            }
            if (content.contains("禁止访问")) {
                println("被禁止访问")
                return Result.failure(Exception("被禁止访问教务处"))
            }
            return try {
                val tokenizer = JSONTokener(content)
                val json = JSONArray(tokenizer)
                Result.success(json)
            } catch (e: java.lang.Exception) {
                println("解析失败")
                Result.failure(Exception("解析失败"))
            }
        }

        fun decodeResourceToString(stream: ByteArray): String {
            return Gzip.uncompressToString(stream, "utf-8")
        }

        fun getResponseContent(stream: ByteArray): String {
            return stream.toString(Charset.forName("utf-8"))
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
                    val result = BitmapFactory.decodeByteArray(it.getOrThrow(), 0, it.getOrThrow().size)
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
            callback: (Result<Unit>) -> Unit
        ) {
            val body = HashMap<String, String>()
            body["account"] = number
            body["pwd"] = password
            body["verifycode"] = verifyCode
            postUrl(getLoginUrl(), body) {
                if (it.isSuccess) {
                    val reader = it.getOrThrow().toString(Charset.forName("utf-8"))
                    val tokener = JSONTokener(reader)
                    val content = JSONObject(tokener)
                    if (content["code"] == 0) {
                        callback(Result.success(Unit))
                        TermsManager.db.SaveAccount(number, password)
                    } else {
                        callback(Result.failure(Exception(content["message"] as String)))
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
                    val result = checkAndGetResponseObject(it.getOrThrow())
                    if (result.isFailure) {
                        callback(Result.failure(result.exceptionOrNull()!!))
                        return@postUrl
                    }
                    val json = result.getOrThrow()

                    if (!json.has("rows")) {
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

            postUrl(getCourseTaskUrl(), body) {
                if (it.isSuccess) {
                    val result = checkAndGetResponseObject(it.getOrThrow())
                    if (result.isFailure) {
                        callback(Result.failure(result.exceptionOrNull()!!))
                        return@postUrl
                    }
                    val json = result.getOrThrow()

                    if (!json.has("rows")) {
                        println("解析失败")
                        callback(Result.failure(Exception("解析失败")))
                        return@postUrl
                    }

                    println(json.getJSONArray("rows"))
                    callback(Result.success(json.getJSONArray("rows")))
                } else {
                    callback(Result.failure(java.lang.Exception("获取失败")))
                }
            }
        }

        fun getCourseData(courseId: String, callback: (Result<JSONArray>) -> Unit) {
            val body = HashMap<String, String>()
            body["kcrwdm"] = courseId
            body["page"] = "1"
            body["rows"] = "100"

            postUrl(getCourseDataUrl(), body) {
                if (it.isSuccess) {
                    val result = checkAndGetResponseObject(it.getOrThrow())
                    if (result.isFailure) {
                        callback(Result.failure(result.exceptionOrNull()!!))
                        return@postUrl
                    }
                    val json = result.getOrThrow()

                    if (!json.has("rows")) {
                        println("解析失败")
                        callback(Result.failure(Exception("解析失败")))
                        return@postUrl
                    }

                    callback(Result.success(json.getJSONArray("rows")))
                } else {
                    callback(Result.failure(java.lang.Exception("获取失败")))
                }
            }
        }

        fun getTermStartDate(termId: String, callback: (Result<Calendar>) -> Unit) {
            fetchUrl(getTermStartDateUrl(termId)) {
                if (it.isSuccess) {
                    val result = checkAndGetResponseArray(it.getOrThrow())
                    if (result.isFailure) {
                        callback(Result.failure(result.exceptionOrNull()!!))
                        return@fetchUrl
                    }
                    val json = result.getOrThrow()

                    val date = json.getJSONArray(1)
                        .getJSONObject(0).getString("rq").split("-").map {
                            it.toInt()
                        }
                    val calendar = Calendar.getInstance()
                    calendar.set(date[0], date[1] - 1, date[2])
                    println("开学日期：$calendar")
                    callback(Result.success(calendar))
                } else {
                    callback(Result.failure(java.lang.Exception("获取失败")))
                }
            }
        }
    }
}
