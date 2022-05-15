package com.fangxm.schedule.ui.login

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.fangxm.schedule.data.LoginRepository

import com.fangxm.schedule.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String, verifyCode: String, callback: (Result<String>) -> Unit) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username, password, verifyCode, callback)
    }

    fun loginDataChanged(username: String, password: String) {
        _loginForm.value = LoginFormState(isDataValid = true)
    }

    fun getVerifyCodeImg(callback: (kotlin.Result<Bitmap>) -> Unit) {
        return loginRepository.dataSource.getVerifyCodeImg(callback)
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}