package com.fangxm.schedule.ui

import android.content.Context
import android.widget.BaseAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.fangxm.schedule.R
import android.widget.TextView

class ButtonItemAdapter(private val mContext: Context,
                        private val data: List<Map<String, String>>) :
    BaseAdapter() {
    private var viewHolder: ViewHolder? = null

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, arg2: ViewGroup): View {
        var view = view
        if (view == null) {
            viewHolder = ViewHolder()
            //获取listview对应的item布局
            view = LayoutInflater.from(mContext).inflate(R.layout.button_item, null)
            //初始化组件
            viewHolder!!.title = view.findViewById<View>(R.id.title) as TextView
            viewHolder!!.extra = view.findViewById<View>(R.id.extra_info) as TextView
            viewHolder!!.divider = view.findViewById(R.id.divider) as View
            viewHolder!!.divider1 = view.findViewById(R.id.divider1) as View
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val map = data[position]
        viewHolder!!.title!!.text = map["title"]
        if (map.containsKey("extra")) viewHolder!!.extra!!.text = map["extra"]
        viewHolder!!.divider!!.visibility = View.GONE
        viewHolder!!.divider1!!.visibility = View.GONE

//        if (position == 0 || position == 1 || position == 3 || position == 5 || position == 6 || position == 8) {
//            val map = data!![position]
//            viewHolder!!.title!!.text = map["title"]
//            viewHolder!!.divider!!.visibility = View.VISIBLE
//            viewHolder!!.divider1!!.visibility = View.GONE
//        } else {
//            if (position == 9) {
//                val map = data!![position]
//                viewHolder!!.title!!.text = map["title"]
//                viewHolder!!.divider!!.visibility = View.GONE
//                viewHolder!!.divider1!!.visibility = View.VISIBLE
//            } else {
//                val map = data!![position]
//                viewHolder!!.title!!.text = map["title"]
//                viewHolder!!.divider!!.visibility = View.GONE
//                viewHolder!!.divider1!!.visibility = View.GONE
//            }
//        }
        return view!!
    }

    internal class ViewHolder {
        var title: TextView? = null
        var extra: TextView? = null
        var divider: View? = null
        var divider1: View? = null
    }
}