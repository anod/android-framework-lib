package info.anodsplace.graphics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PorterDuff
import androidx.core.graphics.createBitmap
import java.lang.ref.SoftReference

class BitmapCachedDecoder(private val iconBitmapSize: Int, private val isLowMemoryDevice: Boolean) {
    companion object {
        private val sLock = Any()
    }

    private val unusedBitmaps: ArrayList<SoftReference<Bitmap>> = ArrayList()

    private val cachedIconCanvas = object : SoftReferenceThreadLocal<Canvas>() {
        override fun initialValue(): Canvas {
            return Canvas()
        }
    }

    private val cachedBitmapFactoryOptions = object : SoftReferenceThreadLocal<BitmapFactory.Options>() {
        override fun initialValue(): BitmapFactory.Options {
            return BitmapFactory.Options()
        }
    }

    fun toBitmap(data: ByteArray?): Bitmap? {
        if (data == null || data.isEmpty()) return null
        var unusedBitmap: Bitmap? = null
        synchronized(sLock) {
            // not in cache; we need to load it from the db
            while ((unusedBitmap == null || !unusedBitmap.isMutable ||
                        unusedBitmap.width != iconBitmapSize ||
                        unusedBitmap.height != iconBitmapSize) && unusedBitmaps.isNotEmpty()
            ) {
                unusedBitmap = unusedBitmaps.removeAt(0).get()
            }
            if (unusedBitmap != null) {
                val canvas = cachedIconCanvas.get()!!
                canvas.setBitmap(unusedBitmap)
                canvas.drawColor(0, PorterDuff.Mode.CLEAR)
                canvas.setBitmap(null)
            }

            if (unusedBitmap == null) {
                unusedBitmap = createBitmap(iconBitmapSize, iconBitmapSize)
            }
        }
        return decodeIcon(data, unusedBitmap!!)
    }

    private fun decodeIcon(data: ByteArray?, unusedBitmap: Bitmap): Bitmap? {
        if (data == null || data.isEmpty()) return null
        val opts = cachedBitmapFactoryOptions.get()!!
        opts.outWidth = iconBitmapSize
        opts.outHeight = iconBitmapSize
        opts.inSampleSize = 1
        if (canUseForInBitmap(unusedBitmap, opts)) {
            opts.inBitmap = unusedBitmap
        }
        if (isLowMemoryDevice) {
            // Always prefer RGB_565 config for low res. If the bitmap has transparency, it will
            // automatically be loaded as ALPHA_8888.
            opts.inPreferredConfig = Bitmap.Config.RGB_565
        } else {
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return try {
            BitmapFactory.decodeByteArray(data, 0, data.size, opts)
        } catch (e: Exception) {
            // throw new RuntimeException(e.getMessage(), e);
            null
        }
    }

    private fun canUseForInBitmap(candidate: Bitmap, targetOptions: BitmapFactory.Options): Boolean {
        // From Android 4.4 (KitKat) onward we can re-use if the byte size of
        // the new bitmap is smaller than the reusable bitmap candidate
        // allocation byte count.
        val width = targetOptions.outWidth / targetOptions.inSampleSize
        val height = targetOptions.outHeight / targetOptions.inSampleSize
        val byteCount = width * height * getBytesPerPixel(candidate.config)
        return byteCount <= candidate.allocationByteCount
    }

    private fun getBytesPerPixel(config: Bitmap.Config?): Int {
        return when (config) {
            Bitmap.Config.ARGB_8888 -> 4
            Bitmap.Config.RGB_565 -> 2
            Bitmap.Config.ARGB_4444 -> 2
            Bitmap.Config.ALPHA_8 -> 1
            else -> 1
        }
    }
}

private abstract class SoftReferenceThreadLocal<T> {
    private val threadLocal: ThreadLocal<SoftReference<T?>?> = ThreadLocal<SoftReference<T?>?>()

    protected abstract fun initialValue(): T?

    fun set(t: T?) {
        threadLocal.set(SoftReference<T?>(t))
    }

    fun get(): T? {
        val reference = threadLocal.get()
        var obj: T?
        if (reference == null) {
            obj = initialValue()
            threadLocal.set(SoftReference<T?>(obj))
        } else {
            obj = reference.get()
            if (obj == null) {
                obj = initialValue()
                threadLocal.set(SoftReference<T?>(obj))
            }
        }
        return obj
    }
}