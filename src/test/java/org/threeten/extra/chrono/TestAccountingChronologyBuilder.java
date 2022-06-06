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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ValueRange;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

/**
 * Test.
 */
public class TestAccountingChronologyBuilder {

    //-----------------------------------------------------------------------
    // isLeapYear(), date(int, int, int)
    //-----------------------------------------------------------------------
    public static Stream<Arguments> data_yearEnding() {
        return Lists.cartesianProduct(
                Lists.newArrayList(DayOfWeek.values()),
                Lists.newArrayList(Month.values())).stream().map(
                        (args) -> arguments(args.toArray()));
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_isLeapYear_inLastWeekOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        IntPredicate isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = -200; year < 600; year++) {
            assertEquals(isLeapYear.test(year), chronology.isLeapYear(year));
        }
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_isLeapYear_nearestEndOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 3).plusMonths(1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        IntPredicate isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = -200; year < 600; year++) {
            assertEquals(isLeapYear.test(year), chronology.isLeapYear(year));
        }
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_previousLeapYears_inLastWeekOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        IntPredicate isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = 1, leapYears = 0; year < 600; year++) {
            if (year != 1 && isLeapYear.test(year - 1)) {
                leapYears++;
            }
            assertEquals(leapYears, chronology.previousLeapYears(year));
        }
        for (int year = 1, leapYears = 0; year >= -200; year--) {
            if (year != 1 && isLeapYear.test(year)) {
                leapYears--;
            }
            assertEquals(leapYears, chronology.previousLeapYears(year));
        }
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_previousLeapYears_nearestEndOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 3).plusMonths(1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        IntPredicate isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = 1, leapYears = 0; year < 600; year++) {
            if (year != 1 && isLeapYear.test(year - 1)) {
                leapYears++;
            }
            assertEquals(leapYears, chronology.previousLeapYears(year));
        }
        for (int year = 1, leapYears = 0; year >= -200; year--) {
            if (year != 1 && isLeapYear.test(year)) {
                leapYears--;
            }
            assertEquals(leapYears, chronology.previousLeapYears(year));
        }
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_date_int_int_int_inLastWeekOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };

        for (int year = -200; year < 600; year++) {
            assertEquals(getYearEnd.apply(year - 1).plusDays(1).toEpochDay(), chronology.date(year, 1, 1).toEpochDay());
        }
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_date_int_int_int_nearestEndOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 3).plusMonths(1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };

        for (int year = -200; year < 600; year++) {
            assertEquals(getYearEnd.apply(year - 1).plusDays(1).toEpochDay(), chronology.date(year, 1, 1).toEpochDay());
        }
    }

    //-----------------------------------------------------------------------
    // range(MONTH_OF_YEAR), range(DAY_OF_MONTH)
    //-----------------------------------------------------------------------
    public static Stream<Arguments> data_range() {
        IntBinaryOperator weeksInMonth = (leapWeekInMonth, offset) -> (leapWeekInMonth + offset) % 3 == 0 ? 6 : 5;

        Stream<Arguments> pattern_4_4_5 = IntStream.range(1, 13)
                .mapToObj((leapWeekInMonth) -> arguments(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS,
                        leapWeekInMonth,
                        ValueRange.of(1, 4, weeksInMonth.applyAsInt(leapWeekInMonth, 0)),
                        ValueRange.of(1, 7 * 4, 7 * weeksInMonth.applyAsInt(leapWeekInMonth, 0)),
                        ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)));

        Stream<Arguments> pattern_4_5_4 = IntStream.range(1, 13)
                .mapToObj((leapWeekInMonth) -> arguments(AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS,
                        leapWeekInMonth,
                        ValueRange.of(1, 4, weeksInMonth.applyAsInt(leapWeekInMonth, 1)),
                        ValueRange.of(1, 7 * 4, 7 * weeksInMonth.applyAsInt(leapWeekInMonth, 1)),
                        ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)));

        Stream<Arguments> pattern_5_4_4 = IntStream.range(1, 13)
                .mapToObj((leapWeekInMonth) -> arguments(AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS,
                        leapWeekInMonth,
                        ValueRange.of(1, 4, weeksInMonth.applyAsInt(leapWeekInMonth, 2)),
                        ValueRange.of(1, 7 * 4, 7 * weeksInMonth.applyAsInt(leapWeekInMonth, 2)),
                        ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)));

        Stream<Arguments> pattern_even_13 = IntStream.range(1, 14)
                .mapToObj((leapWeekInMonth) -> arguments(AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS,
                        leapWeekInMonth,
                        ValueRange.of(1, 4, 5),
                        ValueRange.of(1, 7 * 4, 7 * 5),
                        ValueRange.of(1, 13), ValueRange.of(-999_999 * 13L, 999_999 * 13L + 12)));

        return Streams.concat(pattern_4_4_5, pattern_4_5_4, pattern_5_4_4, pattern_even_13);
    }

    @ParameterizedTest
    @MethodSource("data_range")
    public void test_range(AccountingYearDivision division, int leapWeekInMonth,
            ValueRange expectedWeekOfMonthRange, ValueRange expectedDayOfMonthRange, ValueRange expectedMonthRange, ValueRange expectedProlepticMonthRange) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        assertEquals(expectedWeekOfMonthRange, chronology.range(ChronoField.ALIGNED_WEEK_OF_MONTH));
        assertEquals(expectedDayOfMonthRange, chronology.range(ChronoField.DAY_OF_MONTH));
        assertEquals(ValueRange.of(1, 364, 371), chronology.range(ChronoField.DAY_OF_YEAR));
        assertEquals(expectedMonthRange, chronology.range(ChronoField.MONTH_OF_YEAR));
        assertEquals(expectedProlepticMonthRange, chronology.range(ChronoField.PROLEPTIC_MONTH));
    }

    @ParameterizedTest
    @MethodSource("data_weeksInMonth")
    public void test_date_dayOfMonth_range(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        for (int month = 1; month <= weeksInMonth.length; month++) {
            assertEquals(ValueRange.of(1, weeksInMonth[month - 1] * 7), AccountingDate.of(chronology, 2011, month, 15).range(ChronoField.DAY_OF_MONTH));
            assertEquals(ValueRange.of(1, weeksInMonth[month - 1] * 7 + (month == leapWeekInMonth ? 7 : 0)), AccountingDate.of(chronology, 2012, month, 15).range(ChronoField.DAY_OF_MONTH));
            assertEquals(ValueRange.of(1, weeksInMonth[month - 1]), AccountingDate.of(chronology, 2011, month, 15).range(ChronoField.ALIGNED_WEEK_OF_MONTH));
            assertEquals(ValueRange.of(1, weeksInMonth[month - 1] + (month == leapWeekInMonth ? 1 : 0)), AccountingDate.of(chronology, 2012, month, 15).range(ChronoField.ALIGNED_WEEK_OF_MONTH));
        }
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_date_dayOfYear_inLastWeekOf_range(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        IntPredicate isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = 2007; year < 2015; year++) {
            assertEquals(ValueRange.of(1, isLeapYear.test(year) ? 371 : 364), AccountingDate.of(chronology, year, 3, 5).range(ChronoField.DAY_OF_YEAR));
        }
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_date_dayOfYear_nearestEndOf_range(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInMonth(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 3).plusMonths(1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        IntPredicate isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate prevYearEnd = getYearEnd.apply(year - 1);
            return prevYearEnd.until(currentYearEnd, DAYS) == 371;
        };

        for (int year = 2007; year < 2015; year++) {
            assertEquals(ValueRange.of(1, isLeapYear.test(year) ? 371 : 364), AccountingDate.of(chronology, year, 3, 5).range(ChronoField.DAY_OF_YEAR));
        }
    }

    //-----------------------------------------------------------------------
    // getWeeksInMonth(month),
    // getWeeksAtStartOfMonth(weeks), getMonthFromElapsedWeeks(weeks)
    //-----------------------------------------------------------------------
    public static Stream<Arguments> data_weeksInMonth() {
        Stream<Arguments> pattern_4_4_5 = IntStream.range(1, 13)
                .mapToObj((leapWeekInMonth) -> arguments(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS,
                        new int[] { 4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5 }, leapWeekInMonth));

        Stream<Arguments> pattern_4_5_4 = IntStream.range(1, 13)
                .mapToObj((leapWeekInMonth) -> arguments(AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS,
                        new int[] { 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4 }, leapWeekInMonth));

        Stream<Arguments> pattern_5_4_4 = IntStream.range(1, 13)
                .mapToObj((leapWeekInMonth) -> arguments(AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS,
                        new int[] { 5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4 }, leapWeekInMonth));

        Stream<Arguments> pattern_even_13 = IntStream.range(1, 14)
                .mapToObj((leapWeekInMonth) -> arguments(AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS,
                        new int[] { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, leapWeekInMonth));

        return Streams.concat(pattern_4_4_5, pattern_4_5_4, pattern_5_4_4, pattern_even_13);
    }

    @ParameterizedTest
    @MethodSource("data_weeksInMonth")
    public void test_getWeeksInMonth(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        for (int month = 1; month <= weeksInMonth.length; month++) {
            assertEquals(weeksInMonth[month - 1], chronology.getDivision().getWeeksInMonth(month));
            assertEquals(weeksInMonth[month - 1] + (month == leapWeekInMonth ? 1 : 0), chronology.getDivision().getWeeksInMonth(month, leapWeekInMonth));
        }
    }

    @ParameterizedTest
    @MethodSource("data_weeksInMonth")
    public void test_getWeeksAtStartOf(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        for (int month = 1, elapsedWeeks = 0; month <= weeksInMonth.length; elapsedWeeks += weeksInMonth[month - 1], month++) {
            assertEquals(elapsedWeeks, chronology.getDivision().getWeeksAtStartOfMonth(month));
            assertEquals(elapsedWeeks + (month > leapWeekInMonth ? 1 : 0), chronology.getDivision().getWeeksAtStartOfMonth(month, leapWeekInMonth));
        }
    }

    @ParameterizedTest
    @MethodSource("data_weeksInMonth")
    public void test_getMonthFromElapsedWeeks(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(DayOfWeek.SUNDAY).nearestEndOf(Month.AUGUST)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology();

        for (int month = 1, elapsedWeeks = 0; month <= weeksInMonth.length; elapsedWeeks += weeksInMonth[month - 1], month++) {
            for (int i = 0; i < weeksInMonth[month - 1]; i++) {
                assertEquals(month, chronology.getDivision().getMonthFromElapsedWeeks(elapsedWeeks + i));
                assertEquals(month, chronology.getDivision().getMonthFromElapsedWeeks(elapsedWeeks + i + (month > leapWeekInMonth ? 1 : 0), leapWeekInMonth));
                if (month == leapWeekInMonth && i == weeksInMonth[month - 1] - 1) {
                    assertEquals(month, chronology.getDivision().getMonthFromElapsedWeeks(elapsedWeeks + i + 1, leapWeekInMonth));
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("data_weeksInMonth")
    public void test_negativeWeeks_getMonthFromElapsedWeekspublic(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        assertEquals(1, division.getMonthFromElapsedWeeks(0));
        assertThrows(DateTimeException.class, () -> division.getMonthFromElapsedWeeks(-1));
    }

    @ParameterizedTest
    @MethodSource("data_weeksInMonth")
    public void test_extraWeeks_getMonthFromElapsedWeekspublic(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        int elapsedWeeks = 0;
        for (int month = 1; month <= weeksInMonth.length; month++) {
            elapsedWeeks += weeksInMonth[month - 1];
        }
        int finalElapsedWeeks = elapsedWeeks;
        assertThrows(DateTimeException.class, () -> division.getMonthFromElapsedWeeks(finalElapsedWeeks));
        assertThrows(DateTimeException.class, () -> division.getMonthFromElapsedWeeks(finalElapsedWeeks + 1));
    }

    @ParameterizedTest
    @MethodSource("data_weeksInMonth")
    public void test_extraWeeksLeap_getMonthFromElapsedWeekspublic(AccountingYearDivision division, int[] weeksInMonth, int leapWeekInMonth) {
        int elapsedWeeks = 1;
        for (int month = 1; month <= weeksInMonth.length; month++) {
            elapsedWeeks += weeksInMonth[month - 1];
        }
        int finalElapsedWeeks = elapsedWeeks;
        assertThrows(DateTimeException.class, () -> division.getMonthFromElapsedWeeks(finalElapsedWeeks, leapWeekInMonth));
        assertThrows(DateTimeException.class, () -> division.getMonthFromElapsedWeeks(finalElapsedWeeks + 1, leapWeekInMonth));
    }

    //-----------------------------------------------------------------------
    // toChronology() failures.
    //-----------------------------------------------------------------------
    public static Stream<Arguments> data_badChronology() {
        return Stream.of(
                arguments(DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, 0),
                arguments(DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, -1),
                arguments(DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, 14),
                arguments(DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS, 13),
                arguments(DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS, 13),
                arguments(DayOfWeek.MONDAY, Month.JANUARY, AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS, 13),
                arguments(DayOfWeek.MONDAY, Month.JANUARY, null, 13),
                arguments(DayOfWeek.MONDAY, null, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, 13),
                arguments(null, Month.JANUARY, AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS, 13));
    }

    @ParameterizedTest
    @MethodSource("data_badChronology")
    public void test_badChronology_nearestEndOf(DayOfWeek dayOfWeek, Month ending, AccountingYearDivision division, int leapWeekInMonth) {
        assertThrows(IllegalStateException.class, () -> new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology());
    }

    @ParameterizedTest
    @MethodSource("data_badChronology")
    public void test_badChronology_inLastWeekOf(DayOfWeek dayOfWeek, Month ending, AccountingYearDivision division, int leapWeekInMonth) {
        assertThrows(IllegalStateException.class, () -> new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(division).leapWeekInMonth(leapWeekInMonth)
                .toChronology());
    }

}
