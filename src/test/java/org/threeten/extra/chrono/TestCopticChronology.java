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
import static org.testng.Assert.assertEquals;

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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestCopticChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        Chronology chrono = Chronology.of("Coptic");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, CopticChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Coptic");
        Assert.assertEquals(chrono.getCalendarType(), "coptic");
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("coptic");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, CopticChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Coptic");
        Assert.assertEquals(chrono.getCalendarType(), "coptic");
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
        return new Object[][] {
            {CopticDate.of(-1, 13, 6), LocalDate.of(283, 8, 29)},
            {CopticDate.of(0, 1, 1), LocalDate.of(283, 8, 30)},
            {CopticDate.of(0, 1, 30), LocalDate.of(283, 9, 28)},
            {CopticDate.of(0, 12, 30), LocalDate.of(284, 8, 23)},
            {CopticDate.of(0, 13, 1), LocalDate.of(284, 8, 24)},
            {CopticDate.of(0, 13, 5), LocalDate.of(284, 8, 28)},
            {CopticDate.of(0, 13, 4), LocalDate.of(284, 8, 27)},

            {CopticDate.of(1, 1, 1), LocalDate.of(284, 8, 29)},
            {CopticDate.of(1, 1, 2), LocalDate.of(284, 8, 30)},
            {CopticDate.of(1, 1, 3), LocalDate.of(284, 8, 31)},

            {CopticDate.of(2, 1, 1), LocalDate.of(285, 8, 29)},
            {CopticDate.of(3, 1, 1), LocalDate.of(286, 8, 29)},
            {CopticDate.of(3, 13, 6), LocalDate.of(287, 8, 29)},
            {CopticDate.of(4, 1, 1), LocalDate.of(287, 8, 30)},
            {CopticDate.of(4, 7, 3), LocalDate.of(288, 2, 28)},
            {CopticDate.of(4, 7, 4), LocalDate.of(288, 2, 29)},
            {CopticDate.of(5, 1, 1), LocalDate.of(288, 8, 29)},
            {CopticDate.of(1662, 3, 3), LocalDate.of(1945, 11, 12)},
            {CopticDate.of(1728, 10, 28), LocalDate.of(2012, 7, 5)},
            {CopticDate.of(1728, 10, 29), LocalDate.of(2012, 7, 6)},
        };
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_from_CopticDate(CopticDate coptic, LocalDate iso) {
        assertEquals(LocalDate.from(coptic), iso);
    }

    @Test(dataProvider = "samples")
    public void test_CopticDate_from_LocalDate(CopticDate coptic, LocalDate iso) {
        assertEquals(CopticDate.from(iso), coptic);
    }

    @Test(dataProvider = "samples")
    public void test_CopticDate_chronology_dateEpochDay(CopticDate coptic, LocalDate iso) {
        assertEquals(CopticChronology.INSTANCE.dateEpochDay(iso.toEpochDay()), coptic);
    }

    @Test(dataProvider = "samples")
    public void test_CopticDate_toEpochDay(CopticDate coptic, LocalDate iso) {
        assertEquals(coptic.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider = "samples")
    public void test_CopticDate_until_CopticDate(CopticDate coptic, LocalDate iso) {
        assertEquals(coptic.until(coptic), CopticChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_CopticDate_until_LocalDate(CopticDate coptic, LocalDate iso) {
        assertEquals(coptic.until(iso), CopticChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_until_CopticDate(CopticDate coptic, LocalDate iso) {
        assertEquals(iso.until(coptic), Period.ZERO);
    }

    @Test(dataProvider = "samples")
    public void test_Chronology_date_Temporal(CopticDate coptic, LocalDate iso) {
        assertEquals(CopticChronology.INSTANCE.date(iso), coptic);
    }

    @Test(dataProvider = "samples")
    public void test_plusDays(CopticDate coptic, LocalDate iso) {
        assertEquals(LocalDate.from(coptic.plus(0, DAYS)), iso);
        assertEquals(LocalDate.from(coptic.plus(1, DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(coptic.plus(35, DAYS)), iso.plusDays(35));
        assertEquals(LocalDate.from(coptic.plus(-1, DAYS)), iso.plusDays(-1));
        assertEquals(LocalDate.from(coptic.plus(-60, DAYS)), iso.plusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_minusDays(CopticDate coptic, LocalDate iso) {
        assertEquals(LocalDate.from(coptic.minus(0, DAYS)), iso);
        assertEquals(LocalDate.from(coptic.minus(1, DAYS)), iso.minusDays(1));
        assertEquals(LocalDate.from(coptic.minus(35, DAYS)), iso.minusDays(35));
        assertEquals(LocalDate.from(coptic.minus(-1, DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(coptic.minus(-60, DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_until_DAYS(CopticDate coptic, LocalDate iso) {
        assertEquals(coptic.until(iso.plusDays(0), DAYS), 0);
        assertEquals(coptic.until(iso.plusDays(1), DAYS), 1);
        assertEquals(coptic.until(iso.plusDays(35), DAYS), 35);
        assertEquals(coptic.until(iso.minusDays(40), DAYS), -40);
    }

    @DataProvider(name = "badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {1728, 0, 0},

            {1728, -1, 1},
            {1728, 0, 1},
            {1728, 14, 1},
            {1728, 15, 1},

            {1728, 1, -1},
            {1728, 1, 0},
            {1728, 1, 31},
            {1728, 1, 32},

            {1728, 12, -1},
            {1728, 12, 0},
            {1728, 12, 31},
            {1728, 12, 32},

            {1728, 13, -1},
            {1728, 13, 0},
            {1728, 13, 6},
            {1728, 13, 7},

            {1727, 13, -1},
            {1727, 13, 0},
            {1727, 13, 7},
            {1727, 13, 8},
        };
    }

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates(int year, int month, int dom) {
        CopticDate.of(year, month, dom);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_chronology_dateYearDay_badDate() {
        CopticChronology.INSTANCE.dateYearDay(1728, 366);
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        for (int year = -200; year < 200; year++) {
            CopticDate base = CopticDate.of(year, 1, 1);
            assertEquals(base.isLeapYear(), ((year - 3) % 4) == 0);
            assertEquals(CopticChronology.INSTANCE.isLeapYear(year), ((year + 400 - 3) % 4) == 0);
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(CopticChronology.INSTANCE.isLeapYear(8), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(7), true);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(6), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(5), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(4), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(3), true);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(2), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(1), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(0), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(-1), true);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(-2), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(-3), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(-4), false);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(-5), true);
        assertEquals(CopticChronology.INSTANCE.isLeapYear(-6), false);
    }

    @DataProvider(name = "lengthOfMonth")
    Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {1726, 1, 30},
            {1726, 2, 30},
            {1726, 3, 30},
            {1726, 4, 30},
            {1726, 5, 30},
            {1726, 6, 30},
            {1726, 7, 30},
            {1726, 8, 30},
            {1726, 9, 30},
            {1726, 10, 30},
            {1726, 11, 30},
            {1726, 12, 30},
            {1726, 13, 5},
            {1727, 13, 6},
        };
    }

    @Test(dataProvider = "lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(CopticDate.of(year, month, 1).lengthOfMonth(), length);
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = -200; year < 200; year++) {
            CopticDate base = CopticChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            CopticEra era = (year <= 0 ? CopticEra.BEFORE_AM : CopticEra.AM);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            CopticDate eraBased = CopticChronology.INSTANCE.date(era, yoe, 1, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = -200; year < 200; year++) {
            CopticDate base = CopticChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            CopticEra era = (year <= 0 ? CopticEra.BEFORE_AM : CopticEra.AM);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            CopticDate eraBased = CopticChronology.INSTANCE.dateYearDay(era, yoe, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(CopticChronology.INSTANCE.prolepticYear(CopticEra.AM, 4), 4);
        assertEquals(CopticChronology.INSTANCE.prolepticYear(CopticEra.AM, 3), 3);
        assertEquals(CopticChronology.INSTANCE.prolepticYear(CopticEra.AM, 2), 2);
        assertEquals(CopticChronology.INSTANCE.prolepticYear(CopticEra.AM, 1), 1);
        assertEquals(CopticChronology.INSTANCE.prolepticYear(CopticEra.BEFORE_AM, 1), 0);
        assertEquals(CopticChronology.INSTANCE.prolepticYear(CopticEra.BEFORE_AM, 2), -1);
        assertEquals(CopticChronology.INSTANCE.prolepticYear(CopticEra.BEFORE_AM, 3), -2);
        assertEquals(CopticChronology.INSTANCE.prolepticYear(CopticEra.BEFORE_AM, 4), -3);
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(CopticChronology.INSTANCE.eraOf(1), CopticEra.AM);
        assertEquals(CopticChronology.INSTANCE.eraOf(0), CopticEra.BEFORE_AM);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_Chronology_eraOf_invalid() {
        CopticChronology.INSTANCE.eraOf(2);
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = CopticChronology.INSTANCE.eras();
        assertEquals(eras.size(), 2);
        assertEquals(eras.contains(CopticEra.BEFORE_AM), true);
        assertEquals(eras.contains(CopticEra.AM), true);
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(CopticChronology.INSTANCE.range(DAY_OF_WEEK), ValueRange.of(1, 7));
        assertEquals(CopticChronology.INSTANCE.range(DAY_OF_MONTH), ValueRange.of(1, 5, 30));
        assertEquals(CopticChronology.INSTANCE.range(DAY_OF_YEAR), ValueRange.of(1, 365, 366));
        assertEquals(CopticChronology.INSTANCE.range(MONTH_OF_YEAR), ValueRange.of(1, 13));
    }

    //-----------------------------------------------------------------------
    // CopticDate.range
    //-----------------------------------------------------------------------
    @DataProvider(name = "ranges")
    Object[][] data_ranges() {
        return new Object[][] {
            {1727, 1, 23, DAY_OF_MONTH, 1, 30},
            {1727, 2, 23, DAY_OF_MONTH, 1, 30},
            {1727, 3, 23, DAY_OF_MONTH, 1, 30},
            {1727, 4, 23, DAY_OF_MONTH, 1, 30},
            {1727, 5, 23, DAY_OF_MONTH, 1, 30},
            {1727, 6, 23, DAY_OF_MONTH, 1, 30},
            {1727, 7, 23, DAY_OF_MONTH, 1, 30},
            {1727, 8, 23, DAY_OF_MONTH, 1, 30},
            {1727, 9, 23, DAY_OF_MONTH, 1, 30},
            {1727, 10, 23, DAY_OF_MONTH, 1, 30},
            {1727, 11, 23, DAY_OF_MONTH, 1, 30},
            {1727, 12, 23, DAY_OF_MONTH, 1, 30},
            {1727, 13, 2, DAY_OF_MONTH, 1, 6},
            {1727, 1, 23, DAY_OF_YEAR, 1, 366},
            {1727, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1727, 12, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1727, 13, 2, ALIGNED_WEEK_OF_MONTH, 1, 1},

            {1726, 13, 2, DAY_OF_MONTH, 1, 5},
            {1726, 13, 2, DAY_OF_YEAR, 1, 365},
            {1726, 13, 2, ALIGNED_WEEK_OF_MONTH, 1, 1},

            {1726, 2, 23, WeekFields.ISO.dayOfWeek(), 1, 7},
        };
    }

    @Test(dataProvider = "ranges")
    public void test_range(int year, int month, int dom, TemporalField field, int expectedMin, int expectedMax) {
        assertEquals(CopticDate.of(year, month, dom).range(field), ValueRange.of(expectedMin, expectedMax));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_unsupported() {
        CopticDate.of(1727, 6, 30).range(MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // CopticDate.getLong
    //-----------------------------------------------------------------------
    @DataProvider(name = "getLong")
    Object[][] data_getLong() {
        return new Object[][] {
            {1727, 6, 8, DAY_OF_WEEK, 2},
            {1727, 6, 8, DAY_OF_MONTH, 8},
            {1727, 6, 8, DAY_OF_YEAR, 30 * 5 + 8},
            {1727, 6, 8, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1},
            {1727, 6, 8, ALIGNED_WEEK_OF_MONTH, 2},
            {1727, 6, 8, ALIGNED_DAY_OF_WEEK_IN_YEAR, 4},
            {1727, 6, 8, ALIGNED_WEEK_OF_YEAR, 23},
            {1727, 6, 8, MONTH_OF_YEAR, 6},
            {1727, 6, 8, PROLEPTIC_MONTH, 1727 * 13 + 6 - 1},
            {1727, 6, 8, YEAR, 1727},
            {1727, 6, 8, ERA, 1},
            {1, 6, 8, ERA, 1},
            {0, 6, 8, ERA, 0},

            {1727, 6, 8, WeekFields.ISO.dayOfWeek(), 2},
        };
    }

    @Test(dataProvider = "getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(CopticDate.of(year, month, dom).getLong(field), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_unsupported() {
        CopticDate.of(1727, 6, 30).getLong(MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // CopticDate.with
    //-----------------------------------------------------------------------
    @DataProvider(name = "with")
    Object[][] data_with() {
        return new Object[][] {
            {1727, 6, 8, DAY_OF_WEEK, 4, 1727, 6, 10},
            {1727, 6, 8, DAY_OF_WEEK, 2, 1727, 6, 8},
            {1727, 6, 8, DAY_OF_MONTH, 30, 1727, 6, 30},
            {1727, 6, 8, DAY_OF_MONTH, 8, 1727, 6, 8},
            {1727, 6, 8, DAY_OF_YEAR, 365, 1727, 13, 5},
            {1727, 6, 8, DAY_OF_YEAR, 158, 1727, 6, 8},
            {1727, 6, 8, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 1727, 6, 10},
            {1727, 6, 8, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1, 1727, 6, 8},
            {1727, 6, 8, ALIGNED_WEEK_OF_MONTH, 1, 1727, 6, 1},
            {1727, 6, 8, ALIGNED_WEEK_OF_MONTH, 2, 1727, 6, 8},
            {1727, 6, 8, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 1727, 6, 6},
            {1727, 6, 8, ALIGNED_DAY_OF_WEEK_IN_YEAR, 4, 1727, 6, 8},
            {1727, 6, 8, ALIGNED_WEEK_OF_YEAR, 22, 1727, 6, 1},
            {1727, 6, 8, ALIGNED_WEEK_OF_YEAR, 23, 1727, 6, 8},
            {1727, 6, 8, MONTH_OF_YEAR, 7, 1727, 7, 8},
            {1727, 6, 8, MONTH_OF_YEAR, 6, 1727, 6, 8},
            {1727, 6, 8, PROLEPTIC_MONTH, 2009 * 13 + 3 - 1, 2009, 3, 8},
            {1727, 6, 8, PROLEPTIC_MONTH, 1727 * 13 + 6 - 1, 1727, 6, 8},
            {1727, 6, 8, YEAR, 1728, 1728, 6, 8},
            {1727, 6, 8, YEAR, 1727, 1727, 6, 8},
            {1727, 6, 8, YEAR_OF_ERA, 2012, 2012, 6, 8},
            {1727, 6, 8, YEAR_OF_ERA, 1727, 1727, 6, 8},
            {1727, 6, 8, ERA, 0, -1726, 6, 8},
            {1727, 6, 8, ERA, 1, 1727, 6, 8},

            {1726, 3, 30, MONTH_OF_YEAR, 13, 1726, 13, 5},
            {1727, 3, 30, MONTH_OF_YEAR, 13, 1727, 13, 6},
            {1727, 13, 6, YEAR, 2006, 2006, 13, 5},
            {-1727, 6, 8, YEAR_OF_ERA, 1722, -1721, 6, 8},
            {1727, 6, 8, WeekFields.ISO.dayOfWeek(), 5, 1727, 6, 11},
        };
    }

    @Test(dataProvider = "with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(CopticDate.of(year, month, dom).with(field, value), CopticDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_with_TemporalField_unsupported() {
        CopticDate.of(1727, 6, 30).with(MINUTE_OF_DAY, 0);
    }

    //-----------------------------------------------------------------------
    // CopticDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        CopticDate base = CopticDate.of(1728, 10, 29);
        CopticDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, CopticDate.of(1728, 10, 30));
    }

    @Test
    public void test_adjust2() {
        CopticDate base = CopticDate.of(1728, 13, 2);
        CopticDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, CopticDate.of(1728, 13, 5));
    }

    //-----------------------------------------------------------------------
    // CopticDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        CopticDate coptic = CopticDate.of(1726, 1, 4);
        CopticDate test = coptic.with(LocalDate.of(2012, 7, 6));
        assertEquals(test, CopticDate.of(1728, 10, 29));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_adjust_toMonth() {
        CopticDate coptic = CopticDate.of(1726, 1, 4);
        coptic.with(Month.APRIL);
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(CopticDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToCopticDate() {
        CopticDate coptic = CopticDate.of(1728, 10, 29);
        LocalDate test = LocalDate.MIN.with(coptic);
        assertEquals(test, LocalDate.of(2012, 7, 6));
    }

    @Test
    public void test_LocalDateTime_adjustToCopticDate() {
        CopticDate coptic = CopticDate.of(1728, 10, 29);
        LocalDateTime test = LocalDateTime.MIN.with(coptic);
        assertEquals(test, LocalDateTime.of(2012, 7, 6, 0, 0));
    }

    //-----------------------------------------------------------------------
    // CopticDate.plus
    //-----------------------------------------------------------------------
    @DataProvider(name = "plus")
    Object[][] data_plus() {
        return new Object[][] {
            {1726, 5, 26, 0, DAYS, 1726, 5, 26},
            {1726, 5, 26, 8, DAYS, 1726, 6, 4},
            {1726, 5, 26, -3, DAYS, 1726, 5, 23},
            {1726, 5, 26, 0, WEEKS, 1726, 5, 26},
            {1726, 5, 26, 3, WEEKS, 1726, 6, 17},
            {1726, 5, 26, -5, WEEKS, 1726, 4, 21},
            {1726, 5, 26, 0, MONTHS, 1726, 5, 26},
            {1726, 5, 26, 3, MONTHS, 1726, 8, 26},
            {1726, 5, 26, -6, MONTHS, 1725, 12, 26},
            {1726, 5, 26, 0, YEARS, 1726, 5, 26},
            {1726, 5, 26, 3, YEARS, 1729, 5, 26},
            {1726, 5, 26, -5, YEARS, 1721, 5, 26},
            {1726, 5, 26, 0, DECADES, 1726, 5, 26},
            {1726, 5, 26, 3, DECADES, 1756, 5, 26},
            {1726, 5, 26, -5, DECADES, 1676, 5, 26},
            {1726, 5, 26, 0, CENTURIES, 1726, 5, 26},
            {1726, 5, 26, 3, CENTURIES, 2026, 5, 26},
            {1726, 5, 26, -5, CENTURIES, 1226, 5, 26},
            {1726, 5, 26, 0, MILLENNIA, 1726, 5, 26},
            {1726, 5, 26, 3, MILLENNIA, 4726, 5, 26},
            {1726, 5, 26, -5, MILLENNIA, 1726 - 5000, 5, 26},
            {1726, 5, 26, -1, ERAS, -1725, 5, 26},
        };
    }

    @Test(dataProvider = "plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(CopticDate.of(year, month, dom).plus(amount, unit), CopticDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(dataProvider = "plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(CopticDate.of(year, month, dom).minus(amount, unit), CopticDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_plus_TemporalUnit_unsupported() {
        CopticDate.of(1727, 6, 30).plus(0, MINUTES);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(CopticDate.of(1727, 5, 26).plus(CopticChronology.INSTANCE.period(0, 2, 3)), CopticDate.of(1727, 7, 29));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_Period_ISO() {
        assertEquals(CopticDate.of(1727, 5, 26).plus(Period.ofMonths(2)), CopticDate.of(1727, 7, 26));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(CopticDate.of(1727, 5, 26).minus(CopticChronology.INSTANCE.period(0, 2, 3)), CopticDate.of(1727, 3, 23));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_Period_ISO() {
        assertEquals(CopticDate.of(1727, 5, 26).minus(Period.ofMonths(2)), CopticDate.of(1727, 3, 26));
    }

    //-----------------------------------------------------------------------
    // CopticDate.until
    //-----------------------------------------------------------------------
    @DataProvider(name = "until")
    Object[][] data_until() {
        return new Object[][] {
            {1726, 5, 26, 1726, 5, 26, DAYS, 0},
            {1726, 5, 26, 1726, 6, 1, DAYS, 5},
            {1726, 5, 26, 1726, 5, 20, DAYS, -6},
            {1726, 5, 26, 1726, 5, 26, WEEKS, 0},
            {1726, 5, 26, 1726, 6, 2, WEEKS, 0},
            {1726, 5, 26, 1726, 6, 3, WEEKS, 1},
            {1726, 5, 26, 1726, 5, 26, MONTHS, 0},
            {1726, 5, 26, 1726, 6, 25, MONTHS, 0},
            {1726, 5, 26, 1726, 6, 26, MONTHS, 1},
            {1726, 5, 26, 1726, 5, 26, YEARS, 0},
            {1726, 5, 26, 1727, 5, 25, YEARS, 0},
            {1726, 5, 26, 1727, 5, 26, YEARS, 1},
            {1726, 5, 26, 1726, 5, 26, DECADES, 0},
            {1726, 5, 26, 1736, 5, 25, DECADES, 0},
            {1726, 5, 26, 1736, 5, 26, DECADES, 1},
            {1726, 5, 26, 1726, 5, 26, CENTURIES, 0},
            {1726, 5, 26, 1826, 5, 25, CENTURIES, 0},
            {1726, 5, 26, 1826, 5, 26, CENTURIES, 1},
            {1726, 5, 26, 1726, 5, 26, MILLENNIA, 0},
            {1726, 5, 26, 2726, 5, 25, MILLENNIA, 0},
            {1726, 5, 26, 2726, 5, 26, MILLENNIA, 1},
        };
    }

    @Test(dataProvider = "until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        CopticDate start = CopticDate.of(year1, month1, dom1);
        CopticDate end = CopticDate.of(year2, month2, dom2);
        assertEquals(start.until(end, unit), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_until_TemporalUnit_unsupported() {
        CopticDate start = CopticDate.of(1726, 6, 30);
        CopticDate end = CopticDate.of(1726, 7, 1);
        start.until(end, MINUTES);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    void test_equals() {
        CopticDate a1 = CopticDate.of(1728, 1, 3);
        CopticDate a2 = CopticDate.of(1728, 1, 3);
        CopticDate b = CopticDate.of(1728, 1, 4);
        CopticDate c = CopticDate.of(1728, 2, 3);
        CopticDate d = CopticDate.of(1729, 1, 3);

        assertEquals(a1.equals(a1), true);
        assertEquals(a1.equals(a2), true);
        assertEquals(a1.equals(b), false);
        assertEquals(a1.equals(c), false);
        assertEquals(a1.equals(d), false);

        assertEquals(a1.equals(null), false);
        assertEquals(a1.equals(""), false);

        assertEquals(a1.hashCode(), a2.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name = "toString")
    Object[][] data_toString() {
        return new Object[][] {
            {CopticDate.of(1, 1, 1), "Coptic AM 1-01-01"},
            {CopticDate.of(1728, 10, 28), "Coptic AM 1728-10-28"},
            {CopticDate.of(1728, 10, 29), "Coptic AM 1728-10-29"},
            {CopticDate.of(1727, 13, 5), "Coptic AM 1727-13-05"},
            {CopticDate.of(1727, 13, 6), "Coptic AM 1727-13-06"},
        };
    }

    @Test(dataProvider = "toString")
    public void test_toString(CopticDate coptic, String expected) {
        assertEquals(coptic.toString(), expected);
    }

}
