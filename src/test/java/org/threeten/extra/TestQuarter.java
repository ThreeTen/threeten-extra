/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.extra;

import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.AMPM_OF_DAY;
import static java.time.temporal.ChronoField.CLOCK_HOUR_OF_AMPM;
import static java.time.temporal.ChronoField.CLOCK_HOUR_OF_DAY;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.HOUR_OF_AMPM;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.MICRO_OF_DAY;
import static java.time.temporal.ChronoField.MICRO_OF_SECOND;
import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.SECOND_OF_DAY;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.IsoFields.QUARTER_OF_YEAR;
import static java.time.temporal.IsoFields.QUARTER_YEARS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.threeten.extra.Quarter.Q3;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test Quarter.
 */
public class TestQuarter {

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(Quarter.class));
        assertTrue(Serializable.class.isAssignableFrom(Quarter.class));
        assertTrue(Comparable.class.isAssignableFrom(Quarter.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(Quarter.class));
    }

    //-----------------------------------------------------------------------
    // of(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int_singleton() {
        for (int i = 1; i <= 4; i++) {
            Quarter test = Quarter.of(i);
            assertEquals(i, test.getValue());
        }
    }

    @Test
    public void test_of_int_valueTooLow() {
        assertThrows(DateTimeException.class, () -> Quarter.of(0));
    }

    @Test
    public void test_of_int_valueTooHigh() {
        assertThrows(DateTimeException.class, () -> Quarter.of(5));
    }

    //-----------------------------------------------------------------------
    // ofMonth(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_ofMonth_int_singleton() {
        assertSame(Quarter.Q1, Quarter.ofMonth(1));
        assertSame(Quarter.Q1, Quarter.ofMonth(2));
        assertSame(Quarter.Q1, Quarter.ofMonth(3));
        assertSame(Quarter.Q2, Quarter.ofMonth(4));
        assertSame(Quarter.Q2, Quarter.ofMonth(5));
        assertSame(Quarter.Q2, Quarter.ofMonth(6));
        assertSame(Quarter.Q3, Quarter.ofMonth(7));
        assertSame(Quarter.Q3, Quarter.ofMonth(8));
        assertSame(Quarter.Q3, Quarter.ofMonth(9));
        assertSame(Quarter.Q4, Quarter.ofMonth(10));
        assertSame(Quarter.Q4, Quarter.ofMonth(11));
        assertSame(Quarter.Q4, Quarter.ofMonth(12));
    }

    @Test
    public void test_ofMonth_int_valueTooLow() {
        assertThrows(DateTimeException.class, () -> Quarter.ofMonth(0));
    }

    @Test
    public void test_ofMonth_int_valueTooHigh() {
        assertThrows(DateTimeException.class, () -> Quarter.ofMonth(13));
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor() {
        assertEquals(Quarter.Q2, Quarter.from(LocalDate.of(2011, 6, 6)));
        assertEquals(Quarter.Q1, Quarter.from(LocalDateTime.of(2012, 2, 3, 12, 30)));
    }

    @Test
    public void test_from_TemporalAccessor_Month() {
        assertEquals(Quarter.Q1, Quarter.from(Month.JANUARY));
        assertEquals(Quarter.Q1, Quarter.from(Month.FEBRUARY));
        assertEquals(Quarter.Q1, Quarter.from(Month.MARCH));
        assertEquals(Quarter.Q2, Quarter.from(Month.APRIL));
        assertEquals(Quarter.Q2, Quarter.from(Month.MAY));
        assertEquals(Quarter.Q2, Quarter.from(Month.JUNE));
        assertEquals(Quarter.Q3, Quarter.from(Month.JULY));
        assertEquals(Quarter.Q3, Quarter.from(Month.AUGUST));
        assertEquals(Quarter.Q3, Quarter.from(Month.SEPTEMBER));
        assertEquals(Quarter.Q4, Quarter.from(Month.OCTOBER));
        assertEquals(Quarter.Q4, Quarter.from(Month.NOVEMBER));
        assertEquals(Quarter.Q4, Quarter.from(Month.DECEMBER));
    }

    @Test
    public void test_from_TemporalAccessorl_invalid_noDerive() {
        assertThrows(DateTimeException.class, () -> Quarter.from(LocalTime.of(12, 30)));
    }

    @Test
    public void test_from_TemporalAccessor_null() {
        assertThrows(NullPointerException.class, () -> Quarter.from((TemporalAccessor) null));
    }

    @Test
    public void test_from_parse_CharSequence() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Q'Q");
        assertEquals(Q3, formatter.parse("Q3", Quarter::from));
    }

    //-----------------------------------------------------------------------
    // getDisplayName()
    //-----------------------------------------------------------------------
    @Test
    public void test_getDisplayName() {
        assertEquals("Q1", Quarter.Q1.getDisplayName(TextStyle.SHORT, Locale.US));
    }

    @Test
    public void test_getDisplayName_nullStyle() {
        assertThrows(NullPointerException.class, () -> Quarter.Q1.getDisplayName(null, Locale.US));
    }

    @Test
    public void test_getDisplayName_nullLocale() {
        assertThrows(NullPointerException.class, () -> Quarter.Q1.getDisplayName(TextStyle.FULL, null));
    }

    //-----------------------------------------------------------------------
    // isSupported()
    //-----------------------------------------------------------------------
    @Test
    public void test_isSupported() {
        Quarter test = Quarter.Q1;
        assertEquals(false, test.isSupported(null));
        assertEquals(false, test.isSupported(NANO_OF_SECOND));
        assertEquals(false, test.isSupported(NANO_OF_DAY));
        assertEquals(false, test.isSupported(MICRO_OF_SECOND));
        assertEquals(false, test.isSupported(MICRO_OF_DAY));
        assertEquals(false, test.isSupported(MILLI_OF_SECOND));
        assertEquals(false, test.isSupported(MILLI_OF_DAY));
        assertEquals(false, test.isSupported(SECOND_OF_MINUTE));
        assertEquals(false, test.isSupported(SECOND_OF_DAY));
        assertEquals(false, test.isSupported(MINUTE_OF_HOUR));
        assertEquals(false, test.isSupported(MINUTE_OF_DAY));
        assertEquals(false, test.isSupported(HOUR_OF_AMPM));
        assertEquals(false, test.isSupported(CLOCK_HOUR_OF_AMPM));
        assertEquals(false, test.isSupported(HOUR_OF_DAY));
        assertEquals(false, test.isSupported(CLOCK_HOUR_OF_DAY));
        assertEquals(false, test.isSupported(AMPM_OF_DAY));
        assertEquals(false, test.isSupported(DAY_OF_WEEK));
        assertEquals(false, test.isSupported(ALIGNED_DAY_OF_WEEK_IN_MONTH));
        assertEquals(false, test.isSupported(ALIGNED_DAY_OF_WEEK_IN_YEAR));
        assertEquals(false, test.isSupported(DAY_OF_MONTH));
        assertEquals(false, test.isSupported(DAY_OF_YEAR));
        assertEquals(false, test.isSupported(EPOCH_DAY));
        assertEquals(false, test.isSupported(ALIGNED_WEEK_OF_MONTH));
        assertEquals(false, test.isSupported(ALIGNED_WEEK_OF_YEAR));
        assertEquals(false, test.isSupported(MONTH_OF_YEAR));
        assertEquals(false, test.isSupported(PROLEPTIC_MONTH));
        assertEquals(false, test.isSupported(YEAR_OF_ERA));
        assertEquals(false, test.isSupported(YEAR));
        assertEquals(false, test.isSupported(ERA));
        assertEquals(false, test.isSupported(INSTANT_SECONDS));
        assertEquals(false, test.isSupported(OFFSET_SECONDS));
        assertEquals(true, test.isSupported(QUARTER_OF_YEAR));
    }

    //-----------------------------------------------------------------------
    // range()
    //-----------------------------------------------------------------------
    @Test
    public void test_range() {
        assertEquals(QUARTER_OF_YEAR.range(), Quarter.Q1.range(QUARTER_OF_YEAR));
    }

    @Test
    public void test_range_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Quarter.Q1.range(MONTH_OF_YEAR));
    }

    @Test
    public void test_range_null() {
        assertThrows(NullPointerException.class, () -> Quarter.Q1.range(null));
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(1, Quarter.Q1.get(QUARTER_OF_YEAR));
        assertEquals(2, Quarter.Q2.get(QUARTER_OF_YEAR));
        assertEquals(3, Quarter.Q3.get(QUARTER_OF_YEAR));
        assertEquals(4, Quarter.Q4.get(QUARTER_OF_YEAR));
    }

    @Test
    public void test_get_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Quarter.Q2.get(MONTH_OF_YEAR));
    }

    @Test
    public void test_get_null() {
        assertThrows(NullPointerException.class, () -> Quarter.Q2.get(null));
    }

    //-----------------------------------------------------------------------
    // getLong()
    //-----------------------------------------------------------------------
    @Test
    public void test_getLong() {
        assertEquals(1, Quarter.Q1.getLong(QUARTER_OF_YEAR));
        assertEquals(2, Quarter.Q2.getLong(QUARTER_OF_YEAR));
        assertEquals(3, Quarter.Q3.getLong(QUARTER_OF_YEAR));
        assertEquals(4, Quarter.Q4.getLong(QUARTER_OF_YEAR));
    }

    @Test
    public void test_getLong_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Quarter.Q2.getLong(MONTH_OF_YEAR));
    }

    @Test
    public void test_getLong_null() {
        assertThrows(NullPointerException.class, () -> Quarter.Q2.getLong(null));
    }

    //-----------------------------------------------------------------------
    // plus(long), plus(long,unit)
    //-----------------------------------------------------------------------
    public static Object[][] data_plus() {
        return new Object[][] {
            {1, -5, 4},
            {1, -4, 1},
            {1, -3, 2},
            {1, -2, 3},
            {1, -1, 4},
            {1, 0, 1},
            {1, 1, 2},
            {1, 2, 3},
            {1, 3, 4},
            {1, 4, 1},
            {1, 5, 2},
        };
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_plus_long(int base, long amount, int expected) {
        assertEquals(Quarter.of(expected), Quarter.of(base).plus(amount));
    }

    //-----------------------------------------------------------------------
    // minus(long), minus(long,unit)
    //-----------------------------------------------------------------------
    public static Object[][] data_minus() {
        return new Object[][] {
            {1, -5, 2},
            {1, -4, 1},
            {1, -3, 4},
            {1, -2, 3},
            {1, -1, 2},
            {1, 0, 1},
            {1, 1, 4},
            {1, 2, 3},
            {1, 3, 2},
            {1, 4, 1},
            {1, 5, 4},
        };
    }

    @ParameterizedTest
    @MethodSource("data_minus")
    public void test_minus_long(int base, long amount, int expected) {
        assertEquals(Quarter.of(expected), Quarter.of(base).minus(amount));
    }

    //-----------------------------------------------------------------------
    // length(boolean)
    //-----------------------------------------------------------------------
    @Test
    public void test_length_boolean() {
        assertEquals(91, Quarter.Q1.length(true));
        assertEquals(90, Quarter.Q1.length(false));
        assertEquals(91, Quarter.Q2.length(true));
        assertEquals(91, Quarter.Q2.length(false));
        assertEquals(92, Quarter.Q3.length(true));
        assertEquals(92, Quarter.Q3.length(false));
        assertEquals(92, Quarter.Q4.length(true));
        assertEquals(92, Quarter.Q4.length(false));
    }

    //-----------------------------------------------------------------------
    // firstMonth()
    //-----------------------------------------------------------------------
    @Test
    public void test_firstMonth() {
        assertEquals(Month.JANUARY, Quarter.Q1.firstMonth());
        assertEquals(Month.APRIL, Quarter.Q2.firstMonth());
        assertEquals(Month.JULY, Quarter.Q3.firstMonth());
        assertEquals(Month.OCTOBER, Quarter.Q4.firstMonth());
    }

    //-----------------------------------------------------------------------
    // query()
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertEquals(IsoChronology.INSTANCE, Quarter.Q1.query(TemporalQueries.chronology()));
        assertEquals(null, Quarter.Q1.query(TemporalQueries.localDate()));
        assertEquals(null, Quarter.Q1.query(TemporalQueries.localTime()));
        assertEquals(null, Quarter.Q1.query(TemporalQueries.offset()));
        assertEquals(QUARTER_YEARS, Quarter.Q1.query(TemporalQueries.precision()));
        assertEquals(null, Quarter.Q1.query(TemporalQueries.zone()));
        assertEquals(null, Quarter.Q1.query(TemporalQueries.zoneId()));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals("Q1", Quarter.Q1.toString());
        assertEquals("Q2", Quarter.Q2.toString());
        assertEquals("Q3", Quarter.Q3.toString());
        assertEquals("Q4", Quarter.Q4.toString());
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test
    public void test_enum() {
        assertEquals(Quarter.Q4, Quarter.valueOf("Q4"));
        assertEquals(Quarter.Q1, Quarter.values()[0]);
    }

}
