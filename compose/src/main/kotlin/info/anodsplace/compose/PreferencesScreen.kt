package info.anodsplace.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceSlider(
    initialValue: Int,
    onValueChanged: (Int) -> Unit,
    item: PreferenceItem,
    suffixText: @Composable (() -> Unit)? = null,
    startIcon: @Composable (() -> Unit)? = null,
    endIcon: @Composable (() -> Unit)? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    var value: Float by remember { mutableStateOf(initialValue.toFloat()) }
    Column(
        modifier = Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Preference(
            item = item,
            onClick = { },
            trailing = {
                OutlinedTextField(
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .widthIn(max = 100.dp),
                    value = value.toInt().toString(),
                    onValueChange = { value ->
                        if (value.isEmpty()) {
                            onValueChanged(value.trim().toInt())
                        }
                    },
                    textStyle = MaterialTheme.typography.labelLarge,
                    singleLine = true,
                    trailingIcon = suffixText
                )
            },
            secondary = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    startIcon?.let {
                        IconButton(
                            modifier = Modifier.weight(1f),
                            onClick = { value = max(0f, value - 1.0f) },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            startIcon()
                        }
                    } ?: Box(modifier = Modifier.weight(1f))
                    Slider(
                        enabled = item.enabled,
                        modifier = Modifier.weight(6f),
                        value = value,
                        valueRange = 0f..100f,
                        onValueChangeFinished = {
                            onValueChanged(value.toInt())
                        },
                        onValueChange = { value = it }
                    )
                    endIcon?.let {
                        IconButton(
                            modifier = Modifier.weight(1f),
                            onClick = { value = min(100f, value + 1.0f) },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            endIcon()
                        }
                    } ?: Box(modifier = Modifier.weight(1f))
                }
            }
        )
    }
}

@Composable
fun PreferenceCategory(
    item: PreferenceItem.Category,
    modifier: Modifier = Modifier,
    colors: PreferencesColors = PreferencesDefaults.colors(),
) {
    val text = if (item.titleRes != 0) stringResource(id = item.titleRes) else item.title
    Text(
        text = if (item.capitalize) text.uppercase() else text,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        style = MaterialTheme.typography.titleSmall.copy(colors.categoryColor),
        letterSpacing = 1.2.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencePick(
    item: PreferenceItem.Pick,
    placeholder: @Composable (() -> Unit) = { },
    colors: PreferencesColors = PreferencesDefaults.colors(),
    onPickValue: (String) -> Unit
) {
    val entries =
        if (item.entriesRes == 0) item.entries else stringArrayResource(id = item.entriesRes)
    val entryValues = if (item.entryValuesRes == 0) {
        if (item.entryValues.isEmpty()) entries.mapIndexed { index, _ -> index.toString() }
            .toTypedArray() else item.entryValues
    } else stringArrayResource(id = item.entryValuesRes)
    val selected = remember(item.value) { entryValues.indexOf(item.value) }

    Preference(
        item,
        colors = colors,
        secondary = {
            Column {
                PickGroup(
                    options = entries,
                    selectedIndex = selected,
                    enabled = item.enabled
                ) { newIndex ->
                    val value = entryValues[newIndex]
                    onPickValue(value)
                }
                placeholder()
            }
        },
        onClick = { })
}

@Composable
fun PreferenceColor(
    item: PreferenceItem.Color,
    colors: PreferencesColors,
    onClick: () -> Unit,
    secondary: @Composable (() -> Unit)? = null,
) {
    Preference(
        item = item,
        colors = colors,
        secondary = secondary,
        onClick = onClick,
        trailing = {
            val isNotVisible = item.color.isNotVisible
            val size = 40.dp
            val lineWidth = 1.dp
            val colorModifier = Modifier
                .size(size)
                .clip(shape = MaterialTheme.shapes.medium)
                .let {
                    if (isNotVisible) {
                        it.drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawLine(
                                    color = colors.selectedBorderColor,
                                    start = Offset(0f, size.toPx()),
                                    end = Offset(size.toPx(), 0f),
                                    strokeWidth = lineWidth.toPx()
                                )
                                drawLine(
                                    color = colors.selectedBorderColor,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.toPx(), size.toPx()),
                                    strokeWidth = lineWidth.toPx()
                                )
                            }
                        }
                    } else if (item.color != null) it.background(item.color) else it
                }
                .border(
                    border = BorderStroke(1.dp, colors.selectedBorderColor),
                    shape = MaterialTheme.shapes.medium
                )

            Box(modifier = colorModifier)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Preference(
    item: PreferenceItem,
    colors: PreferencesColors = PreferencesDefaults.colors(),
    onClick: () -> Unit,
    secondary: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ListItem(
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .alpha(if (item.enabled) 1.0f else 0.6f)
                .clickable(onClick = onClick, enabled = item.clickable && item.enabled),
            headlineText = {
                Text(
                    text = if (item.titleRes != 0) stringResource(id = item.titleRes) else item.title,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            supportingText = if (item.summaryRes != 0 || item.summary.isNotEmpty()) {
                {
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = if (item.summaryRes != 0) stringResource(id = item.summaryRes) else item.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colors.descriptionColor
                        )
                    )
                }
            } else null,
            trailingContent = trailing?.let { { it() } }
        )
        secondary?.let {
            Box(
                Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.TopStart
            ) { it() }
        }
    }
}

@Composable
fun PreferenceSwitch(
    checked: Boolean,
    item: PreferenceItem,
    placeholder: @Composable (() -> Unit)? = null,
    colors: PreferencesColors = PreferencesDefaults.colors(),
    switchColors: SwitchColors = SwitchDefaults.colors(),
    onCheckedChange: (Boolean) -> Unit
) {
    Preference(
        item,
        colors,
        secondary = placeholder,
        onClick = { onCheckedChange(!checked) }
    ) {
        Switch(
            enabled = item.enabled,
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = switchColors
        )
    }
}

@Composable
fun PreferenceCheckbox(
    checked: Boolean,
    item: PreferenceItem,
    placeholder: @Composable (() -> Unit)? = null,
    colors: PreferencesColors = PreferencesDefaults.colors(),
    onCheckedChange: (Boolean) -> Unit
) {
    Preference(
        item,
        colors,
        secondary = placeholder,
        onClick = { onCheckedChange(!checked) }
    ) {
        Checkbox(
            enabled = item.enabled,
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                uncheckedColor = colors.checkBoxColor.copy(alpha = 0.6f),
                // disabledColor = checkBoxColor.copy(alpha = 0.38f)
            )
        )
    }
}


