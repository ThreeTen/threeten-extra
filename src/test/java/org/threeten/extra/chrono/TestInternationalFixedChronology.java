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
import static java.time.temporal.ChronoField.EPOCH_DAY;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
public class TestInternationalFixedChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology() {
        Chronology chrono = Chronology.of("Ifc");
        assertNotNull(chrono);
        assertEquals(InternationalFixedChronology.INSTANCE, chrono);
        assertEquals("Ifc", chrono.getId());
        assertEquals(null, chrono.getCalendarType());
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    public static Object[][] data_samples() {
        return new Object[][] {
            {InternationalFixedDate.of(1, 1, 1), LocalDate.of(1, 1, 1)},
            {InternationalFixedDate.of(1, 1, 2), LocalDate.of(1, 1, 2)},

            {InternationalFixedDate.of(1, 6, 27), LocalDate.of(1, 6, 16)},
            {InternationalFixedDate.of(1, 6, 28), LocalDate.of(1, 6, 17)},
            {InternationalFixedDate.of(1, 7, 1), LocalDate.of(1, 6, 18)},
            {InternationalFixedDate.of(1, 7, 2), LocalDate.of(1, 6, 19)},

            {InternationalFixedDate.of(1, 13, 28), LocalDate.of(1, 12, 30)},
            {InternationalFixedDate.of(1, 13, 27), LocalDate.of(1, 12, 29)},
            {InternationalFixedDate.of(1, 13, 29), LocalDate.of(1, 12, 31)},
            {InternationalFixedDate.of(2, 1, 1), LocalDate.of(2, 1, 1)},

            {InternationalFixedDate.of(4, 6, 27), LocalDate.of(4, 6, 15)},
            {InternationalFixedDate.of(4, 6, 28), LocalDate.of(4, 6, 16)},
            {InternationalFixedDate.of(4, 6, 29), LocalDate.of(4, 6, 17)},
            {InternationalFixedDate.of(4, 7, 1), LocalDate.of(4, 6, 18)},
            {InternationalFixedDate.of(4, 7, 2), LocalDate.of(4, 6, 19)},

            {InternationalFixedDate.of(4, 13, 28), LocalDate.of(4, 12, 30)},
            {InternationalFixedDate.of(4, 13, 27), LocalDate.of(4, 12, 29)},
            {InternationalFixedDate.of(4, 13, 29), LocalDate.of(4, 12, 31)},
            {InternationalFixedDate.of(5, 1, 1), LocalDate.of(5, 1, 1)},

            {InternationalFixedDate.of(100, 6, 27), LocalDate.of(100, 6, 16)},
            {InternationalFixedDate.of(100, 6, 28), LocalDate.of(100, 6, 17)},
            {InternationalFixedDate.of(100, 7, 1), LocalDate.of(100, 6, 18)},
            {InternationalFixedDate.of(100, 7, 2), LocalDate.of(100, 6, 19)},

            {InternationalFixedDate.of(400, 6, 27), LocalDate.of(400, 6, 15)},
            {InternationalFixedDate.of(400, 6, 28), LocalDate.of(400, 6, 16)},
            {InternationalFixedDate.of(400, 6, 29), LocalDate.of(400, 6, 17)},
            {InternationalFixedDate.of(400, 7, 1), LocalDate.of(400, 6, 18)},
            {InternationalFixedDate.of(400, 7, 2), LocalDate.of(400, 6, 19)},

            {InternationalFixedDate.of(1582, 9, 28), LocalDate.of(1582, 9, 9)},
            {InternationalFixedDate.of(1582, 10, 1), LocalDate.of(1582, 9, 10)},
            {InternationalFixedDate.of(1945, 10, 27), LocalDate.of(1945, 10, 6)},

            {InternationalFixedDate.of(2012, 6, 15), LocalDate.of(2012, 6, 3)},
            {InternationalFixedDate.of(2012, 6, 16), LocalDate.of(2012, 6, 4)},
        };
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_from_InternationalFixedDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(iso, LocalDate.from(fixed));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_InternationalFixedDate_from_LocalDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(fixed, InternationalFixedDate.from(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_InternationalFixedDate_chronology_dateEpochDay(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(fixed, InternationalFixedChronology.INSTANCE.dateEpochDay(iso.toEpochDay()));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_InternationalFixedDate_toEpochDay(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(iso.toEpochDay(), fixed.toEpochDay());
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_InternationalFixedDate_until_InternationalFixedDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(InternationalFixedChronology.INSTANCE.period(0, 0, 0), fixed.until(fixed));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_InternationalFixedDate_until_LocalDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(InternationalFixedChronology.INSTANCE.period(0, 0, 0), fixed.until(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_until_InternationalFixedDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(Period.ZERO, iso.until(fixed));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Chronology_date_Temporal(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(fixed, InternationalFixedChronology.INSTANCE.date(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_plusDays(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(iso, LocalDate.from(fixed.plus(0, DAYS)));
        assertEquals(iso.plusDays(1), LocalDate.from(fixed.plus(1, DAYS)));
        assertEquals(iso.plusDays(35), LocalDate.from(fixed.plus(35, DAYS)));
        if (LocalDate.ofYearDay(1, 60).isBefore(iso)) {
            assertEquals(iso.plusDays(-1), LocalDate.from(fixed.plus(-1, DAYS)));
            assertEquals(iso.plusDays(-60), LocalDate.from(fixed.plus(-60, DAYS)));
        }
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_minusDays(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(iso, LocalDate.from(fixed.minus(0, DAYS)));
        if (LocalDate.ofYearDay(1, 35).isBefore(iso)) {
            assertEquals(iso.minusDays(1), LocalDate.from(fixed.minus(1, DAYS)));
            assertEquals(iso.minusDays(35), LocalDate.from(fixed.minus(35, DAYS)));
        }
        assertEquals(iso.minusDays(-1), LocalDate.from(fixed.minus(-1, DAYS)));
        assertEquals(iso.minusDays(-60), LocalDate.from(fixed.minus(-60, DAYS)));
    }


    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_until_DAYS(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(0, fixed.until(iso.plusDays(0), DAYS));
        assertEquals(1, fixed.until(iso.plusDays(1), DAYS));
        assertEquals(35, fixed.until(iso.plusDays(35), DAYS));
        if (LocalDate.ofYearDay(1, 40).isBefore(iso)) {
            assertEquals(-40, fixed.until(iso.minusDays(40), DAYS));
        }
    }

    public static Object[][] data_badDates() {
        return new Object[][] {
            {-1, 13, 28},
            {-1, 13, 29},
            {0, 1, 1},

            {1900, -2, 1},
            {1900, 14, 1},
            {1900, 15, 1},

            {1900, 1, -1},
            {1900, 1, 0},
            {1900, 1, 29},

            {1904, -1, -2},
            {1904, -1, 0},
            {1904, -1, 1},

            {1900, -1, 0},
            {1900, -1, -2},

            {1900, 0, -1},
            {1900, 0, 1},
            {1900, 0, 2},

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
            {1900, 13, 30},
        };
    }

    @ParameterizedTest
    @MethodSource("data_badDates")
    public void test_badDates(int year, int month, int dom) {
        assertThrows(DateTimeException.class, () -> InternationalFixedDate.of(year, month, dom));
    }

    public static Object[][] data_badLeapDates() {
        return new Object[][] {
            {1},
            {100},
            {200},
            {300},
            {1900}
        };
    }

    @ParameterizedTest
    @MethodSource("data_badLeapDates")
    public void badLeapDayDates(int year) {
        assertThrows(DateTimeException.class, () -> InternationalFixedDate.of(year, 6, 29));
    }

    @Test
    public void test_chronology_dateYearDay_badDate() {
        assertThrows(DateTimeException.class, () -> InternationalFixedChronology.INSTANCE.dateYearDay(2001, 366));
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        IntPredicate isLeapYear = year -> {
            return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
        };

        for (int year = 1; year < 500; year++) {
            InternationalFixedDate base = InternationalFixedDate.of(year, 1, 1);
            assertEquals(isLeapYear.test(year), base.isLeapYear());
            assertEquals(isLeapYear.test(year) ? 366 : 365, base.lengthOfYear());
            assertEquals(isLeapYear.test(year), InternationalFixedChronology.INSTANCE.isLeapYear(year));
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertTrue(InternationalFixedChronology.INSTANCE.isLeapYear(400));
        assertFalse(InternationalFixedChronology.INSTANCE.isLeapYear(100));
        assertTrue(InternationalFixedChronology.INSTANCE.isLeapYear(4));
        assertFalse(InternationalFixedChronology.INSTANCE.isLeapYear(3));
        assertFalse(InternationalFixedChronology.INSTANCE.isLeapYear(2));
        assertFalse(InternationalFixedChronology.INSTANCE.isLeapYear(1));
    }

    //-----------------------------------------------------------------------
    // lengthOfMonth()
    //-----------------------------------------------------------------------
    public static Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {1900, 1, 28, 28},
            {1900, 2, 28, 28},
            {1900, 3, 28, 28},
            {1900, 4, 28, 28},
            {1900, 5, 28, 28},
            {1900, 6, 28, 28},
            {1900, 7, 28, 28},
            {1900, 8, 28, 28},
            {1900, 9, 28, 28},
            {1900, 10, 28, 28},
            {1900, 11, 28, 28},
            {1900, 12, 28, 28},
            {1900, 13, 29, 29},
            {1904, 6, 29, 29},
        };
    }

    @ParameterizedTest
    @MethodSource("data_lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int day, int length) {
        assertEquals(length, InternationalFixedDate.of(year, month, day).lengthOfMonth());
    }

    @ParameterizedTest
    @MethodSource("data_lengthOfMonth")
    public void test_lengthOfMonthFirst(int year, int month, int day, int length) {
        assertEquals(length, InternationalFixedDate.of(year, month, 1).lengthOfMonth());
    }

    @Test
    public void test_lengthOfMonth_specific() {
        assertEquals(29, InternationalFixedDate.of(1900, 13, 29).lengthOfMonth());
        assertEquals(29, InternationalFixedDate.of(2000, 13, 29).lengthOfMonth());
        assertEquals(29, InternationalFixedDate.of(2000, 6, 29).lengthOfMonth());
    }

    @Test
    public void test_era_valid() {
        Era era = InternationalFixedChronology.INSTANCE.eraOf(1);
        assertNotNull(era);
        assertEquals(1, era.getValue());
    }

    //-----------------------------------------------------------------------
    // data_invalidEraValues()
    //-----------------------------------------------------------------------
    public static Object[][] data_invalidEraValues() {
        return new Object[][] {
                {-1},
                {0},
                {2},
        };
    }

    @ParameterizedTest
    @MethodSource("data_invalidEraValues")
    public void test_era_invalid(int eraValue) {
        assertThrows(DateTimeException.class, () -> InternationalFixedChronology.INSTANCE.eraOf(eraValue));
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = 1; year < 200; year++) {
            InternationalFixedDate base = InternationalFixedChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            InternationalFixedEra era = InternationalFixedEra.CE;
            assertEquals(era, base.getEra());
            assertEquals(year, base.get(YEAR_OF_ERA));
            InternationalFixedDate eraBased = InternationalFixedChronology.INSTANCE.date(era, year, 1, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = 1; year < 200; year++) {
            InternationalFixedDate base = InternationalFixedChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            InternationalFixedEra era = InternationalFixedEra.CE;
            assertEquals(era, base.getEra());
            assertEquals(year, base.get(YEAR_OF_ERA));
            InternationalFixedDate eraBased = InternationalFixedChronology.INSTANCE.dateYearDay(era, year, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(4, InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 4));
        assertEquals(3, InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 3));
        assertEquals(2, InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 2));
        assertEquals(1, InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 1));
        assertEquals(2000, InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 2000));
        assertEquals(1582, InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 1582));
    }

    public static Object[][] data_prolepticYear_bad() {
        return new Object[][] {
            {-10},
            {-1},
            {0},
        };
    }

    @ParameterizedTest
    @MethodSource("data_prolepticYear_bad")
    public void test_prolepticYearBad(int year) {
        assertThrows(DateTimeException.class, () -> InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, year));
    }

    @Test
    public void test_prolepticYear_badEra() {
        assertThrows(ClassCastException.class, () -> InternationalFixedChronology.INSTANCE.prolepticYear(IsoEra.CE, 4));
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(InternationalFixedEra.CE, InternationalFixedChronology.INSTANCE.eraOf(1));
    }

    @Test
    public void test_Chronology_eraOf_invalid() {
        assertThrows(DateTimeException.class, () -> InternationalFixedChronology.INSTANCE.eraOf(0));
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = InternationalFixedChronology.INSTANCE.eras();
        assertEquals(1, eras.size());
        assertTrue(eras.contains(InternationalFixedEra.CE));
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(ValueRange.of(0, 1, 0, 7), InternationalFixedChronology.INSTANCE.range(ALIGNED_DAY_OF_WEEK_IN_MONTH));
        assertEquals(ValueRange.of(0, 1, 0, 7), InternationalFixedChronology.INSTANCE.range(ALIGNED_DAY_OF_WEEK_IN_YEAR));
        assertEquals(ValueRange.of(0, 1, 0, 4), InternationalFixedChronology.INSTANCE.range(ALIGNED_WEEK_OF_MONTH));
        assertEquals(ValueRange.of(0, 1, 0, 52), InternationalFixedChronology.INSTANCE.range(ALIGNED_WEEK_OF_YEAR));
        assertEquals(ValueRange.of(0, 1, 0, 7), InternationalFixedChronology.INSTANCE.range(DAY_OF_WEEK));
        assertEquals(ValueRange.of(1, 29), InternationalFixedChronology.INSTANCE.range(DAY_OF_MONTH));
        assertEquals(ValueRange.of(1, 365, 366), InternationalFixedChronology.INSTANCE.range(DAY_OF_YEAR));
        assertEquals(ValueRange.of(1, 1), InternationalFixedChronology.INSTANCE.range(ERA));
        assertEquals(ValueRange.of(-719_528, 1_000_000 * 365L + 242_499 - 719_528), InternationalFixedChronology.INSTANCE.range(EPOCH_DAY));
        assertEquals(ValueRange.of(1, 13), InternationalFixedChronology.INSTANCE.range(MONTH_OF_YEAR));
        assertEquals(ValueRange.of(13, 1_000_000 * 13L - 1), InternationalFixedChronology.INSTANCE.range(PROLEPTIC_MONTH));
        assertEquals(ValueRange.of(1, 1_000_000), InternationalFixedChronology.INSTANCE.range(YEAR));
        assertEquals(ValueRange.of(1, 1_000_000), InternationalFixedChronology.INSTANCE.range(YEAR_OF_ERA));
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.range
    //-----------------------------------------------------------------------
    public static Object[][] data_ranges() {
        return new Object[][] {
            // Leap Day and Year Day are members of months
            {2012, 6, 29, DAY_OF_MONTH, ValueRange.of(1, 29)},
            {2012, 13, 29, DAY_OF_MONTH, ValueRange.of(1, 29)},
            {2012, 1, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 2, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 3, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 4, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 5, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 6, 23, DAY_OF_MONTH, ValueRange.of(1, 29)},
            {2012, 7, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 8, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 9, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 10, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 11, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 12, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2012, 13, 23, DAY_OF_MONTH, ValueRange.of(1, 29)},

            {2012, 1, 23, DAY_OF_YEAR, ValueRange.of(1, 366)},
            // Leap Day is still in same year, so (1 to 13) in leap year
            {2012, 1, 23, MONTH_OF_YEAR, ValueRange.of(1, 13)},
            // Leap Day/Year Day in own months, so (0 to 0) or (1 to 7)
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, ValueRange.of(0, 0)},
            {2012, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, ValueRange.of(0, 0)},
            {2012, 1, 23, ALIGNED_DAY_OF_WEEK_IN_MONTH, ValueRange.of(1, 7)},
            {2012, 6, 23, ALIGNED_DAY_OF_WEEK_IN_MONTH, ValueRange.of(1, 7)},
            {2012, 12, 23, ALIGNED_DAY_OF_WEEK_IN_MONTH, ValueRange.of(1, 7)},
            // Leap Day/Year Day in own months, so (0 to 0) or (1 to 4)
            {2012, 6, 29, ALIGNED_WEEK_OF_MONTH, ValueRange.of(0, 0)},
            {2012, 13, 29, ALIGNED_WEEK_OF_MONTH, ValueRange.of(0, 0)},
            {2012, 1, 23, ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {2012, 6, 23, ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {2012, 12, 23, ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            // Leap Day/Year Day in own months, so (0 to 0) or (1 to 7)
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, ValueRange.of(0, 0)},
            {2012, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, ValueRange.of(0, 0)},
            {2012, 1, 23, ALIGNED_DAY_OF_WEEK_IN_YEAR, ValueRange.of(1, 7)},
            {2012, 6, 23, ALIGNED_DAY_OF_WEEK_IN_YEAR, ValueRange.of(1, 7)},
            {2012, 12, 23, ALIGNED_DAY_OF_WEEK_IN_YEAR, ValueRange.of(1, 7)},
            // Leap Day/Year Day in own months, so (0 to 0) or (1 to 4)
            {2012, 6, 29, ALIGNED_WEEK_OF_YEAR, ValueRange.of(0, 0)},
            {2012, 13, 29, ALIGNED_WEEK_OF_YEAR, ValueRange.of(0, 0)},
            {2012, 1, 23, ALIGNED_WEEK_OF_YEAR, ValueRange.of(1, 52)},
            {2012, 6, 23, ALIGNED_WEEK_OF_YEAR, ValueRange.of(1, 52)},
            {2012, 12, 23, ALIGNED_WEEK_OF_YEAR, ValueRange.of(1, 52)},
            // Leap Day and Year Day in own 'week's, so (0 to 0) or (1 to 7)
            {2012, 6, 29, DAY_OF_WEEK, ValueRange.of(0, 0)},
            {2012, 13, 29, DAY_OF_WEEK, ValueRange.of(0, 0)},
            {2012, 1, 23, DAY_OF_WEEK, ValueRange.of(1, 7)},
            {2012, 6, 23, DAY_OF_WEEK, ValueRange.of(1, 7)},
            {2012, 12, 23, DAY_OF_WEEK, ValueRange.of(1, 7)},

            {2011, 6, 23, DAY_OF_MONTH, ValueRange.of(1, 28)},
            {2011, 13, 23, DAY_OF_YEAR, ValueRange.of(1, 365)},
            {2011, 13, 23, MONTH_OF_YEAR, ValueRange.of(1, 13)},
        };
    }

    @ParameterizedTest
    @MethodSource("data_ranges")
    public void test_range(int year, int month, int dom, TemporalField field, ValueRange range) {
        assertEquals(range, InternationalFixedDate.of(year, month, dom).range(field));
    }

    @Test
    public void test_range_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> InternationalFixedDate.of(2012, 6, 28).range(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.getLong
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
            {1, 5, 8, ERA, 1},

            {2012, 9, 26, DAY_OF_WEEK, 5},
            {2012, 9, 26, DAY_OF_YEAR, 28 + 28 + 28 + 28 + 28 + 28 + 1 + 28 + 28 + 26},
            {2012, 9, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5},
            {2012, 9, 26, ALIGNED_WEEK_OF_MONTH, 4},
            {2014, 9, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5},
            {2012, 9, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5},
            {2012, 9, 28, ALIGNED_WEEK_OF_YEAR, 36},
            {2014, 9, 28, ALIGNED_WEEK_OF_YEAR, 36},

            {2014, 13, 29, DAY_OF_WEEK, 0},
            {2014, 13, 29, DAY_OF_MONTH, 29},
            {2014, 13, 29, DAY_OF_YEAR, 13 * 28 + 1},
            {2012, 13, 29, DAY_OF_YEAR, 13 * 28 + 1 + 1},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {2014, 13, 29, ALIGNED_WEEK_OF_MONTH, 0},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {2014, 13, 29, ALIGNED_WEEK_OF_YEAR, 0},
            {2014, 13, 29, MONTH_OF_YEAR, 13},
            {2014, 13, 29, PROLEPTIC_MONTH, 2014 * 13 + 13 - 1},

            {2012, 6, 28, DAY_OF_WEEK, 7},
            {2012, 6, 28, DAY_OF_MONTH, 28},
            {2012, 6, 28, DAY_OF_YEAR, 6 * 28},
            {2012, 6, 28, ALIGNED_DAY_OF_WEEK_IN_MONTH, 7},
            {2012, 6, 28, ALIGNED_WEEK_OF_MONTH, 4},
            {2012, 6, 28, ALIGNED_DAY_OF_WEEK_IN_YEAR, 7},
            {2012, 6, 28, ALIGNED_WEEK_OF_YEAR, 24},
            {2012, 6, 28, MONTH_OF_YEAR, 6},
            {2012, 6, 28, PROLEPTIC_MONTH, 2012 * 13 + 6 - 1},

            {2012, 6, 29, DAY_OF_WEEK, 0},
            {2012, 6, 29, DAY_OF_MONTH, 29},
            {2012, 6, 29, DAY_OF_YEAR, 6 * 28 + 1},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {2012, 6, 29, ALIGNED_WEEK_OF_MONTH, 0},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {2012, 6, 29, ALIGNED_WEEK_OF_YEAR, 0},
            {2012, 6, 29, MONTH_OF_YEAR, 6},
            {2012, 6, 29, PROLEPTIC_MONTH, 2012 * 13 + 6 - 1},

            {2012, 7, 1, DAY_OF_WEEK, 1},
            {2012, 7, 1, DAY_OF_MONTH, 1},
            {2012, 7, 1, DAY_OF_YEAR, 6 * 28 + 2},
            {2012, 7, 1, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1},
            {2012, 7, 1, ALIGNED_WEEK_OF_MONTH, 1},
            {2012, 7, 1, ALIGNED_DAY_OF_WEEK_IN_YEAR, 1},
            {2012, 7, 1, ALIGNED_WEEK_OF_YEAR, 25},
            {2012, 7, 1, MONTH_OF_YEAR, 7},
            {2012, 7, 1, PROLEPTIC_MONTH, 2012 * 13 + 7 - 1},
        };
    }

    @ParameterizedTest
    @MethodSource("data_getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(expected, InternationalFixedDate.of(year, month, dom).getLong(field));
    }

    @Test
    public void test_getLong_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> InternationalFixedDate.of(2012, 6, 28).getLong(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.with
    //-----------------------------------------------------------------------
    public static Object[][] data_with() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 1, 2014, 5, 22},
            {2014, 5, 26, DAY_OF_WEEK, 5, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_MONTH, 28, 2014, 5, 28},
            {2014, 5, 26, DAY_OF_MONTH, 26, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_YEAR, 364, 2014, 13, 28},
            {2014, 5, 26, DAY_OF_YEAR, 138, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 5, 24},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 1, 2014, 5, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2014, 5, 23},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 23, 2014, 6, 19},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 20, 2014, 5, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 4, 2014, 4, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 5, 2014, 5, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2013 * 13 + 3 - 1, 2013, 3, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 13 + 5 - 1, 2014, 5, 26},
            {2014, 5, 26, YEAR, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR, 2014, 2014, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2014, 2014, 5, 26},
            {2014, 5, 26, ERA, 1, 2014, 5, 26},

            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 0, 2014, 13, 29},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1, 2014, 13, 22},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 2, 2014, 13, 23},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 13, 24},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 4, 2014, 13, 25},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 2014, 13, 26},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 6, 2014, 13, 27},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 7, 2014, 13, 28},

            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 0, 2014, 13, 29},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 1, 2014, 13, 22},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2014, 13, 23},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, 2014, 13, 24},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 4, 2014, 13, 25},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5, 2014, 13, 26},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 6, 2014, 13, 27},
            {2014, 13, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 7, 2014, 13, 28},

            {2014, 13, 29, ALIGNED_WEEK_OF_MONTH, 0, 2014, 13, 29},
            {2014, 13, 29, ALIGNED_WEEK_OF_MONTH, 3, 2014, 13, 15},

            {2014, 13, 29, ALIGNED_WEEK_OF_YEAR, 0, 2014, 13, 29},
            {2014, 13, 29, ALIGNED_WEEK_OF_YEAR, 3, 2014, 1, 15},

            {2014, 13, 29, DAY_OF_WEEK, 0, 2014, 13, 29},
            {2014, 13, 28, DAY_OF_WEEK, 1, 2014, 13, 22},
            {2014, 13, 28, DAY_OF_WEEK, 2, 2014, 13, 23},
            {2014, 13, 28, DAY_OF_WEEK, 3, 2014, 13, 24},
            {2014, 13, 28, DAY_OF_WEEK, 4, 2014, 13, 25},
            {2014, 13, 28, DAY_OF_WEEK, 5, 2014, 13, 26},
            {2014, 13, 28, DAY_OF_WEEK, 6, 2014, 13, 27},
            {2014, 13, 28, DAY_OF_WEEK, 7, 2014, 13, 28},

            {2014, 13, 29, DAY_OF_MONTH, 1, 2014, 13, 1},
            {2014, 13, 29, DAY_OF_MONTH, 3, 2014, 13, 3},

            {2014, 13, 29, MONTH_OF_YEAR, 1, 2014, 1, 28},
            {2014, 13, 29, MONTH_OF_YEAR, 13, 2014, 13, 29},
            {2014, 13, 29, MONTH_OF_YEAR, 2, 2014, 2, 28},

            {2014, 13, 29, YEAR, 2014, 2014, 13, 29},
            {2014, 13, 29, YEAR, 2013, 2013, 13, 29},

            {2014, 3, 28, DAY_OF_MONTH, 1, 2014, 3, 1},
            {2014, 1, 28, DAY_OF_MONTH, 1, 2014, 1, 1},
            {2014, 3, 28, MONTH_OF_YEAR, 1, 2014, 1, 28},
            {2014, 3, 28, DAY_OF_YEAR, 365, 2014, 13, 29},
            {2012, 3, 28, DAY_OF_YEAR, 366, 2012, 13, 29},

            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 0, 2012, 6, 29},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1, 2012, 6, 22},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 2, 2012, 6, 23},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2012, 6, 24},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 4, 2012, 6, 25},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 2012, 6, 26},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 6, 2012, 6, 27},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_MONTH, 7, 2012, 6, 28},

            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 0, 2012, 6, 29},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 1, 2012, 6, 22},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2012, 6, 23},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, 2012, 6, 24},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 4, 2012, 6, 25},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5, 2012, 6, 26},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 6, 2012, 6, 27},
            {2012, 6, 29, ALIGNED_DAY_OF_WEEK_IN_YEAR, 7, 2012, 6, 28},

            {2012, 6, 29, ALIGNED_WEEK_OF_MONTH, 0, 2012, 6, 29},
            {2012, 6, 29, ALIGNED_WEEK_OF_MONTH, 3, 2012, 6, 15},

            {2012, 6, 29, ALIGNED_WEEK_OF_YEAR, 0, 2012, 6, 29},
            {2012, 6, 29, ALIGNED_WEEK_OF_YEAR, 3, 2012, 1, 15},
            {2012, 1, 1, ALIGNED_WEEK_OF_YEAR, 52, 2012, 13, 22},
            {2012, 13, 28, ALIGNED_WEEK_OF_YEAR, 1, 2012, 1, 7},

            {2012, 6, 29, DAY_OF_WEEK, 0, 2012, 6, 29},
            {2012, 6, 29, DAY_OF_WEEK, 1, 2012, 6, 22},
            {2012, 6, 29, DAY_OF_WEEK, 2, 2012, 6, 23},
            {2012, 6, 29, DAY_OF_WEEK, 3, 2012, 6, 24},
            {2012, 6, 29, DAY_OF_WEEK, 4, 2012, 6, 25},
            {2012, 6, 29, DAY_OF_WEEK, 5, 2012, 6, 26},
            {2012, 6, 29, DAY_OF_WEEK, 6, 2012, 6, 27},
            {2012, 6, 29, DAY_OF_WEEK, 7, 2012, 6, 28},

            {2012, 6, 29, DAY_OF_MONTH, 1, 2012, 6, 1},
            {2012, 6, 29, DAY_OF_MONTH, 3, 2012, 6, 3},

            {2012, 6, 29, MONTH_OF_YEAR, 6, 2012, 6, 29},
            {2012, 6, 29, MONTH_OF_YEAR, 7, 2012, 7, 28},
            {2012, 6, 29, MONTH_OF_YEAR, 2, 2012, 2, 28},

            {2012, 6, 29, YEAR, 2012, 2012, 6, 29},
            {2012, 6, 29, YEAR, 2013, 2013, 6, 28},
            {2012, 6, 29, YEAR, 2011, 2011, 6, 28},
            {2012, 6, 29, YEAR, 2016, 2016, 6, 29},

            {2012, 6, 22, DAY_OF_MONTH, 29, 2012, 6, 29},

            {2012, 3, 28, DAY_OF_MONTH, 1, 2012, 3, 1},
            {2012, 1, 28, DAY_OF_MONTH, 1, 2012, 1, 1},
            {2012, 3, 28, MONTH_OF_YEAR, 1, 2012, 1, 28},
            {2012, 3, 28, DAY_OF_YEAR, 169, 2012, 6, 29},
            {2013, 3, 28, DAY_OF_YEAR, 169, 2013, 7, 1},
            {2013, 7, 1, YEAR, 2012, 2012, 7, 1},
        };
    }

    @ParameterizedTest
    @MethodSource("data_with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(InternationalFixedDate.of(expectedYear, expectedMonth, expectedDom), InternationalFixedDate.of(year, month, dom).with(field, value));
    }

    public static Object[][] data_with_bad() {
        return new Object[][] {
            {2013, 1, 1, ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {2013, 1, 1, ALIGNED_DAY_OF_WEEK_IN_MONTH, 8},
            {2012, 1, 1, ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {2012, 1, 1, ALIGNED_DAY_OF_WEEK_IN_MONTH, 8},

            {2013, 1, 1, ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {2013, 1, 1, ALIGNED_DAY_OF_WEEK_IN_YEAR, 8},
            {2012, 1, 1, ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {2012, 1, 1, ALIGNED_DAY_OF_WEEK_IN_YEAR, 8},

            {2013, 1, 1, ALIGNED_WEEK_OF_MONTH, 0},
            {2013, 1, 1, ALIGNED_WEEK_OF_MONTH, 5},
            {2012, 1, 1, ALIGNED_WEEK_OF_MONTH, 0},
            {2012, 1, 1, ALIGNED_WEEK_OF_MONTH, 5},

            {2013, 1, 1, ALIGNED_WEEK_OF_YEAR, 0},
            {2013, 1, 1, ALIGNED_WEEK_OF_YEAR, 53},
            {2012, 1, 1, ALIGNED_WEEK_OF_YEAR, 0},
            {2012, 1, 1, ALIGNED_WEEK_OF_YEAR, 53},

            {2013, 1, 1, DAY_OF_WEEK, 0},
            {2013, 1, 1, DAY_OF_WEEK, 8},
            {2012, 1, 1, DAY_OF_WEEK, 0},
            {2012, 1, 1, DAY_OF_WEEK, 8},
            {2013, 1, 1, DAY_OF_MONTH, -1},
            {2013, 1, 1, DAY_OF_MONTH, 29},
            {2013, 6, 1, DAY_OF_MONTH, 29},
            {2012, 6, 1, DAY_OF_MONTH, 30},
            {2012, 1, 1, DAY_OF_MONTH, -2},
            {2012, 1, 1, DAY_OF_MONTH, 29},
            {2013, 13, 1, DAY_OF_MONTH, 30},
            {2012, 13, 1, DAY_OF_MONTH, 30},

            {2013, 1, 1, DAY_OF_YEAR, 0},
            {2012, 1, 1, DAY_OF_YEAR, 0},
            {2013, 1, 1, DAY_OF_YEAR, 366},
            {2012, 1, 1, DAY_OF_YEAR, 367},

            {2013, 1, 1, EPOCH_DAY, -719_529},
            {2013, 1, 1, EPOCH_DAY, 1_000_000 * 365L + 242_499 - 719_528 + 1},

            {2013, 1, 1, MONTH_OF_YEAR, -1},
            {2013, 1, 1, MONTH_OF_YEAR, 14},
            {2012, 1, 1, MONTH_OF_YEAR, -2},
            {2012, 1, 1, MONTH_OF_YEAR, 14},

            {2013, 1, 1, YEAR, 0},

            {2012, 6, 21, DAY_OF_WEEK, 0},
            {2012, 6, 21, ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {2012, 6, 21, ALIGNED_WEEK_OF_MONTH, 0},
            {2012, 6, 21, ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {2012, 6, 21, ALIGNED_WEEK_OF_YEAR, 0},
        };
    }

    @ParameterizedTest
    @MethodSource("data_with_bad")
    public void test_with_TemporalField_badValue(int year, int month, int dom, TemporalField field, long value) {
        assertThrows(DateTimeException.class, () -> InternationalFixedDate.of(year, month, dom).with(field, value));
    }

    @Test
    public void test_with_TemporalField_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> InternationalFixedDate.of(2012, 6, 28).with(MINUTE_OF_DAY, 0));
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    public static Object[][] data_temporalAdjusters_lastDayOfMonth() {
        return new Object[][] {
            {2012, 6, 23, 2012, 6, 29},
            {2012, 6, 29, 2012, 6, 29},
            {2009, 6, 23, 2009, 6, 28},
            {2007, 13, 23, 2007, 13, 29},
            {2005, 13, 29, 2005, 13, 29},
        };
    }

    @ParameterizedTest
    @MethodSource("data_temporalAdjusters_lastDayOfMonth")
    public void test_temporalAdjusters_LastDayOfMonth(int year, int month, int day, int expectedYear, int expectedMonth, int expectedDay) {
        InternationalFixedDate base = InternationalFixedDate.of(year, month, day);
        InternationalFixedDate expected = InternationalFixedDate.of(expectedYear, expectedMonth, expectedDay);
        InternationalFixedDate actual = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(expected, actual);
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        InternationalFixedDate fixed = InternationalFixedDate.of(2000, 1, 4);
        InternationalFixedDate test = fixed.with(LocalDate.of(2012, 7, 6));
        assertEquals(InternationalFixedDate.of(2012, 7, 19), test);
    }

    @Test
    public void test_adjust_toMonth() {
        InternationalFixedDate fixed = InternationalFixedDate.of(2000, 1, 4);
        assertThrows(DateTimeException.class, () -> fixed.with(Month.APRIL));
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(InternationalFixedDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToInternationalFixedDate() {
        InternationalFixedDate fixed = InternationalFixedDate.of(2012, 7, 19);
        LocalDate test = LocalDate.MIN.with(fixed);
        assertEquals(LocalDate.of(2012, 7, 6), test);
    }

    @Test
    public void test_LocalDateTime_adjustToInternationalFixedDate() {
        InternationalFixedDate fixed = InternationalFixedDate.of(2012, 7, 19);
        LocalDateTime test = LocalDateTime.MIN.with(fixed);
        assertEquals(LocalDateTime.of(2012, 7, 6, 0, 0), test);
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.plus
    // InternationalFixedDate.minus
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
            {2014, 5, 26, -1, MILLENNIA, 2014 - 1000, 5, 26},

            {2014, 13, 26, 3, WEEKS, 2015, 1, 19},
            {2014, 1, 26, -5, WEEKS, 2013, 13, 19},

            {2012, 6, 26, 3, WEEKS, 2012, 7, 19},
            {2012, 7, 26, -5, WEEKS, 2012, 6, 19},

            {2012, 6, 21, 52 + 1, WEEKS, 2013, 6, 28},
            {2013, 6, 21, 6 * 52 + 1, WEEKS, 2019, 6, 28},
        };
    }

    public static Object[][] data_plus_leap_and_year_day() {
        return new Object[][] {
            {2014, 13, 29, 0, DAYS, 2014, 13, 29},
            {2014, 13, 29, 8, DAYS, 2015, 1, 8},
            {2014, 13, 29, -3, DAYS, 2014, 13, 26},
            {2014, 13, 29, 0, WEEKS, 2014, 13, 29},
            {2014, 13, 29, 3, WEEKS, 2015, 1, 21},
            {2014, 13, 29, -5, WEEKS, 2014, 12, 21},
            {2014, 13, 29, 52, WEEKS, 2015, 13, 29},
            {2014, 13, 29, 0, MONTHS, 2014, 13, 29},
            {2014, 13, 29, 3, MONTHS, 2015, 3, 28},
            {2014, 13, 29, -5, MONTHS, 2014, 8, 28},
            {2014, 13, 29, 13, MONTHS, 2015, 13, 29},
            {2014, 13, 29, 0, YEARS, 2014, 13, 29},
            {2014, 13, 29, 3, YEARS, 2017, 13, 29},
            {2014, 13, 29, -5, YEARS, 2009, 13, 29},

            {2011, 13, 29, 4 * 6, WEEKS, 2012, 6, 29},
            {2012, 13, 29, 4 * -7, WEEKS, 2012, 6, 29},

            {2012, 6, 29, 0, DAYS, 2012, 6, 29},
            {2012, 6, 29, 8, DAYS, 2012, 7, 8},
            {2012, 6, 29, -3, DAYS, 2012, 6, 26},
            {2012, 6, 29, 0, WEEKS, 2012, 6, 29},
            {2012, 6, 29, 3, WEEKS, 2012, 7, 22},
            {2012, 6, 29, -5, WEEKS, 2012, 5, 22},
            {2012, 6, 29, 52 * 4, WEEKS, 2016, 6, 29},
            {2012, 6, 29, 0, MONTHS, 2012, 6, 29},
            {2012, 6, 29, 3, MONTHS, 2012, 9, 28},
            {2012, 6, 29, -5, MONTHS, 2012, 1, 28},
            {2012, 6, 29, 13 * 4, MONTHS, 2016, 6, 29},
            {2012, 6, 29, 0, YEARS, 2012, 6, 29},
            {2012, 6, 29, 3, YEARS, 2015, 6, 28},
            {2012, 6, 29, -5, YEARS, 2007, 6, 28},
            {2012, 6, 29, 4, YEARS, 2016, 6, 29},

            {2012, 6, 29, 4 * 7, WEEKS, 2012, 13, 29},
            {2012, 6, 29, 4 * -6, WEEKS, 2011, 13, 29},
        };
    }

    public static Object[][] data_minus_leap_and_year_day() {
        return new Object[][] {
            {2014, 13, 29, 0, DAYS, 2014, 13, 29},
            {2014, 13, 21, 8, DAYS, 2014, 13, 29},
            {2015, 1, 3, -3, DAYS, 2014, 13, 29},
            {2014, 13, 29, 0, WEEKS, 2014, 13, 29},
            {2014, 13, 7, 3, WEEKS, 2014, 13, 29},
            {2015, 2, 7, -5, WEEKS, 2014, 13, 29},
            {2013, 13, 29, 52, WEEKS, 2014, 13, 29},
            {2014, 13, 29, 0, MONTHS, 2014, 13, 29},
            {2014, 10, 28, 3, MONTHS, 2014, 13, 29},
            {2015, 5, 28, -5, MONTHS, 2014, 13, 29},
            {2013, 13, 29, 13, MONTHS, 2014, 13, 29},
            {2014, 13, 29, 0, YEARS, 2014, 13, 29},
            {2011, 13, 29, 3, YEARS, 2014, 13, 29},
            {2019, 13, 29, -5, YEARS, 2014, 13, 29},

            {2012, 6, 29, 4 * -6, WEEKS, 2011, 13, 29},
            {2012, 6, 29, 4 * 7, WEEKS, 2012, 13, 29},

            {2012, 6, 29, 0, DAYS, 2012, 6, 29},
            {2012, 6, 21, 8, DAYS, 2012, 6, 29},
            {2012, 7, 3, -3, DAYS, 2012, 6, 29},
            {2012, 6, 29, 0, WEEKS, 2012, 6, 29},
            {2012, 6, 8, 3, WEEKS, 2012, 6, 29},
            {2012, 8, 8, -5, WEEKS, 2012, 6, 29},
            {2012, 6, 29, 28, WEEKS, 2012, 13, 29},
            {2008, 6, 29, 52 * 4, WEEKS, 2012, 6, 29},
            {2012, 6, 29, 0, MONTHS, 2012, 6, 29},
            {2012, 3, 28, 3, MONTHS, 2012, 6, 29},
            {2012, 11, 28, -5, MONTHS, 2012, 6, 29},
            {2008, 6, 29, 13 * 4, MONTHS, 2012, 6, 29},
            {2012, 6, 29, 0, YEARS, 2012, 6, 29},
            {2009, 6, 28, 3, YEARS, 2012, 6, 29},
            {2017, 6, 28, -5, YEARS, 2012, 6, 29},
            {2008, 6, 29, 4, YEARS, 2012, 6, 29},

            {2012, 13, 29, 4 * -7, WEEKS, 2012, 6, 29},
            {2011, 13, 29, 4 * 6, WEEKS, 2012, 6, 29},
        };
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(InternationalFixedDate.of(expectedYear, expectedMonth, expectedDom), InternationalFixedDate.of(year, month, dom).plus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_plus_leap_and_year_day")
    public void test_plus_leap_and_year_day_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(InternationalFixedDate.of(expectedYear, expectedMonth, expectedDom), InternationalFixedDate.of(year, month, dom).plus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(InternationalFixedDate.of(expectedYear, expectedMonth, expectedDom), InternationalFixedDate.of(year, month, dom).minus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_minus_leap_and_year_day")
    public void test_minus_leap_and_year_day_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(InternationalFixedDate.of(expectedYear, expectedMonth, expectedDom), InternationalFixedDate.of(year, month, dom).minus(amount, unit));
    }

    @Test
    public void test_plus_TemporalUnit_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> InternationalFixedDate.of(2012, 6, 28).plus(0, MINUTES));
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.until
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
            {2014, 5, 26, 3014, 5, 26, ERAS, 0},

            {2014, 13, 28, 2015, 1, 1, DAYS, 2},
            {2014, 13, 28, 2014, 13, 29, DAYS, 1},
            {2014, 13, 29, 2015, 1, 1, DAYS, 1},
            {2015, 1, 1, 2014, 13, 24, DAYS, -6},
            {2014, 13, 29, 2014, 13, 29, WEEKS, 0},
            {2015, 1, 1, 2015, 1, 1, WEEKS, 0},
            {2015, 1, 1, 2014, 13, 28, WEEKS, 0},
            {2015, 1, 1, 2014, 13, 23, WEEKS, 0},
            {2015, 1, 1, 2014, 13, 22, WEEKS, -1},
            {2014, 13, 29, 2014, 13, 21, WEEKS, -1},
            {2014, 13, 29, 2014, 13, 22, WEEKS, 0},
            {2014, 13, 29, 2015, 1, 7, WEEKS, 0},
            {2014, 13, 29, 2015, 1, 8, WEEKS, 1},
            {2014, 13, 21, 2014, 13, 29, WEEKS, 1},
            {2014, 13, 22, 2014, 13, 29, WEEKS, 0},
            {2015, 1, 7, 2014, 13, 29, WEEKS, 0},
            {2015, 1, 8, 2014, 13, 29, WEEKS, -1},
            {2014, 13, 1, 2015, 1, 1, WEEKS, 4},
            {2014, 13, 29, 2014, 13, 29, MONTHS, 0},
            {2014, 13, 29, 2015, 1, 28, MONTHS, 0},
            {2014, 13, 29, 2015, 2, 1, MONTHS, 1},
            {2015, 2, 1, 2014, 13, 29, MONTHS, -1},
            {2015, 1, 28, 2014, 13, 29, MONTHS, 0},
            {2014, 12, 28, 2014, 13, 29, MONTHS, 1},
            {2014, 13, 1, 2014, 13, 29, MONTHS, 0},
            {2014, 13, 1, 2015, 1, 1, MONTHS, 1},
            {2014, 13, 29, 2014, 13, 29, YEARS, 0},
            {2014, 13, 29, 2015, 13, 28, YEARS, 0},
            {2014, 13, 29, 2015, 13, 29, YEARS, 1},
            {2014, 13, 29, 2016, 1, 1, YEARS, 1},
            {2014, 1, 1, 2014, 13, 29, YEARS, 0},
            {2013, 13, 29, 2014, 13, 29, YEARS, 1},
            {2013, 13, 28, 2014, 13, 29, YEARS, 1},

            {2012, 6, 28, 2012, 7, 1, DAYS, 2},
            {2012, 6, 28, 2012, 6, 29, DAYS, 1},
            {2012, 6, 29, 2012, 7, 1, DAYS, 1},
            {2012, 7, 1, 2012, 6, 24, DAYS, -6},
            {2012, 6, 29, 2012, 6, 29, WEEKS, 0},
            {2012, 7, 1, 2012, 7, 1, WEEKS, 0},
            {2012, 7, 1, 2012, 6, 28, WEEKS, 0},
            {2012, 7, 1, 2012, 6, 23, WEEKS, 0},
            {2012, 7, 1, 2012, 6, 22, WEEKS, -1},
            {2012, 6, 29, 2012, 6, 21, WEEKS, -1},
            {2012, 6, 29, 2012, 6, 22, WEEKS, 0},
            {2012, 6, 29, 2012, 7, 7, WEEKS, 0},
            {2012, 6, 29, 2012, 7, 8, WEEKS, 1},
            {2012, 6, 21, 2012, 6, 29, WEEKS, 1},
            {2012, 6, 22, 2012, 6, 29, WEEKS, 0},
            {2012, 7, 7, 2012, 6, 29, WEEKS, 0},
            {2012, 7, 8, 2012, 6, 29, WEEKS, -1},
            {2012, 6, 29, 2012, 6, 29, MONTHS, 0},
            {2012, 6, 29, 2012, 7, 28, MONTHS, 0},
            {2012, 6, 29, 2012, 8, 1, MONTHS, 1},
            {2012, 8, 1, 2012, 6, 29, MONTHS, -1},
            {2012, 7, 28, 2012, 6, 29, MONTHS, 0},
            {2012, 5, 28, 2012, 6, 29, MONTHS, 1},
            {2012, 6, 1, 2012, 6, 29, MONTHS, 0},
            {2012, 6, 1, 2012, 7, 1, MONTHS, 1},
            {2012, 6, 29, 2012, 6, 29, YEARS, 0},
            {2012, 6, 29, 2013, 6, 28, YEARS, 0},
            {2012, 6, 29, 2013, 7, 1, YEARS, 1},
            {2011, 7, 1, 2012, 6, 29, YEARS, 0},
            {2011, 6, 28, 2012, 6, 29, YEARS, 1},
            {2011, 7, 1, 2012, 7, 1, YEARS, 1},
            {2012, 6, 29, 2011, 6, 28, YEARS, -1},
            {2012, 6, 29, 2011, 7, 1, YEARS, 0},
            {2013, 7, 1, 2012, 6, 29, YEARS, -1},
            {2013, 6, 28, 2012, 6, 29, YEARS, 0},
            {2016, 6, 29, 2012, 6, 29, YEARS, -4},
            {2012, 6, 29, 2016, 6, 29, YEARS, 4},

            // The order is the 28th, Year Day, Leap Day, the 1st.
            // Year Day is "after the 28th"
            // Leap Day is "before the 1st"
            {2012, 6, 29, 2012, 13, 29, DAYS, 197},
            {2012, 6, 29, 2012, 13, 28, WEEKS, 27},
            {2012, 6, 29, 2012, 13, 29, WEEKS, 28},
            {2012, 6, 29, 2013, 1, 1, WEEKS, 28},
            {2012, 6, 29, 2011, 13, 28, WEEKS, -24},
            {2012, 6, 29, 2011, 13, 29, WEEKS, -24},
            {2012, 6, 29, 2012, 1, 1, WEEKS, -23},
            {2012, 13, 29, 2012, 6, 28, WEEKS, -28},
            {2012, 13, 29, 2012, 6, 29, WEEKS, -28},
            {2012, 13, 29, 2012, 7, 1, WEEKS, -27},
            {2011, 13, 29, 2012, 6, 28, WEEKS, 23},
            {2011, 13, 29, 2012, 6, 29, WEEKS, 24},
            {2011, 13, 29, 2012, 7, 1, WEEKS, 24},
            {2012, 13, 29, 2013, 13, 29, WEEKS, 52},
            {2012, 13, 29, 2016, 13, 29, WEEKS, 52 * 4},
            {2012, 6, 29, 2012, 13, 28, MONTHS, 6},
            {2012, 6, 29, 2012, 13, 29, MONTHS, 7},
            {2012, 6, 29, 2013, 1, 1, MONTHS, 7},
            {2012, 6, 29, 2011, 13, 28, MONTHS, -6},
            {2012, 6, 29, 2011, 13, 29, MONTHS, -6},
            {2012, 6, 29, 2012, 1, 1, MONTHS, -5},
            {2012, 6, 29, 2016, 6, 29, WEEKS, 52 * 4},
            {2012, 13, 29, 2012, 6, 28, MONTHS, -7},
            {2012, 13, 29, 2012, 6, 29, MONTHS, -7},
            {2012, 13, 29, 2012, 7, 1, MONTHS, -6},
            {2011, 13, 29, 2012, 6, 28, MONTHS, 5},
            {2011, 13, 29, 2012, 6, 29, MONTHS, 6},
            {2011, 13, 29, 2012, 7, 1, MONTHS, 6},
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
            {2011, 13, 26, 2012, 13, 26, 1, 0, 0},
            {2012, 13, 26, 2011, 13, 26, -1, 0, 0},
            {2012, 13, 26, 2013, 13, 26, 1, 0, 0},

            {2011, 13, 6, 2012, 13, 6, 1, 0, 0},
            {2012, 13, 6, 2011, 13, 6, -1, 0, 0},
            {2011, 13, 1, 2012, 13, 7, 1, 0, 6},
            {2012, 13, 7, 2011, 13, 1, -1, 0, -6},

            {2011, 12, 28, 2012, 13, 1, 1, 0, 1},
            {2012, 13, 1, 2011, 12, 28, -1, 0, -1},
            {2013, 13, 6, 2012, 13, 6, -1, 0, 0},
            {2012, 13, 6, 2013, 13, 6, 1, 0, 0},

            // start with Year Day
            {2012, 13, 29, 2012, 13, 29, 0, 0, 0},
            {2012, 13, 29, 2013, 13, 29, 1, 0, 0},
            {2011, 13, 29, 2010, 13, 29, -1, 0, 0},
            {2000, 13, 29, 2001, 13, 29, 1, 0, 0},
            {2007, 13, 29, 2008, 1, 1, 0, 0, 1},
            {2005, 13, 29, 2006, 2, 1, 0, 1, 1},
            {2003, 13, 29, 2004, 6, 29, 0, 6, 0},
            {2003, 13, 29, 2004, 7, 1, 0, 6, 1},
            {2004, 13, 29, 2004, 6, 29, 0, -7, 0},
            {2004, 13, 29, 2004, 7, 1, 0, -6, -27},
            {2003, 13, 29, 2005, 1, 1, 1, 0, 1},
            {2003, 13, 29, 2002, 13, 28, -1, 0, -1},

            // start with one day before Year Day
            {2003, 13, 28, 2004, 6, 29, 0, 6, 1},
            {2003, 13, 28, 2004, 7, 1, 0, 6, 2},
            {2004, 13, 28, 2004, 6, 29, 0, -7, 0},
            {2004, 13, 28, 2004, 7, 1, 0, -6, -27},
            {2003, 13, 28, 2005, 1, 1, 1, 0, 2},
            {2003, 13, 28, 2002, 13, 28, -1, 0, 0},
            {2007, 13, 28, 2008, 1, 1, 0, 0, 2},
            {2005, 13, 28, 2006, 2, 1, 0, 1, 1},

            // start with Leap Day
            {2008, 6, 29, 2008, 6, 29, 0, 0, 0},
            {2012, 6, 29, 2016, 6, 29, 4, 0, 0},
            {2024, 6, 29, 2020, 6, 29, -4, 0, 0},
            {2000, 6, 29, 2032, 6, 29, 32, 0, 0},
            {2024, 6, 29, 2000, 6, 29, -24, 0, 0},
            {2004, 6, 29, 2004, 13, 29, 0, 7, 0},
            {2004, 6, 29, 2004, 13, 28, 0, 6, 28}, // yes, 6 months and 28 days here, not 7 months flat
            {2004, 6, 29, 2003, 13, 29, 0, -6, 0},
            {2004, 6, 29, 2003, 13, 28, 0, -6, -1},
            {2004, 6, 29, 2003, 6, 28, -1, 0, 0},
            {2000, 6, 29, 2000, 7, 1, 0, 0, 1},
            {2000, 6, 29, 2000, 8, 1, 0, 1, 1},

            // start with one day before Leap Day
            {2004, 6, 28, 2004, 13, 29, 0, 7, 1},
            {2004, 6, 28, 2004, 13, 28, 0, 7, 0},
            {2004, 6, 28, 2003, 13, 29, 0, -5, -28}, // yes, -5 months and -28 days here, not -6 months flat
            {2004, 6, 28, 2003, 13, 28, 0, -6, 0},
            {2000, 6, 28, 2000, 7, 1, 0, 0, 2},
            {2000, 6, 28, 2000, 8, 1, 0, 1, 1},
        };
    }

    @ParameterizedTest
    @MethodSource("data_until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        InternationalFixedDate start = InternationalFixedDate.of(year1, month1, dom1);
        InternationalFixedDate end = InternationalFixedDate.of(year2, month2, dom2);
        assertEquals(expected, start.until(end, unit));
    }

    @ParameterizedTest
    @MethodSource("data_until_period")
    public void test_until_end(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            int yearPeriod, int monthPeriod, int dayPeriod) {
        InternationalFixedDate start = InternationalFixedDate.of(year1, month1, dom1);
        InternationalFixedDate end = InternationalFixedDate.of(year2, month2, dom2);
        ChronoPeriod period = InternationalFixedChronology.INSTANCE.period(yearPeriod, monthPeriod, dayPeriod);
        assertEquals(period, start.until(end));
    }

    @Test
    public void test_until_TemporalUnit_unsupported() {
        InternationalFixedDate start = InternationalFixedDate.of(2012, 6, 28);
        InternationalFixedDate end = InternationalFixedDate.of(2012, 7, 1);
        assertThrows(UnsupportedTemporalTypeException.class, () -> start.until(end, MINUTES));
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.period
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(InternationalFixedDate.of(2014, 8, 1), InternationalFixedDate.of(2014, 5, 26).plus(InternationalFixedChronology.INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_plus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> InternationalFixedDate.of(2014, 5, 26).plus(Period.ofMonths(2)));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(InternationalFixedDate.of(2014, 3, 23), InternationalFixedDate.of(2014, 5, 26).minus(InternationalFixedChronology.INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_minus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> InternationalFixedDate.of(2014, 5, 26).minus(Period.ofMonths(2)));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(InternationalFixedDate.of(2000,  1,  3), InternationalFixedDate.of(2000,  1,  3))
            .addEqualityGroup(InternationalFixedDate.of(2000,  1,  4), InternationalFixedDate.of(2000,  1,  4))
            .addEqualityGroup(InternationalFixedDate.of(2000,  2,  3), InternationalFixedDate.of(2000,  2,  3))
            .addEqualityGroup(InternationalFixedDate.of(2000,  6, 28), InternationalFixedDate.of(2000,  6, 28))
            .addEqualityGroup(InternationalFixedDate.of(2000,  6, 29), InternationalFixedDate.of(2000,  6, 29))
            .addEqualityGroup(InternationalFixedDate.of(2000, 13, 28), InternationalFixedDate.of(2000, 13, 28))
            .addEqualityGroup(InternationalFixedDate.of(2001,  1,  1), InternationalFixedDate.of(2001,  1,  1))
            .addEqualityGroup(InternationalFixedDate.of(2001, 13, 29), InternationalFixedDate.of(2001, 13, 29))
            .addEqualityGroup(InternationalFixedDate.of(2004,  6, 29), InternationalFixedDate.of(2004,  6, 29))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public static Object[][] data_toString() {
        return new Object[][] {
            {InternationalFixedDate.of(1, 1, 1), "Ifc CE 1/01/01"},
            {InternationalFixedDate.of(2012, 6, 23), "Ifc CE 2012/06/23"},

            {InternationalFixedDate.of(1, 13, 29), "Ifc CE 1/13/29"},
            {InternationalFixedDate.of(2012, 6, 29), "Ifc CE 2012/06/29"},
            {InternationalFixedDate.of(2012, 13, 29), "Ifc CE 2012/13/29"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_toString")
    public void test_toString(InternationalFixedDate date, String expected) {
        assertEquals(expected, date.toString());
    }
}
