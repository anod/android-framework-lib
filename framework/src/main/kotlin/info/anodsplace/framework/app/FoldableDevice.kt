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

data class FoldableDeviceLayout(val isWideLayout: Boolean = false, val hinge: Rect = Rect())

interface FoldableDevice {
    val layout: StateFlow<FoldableDeviceLayout>
    var attachedToWindow: Boolean

    class NoOp : FoldableDevice {
        override var attachedToWindow = false
        override val layout = MutableStateFlow(FoldableDeviceLayout(false, Rect()))
    }

    companion object {
        // private fun isDuo(context: Context) = context.packageManager.hasSystemFeature("com.microsoft.device.display.displaymask")
        fun create(activity: ComponentActivity): FoldableDevice = FoldableDeviceReal(activity)
    }
}

fun WindowLayoutInfo.hingeBounds(): Rect {
    val foldingFeature = displayFeatures.firstOrNull {
        it is FoldingFeature && it.isSeparating
    }
    return foldingFeature?.bounds ?: Rect()
}

class FoldableDeviceReal(private val activity: ComponentActivity) : FoldableDevice {
    override var attachedToWindow = false

    private val window: WindowInfoTracker? = try {
        WindowInfoTracker.getOrCreate(activity)
    } catch (e: Exception) {
        null
    }

    override val layout: StateFlow<FoldableDeviceLayout> = window?.windowLayoutInfo(activity)?.map {
        FoldableDeviceLayout(isWideLayout = activity.resources.getBoolean(R.bool.wide_layout), hinge = it.hingeBounds())
    }?.stateIn(activity.lifecycleScope, SharingStarted.WhileSubscribed(), FoldableDeviceLayout(false, Rect()))
            ?: MutableStateFlow(FoldableDeviceLayout(false, Rect()))

}