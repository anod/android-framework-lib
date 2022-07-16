package info.anodsplace.framework.app

import android.app.Activity
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors

/**
 * @author Alex Gavrishev
 * @date 04/12/2017
 */
interface CustomThemeActivity {
    val themeRes: Int
    val themeColors: CustomThemeColors
}

class CustomThemeColor(
        val available: Boolean,
        @ColorInt val colorInt: Int,
        @ColorRes val colorRes: Int,
        @AttrRes val colorAttr: Int,
        val isLight: Boolean) {

    constructor(@ColorInt colorInt: Int = 0,
                @ColorRes colorRes: Int = 0,
                @AttrRes colorAttr: Int = 0,
                isLight: Boolean = false) : this(true, colorInt, colorRes, colorAttr, isLight)

    companion object {
        val none = CustomThemeColor(false, 0, 0, 0, false)
        val white = CustomThemeColor(true, Color.WHITE, 0, 0, true)
        val black = CustomThemeColor(true, Color.BLACK, 0, 0, false)
    }

    fun get(activity: Activity): Int {
        if (colorAttr != 0) {
            return MaterialColors.getColor(activity, colorAttr, activity.javaClass.simpleName)
        }
        if (colorRes != 0) {
            return ContextCompat.getColor(activity, colorRes)
        }
        return colorInt
    }
}

class CustomThemeColors(
        val available: Boolean,
        val statusBarColor: CustomThemeColor,
        val navigationBarColor: CustomThemeColor) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(),
            CustomThemeColor(
                    available = parcel.readByte() != 0.toByte(),
                    colorInt = parcel.readInt(),
                    colorRes = parcel.readInt(),
                    colorAttr = parcel.readInt(),
                    isLight = parcel.readByte() != 0.toByte()
            ),
            CustomThemeColor(
                    available = parcel.readByte() != 0.toByte(),
                    colorInt = parcel.readInt(),
                    colorRes = parcel.readInt(),
                    colorAttr = parcel.readInt(),
                    isLight = parcel.readByte() != 0.toByte()
            )
    )

    constructor(isStatusBarLight: Boolean, @ColorRes statusBarColor: Int, isNavBarLight: Boolean, @ColorRes navigationBarColor: Int) : this(
            available = true,
            statusBarColor = CustomThemeColor(colorRes = statusBarColor, isLight = isStatusBarLight),
            navigationBarColor = CustomThemeColor(colorRes = navigationBarColor, isLight = isNavBarLight)
    )

    constructor(@ColorInt statusBarColor: Int, navigationBarColor: CustomThemeColor) : this(
            available = true,
            statusBarColor = CustomThemeColor(colorInt = statusBarColor, isLight = isStatusBarLight(statusBarColor)),
            navigationBarColor = navigationBarColor
    )

    constructor(statusBarColor: CustomThemeColor, navigationBarColor: CustomThemeColor) : this(true, statusBarColor, navigationBarColor)


    companion object {
        val none = CustomThemeColors(false, CustomThemeColor.none, CustomThemeColor.none)

        fun isStatusBarLight(@ColorInt statusBarColor: Int): Boolean {
            val lum = ColorUtils.calculateLuminance(statusBarColor)
            return lum > 0.5
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<CustomThemeColors> {
            override fun createFromParcel(parcel: Parcel): CustomThemeColors {
                return CustomThemeColors(parcel)
            }

            override fun newArray(size: Int): Array<CustomThemeColors?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (available) 1 else 0)
        parcel.writeInt(if (statusBarColor.available) 1 else 0)
        parcel.writeInt(statusBarColor.colorInt)
        parcel.writeInt(statusBarColor.colorRes)
        parcel.writeInt(statusBarColor.colorAttr)
        parcel.writeByte(if (statusBarColor.isLight) 1 else 0)
        parcel.writeInt(if (navigationBarColor.available) 1 else 0)
        parcel.writeInt(navigationBarColor.colorInt)
        parcel.writeInt(navigationBarColor.colorRes)
        parcel.writeInt(navigationBarColor.colorAttr)
        parcel.writeByte(if (navigationBarColor.isLight) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }
}