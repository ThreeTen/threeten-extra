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
import static java.time.temporal.ChronoUnit.CENTURIES;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLENNIA;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.time.temporal.IsoFields.DAY_OF_QUARTER;
import static java.time.temporal.IsoFields.QUARTER_OF_YEAR;
import static java.time.temporal.IsoFields.QUARTER_YEARS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.threeten.extra.Quarter.Q1;
import static org.threeten.extra.Quarter.Q2;
import static org.threeten.extra.Quarter.Q3;
import static org.threeten.extra.Quarter.Q4;

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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test YearQuarter.
 */
@Test
public class TestYearQuarter {

    private static final YearQuarter TEST = YearQuarter.of(2012, Q2);
    private static final int STANDARD_YEAR_LENGTH = 365;
    private static final int LEAP_YEAR_LENGTH = 366;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(YearQuarter.class));
        assertTrue(Comparable.class.isAssignableFrom(YearQuarter.class));
        assertTrue(TemporalAdjuster.class.isAssignableFrom(YearQuarter.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(YearQuarter.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        YearQuarter test = YearQuarter.of(2012, 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(ois.readObject(), test);
        }
    }

    //-----------------------------------------------------------------------
    // of(int,Quarter)
    //-----------------------------------------------------------------------
    public void test_of_int_Quarter() {
        for (int year = -100; year <= 100; year++) {
            for (Quarter quarter : Quarter.values()) {
                YearQuarter test = YearQuarter.of(year, quarter);
                assertEquals(test.getYear(), year);
                assertEquals(test.getQuarterValue(), quarter.getValue());
                assertEquals(test.getQuarter(), quarter);
            }
        }
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_int_Quarter_yearTooLow() {
        YearQuarter.of(Year.MIN_VALUE - 1, Quarter.Q2);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_int_Quarter_yearTooHigh() {
        YearQuarter.of(Year.MAX_VALUE + 1, Quarter.Q2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_of_int_Quarter_null() {
        YearQuarter.of(2012, (Quarter) null);
    }

    //-----------------------------------------------------------------------
    // of(int,int)
    //-----------------------------------------------------------------------
    public void test_of_int_int() {
        for (int year = -100; year <= 100; year++) {
            for (int quarter = 1; quarter <= 4; quarter++) {
                YearQuarter test = YearQuarter.of(year, quarter);
                assertEquals(test.getYear(), year);
                assertEquals(test.getQuarterValue(), quarter);
                assertEquals(test.getQuarter(), Quarter.of(quarter));
                assertEquals(YearQuarter.of(year, quarter), test);
                assertEquals(YearQuarter.of(year, quarter).hashCode(), test.hashCode());
            }
        }
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_int_int_yearTooLow() {
        YearQuarter.of(Year.MIN_VALUE - 1, 1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_int_int_yearTooHigh() {
        YearQuarter.of(Year.MAX_VALUE + 1, 1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_int_int_quarterTooLow() {
        YearQuarter.of(2012, 0);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_int_int_quarterTooHigh() {
        YearQuarter.of(2012, 5);
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    public void test_from_TemporalAccessor_notLeapYear() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            YearQuarter test = YearQuarter.from(date);
            int expected = ((date.getMonthValue() - 1) / 3) + 1;
            assertEquals(test, YearQuarter.of(2007, expected));
            date = date.plusDays(1);
        }
    }

    public void test_from_TemporalAccessor_leapYear() {
        LocalDate date = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            YearQuarter test = YearQuarter.from(date);
            int expected = ((date.getMonthValue() - 1) / 3) + 1;
            assertEquals(test, YearQuarter.of(2008, expected));
            date = date.plusDays(1);
        }
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_from_TemporalAccessor_noDerive() {
        YearQuarter.from(LocalTime.NOON);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_from_TemporalAccessor_null() {
        YearQuarter.from((TemporalAccessor) null);
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence)
    //-----------------------------------------------------------------------
    public void test_parse_CharSequence() {
        assertEquals(YearQuarter.parse("2012-Q3"), YearQuarter.of(2012, Q3));
    }

    public void test_parse_CharSequence_caseInsensitive() {
        assertEquals(YearQuarter.parse("2012-q3"), YearQuarter.of(2012, Q3));
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequenceDate_invalidYear() {
        YearQuarter.parse("12345-Q3");
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequenceDate_invalidQuarter() {
        YearQuarter.parse("2012-Q0");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceDate_nullCharSequence() {
        YearQuarter.parse((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void test_parse_CharSequenceDateTimeFormatter() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("'Q'Q uuuu");
        assertEquals(YearQuarter.parse("Q3 2012", f), YearQuarter.of(2012, Q3));
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequenceDateDateTimeFormatter_invalidQuarter() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("'Q'Q uuuu");
        YearQuarter.parse("Q0 2012", f);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceDateTimeFormatter_nullCharSequence() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("'Q'Q uuuu");
        YearQuarter.parse((CharSequence) null, f);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceDateTimeFormatter_nullDateTimeFormatter() {
        YearQuarter.parse("", (DateTimeFormatter) null);
    }

    //-----------------------------------------------------------------------
    // isSupported(TemporalField)
    //-----------------------------------------------------------------------
    public void test_isSupported_TemporalField() {
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
        assertEquals(TEST.isSupported(DAY_OF_MONTH), false);
        assertEquals(TEST.isSupported(DAY_OF_YEAR), false);
        assertEquals(TEST.isSupported(EPOCH_DAY), false);
        assertEquals(TEST.isSupported(ALIGNED_WEEK_OF_MONTH), false);
        assertEquals(TEST.isSupported(ALIGNED_WEEK_OF_YEAR), false);
        assertEquals(TEST.isSupported(MONTH_OF_YEAR), false);
        assertEquals(TEST.isSupported(PROLEPTIC_MONTH), false);
        assertEquals(TEST.isSupported(YEAR_OF_ERA), true);
        assertEquals(TEST.isSupported(YEAR), true);
        assertEquals(TEST.isSupported(ERA), true);
        assertEquals(TEST.isSupported(INSTANT_SECONDS), false);
        assertEquals(TEST.isSupported(OFFSET_SECONDS), false);
        assertEquals(TEST.isSupported(QUARTER_OF_YEAR), true);
        assertEquals(TEST.isSupported(DAY_OF_QUARTER), false);
    }

    //-----------------------------------------------------------------------
    // isSupported(TemporalUnit)
    //-----------------------------------------------------------------------
    public void test_isSupported_TemporalUnit() {
        assertEquals(TEST.isSupported((TemporalUnit) null), false);
        assertEquals(TEST.isSupported(NANOS), false);
        assertEquals(TEST.isSupported(MICROS), false);
        assertEquals(TEST.isSupported(MILLIS), false);
        assertEquals(TEST.isSupported(SECONDS), false);
        assertEquals(TEST.isSupported(MINUTES), false);
        assertEquals(TEST.isSupported(HOURS), false);
        assertEquals(TEST.isSupported(DAYS), false);
        assertEquals(TEST.isSupported(WEEKS), false);
        assertEquals(TEST.isSupported(MONTHS), false);
        assertEquals(TEST.isSupported(YEARS), true);
        assertEquals(TEST.isSupported(DECADES), true);
        assertEquals(TEST.isSupported(CENTURIES), true);
        assertEquals(TEST.isSupported(MILLENNIA), true);
        assertEquals(TEST.isSupported(ERA), true);
        assertEquals(TEST.isSupported(FOREVER), false);
        assertEquals(TEST.isSupported(QUARTER_YEARS), true);
    }

    //-----------------------------------------------------------------------
    // range(TemporalField)
    //-----------------------------------------------------------------------
    public void test_range() {
        assertEquals(TEST.range(QUARTER_OF_YEAR), QUARTER_OF_YEAR.range());
        assertEquals(TEST.range(YEAR), YEAR.range());
        assertEquals(TEST.range(YEAR_OF_ERA), ValueRange.of(1, Year.MAX_VALUE));
        assertEquals(TEST.range(ERA), ERA.range());
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
        assertEquals(TEST.get(QUARTER_OF_YEAR), 2);
        assertEquals(TEST.get(YEAR), 2012);
        assertEquals(TEST.get(YEAR_OF_ERA), 2012);
        assertEquals(TEST.get(ERA), 1);
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
        assertEquals(TEST.getLong(QUARTER_OF_YEAR), 2L);
        assertEquals(TEST.getLong(YEAR), 2012L);
        assertEquals(TEST.getLong(YEAR_OF_ERA), 2012L);
        assertEquals(TEST.getLong(ERA), 1L);
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
    // isLeapYear(int)
    //-----------------------------------------------------------------------
    public void test_isLeapYear_int() {
        for (int year = -500; year <= 500; year++) {
            for (Quarter quarter : Quarter.values()) {
                YearQuarter test = YearQuarter.of(year, quarter);
                assertEquals(test.isLeapYear(), Year.isLeap(year));
            }
        }
    }

    //-----------------------------------------------------------------------
    // isValidDay(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_isValidDay_int_nonLeap() {
        assertEquals(YearQuarter.of(2011, Q1).isValidDay(90), true);
        assertEquals(YearQuarter.of(2011, Q1).isValidDay(91), false);
        assertEquals(YearQuarter.of(2011, Q1).isValidDay(92), false);

        assertEquals(YearQuarter.of(2011, Q2).isValidDay(90), true);
        assertEquals(YearQuarter.of(2011, Q2).isValidDay(91), true);
        assertEquals(YearQuarter.of(2011, Q2).isValidDay(92), false);

        assertEquals(YearQuarter.of(2011, Q3).isValidDay(90), true);
        assertEquals(YearQuarter.of(2011, Q3).isValidDay(91), true);
        assertEquals(YearQuarter.of(2011, Q4).isValidDay(90), true);

        assertEquals(YearQuarter.of(2011, Q3).isValidDay(92), true);
        assertEquals(YearQuarter.of(2011, Q4).isValidDay(91), true);
        assertEquals(YearQuarter.of(2011, Q4).isValidDay(92), true);
    }

    @Test
    public void test_isValidDay_int_leap() {
        assertEquals(YearQuarter.of(2012, Q1).isValidDay(90), true);
        assertEquals(YearQuarter.of(2012, Q1).isValidDay(91), true);
        assertEquals(YearQuarter.of(2012, Q1).isValidDay(92), false);

        assertEquals(YearQuarter.of(2012, Q2).isValidDay(90), true);
        assertEquals(YearQuarter.of(2012, Q2).isValidDay(91), true);
        assertEquals(YearQuarter.of(2012, Q2).isValidDay(92), false);

        assertEquals(YearQuarter.of(2012, Q3).isValidDay(90), true);
        assertEquals(YearQuarter.of(2012, Q3).isValidDay(91), true);
        assertEquals(YearQuarter.of(2012, Q3).isValidDay(92), true);

        assertEquals(YearQuarter.of(2012, Q4).isValidDay(90), true);
        assertEquals(YearQuarter.of(2012, Q4).isValidDay(91), true);
        assertEquals(YearQuarter.of(2012, Q4).isValidDay(92), true);
    }

    @Test
    public void test_isValidDay_int_outOfRange() {
        assertEquals(YearQuarter.of(2011, Q1).isValidDay(93), false);
        assertEquals(YearQuarter.of(2011, Q2).isValidDay(93), false);
        assertEquals(YearQuarter.of(2011, Q3).isValidDay(93), false);
        assertEquals(YearQuarter.of(2011, Q4).isValidDay(93), false);

        assertEquals(YearQuarter.of(2011, Q1).isValidDay(0), false);
        assertEquals(YearQuarter.of(2011, Q2).isValidDay(0), false);
        assertEquals(YearQuarter.of(2011, Q3).isValidDay(0), false);
        assertEquals(YearQuarter.of(2011, Q4).isValidDay(0), false);
    }

    //-----------------------------------------------------------------------
    // lengthOfQuarter()
    //-----------------------------------------------------------------------
    public void test_lengthOfQuarter() {
        for (int year = -500; year <= 500; year++) {
            assertEquals(YearQuarter.of(year, Q1).lengthOfQuarter(), Year.isLeap(year) ? 91 : 90);
            assertEquals(YearQuarter.of(year, Q2).lengthOfQuarter(), 91);
            assertEquals(YearQuarter.of(year, Q3).lengthOfQuarter(), 92);
            assertEquals(YearQuarter.of(year, Q4).lengthOfQuarter(), 92);
        }
    }

    //-----------------------------------------------------------------------
    // with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    public void test_with_TemporalAdjuster_Quarter() {
        assertEquals(YearQuarter.of(2007, Q2).with(Q1), YearQuarter.of(2007, Q1));
    }

    public void test_with_TemporalAdjuster_Year() {
        assertEquals(YearQuarter.of(2007, Q2).with(Year.of(2012)), YearQuarter.of(2012, Q2));
    }

    public void test_with_TemporalAdjuster_YearQuarter() {
        assertEquals(YearQuarter.of(2007, Q2).with(YearQuarter.of(2012, Q3)), YearQuarter.of(2012, Q3));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_with_TemporalAdjuster_LocalDate() {
        YearQuarter.of(2007, Q2).with(LocalDate.of(2012, 6, 30));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_with_TemporalAdjuster_null() {
        YearQuarter.of(2007, Q2).with((TemporalAdjuster) null);
    }

    //-----------------------------------------------------------------------
    // withYear(int)
    //-----------------------------------------------------------------------
    public void test_withYear() {
        assertEquals(YearQuarter.of(2007, Q2).withYear(2012), YearQuarter.of(2012, Q2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withYear_int_quarterTooLow() {
        TEST.withYear(Year.MIN_VALUE - 1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withYear_int_quarterTooHigh() {
        TEST.withYear(Year.MAX_VALUE + 1);
    }

    //-----------------------------------------------------------------------
    // withQuarter(int)
    //-----------------------------------------------------------------------
    public void test_withQuarter_int() {
        assertEquals(YearQuarter.of(2007, Q2).withQuarter(1), YearQuarter.of(2007, Q1));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withQuarter_int_quarterTooLow() {
        TEST.withQuarter(0);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withQuarter_int_quarterTooHigh() {
        TEST.withQuarter(5);
    }

    //-----------------------------------------------------------------------
    // plus(long,TemporalUnit)
    //-----------------------------------------------------------------------
    public void test_plus_longTemporalUnit() {
        assertEquals(YearQuarter.of(2007, Q2).plus(5, YEARS), YearQuarter.of(2012, Q2));
        assertEquals(YearQuarter.of(2007, Q2).plus(0, YEARS), YearQuarter.of(2007, Q2));
        assertEquals(YearQuarter.of(2007, Q2).plus(-5, YEARS), YearQuarter.of(2002, Q2));
        assertEquals(YearQuarter.of(2007, Q2).plus(5, QUARTER_YEARS), YearQuarter.of(2008, Q3));
        assertEquals(YearQuarter.of(2007, Q2).plus(0, QUARTER_YEARS), YearQuarter.of(2007, Q2));
        assertEquals(YearQuarter.of(2007, Q2).plus(-5, QUARTER_YEARS), YearQuarter.of(2006, Q1));
    }

    //-----------------------------------------------------------------------
    // plusYears(int)
    //-----------------------------------------------------------------------
    public void test_plusYears() {
        assertEquals(YearQuarter.of(2007, Q2).plusYears(5), YearQuarter.of(2012, Q2));
        assertEquals(YearQuarter.of(2007, Q2).plusYears(0), YearQuarter.of(2007, Q2));
        assertEquals(YearQuarter.of(2007, Q2).plusYears(-5), YearQuarter.of(2002, Q2));
    }

    //-----------------------------------------------------------------------
    // plusQuarters(int)
    //-----------------------------------------------------------------------
    public void test_plusQuarters() {
        assertEquals(YearQuarter.of(2007, Q2).plusQuarters(5), YearQuarter.of(2008, Q3));
        assertEquals(YearQuarter.of(2007, Q2).plusQuarters(0), YearQuarter.of(2007, Q2));
        assertEquals(YearQuarter.of(2007, Q2).plusQuarters(-5), YearQuarter.of(2006, Q1));
    }

    //-----------------------------------------------------------------------
    // minus(long,TemporalUnit)
    //-----------------------------------------------------------------------
    public void test_minus_longTemporalUnit() {
        assertEquals(YearQuarter.of(2007, Q2).minus(5, YEARS), YearQuarter.of(2002, Q2));
        assertEquals(YearQuarter.of(2007, Q2).minus(0, YEARS), YearQuarter.of(2007, Q2));
        assertEquals(YearQuarter.of(2007, Q2).minus(-5, YEARS), YearQuarter.of(2012, Q2));
        assertEquals(YearQuarter.of(2007, Q2).minus(5, QUARTER_YEARS), YearQuarter.of(2006, Q1));
        assertEquals(YearQuarter.of(2007, Q2).minus(0, QUARTER_YEARS), YearQuarter.of(2007, Q2));
        assertEquals(YearQuarter.of(2007, Q2).minus(-5, QUARTER_YEARS), YearQuarter.of(2008, Q3));
    }

    //-----------------------------------------------------------------------
    // minusYears(int)
    //-----------------------------------------------------------------------
    public void test_minusYears() {
        assertEquals(YearQuarter.of(2007, Q2).minusYears(5), YearQuarter.of(2002, Q2));
        assertEquals(YearQuarter.of(2007, Q2).minusYears(0), YearQuarter.of(2007, Q2));
        assertEquals(YearQuarter.of(2007, Q2).minusYears(-5), YearQuarter.of(2012, Q2));
    }

    //-----------------------------------------------------------------------
    // minusQuarters(int)
    //-----------------------------------------------------------------------
    public void test_minusQuarters() {
        assertEquals(YearQuarter.of(2007, Q2).minusQuarters(5), YearQuarter.of(2006, Q1));
        assertEquals(YearQuarter.of(2007, Q2).minusQuarters(0), YearQuarter.of(2007, Q2));
        assertEquals(YearQuarter.of(2007, Q2).minusQuarters(-5), YearQuarter.of(2008, Q3));
    }

    //-----------------------------------------------------------------------
    // lengthOfYear()
    //-----------------------------------------------------------------------
    public void test_lengthOfYear() {
        for (int year = -500; year <= 500; year++) {
            for (Quarter quarter : Quarter.values()) {
                YearQuarter test = YearQuarter.of(year, quarter);
                assertEquals(test.lengthOfYear(), Year.isLeap(year) ? 366 : 365);
            }
        }
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
        assertEquals(TEST.query(TemporalQueries.precision()), QUARTER_YEARS);
        assertEquals(TEST.query(TemporalQueries.zone()), null);
        assertEquals(TEST.query(TemporalQueries.zoneId()), null);
    }

    //-----------------------------------------------------------------------
    // adjustInto(Temporal)
    //-----------------------------------------------------------------------
    public void test_adjustInto_Temporal() {
        for (int month = 1; month < 12; month++) {
            for (int dom = 1; dom < 28; dom++) {
                LocalDate base = LocalDate.of(2007, month, dom);
                LocalDate expected = LocalDate.of(2012, 4 + ((month - 1) % 3), dom);
                assertEquals(YearQuarter.of(2012, Q2).adjustInto(base), expected);
            }
        }
    }

    public void test_adjustInto_Temporal_lastValidDay_nonLeap() {
        LocalDate base = LocalDate.of(2007, 5, 31);
        LocalDate expected = LocalDate.of(2011, 2, 28);
        assertEquals(YearQuarter.of(2011, Q1).adjustInto(base), expected);
    }

    public void test_adjustInto_Temporal_lastValidDay_leap() {
        LocalDate base = LocalDate.of(2007, 5, 31);
        LocalDate expected = LocalDate.of(2012, 2, 29);
        assertEquals(YearQuarter.of(2012, Q1).adjustInto(base), expected);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_adjustInto_Temporal_null() {
        TEST.adjustInto((Temporal) null);
    }

    //-----------------------------------------------------------------------
    // until(Temporal,TemporalUnit)
    //-----------------------------------------------------------------------
    public void test_until_TemporalTemporalUnit_QUARTER_YEARS() {
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q4), QUARTER_YEARS), -2);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q1), QUARTER_YEARS), -1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q2), QUARTER_YEARS), 0);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q3), QUARTER_YEARS), 1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q4), QUARTER_YEARS), 2);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q1), QUARTER_YEARS), 3);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q2), QUARTER_YEARS), 4);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q3), QUARTER_YEARS), 5);
    }

    public void test_until_TemporalTemporalUnit_YEARS() {
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2010, Q2), YEARS), -2);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2010, Q3), YEARS), -1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2010, Q4), YEARS), -1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q1), YEARS), -1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q2), YEARS), -1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q3), YEARS), 0);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q4), YEARS), 0);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q1), YEARS), 0);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q2), YEARS), 0);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q3), YEARS), 0);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q4), YEARS), 0);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q1), YEARS), 0);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q2), YEARS), 1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q3), YEARS), 1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q4), YEARS), 1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2014, Q1), YEARS), 1);
        assertEquals(YearQuarter.of(2012, Q2).until(YearQuarter.of(2014, Q2), YEARS), 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_until_TemporalTemporalUnit_nullTemporal() {
        YearQuarter.of(2012, Q2).until(null, QUARTER_YEARS);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_until_TemporalTemporalUnit_nullTemporalUnit() {
        YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q3), null);
    }

    //-----------------------------------------------------------------------
    // format(DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void test_format() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("'Q'Q uuuu");
        assertEquals(YearQuarter.of(2012, Q1).format(f), "Q1 2012");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_format_null() {
        TEST.format((DateTimeFormatter) null);
    }

    //-----------------------------------------------------------------------
    // atDay(int)
    //-----------------------------------------------------------------------
    public void test_atDay() {
        for (int i = 1; i <= 90; i++) {
            LocalDate expected = LocalDate.of(2012, 1, 1).plusDays(i - 1);
            assertEquals(YearQuarter.of(2012, Q1).atDay(i), expected);
        }
        for (int i = 1; i <= 91; i++) {
            LocalDate expected = LocalDate.of(2012, 4, 1).plusDays(i - 1);
            assertEquals(YearQuarter.of(2012, Q2).atDay(i), expected);
        }
        for (int i = 1; i <= 92; i++) {
            LocalDate expected = LocalDate.of(2012, 7, 1).plusDays(i - 1);
            assertEquals(YearQuarter.of(2012, Q3).atDay(i), expected);
        }
        for (int i = 1; i <= 92; i++) {
            LocalDate expected = LocalDate.of(2012, 10, 1).plusDays(i - 1);
            assertEquals(YearQuarter.of(2012, Q4).atDay(i), expected);
        }
    }

    public void test_atDay_Q1_91_leap() {
        assertEquals(YearQuarter.of(2012, Q1).atDay(91), LocalDate.of(2012, 3, 31));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_atDay_Q1_91_notLeap() {
        YearQuarter.of(2011, Q1).atDay(91);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_atDay_Q1_92() {
        YearQuarter.of(2012, Q1).atDay(92);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_atDay_Q2_92() {
        YearQuarter.of(2012, Q2).atDay(92);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_atDay_tooLow() {
        TEST.atDay(0);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_atDay_tooHigh() {
        TEST.atDay(93);
    }

    //-----------------------------------------------------------------------
    // atEndOfQuarter(int)
    //-----------------------------------------------------------------------
    public void test_atEndOfQuarter() {
        assertEquals(YearQuarter.of(2011, Q1).atEndOfQuarter(), LocalDate.of(2011, 3, 31));
        assertEquals(YearQuarter.of(2011, Q2).atEndOfQuarter(), LocalDate.of(2011, 6, 30));
        assertEquals(YearQuarter.of(2011, Q3).atEndOfQuarter(), LocalDate.of(2011, 9, 30));
        assertEquals(YearQuarter.of(2011, Q4).atEndOfQuarter(), LocalDate.of(2011, 12, 31));

        assertEquals(YearQuarter.of(2012, Q1).atEndOfQuarter(), LocalDate.of(2012, 3, 31));
        assertEquals(YearQuarter.of(2012, Q2).atEndOfQuarter(), LocalDate.of(2012, 6, 30));
        assertEquals(YearQuarter.of(2012, Q3).atEndOfQuarter(), LocalDate.of(2012, 9, 30));
        assertEquals(YearQuarter.of(2012, Q4).atEndOfQuarter(), LocalDate.of(2012, 12, 31));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int year1 = -100; year1 < 100; year1++) {
            for (Quarter quarter1 : Quarter.values()) {
                YearQuarter a = YearQuarter.of(year1, quarter1);
                for (int year2 = -100; year2 < 100; year2++) {
                    for (Quarter quarter2 : Quarter.values()) {
                        YearQuarter b = YearQuarter.of(year2, quarter2);
                        if (year1 < year2) {
                            assertEquals(a.compareTo(b) < 0, true);
                            assertEquals(b.compareTo(a) > 0, true);
                            assertEquals(a.isAfter(b), false);
                            assertEquals(b.isBefore(a), false);
                            assertEquals(b.isAfter(a), true);
                            assertEquals(a.isBefore(b), true);
                        } else if (year1 > year2) {
                            assertEquals(a.compareTo(b) > 0, true);
                            assertEquals(b.compareTo(a) < 0, true);
                            assertEquals(a.isAfter(b), true);
                            assertEquals(b.isBefore(a), true);
                            assertEquals(b.isAfter(a), false);
                            assertEquals(a.isBefore(b), false);
                        } else {
                            if (quarter1.getValue() < quarter2.getValue()) {
                                assertEquals(a.compareTo(b) < 0, true);
                                assertEquals(b.compareTo(a) > 0, true);
                                assertEquals(a.isAfter(b), false);
                                assertEquals(b.isBefore(a), false);
                                assertEquals(b.isAfter(a), true);
                                assertEquals(a.isBefore(b), true);
                            } else if (quarter1.getValue() > quarter2.getValue()) {
                                assertEquals(a.compareTo(b) > 0, true);
                                assertEquals(b.compareTo(a) < 0, true);
                                assertEquals(a.isAfter(b), true);
                                assertEquals(b.isBefore(a), true);
                                assertEquals(b.isAfter(a), false);
                                assertEquals(a.isBefore(b), false);
                            } else {
                                assertEquals(a.compareTo(b), 0);
                                assertEquals(b.compareTo(a), 0);
                                assertEquals(a.isAfter(b), false);
                                assertEquals(b.isBefore(a), false);
                                assertEquals(b.isAfter(a), false);
                                assertEquals(a.isBefore(b), false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_nullYearQuarter() {
        TEST.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int year1 = -100; year1 < 100; year1++) {
            for (Quarter quarter1 : Quarter.values()) {
                YearQuarter a = YearQuarter.of(year1, quarter1);
                for (int year2 = -100; year2 < 100; year2++) {
                    for (Quarter quarter2 : Quarter.values()) {
                        YearQuarter b = YearQuarter.of(year2, quarter2);
                        if (year1 == year2 && quarter1 == quarter2) {
                            assertEquals(a, b);
                            assertEquals(a.hashCode(), b.hashCode());
                        }
                    }
                }
            }
        }
    }

    public void test_equals_nullYearQuarter() {
        assertEquals(TEST.equals(null), false);
    }

    public void test_equals_incorrectType() {
        assertEquals(TEST.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(YearQuarter.of(2012, Q2).toString(), "2012-Q2");
    }

    public void test_toString_bigYear() {
        assertEquals(YearQuarter.of(10000, Q2).toString(), "+10000-Q2");
    }

    public void test_toString_negativeYear() {
        assertEquals(YearQuarter.of(-1, Q2).toString(), "-0001-Q2");
    }

    public void test_toString_negativeBigYear() {
        assertEquals(YearQuarter.of(-10000, Q2).toString(), "-10000-Q2");
    }

}
