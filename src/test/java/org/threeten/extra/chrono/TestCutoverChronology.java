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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestCutoverChronology {

    private static final CutoverChronology BRITISH = CutoverChronology.of(LocalDate.of(1752, 9, 14));
    private static final CutoverChronology VATICAN = CutoverChronology.of(LocalDate.of(1582, 10, 15));

    @DataProvider(name = "cutovers")
    Object[][] data_cutovers() {
        return new Object[][] {
            {BRITISH},
            {VATICAN},
        };
    }

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        Assert.assertEquals(BRITISH.getId(), "Cutover[1752-09-14]");
        Assert.assertEquals(BRITISH.getCalendarType(), null);
        Assert.assertEquals(VATICAN.getId(), "Cutover[1582-10-15]");
        Assert.assertEquals(VATICAN.getCalendarType(), null);
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
        return new Object[][] {
            {CutoverDate.of(BRITISH, 1, 1, 1), LocalDate.of(0, 12, 30)},
            {CutoverDate.of(BRITISH, 1, 1, 2), LocalDate.of(0, 12, 31)},
            {CutoverDate.of(BRITISH, 1, 1, 3), LocalDate.of(1, 1, 1)},
            {CutoverDate.of(VATICAN, 1, 1, 1), LocalDate.of(0, 12, 30)},
            {CutoverDate.of(VATICAN, 1, 1, 2), LocalDate.of(0, 12, 31)},
            {CutoverDate.of(VATICAN, 1, 1, 3), LocalDate.of(1, 1, 1)},

            {CutoverDate.of(BRITISH, 1, 2, 28), LocalDate.of(1, 2, 26)},
            {CutoverDate.of(BRITISH, 1, 3, 1), LocalDate.of(1, 2, 27)},
            {CutoverDate.of(BRITISH, 1, 3, 2), LocalDate.of(1, 2, 28)},
            {CutoverDate.of(BRITISH, 1, 3, 3), LocalDate.of(1, 3, 1)},
            {CutoverDate.of(VATICAN, 1, 2, 28), LocalDate.of(1, 2, 26)},
            {CutoverDate.of(VATICAN, 1, 3, 1), LocalDate.of(1, 2, 27)},
            {CutoverDate.of(VATICAN, 1, 3, 2), LocalDate.of(1, 2, 28)},
            {CutoverDate.of(VATICAN, 1, 3, 3), LocalDate.of(1, 3, 1)},

            {CutoverDate.of(BRITISH, 4, 2, 28), LocalDate.of(4, 2, 26)},
            {CutoverDate.of(BRITISH, 4, 2, 29), LocalDate.of(4, 2, 27)},
            {CutoverDate.of(BRITISH, 4, 3, 1), LocalDate.of(4, 2, 28)},
            {CutoverDate.of(BRITISH, 4, 3, 2), LocalDate.of(4, 2, 29)},
            {CutoverDate.of(BRITISH, 4, 3, 3), LocalDate.of(4, 3, 1)},
            {CutoverDate.of(VATICAN, 4, 2, 28), LocalDate.of(4, 2, 26)},
            {CutoverDate.of(VATICAN, 4, 2, 29), LocalDate.of(4, 2, 27)},
            {CutoverDate.of(VATICAN, 4, 3, 1), LocalDate.of(4, 2, 28)},
            {CutoverDate.of(VATICAN, 4, 3, 2), LocalDate.of(4, 2, 29)},
            {CutoverDate.of(VATICAN, 4, 3, 3), LocalDate.of(4, 3, 1)},

            {CutoverDate.of(BRITISH, 100, 2, 28), LocalDate.of(100, 2, 26)},
            {CutoverDate.of(BRITISH, 100, 2, 29), LocalDate.of(100, 2, 27)},
            {CutoverDate.of(BRITISH, 100, 3, 1), LocalDate.of(100, 2, 28)},
            {CutoverDate.of(BRITISH, 100, 3, 2), LocalDate.of(100, 3, 1)},
            {CutoverDate.of(BRITISH, 100, 3, 3), LocalDate.of(100, 3, 2)},
            {CutoverDate.of(VATICAN, 100, 2, 28), LocalDate.of(100, 2, 26)},
            {CutoverDate.of(VATICAN, 100, 2, 29), LocalDate.of(100, 2, 27)},
            {CutoverDate.of(VATICAN, 100, 3, 1), LocalDate.of(100, 2, 28)},
            {CutoverDate.of(VATICAN, 100, 3, 2), LocalDate.of(100, 3, 1)},
            {CutoverDate.of(VATICAN, 100, 3, 3), LocalDate.of(100, 3, 2)},

            {CutoverDate.of(BRITISH, 0, 12, 31), LocalDate.of(0, 12, 29)},
            {CutoverDate.of(BRITISH, 0, 12, 30), LocalDate.of(0, 12, 28)},
            {CutoverDate.of(VATICAN, 0, 12, 31), LocalDate.of(0, 12, 29)},
            {CutoverDate.of(VATICAN, 0, 12, 30), LocalDate.of(0, 12, 28)},

            {CutoverDate.of(BRITISH, 1582, 10, 4), LocalDate.of(1582, 10, 14)},
            {CutoverDate.of(BRITISH, 1582, 10, 5), LocalDate.of(1582, 10, 15)},
            {CutoverDate.of(VATICAN, 1582, 10, 4), LocalDate.of(1582, 10, 14)},
            {CutoverDate.of(VATICAN, 1582, 10, 5), LocalDate.of(1582, 10, 15)},  // leniently accept invalid
            {CutoverDate.of(VATICAN, 1582, 10, 14), LocalDate.of(1582, 10, 24)},  // leniently accept invalid
            {CutoverDate.of(VATICAN, 1582, 10, 15), LocalDate.of(1582, 10, 15)},

            {CutoverDate.of(BRITISH, 1751, 12, 20), LocalDate.of(1751, 12, 31)},
            {CutoverDate.of(BRITISH, 1751, 12, 31), LocalDate.of(1752, 1, 11)},
            {CutoverDate.of(BRITISH, 1752, 1, 1), LocalDate.of(1752, 1, 12)},
            {CutoverDate.of(BRITISH, 1752, 9, 1), LocalDate.of(1752, 9, 12)},
            {CutoverDate.of(BRITISH, 1752, 9, 2), LocalDate.of(1752, 9, 13)},
            {CutoverDate.of(BRITISH, 1752, 9, 3), LocalDate.of(1752, 9, 14)},  // leniently accept invalid
            {CutoverDate.of(BRITISH, 1752, 9, 13), LocalDate.of(1752, 9, 24)},  // leniently accept invalid
            {CutoverDate.of(BRITISH, 1752, 9, 14), LocalDate.of(1752, 9, 14)},
            {CutoverDate.of(VATICAN, 1752, 1, 1), LocalDate.of(1752, 1, 1)},

            {CutoverDate.of(BRITISH, 1945, 11, 12), LocalDate.of(1945, 11, 12)},
            {CutoverDate.of(BRITISH, 2012, 7, 5), LocalDate.of(2012, 7, 5)},
            {CutoverDate.of(BRITISH, 2012, 7, 6), LocalDate.of(2012, 7, 6)},
            {CutoverDate.of(VATICAN, 1945, 11, 12), LocalDate.of(1945, 11, 12)},
            {CutoverDate.of(VATICAN, 2012, 7, 5), LocalDate.of(2012, 7, 5)},
            {CutoverDate.of(VATICAN, 2012, 7, 6), LocalDate.of(2012, 7, 6)},
        };
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_from_CutoverDate(CutoverDate cutover, LocalDate iso) {
        assertEquals(LocalDate.from(cutover), iso);
    }

    @Test(dataProvider = "samples")
    public void test_CutoverDate_from_LocalDate(CutoverDate cutover, LocalDate iso) {
        assertEquals(CutoverDate.from(cutover.getChronology(), iso), cutover);
    }

    @Test(dataProvider = "samples")
    public void test_CutoverDate_chronology_dateEpochDay(CutoverDate cutover, LocalDate iso) {
        assertEquals(cutover.getChronology().dateEpochDay(iso.toEpochDay()), cutover);
    }

    @Test(dataProvider = "samples")
    public void test_CutoverDate_toEpochDay(CutoverDate cutover, LocalDate iso) {
        assertEquals(cutover.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider = "samples")
    public void test_CutoverDate_until_CutoverDate(CutoverDate cutover, LocalDate iso) {
        assertEquals(cutover.until(cutover), cutover.getChronology().period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_CutoverDate_until_LocalDate(CutoverDate cutover, LocalDate iso) {
        assertEquals(cutover.until(iso), cutover.getChronology().period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_until_CutoverDate(CutoverDate cutover, LocalDate iso) {
        assertEquals(iso.until(cutover), Period.ZERO);
    }

    @Test(dataProvider = "samples")
    public void test_Chronology_date_Temporal(CutoverDate cutover, LocalDate iso) {
        assertEquals(cutover.getChronology().date(iso), cutover);
    }

    @Test(dataProvider = "samples")
    public void test_plusDays(CutoverDate cutover, LocalDate iso) {
        assertEquals(LocalDate.from(cutover.plus(0, DAYS)), iso);
        assertEquals(LocalDate.from(cutover.plus(1, DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(cutover.plus(35, DAYS)), iso.plusDays(35));
        assertEquals(LocalDate.from(cutover.plus(-1, DAYS)), iso.plusDays(-1));
        assertEquals(LocalDate.from(cutover.plus(-60, DAYS)), iso.plusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_minusDays(CutoverDate cutover, LocalDate iso) {
        assertEquals(LocalDate.from(cutover.minus(0, DAYS)), iso);
        assertEquals(LocalDate.from(cutover.minus(1, DAYS)), iso.minusDays(1));
        assertEquals(LocalDate.from(cutover.minus(35, DAYS)), iso.minusDays(35));
        assertEquals(LocalDate.from(cutover.minus(-1, DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(cutover.minus(-60, DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_until_DAYS(CutoverDate cutover, LocalDate iso) {
        assertEquals(cutover.until(iso.plusDays(0), DAYS), 0);
        assertEquals(cutover.until(iso.plusDays(1), DAYS), 1);
        assertEquals(cutover.until(iso.plusDays(35), DAYS), 35);
        assertEquals(cutover.until(iso.minusDays(40), DAYS), -40);
    }

    @DataProvider(name = "badDates")
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

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates_British(int year, int month, int dom) {
        CutoverDate.of(BRITISH, year, month, dom);
    }

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates_Vatican(int year, int month, int dom) {
        CutoverDate.of(VATICAN, year, month, dom);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_Chronology_dateYearDay_badDate() {
        BRITISH.dateYearDay(2001, 366);
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test(dataProvider = "cutovers")
    public void test_Chronology_isLeapYear_loop(CutoverChronology chrono) {
        for (int year = -200; year < 200; year++) {
            CutoverDate base = CutoverDate.of(chrono, year, 1, 1);
            assertEquals(base.isLeapYear(), (year % 4) == 0);
            assertEquals(chrono.isLeapYear(year), (year % 4) == 0);
        }
    }

    @Test(dataProvider = "cutovers")
    public void test_Chronology_isLeapYear_specific(CutoverChronology chrono) {
        assertEquals(chrono.isLeapYear(8), true);
        assertEquals(chrono.isLeapYear(7), false);
        assertEquals(chrono.isLeapYear(6), false);
        assertEquals(chrono.isLeapYear(5), false);
        assertEquals(chrono.isLeapYear(4), true);
        assertEquals(chrono.isLeapYear(3), false);
        assertEquals(chrono.isLeapYear(2), false);
        assertEquals(chrono.isLeapYear(1), false);
        assertEquals(chrono.isLeapYear(0), true);
        assertEquals(chrono.isLeapYear(-1), false);
        assertEquals(chrono.isLeapYear(-2), false);
        assertEquals(chrono.isLeapYear(-3), false);
        assertEquals(chrono.isLeapYear(-4), true);
        assertEquals(chrono.isLeapYear(-5), false);
        assertEquals(chrono.isLeapYear(-6), false);
    }

    //-----------------------------------------------------------------------
    // getCutover()
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_getCutover() {
        assertEquals(BRITISH.getCutover(), LocalDate.of(1752, 9, 14));
        assertEquals(VATICAN.getCutover(), LocalDate.of(1582, 10, 15));
    }

    //-----------------------------------------------------------------------
    // lengthOfMonth()
    //-----------------------------------------------------------------------
    @DataProvider(name = "lengthOfMonth")
    Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {1700, 1, 31},
            {1700, 2, 29},
            {1700, 3, 31},
            {1700, 4, 30},
            {1700, 5, 31},
            {1700, 6, 30},
            {1700, 7, 31},
            {1700, 8, 31},
            {1700, 9, 30},
            {1700, 10, 31},
            {1700, 11, 30},
            {1700, 12, 31},

            {1751, 1, 31},
            {1751, 2, 28},
            {1751, 3, 31},
            {1751, 4, 30},
            {1751, 5, 31},
            {1751, 6, 30},
            {1751, 7, 31},
            {1751, 8, 31},
            {1751, 9, 30},
            {1751, 10, 31},
            {1751, 11, 30},
            {1751, 12, 31},

            {1752, 1, 31},
            {1752, 2, 29},
            {1752, 3, 31},
            {1752, 4, 30},
            {1752, 5, 31},
            {1752, 6, 30},
            {1752, 7, 31},
            {1752, 8, 31},
            {1752, 9, 19},
            {1752, 10, 31},
            {1752, 11, 30},
            {1752, 12, 31},

            {1753, 1, 31},
            {1753, 3, 31},
            {1753, 2, 28},
            {1753, 4, 30},
            {1753, 5, 31},
            {1753, 6, 30},
            {1753, 7, 31},
            {1753, 8, 31},
            {1753, 9, 30},
            {1753, 10, 31},
            {1753, 11, 30},
            {1753, 12, 31},

            {1500, 2, 29},
            {1600, 2, 29},
            {1700, 2, 29},
            {1800, 2, 28},
            {1900, 2, 28},
            {1901, 2, 28},
            {1902, 2, 28},
            {1903, 2, 28},
            {1904, 2, 29},
            {2000, 2, 29},
            {2100, 2, 28},
        };
    }

    @Test(dataProvider = "lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(CutoverDate.of(BRITISH, year, month, 1).lengthOfMonth(), length);
    }

    //-----------------------------------------------------------------------
    // lengthOfYear()
    //-----------------------------------------------------------------------
    @DataProvider(name = "lengthOfYear")
    Object[][] data_lengthOfYear() {
        return new Object[][] {
            {-101, 365},
            {-100, 366},
            {-99, 365},
            {-1, 365},
            {0, 366},
            {100, 366},
            {1600, 366},
            {1700, 366},
            {1751, 365},
            {1748, 366},
            {1749, 365},
            {1750, 365},
            {1751, 365},
            {1752, 355},
            {1753, 365},
            {1500, 366},
            {1600, 366},
            {1700, 366},
            {1800, 365},
            {1900, 365},
            {1901, 365},
            {1902, 365},
            {1903, 365},
            {1904, 366},
            {2000, 366},
            {2100, 365},
        };
    }

    @Test(dataProvider = "lengthOfYear")
    public void test_lengthOfYear_atStart(int year, int length) {
        assertEquals(CutoverDate.of(BRITISH, year, 1, 1).lengthOfYear(), length);
    }

    @Test(dataProvider = "lengthOfYear")
    public void test_lengthOfYear_atEnd(int year, int length) {
        assertEquals(CutoverDate.of(BRITISH, year, 12, 31).lengthOfYear(), length);
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test(dataProvider = "cutovers")
    public void test_era_loop(CutoverChronology chrono) {
        for (int year = -200; year < 200; year++) {
            CutoverDate base = chrono.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            JulianEra era = (year <= 0 ? JulianEra.BC : JulianEra.AD);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            CutoverDate eraBased = chrono.date(era, yoe, 1, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test(dataProvider = "cutovers")
    public void test_era_yearDay_loop(CutoverChronology chrono) {
        for (int year = -200; year < 200; year++) {
            CutoverDate base = chrono.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            JulianEra era = (year <= 0 ? JulianEra.BC : JulianEra.AD);
            assertEquals(era, base.getEra());
            int yoe = (year <= 0 ? 1 - year : year);
            assertEquals(yoe, base.get(YEAR_OF_ERA));
            CutoverDate eraBased = chrono.dateYearDay(era, yoe, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_era_yearDay() {
        assertEquals(BRITISH.dateYearDay(1752, 1), CutoverDate.of(BRITISH, 1752, 1, 1));
        assertEquals(BRITISH.dateYearDay(1752, 244), CutoverDate.of(BRITISH, 1752, 8, 31));
        assertEquals(BRITISH.dateYearDay(1752, 246), CutoverDate.of(BRITISH, 1752, 9, 2));
        assertEquals(BRITISH.dateYearDay(1752, 247), CutoverDate.of(BRITISH, 1752, 9, 14));
        assertEquals(BRITISH.dateYearDay(1752, 257), CutoverDate.of(BRITISH, 1752, 9, 24));
        assertEquals(BRITISH.dateYearDay(1752, 258), CutoverDate.of(BRITISH, 1752, 9, 25));
        assertEquals(BRITISH.dateYearDay(1752, 355), CutoverDate.of(BRITISH, 1752, 12, 31));
        assertEquals(BRITISH.dateYearDay(2014, 1), CutoverDate.of(BRITISH, 2014, 1, 1));
    }

    @Test(dataProvider = "cutovers")
    public void test_prolepticYear_specific(CutoverChronology chrono) {
        assertEquals(chrono.prolepticYear(JulianEra.AD, 4), 4);
        assertEquals(chrono.prolepticYear(JulianEra.AD, 3), 3);
        assertEquals(chrono.prolepticYear(JulianEra.AD, 2), 2);
        assertEquals(chrono.prolepticYear(JulianEra.AD, 1), 1);
        assertEquals(chrono.prolepticYear(JulianEra.BC, 1), 0);
        assertEquals(chrono.prolepticYear(JulianEra.BC, 2), -1);
        assertEquals(chrono.prolepticYear(JulianEra.BC, 3), -2);
        assertEquals(chrono.prolepticYear(JulianEra.BC, 4), -3);
    }

    @Test(dataProvider = "cutovers", expectedExceptions = ClassCastException.class)
    public void test_prolepticYear_badEra(CutoverChronology chrono) {
        chrono.prolepticYear(IsoEra.CE, 4);
    }

    @Test(dataProvider = "cutovers")
    public void test_Chronology_eraOf(CutoverChronology chrono) {
        assertEquals(chrono.eraOf(1), JulianEra.AD);
        assertEquals(chrono.eraOf(0), JulianEra.BC);
    }

    @Test(dataProvider = "cutovers", expectedExceptions = DateTimeException.class)
    public void test_Chronology_eraOf_invalid(CutoverChronology chrono) {
        chrono.eraOf(2);
    }

    @Test(dataProvider = "cutovers")
    public void test_Chronology_eras(CutoverChronology chrono) {
        List<Era> eras = chrono.eras();
        assertEquals(eras.size(), 2);
        assertEquals(eras.contains(JulianEra.BC), true);
        assertEquals(eras.contains(JulianEra.AD), true);
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(BRITISH.range(DAY_OF_WEEK), ValueRange.of(1, 7));
        assertEquals(BRITISH.range(DAY_OF_MONTH), ValueRange.of(1, 28, 31));
        assertEquals(BRITISH.range(DAY_OF_YEAR), ValueRange.of(1, 355, 366));
        assertEquals(BRITISH.range(MONTH_OF_YEAR), ValueRange.of(1, 12));
        assertEquals(BRITISH.range(ALIGNED_WEEK_OF_MONTH), ValueRange.of(1, 3, 5));
        assertEquals(BRITISH.range(ALIGNED_WEEK_OF_YEAR), ValueRange.of(1, 51, 53));
    }

    //-----------------------------------------------------------------------
    // CutoverDate.range
    //-----------------------------------------------------------------------
    @DataProvider(name = "ranges")
    Object[][] data_ranges() {
        return new Object[][] {
            {1700, 1, 23, DAY_OF_MONTH, 1, 31},
            {1700, 2, 23, DAY_OF_MONTH, 1, 29},
            {1700, 3, 23, DAY_OF_MONTH, 1, 31},
            {1700, 4, 23, DAY_OF_MONTH, 1, 30},
            {1700, 5, 23, DAY_OF_MONTH, 1, 31},
            {1700, 6, 23, DAY_OF_MONTH, 1, 30},
            {1700, 7, 23, DAY_OF_MONTH, 1, 31},
            {1700, 8, 23, DAY_OF_MONTH, 1, 31},
            {1700, 9, 23, DAY_OF_MONTH, 1, 30},
            {1700, 10, 23, DAY_OF_MONTH, 1, 31},
            {1700, 11, 23, DAY_OF_MONTH, 1, 30},
            {1700, 12, 23, DAY_OF_MONTH, 1, 31},

            {1751, 1, 23, DAY_OF_MONTH, 1, 31},
            {1751, 2, 23, DAY_OF_MONTH, 1, 28},
            {1751, 3, 23, DAY_OF_MONTH, 1, 31},
            {1751, 4, 23, DAY_OF_MONTH, 1, 30},
            {1751, 5, 23, DAY_OF_MONTH, 1, 31},
            {1751, 6, 23, DAY_OF_MONTH, 1, 30},
            {1751, 7, 23, DAY_OF_MONTH, 1, 31},
            {1751, 8, 23, DAY_OF_MONTH, 1, 31},
            {1751, 9, 23, DAY_OF_MONTH, 1, 30},
            {1751, 10, 23, DAY_OF_MONTH, 1, 31},
            {1751, 11, 23, DAY_OF_MONTH, 1, 30},
            {1751, 12, 23, DAY_OF_MONTH, 1, 31},

            {1752, 1, 23, DAY_OF_MONTH, 1, 31},
            {1752, 2, 23, DAY_OF_MONTH, 1, 29},
            {1752, 3, 23, DAY_OF_MONTH, 1, 31},
            {1752, 4, 23, DAY_OF_MONTH, 1, 30},
            {1752, 5, 23, DAY_OF_MONTH, 1, 31},
            {1752, 6, 23, DAY_OF_MONTH, 1, 30},
            {1752, 7, 23, DAY_OF_MONTH, 1, 31},
            {1752, 8, 23, DAY_OF_MONTH, 1, 31},
            {1752, 9, 23, DAY_OF_MONTH, 1, 30},
            {1752, 10, 23, DAY_OF_MONTH, 1, 31},
            {1752, 11, 23, DAY_OF_MONTH, 1, 30},
            {1752, 12, 23, DAY_OF_MONTH, 1, 31},

            {2012, 1, 23, DAY_OF_MONTH, 1, 31},
            {2012, 2, 23, DAY_OF_MONTH, 1, 29},
            {2012, 3, 23, DAY_OF_MONTH, 1, 31},
            {2012, 4, 23, DAY_OF_MONTH, 1, 30},
            {2012, 5, 23, DAY_OF_MONTH, 1, 31},
            {2012, 6, 23, DAY_OF_MONTH, 1, 30},
            {2012, 7, 23, DAY_OF_MONTH, 1, 31},
            {2012, 8, 23, DAY_OF_MONTH, 1, 31},
            {2012, 9, 23, DAY_OF_MONTH, 1, 30},
            {2012, 10, 23, DAY_OF_MONTH, 1, 31},
            {2012, 11, 23, DAY_OF_MONTH, 1, 30},
            {2012, 12, 23, DAY_OF_MONTH, 1, 31},
            {2011, 2, 23, DAY_OF_MONTH, 1, 28},

            {1700, 1, 23, DAY_OF_YEAR, 1, 366},
            {1751, 1, 23, DAY_OF_YEAR, 1, 365},
            {1752, 1, 23, DAY_OF_YEAR, 1, 355},
            {1753, 1, 23, DAY_OF_YEAR, 1, 365},
            {2012, 1, 23, DAY_OF_YEAR, 1, 366},
            {2011, 2, 23, DAY_OF_YEAR, 1, 365},

            {1752, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 2, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 3, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 4, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 5, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 6, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 7, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 8, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 9, 23, ALIGNED_WEEK_OF_MONTH, 1, 3},
            {1752, 10, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 11, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {1752, 12, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {2012, 1, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {2012, 2, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {2012, 3, 23, ALIGNED_WEEK_OF_MONTH, 1, 5},
            {2011, 2, 23, ALIGNED_WEEK_OF_MONTH, 1, 4},

            {1752, 12, 23, ALIGNED_WEEK_OF_YEAR, 1, 51},
            {2011, 2, 23, ALIGNED_WEEK_OF_YEAR, 1, 53},
            {2012, 2, 23, ALIGNED_WEEK_OF_YEAR, 1, 53},
        };
    }

    @Test(dataProvider = "ranges")
    public void test_range(int year, int month, int dom, TemporalField field, int expectedMin, int expectedMax) {
        assertEquals(CutoverDate.of(BRITISH, year, month, dom).range(field), ValueRange.of(expectedMin, expectedMax));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_unsupported() {
        CutoverDate.of(BRITISH, 2012, 6, 30).range(MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // CutoverDate.getLong
    //-----------------------------------------------------------------------
    @DataProvider(name = "getLong")
    Object[][] data_getLong() {
        return new Object[][] {
            {1752, 5, 26, DAY_OF_WEEK, 2},
            {1752, 5, 26, DAY_OF_MONTH, 26},
            {1752, 5, 26, DAY_OF_YEAR, 31 + 29 + 31 + 30 + 26},
            {1752, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5},
            {1752, 5, 26, ALIGNED_WEEK_OF_MONTH, 4},
            {1752, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 7},
            {1752, 5, 26, ALIGNED_WEEK_OF_YEAR, 21},
            {1752, 5, 26, MONTH_OF_YEAR, 5},

            {1752, 9, 2, DAY_OF_WEEK, 3},
            {1752, 9, 2, DAY_OF_MONTH, 2},
            {1752, 9, 2, DAY_OF_YEAR, 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 2},
            {1752, 9, 2, ALIGNED_DAY_OF_WEEK_IN_MONTH, 2},
            {1752, 9, 2, ALIGNED_WEEK_OF_MONTH, 1},
            {1752, 9, 2, ALIGNED_DAY_OF_WEEK_IN_YEAR, 1},
            {1752, 9, 2, ALIGNED_WEEK_OF_YEAR, 36},
            {1752, 9, 2, MONTH_OF_YEAR, 9},

            {1752, 9, 14, DAY_OF_WEEK, 4},
            {1752, 9, 14, DAY_OF_MONTH, 14},
            {1752, 9, 14, DAY_OF_YEAR, 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 3},
            {1752, 9, 14, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3},
            {1752, 9, 14, ALIGNED_WEEK_OF_MONTH, 1},
            {1752, 9, 14, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2},
            {1752, 9, 14, ALIGNED_WEEK_OF_YEAR, 36},
            {1752, 9, 14, MONTH_OF_YEAR, 9},

            {2014, 5, 26, DAY_OF_WEEK, 1},
            {2014, 5, 26, DAY_OF_MONTH, 26},
            {2014, 5, 26, DAY_OF_YEAR, 31 + 28 + 31 + 30 + 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 6},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 21},
            {2014, 5, 26, MONTH_OF_YEAR, 5},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 12 + 5 - 1},
            {2014, 5, 26, YEAR, 2014},
            {2014, 5, 26, ERA, 1},
            {1, 6, 8, ERA, 1},
            {0, 6, 8, ERA, 0},

            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 1},
        };
    }

    @Test(dataProvider = "getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(CutoverDate.of(BRITISH, year, month, dom).getLong(field), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_unsupported() {
        CutoverDate.of(BRITISH, 2012, 6, 30).getLong(MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // CutoverDate.with
    //-----------------------------------------------------------------------
    @DataProvider(name = "with")
    Object[][] data_with() {
        return new Object[][] {
            {1752, 9, 2, DAY_OF_WEEK, 1, 1752, 8, 31},
            {1752, 9, 2, DAY_OF_WEEK, 4, 1752, 9, 14},
            {1752, 9, 2, DAY_OF_MONTH, 1, 1752, 9, 1},
            {1752, 9, 2, DAY_OF_MONTH, 3, 1752, 9, 14},  // lenient
            {1752, 9, 2, DAY_OF_MONTH, 13, 1752, 9, 24},  // lenient
            {1752, 9, 2, DAY_OF_MONTH, 14, 1752, 9, 14},
            {1752, 9, 2, DAY_OF_MONTH, 30, 1752, 9, 30},
            {1752, 9, 2, DAY_OF_YEAR, 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 1, 1752, 9, 1},
            {1752, 9, 2, DAY_OF_YEAR, 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 3, 1752, 9, 14},
            {1752, 9, 2, DAY_OF_YEAR, 356, 1753, 1, 1},  // lenient
            {1752, 9, 2, DAY_OF_YEAR, 366, 1753, 1, 11},  // lenient
            {1752, 9, 2, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1, 1752, 9, 1},
            {1752, 9, 2, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 1752, 9, 14},
            {1752, 9, 2, ALIGNED_WEEK_OF_MONTH, 2, 1752, 9, 20},
            {1752, 9, 2, ALIGNED_WEEK_OF_MONTH, 3, 1752, 9, 27},
            {1752, 9, 2, ALIGNED_WEEK_OF_MONTH, 4, 1752, 10, 4},  // lenient
            {1752, 9, 2, ALIGNED_WEEK_OF_MONTH, 5, 1752, 10, 11},  // lenient
            {1752, 9, 2, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 1752, 9, 14},
            {1752, 9, 2, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, 1752, 9, 15},
            {1752, 9, 2, ALIGNED_WEEK_OF_YEAR, 1, 1752, 1, 1},
            {1752, 9, 2, ALIGNED_WEEK_OF_YEAR, 35, 1752, 8, 26},
            {1752, 9, 2, ALIGNED_WEEK_OF_YEAR, 37, 1752, 9, 20},
            {1752, 9, 2, ALIGNED_WEEK_OF_YEAR, 51, 1752, 12, 27},
            {1752, 9, 2, ALIGNED_WEEK_OF_YEAR, 52, 1753, 1, 3},  // lenient
            {1752, 9, 2, MONTH_OF_YEAR, 8, 1752, 8, 2},
            {1752, 9, 2, MONTH_OF_YEAR, 10, 1752, 10, 2},

            {1752, 9, 14, DAY_OF_WEEK, 1, 1752, 8, 31},
            {1752, 9, 14, DAY_OF_WEEK, 3, 1752, 9, 2},
            {1752, 9, 14, DAY_OF_MONTH, 1, 1752, 9, 1},
            {1752, 9, 14, DAY_OF_MONTH, 2, 1752, 9, 2},
            {1752, 9, 14, DAY_OF_MONTH, 3, 1752, 9, 14},  // lenient
            {1752, 9, 14, DAY_OF_MONTH, 30, 1752, 9, 30},
            {1752, 9, 14, DAY_OF_YEAR, 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 1, 1752, 9, 1},
            {1752, 9, 14, DAY_OF_YEAR, 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 2, 1752, 9, 2},
            {1752, 9, 14, DAY_OF_YEAR, 356, 1753, 1, 1},  // lenient
            {1752, 9, 14, DAY_OF_YEAR, 366, 1753, 1, 11},  // lenient
            {1752, 9, 14, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1, 1752, 9, 1},
            {1752, 9, 14, ALIGNED_DAY_OF_WEEK_IN_MONTH, 2, 1752, 9, 2},
            {1752, 9, 14, ALIGNED_WEEK_OF_MONTH, 2, 1752, 9, 21},
            {1752, 9, 14, ALIGNED_WEEK_OF_MONTH, 3, 1752, 9, 28},
            {1752, 9, 14, ALIGNED_WEEK_OF_MONTH, 4, 1752, 10, 5},  // lenient
            {1752, 9, 14, ALIGNED_WEEK_OF_MONTH, 5, 1752, 10, 12},  // lenient
            {1752, 9, 14, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 1752, 9, 14},
            {1752, 9, 14, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, 1752, 9, 15},
            {1752, 9, 14, ALIGNED_WEEK_OF_YEAR, 1, 1752, 1, 2},
            {1752, 9, 14, ALIGNED_WEEK_OF_YEAR, 35, 1752, 8, 27},
            {1752, 9, 14, ALIGNED_WEEK_OF_YEAR, 37, 1752, 9, 21},
            {1752, 9, 14, ALIGNED_WEEK_OF_YEAR, 51, 1752, 12, 28},
            {1752, 9, 14, ALIGNED_WEEK_OF_YEAR, 52, 1753, 1, 4},  // lenient
            {1752, 9, 14, MONTH_OF_YEAR, 8, 1752, 8, 14},
            {1752, 9, 14, MONTH_OF_YEAR, 10, 1752, 10, 14},

            // into cutover zone
            {1752, 8, 4, MONTH_OF_YEAR, 9, 1752, 9, 15},  // lenient
            {1752, 10, 8, MONTH_OF_YEAR, 9, 1752, 9, 19},  // lenient
            {1751, 9, 4, YEAR, 1752, 1752, 9, 15},  // lenient
            {1753, 9, 8, YEAR, 1752, 1752, 9, 19},  // lenient
            {1751, 9, 4, YEAR_OF_ERA, 1752, 1752, 9, 15},  // lenient
            {1753, 9, 8, YEAR_OF_ERA, 1752, 1752, 9, 19},  // lenient

            {2014, 5, 26, DAY_OF_WEEK, 3, 2014, 5, 28},
            {2014, 5, 26, DAY_OF_WEEK, 7, 2014, 6, 1},
            {2014, 5, 26, DAY_OF_MONTH, 31, 2014, 5, 31},
            {2014, 5, 26, DAY_OF_MONTH, 26, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_YEAR, 365, 2014, 12, 31},
            {2014, 5, 26, DAY_OF_YEAR, 146, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 5, 24},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 1, 2014, 5, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2014, 5, 22},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 6, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 23, 2014, 6, 9},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 21, 2014, 5, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 7, 2014, 7, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 5, 2014, 5, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2013 * 12 + 3 - 1, 2013, 3, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 12 + 5 - 1, 2014, 5, 26},
            {2014, 5, 26, YEAR, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR, 2014, 2014, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2014, 2014, 5, 26},
            {2014, 5, 26, ERA, 0, -2013, 5, 26},
            {2014, 5, 26, ERA, 1, 2014, 5, 26},

            {2011, 3, 31, MONTH_OF_YEAR, 2, 2011, 2, 28},
            {2012, 3, 31, MONTH_OF_YEAR, 2, 2012, 2, 29},
            {2012, 3, 31, MONTH_OF_YEAR, 6, 2012, 6, 30},
            {2012, 2, 29, YEAR, 2011, 2011, 2, 28},
            {-2013, 6, 8, YEAR_OF_ERA, 2012, -2011, 6, 8},
            {2014, 5, 26, WeekFields.ISO.dayOfWeek(), 2, 2014, 5, 27},
        };
    }

    @Test(dataProvider = "with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(CutoverDate.of(BRITISH, year, month, dom).with(field, value), CutoverDate.of(BRITISH, expectedYear, expectedMonth, expectedDom));
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_with_TemporalField_unsupported() {
        CutoverDate.of(BRITISH, 2012, 6, 30).with(MINUTE_OF_DAY, 0);
    }

    //-----------------------------------------------------------------------
    // CutoverDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @DataProvider(name = "withLastDayOfMonth")
    Object[][] data_lastDayOfMonth() {
        return new Object[][] {
            {CutoverDate.of(BRITISH, 1752, 2, 23), CutoverDate.of(BRITISH, 1752, 2, 29)},
            {CutoverDate.of(BRITISH, 1752, 6, 23), CutoverDate.of(BRITISH, 1752, 6, 30)},
            {CutoverDate.of(BRITISH, 1752, 9, 2), CutoverDate.of(BRITISH, 1752, 9, 30)},
            {CutoverDate.of(BRITISH, 1752, 9, 14), CutoverDate.of(BRITISH, 1752, 9, 30)},
            {CutoverDate.of(BRITISH, 2012, 2, 23), CutoverDate.of(BRITISH, 2012, 2, 29)},
            {CutoverDate.of(BRITISH, 2012, 6, 23), CutoverDate.of(BRITISH, 2012, 6, 30)},
        };
    }

    @Test(dataProvider = "withLastDayOfMonth")
    public void test_adjust_lastDayOfMonth(CutoverDate input, CutoverDate expected) {
        CutoverDate test = input.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, expected);
    }

    //-----------------------------------------------------------------------
    // CutoverDate.with(Local*)
    //-----------------------------------------------------------------------
    @DataProvider(name = "withLocalDate")
    Object[][] data_withLocalDate() {
        return new Object[][] {
            {CutoverDate.of(BRITISH, 1752, 9, 2), LocalDate.of(1752, 9, 12), CutoverDate.of(BRITISH, 1752, 9, 1)},
            {CutoverDate.of(BRITISH, 1752, 9, 14), LocalDate.of(1752, 9, 12), CutoverDate.of(BRITISH, 1752, 9, 1)},
            {CutoverDate.of(BRITISH, 1752, 9, 2), LocalDate.of(1752, 9, 14), CutoverDate.of(BRITISH, 1752, 9, 14)},
            {CutoverDate.of(BRITISH, 1752, 9, 15), LocalDate.of(1752, 9, 14), CutoverDate.of(BRITISH, 1752, 9, 14)},
            {CutoverDate.of(BRITISH, 2012, 2, 23), LocalDate.of(2012, 2, 23), CutoverDate.of(BRITISH, 2012, 2, 23)},
        };
    }

    @Test(dataProvider = "withLocalDate")
    public void test_adjust_LocalDate(CutoverDate input, LocalDate local, CutoverDate expected) {
        CutoverDate test = input.with(local);
        assertEquals(test, expected);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_adjust_toMonth() {
        CutoverDate cutover = CutoverDate.of(BRITISH, 2000, 1, 4);
        cutover.with(Month.APRIL);
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(CutoverDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_withCutoverDate() {
        CutoverDate cutover = CutoverDate.of(BRITISH, 2012, 6, 23);
        LocalDate test = LocalDate.MIN.with(cutover);
        assertEquals(test, LocalDate.of(2012, 6, 23));
    }

    @Test
    public void test_LocalDateTime_withCutoverDate() {
        CutoverDate cutover = CutoverDate.of(BRITISH, 2012, 6, 23);
        LocalDateTime test = LocalDateTime.MIN.with(cutover);
        assertEquals(test, LocalDateTime.of(2012, 6, 23, 0, 0));
    }

    //-----------------------------------------------------------------------
    // CutoverDate.plus
    //-----------------------------------------------------------------------
    @DataProvider(name = "plus")
    Object[][] data_plus() {
        return new Object[][] {
            {1752, 9, 2, -1, DAYS, 1752, 9, 1, true},
            {1752, 9, 2, 0, DAYS, 1752, 9, 2, true},
            {1752, 9, 2, 1, DAYS, 1752, 9, 14, true},
            {1752, 9, 2, 2, DAYS, 1752, 9, 15, true},
            {1752, 9, 14, -1, DAYS, 1752, 9, 2, true},
            {1752, 9, 14, 0, DAYS, 1752, 9, 14, true},
            {1752, 9, 14, 1, DAYS, 1752, 9, 15, true},
            {2014, 5, 26, 0, DAYS, 2014, 5, 26, true},
            {2014, 5, 26, 8, DAYS, 2014, 6, 3, true},
            {2014, 5, 26, -3, DAYS, 2014, 5, 23, true},

            {1752, 9, 2, -1, WEEKS, 1752, 8, 26, true},
            {1752, 9, 2, 0, WEEKS, 1752, 9, 2, true},
            {1752, 9, 2, 1, WEEKS, 1752, 9, 20, true},
            {1752, 9, 14, -1, WEEKS, 1752, 8, 27, true},
            {1752, 9, 14, 0, WEEKS, 1752, 9, 14, true},
            {1752, 9, 14, 1, WEEKS, 1752, 9, 21, true},
            {2014, 5, 26, 0, WEEKS, 2014, 5, 26, true},
            {2014, 5, 26, 3, WEEKS, 2014, 6, 16, true},
            {2014, 5, 26, -5, WEEKS, 2014, 4, 21, true},

            {1752, 9, 2, -1, MONTHS, 1752, 8, 2, true},
            {1752, 9, 2, 0, MONTHS, 1752, 9, 2, true},
            {1752, 9, 2, 1, MONTHS, 1752, 10, 2, true},
            {1752, 9, 14, -1, MONTHS, 1752, 8, 14, true},
            {1752, 9, 14, 0, MONTHS, 1752, 9, 14, true},
            {1752, 9, 14, 1, MONTHS, 1752, 10, 14, true},
            {1752, 8, 12, 1, MONTHS, 1752, 9, 23, false},
            {1752, 10, 12, -1, MONTHS, 1752, 9, 23, false},
            {2014, 5, 26, 0, MONTHS, 2014, 5, 26, true},
            {2014, 5, 26, 3, MONTHS, 2014, 8, 26, true},
            {2014, 5, 26, -5, MONTHS, 2013, 12, 26, true},

            {2014, 5, 26, 0, YEARS, 2014, 5, 26, true},
            {2014, 5, 26, 3, YEARS, 2017, 5, 26, true},
            {2014, 5, 26, -5, YEARS, 2009, 5, 26, true},
            {2014, 5, 26, 0, DECADES, 2014, 5, 26, true},
            {2014, 5, 26, 3, DECADES, 2044, 5, 26, true},
            {2014, 5, 26, -5, DECADES, 1964, 5, 26, true},
            {2014, 5, 26, 0, CENTURIES, 2014, 5, 26, true},
            {2014, 5, 26, 3, CENTURIES, 2314, 5, 26, true},
            {2014, 5, 26, -5, CENTURIES, 1514, 5, 26, true},
            {2014, 5, 26, 0, MILLENNIA, 2014, 5, 26, true},
            {2014, 5, 26, 3, MILLENNIA, 5014, 5, 26, true},
            {2014, 5, 26, -5, MILLENNIA, 2014 - 5000, 5, 26, true},
            {2014, 5, 26, -1, ERAS, -2013, 5, 26, true},
        };
    }

    @Test(dataProvider = "plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom, boolean bidi) {
        assertEquals(CutoverDate.of(BRITISH, year, month, dom).plus(amount, unit), CutoverDate.of(BRITISH, expectedYear, expectedMonth, expectedDom));
    }

    @Test(dataProvider = "plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom, boolean bidi) {
        if (bidi) {
            assertEquals(CutoverDate.of(BRITISH, year, month, dom).minus(amount, unit), CutoverDate.of(BRITISH, expectedYear, expectedMonth, expectedDom));
        }
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_plus_TemporalUnit_unsupported() {
        CutoverDate.of(BRITISH, 2012, 6, 30).plus(0, MINUTES);
    }

    //-----------------------------------------------------------------------
    // CutoverDate.until
    //-----------------------------------------------------------------------
    @DataProvider(name = "until")
    Object[][] data_until() {
        return new Object[][] {
            {1752, 9, 1, 1752, 9, 2, DAYS, 1},
            {1752, 9, 1, 1752, 9, 14, DAYS, 2},
            {1752, 9, 2, 1752, 9, 14, DAYS, 1},
            {1752, 9, 2, 1752, 9, 15, DAYS, 2},
            {1752, 9, 14, 1752, 9, 1, DAYS, -2},
            {1752, 9, 14, 1752, 9, 2, DAYS, -1},
            {2014, 5, 26, 2014, 5, 26, DAYS, 0},
            {2014, 5, 26, 2014, 6, 1, DAYS, 6},
            {2014, 5, 26, 2014, 5, 20, DAYS, -6},

            {1752, 9, 1, 1752, 9, 14, WEEKS, 0},
            {1752, 9, 1, 1752, 9, 18, WEEKS, 0},
            {1752, 9, 1, 1752, 9, 19, WEEKS, 1},
            {1752, 9, 2, 1752, 9, 14, WEEKS, 0},
            {1752, 9, 2, 1752, 9, 19, WEEKS, 0},
            {1752, 9, 2, 1752, 9, 20, WEEKS, 1},
            {2014, 5, 26, 2014, 5, 26, WEEKS, 0},
            {2014, 5, 26, 2014, 6, 1, WEEKS, 0},
            {2014, 5, 26, 2014, 6, 2, WEEKS, 1},

            {1752, 9, 1, 1752, 9, 14, MONTHS, 0},
            {1752, 9, 1, 1752, 9, 30, MONTHS, 0},
            {1752, 9, 1, 1752, 10, 1, MONTHS, 1},
            {1752, 9, 2, 1752, 9, 14, MONTHS, 0},
            {1752, 9, 2, 1752, 10, 1, MONTHS, 0},
            {1752, 9, 2, 1752, 10, 2, MONTHS, 1},
            {1752, 9, 14, 1752, 9, 15, MONTHS, 0},
            {1752, 9, 14, 1752, 10, 13, MONTHS, 0},
            {1752, 9, 14, 1752, 10, 14, MONTHS, 1},
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
        };
    }

    @Test(dataProvider = "until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        CutoverDate start = CutoverDate.of(BRITISH, year1, month1, dom1);
        CutoverDate end = CutoverDate.of(BRITISH, year2, month2, dom2);
        assertEquals(start.until(end, unit), expected);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_until_TemporalUnit_unsupported() {
        CutoverDate start = CutoverDate.of(BRITISH, 2012, 6, 30);
        CutoverDate end = CutoverDate.of(BRITISH, 2012, 7, 1);
        start.until(end, MINUTES);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(
            CutoverDate.of(BRITISH, 1752, 9, 2).plus(BRITISH.period(0, 1, 3)),
            CutoverDate.of(BRITISH, 1752, 10, 5));
        assertEquals(
            CutoverDate.of(BRITISH, 1752, 8, 12).plus(BRITISH.period(0, 1, 0)),
            CutoverDate.of(BRITISH, 1752, 9, 23));
        assertEquals(
            CutoverDate.of(BRITISH, 2014, 5, 26).plus(BRITISH.period(0, 2, 3)),
            CutoverDate.of(BRITISH, 2014, 7, 29));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_Period_ISO() {
        assertEquals(
            CutoverDate.of(BRITISH, 2014, 5, 26).plus(Period.ofMonths(2)),
            CutoverDate.of(BRITISH, 2014, 7, 26));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(
            CutoverDate.of(BRITISH, 1752, 10, 12).minus(BRITISH.period(0, 1, 0)),
            CutoverDate.of(BRITISH, 1752, 9, 23));
        assertEquals(
            CutoverDate.of(BRITISH, 2014, 5, 26).minus(BRITISH.period(0, 2, 3)),
            CutoverDate.of(BRITISH, 2014, 3, 23));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_Period_ISO() {
        assertEquals(
            CutoverDate.of(BRITISH, 2014, 5, 26).minus(Period.ofMonths(2)),
            CutoverDate.of(BRITISH, 2014, 3, 26));
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "untilCLD")
    Object[][] data_untilCLD() {
        return new Object[][] {
            {1752, 7, 2, 1752, 7, 1, 0, 0, -1},
            {1752, 7, 2, 1752, 7, 2, 0, 0, 0},
            {1752, 7, 2, 1752, 9, 1, 0, 1, 30},  // 30 days after 1752-08-02
            {1752, 7, 2, 1752, 9, 2, 0, 2, 0},  // 2 whole months
            {1752, 7, 2, 1752, 9, 14, 0, 2, 1},  // 1 day after 1752-09-02
            {1752, 7, 2, 1752, 9, 30, 0, 2, 17},  // 17 days after 1752-09-02
            {1752, 7, 2, 1752, 10, 1, 0, 2, 18},  // 18 days after 1752-09-02
            {1752, 7, 2, 1752, 10, 2, 0, 3, 0},  // 3 whole months
            {1752, 7, 2, 1752, 10, 3, 0, 3, 1},
            {1752, 7, 2, 1752, 10, 30, 0, 3, 28},
            {1752, 7, 2, 1752, 11, 1, 0, 3, 30},
            {1752, 7, 2, 1752, 11, 2, 0, 4, 0},

            {1752, 7, 3, 1752, 9, 2, 0, 1, 30},  // 30 days after 1752-08-03
            {1752, 7, 3, 1752, 9, 14, 0, 2, 0},  // 2 months
            {1752, 7, 3, 1752, 9, 15, 0, 2, 1},  // 1 day after 1752-09-03 (1752-09-14)
            {1752, 7, 3, 1752, 9, 30, 0, 2, 16},  // 16 days after 1752-09-03 (1752-09-14)
            {1752, 7, 3, 1752, 10, 1, 0, 2, 17},  // 17 days after 1752-09-03 (1752-09-14)
            {1752, 7, 3, 1752, 10, 3, 0, 3, 0},
            {1752, 7, 3, 1752, 10, 4, 0, 3, 1},

            {1752, 7, 4, 1752, 9, 2, 0, 1, 29},  // 29 days after 1752-08-04
            {1752, 7, 4, 1752, 9, 14, 0, 1, 30},  // 30 days after 1752-08-04
            {1752, 7, 4, 1752, 9, 15, 0, 2, 0},  // 2 months
            {1752, 7, 4, 1752, 9, 30, 0, 2, 15},  // 15 days after 1752-09-04 (1752-09-15)
            {1752, 7, 4, 1752, 10, 1, 0, 2, 16},  // 16 days after 1752-09-04 (1752-09-15)
            {1752, 7, 4, 1752, 10, 4, 0, 3, 0},
            {1752, 7, 4, 1752, 10, 5, 0, 3, 1},

            {1752, 7, 13, 1752, 9, 2, 0, 1, 20},  // 20 days after 1752-08-13
            {1752, 7, 13, 1752, 9, 14, 0, 1, 21},  // 21 days after 752-08-13
            {1752, 7, 13, 1752, 9, 24, 0, 2, 0},  // 2 months
            {1752, 7, 13, 1752, 9, 30, 0, 2, 6},  // 6 days after 1752-09-13 (1752-09-24)
            {1752, 7, 13, 1752, 10, 1, 0, 2, 7},  // 7 days after 1752-09-13 (1752-09-24)
            {1752, 7, 13, 1752, 10, 12, 0, 2, 18},  // 18 days after 1752-09-13 (1752-09-24)
            {1752, 7, 13, 1752, 10, 13, 0, 3, 0},
            {1752, 7, 13, 1752, 10, 14, 0, 3, 1},

            {1752, 7, 14, 1752, 9, 2, 0, 1, 19},  // 19 days after 1752-08-14
            {1752, 7, 14, 1752, 9, 14, 0, 2, 0},  // 2 months
            {1752, 7, 14, 1752, 9, 15, 0, 2, 1},  // 1 day after 1752-09-14
            {1752, 7, 14, 1752, 9, 30, 0, 2, 16},  // 16 days after 1752-09-14
            {1752, 7, 14, 1752, 10, 1, 0, 2, 17},  // 17 days after 1752-09-14
            {1752, 7, 14, 1752, 10, 13, 0, 2, 29},  // 29 days after 1752-09-14
            {1752, 7, 14, 1752, 10, 14, 0, 3, 0},
            {1752, 7, 14, 1752, 10, 15, 0, 3, 1},

            {1752, 8, 2, 1752, 9, 2, 0, 1, 0},
            {1752, 8, 2, 1752, 9, 14, 0, 1, 1},  // 1 day after 1752-09-02
            {1752, 8, 2, 1752, 9, 30, 0, 1, 17},  // 17 days after 1752-09-02
            {1752, 8, 2, 1752, 10, 1, 0, 1, 18},  // 18 days after 1752-09-02
            {1752, 8, 2, 1752, 10, 2, 0, 2, 0},
            {1752, 8, 2, 1752, 10, 3, 0, 2, 1},
            {1752, 8, 2, 1752, 10, 30, 0, 2, 28},

            {1752, 8, 16, 1752, 9, 2, 0, 0, 17},
            {1752, 8, 16, 1752, 9, 14, 0, 0, 18},
            {1752, 8, 16, 1752, 9, 15, 0, 0, 19},
            {1752, 8, 16, 1752, 9, 16, 0, 1, 0},
            {1752, 8, 16, 1752, 10, 2, 0, 1, 16},
            {1752, 8, 16, 1752, 10, 15, 0, 1, 29},
            {1752, 8, 16, 1752, 10, 16, 0, 2, 0},
            {1752, 8, 16, 1752, 10, 17, 0, 2, 1},

            {1752, 9, 1, 1752, 8, 31, 0, 0, -1},
            {1752, 9, 1, 1752, 9, 1, 0, 0, 0},
            {1752, 9, 1, 1752, 9, 2, 0, 0, 1},
            {1752, 9, 1, 1752, 9, 14, 0, 0, 2},
            {1752, 9, 1, 1752, 9, 15, 0, 0, 3},
            {1752, 9, 1, 1752, 9, 30, 0, 0, 18},
            {1752, 9, 1, 1752, 10, 1, 0, 1, 0},
            {1752, 9, 1, 1752, 10, 2, 0, 1, 1},

            {1752, 9, 2, 1752, 8, 31, 0, 0, -2},
            {1752, 9, 2, 1752, 9, 1, 0, 0, -1},
            {1752, 9, 2, 1752, 9, 2, 0, 0, 0},
            {1752, 9, 2, 1752, 9, 14, 0, 0, 1},
            {1752, 9, 2, 1752, 9, 30, 0, 0, 17},
            {1752, 9, 2, 1752, 10, 1, 0, 0, 18},
            {1752, 9, 2, 1752, 10, 2, 0, 1, 0},
            {1752, 9, 2, 1752, 10, 3, 0, 1, 1},
            {1752, 9, 2, 1752, 11, 1, 0, 1, 30},
            {1752, 9, 2, 1752, 11, 2, 0, 2, 0},
            {1752, 9, 2, 1752, 11, 3, 0, 2, 1},

            {1752, 9, 14, 1752, 7, 13, 0, -2, -1},
            {1752, 9, 14, 1752, 7, 14, 0, -2, 0},
            {1752, 9, 14, 1752, 8, 13, 0, -1, -1},
            {1752, 9, 14, 1752, 8, 14, 0, -1, 0},
            {1752, 9, 14, 1752, 8, 15, 0, 0, -19},  // 19 days before
            {1752, 9, 14, 1752, 8, 31, 0, 0, -3},  // 3 days before
            {1752, 9, 14, 1752, 9, 1, 0, 0, -2},  // 2 days before
            {1752, 9, 14, 1752, 9, 2, 0, 0, -1},  // 1 day before
            {1752, 9, 14, 1752, 9, 14, 0, 0, 0},
            {1752, 9, 14, 1752, 9, 15, 0, 0, 1},
            {1752, 9, 14, 1752, 9, 30, 0, 0, 16},
            {1752, 9, 14, 1752, 10, 13, 0, 0, 29},
            {1752, 9, 14, 1752, 10, 14, 0, 1, 0},
            {1752, 9, 14, 1752, 10, 15, 0, 1, 1},
            
            {1752, 9, 24, 1752, 7, 23, 0, -2, -1},
            {1752, 9, 24, 1752, 7, 24, 0, -2, 0},
            {1752, 9, 24, 1752, 8, 23, 0, -1, -1},
            {1752, 9, 24, 1752, 8, 24, 0, -1, 0},
            {1752, 9, 24, 1752, 8, 25, 0, 0, -19},  // 19 days before
            {1752, 9, 24, 1752, 8, 31, 0, 0, -13},  // 13 days before
            {1752, 9, 24, 1752, 9, 1, 0, 0, -12},  // 12 days before
            {1752, 9, 24, 1752, 9, 2, 0, 0, -11},  // 11 days before
            {1752, 9, 24, 1752, 9, 14, 0, 0, -10},  // 10 days before
            {1752, 9, 24, 1752, 9, 23, 0, 0, -1},  // 1 day before
            {1752, 9, 24, 1752, 9, 24, 0, 0, 0},
            {1752, 9, 24, 1752, 9, 25, 0, 0, 1},
            {1752, 9, 24, 1752, 9, 30, 0, 0, 6},
            {1752, 9, 24, 1752, 10, 23, 0, 0, 29},
            {1752, 9, 24, 1752, 10, 24, 0, 1, 0},
            {1752, 9, 24, 1752, 10, 25, 0, 1, 1},
            
            {1752, 10, 3, 1752, 10, 1, 0, 0, -2},
            {1752, 10, 3, 1752, 9, 30, 0, 0, -3},
            {1752, 10, 3, 1752, 9, 16, 0, 0, -17},
            {1752, 10, 3, 1752, 9, 15, 0, 0, -18},
            {1752, 10, 3, 1752, 9, 14, 0, 0, -19},
            {1752, 10, 3, 1752, 9, 2, 0, -1, -1},
            {1752, 10, 3, 1752, 9, 1, 0, -1, -2},
            {1752, 10, 3, 1752, 8, 31, 0, -1, -3},
            {1752, 10, 3, 1752, 8, 4, 0, -1, -30},
            {1752, 10, 3, 1752, 8, 3, 0, -2, 0},
            {1752, 10, 3, 1752, 8, 2, 0, -2, -1},
            
            {1752, 10, 4, 1752, 10, 1, 0, 0, -3},
            {1752, 10, 4, 1752, 9, 30, 0, 0, -4},
            {1752, 10, 4, 1752, 9, 16, 0, 0, -18},
            {1752, 10, 4, 1752, 9, 15, 0, 0, -19},
            {1752, 10, 4, 1752, 9, 14, 0, 0, -20},
            {1752, 10, 4, 1752, 9, 2, 0, -1, -2},
            {1752, 10, 4, 1752, 9, 1, 0, -1, -3},
            {1752, 10, 4, 1752, 8, 31, 0, -1, -4},
            {1752, 10, 4, 1752, 8, 5, 0, -1, -30},
            {1752, 10, 4, 1752, 8, 4, 0, -2, 0},
            {1752, 10, 4, 1752, 8, 3, 0, -2, -1},
        };
    }

    @Test(dataProvider = "untilCLD")
    public void test_until_CLD(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            int expectedYears, int expectedMonths, int expectedDays) {
        CutoverDate a = CutoverDate.of(BRITISH, year1, month1, dom1);
        CutoverDate b = CutoverDate.of(BRITISH, year2, month2, dom2);
        ChronoPeriod c = a.until(b);
        assertEquals(
            c,
            BRITISH.period(expectedYears, expectedMonths, expectedDays));
    }

    @Test(dataProvider = "untilCLD")
    public void test_until_CLD_plus(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            int expectedYears, int expectedMonths, int expectedDays) {
        CutoverDate a = CutoverDate.of(BRITISH, year1, month1, dom1);
        CutoverDate b = CutoverDate.of(BRITISH, year2, month2, dom2);
        ChronoPeriod c = a.until(b);
        assertEquals(a.plus(c), b);
    }

    //-------------------------------------------------------------------------
    // atTime(LocalTime)
    //-------------------------------------------------------------------------
    @Test(dataProvider = "cutovers")
    public void test_atTime(CutoverChronology chrono) {
        CutoverDate date = CutoverDate.of(chrono, 2014, 10, 12);
        ChronoLocalDateTime<CutoverDate> test = date.atTime(LocalTime.of(12, 30));
        assertEquals(test.toLocalDate(), date);
        assertEquals(test.toLocalTime(), LocalTime.of(12, 30));
        ChronoLocalDateTime<CutoverDate> test2 = chrono.localDateTime(LocalDateTime.from(test));
        assertEquals(test2, test);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_atTime_null() {
        CutoverDate.of(BRITISH, 2014, 5, 26).atTime(null);
    }

    //-----------------------------------------------------------------------
    // check against GregorianCalendar
    //-----------------------------------------------------------------------
    @Test
    void test_crossCheck_British() {
        CutoverDate test = CutoverDate.of(BRITISH, 1700, 1, 1);
        CutoverDate end = CutoverDate.of(BRITISH, 1800, 1, 1);
        Instant cutover = ZonedDateTime.of(1752, 9, 14, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        gcal.setGregorianChange(Date.from(cutover));
        gcal.clear();
        gcal.set(1700, Calendar.JANUARY, 1);
        while (test.isBefore(end)) {
            assertEquals(test.get(YEAR_OF_ERA), gcal.get(Calendar.YEAR));
            assertEquals(test.get(MONTH_OF_YEAR), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.get(DAY_OF_MONTH), gcal.get(Calendar.DAY_OF_MONTH));
            assertEquals(LocalDate.from(test), gcal.toZonedDateTime().toLocalDate());
            gcal.add(Calendar.DAY_OF_MONTH, 1);
            test = test.plus(1, DAYS);
        }
    }

    @Test
    void test_crossCheck_Vatican() {
        CutoverDate test = CutoverDate.of(VATICAN, 1500, 1, 1);
        CutoverDate end = CutoverDate.of(VATICAN, 1600, 1, 1);
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        gcal.clear();
        gcal.set(1500, Calendar.JANUARY, 1);
        while (test.isBefore(end)) {
            assertEquals(test.get(YEAR_OF_ERA), gcal.get(Calendar.YEAR));
            assertEquals(test.get(MONTH_OF_YEAR), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.get(DAY_OF_MONTH), gcal.get(Calendar.DAY_OF_MONTH));
            assertEquals(LocalDate.from(test), gcal.toZonedDateTime().toLocalDate());
            gcal.add(Calendar.DAY_OF_MONTH, 1);
            test = test.plus(1, DAYS);
        }
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider = "cutovers")
    void test_equals(CutoverChronology chrono) {
        CutoverDate a1 = CutoverDate.of(chrono, 2000, 1, 3);
        CutoverDate a2 = CutoverDate.of(chrono, 2000, 1, 3);
        CutoverDate b = CutoverDate.of(chrono, 2000, 1, 4);
        CutoverDate c = CutoverDate.of(chrono, 2000, 2, 3);
        CutoverDate d = CutoverDate.of(chrono, 2001, 1, 3);

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
            {CutoverDate.of(BRITISH, 1, 1, 1), "Cutover[1752-09-14] AD 1-01-01"},
            {CutoverDate.of(BRITISH, 2012, 6, 23), "Cutover[1752-09-14] AD 2012-06-23"},
            {CutoverDate.of(VATICAN, 1, 1, 1), "Cutover[1582-10-15] AD 1-01-01"},
            {CutoverDate.of(VATICAN, 2012, 6, 23), "Cutover[1582-10-15] AD 2012-06-23"},
        };
    }

    @Test(dataProvider = "toString")
    public void test_toString(CutoverDate cutover, String expected) {
        assertEquals(cutover.toString(), expected);
    }

}
