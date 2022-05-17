package com.fangxm.schedule.ui.schedule.weekContent

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fangxm.schedule.R
import com.fangxm.schedule.data.ClassContent
import com.fangxm.schedule.data.MyCalendar
import com.fangxm.schedule.databinding.FragmentWeekContentBinding
import com.fangxm.schedule.ui.third.CustomButton
import java.util.*

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