data class PreferencesColors(
    val categoryColor: Color,
    val descriptionColor: Color,
    val checkBoxColor: Color,
    val borderColor: Color,
    val selectedBorderColor: Color,
    val containerColor: Color,
    val spacerColor: Color
)

object PreferencesDefaults {
    @Composable
    fun colors(
        categoryColor: Color = MaterialTheme.colorScheme.secondary,
        descriptionColor: Color = MaterialTheme.colorScheme.onSurface,
        checkBoxColor: Color = MaterialTheme.colorScheme.onBackground,
        borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        selectedBorderColor: Color = MaterialTheme.colorScheme.primary,
        containerColor: Color = MaterialTheme.colorScheme.surface,
        spacerColor: Color = MaterialTheme.colorScheme.surfaceVariant
    ) = PreferencesColors(
        categoryColor = categoryColor,
        descriptionColor = descriptionColor,
        checkBoxColor = checkBoxColor,
        borderColor = borderColor,
        selectedBorderColor = selectedBorderColor,
        containerColor = containerColor,
        spacerColor = spacerColor
    )
}

@Composable
fun PreferencesScreen(
    preferences: List<PreferenceItem>,
    modifier: Modifier = Modifier,
    onClick: (item: PreferenceItem) -> Unit = { },
    colors: PreferencesColors = PreferencesDefaults.colors(),
    placeholder: @Composable (PreferenceItem, paddingValues: PaddingValues) -> Unit = { _, _ -> },
) {
    var listItem by remember { mutableStateOf<PreferenceItem.List?>(null) }
    Surface(
        color = colors.containerColor
    ) {
        LazyColumn(
            modifier = modifier.fillMaxWidth()
        ) {
            items(
                preferences.size,
                key = { index -> preferences[index].stableKey },
                contentType = { index -> preferences[index].contentType }
            ) { index ->
                val paddingValues = PaddingValues(top = 16.dp, bottom = 8.dp)
                when (val item = preferences[index]) {
                    is PreferenceItem.Category -> PreferenceCategory(item = item, colors = colors)
                    is PreferenceItem.CheckBox -> {
                        var checked by remember { mutableStateOf(item.checked) }
                        PreferenceCheckbox(
                            checked = checked,
                            item = item,
                            placeholder = {
                                placeholder(item, paddingValues = paddingValues)
                            },
                            colors = colors,
                            onCheckedChange = { newChecked ->
                                checked = newChecked
                                onClick(item.copy(checked = newChecked))
                            })
                    }

                    is PreferenceItem.List -> Preference(
                        item = item,
                        colors = colors,
                        secondary = {
                            placeholder(item, paddingValues = paddingValues)
                        },
                        onClick = { listItem = item }
                    ) { }

                    is PreferenceItem.Pick -> {
                        PreferencePick(
                            item = item,
                            colors = colors,
                            placeholder = { placeholder(item, paddingValues = paddingValues) },
                            onPickValue = { value ->
                                onClick(item.copy(value = value))
                            }
                        )
                    }

                    is PreferenceItem.Switch -> {
                        var checked by remember { mutableStateOf(item.checked) }
                        PreferenceSwitch(
                            checked = checked,
                            item = item,
                            placeholder = {
                                placeholder(item, paddingValues = paddingValues)
                            },
                            colors = colors,
                            onCheckedChange = { newChecked ->
                                checked = newChecked
                                onClick(item.copy(checked = newChecked))
                            })
                    }

                    is PreferenceItem.Color -> PreferenceColor(
                        item = item,
                        colors = colors,
                        secondary = {
                            placeholder(item, paddingValues = paddingValues)
                        },
                        onClick = { onClick(item) })

                    is PreferenceItem.Text -> Preference(
                        item = item,
                        colors = colors,
                        secondary = {
                            placeholder(item, paddingValues = paddingValues)
                        },
                        onClick = { onClick(item) }) { }

                    is PreferenceItem.Placeholder -> {
                        placeholder(item, paddingValues = paddingValues)
                    }

                    is PreferenceItem.Spacer -> {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(item.height)
                                .background(color = colors.spacerColor)
                        )
                    }
                }
            }
        }
    }

    if (listItem != null) {
        PreferenceListDialog(item = listItem!!) { changedListItem ->
            onClick(changedListItem)
            listItem = null
        }
    }
}

