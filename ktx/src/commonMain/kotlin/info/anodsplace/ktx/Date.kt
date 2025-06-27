package info.anodsplace.ktx

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

const val MILLIS_IN_A_DAY = 86400000L

fun getStartOfDay(timestamp: Long): Long {
    return timestamp - (timestamp % MILLIS_IN_A_DAY)
}

@OptIn(ExperimentalTime::class)
fun dayStartAgoMillis(daysAgo: Long, now: Long = Clock.System.now().toEpochMilliseconds()): Long {
    val startOfToday = getStartOfDay(now)
    return startOfToday - (daysAgo * MILLIS_IN_A_DAY)
}