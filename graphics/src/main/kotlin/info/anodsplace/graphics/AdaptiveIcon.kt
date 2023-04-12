package info.anodsplace.graphics

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.util.TypedValue
import kotlin.math.roundToInt

class AdaptiveIcon(
        private val context: Context,
        val mask: Path,
        maxScale: Int = 3
) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)

    private val layerSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 108f * maxScale, context.resources.displayMetrics).roundToInt()

    fun fromDrawable(drawable: AdaptiveIconDrawable): Bitmap {
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

    fun transform(input: Bitmap, widthPx: Int, heightPx: Int): Bitmap {
        if (mask.isEmpty || input.width != input.height || widthPx != heightPx) {
            return input
        }

        val isTransparent = arrayOf(
                input.getPixel(0, 0) == Color.TRANSPARENT,
                input.getPixel(0, input.height - 1) == Color.TRANSPARENT,
                input.getPixel(input.width - 1, 0) == Color.TRANSPARENT,
                input.getPixel(input.width - 1, input.height - 1) == Color.TRANSPARENT
        ).reduce { a, b -> a && b }

        if (!isTransparent) {
            val canvas = Canvas()
            val resultBitmap = Bitmap.createBitmap(widthPx, widthPx, Bitmap.Config.ARGB_8888)

            val maskMatrix = Matrix()
            maskMatrix.setScale(widthPx / MASK_SIZE, widthPx / MASK_SIZE)
            val cMask = Path()
            mask.transform(maskMatrix, cMask)

            val maskBitmap = Bitmap.createBitmap(widthPx, widthPx, Bitmap.Config.ALPHA_8)
            val layersBitmap = Bitmap.createBitmap(widthPx, widthPx, Bitmap.Config.ARGB_8888)
            val background = BitmapDrawable(context.resources, input)
            background.setBounds(0, 0, widthPx, widthPx)

            paint.shader = null

            canvas.run {
                // Apply mask path to mask bitmap
                setBitmap(maskBitmap)
                drawPath(cMask, paint)

                // combine foreground and background on the layers bitmap
                setBitmap(layersBitmap)
                drawColor(Color.TRANSPARENT)

                background.draw(this)

                setBitmap(resultBitmap)
            }

            // Draw mask with layers shader on result bitmap
            paint.shader = BitmapShader(layersBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            canvas.drawBitmap(maskBitmap, 0f, 0f, paint)

            input.recycle()
            return resultBitmap
        }

        return input
    }

    companion object {
        private const val circlePath = "M50 0C77.6 0 100 22.4 100 50C100 77.6 77.6 100 50 100C22.4 100 0 77.6 0 50C0 22.4 22.4 0 50 0Z"
        const val MASK_SIZE = 100f

        fun getSystemDefaultMask(): String {
            val configResId = Resources.getSystem().getIdentifier("config_icon_mask", "string", "android")

            if (configResId == 0) {
                return circlePath
            }

            val configMask = Resources.getSystem().getString(configResId)

            if (configMask.isEmpty()) {
                return circlePath
            }

            return configMask
        }

        fun maskToPath(mask: String): Path {
            if (mask.isEmpty()) {
                return Path()
            }

            return try {
                PathParser.createPathFromPathData(mask) ?: PathParser.createPathFromPathData(circlePath)
            } catch (e: Exception) {
                PathParser.createPathFromPathData(circlePath)
            }
        }

        fun maskToScaledPath(pathMask: String, iconSizePx: Int, maxSize: Float = MASK_SIZE): Path {
            val path = maskToPath(pathMask)
            val outline = Path()
            val maskMatrix = Matrix().apply {
                setScale(iconSizePx / maxSize, iconSizePx / maxSize)
            }
            path.transform(maskMatrix, outline)
            return outline
        }
    }
}