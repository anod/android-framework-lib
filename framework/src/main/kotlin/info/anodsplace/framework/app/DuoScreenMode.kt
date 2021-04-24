// Copyright (c) 2020. Alex Gavrishev
package info.anodsplace.framework.app

import android.app.Activity
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import androidx.core.util.Consumer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.window.FoldingFeature
import androidx.window.WindowLayoutInfo
import androidx.window.WindowManager
import java.util.concurrent.Executor

interface HingeDevice {
    val hinge: LiveData<Rect>
    var attachedToWindow: Boolean

    class NoOp : HingeDevice {
        override var attachedToWindow = false
        override val hinge = MutableLiveData(Rect())
    }

    companion object {
        // private fun isDuo(context: Context) = context.packageManager.hasSystemFeature("com.microsoft.device.display.displaymask")
        fun create(activity: Activity): HingeDevice = HingeDeviceReal(activity)
    }
}

class HingeLiveData(private val xWindowManager: WindowManager) : MutableLiveData<Rect>(Rect()),
    Consumer<WindowLayoutInfo> {
    private val handler = Handler(Looper.getMainLooper())
    private val mainThreadExecutor = Executor { r: Runnable -> handler.post(r) }

    override fun onActive() {
        super.onActive()
        xWindowManager.registerLayoutChangeCallback(mainThreadExecutor, this)
    }

    override fun onInactive() {
        super.onInactive()
        xWindowManager.unregisterLayoutChangeCallback(this)
    }

    override fun accept(info: WindowLayoutInfo?) {
        value = info?.hingeBounds() ?: Rect()
    }
}

fun WindowLayoutInfo.hingeBounds(): Rect {
    val foldingFeature = displayFeatures.firstOrNull {
        it is FoldingFeature && it.isSeparating
    }
    return foldingFeature?.bounds ?: Rect()
}

class HingeDeviceReal(activity: Activity) : HingeDevice {
    override var attachedToWindow = false

    private val xWindowManager: WindowManager? = try {
        WindowManager(activity)
    } catch (e: Exception) {
        null
    }

    override val hinge: LiveData<Rect> = xWindowManager?.let {
        HingeLiveData(it)
    } ?: MutableLiveData(Rect())
//
//    val hinge1: Rect
//        get() {
//            if (!attachedToWindow) {
//                return Rect()
//            }
//            val wm = xWindowManager ?: return Rect()
//            try {
//                val hinge =
//                    wm.currentWindowMetrics.wm.windowLayoutInfo.displayFeatures.firstOrNull { it.type == DisplayFeature.TYPE_HINGE }
//                return hinge?.bounds ?: Rect()
//            } catch (e: Exception) {
//                AppLog.e(e)
//            }
//            return Rect()
//        }
}