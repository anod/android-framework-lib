package info.anodsplace.compose

sealed class ScreenLoadState<out T : Any> {
    object Loading: ScreenLoadState<Nothing>()
    data class Ready<out T : Any>(val value: T): ScreenLoadState<T>()
    data class Error(val message: String, val cause: Exception? = null): ScreenLoadState<Nothing>()
}