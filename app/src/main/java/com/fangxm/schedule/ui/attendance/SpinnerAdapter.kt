package com.fangxm.schedule.ui.attendance

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.view.setPadding

class SpinnerAdapter(private val mContext: Context,
                     private val data: List<String>) : BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(p0: Int): Any {
        return data[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = TextView(mContext)
        view.text = data[p0]
        view.setPadding(10)
        return view
    }
}