package com.fangxm.schedule.data

import android.graphics.Bitmap
import android.widget.Toast
import com.fangxm.schedule.BcAPI
import com.fangxm.schedule.JwAPI
import com.fangxm.schedule.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun getVerifyCodeImg(callback: (Result<Bitmap>) -> Unit) {
        JwAPI.getVerifyCodeImg(callback)
    }

    fun login(type: String, number: String, password: String, verifyCode: String, callback: (Result<Unit>) -> Unit) {
        if (type == "student") {
            JwAPI.login(number, password, verifyCode, callback)
            BcAPI.login(type[0].toString(), number, password){
                if (it.isSuccess) {
                    println("考勤登录成功")
                } else {
                    println("考勤登录失败")
                }
            }
        }
        else BcAPI.login(type[0].toString(), number, password, callback)
    }

    fun logout() {
        // TODO: revoke authentication
    }
}