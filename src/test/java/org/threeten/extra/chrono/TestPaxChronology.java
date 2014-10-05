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
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Clockwork-Muse
 */
@Test
@SuppressWarnings({"static-method", "javadoc", "checkstyle:magicnumber", "checkstyle:javadocmethod", "checkstyle:designforextension"})
public class TestPaxChronology {

    // -----------------------------------------------------------------------
    // Chronology.of(String)
    // -----------------------------------------------------------------------
    @Test
    @SuppressWarnings("checkstyle:multiplestringliterals")
    public void test_chronology_of_name() {
        final Chronology chrono = Chronology.of("Pax");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, PaxChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Pax");
        Assert.assertEquals(chrono.getCalendarType(), "pax");
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("pax");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, PaxChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Pax");
        Assert.assertEquals(chrono.getCalendarType(), "pax");
    }

    // -----------------------------------------------------------------------
    // creation, toLocalDate()
    // -----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
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
        };
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_from_PaxDate(final PaxDate pax, final LocalDate iso) {
        assertEquals(LocalDate.from(pax), iso);
    }

    @Test(dataProvider = "samples")
    public void test_PaxDate_from_LocalDate(PaxDate pax, LocalDate iso) {
        assertEquals(PaxDate.from(iso), pax);
    }

    @Test(dataProvider = "samples")
    public void test_PaxDate_chronology_dateEpochDay(PaxDate pax, LocalDate iso) {
        assertEquals(PaxChronology.INSTANCE.dateEpochDay(iso.toEpochDay()), pax);
    }

    @Test(dataProvider = "samples")
    public void test_PaxDate_toEpochDay(PaxDate pax, LocalDate iso) {
        assertEquals(pax.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider = "samples")
    public void test_PaxDate_until_CoptiDate(PaxDate pax, LocalDate iso) {
        assertEquals(pax.until(pax), PaxChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_PaxDate_until_LocalDate(PaxDate pax, LocalDate iso) {
        assertEquals(pax.until(iso), PaxChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_until_CoptiDate(PaxDate pax, LocalDate iso) {
        assertEquals(iso.until(pax), Period.ZERO);
    }

    @Test(dataProvider = "samples")
    public void test_Chronology_date_Temporal(PaxDate pax, LocalDate iso) {
        assertEquals(PaxChronology.INSTANCE.date(iso), pax);
    }

    @Test(dataProvider = "samples")
    public void test_plusDays(PaxDate pax, LocalDate iso) {
        assertEquals(LocalDate.from(pax.plus(0, DAYS)), iso);
        assertEquals(LocalDate.from(pax.plus(1, DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(pax.plus(35, DAYS)), iso.plusDays(35));
        assertEquals(LocalDate.from(pax.plus(-1, DAYS)), iso.plusDays(-1));
        assertEquals(LocalDate.from(pax.plus(-60, DAYS)), iso.plusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_minusDays(PaxDate pax, LocalDate iso) {
        assertEquals(LocalDate.from(pax.minus(0, DAYS)), iso);
        assertEquals(LocalDate.from(pax.minus(1, DAYS)), iso.minusDays(1));
        assertEquals(LocalDate.from(pax.minus(35, DAYS)), iso.minusDays(35));
        assertEquals(LocalDate.from(pax.minus(-1, DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(pax.minus(-60, DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_until_DAYS(PaxDate pax, LocalDate iso) {
        assertEquals(pax.until(iso.plusDays(0), DAYS), 0);
        assertEquals(pax.until(iso.plusDays(1), DAYS), 1);
        assertEquals(pax.until(iso.plusDays(35), DAYS), 35);
        assertEquals(pax.until(iso.minusDays(40), DAYS), -40);
    }

    @DataProvider(name = "badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {2012, 0, 0},

            {2012, 0, 1},
            {2012, 1, 1},
            {2012, -1, 1},
            {2012, 0, 1},
            {2012, 15, 1},
            {2012, 16, 1},

            {2012, 1, -1},
            {2012, 1, 0},
            {2012, 1, 29},

            {2012, 13, -1},
            {2012, 13, 0},
            {2012, 13, 8},
            {2012, 14, -1},
            {2012, 14, 0},
            {2012, 14, 29},
            {2012, 14, 30},

            {2011, 13, -1},
            {2011, 13, 0},
            {2011, 14, 29},
            {2011, 14, 30},
            {2011, 14, 1},
            {2011, 14, 2},

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

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates(final int year, final int month, final int dom) {
        PaxDate.of(year, month, dom);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_chronology_dateYearDay_badDate() {
        PaxChronology.INSTANCE.dateYearDay(2001, 365);
    }

    // -----------------------------------------------------------------------
    // isLeapYear()
    // -----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        Predicate<Integer> isLeapYear = year -> {
            int lastTwoDigits = year % 100;
            return (lastTwoDigits == 0 && year % 400 != 0) || lastTwoDigits == 99 || lastTwoDigits % 6 == 0;
        };
        for (int year = -500; year < 500; year++) {
            PaxDate base = PaxDate.of(year, 1, 1);
            assertEquals(base.isLeapYear(), isLeapYear.test(year));
            assertEquals(PaxChronology.INSTANCE.isLeapYear(year), isLeapYear.test(year));
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(PaxChronology.INSTANCE.isLeapYear(400), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(100), true);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(99), true);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(7), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(6), true);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(5), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(4), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(3), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(2), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(1), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(0), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(-1), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(-2), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(-3), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(-4), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(-5), false);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(-6), true);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(-99), true);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(-100), true);
        assertEquals(PaxChronology.INSTANCE.isLeapYear(-400), false);
    }

    @DataProvider(name = "lengthOfMonth")
    Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {2014, 1, 28},
            {2014, 2, 28},
            {2014, 3, 28},
            {2014, 4, 28},
            {2014, 5, 28},
            {2014, 6, 28},
            {2014, 7, 28},
            {2014, 8, 28},
            {2014, 9, 28},
            {2014, 10, 28},
            {2014, 11, 28},
            {2014, 12, 28},
            {2014, 13, 28},

            {2015, 13, 28},
            {2016, 13, 28},
            {2017, 13, 28},
            {2018, 13, 7},
            {2018, 14, 28},
            {2100, 13, 7},
            {2100, 14, 28},
            {2000, 13, 28},
        };
    }

    @Test(dataProvider = "lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(PaxDate.of(year, month, 1).lengthOfMonth(), length);
    }

    // -----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    // -----------------------------------------------------------------------
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
            assertEquals(eraBased, base);
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
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(PaxChronology.INSTANCE.prolepticYear(PaxEra.CE, 4), 4);
        assertEquals(PaxChronology.INSTANCE.prolepticYear(PaxEra.CE, 3), 3);
        assertEquals(PaxChronology.INSTANCE.prolepticYear(PaxEra.CE, 2), 2);
        assertEquals(PaxChronology.INSTANCE.prolepticYear(PaxEra.CE, 1), 1);
        assertEquals(PaxChronology.INSTANCE.prolepticYear(PaxEra.BCE, 1), 0);
        assertEquals(PaxChronology.INSTANCE.prolepticYear(PaxEra.BCE, 2), -1);
        assertEquals(PaxChronology.INSTANCE.prolepticYear(PaxEra.BCE, 3), -2);
        assertEquals(PaxChronology.INSTANCE.prolepticYear(PaxEra.BCE, 4), -3);
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void test_prolepticYear_badEra() {
        PaxChronology.INSTANCE.prolepticYear(IsoEra.CE, 4);
    }

    public void test_Chronology_eraOf() {
        assertEquals(PaxChronology.INSTANCE.eraOf(1), PaxEra.CE);
        assertEquals(PaxChronology.INSTANCE.eraOf(0), PaxEra.BCE);
    }

    @Test
    public void test_Chronology_eraOf_invalid() {
        PaxChronology.INSTANCE.eraOf(2);
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = PaxChronology.INSTANCE.eras();
        assertEquals(eras.size(), 2);
        assertEquals(eras.contains(PaxEra.BCE), true);
        assertEquals(eras.contains(PaxEra.CE), true);
    }

    // -----------------------------------------------------------------------
    // Chronology.range
    // -----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(PaxChronology.INSTANCE.range(DAY_OF_WEEK), ValueRange.of(1, 7));
        assertEquals(PaxChronology.INSTANCE.range(DAY_OF_MONTH), ValueRange.of(1, 7, 28));
        assertEquals(PaxChronology.INSTANCE.range(DAY_OF_YEAR), ValueRange.of(1, 364, 371));
        assertEquals(PaxChronology.INSTANCE.range(MONTH_OF_YEAR), ValueRange.of(1, 13, 14));
    }

    // -----------------------------------------------------------------------
    // PaxDate.range
    // -----------------------------------------------------------------------
    @DataProvider(name = "ranges")
    Object[][] data_ranges() {
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
            {2012, 1, 23, DAY_OF_YEAR, 1, 371},
            {2012, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},
            {2012, 12, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},
            {2012, 13, 3, ALIGNED_WEEK_OF_MONTH, 1, 1},
            {2012, 14, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},

            {2014, 13, 23, DAY_OF_MONTH, 1, 28},
            {2014, 13, 23, DAY_OF_YEAR, 1, 364},
            {2014, 13, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},

            {2014, 2, 23, IsoFields.QUARTER_OF_YEAR, 1, 4},
        };
    }

    @Test(dataProvider = "ranges")
    public void test_range(int year, int month, int dom, TemporalField field, int expectedMin, int expectedMax) {
        assertEquals(PaxDate.of(year, month, dom).range(field), ValueRange.of(expectedMin, expectedMax));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_unsupported() {
        PaxDate.of(2012, 6, 28).range(MINUTE_OF_DAY);
    }

    // -----------------------------------------------------------------------
    // PaxDate.getLong
    // -----------------------------------------------------------------------
    @DataProvider(name = "getLong")
    Object[][] data_getLong() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 4},
            {2014, 5, 26, DAY_OF_MONTH, 26},
            {2014, 5, 26, DAY_OF_YEAR, 28 + 28 + 28 + 28 + 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 4},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 4},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 20},
            {2014, 5, 26, MONTH_OF_YEAR, 5},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 13 + 340 - 3},
            {2014, 5, 26, YEAR, 2014},
            {2014, 5, 26, ERA, 1},
            {1, 6, 8, ERA, 1},
            {0, 6, 8, ERA, 0},

            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 4},
        };
    }

    @Test(dataProvider = "getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(PaxDate.of(year, month, dom).getLong(field), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_unsupported() {
        PaxDate.of(2012, 6, 28).getLong(MINUTE_OF_DAY);
    }

    // -----------------------------------------------------------------------
    // PaxDate.with
    // -----------------------------------------------------------------------
    @DataProvider(name = "with")
    Object[][] data_with() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 3, 2014, 5, 25},
            {2014, 5, 26, DAY_OF_MONTH, 28, 2014, 5, 28},
            {2014, 5, 26, DAY_OF_YEAR, 364, 2014, 13, 28},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 5, 25},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 1, 2014, 5, 4},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2014, 5, 24},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 23, 2014, 6, 19},
            {2014, 5, 26, MONTH_OF_YEAR, 7, 2014, 7, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, (2013 * 13 + 340 - 3) - 2, 2013, 3, 26},
            {2014, 5, 26, YEAR, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2012, 2012, 5, 26},
            {2014, 5, 26, ERA, 0, -2013, 5, 26},

            {2011, 12, 28, MONTH_OF_YEAR, 13, 2011, 13, 28},
            {2012, 12, 28, MONTH_OF_YEAR, 13, 2012, 13, 7},
            {2012, 13, 7, YEAR, 2011, 2011, 13, 7},
            {-2013, 6, 8, YEAR_OF_ERA, 2012, -2011, 6, 8},
            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 3, 2014, 5, 25},
        };
    }

    @Test(dataProvider = "with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(PaxDate.of(year, month, dom).with(field, value), PaxDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_with_TemporalField_unsupported() {
        PaxDate.of(2012, 6, 30).with(MINUTE_OF_DAY, 0);
    }

    // -----------------------------------------------------------------------
    // with(WithAdjuster)
    // -----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        PaxDate base = PaxDate.of(2013, 4, 5);
        PaxDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, PaxDate.of(2013, 4, 28));
    }

    @Test
    public void test_adjust2() {
        PaxDate base = PaxDate.of(2012, 13, 2);
        PaxDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, PaxDate.of(2012, 13, 7));
    }

    // -----------------------------------------------------------------------
    // PaxDate.with(Local*)
    // -----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        final ChronoLocalDate jdate = PaxChronology.INSTANCE.date(2200, 4, 3);
        final ChronoLocalDate test = jdate.with(LocalDate.of(2014, 6, 29));
        assertEquals(test, PaxChronology.INSTANCE.date(2014, 6, 16));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_adjust_toMonth() {
        final ChronoLocalDate jdate = PaxChronology.INSTANCE.date(2014, 2, 4);
        jdate.with(Month.APRIL);
    }

    // -----------------------------------------------------------------------
    // LocalDate.with(PaxDate)
    // -----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToPaxDate() {
        final ChronoLocalDate jdate = PaxChronology.INSTANCE.date(2014, 6, 16);
        final LocalDate test = LocalDate.MIN.with(jdate);
        assertEquals(test, LocalDate.of(2014, 6, 29));
    }

    @Test
    public void test_LocalDateTime_adjustToPaxDate() {
        final ChronoLocalDate jdate = PaxChronology.INSTANCE.date(2014, 6, 16);
        final LocalDateTime test = LocalDateTime.MIN.with(jdate);
        assertEquals(test, LocalDateTime.of(2014, 6, 29, 0, 0));
    }

    // -----------------------------------------------------------------------
    // PaxDate.plus
    // -----------------------------------------------------------------------
    @DataProvider(name = "plus")
    Object[][] data_plus() {
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

            {2012, 12, 26, 1, MONTHS, 2012, 13, 7},
            {2012, 13, 6, 3, MONTHS, 2013, 2, 6},
            {2014, 14, 26, -1, MONTHS, 2014, 13, 7},
            {2011, 13, 26, 1, YEARS, 2012, 14, 26},
            {2012, 13, 6, 3, YEARS, 2015, 13, 6},
            {2014, 13, 26, -2, YEARS, 2012, 14, 26},
            {2012, 14, 26, -6, YEARS, 2006, 14, 26},
            {2012, 13, 6, -6, YEARS, 2006, 13, 6},
        };
    }

    @Test(dataProvider = "plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(PaxDate.of(year, month, dom).plus(amount, unit), PaxDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(dataProvider = "plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(PaxDate.of(year, month, dom).minus(amount, unit), PaxDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_plus_TemporalUnit_unsupported() {
        PaxDate.of(2012, 6, 10).plus(0, MINUTES);
    }

    // -----------------------------------------------------------------------
    // PaxDate.until
    // -----------------------------------------------------------------------
    @DataProvider(name = "until")
    Object[][] data_until() {
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

            {2011, 13, 26, 2013, 13, 26, YEARS, 2},
            {2011, 13, 26, 2012, 14, 26, YEARS, 1},
            {2012, 14, 26, 2011, 13, 26, YEARS, -1},
            {2012, 14, 26, 2013, 13, 26, YEARS, 1},
            {2011, 13, 6, 2012, 13, 6, YEARS, 0},
            {2013, 13, 6, 2012, 13, 6, YEARS, 0},
            {2012, 13, 6, 2011, 13, 6, YEARS, -1},
            {2012, 13, 6, 2013, 13, 6, YEARS, 1},
        };
    }

    @Test(dataProvider = "until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        PaxDate start = PaxDate.of(year1, month1, dom1);
        PaxDate end = PaxDate.of(year2, month2, dom2);
        assertEquals(start.until(end, unit), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_until_TemporalUnit_unsupported() {
        PaxDate start = PaxDate.of(2012, 6, 30);
        PaxDate end = PaxDate.of(2012, 7, 1);
        start.until(end, MINUTES);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(PaxDate.of(2014, 5, 26).plus(PaxChronology.INSTANCE.period(0, 2, 2)), PaxDate.of(2014, 7, 28));
        assertEquals(PaxDate.of(2011, 13, 26).plus(PaxChronology.INSTANCE.period(1, 2, 2)), PaxDate.of(2013, 2, 28));
        assertEquals(PaxDate.of(2012, 13, 6).plus(PaxChronology.INSTANCE.period(1, 2, 2)), PaxDate.of(2014, 2, 8));
        assertEquals(PaxDate.of(2012, 12, 6).plus(PaxChronology.INSTANCE.period(0, 1, 2)), PaxDate.of(2012, 14, 1));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_Period_ISO() {
        assertEquals(PaxDate.of(2014, 5, 26).plus(Period.ofMonths(2)), PaxDate.of(2014, 7, 26));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(PaxDate.of(2014, 5, 26).minus(PaxChronology.INSTANCE.period(0, 2, 3)), PaxDate.of(2014, 3, 23));
        assertEquals(PaxDate.of(2012, 14, 26).minus(PaxChronology.INSTANCE.period(1, 2, 2)), PaxDate.of(2013, 11, 24));
        assertEquals(PaxDate.of(2012, 13, 6).minus(PaxChronology.INSTANCE.period(1, 2, 2)), PaxDate.of(2014, 11, 4));
        assertEquals(PaxDate.of(2012, 14, 1).minus(PaxChronology.INSTANCE.period(0, 1, 2)), PaxDate.of(2012, 12, 27));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_Period_ISO() {
        assertEquals(PaxDate.of(2014, 5, 26).minus(Period.ofMonths(2)), PaxDate.of(2014, 3, 26));
    }

    // -----------------------------------------------------------------------
    // equals()
    // -----------------------------------------------------------------------
    @Test
    void test_equals() {
        PaxDate a1 = PaxDate.of(2000, 1, 3);
        PaxDate a2 = PaxDate.of(2000, 1, 3);
        PaxDate b = PaxDate.of(2000, 1, 4);
        PaxDate c = PaxDate.of(2000, 2, 3);
        PaxDate d = PaxDate.of(2001, 1, 3);

        assertEquals(a1.equals(a1), true);
        assertEquals(a1.equals(a2), true);
        assertEquals(a1.equals(b), false);
        assertEquals(a1.equals(c), false);
        assertEquals(a1.equals(d), false);

        assertEquals(a1.equals(null), false);
        assertEquals(a1.equals(""), false);

        assertEquals(a1.hashCode(), a2.hashCode());
    }

    // -----------------------------------------------------------------------
    // toString()
    // -----------------------------------------------------------------------
    @DataProvider(name = "toString")
    Object[][] data_toString() {
        return new Object[][] { {PaxChronology.INSTANCE.date(-3, 5, 8), "Pax BCE 0002-05-08"},
            {PaxChronology.INSTANCE.date(-8, 1, 28), "Pax BCE 0007-01-28"},
            {PaxChronology.INSTANCE.date(2012, 13, 4), "Pax CE 2012-13-04"},
            {PaxChronology.INSTANCE.date(2014, 4, 27), "Pax CE 2014-04-27"},};
    }

    @Test(dataProvider = "toString")
    public void test_toString(final PaxDate pax, final String expected) {
        assertEquals(pax.toString(), expected);
    }

}
