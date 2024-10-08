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
package org.threeten.extra.chrono;

import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.MINUTE_OF_DAY;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.ChronoUnit.CENTURIES;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.ERAS;
import static java.time.temporal.ChronoUnit.MILLENNIA;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

/**
 * Test.
 */
public class TestJulianChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        Chronology chrono = Chronology.of("Julian");
        assertNotNull(chrono);
        assertEquals(JulianChronology.INSTANCE, chrono);
        assertEquals("Julian", chrono.getId());
        assertEquals("julian", chrono.getCalendarType());
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("julian");
        assertNotNull(chrono);
        assertEquals(JulianChronology.INSTANCE, chrono);
        assertEquals("Julian", chrono.getId());
        assertEquals("julian", chrono.getCalendarType());
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    public static Object[][] data_samples() {
        return new Object[][] {
            {JulianDate.of(1, 1, 1), LocalDate.of(0, 12, 30)},
            {JulianDate.of(1, 1, 2), LocalDate.of(0, 12, 31)},
            {JulianDate.of(1, 1, 3), LocalDate.of(1, 1, 1)},

            {JulianDate.of(1, 2, 28), LocalDate.of(1, 2, 26)},
            {JulianDate.of(1, 3, 1), LocalDate.of(1, 2, 27)},
            {JulianDate.of(1, 3, 2), LocalDate.of(1, 2, 28)},
            {JulianDate.of(1, 3, 3), LocalDate.of(1, 3, 1)},

            {JulianDate.of(4, 2, 28), LocalDate.of(4, 2, 26)},
            {JulianDate.of(4, 2, 29), LocalDate.of(4, 2, 27)},
            {JulianDate.of(4, 3, 1), LocalDate.of(4, 2, 28)},
            {JulianDate.of(4, 3, 2), LocalDate.of(4, 2, 29)},
            {JulianDate.of(4, 3, 3), LocalDate.of(4, 3, 1)},

            {JulianDate.of(100, 2, 28), LocalDate.of(100, 2, 26)},
            {JulianDate.of(100, 2, 29), LocalDate.of(100, 2, 27)},
            {JulianDate.of(100, 3, 1), LocalDate.of(100, 2, 28)},
            {JulianDate.of(100, 3, 2), LocalDate.of(100, 3, 1)},
            {JulianDate.of(100, 3, 3), LocalDate.of(100, 3, 2)},

            {JulianDate.of(0, 12, 31), LocalDate.of(0, 12, 29)},
            {JulianDate.of(0, 12, 30), LocalDate.of(0, 12, 28)},

            {JulianDate.of(1582, 10, 4), LocalDate.of(1582, 10, 14)},
            {JulianDate.of(1582, 10, 5), LocalDate.of(1582, 10, 15)},
            {JulianDate.of(1945, 10, 30), LocalDate.of(1945, 11, 12)},

            {JulianDate.of(2012, 6, 22), LocalDate.of(2012, 7, 5)},
            {JulianDate.of(2012, 6, 23), LocalDate.of(2012, 7, 6)},
        };
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_from_JulianDate(JulianDate julian, LocalDate iso) {
        assertEquals(iso, LocalDate.from(julian));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_JulianDate_from_LocalDate(JulianDate julian, LocalDate iso) {
        assertEquals(julian, JulianDate.from(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_JulianDate_chronology_dateEpochDay(JulianDate julian, LocalDate iso) {
        assertEquals(julian, JulianChronology.INSTANCE.dateEpochDay(iso.toEpochDay()));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_JulianDate_toEpochDay(JulianDate julian, LocalDate iso) {
        assertEquals(iso.toEpochDay(), julian.toEpochDay());
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_JulianDate_until_JulianDate(JulianDate julian, LocalDate iso) {
        assertEquals(JulianChronology.INSTANCE.period(0, 0, 0), julian.until(julian));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_JulianDate_until_LocalDate(JulianDate julian, LocalDate iso) {
        assertEquals(JulianChronology.INSTANCE.period(0, 0, 0), julian.until(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_until_JulianDate(JulianDate julian, LocalDate iso) {
        assertEquals(Period.ZERO, iso.until(julian));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Chronology_date_Temporal(JulianDate julian, LocalDate iso) {
        assertEquals(julian, JulianChronology.INSTANCE.date(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_plusDays(JulianDate julian, LocalDate iso) {
        assertEquals(iso, LocalDate.from(julian.plus(0, DAYS)));
        assertEquals(iso.plusDays(1), LocalDate.from(julian.plus(1, DAYS)));
        assertEquals(iso.plusDays(35), LocalDate.from(julian.plus(35, DAYS)));
        assertEquals(iso.plusDays(-1), LocalDate.from(julian.plus(-1, DAYS)));
        assertEquals(iso.plusDays(-60), LocalDate.from(julian.plus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_minusDays(JulianDate julian, LocalDate iso) {
        assertEquals(iso, LocalDate.from(julian.minus(0, DAYS)));
        assertEquals(iso.minusDays(1), LocalDate.from(julian.minus(1, DAYS)));
        assertEquals(iso.minusDays(35), LocalDate.from(julian.minus(35, DAYS)));
        assertEquals(iso.minusDays(-1), LocalDate.from(julian.minus(-1, DAYS)));
        assertEquals(iso.minusDays(-60), LocalDate.from(julian.minus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_until_DAYS(JulianDate julian, LocalDate iso) {
        assertEquals(0, julian.until(iso.plusDays(0), DAYS));
        assertEquals(1, julian.until(iso.plusDays(1), DAYS));
        assertEquals(35, julian.until(iso.plusDays(35), DAYS));
        assertEquals(-40, julian.until(iso.minusDays(40), DAYS));
    }

    public static Object[][] data_badDates() {
        return new Object[][] {
            {1900, 0, 0},

            {1900, -1, 1},
            {1900, 0, 1},
            {1900, 13, 1},
            {1900, 14, 1},

            {1900, 1, -1},
            {1900, 1, 0},
            {1900, 1, 32},

            {1900, 2, -1},
            {1900, 2, 0},
            {1900, 2, 30},
            {1900, 2, 31},
            {1900, 2, 32},

            {1899, 2, -1},
            {1899, 2, 0},
            {1899, 2, 29},
            {1899, 2, 30},
            {1899, 2, 31},
            {1899, 2, 32},

            {1900, 12, -1},
            {1900, 12, 0},
            {1900, 12, 32},

            {1900, 3, 32},
            {1900, 4, 31},
            {1900, 5, 32},
            {1900, 6, 31},
            {1900, 7, 32},
            {1900, 8, 32},
            {1900, 9, 31},
            {1900, 10, 32},
            {1900, 11, 31},
        };
    }

    @ParameterizedTest
    @MethodSource("data_badDates")
    public void test_badDates(int year, int month, int dom) {
        assertThrows(DateTimeException.class, () -> JulianDate.of(year, month, dom));
    }

    @Test
    public void test_chronology_dateYearDay_badDate() {
        assertThrows(DateTimeException.class, () -> JulianChronology.INSTANCE.dateYearDay(2001, 366));
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        for (int year = -200; year < 200; year++) {
            JulianDate base = JulianDate.of(year, 1, 1);
            assertEquals((year % 4) == 0, base.isLeapYear());
            assertEquals((year % 4) == 0, JulianChronology.INSTANCE.isLeapYear(year));
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(true, JulianChronology.INSTANCE.isLeapYear(8));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(7));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(6));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(5));
        assertEquals(true, JulianChronology.INSTANCE.isLeapYear(4));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(3));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(2));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(1));
        assertEquals(true, JulianChronology.INSTANCE.isLeapYear(0));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(-1));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(-2));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(-3));
        assertEquals(true, JulianChronology.INSTANCE.isLeapYear(-4));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(-5));
        assertEquals(false, JulianChronology.INSTANCE.isLeapYear(-6));
    }

    public static Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {1900, 1, 31},
            {1900, 2, 29},
            {1900, 3, 31},
            {1900, 4, 30},
            {1900, 5, 31},
            {1900, 6, 30},
            {1900, 7, 31},
            {1900, 8, 31},
            {1900, 9, 30},
            {1900, 10, 31},
            {1900, 11, 30},
            {1900, 12, 31},

            {1901, 2, 28},
            {1902, 2, 28},
            {1903, 2, 28},
            {1904, 2, 29},
            {2000, 2, 29},
            {2100, 2, 29},
        };
    }

    @ParameterizedTest
    @MethodSource("data_lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(length, JulianDate.of(year, month, 1).lengthOfMonth());
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = -200; year < 200; year++) {
            JulianDate base = JulianChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            JulianEra era = (year <= 0 ? JulianEra.BC : JulianEra.AD);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            JulianDate eraBased = JulianChronology.INSTANCE.date(era, yoe, 1, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = -200; year < 200; year++) {
            JulianDate base = JulianChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            JulianEra era = (year <= 0 ? JulianEra.BC : JulianEra.AD);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            JulianDate eraBased = JulianChronology.INSTANCE.dateYearDay(era, yoe, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(4, JulianChronology.INSTANCE.prolepticYear(JulianEra.AD, 4));
        assertEquals(3, JulianChronology.INSTANCE.prolepticYear(JulianEra.AD, 3));
        assertEquals(2, JulianChronology.INSTANCE.prolepticYear(JulianEra.AD, 2));
        assertEquals(1, JulianChronology.INSTANCE.prolepticYear(JulianEra.AD, 1));
        assertEquals(0, JulianChronology.INSTANCE.prolepticYear(JulianEra.BC, 1));
        assertEquals(-1, JulianChronology.INSTANCE.prolepticYear(JulianEra.BC, 2));
        assertEquals(-2, JulianChronology.INSTANCE.prolepticYear(JulianEra.BC, 3));
        assertEquals(-3, JulianChronology.INSTANCE.prolepticYear(JulianEra.BC, 4));
    }

    @Test
    public void test_prolepticYear_badEra() {
        assertThrows(ClassCastException.class, () -> JulianChronology.INSTANCE.prolepticYear(IsoEra.CE, 4));
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(JulianEra.AD, JulianChronology.INSTANCE.eraOf(1));
        assertEquals(JulianEra.BC, JulianChronology.INSTANCE.eraOf(0));
    }

    @Test
    public void test_Chronology_eraOf_invalid() {
        assertThrows(DateTimeException.class, () -> JulianChronology.INSTANCE.eraOf(2));
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = JulianChronology.INSTANCE.eras();
        assertEquals(2, eras.size());
        assertEquals(true, eras.contains(JulianEra.BC));
        assertEquals(true, eras.contains(JulianEra.AD));
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(ValueRange.of(1, 7), JulianChronology.INSTANCE.range(DAY_OF_WEEK));
        assertEquals(ValueRange.of(1, 28, 31), JulianChronology.INSTANCE.range(DAY_OF_MONTH));
        assertEquals(ValueRange.of(1, 365, 366), JulianChronology.INSTANCE.range(DAY_OF_YEAR));
        assertEquals(ValueRange.of(1, 12), JulianChronology.INSTANCE.range(MONTH_OF_YEAR));
    }

    //-----------------------------------------------------------------------
    // JulianDate.range
    //-----------------------------------------------------------------------
    public static Object[][] data_ranges() {
        return new Object[][] {
            {2012, 1, 23, DAY_OF_MONTH, 1, 31},
            {2012, 2, 23, DAY_OF_MONTH, 1, 29},
            {2012, 3, 23, DAY_OF_MONTH, 1, 31},
            {2012, 4, 23, DAY_OF_MONTH, 1, 30},
            {2012, 5, 23, DAY_OF_MONTH, 1, 31},
            {2012, 6, 23, DAY_OF_MONTH, 1, 30},
            {2012, 7, 23, DAY_OF_MONTH, 1, 31},
            {2012, 8, 23, DAY_OF_MONTH, 1, 31},
            {2012, 9, 23, DAY_OF_MONTH, 1, 30},
            {2012, 10, 23, DAY_OF_MONTH, 1, 31},
            {2012, 11, 23, DAY_OF_MONTH, 1, 30},
            {2012, 12, 23, DAY_OF_MONTH, 1, 31},
            {2012, 1, 23, DAY_OF_YEAR, 1, 366},
            {2012, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {2012, 2, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {2012, 3, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},

            {2011, 2, 23, DAY_OF_MONTH, 1, 28},
            {2011, 2, 23, DAY_OF_YEAR, 1, 365},
            {2011, 2, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},
        };
    }

    @ParameterizedTest
    @MethodSource("data_ranges")
    public void test_range(int year, int month, int dom, TemporalField field, int expectedMin, int expectedMax) {
        assertEquals(ValueRange.of(expectedMin, expectedMax), JulianDate.of(year, month, dom).range(field));
    }

    @Test
    public void test_range_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> JulianDate.of(2012, 6, 30).range(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // JulianDate.getLong
    //-----------------------------------------------------------------------
    public static Object[][] data_getLong() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 7},
            {2014, 5, 26, DAY_OF_MONTH, 26},
            {2014, 5, 26, DAY_OF_YEAR, 31 + 28 + 31 + 30 + 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 6},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 21},
            {2014, 5, 26, MONTH_OF_YEAR, 5},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 12 + 5 - 1},
            {2014, 5, 26, YEAR, 2014},
            {2014, 5, 26, ERA, 1},
            {1, 6, 8, ERA, 1},
            {0, 6, 8, ERA, 0},

            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 7},
        };
    }

    @ParameterizedTest
    @MethodSource("data_getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(expected, JulianDate.of(year, month, dom).getLong(field));
    }

    @Test
    public void test_getLong_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> JulianDate.of(2012, 6, 30).getLong(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // JulianDate.with
    //-----------------------------------------------------------------------
    public static Object[][] data_with() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 3, 2014, 5, 22},
            {2014, 5, 26, DAY_OF_WEEK, 7, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_MONTH, 31, 2014, 5, 31},
            {2014, 5, 26, DAY_OF_MONTH, 26, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_YEAR, 365, 2014, 12, 31},
            {2014, 5, 26, DAY_OF_YEAR, 146, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 5, 24},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 1, 2014, 5, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2014, 5, 22},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 6, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 23, 2014, 6, 9},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 21, 2014, 5, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 7, 2014, 7, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 5, 2014, 5, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2013 * 12 + 3 - 1, 2013, 3, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 12 + 5 - 1, 2014, 5, 26},
            {2014, 5, 26, YEAR, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR, 2014, 2014, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2014, 2014, 5, 26},
            {2014, 5, 26, ERA, 0, -2013, 5, 26},
            {2014, 5, 26, ERA, 1, 2014, 5, 26},

            {2011, 3, 31, MONTH_OF_YEAR, 2, 2011, 2, 28},
            {2012, 3, 31, MONTH_OF_YEAR, 2, 2012, 2, 29},
            {2012, 3, 31, MONTH_OF_YEAR, 6, 2012, 6, 30},
            {2012, 2, 29, YEAR, 2011, 2011, 2, 28},
            {-2013, 6, 8, YEAR_OF_ERA, 2012, -2011, 6, 8},
            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 3, 2014, 5, 22},
        };
    }

    @ParameterizedTest
    @MethodSource("data_with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(JulianDate.of(expectedYear, expectedMonth, expectedDom), JulianDate.of(year, month, dom).with(field, value));
    }

    @Test
    public void test_with_TemporalField_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> JulianDate.of(2012, 6, 30).with(MINUTE_OF_DAY, 0));
    }

    //-----------------------------------------------------------------------
    // JulianDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        JulianDate base = JulianDate.of(2012, 6, 23);
        JulianDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(JulianDate.of(2012, 6, 30), test);
    }

    @Test
    public void test_adjust2() {
        JulianDate base = JulianDate.of(2012, 2, 23);
        JulianDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(JulianDate.of(2012, 2, 29), test);
    }

    //-----------------------------------------------------------------------
    // JulianDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        JulianDate julian = JulianDate.of(2000, 1, 4);
        JulianDate test = julian.with(LocalDate.of(2012, 7, 6));
        assertEquals(JulianDate.of(2012, 6, 23), test);
    }

    @Test
    public void test_adjust_toMonth() {
        JulianDate julian = JulianDate.of(2000, 1, 4);
        assertThrows(DateTimeException.class, () -> julian.with(Month.APRIL));
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(JulianDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToJulianDate() {
        JulianDate julian = JulianDate.of(2012, 6, 23);
        LocalDate test = LocalDate.MIN.with(julian);
        assertEquals(LocalDate.of(2012, 7, 6), test);
    }

    @Test
    public void test_LocalDateTime_adjustToJulianDate() {
        JulianDate julian = JulianDate.of(2012, 6, 23);
        LocalDateTime test = LocalDateTime.MIN.with(julian);
        assertEquals(LocalDateTime.of(2012, 7, 6, 0, 0), test);
    }

    //-----------------------------------------------------------------------
    // JulianDate.plus
    //-----------------------------------------------------------------------
    public static Object[][] data_plus() {
        return new Object[][] {
            {2014, 5, 26, 0, DAYS, 2014, 5, 26},
            {2014, 5, 26, 8, DAYS, 2014, 6, 3},
            {2014, 5, 26, -3, DAYS, 2014, 5, 23},
            {2014, 5, 26, 0, WEEKS, 2014, 5, 26},
            {2014, 5, 26, 3, WEEKS, 2014, 6, 16},
            {2014, 5, 26, -5, WEEKS, 2014, 4, 21},
            {2014, 5, 26, 0, MONTHS, 2014, 5, 26},
            {2014, 5, 26, 3, MONTHS, 2014, 8, 26},
            {2014, 5, 26, -5, MONTHS, 2013, 12, 26},
            {2014, 5, 26, 0, YEARS, 2014, 5, 26},
            {2014, 5, 26, 3, YEARS, 2017, 5, 26},
            {2014, 5, 26, -5, YEARS, 2009, 5, 26},
            {2014, 5, 26, 0, DECADES, 2014, 5, 26},
            {2014, 5, 26, 3, DECADES, 2044, 5, 26},
            {2014, 5, 26, -5, DECADES, 1964, 5, 26},
            {2014, 5, 26, 0, CENTURIES, 2014, 5, 26},
            {2014, 5, 26, 3, CENTURIES, 2314, 5, 26},
            {2014, 5, 26, -5, CENTURIES, 1514, 5, 26},
            {2014, 5, 26, 0, MILLENNIA, 2014, 5, 26},
            {2014, 5, 26, 3, MILLENNIA, 5014, 5, 26},
            {2014, 5, 26, -5, MILLENNIA, 2014 - 5000, 5, 26},
            {2014, 5, 26, -1, ERAS, -2013, 5, 26},
        };
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(JulianDate.of(expectedYear, expectedMonth, expectedDom), JulianDate.of(year, month, dom).plus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(JulianDate.of(expectedYear, expectedMonth, expectedDom), JulianDate.of(year, month, dom).minus(amount, unit));
    }

    @Test
    public void test_plus_TemporalUnit_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> JulianDate.of(2012, 6, 30).plus(0, MINUTES));
    }

    //-----------------------------------------------------------------------
    // JulianDate.until
    //-----------------------------------------------------------------------
    public static Object[][] data_until() {
        return new Object[][] {
            {2014, 5, 26, 2014, 5, 26, DAYS, 0},
            {2014, 5, 26, 2014, 6, 1, DAYS, 6},
            {2014, 5, 26, 2014, 5, 20, DAYS, -6},
            {2014, 5, 26, 2014, 5, 26, WEEKS, 0},
            {2014, 5, 26, 2014, 6, 1, WEEKS, 0},
            {2014, 5, 26, 2014, 6, 2, WEEKS, 1},
            {2014, 5, 26, 2014, 5, 26, MONTHS, 0},
            {2014, 5, 26, 2014, 6, 25, MONTHS, 0},
            {2014, 5, 26, 2014, 6, 26, MONTHS, 1},
            {2014, 5, 26, 2014, 5, 26, YEARS, 0},
            {2014, 5, 26, 2015, 5, 25, YEARS, 0},
            {2014, 5, 26, 2015, 5, 26, YEARS, 1},
            {2014, 5, 26, 2014, 5, 26, DECADES, 0},
            {2014, 5, 26, 2024, 5, 25, DECADES, 0},
            {2014, 5, 26, 2024, 5, 26, DECADES, 1},
            {2014, 5, 26, 2014, 5, 26, CENTURIES, 0},
            {2014, 5, 26, 2114, 5, 25, CENTURIES, 0},
            {2014, 5, 26, 2114, 5, 26, CENTURIES, 1},
            {2014, 5, 26, 2014, 5, 26, MILLENNIA, 0},
            {2014, 5, 26, 3014, 5, 25, MILLENNIA, 0},
            {2014, 5, 26, 3014, 5, 26, MILLENNIA, 1},
            {-2013, 5, 26, 0, 5, 26, ERAS, 0},
            {-2013, 5, 26, 2014, 5, 26, ERAS, 1},
        };
    }

    @ParameterizedTest
    @MethodSource("data_until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        JulianDate start = JulianDate.of(year1, month1, dom1);
        JulianDate end = JulianDate.of(year2, month2, dom2);
        assertEquals(expected, start.until(end, unit));
    }

    @Test
    public void test_until_TemporalUnit_unsupported() {
        JulianDate start = JulianDate.of(2012, 6, 30);
        JulianDate end = JulianDate.of(2012, 7, 1);
        assertThrows(UnsupportedTemporalTypeException.class, () -> start.until(end, MINUTES));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(JulianDate.of(2014, 7, 29), JulianDate.of(2014, 5, 26).plus(JulianChronology.INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_plus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> JulianDate.of(2014, 5, 26).plus(Period.ofMonths(2)));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(JulianDate.of(2014, 3, 23), JulianDate.of(2014, 5, 26).minus(JulianChronology.INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_minus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> JulianDate.of(2014, 5, 26).minus(Period.ofMonths(2)));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(JulianDate.of(2000, 1, 3), JulianDate.of(2000, 1, 3))
            .addEqualityGroup(JulianDate.of(2000, 1, 4), JulianDate.of(2000, 1, 4))
            .addEqualityGroup(JulianDate.of(2000, 2, 3), JulianDate.of(2000, 2, 3))
            .addEqualityGroup(JulianDate.of(2001, 1, 3), JulianDate.of(2001, 1, 3))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public static Object[][] data_toString() {
        return new Object[][] {
            {JulianDate.of(1, 1, 1), "Julian AD 1-01-01"},
            {JulianDate.of(2012, 6, 23), "Julian AD 2012-06-23"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_toString")
    public void test_toString(JulianDate julian, String expected) {
        assertEquals(expected, julian.toString());
    }

}
