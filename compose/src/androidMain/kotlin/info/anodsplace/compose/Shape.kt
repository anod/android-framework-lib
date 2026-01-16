package info.anodsplace.compose

import android.graphics.Path
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import info.anodsplace.graphics.AdaptiveIcon

fun Path.toShape(): Shape = GenericShape {
        _, _ -> addPath(this@toShape.asComposePath())
}

class SystemIconShape(iconSizePx: Int) : Shape {
    private val shape = AndroidPathIconShape(
        androidPath = AdaptiveIcon.getSystemDefaultMask().let { mask ->
            AdaptiveIcon.maskToScaledPath(mask, iconSizePx)
        }
    )

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = shape.createOutline(size, layoutDirection, density)
}

class AndroidPathIconShape(androidPath: Path) : Shape {
    private val shape = androidPath.toShape()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = shape.createOutline(size, layoutDirection, density)
}