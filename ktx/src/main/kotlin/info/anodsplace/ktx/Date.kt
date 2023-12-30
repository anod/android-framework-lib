package info.anodsplace.ktx

import java.util.concurrent.TimeUnit

fun dayStartAgoMillis(days: Long): Long {
    val timestamp = System.currentTimeMillis()
    // https://stackoverflow.com/questions/13892163/get-timestamp-for-start-of-day
    // val dayEnd = dayStart + 86399999
    val dayStart = timestamp - (timestamp % 86400000)
    return dayStart - TimeUnit.DAYS.toMillis(days)
}