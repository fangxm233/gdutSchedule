package com.fangxm.schedule.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangxm.schedule.data.MyCalendar
import com.fangxm.schedule.data.TermsManager
import com.fangxm.schedule.databinding.FragmentScheduleBinding
import com.fangxm.schedule.ui.schedule.weekContent.OnPageChangeCallBack
import com.fangxm.schedule.ui.schedule.weekContent.WeekContentFragmentAdapter
import java.util.*

class ScheduleFragment : Fragment() {

    private lateinit var scheduleViewModel: ScheduleViewModel
    private var _binding: FragmentScheduleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        scheduleViewModel =
                ViewModelProvider(this).get(ScheduleViewModel::class.java)

        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textSchedule
//        scheduleViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        val recyclerView = binding.weekDisplay
        val pager = binding.contentPage

        if (!TermsManager.hasTermData("202102")) {
            return root
        }

        val weekData = TermsManager.getTermCoursesByWeek("202102")
        val list = List(weekData.size) {
            "第" + (it + 1) + "周"
        }

        val horizontalLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = horizontalLayoutManager
        val weekNumAdapter = ScheduleWeekNumAdapter(requireContext(), list) {
            pager.setCurrentItem(it, true)
        }
        recyclerView.adapter = weekNumAdapter

        var startDate = MyCalendar(TermsManager.getTermStartDate("202102")).previousDay()

        val data = Array(weekData.size) {
            (1..7).map {weekDate ->
                startDate = startDate.nextDay()
                Pair(startDate, weekData[it].filter { content ->
                    content.weekDate == weekDate
                }.toTypedArray())
            }.toTypedArray()
        }

        val fragmentAdapter = WeekContentFragmentAdapter(requireActivity(), data)
        pager.adapter = fragmentAdapter

        pager.registerOnPageChangeCallback(OnPageChangeCallBack{
            var targetX = 0
            var targetWidth = 0

            for (index in list.indices) {
                val view = (recyclerView.adapter as ScheduleWeekNumAdapter).getView(index)
                if (index >= it) {
                    targetWidth = view.width
                    (recyclerView.adapter as ScheduleWeekNumAdapter).setFocused(view)
                    break
                }
                targetX += view.width
            }

            targetX -= recyclerView.width / 2 - targetWidth / 2
            targetX = 0.coerceAtLeast(targetX)
            val current = recyclerView.computeHorizontalScrollOffset()

            recyclerView.smoothScrollBy(targetX - current, 0)

            binding.textCurrentWeek.text = (it + 1).toString()
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}