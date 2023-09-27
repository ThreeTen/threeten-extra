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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.threeten.extra.TemporalFields.HALF_OF_YEAR;
import static org.threeten.extra.TemporalFields.HALF_YEARS;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test Half.
 */
public class TestHalf {

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(Half.class));
        assertTrue(Serializable.class.isAssignableFrom(Half.class));
        assertTrue(Comparable.class.isAssignableFrom(Half.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(Half.class));
    }

    //-----------------------------------------------------------------------
    // of(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int() {
        assertEquals(1, Half.of(1).getValue());
        assertEquals(2, Half.of(2).getValue());
        assertThrows(DateTimeException.class, () -> Half.of(0));
        assertThrows(DateTimeException.class, () -> Half.of(3));
    }

    //-----------------------------------------------------------------------
    // ofMonth(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_ofMonth_int_singleton() {
        assertSame(Half.H1, Half.ofMonth(1));
        assertSame(Half.H1, Half.ofMonth(2));
        assertSame(Half.H1, Half.ofMonth(3));
        assertSame(Half.H1, Half.ofMonth(4));
        assertSame(Half.H1, Half.ofMonth(5));
        assertSame(Half.H1, Half.ofMonth(6));
        assertSame(Half.H2, Half.ofMonth(7));
        assertSame(Half.H2, Half.ofMonth(8));
        assertSame(Half.H2, Half.ofMonth(9));
        assertSame(Half.H2, Half.ofMonth(10));
        assertSame(Half.H2, Half.ofMonth(11));
        assertSame(Half.H2, Half.ofMonth(12));
    }

    @Test
    public void test_ofMonth_int_valueTooLow() {
        assertThrows(DateTimeException.class, () -> Half.ofMonth(0));
    }

    @Test
    public void test_ofMonth_int_valueTooHigh() {
        assertThrows(DateTimeException.class, () -> Half.ofMonth(13));
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor() {
        assertEquals(Half.H2, Half.from(LocalDate.of(2011, 7, 6)));
        assertEquals(Half.H1, Half.from(LocalDateTime.of(2012, 2, 3, 12, 30)));
    }

    @Test
    public void test_from_TemporalAccessor_Month() {
        assertEquals(Half.H1, Half.from(Month.JANUARY));
        assertEquals(Half.H1, Half.from(Month.FEBRUARY));
        assertEquals(Half.H1, Half.from(Month.MARCH));
        assertEquals(Half.H1, Half.from(Month.APRIL));
        assertEquals(Half.H1, Half.from(Month.MAY));
        assertEquals(Half.H1, Half.from(Month.JUNE));
        assertEquals(Half.H2, Half.from(Month.JULY));
        assertEquals(Half.H2, Half.from(Month.AUGUST));
        assertEquals(Half.H2, Half.from(Month.SEPTEMBER));
        assertEquals(Half.H2, Half.from(Month.OCTOBER));
        assertEquals(Half.H2, Half.from(Month.NOVEMBER));
        assertEquals(Half.H2, Half.from(Month.DECEMBER));

        assertEquals(Half.H1, Half.from(Half.H1));
        assertEquals(Half.H2, Half.from(Half.H2));
    }

    @Test
    public void test_from_TemporalAccessorl_invalid_noDerive() {
        assertThrows(DateTimeException.class, () -> Half.from(LocalTime.of(12, 30)));
    }

    @Test
    public void test_from_TemporalAccessor_null() {
        assertThrows(NullPointerException.class, () -> Half.from((TemporalAccessor) null));
    }

    @Test
    public void test_from_parse_CharSequence() {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendLiteral('H')
                .appendValue(HALF_OF_YEAR, 1)
                .toFormatter();
        assertEquals(Half.H2, formatter.parse("H2", Half::from));
    }

    //-----------------------------------------------------------------------
    // getDisplayName()
    //-----------------------------------------------------------------------
    @Test
    public void test_getDisplayName() {
        assertTrue(Half.H1.getDisplayName(TextStyle.SHORT, Locale.US).contains("1"));
    }

    @Test
    public void test_getDisplayName_nullStyle() {
        assertThrows(NullPointerException.class, () -> Half.H1.getDisplayName(null, Locale.US));
    }

    @Test
    public void test_getDisplayName_nullLocale() {
        assertThrows(NullPointerException.class, () -> Half.H1.getDisplayName(TextStyle.FULL, null));
    }

    //-----------------------------------------------------------------------
    // isSupported()
    //-----------------------------------------------------------------------
    @Test
    public void test_isSupported() {
        Half test = Half.H1;
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
        assertEquals(false, test.isSupported(QUARTER_OF_YEAR));
        assertEquals(true, test.isSupported(HALF_OF_YEAR));
    }

    //-----------------------------------------------------------------------
    // range()
    //-----------------------------------------------------------------------
    @Test
    public void test_range() {
        assertEquals(HALF_OF_YEAR.range(), Half.H1.range(HALF_OF_YEAR));
    }

    @Test
    public void test_range_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Half.H1.range(MONTH_OF_YEAR));
    }

    @Test
    public void test_range_null() {
        assertThrows(NullPointerException.class, () -> Half.H1.range(null));
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(1, Half.H1.get(HALF_OF_YEAR));
        assertEquals(2, Half.H2.get(HALF_OF_YEAR));
    }

    @Test
    public void test_get_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Half.H2.get(MONTH_OF_YEAR));
    }

    @Test
    public void test_get_null() {
        assertThrows(NullPointerException.class, () -> Half.H2.get(null));
    }

    //-----------------------------------------------------------------------
    // getLong()
    //-----------------------------------------------------------------------
    @Test
    public void test_getLong() {
        assertEquals(1, Half.H1.getLong(HALF_OF_YEAR));
        assertEquals(2, Half.H2.getLong(HALF_OF_YEAR));
    }

    @Test
    public void test_getLong_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Half.H2.getLong(MONTH_OF_YEAR));
    }

    @Test
    public void test_getLong_null() {
        assertThrows(NullPointerException.class, () -> Half.H2.getLong(null));
    }

    //-----------------------------------------------------------------------
    // plus(long), plus(long,unit)
    //-----------------------------------------------------------------------
    public static Object[][] data_plus() {
        return new Object[][] {
                {1, -4, 1},
                {1, -3, 2},
                {1, -2, 1},
                {1, -1, 2},
                {1, 0, 1},
                {1, 1, 2},
                {1, 2, 1},
                {1, 3, 2},
                {1, 4, 1},
        };
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_plus_long(int base, long amount, int expected) {
        assertEquals(Half.of(expected), Half.of(base).plus(amount));
    }

    //-----------------------------------------------------------------------
    // minus(long), minus(long,unit)
    //-----------------------------------------------------------------------
    public static Object[][] data_minus() {
        return new Object[][] {
                {1, -4, 1},
                {1, -3, 2},
                {1, -2, 1},
                {1, -1, 2},
                {1, 0, 1},
                {1, 1, 2},
                {1, 2, 1},
                {1, 3, 2},
                {1, 4, 1},
        };
    }

    @ParameterizedTest
    @MethodSource("data_minus")
    public void test_minus_long(int base, long amount, int expected) {
        assertEquals(Half.of(expected), Half.of(base).minus(amount));
    }

    //-----------------------------------------------------------------------
    // length(boolean)
    //-----------------------------------------------------------------------
    @Test
    public void test_length_boolean() {
        assertEquals(182, Half.H1.length(true));
        assertEquals(181, Half.H1.length(false));
        assertEquals(184, Half.H2.length(true));
        assertEquals(184, Half.H2.length(false));
    }

    //-----------------------------------------------------------------------
    // firstMonth()
    //-----------------------------------------------------------------------
    @Test
    public void test_firstMonth() {
        assertEquals(Month.JANUARY, Half.H1.firstMonth());
        assertEquals(Month.JULY, Half.H2.firstMonth());
    }

    //-----------------------------------------------------------------------
    // query()
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertEquals(IsoChronology.INSTANCE, Half.H1.query(TemporalQueries.chronology()));
        assertEquals(null, Half.H1.query(TemporalQueries.localDate()));
        assertEquals(null, Half.H1.query(TemporalQueries.localTime()));
        assertEquals(null, Half.H1.query(TemporalQueries.offset()));
        assertEquals(HALF_YEARS, Half.H1.query(TemporalQueries.precision()));
        assertEquals(null, Half.H1.query(TemporalQueries.zone()));
        assertEquals(null, Half.H1.query(TemporalQueries.zoneId()));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals("H1", Half.H1.toString());
        assertEquals("H2", Half.H2.toString());
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test
    public void test_enum() {
        assertEquals(Half.H2, Half.valueOf("H2"));
        assertEquals(Half.H1, Half.values()[0]);
    }

}
