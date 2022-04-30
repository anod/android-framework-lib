package info.anodsplace.framework.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author algavris
 * @date 08/05/2016.
 */

sealed class PermissionResult
object Granted : PermissionResult()
object Denied : PermissionResult()

sealed class AppPermission(val value: String)
object CallPhone : AppPermission(Manifest.permission.CALL_PHONE)
object ReadContacts : AppPermission(Manifest.permission.READ_CONTACTS)

object AnswerPhoneCalls : AppPermission(Manifest.permission.ANSWER_PHONE_CALLS)
object ModifyPhoneState : AppPermission(Manifest.permission.MODIFY_PHONE_STATE)
object CanDrawOverlay : AppPermission(AppPermissions.Permission.CAN_DRAW_OVERLAY)
object WriteSettings : AppPermission(AppPermissions.Permission.WRITE_SETTINGS)

@RequiresApi(Build.VERSION_CODES.Q)
object ActivityRecognition : AppPermission(Manifest.permission.ACTIVITY_RECOGNITION)

@RequiresApi(31)
object BluetoothConnect : AppPermission(AppPermissions.Permission.BLUETOOTH_CONNECT)
@RequiresApi(31)
object BluetoothScan : AppPermission(AppPermissions.Permission.BLUETOOTH_SCAN)

class RequestPermission(private val permission: AppPermission): ActivityResultContract<Void, Boolean>() {
    private val request = ActivityResultContracts.RequestPermission()

    override fun createIntent(context: Context, input: Void): Intent {
        return request.createIntent(context, permission.value)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return request.parseResult(resultCode, intent)
    }
}

typealias MultiplePermissionsResult = Pair<Boolean, Map<String, Boolean>>

class RequestMultiplePermissions(private val permissions: List<AppPermission>): ActivityResultContract<Unit, MultiplePermissionsResult>() {
    private val request = ActivityResultContracts.RequestMultiplePermissions()

    override fun createIntent(context: Context, input: Unit): Intent {
        return request.createIntent(context, permissions.map { it.value }.toTypedArray())
    }

    override fun parseResult(resultCode: Int, intent: Intent?): MultiplePermissionsResult {
        val result = request.parseResult(resultCode, intent)
        val failed = result.values.firstOrNull { !it } != false
        return Pair(failed, result)
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

    fun isGranted(context: Context, permission: AppPermission): Boolean {
        if (permission == CanDrawOverlay) {
            return Settings.canDrawOverlays(context)
        }
        if (permission == WriteSettings) {
            return Settings.System.canWrite(context)
        }
        if (permission == AnswerPhoneCalls) {
            return ContextCompat.checkSelfPermission(context, permission.value) == PackageManager.PERMISSION_GRANTED
        }
        return ContextCompat.checkSelfPermission(context, permission.value) == PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowMessage(activity: FragmentActivity, permission: AppPermission): Boolean {
        if (isGranted(activity, permission)) {
            return false
        }
        if (permission == CanDrawOverlay) {
            return true
        }
        if (permission == WriteSettings) {
            return true
        }
        if (permission == AnswerPhoneCalls) {
            return true
        }

        if (permission == ActivityRecognition) {
            return true
        }
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.value)
    }

    fun register(fragment: Fragment, permission: AppPermission, callback: ActivityResultCallback<Boolean>): ActivityResultLauncher<Void> {
        return fragment.registerForActivityResult(RequestPermission(permission), callback)
    }

    fun register(fragment: Fragment, permissions: List<AppPermission>, callback: ActivityResultCallback<MultiplePermissionsResult>): ActivityResultLauncher<Unit> {
        return fragment.registerForActivityResult(RequestMultiplePermissions(permissions), callback)
    }

    fun register(activity: FragmentActivity, permission: AppPermission, callback: ActivityResultCallback<Boolean>): ActivityResultLauncher<Void> {
        return activity.registerForActivityResult(RequestPermission(permission), callback)
    }

    fun register(activity: FragmentActivity, permissions: List<AppPermission>, callback: ActivityResultCallback<MultiplePermissionsResult>): ActivityResultLauncher<Unit> {
        return activity.registerForActivityResult(RequestMultiplePermissions(permissions), callback)
    }

    fun requestDrawOverlay(fragment: Fragment, requestCode: Int) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + fragment.requireContext().packageName))
        fragment.startActivityForResult(intent, requestCode)
    }

    fun requestDrawOverlay(activity: FragmentActivity, requestCode: Int) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.packageName))
        activity.startActivityForResult(intent, requestCode)
    }

    fun requestWriteSettings(fragment: Fragment, requestCode: Int) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + fragment.requireContext().packageName))
        fragment.startActivityForResult(intent, requestCode)
    }

    fun requestWriteSettings(activity: FragmentActivity, requestCode: Int) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + activity.packageName))
        activity.startActivityForResult(intent, requestCode)
    }

    fun requestAnswerPhoneCalls(fragment: Fragment, requestCode: Int) {
        request(fragment, AnswerPhoneCalls, requestCode)
    }

    fun requestAnswerPhoneCalls(activity: FragmentActivity, requestCode: Int) {
        request(activity, AnswerPhoneCalls, requestCode)
    }

    private fun request(activity: FragmentActivity, permission: AppPermission, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission.value), requestCode)
    }

    private fun request(fragment: Fragment, permission: AppPermission, requestCode: Int) {
        fragment.requestPermissions(arrayOf(permission.value), requestCode)
    }

    fun checkResult(requestCode: Int, grantResults: IntArray, checkPermission: Int, result: (result: PermissionResult) -> Unit) {
        if (requestCode == checkPermission) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                result(Granted)
            } else {
                result(Denied)
            }
        }
    }
}