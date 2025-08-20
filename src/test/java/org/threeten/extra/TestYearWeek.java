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

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
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
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MICROS;
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
import static java.time.temporal.IsoFields.WEEK_BASED_YEAR;
import static java.time.temporal.IsoFields.WEEK_BASED_YEARS;
import static java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.IsoChronology;
import java.time.chrono.ThaiBuddhistDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.RetryingTest;

import com.google.common.testing.EqualsTester;

public class TestYearWeek {

    private static final YearWeek TEST_NON_LEAP = YearWeek.of(2014, 1);
    private static final YearWeek TEST = YearWeek.of(2015, 1);

    public static Object[][] data_sampleYearWeeks() {
        return new Object[][]{
            {2015, 1},
            {2015, 2},
            {2015, 3},
            {2015, 4},
            {2015, 5},
            {2015, 6},
            {2015, 7},
            {2015, 8},
            {2015, 9},
            {2015, 10},
            {2015, 11},
            {2015, 12},
            {2015, 13},
            {2015, 14},
            {2015, 15},
            {2015, 16},
            {2015, 17},
            {2015, 18},
            {2015, 19},
            {2015, 20},
            {2015, 21},
            {2015, 22},
            {2015, 21},
            {2015, 22},
            {2015, 23},
            {2015, 23},
            {2015, 24},
            {2015, 25},
            {2015, 26},
            {2015, 27},
            {2015, 28},
            {2015, 29},
            {2015, 30},
            {2015, 31},
            {2015, 32},
            {2015, 33},
            {2015, 34},
            {2015, 35},
            {2015, 36},
            {2015, 37},
            {2015, 38},
            {2015, 39},
            {2015, 40},
            {2015, 41},
            {2015, 42},
            {2015, 43},
            {2015, 44},
            {2015, 45},
            {2015, 46},
            {2015, 47},
            {2015, 48},
            {2015, 49},
            {2015, 50},
            {2015, 51},
            {2015, 52},
            {2015, 53}
        };
    }

    public static Object[][] data_53WeekYear() {
        return new Object[][]{
            {4},
            {9},
            {15},
            {20},
            {26},
            {32},
            {37},
            {43},
            {48},
            {54},
            {60},
            {65},
            {71},
            {76},
            {82},
            {88},
            {93},
            {99},
            {105},
            {111},
            {116},
            {122},
            {128},
            {133},
            {139},
            {144},
            {150},
            {156},
            {161},
            {167},
            {172},
            {178},
            {184},
            {189},
            {195},
            {201},
            {207},
            {212},
            {218},
            {224},
            {229},
            {235},
            {240},
            {246},
            {252},
            {257},
            {263},
            {268},
            {274},
            {280},
            {285},
            {291},
            {296},
            {303},
            {308},
            {314},
            {320},
            {325},
            {331},
            {336},
            {342},
            {348},
            {353},
            {359},
            {364},
            {370},
            {376},
            {381},
            {387},
            {392},
            {398}
        };
    }

    public static Object[][] data_sampleAtDay() {
        return new Object[][]{
            {2014, 52, MONDAY,    2014, 12, 22},
            {2014, 52, TUESDAY,   2014, 12, 23},
            {2014, 52, WEDNESDAY, 2014, 12, 24},
            {2014, 52, THURSDAY,  2014, 12, 25},
            {2014, 52, FRIDAY,    2014, 12, 26},
            {2014, 52, SATURDAY,  2014, 12, 27},
            {2014, 52, SUNDAY,    2014, 12, 28},
            {2015,  1, MONDAY,    2014, 12, 29},
            {2015,  1, TUESDAY,   2014, 12, 30},
            {2015,  1, WEDNESDAY, 2014, 12, 31},
            {2015,  1, THURSDAY,  2015,  1,  1},
            {2015,  1, FRIDAY,    2015,  1,  2},
            {2015,  1, SATURDAY,  2015,  1,  3},
            {2015,  1, SUNDAY,    2015,  1,  4},
            {2015, 53, FRIDAY,    2016,  1,  1},
            {2015, 53, SATURDAY,  2016,  1,  2},
            {2015, 53, SUNDAY,    2016,  1,  3},
            {2016,  1, MONDAY,    2016,  1,  4},
            {2016, 52, SUNDAY,    2017,  1,  1},
            {2017,  1, MONDAY,    2017,  1,  2},
            {2017,  1, TUESDAY,   2017,  1,  3},
            {2017,  1, WEDNESDAY, 2017,  1,  4},
            {2017,  1, THURSDAY,  2017,  1,  5},
            {2017,  1, FRIDAY,    2017,  1,  6},
            {2017,  1, SATURDAY,  2017,  1,  7},
            {2017,  1, SUNDAY,    2017,  1,  8},
            {2025,  1, MONDAY,    2024, 12, 30},
        };
    }

