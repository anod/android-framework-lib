package info.anodsplace.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
    color = MaterialTheme.colors.background,
    contentColor = MaterialTheme.colors.onBackground
) {
    content()
}