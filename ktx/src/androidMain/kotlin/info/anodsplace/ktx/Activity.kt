package info.anodsplace.ktx

import android.app.Activity
import android.os.Bundle

fun Activity.extras(): Bundle = intent?.extras ?: Bundle.EMPTY