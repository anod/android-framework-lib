package info.anodsplace.framework.app

import android.content.Context
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.core.content.getSystemService
import info.anodsplace.applog.AppLog

class AlertWindow(private val context: Context) {
   private val wm: WindowManager by lazy { context.getSystemService()!! }
    private var view: View? = null

    companion object {
        fun hasPermission(context: Context): Boolean {

            return Settings.canDrawOverlays(context)
        }
    }

    fun show(params: WindowManager.LayoutParams, configure: (view: View) -> Unit) {

        if (!hasPermission(context)) {
            AppLog.e("No permission")
            return
        }

        val overlay = WindowManager.LayoutParams(
            params.width,
            params.height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or params.flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            x = params.x
            y = params.y
            gravity = params.gravity
        }

        view = View(context).also {
            configure(it)
            wm.addView(it, overlay)
        }
    }

    fun hide() {
        view?.let {
            try {
                if (it.isAttachedToWindow) {
                    it.setOnClickListener(null)
                    wm.removeView(it)
                }
            } catch (e: Exception) {
                AppLog.e(e)
            }
        }
        view = null
    }

    fun move(x: Int, y: Int) {
        view?.let {
            try {
                wm.updateViewLayout(it, (it.layoutParams as WindowManager.LayoutParams).also { params ->
                    params.x = x
                    params.y = y
                })
            } catch (e: Exception) {
                AppLog.e(e)
            }
        }
    }
}
