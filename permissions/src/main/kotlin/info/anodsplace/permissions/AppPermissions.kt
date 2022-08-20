package info.anodsplace.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * @author algavris
 * @date 08/05/2016.
 */

sealed class AppPermission(val value: String) {
    override fun equals(other: Any?): Boolean = (other as? AppPermission)?.value == value
    override fun hashCode(): Int = value.hashCode()

    object CallPhone : AppPermission(Manifest.permission.CALL_PHONE)
    object ReadContacts : AppPermission(Manifest.permission.READ_CONTACTS)
    object AnswerPhoneCalls : AppPermission(Manifest.permission.ANSWER_PHONE_CALLS)
    object ModifyPhoneState : AppPermission(Manifest.permission.MODIFY_PHONE_STATE)
    object CanDrawOverlay : AppPermission(AppPermissions.Permission.CAN_DRAW_OVERLAY)
    object WriteSettings : AppPermission(AppPermissions.Permission.WRITE_SETTINGS)

    @RequiresApi(33)
    object PostNotification : AppPermission(Manifest.permission.POST_NOTIFICATIONS)

    @RequiresApi(Build.VERSION_CODES.Q)
    object ActivityRecognition : AppPermission(Manifest.permission.ACTIVITY_RECOGNITION)

    @RequiresApi(31)
    object BluetoothConnect : AppPermission(AppPermissions.Permission.BLUETOOTH_CONNECT)

    @RequiresApi(31)
    object BluetoothScan : AppPermission(AppPermissions.Permission.BLUETOOTH_SCAN)
}

fun List<AppPermission>.filterRequired(activity: ComponentActivity, needPermission: (AppPermission) -> Boolean): List<AppPermission> {
    val permissions = mutableListOf<AppPermission>()
    for (permission in this) {
        if (needPermission(permission) && AppPermissions.shouldShowMessage(activity, permission)) {
            permissions.add(permission)
        }
    }
    return permissions
}

fun AppPermission.toRequestInput(): AppPermissions.Request.Input {
    return listOf(this).toRequestInputs().first()
}

fun List<AppPermission>.toRequestInputs(): List<AppPermissions.Request.Input> {
    val manifestPermissions = mutableListOf<String>()
    var canDrawOverlay = false
    var writeSettings = false
    this.forEach {
        when (it) {
            AppPermission.CanDrawOverlay -> canDrawOverlay = true
            AppPermission.WriteSettings -> writeSettings = true
            AppPermission.AnswerPhoneCalls -> {
                manifestPermissions.add(it.value)
            }
            AppPermission.ActivityRecognition -> {
                manifestPermissions.add(it.value)
            }
            else -> manifestPermissions.add(it.value)
        }
    }

    return mutableListOf<AppPermissions.Request.Input>().apply {
        if (canDrawOverlay) {
            add(AppPermissions.Request.Input.CanDrawOverlay)
        }
        if (writeSettings) {
            add(AppPermissions.Request.Input.WriteSettings)
        }
        if (manifestPermissions.isNotEmpty()) {
            add(AppPermissions.Request.Input.Permissions(manifestPermissions.toTypedArray()))
        }
    }
}

object AppPermissions {
    interface Permission {
        companion object {
            const val CAN_DRAW_OVERLAY = "CAN_DRAW_OVERLAY"
            const val WRITE_SETTINGS = "WRITE_SETTINGS"

            const val BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT"
            const val BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN"
        }
    }

    class Request : ActivityResultContract<Request.Input, Map<String, Boolean>>() {
        private var input: Input? = null
        private val request = ActivityResultContracts.RequestMultiplePermissions()
        private val startActivity = ActivityResultContracts.StartActivityForResult()

        sealed interface Input {
            object CanDrawOverlay : Input
            object WriteSettings : Input
            class Permissions(val value: Array<String>) : Input
        }

        override fun createIntent(context: Context, input: Input): Intent {
            this.input = input
            return when (input) {
                is Input.CanDrawOverlay -> startActivity.createIntent(
                        context,
                        Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context.packageName)
                        )
                )
                is Input.WriteSettings -> startActivity.createIntent(
                        context,
                        Intent(
                                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                Uri.parse("package:" + context.packageName)
                        )
                )
                is Input.Permissions -> request.createIntent(context, input.value)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Map<String, Boolean> {
            return when (input) {
                is Input.CanDrawOverlay -> {
                    val granted = startActivity.parseResult(resultCode, intent).resultCode == Activity.RESULT_OK
                    return mapOf(Permission.CAN_DRAW_OVERLAY to granted)
                }
                is Input.WriteSettings -> {
                    val granted = startActivity.parseResult(resultCode, intent).resultCode == Activity.RESULT_OK
                    return mapOf(Permission.WRITE_SETTINGS to granted)
                }
                else -> request.parseResult(resultCode, intent)
            }
        }
    }

    fun fromValue(value: String): AppPermission {
        return when (value) {
            AppPermission.CallPhone.value -> AppPermission.CallPhone
            AppPermission.ReadContacts.value -> AppPermission.ReadContacts
            AppPermission.AnswerPhoneCalls.value -> AppPermission.AnswerPhoneCalls
            AppPermission.ModifyPhoneState.value -> AppPermission.ModifyPhoneState
            AppPermission.CanDrawOverlay.value -> AppPermission.CanDrawOverlay
            AppPermission.WriteSettings.value -> AppPermission.WriteSettings
            AppPermission.ActivityRecognition.value -> AppPermission.ActivityRecognition
            AppPermission.BluetoothConnect.value -> AppPermission.BluetoothConnect
            AppPermission.BluetoothScan.value -> AppPermission.BluetoothScan
            else -> throw IllegalArgumentException("Unknown $value")
        }
    }

    fun isGranted(context: Context, permission: AppPermission): Boolean {
        if (permission == AppPermission.CanDrawOverlay) {
            return Settings.canDrawOverlays(context)
        }
        if (permission == AppPermission.WriteSettings) {
            return Settings.System.canWrite(context)
        }
        if (permission == AppPermission.AnswerPhoneCalls) {
            return ContextCompat.checkSelfPermission(context, permission.value) == PackageManager.PERMISSION_GRANTED
        }
        return ContextCompat.checkSelfPermission(context, permission.value) == PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowMessage(activity: ComponentActivity, permission: AppPermission): Boolean {
        if (isGranted(activity, permission)) {
            return false
        }
        if (permission == AppPermission.CanDrawOverlay) {
            return true
        }
        if (permission == AppPermission.WriteSettings) {
            return true
        }
        if (permission == AppPermission.AnswerPhoneCalls) {
            return true
        }
        if (permission == AppPermission.ActivityRecognition) {
            return true
        }
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.value)
    }
}