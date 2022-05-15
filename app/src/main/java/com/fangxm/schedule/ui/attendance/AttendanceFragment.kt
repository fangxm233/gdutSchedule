package com.fangxm.schedule.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fangxm.schedule.BcAPI
import com.fangxm.schedule.databinding.FragmentAttendanceBinding
import java.text.SimpleDateFormat
import java.util.*

class AttendanceFragment : Fragment() {

    private lateinit var attendanceViewModel: AttendanceViewModel
    private var _binding: FragmentAttendanceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var current: List<AttendanceContent>? = null
    private var history: List<AttendanceContent>? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        attendanceViewModel =
                ViewModelProvider(this).get(AttendanceViewModel::class.java)

        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val list = binding.listContent

        fetchData()
        refreshList()

        list.setOnItemClickListener { adapterView, view, i, l ->

        }
        // 教师界面: 发起签到，签到列表，历史签到记录
        // 发起签到: 选择课程，持续时间，是否定位，获取定位，发起签到
        // 签到列表: 签到类型，课程，发起时间，剩余时间，已签到人数，总人数，是否结束，结束签到，内容: 展示已签到人，签到时间
        // 历史签到记录: 签到类型，课程，发起时间，已签到人数，总人数，内容: 展示已签到人，签到时间

        // 学生界面: 当前签到，历史签到记录
        // 当前签到: 签到类型，课程，发起时间，剩余时间，定位并签到
        // 历史签到记录: 签到类型，课程，发起时间，是否签到

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchData() {
        val data: MutableList<AttendanceContent> = mutableListOf()
        if (BcAPI.no == null) {
            data.add(AttendanceContent("学生登录"))
            data.add(AttendanceContent("教师登录"))
            this.current = data
            this.history = listOf()
            return
        }

        this.current = listOf(AttendanceContent("正在刷新..."))
        this.history = listOf()
        if(BcAPI.loggedinType == "teacher") {
            BcAPI.getTeacherAttendances {
                if (it.isFailure) {
                    Toast.makeText(requireContext(), "获取失败", Toast.LENGTH_SHORT).show()
                    return@getTeacherAttendances
                }

                val json = it.getOrThrow()

                if (json.length() == 0) {
                    this.current = listOf()
                    this.history = listOf()
                    return@getTeacherAttendances
                }
                for (i in 0 until json.length()) {
                    val content = json.getJSONObject(i)
                    val time = SimpleDateFormat("YYYY-MM-DD hh-m-ss").format(content.getLong("time"))
                    val current = Calendar.getInstance()
                    val dur = content.getString("dur").toInt()
                    val min = (current.timeInMillis - content.getLong("time")) / 1000 / 60

                    data.add(AttendanceContent(content.getString("type"),
                        content.getString("ano"), content.getString("cname"),
                        time, dur, content.getString("checked"), content.getString("total"),
                        content.getString("ended").equals("y") || min > dur))
                }
                this.current = data.filter {
                    !it.ended
                }
                this.history = data.filter {
                    it.ended
                }
                refreshList()
            }
        } else {
            BcAPI.getStudentAttendances {
                if (it.isFailure) {
                    Toast.makeText(requireContext(), "获取失败", Toast.LENGTH_SHORT).show()
                    return@getStudentAttendances
                }

                val json = it.getOrThrow()

                if (json.length() == 0) {
                    this.current = listOf()
                    this.history = listOf()
                    return@getStudentAttendances
                }
                for (i in 0 until json.length()) {
                    val content = json.getJSONObject(i)
                    val time = SimpleDateFormat("YYYY-MM-DD hh-m-ss").format(content.getLong("time"))
                    val current = Calendar.getInstance()
                    val dur = content.getString("dur").toInt()
                    val min = (current.timeInMillis - content.getLong("time")) / 1000 / 60

                    data.add(AttendanceContent(content.getString("type"),
                        content.getString("ano"), content.getString("cname"),
                        time, content.getString("dur").toInt(), content.getBoolean("checked"),
                        content.getString("ended").equals("y") || min > dur))
                }
                this.current = data.filter {
                    !it.ended
                }
                this.history = data.filter {
                    it.ended
                }
                refreshList()
            }
        }
    }

    private fun refreshList() {
        val list = binding.listContent

        fetchData()

        var type: String = if (current == null && history == null) {
            "button"
        } else if (BcAPI.no == null) {
            "button"
        } else if (BcAPI.loggedinType == "teacher") {
            "teacher"
        } else {
            "student"
        }

        val adapter = AttendanceSortAdapter(requireContext(), type,
            current!!, history!!)
        list.adapter = adapter
    }
}