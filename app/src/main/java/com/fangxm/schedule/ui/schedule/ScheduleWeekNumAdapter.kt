package com.fangxm.schedule.ui.schedule

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.fangxm.schedule.R
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fangxm.schedule.ActivityManager


class ScheduleWeekNumAdapter(private val mContext: Context,
                             private var data: List<String>,
                             private val callback: (position: Int) -> Unit) :
    RecyclerView.Adapter<ScheduleWeekNumAdapter.ViewHolder>() {
    private val views: Array<View?> = Array(data.size){null}
    private var currentFocused: Int = -1

    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(position: Int): Any {
        return data[position]
    }

    fun setFocused(view: View) {
        if (currentFocused != -1) {
            setFocusedColor(views[currentFocused]!!, false)
        }
        setFocusedColor(view, true)
        currentFocused = view.tag as Int
    }

    fun getView(position: Int): View {
        return views[position]!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(itemView: View,
                     private val callback: (position: Int) -> Unit) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var title: TextView? = itemView.findViewById(R.id.title)

        init {
            itemView.setOnClickListener(this::onClick)
        }

        override fun onClick(p0: View?) {
            callback(p0!!.tag as Int)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.week_item, null)
        return ViewHolder(view){
            callback(it)
            setFocused(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        views[position] = holder.itemView
        holder.title!!.text = data[position]
        holder.itemView.tag = position

        // 由于ViewHolder会被重复使用，所以要重置颜色防止高亮错误
        setFocusedColor(holder.itemView, currentFocused == position)
    }

    fun setFocusedColor(view: View, focused: Boolean) {
        val text = view.findViewById<TextView>(R.id.title)
        if (!focused) {
            // TODO: 获取颜色而不是硬编码
            text.setTextColor(Color.parseColor("#8A000000"))
        } else {
            val color = ContextCompat.getColor(ActivityManager.getCurrentActivity()!!.baseContext, R.color.pink)
            text.setTextColor(color)
        }
    }
}