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
import java.time.chrono.ChronoPeriod;
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
import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

/**
 * Test.
 */
@SuppressWarnings({"static-method", "javadoc"})
public class TestPaxChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        Chronology chrono = Chronology.of("Pax");
        assertNotNull(chrono);
        assertEquals(PaxChronology.INSTANCE, chrono);
        assertEquals("Pax", chrono.getId());
        assertEquals("pax", chrono.getCalendarType());
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("pax");
        assertNotNull(chrono);
        assertEquals(PaxChronology.INSTANCE, chrono);
        assertEquals("Pax", chrono.getId());
        assertEquals("pax", chrono.getCalendarType());
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    public static Object[][] data_samples() {
        return new Object[][] {
            {PaxDate.of(1, 1, 1), LocalDate.of(0, 12, 31)},
            {PaxDate.of(1, 1, 2), LocalDate.of(1, 1, 1)},
            {PaxDate.of(1, 1, 3), LocalDate.of(1, 1, 2)},

            {PaxDate.of(1, 1, 28), LocalDate.of(1, 1, 27)},
            {PaxDate.of(1, 2, 1), LocalDate.of(1, 1, 28)},
            {PaxDate.of(1, 2, 2), LocalDate.of(1, 1, 29)},
            {PaxDate.of(1, 2, 3), LocalDate.of(1, 1, 30)},

            {PaxDate.of(6, 13, 6), LocalDate.of(6, 12, 1)},
            {PaxDate.of(6, 13, 7), LocalDate.of(6, 12, 2)},
            {PaxDate.of(6, 14, 1), LocalDate.of(6, 12, 3)},
            {PaxDate.of(6, 14, 2), LocalDate.of(6, 12, 4)},
            {PaxDate.of(6, 14, 3), LocalDate.of(6, 12, 5)},
            {PaxDate.of(6, 14, 27), LocalDate.of(6, 12, 29)},
            {PaxDate.of(6, 14, 28), LocalDate.of(6, 12, 30)},
            {PaxDate.of(7, 1, 1), LocalDate.of(6, 12, 31)},
            {PaxDate.of(7, 1, 2), LocalDate.of(7, 1, 1)},

            {PaxDate.of(399, 13, 6), LocalDate.of(399, 12, 3)},
            {PaxDate.of(399, 13, 7), LocalDate.of(399, 12, 4)},
            {PaxDate.of(399, 14, 1), LocalDate.of(399, 12, 5)},
            {PaxDate.of(399, 14, 2), LocalDate.of(399, 12, 6)},
            {PaxDate.of(399, 14, 3), LocalDate.of(399, 12, 7)},
            {PaxDate.of(400, 13, 27), LocalDate.of(400, 12, 29)},
            {PaxDate.of(400, 13, 28), LocalDate.of(400, 12, 30)},
            {PaxDate.of(401, 1, 1), LocalDate.of(400, 12, 31)},
            {PaxDate.of(401, 1, 2), LocalDate.of(401, 1, 1)},
            {PaxDate.of(401, 1, 3), LocalDate.of(401, 1, 2)},

            {PaxDate.of(0, 13, 28), LocalDate.of(0, 12, 30)},
            {PaxDate.of(0, 13, 27), LocalDate.of(0, 12, 29)},

            {PaxDate.of(1582, 10, 5), LocalDate.of(1582, 9, 9)},
            {PaxDate.of(1582, 10, 6), LocalDate.of(1582, 9, 10)},
            {PaxDate.of(1945, 10, 28), LocalDate.of(1945, 10, 6)},

            {PaxDate.of(2012, 6, 23), LocalDate.of(2012, 6, 4)},
            {PaxDate.of(2012, 6, 24), LocalDate.of(2012, 6, 5)},

            {PaxDate.of(-6, 1, 1), LocalDate.of(-6, 1, 2)},
            {PaxDate.of(-6, 13, 6), LocalDate.of(-6, 12, 9)},
            {PaxDate.of(-6, 13, 7), LocalDate.of(-6, 12, 10)},
            {PaxDate.of(-6, 14, 1), LocalDate.of(-6, 12, 11)},
            {PaxDate.of(-6, 14, 2), LocalDate.of(-6, 12, 12)},
            {PaxDate.of(-6, 14, 27), LocalDate.of(-5, 1, 6)},
            {PaxDate.of(-6, 14, 28), LocalDate.of(-5, 1, 7)},
            {PaxDate.of(-5, 1, 1), LocalDate.of(-5, 1, 8)},
            {PaxDate.of(-5, 1, 2), LocalDate.of(-5, 1, 9)},
            {PaxDate.of(-99, 1, 1), LocalDate.of(-99, 1, 6)},
            {PaxDate.of(-99, 13, 6), LocalDate.of(-99, 12, 13)},
            {PaxDate.of(-99, 13, 7), LocalDate.of(-99, 12, 14)},
            {PaxDate.of(-99, 14, 1), LocalDate.of(-99, 12, 15)},
            {PaxDate.of(-99, 14, 2), LocalDate.of(-99, 12, 16)},
            {PaxDate.of(-100, 1, 1), LocalDate.of(-101, 12, 31)},
            {PaxDate.of(-100, 13, 6), LocalDate.of(-100, 12, 7)},
            {PaxDate.of(-100, 13, 7), LocalDate.of(-100, 12, 8)},
            {PaxDate.of(-100, 14, 1), LocalDate.of(-100, 12, 9)},
            {PaxDate.of(-100, 14, 2), LocalDate.of(-100, 12, 10)},
        };
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_from_PaxDate(PaxDate pax, LocalDate iso) {
        assertEquals(iso, LocalDate.from(pax));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_PaxDate_from_LocalDate(PaxDate pax, LocalDate iso) {
        assertEquals(pax, PaxDate.from(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_PaxDate_chronology_dateEpochDay(PaxDate pax, LocalDate iso) {
        assertEquals(pax, PaxChronology.INSTANCE.dateEpochDay(iso.toEpochDay()));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_PaxDate_toEpochDay(PaxDate pax, LocalDate iso) {
        assertEquals(iso.toEpochDay(), pax.toEpochDay());
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_PaxDate_until_PaxDate(PaxDate pax, LocalDate iso) {
        assertEquals(PaxChronology.INSTANCE.period(0, 0, 0), pax.until(pax));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_PaxDate_until_LocalDate(PaxDate pax, LocalDate iso) {
        assertEquals(PaxChronology.INSTANCE.period(0, 0, 0), pax.until(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_until_PaxDate(PaxDate pax, LocalDate iso) {
        assertEquals(Period.ZERO, iso.until(pax));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Chronology_date_Temporal(PaxDate pax, LocalDate iso) {
        assertEquals(pax, PaxChronology.INSTANCE.date(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_plusDays(PaxDate pax, LocalDate iso) {
        assertEquals(iso, LocalDate.from(pax.plus(0, DAYS)));
        assertEquals(iso.plusDays(1), LocalDate.from(pax.plus(1, DAYS)));
        assertEquals(iso.plusDays(35), LocalDate.from(pax.plus(35, DAYS)));
        assertEquals(iso.plusDays(-1), LocalDate.from(pax.plus(-1, DAYS)));
        assertEquals(iso.plusDays(-60), LocalDate.from(pax.plus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_minusDays(PaxDate pax, LocalDate iso) {
        assertEquals(iso, LocalDate.from(pax.minus(0, DAYS)));
        assertEquals(iso.minusDays(1), LocalDate.from(pax.minus(1, DAYS)));
        assertEquals(iso.minusDays(35), LocalDate.from(pax.minus(35, DAYS)));
        assertEquals(iso.minusDays(-1), LocalDate.from(pax.minus(-1, DAYS)));
        assertEquals(iso.minusDays(-60), LocalDate.from(pax.minus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_until_DAYS(PaxDate pax, LocalDate iso) {
        assertEquals(0, pax.until(iso.plusDays(0), DAYS));
        assertEquals(1, pax.until(iso.plusDays(1), DAYS));
        assertEquals(35, pax.until(iso.plusDays(35), DAYS));
        assertEquals(-40, pax.until(iso.minusDays(40), DAYS));
    }

    public static Object[][] data_badDates() {
        return new Object[][] {
            {1900, 0, 0},

            {1900, -1, 1},
            {1900, 0, 1},
            {1900, 15, 1},
            {1900, 16, 1},

            {1900, 1, -1},
            {1900, 1, 0},
            {1900, 1, 29},

            {1900, 13, -1},
            {1900, 13, 0},
            {1900, 13, 8},
            {1900, 14, -1},
            {1900, 14, 0},
            {1900, 14, 29},
            {1900, 14, 30},

            {1898, 13, -1},
            {1898, 13, 0},
            {1898, 14, 29},
            {1898, 14, 30},
            {1898, 14, 1},
            {1898, 14, 2},

            {1900, 14, -1},
            {1900, 14, 0},
            {1900, 14, 29},

            {1900, 2, 29},
            {1900, 3, 29},
            {1900, 4, 29},
            {1900, 5, 29},
            {1900, 6, 29},
            {1900, 7, 29},
            {1900, 8, 29},
            {1900, 9, 29},
            {1900, 10, 29},
            {1900, 11, 29},
            {1900, 12, 29},
        };
    }

    @ParameterizedTest
    @MethodSource("data_badDates")
    public void test_badDates(int year, int month, int dom) {
        assertThrows(DateTimeException.class, () -> PaxDate.of(year, month, dom));
    }

    @Test
    public void test_chronology_dateYearDay_badDate() {
        assertThrows(DateTimeException.class, () -> PaxChronology.INSTANCE.dateYearDay(2001, 365));
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        IntPredicate isLeapYear = year -> {
            int lastTwoDigits = Math.abs(year % 100);
            return (year % 400 != 0 && (lastTwoDigits == 0 || lastTwoDigits % 6 == 0)) || lastTwoDigits == 99;
        };
        for (int year = -500; year < 500; year++) {
            PaxDate base = PaxDate.of(year, 1, 1);
            assertEquals(isLeapYear.test(year), base.isLeapYear());
            assertEquals(isLeapYear.test(year), PaxChronology.INSTANCE.isLeapYear(year));
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(400));
        assertEquals(true, PaxChronology.INSTANCE.isLeapYear(100));
        assertEquals(true, PaxChronology.INSTANCE.isLeapYear(99));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(7));
        assertEquals(true, PaxChronology.INSTANCE.isLeapYear(6));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(5));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(4));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(3));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(2));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(1));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(0));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(-1));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(-2));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(-3));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(-4));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(-5));
        assertEquals(true, PaxChronology.INSTANCE.isLeapYear(-6));
        assertEquals(true, PaxChronology.INSTANCE.isLeapYear(-99));
        assertEquals(true, PaxChronology.INSTANCE.isLeapYear(-100));
        assertEquals(false, PaxChronology.INSTANCE.isLeapYear(-400));
    }

    public static Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {1900, 1, 28},
            {1900, 2, 28},
            {1900, 3, 28},
            {1900, 4, 28},
            {1900, 5, 28},
            {1900, 6, 28},
            {1900, 7, 28},
            {1900, 8, 28},
            {1900, 9, 28},
            {1900, 10, 28},
            {1900, 11, 28},
            {1900, 12, 28},
            {1900, 13, 7},
            {1900, 14, 28},

            {1901, 13, 28},
            {1902, 13, 28},
            {1903, 13, 28},
            {1904, 13, 28},
            {1905, 13, 28},
            {1906, 13, 7},
            {2000, 13, 28},
            {2100, 13, 7},
        };
    }

    @ParameterizedTest
    @MethodSource("data_lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(length, PaxDate.of(year, month, 1).lengthOfMonth());
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = -200; year < 200; year++) {
            PaxDate base = PaxChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            PaxEra era = (year <= 0 ? PaxEra.BCE : PaxEra.CE);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            PaxDate eraBased = PaxChronology.INSTANCE.date(era, yoe, 1, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = -200; year < 200; year++) {
            PaxDate base = PaxChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            PaxEra era = (year <= 0 ? PaxEra.BCE : PaxEra.CE);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            PaxDate eraBased = PaxChronology.INSTANCE.dateYearDay(era, yoe, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(4, PaxChronology.INSTANCE.prolepticYear(PaxEra.CE, 4));
        assertEquals(3, PaxChronology.INSTANCE.prolepticYear(PaxEra.CE, 3));
        assertEquals(2, PaxChronology.INSTANCE.prolepticYear(PaxEra.CE, 2));
        assertEquals(1, PaxChronology.INSTANCE.prolepticYear(PaxEra.CE, 1));
        assertEquals(0, PaxChronology.INSTANCE.prolepticYear(PaxEra.BCE, 1));
        assertEquals(-1, PaxChronology.INSTANCE.prolepticYear(PaxEra.BCE, 2));
        assertEquals(-2, PaxChronology.INSTANCE.prolepticYear(PaxEra.BCE, 3));
        assertEquals(-3, PaxChronology.INSTANCE.prolepticYear(PaxEra.BCE, 4));
    }

    @Test
    public void test_prolepticYear_badEra() {
        assertThrows(ClassCastException.class, () -> PaxChronology.INSTANCE.prolepticYear(IsoEra.CE, 4));
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(PaxEra.CE, PaxChronology.INSTANCE.eraOf(1));
        assertEquals(PaxEra.BCE, PaxChronology.INSTANCE.eraOf(0));
    }

    @Test
    public void test_Chronology_eraOf_invalid() {
        assertThrows(DateTimeException.class, () -> PaxChronology.INSTANCE.eraOf(2));
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = PaxChronology.INSTANCE.eras();
        assertEquals(2, eras.size());
        assertEquals(true, eras.contains(PaxEra.BCE));
        assertEquals(true, eras.contains(PaxEra.CE));
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(ValueRange.of(1, 7), PaxChronology.INSTANCE.range(DAY_OF_WEEK));
        assertEquals(ValueRange.of(1, 7, 28), PaxChronology.INSTANCE.range(DAY_OF_MONTH));
        assertEquals(ValueRange.of(1, 364, 371), PaxChronology.INSTANCE.range(DAY_OF_YEAR));
        assertEquals(ValueRange.of(1, 13, 14), PaxChronology.INSTANCE.range(MONTH_OF_YEAR));
    }

    //-----------------------------------------------------------------------
    // PaxDate.range
    //-----------------------------------------------------------------------
    public static Object[][] data_ranges() {
        return new Object[][] {
            {2012, 1, 23, DAY_OF_MONTH, 1, 28},
            {2012, 2, 23, DAY_OF_MONTH, 1, 28},
            {2012, 3, 23, DAY_OF_MONTH, 1, 28},
            {2012, 4, 23, DAY_OF_MONTH, 1, 28},
            {2012, 5, 23, DAY_OF_MONTH, 1, 28},
            {2012, 6, 23, DAY_OF_MONTH, 1, 28},
            {2012, 7, 23, DAY_OF_MONTH, 1, 28},
            {2012, 8, 23, DAY_OF_MONTH, 1, 28},
            {2012, 9, 23, DAY_OF_MONTH, 1, 28},
            {2012, 10, 23, DAY_OF_MONTH, 1, 28},
            {2012, 11, 23, DAY_OF_MONTH, 1, 28},
            {2012, 12, 23, DAY_OF_MONTH, 1, 28},
            {2012, 13, 3, DAY_OF_MONTH, 1, 7},
            {2012, 14, 23, DAY_OF_MONTH, 1, 28},

            {2012, 1, 23, MONTH_OF_YEAR, 1, 14},
            {2012, 1, 23, DAY_OF_YEAR, 1, 371},

            {2012, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},
            {2012, 13, 3, ALIGNED_WEEK_OF_MONTH, 1, 1},
            {2012, 14, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},

            {2011, 13, 23, DAY_OF_MONTH, 1, 28},
            {2011, 1, 23, MONTH_OF_YEAR, 1, 13},
            {2011, 13, 23, DAY_OF_YEAR, 1, 364},
            {2011, 13, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},
        };
    }

    @ParameterizedTest
    @MethodSource("data_ranges")
    public void test_range(int year, int month, int dom, TemporalField field, int expectedMin, int expectedMax) {
        assertEquals(ValueRange.of(expectedMin, expectedMax), PaxDate.of(year, month, dom).range(field));
    }

    @Test
    public void test_range_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> PaxDate.of(2012, 6, 28).range(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // PaxDate.getLong
    //-----------------------------------------------------------------------
    public static Object[][] data_getLong() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 4},
            {2014, 5, 26, DAY_OF_MONTH, 26},
            {2014, 5, 26, DAY_OF_YEAR, 28 + 28 + 28 + 28 + 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 20},
            {2014, 5, 26, MONTH_OF_YEAR, 5},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 13 + 20 * 18 - 5 + 2 + 5 - 1},
            {2014, 5, 26, YEAR, 2014},
            {2014, 5, 26, ERA, 1},
            {1, 6, 8, ERA, 1},
            {0, 6, 8, ERA, 0},

            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 4},
        };
    }

    @ParameterizedTest
    @MethodSource("data_getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(expected, PaxDate.of(year, month, dom).getLong(field));
    }

    @Test
    public void test_getLong_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> PaxDate.of(2012, 6, 28).getLong(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // PaxDate.with
    //-----------------------------------------------------------------------
    public static Object[][] data_with() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 3, 2014, 5, 25},
            {2014, 5, 26, DAY_OF_WEEK, 4, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_MONTH, 28, 2014, 5, 28},
            {2014, 5, 26, DAY_OF_MONTH, 26, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_YEAR, 364, 2014, 13, 28},
            {2014, 5, 26, DAY_OF_YEAR, 138, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 5, 24},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 1, 2014, 5, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, 2014, 5, 24},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 23, 2014, 6, 19},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 20, 2014, 5, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 7, 2014, 7, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 5, 2014, 5, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2013 * 13 + 20 * 18 - 5 + 2 + 3 - 1, 2013, 3, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2013 * 13 + 20 * 18 - 5 + 2 + 5 - 1, 2013, 5, 26},
            {2014, 5, 26, YEAR, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR, 2014, 2014, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2014, 2014, 5, 26},
            {2014, 5, 26, ERA, 0, -2013, 5, 26},
            {2014, 5, 26, ERA, 1, 2014, 5, 26},

            {2011, 3, 28, MONTH_OF_YEAR, 13, 2011, 13, 28},
            {2012, 3, 28, MONTH_OF_YEAR, 13, 2012, 13, 7},
            {2012, 3, 28, MONTH_OF_YEAR, 6, 2012, 6, 28},
            {2012, 13, 7, YEAR, 2011, 2011, 13, 7},
            {-2013, 6, 8, YEAR_OF_ERA, 2012, -2011, 6, 8},
            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 3, 2014, 5, 25},
        };
    }

    @ParameterizedTest
    @MethodSource("data_with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(PaxDate.of(expectedYear, expectedMonth, expectedDom), PaxDate.of(year, month, dom).with(field, value));
    }

    @Test
    public void test_with_TemporalField_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> PaxDate.of(2012, 6, 28).with(MINUTE_OF_DAY, 0));
    }

    //-----------------------------------------------------------------------
    // PaxDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        PaxDate base = PaxDate.of(2012, 6, 23);
        PaxDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(PaxDate.of(2012, 6, 28), test);
    }

    @Test
    public void test_adjust2() {
        PaxDate base = PaxDate.of(2012, 13, 2);
        PaxDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(PaxDate.of(2012, 13, 7), test);
    }

    //-----------------------------------------------------------------------
    // PaxDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        PaxDate pax = PaxDate.of(2000, 1, 4);
        PaxDate test = pax.with(LocalDate.of(2012, 7, 6));
        assertEquals(PaxDate.of(2012, 7, 27), test);
    }

    @Test
    public void test_adjust_toMonth() {
        PaxDate pax = PaxDate.of(2000, 1, 4);
        assertThrows(DateTimeException.class, () -> pax.with(Month.APRIL));
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(PaxDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToPaxDate() {
        PaxDate pax = PaxDate.of(2012, 6, 23);
        LocalDate test = LocalDate.MIN.with(pax);
        assertEquals(LocalDate.of(2012, 6, 4), test);
    }

    @Test
    public void test_LocalDateTime_adjustToPaxDate() {
        PaxDate pax = PaxDate.of(2012, 6, 23);
        LocalDateTime test = LocalDateTime.MIN.with(pax);
        assertEquals(LocalDateTime.of(2012, 6, 4, 0, 0), test);
    }

    //-----------------------------------------------------------------------
    // PaxDate.plus
    //-----------------------------------------------------------------------
    public static Object[][] data_plus() {
        return new Object[][] {
            {2014, 5, 26, 0, DAYS, 2014, 5, 26},
            {2014, 5, 26, 8, DAYS, 2014, 6, 6},
            {2014, 5, 26, -3, DAYS, 2014, 5, 23},
            {2014, 5, 26, 0, WEEKS, 2014, 5, 26},
            {2014, 5, 26, 3, WEEKS, 2014, 6, 19},
            {2014, 5, 26, -5, WEEKS, 2014, 4, 19},
            {2014, 5, 26, 0, MONTHS, 2014, 5, 26},
            {2014, 5, 26, 3, MONTHS, 2014, 8, 26},
            {2014, 5, 26, -5, MONTHS, 2013, 13, 26},
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

            {2012, 13, 6, 3, MONTHS, 2013, 2, 6},
            {2011, 13, 26, 1, YEARS, 2012, 14, 26},
            {2014, 13, 26, -2, YEARS, 2012, 14, 26},
            {2012, 14, 26, -6, YEARS, 2006, 14, 26},
            {2012, 13, 6, -6, YEARS, 2006, 13, 6},

            {-2014, 5, 26, 0, MONTHS, -2014, 5, 26},
            {-2014, 5, 26, 3, MONTHS, -2014, 8, 26},
            {-2014, 5, 26, -5, MONTHS, -2015, 13, 26},
        };
    }

    public static Object[][] data_plus_leap() {
        return new Object[][] {
            {2012, 12, 26, 1, MONTHS, 2012, 13, 7},
            {2012, 14, 26, -1, MONTHS, 2012, 13, 7},
            {2012, 13, 6, 3, YEARS, 2015, 13, 6},
        };
    }

    public static Object[][] data_minus_leap() {
        return new Object[][] {
            {2012, 13, 7, -1, MONTHS, 2012, 12, 26},
            {2012, 13, 7, 1, MONTHS, 2012, 14, 26},
            {2012, 14, 6, 3, YEARS, 2015, 13, 6},
        };
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(PaxDate.of(expectedYear, expectedMonth, expectedDom), PaxDate.of(year, month, dom).plus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_plus_leap")
    public void test_plus_leap_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        test_plus_TemporalUnit(year, month, dom, amount, unit, expectedYear, expectedMonth, expectedDom);
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(PaxDate.of(expectedYear, expectedMonth, expectedDom), PaxDate.of(year, month, dom).minus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_minus_leap")
    public void test_minus_leap_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        test_minus_TemporalUnit(year, month, dom, amount, unit, expectedYear, expectedMonth, expectedDom);
    }

    @Test
    public void test_plus_TemporalUnit_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> PaxDate.of(2012, 6, 10).plus(0, MINUTES));
    }

    //-----------------------------------------------------------------------
    // PaxDate.until
    //-----------------------------------------------------------------------
    public static Object[][] data_until() {
        return new Object[][] {
            {2014, 5, 26, 2014, 5, 26, DAYS, 0},
            {2014, 5, 26, 2014, 6, 4, DAYS, 6},
            {2014, 5, 26, 2014, 5, 20, DAYS, -6},
            {2014, 5, 26, 2014, 5, 26, WEEKS, 0},
            {2014, 5, 26, 2014, 6, 4, WEEKS, 0},
            {2014, 5, 26, 2014, 6, 5, WEEKS, 1},
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

            {2011, 13, 26, 2013, 13, 26, YEARS, 2},
            {2011, 13, 26, 2012, 14, 26, YEARS, 1},
            {2012, 14, 26, 2011, 13, 26, YEARS, -1},
            {2012, 14, 26, 2013, 13, 26, YEARS, 1},
            {2011, 13, 6, 2012, 13, 6, YEARS, 0},
            {2012, 13, 6, 2011, 13, 6, YEARS, 0},
            {2011, 13, 1, 2012, 13, 7, YEARS, 0},
            {2012, 13, 7, 2011, 13, 1, YEARS, 0},
            {2011, 12, 28, 2012, 13, 1, YEARS, 1},
            {2012, 13, 1, 2011, 12, 28, YEARS, -1},
            {2013, 13, 6, 2012, 13, 6, YEARS, -1},
            {2012, 13, 6, 2013, 13, 6, YEARS, 1},
        };
    }

    public static Object[][] data_until_period() {
        return new Object[][] {
            {2014, 5, 26, 2014, 5, 26, 0, 0, 0},
            {2014, 5, 26, 2014, 6, 4, 0, 0, 6},
            {2014, 5, 26, 2014, 5, 20, 0, 0, -6},
            {2014, 5, 26, 2014, 6, 5, 0, 0, 7},
            {2014, 5, 26, 2014, 6, 25, 0, 0, 27},
            {2014, 5, 26, 2014, 6, 26, 0, 1, 0},
            {2014, 5, 26, 2015, 5, 25, 0, 12, 27},
            {2014, 5, 26, 2015, 5, 26, 1, 0, 0},
            {2014, 5, 26, 2024, 5, 25, 9, 12, 27},

            {2011, 13, 26, 2013, 13, 26, 2, 0, 0},
            {2011, 13, 26, 2012, 14, 26, 1, 0, 0},
            {2012, 14, 26, 2011, 13, 26, -1, 0, 0},
            {2012, 14, 26, 2013, 13, 26, 1, 0, 0},
            {2011, 13, 6, 2012, 13, 6, 0, 13, 0},
            {2012, 13, 6, 2011, 13, 6, 0, -13, 0},
            {2011, 13, 1, 2012, 13, 7, 0, 13, 6},
            {2012, 13, 7, 2011, 13, 1, 0, -13, -6},
            {2011, 12, 28, 2012, 13, 1, 1, 0, 1},
            {2012, 13, 1, 2011, 12, 28, -1, 0, -1},
            {2013, 13, 6, 2012, 13, 6, -1, -1, 0},
            {2012, 13, 6, 2013, 13, 6, 1, 0, 0},
        };
    }

    @ParameterizedTest
    @MethodSource("data_until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        PaxDate start = PaxDate.of(year1, month1, dom1);
        PaxDate end = PaxDate.of(year2, month2, dom2);
        assertEquals(expected, start.until(end, unit));
    }

    @ParameterizedTest
    @MethodSource("data_until_period")
    public void test_until_end(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            int yearPeriod, int monthPeriod, int dayPeriod) {
        PaxDate start = PaxDate.of(year1, month1, dom1);
        PaxDate end = PaxDate.of(year2, month2, dom2);
        ChronoPeriod period = PaxChronology.INSTANCE.period(yearPeriod, monthPeriod, dayPeriod);
        assertEquals(period, start.until(end));
    }

    @Test
    public void test_until_TemporalUnit_unsupported() {
        PaxDate start = PaxDate.of(2012, 6, 28);
        PaxDate end = PaxDate.of(2012, 7, 1);
        assertThrows(UnsupportedTemporalTypeException.class, () -> start.until(end, MINUTES));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(PaxDate.of(2014, 7, 28), PaxDate.of(2014, 5, 26).plus(PaxChronology.INSTANCE.period(0, 2, 2)));
    }

    @Test
    public void test_plus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> PaxDate.of(2014, 5, 26).plus(Period.ofMonths(2)));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(PaxDate.of(2014, 3, 23), PaxDate.of(2014, 5, 26).minus(PaxChronology.INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_minus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> PaxDate.of(2014, 5, 26).minus(Period.ofMonths(2)));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(PaxDate.of(2000, 1, 3), PaxDate.of(2000, 1, 3))
            .addEqualityGroup(PaxDate.of(2000, 1, 4), PaxDate.of(2000, 1, 4))
            .addEqualityGroup(PaxDate.of(2000, 2, 3), PaxDate.of(2000, 2, 3))
            .addEqualityGroup(PaxDate.of(2001, 1, 3), PaxDate.of(2001, 1, 3))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public static Object[][] data_toString() {
        return new Object[][] {
            {PaxDate.of(1, 1, 1), "Pax CE 1-01-01"},
            {PaxDate.of(2012, 6, 23), "Pax CE 2012-06-23"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_toString")
    public void test_toString(PaxDate pax, String expected) {
        assertEquals(expected, pax.toString());
    }

}
