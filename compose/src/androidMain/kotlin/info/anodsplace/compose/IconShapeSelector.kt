package info.anodsplace.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import info.anodsplace.graphics.AdaptiveIcon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconShapeSelector(
        pathMasks: Array<String>,
        names: Array<String>,
        selected: String,
        modifier: Modifier = Modifier,
        defaultSystemMask: String = "",
        systemMaskName: String = "",
        onPathChange: (String) -> Unit = {}
) {
    val iconSize = 48.dp
    val iconSizePx = with(LocalDensity.current) { iconSize.roundToPx() }
    var value by remember { mutableStateOf(selected) }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement  = Arrangement.spacedBy(4.dp),
    ) {
        val isNone = value.isEmpty()
        Box(
            modifier = Modifier
                .size(iconSize, iconSize)
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(
                        alpha = if (isNone) 1.0f else 0.1f
                )))
                .clickable(onClick = {
                    value = ""
                    onPathChange("")
                }),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = names[0],
                color = if (isNone) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
            )
        }

        if (!isNone) {
            val exists = pathMasks.firstOrNull { it == defaultSystemMask } != null
            if (!exists) {
                IconShape(
                    pathMask = defaultSystemMask,
                    isSelected = value == defaultSystemMask,
                    title = systemMaskName,
                    onClick = {
                        value = defaultSystemMask
                        onPathChange(defaultSystemMask)
                    },
                    iconSizePx = iconSizePx,
                    iconSize = iconSize
                )
            }
        }

        pathMasks.filter { it.isNotEmpty() }.forEachIndexed { index, pathMask ->
            IconShape(
                pathMask = pathMask,
                isSelected = value == pathMask,
                title = names[index],
                onClick = {
                    value = pathMask
                    onPathChange(pathMask)
                },
                iconSizePx = iconSizePx,
                iconSize = iconSize
            )
        }
    }
}

@Composable
private fun IconShape(
    pathMask: String,
    isSelected: Boolean,
    title: String,
    iconSizePx: Int,
    iconSize: Dp,
    onClick: () -> Unit
) {
    val outline = AdaptiveIcon.maskToScaledPath(pathMask, iconSizePx)
    Box(
        modifier = Modifier
            .size(iconSize, iconSize)
            .clip(AndroidPathIconShape(androidPath = outline))
            .clickable(onClick = onClick, role = Role.Button, onClickLabel = title)
            .background(color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
    ) { }
}