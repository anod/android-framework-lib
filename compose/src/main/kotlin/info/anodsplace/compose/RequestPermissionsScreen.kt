package info.anodsplace.compose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import info.anodsplace.permissions.AppPermission
import info.anodsplace.permissions.AppPermissions
import info.anodsplace.permissions.toRequestInputs

@Immutable
data class PermissionDescription(
    val permission: AppPermission,
    @DrawableRes val iconRes: Int,
    val icon: Painter? = null,
    @StringRes val titleRes: Int,
    val title: String = "",
    @StringRes val descRes: Int,
    val desc: String = "",
)

@Immutable
data class RequestPermissionsScreenDescription(
    @StringRes val descRes: Int = 0,
    val desc: String = "",
    @StringRes val titleRes: Int = 0,
    val title: String = "",
    @StringRes val allowAccessRes: Int = 0,
    val allowAccess: String = "",
    @StringRes val cancelRes: Int = 0,
    val cancel: String = ""
) {
    constructor() : this(
        title = "The app is missing required permissions",
        desc = "",
        allowAccess = "Allow access",
        cancel = "Cancel"
    )
}

@Composable
fun RequestPermissionsScreen(
    input: List<PermissionDescription>,
    screenDescription: RequestPermissionsScreenDescription,
    onResult: (List<AppPermission>, e: Exception?) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentRequest by remember { mutableIntStateOf(-1) }
    val requests = remember(input) { input.map { it.permission }.toRequestInputs() }
    val result = remember { mutableStateListOf<AppPermission>() }
    var allowEnabled by remember {
        mutableStateOf(true)
    }
    val permissionRequest = rememberLauncherForActivityResult(contract = AppPermissions.Request()) { permissionResults ->
        permissionResults.forEach { (permissionValue, permissionResult) ->
            if (!permissionResult) {
                result.add(AppPermissions.fromValue(permissionValue))
            }
        }
        allowEnabled = true
        currentRequest += 1
    }

    val title = if (screenDescription.titleRes != 0) stringResource(id = screenDescription.titleRes) else screenDescription.title
    val desc = if (screenDescription.descRes != 0) stringResource(id = screenDescription.descRes) else screenDescription.desc

    Column(
        modifier = modifier
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(text = desc, style = MaterialTheme.typography.labelMedium)
        LazyColumn {
            items(input.size) { index ->
                val item = input[index]
                val itemTitle = if (item.titleRes != 0) stringResource(id = item.titleRes) else item.title
                val itemDesc = if (item.descRes != 0) stringResource(id = item.descRes) else item.desc
                val icon = if (item.iconRes != 0) painterResource(id = item.iconRes) else item.icon!!

                ListItem(
                    leadingContent = { Icon(painter = icon, contentDescription = null) },
                    headlineContent = { Text(text = itemTitle) },
                    supportingContent = { Text(text = itemDesc) },
                )
            }
        }

        Row {
            Button(
                enabled = allowEnabled,
                onClick = {
                    onResult(emptyList(), null)
                }
            ) {
                Text(text = if (screenDescription.cancelRes != 0) stringResource(id = screenDescription.cancelRes) else screenDescription.cancel)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                enabled = allowEnabled,
                onClick = {
                    currentRequest += 1
                    allowEnabled = false
                }
            ) {
                Text(text = if (screenDescription.allowAccessRes != 0) stringResource(id = screenDescription.allowAccessRes) else screenDescription.allowAccess)
            }
        }
    }

    LaunchedEffect(key1 = currentRequest) {
        if (currentRequest >= 0) {
            if (currentRequest < requests.size) {
                try {
                    permissionRequest.launch(requests[0])
                } catch (e: Exception) {
                    onResult(result, e)
                }
            } else {
                onResult(result, null)
            }
        }
    }
}

@Preview("RequestPermissionsScreen Dark", widthDp = 360, heightDp = 1020)
@Composable
fun RequestPermissionsScreenDark() {
    Surface {
        RequestPermissionsScreen(
            input = listOf(

            ),
            screenDescription = RequestPermissionsScreenDescription(),
            onResult = { _, _ -> }
        )
    }
}