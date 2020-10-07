package info.anodsplace.framework.app

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.core.content.getSystemService
import info.anodsplace.framework.AppLog

class AlertWindow(private val context: Context) {
    private val wm: WindowManager = context.getSystemService()!!
    private var view: View? = null

    companion object {
        val isSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        fun hasPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return false
            }

            return Settings.canDrawOverlays(context)
        }
    }

    fun show(params: WindowManager.LayoutParams, configure: (view: View) -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            AppLog.e("Old sdk")
            return
        }

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
        )
        overlay.x = params.x
        overlay.y = params.y
        overlay.gravity = params.gravity

        view = View(context).also {
            configure(it)
            wm.addView(it, overlay)
        }
    }

    fun hide() {
        view?.let {
            it.setOnClickListener(null)
            wm.removeView(it)
        }
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
