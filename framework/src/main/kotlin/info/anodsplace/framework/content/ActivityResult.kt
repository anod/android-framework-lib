package info.anodsplace.framework.content

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContracts
import info.anodsplace.framework.AppLog

fun CreateDocument(initialUri: Uri, dataType: String) = object: ActivityResultContracts.CreateDocument() {

    @SuppressLint("MissingSuperCall")
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            putExtra(Intent.EXTRA_TITLE, input)
            try {
                setDataAndType(initialUri, dataType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri)
                }
            } catch (e: Exception) {
                AppLog.e(e)
            }
        }
    }
}