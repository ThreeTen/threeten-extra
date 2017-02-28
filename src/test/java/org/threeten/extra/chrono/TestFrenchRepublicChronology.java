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
import java.time.chrono.IsoEra;
import java.time.temporal.IsoFields;
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
public class TestFrenchRepublicChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        Chronology chrono = Chronology.of("French Republican");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, FrenchRepublicChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "French Republican");
        Assert.assertEquals(chrono.getCalendarType(), "frenchrepublican");
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("frenchrepublican");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, FrenchRepublicChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "French Republican");
        Assert.assertEquals(chrono.getCalendarType(), "frenchrepublican");
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
        return new Object[][] {
            {FrenchRepublicDate.of(1, 1, 1), LocalDate.of(1792, 9, 22)},
            {FrenchRepublicDate.of(1, 1, 2), LocalDate.of(1792, 9, 23)},
            {FrenchRepublicDate.of(1, 1, 3), LocalDate.of(1792, 9, 24)},
            {FrenchRepublicDate.of(1, 1, 30), LocalDate.of(1792, 10, 21)},

            {FrenchRepublicDate.of(1, 4, 11), LocalDate.of(1792, 12, 31)},
            {FrenchRepublicDate.of(1, 4, 12), LocalDate.of(1793, 1, 1)},
            {FrenchRepublicDate.of(1, 6, 10), LocalDate.of(1793, 2, 28)},
            {FrenchRepublicDate.of(1, 6, 11), LocalDate.of(1793, 3, 1)},

            {FrenchRepublicDate.of(1, 12, 30), LocalDate.of(1793, 9, 16)},
            {FrenchRepublicDate.of(1, 13, 1), LocalDate.of(1793, 9, 17)},
            {FrenchRepublicDate.of(1, 13, 2), LocalDate.of(1793, 9, 18)},
            {FrenchRepublicDate.of(1, 13, 3), LocalDate.of(1793, 9, 19)},
            {FrenchRepublicDate.of(1, 13, 4), LocalDate.of(1793, 9, 20)},
            {FrenchRepublicDate.of(1, 13, 5), LocalDate.of(1793, 9, 21)},

            {FrenchRepublicDate.of(2, 1, 1), LocalDate.of(1793, 9, 22)},
            {FrenchRepublicDate.of(2, 1, 2), LocalDate.of(1793, 9, 23)},
            {FrenchRepublicDate.of(2, 1, 3), LocalDate.of(1793, 9, 24)},
            {FrenchRepublicDate.of(2, 1, 30), LocalDate.of(1793, 10, 21)},
            {FrenchRepublicDate.of(2, 4, 11), LocalDate.of(1793, 12, 31)},
            {FrenchRepublicDate.of(2, 4, 12), LocalDate.of(1794, 1, 1)},
            {FrenchRepublicDate.of(2, 6, 10), LocalDate.of(1794, 2, 28)},
            {FrenchRepublicDate.of(2, 6, 11), LocalDate.of(1794, 3, 1)},
            {FrenchRepublicDate.of(2, 12, 30), LocalDate.of(1794, 9, 16)},
            {FrenchRepublicDate.of(2, 13, 1), LocalDate.of(1794, 9, 17)},
            {FrenchRepublicDate.of(2, 13, 2), LocalDate.of(1794, 9, 18)},
            {FrenchRepublicDate.of(2, 13, 3), LocalDate.of(1794, 9, 19)},
            {FrenchRepublicDate.of(2, 13, 4), LocalDate.of(1794, 9, 20)},
            {FrenchRepublicDate.of(2, 13, 5), LocalDate.of(1794, 9, 21)},

            {FrenchRepublicDate.of(3, 1, 1), LocalDate.of(1794, 9, 22)},
            {FrenchRepublicDate.of(3, 1, 2), LocalDate.of(1794, 9, 23)},
            {FrenchRepublicDate.of(3, 1, 3), LocalDate.of(1794, 9, 24)},
            {FrenchRepublicDate.of(3, 1, 30), LocalDate.of(1794, 10, 21)},
            {FrenchRepublicDate.of(3, 4, 11), LocalDate.of(1794, 12, 31)},
            {FrenchRepublicDate.of(3, 4, 12), LocalDate.of(1795, 1, 1)},
            {FrenchRepublicDate.of(3, 6, 10), LocalDate.of(1795, 2, 28)},
            {FrenchRepublicDate.of(3, 6, 11), LocalDate.of(1795, 3, 1)},
            {FrenchRepublicDate.of(3, 12, 30), LocalDate.of(1795, 9, 16)},
            {FrenchRepublicDate.of(3, 13, 1), LocalDate.of(1795, 9, 17)},
            {FrenchRepublicDate.of(3, 13, 2), LocalDate.of(1795, 9, 18)},
            {FrenchRepublicDate.of(3, 13, 3), LocalDate.of(1795, 9, 19)},
            {FrenchRepublicDate.of(3, 13, 4), LocalDate.of(1795, 9, 20)},
            {FrenchRepublicDate.of(3, 13, 5), LocalDate.of(1795, 9, 21)},
            {FrenchRepublicDate.of(3, 13, 6), LocalDate.of(1795, 9, 22)},

            {FrenchRepublicDate.of(4, 1, 1), LocalDate.of(1795, 9, 23)},
            {FrenchRepublicDate.of(4, 1, 2), LocalDate.of(1795, 9, 24)},
            {FrenchRepublicDate.of(4, 1, 3), LocalDate.of(1795, 9, 25)},
            {FrenchRepublicDate.of(4, 1, 30), LocalDate.of(1795, 10, 22)},
            {FrenchRepublicDate.of(4, 4, 10), LocalDate.of(1795, 12, 31)},
            {FrenchRepublicDate.of(4, 4, 11), LocalDate.of(1796, 1, 1)},
            {FrenchRepublicDate.of(4, 6, 9), LocalDate.of(1796, 2, 28)},
            {FrenchRepublicDate.of(4, 6, 10), LocalDate.of(1796, 2, 29)},
            {FrenchRepublicDate.of(4, 6, 11), LocalDate.of(1796, 3, 1)},
            {FrenchRepublicDate.of(4, 12, 30), LocalDate.of(1796, 9, 16)},
            {FrenchRepublicDate.of(4, 13, 1), LocalDate.of(1796, 9, 17)},
            {FrenchRepublicDate.of(4, 13, 2), LocalDate.of(1796, 9, 18)},
            {FrenchRepublicDate.of(4, 13, 3), LocalDate.of(1796, 9, 19)},
            {FrenchRepublicDate.of(4, 13, 4), LocalDate.of(1796, 9, 20)},
            {FrenchRepublicDate.of(4, 13, 5), LocalDate.of(1796, 9, 21)},

            {FrenchRepublicDate.of(14, 4, 10), LocalDate.of(1805, 12, 31)},
            {FrenchRepublicDate.of(14, 4, 11), LocalDate.of(1806, 1, 1)},

            {FrenchRepublicDate.of(15, 1, 1), LocalDate.of(1806, 9, 23)},
            {FrenchRepublicDate.of(16, 1, 1), LocalDate.of(1807, 9, 24)},
            {FrenchRepublicDate.of(17, 1, 1), LocalDate.of(1808, 9, 23)},
            {FrenchRepublicDate.of(18, 1, 1), LocalDate.of(1809, 9, 23)},
            {FrenchRepublicDate.of(19, 1, 1), LocalDate.of(1810, 9, 23)},
            {FrenchRepublicDate.of(20, 1, 1), LocalDate.of(1811, 9, 24)},

            {FrenchRepublicDate.of(79, 8, 16), LocalDate.of(1871, 5, 6)},
            {FrenchRepublicDate.of(79, 9, 3), LocalDate.of(1871, 5, 23)},

            {FrenchRepublicDate.of(224, 1, 1), LocalDate.of(2015, 9, 25)},
            {FrenchRepublicDate.of(225, 1, 1), LocalDate.of(2016, 9, 24)},
            {FrenchRepublicDate.of(226, 1, 1), LocalDate.of(2017, 9, 24)},
            {FrenchRepublicDate.of(227, 1, 1), LocalDate.of(2018, 9, 24)},
        };
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_from_FrenchRepublicDate(FrenchRepublicDate french, LocalDate iso) {
        assertEquals(LocalDate.from(french), iso);
    }

    @Test(dataProvider = "samples")
    public void test_FrenchRepublicDate_from_LocalDate(FrenchRepublicDate french, LocalDate iso) {
        assertEquals(FrenchRepublicDate.from(iso), french);
    }



    @Test(dataProvider = "samples")
    public void test_FrenchRepublicDate_chronology_dateEpochDay(FrenchRepublicDate frenchRepublic, LocalDate iso) {
        assertEquals(FrenchRepublicChronology.INSTANCE.dateEpochDay(iso.toEpochDay()), frenchRepublic);
    }

    @Test(dataProvider = "samples")
    public void test_FrenchRepublicDate_toEpochDay(FrenchRepublicDate frenchRepublic, LocalDate iso) {
        assertEquals(frenchRepublic.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider = "samples")
    public void test_FrenchRepublicDate_until_FrenchRepublicDate(FrenchRepublicDate frenchRepublic, LocalDate iso) {
        assertEquals(frenchRepublic.until(frenchRepublic), FrenchRepublicChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_FrenchRepublicDate_until_LocalDate(FrenchRepublicDate frenchRepublic, LocalDate iso) {
        assertEquals(frenchRepublic.until(iso), FrenchRepublicChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_until_FrenchRepublicDate(FrenchRepublicDate frenchRepublic, LocalDate iso) {
        assertEquals(iso.until(frenchRepublic), Period.ZERO);
    }

    @Test(dataProvider = "samples")
    public void test_Chronology_date_Temporal(FrenchRepublicDate frenchRepublic, LocalDate iso) {
        assertEquals(FrenchRepublicChronology.INSTANCE.date(iso), frenchRepublic);
    }

    @Test(dataProvider = "samples")
    public void test_plusDays(FrenchRepublicDate frenchRepublic, LocalDate iso) {
        assertEquals(LocalDate.from(frenchRepublic.plus(0, DAYS)), iso);
        assertEquals(LocalDate.from(frenchRepublic.plus(1, DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(frenchRepublic.plus(35, DAYS)), iso.plusDays(35));
        assertEquals(LocalDate.from(frenchRepublic.plus(-1, DAYS)), iso.plusDays(-1));
        assertEquals(LocalDate.from(frenchRepublic.plus(-60, DAYS)), iso.plusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_minusDays(FrenchRepublicDate frenchRepublic, LocalDate iso) {
        assertEquals(LocalDate.from(frenchRepublic.minus(0, DAYS)), iso);
        assertEquals(LocalDate.from(frenchRepublic.minus(1, DAYS)), iso.minusDays(1));
        assertEquals(LocalDate.from(frenchRepublic.minus(35, DAYS)), iso.minusDays(35));
        assertEquals(LocalDate.from(frenchRepublic.minus(-1, DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(frenchRepublic.minus(-60, DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_until_DAYS(FrenchRepublicDate frenchRepublic, LocalDate iso) {
        assertEquals(frenchRepublic.until(iso.plusDays(0), DAYS), 0);
        assertEquals(frenchRepublic.until(iso.plusDays(1), DAYS), 1);
        assertEquals(frenchRepublic.until(iso.plusDays(35), DAYS), 35);
        assertEquals(frenchRepublic.until(iso.minusDays(40), DAYS), -40);
    }

    @DataProvider(name = "badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {1, 0, 0},

            {1, -1, 1},
            {1, 0, 1},
            {1, 14, 1},
            {1, 15, 1},

            {1, 1, -1},
            {1, 1, 0},
            {1, 1, 31},

            {1, 2, -1},
            {1, 2, 0},
            {1, 2, 31},
            {1, 2, 32},

            {1, 13, -1},
            {1, 13, 0},
            {1, 13, 6},
            {1, 13, 7},
            {1, 13, 8},
            {1, 13, 9},
            {1, 13, 10},

            {3, 13, -1},
            {3, 13, 0},
            {3, 13, 7},
            {3, 13, 8},
            {3, 13, 9},
            {3, 13, 10},

            {1, 12, -1},
            {1, 12, 0},
            {1, 12, 31},
            {1, 12, 32},

            {1, 3, 31},
            {1, 4, 31},
            {1, 5, 31},
            {1, 6, 31},
            {1, 7, 31},
            {1, 8, 31},
            {1, 9, 31},
            {1, 10, 31},
            {1, 11, 31},
            {1, 12, 31},
            {1, 13, 31},
        };
    }

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates(int year, int month, int dom) {
        FrenchRepublicDate.of(year, month, dom);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_chronology_dateYearDay_badDate() {
        FrenchRepublicChronology.INSTANCE.dateYearDay(1, 366);
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        for (int year = -200; year < 200; year++) {
            FrenchRepublicDate base = FrenchRepublicDate.of(year, 1, 1);
            assertEquals(base.isLeapYear(), ((year+1) % 4) == 0);
            assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(year), ((year+1) % 4) == 0);
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(1), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(2), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(3), true);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(4), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(5), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(6), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(7), true);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(8), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(9), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(10), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(11), true);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(12), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(13), false);
        assertEquals(FrenchRepublicChronology.INSTANCE.isLeapYear(14), false);
    }

    @DataProvider(name = "lengthOfMonth")
    Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {1, 1, 30},
            {1, 2, 30},
            {1, 3, 30},
            {1, 4, 30},
            {1, 5, 30},
            {1, 6, 30},
            {1, 7, 30},
            {1, 8, 30},
            {1, 9, 30},
            {1, 10, 30},
            {1, 11, 30},
            {1, 12, 30},
            {1, 13, 5},

            {1, 13, 5},
            {2, 13, 5},
            {3, 13, 6},
            {4, 13, 5},
            {5, 13, 5},
            {6, 13, 5},
            {7, 13, 6},
            {8, 13, 5},
            {9, 13, 5},
            {10, 13, 5},
            {11, 13, 6},
            {12, 13, 5},
            {13, 13, 5},
            {14, 13, 5},
        };
    }

    @Test(dataProvider = "lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(FrenchRepublicDate.of(year, month, 1).lengthOfMonth(), length);
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = -200; year < 200; year++) {
            FrenchRepublicDate base = FrenchRepublicChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            FrenchRepublicEra era = (year <= 0 ? FrenchRepublicEra.BEFORE_REPUBLICAN : FrenchRepublicEra.REPUBLICAN);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            FrenchRepublicDate eraBased = FrenchRepublicChronology.INSTANCE.date(era, yoe, 1, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = -200; year < 200; year++) {
            FrenchRepublicDate base = FrenchRepublicChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            FrenchRepublicEra era = (year <= 0 ? FrenchRepublicEra.BEFORE_REPUBLICAN : FrenchRepublicEra.REPUBLICAN);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            FrenchRepublicDate eraBased = FrenchRepublicChronology.INSTANCE.dateYearDay(era, yoe, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(FrenchRepublicChronology.INSTANCE.prolepticYear(FrenchRepublicEra.REPUBLICAN, 4), 4);
        assertEquals(FrenchRepublicChronology.INSTANCE.prolepticYear(FrenchRepublicEra.REPUBLICAN, 3), 3);
        assertEquals(FrenchRepublicChronology.INSTANCE.prolepticYear(FrenchRepublicEra.REPUBLICAN, 2), 2);
        assertEquals(FrenchRepublicChronology.INSTANCE.prolepticYear(FrenchRepublicEra.REPUBLICAN, 1), 1);
        assertEquals(FrenchRepublicChronology.INSTANCE.prolepticYear(FrenchRepublicEra.BEFORE_REPUBLICAN, 1), 0);
        assertEquals(FrenchRepublicChronology.INSTANCE.prolepticYear(FrenchRepublicEra.BEFORE_REPUBLICAN, 2), -1);
        assertEquals(FrenchRepublicChronology.INSTANCE.prolepticYear(FrenchRepublicEra.BEFORE_REPUBLICAN, 3), -2);
        assertEquals(FrenchRepublicChronology.INSTANCE.prolepticYear(FrenchRepublicEra.BEFORE_REPUBLICAN, 4), -3);
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void test_prolepticYear_badEra() {
        FrenchRepublicChronology.INSTANCE.prolepticYear(IsoEra.CE, 4);
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(FrenchRepublicChronology.INSTANCE.eraOf(1), FrenchRepublicEra.REPUBLICAN);
        assertEquals(FrenchRepublicChronology.INSTANCE.eraOf(0), FrenchRepublicEra.BEFORE_REPUBLICAN);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_Chronology_eraOf_invalid() {
        FrenchRepublicChronology.INSTANCE.eraOf(2);
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = FrenchRepublicChronology.INSTANCE.eras();
        assertEquals(eras.size(), 2);
        assertEquals(eras.contains(FrenchRepublicEra.BEFORE_REPUBLICAN), true);
        assertEquals(eras.contains(FrenchRepublicEra.REPUBLICAN), true);
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(FrenchRepublicChronology.INSTANCE.range(DAY_OF_WEEK), ValueRange.of(1, 10));
        assertEquals(FrenchRepublicChronology.INSTANCE.range(DAY_OF_MONTH), ValueRange.of(1, 5, 30));
        assertEquals(FrenchRepublicChronology.INSTANCE.range(DAY_OF_YEAR), ValueRange.of(1, 365, 366));
        assertEquals(FrenchRepublicChronology.INSTANCE.range(MONTH_OF_YEAR), ValueRange.of(1, 13));
    }

    //-----------------------------------------------------------------------
    // FrenchRepublicDate.range
    //-----------------------------------------------------------------------
    @DataProvider(name = "ranges")
    Object[][] data_ranges() {
        return new Object[][] {
            {1, 1, 23, DAY_OF_MONTH, 1, 30},
            {1, 2, 23, DAY_OF_MONTH, 1, 30},
            {1, 3, 23, DAY_OF_MONTH, 1, 30},
            {1, 4, 23, DAY_OF_MONTH, 1, 30},
            {1, 5, 23, DAY_OF_MONTH, 1, 30},
            {1, 6, 23, DAY_OF_MONTH, 1, 30},
            {1, 7, 23, DAY_OF_MONTH, 1, 30},
            {1, 8, 23, DAY_OF_MONTH, 1, 30},
            {1, 9, 23, DAY_OF_MONTH, 1, 30},
            {1, 10, 23, DAY_OF_MONTH, 1, 30},
            {1, 11, 23, DAY_OF_MONTH, 1, 30},
            {1, 12, 23, DAY_OF_MONTH, 1, 30},
            {1, 13, 2, DAY_OF_MONTH, 1, 5},
            {1, 1, 23, DAY_OF_YEAR, 1, 365},
            {1, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 3},
            {1, 12, 23, ALIGNED_WEEK_OF_MONTH, 1, 3},
            {1, 13, 2, ALIGNED_WEEK_OF_MONTH, 1, 1},

            {3, 13, 2, DAY_OF_MONTH, 1, 6},
            {3, 13, 2, DAY_OF_YEAR, 1, 366},
            {3, 13, 2, ALIGNED_WEEK_OF_MONTH, 1, 1},
        };
    }

    @Test(dataProvider = "ranges")
    public void test_range(int year, int month, int dom, TemporalField field, int expectedMin, int expectedMax) {
        assertEquals(FrenchRepublicDate.of(year, month, dom).range(field), ValueRange.of(expectedMin, expectedMax));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_unsupported() {
        FrenchRepublicDate.of(1, 6, 30).range(MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // FrenchRepublicDate.getLong
    //-----------------------------------------------------------------------
    @DataProvider(name = "getLong")
    Object[][] data_getLong() {
        return new Object[][] {
            {1, 6, 8, DAY_OF_WEEK, 8},
            {1, 6, 8, DAY_OF_MONTH, 8},
            {1, 6, 8, DAY_OF_YEAR, 30 * 5 + 8},
            {1, 6, 8, ALIGNED_DAY_OF_WEEK_IN_MONTH, 8},
            {1, 6, 8, ALIGNED_WEEK_OF_MONTH, 1},
            {1, 6, 8, ALIGNED_DAY_OF_WEEK_IN_YEAR, 8},
            {1, 6, 8, ALIGNED_WEEK_OF_YEAR, 16},
            {1, 6, 8, MONTH_OF_YEAR, 6},
            {1, 6, 8, PROLEPTIC_MONTH, 13 + 6 - 1},
            {1, 6, 8, YEAR, 1},
            {1, 6, 8, ERA, 1},

            {1, 6, 8, ERA, 1},
            {0, 6, 8, ERA, 0},
        };
    }

    @Test(dataProvider = "getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(FrenchRepublicDate.of(year, month, dom).getLong(field), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_unsupported() {
        FrenchRepublicDate.of(1, 6, 30).getLong(MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // FrenchRepublicDate.with
    //-----------------------------------------------------------------------
    @DataProvider(name = "with")
    Object[][] data_with() {
        return new Object[][] {
            {1, 5, 26, DAY_OF_WEEK, 3, 1, 5, 23},
            {1, 5, 26, DAY_OF_WEEK, 10, 1, 5, 30},
            {1, 5, 26, DAY_OF_MONTH, 30, 1, 5, 30},
            {1, 5, 26, DAY_OF_MONTH, 26, 1, 5, 26},
            {1, 5, 26, DAY_OF_YEAR, 365, 1, 13, 5},
            {1, 5, 26, DAY_OF_YEAR, 146, 1, 5, 26},
            {1, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 1, 5, 23},
            {1, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 1, 5, 25},
            {1, 5, 26, ALIGNED_WEEK_OF_MONTH, 1, 1, 5, 6},
            {1, 5, 26, ALIGNED_WEEK_OF_MONTH, 3, 1, 5, 26},
            {1, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 1, 5, 22},
            {1, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 6, 1, 5, 26},
            {1, 5, 26, ALIGNED_WEEK_OF_YEAR, 23, 1, 8, 16},
            {1, 5, 26, ALIGNED_WEEK_OF_YEAR, 21, 1, 7, 26},
            {1, 5, 26, MONTH_OF_YEAR, 7, 1, 7, 26},
            {1, 5, 26, MONTH_OF_YEAR, 5, 1, 5, 26},

            {1, 5, 26, PROLEPTIC_MONTH, 3 * 13 + 3 - 1, 3, 3, 26},
            {1, 5, 26, PROLEPTIC_MONTH, 4 * 13 + 5 - 1, 4, 5, 26},
            {1, 5, 26, YEAR, 2, 2, 5, 26},
            {1, 5, 26, YEAR, 3, 3, 5, 26},
            {1, 5, 26, YEAR_OF_ERA, 2, 2, 5, 26},
            {1, 5, 26, YEAR_OF_ERA, 3, 3, 5, 26},
            //{1, 5, 26, ERA, 0, -1, 5, 26},
            {1, 5, 26, ERA, 1, 1, 5, 26},

            {1, 3, 30, MONTH_OF_YEAR, 13, 1, 13, 5},
            {3, 3, 30, MONTH_OF_YEAR, 13, 3, 13, 6},
            {1, 3, 30, MONTH_OF_YEAR, 6, 1, 6, 30},
            {3, 13, 6, YEAR, 4, 4, 13, 5},
            {-3, 6, 8, YEAR_OF_ERA, 2, -1, 6, 8},
        };
    }

    @Test(dataProvider = "with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(FrenchRepublicDate.of(year, month, dom).with(field, value), FrenchRepublicDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_with_TemporalField_unsupported() {
        FrenchRepublicDate.of(1, 6, 30).with(MINUTE_OF_DAY, 0);
    }

    //-----------------------------------------------------------------------
    // FrenchRepublicDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        FrenchRepublicDate base = FrenchRepublicDate.of(1, 6, 23);
        FrenchRepublicDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, FrenchRepublicDate.of(1, 6, 30));
    }

    @Test
    public void test_adjust2() {
        FrenchRepublicDate base = FrenchRepublicDate.of(3, 13, 2);
        FrenchRepublicDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, FrenchRepublicDate.of(3, 13, 6));
    }

    //-----------------------------------------------------------------------
    // FrenchRepublicDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        FrenchRepublicDate frenchRepublic = FrenchRepublicDate.of(1, 1, 4);
        FrenchRepublicDate test = frenchRepublic.with(LocalDate.of(1793, 2, 28));
        assertEquals(test, FrenchRepublicDate.of(1, 6, 10));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_adjust_toMonth() {
        FrenchRepublicDate frenchRepublic = FrenchRepublicDate.of(1, 1, 4);
        frenchRepublic.with(Month.APRIL);
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(FrenchRepublicDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToFrenchRepublicDate() {
        FrenchRepublicDate frenchRepublic = FrenchRepublicDate.of(1, 6, 10);
        LocalDate test = LocalDate.MIN.with(frenchRepublic);
        assertEquals(test, LocalDate.of(1793, 2, 28));
    }

    @Test
    public void test_LocalDateTime_adjustToFrenchRepublicDate() {
        FrenchRepublicDate frenchRepublic = FrenchRepublicDate.of(1, 6, 10);
        LocalDateTime test = LocalDateTime.MIN.with(frenchRepublic);
        assertEquals(test, LocalDateTime.of(1793, 2, 28, 0, 0));
    }

    //-----------------------------------------------------------------------
    // FrenchRepublicDate.plus
    //-----------------------------------------------------------------------
    @DataProvider(name = "plus")
    Object[][] data_plus() {
        return new Object[][] {
            {1, 5, 26, 0, DAYS, 1, 5, 26},
            {1, 5, 26, 8, DAYS, 1, 6, 4},
            {1, 5, 26, -3, DAYS, 1, 5, 23},
            {1, 5, 26, 0, WEEKS, 1, 5, 26},
            {1, 5, 26, 3, WEEKS, 1, 6, 26},
            {1, 5, 26, -5, WEEKS, 1, 4, 6},
            {1, 12, 25, 1, WEEKS, 1, 13, 5},
            //{1, 12, 25, 2, WEEKS, 2, 1, 5},
            //{1, 12, 25, 3, WEEKS, 2, 2, 5},
            {1, 5, 26, 0, MONTHS, 1, 5, 26},
            {1, 5, 26, 3, MONTHS, 1, 8, 26},
            {2, 5, 5, -5, MONTHS, 1, 13, 5},
            {1, 5, 26, 0, YEARS, 1, 5, 26},
            {1, 5, 26, 3, YEARS, 4, 5, 26},
            {13, 5, 26, -5, YEARS, 8, 5, 26},
            {1, 5, 26, 0, DECADES, 1, 5, 26},
            {1, 5, 26, 3, DECADES, 31, 5, 26},
            {12, 5, 26, -1, DECADES, 2, 5, 26},
            {1, 5, 26, 0, CENTURIES, 1, 5, 26},
            {1, 5, 26, 3, CENTURIES, 301, 5, 26},
            {501, 5, 26, -5, CENTURIES, 1, 5, 26},
            {1, 5, 26, 0, MILLENNIA, 1, 5, 26},
            {1, 5, 26, 3, MILLENNIA, 3001, 5, 26},
            {5002, 5, 26, -5, MILLENNIA, 5002 - 5000, 5, 26},
            //{2, 5, 26, -1, ERAS, -1, 5, 26},
        };
    }

    @Test(dataProvider = "plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(FrenchRepublicDate.of(year, month, dom).plus(amount, unit), FrenchRepublicDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(dataProvider = "plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(FrenchRepublicDate.of(year, month, dom).minus(amount, unit), FrenchRepublicDate.of(expectedYear, expectedMonth, expectedDom));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_plus_TemporalUnit_unsupported() {
        FrenchRepublicDate.of(2012, 6, 30).plus(0, MINUTES);
    }

    //-----------------------------------------------------------------------
    // FrenchRepublicDate.until
    //-----------------------------------------------------------------------
    @DataProvider(name = "until")
    Object[][] data_until() {
        return new Object[][] {
            {1, 5, 26, 1, 5, 26, DAYS, 0},
            {1, 5, 26, 1, 6, 1, DAYS, 5},
            {1, 5, 26, 1, 5, 20, DAYS, -6},
            {1, 5, 26, 1, 5, 21, WEEKS, 0},
            {1, 5, 26, 1, 5, 30, WEEKS, 0},
            {1, 5, 26, 1, 6, 1, WEEKS, 0},
            {1, 5, 26, 1, 6, 5, WEEKS, 0},
            {1, 5, 26, 1, 6, 6, WEEKS, 1},
            {1, 5, 26, 1, 5, 26, MONTHS, 0},
            {1, 5, 26, 1, 6, 25, MONTHS, 0},
            {1, 5, 26, 1, 6, 26, MONTHS, 1},
            {1, 5, 26, 1, 5, 26, YEARS, 0},
            {1, 5, 26, 2, 5, 25, YEARS, 0},
            {1, 5, 26, 2, 5, 26, YEARS, 1},
            {1, 5, 26, 1, 5, 26, DECADES, 0},
            {1, 5, 26, 11, 5, 25, DECADES, 0},
            {1, 5, 26, 11, 5, 26, DECADES, 1},
            {1, 5, 26, 1, 5, 26, CENTURIES, 0},
            {1, 5, 26, 101, 5, 25, CENTURIES, 0},
            {1, 5, 26, 101, 5, 26, CENTURIES, 1},
            {1, 5, 26, 1, 5, 26, MILLENNIA, 0},
            {1, 5, 26, 1001, 5, 25, MILLENNIA, 0},
            {1, 5, 26, 1001, 5, 26, MILLENNIA, 1},
            /*{-2013, 5, 26, 0, 5, 26, ERAS, 0},
            {-2013, 5, 26, 2014, 5, 26, ERAS, 1},*/
        };
    }

    @Test(dataProvider = "until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        FrenchRepublicDate start = FrenchRepublicDate.of(year1, month1, dom1);
        FrenchRepublicDate end = FrenchRepublicDate.of(year2, month2, dom2);
        assertEquals(start.until(end, unit), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_until_TemporalUnit_unsupported() {
        FrenchRepublicDate start = FrenchRepublicDate.of(2012, 6, 30);
        FrenchRepublicDate end = FrenchRepublicDate.of(2012, 7, 1);
        start.until(end, MINUTES);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(FrenchRepublicDate.of(1, 5, 26).plus(FrenchRepublicChronology.INSTANCE.period(0, 2, 3)), FrenchRepublicDate.of(1, 7, 29));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_Period_ISO() {
        assertEquals(FrenchRepublicDate.of(1, 5, 26).plus(Period.ofMonths(2)), FrenchRepublicDate.of(1, 7, 26));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(FrenchRepublicDate.of(1, 5, 26).minus(FrenchRepublicChronology.INSTANCE.period(0, 2, 3)), FrenchRepublicDate.of(1, 3, 23));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_Period_ISO() {
        assertEquals(FrenchRepublicDate.of(1, 5, 26).minus(Period.ofMonths(2)), FrenchRepublicDate.of(1, 3, 26));
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    void test_equals() {
        FrenchRepublicDate a1 = FrenchRepublicDate.of(1, 1, 3);
        FrenchRepublicDate a2 = FrenchRepublicDate.of(1, 1, 3);
        FrenchRepublicDate b = FrenchRepublicDate.of(1, 1, 4);
        FrenchRepublicDate c = FrenchRepublicDate.of(1, 2, 3);
        FrenchRepublicDate d = FrenchRepublicDate.of(2, 1, 3);

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
            {FrenchRepublicDate.of(1, 1, 1), "French Republican REPUBLICAN 1-01-01"},
            {FrenchRepublicDate.of(14, 13, 5), "French Republican REPUBLICAN 14-13-05"},
        };
    }

    @Test(dataProvider = "toString")
    public void test_toString(FrenchRepublicDate frenchRepublic, String expected) {
        assertEquals(frenchRepublic.toString(), expected);
    }

}
