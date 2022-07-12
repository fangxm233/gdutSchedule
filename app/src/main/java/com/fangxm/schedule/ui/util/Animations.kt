package com.fangxm.schedule.ui.util

import android.animation.ValueAnimator
import android.view.Window
import android.view.WindowManager

object Animations {
    fun fadeInBackground(window: Window, alpha: Float, duration: Long) {
        if (window.attributes.alpha < 1f) return
        val ani = ValueAnimator.ofFloat(1f, alpha)
        ani.duration = duration
        ani.addUpdateListener { animator ->
            val lp: WindowManager.LayoutParams =
                window.attributes
            lp.alpha = animator.animatedValue as Float
            window.attributes = lp
        }
        ani.start()
    }

    fun fadeOutBackground(window: Window, alpha: Float, duration: Long) {
        if (window.attributes.alpha > alpha) return
        val ani = ValueAnimator.ofFloat(alpha, 1f)
        ani.duration = duration
        ani.addUpdateListener { animator ->
            val lp: WindowManager.LayoutParams =
                window.attributes
            lp.alpha = animator.animatedValue as Float
            window.attributes = lp
        }
        ani.start()
    }
}