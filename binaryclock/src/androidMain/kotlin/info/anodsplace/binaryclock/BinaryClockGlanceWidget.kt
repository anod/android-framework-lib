package info.anodsplace.binaryclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BinaryClockGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val now = LocalTime.now()
            BinaryClockWidgetContent(
                digits = BinaryClockDigits.timeDigits(
                    hour = now.hour,
                    minute = now.minute,
                    second = now.second,
                ),
            )
        }
    }
}

class BinaryClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BinaryClockGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        BinaryClockRefreshScheduler.scheduleNext(context)
    }

    override fun onDisabled(context: Context) {
        BinaryClockRefreshScheduler.cancel(context)
        super.onDisabled(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != ACTION_REFRESH_BINARY_CLOCK) {
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val applicationContext = context.applicationContext
                glanceAppWidget.updateAll(applicationContext)
                BinaryClockRefreshScheduler.scheduleNext(applicationContext)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

@Composable
internal fun BinaryClockWidgetContent(digits: List<Int>) {
    val isCompactMode = LocalSize.current.width <= MinimumWidgetWidth
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF101010)))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            digits.forEachIndexed { index, digit ->
                BinaryDigitColumn(
                    digit = digit,
                    label = labels[index],
                    compact = isCompactMode,
                )
                if (index < digits.lastIndex) {
                    Spacer(modifier = GlanceModifier.width(if (index == 1 || index == 3) 12.dp else 6.dp))
                }
            }
        }
    }
}

@Composable
private fun BinaryDigitColumn(digit: Int, label: String, compact: Boolean) {
    val dotSize = if (compact) 16.dp else 20.dp
    val dotFontSize = if (compact) 14.sp else 18.sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BinaryClockDigits.digitBits(digit).forEach { active ->
            Text(
                text = if (active) "●" else "○",
                modifier = GlanceModifier.width(dotSize).height(dotSize),
                style = TextStyle(
                    color = ColorProvider(if (active) Color.White else Color(0xFF555555)),
                    fontSize = dotFontSize,
                ),
            )
        }
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = label,
            style = TextStyle(
                color = ColorProvider(Color(0xFF888888)),
                fontSize = 10.sp,
            ),
        )
    }
}

private val labels = listOf("H", "H", "M", "M", "S", "S")
private val MinimumWidgetWidth = 180.dp

private const val ACTION_REFRESH_BINARY_CLOCK = "info.anodsplace.binaryclock.action.REFRESH"
private const val MINUTE_MILLIS = 60_000L

private object BinaryClockRefreshScheduler {
    fun scheduleNext(context: Context) {
        val nextMinute = System.currentTimeMillis().let { now ->
            now - (now % MINUTE_MILLIS) + MINUTE_MILLIS
        }
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val refreshIntent = pendingIntent(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, nextMinute, refreshIntent)
        } else {
            alarmManager.setWindow(AlarmManager.RTC, nextMinute, MINUTE_MILLIS, refreshIntent)
        }
    }

    fun cancel(context: Context) {
        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent(context))
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, BinaryClockWidgetReceiver::class.java)
            .setAction(ACTION_REFRESH_BINARY_CLOCK)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
