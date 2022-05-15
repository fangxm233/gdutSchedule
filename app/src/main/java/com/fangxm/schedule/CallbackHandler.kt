package com.fangxm.schedule

class CallbackHandler {
    companion object{
        @JvmStatic
        fun runOnUI(runnable: Runnable) {
            ActivityManager.getCurrentActivity()?.runOnUiThread(runnable)
        }
    }
}