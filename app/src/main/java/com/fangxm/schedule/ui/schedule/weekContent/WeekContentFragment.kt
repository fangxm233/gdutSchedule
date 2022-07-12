package com.fangxm.schedule.ui.schedule.weekContent

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fangxm.schedule.R
import com.fangxm.schedule.data.ClassContent
import com.fangxm.schedule.data.MyCalendar
import com.fangxm.schedule.databinding.FragmentWeekContentBinding
import com.fangxm.schedule.ui.third.CustomButton

import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.animation.ValueAnimator.AnimatorUpdateListener
import com.fangxm.schedule.ui.util.Animations

class WeekContentFragment(var weekData: Array<Pair<MyCalendar, Array<ClassContent>>>) : Fragment() {
    private var _binding: FragmentWeekContentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val weekDate = listOf("星期一","星期二","星期三","星期四","星期五","星期六","星期日")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeekContentBinding.inflate(inflater, container, false)

        val root = binding.root

        val list1 = binding.contentLayout

        for (i in 0..6) {
            val innerList = LinearLayout(requireContext())
            innerList.orientation = LinearLayout.VERTICAL

            val weekDateView = inflater.inflate(R.layout.week_date_item, null) as LinearLayout
            weekDateView.findViewById<TextView>(R.id.content1).text = weekDate[i]
            weekDateView.findViewById<TextView>(R.id.content2).text =
                 "${weekData[i].first.month.toString()}.${weekData[i].first.day}"
            var param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 3.5f)
            innerList.addView(weekDateView, param)

            val dayData = weekData[i].second.sortedBy {
                it.startNum
            }
            if (dayData.count() == 0) {
                fillEmptyViews(innerList, 12)
            } else {
                var filledIndex = 1
                dayData.forEach {
                    if (it.startNum > filledIndex) {
                        fillEmptyViews(innerList, it.startNum - filledIndex)
                    }
                    val courseView = inflater.inflate(R.layout.course_item, null) as CustomButton
                    courseView.findViewById<TextView>(R.id.content1).text = it.title
                    courseView.findViewById<TextView>(R.id.content2).text = it.classroom
                    courseView.setBgNormalColor(Color.parseColor(it.color))
                    .setBgPressedColor(Color.parseColor(it.color)).use()
                    if (it.type == "exam") {
                        courseView.findViewById<TextView>(R.id.exam).visibility = View.VISIBLE
                    }
                    courseView.setOnClickListener { view ->
                        val classInfo = inflater.inflate(R.layout.class_info, null)
                        classInfo.findViewById<TextView>(R.id.content1).movementMethod = ScrollingMovementMethod.getInstance()
                        classInfo.findViewById<TextView>(R.id.content2).movementMethod = ScrollingMovementMethod.getInstance()
                        classInfo.findViewById<TextView>(R.id.content3).movementMethod = ScrollingMovementMethod.getInstance()
                        classInfo.findViewById<TextView>(R.id.content4).movementMethod = ScrollingMovementMethod.getInstance()
                        classInfo.findViewById<TextView>(R.id.content5).movementMethod = ScrollingMovementMethod.getInstance()
                        if (it.type == "exam") {
                            classInfo.findViewById<TextView>(R.id.title).text = it.title + " 考试"
                            classInfo.findViewById<TextView>(R.id.label1).text = "考试地点:"
                            classInfo.findViewById<TextView>(R.id.content1).text = it.classroom
                            classInfo.findViewById<TextView>(R.id.label2).text = "考试时间:"
                            classInfo.findViewById<TextView>(R.id.content2).text = it.time
                            classInfo.findViewById<TextView>(R.id.label3).text = "考试形式:"
                            classInfo.findViewById<TextView>(R.id.content3).text = it.examForm
                            classInfo.findViewById<TextView>(R.id.label4).text = "监考老师:"
                            classInfo.findViewById<TextView>(R.id.content4).text = it.teacherName
                            classInfo.findViewById<TextView>(R.id.label5).text = "考试日期:"
                            classInfo.findViewById<TextView>(R.id.content5).text = it.date
                        } else {
                            classInfo.findViewById<TextView>(R.id.title).text = it.title
                            classInfo.findViewById<TextView>(R.id.label1).text = "上课地点:"
                            classInfo.findViewById<TextView>(R.id.content1).text = it.classroom
                            classInfo.findViewById<TextView>(R.id.label2).text = "上课时间:"
                            classInfo.findViewById<TextView>(R.id.content2).text = it.time
                            classInfo.findViewById<TextView>(R.id.label3).text = "任课老师:"
                            classInfo.findViewById<TextView>(R.id.content3).text = it.teacherName
                            classInfo.findViewById<TextView>(R.id.label4).text = "授课内容:"
                            classInfo.findViewById<TextView>(R.id.content4).text = it.description
                            classInfo.findViewById<TextView>(R.id.label5).text = "上课班级:"
                            classInfo.findViewById<TextView>(R.id.content5).text = it.classes
                        }
                        val popup = PopupWindow(classInfo, 900,800)
                        popup.isOutsideTouchable = true
                        popup.isFocusable = true

                        val window = requireActivity().window
                        popup.animationStyle = R.style.PopupAnimation
                        Animations.fadeInBackground(window, 0.5f, 200)
                        popup.setOnDismissListener {
                            Animations.fadeOutBackground(window, 0.5f, 200)
                        }

                        popup.showAtLocation(window.decorView, Gravity.CENTER, 0, 0)
                    }
                    param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 5f * it.length)
                    innerList.addView(courseView, param)

                    filledIndex = it.startNum + it.length
                }
                if (filledIndex < 12) {
                    fillEmptyViews(innerList, 13 - filledIndex)
                }
            }

            param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            list1.addView(innerList, param)
        }


        return root
    }

    fun fillEmptyViews(list: LinearLayout, count: Int) {
        for (i in 1..count) {
            val view = View(requireContext())
            val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 5f)
            list.addView(view, param)
        }
    }
}