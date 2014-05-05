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
    @DataProvider(name="samples")
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

    @Test(dataProvider="samples")
    public void test_LocalDate_from_CopticDate(CopticDate coptic, LocalDate iso) {
        assertEquals(LocalDate.from(coptic), iso);
    }

    @Test(dataProvider="samples")
    public void test_CopticDate_from_LocalDate(CopticDate coptic, LocalDate iso) {
        assertEquals(CopticDate.from(iso), coptic);
    }

    @Test(dataProvider="samples")
    public void test_CopticDate_chronology_dateEpochDay(CopticDate coptic, LocalDate iso) {
        assertEquals(CopticChronology.INSTANCE.dateEpochDay(iso.toEpochDay()), coptic);
    }

    @Test(dataProvider="samples")
    public void test_CopticDate_toEpochDay(CopticDate coptic, LocalDate iso) {
        assertEquals(coptic.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider="samples")
    public void test_CopticDate_until_CoptiDate(CopticDate coptic, LocalDate iso) {
        assertEquals(coptic.until(coptic), CopticChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider="samples")
    public void test_CopticDate_until_LocalDate(CopticDate coptic, LocalDate iso) {
        assertEquals(coptic.until(iso), CopticChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider="samples")
    public void test_LocalDate_until_CoptiDate(CopticDate coptic, LocalDate iso) {
        assertEquals(iso.until(coptic), Period.ZERO);
    }

    @Test(dataProvider="samples")
    public void test_Chronology_date_Temporal(CopticDate coptic, LocalDate iso) {
        assertEquals(CopticChronology.INSTANCE.date(iso), coptic);
    }

    @Test(dataProvider="samples")
    public void test_plusDays(CopticDate coptic, LocalDate iso) {
        assertEquals(LocalDate.from(coptic.plus(0, DAYS)), iso);
        assertEquals(LocalDate.from(coptic.plus(1, DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(coptic.plus(35, DAYS)), iso.plusDays(35));
        assertEquals(LocalDate.from(coptic.plus(-1, DAYS)), iso.plusDays(-1));
        assertEquals(LocalDate.from(coptic.plus(-60, DAYS)), iso.plusDays(-60));
    }

    @Test(dataProvider="samples")
    public void test_minusDays(CopticDate coptic, LocalDate iso) {
        assertEquals(LocalDate.from(coptic.minus(0, DAYS)), iso);
        assertEquals(LocalDate.from(coptic.minus(1, DAYS)), iso.minusDays(1));
        assertEquals(LocalDate.from(coptic.minus(35, DAYS)), iso.minusDays(35));
        assertEquals(LocalDate.from(coptic.minus(-1, DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(coptic.minus(-60, DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider="samples")
    public void test_until_DAYS(CopticDate coptic, LocalDate iso) {
        assertEquals(coptic.until(iso.plusDays(0), DAYS), 0);
        assertEquals(coptic.until(iso.plusDays(1), DAYS), 1);
        assertEquals(coptic.until(iso.plusDays(35), DAYS), 35);
        assertEquals(coptic.until(iso.minusDays(40), DAYS), -40);
    }

    @DataProvider(name="badDates")
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

    @Test(dataProvider="badDates", expectedExceptions=DateTimeException.class)
    public void test_badDates(int year, int month, int dom) {
        CopticDate.of(year, month, dom);
    }

    @Test(expectedExceptions=DateTimeException.class)
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

    @DataProvider(name="lengthOfMonth")
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

    @Test(dataProvider="lengthOfMonth")
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

    @Test(expectedExceptions=DateTimeException.class)
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
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {CopticDate.of(1, 1, 1), "Coptic AM 1-01-01"},
            {CopticDate.of(1728, 10, 28), "Coptic AM 1728-10-28"},
            {CopticDate.of(1728, 10, 29), "Coptic AM 1728-10-29"},
            {CopticDate.of(1727, 13, 5), "Coptic AM 1727-13-05"},
            {CopticDate.of(1727, 13, 6), "Coptic AM 1727-13-06"},
        };
    }

    @Test(dataProvider="toString")
    public void test_toString(CopticDate coptic, String expected) {
        assertEquals(coptic.toString(), expected);
    }

}
