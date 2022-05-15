package com.fangxm.schedule.ui.attendance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.fangxm.schedule.R
import java.lang.Exception

class AttendanceSortAdapter(private val mContext: Context,
                            private val type: String,
                            private val current: List<AttendanceContent>,
                            private val history: List<AttendanceContent>) :
    BaseAdapter() {

    override fun getCount(): Int {
        var count = 0
        if (type == "teacher") count = 1
        if (type == "button") return current.size
        if (current.isNotEmpty()) {
            count += current.size + 1
        }
        if (history.isNotEmpty()) {
            count += history.size + 1
        }
        return count
    }

    override fun getItem(position: Int): Any {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(mContext)

        if (type == "button") {
            val data = current[p0]
            val view = inflater.inflate(R.layout.button_item, null)
            view.findViewById<TextView>(R.id.title).text = data.title
            return view
        } else if (type == "teacher") {
            if (p0 == 0) {
                val view = inflater.inflate(R.layout.raise_attendance, null)
                return view
            }
            if (current.isNotEmpty()) {
                if (p0 == 1) {
                    val view = TextView(mContext)
                    view.text = "当前签到"
                    return view
                }
                if (p0 > 1 && p0 < current.size + 3)
                    return createTeacherAttendance(current[p0 - 2])
            }
            if (history.isNotEmpty()) {
                if (p0 == current.size + 1) {
                    val view = TextView(mContext)
                    view.text = "历史签到"
                    return view
                }
                return createTeacherAttendance(history[p0 - current.size - 3])
            }
        }
        throw Exception()
    }

    fun createTeacherAttendance(data: AttendanceContent): View {
        val view = LinearLayout(mContext)
        view.orientation = LinearLayout.VERTICAL
        val inflater = LayoutInflater.from(mContext)

        val type = inflater.inflate(R.layout.button_item, null)
        type.findViewById<View>(R.id.line_divider).visibility = View.VISIBLE
        type.findViewById<TextView>(R.id.title).text = "签到类型:"
        type.findViewById<TextView>(R.id.extra_info).text = getAttendanceTypeText(data.type)
        view.addView(type)

        val course = inflater.inflate(R.layout.button_item, null)
        course.findViewById<View>(R.id.line_divider).visibility = View.VISIBLE
        course.findViewById<TextView>(R.id.title).text = "课程:"
        course.findViewById<TextView>(R.id.extra_info).text = data.courseName
        view.addView(course)

        val time = inflater.inflate(R.layout.button_item, null)
        time.findViewById<View>(R.id.line_divider).visibility = View.VISIBLE
        time.findViewById<TextView>(R.id.title).text = "发起时间:"
        time.findViewById<TextView>(R.id.extra_info).text = data.raiseTime
        view.addView(time)

        val duration = inflater.inflate(R.layout.button_item, null)
        duration.findViewById<View>(R.id.line_divider).visibility = View.VISIBLE
        duration.findViewById<TextView>(R.id.title).text = "持续时间:"
        duration.findViewById<TextView>(R.id.extra_info).text = data.duration.toString() + "分钟"
        view.addView(duration)

        val checked = inflater.inflate(R.layout.button_item, null)
        checked.findViewById<View>(R.id.divider).visibility = View.VISIBLE
        checked.findViewById<TextView>(R.id.title).text = "已签:"
        checked.findViewById<TextView>(R.id.extra_info).text = data.checkedNum + "/" + data.totalNum + "人"
        view.addView(checked)

        return view
    }

    fun createStudentAttendance(data: AttendanceContent): View {
        val view = LinearLayout(mContext)
        view.orientation = LinearLayout.VERTICAL
        val inflater = LayoutInflater.from(mContext)

        val type = inflater.inflate(R.layout.button_item, null)
        type.findViewById<View>(R.id.line_divider).visibility = View.VISIBLE
        type.findViewById<TextView>(R.id.title).text = "签到类型:"
        type.findViewById<TextView>(R.id.extra_info).text = getAttendanceTypeText(data.type)
        view.addView(type)

        val course = inflater.inflate(R.layout.button_item, null)
        course.findViewById<View>(R.id.line_divider).visibility = View.VISIBLE
        course.findViewById<TextView>(R.id.title).text = "课程:"
        course.findViewById<TextView>(R.id.extra_info).text = data.courseName
        view.addView(course)

        val time = inflater.inflate(R.layout.button_item, null)
        time.findViewById<View>(R.id.line_divider).visibility = View.VISIBLE
        time.findViewById<TextView>(R.id.title).text = "发起时间:"
        time.findViewById<TextView>(R.id.extra_info).text = data.raiseTime
        view.addView(time)

        val duration = inflater.inflate(R.layout.button_item, null)
        duration.findViewById<View>(R.id.line_divider).visibility = View.VISIBLE
        duration.findViewById<TextView>(R.id.title).text = "持续时间:"
        duration.findViewById<TextView>(R.id.extra_info).text = data.duration.toString() + "分钟"
        view.addView(duration)

        val checked = inflater.inflate(R.layout.button_item, null)
        checked.findViewById<View>(R.id.divider).visibility = View.VISIBLE
        checked.findViewById<TextView>(R.id.title).text = "是否签到:"
        checked.findViewById<TextView>(R.id.extra_info).text = if (data.checked) "已签" else "未签"
        view.addView(checked)

        return view
    }

    fun getAttendanceTypeText(type: String): String {
        return if (type == "0") "普通签到" else "GPS签到"
    }
}