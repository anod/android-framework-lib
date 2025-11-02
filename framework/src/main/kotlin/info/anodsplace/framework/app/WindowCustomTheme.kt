package info.anodsplace.framework.app

import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import info.anodsplace.applog.AppLog

object WindowCustomTheme {
    fun apply(themeColors: CustomThemeColors, window: Window, view: View) {
        if (themeColors.statusBarColor.available) {
            window.statusBarColor = themeColors.statusBarColor.colorInt
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = themeColors.statusBarColor.isLight
            AppLog.d("statusBarColor ${themeColors.statusBarColor.colorInt.toHexString()}, isAppearanceLightStatusBars: ${themeColors.statusBarColor.isLight}")
        }
        if (themeColors.navigationBarColor.available) {
            window.navigationBarColor = themeColors.navigationBarColor.colorInt
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = themeColors.navigationBarColor.isLight
        }
    }
}