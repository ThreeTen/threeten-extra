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
import static org.testng.Assert.assertEquals;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestDiscordianChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        Chronology chrono = Chronology.of("Discordian");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, DiscordianChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Discordian");
        Assert.assertEquals(chrono.getCalendarType(), "discordian");
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("discordian");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, DiscordianChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Discordian");
        Assert.assertEquals(chrono.getCalendarType(), "discordian");
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
        return new Object[][] {
            {DiscordianDate.of(1167, 1, 1), LocalDate.of(1, 1, 1)},
            {DiscordianDate.of(1167, 1, 2), LocalDate.of(1, 1, 2)},
            {DiscordianDate.of(1167, 1, 3), LocalDate.of(1, 1, 3)},

            {DiscordianDate.of(1167, 1, 57), LocalDate.of(1, 2, 26)},
            {DiscordianDate.of(1167, 1, 58), LocalDate.of(1, 2, 27)},
            {DiscordianDate.of(1167, 1, 59), LocalDate.of(1, 2, 28)},
            {DiscordianDate.of(1167, 1, 60), LocalDate.of(1, 3, 1)},

            {DiscordianDate.of(1170, 1, 57), LocalDate.of(4, 2, 26)},
            {DiscordianDate.of(1170, 1, 58), LocalDate.of(4, 2, 27)},
            {DiscordianDate.of(1170, 1, 59), LocalDate.of(4, 2, 28)},
            {DiscordianDate.of(1170, 0, 0), LocalDate.of(4, 2, 29)},
            {DiscordianDate.of(1170, 1, 60), LocalDate.of(4, 3, 1)},

            {DiscordianDate.of(1266, 1, 57), LocalDate.of(100, 2, 26)},
            {DiscordianDate.of(1266, 1, 58), LocalDate.of(100, 2, 27)},
            {DiscordianDate.of(1266, 1, 59), LocalDate.of(100, 2, 28)},
            {DiscordianDate.of(1266, 1, 60), LocalDate.of(100, 3, 1)},
            {DiscordianDate.of(1266, 1, 61), LocalDate.of(100, 3, 2)},

            {DiscordianDate.of(1166, 5, 73), LocalDate.of(0, 12, 31)},
            {DiscordianDate.of(1166, 5, 72), LocalDate.of(0, 12, 30)},

            {DiscordianDate.of(2748, 4, 68), LocalDate.of(1582, 10, 14)},
            {DiscordianDate.of(2748, 4, 69), LocalDate.of(1582, 10, 15)},
            {DiscordianDate.of(3111, 5, 24), LocalDate.of(1945, 11, 12)},

            {DiscordianDate.of(3178, 3, 40), LocalDate.of(2012, 7, 5)},
            {DiscordianDate.of(3178, 3, 41), LocalDate.of(2012, 7, 6)},
        };
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_from_DiscordianDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(LocalDate.from(discordian), iso);
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_from_LocalDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(DiscordianDate.from(iso), discordian);
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_chronology_dateEpochDay(DiscordianDate discordian, LocalDate iso) {
        assertEquals(DiscordianChronology.INSTANCE.dateEpochDay(iso.toEpochDay()), discordian);
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_toEpochDay(DiscordianDate discordian, LocalDate iso) {
        assertEquals(discordian.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_until_DiscordianDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(discordian.until(discordian), DiscordianChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_until_LocalDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(discordian.until(iso), DiscordianChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_until_DiscordianDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(iso.until(discordian), Period.ZERO);
    }

    @Test(dataProvider = "samples")
    public void test_Chronology_date_Temporal(DiscordianDate discordian, LocalDate iso) {
        assertEquals(DiscordianChronology.INSTANCE.date(iso), discordian);
    }

    @Test(dataProvider = "samples")
    public void test_plusDays(DiscordianDate discordian, LocalDate iso) {
        assertEquals(LocalDate.from(discordian.plus(0, DAYS)), iso);
        assertEquals(LocalDate.from(discordian.plus(1, DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(discordian.plus(35, DAYS)), iso.plusDays(35));
        assertEquals(LocalDate.from(discordian.plus(-1, DAYS)), iso.plusDays(-1));
        assertEquals(LocalDate.from(discordian.plus(-60, DAYS)), iso.plusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_minusDays(DiscordianDate discordian, LocalDate iso) {
        assertEquals(LocalDate.from(discordian.minus(0, DAYS)), iso);
        assertEquals(LocalDate.from(discordian.minus(1, DAYS)), iso.minusDays(1));
        assertEquals(LocalDate.from(discordian.minus(35, DAYS)), iso.minusDays(35));
        assertEquals(LocalDate.from(discordian.minus(-1, DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(discordian.minus(-60, DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_until_DAYS(DiscordianDate discordian, LocalDate iso) {
        assertEquals(discordian.until(iso.plusDays(0), DAYS), 0);
        assertEquals(discordian.until(iso.plusDays(1), DAYS), 1);
        assertEquals(discordian.until(iso.plusDays(35), DAYS), 35);
        assertEquals(discordian.until(iso.minusDays(40), DAYS), -40);
    }

    @DataProvider(name = "badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {1900, 0, 0},

            {1900, -1, 1},
            {1900, 0, 1},
            {1900, 6, 1},
            {1900, 7, 1},

            {1900, 1, -1},
            {1900, 1, 0},
            {1900, 1, 74},

            {1900, 0, 0},

            {1900, 5, -1},
            {1900, 5, 0},
            {1900, 5, 74},

            {1900, 2, 74},
            {1900, 3, 74},
            {1900, 4, 74},
        };
    }

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates(int year, int month, int dom) {
        DiscordianDate.of(year, month, dom);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_chronology_dateYearDay_badDate() {
        DiscordianChronology.INSTANCE.dateYearDay(2001, 366);
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        Predicate<Integer> isLeapYear = year -> {
            int offsetYear = year - 1166;
            return offsetYear % 4 == 0 && (offsetYear % 400 == 0 || offsetYear % 100 != 0);
        };
        for (int year = 1066; year < 1567; year++) {
            DiscordianDate base = DiscordianDate.of(year, 1, 1);
            assertEquals(base.isLeapYear(), isLeapYear.test(year), "Mismatch on " + year);
            assertEquals(DiscordianChronology.INSTANCE.isLeapYear(year), isLeapYear.test(year), "Mismatch on " + year);
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1174), true);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1173), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1172), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1171), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1170), true);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1169), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1168), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1167), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1166), true);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1165), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1164), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1163), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1162), true);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1161), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1160), false);
    }

    @DataProvider(name = "lengthOfMonth")
    Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {1900, 1, 73},
            {1900, 2, 73},
            {1900, 3, 73},
            {1900, 4, 73},
            {1900, 5, 73},

            {1901, 1, 73},
            {1902, 1, 73},
            {1903, 1, 73},
            {1904, 1, 73},
            {1966, 1, 73},
            {2066, 1, 73},
        };
    }

    @Test(dataProvider = "lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(DiscordianDate.of(year, month, 1).lengthOfMonth(), length);
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = 1; year < 200; year++) {
            DiscordianDate base = DiscordianChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            assertEquals(DiscordianEra.YOLD, base.getEra());
            assertEquals(year, base.get(YEAR_OF_ERA));
            DiscordianDate eraBased = DiscordianChronology.INSTANCE.date(DiscordianEra.YOLD, year, 1, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = 1; year < 200; year++) {
            DiscordianDate base = DiscordianChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            assertEquals(DiscordianEra.YOLD, base.getEra());
            assertEquals(year, base.get(YEAR_OF_ERA));
            DiscordianDate eraBased = DiscordianChronology.INSTANCE.dateYearDay(DiscordianEra.YOLD, year, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(DiscordianChronology.INSTANCE.prolepticYear(DiscordianEra.YOLD, 4), 4);
        assertEquals(DiscordianChronology.INSTANCE.prolepticYear(DiscordianEra.YOLD, 3), 3);
        assertEquals(DiscordianChronology.INSTANCE.prolepticYear(DiscordianEra.YOLD, 2), 2);
        assertEquals(DiscordianChronology.INSTANCE.prolepticYear(DiscordianEra.YOLD, 1), 1);
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void test_prolepticYear_badEra() {
        DiscordianChronology.INSTANCE.prolepticYear(IsoEra.CE, 4);
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(DiscordianChronology.INSTANCE.eraOf(1), DiscordianEra.YOLD);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_Chronology_eraOf_invalid() {
        DiscordianChronology.INSTANCE.eraOf(2);
        DiscordianChronology.INSTANCE.eraOf(0);
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = DiscordianChronology.INSTANCE.eras();
        assertEquals(eras.size(), 1);
        assertEquals(eras.contains(DiscordianEra.YOLD), true);
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(DiscordianChronology.INSTANCE.range(ALIGNED_DAY_OF_WEEK_IN_MONTH), ValueRange.of(0, 1, 5, 5));
        assertEquals(DiscordianChronology.INSTANCE.range(ALIGNED_DAY_OF_WEEK_IN_YEAR), ValueRange.of(0, 1, 5, 5));
        assertEquals(DiscordianChronology.INSTANCE.range(ALIGNED_WEEK_OF_MONTH), ValueRange.of(0, 1, 15, 15));
        assertEquals(DiscordianChronology.INSTANCE.range(ALIGNED_WEEK_OF_YEAR), ValueRange.of(0, 1, 73, 73));
        assertEquals(DiscordianChronology.INSTANCE.range(DAY_OF_WEEK), ValueRange.of(0, 1, 5, 5));
        assertEquals(DiscordianChronology.INSTANCE.range(DAY_OF_MONTH), ValueRange.of(0, 1, 73, 73));
        assertEquals(DiscordianChronology.INSTANCE.range(DAY_OF_YEAR), ValueRange.of(1, 365, 366));
        assertEquals(DiscordianChronology.INSTANCE.range(EPOCH_DAY), ValueRange.of(-1_145_400, 999_999 * 365L + 242_499));
        assertEquals(DiscordianChronology.INSTANCE.range(ERA), ValueRange.of(1, 1));
        assertEquals(DiscordianChronology.INSTANCE.range(MONTH_OF_YEAR), ValueRange.of(0, 1, 5, 5));
        assertEquals(DiscordianChronology.INSTANCE.range(PROLEPTIC_MONTH), ValueRange.of(0, 999_999 * 5L + 5 - 1));
        assertEquals(DiscordianChronology.INSTANCE.range(YEAR), ValueRange.of(1, 999_999));
        assertEquals(DiscordianChronology.INSTANCE.range(YEAR_OF_ERA), ValueRange.of(1, 999_999));
    }

    //-----------------------------------------------------------------------
    // DiscordianDate.range
    //-----------------------------------------------------------------------
    @DataProvider(name = "ranges")
    Object[][] data_ranges() {
        return new Object[][] {
            {2010, 0, 0, DAY_OF_MONTH, 0, 0},
            {2010, 1, 23, DAY_OF_MONTH, 1, 73},
            {2010, 2, 23, DAY_OF_MONTH, 1, 73},
            {2010, 3, 23, DAY_OF_MONTH, 1, 73},
            {2010, 4, 23, DAY_OF_MONTH, 1, 73},
            {2010, 5, 23, DAY_OF_MONTH, 1, 73},
            {2010, 1, 23, DAY_OF_YEAR, 1, 366},
            {2010, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 15},
            {2010, 1, 1, MONTH_OF_YEAR, 0, 5},
            {2010, 0, 0, ALIGNED_DAY_OF_WEEK_IN_MONTH, 0, 0},
            {2010, 0, 0, ALIGNED_DAY_OF_WEEK_IN_YEAR, 0, 0},
            {2010, 0, 0, ALIGNED_WEEK_OF_MONTH, 0, 0},
            {2010, 0, 0, ALIGNED_WEEK_OF_YEAR, 0, 0},
            {2010, 0, 0, DAY_OF_WEEK, 0, 0},

            {2011, 2, 23, DAY_OF_YEAR, 1, 365},
            {2011, 1, 23, MONTH_OF_YEAR, 1, 5},

            {2011, 2, 23, IsoFields.QUARTER_OF_YEAR, 1, 4},
        };
    }

    @Test(dataProvider = "ranges")
    public void test_range(int year, int month, int dom, TemporalField field, int expectedMin, int expectedMax) {
        assertEquals(DiscordianDate.of(year, month, dom).range(field), ValueRange.of(expectedMin, expectedMax));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_unsupported() {
        DiscordianDate.of(2012, 5, 30).range(MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // DiscordianDate.getLong
    //-----------------------------------------------------------------------
    @DataProvider(name = "getLong")
    Object[][] data_getLong() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 3},
            {2014, 5, 26, DAY_OF_MONTH, 26},
            {2014, 5, 26, DAY_OF_YEAR, 1 + 73 + 73 + 73 + 73 + 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 6},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 64},
            {2014, 5, 26, MONTH_OF_YEAR, 5},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 5 + 5 - 1},
            {2014, 5, 26, YEAR, 2014},
            {2014, 5, 26, ERA, 1},
            {1, 5, 8, ERA, 1},

            {2014, 0, 0, DAY_OF_WEEK, 0},
            {2014, 0, 0, DAY_OF_MONTH, 0},
            {2014, 0, 0, DAY_OF_YEAR, 60},
            {2014, 0, 0, ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {2014, 0, 0, ALIGNED_WEEK_OF_MONTH, 0},
            {2014, 0, 0, ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {2014, 0, 0, ALIGNED_WEEK_OF_YEAR, 0},
            {2014, 0, 0, MONTH_OF_YEAR, 0},
            {2014, 0, 0, PROLEPTIC_MONTH, 2014 * 5 + 1 - 1},
        };
    }

    @Test(dataProvider = "getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(DiscordianDate.of(year, month, dom).getLong(field), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_unsupported() {
        DiscordianDate.of(2012, 1, 30).getLong(MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // DiscordianDate.with
    //-----------------------------------------------------------------------
    @DataProvider(name = "with")
    Object[][] data_with() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 1, 2014, 5, 24},
            {2014, 5, 26, DAY_OF_WEEK, 3, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_MONTH, 31, 2014, 5, 31},
            {2014, 5, 26, DAY_OF_MONTH, 26, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_YEAR, 365, 2014, 5, 72},
            {2014, 5, 26, DAY_OF_YEAR, 319, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 5, 28},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 1, 2014, 5, 1},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 6, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2014, 5, 25},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 23, 2014, 2, 40},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 64, 2014, 5, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 4, 2014, 4, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 5, 2014, 5, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2013 * 5 + 3 - 1, 2013, 3, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 5 + 5 - 1, 2014, 5, 26},
            {2014, 5, 26, YEAR, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR, 2014, 2014, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2014, 2014, 5, 26},
            {2014, 5, 26, ERA, 1, 2014, 5, 26},

            {2014, 0, 0, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 0, 0},
            {2014, 0, 0, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, 2014, 0, 0},
            {2014, 0, 0, ALIGNED_WEEK_OF_MONTH, 3, 2014, 0, 0},
            {2014, 0, 0, ALIGNED_WEEK_OF_YEAR, 3, 2014, 1, 15},
            {2014, 0, 0, DAY_OF_WEEK, 3, 2014, 0, 0},
            {2014, 0, 0, DAY_OF_MONTH, 3, 2014, 0, 0},
            {2014, 3, 31, DAY_OF_MONTH, 0, 2014, 3, 60},
            {2014, 1, 31, DAY_OF_MONTH, 0, 2014, 1, 60},
            {2013, 1, 31, DAY_OF_MONTH, 0, 2013, 1, 60},
            {2014, 0, 0, MONTH_OF_YEAR, 1, 2014, 1, 60},
            {2014, 3, 31, MONTH_OF_YEAR, 0, 2014, 0, 0},
            {2013, 3, 31, MONTH_OF_YEAR, 0, 2013, 1, 60},
            {2014, 3, 31, DAY_OF_YEAR, 60, 2014, 0, 0},
            {2013, 3, 31, DAY_OF_YEAR, 60, 2013, 1, 60},
            {2014, 0, 0, YEAR, 2013, 2013, 1, 60},
            {2013, 1, 60, YEAR, 2014, 2014, 1, 60},
        };
    }

    @Test(dataProvider = "with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(DiscordianDate.of(year, month, dom).with(field, value), DiscordianDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_with_TemporalField_unsupported() {
        DiscordianDate.of(2012, 5, 30).with(MINUTE_OF_DAY, 0);
    }

    //-----------------------------------------------------------------------
    // DiscordianDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        DiscordianDate base = DiscordianDate.of(2014, 0, 0);
        DiscordianDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, DiscordianDate.of(2014, 0, 0));
    }

    @Test
    public void test_adjust2() {
        DiscordianDate base = DiscordianDate.of(2012, 2, 23);
        DiscordianDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, DiscordianDate.of(2012, 2, 73));
    }

    //-----------------------------------------------------------------------
    // DiscordianDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        DiscordianDate discordian = DiscordianDate.of(2000, 1, 4);
        DiscordianDate test = discordian.with(LocalDate.of(2012, 7, 6));
        assertEquals(test, DiscordianDate.of(3178, 3, 41));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_adjust_toMonth() {
        DiscordianDate discordian = DiscordianDate.of(2000, 1, 4);
        discordian.with(Month.APRIL);
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(DiscordianDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToDiscordianDate() {
        DiscordianDate discordian = DiscordianDate.of(3178, 3, 41);
        LocalDate test = LocalDate.MIN.with(discordian);
        assertEquals(test, LocalDate.of(2012, 7, 6));
    }

    @Test
    public void test_LocalDateTime_adjustToDiscordianDate() {
        DiscordianDate discordian = DiscordianDate.of(3178, 3, 41);
        LocalDateTime test = LocalDateTime.MIN.with(discordian);
        assertEquals(test, LocalDateTime.of(2012, 7, 6, 0, 0));
    }

    //-----------------------------------------------------------------------
    // DiscordianDate.plus
    //-----------------------------------------------------------------------
    @DataProvider(name = "plus")
    Object[][] data_plus() {
        return new Object[][] {
            {2014, 5, 26, 0, DAYS, 2014, 5, 26},
            {2014, 5, 26, 8, DAYS, 2014, 5, 34},
            {2014, 5, 26, -3, DAYS, 2014, 5, 23},
            {2014, 5, 26, 0, WEEKS, 2014, 5, 26},
            {2014, 5, 26, 3, WEEKS, 2014, 5, 41},
            {2014, 5, 26, -5, WEEKS, 2014, 5, 1},
            {2014, 5, 26, 0, MONTHS, 2014, 5, 26},
            {2014, 5, 26, 3, MONTHS, 2015, 3, 26},
            {2014, 5, 26, -5, MONTHS, 2013, 5, 26},
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
        };
    }

    @DataProvider(name = "plus_leap")
    Object[][] data_plus_leap() {
        return new Object[][] {
            {2014, 0, 0, 0, DAYS, 2014, 0, 0},
            {2014, 0, 0, 8, DAYS, 2014, 1, 67},
            {2014, 0, 0, -3, DAYS, 2014, 1, 57},
            {2014, 0, 0, 0, WEEKS, 2014, 0, 0},
            {2014, 0, 0, 3, WEEKS, 2014, 2, 2},
            {2014, 0, 0, -5, WEEKS, 2014, 1, 35},
            {2014, 0, 0, 73 * 4, WEEKS, 2018, 0, 0},
            {2014, 0, 0, 0, MONTHS, 2014, 0, 0},
            {2014, 0, 0, 3, MONTHS, 2014, 4, 60},
            {2014, 0, 0, -5, MONTHS, 2013, 1, 60},
            {2014, 0, 0, 20, MONTHS, 2018, 0, 0},
            {2014, 0, 0, 0, YEARS, 2014, 0, 0},
            {2014, 0, 0, 3, YEARS, 2017, 1, 60},
            {2014, 0, 0, -5, YEARS, 2009, 1, 60},
            {2014, 0, 0, 4, YEARS, 2018, 0, 0},
        };
    }

    @DataProvider(name = "minus_leap")
    Object[][] data_minus_leap() {
        return new Object[][] {
            {2014, 0, 0, 0, DAYS, 2014, 0, 0},
            {2014, 1, 52, 8, DAYS, 2014, 0, 0},
            {2014, 1, 62, -3, DAYS, 2014, 0, 0},
            {2014, 0, 0, 0, WEEKS, 2014, 0, 0},
            {2014, 1, 45, 3, WEEKS, 2014, 0, 0},
            {2014, 2, 12, -5, WEEKS, 2014, 0, 0},
            {2010, 0, 0, 73 * 4, WEEKS, 2014, 0, 0},
            {2014, 0, 0, 0, MONTHS, 2014, 0, 0},
            {2013, 3, 60, 3, MONTHS, 2014, 0, 0},
            {2015, 1, 60, -5, MONTHS, 2014, 0, 0},
            {2010, 0, 0, 20, MONTHS, 2014, 0, 0},
            {2014, 0, 0, 0, YEARS, 2014, 0, 0},
            {2011, 1, 60, 3, YEARS, 2014, 0, 0},
            {2019, 1, 60, -5, YEARS, 2014, 0, 0},
            {2010, 0, 0, 4, YEARS, 2014, 0, 0},
        };
    }

    @Test(dataProvider = "plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(DiscordianDate.of(year, month, dom).plus(amount, unit), DiscordianDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(dataProvider = "plus_leap")
    public void test_plus_leap_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(DiscordianDate.of(year, month, dom).plus(amount, unit), DiscordianDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(dataProvider = "plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(DiscordianDate.of(year, month, dom).minus(amount, unit), DiscordianDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(dataProvider = "minus_leap")
    public void test_minus_leap_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(DiscordianDate.of(year, month, dom).minus(amount, unit), DiscordianDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_plus_TemporalUnit_unsupported() {
        DiscordianDate.of(2012, 5, 30).plus(0, MINUTES);
    }

    //-----------------------------------------------------------------------
    // DiscordianDate.until
    //-----------------------------------------------------------------------
    @DataProvider(name = "until")
    Object[][] data_until() {
        return new Object[][] {
            {2014, 5, 26, 2014, 5, 26, DAYS, 0},
            {2014, 5, 26, 2014, 5, 32, DAYS, 6},
            {2014, 5, 26, 2014, 5, 20, DAYS, -6},
            {2014, 5, 26, 2014, 5, 26, WEEKS, 0},
            {2014, 5, 26, 2014, 5, 30, WEEKS, 0},
            {2014, 5, 26, 2014, 5, 31, WEEKS, 1},
            {2014, 5, 26, 2014, 5, 26, MONTHS, 0},
            {2014, 5, 26, 2015, 1, 25, MONTHS, 0},
            {2014, 5, 26, 2015, 1, 26, MONTHS, 1},
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
            {2013, 5, 26, 3014, 5, 26, ERAS, 0},

            {2014, 1, 59, 2014, 1, 60, DAYS, 2},
            {2014, 1, 59, 2014, 0, 0, DAYS, 1},
            {2014, 0, 0, 2014, 1, 60, DAYS, 1},
            {2014, 1, 60, 2014, 1, 55, DAYS, -6},
            {2014, 0, 0, 2014, 0, 0, WEEKS, 0},
            {2014, 1, 60, 2014, 1, 60, WEEKS, 0},
            {2014, 1, 60, 2014, 1, 59, WEEKS, 0},
            {2014, 1, 60, 2014, 1, 56, WEEKS, 0},
            {2014, 1, 60, 2014, 1, 55, WEEKS, -1},
            {2014, 0, 0, 2014, 1, 54, WEEKS, -1},
            {2014, 0, 0, 2014, 1, 55, WEEKS, 0},
            {2014, 0, 0, 2014, 1, 64, WEEKS, 0},
            {2014, 0, 0, 2014, 1, 65, WEEKS, 1},
            {2014, 1, 54, 2014, 0, 0, WEEKS, 1},
            {2014, 1, 55, 2014, 0, 0, WEEKS, 0},
            {2014, 1, 64, 2014, 0, 0, WEEKS, 0},
            {2014, 1, 65, 2014, 0, 0, WEEKS, -1},
            {2014, 0, 0, 2014, 0, 0, MONTHS, 0},
            {2014, 0, 0, 2014, 2, 59, MONTHS, 0},
            {2014, 0, 0, 2014, 2, 60, MONTHS, 1},
            {2014, 2, 60, 2014, 0, 0, MONTHS, -1},
            {2014, 2, 59, 2014, 0, 0, MONTHS, 0},
            {2013, 5, 59, 2014, 0, 0, MONTHS, 1},
            {2013, 5, 60, 2014, 0, 0, MONTHS, 0},
            {2013, 5, 60, 2014, 1, 60, MONTHS, 1},
            {2014, 0, 0, 2014, 0, 0, YEARS, 0},
            {2014, 0, 0, 2015, 1, 59, YEARS, 0},
            {2014, 0, 0, 2015, 1, 60, YEARS, 1},
            {2013, 1, 60, 2014, 0, 0, YEARS, 0},
            {2013, 1, 59, 2014, 0, 0, YEARS, 1},
            {2013, 1, 60, 2014, 1, 60, YEARS, 1},
            {2014, 0, 0, 2013, 1, 59, YEARS, -1},
            {2014, 0, 0, 2013, 1, 60, YEARS, 0},
            {2015, 1, 60, 2014, 0, 0, YEARS, -1},
            {2015, 1, 59, 2014, 0, 0, YEARS, 0},
            {2018, 0, 0, 2014, 0, 0, YEARS, -4},
            {2014, 0, 0, 2018, 0, 0, YEARS, 4},
        };
    }

    @Test(dataProvider = "until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        DiscordianDate start = DiscordianDate.of(year1, month1, dom1);
        DiscordianDate end = DiscordianDate.of(year2, month2, dom2);
        assertEquals(start.until(end, unit), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_until_TemporalUnit_unsupported() {
        DiscordianDate start = DiscordianDate.of(2012, 1, 30);
        DiscordianDate end = DiscordianDate.of(2012, 2, 1);
        start.until(end, MINUTES);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(DiscordianDate.of(2014, 5, 26).plus(DiscordianChronology.INSTANCE.period(0, 2, 3)), DiscordianDate.of(2015, 2, 29));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_Period_ISO() {
        assertEquals(DiscordianDate.of(2014, 5, 26).plus(Period.ofMonths(2)), DiscordianDate.of(2015, 2, 26));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(DiscordianDate.of(2014, 5, 26).minus(DiscordianChronology.INSTANCE.period(0, 2, 3)), DiscordianDate.of(2014, 3, 23));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_Period_ISO() {
        assertEquals(DiscordianDate.of(2014, 5, 26).minus(Period.ofMonths(2)), DiscordianDate.of(2014, 3, 26));
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    void test_equals() {
        DiscordianDate a1 = DiscordianDate.of(2000, 1, 3);
        DiscordianDate a2 = DiscordianDate.of(2000, 1, 3);
        DiscordianDate b = DiscordianDate.of(2000, 1, 4);
        DiscordianDate c = DiscordianDate.of(2000, 2, 3);
        DiscordianDate d = DiscordianDate.of(2001, 1, 3);

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
            {DiscordianDate.of(1, 1, 1), "Discordian YOLD 1-1-01"},
            {DiscordianDate.of(2012, 5, 23), "Discordian YOLD 2012-5-23"},
            {DiscordianDate.of(2014, 0, 0), "Discordian YOLD 2014 St. Tib's Day"},
        };
    }

    @Test(dataProvider = "toString")
    public void test_toString(DiscordianDate discordian, String expected) {
        assertEquals(discordian.toString(), expected);
    }

}
