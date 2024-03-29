package info.anodsplace.graphics

import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target

/**
 * @author Alex Gavrishev
 * *
 * @date 03/03/2017.
 */
private val sDarkTargets = arrayOf(Target.DARK_VIBRANT, Target.DARK_MUTED, Target.MUTED, Target.VIBRANT)
private val sLightTargets = arrayOf(Target.LIGHT_VIBRANT, Target.LIGHT_MUTED, Target.MUTED, Target.VIBRANT)

fun Palette.chooseDark(): Palette.Swatch? {
    sDarkTargets
            .mapNotNull { this.getSwatchForTarget(it) }
            .forEach { return it }
    return null
}

fun Palette.chooseLight(@ColorInt defaultColor: Int): Palette.Swatch {
    sLightTargets
            .mapNotNull { this.getSwatchForTarget(it) }
            .forEach { return it }
    return Palette.Swatch(defaultColor, 0)
}
