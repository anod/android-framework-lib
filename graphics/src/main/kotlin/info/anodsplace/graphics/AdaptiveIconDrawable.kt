package info.anodsplace.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Icon
import android.util.TypedValue
import androidx.core.graphics.drawable.toBitmap

fun AdaptiveIconDrawable.toIcon(context: Context): Icon {
    val appIconSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 108f, context.resources.displayMetrics).toInt()
    val bitmap = toBitmap(appIconSize, appIconSize, Bitmap.Config.ARGB_8888)
    return Icon.createWithAdaptiveBitmap(bitmap)
}