package info.anodsplace.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class FastBitmapDrawable(private var mBitmap: Bitmap?) : Drawable() {
    private var width: Int = 0
    private var height: Int = 0

    var bitmap: Bitmap?
        get() = mBitmap
        set(b) {
            mBitmap = b
            if (b != null) {
                width = b.width
                height = b.height
            } else {
                height = 0
                width = 0
            }
        }

    init {
        if (mBitmap != null) {
            width = mBitmap!!.width
            height = mBitmap!!.height
        } else {
            height = 0
            width = 0
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(mBitmap!!, 0.0f, 0.0f, null)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {
        //Not supported
    }

    override fun setColorFilter(cf: ColorFilter?) {
        //Not Supported
    }

    override fun getIntrinsicWidth(): Int {
        return width
    }

    override fun getIntrinsicHeight(): Int {
        return height
    }

    override fun getMinimumWidth(): Int {
        return width
    }

    override fun getMinimumHeight(): Int {
        return height
    }
}