package com.fangxm.schedule.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fangxm.schedule.ActivityManager
import com.fangxm.schedule.JwAPI
import com.fangxm.schedule.LoginActivity
import com.fangxm.schedule.data.TermsManager
import com.fangxm.schedule.databinding.FragmentMyBinding
import com.fangxm.schedule.ui.ButtonItemAdapter

class MyFragment : Fragment() {

    private lateinit var myViewModel: MyViewModel
    private var _binding: FragmentMyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val buttonTexts = arrayOf("教师登录", "学生登录", "获取课表")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        myViewModel =
                ViewModelProvider(this).get(MyViewModel::class.java)

        _binding = FragmentMyBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textMy
//        myViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        val listView = binding.listContent

        val list = List(buttonTexts.size) {
            val map = HashMap<String, String>()
            map["title"] = buttonTexts[it]
            map
        }

        val adapter = ButtonItemAdapter(requireActivity().applicationContext, list)

        val termId = "202102"

        listView.setOnItemClickListener { _, _, i, _ ->
            val data = list[i]
            if (data["title"] == "学生登录") {
                toLogin(1, "student")
                return@setOnItemClickListener
            }
            if (data["title"] == "教师登录") {
                toLogin(1, "teacher")
                return@setOnItemClickListener
            }
            if (data["title"] == "获取课表") {
                // 防止切换走了导致requireContext报错
                val context = requireContext()
                TermsManager.clearTermData(termId)
                JwAPI.getCoursesData(termId) {
                    if (it.isFailure) {
                        val message = it.exceptionOrNull()!!.message!!
                        if (message == "登录信息过期") {
                            Toast.makeText(context, "登录信息过期", Toast.LENGTH_SHORT).show()
                            toLogin(1, "student")
                            return@getCoursesData
                        } else {
                            Toast.makeText(context, "未知错误: $message", Toast.LENGTH_SHORT).show()
                            return@getCoursesData
                        }
                    }

                    TermsManager.setTermCoursesFromJson(termId, it.getOrThrow())
                    Toast.makeText(context, "课表获取成功", Toast.LENGTH_SHORT).show()
                }
                JwAPI.getExamData(termId) {
                    if (it.isFailure) {
                        val message = it.exceptionOrNull()!!.message!!
                        if (message != "登录信息过期") {
                            Toast.makeText(context, "未知错误: $message", Toast.LENGTH_SHORT).show()
                            return@getExamData
                        }
                        return@getExamData
                    }

                    TermsManager.setTermExamsFromJson(termId, it.getOrThrow())
                    Toast.makeText(context, "考试安排获取成功", Toast.LENGTH_SHORT).show()
                }
                JwAPI.getTermStartDate(termId) {
                    if (it.isFailure) {
                        val message = it.exceptionOrNull()!!.message!!
                        if (message != "登录信息过期") {
                            Toast.makeText(context, "未知错误: $message", Toast.LENGTH_SHORT).show()
                            return@getTermStartDate
                        }
                        return@getTermStartDate
                    }

                    TermsManager.setTermStartDate(termId, it.getOrThrow())
                    Toast.makeText(context, "开学日期获取成功", Toast.LENGTH_SHORT).show()
                }
            }
        }

        listView.adapter = adapter

        return root
    }

    fun toLogin(requestCode: Int, type: String) {
        val intent = Intent()
        intent.setClass(requireActivity().applicationContext, LoginActivity::class.java)
        intent.putExtra("type", type)
        ActivityManager.getCurrentActivity()?.startActivityForResult(intent, requestCode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}