package info.anodsplace.framework.app

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

/**
 * @author Alex Gavrishev
 * @date 04/12/2017
 */

class CustomThemeColor(
    val available: Boolean,
    @param:ColorInt val colorInt: Int,
    val isLight: Boolean) {

    constructor(@ColorInt colorInt: Int = 0,
                isLight: Boolean = false
    ) : this(true, colorInt, isLight)

    companion object {
        val none = CustomThemeColor(false, 0, false)
        val white = CustomThemeColor(true, Color.WHITE, true)
        val black = CustomThemeColor(true, Color.BLACK, false)
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
                    isLight = parcel.readByte() != 0.toByte()
            ),
            CustomThemeColor(
                    available = parcel.readByte() != 0.toByte(),
                    colorInt = parcel.readInt(),
                    isLight = parcel.readByte() != 0.toByte()
            )
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
        parcel.writeByte(if (statusBarColor.isLight) 1 else 0)
        parcel.writeInt(if (navigationBarColor.available) 1 else 0)
        parcel.writeInt(navigationBarColor.colorInt)
        parcel.writeByte(if (navigationBarColor.isLight) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }
}