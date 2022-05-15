package com.fangxm.schedule.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val verifyCodeError: Int? = null,
    val isDataValid: Boolean = false
)