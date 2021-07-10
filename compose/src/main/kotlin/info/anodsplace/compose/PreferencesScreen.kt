package info.anodsplace.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreferenceSlider(
    initialValue: Int,
    onValueChanged: (Int) -> Unit,
    item: PreferenceItem,
    suffixText: @Composable () -> Unit = {},
    startIcon: @Composable (modifier: Modifier) -> Unit = { Box(modifier = it) {} },
    endIcon: @Composable (modifier: Modifier) -> Unit = { Box(modifier = it) {} }
) {
    var value: Float by remember { mutableStateOf(initialValue.toFloat()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
            verticalAlignment = Alignment.CenterVertically) {
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
    color: Color = MaterialTheme.colors.secondary,
) {
    Text(
        text = if (item.titleRes != 0) stringResource(id = item.titleRes) else item.title,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        style = MaterialTheme.typography.overline.copy(
            color,
            fontSize = 14.sp,
        )
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Preference(item: PreferenceItem, paddingValues: PaddingValues = PaddingValues(4.dp), onClick: () -> Unit, content: @Composable (() -> Unit)? = null) {
    ListItem(
        modifier = Modifier
            .defaultMinSize(minHeight = 48.dp)
            .fillMaxWidth()
            .alpha(if (item.enabled) 1.0f else 0.6f)
            .clickable(onClick = onClick, enabled = item.enabled)
            .padding(paddingValues),
        icon = null,
        text = {
            Text(
                text = if (item.titleRes != 0) stringResource(id = item.titleRes) else item.title,
                style = MaterialTheme.typography.h5.copy(
                    fontSize = 20.sp,
                )
            )
        },
        secondaryText = if (item.summaryRes != 0 || item.summary.isNotEmpty()) {
            {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = if (item.summaryRes != 0) stringResource(id = item.summaryRes) else item.summary,
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.onSurface
                    )
                )
            }
        } else null,
        trailing = content
    )
}

@Composable
fun PreferenceSwitch(checked: Boolean, item: PreferenceItem, paddingValues: PaddingValues = PaddingValues(4.dp), onCheckedChange: (Boolean) -> Unit) {
    Preference(item, paddingValues, onClick = {
        onCheckedChange(!checked)
    }) {
        Switch(
            enabled = item.enabled,
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors()
        )
    }
}

@Composable
fun PreferenceCheckbox(checked: Boolean, item: PreferenceItem, paddingValues: PaddingValues = PaddingValues(4.dp), onCheckedChange: (Boolean) -> Unit) {
    Preference(item, paddingValues, onClick = {
        onCheckedChange(!checked)
    }) {
        Checkbox(
            enabled = item.enabled,
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                uncheckedColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                disabledColor = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.disabled),
            )
        )
    }
}

@Composable
fun PreferencesScreen(
    preferences: List<PreferenceItem>,
    modifier: Modifier = Modifier,
    onClick: (item: PreferenceItem) -> Unit = { },
    categoryColor: Color = MaterialTheme.colors.secondary,
    placeholder: @Composable (PreferenceItem.Placeholder) -> Unit = { },
) {
    var listItem by remember { mutableStateOf<PreferenceItem.List?>(null) }
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(preferences.size) { index ->
            val paddingValues = PaddingValues(16.dp)
            when (val item = preferences[index]) {
                is PreferenceItem.Category -> PreferenceCategory(item = item, color = categoryColor)
                is PreferenceItem.CheckBox -> {
                    var checked by remember { mutableStateOf(item.checked) }
                    PreferenceCheckbox(
                        paddingValues = paddingValues,
                        checked = checked,
                        item = item,
                        onCheckedChange = { newChecked ->
                            checked = newChecked
                            item.checked = newChecked
                            onClick(item)
                        })
                }
                is PreferenceItem.List -> Preference(
                    paddingValues = paddingValues,
                    item = item,
                    onClick = { listItem = item }
                ) { }
                is PreferenceItem.Switch -> {
                    var checked by remember { mutableStateOf(item.checked) }
                    PreferenceSwitch(
                        paddingValues = paddingValues,
                        checked = checked,
                        item = item,
                        onCheckedChange = { newChecked ->
                        item.checked = newChecked
                        checked = newChecked
                        onClick(item)
                    })
                }
                is PreferenceItem.Text -> Preference(
                    paddingValues = paddingValues,
                    item = item,
                    onClick = { onClick(item) }) { }
                is PreferenceItem.Placeholder -> {
                    placeholder(item)
                }
            }
        }
    }

    if (listItem != null) {
        PreferenceListDialog(item = listItem!!) { value ->
            onClick(listItem!!)
            listItem = null
        }
    }
}

@Composable
fun PreferenceListDialog(item: PreferenceItem.List, onValueChange: (value: String) -> Unit) {
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
                onValueChange(value)
            }
        },
        buttons = { },
        onDismissRequest = {
            onValueChange(value)
        }
    )
}


@Preview("InCarScreen Light")
@Composable
fun InCarScreenLight() {
    MaterialTheme {
        BackgroundSurface {
            PreferencesScreen(listOf(
                PreferenceItem.Category(title ="Category"),
                PreferenceItem.Text(title ="Bluetooth device", summary = "Choose bluetooth device which enable InCar mode"),
                PreferenceItem.CheckBox(checked = true, title ="Keep screen On", summary = "When checked, prevents screen from automatically turning off"),
                PreferenceItem.Switch(checked = true, title ="Route to speaker", summary = "Route all incoming calls to phones speaker"),
            ), onClick = {})
        }
    }
}

@Preview("InCarScreen Dark")
@Composable
fun InCarScreenDark() {
    MaterialTheme {
        BackgroundSurface {
            PreferencesScreen(listOf(
                PreferenceItem.Category(title ="Category"),
                PreferenceItem.Text(title ="Bluetooth device", summary = "Choose bluetooth device which enable InCar mode"),
                PreferenceItem.CheckBox(checked = true, title ="Keep screen On", summary = "When checked, prevents screen from automatically turning off"),
                PreferenceItem.Switch(checked = true, title ="Route to speaker", summary = "Route all incoming calls to phones speaker"),
            ), onClick = {})
        }
    }
}

