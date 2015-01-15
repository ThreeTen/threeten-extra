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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
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
        assertEquals(chronology, InternationalFixedChronology.INSTANCE);
        assertEquals(chronology.getId(), "Ifc");
        assertEquals(chronology.getCalendarType(), "ifc");
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("ifc");
        assertNotNull(chrono);
        assertEquals(chrono, InternationalFixedChronology.INSTANCE);
        assertEquals(chrono.getId(), "Ifc");
        assertEquals(chrono.getCalendarType(), "ifc");
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.of
    // -----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
        return new Object[][] {
            {InternationalFixedDate.of(1, 1, 1), LocalDate.of(1, 1, 1)},
            {InternationalFixedDate.of(1, 1, 2), LocalDate.of(1, 1, 2)},

            {InternationalFixedDate.of(1, 1, 27), LocalDate.of(1, 1, 27)},
            {InternationalFixedDate.of(1, 1, 28), LocalDate.of(1, 1, 28)},
            {InternationalFixedDate.of(1, 2, 1), LocalDate.of(1, 1, 29)},
            {InternationalFixedDate.of(1, 2, 2), LocalDate.of(1, 1, 30)},

            {InternationalFixedDate.of(1, 13, 28), LocalDate.of(1, 12, 30)},
            {InternationalFixedDate.of(1, 13, 27), LocalDate.of(1, 12, 29)},
            {InternationalFixedDate.yearDay(1), LocalDate.of(1, 12, 31)},
            {InternationalFixedDate.of(2, 1, 1), LocalDate.of(2, 1, 1)},

            {InternationalFixedDate.of(6, 12, 27), LocalDate.of(6, 12, 1)},
            {InternationalFixedDate.of(6, 12, 28), LocalDate.of(6, 12, 2)},
            {InternationalFixedDate.of(6, 13, 1), LocalDate.of(6, 12, 3)},
            {InternationalFixedDate.of(6, 13, 2), LocalDate.of(6, 12, 4)},
            {InternationalFixedDate.of(6, 13, 3), LocalDate.of(6, 12, 5)},
            {InternationalFixedDate.of(6, 13, 27), LocalDate.of(6, 12, 29)},
            {InternationalFixedDate.of(6, 13, 28), LocalDate.of(6, 12, 30)},
            {InternationalFixedDate.yearDay(6), LocalDate.of(6, 12, 31)},
            {InternationalFixedDate.of(7, 1, 1), LocalDate.of(7, 1, 1)},

            {InternationalFixedDate.of(399, 13, 1), LocalDate.of(399, 12, 3)},
            {InternationalFixedDate.of(399, 13, 2), LocalDate.of(399, 12, 4)},
            {InternationalFixedDate.of(399, 13, 3), LocalDate.of(399, 12, 5)},
            {InternationalFixedDate.of(399, 13, 4), LocalDate.of(399, 12, 6)},
            {InternationalFixedDate.of(399, 13, 5), LocalDate.of(399, 12, 7)},

            {InternationalFixedDate.of(400, 1, 1), LocalDate.of(400, 1, 1)},
            {InternationalFixedDate.of(400, 1, 2), LocalDate.of(400, 1, 2)},
            {InternationalFixedDate.leapDay(400), LocalDate.of(400, 6, 17)},
            {InternationalFixedDate.of(400, 13, 27), LocalDate.of(400, 12, 29)},
            {InternationalFixedDate.of(400, 13, 28), LocalDate.of(400, 12, 30)},
            {InternationalFixedDate.yearDay(400), LocalDate.of(400, 12, 31)},
            {InternationalFixedDate.of(401, 1, 1), LocalDate.of(401, 1, 1)},
            {InternationalFixedDate.of(401, 1, 2), LocalDate.of(401, 1, 2)},

            {InternationalFixedDate.of(1582, 9, 28), LocalDate.of(1582, 9, 9)},
            {InternationalFixedDate.of(1582, 10, 1), LocalDate.of(1582, 9, 10)},
            {InternationalFixedDate.of(1945, 10, 27), LocalDate.of(1945, 10, 6)},

            {InternationalFixedDate.of(2012, 6, 15), LocalDate.of(2012, 6, 3)},
            {InternationalFixedDate.of(2012, 6, 16), LocalDate.of(2012, 6, 4)},
        };
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_from_InternationalFixedDate(InternationalFixedDate date, LocalDate iso) {
        assertEquals(LocalDate.from(date), iso);
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_from_LocalDate(InternationalFixedDate date, LocalDate iso) {
        assertEquals(InternationalFixedDate.from(iso), date);
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_chronology_dateEpochDay(InternationalFixedDate date, LocalDate iso) {
        assertEquals(InternationalFixedChronology.INSTANCE.dateEpochDay(iso.toEpochDay()), date);
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_toEpochDay(InternationalFixedDate date, LocalDate iso) {
        assertEquals(date.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_until_InternationalFixedDate(InternationalFixedDate date, LocalDate iso) {
        assertEquals(date.until(date), InternationalFixedChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_InternationalFixedDate_until_LocalDate(InternationalFixedDate date, LocalDate iso) {
        assertEquals(date.until(iso), InternationalFixedChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_until_InternationalFixedDate(InternationalFixedDate date, LocalDate iso) {
        assertEquals(iso.until(date), Period.ZERO);
    }

    @Test(dataProvider = "samples")
    public void test_Chronology_date_Temporal(InternationalFixedDate date, LocalDate iso) {
        assertEquals(InternationalFixedChronology.INSTANCE.date(iso), date);
    }

    @Test(dataProvider = "samples")
    public void test_plusDays(InternationalFixedDate date, LocalDate iso) {
        assertEquals(LocalDate.from(date.plus(0, ChronoUnit.DAYS)), iso);
        assertEquals(LocalDate.from(date.plus(1, ChronoUnit.DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(date.plus(35, ChronoUnit.DAYS)), iso.plusDays(35));
    }

    @Test(dataProvider = "samples")
    public void test_minusDays(InternationalFixedDate date, LocalDate iso) {
        assertEquals(LocalDate.from(date.minus(0, ChronoUnit.DAYS)), iso);
        assertEquals(LocalDate.from(date.minus(-1, ChronoUnit.DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(date.minus(-60, ChronoUnit.DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_until_DAYS(InternationalFixedDate date, LocalDate iso) {
        assertEquals(date.until(iso.plusDays(0), ChronoUnit.DAYS), 0);
        assertEquals(date.until(iso.plusDays(1), ChronoUnit.DAYS), 1);
        assertEquals(date.until(iso.plusDays(35), ChronoUnit.DAYS), 35);
    }

    @DataProvider(name = "badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {0, 1, 1},

            {1900, 0, 0},
            {1900, -1, 1},
            {1900, 0, 1},
            {1900, 15, 1},
            {1900, 16, 1},

            {1900, 1, -1},
            {1900, 1, 0},
            {1900, 1, 29},

            {1900, 13, -1},
            {1900, 13, 0},
            {1900, 14, -1},
            {1900, 14, 0},
            {1900, 14, 1},
            {1900, 14, 29},
            {1900, 14, 30},

            {1898, 13, -1},
            {1898, 13, 0},
            {1898, 14, 29},
            {1898, 14, 30},
            {1898, 14, 1},
            {1898, 14, 2},

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
        InternationalFixedChronology.INSTANCE.dateYearDay(2001, 0);
        InternationalFixedChronology.INSTANCE.dateYearDay(2001, -1);
        InternationalFixedChronology.INSTANCE.dateYearDay(2001, 366);
        InternationalFixedChronology.INSTANCE.dateYearDay(2001, 999);

        InternationalFixedChronology.INSTANCE.dateYearDay(2000, 0);
        InternationalFixedChronology.INSTANCE.dateYearDay(2000, -1);
        InternationalFixedChronology.INSTANCE.dateYearDay(2000, 367);
        InternationalFixedChronology.INSTANCE.dateYearDay(2000, 999);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.getDayOfYear
    // -----------------------------------------------------------------------
    @Test
    public void test_dateYearDay() {
        checkYearDateForYear(2001, 365);
        checkYearDateForYear(2004, 366);

        assertEquals(InternationalFixedChronology.INSTANCE.dateYearDay(2001, 365), InternationalFixedDate.yearDay(2001));
        assertEquals(InternationalFixedChronology.INSTANCE.dateYearDay(2004, 169), InternationalFixedDate.leapDay(2004));
        assertEquals(InternationalFixedChronology.INSTANCE.dateYearDay(2004, 366), InternationalFixedDate.yearDay(2004));
    }

    private void checkYearDateForYear(int year, int daysInYear) {
        InternationalFixedDate date = null;
        int month = 0;
        int dayOfMonth = 0;
        int y = 0;

        for (int i = 1; i <= daysInYear; i++) {
            date = InternationalFixedChronology.INSTANCE.dateYearDay(year, i);
            assertEquals(date.getDayOfYear(), i);

            if (date.isYearDay() || date.isLeapDay()) {
                // year day / leap day are not part of any months!
                dayOfMonth = 0;
                month = date.isLeapDay() ? -1 : 0;
            } else {
                y = (i > 169 && date.isLeapYear()) ? i - 1 : i;
                dayOfMonth = ((y - 1) % 28) + 1;
                month = ((y - 1) / 28) + 1;
            }

            assertEquals(month, date.getMonth());
            assertEquals(dayOfMonth, date.getDayOfMonth());
        }
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.isLeapYear
    // -----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        Predicate<Integer> isLeapYear = year -> {
            return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
        };

        for (int year = 1; year < 1001; year++) {
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
    // InternationalFixedDate.era
    // InternationalFixedDate.dateYearDay
    // InternationalFixedDate.prolepticYear
    // -----------------------------------------------------------------------
    @DataProvider(name = "lengthOfMonth")
    Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {InternationalFixedDate.yearDay(1900), 1},
            {InternationalFixedDate.leapDay(2000), 1},
            {InternationalFixedDate.yearDay(2000), 1},
        };
    }

    @Test(dataProvider = "lengthOfMonth")
    public void test_lengthOfMonth(
            InternationalFixedDate date,
            int length) {
        assertEquals(date.lengthOfMonth(), length);
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.era
    // InternationalFixedDate.dateYearDay
    // InternationalFixedDate.prolepticYear
    // -----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = 1; year < 401; year++) {
            InternationalFixedDate base = InternationalFixedChronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(ChronoField.YEAR));
            InternationalFixedEra era = InternationalFixedEra.CE;
            assertEquals(era, base.getEra());
            assertEquals(year, base.get(ChronoField.YEAR_OF_ERA));
            InternationalFixedDate eraBased = InternationalFixedChronology.INSTANCE.date(era, year, 1, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = 1; year < 401; year++) {
            InternationalFixedDate base = InternationalFixedChronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(ChronoField.YEAR));
            InternationalFixedEra era = InternationalFixedEra.CE;
            assertEquals(era, base.getEra());
            assertEquals(year, base.get(ChronoField.YEAR_OF_ERA));
            InternationalFixedDate eraBased = InternationalFixedChronology.INSTANCE.dateYearDay(era, year, 1);
            assertEquals(eraBased, base);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 4), 4);
        assertEquals(InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 3), 3);
        assertEquals(InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 2), 2);
        assertEquals(InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 1), 1);
        assertEquals(InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 2000), 2000);
        assertEquals(InternationalFixedChronology.INSTANCE.prolepticYear(InternationalFixedEra.CE, 1582), 1582);
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void test_prolepticYear_badEra() {
        InternationalFixedChronology.INSTANCE.prolepticYear(IsoEra.CE, 4);
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
        assertEquals(InternationalFixedChronology.INSTANCE.range(ChronoField.DAY_OF_WEEK), ValueRange.of(0, 1, 7));
        assertEquals(InternationalFixedChronology.INSTANCE.range(ChronoField.DAY_OF_MONTH), ValueRange.of(-1, 28));
        assertEquals(InternationalFixedChronology.INSTANCE.range(ChronoField.DAY_OF_YEAR), ValueRange.of(1, 365, 366));
        assertEquals(InternationalFixedChronology.INSTANCE.range(ChronoField.MONTH_OF_YEAR), ValueRange.of(-1, 13));
        assertEquals(InternationalFixedChronology.INSTANCE.range(ChronoField.ALIGNED_WEEK_OF_MONTH), ValueRange.of(1, 4));
        assertEquals(InternationalFixedChronology.INSTANCE.range(ChronoField.PROLEPTIC_MONTH), ValueRange.of(13, 13_000_000 - 1));
        assertEquals(InternationalFixedChronology.INSTANCE.range(ChronoField.YEAR_OF_ERA), ValueRange.of(1, 1_000_000));
        assertEquals(InternationalFixedChronology.INSTANCE.range(ChronoField.YEAR), ValueRange.of(1, 1_000_000));
        assertEquals(InternationalFixedChronology.INSTANCE.range(ChronoField.EPOCH_DAY), ValueRange.of(-719528, 364_522_971));
    }

    // -----------------------------------------------------------------------
    // InternationalFixedDate.range
    // -----------------------------------------------------------------------
    @DataProvider(name = "ranges")
    Object[][] data_ranges() {
        return new Object[][] {
            {InternationalFixedDate.of(2011, 1, 23), ChronoField.DAY_OF_MONTH, ValueRange.of(-1, 28)},
            {InternationalFixedDate.of(2012, 1, 23), ChronoField.DAY_OF_MONTH, ValueRange.of(-1, 28)},

            {InternationalFixedDate.of(2011, 1, 23), ChronoField.DAY_OF_YEAR, ValueRange.of(1, 365)},
            {InternationalFixedDate.of(2012, 1, 23), ChronoField.DAY_OF_YEAR, ValueRange.of(1, 366)},

            {InternationalFixedDate.of(2011, 1, 1), ChronoField.DAY_OF_WEEK, ValueRange.of(0, 1, 7)},
            {InternationalFixedDate.of(2012, 1, 1), ChronoField.DAY_OF_WEEK, ValueRange.of(0, 1, 7)},

            {InternationalFixedDate.of(2011, 1, 23), ChronoField.MONTH_OF_YEAR, ValueRange.of(-1, 13)},
            {InternationalFixedDate.of(2012, 1, 23), ChronoField.MONTH_OF_YEAR, ValueRange.of(-1, 13)},

            {InternationalFixedDate.of(2011, 1, 3), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {InternationalFixedDate.of(2011, 1, 10), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {InternationalFixedDate.of(2011, 1, 17), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {InternationalFixedDate.of(2011, 1, 24), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {InternationalFixedDate.of(2012, 1, 3), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {InternationalFixedDate.of(2012, 1, 10), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {InternationalFixedDate.of(2012, 1, 17), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {InternationalFixedDate.of(2012, 1, 24), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},

            {InternationalFixedDate.of(2011, 1, 23), ChronoField.PROLEPTIC_MONTH, ValueRange.of(13, 12_999_999)},
            {InternationalFixedDate.of(2012, 1, 23), ChronoField.PROLEPTIC_MONTH, ValueRange.of(13, 12_999_999)},

            {InternationalFixedDate.of(2011, 1, 23), ChronoField.YEAR_OF_ERA, ValueRange.of(1, 1_000_000)},
            {InternationalFixedDate.of(2012, 1, 23), ChronoField.YEAR_OF_ERA, ValueRange.of(1, 1_000_000)},

            {InternationalFixedDate.of(2011, 1, 23), ChronoField.YEAR, ValueRange.of(1, 1_000_000)},
            {InternationalFixedDate.of(2012, 1, 23), ChronoField.YEAR, ValueRange.of(1, 1_000_000)},

            {InternationalFixedDate.of(2011, 1, 23), ChronoField.EPOCH_DAY, ValueRange.of(-719_528, 364_522_971)},
            {InternationalFixedDate.of(2012, 1, 23), ChronoField.EPOCH_DAY, ValueRange.of(-719_528, 364_522_971)},

            {InternationalFixedDate.yearDay(2011), ChronoField.DAY_OF_MONTH, ValueRange.of(0, 0)},
            {InternationalFixedDate.leapDay(2012), ChronoField.DAY_OF_MONTH, ValueRange.of(0, 0)},
            {InternationalFixedDate.yearDay(2012), ChronoField.DAY_OF_MONTH, ValueRange.of(0, 0)},

            {InternationalFixedDate.yearDay(2011), ChronoField.DAY_OF_WEEK, ValueRange.of(0, 0)},
            {InternationalFixedDate.leapDay(2012), ChronoField.DAY_OF_WEEK, ValueRange.of(0, 0)},
            {InternationalFixedDate.yearDay(2012), ChronoField.DAY_OF_WEEK, ValueRange.of(0, 0)},

            {InternationalFixedDate.yearDay(2011), ChronoField.DAY_OF_YEAR, ValueRange.of(1, 365)},
            {InternationalFixedDate.leapDay(2012), ChronoField.DAY_OF_YEAR, ValueRange.of(1, 366)},
            {InternationalFixedDate.yearDay(2012), ChronoField.DAY_OF_YEAR, ValueRange.of(1, 366)},

            {InternationalFixedDate.yearDay(2011), ChronoField.MONTH_OF_YEAR, ValueRange.of(0, 0)},
            {InternationalFixedDate.leapDay(2012), ChronoField.MONTH_OF_YEAR, ValueRange.of(0, 0)},
            {InternationalFixedDate.yearDay(2012), ChronoField.MONTH_OF_YEAR, ValueRange.of(0, 0)},

            {InternationalFixedDate.yearDay(2011), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(0, 0)},
            {InternationalFixedDate.leapDay(2012), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(0, 0)},
            {InternationalFixedDate.yearDay(2012), ChronoField.ALIGNED_WEEK_OF_MONTH, ValueRange.of(0, 0)},

            {InternationalFixedDate.yearDay(2011), ChronoField.PROLEPTIC_MONTH, ValueRange.of(13, 12_999_999)},
            {InternationalFixedDate.leapDay(2012), ChronoField.PROLEPTIC_MONTH, ValueRange.of(13, 12_999_999)},
            {InternationalFixedDate.yearDay(2012), ChronoField.PROLEPTIC_MONTH, ValueRange.of(13, 12_999_999)},

            {InternationalFixedDate.yearDay(2011), ChronoField.YEAR_OF_ERA, ValueRange.of(1, 1_000_000)},
            {InternationalFixedDate.leapDay(2012), ChronoField.YEAR_OF_ERA, ValueRange.of(1, 1_000_000)},
            {InternationalFixedDate.yearDay(2012), ChronoField.YEAR_OF_ERA, ValueRange.of(1, 1_000_000)},

            {InternationalFixedDate.yearDay(2011), ChronoField.YEAR, ValueRange.of(1, 1_000_000)},
            {InternationalFixedDate.leapDay(2012), ChronoField.YEAR, ValueRange.of(1, 1_000_000)},
            {InternationalFixedDate.yearDay(2012), ChronoField.YEAR, ValueRange.of(1, 1_000_000)},

            {InternationalFixedDate.yearDay(2011), ChronoField.EPOCH_DAY, ValueRange.of(-719_528, 364_522_971)},
            {InternationalFixedDate.leapDay(2012), ChronoField.EPOCH_DAY, ValueRange.of(-719_528, 364_522_971)},
            {InternationalFixedDate.yearDay(2012), ChronoField.EPOCH_DAY, ValueRange.of(-719_528, 364_522_971)},
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
            {InternationalFixedDate.yearDay(2007), ChronoField.DAY_OF_WEEK, 0},
            {InternationalFixedDate.leapDay(2008), ChronoField.DAY_OF_WEEK, 0},
            {InternationalFixedDate.yearDay(2008), ChronoField.DAY_OF_WEEK, 0},

            {InternationalFixedDate.yearDay(2007), ChronoField.DAY_OF_MONTH, 0},
            {InternationalFixedDate.leapDay(2008), ChronoField.DAY_OF_MONTH, 0},
            {InternationalFixedDate.yearDay(2008), ChronoField.DAY_OF_MONTH, 0},

            {InternationalFixedDate.yearDay(2007), ChronoField.DAY_OF_YEAR, 365},
            {InternationalFixedDate.leapDay(2008), ChronoField.DAY_OF_YEAR, 169},
            {InternationalFixedDate.yearDay(2008), ChronoField.DAY_OF_YEAR, 366},

            {InternationalFixedDate.yearDay(2007), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {InternationalFixedDate.leapDay(2008), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},
            {InternationalFixedDate.yearDay(2008), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 0},

            {InternationalFixedDate.yearDay(2007), ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {InternationalFixedDate.leapDay(2008), ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},
            {InternationalFixedDate.yearDay(2008), ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 0},

            {InternationalFixedDate.yearDay(2007), ChronoField.ALIGNED_WEEK_OF_MONTH, 0},
            {InternationalFixedDate.leapDay(2008), ChronoField.ALIGNED_WEEK_OF_MONTH, 0},
            {InternationalFixedDate.yearDay(2008), ChronoField.ALIGNED_WEEK_OF_MONTH, 0},

            {InternationalFixedDate.yearDay(2007), ChronoField.ALIGNED_WEEK_OF_YEAR, 0},
            {InternationalFixedDate.leapDay(2008), ChronoField.ALIGNED_WEEK_OF_YEAR, 0},
            {InternationalFixedDate.yearDay(2008), ChronoField.ALIGNED_WEEK_OF_YEAR, 0},

            {InternationalFixedDate.yearDay(2007), ChronoField.MONTH_OF_YEAR, 0},
            {InternationalFixedDate.leapDay(2008), ChronoField.MONTH_OF_YEAR, -1},
            {InternationalFixedDate.yearDay(2008), ChronoField.MONTH_OF_YEAR, 0},

            {InternationalFixedDate.yearDay(2007), ChronoField.PROLEPTIC_MONTH, 2007 * 13 + 12},
            {InternationalFixedDate.leapDay(2008), ChronoField.PROLEPTIC_MONTH, 2008 * 13 + 6},
            {InternationalFixedDate.yearDay(2008), ChronoField.PROLEPTIC_MONTH, 2008 * 13 + 12},

            {InternationalFixedDate.yearDay(2007), ChronoField.YEAR, 2007},
            {InternationalFixedDate.leapDay(2008), ChronoField.YEAR, 2008},
            {InternationalFixedDate.yearDay(2008), ChronoField.YEAR, 2008},

            {InternationalFixedDate.yearDay(2007), ChronoField.ERA, 1},
            {InternationalFixedDate.leapDay(2008), ChronoField.ERA, 1},
            {InternationalFixedDate.yearDay(2008), ChronoField.ERA, 1},
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
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.DAY_OF_WEEK, 3, InternationalFixedDate.of(2014, 5, 25)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.DAY_OF_WEEK, 4, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.DAY_OF_MONTH, 28, InternationalFixedDate.of(2014, 5, 28)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.DAY_OF_MONTH, 26, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.DAY_OF_YEAR, 364, InternationalFixedDate.of(2014, 13, 28)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.DAY_OF_YEAR, 138, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, InternationalFixedDate.of(2014, 5, 24)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.ALIGNED_WEEK_OF_MONTH, 1, InternationalFixedDate.of(2014, 5, 5)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.ALIGNED_WEEK_OF_MONTH, 4, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, InternationalFixedDate.of(2014, 5, 24)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 5, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.ALIGNED_WEEK_OF_YEAR, 23, InternationalFixedDate.of(2014, 6, 19)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.ALIGNED_WEEK_OF_YEAR, 20, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.MONTH_OF_YEAR, 7, InternationalFixedDate.of(2014, 7, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.MONTH_OF_YEAR, 5, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.PROLEPTIC_MONTH, 2013 * 13 + 4, InternationalFixedDate.of(2013, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.PROLEPTIC_MONTH, 2013 * 13 + 5, InternationalFixedDate.of(2013, 6, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.YEAR, 2012, InternationalFixedDate.of(2012, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.YEAR, 2014, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.YEAR_OF_ERA, 2012, InternationalFixedDate.of(2012, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.YEAR_OF_ERA, 2014, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.ERA, 1, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2011, 3, 28), ChronoField.MONTH_OF_YEAR, 13, InternationalFixedDate.of(2011, 13, 28)},
            {InternationalFixedDate.of(2012, 3, 28), ChronoField.MONTH_OF_YEAR, 13, InternationalFixedDate.of(2012, 13, 28)},
            {InternationalFixedDate.of(2012, 3, 28), ChronoField.MONTH_OF_YEAR, 6, InternationalFixedDate.of(2012, 6, 28)},
            {InternationalFixedDate.of(2012, 13, 7), ChronoField.YEAR, 2011, InternationalFixedDate.of(2011, 13, 7)},
            {InternationalFixedDate.of(2014, 5, 26), ChronoField.DAY_OF_WEEK, 3, InternationalFixedDate.of(2014, 5, 25)},
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
        InternationalFixedDate.of(2012, 6, 28).with(ChronoField.MINUTE_OF_DAY, 0);
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
            {InternationalFixedDate.of(2014, 5, 1), 0, ChronoUnit.DAYS, InternationalFixedDate.of(2014, 5, 1)},
            {InternationalFixedDate.of(2014, 5, 26), 8, ChronoUnit.DAYS, InternationalFixedDate.of(2014, 6, 6)},
            {InternationalFixedDate.of(2014, 5, 26), -3, ChronoUnit.DAYS, InternationalFixedDate.of(2014, 5, 23)},
            {InternationalFixedDate.of(2014, 5, 26), 0, ChronoUnit.WEEKS, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), 3, ChronoUnit.WEEKS, InternationalFixedDate.of(2014, 6, 19)},
            {InternationalFixedDate.of(2014, 5, 26), -5, ChronoUnit.WEEKS, InternationalFixedDate.of(2014, 4, 19)},
            {InternationalFixedDate.of(2014, 5, 3), 0, ChronoUnit.MONTHS, InternationalFixedDate.of(2014, 5, 3)},
            {InternationalFixedDate.of(2014, 5, 26), 3, ChronoUnit.MONTHS, InternationalFixedDate.of(2014, 8, 26)},
            {InternationalFixedDate.of(2014, 5, 4), -5, ChronoUnit.MONTHS, InternationalFixedDate.of(2013, 13, 4)},
            {InternationalFixedDate.of(2014, 5, 26), 0, ChronoUnit.YEARS, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), 3, ChronoUnit.YEARS, InternationalFixedDate.of(2017, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), -5, ChronoUnit.YEARS, InternationalFixedDate.of(2009, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), 0, ChronoUnit.DECADES, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), 3, ChronoUnit.DECADES, InternationalFixedDate.of(2044, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), -5, ChronoUnit.DECADES, InternationalFixedDate.of(1964, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), 0, ChronoUnit.CENTURIES, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), 3, ChronoUnit.CENTURIES, InternationalFixedDate.of(2314, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), -5, ChronoUnit.CENTURIES, InternationalFixedDate.of(1514, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), 0, ChronoUnit.MILLENNIA, InternationalFixedDate.of(2014, 5, 26)},
            {InternationalFixedDate.of(2014, 5, 26), 3, ChronoUnit.MILLENNIA, InternationalFixedDate.of(5014, 5, 26)},

            {InternationalFixedDate.of(2013, 1, 26), -1, ChronoUnit.MONTHS, InternationalFixedDate.of(2012, 13, 26)},
            {InternationalFixedDate.of(2011, 10, 6), 3, ChronoUnit.MONTHS, InternationalFixedDate.of(2011, 13, 6)},
            {InternationalFixedDate.of(2012, 13, 6), 3, ChronoUnit.MONTHS, InternationalFixedDate.of(2013, 3, 6)},
            {InternationalFixedDate.of(2012, 13, 7), -1, ChronoUnit.MONTHS, InternationalFixedDate.of(2012, 12, 7)},
            {InternationalFixedDate.of(2013, 12, 7), 1, ChronoUnit.MONTHS, InternationalFixedDate.of(2013, 13, 7)},
            {InternationalFixedDate.of(2012, 13, 26), 1, ChronoUnit.MONTHS, InternationalFixedDate.of(2013, 1, 26)},

            {InternationalFixedDate.of(2012, 13, 6), -6, ChronoUnit.YEARS, InternationalFixedDate.of(2006, 13, 6)},
            {InternationalFixedDate.of(2013, 13, 6), -3, ChronoUnit.YEARS, InternationalFixedDate.of(2010, 13, 6)},
            {InternationalFixedDate.of(2011, 13, 26), 1, ChronoUnit.YEARS, InternationalFixedDate.of(2012, 13, 26)},
            {InternationalFixedDate.of(2014, 13, 26), -2, ChronoUnit.YEARS, InternationalFixedDate.of(2012, 13, 26)},
            {InternationalFixedDate.of(2012, 13, 26), -6, ChronoUnit.YEARS, InternationalFixedDate.of(2006, 13, 26)},
            {InternationalFixedDate.of(2012, 13, 6), 3, ChronoUnit.YEARS, InternationalFixedDate.of(2015, 13, 6)},
            {InternationalFixedDate.of(2015, 13, 6), -3, ChronoUnit.YEARS, InternationalFixedDate.of(2012, 13, 6)},

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
            {InternationalFixedDate.leapDay(2012), 1, ChronoUnit.DAYS, InternationalFixedDate.of(2012, 7, 1)},
            {InternationalFixedDate.leapDay(2012), -1, ChronoUnit.DAYS, InternationalFixedDate.of(2012, 6, 28)},
            {InternationalFixedDate.leapDay(2012), 1, ChronoUnit.WEEKS, InternationalFixedDate.of(2012, 7, 7)},
            {InternationalFixedDate.leapDay(2012), -1, ChronoUnit.WEEKS, InternationalFixedDate.of(2012, 6, 22)},

            {InternationalFixedDate.leapDay(2012), 1, ChronoUnit.MONTHS, InternationalFixedDate.of(2012, 8, 1)},
            {InternationalFixedDate.leapDay(2012), -1, ChronoUnit.MONTHS, InternationalFixedDate.of(2012, 6, 1)},
            {InternationalFixedDate.leapDay(2012), 3, ChronoUnit.YEARS, InternationalFixedDate.of(2015, 7, 1)},
            {InternationalFixedDate.leapDay(2012), 4, ChronoUnit.YEARS, InternationalFixedDate.leapDay(2016)},
            {InternationalFixedDate.leapDay(2012), -8, ChronoUnit.YEARS, InternationalFixedDate.leapDay(2004)},

            {InternationalFixedDate.yearDay(2012), 1, ChronoUnit.DAYS, InternationalFixedDate.of(2013, 1, 1)},
            {InternationalFixedDate.yearDay(2012), -1, ChronoUnit.DAYS, InternationalFixedDate.of(2012, 13, 28)},
            {InternationalFixedDate.yearDay(2012), 1, ChronoUnit.WEEKS, InternationalFixedDate.of(2013, 1, 7)},
            {InternationalFixedDate.yearDay(2012), -1, ChronoUnit.WEEKS, InternationalFixedDate.of(2012, 13, 22)},
            {InternationalFixedDate.yearDay(2012), 1, ChronoUnit.MONTHS, InternationalFixedDate.of(2013, 1, 28)},
            {InternationalFixedDate.yearDay(2012), -1, ChronoUnit.MONTHS, InternationalFixedDate.of(2012, 12, 28)},
            {InternationalFixedDate.yearDay(2012), 3, ChronoUnit.YEARS, InternationalFixedDate.yearDay(2015)},
            {InternationalFixedDate.yearDay(2012), 4, ChronoUnit.YEARS, InternationalFixedDate.yearDay(2016)},
            {InternationalFixedDate.yearDay(2012), -8, ChronoUnit.YEARS, InternationalFixedDate.yearDay(2004)},
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
            {InternationalFixedDate.leapDay(2012), 1, ChronoUnit.DAYS, InternationalFixedDate.of(2012, 6, 28)},
            {InternationalFixedDate.leapDay(2012), -1, ChronoUnit.DAYS, InternationalFixedDate.of(2012, 7, 1)},
            {InternationalFixedDate.leapDay(2012), 1, ChronoUnit.MONTHS, InternationalFixedDate.of(2012, 6, 1)},
            {InternationalFixedDate.leapDay(2012), -1, ChronoUnit.MONTHS, InternationalFixedDate.of(2012, 8, 1)},
            {InternationalFixedDate.leapDay(2012), 3, ChronoUnit.YEARS, InternationalFixedDate.of(2009, 7, 1)},
            {InternationalFixedDate.leapDay(2012), 4, ChronoUnit.YEARS, InternationalFixedDate.leapDay(2008)},
            {InternationalFixedDate.leapDay(2012), -8, ChronoUnit.YEARS, InternationalFixedDate.leapDay(2020)},

            {InternationalFixedDate.yearDay(2012), 1, ChronoUnit.DAYS, InternationalFixedDate.of(2012, 13, 28)},
            {InternationalFixedDate.yearDay(2012), -1, ChronoUnit.DAYS, InternationalFixedDate.of(2013, 1, 1)},
            {InternationalFixedDate.yearDay(2012), 1, ChronoUnit.MONTHS, InternationalFixedDate.of(2012, 12, 28)},
            {InternationalFixedDate.yearDay(2012), -1, ChronoUnit.MONTHS, InternationalFixedDate.of(2013, 1, 28)},
            {InternationalFixedDate.yearDay(2012), 3, ChronoUnit.YEARS, InternationalFixedDate.yearDay(2009)},
            {InternationalFixedDate.yearDay(2012), 4, ChronoUnit.YEARS, InternationalFixedDate.yearDay(2008)},
            {InternationalFixedDate.yearDay(2012), -8, ChronoUnit.YEARS, InternationalFixedDate.yearDay(2020)},
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
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 5, 26), ChronoUnit.DAYS, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 6, 4), ChronoUnit.DAYS, 6},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 5, 20), ChronoUnit.DAYS, -6},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 5, 26), ChronoUnit.DAYS, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 6, 4), ChronoUnit.DAYS, 6},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 5, 20), ChronoUnit.DAYS, -6},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), ChronoUnit.DAYS, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012, 7, 1), ChronoUnit.DAYS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012, 6, 21), ChronoUnit.DAYS, -8},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.DAYS, 197},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.DAYS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2013, 1, 1), ChronoUnit.DAYS, 1},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2012, 13, 21), ChronoUnit.DAYS, -8},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), ChronoUnit.DAYS, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2012, 1, 1), ChronoUnit.DAYS, 1},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2011, 13, 21), ChronoUnit.DAYS, -8},

            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 5, 26), ChronoUnit.WEEKS, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 6, 4), ChronoUnit.WEEKS, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 6, 5), ChronoUnit.WEEKS, 1},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 5, 26), ChronoUnit.WEEKS, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 6, 4), ChronoUnit.WEEKS, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 6, 5), ChronoUnit.WEEKS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), ChronoUnit.WEEKS, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012, 7, 7), ChronoUnit.WEEKS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012, 5, 1), ChronoUnit.WEEKS, -8},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.WEEKS, 28},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.WEEKS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2013, 1, 9), ChronoUnit.WEEKS, 1},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2012, 11, 17), ChronoUnit.WEEKS, -9},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), ChronoUnit.WEEKS, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2012, 3, 13), ChronoUnit.WEEKS, 9},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2011, 9, 6), ChronoUnit.WEEKS, -19},

            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 5, 26), ChronoUnit.MONTHS, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 6, 25), ChronoUnit.MONTHS, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 6, 26), ChronoUnit.MONTHS, 1},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 5, 26), ChronoUnit.MONTHS, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 6, 25), ChronoUnit.MONTHS, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 6, 26), ChronoUnit.MONTHS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), ChronoUnit.MONTHS, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012, 10, 7), ChronoUnit.MONTHS, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2012, 2, 19), ChronoUnit.MONTHS, -4},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.MONTHS, 6},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.MONTHS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2013, 1, 9), ChronoUnit.MONTHS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2012, 4, 17), ChronoUnit.MONTHS, -9},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), ChronoUnit.MONTHS, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2012, 3, 23), ChronoUnit.MONTHS, 2},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2011, 8, 6), ChronoUnit.MONTHS, -5},

            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 5, 26), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2012, 5, 25), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2012, 5, 26), ChronoUnit.YEARS, 1},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 5, 26), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2015, 5, 25), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2015, 5, 26), ChronoUnit.YEARS, 1},
            {InternationalFixedDate.of(2011, 13, 26), InternationalFixedDate.of(2013, 13, 26), ChronoUnit.YEARS, 2},
            {InternationalFixedDate.of(2011, 13, 26), InternationalFixedDate.of(2012, 13, 26), ChronoUnit.YEARS, 1},
            {InternationalFixedDate.of(2012, 13, 26), InternationalFixedDate.of(2011, 13, 26), ChronoUnit.YEARS, -1},
            {InternationalFixedDate.of(2012, 13, 26), InternationalFixedDate.of(2013, 13, 26), ChronoUnit.YEARS, 1},
            {InternationalFixedDate.of(2011, 13, 6), InternationalFixedDate.of(2011, 13, 6), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.of(2012, 13, 6), InternationalFixedDate.of(2012, 13, 6), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.of(2011, 13, 1), InternationalFixedDate.of(2011, 13, 1), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.of(2012, 13, 7), InternationalFixedDate.of(2012, 13, 7), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.of(2011, 12, 28), InternationalFixedDate.of(2012, 12, 28), ChronoUnit.YEARS, 1},
            {InternationalFixedDate.of(2012, 13, 1), InternationalFixedDate.of(2011, 13, 1), ChronoUnit.YEARS, -1},
            {InternationalFixedDate.of(2013, 13, 6), InternationalFixedDate.of(2012, 13, 6), ChronoUnit.YEARS, -1},
            {InternationalFixedDate.of(2012, 13, 6), InternationalFixedDate.of(2013, 13, 6), ChronoUnit.YEARS, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2015, 10, 7), ChronoUnit.YEARS, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2008, 2, 19), ChronoUnit.YEARS, -4},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2018), ChronoUnit.YEARS, 6},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2013, 13, 28), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2014), ChronoUnit.YEARS, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2004, 4, 17), ChronoUnit.YEARS, -8},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), ChronoUnit.YEARS, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2015, 3, 23), ChronoUnit.YEARS, 3},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2007, 8, 6), ChronoUnit.YEARS, -4},

            {InternationalFixedDate.of(2013, 5, 26), InternationalFixedDate.of(2013, 5, 26), ChronoUnit.DECADES, 0},
            {InternationalFixedDate.of(2013, 5, 26), InternationalFixedDate.of(2023, 5, 25), ChronoUnit.DECADES, 0},
            {InternationalFixedDate.of(2013, 5, 26), InternationalFixedDate.of(2023, 5, 26), ChronoUnit.DECADES, 1},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 5, 26), ChronoUnit.DECADES, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2024, 5, 25), ChronoUnit.DECADES, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2024, 5, 26), ChronoUnit.DECADES, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), ChronoUnit.DECADES, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2020), ChronoUnit.DECADES, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2045, 10, 7), ChronoUnit.DECADES, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(1978, 2, 19), ChronoUnit.DECADES, -3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2078), ChronoUnit.DECADES, 6},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.DECADES, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2022, 13, 28), ChronoUnit.DECADES, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2038), ChronoUnit.DECADES, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(1924, 4, 17), ChronoUnit.DECADES, -8},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), ChronoUnit.DECADES, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2045, 3, 23), ChronoUnit.DECADES, 3},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(1987, 8, 6), ChronoUnit.DECADES, -2},

            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 5, 26), ChronoUnit.CENTURIES, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2111, 5, 25), ChronoUnit.CENTURIES, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2111, 5, 26), ChronoUnit.CENTURIES, 1},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 5, 26), ChronoUnit.CENTURIES, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2114, 5, 25), ChronoUnit.CENTURIES, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2114, 5, 26), ChronoUnit.CENTURIES, 1},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), ChronoUnit.CENTURIES, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2108), ChronoUnit.CENTURIES, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(2312, 10, 7), ChronoUnit.CENTURIES, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(1639, 2, 19), ChronoUnit.CENTURIES, -3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(2678), ChronoUnit.CENTURIES, 6},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.CENTURIES, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2022, 13, 28), ChronoUnit.CENTURIES, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2238), ChronoUnit.CENTURIES, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(1124, 4, 17), ChronoUnit.CENTURIES, -8},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), ChronoUnit.CENTURIES, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(2345, 3, 23), ChronoUnit.CENTURIES, 3},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(1787, 8, 6), ChronoUnit.CENTURIES, -2},

            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(2011, 5, 26), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(3011, 5, 25), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.of(2011, 5, 26), InternationalFixedDate.of(5011, 5, 26), ChronoUnit.MILLENNIA, 3},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(2014, 5, 26), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(3014, 5, 25), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.of(2014, 5, 26), InternationalFixedDate.of(5014, 5, 26), ChronoUnit.MILLENNIA, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.leapDay(2012), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(3012, 6, 28), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(6012, 5, 7), ChronoUnit.MILLENNIA, 3},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.of(12, 2, 19), ChronoUnit.MILLENNIA, -2},
            {InternationalFixedDate.leapDay(2012), InternationalFixedDate.yearDay(4678), ChronoUnit.MILLENNIA, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(2012), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(2022, 13, 28), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.yearDay(4238), ChronoUnit.MILLENNIA, 2},
            {InternationalFixedDate.yearDay(2012), InternationalFixedDate.of(4, 4, 17), ChronoUnit.MILLENNIA, -2},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.yearDay(2011), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(3011, 13, 28), ChronoUnit.MILLENNIA, 0},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(3456, 3, 23), ChronoUnit.MILLENNIA, 1},
            {InternationalFixedDate.yearDay(2011), InternationalFixedDate.of(1, 8, 6), ChronoUnit.MILLENNIA, -2},
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
        assertEquals(leapDay.until(yearDay, ChronoUnit.DAYS), InternationalFixedChronology.DAYS_IN_MONTH * 7 + 1);
        assertEquals(leapDay.until(yearDay, ChronoUnit.MONTHS), 6);
        assertEquals(yearDay.getDayOfYear(), InternationalFixedChronology.DAYS_IN_YEAR + 1);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_until_TemporalUnit_unsupported() {
        InternationalFixedDate start = InternationalFixedDate.of(2012, 6, 28);
        InternationalFixedDate end = InternationalFixedDate.of(2012, 7, 1);
        start.until(end, ChronoUnit.MINUTES);
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
        InternationalFixedDate f = InternationalFixedDate.of(2001, 13, 28).plus(1, ChronoUnit.DAYS);
        LocalDate iso = LocalDate.of(2001, 12, 31);

        assertEquals(iso.toEpochDay(), e.toEpochDay());
        assertEquals(iso.toEpochDay(), f.toEpochDay());
        assertEquals(e, f);
        assertEquals(e.toString(), f.toString());
        assertEquals(e.toEpochDay(), f.toEpochDay());

        e = InternationalFixedDate.leapDay(2004);
        f = InternationalFixedDate.of(2004, 6, 28).plus(1, ChronoUnit.DAYS);
        iso = LocalDate.of(2004, 6, 17);

        assertEquals(iso.toEpochDay(), e.toEpochDay());
        assertEquals(iso.toEpochDay(), f.toEpochDay());
        assertEquals(e, f);
        assertEquals(e.toString(), f.toString());
        assertEquals(e.toEpochDay(), f.toEpochDay());

        e = InternationalFixedDate.yearDay(2004);
        f = InternationalFixedDate.of(2004, 13, 28).plus(1, ChronoUnit.DAYS);
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
            {InternationalFixedDate.of(1, 1, 1), "Ifc CE 1-01-01"},
            {InternationalFixedDate.of(2012, 6, 23), "Ifc CE 2012-06-23"},

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
            {InternationalFixedDate.of(1, 1, 1), 7},
            {InternationalFixedDate.of(2012, 1, 1), 7},

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