@Composable
fun PreferenceListDialog(
    item: PreferenceItem.List,
    onValueChange: (item: PreferenceItem.List) -> Unit
) {
    val entries = stringArrayResource(id = item.entries)
    val entryValues = if (item.entryValues == 0)
        entries.mapIndexed { index, _ -> index.toString() }.toTypedArray()
    else stringArrayResource(id = item.entryValues)
    var value by remember { mutableStateOf(item.value) }
    AlertDialog(
        modifier = Modifier.padding(16.dp),
        title = { Text(text = if (item.titleRes != 0) stringResource(id = item.titleRes) else item.title) },
        text = {
            val selected = entryValues.indexOf(value)
            RadioGroup(entries, selected) { newIndex ->
                value = entryValues[newIndex]
                onValueChange(item.copy(value = value))
            }
        },
        confirmButton = { },
        onDismissRequest = {
            onValueChange(item)
        }
    )
}

@Preview("InCarScreen Light", widthDp = 360, heightDp = 1020)
@Composable
fun InCarScreenLight() {
    MaterialTheme {
        Surface(
            color = MaterialTheme.colorScheme.secondary
        ) {
            PreferencesScreen(
                preferences = listOf(
                    PreferenceItem.Category(title = "Category"),
                    PreferenceItem.Pick(
                        entries = arrayOf(
                            "Manually",
                            "Every hour",
                            "Every 2 hours",
                            "Every 3 hours",
                            "Every 6 hours",
                            "Every 12 hours"
                        ),
                        value = "Every 2 hours",
                        title = "Check for new updates",
                        key = "update_frequency"
                    ),
                    PreferenceItem.Color(
                        title = "Background color",
                        summary = "#${Color(48340).toColorHex()}",
                        color = Color(48340)
                    ),
                    PreferenceItem.Text(
                        title = "Bluetooth device",
                        summary = "Choose bluetooth device which enable InCar mode"
                    ),
                    PreferenceItem.CheckBox(
                        checked = true,
                        title = "Keep screen On",
                        summary = "When checked, prevents screen from automatically turning off"
                    ),
                    PreferenceItem.Switch(
                        checked = true,
                        title = "Route to speaker",
                        summary = "Route all incoming calls to phones speaker"
                    ),
                    PreferenceItem.Placeholder(key = "slider"),
                    PreferenceItem.Color(
                        title = "No color",
                        summary = "Item without assigned color",
                        color = null
                    ),
                    PreferenceItem.Switch(checked = false, title = "Wi-Fi Only")
                ),
                onClick = {},
                placeholder = { preferenceItem, _ ->
                    when (preferenceItem.key) {
                        "slider" -> {
                            PreferenceSlider(
                                initialValue = 100,
                                onValueChanged = { },
                                item = PreferenceItem.Text(
                                    title = "Media level",
                                    summary = "Change desired level of volume"
                                ),
                                startIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.VolumeDown,
                                        contentDescription = null
                                    )
                                },
                                endIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.VolumeUp,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}

@Preview("InCarScreen Dark", widthDp = 360, heightDp = 1020)
@Composable
fun InCarScreenDark() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            PreferencesScreen(listOf(
                PreferenceItem.Category(title = "Category"),
                PreferenceItem.Spacer(),
                PreferenceItem.Text(
                    title = "Bluetooth device",
                    summary = "Choose bluetooth device which enable InCar mode"
                ),
                PreferenceItem.Color(
                    title = "Background color",
                    color = Color(48340)
                ),
                PreferenceItem.Color(
                    title = "No color",
                    summary = "Item without assigned color",
                    color = null
                ),
                PreferenceItem.Spacer(),
                PreferenceItem.CheckBox(
                    checked = true,
                    title = "Keep screen On",
                    summary = "When checked, prevents screen from automatically turning off"
                ),
                PreferenceItem.Switch(
                    checked = true,
                    title = "Route to speaker",
                    summary = "Route all incoming calls to phones speaker"
                ),
                PreferenceItem.Switch(checked = false, title = "Wi-Fi Only"),
                PreferenceItem.Pick(
                    entries = arrayOf("All", "Installed", "Not Installed", "Updatable"),
                    value = "All",
                    title = "Default list",
                    summary = "Choose which filter will be applied when app is opened",
                    key = "default-filter"
                ),
            ), onClick = {})
        }
    }
}