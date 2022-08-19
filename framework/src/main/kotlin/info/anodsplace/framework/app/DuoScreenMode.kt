// Copyright (c) 2020. Alex Gavrishev
package info.anodsplace.framework.app

import android.graphics.Rect
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import info.anodsplace.framework.R
import kotlinx.coroutines.flow.*

data class HingeDeviceLayout(val isWideLayout: Boolean = false, val hinge: Rect = Rect())

interface HingeDevice {
    val layout: StateFlow<HingeDeviceLayout>
    var attachedToWindow: Boolean

    class NoOp : HingeDevice {
        override var attachedToWindow = false
        override val layout = MutableStateFlow(HingeDeviceLayout(false, Rect()))
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

class HingeDeviceReal(private val activity: ComponentActivity) : HingeDevice {
    override var attachedToWindow = false

    private val window: WindowInfoTracker? = try {
        WindowInfoTracker.getOrCreate(activity)
    } catch (e: Exception) {
        null
    }

    override val layout: StateFlow<HingeDeviceLayout> = window?.windowLayoutInfo(activity)?.map {
        HingeDeviceLayout(isWideLayout = activity.resources.getBoolean(R.bool.wide_layout), hinge = it.hingeBounds())
    }?.stateIn(activity.lifecycleScope, SharingStarted.WhileSubscribed(), HingeDeviceLayout(false, Rect()))
            ?: MutableStateFlow(HingeDeviceLayout(false, Rect()))

}