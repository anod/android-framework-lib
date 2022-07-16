package info.anodsplace.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

@Composable
fun BackgroundSurface(
        modifier: Modifier = Modifier,
        shape: Shape = RectangleShape,
        content: @Composable () -> Unit
) = Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
) {
    content()
}