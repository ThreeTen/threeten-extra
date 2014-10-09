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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test AmPm.
 */
@Test
public class TestAmPm {

    @BeforeMethod
    public void setUp() {
    }

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
            assertEquals(test.getValue(), i);
        }
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_int_valueTooLow() {
        AmPm.of(-1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_int_valueTooHigh() {
        AmPm.of(2);
    }

    //-----------------------------------------------------------------------
    // ofHour(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_ofHour_int_singleton() {
        for (int i = 0; i < 12; i++) {
            assertEquals(AmPm.ofHour(i), AmPm.AM);
        }
        for (int i = 12; i < 24; i++) {
            assertEquals(AmPm.ofHour(i), AmPm.PM);
        }
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_ofHour_int_valueTooLow() {
        AmPm.ofHour(-1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_ofHour_int_valueTooHigh() {
        AmPm.ofHour(24);
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor() {
        assertEquals(AmPm.from(LocalTime.of(8, 30)), AmPm.AM);
        assertEquals(AmPm.from(LocalTime.of(17, 30)), AmPm.PM);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_from_TemporalAccessor_invalid_noDerive() {
        AmPm.from(LocalDate.of(2007, 7, 30));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_from_TemporalAccessor_null() {
        AmPm.from((TemporalAccessor) null);
    }

    //-----------------------------------------------------------------------
    // getDisplayName()
    //-----------------------------------------------------------------------
    @Test
    public void test_getDisplayName() {
        assertEquals(AmPm.AM.getDisplayName(TextStyle.SHORT, Locale.US), "AM");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getDisplayName_nullStyle() {
        AmPm.AM.getDisplayName(null, Locale.US);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getDisplayName_nullLocale() {
        AmPm.AM.getDisplayName(TextStyle.FULL, null);
    }

    //-----------------------------------------------------------------------
    // isSupported()
    //-----------------------------------------------------------------------
    public void test_isSupported() {
        AmPm test = AmPm.AM;
        assertEquals(test.isSupported(null), false);
        assertEquals(test.isSupported(NANO_OF_SECOND), false);
        assertEquals(test.isSupported(NANO_OF_DAY), false);
        assertEquals(test.isSupported(MICRO_OF_SECOND), false);
        assertEquals(test.isSupported(MICRO_OF_DAY), false);
        assertEquals(test.isSupported(MILLI_OF_SECOND), false);
        assertEquals(test.isSupported(MILLI_OF_DAY), false);
        assertEquals(test.isSupported(SECOND_OF_MINUTE), false);
        assertEquals(test.isSupported(SECOND_OF_DAY), false);
        assertEquals(test.isSupported(MINUTE_OF_HOUR), false);
        assertEquals(test.isSupported(MINUTE_OF_DAY), false);
        assertEquals(test.isSupported(HOUR_OF_AMPM), false);
        assertEquals(test.isSupported(CLOCK_HOUR_OF_AMPM), false);
        assertEquals(test.isSupported(HOUR_OF_DAY), false);
        assertEquals(test.isSupported(CLOCK_HOUR_OF_DAY), false);
        assertEquals(test.isSupported(AMPM_OF_DAY), true);
        assertEquals(test.isSupported(DAY_OF_WEEK), false);
        assertEquals(test.isSupported(ALIGNED_DAY_OF_WEEK_IN_MONTH), false);
        assertEquals(test.isSupported(ALIGNED_DAY_OF_WEEK_IN_YEAR), false);
        assertEquals(test.isSupported(DAY_OF_MONTH), false);
        assertEquals(test.isSupported(DAY_OF_YEAR), false);
        assertEquals(test.isSupported(EPOCH_DAY), false);
        assertEquals(test.isSupported(ALIGNED_WEEK_OF_MONTH), false);
        assertEquals(test.isSupported(ALIGNED_WEEK_OF_YEAR), false);
        assertEquals(test.isSupported(MONTH_OF_YEAR), false);
        assertEquals(test.isSupported(PROLEPTIC_MONTH), false);
        assertEquals(test.isSupported(YEAR_OF_ERA), false);
        assertEquals(test.isSupported(YEAR), false);
        assertEquals(test.isSupported(ERA), false);
        assertEquals(test.isSupported(INSTANT_SECONDS), false);
        assertEquals(test.isSupported(OFFSET_SECONDS), false);
    }

    //-----------------------------------------------------------------------
    // range()
    //-----------------------------------------------------------------------
    public void test_range() {
        assertEquals(AmPm.AM.range(AMPM_OF_DAY), AMPM_OF_DAY.range());
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_invalidField() {
        AmPm.AM.range(MONTH_OF_YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_range_null() {
        AmPm.AM.range(null);
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(AmPm.AM.get(AMPM_OF_DAY), 0);
        assertEquals(AmPm.PM.get(AMPM_OF_DAY), 1);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_get_invalidField() {
        AmPm.PM.get(MONTH_OF_YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_get_null() {
        AmPm.PM.get(null);
    }

    //-----------------------------------------------------------------------
    // getLong()
    //-----------------------------------------------------------------------
    public void test_getLong() {
        assertEquals(AmPm.AM.getLong(AMPM_OF_DAY), 0);
        assertEquals(AmPm.PM.getLong(AMPM_OF_DAY), 1);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_invalidField() {
        AmPm.PM.getLong(MONTH_OF_YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getLong_null() {
        AmPm.PM.getLong(null);
    }

    //-----------------------------------------------------------------------
    // query()
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertEquals(AmPm.AM.query(TemporalQueries.chronology()), null);
        assertEquals(AmPm.AM.query(TemporalQueries.localDate()), null);
        assertEquals(AmPm.AM.query(TemporalQueries.localTime()), null);
        assertEquals(AmPm.AM.query(TemporalQueries.offset()), null);
        assertEquals(AmPm.AM.query(TemporalQueries.precision()), HALF_DAYS);
        assertEquals(AmPm.AM.query(TemporalQueries.zone()), null);
        assertEquals(AmPm.AM.query(TemporalQueries.zoneId()), null);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals(AmPm.AM.toString(), "AM");
        assertEquals(AmPm.PM.toString(), "PM");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test
    public void test_enum() {
        assertEquals(AmPm.valueOf("AM"), AmPm.AM);
        assertEquals(AmPm.values()[0], AmPm.AM);
    }

}
