package info.anodsplace.compose

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun OverlayScrim(
        color: Color,
        onDismiss: () -> Unit,
        visible: Boolean
) {
    if (color != Color.Transparent) {
        val alpha = animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec(),
            label = "OverlayScrim"
        ).value
        val dismissModifier = if (visible) Modifier.clickable { onDismiss() } else Modifier
        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}