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
import static org.threeten.extra.Half.H1;
import static org.threeten.extra.Half.H2;
import static org.threeten.extra.TemporalFields.DAY_OF_HALF;
import static org.threeten.extra.TemporalFields.HALF_OF_YEAR;
import static org.threeten.extra.TemporalFields.HALF_YEARS;

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
import java.time.format.DateTimeFormatterBuilder;
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
 * Test YearHalf.
 */
public class TestYearHalf {

    private static final YearHalf TEST = YearHalf.of(2012, H2);
    private static final int STANDARD_YEAR_LENGTH = 365;
    private static final int LEAP_YEAR_LENGTH = 366;

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(YearHalf.class));
        assertTrue(Comparable.class.isAssignableFrom(YearHalf.class));
        assertTrue(TemporalAdjuster.class.isAssignableFrom(YearHalf.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(YearHalf.class));
    }

    @Test
    public void test_serialization() throws IOException, ClassNotFoundException {
        YearHalf test = YearHalf.of(2012, 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(test, ois.readObject());
        }
    }

    //-----------------------------------------------------------------------
    // of(Year,Half)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_Year_Half() {
        for (int year = -100; year <= 100; year++) {
            for (Half half : Half.values()) {
                YearHalf test = YearHalf.of(Year.of(year), half);
                assertEquals(year, test.getYear());
                assertEquals(half.getValue(), test.getHalfValue());
                assertEquals(half, test.getHalf());
            }
        }
    }

    @Test
    public void test_of_Year_Half_nullHalf() {
        assertThrows(NullPointerException.class, () -> YearHalf.of(Year.of(2012), (Half) null));
    }

    @Test
    public void test_of_Year_Half_nullYear() {
        assertThrows(NullPointerException.class, () -> YearHalf.of((Year) null, Half.H2));
    }

    @Test
    public void test_of_Year_Half_nullBoth() {
        assertThrows(NullPointerException.class, () -> YearHalf.of((Year) null, (Half) null));
    }

    //-----------------------------------------------------------------------
    // of(Year,int)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_Year_int() {
        for (int year = -100; year <= 100; year++) {
            for (Half half : Half.values()) {
                YearHalf test = YearHalf.of(Year.of(year), half.getValue());
                assertEquals(year, test.getYear());
                assertEquals(half.getValue(), test.getHalfValue());
                assertEquals(half, test.getHalf());
            }
        }
    }

    @Test
    public void test_of_Year_int_null() {
        assertThrows(NullPointerException.class, () -> YearHalf.of((Year) null, 2));
    }

    //-----------------------------------------------------------------------
    // of(int,Half)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int_Half() {
        for (int year = -100; year <= 100; year++) {
            for (Half half : Half.values()) {
                YearHalf test = YearHalf.of(year, half);
                assertEquals(year, test.getYear());
                assertEquals(half.getValue(), test.getHalfValue());
                assertEquals(half, test.getHalf());
            }
        }
    }

    @Test
    public void test_of_int_Half_yearTooLow() {
        assertThrows(DateTimeException.class, () -> YearHalf.of(Year.MIN_VALUE - 1, Half.H2));
    }

    @Test
    public void test_of_int_Half_yearTooHigh() {
        assertThrows(DateTimeException.class, () -> YearHalf.of(Year.MAX_VALUE + 1, Half.H2));
    }

    @Test
    public void test_of_int_Half_null() {
        assertThrows(NullPointerException.class, () -> YearHalf.of(2012, (Half) null));
    }

    //-----------------------------------------------------------------------
    // of(int,int)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int_int() {
        for (int year = -100; year <= 100; year++) {
            for (int half = 1; half <= 2; half++) {
                YearHalf test = YearHalf.of(year, half);
                assertEquals(year, test.getYear());
                assertEquals(half, test.getHalfValue());
                assertEquals(Half.of(half), test.getHalf());
                assertEquals(test, YearHalf.of(year, half));
                assertEquals(test.hashCode(), YearHalf.of(year, half).hashCode());
            }
        }
    }

    @Test
    public void test_of_int_int_yearTooLow() {
        assertThrows(DateTimeException.class, () -> YearHalf.of(Year.MIN_VALUE - 1, 1));
    }

    @Test
    public void test_of_int_int_yearTooHigh() {
        assertThrows(DateTimeException.class, () -> YearHalf.of(Year.MAX_VALUE + 1, 1));
    }

    @Test
    public void test_of_int_int_halfTooLow() {
        assertThrows(DateTimeException.class, () -> YearHalf.of(2012, 0));
    }

    @Test
    public void test_of_int_int_halfTooHigh() {
        assertThrows(DateTimeException.class, () -> YearHalf.of(2012, 3));
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor_notLeapYear() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            YearHalf test = YearHalf.from(date);
            int expected = ((date.getMonthValue() - 1) / 6) + 1;
            assertEquals(YearHalf.of(2007, expected), test);
            date = date.plusDays(1);
        }
    }

    @Test
    public void test_from_TemporalAccessor_leapYear() {
        LocalDate date = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            YearHalf test = YearHalf.from(date);
            int expected = ((date.getMonthValue() - 1) / 6) + 1;
            assertEquals(YearHalf.of(2008, expected), test);
            date = date.plusDays(1);
        }
    }

    @Test
    public void test_from_TemporalAccessor_noDerive() {
        assertThrows(DateTimeException.class, () -> YearHalf.from(LocalTime.NOON));
    }

    @Test
    public void test_from_TemporalAccessor_null() {
        assertThrows(NullPointerException.class, () -> YearHalf.from((TemporalAccessor) null));
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequence() {
        assertEquals(YearHalf.of(2012, H2), YearHalf.parse("2012-H2"));
    }

    @Test
    public void test_parse_CharSequence_caseInsensitive() {
        assertEquals(YearHalf.of(2012, H1), YearHalf.parse("2012-h1"));
    }

    @Test
    public void test_parse_CharSequenceDate_invalidYear() {
        assertThrows(DateTimeParseException.class, () -> YearHalf.parse("12345-H1"));
    }

    @Test
    public void test_parse_CharSequenceDate_invalidHalf() {
        assertThrows(DateTimeParseException.class, () -> YearHalf.parse("2012-H0"));
    }

    @Test
    public void test_parse_CharSequenceDate_nullCharSequence() {
        assertThrows(NullPointerException.class, () -> YearHalf.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequenceDateTimeFormatter() {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
                .appendLiteral('H')
                .appendValue(HALF_OF_YEAR, 1)
                .appendLiteral(' ')
                .appendValue(YEAR)
                .toFormatter();
        assertEquals(YearHalf.of(2012, H1), YearHalf.parse("H1 2012", f));
    }

    @Test
    public void test_parse_CharSequenceDateDateTimeFormatter_invalidHalf() {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
                .appendLiteral('H')
                .appendValue(HALF_OF_YEAR, 1)
                .appendLiteral(' ')
                .appendValue(YEAR)
                .toFormatter();
        assertThrows(DateTimeParseException.class, () -> YearHalf.parse("H0 2012", f));
    }

    @Test
    public void test_parse_CharSequenceDateTimeFormatter_nullCharSequence() {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
                .appendLiteral('H')
                .appendValue(HALF_OF_YEAR, 1)
                .appendLiteral(' ')
                .appendValue(YEAR)
                .toFormatter();
        assertThrows(NullPointerException.class, () -> YearHalf.parse((CharSequence) null, f));
    }

    @Test
    public void test_parse_CharSequenceDateTimeFormatter_nullDateTimeFormatter() {
        assertThrows(NullPointerException.class, () -> YearHalf.parse("", (DateTimeFormatter) null));
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
        assertEquals(false, TEST.isSupported(QUARTER_OF_YEAR));
        assertEquals(false, TEST.isSupported(DAY_OF_QUARTER));
        assertEquals(true, TEST.isSupported(HALF_OF_YEAR));
        assertEquals(false, TEST.isSupported(DAY_OF_HALF));
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
        assertEquals(false, TEST.isSupported(QUARTER_YEARS));
        assertEquals(true, TEST.isSupported(HALF_YEARS));
    }

    //-----------------------------------------------------------------------
    // range(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_range() {
        assertEquals(HALF_OF_YEAR.range(), TEST.range(HALF_OF_YEAR));
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
        assertEquals(2, TEST.get(HALF_OF_YEAR));
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
        assertEquals(2L, TEST.getLong(HALF_OF_YEAR));
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
            for (Half half : Half.values()) {
                YearHalf test = YearHalf.of(year, half);
                assertEquals(Year.isLeap(year), test.isLeapYear());
            }
        }
    }

    //-----------------------------------------------------------------------
    // isValidDay(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_isValidDay_int_nonLeap() {
        assertEquals(true, YearHalf.of(2011, H1).isValidDay(181));
        assertEquals(false, YearHalf.of(2011, H1).isValidDay(182));
        assertEquals(false, YearHalf.of(2011, H1).isValidDay(183));

        assertEquals(true, YearHalf.of(2011, H2).isValidDay(183));
        assertEquals(true, YearHalf.of(2011, H2).isValidDay(184));
        assertEquals(false, YearHalf.of(2011, H2).isValidDay(185));
    }

    @Test
    public void test_isValidDay_int_leap() {
        assertEquals(true, YearHalf.of(2012, H1).isValidDay(181));
        assertEquals(true, YearHalf.of(2012, H1).isValidDay(182));
        assertEquals(false, YearHalf.of(2012, H1).isValidDay(183));

        assertEquals(true, YearHalf.of(2012, H2).isValidDay(183));
        assertEquals(true, YearHalf.of(2012, H2).isValidDay(184));
        assertEquals(false, YearHalf.of(2012, H2).isValidDay(185));
    }

    @Test
    public void test_isValidDay_int_outOfRange() {
        assertEquals(false, YearHalf.of(2011, H1).isValidDay(185));
        assertEquals(false, YearHalf.of(2011, H2).isValidDay(185));

        assertEquals(false, YearHalf.of(2011, H1).isValidDay(0));
        assertEquals(false, YearHalf.of(2011, H2).isValidDay(0));
    }

    //-----------------------------------------------------------------------
    // lengthOfHalf()
    //-----------------------------------------------------------------------
    @Test
    public void test_lengthOfHalf() {
        for (int year = -500; year <= 500; year++) {
            assertEquals(Year.isLeap(year) ? 182 : 181, YearHalf.of(year, H1).lengthOfHalf());
            assertEquals(184, YearHalf.of(year, H2).lengthOfHalf());
        }
    }

    //-----------------------------------------------------------------------
    // with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_TemporalAdjuster_Half() {
        assertEquals(YearHalf.of(2007, H1), YearHalf.of(2007, H2).with(H1));
    }

    @Test
    public void test_with_TemporalAdjuster_Year() {
        assertEquals(YearHalf.of(2012, H2), YearHalf.of(2007, H2).with(Year.of(2012)));
    }

    @Test
    public void test_with_TemporalAdjuster_YearHalf() {
        assertEquals(YearHalf.of(2012, H1), YearHalf.of(2007, H2).with(YearHalf.of(2012, H1)));
    }

    @Test
    public void test_with_TemporalAdjuster_LocalDate() {
        assertThrows(DateTimeException.class, () -> YearHalf.of(2007, H2).with(LocalDate.of(2012, 6, 30)));
    }

    @Test
    public void test_with_TemporalAdjuster_null() {
        assertThrows(NullPointerException.class, () -> YearHalf.of(2007, H2).with((TemporalAdjuster) null));
    }

    //-----------------------------------------------------------------------
    // withYear(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_withYear() {
        assertEquals(YearHalf.of(2012, H2), YearHalf.of(2007, H2).withYear(2012));
    }

    @Test
    public void test_withYear_int_halfTooLow() {
        assertThrows(DateTimeException.class, () -> TEST.withYear(Year.MIN_VALUE - 1));
    }

    @Test
    public void test_withYear_int_halfTooHigh() {
        assertThrows(DateTimeException.class, () -> TEST.withYear(Year.MAX_VALUE + 1));
    }

    //-----------------------------------------------------------------------
    // withHalf(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_withHalf_int() {
        assertEquals(YearHalf.of(2007, H1), YearHalf.of(2007, H2).withHalf(1));
    }

    @Test
    public void test_withHalf_int_halfTooLow() {
        assertThrows(DateTimeException.class, () -> TEST.withHalf(0));
    }

    @Test
    public void test_withHalf_int_halfTooHigh() {
        assertThrows(DateTimeException.class, () -> TEST.withHalf(3));
    }

    //-----------------------------------------------------------------------
    // plus(long,TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_longTemporalUnit() {
        assertEquals(YearHalf.of(2012, H2), YearHalf.of(2007, H2).plus(5, YEARS));
        assertEquals(YearHalf.of(2007, H2), YearHalf.of(2007, H2).plus(0, YEARS));
        assertEquals(YearHalf.of(2002, H2), YearHalf.of(2007, H2).plus(-5, YEARS));
        assertEquals(YearHalf.of(2010, H1), YearHalf.of(2007, H2).plus(5, HALF_YEARS));
        assertEquals(YearHalf.of(2007, H2), YearHalf.of(2007, H2).plus(0, HALF_YEARS));
        assertEquals(YearHalf.of(2005, H1), YearHalf.of(2007, H2).plus(-5, HALF_YEARS));
    }

    //-----------------------------------------------------------------------
    // plusYears(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_plusYears() {
        assertEquals(YearHalf.of(2012, H2), YearHalf.of(2007, H2).plusYears(5));
        assertEquals(YearHalf.of(2007, H2), YearHalf.of(2007, H2).plusYears(0));
        assertEquals(YearHalf.of(2002, H2), YearHalf.of(2007, H2).plusYears(-5));
    }

    //-----------------------------------------------------------------------
    // plusHalves(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_plusHalves() {
        assertEquals(YearHalf.of(2010, H1), YearHalf.of(2007, H2).plusHalves(5));
        assertEquals(YearHalf.of(2007, H2), YearHalf.of(2007, H2).plusHalves(0));
        assertEquals(YearHalf.of(2005, H1), YearHalf.of(2007, H2).plusHalves(-5));
    }

    //-----------------------------------------------------------------------
    // minus(long,TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_longTemporalUnit() {
        assertEquals(YearHalf.of(2002, H2), YearHalf.of(2007, H2).minus(5, YEARS));
        assertEquals(YearHalf.of(2007, H2), YearHalf.of(2007, H2).minus(0, YEARS));
        assertEquals(YearHalf.of(2012, H2), YearHalf.of(2007, H2).minus(-5, YEARS));
        assertEquals(YearHalf.of(2005, H1), YearHalf.of(2007, H2).minus(5, HALF_YEARS));
        assertEquals(YearHalf.of(2007, H2), YearHalf.of(2007, H2).minus(0, HALF_YEARS));
        assertEquals(YearHalf.of(2010, H1), YearHalf.of(2007, H2).minus(-5, HALF_YEARS));
    }

    //-----------------------------------------------------------------------
    // minusYears(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_minusYears() {
        assertEquals(YearHalf.of(2002, H2), YearHalf.of(2007, H2).minusYears(5));
        assertEquals(YearHalf.of(2007, H2), YearHalf.of(2007, H2).minusYears(0));
        assertEquals(YearHalf.of(2012, H2), YearHalf.of(2007, H2).minusYears(-5));
    }

    //-----------------------------------------------------------------------
    // minusHalves(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_minusHalves() {
        assertEquals(YearHalf.of(2005, H1), YearHalf.of(2007, H2).minusHalves(5));
        assertEquals(YearHalf.of(2007, H2), YearHalf.of(2007, H2).minusHalves(0));
        assertEquals(YearHalf.of(2010, H1), YearHalf.of(2007, H2).minusHalves(-5));
    }

    //-----------------------------------------------------------------------
    // lengthOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_lengthOfYear() {
        for (int year = -500; year <= 500; year++) {
            for (Half half : Half.values()) {
                YearHalf test = YearHalf.of(year, half);
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
        assertEquals(HALF_YEARS, TEST.query(TemporalQueries.precision()));
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
                LocalDate expected = LocalDate.of(2012, 7 + ((month - 1) % 6), dom);
                assertEquals(expected, YearHalf.of(2012, H2).adjustInto(base));
            }
        }
    }

    @Test
    public void test_adjustInto_Temporal_lastValidDay_nonLeap() {
        LocalDate base = LocalDate.of(2007, 8, 31);
        LocalDate expected = LocalDate.of(2011, 2, 28);
        assertEquals(expected, YearHalf.of(2011, H1).adjustInto(base));
    }

    @Test
    public void test_adjustInto_Temporal_lastValidDay_leap() {
        LocalDate base = LocalDate.of(2007, 8, 31);
        LocalDate expected = LocalDate.of(2012, 2, 29);
        assertEquals(expected, YearHalf.of(2012, H1).adjustInto(base));
    }

    @Test
    public void test_adjustInto_Temporal_null() {
        assertThrows(NullPointerException.class, () -> TEST.adjustInto((Temporal) null));
    }

    //-----------------------------------------------------------------------
    // until(Temporal,TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_until_TemporalTemporalUnit_HALF_YEARS() {
        assertEquals(-2, YearHalf.of(2012, H2).until(YearHalf.of(2011, H2), HALF_YEARS));
        assertEquals(-1, YearHalf.of(2012, H2).until(YearHalf.of(2012, H1), HALF_YEARS));
        assertEquals(0, YearHalf.of(2012, H2).until(YearHalf.of(2012, H2), HALF_YEARS));
        assertEquals(1, YearHalf.of(2012, H2).until(YearHalf.of(2013, H1), HALF_YEARS));
        assertEquals(2, YearHalf.of(2012, H2).until(YearHalf.of(2013, H2), HALF_YEARS));
        assertEquals(3, YearHalf.of(2012, H2).until(YearHalf.of(2014, H1), HALF_YEARS));
    }

    @Test
    public void test_until_TemporalTemporalUnit_YEARS() {
        assertEquals(-2, YearHalf.of(2012, H2).until(YearHalf.of(2010, H2), YEARS));
        assertEquals(-1, YearHalf.of(2012, H2).until(YearHalf.of(2011, H1), YEARS));
        assertEquals(-1, YearHalf.of(2012, H2).until(YearHalf.of(2011, H2), YEARS));
        assertEquals(0, YearHalf.of(2012, H2).until(YearHalf.of(2012, H1), YEARS));
        assertEquals(0, YearHalf.of(2012, H2).until(YearHalf.of(2012, H2), YEARS));
        assertEquals(0, YearHalf.of(2012, H2).until(YearHalf.of(2013, H1), YEARS));
        assertEquals(1, YearHalf.of(2012, H2).until(YearHalf.of(2013, H2), YEARS));
        assertEquals(1, YearHalf.of(2012, H2).until(YearHalf.of(2014, H1), YEARS));
        assertEquals(2, YearHalf.of(2012, H2).until(YearHalf.of(2014, H2), YEARS));
    }

    @Test
    public void test_until_TemporalTemporalUnit_nullTemporal() {
        assertThrows(NullPointerException.class, () -> YearHalf.of(2012, H2).until(null, HALF_YEARS));
    }

    @Test
    public void test_until_TemporalTemporalUnit_nullTemporalUnit() {
        assertThrows(NullPointerException.class, () -> YearHalf.of(2012, H2).until(YearHalf.of(2012, H1), null));
    }

    //-----------------------------------------------------------------------
    // halvesUntil(YearHalf)
    //-----------------------------------------------------------------------
    @Test
    public void test_halvesUntil_null() {
        assertThrows(NullPointerException.class, () -> YearHalf.of(2012, H2).halvesUntil(null));
    }

    @Test
    public void test_halvesUntil_IllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> YearHalf.of(2012, H2).halvesUntil(YearHalf.of(2012, H1)));
    }

    @Test
    public void test_halvesUntil() {
        assertEquals(1, YearHalf.of(2012, H1).halvesUntil(YearHalf.of(2012, H2)).count());
        assertEquals(4, YearHalf.of(2012, H2).halvesUntil(YearHalf.of(2014, H2)).count());

        YearHalf start = YearHalf.of(2012, H1);
        YearHalf end = YearHalf.of(2013, H2);
        Stream<YearHalf> stream = start.halvesUntil(end);

        List<YearHalf> expects = Arrays.asList(
                YearHalf.of(start.getYear(), H1),
                YearHalf.of(start.getYear(), H2),
                YearHalf.of(end.getYear(), H1));
        assertEquals(expects, stream.collect(Collectors.toList()));
    }

    //-----------------------------------------------------------------------
    // format(DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void test_format() {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
                .appendLiteral('H')
                .appendValue(HALF_OF_YEAR, 1)
                .appendLiteral(' ')
                .appendValue(YEAR)
                .toFormatter();
        assertEquals("H1 2012", YearHalf.of(2012, H1).format(f));
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
        for (int i = 1; i <= 182; i++) {
            LocalDate expected = LocalDate.of(2012, 1, 1).plusDays(i - 1);
            assertEquals(expected, YearHalf.of(2012, H1).atDay(i));
        }
        for (int i = 1; i <= 184; i++) {
            LocalDate expected = LocalDate.of(2012, 7, 1).plusDays(i - 1);
            assertEquals(expected, YearHalf.of(2012, H2).atDay(i));
        }
    }

    @Test
    public void test_atDay_H1_181_leap() {
        assertEquals(LocalDate.of(2012, 6, 30), YearHalf.of(2012, H1).atDay(182));
    }

    @Test
    public void test_atDay_H1_182_notLeap() {
        assertThrows(DateTimeException.class, () -> YearHalf.of(2011, H1).atDay(182));
    }

    @Test
    public void test_atDay_H1_183() {
        assertThrows(DateTimeException.class, () -> YearHalf.of(2012, H1).atDay(183));
    }

    @Test
    public void test_atDay_tooLow() {
        assertThrows(DateTimeException.class, () -> TEST.atDay(0));
    }

    @Test
    public void test_atDay_tooHigh() {
        assertThrows(DateTimeException.class, () -> TEST.atDay(185));
    }

    //-----------------------------------------------------------------------
    // atEndOfHalf(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_atEndOfHalf() {
        assertEquals(LocalDate.of(2011, 6, 30), YearHalf.of(2011, H1).atEndOfHalf());
        assertEquals(LocalDate.of(2011, 12, 31), YearHalf.of(2011, H2).atEndOfHalf());

        assertEquals(LocalDate.of(2012, 6, 30), YearHalf.of(2012, H1).atEndOfHalf());
        assertEquals(LocalDate.of(2012, 12, 31), YearHalf.of(2012, H2).atEndOfHalf());
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        for (int year1 = -100; year1 < 100; year1++) {
            for (Half half1 : Half.values()) {
                YearHalf a = YearHalf.of(year1, half1);
                for (int year2 = -100; year2 < 100; year2++) {
                    for (Half half2 : Half.values()) {
                        YearHalf b = YearHalf.of(year2, half2);
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
                            if (half1.getValue() < half2.getValue()) {
                                assertEquals(true, a.compareTo(b) < 0);
                                assertEquals(true, b.compareTo(a) > 0);
                                assertEquals(false, a.isAfter(b));
                                assertEquals(false, b.isBefore(a));
                                assertEquals(true, b.isAfter(a));
                                assertEquals(true, a.isBefore(b));
                            } else if (half1.getValue() > half2.getValue()) {
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
    public void test_compareTo_nullYearHalf() {
        assertThrows(NullPointerException.class, () -> TEST.compareTo(null));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        EqualsTester tester = new EqualsTester();
        for (int year = -100; year <= 100; year++) {
            for (Half half : Half.values()) {
                YearHalf instance1 = YearHalf.of(year, half);
                YearHalf instance2 = YearHalf.of(year, half);
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
        assertEquals("2012-H2", YearHalf.of(2012, H2).toString());
    }

    @Test
    public void test_toString_bigYear() {
        assertEquals("+10000-H2", YearHalf.of(10000, H2).toString());
    }

    @Test
    public void test_toString_negativeYear() {
        assertEquals("-0001-H2", YearHalf.of(-1, H2).toString());
    }

    @Test
    public void test_toString_negativeBigYear() {
        assertEquals("-10000-H2", YearHalf.of(-10000, H2).toString());
    }

}
