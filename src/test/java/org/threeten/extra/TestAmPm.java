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
import static java.time.temporal.ChronoUnit.HALF_DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Test AmPm.
 */
public class TestAmPm {

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(AmPm.class));
        assertTrue(Serializable.class.isAssignableFrom(AmPm.class));
        assertTrue(Comparable.class.isAssignableFrom(AmPm.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(AmPm.class));
    }

    //-----------------------------------------------------------------------
    // of(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int_singleton_equals() {
        for (int i = 0; i <= 1; i++) {
            AmPm test = AmPm.of(i);
            assertEquals(i, test.getValue());
        }
    }

    @Test
    public void test_of_int_valueTooLow() {
        assertThrows(DateTimeException.class, () -> AmPm.of(-1));
    }

    @Test
    public void test_of_int_valueTooHigh() {
        assertThrows(DateTimeException.class, () -> AmPm.of(2));
    }

    //-----------------------------------------------------------------------
    // ofHour(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_ofHour_int_singleton() {
        for (int i = 0; i < 12; i++) {
            assertSame(AmPm.AM, AmPm.ofHour(i));
        }
        for (int i = 12; i < 24; i++) {
        	assertSame(AmPm.PM, AmPm.ofHour(i));
        }
    }

    @Test
    public void test_ofHour_int_valueTooLow() {
        assertThrows(DateTimeException.class, () -> AmPm.ofHour(-1));
    }

    @Test
    public void test_ofHour_int_valueTooHigh() {
        assertThrows(DateTimeException.class, () -> AmPm.ofHour(24));
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor() {
        assertEquals(AmPm.AM, AmPm.from(LocalTime.of(8, 30)));
        assertEquals(AmPm.PM, AmPm.from(LocalTime.of(17, 30)));
    }

    @Test
    public void test_from_TemporalAccessor_invalid_noDerive() {
        assertThrows(DateTimeException.class, () -> AmPm.from(LocalDate.of(2007, 7, 30)));
    }

    @Test
    public void test_from_TemporalAccessor_null() {
        assertThrows(NullPointerException.class, () -> AmPm.from((TemporalAccessor) null));
    }

    //-----------------------------------------------------------------------
    // getDisplayName()
    //-----------------------------------------------------------------------
    @Test
    public void test_getDisplayName() {
        assertEquals("AM", AmPm.AM.getDisplayName(TextStyle.SHORT, Locale.US));
    }

    @Test
    public void test_getDisplayName_nullStyle() {
        assertThrows(NullPointerException.class, () -> AmPm.AM.getDisplayName(null, Locale.US));
    }

    @Test
    public void test_getDisplayName_nullLocale() {
        assertThrows(NullPointerException.class, () -> AmPm.AM.getDisplayName(TextStyle.FULL, null));
    }

    //-----------------------------------------------------------------------
    // isSupported()
    //-----------------------------------------------------------------------
    @Test
    public void test_isSupported() {
        AmPm test = AmPm.AM;
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
        assertEquals(true, test.isSupported(AMPM_OF_DAY));
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
    }

    //-----------------------------------------------------------------------
    // range()
    //-----------------------------------------------------------------------
    @Test
    public void test_range() {
        assertEquals(AMPM_OF_DAY.range(), AmPm.AM.range(AMPM_OF_DAY));
    }

    @Test
    public void test_range_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> AmPm.AM.range(MONTH_OF_YEAR));
    }

    @Test
    public void test_range_null() {
        assertThrows(NullPointerException.class, () -> AmPm.AM.range(null));
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(0, AmPm.AM.get(AMPM_OF_DAY));
        assertEquals(1, AmPm.PM.get(AMPM_OF_DAY));
    }

    @Test
    public void test_get_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> AmPm.PM.get(MONTH_OF_YEAR));
    }

    @Test
    public void test_get_null() {
        assertThrows(NullPointerException.class, () -> AmPm.PM.get(null));
    }

    //-----------------------------------------------------------------------
    // getLong()
    //-----------------------------------------------------------------------
    @Test
    public void test_getLong() {
        assertEquals(0, AmPm.AM.getLong(AMPM_OF_DAY));
        assertEquals(1, AmPm.PM.getLong(AMPM_OF_DAY));
    }

    @Test
    public void test_getLong_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> AmPm.PM.getLong(MONTH_OF_YEAR));
    }

    @Test
    public void test_getLong_null() {
        assertThrows(NullPointerException.class, () -> AmPm.PM.getLong(null));
    }

    //-----------------------------------------------------------------------
    // query()
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertEquals(null, AmPm.AM.query(TemporalQueries.chronology()));
        assertEquals(null, AmPm.AM.query(TemporalQueries.localDate()));
        assertEquals(null, AmPm.AM.query(TemporalQueries.localTime()));
        assertEquals(null, AmPm.AM.query(TemporalQueries.offset()));
        assertEquals(HALF_DAYS, AmPm.AM.query(TemporalQueries.precision()));
        assertEquals(null, AmPm.AM.query(TemporalQueries.zone()));
        assertEquals(null, AmPm.AM.query(TemporalQueries.zoneId()));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals("AM", AmPm.AM.toString());
        assertEquals("PM", AmPm.PM.toString());
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test
    public void test_enum() {
        assertEquals(AmPm.AM, AmPm.valueOf("AM"));
        assertEquals(AmPm.AM, AmPm.values()[0]);
    }

}
