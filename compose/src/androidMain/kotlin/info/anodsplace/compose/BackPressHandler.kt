package info.anodsplace.compose

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.runtime.*

/**
 * This [Local] is used to provide an [OnBackPressedDispatcherOwner]:
 *
 * ```
 * Providers(BackPressedDispatcherAmbient provides requireActivity()) { }
 * ```
 *
 * and setting up the callbacks with [BackPressHandler].
 */
val LocalBackPressedDispatcher =
    staticCompositionLocalOf<OnBackPressedDispatcherOwner> { error("Ambient used without Provider") }

/**
 * This [Composable] can be used with a [LocalBackPressedDispatcher] to intercept a back press (if
 * [enabled]).
 */
@Composable
fun BackPressHandler(onBackPressed: () -> Unit, enabled: Boolean = true) {
    val dispatcher = LocalBackPressedDispatcher.current.onBackPressedDispatcher

    // This callback is going to be remembered only if onBackPressed is referentially equal.
    val backCallback = remember(onBackPressed) {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    SideEffect {
        backCallback.isEnabled = enabled
    }

    DisposableEffect(dispatcher, onBackPressed) {
        // Whenever there's a new dispatcher set up the callback
        dispatcher.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}