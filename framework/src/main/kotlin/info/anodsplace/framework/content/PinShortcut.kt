package info.anodsplace.framework.content

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PinShortcut(
    val id: String,
    val title: CharSequence,
    val intent: Intent,
    val icon: Icon
)

private fun PinShortcut.toShortcutInfo(context: Context): ShortcutInfo = ShortcutInfo.Builder(context, id)
        .setShortLabel(title)
        .setIntent(intent)
        .setIcon(icon)
        .build()

class PinShortcutManager(
        private val context: Context,
        private val androidShortcuts: ShortcutManager
) {
    val isSupported
        get() = androidShortcuts.isRequestPinShortcutSupported

    fun isPinned(shortcutId: String) = androidShortcuts.pinnedShortcuts.firstOrNull { it.id == shortcutId } != null

    suspend fun create(shortcut: PinShortcut) = withContext(Dispatchers.Default) {
        val pinShortcutInfo = shortcut.toShortcutInfo(context)
        val pinnedShortcutCallbackIntent = androidShortcuts.createShortcutResultIntent(pinShortcutInfo)
        val successCallback = PendingIntent.getBroadcast(context, /* request code */ 0, pinnedShortcutCallbackIntent, /* flags */ PendingIntent.FLAG_IMMUTABLE)
        androidShortcuts.requestPinShortcut(pinShortcutInfo, successCallback.intentSender)
    }

    suspend fun update(shortcut: PinShortcut) = withContext(Dispatchers.Default) {
        val pinShortcutInfo = shortcut.toShortcutInfo(context)
        androidShortcuts.updateShortcuts(listOf(pinShortcutInfo))
    }
}