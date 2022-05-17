package com.fangxm.schedule.ui.attendance

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fangxm.schedule.*
import com.fangxm.schedule.databinding.FragmentAttendanceBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AttendanceFragment : Fragment() {

    private lateinit var attendanceViewModel: AttendanceViewModel
    private var _binding: FragmentAttendanceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var current: List<AttendanceContent>? = null
    private var history: List<AttendanceContent>? = null
    private var teachingCourses = hashMapOf<String, String>() // name to cno
    private var duration = listOf(1, 5, 10, 20, 30, 40, 60)
    private var type: String? = null

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
            if (view.tag == null) return@setOnItemClickListener
            if (view.tag is String) {
                if (view.tag == "学生登录") {
                    toLogin(1, "student")
                    return@setOnItemClickListener
                }
                if (view.tag == "教师登录") {
                    toLogin(1, "teacher")
                    return@setOnItemClickListener
                }
            }
            if (view.tag is AttendanceContent) {
                val content = view.tag as AttendanceContent
                if (BcAPI.loggedinType == "student") {
                    BcAPI.checkin(content.ano!!) {
                        if (it.isSuccess) {
                            Toast.makeText(requireContext(), "签到成功", Toast.LENGTH_SHORT).show()
                            fetchData()
                            return@checkin
                        } else {
                            Toast.makeText(requireContext(), "签到失败: ${it.exceptionOrNull()!!.message}",
                                Toast.LENGTH_SHORT).show()
                            return@checkin
                        }
                    }
                } else {
                    val intent = Intent()
                    intent.setClass(requireActivity().applicationContext, CheckedStudents::class.java)
                    intent.putExtra("ano", content.ano!!)
                    intent.putExtra("ended", content.ended)
                    ActivityManager.getCurrentActivity()?.startActivityForResult(intent, 2)
                }
            }
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

    override fun onResume() {
        super.onResume()
        fetchData()
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
            this.type = "button"
            return
        }

        this.current = listOf(AttendanceContent("正在刷新..."))
        this.history = listOf()
        this.type = "button"
        if(BcAPI.loggedinType == "teacher") {
            BcAPI.getTeacherAttendances {
                if (it.isFailure) {
                    Toast.makeText(requireContext(), "获取失败", Toast.LENGTH_SHORT).show()
                    return@getTeacherAttendances
                }
                this.type = "teacher"

                val json = it.getOrThrow()

                if (json.length() == 0) {
                    this.current = listOf()
                    this.history = listOf()
                    refreshList()
                    return@getTeacherAttendances
                }
                for (i in 0 until json.length()) {
                    val content = json.getJSONObject(i)
                    val format = SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.SIMPLIFIED_CHINESE)
                    format.timeZone = TimeZone.getTimeZone("Asia/Shanghai");
                    val time = format.format(content.getLong("time"))
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
                }.reversed()
                this.history = data.filter {
                    it.ended
                }.reversed()
                this.type = "teacher"
                refreshList()
            }

            BcAPI.getTeachingCourses {
                if (it.isFailure) {
                    Toast.makeText(requireContext(), "课程获取失败", Toast.LENGTH_SHORT).show()
                    return@getTeachingCourses
                }

                val json = it.getOrThrow()
                teachingCourses = HashMap()

                for (i in 0 until  json.length()) {
                    val content = json.getJSONObject(i)
                    val cno = content.getString("cno")
                    val name = content.getString("cname")
                    teachingCourses!![name] = cno
                }

                refreshList()
            }
        } else {
            BcAPI.getStudentAttendances {
                if (it.isFailure) {
                    Toast.makeText(requireContext(), "获取失败", Toast.LENGTH_SHORT).show()
                    return@getStudentAttendances
                }
                this.type = "student"

                val json = it.getOrThrow()
                if (json.length() == 0) {
                    this.current = listOf()
                    this.history = listOf()
                    refreshList()
                    return@getStudentAttendances
                }
                for (i in 0 until json.length()) {
                    val content = json.getJSONObject(i)
                    val format = SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.SIMPLIFIED_CHINESE)
                    format.timeZone = TimeZone.getTimeZone("Asia/Shanghai");
                    val time = format.format(content.getLong("time"))
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
                }.reversed()
                this.history = data.filter {
                    it.ended
                }.reversed()
                refreshList()
            }
        }
    }

    private fun refreshList() {
        val list = binding.listContent

        val adapter = AttendanceSortAdapter(requireContext(), this.type!!,
            current!!, history!!, teachingCourses, duration) { cno, duration, locate ->
            val activity = ActivityManager.getCurrentActivity() as MainActivity
            val long = activity.getLocation()!!.longitude
            val lat = activity.getLocation()!!.latitude
            BcAPI.raiseAttendance(if (locate) "1" else "0", cno, duration.toString(), long.toString(), lat.toString()) {
                if (it.isSuccess) {
                    Toast.makeText(requireContext(), "签到发起成功", Toast.LENGTH_SHORT).show()
                    fetchData()
                    refreshList()
                }
            }
        }
        list.adapter = adapter
    }

    fun toLogin(requestCode: Int, type: String) {
        val intent = Intent()
        intent.setClass(requireActivity().applicationContext, LoginActivity::class.java)
        intent.putExtra("type", type)
        ActivityManager.getCurrentActivity()?.startActivityForResult(intent, requestCode)
    }
}