    public static Object[][] data_outOfBounds() {
    	return new Object[][] {
    		{IsoFields.WEEK_OF_WEEK_BASED_YEAR, 54},
    		{IsoFields.WEEK_OF_WEEK_BASED_YEAR, 0},
    		{IsoFields.WEEK_BASED_YEAR, 1000000000},
    		{IsoFields.WEEK_BASED_YEAR, -1000000000},
    	};
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(YearWeek.class));
        assertTrue(Comparable.class.isAssignableFrom(YearWeek.class));
        assertTrue(TemporalAdjuster.class.isAssignableFrom(YearWeek.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(YearWeek.class));
    }

    @Test
    public void test_serialization() throws IOException, ClassNotFoundException {
        YearWeek test = YearWeek.of(2015, 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(test, ois.readObject());
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @RetryingTest(100)
    public void test_now() {
        assertEquals(YearWeek.now(Clock.systemDefaultZone()), YearWeek.now());
    }

    //-----------------------------------------------------------------------
    // now(ZoneId)
    //-----------------------------------------------------------------------
    @Test
    public void now_ZoneId_nullZoneId() {
        assertThrows(NullPointerException.class, () -> YearWeek.now((ZoneId) null));
    }

    @RetryingTest(100)
    public void now_ZoneId() {
        ZoneId zone = ZoneId.of("UTC+01:02:03");
        assertEquals(YearWeek.now(Clock.system(zone)), YearWeek.now(zone));
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test
    public void now_Clock() {
        Instant instant = LocalDateTime.of(2010, 12, 31, 0, 0).toInstant(ZoneOffset.UTC);
        Clock clock = Clock.fixed(instant, ZoneOffset.UTC);
        YearWeek test = YearWeek.now(clock);
        assertEquals(2010, test.getYear());
        assertEquals(52, test.getWeek());
    }

    @Test
    public void now_Clock_nullClock() {
        assertThrows(NullPointerException.class, () -> YearWeek.now((Clock) null));
    }

    //-----------------------------------------------------------------------
    // of(Year, int)
    //-----------------------------------------------------------------------
    @ParameterizedTest
    @MethodSource("data_sampleYearWeeks")
    public void test_of_Year_int(int year, int week) {
        YearWeek yearWeek = YearWeek.of(Year.of(year), week);
        assertEquals(year, yearWeek.getYear());
        assertEquals(week, yearWeek.getWeek());
    }

    @Test
    public void test_carry_Year_int() {
        assertTrue(YearWeek.of(Year.of(2014), 53).equals(TEST));
    }

    //-----------------------------------------------------------------------
    // of(int, int)
    //-----------------------------------------------------------------------
    @ParameterizedTest
    @MethodSource("data_sampleYearWeeks")
    public void test_of(int year, int week) {
        YearWeek yearWeek = YearWeek.of(year, week);
        assertEquals(year, yearWeek.getYear());
        assertEquals(week, yearWeek.getWeek());
    }

    @Test
    public void test_carry() {
        assertTrue(YearWeek.of(2014, 53).equals(TEST));
    }

    @Test
    public void test_of_year_tooLow() {
        assertThrows(DateTimeException.class, () -> YearWeek.of(Integer.MIN_VALUE, 1));
    }

    @Test
    public void test_of_year_tooHigh() {
        assertThrows(DateTimeException.class, () -> YearWeek.of(Integer.MAX_VALUE, 1));
    }

    @Test
    public void test_of_invalidWeekValue() {
        assertThrows(DateTimeException.class, () -> YearWeek.of(2015, 54));
    }

    @Test
    public void test_of_invalidWeekValueZero() {
        assertThrows(DateTimeException.class, () -> YearWeek.of(2015, 0));
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
        assertEquals(false, TEST.isSupported(YEAR_OF_ERA));
        assertEquals(false, TEST.isSupported(YEAR));
        assertEquals(false, TEST.isSupported(ERA));
        assertEquals(false, TEST.isSupported(INSTANT_SECONDS));
        assertEquals(false, TEST.isSupported(OFFSET_SECONDS));
        assertEquals(false, TEST.isSupported(QUARTER_OF_YEAR));
        assertEquals(false, TEST.isSupported(DAY_OF_QUARTER));
        assertEquals(true, TEST.isSupported(WEEK_BASED_YEAR));
        assertEquals(true, TEST.isSupported(WEEK_OF_WEEK_BASED_YEAR));
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
        assertEquals(true, TEST.isSupported(WEEKS));
        assertEquals(false, TEST.isSupported(MONTHS));
        assertEquals(false, TEST.isSupported(YEARS));
        assertEquals(false, TEST.isSupported(CENTURIES));
        assertEquals(true, TEST.isSupported(WEEK_BASED_YEARS));
        assertEquals(false, TEST.isSupported(QUARTER_YEARS));
    }

    //-----------------------------------------------------------------------
    // atDay(DayOfWeek)
    //-----------------------------------------------------------------------
    @ParameterizedTest
    @MethodSource("data_sampleAtDay")
    public void test_atDay(int weekBasedYear, int weekOfWeekBasedYear, DayOfWeek dayOfWeek, int year, int month, int dayOfMonth) {
        YearWeek yearWeek = YearWeek.of(weekBasedYear, weekOfWeekBasedYear);
        LocalDate expected = LocalDate.of(year, month, dayOfMonth);
        LocalDate actual = yearWeek.atDay(dayOfWeek);
        assertEquals(expected, actual);
    }

    @Test
    public void test_atDay_loop20years() {
        YearWeek yearWeek = YearWeek.of(1998, 51);
        LocalDate expected = LocalDate.of(1998, 12, 14);
        for (int i = 0; i < (20 * 53); i++) {
            for (int j = 1; j <= 7; j++) {
                DayOfWeek dow = DayOfWeek.of(j);
                LocalDate actual = yearWeek.atDay(dow);
                assertEquals(expected, actual);
                expected = expected.plusDays(1);
            }
            yearWeek = yearWeek.plusWeeks(1);
        }
    }

    @Test
    public void test_atDay_null() {
        assertThrows(NullPointerException.class, () -> TEST.atDay(null));
    }

    //-----------------------------------------------------------------------
    // is53WeekYear()
    //-----------------------------------------------------------------------
    @ParameterizedTest
    @MethodSource("data_53WeekYear")
    public void test_is53WeekYear(int year) {
        YearWeek yearWeek = YearWeek.of(year, 1);
        assertTrue(yearWeek.is53WeekYear());
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        for (int year1 = -100; year1 < 100; year1++) {
            for (int week1 = 1; week1 < 53; week1++) {
                YearWeek a = YearWeek.of(year1, week1);
                for (int year2 = -100; year2 < 100; year2++) {
                    for (int week2 = 1; week2 < 53; week2++) {
                        YearWeek b = YearWeek.of(year2, week2);
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
                            if (week1 < week2) {
                                assertEquals(true, a.compareTo(b) < 0);
                                assertEquals(true, b.compareTo(a) > 0);
                                assertEquals(false, a.isAfter(b));
                                assertEquals(false, b.isBefore(a));
                                assertEquals(true, b.isAfter(a));
                                assertEquals(true, a.isBefore(b));
                            } else if (week1 > week2) {
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
    public void test_compareTo_nullYearWeek() {
        assertThrows(NullPointerException.class, () -> TEST.compareTo(null));
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @ParameterizedTest
    @MethodSource("data_sampleAtDay")
    public void test_from(int weekBasedYear, int weekOfWeekBasedYear, DayOfWeek dayOfWeek, int year, int month, int dayOfMonth) {
        YearWeek expected = YearWeek.of(weekBasedYear, weekOfWeekBasedYear);
        LocalDate ld = LocalDate.of(year, month, dayOfMonth);
        assertEquals(expected, YearWeek.from(ld));
        assertEquals(expected, YearWeek.from(ThaiBuddhistDate.from(ld)));
        assertEquals(expected, YearWeek.from(expected));
    }

    @Test
    public void test_from_TemporalAccessor_noDerive() {
        assertThrows(DateTimeException.class, () -> YearWeek.from(LocalTime.NOON));
    }

    @Test
    public void test_from_TemporalAccessor_null() {
        assertThrows(NullPointerException.class, () -> YearWeek.from((TemporalAccessor) null));
    }

    //-----------------------------------------------------------------------
    // get(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(2015, TEST.get(WEEK_BASED_YEAR));
        assertEquals(1, TEST.get(WEEK_OF_WEEK_BASED_YEAR));
    }

    @Test
    public void test_get_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> TEST.get(YEAR));
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
        assertEquals(2015L, TEST.getLong(WEEK_BASED_YEAR));
        assertEquals(1L, TEST.getLong(WEEK_OF_WEEK_BASED_YEAR));
    }

    @Test
    public void test_getLong_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> TEST.getLong(YEAR));
    }

    @Test
    public void test_getLong_null() {
        assertThrows(NullPointerException.class, () -> TEST.getLong((TemporalField) null));
    }

    //-----------------------------------------------------------------------
    // lengthOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_lengthOfYear() {
        assertEquals(364, YearWeek.of(2014, 1).lengthOfYear());
        assertEquals(371, YearWeek.of(2015, 1).lengthOfYear());
    }

    //-----------------------------------------------------------------------
    // with(TemporalField, long)
    //-----------------------------------------------------------------------
    @Test()
    public void test_with() {
        assertEquals(YearWeek.of(2015, 10), TEST.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 10));
        assertEquals(YearWeek.of(2016, 1), TEST.with(IsoFields.WEEK_BASED_YEAR, 2016));
    }

    @ParameterizedTest
    @MethodSource("data_outOfBounds")
    public void test_with_outOfBounds(TemporalField field, long newValue) {
        assertThrows(DateTimeException.class, () -> TEST.with(field, newValue));
    }

    @Test
    public void test_with_TemporalAdjuster_unsupportedType() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> TEST.with(ChronoField.MONTH_OF_YEAR, 5));
    }

    //-----------------------------------------------------------------------
    // with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_unsupportedType() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> TEST.with(TemporalAdjusters.firstDayOfMonth()));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals("2015-W01", TEST.toString());
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequence() {
        assertEquals(TEST, YearWeek.parse("2015-W01"));
    }

    @Test
    public void test_parse_CharSequenceDate_invalidYear() {
        assertThrows(DateTimeParseException.class, () -> YearWeek.parse("12345-W7"));
    }

    @Test
    public void test_parse_CharSequenceDate_invalidWeek() {
        assertThrows(DateTimeParseException.class, () -> YearWeek.parse("2015-W54"));
    }

    @Test
    public void test_parse_CharSequenceDate_nullCharSequence() {
        assertThrows(NullPointerException.class, () -> YearWeek.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequenceDateTimeFormatter() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E 'W'w YYYY").withLocale(Locale.ENGLISH);
        assertEquals(TEST, YearWeek.parse("Mon W1 2015", f));
    }

    @Test
    public void test_parse_CharSequenceDateDateTimeFormatter_invalidWeek() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E 'W'w YYYY").withLocale(Locale.ENGLISH);
        assertThrows(DateTimeParseException.class, () -> YearWeek.parse("Mon W99 2015", f));
    }

    @Test
    public void test_parse_CharSequenceDateTimeFormatter_nullCharSequence() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E 'W'w YYYY").withLocale(Locale.ENGLISH);
        assertThrows(NullPointerException.class, () -> YearWeek.parse((CharSequence) null, f));
    }

