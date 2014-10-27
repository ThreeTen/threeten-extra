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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
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
                .withDivision(AccountingPeriod.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInPeriod(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        Predicate<Integer> isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate nextYearEnd = getYearEnd.apply(year + 1);
            return currentYearEnd.until(nextYearEnd, DAYS) == 371;
        };

        for (int year = -200; year < 400; year++) {
            assertEquals(chronology.isLeapYear(year), isLeapYear.test(year), "Fails on " + year);
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_isLeapYear_nearestEndOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingPeriod.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInPeriod(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).plusMonths(1).plusDays(3 - 1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };
        Predicate<Integer> isLeapYear = year -> {
            LocalDate currentYearEnd = getYearEnd.apply(year);
            LocalDate nextYearEnd = getYearEnd.apply(year + 1);
            return currentYearEnd.until(nextYearEnd, DAYS) == 371;
        };

        for (int year = -200; year < 400; year++) {
            assertEquals(chronology.isLeapYear(year), isLeapYear.test(year), "Fails on " + year);
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_date_int_int_int_inLastWeekOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).inLastWeekOf(ending)
                .withDivision(AccountingPeriod.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInPeriod(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };

        for (int year = -200; year < 400; year++) {
            assertEquals(chronology.date(year, 1, 1), chronology.date(getYearEnd.apply(year - 1).plusDays(1)));
        }
    }

    @Test(dataProvider = "yearEnding")
    public void test_date_int_int_int_nearestEndOf(DayOfWeek dayOfWeek, Month ending) {
        AccountingChronology chronology = new AccountingChronologyBuilder().endsOn(dayOfWeek).nearestEndOf(ending)
                .withDivision(AccountingPeriod.QUARTERS_OF_PATTERN_4_4_5_WEEKS).leapWeekInPeriod(12)
                .toChronology();

        IntFunction<LocalDate> getYearEnd = year -> {
            return LocalDate.of(year, ending, 1).plusMonths(1).plusDays(3 - 1).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        };

        for (int year = -200; year < 400; year++) {
            assertEquals(chronology.date(year, 1, 1), chronology.date(getYearEnd.apply(year - 1).plusDays(1)));
        }
    }

}
