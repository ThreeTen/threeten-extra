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

import static java.time.temporal.ChronoUnit.DAYS;
import static org.testng.Assert.assertEquals;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ValueRange;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestAccountingChronologyBuilder {

    //-----------------------------------------------------------------------
    // isLeapYear(), date(int, int, int)
    //-----------------------------------------------------------------------
    @DataProvider(name = "yearEnding")
    Object[][] data_yearEnding() {
        return new Object[][] {
            {DayOfWeek.MONDAY, Month.JANUARY},
            {DayOfWeek.TUESDAY, Month.MARCH},
            {DayOfWeek.WEDNESDAY, Month.APRIL},
            {DayOfWeek.THURSDAY, Month.MAY},
            {DayOfWeek.FRIDAY, Month.JUNE},
            {DayOfWeek.SATURDAY, Month.JULY},
            {DayOfWeek.SUNDAY, Month.AUGUST},
            {DayOfWeek.MONDAY, Month.SEPTEMBER},
            {DayOfWeek.TUESDAY, Month.OCTOBER},
            {DayOfWeek.WEDNESDAY, Month.NOVEMBER},
            {DayOfWeek.THURSDAY, Month.DECEMBER},

            {DayOfWeek.MONDAY, Month.FEBRUARY},
            {DayOfWeek.TUESDAY, Month.FEBRUARY},
            {DayOfWeek.WEDNESDAY, Month.FEBRUARY},
            {DayOfWeek.THURSDAY, Month.FEBRUARY},
            {DayOfWeek.FRIDAY, Month.FEBRUARY},
            {DayOfWeek.SATURDAY, Month.FEBRUARY},
            {DayOfWeek.SUNDAY, Month.FEBRUARY},
        };
    }

    @Test(dataProvider = "yearEnding")
    public void test_isLeapYear_inLastWeekOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        Predicate<Integer> isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = -200; year < 600; year++) {
            assertEquals(chronology.isLeapYear(year), isLeapYear.test(year), "Fails on " + year);
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_isLeapYear_nearestEndOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 3).plusMonths(1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        Predicate<Integer> isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = -200; year < 600; year++) {
            assertEquals(chronology.isLeapYear(year), isLeapYear.test(year), "Fails on " + year);
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_previousLeapYears_inLastWeekOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        Predicate<Integer> isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = 1, leapYears = 0; year < 600; year++) {
            if (year != 1 && isLeapYear.test(year - 1)) {
                leapYears++;
            }
            assertEquals(chronology.previousLeapYears(year), leapYears, "Fails on " + year);
        }
        for (int year = 1, leapYears = 0; year >= -200; year--) {
            if (year != 1 && isLeapYear.test(year)) {
                leapYears--;
            }
            assertEquals(chronology.previousLeapYears(year), leapYears, "Fails on " + year);
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_previousLeapYears_nearestEndOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 3).plusMonths(1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        Predicate<Integer> isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = 1, leapYears = 0; year < 600; year++) {
            if (year != 1 && isLeapYear.test(year - 1)) {
                leapYears++;
            }
            assertEquals(chronology.previousLeapYears(year), leapYears, "Fails on " + year);
        }
        for (int year = 1, leapYears = 0; year >= -200; year--) {
            if (year != 1 && isLeapYear.test(year)) {
                leapYears--;
            }
            assertEquals(chronology.previousLeapYears(year), leapYears, "Fails on " + year);
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_date_int_int_int_inLastWeekOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };

        for (int year = -200; year < 600; year++) {
            assertEquals(chronology.date(year, 1, 1).toEpochDay(), getYearEnd.apply(year - 1).plusDays(1).toEpochDay());
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_date_int_int_int_nearestEndOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 3).plusMonths(1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };

        for (int year = -200; year < 600; year++) {
            assertEquals(chronology.date(year, 1, 1).toEpochDay(), getYearEnd.apply(year - 1).plusDays(1).toEpochDay());
        }
    }

    //-----------------------------------------------------------------------
    // range(MONTH_OF_YEAR), range(DAY_OF_MONTH)
    //-----------------------------------------------------------------------
    @DataProvider(name = "range")
    Object[][] data_range() {
        return new Object[][] {
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 1,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 2,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 3,
                ValueRange.of(1, 4, 6), ValueRange.of(1, 28, 42), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 4,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 5,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 6,
                ValueRange.of(1, 4, 6), ValueRange.of(1, 28, 42), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 7,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 8,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 9,
                ValueRange.of(1, 4, 6), ValueRange.of(1, 28, 42), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 10,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 11,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 12,
                ValueRange.of(1, 4, 6), ValueRange.of(1, 28, 42), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},

            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS, 1,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS, 2,
                ValueRange.of(1, 4, 6), ValueRange.of(1, 28, 42), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS, 3,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},

            {AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS, 1,
                ValueRange.of(1, 4, 6), ValueRange.of(1, 28, 42), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS, 2,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS, 3,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)},

            {AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, 1,
                ValueRange.of(1, 4, 5), ValueRange.of(1, 28, 35), ValueRange.of(1, 13), ValueRange.of(-999_999 * 13L, 999_999 * 13L + 12)},
        };
    }

    @Test(dataProvider = "range")
    public void test_range(AccountingYearDivision division, int leapWeekInMonth,
            ValueRange expectedWeekOfMonthRange, ValueRange expectedDayOfMonthRange, ValueRange expectedMonthRange, ValueRange expectedProlepticMonthRange) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        assertEquals(chronology.range(ChronoField.ALIGNED_WEEK_OF_MONTH), expectedWeekOfMonthRange);
        assertEquals(chronology.range(ChronoField.DAY_OF_MONTH), expectedDayOfMonthRange);
        assertEquals(chronology.range(ChronoField.DAY_OF_YEAR), ValueRange.of(1, 364, 371));
        assertEquals(chronology.range(ChronoField.MONTH_OF_YEAR), expectedMonthRange);
        assertEquals(chronology.range(ChronoField.PROLEPTIC_MONTH), expectedProlepticMonthRange);
    }

    @Test(dataProvider = "weeksInMonth")
    public void test_date_dayOfMonth_range(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        for (int month = 1; month <= weeksInMonth.length; month++) {
            assertEquals(AccountingDate.of(chronology, 2011, month, 15).range(ChronoField.DAY_OF_MONTH), ValueRange.of(1, weeksInMonth[month - 1] * 7));
            assertEquals(AccountingDate.of(chronology, 2012, month, 15).range(ChronoField.DAY_OF_MONTH), ValueRange.of(1, weeksInMonth[month - 1] * 7 + (month == leapWeekInMonth ? 7 : 0)));
            assertEquals(AccountingDate.of(chronology, 2011, month, 15).range(ChronoField.ALIGNED_WEEK_OF_MONTH), ValueRange.of(1, weeksInMonth[month - 1]));
            assertEquals(AccountingDate.of(chronology, 2012, month, 15).range(ChronoField.ALIGNED_WEEK_OF_MONTH), ValueRange.of(1, weeksInMonth[month - 1] + (month == leapWeekInMonth ? 1 : 0)));
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_date_dayOfYear_inLastWeekOf_range(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        Predicate<Integer> isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = 2007; year < 2015; year++) {
            assertEquals(AccountingDate.of(chronology, year, 3, 5).range(ChronoField.DAY_OF_YEAR), ValueRange.of(1, isLeapYear.test(year) ? 371 : 364));
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_date_dayOfYear_nearestEndOf_range(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 3).plusMonths(1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        Predicate<Integer> isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = 2007; year < 2015; year++) {
            assertEquals(AccountingDate.of(chronology, year, 3, 5).range(ChronoField.DAY_OF_YEAR), ValueRange.of(1, isLeapYear.test(year) ? 371 : 364));
        }
    }

    //-----------------------------------------------------------------------
    // getWeeksInMonth(month), 
    // getWeeksAtStartOfMonth(weeks), getMonthFromElapsedWeeks(weeks)
    //-----------------------------------------------------------------------
    @DataProvider(name = "weeksInMonth")
    Object[][] data_weeksInMonth() {
        return new Object[][] {
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 1},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 2},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 3},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 4},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 5},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 6},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 7},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 8},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 9},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 10},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 11},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}, 12},

            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS, new int[] {4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4}, 1},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS, new int[] {4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4}, 2},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS, new int[] {4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4}, 3},

            {AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS, new int[] {5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4}, 1},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS, new int[] {5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4}, 2},
            {AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS, new int[] {5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4}, 3},

            {AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4}, 13},
        };
    }

    @Test(dataProvider = "weeksInMonth")
    public void test_getWeeksInMonth(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        for (int month = 1; month <= weeksInMonth.length; month++) {
            assertEquals(chronology.division.getWeeksInMonth(month), weeksInMonth[month - 1]);
            assertEquals(chronology.division.getWeeksInMonth(month, leapWeekInMonth), weeksInMonth[month - 1] + (month == leapWeekInMonth ? 1 : 0));
        }
    }

    @Test(dataProvider = "weeksInMonth")
    public void test_getWeeksAtStartOf(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        for (int month = 1, elapsedWeeks = 0; month <= weeksInMonth.length; elapsedWeeks += weeksInMonth[month - 1], month++) {
            assertEquals(chronology.division.getWeeksAtStartOfMonth(month), elapsedWeeks);
            assertEquals(chronology.division.getWeeksAtStartOfMonth(month, leapWeekInMonth), elapsedWeeks + (month > leapWeekInMonth ? 1 : 0));
        }
    }

    @Test(dataProvider = "weeksInMonth")
    public void test_getMonthFromElapsedWeeks(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        for (int month = 1, elapsedWeeks = 0; month <= weeksInMonth.length; elapsedWeeks += weeksInMonth[month - 1], month++) {
            for (int i = 0; i < weeksInMonth[month - 1]; i++) {
                assertEquals(chronology.division.getMonthFromElapsedWeeks(elapsedWeeks + i), month);
                assertEquals(chronology.division.getMonthFromElapsedWeeks(elapsedWeeks + i + (month > leapWeekInMonth ? 1 : 0), leapWeekInMonth), month);
                if (month == leapWeekInMonth && i == weeksInMonth[month - 1] - 1) {
                    assertEquals(chronology.division.getMonthFromElapsedWeeks(elapsedWeeks + i + 1, leapWeekInMonth), month);
                }
            }
        }
    }

    @Test(dataProvider = "weeksInMonth", expectedExceptions = DateTimeException.class)
    public void test_negativeWeeks_getMonthFromElapsedWeekspublic(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        assertEquals(division.getMonthFromElapsedWeeks(0), 1);
        division.getMonthFromElapsedWeeks(-1);
    }

    @Test(dataProvider = "weeksInMonth", expectedExceptions = DateTimeException.class)
    public void test_extraWeeks_getMonthFromElapsedWeekspublic(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        int elapsedWeeks = 0;
        for (int month = 1; month <= weeksInMonth.length; month++) {
            elapsedWeeks += weeksInMonth[month - 1];
        }
        assertEquals(division.getMonthFromElapsedWeeks(elapsedWeeks), weeksInMonth.length);
        division.getMonthFromElapsedWeeks(elapsedWeeks + 1);
    }

    @Test(dataProvider = "weeksInMonth", expectedExceptions = DateTimeException.class)
    public void test_extraWeeksLeap_getMonthFromElapsedWeekspublic(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        int elapsedWeeks = 1;
        for (int month = 1; month <= weeksInMonth.length; month++) {
            elapsedWeeks += weeksInMonth[month - 1];
        }
        assertEquals(division.getMonthFromElapsedWeeks(elapsedWeeks, leapWeekInMonth), weeksInMonth.length);
        division.getMonthFromElapsedWeeks(elapsedWeeks + 1, leapWeekInMonth);
    }

    //-----------------------------------------------------------------------
    // toChronology() failures.
    //-----------------------------------------------------------------------
    @DataProvider(name = "badChronology")
    Object[][] data_badChronology() {
        return new Object[][] {
            {DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, 0},
            {DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, -1},
            {DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, 14},
            {DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 13},
            {DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS, 13},
            {DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS, 13},
            {DayOfWeek.MONDAY, Month.JANUARY, null, 13},
            {DayOfWeek.MONDAY, null, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, 13},
            {null, Month.JANUARY, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, 13},

        };
    }

    @Test(dataProvider = "badChronology", expectedExceptions = IllegalStateException.class)
    public void test_badChronology_nearestEndOf(DayOfWeek dayOfWeek, Month ending, AccountingYearDivision division, int leapWeekInMonth) {
        new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();
    }

    @Test(dataProvider = "badChronology", expectedExceptions = IllegalStateException.class)
    public void test_badChronology_inLastWeekOf(DayOfWeek dayOfWeek, Month ending, AccountingYearDivision division, int leapWeekInMonth) {
        new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();
    }

}
