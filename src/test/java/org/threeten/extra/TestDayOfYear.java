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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DayOfYear.
 */
@Test
public class TestDayOfYear {

    private static final Year YEAR_STANDARD = Year.of(2007);
    private static final Year YEAR_LEAP = Year.of(2008);
    private static final int STANDARD_YEAR_LENGTH = 365;
    private static final int LEAP_YEAR_LENGTH = 366;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(DayOfYear.class));
        assertTrue(Comparable.class.isAssignableFrom(DayOfYear.class));
        assertTrue(TemporalAdjuster.class.isAssignableFrom(DayOfYear.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(DayOfYear.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        DayOfYear test = DayOfYear.of(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(test.getValue(), i);
            assertEquals(DayOfYear.of(i), test);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_factory_int_minuteTooLow() {
        DayOfYear.of(0);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_factory_int_hourTooHigh() {
        DayOfYear.of(367);
    }

    //-----------------------------------------------------------------------
    public void test_from_Temporal_notLeapYear() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.from(date);
            assertEquals(test.getValue(), i);
            date = date.plusDays(1);
        }
        DayOfYear test = DayOfYear.from(date);
        assertEquals(test.getValue(), 1);
    }

    public void test_from_Temporal_leapYear() {
        LocalDate date = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.from(date);
            assertEquals(test.getValue(), i);
            date = date.plusDays(1);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_factory_Temporal_noDerive() {
        DayOfYear.from(LocalTime.NOON);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Temporal_null() {
        DayOfYear.from((TemporalAccessor) null);
    }

    //-----------------------------------------------------------------------
    // isSupported()
    //-----------------------------------------------------------------------
    public void test_isSupported() {
        DayOfYear test = DayOfYear.of(12);
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
        assertEquals(test.isSupported(AMPM_OF_DAY), false);
        assertEquals(test.isSupported(DAY_OF_WEEK), false);
        assertEquals(test.isSupported(ALIGNED_DAY_OF_WEEK_IN_MONTH), false);
        assertEquals(test.isSupported(ALIGNED_DAY_OF_WEEK_IN_YEAR), false);
        assertEquals(test.isSupported(DAY_OF_MONTH), false);
        assertEquals(test.isSupported(DAY_OF_YEAR), true);
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
        DayOfYear test = DayOfYear.of(12);
        assertEquals(test.range(DAY_OF_YEAR), DAY_OF_YEAR.range());
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_invalidField() {
        DayOfYear test = DayOfYear.of(12);
        test.range(MONTH_OF_YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_range_null() {
        DayOfYear test = DayOfYear.of(12);
        test.range(null);
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    public void test_get() {
        DayOfYear test = DayOfYear.of(12);
        assertEquals(test.get(DAY_OF_YEAR), 12);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_get_invalidField() {
        DayOfYear test = DayOfYear.of(12);
        test.get(MONTH_OF_YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_get_null() {
        DayOfYear test = DayOfYear.of(12);
        test.get(null);
    }

    //-----------------------------------------------------------------------
    // getLong()
    //-----------------------------------------------------------------------
    public void test_getLong() {
        DayOfYear test = DayOfYear.of(12);
        assertEquals(test.getLong(DAY_OF_YEAR), 12L);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_invalidField() {
        DayOfYear test = DayOfYear.of(12);
        test.getLong(MONTH_OF_YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getLong_null() {
        DayOfYear test = DayOfYear.of(12);
        test.getLong(null);
    }

    //-----------------------------------------------------------------------
    // isValidYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isValidYearMonth_366() {
        DayOfYear test = DayOfYear.of(366);
        assertEquals(test.isValidYear(2011), false);
        assertEquals(test.isValidYear(2012), true);
        assertEquals(test.isValidYear(2013), false);
    }

    public void test_isValidYearMonth_365() {
        DayOfYear test = DayOfYear.of(365);
        assertEquals(test.isValidYear(2011), true);
        assertEquals(test.isValidYear(2012), true);
        assertEquals(test.isValidYear(2013), true);
    }

    //-----------------------------------------------------------------------
    // query()
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        DayOfYear test = DayOfYear.of(12);
        assertEquals(test.query(TemporalQueries.chronology()), IsoChronology.INSTANCE);
        assertEquals(test.query(TemporalQueries.localDate()), null);
        assertEquals(test.query(TemporalQueries.localTime()), null);
        assertEquals(test.query(TemporalQueries.offset()), null);
        assertEquals(test.query(TemporalQueries.precision()), null);
        assertEquals(test.query(TemporalQueries.zone()), null);
        assertEquals(test.query(TemporalQueries.zoneId()), null);
    }

    //-----------------------------------------------------------------------
    // adjustInto()
    //-----------------------------------------------------------------------
    public void test_adjustInto_fromStartOfYear_notLeapYear() {
        LocalDate base = LocalDate.of(2007, 1, 1);
        LocalDate expected = base;
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(test.adjustInto(base), expected);
            expected = expected.plusDays(1);
        }
    }

    public void test_adjustInto_fromEndOfYear_notLeapYear() {
        LocalDate base = LocalDate.of(2007, 12, 31);
        LocalDate expected = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(test.adjustInto(base), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_adjustInto_fromStartOfYear_notLeapYear_day366() {
        LocalDate base = LocalDate.of(2007, 1, 1);
        DayOfYear test = DayOfYear.of(LEAP_YEAR_LENGTH);
        test.adjustInto(base);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_adjustInto_fromEndOfYear_notLeapYear_day366() {
        LocalDate base = LocalDate.of(2007, 12, 31);
        DayOfYear test = DayOfYear.of(LEAP_YEAR_LENGTH);
        test.adjustInto(base);
    }

    public void test_adjustInto_fromStartOfYear_leapYear() {
        LocalDate base = LocalDate.of(2008, 1, 1);
        LocalDate expected = base;
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(test.adjustInto(base), expected);
            expected = expected.plusDays(1);
        }
    }

    public void test_adjustInto_fromEndOfYear_leapYear() {
        LocalDate base = LocalDate.of(2008, 12, 31);
        LocalDate expected = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(test.adjustInto(base), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustInto_nullLocalDate() {
        LocalDate date = null;
        DayOfYear test = DayOfYear.of(1);
        test.adjustInto(date);
    }

    //-----------------------------------------------------------------------
    // atYear(Year)
    //-----------------------------------------------------------------------
    public void test_atYear_Year_notLeapYear() {
        LocalDate expected = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(test.atYear(YEAR_STANDARD), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_atYear_fromStartOfYear_notLeapYear_day366() {
        DayOfYear test = DayOfYear.of(LEAP_YEAR_LENGTH);
        test.atYear(YEAR_STANDARD);
    }

    public void test_atYear_Year_leapYear() {
        LocalDate expected = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(test.atYear(YEAR_LEAP), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atYear_Year_nullYear() {
        DayOfYear test = DayOfYear.of(1);
        test.atYear((Year) null);
    }

    //-----------------------------------------------------------------------
    // atYear(int)
    //-----------------------------------------------------------------------
    public void test_atYear_int_notLeapYear() {
        LocalDate expected = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(test.atYear(2007), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_atYear_int_fromStartOfYear_notLeapYear_day366() {
        DayOfYear test = DayOfYear.of(LEAP_YEAR_LENGTH);
        test.atYear(2007);
    }

    public void test_atYear_int_leapYear() {
        LocalDate expected = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(test.atYear(2008), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_atYear_int_invalidDay() {
        DayOfYear test = DayOfYear.of(1);
        test.atYear(Year.MIN_VALUE - 1);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear a = DayOfYear.of(i);
            for (int j = 1; j <= LEAP_YEAR_LENGTH; j++) {
                DayOfYear b = DayOfYear.of(j);
                if (i < j) {
                    assertEquals(a.compareTo(b) < 0, true);
                    assertEquals(b.compareTo(a) > 0, true);
                } else if (i > j) {
                    assertEquals(a.compareTo(b) > 0, true);
                    assertEquals(b.compareTo(a) < 0, true);
                } else {
                    assertEquals(a.compareTo(b), 0);
                    assertEquals(b.compareTo(a), 0);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_nullDayOfYear() {
        DayOfYear doy = null;
        DayOfYear test = DayOfYear.of(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear a = DayOfYear.of(i);
            for (int j = 1; j <= LEAP_YEAR_LENGTH; j++) {
                DayOfYear b = DayOfYear.of(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullDayOfYear() {
        DayOfYear doy = null;
        DayOfYear test = DayOfYear.of(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        DayOfYear test = DayOfYear.of(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear a = DayOfYear.of(i);
            assertEquals(a.toString(), "DayOfYear:" + i);
        }
    }

}