    @Test
    public void test_parse_CharSequenceDateTimeFormatter_nullDateTimeFormatter() {
        assertThrows(NullPointerException.class, () -> YearWeek.parse("", (DateTimeFormatter) null));
    }

    //-----------------------------------------------------------------------
    // format()
    //-----------------------------------------------------------------------
    @Test
    public void test_format() {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
                .appendValue(WEEK_BASED_YEAR, 4)
                .appendLiteral('-')
                .appendValue(WEEK_OF_WEEK_BASED_YEAR, 2)
                .toFormatter();
        assertEquals("2015-01", TEST.format(f));
    }

    //-----------------------------------------------------------------------
    // adjustInto()
    //-----------------------------------------------------------------------
    @Test
    public void test_adjustInto() {
        YearWeek yw = YearWeek.of(2016, 1);
        LocalDate date = LocalDate.of(2015, 6, 20);
        assertEquals(LocalDate.of(2016, 1, 9), yw.adjustInto(date));
    }

    @Test
    public void test_adjustInto_badChronology() {
        YearWeek yw = YearWeek.of(2016, 1);
        ThaiBuddhistDate date = ThaiBuddhistDate.from(LocalDate.of(2015, 6, 20));
        assertThrows(DateTimeException.class, () -> yw.adjustInto(date));
    }

