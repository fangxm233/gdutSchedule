package com.fangxm.schedule.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fangxm.schedule.BcAPI
import com.fangxm.schedule.data.MyCalendar
import com.fangxm.schedule.data.TermsManager
import com.fangxm.schedule.databinding.FragmentScheduleBinding
import com.fangxm.schedule.ui.schedule.weekContent.OnPageChangeCallBack
import com.fangxm.schedule.ui.schedule.weekContent.WeekContentFragmentAdapter
import java.util.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener




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

        val termId = "202201"
        TermsManager.init(requireContext())
        if (!TermsManager.hasTermData(termId)) {
            return root
        }

        val weekData = if (BcAPI.loggedinType == "teacher")
            TermsManager.getTeacherTermCoursesByWeek(termId, BcAPI.name!!)
        else TermsManager.getTermCoursesByWeek(termId)

        val list = List(weekData.size) {
            "第" + (it + 1) + "周"
        }

        val weekNumScroller = binding.weekDisplayScroller
        val weekNumAdapter = ScheduleWeekNumAdapter(recyclerView, requireContext(), list) {
            pager.setCurrentItem(it, true)
        }

        var startDate = MyCalendar(TermsManager.getTermStartDate(termId)).previousDay()

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
            val endPos = weekNumAdapter.getView(it).x.toInt()
            val halfWidth = weekNumAdapter.getView(it).width / 2
            weekNumAdapter.setFocused(weekNumAdapter.getView(it))
            weekNumScroller.smoothScrollTo(endPos + halfWidth - weekNumScroller.width / 2, 0)
            binding.textCurrentWeek.text = (it + 1).toString()
        })

        root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val currentWeek = TermsManager.currentWeek(termId)
                pager.setCurrentItem(currentWeek - 1, true)
                root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}