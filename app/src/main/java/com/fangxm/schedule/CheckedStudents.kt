package com.fangxm.schedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.fangxm.schedule.databinding.ActivityCheckedStudentsBinding
import com.fangxm.schedule.databinding.ActivityLoginBinding
import com.fangxm.schedule.ui.ButtonItemAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class CheckedStudents : AppCompatActivity() {
    private lateinit var binding: ActivityCheckedStudentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckedStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ano = intent.getStringExtra("ano")!!
        val ended = intent.getBooleanExtra("ended", true)

        println(ended)
        if (!ended) binding.end.visibility = View.VISIBLE

        val list = binding.details
        val adapter = ButtonItemAdapter(applicationContext, listOf(mapOf(Pair("title", "正在获取..."))))
        list.adapter = adapter

        binding.end.setOnClickListener {
            BcAPI.endAttendance(ano) {
                if (it.isFailure) {
                    Toast.makeText(applicationContext,"结束签到失败", Toast.LENGTH_SHORT).show()
                    return@endAttendance
                } else {
                    Toast.makeText(applicationContext,"结束签到成功", Toast.LENGTH_SHORT).show()
                    binding.end.visibility = View.GONE
                    return@endAttendance
                }
            }
        }

        BcAPI.getAttendanceDetail(ano) {
            if (it.isFailure) {
                Toast.makeText(applicationContext,"详情获取失败", Toast.LENGTH_SHORT).show()
                return@getAttendanceDetail
            }

            val json = it.getOrThrow()

            if (json.length() == 0) {
                val adapter = ButtonItemAdapter(applicationContext, listOf(mapOf(Pair("title", "暂无数据"))))
                list.adapter = adapter
                return@getAttendanceDetail
            }
            val data = mutableListOf<HashMap<String, String>>()
            println(json.length())
            for (i in 0 until json.length()) {
                val content = json.getJSONObject(i)
                val format = SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.SIMPLIFIED_CHINESE)
                format.timeZone = TimeZone.getTimeZone("Asia/Shanghai");
                val timestamp = content.getLong("time")
                val time = if (timestamp == 0L) "未签到" else format.format(content.getLong("time"))
                data.add(hashMapOf(Pair("title", content.getString("sname")), Pair("extra", time)))
            }
            println(data)
            val adapter = ButtonItemAdapter(applicationContext, data)
            list.adapter = adapter
        }
    }
}