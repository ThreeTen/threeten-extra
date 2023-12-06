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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

/**
 * Test YearQuarter.
 */
public class TestYearQuarter {

    private static final YearQuarter TEST = YearQuarter.of(2012, Q2);
    private static final int STANDARD_YEAR_LENGTH = 365;
    private static final int LEAP_YEAR_LENGTH = 366;

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(YearQuarter.class));
        assertTrue(Comparable.class.isAssignableFrom(YearQuarter.class));
        assertTrue(TemporalAdjuster.class.isAssignableFrom(YearQuarter.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(YearQuarter.class));
    }

    @Test
    public void test_serialization() throws IOException, ClassNotFoundException {
        YearQuarter test = YearQuarter.of(2012, 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(test, ois.readObject());
        }
    }

    //-----------------------------------------------------------------------
    // of(Year,Quarter)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_Year_Quarter() {
        for (int year = -100; year <= 100; year++) {
            for (Quarter quarter : Quarter.values()) {
                YearQuarter test = YearQuarter.of(Year.of(year), quarter);
                assertEquals(year, test.getYear());
                assertEquals(quarter.getValue(), test.getQuarterValue());
                assertEquals(quarter, test.getQuarter());
            }
        }
    }

    @Test
    public void test_of_Year_Quarter_nullQuarter() {
        assertThrows(NullPointerException.class, () -> YearQuarter.of(Year.of(2012), (Quarter) null));
    }

    @Test
    public void test_of_Year_Quarter_nullYear() {
        assertThrows(NullPointerException.class, () -> YearQuarter.of((Year) null, Quarter.Q2));
    }

    @Test
    public void test_of_Year_Quarter_nullBoth() {
        assertThrows(NullPointerException.class, () -> YearQuarter.of((Year) null, (Quarter) null));
    }

    //-----------------------------------------------------------------------
    // of(Year,int)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_Year_int() {
        for (int year = -100; year <= 100; year++) {
            for (Quarter quarter : Quarter.values()) {
                YearQuarter test = YearQuarter.of(Year.of(year), quarter.getValue());
                assertEquals(year, test.getYear());
                assertEquals(quarter.getValue(), test.getQuarterValue());
                assertEquals(quarter, test.getQuarter());
            }
        }
    }

    @Test
    public void test_of_Year_int_null() {
        assertThrows(NullPointerException.class, () -> YearQuarter.of((Year) null, 2));
    }

    //-----------------------------------------------------------------------
    // of(int,Quarter)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int_Quarter() {
        for (int year = -100; year <= 100; year++) {
            for (Quarter quarter : Quarter.values()) {
                YearQuarter test = YearQuarter.of(year, quarter);
                assertEquals(year, test.getYear());
                assertEquals(quarter.getValue(), test.getQuarterValue());
                assertEquals(quarter, test.getQuarter());
            }
        }
    }

    @Test
    public void test_of_int_Quarter_yearTooLow() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(Year.MIN_VALUE - 1, Quarter.Q2));
    }

    @Test
    public void test_of_int_Quarter_yearTooHigh() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(Year.MAX_VALUE + 1, Quarter.Q2));
    }

    @Test
    public void test_of_int_Quarter_null() {
        assertThrows(NullPointerException.class, () -> YearQuarter.of(2012, (Quarter) null));
    }

    //-----------------------------------------------------------------------
    // of(int,int)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int_int() {
        for (int year = -100; year <= 100; year++) {
            for (int quarter = 1; quarter <= 4; quarter++) {
                YearQuarter test = YearQuarter.of(year, quarter);
                assertEquals(year, test.getYear());
                assertEquals(quarter, test.getQuarterValue());
                assertEquals(Quarter.of(quarter), test.getQuarter());
                assertEquals(test, YearQuarter.of(year, quarter));
                assertEquals(test.hashCode(), YearQuarter.of(year, quarter).hashCode());
            }
        }
    }

    @Test
    public void test_of_int_int_yearTooLow() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(Year.MIN_VALUE - 1, 1));
    }

    @Test
    public void test_of_int_int_yearTooHigh() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(Year.MAX_VALUE + 1, 1));
    }

    @Test
    public void test_of_int_int_quarterTooLow() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(2012, 0));
    }

    @Test
    public void test_of_int_int_quarterTooHigh() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(2012, 5));
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor_notLeapYear() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            YearQuarter test = YearQuarter.from(date);
            int expected = ((date.getMonthValue() - 1) / 3) + 1;
            assertEquals(YearQuarter.of(2007, expected), test);
            date = date.plusDays(1);
        }
    }

    @Test
    public void test_from_TemporalAccessor_leapYear() {
        LocalDate date = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            YearQuarter test = YearQuarter.from(date);
            int expected = ((date.getMonthValue() - 1) / 3) + 1;
            assertEquals(YearQuarter.of(2008, expected), test);
            date = date.plusDays(1);
        }
    }

    @Test
    public void test_from_TemporalAccessor_noDerive() {
        assertThrows(DateTimeException.class, () -> YearQuarter.from(LocalTime.NOON));
    }

    @Test
    public void test_from_TemporalAccessor_null() {
        assertThrows(NullPointerException.class, () -> YearQuarter.from((TemporalAccessor) null));
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequence() {
        assertEquals(YearQuarter.of(2012, Q3), YearQuarter.parse("2012-Q3"));
    }

    @Test
    public void test_parse_CharSequence_caseInsensitive() {
        assertEquals(YearQuarter.of(2012, Q3), YearQuarter.parse("2012-q3"));
    }

    @Test
    public void test_parse_CharSequenceDate_invalidYear() {
        assertThrows(DateTimeParseException.class, () -> YearQuarter.parse("12345-Q3"));
    }

    @Test
    public void test_parse_CharSequenceDate_invalidQuarter() {
        assertThrows(DateTimeParseException.class, () -> YearQuarter.parse("2012-Q0"));
    }

    @Test
    public void test_parse_CharSequenceDate_nullCharSequence() {
        assertThrows(NullPointerException.class, () -> YearQuarter.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequenceDateTimeFormatter() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("'Q'Q uuuu");
        assertEquals(YearQuarter.of(2012, Q3), YearQuarter.parse("Q3 2012", f));
    }

    @Test
    public void test_parse_CharSequenceDateDateTimeFormatter_invalidQuarter() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("'Q'Q uuuu");
        assertThrows(DateTimeParseException.class, () -> YearQuarter.parse("Q0 2012", f));
    }

    @Test
    public void test_parse_CharSequenceDateTimeFormatter_nullCharSequence() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("'Q'Q uuuu");
        assertThrows(NullPointerException.class, () -> YearQuarter.parse((CharSequence) null, f));
    }

    @Test
    public void test_parse_CharSequenceDateTimeFormatter_nullDateTimeFormatter() {
        assertThrows(NullPointerException.class, () -> YearQuarter.parse("", (DateTimeFormatter) null));
    }

    //-----------------------------------------------------------------------
    // isSupported(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_isSupported_TemporalField() {
        assertEquals(false, TEST.isSupported((TemporalField) null));
        assertEquals(false, TEST.isSupported(NANO_OF_SECOND));
        assertEquals(false, TEST.isSupported(NANO_OF_DAY));
        assertEquals(false, TEST.isSupported(MICRO_OF_SECOND));
        assertEquals(false, TEST.isSupported(MICRO_OF_DAY));
        assertEquals(false, TEST.isSupported(MILLI_OF_SECOND));
        assertEquals(false, TEST.isSupported(MILLI_OF_DAY));
        assertEquals(false, TEST.isSupported(SECOND_OF_MINUTE));
        assertEquals(false, TEST.isSupported(SECOND_OF_DAY));
        assertEquals(false, TEST.isSupported(MINUTE_OF_HOUR));
        assertEquals(false, TEST.isSupported(MINUTE_OF_DAY));
        assertEquals(false, TEST.isSupported(HOUR_OF_AMPM));
        assertEquals(false, TEST.isSupported(CLOCK_HOUR_OF_AMPM));
        assertEquals(false, TEST.isSupported(HOUR_OF_DAY));
        assertEquals(false, TEST.isSupported(CLOCK_HOUR_OF_DAY));
        assertEquals(false, TEST.isSupported(AMPM_OF_DAY));
        assertEquals(false, TEST.isSupported(DAY_OF_WEEK));
        assertEquals(false, TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_MONTH));
        assertEquals(false, TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_YEAR));
        assertEquals(false, TEST.isSupported(DAY_OF_MONTH));
        assertEquals(false, TEST.isSupported(DAY_OF_YEAR));
        assertEquals(false, TEST.isSupported(EPOCH_DAY));
        assertEquals(false, TEST.isSupported(ALIGNED_WEEK_OF_MONTH));
        assertEquals(false, TEST.isSupported(ALIGNED_WEEK_OF_YEAR));
        assertEquals(false, TEST.isSupported(MONTH_OF_YEAR));
        assertEquals(false, TEST.isSupported(PROLEPTIC_MONTH));
        assertEquals(true, TEST.isSupported(YEAR_OF_ERA));
        assertEquals(true, TEST.isSupported(YEAR));
        assertEquals(true, TEST.isSupported(ERA));
        assertEquals(false, TEST.isSupported(INSTANT_SECONDS));
        assertEquals(false, TEST.isSupported(OFFSET_SECONDS));
        assertEquals(true, TEST.isSupported(QUARTER_OF_YEAR));
        assertEquals(false, TEST.isSupported(DAY_OF_QUARTER));
    }

    //-----------------------------------------------------------------------
    // isSupported(TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_isSupported_TemporalUnit() {
        assertEquals(false, TEST.isSupported((TemporalUnit) null));
        assertEquals(false, TEST.isSupported(NANOS));
        assertEquals(false, TEST.isSupported(MICROS));
        assertEquals(false, TEST.isSupported(MILLIS));
        assertEquals(false, TEST.isSupported(SECONDS));
        assertEquals(false, TEST.isSupported(MINUTES));
        assertEquals(false, TEST.isSupported(HOURS));
        assertEquals(false, TEST.isSupported(DAYS));
        assertEquals(false, TEST.isSupported(WEEKS));
        assertEquals(false, TEST.isSupported(MONTHS));
        assertEquals(true, TEST.isSupported(YEARS));
        assertEquals(true, TEST.isSupported(DECADES));
        assertEquals(true, TEST.isSupported(CENTURIES));
        assertEquals(true, TEST.isSupported(MILLENNIA));
        assertEquals(true, TEST.isSupported(ERA));
        assertEquals(false, TEST.isSupported(FOREVER));
        assertEquals(true, TEST.isSupported(QUARTER_YEARS));
    }

    //-----------------------------------------------------------------------
    // range(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_range() {
        assertEquals(QUARTER_OF_YEAR.range(), TEST.range(QUARTER_OF_YEAR));
        assertEquals(YEAR.range(), TEST.range(YEAR));
        assertEquals(ValueRange.of(1, Year.MAX_VALUE), TEST.range(YEAR_OF_ERA));
        assertEquals(ERA.range(), TEST.range(ERA));
    }

    @Test
    public void test_range_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> TEST.range(MONTH_OF_YEAR));
    }

    @Test
    public void test_range_null() {
        assertThrows(NullPointerException.class, () -> TEST.range((TemporalField) null));
    }

    //-----------------------------------------------------------------------
    // get(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(2, TEST.get(QUARTER_OF_YEAR));
        assertEquals(2012, TEST.get(YEAR));
        assertEquals(2012, TEST.get(YEAR_OF_ERA));
        assertEquals(1, TEST.get(ERA));
    }

    @Test
    public void test_get_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> TEST.get(MONTH_OF_YEAR));
    }

    @Test
    public void test_get_null() {
        assertThrows(NullPointerException.class, () -> TEST.get((TemporalField) null));
    }

    //-----------------------------------------------------------------------
    // getLong(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_getLong() {
        assertEquals(2L, TEST.getLong(QUARTER_OF_YEAR));
        assertEquals(2012L, TEST.getLong(YEAR));
        assertEquals(2012L, TEST.getLong(YEAR_OF_ERA));
        assertEquals(1L, TEST.getLong(ERA));
    }

    @Test
    public void test_getLong_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> TEST.getLong(MONTH_OF_YEAR));
    }

    @Test
    public void test_getLong_null() {
        assertThrows(NullPointerException.class, () -> TEST.getLong((TemporalField) null));
    }

    //-----------------------------------------------------------------------
    // isLeapYear(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_int() {
        for (int year = -500; year <= 500; year++) {
            for (Quarter quarter : Quarter.values()) {
                YearQuarter test = YearQuarter.of(year, quarter);
                assertEquals(Year.isLeap(year), test.isLeapYear());
            }
        }
    }

    //-----------------------------------------------------------------------
    // isValidDay(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_isValidDay_int_nonLeap() {
        assertEquals(true, YearQuarter.of(2011, Q1).isValidDay(90));
        assertEquals(false, YearQuarter.of(2011, Q1).isValidDay(91));
        assertEquals(false, YearQuarter.of(2011, Q1).isValidDay(92));

        assertEquals(true, YearQuarter.of(2011, Q2).isValidDay(90));
        assertEquals(true, YearQuarter.of(2011, Q2).isValidDay(91));
        assertEquals(false, YearQuarter.of(2011, Q2).isValidDay(92));

        assertEquals(true, YearQuarter.of(2011, Q3).isValidDay(90));
        assertEquals(true, YearQuarter.of(2011, Q3).isValidDay(91));
        assertEquals(true, YearQuarter.of(2011, Q4).isValidDay(90));

        assertEquals(true, YearQuarter.of(2011, Q3).isValidDay(92));
        assertEquals(true, YearQuarter.of(2011, Q4).isValidDay(91));
        assertEquals(true, YearQuarter.of(2011, Q4).isValidDay(92));
    }

    @Test
    public void test_isValidDay_int_leap() {
        assertEquals(true, YearQuarter.of(2012, Q1).isValidDay(90));
        assertEquals(true, YearQuarter.of(2012, Q1).isValidDay(91));
        assertEquals(false, YearQuarter.of(2012, Q1).isValidDay(92));

        assertEquals(true, YearQuarter.of(2012, Q2).isValidDay(90));
        assertEquals(true, YearQuarter.of(2012, Q2).isValidDay(91));
        assertEquals(false, YearQuarter.of(2012, Q2).isValidDay(92));

        assertEquals(true, YearQuarter.of(2012, Q3).isValidDay(90));
        assertEquals(true, YearQuarter.of(2012, Q3).isValidDay(91));
        assertEquals(true, YearQuarter.of(2012, Q3).isValidDay(92));

        assertEquals(true, YearQuarter.of(2012, Q4).isValidDay(90));
        assertEquals(true, YearQuarter.of(2012, Q4).isValidDay(91));
        assertEquals(true, YearQuarter.of(2012, Q4).isValidDay(92));
    }

    @Test
    public void test_isValidDay_int_outOfRange() {
        assertEquals(false, YearQuarter.of(2011, Q1).isValidDay(93));
        assertEquals(false, YearQuarter.of(2011, Q2).isValidDay(93));
        assertEquals(false, YearQuarter.of(2011, Q3).isValidDay(93));
        assertEquals(false, YearQuarter.of(2011, Q4).isValidDay(93));

        assertEquals(false, YearQuarter.of(2011, Q1).isValidDay(0));
        assertEquals(false, YearQuarter.of(2011, Q2).isValidDay(0));
        assertEquals(false, YearQuarter.of(2011, Q3).isValidDay(0));
        assertEquals(false, YearQuarter.of(2011, Q4).isValidDay(0));
    }

    //-----------------------------------------------------------------------
    // lengthOfQuarter()
    //-----------------------------------------------------------------------
    @Test
    public void test_lengthOfQuarter() {
        for (int year = -500; year <= 500; year++) {
            assertEquals(Year.isLeap(year) ? 91 : 90, YearQuarter.of(year, Q1).lengthOfQuarter());
            assertEquals(91, YearQuarter.of(year, Q2).lengthOfQuarter());
            assertEquals(92, YearQuarter.of(year, Q3).lengthOfQuarter());
            assertEquals(92, YearQuarter.of(year, Q4).lengthOfQuarter());
        }
    }

    //-----------------------------------------------------------------------
    // with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_TemporalAdjuster_Quarter() {
        assertEquals(YearQuarter.of(2007, Q1), YearQuarter.of(2007, Q2).with(Q1));
    }

    @Test
    public void test_with_TemporalAdjuster_Year() {
        assertEquals(YearQuarter.of(2012, Q2), YearQuarter.of(2007, Q2).with(Year.of(2012)));
    }

    @Test
    public void test_with_TemporalAdjuster_YearQuarter() {
        assertEquals(YearQuarter.of(2012, Q3), YearQuarter.of(2007, Q2).with(YearQuarter.of(2012, Q3)));
    }

    @Test
    public void test_with_TemporalAdjuster_LocalDate() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(2007, Q2).with(LocalDate.of(2012, 6, 30)));
    }

    @Test
    public void test_with_TemporalAdjuster_null() {
        assertThrows(NullPointerException.class, () -> YearQuarter.of(2007, Q2).with((TemporalAdjuster) null));
    }

    //-----------------------------------------------------------------------
    // withYear(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_withYear() {
        assertEquals(YearQuarter.of(2012, Q2), YearQuarter.of(2007, Q2).withYear(2012));
    }

    @Test
    public void test_withYear_int_quarterTooLow() {
        assertThrows(DateTimeException.class, () -> TEST.withYear(Year.MIN_VALUE - 1));
    }

    @Test
    public void test_withYear_int_quarterTooHigh() {
        assertThrows(DateTimeException.class, () -> TEST.withYear(Year.MAX_VALUE + 1));
    }

    //-----------------------------------------------------------------------
    // withQuarter(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_withQuarter_int() {
        assertEquals(YearQuarter.of(2007, Q1), YearQuarter.of(2007, Q2).withQuarter(1));
    }

    @Test
    public void test_withQuarter_int_quarterTooLow() {
        assertThrows(DateTimeException.class, () -> TEST.withQuarter(0));
    }

    @Test
    public void test_withQuarter_int_quarterTooHigh() {
        assertThrows(DateTimeException.class, () -> TEST.withQuarter(5));
    }

    //-----------------------------------------------------------------------
    // plus(long,TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_longTemporalUnit() {
        assertEquals(YearQuarter.of(2012, Q2), YearQuarter.of(2007, Q2).plus(5, YEARS));
        assertEquals(YearQuarter.of(2007, Q2), YearQuarter.of(2007, Q2).plus(0, YEARS));
        assertEquals(YearQuarter.of(2002, Q2), YearQuarter.of(2007, Q2).plus(-5, YEARS));
        assertEquals(YearQuarter.of(2008, Q3), YearQuarter.of(2007, Q2).plus(5, QUARTER_YEARS));
        assertEquals(YearQuarter.of(2007, Q2), YearQuarter.of(2007, Q2).plus(0, QUARTER_YEARS));
        assertEquals(YearQuarter.of(2006, Q1), YearQuarter.of(2007, Q2).plus(-5, QUARTER_YEARS));
    }

    //-----------------------------------------------------------------------
    // plusYears(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_plusYears() {
        assertEquals(YearQuarter.of(2012, Q2), YearQuarter.of(2007, Q2).plusYears(5));
        assertEquals(YearQuarter.of(2007, Q2), YearQuarter.of(2007, Q2).plusYears(0));
        assertEquals(YearQuarter.of(2002, Q2), YearQuarter.of(2007, Q2).plusYears(-5));
    }

    //-----------------------------------------------------------------------
    // plusQuarters(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_plusQuarters() {
        assertEquals(YearQuarter.of(2008, Q3), YearQuarter.of(2007, Q2).plusQuarters(5));
        assertEquals(YearQuarter.of(2007, Q2), YearQuarter.of(2007, Q2).plusQuarters(0));
        assertEquals(YearQuarter.of(2006, Q1), YearQuarter.of(2007, Q2).plusQuarters(-5));
    }

    //-----------------------------------------------------------------------
    // minus(long,TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_longTemporalUnit() {
        assertEquals(YearQuarter.of(2002, Q2), YearQuarter.of(2007, Q2).minus(5, YEARS));
        assertEquals(YearQuarter.of(2007, Q2), YearQuarter.of(2007, Q2).minus(0, YEARS));
        assertEquals(YearQuarter.of(2012, Q2), YearQuarter.of(2007, Q2).minus(-5, YEARS));
        assertEquals(YearQuarter.of(2006, Q1), YearQuarter.of(2007, Q2).minus(5, QUARTER_YEARS));
        assertEquals(YearQuarter.of(2007, Q2), YearQuarter.of(2007, Q2).minus(0, QUARTER_YEARS));
        assertEquals(YearQuarter.of(2008, Q3), YearQuarter.of(2007, Q2).minus(-5, QUARTER_YEARS));
    }

    //-----------------------------------------------------------------------
    // minusYears(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_minusYears() {
        assertEquals(YearQuarter.of(2002, Q2), YearQuarter.of(2007, Q2).minusYears(5));
        assertEquals(YearQuarter.of(2007, Q2), YearQuarter.of(2007, Q2).minusYears(0));
        assertEquals(YearQuarter.of(2012, Q2), YearQuarter.of(2007, Q2).minusYears(-5));
    }

    //-----------------------------------------------------------------------
    // minusQuarters(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_minusQuarters() {
        assertEquals(YearQuarter.of(2006, Q1), YearQuarter.of(2007, Q2).minusQuarters(5));
        assertEquals(YearQuarter.of(2007, Q2), YearQuarter.of(2007, Q2).minusQuarters(0));
        assertEquals(YearQuarter.of(2008, Q3), YearQuarter.of(2007, Q2).minusQuarters(-5));
    }

    //-----------------------------------------------------------------------
    // lengthOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_lengthOfYear() {
        for (int year = -500; year <= 500; year++) {
            for (Quarter quarter : Quarter.values()) {
                YearQuarter test = YearQuarter.of(year, quarter);
                assertEquals(Year.isLeap(year) ? 366 : 365, test.lengthOfYear());
            }
        }
    }

    //-----------------------------------------------------------------------
    // query(TemporalQuery)
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertEquals(IsoChronology.INSTANCE, TEST.query(TemporalQueries.chronology()));
        assertEquals(null, TEST.query(TemporalQueries.localDate()));
        assertEquals(null, TEST.query(TemporalQueries.localTime()));
        assertEquals(null, TEST.query(TemporalQueries.offset()));
        assertEquals(QUARTER_YEARS, TEST.query(TemporalQueries.precision()));
        assertEquals(null, TEST.query(TemporalQueries.zone()));
        assertEquals(null, TEST.query(TemporalQueries.zoneId()));
    }

    //-----------------------------------------------------------------------
    // adjustInto(Temporal)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjustInto_Temporal() {
        for (int month = 1; month < 12; month++) {
            for (int dom = 1; dom < 28; dom++) {
                LocalDate base = LocalDate.of(2007, month, dom);
                LocalDate expected = LocalDate.of(2012, 4 + ((month - 1) % 3), dom);
                assertEquals(expected, YearQuarter.of(2012, Q2).adjustInto(base));
            }
        }
    }

    @Test
    public void test_adjustInto_Temporal_lastValidDay_nonLeap() {
        LocalDate base = LocalDate.of(2007, 5, 31);
        LocalDate expected = LocalDate.of(2011, 2, 28);
        assertEquals(expected, YearQuarter.of(2011, Q1).adjustInto(base));
    }

    @Test
    public void test_adjustInto_Temporal_lastValidDay_leap() {
        LocalDate base = LocalDate.of(2007, 5, 31);
        LocalDate expected = LocalDate.of(2012, 2, 29);
        assertEquals(expected, YearQuarter.of(2012, Q1).adjustInto(base));
    }

    @Test
    public void test_adjustInto_Temporal_null() {
        assertThrows(NullPointerException.class, () -> TEST.adjustInto((Temporal) null));
    }

    //-----------------------------------------------------------------------
    // until(Temporal,TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_until_TemporalTemporalUnit_QUARTER_YEARS() {
        assertEquals(-2, YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q4), QUARTER_YEARS));
        assertEquals(-1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q1), QUARTER_YEARS));
        assertEquals(0, YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q2), QUARTER_YEARS));
        assertEquals(1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q3), QUARTER_YEARS));
        assertEquals(2, YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q4), QUARTER_YEARS));
        assertEquals(3, YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q1), QUARTER_YEARS));
        assertEquals(4, YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q2), QUARTER_YEARS));
        assertEquals(5, YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q3), QUARTER_YEARS));
    }

    @Test
    public void test_until_TemporalTemporalUnit_YEARS() {
        assertEquals(-2, YearQuarter.of(2012, Q2).until(YearQuarter.of(2010, Q2), YEARS));
        assertEquals(-1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2010, Q3), YEARS));
        assertEquals(-1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2010, Q4), YEARS));
        assertEquals(-1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q1), YEARS));
        assertEquals(-1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q2), YEARS));
        assertEquals(0, YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q3), YEARS));
        assertEquals(0, YearQuarter.of(2012, Q2).until(YearQuarter.of(2011, Q4), YEARS));
        assertEquals(0, YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q1), YEARS));
        assertEquals(0, YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q2), YEARS));
        assertEquals(0, YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q3), YEARS));
        assertEquals(0, YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q4), YEARS));
        assertEquals(0, YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q1), YEARS));
        assertEquals(1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q2), YEARS));
        assertEquals(1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q3), YEARS));
        assertEquals(1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2013, Q4), YEARS));
        assertEquals(1, YearQuarter.of(2012, Q2).until(YearQuarter.of(2014, Q1), YEARS));
        assertEquals(2, YearQuarter.of(2012, Q2).until(YearQuarter.of(2014, Q2), YEARS));
    }

    @Test
    public void test_until_TemporalTemporalUnit_nullTemporal() {
        assertThrows(NullPointerException.class, () -> YearQuarter.of(2012, Q2).until(null, QUARTER_YEARS));
    }

    @Test
    public void test_until_TemporalTemporalUnit_nullTemporalUnit() {
        assertThrows(NullPointerException.class, () -> YearQuarter.of(2012, Q2).until(YearQuarter.of(2012, Q3), null));
    }

    //-----------------------------------------------------------------------
    // quartersUntil(YearQuarter)
    //-----------------------------------------------------------------------
    @Test
    public void test_quartersUntil_null() {
        assertThrows(NullPointerException.class, () -> YearQuarter.of(2012, Q2).quartersUntil(null));
    }

    @Test
    public void test_quartersUntil_IllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> YearQuarter.of(2012, Q2).quartersUntil(YearQuarter.of(2012, Q1)));
    }

    @Test
    public void test_quartersUntil() {
        assertEquals(2, YearQuarter.of(2012, Q2).quartersUntil(YearQuarter.of(2012, Q4)).count());
        assertEquals(10, YearQuarter.of(2012, Q2).quartersUntil(YearQuarter.of(2014, Q4)).count());

        YearQuarter start = YearQuarter.of(2012, Q1);
        YearQuarter end = YearQuarter.of(2013, Q3);
        Stream<YearQuarter> stream = start.quartersUntil(end);

        List<YearQuarter> expects = Arrays.asList(
                YearQuarter.of(start.getYear(), Q1),
                YearQuarter.of(start.getYear(), Q2),
                YearQuarter.of(start.getYear(), Q3),
                YearQuarter.of(start.getYear(), Q4),
                YearQuarter.of(end.getYear(), Q1),
                YearQuarter.of(end.getYear(), Q2));
        assertEquals(expects, stream.collect(Collectors.toList()));
    }

    //-----------------------------------------------------------------------
    // format(DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void test_format() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("'Q'Q uuuu");
        assertEquals("Q1 2012", YearQuarter.of(2012, Q1).format(f));
    }

    @Test
    public void test_format_null() {
        assertThrows(NullPointerException.class, () -> TEST.format((DateTimeFormatter) null));
    }

    //-----------------------------------------------------------------------
    // atDay(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_atDay() {
        for (int i = 1; i <= 90; i++) {
            LocalDate expected = LocalDate.of(2012, 1, 1).plusDays(i - 1);
            assertEquals(expected, YearQuarter.of(2012, Q1).atDay(i));
        }
        for (int i = 1; i <= 91; i++) {
            LocalDate expected = LocalDate.of(2012, 4, 1).plusDays(i - 1);
            assertEquals(expected, YearQuarter.of(2012, Q2).atDay(i));
        }
        for (int i = 1; i <= 92; i++) {
            LocalDate expected = LocalDate.of(2012, 7, 1).plusDays(i - 1);
            assertEquals(expected, YearQuarter.of(2012, Q3).atDay(i));
        }
        for (int i = 1; i <= 92; i++) {
            LocalDate expected = LocalDate.of(2012, 10, 1).plusDays(i - 1);
            assertEquals(expected, YearQuarter.of(2012, Q4).atDay(i));
        }
    }

    @Test
    public void test_atDay_Q1_91_leap() {
        assertEquals(LocalDate.of(2012, 3, 31), YearQuarter.of(2012, Q1).atDay(91));
    }

    @Test
    public void test_atDay_Q1_91_notLeap() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(2011, Q1).atDay(91));
    }

    @Test
    public void test_atDay_Q1_92() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(2012, Q1).atDay(92));
    }

    @Test
    public void test_atDay_Q2_92() {
        assertThrows(DateTimeException.class, () -> YearQuarter.of(2012, Q2).atDay(92));
    }

    @Test
    public void test_atDay_tooLow() {
        assertThrows(DateTimeException.class, () -> TEST.atDay(0));
    }

    @Test
    public void test_atDay_tooHigh() {
        assertThrows(DateTimeException.class, () -> TEST.atDay(93));
    }

    //-----------------------------------------------------------------------
    // atEndOfQuarter(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_atEndOfQuarter() {
        assertEquals(LocalDate.of(2011, 3, 31), YearQuarter.of(2011, Q1).atEndOfQuarter());
        assertEquals(LocalDate.of(2011, 6, 30), YearQuarter.of(2011, Q2).atEndOfQuarter());
        assertEquals(LocalDate.of(2011, 9, 30), YearQuarter.of(2011, Q3).atEndOfQuarter());
        assertEquals(LocalDate.of(2011, 12, 31), YearQuarter.of(2011, Q4).atEndOfQuarter());

        assertEquals(LocalDate.of(2012, 3, 31), YearQuarter.of(2012, Q1).atEndOfQuarter());
        assertEquals(LocalDate.of(2012, 6, 30), YearQuarter.of(2012, Q2).atEndOfQuarter());
        assertEquals(LocalDate.of(2012, 9, 30), YearQuarter.of(2012, Q3).atEndOfQuarter());
        assertEquals(LocalDate.of(2012, 12, 31), YearQuarter.of(2012, Q4).atEndOfQuarter());
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        for (int year1 = -100; year1 < 100; year1++) {
            for (Quarter quarter1 : Quarter.values()) {
                YearQuarter a = YearQuarter.of(year1, quarter1);
                for (int year2 = -100; year2 < 100; year2++) {
                    for (Quarter quarter2 : Quarter.values()) {
                        YearQuarter b = YearQuarter.of(year2, quarter2);
                        if (year1 < year2) {
                            assertEquals(true, a.compareTo(b) < 0);
                            assertEquals(true, b.compareTo(a) > 0);
                            assertEquals(false, a.isAfter(b));
                            assertEquals(false, b.isBefore(a));
                            assertEquals(true, b.isAfter(a));
                            assertEquals(true, a.isBefore(b));
                        } else if (year1 > year2) {
                            assertEquals(true, a.compareTo(b) > 0);
                            assertEquals(true, b.compareTo(a) < 0);
                            assertEquals(true, a.isAfter(b));
                            assertEquals(true, b.isBefore(a));
                            assertEquals(false, b.isAfter(a));
                            assertEquals(false, a.isBefore(b));
                        } else {
                            if (quarter1.getValue() < quarter2.getValue()) {
                                assertEquals(true, a.compareTo(b) < 0);
                                assertEquals(true, b.compareTo(a) > 0);
                                assertEquals(false, a.isAfter(b));
                                assertEquals(false, b.isBefore(a));
                                assertEquals(true, b.isAfter(a));
                                assertEquals(true, a.isBefore(b));
                            } else if (quarter1.getValue() > quarter2.getValue()) {
                                assertEquals(true, a.compareTo(b) > 0);
                                assertEquals(true, b.compareTo(a) < 0);
                                assertEquals(true, a.isAfter(b));
                                assertEquals(true, b.isBefore(a));
                                assertEquals(false, b.isAfter(a));
                                assertEquals(false, a.isBefore(b));
                            } else {
                                assertEquals(0, a.compareTo(b));
                                assertEquals(0, b.compareTo(a));
                                assertEquals(false, a.isAfter(b));
                                assertEquals(false, b.isBefore(a));
                                assertEquals(false, b.isAfter(a));
                                assertEquals(false, a.isBefore(b));
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void test_compareTo_nullYearQuarter() {
        assertThrows(NullPointerException.class, () -> TEST.compareTo(null));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        EqualsTester tester = new EqualsTester();
        for (int year = -100; year <= 100; year++) {
            for (Quarter quarter : Quarter.values()) {
                YearQuarter instance1 = YearQuarter.of(year, quarter);
                YearQuarter instance2 = YearQuarter.of(year, quarter);
                tester.addEqualityGroup(instance1, instance2);
            }
        }
        tester.testEquals();
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals("2012-Q2", YearQuarter.of(2012, Q2).toString());
    }

    @Test
    public void test_toString_bigYear() {
        assertEquals("+10000-Q2", YearQuarter.of(10000, Q2).toString());
    }

    @Test
    public void test_toString_negativeYear() {
        assertEquals("-0001-Q2", YearQuarter.of(-1, Q2).toString());
    }

    @Test
    public void test_toString_negativeBigYear() {
        assertEquals("-10000-Q2", YearQuarter.of(-10000, Q2).toString());
    }

}
