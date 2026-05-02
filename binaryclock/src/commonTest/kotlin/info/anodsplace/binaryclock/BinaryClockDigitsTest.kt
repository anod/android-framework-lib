package info.anodsplace.binaryclock

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BinaryClockDigitsTest {

    @Test
    fun digitBitsMapDecimalDigitToFourBits() {
        assertEquals(listOf(false, false, false, false), BinaryClockDigits.digitBits(0))
        assertEquals(listOf(false, false, false, true), BinaryClockDigits.digitBits(1))
        assertEquals(listOf(false, false, true, false), BinaryClockDigits.digitBits(2))
        assertEquals(listOf(false, true, false, true), BinaryClockDigits.digitBits(5))
        assertEquals(listOf(true, false, false, true), BinaryClockDigits.digitBits(9))
    }

    @Test
    fun timeDigitsAlwaysReturnSixDigits() {
        assertEquals(listOf(0, 0, 0, 0, 0, 0), BinaryClockDigits.timeDigits(0, 0, 0))
        assertEquals(listOf(1, 2, 0, 0, 0, 0), BinaryClockDigits.timeDigits(12, 0, 0))
        assertEquals(listOf(0, 7, 0, 5, 0, 9), BinaryClockDigits.timeDigits(7, 5, 9))
        assertEquals(listOf(2, 3, 5, 9, 5, 9), BinaryClockDigits.timeDigits(23, 59, 59))
    }

    @Test
    fun invalidValuesFailFast() {
        assertFailsWith<IllegalArgumentException> { BinaryClockDigits.digitBits(10) }
        assertFailsWith<IllegalArgumentException> { BinaryClockDigits.timeDigits(24, 0, 0) }
        assertFailsWith<IllegalArgumentException> { BinaryClockDigits.timeDigits(0, 60, 0) }
        assertFailsWith<IllegalArgumentException> { BinaryClockDigits.timeDigits(0, 0, 60) }
    }
}
