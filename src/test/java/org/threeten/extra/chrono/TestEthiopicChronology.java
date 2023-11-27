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
public class TestEthiopicChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        Chronology chrono = Chronology.of("Ethiopic");
        assertNotNull(chrono);
        assertEquals(EthiopicChronology.INSTANCE, chrono);
        assertEquals("Ethiopic", chrono.getId());
        assertEquals("ethiopic", chrono.getCalendarType());
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("ethiopic");
        assertNotNull(chrono);
        assertEquals(EthiopicChronology.INSTANCE, chrono);
        assertEquals("Ethiopic", chrono.getId());
        assertEquals("ethiopic", chrono.getCalendarType());
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    public static Object[][] data_samples() {
        return new Object[][] {
            {EthiopicDate.of(-1, 13, 6), LocalDate.of(7, 8, 27)},
            {EthiopicDate.of(0, 1, 1), LocalDate.of(7, 8, 28)},
            {EthiopicDate.of(0, 1, 30), LocalDate.of(7, 9, 26)},
            {EthiopicDate.of(0, 12, 30), LocalDate.of(8, 8, 21)},
            {EthiopicDate.of(0, 13, 1), LocalDate.of(8, 8, 22)},
            {EthiopicDate.of(0, 13, 4), LocalDate.of(8, 8, 25)},
            {EthiopicDate.of(0, 13, 5), LocalDate.of(8, 8, 26)},

            {EthiopicDate.of(1, 1, 1), LocalDate.of(8, 8, 27)},
            {EthiopicDate.of(1, 1, 2), LocalDate.of(8, 8, 28)},
            {EthiopicDate.of(1, 1, 3), LocalDate.of(8, 8, 29)},

            {EthiopicDate.of(2, 1, 1), LocalDate.of(9, 8, 27)},
            {EthiopicDate.of(3, 1, 1), LocalDate.of(10, 8, 27)},
            {EthiopicDate.of(3, 13, 6), LocalDate.of(11, 8, 27)},
            {EthiopicDate.of(4, 1, 1), LocalDate.of(11, 8, 28)},
            {EthiopicDate.of(4, 7, 5), LocalDate.of(12, 2, 28)},
            {EthiopicDate.of(4, 7, 6), LocalDate.of(12, 2, 29)},
            {EthiopicDate.of(5, 1, 1), LocalDate.of(12, 8, 27)},
            {EthiopicDate.of(1938, 3, 3), LocalDate.of(1945, 11, 12)},
            {EthiopicDate.of(2004, 2, 5), LocalDate.of(2011, 10, 16)},
            {EthiopicDate.of(2004, 10, 28), LocalDate.of(2012, 7, 5)},
            {EthiopicDate.of(2004, 10, 29), LocalDate.of(2012, 7, 6)},
        };
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_from_EthiopicDate(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(iso, LocalDate.from(ethiopic));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_EthiopicDate_from_LocalDate(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(ethiopic, EthiopicDate.from(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_EthiopicDate_chronology_dateEpochDay(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(ethiopic, EthiopicChronology.INSTANCE.dateEpochDay(iso.toEpochDay()));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_EthiopicDate_toEpochDay(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(iso.toEpochDay(), ethiopic.toEpochDay());
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_EthiopicDate_until_EthiopicDate(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(EthiopicChronology.INSTANCE.period(0, 0, 0), ethiopic.until(ethiopic));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_EthiopicDate_until_LocalDate(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(EthiopicChronology.INSTANCE.period(0, 0, 0), ethiopic.until(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_until_EthiopicDate(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(Period.ZERO, iso.until(ethiopic));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Chronology_date_Temporal(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(ethiopic, EthiopicChronology.INSTANCE.date(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_plusDays(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(iso, LocalDate.from(ethiopic.plus(0, DAYS)));
        assertEquals(iso.plusDays(1), LocalDate.from(ethiopic.plus(1, DAYS)));
        assertEquals(iso.plusDays(35), LocalDate.from(ethiopic.plus(35, DAYS)));
        assertEquals(iso.plusDays(-1), LocalDate.from(ethiopic.plus(-1, DAYS)));
        assertEquals(iso.plusDays(-60), LocalDate.from(ethiopic.plus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_minusDays(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(iso, LocalDate.from(ethiopic.minus(0, DAYS)));
        assertEquals(iso.minusDays(1), LocalDate.from(ethiopic.minus(1, DAYS)));
        assertEquals(iso.minusDays(35), LocalDate.from(ethiopic.minus(35, DAYS)));
        assertEquals(iso.minusDays(-1), LocalDate.from(ethiopic.minus(-1, DAYS)));
        assertEquals(iso.minusDays(-60), LocalDate.from(ethiopic.minus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_until_DAYS(EthiopicDate ethiopic, LocalDate iso) {
        assertEquals(0, ethiopic.until(iso.plusDays(0), DAYS));
        assertEquals(1, ethiopic.until(iso.plusDays(1), DAYS));
        assertEquals(35, ethiopic.until(iso.plusDays(35), DAYS));
        assertEquals(-40, ethiopic.until(iso.minusDays(40), DAYS));
    }

    public static Object[][] data_badDates() {
        return new Object[][] {
            {2008, 0, 0},

            {2008, -1, 1},
            {2008, 0, 1},
            {2008, 14, 1},
            {2008, 15, 1},

            {2008, 1, -1},
            {2008, 1, 0},
            {2008, 1, 31},
            {2008, 1, 32},

            {2008, 12, -1},
            {2008, 12, 0},
            {2008, 12, 31},
            {2008, 12, 32},

            {2008, 13, -1},
            {2008, 13, 0},
            {2008, 13, 6},
            {2008, 13, 7},

            {2007, 13, -1},
            {2007, 13, 0},
            {2007, 13, 7},
            {2007, 13, 8},
        };
    }

    @ParameterizedTest
    @MethodSource("data_badDates")
    public void test_badDates(int year, int month, int dom) {
        assertThrows(DateTimeException.class, () -> EthiopicDate.of(year, month, dom));
    }

    @Test
    public void test_chronology_dateYearDay_badDate() {
        assertThrows(DateTimeException.class, () -> EthiopicChronology.INSTANCE.dateYearDay(2008, 366));
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        for (int year = -200; year < 200; year++) {
            EthiopicDate base = EthiopicDate.of(year, 1, 1);
            assertEquals(((year - 3) % 4) == 0, base.isLeapYear());
            assertEquals(((year + 400 - 3) % 4) == 0, EthiopicChronology.INSTANCE.isLeapYear(year));
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(8));
        assertEquals(true, EthiopicChronology.INSTANCE.isLeapYear(7));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(6));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(5));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(4));
        assertEquals(true, EthiopicChronology.INSTANCE.isLeapYear(3));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(2));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(1));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(0));
        assertEquals(true, EthiopicChronology.INSTANCE.isLeapYear(-1));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(-2));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(-3));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(-4));
        assertEquals(true, EthiopicChronology.INSTANCE.isLeapYear(-5));
        assertEquals(false, EthiopicChronology.INSTANCE.isLeapYear(-6));
    }

    public static Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {2006, 1, 30},
            {2006, 2, 30},
            {2006, 3, 30},
            {2006, 4, 30},
            {2006, 5, 30},
            {2006, 6, 30},
            {2006, 7, 30},
            {2006, 8, 30},
            {2006, 9, 30},
            {2006, 10, 30},
            {2006, 11, 30},
            {2006, 12, 30},
            {2006, 13, 5},
            {2007, 13, 6},
        };
    }

    @ParameterizedTest
    @MethodSource("data_lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(length, EthiopicDate.of(year, month, 1).lengthOfMonth());
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = -200; year < 200; year++) {
            EthiopicDate base = EthiopicChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            EthiopicEra era = (year <= 0 ? EthiopicEra.BEFORE_INCARNATION : EthiopicEra.INCARNATION);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            EthiopicDate eraBased = EthiopicChronology.INSTANCE.date(era, yoe, 1, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = -200; year < 200; year++) {
            EthiopicDate base = EthiopicChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            EthiopicEra era = (year <= 0 ? EthiopicEra.BEFORE_INCARNATION : EthiopicEra.INCARNATION);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            EthiopicDate eraBased = EthiopicChronology.INSTANCE.dateYearDay(era, yoe, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(4, EthiopicChronology.INSTANCE.prolepticYear(EthiopicEra.INCARNATION, 4));
        assertEquals(3, EthiopicChronology.INSTANCE.prolepticYear(EthiopicEra.INCARNATION, 3));
        assertEquals(2, EthiopicChronology.INSTANCE.prolepticYear(EthiopicEra.INCARNATION, 2));
        assertEquals(1, EthiopicChronology.INSTANCE.prolepticYear(EthiopicEra.INCARNATION, 1));
        assertEquals(0, EthiopicChronology.INSTANCE.prolepticYear(EthiopicEra.BEFORE_INCARNATION, 1));
        assertEquals(-1, EthiopicChronology.INSTANCE.prolepticYear(EthiopicEra.BEFORE_INCARNATION, 2));
        assertEquals(-2, EthiopicChronology.INSTANCE.prolepticYear(EthiopicEra.BEFORE_INCARNATION, 3));
        assertEquals(-3, EthiopicChronology.INSTANCE.prolepticYear(EthiopicEra.BEFORE_INCARNATION, 4));
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(EthiopicEra.INCARNATION, EthiopicChronology.INSTANCE.eraOf(1));
        assertEquals(EthiopicEra.BEFORE_INCARNATION, EthiopicChronology.INSTANCE.eraOf(0));
    }

    @Test
    public void test_Chronology_eraOf_invalid() {
        assertThrows(DateTimeException.class, () -> EthiopicChronology.INSTANCE.eraOf(2));
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = EthiopicChronology.INSTANCE.eras();
        assertEquals(2, eras.size());
        assertEquals(true, eras.contains(EthiopicEra.BEFORE_INCARNATION));
        assertEquals(true, eras.contains(EthiopicEra.INCARNATION));
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(ValueRange.of(1, 7), EthiopicChronology.INSTANCE.range(DAY_OF_WEEK));
        assertEquals(ValueRange.of(1, 5, 30), EthiopicChronology.INSTANCE.range(DAY_OF_MONTH));
        assertEquals(ValueRange.of(1, 365, 366), EthiopicChronology.INSTANCE.range(DAY_OF_YEAR));
        assertEquals(ValueRange.of(1, 13), EthiopicChronology.INSTANCE.range(MONTH_OF_YEAR));
    }

    //-----------------------------------------------------------------------
    // EthiopicDate.range
    //-----------------------------------------------------------------------
    public static Object[][] data_ranges() {
        return new Object[][] {
            {2007, 1, 23, DAY_OF_MONTH, 1, 30},
            {2007, 2, 23, DAY_OF_MONTH, 1, 30},
            {2007, 3, 23, DAY_OF_MONTH, 1, 30},
            {2007, 4, 23, DAY_OF_MONTH, 1, 30},
            {2007, 5, 23, DAY_OF_MONTH, 1, 30},
            {2007, 6, 23, DAY_OF_MONTH, 1, 30},
            {2007, 7, 23, DAY_OF_MONTH, 1, 30},
            {2007, 8, 23, DAY_OF_MONTH, 1, 30},
            {2007, 9, 23, DAY_OF_MONTH, 1, 30},
            {2007, 10, 23, DAY_OF_MONTH, 1, 30},
            {2007, 11, 23, DAY_OF_MONTH, 1, 30},
            {2007, 12, 23, DAY_OF_MONTH, 1, 30},
            {2007, 13, 2, DAY_OF_MONTH, 1, 6},
            {2007, 1, 23, DAY_OF_YEAR, 1, 366},
            {2007, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {2007, 12, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {2007, 13, 2, ALIGNED_WEEK_OF_MONTH, 1, 1},

            {2006, 13, 2, DAY_OF_MONTH, 1, 5},
            {2006, 13, 2, DAY_OF_YEAR, 1, 365},
            {2006, 13, 2, ALIGNED_WEEK_OF_MONTH, 1, 1},

            {2006, 2, 23, WeekFields.ISO.dayOfWeek(), 1, 7},
        };
    }

    @ParameterizedTest
    @MethodSource("data_ranges")
    public void test_range(int year, int month, int dom, TemporalField field, int expectedMin, int expectedMax) {
        assertEquals(ValueRange.of(expectedMin, expectedMax), EthiopicDate.of(year, month, dom).range(field));
    }

    @Test
    public void test_range_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> EthiopicDate.of(2007, 6, 30).range(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // EthiopicDate.getLong
    //-----------------------------------------------------------------------
    public static Object[][] data_getLong() {
        return new Object[][] {
            {2007, 6, 8, DAY_OF_WEEK, 7},
            {2007, 6, 8, DAY_OF_MONTH, 8},
            {2007, 6, 8, DAY_OF_YEAR, 30 * 5 + 8},
            {2007, 6, 8, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1},
            {2007, 6, 8, ALIGNED_WEEK_OF_MONTH, 2},
            {2007, 6, 8, ALIGNED_DAY_OF_WEEK_IN_YEAR, 4},
            {2007, 6, 8, ALIGNED_WEEK_OF_YEAR, 23},
            {2007, 6, 8, MONTH_OF_YEAR, 6},
            {2007, 6, 8, PROLEPTIC_MONTH, 2007 * 13 + 6 - 1},
            {2007, 6, 8, YEAR, 2007},
            {2007, 6, 8, ERA, 1},
            {1, 6, 8, ERA, 1},
            {0, 6, 8, ERA, 0},

            {2007, 6, 8, WeekFields.ISO.dayOfWeek(), 7},
        };
    }

    @ParameterizedTest
    @MethodSource("data_getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(expected, EthiopicDate.of(year, month, dom).getLong(field));
    }

    @Test
    public void test_getLong_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> EthiopicDate.of(2007, 6, 30).getLong(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // EthiopicDate.with
    //-----------------------------------------------------------------------
    public static Object[][] data_with() {
        return new Object[][] {
            {2007, 6, 8, DAY_OF_WEEK, 3, 2007, 6, 4},
            {2007, 6, 8, DAY_OF_WEEK, 7, 2007, 6, 8},
            {2007, 6, 8, DAY_OF_MONTH, 30, 2007, 6, 30},
            {2007, 6, 8, DAY_OF_MONTH, 8, 2007, 6, 8},
            {2007, 6, 8, DAY_OF_YEAR, 365, 2007, 13, 5},
            {2007, 6, 8, DAY_OF_YEAR, 366, 2007, 13, 6},
            {2007, 6, 8, DAY_OF_YEAR, 158, 2007, 6, 8},
            {2007, 6, 8, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2007, 6, 10},
            {2007, 6, 8, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1, 2007, 6, 8},
            {2007, 6, 8, ALIGNED_WEEK_OF_MONTH, 1, 2007, 6, 1},
            {2007, 6, 8, ALIGNED_WEEK_OF_MONTH, 2, 2007, 6, 8},
            {2007, 6, 8, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2007, 6, 6},
            {2007, 6, 8, ALIGNED_DAY_OF_WEEK_IN_YEAR, 4, 2007, 6, 8},
            {2007, 6, 8, ALIGNED_WEEK_OF_YEAR, 22, 2007, 6, 1},
            {2007, 6, 8, ALIGNED_WEEK_OF_YEAR, 23, 2007, 6, 8},
            {2007, 6, 8, MONTH_OF_YEAR, 7, 2007, 7, 8},
            {2007, 6, 8, MONTH_OF_YEAR, 6, 2007, 6, 8},
            {2007, 6, 8, PROLEPTIC_MONTH, 2009 * 13 + 3 - 1, 2009, 3, 8},
            {2007, 6, 8, PROLEPTIC_MONTH, 2007 * 13 + 6 - 1, 2007, 6, 8},
            {2007, 6, 8, YEAR, 2012, 2012, 6, 8},
            {2007, 6, 8, YEAR, 2007, 2007, 6, 8},
            {2007, 6, 8, YEAR_OF_ERA, 2012, 2012, 6, 8},
            {2007, 6, 8, YEAR_OF_ERA, 2007, 2007, 6, 8},
            {2007, 6, 8, ERA, 0, -2006, 6, 8},
            {2007, 6, 8, ERA, 1, 2007, 6, 8},

            {2006, 6, 8, DAY_OF_YEAR, 366, 2006, 13, 5},
            {2006, 3, 30, MONTH_OF_YEAR, 13, 2006, 13, 5},
            {2007, 3, 30, MONTH_OF_YEAR, 13, 2007, 13, 6},
            {2007, 13, 6, DAY_OF_MONTH, 30, 2007, 13, 6},
            {2007, 13, 6, YEAR, 2006, 2006, 13, 5},
            {-2005, 6, 8, YEAR_OF_ERA, 2004, -2003, 6, 8},
            {2007, 6, 8, WeekFields.ISO.dayOfWeek(), 4, 2007, 6, 5},
        };
    }

    @ParameterizedTest
    @MethodSource("data_with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(EthiopicDate.of(expectedYear, expectedMonth, expectedDom), EthiopicDate.of(year, month, dom).with(field, value));
    }

    public static Object[][] data_with_bad() {
        return new Object[][] {
            {2007, 6, 8, DAY_OF_WEEK, 0},
            {2007, 6, 8, DAY_OF_WEEK, 8},
            {2007, 6, 8, DAY_OF_MONTH, 0},
            {2007, 6, 8, DAY_OF_MONTH, 31},
            {2007, 13, 1, DAY_OF_MONTH, 31},
            {2007, 6, 8, DAY_OF_YEAR, 0},
            {2007, 6, 8, DAY_OF_YEAR, 367},
            {2007, 6, 8, MONTH_OF_YEAR, 0},
            {2007, 6, 8, MONTH_OF_YEAR, 14},
        };
    }

    @ParameterizedTest
    @MethodSource("data_with_bad")
    public void test_with_TemporalField_badValue(int year, int month, int dom, TemporalField field, long value) {
        assertThrows(DateTimeException.class, () -> EthiopicDate.of(year, month, dom).with(field, value));
    }

    @Test
    public void test_with_TemporalField_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> EthiopicDate.of(2006, 6, 30).with(MINUTE_OF_DAY, 0));
    }

    //-----------------------------------------------------------------------
    // EthiopicDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        EthiopicDate base = EthiopicDate.of(2005, 10, 29);
        EthiopicDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(EthiopicDate.of(2005, 10, 30), test);
    }

    @Test
    public void test_adjust2() {
        EthiopicDate base = EthiopicDate.of(2005, 13, 2);
        EthiopicDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(EthiopicDate.of(2005, 13, 5), test);
    }

    //-----------------------------------------------------------------------
    // EthiopicDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        EthiopicDate ethiopic = EthiopicDate.of(2001, 1, 4);
        EthiopicDate test = ethiopic.with(LocalDate.of(2011, 10, 16));
        assertEquals(EthiopicDate.of(2004, 2, 5), test);
    }

    @Test
    public void test_adjust_toMonth() {
        EthiopicDate ethiopic = EthiopicDate.of(2004, 1, 4);
        assertThrows(DateTimeException.class, () -> ethiopic.with(Month.APRIL));
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(EthiopicDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToEthiopicDate() {
        EthiopicDate ethiopic = EthiopicDate.of(2004, 2, 5);
        LocalDate test = LocalDate.MIN.with(ethiopic);
        assertEquals(LocalDate.of(2011, 10, 16), test);
    }

    @Test
    public void test_LocalDateTime_adjustToEthiopicDate() {
        EthiopicDate ethiopic = EthiopicDate.of(2004, 2, 5);
        LocalDateTime test = LocalDateTime.MIN.with(ethiopic);
        assertEquals(LocalDateTime.of(2011, 10, 16, 0, 0), test);
    }

    //-----------------------------------------------------------------------
    // EthiopicDate.plus
    //-----------------------------------------------------------------------
    public static Object[][] data_plus() {
        return new Object[][] {
            {2006, 5, 26, 0, DAYS, 2006, 5, 26},
            {2006, 5, 26, 8, DAYS, 2006, 6, 4},
            {2006, 5, 26, -3, DAYS, 2006, 5, 23},
            {2006, 5, 26, 0, WEEKS, 2006, 5, 26},
            {2006, 5, 26, 3, WEEKS, 2006, 6, 17},
            {2006, 5, 26, -5, WEEKS, 2006, 4, 21},
            {2006, 5, 26, 0, MONTHS, 2006, 5, 26},
            {2006, 5, 26, 3, MONTHS, 2006, 8, 26},
            {2006, 5, 26, -6, MONTHS, 2005, 12, 26},
            {2006, 5, 26, 0, YEARS, 2006, 5, 26},
            {2006, 5, 26, 3, YEARS, 2009, 5, 26},
            {2006, 5, 26, -5, YEARS, 2001, 5, 26},
            {2006, 5, 26, 0, DECADES, 2006, 5, 26},
            {2006, 5, 26, 3, DECADES, 2036, 5, 26},
            {2006, 5, 26, -5, DECADES, 1956, 5, 26},
            {2006, 5, 26, 0, CENTURIES, 2006, 5, 26},
            {2006, 5, 26, 3, CENTURIES, 2306, 5, 26},
            {2006, 5, 26, -5, CENTURIES, 1506, 5, 26},
            {2006, 5, 26, 0, MILLENNIA, 2006, 5, 26},
            {2006, 5, 26, 3, MILLENNIA, 5006, 5, 26},
            {2006, 5, 26, -5, MILLENNIA, 2006 - 5000, 5, 26},
            {2006, 5, 26, -1, ERAS, -2005, 5, 26},
        };
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(EthiopicDate.of(expectedYear, expectedMonth, expectedDom), EthiopicDate.of(year, month, dom).plus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(EthiopicDate.of(expectedYear, expectedMonth, expectedDom), EthiopicDate.of(year, month, dom).minus(amount, unit));
    }

    @Test
    public void test_plus_TemporalUnit_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> EthiopicDate.of(2006, 6, 30).plus(0, MINUTES));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(EthiopicDate.of(2006, 7, 29), EthiopicDate.of(2006, 5, 26).plus(EthiopicChronology.INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_plus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> EthiopicDate.of(2006, 5, 26).plus(Period.ofMonths(2)));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(EthiopicDate.of(2006, 3, 23), EthiopicDate.of(2006, 5, 26).minus(EthiopicChronology.INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_minus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> EthiopicDate.of(2006, 5, 26).minus(Period.ofMonths(2)));
    }

    //-----------------------------------------------------------------------
    // EthiopicDate.until
    //-----------------------------------------------------------------------
    public static Object[][] data_until() {
        return new Object[][] {
            {2006, 5, 26, 2006, 5, 26, DAYS, 0},
            {2006, 5, 26, 2006, 6, 1, DAYS, 5},
            {2006, 5, 26, 2006, 5, 20, DAYS, -6},
            {2006, 5, 26, 2006, 5, 26, WEEKS, 0},
            {2006, 5, 26, 2006, 6, 2, WEEKS, 0},
            {2006, 5, 26, 2006, 6, 3, WEEKS, 1},
            {2006, 5, 26, 2006, 5, 26, MONTHS, 0},
            {2006, 5, 26, 2006, 6, 25, MONTHS, 0},
            {2006, 5, 26, 2006, 6, 26, MONTHS, 1},
            {2006, 5, 26, 2006, 5, 26, YEARS, 0},
            {2006, 5, 26, 2007, 5, 25, YEARS, 0},
            {2006, 5, 26, 2007, 5, 26, YEARS, 1},
            {2006, 5, 26, 2006, 5, 26, DECADES, 0},
            {2006, 5, 26, 2016, 5, 25, DECADES, 0},
            {2006, 5, 26, 2016, 5, 26, DECADES, 1},
            {2006, 5, 26, 2006, 5, 26, CENTURIES, 0},
            {2006, 5, 26, 2106, 5, 25, CENTURIES, 0},
            {2006, 5, 26, 2106, 5, 26, CENTURIES, 1},
            {2006, 5, 26, 2006, 5, 26, MILLENNIA, 0},
            {2006, 5, 26, 3006, 5, 25, MILLENNIA, 0},
            {2006, 5, 26, 3006, 5, 26, MILLENNIA, 1},
        };
    }

    @ParameterizedTest
    @MethodSource("data_until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        EthiopicDate start = EthiopicDate.of(year1, month1, dom1);
        EthiopicDate end = EthiopicDate.of(year2, month2, dom2);
        assertEquals(expected, start.until(end, unit));
    }

    @Test
    public void test_until_TemporalUnit_unsupported() {
        EthiopicDate start = EthiopicDate.of(2006, 6, 30);
        EthiopicDate end = EthiopicDate.of(2006, 7, 1);
        assertThrows(UnsupportedTemporalTypeException.class, () -> start.until(end, MINUTES));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(EthiopicDate.of(2004, 1, 3), EthiopicDate.of(2004, 1, 3))
            .addEqualityGroup(EthiopicDate.of(2004, 1, 4), EthiopicDate.of(2004, 1, 4))
            .addEqualityGroup(EthiopicDate.of(2004, 2, 3), EthiopicDate.of(2004, 2, 3))
            .addEqualityGroup(EthiopicDate.of(2005, 1, 3), EthiopicDate.of(2005, 1, 3))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public static Object[][] data_toString() {
        return new Object[][] {
            {EthiopicDate.of(1, 1, 1), "Ethiopic INCARNATION 1-01-01"},
            {EthiopicDate.of(2007, 10, 28), "Ethiopic INCARNATION 2007-10-28"},
            {EthiopicDate.of(2007, 10, 29), "Ethiopic INCARNATION 2007-10-29"},
            {EthiopicDate.of(2007, 13, 5), "Ethiopic INCARNATION 2007-13-05"},
            {EthiopicDate.of(2007, 13, 6), "Ethiopic INCARNATION 2007-13-06"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_toString")
    public void test_toString(EthiopicDate ethiopic, String expected) {
        assertEquals(expected, ethiopic.toString());
    }

}
