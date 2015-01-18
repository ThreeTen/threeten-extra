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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

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
import static java.time.temporal.ChronoUnit.MILLENNIA;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;

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
import java.util.function.Predicate;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
@SuppressWarnings({"static-method", "javadoc"})
public class TestInternationalFixedChronology {

    // -----------------------------------------------------------------------
    // Chronology.of(String)
    // Chronology.getId
    // Chronology.getCalendarType
    // -----------------------------------------------------------------------
    @Test
    public void test_chronology() {
        Chronology chronology = Chronology.of("Ifc");
        assertNotNull(chronology);
        assertEquals (chronology, InternationalFixedChronology.INSTANCE);
        assertEquals(chronology.getId(), "Ifc");
        assertEquals (chronology.getCalendarType(), "ifc");
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chronology = Chronology.of("ifc");
        assertNotNull(chronology);
        assertEquals(chronology, InternationalFixedChronology.INSTANCE);
        assertEquals(chronology.getId(), "Ifc");
        assertEquals(chronology.getCalendarType(), "ifc");
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.of
    // -----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
        return new Object[][] {
                {InternationalFixedDate.of(1,  6, 27), LocalDate.of(1,  6, 16)},
                {InternationalFixedDate.of(1,  6, 28), LocalDate.of(1,  6, 17)},
                {InternationalFixedDate.of(1,  7,  1), LocalDate.of(1,  6, 18)},
                {InternationalFixedDate.of(1,  7,  2), LocalDate.of(1,  6, 19)},

                {InternationalFixedDate.of(1, 13, 28), LocalDate.of(1, 12, 30)},
                {InternationalFixedDate.of(1, 13, 27), LocalDate.of(1, 12, 29)},
                {InternationalFixedDate.of(1,  0,  0), LocalDate.of(1, 12, 31)},
                {InternationalFixedDate.yearDay(1),    LocalDate.of(1, 12, 31)},
                {InternationalFixedDate.of(2,  1,  1), LocalDate.of(2,  1,  1)},

                {InternationalFixedDate.of(4,  6, 27), LocalDate.of(4,  6, 15)},
                {InternationalFixedDate.of(4,  6, 28), LocalDate.of(4,  6, 16)},
                {InternationalFixedDate.of(4, -1, -1), LocalDate.of(4,  6, 17)},
                {InternationalFixedDate.leapDay(4),    LocalDate.of(4,  6, 17)},
                {InternationalFixedDate.of(4,  7,  1), LocalDate.of(4,  6, 18)},
                {InternationalFixedDate.of(4,  7,  2), LocalDate.of(4,  6, 19)},

                {InternationalFixedDate.of(4,  1,  1), LocalDate.of(4,  1,  1)},
                {InternationalFixedDate.of(4, 13, 28), LocalDate.of(4, 12, 30)},
                {InternationalFixedDate.of(4, 13, 27), LocalDate.of(4, 12, 29)},
                {InternationalFixedDate.of(4,  0,  0), LocalDate.of(4, 12, 31)},
                {InternationalFixedDate.yearDay(4),    LocalDate.of(4, 12, 31)},

                {InternationalFixedDate.of(100,  6, 27), LocalDate.of(100,  6, 16)},
                {InternationalFixedDate.of(100,  6, 28), LocalDate.of(100,  6, 17)},
                {InternationalFixedDate.of(100,  7,  1), LocalDate.of(100,  6, 18)},
                {InternationalFixedDate.of(100,  7,  2), LocalDate.of(100,  6, 19)},

                {InternationalFixedDate.of(400,  6, 27), LocalDate.of(400,  6, 15)},
                {InternationalFixedDate.of(400,  6, 28), LocalDate.of(400,  6, 16)},
                {InternationalFixedDate.of(400, -1, -1), LocalDate.of(400,  6, 17)},
                {InternationalFixedDate.leapDay(400),    LocalDate.of(400,  6, 17)},
                {InternationalFixedDate.of(400,  7,  1), LocalDate.of(400,  6, 18)},
                {InternationalFixedDate.of(400,  7,  2), LocalDate.of(400,  6, 19)},

                {InternationalFixedDate.of(1582,  9, 28), LocalDate.of(1582,  9,  9)},
                {InternationalFixedDate.of(1582, 10,  1), LocalDate.of(1582,  9, 10)},
                {InternationalFixedDate.of(1945, 10, 27), LocalDate.of(1945, 10,  6)},

                {InternationalFixedDate.of(2012,  6, 15), LocalDate.of(2012,  6,  3)},
                {InternationalFixedDate.of(2012,  6, 16), LocalDate.of(2012,  6,  4)},
        };
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_from_InternationalFixedDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(LocalDate.from(fixed), iso);
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_from_LocalDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(InternationalFixedDate.from(iso), fixed);
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_chronology_fixedEpochDay(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(InternationalFixedChronology.INSTANCE.dateEpochDay(iso.toEpochDay()), fixed);
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_toEpochDay(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(fixed.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_until_InternationalFixedDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(fixed.until(fixed), InternationalFixedChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_until_LocalDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(fixed.until(iso), InternationalFixedChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_until_InternationalFixedDate(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(iso.until(fixed), Period.ZERO);
    }

    @Test(dataProvider = "samples")
    public void test_Chronology_fixed_Temporal(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(InternationalFixedChronology.INSTANCE.date(iso), fixed);
    }

    @Test(dataProvider = "samples")
    public void test_plusDays(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(LocalDate.from(fixed.plus(0, DAYS)), iso);
        assertEquals(LocalDate.from(fixed.plus(1, DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(fixed.plus(35, DAYS)), iso.plusDays(35));
        assertEquals(LocalDate.from(fixed.plus(-1, DAYS)), iso.plusDays(-1));
        assertEquals(LocalDate.from(fixed.plus(-60, DAYS)), iso.plusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_minusDays(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(LocalDate.from(fixed.minus(0, DAYS)), iso);
        assertEquals(LocalDate.from(fixed.minus(1, DAYS)), iso.minusDays(1));
        assertEquals(LocalDate.from(fixed.minus(35, DAYS)), iso.minusDays(35));
        assertEquals(LocalDate.from(fixed.minus(-1, DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(fixed.minus(-60, DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_until_DAYS(InternationalFixedDate fixed, LocalDate iso) {
        assertEquals(fixed.until(iso.plusDays(0), DAYS), 0);
        assertEquals(fixed.until(iso.plusDays(1), DAYS), 1);
        assertEquals(fixed.until(iso.plusDays(35), DAYS), 35);
        assertEquals(fixed.until(iso.minusDays(40), DAYS), -40);
    }

    @DataProvider(name = "badDates")
    Object[][] data_badDates() {
        return new Object[][] {
                {-1, 13, 28},
                {-1, 0, 0},
                {0, 1, 1},

                {1900, -2, 1},
                {1900, 14, 1},
                {1900, 15, 1},

                {1900, 1, -1},
                {1900, 1, 0},
                {1900, 1, 29},

                {1904, -1, 1},
                {1904, -1, 2},

                {1900, -1, -1},
                {1900, -1, 0},
                {1900, -1, 1},

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
                {1900, 13, 29},
        };
    }

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates(int year, int month, int dom) {
        InternationalFixedDate.of(year, month, dom);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void badLeapDayAndYearDayDates() {
        InternationalFixedDate.leapDay(1);
        InternationalFixedDate.leapDay(100);
        InternationalFixedDate.leapDay(200);
        InternationalFixedDate.leapDay(300);
        InternationalFixedDate.leapDay(1900);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_chronology_dateYearDay_badDate() {
        InternationalFixedChronology.INSTANCE.dateYearDay(2001, 366);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.isLeapYear
    // -----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        Predicate<Integer> isLeapYear = year -> {
            return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
        };

        for (int year = 1; year <= 500; year++) {
            InternationalFixedDate base = InternationalFixedDate.of(year, 1, 1);
            assertEquals(base.isLeapYear(), isLeapYear.test(year), "Year " + year + " is failing");
            assertEquals(InternationalFixedChronology.INSTANCE.isLeapYear(year), isLeapYear.test(year), "Year " + year + " is failing leap-year test");
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(InternationalFixedChronology.INSTANCE.isLeapYear(400), true);
        assertEquals(InternationalFixedChronology.INSTANCE.isLeapYear(100), false);
        assertEquals(InternationalFixedChronology.INSTANCE.isLeapYear(4), true);
        assertEquals(InternationalFixedChronology.INSTANCE.isLeapYear(3), false);
        assertEquals(InternationalFixedChronology.INSTANCE.isLeapYear(2), false);
        assertEquals(InternationalFixedChronology.INSTANCE.isLeapYear(1), false);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.lengthOfMonth
    // -----------------------------------------------------------------------
    @DataProvider(name = "lengthOfMonth")
    Object[][] data_lengthOfMonth() {
        return new Object[][] {
                {1900, 1, 28},
                {1900, 2, 28},
                {1900, 3, 28},
                {1900, 4, 28},
                {1900, 5, 28},
                {1900, 6, 28},
                {1900, 7, 28},
                {1900, 8, 28},
                {1900, 9, 28},
                {1900, 10, 28},
                {1900, 11, 28},
                {1900, 12, 28},
                {1900, 13, 28},
        };
    }

    @Test(dataProvider = "lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(InternationalFixedDate.of(year, month, 1).lengthOfMonth(), length);
    }

    @Test
    public void test_lengthOfMonth_specific() {
        assertEquals(InternationalFixedDate.yearDay(1900).lengthOfMonth(), 1);
        assertEquals(InternationalFixedDate.of(1900, 0, 0).lengthOfMonth(), 1);
        assertEquals(InternationalFixedDate.yearDay(2000).lengthOfMonth(), 1);
        assertEquals(InternationalFixedDate.of(2000, 0, 0).lengthOfMonth(), 1);
        assertEquals(InternationalFixedDate.leapDay(2000).lengthOfMonth(), 1);
        assertEquals(InternationalFixedDate.of(2000, -1, -1).lengthOfMonth(), 1);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.era
    // InternationalFixedDate.get
    // InternationalFixedChronology.date
    // InternationalFixedChronology.dateYearDay
    // -----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = 1; year < 201; year++) {
            InternationalFixedDate base = InternationalFixedChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            InternationalFixedEra era = InternationalFixedEra.CE;
            assertEquals(era, base.getEra());
            assertEquals(year, base.get(YEAR_OF_ERA));
            InternationalFixedDate eraBased = InternationalFixedChronology.INSTANCE.date(era, year, 1, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = 1; year < 201; year++) {
            InternationalFixedDate base = InternationalFixedChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            InternationalFixedEra era = InternationalFixedEra.CE;
            assertEquals(era, base.getEra());
            assertEquals(year, base.get(YEAR_OF_ERA));
            InternationalFixedDate eraBased = InternationalFixedChronology.INSTANCE.dateYearDay(era, year, 1);
            assertEquals(eraBased, base);
        }
    }

    // -----------------------------------------------------------------------
    // InternationalFixedChronology.prolepticYear
    // -----------------------------------------------------------------------
    @DataProvider(name = "prolepticYear")
    Object[][] data_prolepticYear() {
        return new Object[][] {
                {InternationalFixedEra.CE, 1},
                {InternationalFixedEra.CE, 2},
                {InternationalFixedEra.CE, 3},
                {InternationalFixedEra.CE, 4},
                {InternationalFixedEra.CE, 1581},
                {InternationalFixedEra.CE, 1582},
                {InternationalFixedEra.CE, 2000},
        };
    }

    @Test(dataProvider = "prolepticYear")
    public void test_prolepticYear(Era era, int year) {
        assertEquals(InternationalFixedChronology.INSTANCE.prolepticYear(era, year), year);
    }

    @DataProvider(name = "prolepticYearBad")
    Object[][] data_prolepticYear_bad() {
        return new Object[][] {
                {InternationalFixedEra.CE, -10},
                {InternationalFixedEra.CE, -1},
                {InternationalFixedEra.CE, 0},
                {IsoEra.CE, 4},
                {JulianEra.AD, 1581},
                {PaxEra.CE, 2000},
        };
    }

    @Test(dataProvider = "prolepticYearBad", expectedExceptions = DateTimeException.class)
    public void test_prolepticYearBad(Era era, int year) {
        assertEquals(InternationalFixedChronology.INSTANCE.prolepticYear(era, year), year);
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(InternationalFixedChronology.INSTANCE.eraOf(0), InternationalFixedEra.CE);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_Chronology_eraOf_invalid() {
        InternationalFixedChronology.INSTANCE.eraOf(2);
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = InternationalFixedChronology.INSTANCE.eras();
        assertEquals(eras.size(), 1);
        assertEquals(eras.contains(InternationalFixedEra.CE), true);
    }

    // -----------------------------------------------------------------------
    // Chronology.range
    // -----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(InternationalFixedChronology.INSTANCE.range(DAY_OF_WEEK),           ValueRange.of( 0,   1, 7));
        assertEquals(InternationalFixedChronology.INSTANCE.range(DAY_OF_MONTH),          ValueRange.of(-1,  28));
        assertEquals(InternationalFixedChronology.INSTANCE.range(DAY_OF_YEAR),           ValueRange.of( 1, 365, 366));
        assertEquals(InternationalFixedChronology.INSTANCE.range(MONTH_OF_YEAR),         ValueRange.of(-1,  13));
        assertEquals(InternationalFixedChronology.INSTANCE.range(ALIGNED_WEEK_OF_MONTH), ValueRange.of( 1,   4));
        assertEquals(InternationalFixedChronology.INSTANCE.range(PROLEPTIC_MONTH),       ValueRange.of(13, 13_000_000 - 1));
        assertEquals(InternationalFixedChronology.INSTANCE.range(YEAR_OF_ERA),           ValueRange.of( 1,  1_000_000));
        assertEquals(InternationalFixedChronology.INSTANCE.range(YEAR),                  ValueRange.of( 1,  1_000_000));
        assertEquals(InternationalFixedChronology.INSTANCE.range(EPOCH_DAY),             ValueRange.of(-719528, 364_522_971));
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.range
    // -----------------------------------------------------------------------
    @DataProvider(name = "ranges")
    Object[][] data_ranges() {
        return new Object[][] {
                {InternationalFixedDate.of(2011,  1, 23), DAY_OF_MONTH,          ValueRange.of(-1,  28)},
                {InternationalFixedDate.of(2012,  1, 23), DAY_OF_MONTH,          ValueRange.of(-1,  28)},

                {InternationalFixedDate.of(2011,  1, 23), DAY_OF_YEAR,           ValueRange.of( 1, 365)},
                {InternationalFixedDate.of(2012,  1, 23), DAY_OF_YEAR,           ValueRange.of( 1, 366)},

                {InternationalFixedDate.of(2011,  1,  1), DAY_OF_WEEK,           ValueRange.of( 0,   1, 7)},
                {InternationalFixedDate.of(2012,  1,  1), DAY_OF_WEEK,           ValueRange.of( 0,   1, 7)},

                {InternationalFixedDate.of(2011,  1, 23), MONTH_OF_YEAR,         ValueRange.of(-1,  13)},
                {InternationalFixedDate.of(2012,  1, 23), MONTH_OF_YEAR,         ValueRange.of(-1,  13)},

                {InternationalFixedDate.of(2011,  1,  3), ALIGNED_WEEK_OF_MONTH, ValueRange.of( 1,   4)},
                {InternationalFixedDate.of(2011,  1, 10), ALIGNED_WEEK_OF_MONTH, ValueRange.of( 1,   4)},
                {InternationalFixedDate.of(2011,  1, 17), ALIGNED_WEEK_OF_MONTH, ValueRange.of( 1,   4)},
                {InternationalFixedDate.of(2011,  1, 24), ALIGNED_WEEK_OF_MONTH, ValueRange.of( 1,   4)},
                {InternationalFixedDate.of(2012,  1,  3), ALIGNED_WEEK_OF_MONTH, ValueRange.of( 1,   4)},
                {InternationalFixedDate.of(2012,  1, 10), ALIGNED_WEEK_OF_MONTH, ValueRange.of( 1,   4)},
                {InternationalFixedDate.of(2012,  1, 17), ALIGNED_WEEK_OF_MONTH, ValueRange.of( 1,   4)},
                {InternationalFixedDate.of(2012,  1, 24), ALIGNED_WEEK_OF_MONTH, ValueRange.of( 1,   4)},

                {InternationalFixedDate.of(2011,  1, 23), PROLEPTIC_MONTH,       ValueRange.of(13, 12_999_999)},
                {InternationalFixedDate.of(2012,  1, 23), PROLEPTIC_MONTH,       ValueRange.of(13, 12_999_999)},

                {InternationalFixedDate.of(2011,  1, 23), YEAR_OF_ERA,           ValueRange.of( 1,  1_000_000)},
                {InternationalFixedDate.of(2012,  1, 23), YEAR_OF_ERA,           ValueRange.of( 1,  1_000_000)},

                {InternationalFixedDate.of(2011,  1, 23), YEAR,                  ValueRange.of( 1,  1_000_000)},
                {InternationalFixedDate.of(2012,  1, 23), YEAR,                  ValueRange.of( 1,  1_000_000)},

                {InternationalFixedDate.of(2011,  1, 23), EPOCH_DAY,      ValueRange.of(-719_528, 364_522_971)},
                {InternationalFixedDate.of(2012,  1, 23), EPOCH_DAY,      ValueRange.of(-719_528, 364_522_971)},

                {InternationalFixedDate.yearDay(2011), DAY_OF_MONTH,        ValueRange.of(  0,   0)},
                {InternationalFixedDate.leapDay(2012), DAY_OF_MONTH,        ValueRange.of(  0,   0)},
                {InternationalFixedDate.yearDay(2012), DAY_OF_MONTH,        ValueRange.of(  0,   0)},

                {InternationalFixedDate.yearDay(2011), DAY_OF_WEEK,         ValueRange.of(  0,   0)},
                {InternationalFixedDate.leapDay(2012), DAY_OF_WEEK,         ValueRange.of(  0,   0)},
                {InternationalFixedDate.yearDay(2012), DAY_OF_WEEK,         ValueRange.of(  0,   0)},

                {InternationalFixedDate.yearDay(2011), DAY_OF_YEAR,         ValueRange.of(  1, 365)},
                {InternationalFixedDate.leapDay(2012), DAY_OF_YEAR,         ValueRange.of(  1, 366)},
                {InternationalFixedDate.yearDay(2012), DAY_OF_YEAR,         ValueRange.of(  1, 366)},

                {InternationalFixedDate.yearDay(2011), MONTH_OF_YEAR,       ValueRange.of(  0,   0)},
                {InternationalFixedDate.leapDay(2012), MONTH_OF_YEAR,       ValueRange.of(  0,   0)},
                {InternationalFixedDate.yearDay(2012), MONTH_OF_YEAR,       ValueRange.of(  0,   0)},

                {InternationalFixedDate.yearDay(2011), ALIGNED_WEEK_OF_MONTH, ValueRange.of(0,  0)},
                {InternationalFixedDate.leapDay(2012), ALIGNED_WEEK_OF_MONTH, ValueRange.of(0,  0)},
                {InternationalFixedDate.yearDay(2012), ALIGNED_WEEK_OF_MONTH, ValueRange.of(0,  0)},

                {InternationalFixedDate.yearDay(2011), PROLEPTIC_MONTH,      ValueRange.of(13, 12_999_999)},
                {InternationalFixedDate.leapDay(2012), PROLEPTIC_MONTH,      ValueRange.of(13, 12_999_999)},
                {InternationalFixedDate.yearDay(2012), PROLEPTIC_MONTH,      ValueRange.of(13, 12_999_999)},

                {InternationalFixedDate.yearDay(2011), YEAR_OF_ERA,          ValueRange.of( 1,  1_000_000)},
                {InternationalFixedDate.leapDay(2012), YEAR_OF_ERA,          ValueRange.of( 1,  1_000_000)},
                {InternationalFixedDate.yearDay(2012), YEAR_OF_ERA,          ValueRange.of( 1,  1_000_000)},

                {InternationalFixedDate.yearDay(2011), YEAR,                 ValueRange.of( 1,  1_000_000)},
                {InternationalFixedDate.leapDay(2012), YEAR,                 ValueRange.of( 1,  1_000_000)},
                {InternationalFixedDate.yearDay(2012), YEAR,                 ValueRange.of( 1,  1_000_000)},

                {InternationalFixedDate.yearDay(2011), EPOCH_DAY,      ValueRange.of(-719_528, 364_522_971)},
                {InternationalFixedDate.leapDay(2012), EPOCH_DAY,      ValueRange.of(-719_528, 364_522_971)},
                {InternationalFixedDate.yearDay(2012), EPOCH_DAY,      ValueRange.of(-719_528, 364_522_971)},
        };
    }

    @Test(dataProvider = "ranges")
    public void test_range(
            InternationalFixedDate date,
            TemporalField field,
            ValueRange expectedRange) {
        assertEquals(date.range(field), expectedRange);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.getLong
    // -----------------------------------------------------------------------
    @DataProvider(name = "getLong")
    Object[][] data_getLong() {
        return new Object[][] {
            {InternationalFixedDate.yearDay(2007), DAY_OF_WEEK, 0},
            {InternationalFixedDate.leapDay(2008), DAY_OF_WEEK, 0},
            {InternationalFixedDate.yearDay(2008), DAY_OF_WEEK, 0},

            {InternationalFixedDate.yearDay(2007), DAY_OF_MONTH,  0},
            {InternationalFixedDate.leapDay(2008), DAY_OF_MONTH, -1},
            {InternationalFixedDate.yearDay(2008), DAY_OF_MONTH,  0},

            {InternationalFixedDate.yearDay(2007), DAY_OF_YEAR, 365},
            {InternationalFixedDate.leapDay(2008), DAY_OF_YEAR, 169},
            {InternationalFixedDate.yearDay(2008), DAY_OF_YEAR, 366},

            {InternationalFixedDate.yearDay(2007), ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {InternationalFixedDate.leapDay(2008), ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {InternationalFixedDate.yearDay(2008), ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},

            {InternationalFixedDate.yearDay(2007), ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {InternationalFixedDate.leapDay(2008), ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {InternationalFixedDate.yearDay(2008), ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},

            {InternationalFixedDate.yearDay(2007), ALIGNED_WEEK_OF_MONTH, 0},
            {InternationalFixedDate.leapDay(2008), ALIGNED_WEEK_OF_MONTH, 0},
            {InternationalFixedDate.yearDay(2008), ALIGNED_WEEK_OF_MONTH, 0},

            {InternationalFixedDate.yearDay(2007), ALIGNED_WEEK_OF_YEAR, 0},
            {InternationalFixedDate.leapDay(2008), ALIGNED_WEEK_OF_YEAR, 0},
            {InternationalFixedDate.yearDay(2008), ALIGNED_WEEK_OF_YEAR, 0},

            {InternationalFixedDate.yearDay(2007), MONTH_OF_YEAR,  0},
            {InternationalFixedDate.leapDay(2008), MONTH_OF_YEAR, -1},
            {InternationalFixedDate.yearDay(2008), MONTH_OF_YEAR,  0},

            {InternationalFixedDate.yearDay(2007), PROLEPTIC_MONTH, 2007 * 13 + 12},
            {InternationalFixedDate.leapDay(2008), PROLEPTIC_MONTH, 2008 * 13 +  6},
            {InternationalFixedDate.yearDay(2008), PROLEPTIC_MONTH, 2008 * 13 + 12},

            {InternationalFixedDate.yearDay(2007), YEAR, 2007},
            {InternationalFixedDate.leapDay(2008), YEAR, 2008},
            {InternationalFixedDate.yearDay(2008), YEAR, 2008},

            {InternationalFixedDate.yearDay(2007), ERA, 1},
            {InternationalFixedDate.leapDay(2008), ERA, 1},
            {InternationalFixedDate.yearDay(2008), ERA, 1},
        };
    }

    @Test(dataProvider = "getLong")
    public void test_data_getLong(
            InternationalFixedDate date,
            TemporalField field,
            long expected) {
        assertEquals(date.getLong(field), expected);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.with
    // -----------------------------------------------------------------------
    @DataProvider(name = "with")
    Object[][] data_with() {
        return new Object[][] {
            {InternationalFixedDate.of(2014,  5, 26), DAY_OF_WEEK, 3, InternationalFixedDate.of(2014, 5, 25)},
            {InternationalFixedDate.of(2014,  5, 26), DAY_OF_WEEK, 4, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), DAY_OF_MONTH, 28, InternationalFixedDate.of(2014, 5, 28)},
            {InternationalFixedDate.of(2014,  5, 26), DAY_OF_MONTH, 26, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.leapDay(2000),    DAY_OF_MONTH, 10, InternationalFixedDate.of(2000,  7, 10)},
            {InternationalFixedDate.yearDay(1999),    DAY_OF_MONTH, 13, InternationalFixedDate.of(1999, 13, 13)},
            {InternationalFixedDate.of(2014,  5, 26), DAY_OF_YEAR, 138, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(1999,  1,  1), DAY_OF_YEAR, 365, InternationalFixedDate.yearDay(1999)},
            {InternationalFixedDate.of(2000,  1,  1), DAY_OF_YEAR, 169, InternationalFixedDate.leapDay(2000)},
            {InternationalFixedDate.of(2000,  1,  1), DAY_OF_YEAR, 366, InternationalFixedDate.yearDay(2000)},
            {InternationalFixedDate.of(2014,  5, 26), ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, InternationalFixedDate.of(2014, 5, 24)},
            {InternationalFixedDate.of(2014,  5, 26), ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), ALIGNED_WEEK_OF_MONTH, 1, InternationalFixedDate.of(2014, 5, 5)},
            {InternationalFixedDate.of(2014,  5, 26), ALIGNED_WEEK_OF_MONTH, 4, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, InternationalFixedDate.of(2014, 5, 24)},
            {InternationalFixedDate.of(2014,  5, 26), ALIGNED_DAY_OF_WEEK_IN_YEAR, 5, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), ALIGNED_WEEK_OF_YEAR, 23, InternationalFixedDate.of(2014, 6, 19)},
            {InternationalFixedDate.of(2014,  5, 26), ALIGNED_WEEK_OF_YEAR, 20, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), MONTH_OF_YEAR,  7, InternationalFixedDate.of(2014,  7, 26)},
            {InternationalFixedDate.of(2014,  5, 26), MONTH_OF_YEAR,  5, InternationalFixedDate.of(2014,  5, 26)},
            {InternationalFixedDate.leapDay(2000),    MONTH_OF_YEAR, 10, InternationalFixedDate.of(2000, 10,  1)},
            {InternationalFixedDate.leapDay(2000),    MONTH_OF_YEAR,  7, InternationalFixedDate.of(2000,  7,  1)},
            {InternationalFixedDate.yearDay(1999),    MONTH_OF_YEAR,  9, InternationalFixedDate.of(1999,  9, 28)},
            {InternationalFixedDate.yearDay(1999),    MONTH_OF_YEAR, 13, InternationalFixedDate.of(1999, 13, 28)},

            {InternationalFixedDate.of(2014,  5, 26), PROLEPTIC_MONTH, 2013 * 13 + 4, InternationalFixedDate.of(2013, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), PROLEPTIC_MONTH, 2013 * 13 + 5, InternationalFixedDate.of(2013, 6, 26)},
            {InternationalFixedDate.of(2014,  5, 26), YEAR, 2012, InternationalFixedDate.of(2012, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), YEAR, 2014, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), YEAR_OF_ERA, 2012, InternationalFixedDate.of(2012, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), YEAR_OF_ERA, 2014, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), ERA, 1, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2011,  3, 28), MONTH_OF_YEAR, 13, InternationalFixedDate.of(2011, 13, 28)},
            {InternationalFixedDate.of(2012,  3, 28), MONTH_OF_YEAR, 13, InternationalFixedDate.of(2012, 13, 28)},
            {InternationalFixedDate.of(2012,  3, 28), MONTH_OF_YEAR, 6, InternationalFixedDate.of(2012, 6, 28)},
            {InternationalFixedDate.of(2012, 13,  7), YEAR, 2011, InternationalFixedDate.of(2011, 13, 7)},
            {InternationalFixedDate.of(2014,  5, 26), DAY_OF_WEEK, 3, InternationalFixedDate.of(2014, 5, 25)},
        };
    }

    @Test(dataProvider = "with")
    public void test_with_TemporalField(
            InternationalFixedDate date,
            TemporalField field,
            long value,
            InternationalFixedDate expectedDate) {
        assertEquals(date.with(field, value), expectedDate);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_with_TemporalField_unsupported() {
        InternationalFixedDate.of(2012, 6, 28).with(MINUTE_OF_DAY, 0);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.with(TemporalAdjuster)
    // -----------------------------------------------------------------------
    @Test
    public void test_adjust_toLastDayOfMonth() {
        InternationalFixedDate base = InternationalFixedDate.of(2012, 6, 23);
        InternationalFixedDate test = (InternationalFixedDate) base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, InternationalFixedDate.of(2012, 6, 28));

        base = InternationalFixedDate.of(2012, 13, 2);
        test = (InternationalFixedDate) base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, InternationalFixedDate.of(2012, 13, 28));
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.with(Local*)
    // -----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        InternationalFixedDate date = InternationalFixedDate.of(2000, 1, 4);
        InternationalFixedDate test = (InternationalFixedDate) date.with(LocalDate.of(2012, 7, 14));
        assertEquals(test, InternationalFixedDate.of(2012, 7, 27));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_adjust_toMonth() {
        InternationalFixedDate date = InternationalFixedDate.of(2000, 1, 4);
        date.with(Month.APRIL);
    }

    // -----------------------------------------------------------------------
    // LocalDate.with(InternationalFixedDate)
    // -----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToInternationalFixedDate() {
        InternationalFixedDate date = InternationalFixedDate.of(2012, 6, 15);
        LocalDate test = LocalDate.MIN.with(date);
        assertEquals(test, LocalDate.of(2012, 6, 3));
    }

    @Test
    public void test_LocalDateTime_adjustToInternationalFixedDate() {
        InternationalFixedDate date = InternationalFixedDate.of(2012, 6, 15);
        LocalDateTime test = LocalDateTime.MIN.with(date);
        assertEquals(test, LocalDateTime.of(2012, 6, 3, 0, 0));
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.plus
    // InternationalFixedDate.minus
    // -----------------------------------------------------------------------
    @DataProvider(name = "plus")
    Object[][] data_plus() {
        return new Object[][] {
            {InternationalFixedDate.of(2014,  5,  1),  0, DAYS,      InternationalFixedDate.of(2014,  5,  1)},
            {InternationalFixedDate.of(2014,  5, 26),  8, DAYS,      InternationalFixedDate.of(2014,  6,  6)},
            {InternationalFixedDate.of(2014,  5, 26), -3, DAYS,      InternationalFixedDate.of(2014,  5, 23)},
            {InternationalFixedDate.of(2014,  5, 26),  0, WEEKS,     InternationalFixedDate.of(2014,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26),  3, WEEKS,     InternationalFixedDate.of(2014,  6, 19)},
            {InternationalFixedDate.of(2014,  5, 26), -5, WEEKS,     InternationalFixedDate.of(2014,  4, 19)},
            {InternationalFixedDate.of(2014,  5,  3),  0, MONTHS,    InternationalFixedDate.of(2014,  5,  3)},
            {InternationalFixedDate.of(2014,  5, 26),  3, MONTHS,    InternationalFixedDate.of(2014,  8, 26)},
            {InternationalFixedDate.of(2014,  5,  4), -5, MONTHS,    InternationalFixedDate.of(2013, 13,  4)},
            {InternationalFixedDate.of(2014,  5, 26),  0, YEARS,     InternationalFixedDate.of(2014,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26),  3, YEARS,     InternationalFixedDate.of(2017,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), -5, YEARS,     InternationalFixedDate.of(2009,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26),  0, DECADES,   InternationalFixedDate.of(2014,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26),  3, DECADES,   InternationalFixedDate.of(2044,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), -5, DECADES,   InternationalFixedDate.of(1964,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26),  0, CENTURIES, InternationalFixedDate.of(2014,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26),  3, CENTURIES, InternationalFixedDate.of(2314,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26), -5, CENTURIES, InternationalFixedDate.of(1514,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26),  0, MILLENNIA, InternationalFixedDate.of(2014,  5, 26)},
            {InternationalFixedDate.of(2014,  5, 26),  3, MILLENNIA, InternationalFixedDate.of(5014,  5, 26)},

            {InternationalFixedDate.of(2013,  1, 26), -1, MONTHS,    InternationalFixedDate.of(2012, 13, 26)},
            {InternationalFixedDate.of(2011, 10,  6),  3, MONTHS,    InternationalFixedDate.of(2011, 13,  6)},
            {InternationalFixedDate.of(2012, 13,  6),  3, MONTHS,    InternationalFixedDate.of(2013,  3,  6)},
            {InternationalFixedDate.of(2012, 13,  7), -1, MONTHS,    InternationalFixedDate.of(2012, 12,  7)},
            {InternationalFixedDate.of(2013, 12,  7),  1, MONTHS,    InternationalFixedDate.of(2013, 13,  7)},
            {InternationalFixedDate.of(2012, 13, 26),  1, MONTHS,    InternationalFixedDate.of(2013,  1, 26)},

            {InternationalFixedDate.of(2012, 13,  6), -6, YEARS,     InternationalFixedDate.of(2006, 13,  6)},
            {InternationalFixedDate.of(2013, 13,  6), -3, YEARS,     InternationalFixedDate.of(2010, 13,  6)},
            {InternationalFixedDate.of(2011, 13, 26),  1, YEARS,     InternationalFixedDate.of(2012, 13, 26)},
            {InternationalFixedDate.of(2014, 13, 26), -2, YEARS,     InternationalFixedDate.of(2012, 13, 26)},
            {InternationalFixedDate.of(2012, 13, 26), -6, YEARS,     InternationalFixedDate.of(2006, 13, 26)},
            {InternationalFixedDate.of(2012, 13,  6),  3, YEARS,     InternationalFixedDate.of(2015, 13,  6)},
            {InternationalFixedDate.of(2015, 13,  6), -3, YEARS,     InternationalFixedDate.of(2012, 13,  6)},

        };
    }

    @Test(dataProvider = "plus")
    public void test_plus_TemporalUnit(
            InternationalFixedDate start,
            long amount,
            TemporalUnit unit,
            InternationalFixedDate end) {
        assertEquals(start.plus(amount, unit), end);
    }

    @Test(dataProvider = "plus")
    public void test_minus_TemporalUnit(
            InternationalFixedDate start,
            long amount,
            TemporalUnit unit,
            InternationalFixedDate end) {
        assertEquals(end.minus(amount, unit), start);
    }

    @DataProvider(name = "plusSpecial")
    Object[][] data_plus_special() {
        return new Object[][] {
            {InternationalFixedDate.leapDay(2012),  1, DAYS,   InternationalFixedDate.of(2012,  7,  1)},
            {InternationalFixedDate.leapDay(2012), -1, DAYS,   InternationalFixedDate.of(2012,  6, 28)},
            {InternationalFixedDate.leapDay(2012),  1, WEEKS,  InternationalFixedDate.of(2012,  7,  7)},
            {InternationalFixedDate.leapDay(2012), -1, WEEKS,  InternationalFixedDate.of(2012,  6, 22)},
            {InternationalFixedDate.of(2012, 6, 28), 1, WEEKS, InternationalFixedDate.of(2012,  7,  7)},
            {InternationalFixedDate.leapDay(2012),  1, MONTHS, InternationalFixedDate.of(2012,  8,  1)},
            {InternationalFixedDate.leapDay(2012), -1, MONTHS, InternationalFixedDate.of(2012,  6,  1)},
            {InternationalFixedDate.leapDay(2012), 52, MONTHS, InternationalFixedDate.leapDay(2016)},
            {InternationalFixedDate.leapDay(1896), 52, MONTHS, InternationalFixedDate.of(1900, 7, 1)},
            {InternationalFixedDate.leapDay(2012),  3, YEARS,  InternationalFixedDate.of(2015,  7,  1)},
            {InternationalFixedDate.leapDay(2012),  4, YEARS,  InternationalFixedDate.leapDay(2016)},
            {InternationalFixedDate.leapDay(2012), -8, YEARS,  InternationalFixedDate.leapDay(2004)},

            {InternationalFixedDate.yearDay(2012),  1, DAYS,   InternationalFixedDate.of(2013,  1,  1)},
            {InternationalFixedDate.yearDay(2012), -1, DAYS,   InternationalFixedDate.of(2012, 13, 28)},
            {InternationalFixedDate.yearDay(2012),  1, WEEKS,  InternationalFixedDate.of(2013,  1,  7)},
            {InternationalFixedDate.yearDay(2012), -1, WEEKS,  InternationalFixedDate.of(2012, 13, 22)},
            {InternationalFixedDate.of(2012, 13, 28), 1, WEEKS, InternationalFixedDate.of(2013,  1, 7)},
            {InternationalFixedDate.yearDay(2012),  1, MONTHS, InternationalFixedDate.of(2013,  1, 28)},
            {InternationalFixedDate.yearDay(2012), -1, MONTHS, InternationalFixedDate.of(2012, 12, 28)},
            {InternationalFixedDate.yearDay(2012), 13, MONTHS, InternationalFixedDate.yearDay(2013)},
            {InternationalFixedDate.yearDay(2012),  3, YEARS,  InternationalFixedDate.yearDay(2015)},
            {InternationalFixedDate.yearDay(2012),  4, YEARS,  InternationalFixedDate.yearDay(2016)},
            {InternationalFixedDate.yearDay(2012), -8, YEARS,  InternationalFixedDate.yearDay(2004)},
        };
    }

    @Test(dataProvider = "plusSpecial")
    public void test_plus_TemporalUnit_Special(
            InternationalFixedDate start,
            long amount,
            TemporalUnit unit,
            InternationalFixedDate end) {
        assertEquals(start.plus(amount, unit), end);
    }

    @DataProvider(name = "minusSpecial")
    Object[][] data_minus_special() {
        return new Object[][] {
            {InternationalFixedDate.leapDay(2012),  1, DAYS,   InternationalFixedDate.of(2012,  6, 28)},
            {InternationalFixedDate.leapDay(2012), -1, DAYS,   InternationalFixedDate.of(2012,  7,  1)},
            {InternationalFixedDate.leapDay(2012),  1, WEEKS,  InternationalFixedDate.of(2012,  6, 22)},
            {InternationalFixedDate.leapDay(2012), -1, WEEKS,  InternationalFixedDate.of(2012,  7,  7)},
            {InternationalFixedDate.of(2012, 7, 7), 1, WEEKS,  InternationalFixedDate.of(2012,  6, 28)},
            {InternationalFixedDate.leapDay(2012),  1, MONTHS, InternationalFixedDate.of(2012,  6,  1)},
            {InternationalFixedDate.leapDay(2012), -1, MONTHS, InternationalFixedDate.of(2012, 8, 1)},
            {InternationalFixedDate.leapDay(2016), 52, MONTHS, InternationalFixedDate.leapDay(2012)},
            {InternationalFixedDate.leapDay(2104), 52, MONTHS, InternationalFixedDate.of(2100,  7,  1)},
            {InternationalFixedDate.leapDay(2012),  3, YEARS,  InternationalFixedDate.of(2009,  7,  1)},
            {InternationalFixedDate.leapDay(2012),  4, YEARS,  InternationalFixedDate.leapDay(2008)},
            {InternationalFixedDate.leapDay(2012), -8, YEARS,  InternationalFixedDate.leapDay(2020)},

            {InternationalFixedDate.yearDay(2012),  1, DAYS,   InternationalFixedDate.of(2012, 13, 28)},
            {InternationalFixedDate.yearDay(2012), -1, DAYS,   InternationalFixedDate.of(2013,  1,  1)},
            {InternationalFixedDate.yearDay(2012),  1, WEEKS,  InternationalFixedDate.of(2012, 13, 22)},
            {InternationalFixedDate.yearDay(2012), -1, WEEKS,  InternationalFixedDate.of(2013,  1,  7)},
            {InternationalFixedDate.of(2012, 1, 7), 1, WEEKS,  InternationalFixedDate.of(2011, 13, 28)},
            {InternationalFixedDate.yearDay(2012),  1, MONTHS, InternationalFixedDate.of(2012, 12, 28)},
            {InternationalFixedDate.yearDay(2012), -1, MONTHS, InternationalFixedDate.of(2013,  1, 28)},
            {InternationalFixedDate.yearDay(2012), 13, MONTHS, InternationalFixedDate.yearDay(2011)},
            {InternationalFixedDate.yearDay(2012),  3, YEARS,  InternationalFixedDate.yearDay(2009)},
            {InternationalFixedDate.yearDay(2012),  4, YEARS,  InternationalFixedDate.yearDay(2008)},
            {InternationalFixedDate.yearDay(2012), -8, YEARS,  InternationalFixedDate.yearDay(2020)},
        };
    }

    @Test(dataProvider = "minusSpecial")
    public void test_minus_TemporalUnit_Special(
            InternationalFixedDate start,
            long amount,
            TemporalUnit unit,
            InternationalFixedDate end) {
        assertEquals(start.minus(amount, unit), end);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.until
    // -----------------------------------------------------------------------
    @DataProvider(name = "until")
    Object[][] data_until() {
        return new Object[][] {
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  5, 26), DAYS, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  6,  4), DAYS, 6},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  5, 20), DAYS, -6},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  5, 26), DAYS, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  6,  4), DAYS, 6},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  5, 20), DAYS, -6},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), DAYS, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012,  7,  1), DAYS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012,  6, 21), DAYS, -8},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2012), DAYS, 197},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), DAYS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2013,  1,  1), DAYS, 1},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2012, 13, 21), DAYS, -8},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), DAYS, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2012,  1,  1), DAYS, 1},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2011, 13, 21), DAYS, -8},

            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  5, 26), WEEKS, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  6,  4), WEEKS, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  6,  5), WEEKS, 1},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  5, 26), WEEKS, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  6,  4), WEEKS, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  6,  5), WEEKS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), WEEKS, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012,  7,  7), WEEKS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012,  5,  1), WEEKS, -8},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2012), WEEKS, 28},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), WEEKS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2013,  1,  9), WEEKS, 1},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2012, 11, 17), WEEKS, -9},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), WEEKS, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2012,  3, 13), WEEKS, 9},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2011,  9,  6), WEEKS, -19},

            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  5, 26), MONTHS, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  6, 25), MONTHS, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  6, 26), MONTHS, 1},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  5, 26), MONTHS, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  6, 25), MONTHS, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  6, 26), MONTHS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), MONTHS, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012, 10,  7), MONTHS, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012,  2, 19), MONTHS, -4},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2012), MONTHS, 6},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), MONTHS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2013,  1,  9), MONTHS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2012,  4, 17), MONTHS, -9},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), MONTHS, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2012,  3, 23), MONTHS, 2},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2011,  8,  6), MONTHS, -5},

            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  5, 26), YEARS, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2012,  5, 25), YEARS, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2012,  5, 26), YEARS, 1},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  5, 26), YEARS, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2015,  5, 25), YEARS, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2015,  5, 26), YEARS, 1},
            {InternationalFixedDate.of(2011, 13, 26), InternationalFixedDate.of(2013, 13, 26), YEARS, 2},
            {InternationalFixedDate.of(2011, 13, 26), InternationalFixedDate.of(2012, 13, 26), YEARS, 1},
            {InternationalFixedDate.of(2012, 13, 26), InternationalFixedDate.of(2011, 13, 26), YEARS, -1},
            {InternationalFixedDate.of(2012, 13, 26), InternationalFixedDate.of(2013, 13, 26), YEARS, 1},
            {InternationalFixedDate.of(2011, 13,  6), InternationalFixedDate.of(2011, 13,  6), YEARS, 0},
            {InternationalFixedDate.of(2012, 13,  6), InternationalFixedDate.of(2012, 13,  6), YEARS, 0},
            {InternationalFixedDate.of(2011, 13,  1), InternationalFixedDate.of(2011, 13,  1), YEARS, 0},
            {InternationalFixedDate.of(2012, 13,  7), InternationalFixedDate.of(2012, 13,  7), YEARS, 0},
            {InternationalFixedDate.of(2011, 12, 28), InternationalFixedDate.of(2012, 12, 28), YEARS, 1},
            {InternationalFixedDate.of(2012, 13,  1), InternationalFixedDate.of(2011, 13,  1), YEARS, -1},
            {InternationalFixedDate.of(2013, 13,  6), InternationalFixedDate.of(2012, 13,  6), YEARS, -1},
            {InternationalFixedDate.of(2012, 13,  6), InternationalFixedDate.of(2013, 13,  6), YEARS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), YEARS, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2015, 10,  7), YEARS, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2008,  2, 19), YEARS, -4},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2018), YEARS, 6},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), YEARS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2013, 13, 28), YEARS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2014), YEARS, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2004,  4, 17), YEARS, -8},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), YEARS, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2015,  3, 23), YEARS, 3},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2007,  8,  6), YEARS, -4},

            {InternationalFixedDate.of(2013,  5, 26), InternationalFixedDate.of(2013,  5, 26), DECADES, 0},
            {InternationalFixedDate.of(2013,  5, 26), InternationalFixedDate.of(2023,  5, 25), DECADES, 0},
            {InternationalFixedDate.of(2013,  5, 26), InternationalFixedDate.of(2023,  5, 26), DECADES, 1},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  5, 26), DECADES, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2024,  5, 25), DECADES, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2024,  5, 26), DECADES, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), DECADES, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2020), DECADES, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2045, 10,  7), DECADES, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(1978,  2, 19), DECADES, -3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2078), DECADES, 6},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), DECADES, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2022, 13, 28), DECADES, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2038), DECADES, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(1924,  4, 17), DECADES, -8},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), DECADES, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2045,  3, 23), DECADES, 3},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(1987,  8,  6), DECADES, -2},

            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  5, 26), CENTURIES, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2111,  5, 25), CENTURIES, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2111,  5, 26), CENTURIES, 1},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  5, 26), CENTURIES, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2114,  5, 25), CENTURIES, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2114,  5, 26), CENTURIES, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), CENTURIES, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2108), CENTURIES, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2312, 10,  7), CENTURIES, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(1639,  2, 19), CENTURIES, -3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2678), CENTURIES, 6},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), CENTURIES, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2022, 13, 28), CENTURIES, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2238), CENTURIES, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(1124,  4, 17), CENTURIES, -8},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), CENTURIES, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2345,  3, 23), CENTURIES, 3},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(1787,  8,  6), CENTURIES, -2},

            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(2011,  5, 26), MILLENNIA, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(3011,  5, 25), MILLENNIA, 0},
            {InternationalFixedDate.of(2011,  5, 26), InternationalFixedDate.of(5011,  5, 26), MILLENNIA, 3},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(2014,  5, 26), MILLENNIA, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(3014,  5, 25), MILLENNIA, 0},
            {InternationalFixedDate.of(2014,  5, 26), InternationalFixedDate.of(5014,  5, 26), MILLENNIA, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), MILLENNIA, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(3012,  6, 28), MILLENNIA, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(6012,  5,  7), MILLENNIA, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(  12,  2, 19), MILLENNIA, -2},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(4678), MILLENNIA, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), MILLENNIA, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2022, 13, 28), MILLENNIA, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(4238), MILLENNIA, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(   4,  4, 17), MILLENNIA, -2},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), MILLENNIA, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(3011, 13, 28), MILLENNIA, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(3456,  3, 23), MILLENNIA, 1},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(   1,  8,  6), MILLENNIA, -2},
        };
    }

    @Test(dataProvider = "until")
    public void test_until_TemporalUnit(
            InternationalFixedDate start,
            InternationalFixedDate end,
            TemporalUnit unit,
            long expected) {
        assertEquals(start.until(end, unit), expected);
    }

    @Test
    public void test_until_leap() {
        InternationalFixedDate leapDay = InternationalFixedDate.leapDay(2008);
        InternationalFixedDate yearDay = InternationalFixedDate.yearDay(2008);
        assertEquals(leapDay.until(yearDay, DAYS), InternationalFixedChronology.DAYS_IN_MONTH * 7 + 1);
        assertEquals(leapDay.until(yearDay, MONTHS), 6);
        assertEquals(yearDay.getDayOfYear(), InternationalFixedChronology.DAYS_IN_YEAR + 1);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_until_TemporalUnit_unsupported() {
        InternationalFixedDate start = InternationalFixedDate.of(2012, 6, 28);
        InternationalFixedDate end = InternationalFixedDate.of(2012, 7, 1);
        start.until(end, MINUTES);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.period
    // -----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(InternationalFixedDate.of(2014, 5, 26).plus(InternationalFixedChronology.INSTANCE.period(0, 2, 2)), InternationalFixedDate.of(2014, 7, 28));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_Period_ISO() {
        assertEquals(InternationalFixedDate.of(2014, 5, 26).plus(Period.ofMonths(2)), InternationalFixedDate.of(2014, 7, 26));
    }

    @Test
    public void test_minus_Period() {
        ChronoPeriod period = InternationalFixedChronology.INSTANCE.period(0, 2, 3);
        assertEquals(InternationalFixedDate.of(2014, 5, 26).minus(period), InternationalFixedDate.of(2014, 3, 23));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_Period_ISO() {
        ChronoPeriod period = Period.ofMonths(2);
        assertEquals(InternationalFixedDate.of(2014, 5, 26).minus(period), InternationalFixedDate.of(2014, 3, 26));
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.equals
    // -----------------------------------------------------------------------
    @Test
    void test_equals() {
        InternationalFixedDate a1 = InternationalFixedDate.of(2000, 1, 3);
        InternationalFixedDate a2 = InternationalFixedDate.of(2000, 1, 3);
        InternationalFixedDate b = InternationalFixedDate.of(2000, 1, 4);
        InternationalFixedDate c = InternationalFixedDate.of(2000, 2, 3);
        InternationalFixedDate d = InternationalFixedDate.of(2001, 1, 3);

        assertEquals(a1.equals(a1), true);
        assertEquals(a1.equals(a2), true);
        assertEquals(a1.equals(b), false);
        assertEquals(a1.equals(c), false);
        assertEquals(a1.equals(d), false);

        assertEquals(a1.equals(null), false);
        assertEquals("".equals(a1), false);

        assertEquals(a1.hashCode(), a2.hashCode());

        InternationalFixedDate e = InternationalFixedDate.yearDay(2001);
        InternationalFixedDate f = InternationalFixedDate.of(2001, 13, 28).plus(1, DAYS);
        LocalDate iso = LocalDate.of(2001, 12, 31);

        assertEquals(iso.toEpochDay(), e.toEpochDay());
        assertEquals(iso.toEpochDay(), f.toEpochDay());
        assertEquals(e, f);
        assertEquals(e.toString(), f.toString());
        assertEquals(e.toEpochDay(), f.toEpochDay());

        e = InternationalFixedDate.leapDay(2004);
        f = InternationalFixedDate.of(2004, 6, 28).plus(1, DAYS);
        iso = LocalDate.of(2004, 6, 17);

        assertEquals(iso.toEpochDay(), e.toEpochDay());
        assertEquals(iso.toEpochDay(), f.toEpochDay());
        assertEquals(e, f);
        assertEquals(e.toString(), f.toString());
        assertEquals(e.toEpochDay(), f.toEpochDay());

        e = InternationalFixedDate.yearDay(2004);
        f = InternationalFixedDate.of(2004, 13, 28).plus(1, DAYS);
        iso = LocalDate.of(2004, 12, 31);

        assertEquals(iso.toEpochDay(), e.toEpochDay());
        assertEquals(iso.toEpochDay(), f.toEpochDay());
        assertEquals(e, f);
        assertEquals(e.toString(), f.toString());
        assertEquals(e.toEpochDay(), f.toEpochDay());
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.toString
    // -----------------------------------------------------------------------
    @DataProvider(name = "toString")
    Object[][] data_toString() {
        return new Object[][] {
            {InternationalFixedDate.of(   1,  1,  1), "Ifc CE 1-01-01"},
            {InternationalFixedDate.of(2012,  6, 23), "Ifc CE 2012-06-23"},

            {InternationalFixedDate.yearDay(1), "Ifc CE 1 Year Day"},
            {InternationalFixedDate.leapDay(2012), "Ifc CE 2012 Leap Day"},
            {InternationalFixedDate.yearDay(2012), "Ifc CE 2012 Year Day"},
        };
    }

    @Test(dataProvider = "toString")
    public void test_toString(InternationalFixedDate date, String expected) {
        assertEquals(date.toString(), expected);
    }


    // -----------------------------------------------------------------------
    // InternationalFixedDate.getDayOfWeek
    // -----------------------------------------------------------------------
    @DataProvider(name = "getDayOfWeek")
    Object[][] data_day_of_week() {
        return new Object[][] {
            {InternationalFixedDate.of(   1,  1,  1), 7},
            {InternationalFixedDate.of(2012,  1,  1), 7},

            {InternationalFixedDate.yearDay(2011), 0},
            {InternationalFixedDate.leapDay(2012), 0},
            {InternationalFixedDate.yearDay(2012), 0},
        };
    }

    @Test(dataProvider = "getDayOfWeek")
    public void test_week_day(InternationalFixedDate date, int weekDay) {
        assertEquals(date.getDayOfWeek(), weekDay);
    }
}