    //-----------------------------------------------------------------------
    // until(Temporal, TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_until_weeks() {
        assertEquals(1, TEST.until(YearWeek.of(2015, 2), ChronoUnit.WEEKS));
        assertEquals(2, TEST.until(YearWeek.of(2015, 3), ChronoUnit.WEEKS));
        assertEquals(52, TEST_NON_LEAP.until(TEST, ChronoUnit.WEEKS));
        assertEquals(53, TEST.until(YearWeek.of(2016, 1), ChronoUnit.WEEKS));
    }

    @Test
    public void test_until_years() {
        assertEquals(1, TEST.until(YearWeek.of(2016, 1), IsoFields.WEEK_BASED_YEARS));
        assertEquals(-1, TEST.until(YearWeek.of(2014, 1), IsoFields.WEEK_BASED_YEARS));
        assertEquals(0, TEST.until(YearWeek.of(2015, 53), IsoFields.WEEK_BASED_YEARS));
        assertEquals(0, YearWeek.of(2015, 10).until(YearWeek.of(2015, 5), IsoFields.WEEK_BASED_YEARS));
        assertEquals(0, YearWeek.of(2015, 10).until(YearWeek.of(2016, 5), IsoFields.WEEK_BASED_YEARS));
        assertEquals(0, TEST.until(YearWeek.of(2014, 2), IsoFields.WEEK_BASED_YEARS));
        assertEquals(-1, TEST.until(YearWeek.of(2013, 2), IsoFields.WEEK_BASED_YEARS));
    }

    @Test
    public void test_until_unsupportedType() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> TEST.until(YearWeek.of(2016, 1), ChronoUnit.MONTHS));
    }

    //-----------------------------------------------------------------------
    // range(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_range() {
        assertEquals(WEEK_BASED_YEAR.range(), TEST_NON_LEAP.range(WEEK_BASED_YEAR));
        assertEquals(WEEK_BASED_YEAR.range(), TEST.range(WEEK_BASED_YEAR));

        assertEquals(ValueRange.of(1, 52), TEST_NON_LEAP.range(WEEK_OF_WEEK_BASED_YEAR));
        assertEquals(ValueRange.of(1, 53), TEST.range(WEEK_OF_WEEK_BASED_YEAR));
    }

    @Test
    public void test_range_invalidField() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> TEST.range(YEAR));
    }

    @Test
    public void test_range_null() {
        assertThrows(NullPointerException.class, () -> TEST.range((TemporalField) null));
    }

    //-----------------------------------------------------------------------
    // withYear(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_withYear() {
        assertEquals(YearWeek.of(2014, 1), YearWeek.of(2015, 1).withYear(2014));
        assertEquals(YearWeek.of(2009, 53), YearWeek.of(2015, 53).withYear(2009));
    }

    @Test
    public void test_withYear_sameYear() {
        assertEquals(YearWeek.of(2015, 1), YearWeek.of(2015, 1).withYear(2015));
    }

    @Test
    public void test_withYear_resolve() {
        assertEquals(YearWeek.of(2014, 52), YearWeek.of(2015, 53).withYear(2014));
    }

    @Test
    public void test_withYear_int_max() {
        assertThrows(DateTimeException.class, () -> TEST.withYear(Integer.MAX_VALUE));
    }

    @Test
    public void test_withYear_int_min() {
        assertThrows(DateTimeException.class, () -> TEST.withYear(Integer.MIN_VALUE));
    }

    //-----------------------------------------------------------------------
    // plus(int, TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus() {
        assertEquals(YearWeek.of(2015, 2), TEST.plus(1, ChronoUnit.WEEKS));
        assertEquals(YearWeek.of(2016, 1), TEST.plus(1, IsoFields.WEEK_BASED_YEARS));
    }

    @Test
    public void test_plus_unsupportedType() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> YearWeek.of(2014, 1).plus(1, ChronoUnit.DAYS));
    }

    //-----------------------------------------------------------------------
    // plus(TemporalAmount)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount() {
        assertEquals(YearWeek.of(2015, 2), TEST.plus(Weeks.of(1)));
    }

    //-----------------------------------------------------------------------
    // withWeek(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_withWeek() {
        assertEquals(YearWeek.of(2015, 52), TEST.withWeek(52));
        assertEquals(TEST, YearWeek.of(2014, 1).withWeek(53));
    }

    @Test
    public void test_withWeek_sameWeek() {
        assertEquals(YearWeek.of(2014, 2), YearWeek.of(2014, 2).withWeek(2));
    }

    @Test
    public void test_withWeek_int_max() {
        assertThrows(DateTimeException.class, () -> TEST.withWeek(Integer.MAX_VALUE));
    }

    @Test
    public void test_withWeek_int_min() {
        assertThrows(DateTimeException.class, () -> TEST.withWeek(Integer.MIN_VALUE));
    }

    //-----------------------------------------------------------------------
    // plusYears(long)
    //-----------------------------------------------------------------------
    @Test
    public void test_plusYears() {
        assertEquals(YearWeek.of(2013, 1), TEST.plusYears(-2));
        assertEquals(YearWeek.of(2014, 1), TEST.plusYears(-1));
        assertEquals(TEST, TEST.plusYears(0));
        assertEquals(YearWeek.of(2016, 1), TEST.plusYears(1));
        assertEquals(YearWeek.of(2017, 1), TEST.plusYears(2));
    }

    @Test
    public void test_plusYears_changeWeek() {
        assertEquals(YearWeek.of(2014, 52), YearWeek.of(2015, 53).plusYears(-1));
        assertEquals(YearWeek.of(2015, 53), YearWeek.of(2015, 53).plusYears(0));
        assertEquals(YearWeek.of(2016, 52), YearWeek.of(2015, 53).plusYears(1));
        assertEquals(YearWeek.of(2020, 53), YearWeek.of(2015, 53).plusYears(5));
    }

    @Test
    public void test_plusYears_max_long() {
        assertThrows(ArithmeticException.class, () -> TEST.plusYears(Long.MAX_VALUE));
    }

    @Test
    public void test_plusYears_min_long() {
        assertThrows(ArithmeticException.class, () -> TEST.plusYears(Long.MIN_VALUE));
    }

    //-----------------------------------------------------------------------
    // plusWeeks(long)
    //-----------------------------------------------------------------------
    @Test
    public void test_plusWeeks() {
        assertEquals(TEST, TEST.plusWeeks(0));
        assertEquals(YearWeek.of(2015, 2), TEST.plusWeeks(1));
        assertEquals(YearWeek.of(2015, 3), TEST.plusWeeks(2));
        assertEquals(YearWeek.of(2015, 52), TEST.plusWeeks(51));
        assertEquals(YearWeek.of(2015, 53), TEST.plusWeeks(52));
        assertEquals(YearWeek.of(2016, 1), TEST.plusWeeks(53));
        assertEquals(YearWeek.of(2021, 1), TEST.plusWeeks(314));
    }

    @Test
    public void test_plusWeeks_negative() {
        assertEquals(TEST, TEST.plusWeeks(0));
        assertEquals(YearWeek.of(2014, 52), TEST.plusWeeks(-1));
        assertEquals(YearWeek.of(2014, 51), TEST.plusWeeks(-2));
        assertEquals(YearWeek.of(2014, 2), TEST.plusWeeks(-51));
        assertEquals(YearWeek.of(2014, 1), TEST.plusWeeks(-52));
        assertEquals(YearWeek.of(2013, 52), TEST.plusWeeks(-53));
        assertEquals(YearWeek.of(2009, 53), TEST.plusWeeks(-261));
    }

    @Test
    public void test_plusWeeks_max_long() {
        assertThrows(ArithmeticException.class, () -> TEST.plusWeeks(Long.MAX_VALUE));
    }

    @Test
    public void test_plusWeeks_min_long() {
        assertThrows(ArithmeticException.class, () -> TEST.plusWeeks(Long.MIN_VALUE));
    }

    //-----------------------------------------------------------------------
    // minus(int, TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus() {
        assertEquals(YearWeek.of(2014, 1), YearWeek.of(2014, 2).minus(1, ChronoUnit.WEEKS));
    }

    @Test
    public void test_minus_overflow() {
        assertThrows(ArithmeticException.class, () -> TEST.minus(Long.MIN_VALUE, ChronoUnit.WEEKS));
    }

    //-----------------------------------------------------------------------
    // minus(TemporalAmount)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount() {
        assertEquals(YearWeek.of(2014, 1), YearWeek.of(2014, 2).minus(Weeks.of(1)));
    }

    //-----------------------------------------------------------------------
    // minusYears(long)
    //-----------------------------------------------------------------------
    @Test
    public void test_minusYears() {
        assertEquals(YearWeek.of(2017, 1), TEST.minusYears(-2));
        assertEquals(YearWeek.of(2016, 1), TEST.minusYears(-1));
        assertEquals(TEST, TEST.minusYears(0));
        assertEquals(YearWeek.of(2014, 1), TEST.minusYears(1));
        assertEquals(YearWeek.of(2013, 1), TEST.minusYears(2));
    }

    @Test
    public void test_minusYears_changeWeek() {
        assertEquals(YearWeek.of(2020, 53), YearWeek.of(2015, 53).minusYears(-5));
        assertEquals(YearWeek.of(2016, 52), YearWeek.of(2015, 53).minusYears(-1));
        assertEquals(YearWeek.of(2015, 53), YearWeek.of(2015, 53).minusYears(0));
        assertEquals(YearWeek.of(2014, 52), YearWeek.of(2015, 53).minusYears(1));
    }

    @Test
    public void test_minusYears_max_long() {
        assertThrows(ArithmeticException.class, () -> TEST.minusYears(Long.MAX_VALUE));
    }

    @Test
    public void test_minusYears_min_long() {
        assertThrows(ArithmeticException.class, () -> TEST.minusYears(Long.MIN_VALUE));
    }

    //-----------------------------------------------------------------------
    // minusWeeks(long)
    //-----------------------------------------------------------------------
    @Test
    public void test_minusWeeks() {
        assertEquals(TEST, TEST.minusWeeks(0));
        assertEquals(YearWeek.of(2014, 52), TEST.minusWeeks(1));
        assertEquals(YearWeek.of(2014, 51), TEST.minusWeeks(2));
        assertEquals(YearWeek.of(2014, 2), TEST.minusWeeks(51));
        assertEquals(YearWeek.of(2014, 1), TEST.minusWeeks(52));
        assertEquals(YearWeek.of(2013, 52), TEST.minusWeeks(53));
        assertEquals(YearWeek.of(2009, 53), TEST.minusWeeks(261));
    }

    @Test
    public void test_minusWeeks_negative() {
        assertEquals(TEST, TEST.minusWeeks(0));
        assertEquals(YearWeek.of(2015, 2), TEST.minusWeeks(-1));
        assertEquals(YearWeek.of(2015, 3), TEST.minusWeeks(-2));
        assertEquals(YearWeek.of(2015, 52), TEST.minusWeeks(-51));
        assertEquals(YearWeek.of(2015, 53), TEST.minusWeeks(-52));
        assertEquals(YearWeek.of(2016, 1), TEST.minusWeeks(-53));
        assertEquals(YearWeek.of(2021, 1), TEST.minusWeeks(-314));
    }

    @Test
    public void test_minWeeks_max_long() {
        assertThrows(ArithmeticException.class, () -> TEST.plusWeeks(Long.MAX_VALUE));
    }

    @Test
    public void test_minWeeks_min_long() {
        assertThrows(ArithmeticException.class, () -> TEST.plusWeeks(Long.MIN_VALUE));
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
        assertEquals(ChronoUnit.WEEKS, TEST.query(TemporalQueries.precision()));
        assertEquals(null, TEST.query(TemporalQueries.zone()));
        assertEquals(null, TEST.query(TemporalQueries.zoneId()));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(YearWeek.of(2015, 4), YearWeek.of(2015, 4))
            .addEqualityGroup(YearWeek.of(2015, 6), YearWeek.of(2015, 6))
            .addEqualityGroup(YearWeek.of(2016, 6), YearWeek.of(2016, 6))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public static Object[][] data_sampleToString() {
        return new Object[][]{
            {2015, 1, "2015-W01"},
            {2015, 10, "2015-W10"},
            {999, 1, "0999-W01"},
            {-999, 1, "-0999-W01"},
            {10000, 1, "+10000-W01"},
            {-10000, 1, "-10000-W01"},};
    }

    @ParameterizedTest
    @MethodSource("data_sampleToString")
    public void test_toString(int year, int week, String expected) {
        YearWeek yearWeek = YearWeek.of(year, week);
        String s = yearWeek.toString();
        assertEquals(expected, s);
    }

}
