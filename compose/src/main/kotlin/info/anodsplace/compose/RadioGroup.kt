package info.anodsplace.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RadioGroup(
    radioOptions: Array<String>,
    selectedIndex: Int,
    onValueChanged: (index: Int) -> Unit
) {
    val (selectedOption, onOptionSelected) = remember { mutableIntStateOf(selectedIndex) }
    Column(Modifier.selectableGroup()) {
        radioOptions.forEachIndexed { index, text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (index == selectedOption),
                        onClick = {
                            onValueChanged(index)
                            onOptionSelected(index)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (index == selectedOption),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.merge(),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}