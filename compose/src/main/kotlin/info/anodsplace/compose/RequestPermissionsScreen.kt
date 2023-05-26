package info.anodsplace.compose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import info.anodsplace.permissions.AppPermission
import info.anodsplace.permissions.AppPermissions
import info.anodsplace.permissions.toRequestInputs

@Immutable
data class PermissionDescription(
    val permission: AppPermission,
    @DrawableRes val iconsRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int
)

@Immutable
data class RequestPermissionsScreenDescription(
    @StringRes val descRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val allowAccessRes: Int,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestPermissionsScreen(input: List<PermissionDescription>, screenDescription: RequestPermissionsScreenDescription, onResult: (List<AppPermission>) -> Unit) {
    val currentRequest = remember { mutableStateOf(-1) }
    val requests = input.map { it.permission }.toRequestInputs()
    val result = remember {
        mutableStateListOf<AppPermission>()
    }
    val permissionRequest = rememberLauncherForActivityResult(contract = AppPermissions.Request()) { permissionResults ->
        permissionResults.forEach { (permissionValue, permissionResult) ->
            if (!permissionResult) {
                result.add(AppPermissions.fromValue(permissionValue))
            }
        }
        currentRequest.value = currentRequest.value + 1
    }

    Column {
        Text(text = stringResource(id = screenDescription.descRes), style = MaterialTheme.typography.labelSmall)
        Text(text = stringResource(id = screenDescription.titleRes), style = MaterialTheme.typography.labelSmall)
        LazyColumn {
            items(input.size) { index ->
                val desc = input[index]
                ListItem(
                    leadingContent = { Icon(painter = painterResource(id = desc.iconsRes), contentDescription = null) },
                    headlineContent = { Text(text = stringResource(id = desc.titleRes)) },
                    supportingContent = { Text(text = stringResource(id = desc.descRes)) },
                )
            }
        }
        Button(onClick = {
            currentRequest.value = currentRequest.value + 1
        }) {
            Text(text = stringResource(id = screenDescription.allowAccessRes))
        }
    }

    if (currentRequest.value > 0 && currentRequest.value < requests.size) {
        LaunchedEffect(key1 = currentRequest) {
            permissionRequest.launch(requests[currentRequest.value])
        }
    } else if (currentRequest.value >= requests.size) {
        LaunchedEffect(key1 = currentRequest) {
            onResult(result)
        }
    }
}