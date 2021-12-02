// Copyright (c) 2020. Alex Gavrishev
package info.anodsplace.framework.app

import android.graphics.Rect
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import kotlinx.coroutines.flow.*

interface HingeDevice {
    val hinge: StateFlow<Rect>
    var attachedToWindow: Boolean

    class NoOp : HingeDevice {
        override var attachedToWindow = false
        override val hinge = MutableStateFlow(Rect())
    }

    companion object {
        // private fun isDuo(context: Context) = context.packageManager.hasSystemFeature("com.microsoft.device.display.displaymask")
        fun create(activity: ComponentActivity): HingeDevice = HingeDeviceReal(activity)
    }
}

fun WindowLayoutInfo.hingeBounds(): Rect {
    val foldingFeature = displayFeatures.firstOrNull {
        it is FoldingFeature && it.isSeparating
    }
    return foldingFeature?.bounds ?: Rect()
}

class HingeDeviceReal(activity: ComponentActivity) : HingeDevice {
    override var attachedToWindow = false

    private val window: WindowInfoTracker? = try {
        WindowInfoTracker.getOrCreate(activity)
    } catch (e: Exception) {
        null
    }

    override val hinge: StateFlow<Rect> = window?.windowLayoutInfo(activity)?.map {
        it.hingeBounds()
    }?.stateIn(activity.lifecycleScope, SharingStarted.WhileSubscribed(), Rect())
        ?: MutableStateFlow(Rect())

}