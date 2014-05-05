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

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.testng.Assert.assertEquals;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ValueRange;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestJulianChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        Chronology chrono = Chronology.of("Julian");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, JulianChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Julian");
        Assert.assertEquals(chrono.getCalendarType(), "julian");
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("julian");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, JulianChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Julian");
        Assert.assertEquals(chrono.getCalendarType(), "julian");
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name="samples")
    Object[][] data_samples() {
        return new Object[][] {
            {JulianDate.of(1, 1, 1), LocalDate.of(0, 12, 30)},
            {JulianDate.of(1, 1, 2), LocalDate.of(0, 12, 31)},
            {JulianDate.of(1, 1, 3), LocalDate.of(1, 1, 1)},

            {JulianDate.of(1, 2, 28), LocalDate.of(1, 2, 26)},
            {JulianDate.of(1, 3, 1), LocalDate.of(1, 2, 27)},
            {JulianDate.of(1, 3, 2), LocalDate.of(1, 2, 28)},
            {JulianDate.of(1, 3, 3), LocalDate.of(1, 3, 1)},

            {JulianDate.of(4, 2, 28), LocalDate.of(4, 2, 26)},
            {JulianDate.of(4, 2, 29), LocalDate.of(4, 2, 27)},
            {JulianDate.of(4, 3, 1), LocalDate.of(4, 2, 28)},
            {JulianDate.of(4, 3, 2), LocalDate.of(4, 2, 29)},
            {JulianDate.of(4, 3, 3), LocalDate.of(4, 3, 1)},

            {JulianDate.of(100, 2, 28), LocalDate.of(100, 2, 26)},
            {JulianDate.of(100, 2, 29), LocalDate.of(100, 2, 27)},
            {JulianDate.of(100, 3, 1), LocalDate.of(100, 2, 28)},
            {JulianDate.of(100, 3, 2), LocalDate.of(100, 3, 1)},
            {JulianDate.of(100, 3, 3), LocalDate.of(100, 3, 2)},

            {JulianDate.of(0, 12, 31), LocalDate.of(0, 12, 29)},
            {JulianDate.of(0, 12, 30), LocalDate.of(0, 12, 28)},

            {JulianDate.of(1582, 10, 4), LocalDate.of(1582, 10, 14)},
            {JulianDate.of(1582, 10, 5), LocalDate.of(1582, 10, 15)},
            {JulianDate.of(1945, 10, 30), LocalDate.of(1945, 11, 12)},

            {JulianDate.of(2012, 6, 22), LocalDate.of(2012, 7, 5)},
            {JulianDate.of(2012, 6, 23), LocalDate.of(2012, 7, 6)},
        };
    }

    @Test(dataProvider="samples")
    public void test_LocalDate_from_JulianDate(JulianDate julian, LocalDate iso) {
        assertEquals(LocalDate.from(julian), iso);
    }

    @Test(dataProvider="samples")
    public void test_JulianDate_from_LocalDate(JulianDate julian, LocalDate iso) {
        assertEquals(JulianDate.from(iso), julian);
    }

    @Test(dataProvider="samples")
    public void test_JulianDate_chronology_dateEpochDay(JulianDate julian, LocalDate iso) {
        assertEquals(JulianChronology.INSTANCE.dateEpochDay(iso.toEpochDay()), julian);
    }

    @Test(dataProvider="samples")
    public void test_JulianDate_toEpochDay(JulianDate julian, LocalDate iso) {
        assertEquals(julian.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider="samples")
    public void test_JulianDate_until_CoptiDate(JulianDate julian, LocalDate iso) {
        assertEquals(julian.until(julian), JulianChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider="samples")
    public void test_JulianDate_until_LocalDate(JulianDate julian, LocalDate iso) {
        assertEquals(julian.until(iso), JulianChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider="samples")
    public void test_LocalDate_until_CoptiDate(JulianDate julian, LocalDate iso) {
        assertEquals(iso.until(julian), Period.ZERO);
    }

    @Test(dataProvider="samples")
    public void test_Chronology_date_Temporal(JulianDate julian, LocalDate iso) {
        assertEquals(JulianChronology.INSTANCE.date(iso), julian);
    }

    @Test(dataProvider="samples")
    public void test_plusDays(JulianDate julian, LocalDate iso) {
        assertEquals(LocalDate.from(julian.plus(0, DAYS)), iso);
        assertEquals(LocalDate.from(julian.plus(1, DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(julian.plus(35, DAYS)), iso.plusDays(35));
        assertEquals(LocalDate.from(julian.plus(-1, DAYS)), iso.plusDays(-1));
        assertEquals(LocalDate.from(julian.plus(-60, DAYS)), iso.plusDays(-60));
    }

    @Test(dataProvider="samples")
    public void test_minusDays(JulianDate julian, LocalDate iso) {
        assertEquals(LocalDate.from(julian.minus(0, DAYS)), iso);
        assertEquals(LocalDate.from(julian.minus(1, DAYS)), iso.minusDays(1));
        assertEquals(LocalDate.from(julian.minus(35, DAYS)), iso.minusDays(35));
        assertEquals(LocalDate.from(julian.minus(-1, DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(julian.minus(-60, DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider="samples")
    public void test_until_DAYS(JulianDate julian, LocalDate iso) {
        assertEquals(julian.until(iso.plusDays(0), DAYS), 0);
        assertEquals(julian.until(iso.plusDays(1), DAYS), 1);
        assertEquals(julian.until(iso.plusDays(35), DAYS), 35);
        assertEquals(julian.until(iso.minusDays(40), DAYS), -40);
    }

    @DataProvider(name="badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {1900, 0, 0},

            {1900, -1, 1},
            {1900, 0, 1},
            {1900, 13, 1},
            {1900, 14, 1},

            {1900, 1, -1},
            {1900, 1, 0},
            {1900, 1, 32},

            {1900, 2, -1},
            {1900, 2, 0},
            {1900, 2, 30},
            {1900, 2, 31},
            {1900, 2, 32},

            {1899, 2, -1},
            {1899, 2, 0},
            {1899, 2, 29},
            {1899, 2, 30},
            {1899, 2, 31},
            {1899, 2, 32},

            {1900, 12, -1},
            {1900, 12, 0},
            {1900, 12, 32},

            {1900, 3, 32},
            {1900, 4, 31},
            {1900, 5, 32},
            {1900, 6, 31},
            {1900, 7, 32},
            {1900, 8, 32},
            {1900, 9, 31},
            {1900, 10, 32},
            {1900, 11, 31},
        };
    }

    @Test(dataProvider="badDates", expectedExceptions=DateTimeException.class)
    public void test_badDates(int year, int month, int dom) {
        JulianDate.of(year, month, dom);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_chronology_dateYearDay_badDate() {
        JulianChronology.INSTANCE.dateYearDay(2001, 366);
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        for (int year = -200; year < 200; year++) {
            JulianDate base = JulianDate.of(year, 1, 1);
            assertEquals(base.isLeapYear(), (year % 4) == 0);
            assertEquals(JulianChronology.INSTANCE.isLeapYear(year), (year  % 4) == 0);
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(JulianChronology.INSTANCE.isLeapYear(8), true);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(7), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(6), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(5), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(4), true);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(3), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(2), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(1), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(0), true);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(-1), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(-2), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(-3), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(-4), true);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(-5), false);
        assertEquals(JulianChronology.INSTANCE.isLeapYear(-6), false);
    }

    @DataProvider(name="lengthOfMonth")
    Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {1900, 1, 31},
            {1900, 2, 29},
            {1900, 3, 31},
            {1900, 4, 30},
            {1900, 5, 31},
            {1900, 6, 30},
            {1900, 7, 31},
            {1900, 8, 31},
            {1900, 9, 30},
            {1900, 10, 31},
            {1900, 11, 30},
            {1900, 12, 31},

            {1901, 2, 28},
            {1902, 2, 28},
            {1903, 2, 28},
            {1904, 2, 29},
            {2000, 2, 29},
            {2100, 2, 29},
        };
    }

    @Test(dataProvider="lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(JulianDate.of(year, month, 1).lengthOfMonth(), length);
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = -200; year < 200; year++) {
            JulianDate base = JulianChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            JulianEra era = (year <= 0 ? JulianEra.BC : JulianEra.AD);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            JulianDate eraBased = JulianChronology.INSTANCE.date(era, yoe, 1, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = -200; year < 200; year++) {
            JulianDate base = JulianChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            JulianEra era = (year <= 0 ? JulianEra.BC : JulianEra.AD);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            JulianDate eraBased = JulianChronology.INSTANCE.dateYearDay(era, yoe, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(JulianChronology.INSTANCE.prolepticYear(JulianEra.AD, 4), 4);
        assertEquals(JulianChronology.INSTANCE.prolepticYear(JulianEra.AD, 3), 3);
        assertEquals(JulianChronology.INSTANCE.prolepticYear(JulianEra.AD, 2), 2);
        assertEquals(JulianChronology.INSTANCE.prolepticYear(JulianEra.AD, 1), 1);
        assertEquals(JulianChronology.INSTANCE.prolepticYear(JulianEra.BC, 1), 0);
        assertEquals(JulianChronology.INSTANCE.prolepticYear(JulianEra.BC, 2), -1);
        assertEquals(JulianChronology.INSTANCE.prolepticYear(JulianEra.BC, 3), -2);
        assertEquals(JulianChronology.INSTANCE.prolepticYear(JulianEra.BC, 4), -3);
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void test_prolepticYear_badEra() {
        JulianChronology.INSTANCE.prolepticYear(IsoEra.CE, 4);
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(JulianChronology.INSTANCE.eraOf(1), JulianEra.AD);
        assertEquals(JulianChronology.INSTANCE.eraOf(0), JulianEra.BC);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_Chronology_eraOf_invalid() {
        JulianChronology.INSTANCE.eraOf(2);
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = JulianChronology.INSTANCE.eras();
        assertEquals(eras.size(), 2);
        assertEquals(eras.contains(JulianEra.BC), true);
        assertEquals(eras.contains(JulianEra.AD), true);
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(JulianChronology.INSTANCE.range(DAY_OF_WEEK), ValueRange.of(1, 7));
        assertEquals(JulianChronology.INSTANCE.range(DAY_OF_MONTH), ValueRange.of(1, 28, 31));
        assertEquals(JulianChronology.INSTANCE.range(DAY_OF_YEAR), ValueRange.of(1, 365, 366));
        assertEquals(JulianChronology.INSTANCE.range(MONTH_OF_YEAR), ValueRange.of(1, 12));
    }

    //-----------------------------------------------------------------------
    // JulianDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        JulianDate base = JulianDate.of(2012, 6, 23);
        JulianDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, JulianDate.of(2012, 6, 30));
    }

    @Test
    public void test_adjust2() {
        JulianDate base = JulianDate.of(2012, 2, 23);
        JulianDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, JulianDate.of(2012, 2, 29));
    }

    //-----------------------------------------------------------------------
    // JulianDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        JulianDate julian = JulianDate.of(2000, 1, 4);
        JulianDate test = julian.with(LocalDate.of(2012, 7, 6));
        assertEquals(test, JulianDate.of(2012, 6, 23));
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_adjust_toMonth() {
        JulianDate julian = JulianDate.of(2000, 1, 4);
        julian.with(Month.APRIL);
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(JulianDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToJulianDate() {
        JulianDate julian = JulianDate.of(2012, 6, 23);
        LocalDate test = LocalDate.MIN.with(julian);
        assertEquals(test, LocalDate.of(2012, 7, 6));
    }

    @Test
    public void test_LocalDateTime_adjustToJulianDate() {
        JulianDate julian = JulianDate.of(2012, 6, 23);
        LocalDateTime test = LocalDateTime.MIN.with(julian);
        assertEquals(test, LocalDateTime.of(2012, 7, 6, 0, 0));
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    void test_equals() {
        JulianDate a1 = JulianDate.of(2000, 1, 3);
        JulianDate a2 = JulianDate.of(2000, 1, 3);
        JulianDate b = JulianDate.of(2000, 1, 4);
        JulianDate c = JulianDate.of(2000, 2, 3);
        JulianDate d = JulianDate.of(2001, 1, 3);
        
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
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {JulianDate.of(1, 1, 1), "Julian AD 1-01-01"},
            {JulianDate.of(2012, 6, 23), "Julian AD 2012-06-23"},
        };
    }

    @Test(dataProvider="toString")
    public void test_toString(JulianDate julian, String expected) {
        assertEquals(julian.toString(), expected);
    }

}
