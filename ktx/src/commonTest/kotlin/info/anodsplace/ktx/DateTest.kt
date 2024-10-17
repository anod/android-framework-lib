package info.anodsplace.ktx

import kotlin.test.Test
import kotlin.test.assertEquals

class DateTest {

    @Test
    fun testGetStartOfDay() {
        // Test timestamp for October 5, 2023, 12:34:56.789 (GMT)
        val timestamp = 1696535696789L
        // Expected start of day for October 5, 2023 (GMT)
        val expectedStartOfDay = 1696464000000L
        assertEquals(expectedStartOfDay, getStartOfDay(timestamp))
    }

    @Test
    fun testDayStartAgoMillis() {
        // Current time in milliseconds (for testing, we'll use a fixed time)
        val now = 1696535696789L
        // Assume now is October 5, 2023, 12:34:56.789 (GMT)
        val expectedDaysAgo1 = 1696377600000L // Start of October 4, 2023 (1 day ago)
        val expectedDaysAgo2 = 1696291200000L // Start of October 3, 2023 (2 days ago)

        assertEquals(expectedDaysAgo1, dayStartAgoMillis(1, now))
        assertEquals(expectedDaysAgo2, dayStartAgoMillis(2, now))
    }
}