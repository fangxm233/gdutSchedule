package com.fangxm.schedule.ui.schedule.weekContent

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.fangxm.schedule.data.ClassContent

class WeekContentFragmentAdapter(activity: FragmentActivity, val data: Array<Array<Array<ClassContent>>>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = WeekContentFragment(data[position])
        return fragment
    }
}

class OnPageChangeCallBack(val callback: (position: Int) -> Unit): ViewPager2.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        callback(position)
    }
}