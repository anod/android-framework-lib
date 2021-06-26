package info.anodsplace.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BackgroundSurface(modifier: Modifier = Modifier, content: @Composable () -> Unit) = Surface(
    modifier = modifier,
    color = MaterialTheme.colors.background
) {
    content()
}