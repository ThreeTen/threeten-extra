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
import static org.junit.jupiter.api.Assertions.assertAll;
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
import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
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
    @FunctionalInterface
    private interface GetYearEnd {
        LocalDate apply(int year, Month ending, DayOfWeek dayOfWeek);
    }

    // -----------------------------------------------------------------------
    // isLeapYear(), date(int, int, int)
    // -----------------------------------------------------------------------
    public static Stream<Arguments> data_yearEnding() {
        return Lists.cartesianProduct(
                Lists.newArrayList(DayOfWeek.values()),
                Lists.newArrayList(Month.values()),
                Lists.newArrayList(new Object[] {
                        (BiFunction<AccountingChronologyBuilder, Month, AccountingChronologyBuilder>) AccountingChronologyBuilder::inLastWeekOf,
                        (GetYearEnd) ((year, ending, dayOfWeek) -> LocalDate.of(year, ending, 1)
                                .with(TemporalAdjusters.lastDayOfMonth())
                                .with(TemporalAdjusters.previousOrSame(dayOfWeek)))
                }, new Object[] {
                        (BiFunction<AccountingChronologyBuilder, Month, AccountingChronologyBuilder>) AccountingChronologyBuilder::nearestEndOf,
                        (GetYearEnd) ((year, ending, dayOfWeek) -> LocalDate.of(year, ending, 3).plusMonths(1)
                                .with(TemporalAdjusters.previousOrSame(dayOfWeek)))
                })).stream().map(
                        (args) -> {
                            DayOfWeek dayOfWeek = (DayOfWeek) args.get(0);
                            Month ending = (Month) args.get(1);
                            BiFunction<AccountingChronologyBuilder, Month, AccountingChronologyBuilder> endingType = 
                                (BiFunction<AccountingChronologyBuilder, Month, AccountingChronologyBuilder>) ((Object[]) args.get(2))[0];
                            GetYearEnd getYearEnd = (GetYearEnd) ((Object[]) args.get(2))[1];

                            AccountingChronology chrono = endingType.apply(new AccountingChronologyBuilder(), ending).endsOn(dayOfWeek)
                                    .withDivision(AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS)
                                    .leapWeekInMonth(12)
                                    .toChronology();

                            IntPredicate isLeapYear = year -> {
                                LocalDate currentYearEnd = getYearEnd.apply(year, ending, dayOfWeek);
                                LocalDate prevYearEnd = getYearEnd.apply(year - 1, ending, dayOfWeek);
                                return prevYearEnd.until(currentYearEnd, DAYS) == 371;
                            };

                            return arguments(chrono, getYearEnd, isLeapYear, dayOfWeek, ending);

                        });
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_isLeapYear(AccountingChronology chronology, GetYearEnd _getYearEnd, IntPredicate isLeapYear, DayOfWeek _dayOfWeek, Month _ending) {
        assertAll(IntStream.range(-200, 600).mapToObj(
                year -> () -> assertEquals(isLeapYear.test(year), chronology.isLeapYear(year),
                        () -> String.format("for year %d ", year))));
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_previousLeapYears(AccountingChronology chronology, GetYearEnd _getYearEnd, IntPredicate isLeapYear, DayOfWeek _dayOfWeek, Month _ending) {
        for (int year = 1, leapYears = 0; year < 600; year++) {
            if (year != 1 && isLeapYear.test(year - 1)) {
                leapYears++;
            }
            final int loopYear = year;
            assertEquals(leapYears, chronology.previousLeapYears(year), () -> String.format("for year %d ", loopYear));
        }
        for (int year = 1, leapYears = 0; year >= -200; year--) {
            if (year != 1 && isLeapYear.test(year)) {
                leapYears--;
            }
            final int loopYear = year;
            assertEquals(leapYears, chronology.previousLeapYears(year), () -> String.format("for year %d ", loopYear));
        }
    }

    @ParameterizedTest
    @MethodSource("data_yearEnding")
    public void test_date_int_int_int(AccountingChronology chronology, GetYearEnd getYearEnd, IntPredicate isLeapYear, DayOfWeek dayOfWeek, Month ending) {
        assertAll(IntStream.range(-200, 600).mapToObj(
            year -> () -> assertEquals(getYearEnd.apply(year - 1, ending, dayOfWeek).plusDays(1).toEpochDay(), chronology.date(year, 1, 1).toEpochDay(),
                    () -> String.format("for year %d ", year))));
    }

    //-----------------------------------------------------------------------
    // range(MONTH_OF_YEAR), range(DAY_OF_MONTH)
    //-----------------------------------------------------------------------
    public static Stream<Arguments> data_range() {
        IntBinaryOperator weeksInMonth = (leapWeekInMonth, offset) -> (leapWeekInMonth + offset) % 3 == 0 ? 6 : 5;

        Stream<Object[]> pattern_4_4_5 = IntStream.range(1, 13)
                .mapToObj((leapWeekInMonth) -> new Object[] { AccountingYearDivision.QUARTERS_OF_PATTERN_4_4_5_WEEKS,
                        leapWeekInMonth,
                        ValueRange.of(1, 4, weeksInMonth.applyAsInt(leapWeekInMonth, 0)),
                        ValueRange.of(1, 7 * 4, 7 * weeksInMonth.applyAsInt(leapWeekInMonth, 0)),
                        ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)});

        Stream<Object[]> pattern_4_5_4 = IntStream.range(1, 13)
                .mapToObj((leapWeekInMonth) -> new Object[] { AccountingYearDivision.QUARTERS_OF_PATTERN_4_5_4_WEEKS,
                        leapWeekInMonth,
                        ValueRange.of(1, 4, weeksInMonth.applyAsInt(leapWeekInMonth, 1)),
                        ValueRange.of(1, 7 * 4, 7 * weeksInMonth.applyAsInt(leapWeekInMonth, 1)),
                        ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)});

        Stream<Object[]> pattern_5_4_4 = IntStream.range(1, 13)
                .mapToObj((leapWeekInMonth) -> new Object[] { AccountingYearDivision.QUARTERS_OF_PATTERN_5_4_4_WEEKS,
                        leapWeekInMonth,
                        ValueRange.of(1, 4, weeksInMonth.applyAsInt(leapWeekInMonth, 2)),
                        ValueRange.of(1, 7 * 4, 7 * weeksInMonth.applyAsInt(leapWeekInMonth, 2)),
                        ValueRange.of(1, 12), ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11)});

        Stream<Object[]> pattern_even_13 = IntStream.range(1, 14)
                .mapToObj((leapWeekInMonth) -> new Object[] { AccountingYearDivision.THIRTEEN_EVEN_MONTHS_OF_4_WEEKS,
                        leapWeekInMonth,
                        ValueRange.of(1, 4, 5),
                        ValueRange.of(1, 7 * 4, 7 * 5),
                        ValueRange.of(1, 13), ValueRange.of(-999_999 * 13L, 999_999 * 13L + 12)});

        return Lists.cartesianProduct(
                Streams.concat(pattern_4_4_5, pattern_4_5_4, pattern_5_4_4, pattern_even_13).collect(Collectors.toList())).stream().map(args -> {
                    AccountingYearDivision division = (AccountingYearDivision) ((Object[]) args.get(0))[0];
                    int leapWeekInMonth = (int) ((Object[]) args.get(0))[1];
                    ValueRange expectedWeekOfMonthRange = (ValueRange) ((Object[]) args.get(0))[2];
                    ValueRange expectedDayOfMonthRange = (ValueRange) ((Object[]) args.get(0))[3];
                    ValueRange expectedMonthRange = (ValueRange) ((Object[]) args.get(0))[4];
                    ValueRange expectedProlepticMonthRange = (ValueRange) ((Object[]) args.get(0))[5];

                    AccountingChronologyBuilder builder = new AccountingChronologyBuilder().nearestEndOf(Month.AUGUST).endsOn(DayOfWeek.SUNDAY)
                            .withDivision(division).leapWeekInMonth(leapWeekInMonth);

                    return arguments(builder.toChronology(), expectedWeekOfMonthRange, expectedDayOfMonthRange, expectedMonthRange, expectedProlepticMonthRange);
                });
    }

    @ParameterizedTest
    @MethodSource("data_range")
    public void test_range(AccountingChronology chronology, ValueRange expectedWeekOfMonthRange, ValueRange expectedDayOfMonthRange, ValueRange expectedMonthRange, ValueRange expectedProlepticMonthRange) {
        assertAll(
                () -> assertEquals(expectedWeekOfMonthRange, chronology.range(ChronoField.ALIGNED_WEEK_OF_MONTH)),
                () -> assertEquals(expectedDayOfMonthRange, chronology.range(ChronoField.DAY_OF_MONTH)),
                () -> assertEquals(ValueRange.of(1, 364, 371), chronology.range(ChronoField.DAY_OF_YEAR)),
                () -> assertEquals(expectedMonthRange, chronology.range(ChronoField.MONTH_OF_YEAR)),
                () -> assertEquals(expectedProlepticMonthRange, chronology.range(ChronoField.PROLEPTIC_MONTH)));
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
    public void test_date_dayOfYear_range(AccountingChronology chronology, GetYearEnd _getYearEnd, IntPredicate isLeapYear, DayOfWeek _dayOfWeek, Month _ending) {
        assertAll(IntStream.range(2007, 2015).mapToObj(
            year -> () -> assertEquals(ValueRange.of(1, isLeapYear.test(year) ? 371 : 364), AccountingDate.of(chronology, year, 3, 5).range(ChronoField.DAY_OF_YEAR),
                    () -> String.format("for year %d ", year))));
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
