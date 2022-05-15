package com.fangxm.schedule.data

import android.graphics.Bitmap
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

    fun login(number: String, password: String, verifyCode: String, callback: (Result<String>) -> Unit) {
//        try {
            JwAPI.login(number, password, verifyCode, callback)
            // TODO: handle loggedInUser authentication
//            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
//            return Result.success(fakeUser)
//        } catch (e: Throwable) {
//            return Result.failure(IOException("Error logging in", e))
//        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}