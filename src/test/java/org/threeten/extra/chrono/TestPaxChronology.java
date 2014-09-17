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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Clockwork-Muse
 */
@Test
@SuppressWarnings({ "static-method", "javadoc", "checkstyle:magicnumber", "checkstyle:javadocmethod", "checkstyle:designforextension" })
public class TestPaxChronology {
    // -----------------------------------------------------------------------
    // with(WithAdjuster)
    // -----------------------------------------------------------------------
    @Test
    public void testAdjust1() {
        final ChronoLocalDate base = PaxChronology.INSTANCE.date(2013, 4, 5);
        final ChronoLocalDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, PaxChronology.INSTANCE.date(2013, 4, 28));
    }

    @Test
    public void testAdjust2() {
        final ChronoLocalDate base = PaxChronology.INSTANCE.date(2012, 13, 2);
        final ChronoLocalDate test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, PaxChronology.INSTANCE.date(2012, 13, 7));
    }

    // -----------------------------------------------------------------------
    // PaxDate.with(Local*)
    // -----------------------------------------------------------------------
    @Test
    public void testAdjustToLocalDate() {
        final ChronoLocalDate jdate = PaxChronology.INSTANCE.date(2200, 4, 3);
        final ChronoLocalDate test = jdate.with(LocalDate.of(2014, 6, 29));
        assertEquals(test, PaxChronology.INSTANCE.date(2014, 6, 16));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void testAdjustToMonth() {
        final ChronoLocalDate jdate = PaxChronology.INSTANCE.date(2014, 2, 4);
        jdate.with(Month.APRIL);
    }

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void testBadDates(final int year, final int month, final int dom) {
        PaxChronology.INSTANCE.date(year, month, dom);
    }

    // -----------------------------------------------------------------------
    // Chrono.ofName("Pax") Lookup by name
    // -----------------------------------------------------------------------
    @Test
    @SuppressWarnings("checkstyle:multiplestringliterals")
    public void testChronoByName() {
        final Chronology c = PaxChronology.INSTANCE;
        final Chronology test = Chronology.of("Pax");
        Assert.assertNotNull(test, "The Pax calendar could not be found byName");
        Assert.assertEquals(test.getId(), "Pax", "ID mismatch");
        Assert.assertEquals(test.getCalendarType(), null, "Value provided");
        Assert.assertEquals(test, c);
    }

    @Test
    public void testEqualsFalse() {
        assertFalse(PaxChronology.INSTANCE.equals(IsoChronology.INSTANCE));
    }

    // -----------------------------------------------------------------------
    // equals()
    // -----------------------------------------------------------------------
    @Test
    public void testEqualsTrue() {
        assertTrue(PaxChronology.INSTANCE.equals(PaxChronology.INSTANCE));
    }

    @Test(dataProvider = "samples")
    public void testFromCalendrical(final ChronoLocalDate ddate, final LocalDate iso) {
        assertEquals(PaxChronology.INSTANCE.date(iso), ddate);
    }

    // -----------------------------------------------------------------------
    // LocalDate.with(PaxDate)
    // -----------------------------------------------------------------------
    @Test
    public void testLocalDateAdjustToPaxDate() {
        final ChronoLocalDate jdate = PaxChronology.INSTANCE.date(2014, 6, 16);
        final LocalDate test = LocalDate.MIN.with(jdate);
        assertEquals(test, LocalDate.of(2014, 6, 29));
    }

    @Test
    public void testLocalDateTimeAdjustToPaxDate() {
        final ChronoLocalDate jdate = PaxChronology.INSTANCE.date(2014, 6, 16);
        final LocalDateTime test = LocalDateTime.MIN.with(jdate);
        assertEquals(test, LocalDateTime.of(2014, 6, 29, 0, 0));
    }

    @Test
    public void testPaxInvalidEras() {
        final int[] badEras = { -500, -498, -497, -1, 2, 3, 500 };
        for (final int badEra : badEras) {
            try {
                final Era era = PaxChronology.INSTANCE.eraOf(badEra);
                fail("PaxChrono.eraOf returned " + era + " + for invalid eraValue " + badEra);
            } catch (final DateTimeException ex) {
                assertTrue(true, "Exception caught");
            }
        }
    }

    @Test(dataProvider = "PaxEras")
    public void testPaxValidEras(final Era era, final int eraValue, final String name) {
        assertEquals(era.getValue(), eraValue, "EraValue");
        assertEquals(era.toString(), name, "Era Name");
        assertEquals(era, PaxChronology.INSTANCE.eraOf(eraValue), "PaxChrono.eraOf()");
        final List<Era> eras = PaxChronology.INSTANCE.eras();
        assertTrue(eras.contains(era), "Era is not present in PaxChrono.INSTANCE.eras()");
    }

    @Test(dataProvider = "samples")
    public void testToLocalDate(final ChronoLocalDate ddate, final LocalDate iso) {
        assertEquals(LocalDate.from(ddate), iso);
    }

    @Test(dataProvider = "toString")
    public void testToString(final ChronoLocalDate ddate, final String expected) {
        assertEquals(ddate.toString(), expected);
    }

    @DataProvider(name = "badDates")
    @SuppressWarnings("checkstyle:indentation")
    Object[][] dataBadDates() {
        return new Object[][] { { 2012, 0, 1 },
                { 2012, 1, 0 },
                { 2012, 0, 0 },
                { 2012, -1, 1 },
                { 2012, 0, 1 },
                { 2012, 15, 1 },
                { 2012, 16, 1 },
                { 2012, 1, -1 },
                { 2012, 1, 0 },
                { 2012, 1, 29 },
                { 2012, 13, -1 },
                { 2012, 13, 0 },
                { 2012, 13, 8 },
        };
    }

    // -----------------------------------------------------------------------
    // Check Pax Eras
    // TODO: Figure out names (if any)
    // -----------------------------------------------------------------------
    @DataProvider(name = "PaxEras")
    Object[][] dataPaxEras() {
        return new Object[][] { { PaxEra.BCE, 0, "BCE" }, { PaxEra.CE, 1, "CE" }, };
    }

    // -----------------------------------------------------------------------
    // creation, toLocalDate()
    // -----------------------------------------------------------------------
    @DataProvider(name = "samples")
    @SuppressWarnings("checkstyle:indentation")
    Object[][] dataSamples() {
        return new Object[][] { { PaxChronology.INSTANCE.date(1, 1, 1), LocalDate.of(0, 12, 31) },
                { PaxChronology.INSTANCE.date(1, 1, 2), LocalDate.of(1, 1, 1) },
                { PaxChronology.INSTANCE.date(1, 1, 3), LocalDate.of(1, 1, 2) },
                { PaxChronology.INSTANCE.date(2, 1, 1), LocalDate.of(1, 12, 30) },
                { PaxChronology.INSTANCE.date(3, 1, 1), LocalDate.of(2, 12, 29) },
                { PaxChronology.INSTANCE.date(3, 11, 15), LocalDate.of(3, 10, 25) },
                { PaxChronology.INSTANCE.date(2014, 3, 5), LocalDate.of(2014, 3, 3) },
                { PaxChronology.INSTANCE.date(2014, 11, 23), LocalDate.of(2014, 10, 28) },
                { PaxChronology.INSTANCE.date(2014, 11, 24), LocalDate.of(2014, 10, 29) },
        };
    }

    // -----------------------------------------------------------------------
    // toString()
    // -----------------------------------------------------------------------
    @DataProvider(name = "toString")
    @SuppressWarnings("checkstyle:indentation")
    Object[][] dataToString() {
        return new Object[][] { { PaxChronology.INSTANCE.date(-3, 5, 8), "Pax BCE 0002-05-08" },
                { PaxChronology.INSTANCE.date(-8, 1, 28), "Pax BCE 0007-01-28" },
                { PaxChronology.INSTANCE.date(2012, 13, 4), "Pax CE 2012-13-04" },
                { PaxChronology.INSTANCE.date(2014, 4, 27), "Pax CE 2014-04-27" }, };
    }

}
