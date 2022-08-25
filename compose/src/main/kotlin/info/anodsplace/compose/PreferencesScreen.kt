package info.anodsplace.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceSlider(
        initialValue: Int,
        onValueChanged: (Int) -> Unit,
        item: PreferenceItem,
        suffixText: @Composable () -> Unit = {},
        startIcon: @Composable (modifier: Modifier) -> Unit = { Box(modifier = it) {} },
        endIcon: @Composable (modifier: Modifier) -> Unit = { Box(modifier = it) {} },
        paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    var value: Float by remember { mutableStateOf(initialValue.toFloat()) }
    Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Preference(item = item, onClick = { }) { }
        OutlinedTextField(
                value = value.toInt().toString(),
                onValueChange = { value ->
                    if (value.isEmpty()) {
                        onValueChanged(value.trim().toInt())
                    }
                },
                trailingIcon = suffixText
        )
        Row(
                modifier = Modifier
                        .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            startIcon(
                    Modifier
                            .size(24.dp)
                            .weight(1f)
            )
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
            endIcon(
                    Modifier
                            .size(24.dp)
                            .weight(1f)
            )
        }
    }
}

@Composable
fun PreferenceCategory(
        item: PreferenceItem.Category,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
            text = if (item.titleRes != 0) stringResource(id = item.titleRes) else item.title,
            modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            style = MaterialTheme.typography.titleSmall.copy(color)
    )
}

@Composable
fun PreferencePick(
        item: PreferenceItem.Pick,
        placeholder: @Composable (() -> Unit) = { },
        descriptionColor: Color = MaterialTheme.colorScheme.onSurface,
        onPickValue: (String) -> Unit
) {
    val entries =
            if (item.entriesRes == 0) item.entries else stringArrayResource(id = item.entriesRes)
    val entryValues = if (item.entryValuesRes == 0) {
        if (item.entryValues.isEmpty()) entries.mapIndexed { index, _ -> index.toString() }
                .toTypedArray() else item.entryValues
    } else stringArrayResource(id = item.entryValuesRes)
    var value by remember { mutableStateOf(item.value) }
    val selected = entryValues.indexOf(value)

    Preference(
            item,
            descriptionColor,
            secondary = {
                Column {
                    PickGroup(
                            options = entries,
                            selectedIndex = selected,
                            enabled = item.enabled
                    ) { newIndex ->
                        value = entryValues[newIndex]
                        item.value = value
                        onPickValue(value)
                    }
                    placeholder()
                }
            },
            onClick = { })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Preference(
        item: PreferenceItem,
        descriptionColor: Color = MaterialTheme.colorScheme.onSurface,
        onClick: () -> Unit,
        secondary: @Composable (() -> Unit)? = null,
        trailing: @Composable (() -> Unit)? = null
) {

    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
    ) {
        ListItem(
                modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .alpha(if (item.enabled) 1.0f else 0.6f)
                        .clickable(onClick = onClick, enabled = item.enabled),
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
                                        color = descriptionColor
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
        descriptionColor: Color = MaterialTheme.colorScheme.onSurface,
        switchColors: SwitchColors = SwitchDefaults.colors(),
        onCheckedChange: (Boolean) -> Unit
) {
    Preference(item, descriptionColor,
            secondary = placeholder,
            onClick = {
                onCheckedChange(!checked)
            }) {
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
        descriptionColor: Color = MaterialTheme.colorScheme.onSurface,
        checkBoxColor: Color = MaterialTheme.colorScheme.onBackground,
        onCheckedChange: (Boolean) -> Unit
) {
    Preference(item, descriptionColor,
            secondary = placeholder,
            onClick = {
                onCheckedChange(!checked)
            }) {
        Checkbox(
                enabled = item.enabled,
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                        uncheckedColor = checkBoxColor.copy(alpha = 0.6f),
                        // disabledColor = checkBoxColor.copy(alpha = 0.38f)
                )
        )
    }
}

@Composable
fun PreferencesScreen(
        preferences: List<PreferenceItem>,
        modifier: Modifier = Modifier,
        onClick: (item: PreferenceItem) -> Unit = { },
        categoryColor: Color = MaterialTheme.colorScheme.secondary,
        descriptionColor: Color = MaterialTheme.colorScheme.onSurface,
        checkBoxColor: Color = MaterialTheme.colorScheme.onBackground,
        placeholder: @Composable (PreferenceItem, paddingValues: PaddingValues) -> Unit = { _, _ -> },
) {
    var listItem by remember { mutableStateOf<PreferenceItem.List?>(null) }
    LazyColumn(
            modifier = modifier.fillMaxWidth()
    ) {
        items(preferences.size) { index ->
            val paddingValues = PaddingValues(top = 16.dp, bottom = 8.dp)
            when (val item = preferences[index]) {
                is PreferenceItem.Category -> PreferenceCategory(item = item, color = categoryColor)
                is PreferenceItem.CheckBox -> {
                    var checked by remember { mutableStateOf(item.checked) }
                    PreferenceCheckbox(
                            checked = checked,
                            item = item,
                            placeholder = {
                                placeholder(item, paddingValues = paddingValues)
                            },
                            descriptionColor = descriptionColor,
                            checkBoxColor = checkBoxColor,
                            onCheckedChange = { newChecked ->
                                checked = newChecked
                                item.checked = newChecked
                                onClick(item)
                            })
                }
                is PreferenceItem.List -> Preference(
                        item = item,
                        descriptionColor = descriptionColor,
                        secondary = {
                            placeholder(item, paddingValues = paddingValues)
                        },
                        onClick = { listItem = item }
                ) { }
                is PreferenceItem.Pick -> PreferencePick(
                        item = item,
                        descriptionColor = descriptionColor,
                        placeholder = { placeholder(item, paddingValues = paddingValues) },
                        onPickValue = { value ->
                            item.value = value
                            onClick(item)
                        }
                )
                is PreferenceItem.Switch -> {
                    var checked by remember { mutableStateOf(item.checked) }
                    PreferenceSwitch(
                            checked = checked,
                            item = item,
                            placeholder = {
                                placeholder(item, paddingValues = paddingValues)
                            },
                            descriptionColor = descriptionColor,
                            onCheckedChange = { newChecked ->
                                item.checked = newChecked
                                checked = newChecked
                                onClick(item)
                            })
                }
                is PreferenceItem.Text -> Preference(
                        item = item,
                        descriptionColor = descriptionColor,
                        secondary = {
                            placeholder(item, paddingValues = paddingValues)
                        },
                        onClick = { onClick(item) }) { }
                is PreferenceItem.Placeholder -> {
                    placeholder(item, paddingValues = paddingValues)
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
                    item.value = value
                    onValueChange(item)
                }
            },
            confirmButton = { },
            onDismissRequest = {
                onValueChange(item)
            }
    )
}


@Preview("InCarScreen Light", showSystemUi = true)
@Composable
fun InCarScreenLight() {
    MaterialTheme {
        BackgroundSurface {
            PreferencesScreen(listOf(
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
                    PreferenceItem.Switch(checked = false, title = "Wi-Fi Only"),
            ), onClick = {})
        }
    }
}

@Preview("InCarScreen Dark", showSystemUi = true)
@Composable
fun InCarScreenDark() {
    MaterialTheme(
            colorScheme = darkColorScheme()
    ) {
        BackgroundSurface {
            PreferencesScreen(listOf(
                    PreferenceItem.Category(title = "Category"),
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