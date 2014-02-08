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

import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;
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
import java.time.Month;
import java.time.MonthDay;
import java.time.YearMonth;
import java.time.chrono.IsoChronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DayOfMonth.
 */
@Test
public class TestDayOfMonth {

    private static final int MAX_LENGTH = 31;
    private static final DayOfMonth TEST = DayOfMonth.of(12);

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(DayOfMonth.class));
        assertTrue(Comparable.class.isAssignableFrom(DayOfMonth.class));
        assertTrue(TemporalAdjuster.class.isAssignableFrom(DayOfMonth.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(DayOfMonth.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        DayOfMonth test = DayOfMonth.of(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    //-----------------------------------------------------------------------
    // of(int)
    //-----------------------------------------------------------------------
    public void test_of_int_singleton() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth test = DayOfMonth.of(i);
            assertEquals(test.getValue(), i);
            assertEquals(DayOfMonth.of(i), test);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_of_int_tooLow() {
        DayOfMonth.of(0);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_of_int_tooHigh() {
        DayOfMonth.of(32);
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    public void test_from_TemporalAccessor_notLeapYear() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= 31; i++) {  // Jan
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 28; i++) {  // Feb
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Mar
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Apr
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // May
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Jun
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Jul
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Aug
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Sep
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Oct
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Nov
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Dec
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
    }

    public void test_from_TemporalAccessor_leapYear() {
        LocalDate date = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= 31; i++) {  // Jan
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 29; i++) {  // Feb
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Mar
            assertEquals(DayOfMonth.from(date).getValue(), i);
            date = date.plusDays(1);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_from_TemporalAccessor_noDerive() {
        DayOfMonth.from(LocalTime.NOON);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_from_TemporalAccessor_null() {
        DayOfMonth.from((TemporalAccessor) null);
    }

    //-----------------------------------------------------------------------
    // isSupported(TemporalField)
    //-----------------------------------------------------------------------
    public void test_isSupported() {
        assertEquals(TEST.isSupported((TemporalField) null), false);
        assertEquals(TEST.isSupported(NANO_OF_SECOND), false);
        assertEquals(TEST.isSupported(NANO_OF_DAY), false);
        assertEquals(TEST.isSupported(MICRO_OF_SECOND), false);
        assertEquals(TEST.isSupported(MICRO_OF_DAY), false);
        assertEquals(TEST.isSupported(MILLI_OF_SECOND), false);
        assertEquals(TEST.isSupported(MILLI_OF_DAY), false);
        assertEquals(TEST.isSupported(SECOND_OF_MINUTE), false);
        assertEquals(TEST.isSupported(SECOND_OF_DAY), false);
        assertEquals(TEST.isSupported(MINUTE_OF_HOUR), false);
        assertEquals(TEST.isSupported(MINUTE_OF_DAY), false);
        assertEquals(TEST.isSupported(HOUR_OF_AMPM), false);
        assertEquals(TEST.isSupported(CLOCK_HOUR_OF_AMPM), false);
        assertEquals(TEST.isSupported(HOUR_OF_DAY), false);
        assertEquals(TEST.isSupported(CLOCK_HOUR_OF_DAY), false);
        assertEquals(TEST.isSupported(AMPM_OF_DAY), false);
        assertEquals(TEST.isSupported(DAY_OF_WEEK), false);
        assertEquals(TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_MONTH), false);
        assertEquals(TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_YEAR), false);
        assertEquals(TEST.isSupported(DAY_OF_MONTH), true);
        assertEquals(TEST.isSupported(DAY_OF_YEAR), false);
        assertEquals(TEST.isSupported(EPOCH_DAY), false);
        assertEquals(TEST.isSupported(ALIGNED_WEEK_OF_MONTH), false);
        assertEquals(TEST.isSupported(ALIGNED_WEEK_OF_YEAR), false);
        assertEquals(TEST.isSupported(MONTH_OF_YEAR), false);
        assertEquals(TEST.isSupported(PROLEPTIC_MONTH), false);
        assertEquals(TEST.isSupported(YEAR_OF_ERA), false);
        assertEquals(TEST.isSupported(YEAR), false);
        assertEquals(TEST.isSupported(ERA), false);
        assertEquals(TEST.isSupported(INSTANT_SECONDS), false);
        assertEquals(TEST.isSupported(OFFSET_SECONDS), false);
    }

    //-----------------------------------------------------------------------
    // range(TemporalField)
    //-----------------------------------------------------------------------
    public void test_range() {
        assertEquals(TEST.range(DAY_OF_MONTH), DAY_OF_MONTH.range());
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_invalidField() {
        TEST.range(MONTH_OF_YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_range_null() {
        TEST.range((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // get(TemporalField)
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(TEST.get(DAY_OF_MONTH), 12);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_get_invalidField() {
        TEST.get(MONTH_OF_YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_get_null() {
        TEST.get((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // getLong(TemporalField)
    //-----------------------------------------------------------------------
    public void test_getLong() {
        assertEquals(TEST.getLong(DAY_OF_MONTH), 12L);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_invalidField() {
        TEST.getLong(MONTH_OF_YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getLong_null() {
        TEST.getLong((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // isValidYearMonth(YearMonth)
    //-----------------------------------------------------------------------
    @Test
    public void test_isValidYearMonth_31() {
        DayOfMonth test = DayOfMonth.of(31);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 1)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 2)), false);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 3)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 4)), false);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 5)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 6)), false);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 7)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 8)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 9)), false);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 10)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 11)), false);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 12)), true);
    }

    @Test
    public void test_isValidYearMonth_30() {
        DayOfMonth test = DayOfMonth.of(30);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 1)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 2)), false);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 3)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 4)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 5)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 6)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 7)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 8)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 9)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 10)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 11)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 12)), true);
    }

    @Test
    public void test_isValidYearMonth_29() {
        DayOfMonth test = DayOfMonth.of(29);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 1)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 2)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 3)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 4)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 5)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 6)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 7)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 8)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 9)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 10)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 11)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 12)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2011, 2)), false);
    }

    @Test
    public void test_isValidYearMonth_28() {
        DayOfMonth test = DayOfMonth.of(28);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 1)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 2)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 3)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 4)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 5)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 6)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 7)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 8)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 9)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 10)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 11)), true);
        assertEquals(test.isValidYearMonth(YearMonth.of(2012, 12)), true);
    }

    //-----------------------------------------------------------------------
    // query(TemporalQuery)
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertEquals(TEST.query(TemporalQueries.chronology()), IsoChronology.INSTANCE);
        assertEquals(TEST.query(TemporalQueries.localDate()), null);
        assertEquals(TEST.query(TemporalQueries.localTime()), null);
        assertEquals(TEST.query(TemporalQueries.offset()), null);
        assertEquals(TEST.query(TemporalQueries.precision()), null);
        assertEquals(TEST.query(TemporalQueries.zone()), null);
        assertEquals(TEST.query(TemporalQueries.zoneId()), null);
    }

    //-----------------------------------------------------------------------
    // adjustInto(Temporal)
    //-----------------------------------------------------------------------
    public void test_adjustInto() {
        LocalDate base = LocalDate.of(2007, 1, 1);
        LocalDate expected = base;
        for (int i = 1; i <= MAX_LENGTH; i++) {  // Jan
            Temporal result = DayOfMonth.of(i).adjustInto(base);
            assertEquals(result, expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_adjustInto_april31() {
        LocalDate base = LocalDate.of(2007, 4, 1);
        DayOfMonth test = DayOfMonth.of(31);
        test.adjustInto(base);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_adjustInto_february29_notLeapYear() {
        LocalDate base = LocalDate.of(2007, 2, 1);
        DayOfMonth test = DayOfMonth.of(29);
        test.adjustInto(base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustInto_null() {
        TEST.adjustInto((Temporal) null);
    }

    //-----------------------------------------------------------------------
    // atMonth(Month)
    //-----------------------------------------------------------------------
    @Test
    public void test_atMonth_Month_31() {
        DayOfMonth test = DayOfMonth.of(31);
        assertEquals(test.atMonth(JANUARY), MonthDay.of(1, 31));
        assertEquals(test.atMonth(FEBRUARY), MonthDay.of(2, 29));
        assertEquals(test.atMonth(MARCH), MonthDay.of(3, 31));
        assertEquals(test.atMonth(APRIL), MonthDay.of(4, 30));
        assertEquals(test.atMonth(MAY), MonthDay.of(5, 31));
        assertEquals(test.atMonth(JUNE), MonthDay.of(6, 30));
        assertEquals(test.atMonth(JULY), MonthDay.of(7, 31));
        assertEquals(test.atMonth(AUGUST), MonthDay.of(8, 31));
        assertEquals(test.atMonth(SEPTEMBER), MonthDay.of(9, 30));
        assertEquals(test.atMonth(OCTOBER), MonthDay.of(10, 31));
        assertEquals(test.atMonth(NOVEMBER), MonthDay.of(11, 30));
        assertEquals(test.atMonth(DECEMBER), MonthDay.of(12, 31));
    }

    @Test
    public void test_atMonth_Month_28() {
        DayOfMonth test = DayOfMonth.of(28);
        assertEquals(test.atMonth(JANUARY), MonthDay.of(1, 28));
        assertEquals(test.atMonth(FEBRUARY), MonthDay.of(2, 28));
        assertEquals(test.atMonth(MARCH), MonthDay.of(3, 28));
        assertEquals(test.atMonth(APRIL), MonthDay.of(4, 28));
        assertEquals(test.atMonth(MAY), MonthDay.of(5, 28));
        assertEquals(test.atMonth(JUNE), MonthDay.of(6, 28));
        assertEquals(test.atMonth(JULY), MonthDay.of(7, 28));
        assertEquals(test.atMonth(AUGUST), MonthDay.of(8, 28));
        assertEquals(test.atMonth(SEPTEMBER), MonthDay.of(9, 28));
        assertEquals(test.atMonth(OCTOBER), MonthDay.of(10, 28));
        assertEquals(test.atMonth(NOVEMBER), MonthDay.of(11, 28));
        assertEquals(test.atMonth(DECEMBER), MonthDay.of(12, 28));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atMonth_null() {
        TEST.atMonth((Month) null);
    }

    //-----------------------------------------------------------------------
    // atMonth(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_atMonth_int_31() {
        DayOfMonth test = DayOfMonth.of(31);
        assertEquals(test.atMonth(1), MonthDay.of(1, 31));
        assertEquals(test.atMonth(2), MonthDay.of(2, 29));
        assertEquals(test.atMonth(3), MonthDay.of(3, 31));
        assertEquals(test.atMonth(4), MonthDay.of(4, 30));
        assertEquals(test.atMonth(5), MonthDay.of(5, 31));
        assertEquals(test.atMonth(6), MonthDay.of(6, 30));
        assertEquals(test.atMonth(7), MonthDay.of(7, 31));
        assertEquals(test.atMonth(8), MonthDay.of(8, 31));
        assertEquals(test.atMonth(9), MonthDay.of(9, 30));
        assertEquals(test.atMonth(10), MonthDay.of(10, 31));
        assertEquals(test.atMonth(11), MonthDay.of(11, 30));
        assertEquals(test.atMonth(12), MonthDay.of(12, 31));
    }

    @Test
    public void test_atMonth_int_28() {
        DayOfMonth test = DayOfMonth.of(28);
        assertEquals(test.atMonth(1), MonthDay.of(1, 28));
        assertEquals(test.atMonth(2), MonthDay.of(2, 28));
        assertEquals(test.atMonth(3), MonthDay.of(3, 28));
        assertEquals(test.atMonth(4), MonthDay.of(4, 28));
        assertEquals(test.atMonth(5), MonthDay.of(5, 28));
        assertEquals(test.atMonth(6), MonthDay.of(6, 28));
        assertEquals(test.atMonth(7), MonthDay.of(7, 28));
        assertEquals(test.atMonth(8), MonthDay.of(8, 28));
        assertEquals(test.atMonth(9), MonthDay.of(9, 28));
        assertEquals(test.atMonth(10), MonthDay.of(10, 28));
        assertEquals(test.atMonth(11), MonthDay.of(11, 28));
        assertEquals(test.atMonth(12), MonthDay.of(12, 28));
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_atMonth_tooLow() {
        TEST.atMonth(0);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_atMonth_tooHigh() {
        TEST.atMonth(13);
    }

    //-----------------------------------------------------------------------
    // atYearMonth(YearMonth)
    //-----------------------------------------------------------------------
    @Test
    public void test_atYearMonth_31() {
        DayOfMonth test = DayOfMonth.of(31);
        assertEquals(test.atYearMonth(YearMonth.of(2012, 1)), LocalDate.of(2012, 1, 31));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 2)), LocalDate.of(2012, 2, 29));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 3)), LocalDate.of(2012, 3, 31));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 4)), LocalDate.of(2012, 4, 30));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 5)), LocalDate.of(2012, 5, 31));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 6)), LocalDate.of(2012, 6, 30));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 7)), LocalDate.of(2012, 7, 31));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 8)), LocalDate.of(2012, 8, 31));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 9)), LocalDate.of(2012, 9, 30));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 10)), LocalDate.of(2012, 10, 31));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 11)), LocalDate.of(2012, 11, 30));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 12)), LocalDate.of(2012, 12, 31));
        assertEquals(test.atYearMonth(YearMonth.of(2011, 2)), LocalDate.of(2011, 2, 28));
    }

    @Test
    public void test_atYearMonth_28() {
        DayOfMonth test = DayOfMonth.of(28);
        assertEquals(test.atYearMonth(YearMonth.of(2012, 1)), LocalDate.of(2012, 1, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 2)), LocalDate.of(2012, 2, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 3)), LocalDate.of(2012, 3, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 4)), LocalDate.of(2012, 4, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 5)), LocalDate.of(2012, 5, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 6)), LocalDate.of(2012, 6, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 7)), LocalDate.of(2012, 7, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 8)), LocalDate.of(2012, 8, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 9)), LocalDate.of(2012, 9, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 10)), LocalDate.of(2012, 10, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 11)), LocalDate.of(2012, 11, 28));
        assertEquals(test.atYearMonth(YearMonth.of(2012, 12)), LocalDate.of(2012, 12, 28));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atYearMonth_null() {
        TEST.atYearMonth((YearMonth) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth a = DayOfMonth.of(i);
            for (int j = 1; j <= MAX_LENGTH; j++) {
                DayOfMonth b = DayOfMonth.of(j);
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
    public void test_compareTo_nullDayOfMonth() {
        DayOfMonth doy = null;
        DayOfMonth test = DayOfMonth.of(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth a = DayOfMonth.of(i);
            for (int j = 1; j <= MAX_LENGTH; j++) {
                DayOfMonth b = DayOfMonth.of(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullDayOfMonth() {
        DayOfMonth doy = null;
        DayOfMonth test = DayOfMonth.of(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        DayOfMonth test = DayOfMonth.of(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            DayOfMonth a = DayOfMonth.of(i);
            assertEquals(a.toString(), "DayOfMonth:" + i);
        }
    }

}
