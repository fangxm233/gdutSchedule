package com.fangxm.schedule

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.fangxm.schedule.CallbackHandler
import com.fangxm.schedule.databinding.ActivityLoginBinding

import com.fangxm.schedule.R
import com.fangxm.schedule.ui.login.LoggedInUserView
import com.fangxm.schedule.ui.login.LoginViewModel
import com.fangxm.schedule.ui.login.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val number = binding.number
        val password = binding.password
        val verifyCode = binding.verifyCode
        val verifyCodeImg = binding.verifyCodeImg
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                number.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        number.afterTextChanged {
            loginViewModel.loginDataChanged(
                number.text.toString(),
                password.text.toString()
            )
        }

        val callback = { it: Result<String> ->
            if (it.isSuccess) {
                Toast.makeText(applicationContext, "登录成功", Toast.LENGTH_SHORT).show()
                setResult(200)
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    "登录失败: ${it.exceptionOrNull()!!.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            loading.visibility = View.GONE
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    number.text.toString(),
                    password.text.toString()
                )
            }
        }

        verifyCode.apply {
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            number.text.toString(),
                            password.text.toString(),
                            verifyCode.text.toString(),
                            callback
                        )
                }
                false
            }

            setOnFocusChangeListener { _, focus ->
                if (focus)
                    loginViewModel.getVerifyCodeImg {
                        verifyCodeImg.setImageBitmap(it.getOrThrow())
                    }
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(
                    number.text.toString(),
                    password.text.toString(),
                    verifyCode.text.toString(),
                    callback
                )
            }
        }

        verifyCodeImg.setOnClickListener {
            loginViewModel.getVerifyCodeImg {
                verifyCodeImg.setImageBitmap(it.getOrThrow())
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.number
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}