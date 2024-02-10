package info.anodsplace.framework.anim

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewAnimationUtils
import kotlin.math.max

/**
 * @author alex
 * @date 2015-05-25
 */
object RevealAnimatorCompat {
    private const val duration = 150L

    fun show(viewRoot: View, x: Int, y: Int, delay: Int): Animator {
        val anim: Animator
        val finalRadius = max(viewRoot.width, viewRoot.height)
        anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0f, finalRadius.toFloat())
        anim.duration = duration
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                viewRoot.visibility = View.VISIBLE
            }
        })
        anim.startDelay = delay.toLong()
        return anim
    }

    fun hide(viewRoot: View, x: Int, y: Int, delay: Int): Animator {
        val anim: Animator
        val initialRadius = viewRoot.width
        anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, initialRadius.toFloat(), 0f)
        anim.duration = duration
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                viewRoot.visibility = View.INVISIBLE
            }
        })
        anim.duration = duration
        anim.startDelay = delay.toLong()
        return anim
    }
}