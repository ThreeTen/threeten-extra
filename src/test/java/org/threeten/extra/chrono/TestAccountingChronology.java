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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
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
public class TestAccountingChronology {

    private static AccountingChronology INSTANCE = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST).
            withDivision(AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS).leapWeekInMonth(13).toChronology();

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        assertEquals("Accounting", INSTANCE.getId());
    }

    @Test
    public void test_chronology_of_name_id() {
        assertEquals(null, INSTANCE.getCalendarType());
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    public static Object[][] data_samples() {
        return new Object[][] {
            {INSTANCE.date(1, 1, 1), LocalDate.of(0, 9, 4)},
            {INSTANCE.date(1, 1, 2), LocalDate.of(0, 9, 5)},
            {INSTANCE.date(1, 1, 3), LocalDate.of(0, 9, 6)},

            {INSTANCE.date(2011, 13, 28), LocalDate.of(2011, 8, 28)},
            {INSTANCE.date(2012, 1, 1), LocalDate.of(2011, 8, 29)},
            {INSTANCE.date(2012, 1, 2), LocalDate.of(2011, 8, 30)},
            {INSTANCE.date(2012, 1, 3), LocalDate.of(2011, 8, 31)},

            {INSTANCE.date(2012, 13, 28), LocalDate.of(2012, 8, 26)},
            {INSTANCE.date(2012, 13, 29), LocalDate.of(2012, 8, 27)},
            {INSTANCE.date(2012, 13, 30), LocalDate.of(2012, 8, 28)},
            {INSTANCE.date(2012, 13, 31), LocalDate.of(2012, 8, 29)},
            {INSTANCE.date(2012, 13, 32), LocalDate.of(2012, 8, 30)},
            {INSTANCE.date(2012, 13, 33), LocalDate.of(2012, 8, 31)},
            {INSTANCE.date(2012, 13, 34), LocalDate.of(2012, 9, 1)},
            {INSTANCE.date(2012, 13, 35), LocalDate.of(2012, 9, 2)},
            {INSTANCE.date(2013, 1, 1), LocalDate.of(2012, 9, 3)},
            {INSTANCE.date(2013, 1, 2), LocalDate.of(2012, 9, 4)},
            {INSTANCE.date(2013, 1, 3), LocalDate.of(2012, 9, 5)},

            {INSTANCE.date(0, 13, 35), LocalDate.of(0, 9, 3)},
            {INSTANCE.date(0, 13, 34), LocalDate.of(0, 9, 2)},

            {INSTANCE.date(1583, 2, 18), LocalDate.of(1582, 10, 14)},
            {INSTANCE.date(1583, 2, 19), LocalDate.of(1582, 10, 15)},
            {INSTANCE.date(1946, 3, 15), LocalDate.of(1945, 11, 12)},

            {INSTANCE.date(2012, 12, 4), LocalDate.of(2012, 7, 5)},
            {INSTANCE.date(2012, 12, 5), LocalDate.of(2012, 7, 6)},
        };
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_from_AccountingDate(AccountingDate accounting, LocalDate iso) {
        assertEquals(iso, LocalDate.from(accounting));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_AccountingDate_from_LocalDate(AccountingDate accounting, LocalDate iso) {
        assertEquals(accounting, AccountingDate.from(INSTANCE, iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_AccountingDate_chronology_dateEpochDay(AccountingDate accounting, LocalDate iso) {
        assertEquals(accounting, INSTANCE.dateEpochDay(iso.toEpochDay()));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_AccountingDate_toEpochDay(AccountingDate accounting, LocalDate iso) {
        assertEquals(iso.toEpochDay(), accounting.toEpochDay());
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_AccountingDate_until_CoptiDate(AccountingDate accounting, LocalDate iso) {
        assertEquals(INSTANCE.period(0, 0, 0), accounting.until(accounting));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_AccountingDate_until_LocalDate(AccountingDate accounting, LocalDate iso) {
        assertEquals(INSTANCE.period(0, 0, 0), accounting.until(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_until_CoptiDate(AccountingDate accounting, LocalDate iso) {
        assertEquals(Period.ZERO, iso.until(accounting));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Chronology_date_Temporal(AccountingDate accounting, LocalDate iso) {
        assertEquals(accounting, INSTANCE.date(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_plusDays(AccountingDate accounting, LocalDate iso) {
        assertEquals(iso, LocalDate.from(accounting.plus(0, DAYS)));
        assertEquals(iso.plusDays(1), LocalDate.from(accounting.plus(1, DAYS)));
        assertEquals(iso.plusDays(35), LocalDate.from(accounting.plus(35, DAYS)));
        assertEquals(iso.plusDays(-1), LocalDate.from(accounting.plus(-1, DAYS)));
        assertEquals(iso.plusDays(-60), LocalDate.from(accounting.plus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_minusDays(AccountingDate accounting, LocalDate iso) {
        assertEquals(iso, LocalDate.from(accounting.minus(0, DAYS)));
        assertEquals(iso.minusDays(1), LocalDate.from(accounting.minus(1, DAYS)));
        assertEquals(iso.minusDays(35), LocalDate.from(accounting.minus(35, DAYS)));
        assertEquals(iso.minusDays(-1), LocalDate.from(accounting.minus(-1, DAYS)));
        assertEquals(iso.minusDays(-60), LocalDate.from(accounting.minus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_until_DAYS(AccountingDate accounting, LocalDate iso) {
        assertEquals(0, accounting.until(iso.plusDays(0), DAYS));
        assertEquals(1, accounting.until(iso.plusDays(1), DAYS));
        assertEquals(35, accounting.until(iso.plusDays(35), DAYS));
        assertEquals(-40, accounting.until(iso.minusDays(40), DAYS));
    }

    public static Object[][] data_badDates() {
        return new Object[][] {
            {2012, 0, 0},

            {2012, -1, 1},
            {2012, 0, 1},
            {2012, 14, 1},
            {2012, 15, 1},

            {2012, 1, -1},
            {2012, 1, 0},
            {2012, 1, 29},
            {2012, 13, -1},
            {2012, 13, 0},
            {2012, 13, 36},
            {2012, 13, 37},
            {2012, 13, 38},

            {2011, 13, -1},
            {2011, 13, 0},
            {2011, 13, 29},
            {2011, 13, 30},
            {2011, 13, 31},
            {2011, 13, 32},
            {2011, 13, 33},
            {2011, 13, 34},
            {2011, 13, 35},

            {2012, 2, 29},
            {2012, 3, 29},
            {2012, 4, 29},
            {2012, 5, 29},
            {2012, 6, 29},
            {2012, 7, 29},
            {2012, 8, 29},
            {2012, 9, 29},
            {2012, 10, 29},
            {2012, 11, 29},
            {2012, 12, 29},
        };
    }

    @ParameterizedTest
    @MethodSource("data_badDates")
    public void test_badDates(int year, int month, int dom) {
        assertThrows(DateTimeException.class, () -> INSTANCE.date(year, month, dom));
    }

    @Test
    public void test_chronology_dateYearDay_badDate() {
        assertThrows(DateTimeException.class, () -> INSTANCE.dateYearDay(2001, 366));
    }

    @Test
    public void test_date_create_no_chronology() {
        assertThrows(NullPointerException.class, () -> AccountingDate.create(null, 2012, 1, 1));
    }

    @Test
    public void test_date_from_no_chronology() {
        assertThrows(NullPointerException.class, () -> AccountingDate.from(null, LocalDate.of(2012, 1, 1)));
    }

    @Test
    public void test_date_now_no_chronology() {
        assertThrows(NullPointerException.class, () -> AccountingDate.now(null));
    }

    @Test
    public void test_date_of_no_chronology() {
        assertThrows(NullPointerException.class, () -> AccountingDate.of(null, 2012, 1, 1));
    }

    @Test
    public void test_date_ofEpochDay_no_chronology() {
        assertThrows(NullPointerException.class, () -> AccountingDate.ofEpochDay(null, 0));
    }

    @Test
    public void test_date_ofYearDay_no_chronology() {
        assertThrows(NullPointerException.class, () -> AccountingDate.ofYearDay(null, 0, 1));
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        IntPredicate isLeapYear = year -> {
            LocalDate currentYearEnd = LocalDate.of(year, 9, 3).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            LocalDate prevYearEnd = LocalDate.of(year - 1, 9, 3).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };
        for (int year = -200; year < 200; year++) {
            AccountingDate base = INSTANCE.date(year, 1, 1);
            assertEquals(isLeapYear.test(year), base.isLeapYear());
            assertEquals(isLeapYear.test(year), INSTANCE.isLeapYear(year));
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(false, INSTANCE.isLeapYear(8));
        assertEquals(false, INSTANCE.isLeapYear(7));
        assertEquals(true, INSTANCE.isLeapYear(6));
        assertEquals(false, INSTANCE.isLeapYear(5));
        assertEquals(false, INSTANCE.isLeapYear(4));
        assertEquals(false, INSTANCE.isLeapYear(3));
        assertEquals(false, INSTANCE.isLeapYear(2));
        assertEquals(false, INSTANCE.isLeapYear(1));
        assertEquals(true, INSTANCE.isLeapYear(0));
        assertEquals(false, INSTANCE.isLeapYear(-1));
        assertEquals(false, INSTANCE.isLeapYear(-2));
        assertEquals(false, INSTANCE.isLeapYear(-3));
        assertEquals(false, INSTANCE.isLeapYear(-4));
        assertEquals(true, INSTANCE.isLeapYear(-5));
        assertEquals(false, INSTANCE.isLeapYear(-6));
    }

    public static Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {2012, 1, 28},
            {2012, 2, 28},
            {2012, 3, 28},
            {2012, 4, 28},
            {2012, 5, 28},
            {2012, 6, 28},
            {2012, 7, 28},
            {2012, 8, 28},
            {2012, 9, 28},
            {2012, 10, 28},
            {2012, 11, 28},
            {2012, 12, 28},
            {2012, 13, 35},

            {2013, 13, 28},
            {2014, 13, 28},
            {2015, 13, 28},
            {2016, 13, 28},
        };
    }

    @ParameterizedTest
    @MethodSource("data_lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(length, INSTANCE.date(year, month, 1).lengthOfMonth());
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = -200; year < 200; year++) {
            AccountingDate base = INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            AccountingEra era = (year <= 0 ? AccountingEra.BCE : AccountingEra.CE);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            AccountingDate eraBased = INSTANCE.date(era, yoe, 1, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = -200; year < 200; year++) {
            AccountingDate base = INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            AccountingEra era = (year <= 0 ? AccountingEra.BCE : AccountingEra.CE);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            AccountingDate eraBased = INSTANCE.dateYearDay(era, yoe, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(4, INSTANCE.prolepticYear(AccountingEra.CE, 4));
        assertEquals(3, INSTANCE.prolepticYear(AccountingEra.CE, 3));
        assertEquals(2, INSTANCE.prolepticYear(AccountingEra.CE, 2));
        assertEquals(1, INSTANCE.prolepticYear(AccountingEra.CE, 1));
        assertEquals(0, INSTANCE.prolepticYear(AccountingEra.BCE, 1));
        assertEquals(-1, INSTANCE.prolepticYear(AccountingEra.BCE, 2));
        assertEquals(-2, INSTANCE.prolepticYear(AccountingEra.BCE, 3));
        assertEquals(-3, INSTANCE.prolepticYear(AccountingEra.BCE, 4));
    }

    @Test
    public void test_prolepticYear_badEra() {
        assertThrows(ClassCastException.class, () -> INSTANCE.prolepticYear(IsoEra.CE, 4));
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(AccountingEra.CE, INSTANCE.eraOf(1));
        assertEquals(AccountingEra.BCE, INSTANCE.eraOf(0));
    }

    @Test
    public void test_Chronology_eraOf_invalid() {
        assertThrows(DateTimeException.class, () -> INSTANCE.eraOf(2));
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = INSTANCE.eras();
        assertEquals(2, eras.size());
        assertEquals(true, eras.contains(AccountingEra.BCE));
        assertEquals(true, eras.contains(AccountingEra.CE));
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(ValueRange.of(1, 7), INSTANCE.range(DAY_OF_WEEK));
        assertEquals(ValueRange.of(1, 28, 35), INSTANCE.range(DAY_OF_MONTH));
        assertEquals(ValueRange.of(1, 364, 371), INSTANCE.range(DAY_OF_YEAR));
        assertEquals(ValueRange.of(1, 13), INSTANCE.range(MONTH_OF_YEAR));
        assertEquals(ValueRange.of(1, 52, 53), INSTANCE.range(ALIGNED_WEEK_OF_YEAR));
    }

    //-----------------------------------------------------------------------
    // AccountingDate.range
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
            {2012, 13, 23, DAY_OF_MONTH, 1, 35},
            {2012, 1, 23, DAY_OF_YEAR, 1, 371},
            {2012, 12, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},
            {2012, 13, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {2013, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},

            {2011, 13, 23, DAY_OF_MONTH, 1, 28},
            {2011, 13, 23, DAY_OF_YEAR, 1, 364},
            {2011, 13, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},
        };
    }

    @ParameterizedTest
    @MethodSource("data_ranges")
    public void test_range(int year, int month, int dom, TemporalField field, int expectedMin, int expectedMax) {
        assertEquals(ValueRange.of(expectedMin, expectedMax), INSTANCE.date(year, month, dom).range(field));
    }

    @Test
    public void test_range_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> INSTANCE.date(2012, 6, 28).range(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // AccountingDate.getLong
    //-----------------------------------------------------------------------
    public static Object[][] data_getLong() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 5},
            {2014, 5, 26, DAY_OF_MONTH, 26},
            {2014, 5, 26, DAY_OF_YEAR, 28 + 28 + 28 + 28 + 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 20},
            {2014, 5, 26, MONTH_OF_YEAR, 5},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 13 + 5 - 1},
            {2014, 5, 26, YEAR, 2014},
            {2014, 5, 26, ERA, 1},
            {1, 6, 8, ERA, 1},
            {0, 6, 8, ERA, 0},

            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 5},
        };
    }

    @ParameterizedTest
    @MethodSource("data_getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(expected, INSTANCE.date(year, month, dom).getLong(field));
    }

    @Test
    public void test_getLong_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> INSTANCE.date(2012, 6, 28).getLong(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // AccountingDate.with
    //-----------------------------------------------------------------------
    public static Object[][] data_with() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 3, 2014, 5, 24},
            {2014, 5, 26, DAY_OF_WEEK, 5, 2014, 5, 26},
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
            {2014, 5, 26, PROLEPTIC_MONTH, 2013 * 13 + 3 - 1, 2013, 3, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 13 + 5 - 1, 2014, 5, 26},
            {2014, 5, 26, YEAR, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR, 2014, 2014, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2014, 2014, 5, 26},
            {2014, 5, 26, ERA, 0, -2013, 5, 26},
            {2014, 5, 26, ERA, 1, 2014, 5, 26},

            {2011, 3, 28, MONTH_OF_YEAR, 13, 2011, 13, 28},
            {2012, 3, 28, MONTH_OF_YEAR, 13, 2012, 13, 28},
            {2012, 13, 35, MONTH_OF_YEAR, 6, 2012, 6, 28},
            {2012, 13, 35, YEAR, 2011, 2011, 13, 28},
            {-2013, 6, 8, YEAR_OF_ERA, 2012, -2011, 6, 8},
            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 3, 2014, 5, 24},

        };
    }

    @ParameterizedTest
    @MethodSource("data_with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(INSTANCE.date(expectedYear, expectedMonth, expectedDom), INSTANCE.date(year, month, dom).with(field, value));
    }

    @Test
    public void test_with_TemporalField_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> INSTANCE.date(2012, 6, 28).with(MINUTE_OF_DAY, 0));
    }

    //-----------------------------------------------------------------------
    // AccountingDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        AccountingDate base = INSTANCE.date(2012, 6, 23);
        AccountingDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(INSTANCE.date(2012, 6, 28), test);
    }

    @Test
    public void test_adjust2() {
        AccountingDate base = INSTANCE.date(2012, 13, 23);
        AccountingDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(INSTANCE.date(2012, 13, 35), test);
    }

    //-----------------------------------------------------------------------
    // AccountingDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        AccountingDate accounting = INSTANCE.date(2000, 1, 4);
        AccountingDate test = accounting.with(LocalDate.of(2012, 7, 6));
        assertEquals(INSTANCE.date(2012, 12, 5), test);
    }

    @Test
    public void test_adjust_toMonth() {
        AccountingDate accounting = INSTANCE.date(2000, 1, 4);
        assertThrows(DateTimeException.class, () -> accounting.with(Month.APRIL));
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(AccountingDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToAccountingDate() {
        AccountingDate accounting = INSTANCE.date(2012, 6, 23);
        LocalDate test = LocalDate.MIN.with(accounting);
        assertEquals(LocalDate.of(2012, 2, 7), test);
    }

    @Test
    public void test_LocalDateTime_adjustToAccountingDate() {
        AccountingDate accounting = INSTANCE.date(2012, 6, 23);
        LocalDateTime test = LocalDateTime.MIN.with(accounting);
        assertEquals(LocalDateTime.of(2012, 2, 7, 0, 0), test);
    }

    //-----------------------------------------------------------------------
    // AccountingDate.plus
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
        };
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(INSTANCE.date(expectedYear, expectedMonth, expectedDom), INSTANCE.date(year, month, dom).plus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(INSTANCE.date(expectedYear, expectedMonth, expectedDom), INSTANCE.date(year, month, dom).minus(amount, unit));
    }

    @Test
    public void test_plus_TemporalUnit_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> INSTANCE.date(2012, 6, 28).plus(0, MINUTES));
    }

    //-----------------------------------------------------------------------
    // AccountingDate.until
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
        };
    }

    @ParameterizedTest
    @MethodSource("data_until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        AccountingDate start = INSTANCE.date(year1, month1, dom1);
        AccountingDate end = INSTANCE.date(year2, month2, dom2);
        assertEquals(expected, start.until(end, unit));
    }

    @Test
    public void test_until_TemporalUnit_unsupported() {
        AccountingDate start = INSTANCE.date(2012, 6, 28);
        AccountingDate end = INSTANCE.date(2012, 7, 1);
        assertThrows(UnsupportedTemporalTypeException.class, () -> start.until(end, MINUTES));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(INSTANCE.date(2014, 8, 1), INSTANCE.date(2014, 5, 26).plus(INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_plus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> INSTANCE.date(2014, 5, 26).plus(Period.ofMonths(2)));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(INSTANCE.date(2014, 3, 23), INSTANCE.date(2014, 5, 26).minus(INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_minus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> INSTANCE.date(2014, 5, 26).minus(Period.ofMonths(2)));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        AccountingChronology other = new AccountingChronologyBuilder()
            .endsOn(DayOfWeek.WEDNESDAY)
            .nearestEndOf(Month.AUGUST)
            .leapWeekInMonth(13)
            .withDivision(AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS)
            .accountingYearEndsInIsoYear()
            .toChronology();
        new EqualsTester()
            .addEqualityGroup(INSTANCE.date(2000, 1, 3), INSTANCE.date(2000, 1, 3))
            .addEqualityGroup(INSTANCE.date(2000, 1, 4), INSTANCE.date(2000, 1, 4))
            .addEqualityGroup(INSTANCE.date(2000, 2, 3), INSTANCE.date(2000, 2, 3))
            .addEqualityGroup(INSTANCE.date(2001, 1, 3), INSTANCE.date(2001, 1, 3))
            .addEqualityGroup(other.date(2000, 1, 3), other.date(2000, 1, 3))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public static Object[][] data_toString() {
        AccountingChronology other = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST).
        withDivision(AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS).leapWeekInMonth(13).accountingYearStartsInIsoYear().toChronology();

        return new Object[][] {
                { INSTANCE.date(1, 1, 1),
                        "Accounting calendar ends on SUNDAY nearest end of AUGUST, year divided in THIRTEEN_EVEN_MONTHS_OF_4_WEEKS with leap-week in month 13 ending in the given ISO year CE 1-01-01" },
                { INSTANCE.date(2012, 6, 23),
                        "Accounting calendar ends on SUNDAY nearest end of AUGUST, year divided in THIRTEEN_EVEN_MONTHS_OF_4_WEEKS with leap-week in month 13 ending in the given ISO year CE 2012-06-23" },
                { other.date(1, 1, 1),
                        "Accounting calendar ends on SUNDAY nearest end of AUGUST, year divided in THIRTEEN_EVEN_MONTHS_OF_4_WEEKS with leap-week in month 13 starting in the given ISO year CE 1-01-01" },
                { other.date(2012, 6, 23),
                        "Accounting calendar ends on SUNDAY nearest end of AUGUST, year divided in THIRTEEN_EVEN_MONTHS_OF_4_WEEKS with leap-week in month 13 starting in the given ISO year CE 2012-06-23" },
        };
    }

    @ParameterizedTest
    @MethodSource("data_toString")
    public void test_toString(AccountingDate accounting, String expected) {
        assertEquals(expected, accounting.toString());
    }

}
