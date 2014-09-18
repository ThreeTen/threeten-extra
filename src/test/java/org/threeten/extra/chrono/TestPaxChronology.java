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

import static java.time.temporal.ChronoUnit.DAYS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Ignore;
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
        Assert.assertEquals(chrono.getCalendarType(), null);
    }

    @Test
    @Ignore
    // Ignored because no Pax CDML entry.
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

            {PaxDate.of(6, 13, 6), LocalDate.of(4, 12, 1)},
            {PaxDate.of(6, 13, 7), LocalDate.of(4, 12, 2)},
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

            {PaxDate.of(1582, 10, 4), LocalDate.of(1582, 9, 9)},
            {PaxDate.of(1582, 10, 5), LocalDate.of(1582, 9, 10)},
            {PaxDate.of(1945, 10, 27), LocalDate.of(1945, 10, 6)},

            {PaxDate.of(2012, 6, 22), LocalDate.of(2012, 6, 4)},
            {PaxDate.of(2012, 6, 23), LocalDate.of(2012, 6, 5)},
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

    @Test(dataProvider = "PaxEras")
    public void test_Chronology_eraOf(final Era era, final int eraValue, final String name) {
        assertEquals(era.getValue(), eraValue, "EraValue");
        assertEquals(era.toString(), name, "Era Name");
        assertEquals(era, PaxChronology.INSTANCE.eraOf(eraValue), "PaxChrono.eraOf()");
        final List<Era> eras = PaxChronology.INSTANCE.eras();
        assertTrue(eras.contains(era), "Era is not present in PaxChrono.INSTANCE.eras()");
    }

    @Test
    public void test_Chronology_eraOf_invalid() {
        final int[] badEras = {-500, -498, -497, -1, 2, 3, 500};
        for (final int badEra : badEras) {
            try {
                final Era era = PaxChronology.INSTANCE.eraOf(badEra);
                fail("PaxChrono.eraOf returned " + era + " + for invalid eraValue " + badEra);
            } catch (final DateTimeException ex) {
                assertTrue(true, "Exception caught");
            }
        }
    }

    // -----------------------------------------------------------------------
    // with(WithAdjuster)
    // -----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        final ChronoLocalDate base = PaxChronology.INSTANCE.date(2013, 4, 5);
        final ChronoLocalDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, PaxChronology.INSTANCE.date(2013, 4, 28));
    }

    @Test
    public void test_adjust2() {
        final ChronoLocalDate base = PaxChronology.INSTANCE.date(2012, 13, 2);
        final ChronoLocalDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, PaxChronology.INSTANCE.date(2012, 13, 7));
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
    // Check Pax Eras
    // TODO: Figure out names (if any)
    // -----------------------------------------------------------------------
    @DataProvider(name = "PaxEras")
    Object[][] dataPaxEras() {
        return new Object[][] { {PaxEra.BCE, 0, "BCE"}, {PaxEra.CE, 1, "CE"},};
    }

    // -----------------------------------------------------------------------
    // equals()
    // -----------------------------------------------------------------------
    @Test
    public void test_equals() {
        assertTrue(PaxChronology.INSTANCE.equals(PaxChronology.INSTANCE));
    }

    @Test
    public void testEqualsFalse() {
        assertFalse(PaxChronology.INSTANCE.equals(IsoChronology.INSTANCE));
    }

    // -----------------------------------------------------------------------
    // toString()
    // -----------------------------------------------------------------------
    @DataProvider(name = "toString")
    @SuppressWarnings("checkstyle:indentation")
    Object[][] data_toString() {
        return new Object[][] { {PaxChronology.INSTANCE.date(-3, 5, 8), "Pax BCE 0002-05-08"},
            {PaxChronology.INSTANCE.date(-8, 1, 28), "Pax BCE 0007-01-28"},
            {PaxChronology.INSTANCE.date(2012, 13, 4), "Pax CE 2012-13-04"},
            {PaxChronology.INSTANCE.date(2014, 4, 27), "Pax CE 2014-04-27"},};
    }

    @Test(dataProvider = "toString")
    public void test_toString(final ChronoLocalDate ddate, final String expected) {
        assertEquals(ddate.toString(), expected);
    }

}
