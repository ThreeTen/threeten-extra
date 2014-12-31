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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.*;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.*;
import java.util.List;
import java.util.function.Predicate;

import static org.testng.Assert.assertEquals;

/**
 * Test.
 */
@Test
@SuppressWarnings ({ "static-method", "javadoc" })
public class TestInternationalFixedChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name () {
        Chronology chronology = Chronology.of ("Ifc");
        Assert.assertNotNull (chronology);
        Assert.assertEquals (chronology, InternationalFixedChronology.INSTANCE);
        Assert.assertEquals (chronology.getId (), "Ifc");
        Assert.assertEquals (chronology.getCalendarType (), "ifc");
    }

    @Test
    public void test_chronology_of_name_id () {
        Chronology chronology = Chronology.of ("Ifc");
        Assert.assertNotNull (chronology);
        Assert.assertEquals (chronology, InternationalFixedChronology.INSTANCE);
        Assert.assertEquals (chronology.getId (), "Ifc");
        Assert.assertEquals (chronology.getCalendarType (), "ifc");
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider (name = "samples")
    Object[][] data_samples () {
        return new Object[][] {
                { InternationalFixedDate.of (1, 13, 29), LocalDate.of (1, 12, 31) },
                { InternationalFixedDate.of (1, 1, 1), LocalDate.of (1, 1, 1) },
                { InternationalFixedDate.of (1, 1, 2), LocalDate.of (1, 1, 2) },

                { InternationalFixedDate.of (1, 1, 27), LocalDate.of (1, 1, 27) },
                { InternationalFixedDate.of (1, 1, 28), LocalDate.of (1, 1, 28) },
                { InternationalFixedDate.of (1, 2, 1), LocalDate.of (1, 1, 29) },
                { InternationalFixedDate.of (1, 2, 2), LocalDate.of (1, 1, 30) },

                { InternationalFixedDate.of (6, 12, 27), LocalDate.of (6, 12, 1) },
                { InternationalFixedDate.of (6, 12, 28), LocalDate.of (6, 12, 2) },
                { InternationalFixedDate.of (6, 13, 1), LocalDate.of (6, 12, 3) },
                { InternationalFixedDate.of (6, 13, 2), LocalDate.of (6, 12, 4) },
                { InternationalFixedDate.of (6, 13, 3), LocalDate.of (6, 12, 5) },
                { InternationalFixedDate.of (6, 13, 27), LocalDate.of (6, 12, 29) },
                { InternationalFixedDate.of (6, 13, 28), LocalDate.of (6, 12, 30) },
                { InternationalFixedDate.of (6, 13, 29), LocalDate.of (6, 12, 31) },
                { InternationalFixedDate.of (7, 1, 1), LocalDate.of (7, 1, 1) },

                { InternationalFixedDate.of (399, 13, 1), LocalDate.of (399, 12, 3) },
                { InternationalFixedDate.of (399, 13, 2), LocalDate.of (399, 12, 4) },
                { InternationalFixedDate.of (399, 13, 3), LocalDate.of (399, 12, 5) },
                { InternationalFixedDate.of (399, 13, 4), LocalDate.of (399, 12, 6) },
                { InternationalFixedDate.of (399, 13, 5), LocalDate.of (399, 12, 7) },
                { InternationalFixedDate.of (400, 13, 27), LocalDate.of (400, 12, 29) },
                { InternationalFixedDate.of (400, 13, 28), LocalDate.of (400, 12, 30) },
                { InternationalFixedDate.of (400, 13, 29), LocalDate.of (400, 12, 31) },
                { InternationalFixedDate.of (401, 1, 1), LocalDate.of (401, 1, 1) },
                { InternationalFixedDate.of (401, 1, 2), LocalDate.of (401, 1, 2) },

                { InternationalFixedDate.of (1, 13, 28), LocalDate.of (1, 12, 30) },
                { InternationalFixedDate.of (1, 13, 27), LocalDate.of (1, 12, 29) },

                { InternationalFixedDate.of (1582, 9, 28), LocalDate.of (1582, 9, 9) },
                { InternationalFixedDate.of (1582, 10, 1), LocalDate.of (1582, 9, 10) },
                { InternationalFixedDate.of (1945, 10, 27), LocalDate.of (1945, 10, 6) },

                { InternationalFixedDate.of (2012, 6, 15), LocalDate.of (2012, 6, 4) },
                { InternationalFixedDate.of (2012, 6, 16), LocalDate.of (2012, 6, 5) },
        };
    }

    @Test (dataProvider = "samples")
    public void test_LocalDate_from_InternationalFixedDate (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (LocalDate.from (date), iso);
    }

    @Test (dataProvider = "samples")
    public void test_InternationalFixedDate_from_LocalDate (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (InternationalFixedDate.from (iso), date);
    }

    @Test (dataProvider = "samples")
    public void test_InternationalFixedDate_chronology_dateEpochDay (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (InternationalFixedChronology.INSTANCE.dateEpochDay (iso.toEpochDay ()), date);
    }

    @Test (dataProvider = "samples")
    public void test_InternationalFixedDate_toEpochDay (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (date.toEpochDay (), iso.toEpochDay ());
    }

    @Test (dataProvider = "samples")
    public void test_InternationalFixedDate_until_InternationalFixedDate (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (date.until (date), InternationalFixedChronology.INSTANCE.period (0, 0, 0));
    }

    @Test (dataProvider = "samples")
    public void test_InternationalFixedDate_until_LocalDate (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (date.until (iso), InternationalFixedChronology.INSTANCE.period (0, 0, 0));
    }

    @Test (dataProvider = "samples")
    public void test_LocalDate_until_InternationalFixedDate (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (iso.until (date), Period.ZERO);
    }

    @Test (dataProvider = "samples")
    public void test_Chronology_date_Temporal (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (InternationalFixedChronology.INSTANCE.date (iso), date);
    }

    @Test (dataProvider = "samples")
    public void test_plusDays (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (LocalDate.from (date.plus (0, ChronoUnit.DAYS)), iso);
        assertEquals (LocalDate.from (date.plus (1, ChronoUnit.DAYS)), iso.plusDays (1));
        assertEquals (LocalDate.from (date.plus (35, ChronoUnit.DAYS)), iso.plusDays (35));
    }

    @Test (dataProvider = "samples")
    public void test_minusDays (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (LocalDate.from (date.minus (0, ChronoUnit.DAYS)), iso);
        assertEquals (LocalDate.from (date.minus (-1, ChronoUnit.DAYS)), iso.minusDays (-1));
        assertEquals (LocalDate.from (date.minus (-60, ChronoUnit.DAYS)), iso.minusDays (-60));
    }

    @Test (dataProvider = "samples")
    public void test_until_DAYS (final InternationalFixedDate date, final LocalDate iso) {
        assertEquals (date.until (iso.plusDays (0), ChronoUnit.DAYS), 0);
        assertEquals (date.until (iso.plusDays (1), ChronoUnit.DAYS), 1);
        assertEquals (date.until (iso.plusDays (35), ChronoUnit.DAYS), 35);
    }

    @DataProvider (name = "badDates")
    Object[][] data_badDates () {
        return new Object[][] {
                {    0, 1, 1 },
                { 1900, 0, 0 },
                { 1900, -1, 1 },
                { 1900, 0, 1 },
                { 1900, 15, 1 },
                { 1900, 16, 1 },

                { 1900, 1, -1 },
                { 1900, 1, 0 },
                { 1900, 1, 29 },

                { 1900, 13, -1 },
                { 1900, 13, 0 },
                { 1900, 13, 88 },
                { 1900, 14, -1 },
                { 1900, 14, 0 },
                { 1900, 14, 29 },
                { 1900, 14, 30 },

                { 1898, 13, -1 },
                { 1898, 13, 0 },
                { 1898, 14, 29 },
                { 1898, 14, 30 },
                { 1898, 14, 1 },
                { 1898, 14, 2 },

                { 1900, 14, -1 },
                { 1900, 14, 0 },
                { 1900, 14, 29 },

                { 1900, 2, 29 },
                { 1900, 3, 29 },
                { 1900, 4, 29 },
                { 1900, 5, 29 },
                { 1900, 6, 29 },
                { 1900, 7, 29 },
                { 1900, 8, 29 },
                { 1900, 9, 29 },
                { 1900, 10, 29 },
                { 1900, 11, 29 },
                { 1900, 12, 29 },
        };
    }

    @Test (dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates (final int year, final int month, final int dom) {
        InternationalFixedDate.of (year, month, dom);
    }

    @Test (expectedExceptions = DateTimeException.class)
    public void test_chronology_dateYearDay_badDate () {
        InternationalFixedChronology.INSTANCE.dateYearDay (2001, 365);
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop () {
        Predicate<Integer> isLeapYear = year -> {
            return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
        };

        for (int year = 1; year < 1001; year++) {
            InternationalFixedDate base = InternationalFixedDate.of (year, 1, 1);
            assertEquals (base.isLeapYear (), isLeapYear.test (year), "Year " + year + " is failing");
            assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (year), isLeapYear.test (year), "Year " + year + " is failing");
        }
    }

    @Test
    public void test_isLeapYear_specific () {
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (400), true);
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (100), false);
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (99), false);
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (7), false);
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (6), false);
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (5), false);
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (4), true);
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (3), false);
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (2), false);
        assertEquals (InternationalFixedChronology.INSTANCE.isLeapYear (1), false);
    }

    @DataProvider (name = "lengthOfMonth")
    Object[][] data_lengthOfMonth () {
        return new Object[][] {
                { 1900, 1, 28 },
                { 1900, 2, 28 },
                { 1900, 3, 28 },
                { 1900, 4, 28 },
                { 1900, 5, 28 },
                { 1900, 6, 28 },
                { 1900, 7, 28 },
                { 1900, 8, 28 },
                { 1900, 9, 28 },
                { 1900, 10, 28 },
                { 1900, 11, 28 },
                { 1900, 12, 28 },
                { 1900, 13, 29 },

                { 1901, 13, 29 },
                { 1902, 13, 29 },
                { 1903, 13, 29 },
                { 1904, 13, 29 },
                { 1905, 13, 29 },
                { 1906, 13, 29 },
                { 2000, 13, 29 },
                { 2100, 13, 29 },
        };
    }

    @Test (dataProvider = "lengthOfMonth")
    public void test_lengthOfMonth (final int year, final int month, final int length) {
        InternationalFixedDate date = InternationalFixedDate.of (year, month, 1);
        assertEquals (date.lengthOfMonth (), length);
    }

    //-----------------------------------------------------------------------
    // era, prolepticYear and dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop () {
        for (int year = 1; year < 401; year++) {
            InternationalFixedDate base = InternationalFixedChronology.INSTANCE.date (year, 1, 1);
            assertEquals (year, base.get (ChronoField.YEAR));
            InternationalFixedEra era = InternationalFixedEra.CE;
            assertEquals (era, base.getEra ());
            assertEquals (year, base.get (ChronoField.YEAR_OF_ERA));
            InternationalFixedDate eraBased = InternationalFixedChronology.INSTANCE.date (era, year, 1, 1);
            assertEquals (eraBased, base);
        }
    }

    @Test
    public void test_era_yearDay_loop () {
        for (int year = 1; year < 401; year++) {
            InternationalFixedDate base = InternationalFixedChronology.INSTANCE.dateYearDay (year, 1);
            assertEquals (year, base.get (ChronoField.YEAR));
            InternationalFixedEra era = InternationalFixedEra.CE;
            assertEquals (era, base.getEra ());
            assertEquals (year, base.get (ChronoField.YEAR_OF_ERA));
            InternationalFixedDate eraBased = InternationalFixedChronology.INSTANCE.dateYearDay (era, year, 1);
            assertEquals (eraBased, base);
        }
    }

    @Test
    public void test_prolepticYear_specific () {
        assertEquals (InternationalFixedChronology.INSTANCE.prolepticYear (InternationalFixedEra.CE, 4), 4);
        assertEquals (InternationalFixedChronology.INSTANCE.prolepticYear (InternationalFixedEra.CE, 3), 3);
        assertEquals (InternationalFixedChronology.INSTANCE.prolepticYear (InternationalFixedEra.CE, 2), 2);
        assertEquals (InternationalFixedChronology.INSTANCE.prolepticYear (InternationalFixedEra.CE, 1), 1);
    }

    @Test (expectedExceptions = ClassCastException.class)
    public void test_prolepticYear_badEra () {
        InternationalFixedChronology.INSTANCE.prolepticYear (IsoEra.CE, 4);
    }

    @Test
    public void test_Chronology_eraOf () {
        assertEquals (InternationalFixedChronology.INSTANCE.eraOf (0), InternationalFixedEra.CE);
    }

    @Test (expectedExceptions = DateTimeException.class)
    public void test_Chronology_eraOf_invalid () {
        InternationalFixedChronology.INSTANCE.eraOf (2);
    }

    @Test
    public void test_Chronology_eras () {
        List<Era> eras = InternationalFixedChronology.INSTANCE.eras ();
        assertEquals (eras.size (), 1);
        assertEquals (eras.contains (InternationalFixedEra.CE), true);
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range () {
        assertEquals (InternationalFixedChronology.INSTANCE.range (ChronoField.DAY_OF_WEEK), ValueRange.of (1, 7));
        assertEquals (InternationalFixedChronology.INSTANCE.range (ChronoField.DAY_OF_MONTH), ValueRange.of (1, 28, 29));
        assertEquals (InternationalFixedChronology.INSTANCE.range (ChronoField.DAY_OF_YEAR), ValueRange.of (1, 364, 365));
        assertEquals (InternationalFixedChronology.INSTANCE.range (ChronoField.MONTH_OF_YEAR), ValueRange.of (1, 13));
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.range
    //-----------------------------------------------------------------------
    @DataProvider (name = "ranges")
    Object[][] data_ranges () {
        return new Object[][] {
                { 2012, 1, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 2, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 3, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 4, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 5, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 6, 23, ChronoField.DAY_OF_MONTH, 1, 29 },
                { 2012, 7, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 8, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 9, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 10, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 11, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 12, 23, ChronoField.DAY_OF_MONTH, 1, 28 },
                { 2012, 13, 3, ChronoField.DAY_OF_MONTH, 1, 29 },
                { 2012, 13, 23, ChronoField.DAY_OF_MONTH, 1, 29 },

                { 2012, 1, 23, ChronoField.MONTH_OF_YEAR, 1, 13 },
                { 2012, 1, 23, ChronoField.DAY_OF_YEAR, 1, 365 },

                { 2012, 1, 23, ChronoField.ALIGNED_WEEK_OF_MONTH, 1, 4 },
                { 2012, 13, 3, ChronoField.ALIGNED_WEEK_OF_MONTH, 1, 4 },
                { 2012, 13, 23, ChronoField.ALIGNED_WEEK_OF_MONTH, 1, 4 },

                { 2011, 13, 23, ChronoField.DAY_OF_MONTH, 1, 29 },
                { 2011, 1, 23, ChronoField.MONTH_OF_YEAR, 1, 13 },
                { 2011, 13, 23, ChronoField.DAY_OF_YEAR, 1, 364 },
                { 2011, 13, 23, ChronoField.ALIGNED_WEEK_OF_MONTH, 1, 4 },
        };
    }

    @Test (dataProvider = "ranges")
    public void test_range (final int year, final int month, final int dom, final TemporalField field, final int expectedMin, final int expectedMax) {
        assertEquals (InternationalFixedDate.of (year, month, dom).range (field), ValueRange.of (expectedMin, expectedMax));
    }

    @Test (expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_unsupported () {
        InternationalFixedDate.of (2012, 6, 28).range (ChronoField.MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.getLong
    //-----------------------------------------------------------------------
    @DataProvider (name = "getLong")
    Object[][] data_getLong () {
        return new Object[][] {
                { 2014, 5, 26, ChronoField.DAY_OF_WEEK, 7 },
                { 2014, 5, 26, ChronoField.DAY_OF_MONTH, 26 },
                { 2014, 5, 26, ChronoField.DAY_OF_YEAR, 28 + 28 + 28 + 28 + 26 },
                { 2014, 5, 26, ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 5 },
                { 2014, 5, 26, ChronoField.ALIGNED_WEEK_OF_MONTH, 4 },
                { 2014, 5, 26, ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 5 },
                { 2014, 5, 26, ChronoField.ALIGNED_WEEK_OF_YEAR, 20 },
                { 2014, 5, 26, ChronoField.MONTH_OF_YEAR, 5 },
                { 2014, 5, 26, ChronoField.PROLEPTIC_MONTH, 2014 * 13 + 5 - 1 },
                { 2014, 5, 26, ChronoField.YEAR, 2014 },
                { 2014, 5, 26, ChronoField.ERA, 1 },
                {    1, 6,  8, ChronoField.ERA, 1 },
                { 2014, 5, 26, WeekFields.ISO.dayOfWeek (), 7 },
        };
    }

    @Test (dataProvider = "getLong")
    public void test_getLong (final int year, final int month, final int dom, final TemporalField field, final long expected) {
        assertEquals (InternationalFixedDate.of (year, month, dom).getLong (field), expected);
    }

    @Test (expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_unsupported () {
        InternationalFixedDate.of (2012, 6, 28).getLong (ChronoField.MINUTE_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.with
    //-----------------------------------------------------------------------
    @DataProvider (name = "with")
    Object[][] data_with () {
        return new Object[][] {
                { 2014, 5, 26, ChronoField.DAY_OF_WEEK, 3, 2014, 5, 22 },
                { 2014, 5, 26, ChronoField.DAY_OF_WEEK, 4, 2014, 5, 23 },
                { 2014, 5, 26, ChronoField.DAY_OF_MONTH, 28, 2014, 5, 28 },
                { 2014, 5, 26, ChronoField.DAY_OF_MONTH, 26, 2014, 5, 26 },
                { 2014, 5, 26, ChronoField.DAY_OF_YEAR, 364, 2014, 13, 28 },
                { 2014, 5, 26, ChronoField.DAY_OF_YEAR, 138, 2014, 5, 26 },
                { 2014, 5, 26, ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 5, 24 },
                { 2014, 5, 26, ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 2014, 5, 26 },
                { 2014, 5, 26, ChronoField.ALIGNED_WEEK_OF_MONTH, 1, 2014, 5, 5 },
                { 2014, 5, 26, ChronoField.ALIGNED_WEEK_OF_MONTH, 4, 2014, 5, 26 },
                { 2014, 5, 26, ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, 2014, 5, 24 },
                { 2014, 5, 26, ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 5, 2014, 5, 26 },
                { 2014, 5, 26, ChronoField.ALIGNED_WEEK_OF_YEAR, 23, 2014, 6, 19 },
                { 2014, 5, 26, ChronoField.ALIGNED_WEEK_OF_YEAR, 20, 2014, 5, 26 },
                { 2014, 5, 26, ChronoField.MONTH_OF_YEAR, 7, 2014, 7, 26 },
                { 2014, 5, 26, ChronoField.MONTH_OF_YEAR, 5, 2014, 5, 26 },
                { 2014, 5, 26, ChronoField.PROLEPTIC_MONTH, 2013 * 13 + 4, 2013, 5, 26 },
                { 2014, 5, 26, ChronoField.PROLEPTIC_MONTH, 2013 * 13 + 5, 2013, 6, 26 },
                { 2014, 5, 26, ChronoField.YEAR, 2012, 2012, 5, 26 },
                { 2014, 5, 26, ChronoField.YEAR, 2014, 2014, 5, 26 },
                { 2014, 5, 26, ChronoField.YEAR_OF_ERA, 2012, 2012, 5, 26 },
                { 2014, 5, 26, ChronoField.YEAR_OF_ERA, 2014, 2014, 5, 26 },
                { 2014, 5, 26, ChronoField.ERA, 1, 2014, 5, 26 },

                { 2011, 3, 28, ChronoField.MONTH_OF_YEAR, 13, 2011, 13, 28 },
                { 2012, 3, 28, ChronoField.MONTH_OF_YEAR, 13, 2012, 13, 28 },
                { 2012, 3, 28, ChronoField.MONTH_OF_YEAR, 6, 2012, 6, 28 },
                { 2012, 13, 7, ChronoField.YEAR, 2011, 2011, 13, 7 },
                { 2014, 5, 26, WeekFields.ISO.dayOfWeek (), 3, 2014, 5, 22 },
        };
    }

    @Test (dataProvider = "with")
    public void test_with_TemporalField (
            final int year,
            final int month,
            final int dom,
            final TemporalField field,
            final long value,
            final int expectedYear,
            final int expectedMonth,
            final int expectedDom) {
        assertEquals (InternationalFixedDate.of (year, month, dom).with (field, value), InternationalFixedDate.of (expectedYear, expectedMonth, expectedDom));
    }

    @Test (expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_with_TemporalField_unsupported () {
        InternationalFixedDate.of (2012, 6, 28).with (ChronoField.MINUTE_OF_DAY, 0);
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLastDayOfMonth () {
        InternationalFixedDate base = InternationalFixedDate.of (2012, 6, 23);
        InternationalFixedDate test = base.with (TemporalAdjusters.lastDayOfMonth ());
        assertEquals (test, InternationalFixedDate.of (2012, 6, 29));

        base = InternationalFixedDate.of (2012, 13, 2);
        test = base.with (TemporalAdjusters.lastDayOfMonth ());
        assertEquals (test, InternationalFixedDate.of (2012, 13, 29));
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate () {
        InternationalFixedDate date = InternationalFixedDate.of (2000, 1, 4);
        InternationalFixedDate test = date.with (LocalDate.of (2012, 7, 14));
        assertEquals (test, InternationalFixedDate.of (2012, 7, 27));
    }

    @Test (expectedExceptions = DateTimeException.class)
    public void test_adjust_toMonth () {
        InternationalFixedDate date = InternationalFixedDate.of (2000, 1, 4);
        date.with (Month.APRIL);
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(InternationalFixedDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToInternationalFixedDate () {
        InternationalFixedDate date = InternationalFixedDate.of (2012, 6, 15);
        LocalDate test = LocalDate.MIN.with (date);
        assertEquals (test, LocalDate.of (2012, 6, 4));
    }

    @Test
    public void test_LocalDateTime_adjustToInternationalFixedDate () {
        InternationalFixedDate date = InternationalFixedDate.of (2012, 6, 15);
        LocalDateTime test = LocalDateTime.MIN.with (date);
        assertEquals (test, LocalDateTime.of (2012, 6, 4, 0, 0));
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.plus
    //-----------------------------------------------------------------------
    @DataProvider (name = "plus")
    Object[][] data_plus () {
        return new Object[][] {
                { 2014, 5, 26, 0, ChronoUnit.DAYS, 2014, 5, 26 },
                { 2014, 5, 26, 8, ChronoUnit.DAYS, 2014, 6, 6 },
                { 2014, 5, 26, -3, ChronoUnit.DAYS, 2014, 5, 23 },
                { 2014, 5, 26, 0, ChronoUnit.WEEKS, 2014, 5, 26 },
                { 2014, 5, 26, 3, ChronoUnit.WEEKS, 2014, 6, 19 },
                { 2014, 5, 26, -5, ChronoUnit.WEEKS, 2014, 4, 19 },
                { 2014, 5, 26, 0, ChronoUnit.MONTHS, 2014, 5, 26 },
                { 2014, 5, 26, 3, ChronoUnit.MONTHS, 2014, 8, 26 },
                { 2014, 5, 26, -5, ChronoUnit.MONTHS, 2013, 13, 26 },
                { 2014, 5, 26, 0, ChronoUnit.YEARS, 2014, 5, 26 },
                { 2014, 5, 26, 3, ChronoUnit.YEARS, 2017, 5, 26 },
                { 2014, 5, 26, -5, ChronoUnit.YEARS, 2009, 5, 26 },
                { 2014, 5, 26, 0, ChronoUnit.DECADES, 2014, 5, 26 },
                { 2014, 5, 26, 3, ChronoUnit.DECADES, 2044, 5, 26 },
                { 2014, 5, 26, -5, ChronoUnit.DECADES, 1964, 5, 26 },
                { 2014, 5, 26, 0, ChronoUnit.CENTURIES, 2014, 5, 26 },
                { 2014, 5, 26, 3, ChronoUnit.CENTURIES, 2314, 5, 26 },
                { 2014, 5, 26, -5, ChronoUnit.CENTURIES, 1514, 5, 26 },
                { 2014, 5, 26, 0, ChronoUnit.MILLENNIA, 2014, 5, 26 },
                { 2014, 5, 26, 3, ChronoUnit.MILLENNIA, 5014, 5, 26 },

                { 2012, 13, 6, 3, ChronoUnit.MONTHS, 2013, 3, 6 },
                { 2011, 13, 26, 1, ChronoUnit.YEARS, 2012, 13, 26 },
                { 2014, 13, 26, -2, ChronoUnit.YEARS, 2012, 13, 26 },
                { 2012, 13, 26, -6, ChronoUnit.YEARS, 2006, 13, 26 },
                { 2012, 13, 6, -6, ChronoUnit.YEARS, 2006, 13, 6 },
        };
    }

    @DataProvider (name = "plusLeap")
    Object[][] data_plus_leap () {
        return new Object[][] {
                { 2012, 12, 26, 1, ChronoUnit.MONTHS, 2012, 13, 26 },
                { 2012, 13, 26, -1, ChronoUnit.MONTHS, 2012, 12, 26 },
                { 2012, 13, 6, 3, ChronoUnit.YEARS, 2015, 13, 6 },
        };
    }

    @DataProvider (name = "minusLeap")
    Object[][] data_minus_leap () {
        return new Object[][] {
                { 2012, 13, 7, -1, ChronoUnit.MONTHS, 2012, 12, 7 },
                { 2012, 13, 7, 1, ChronoUnit.MONTHS, 2013, 1, 7 },
                { 2012, 13, 6, 3, ChronoUnit.YEARS, 2015, 13, 6 },
        };
    }

    @Test (dataProvider = "plus")
    public void test_plus_TemporalUnit (
            final int year,
            final int month,
            final int dom,
            final long amount,
            final TemporalUnit unit,
            final int expectedYear,
            final int expectedMonth,
            final int expectedDom) {
        assertEquals (InternationalFixedDate.of (year, month, dom).plus (amount, unit), InternationalFixedDate.of (expectedYear, expectedMonth, expectedDom));
    }

    @Test (dataProvider = "plusLeap")
    public void test_plus_leap_TemporalUnit (
            final int year,
            final int month,
            final int dom,
            final long amount,
            final TemporalUnit unit,
            final int expectedYear,
            final int expectedMonth,
            final int expectedDom) {
        test_plus_TemporalUnit (year, month, dom, amount, unit, expectedYear, expectedMonth, expectedDom);
    }

    @Test (dataProvider = "plus")
    public void test_minus_TemporalUnit (
            final int expectedYear,
            final int expectedMonth,
            final int expectedDom,
            final long amount,
            final TemporalUnit unit,
            final int year,
            final int month,
            final int dom) {
        assertEquals (InternationalFixedDate.of (year, month, dom).minus (amount, unit), InternationalFixedDate.of (expectedYear, expectedMonth, expectedDom));
    }

    @Test (dataProvider = "minusLeap")
    public void test_minus_leap_TemporalUnit (
            final int year,
            final int month,
            final int dom,
            final long amount,
            final TemporalUnit unit,
            final int expectedYear,
            final int expectedMonth,
            final int expectedDom) {
        test_minus_TemporalUnit (year, month, dom, amount, unit, expectedYear, expectedMonth, expectedDom);
    }

    @Test (expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_plus_TemporalUnit_unsupported () {
        InternationalFixedDate.of (2012, 6, 10).plus (0, ChronoUnit.MINUTES);
    }

    //-----------------------------------------------------------------------
    // InternationalFixedDate.until
    //-----------------------------------------------------------------------
    @DataProvider (name = "until")
    Object[][] data_until () {
        return new Object[][] {
                { 2014, 5, 26, 2014, 5, 26, ChronoUnit.DAYS, 0 },
                { 2014, 5, 26, 2014, 6, 4, ChronoUnit.DAYS, 6 },
                { 2014, 5, 26, 2014, 5, 20, ChronoUnit.DAYS, -6 },
                { 2014, 5, 26, 2014, 5, 26, ChronoUnit.WEEKS, 0 },
                { 2014, 5, 26, 2014, 6, 4, ChronoUnit.WEEKS, 0 },
                { 2014, 5, 26, 2014, 6, 5, ChronoUnit.WEEKS, 1 },
                { 2014, 5, 26, 2014, 5, 26, ChronoUnit.MONTHS, 0 },
                { 2014, 5, 26, 2014, 6, 25, ChronoUnit.MONTHS, 0 },
                { 2014, 5, 26, 2014, 6, 26, ChronoUnit.MONTHS, 1 },
                { 2014, 5, 26, 2014, 5, 26, ChronoUnit.YEARS, 0 },
                { 2014, 5, 26, 2015, 5, 25, ChronoUnit.YEARS, 0 },
                { 2014, 5, 26, 2015, 5, 26, ChronoUnit.YEARS, 1 },
                { 2014, 5, 26, 2014, 5, 26, ChronoUnit.DECADES, 0 },
                { 2014, 5, 26, 2024, 5, 25, ChronoUnit.DECADES, 0 },
                { 2014, 5, 26, 2024, 5, 26, ChronoUnit.DECADES, 1 },
                { 2014, 5, 26, 2014, 5, 26, ChronoUnit.CENTURIES, 0 },
                { 2014, 5, 26, 2114, 5, 25, ChronoUnit.CENTURIES, 0 },
                { 2014, 5, 26, 2114, 5, 26, ChronoUnit.CENTURIES, 1 },
                { 2014, 5, 26, 2014, 5, 26, ChronoUnit.MILLENNIA, 0 },
                { 2014, 5, 26, 3014, 5, 25, ChronoUnit.MILLENNIA, 0 },
                { 2014, 5, 26, 3014, 5, 26, ChronoUnit.MILLENNIA, 1 },

                { 2011, 13, 26, 2013, 13, 26, ChronoUnit.YEARS, 2 },
                { 2011, 13, 26, 2012, 13, 26, ChronoUnit.YEARS, 1 },
                { 2012, 13, 26, 2011, 13, 26, ChronoUnit.YEARS, -1 },
                { 2012, 13, 26, 2013, 13, 26, ChronoUnit.YEARS, 1 },
                { 2011, 13, 6, 2011, 13, 6, ChronoUnit.YEARS, 0 },
                { 2012, 13, 6, 2012, 13, 6, ChronoUnit.YEARS, 0 },
                { 2011, 13, 1, 2011, 13, 1, ChronoUnit.YEARS, 0 },
                { 2012, 13, 7, 2012, 13, 7, ChronoUnit.YEARS, 0 },
                { 2011, 12, 28, 2012, 13, 1, ChronoUnit.YEARS, 1 },
                { 2012, 13, 1, 2011, 13, 1, ChronoUnit.YEARS, -1 },
                { 2013, 13, 6, 2012, 13, 6, ChronoUnit.YEARS, -1 },
                { 2012, 13, 6, 2013, 13, 6, ChronoUnit.YEARS, 1 },
        };
    }

    @Test (dataProvider = "until")
    public void test_until_TemporalUnit (
            final int year1,
            final int month1,
            final int dom1,
            final int year2,
            final int month2,
            final int dom2,
            final TemporalUnit unit,
            final long expected) {
        InternationalFixedDate start = InternationalFixedDate.of (year1, month1, dom1);
        InternationalFixedDate end = InternationalFixedDate.of (year2, month2, dom2);

        assertEquals (start.until (end, unit), expected);
    }

    @Test (expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_until_TemporalUnit_unsupported () {
        InternationalFixedDate start = InternationalFixedDate.of (2012, 6, 28);
        InternationalFixedDate end = InternationalFixedDate.of (2012, 7, 1);
        start.until (end, ChronoUnit.MINUTES);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period () {
        assertEquals (InternationalFixedDate.of (2014, 5, 26).plus (InternationalFixedChronology.INSTANCE.period (0, 2, 2)), InternationalFixedDate.of (2014, 7, 28));
    }

    @Test (expectedExceptions = DateTimeException.class)
    public void test_plus_Period_ISO () {
        assertEquals (InternationalFixedDate.of (2014, 5, 26).plus (Period.ofMonths (2)), InternationalFixedDate.of (2014, 7, 26));
    }

    @Test
    public void test_minus_Period () {
        ChronoPeriod period = InternationalFixedChronology.INSTANCE.period (0, 2, 3);
        assertEquals (InternationalFixedDate.of (2014, 5, 26).minus (period), InternationalFixedDate.of (2014, 3, 23));
    }

    @Test (expectedExceptions = DateTimeException.class)
    public void test_minus_Period_ISO () {
        ChronoPeriod period = Period.ofMonths (2);
        assertEquals (InternationalFixedDate.of (2014, 5, 26).minus (period), InternationalFixedDate.of (2014, 3, 26));
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    void test_equals () {
        InternationalFixedDate a1 = InternationalFixedDate.of (2000, 1, 3);
        InternationalFixedDate a2 = InternationalFixedDate.of (2000, 1, 3);
        InternationalFixedDate b = InternationalFixedDate.of (2000, 1, 4);
        InternationalFixedDate c = InternationalFixedDate.of (2000, 2, 3);
        InternationalFixedDate d = InternationalFixedDate.of (2001, 1, 3);

        assertEquals (a1.equals (a1), true);
        assertEquals (a1.equals (a2), true);
        assertEquals (a1.equals (b), false);
        assertEquals (a1.equals (c), false);
        assertEquals (a1.equals (d), false);

        assertEquals (a1.equals (null), false);
        assertEquals ("".equals (a1), false);

        assertEquals (a1.hashCode (), a2.hashCode ());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider (name = "toString")
    Object[][] data_toString () {
        return new Object[][] {
                { InternationalFixedDate.of (1, 1, 1), "Ifc CE 1-01-01" },
                { InternationalFixedDate.of (2012, 6, 23), "Ifc CE 2012-06-23" },
        };
    }

    @Test (dataProvider = "toString")
    public void test_toString (final InternationalFixedDate date, final String expected) {
        assertEquals (date.toString (), expected);
    }
}
