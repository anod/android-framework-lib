package info.anodsplace.binaryclock

import android.content.Context
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
}

@Composable
internal fun BinaryClockWidgetContent(digits: List<Int>) {
    val size = LocalSize.current
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
                    compact = size.width < 180.dp,
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
