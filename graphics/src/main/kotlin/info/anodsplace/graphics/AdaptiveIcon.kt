package info.anodsplace.graphics

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Build
import android.util.TypedValue
import kotlin.math.roundToInt

@TargetApi(Build.VERSION_CODES.O)
class AdaptiveIcon(
    val drawable: AdaptiveIconDrawable, private val mask: Path, val context: Context, maxScale: Int = 3) {

    constructor(drawable: AdaptiveIconDrawable, pathData: String, context: Context)
        : this(drawable, PathParser.createPathFromPathData(pathData), context)

    private val layerSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 108f * maxScale, context.resources.displayMetrics).roundToInt()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or
            Paint.FILTER_BITMAP_FLAG)

    @TargetApi(Build.VERSION_CODES.O)
    fun toBitmap(): Bitmap {
        val canvas = Canvas()
        val resultBitmap = Bitmap.createBitmap(layerSize, layerSize, Bitmap.Config.ARGB_8888)
        drawable.setBounds(0, 0, layerSize, layerSize)

        if (mask.isEmpty) {
            canvas.setBitmap(resultBitmap)
            drawable.draw(canvas)
            return resultBitmap
        }

        val maskMatrix = Matrix()
        maskMatrix.setScale(layerSize / MASK_SIZE, layerSize / MASK_SIZE)
        val cMask = Path()
        mask.transform(maskMatrix, cMask)

        val maskBitmap = Bitmap.createBitmap(layerSize, layerSize, Bitmap.Config.ALPHA_8)
        val layersBitmap = Bitmap.createBitmap(layerSize, layerSize, Bitmap.Config.ARGB_8888)
        val background = drawable.background
        val foreground = drawable.foreground

        paint.shader = null

        canvas.run {
            // Apply mask path to mask bitmap
            setBitmap(maskBitmap)
            drawPath(cMask, paint)

            // combine foreground and background on the layers bitmap
            setBitmap(layersBitmap)
            drawColor(Color.BLACK)

            background.draw(this)
            foreground.draw(this)

            setBitmap(resultBitmap)
        }

        // Draw mask with layers shader on result bitmap
        paint.shader = BitmapShader(layersBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawBitmap(maskBitmap, 0f, 0f, paint)

        return resultBitmap
    }

    companion object {
        const val MASK_SIZE = 100f
    }
}