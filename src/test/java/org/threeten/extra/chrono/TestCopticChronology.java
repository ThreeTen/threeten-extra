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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.chrono.Chronology;
import java.time.temporal.TemporalAdjusters;

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

    //-----------------------------------------------------------------------
    // with(TemporalAdjuster)
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

    @Test(groups={"tck"}, expectedExceptions=DateTimeException.class)
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
