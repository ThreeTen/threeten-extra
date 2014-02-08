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
package org.threeten.extra;

import static java.time.temporal.IsoFields.QUARTER_OF_YEAR;
import static java.time.temporal.IsoFields.QUARTER_YEARS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.IsoChronology;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.util.Locale;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test Quarter.
 */
@Test
public class TestQuarter {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(Quarter.class));
        assertTrue(Serializable.class.isAssignableFrom(Quarter.class));
        assertTrue(Comparable.class.isAssignableFrom(Quarter.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(Quarter.class));
    }

    //-----------------------------------------------------------------------
    // of(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int_singleton() {
        for (int i = 1; i <= 4; i++) {
            Quarter test = Quarter.of(i);
            assertEquals(test.getValue(), i);
            assertSame(Quarter.of(i), test);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_of_int_valueTooLow() {
        Quarter.of(0);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_of_int_valueTooHigh() {
        Quarter.of(5);
    }

    //-----------------------------------------------------------------------
    // ofMonth(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_ofMonth_int_singleton() {
        assertEquals(Quarter.ofMonth(1), Quarter.Q1);
        assertEquals(Quarter.ofMonth(2), Quarter.Q1);
        assertEquals(Quarter.ofMonth(3), Quarter.Q1);
        assertEquals(Quarter.ofMonth(4), Quarter.Q2);
        assertEquals(Quarter.ofMonth(5), Quarter.Q2);
        assertEquals(Quarter.ofMonth(6), Quarter.Q2);
        assertEquals(Quarter.ofMonth(7), Quarter.Q3);
        assertEquals(Quarter.ofMonth(8), Quarter.Q3);
        assertEquals(Quarter.ofMonth(9), Quarter.Q3);
        assertEquals(Quarter.ofMonth(10), Quarter.Q4);
        assertEquals(Quarter.ofMonth(11), Quarter.Q4);
        assertEquals(Quarter.ofMonth(12), Quarter.Q4);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_ofMonth_int_valueTooLow() {
        Quarter.ofMonth(0);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_ofMonth_int_valueTooHigh() {
        Quarter.ofMonth(13);
    }

    //-----------------------------------------------------------------------
    // from(Temporal)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_Temporal_Month() {
        assertEquals(Quarter.from(Month.JANUARY), Quarter.Q1);
        assertEquals(Quarter.from(Month.FEBRUARY), Quarter.Q1);
        assertEquals(Quarter.from(Month.MARCH), Quarter.Q1);
        assertEquals(Quarter.from(Month.APRIL), Quarter.Q2);
        assertEquals(Quarter.from(Month.MAY), Quarter.Q2);
        assertEquals(Quarter.from(Month.JUNE), Quarter.Q2);
        assertEquals(Quarter.from(Month.JULY), Quarter.Q3);
        assertEquals(Quarter.from(Month.AUGUST), Quarter.Q3);
        assertEquals(Quarter.from(Month.SEPTEMBER), Quarter.Q3);
        assertEquals(Quarter.from(Month.OCTOBER), Quarter.Q4);
        assertEquals(Quarter.from(Month.NOVEMBER), Quarter.Q4);
        assertEquals(Quarter.from(Month.DECEMBER), Quarter.Q4);
    }

    @Test
    public void test_from_Temporal() {
        assertEquals(Quarter.from(LocalDate.of(2011, 6, 6)), Quarter.Q2);
        assertEquals(Quarter.from(LocalDateTime.of(2012, 2, 3, 12, 30)), Quarter.Q1);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_from_Temporal_invalid_noDerive() {
        Quarter.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_from_Temporal_null() {
        Quarter.from((TemporalAccessor) null);
    }

    //-----------------------------------------------------------------------
    // getDisplayName()
    //-----------------------------------------------------------------------
    @Test
    public void test_getDisplayName() {
        assertEquals(Quarter.Q1.getDisplayName(TextStyle.SHORT, Locale.US), "Q1");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getDisplayName_nullStyle() {
        Quarter.Q1.getDisplayName(null, Locale.US);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getDisplayName_nullLocale() {
        Quarter.Q1.getDisplayName(TextStyle.FULL, null);
    }

    //-----------------------------------------------------------------------
    // get(TemporalField)
    //-----------------------------------------------------------------------
    @DataProvider(name="invalidFields")
    Object[][] data_invalidFields() {
        return new Object[][] {
            {ChronoField.NANO_OF_DAY},
            {ChronoField.HOUR_OF_DAY},
            {ChronoField.DAY_OF_MONTH},
            {ChronoField.MONTH_OF_YEAR},
            {ChronoField.INSTANT_SECONDS},
//            {MockFieldNoValue.INSTANCE},
        };
    }

    @Test
    public void test_get_TemporalField() {
        assertEquals(Quarter.Q1.get(QUARTER_OF_YEAR), 1);
        assertEquals(Quarter.Q2.get(QUARTER_OF_YEAR), 2);
        assertEquals(Quarter.Q3.get(QUARTER_OF_YEAR), 3);
        assertEquals(Quarter.Q4.get(QUARTER_OF_YEAR), 4);
    }

    @Test
    public void test_getLong_TemporalField() {
        assertEquals(Quarter.Q1.getLong(QUARTER_OF_YEAR), 1);
        assertEquals(Quarter.Q2.getLong(QUARTER_OF_YEAR), 2);
        assertEquals(Quarter.Q3.getLong(QUARTER_OF_YEAR), 3);
        assertEquals(Quarter.Q4.getLong(QUARTER_OF_YEAR), 4);
    }

    @Test(dataProvider="invalidFields", expectedExceptions=DateTimeException.class )
    public void test_get_TemporalField_invalidField(TemporalField field) {
        Quarter.Q1.getLong(field);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_TemporalField_null() {
        Quarter.Q1.getLong((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // plus(long), plus(long,unit)
    //-----------------------------------------------------------------------
    @DataProvider(name="plus")
    Object[][] data_plus() {
        return new Object[][] {
            {1, -5, 4},
            {1, -4, 1},
            {1, -3, 2},
            {1, -2, 3},
            {1, -1, 4},
            {1, 0, 1},
            {1, 1, 2},
            {1, 2, 3},
            {1, 3, 4},
            {1, 4, 1},
            {1, 5, 2},
        };
    }

    @Test(dataProvider="plus")
    public void test_plus_long(int base, long amount, int expected) {
        assertEquals(Quarter.of(base).plus(amount), Quarter.of(expected));
    }

    //-----------------------------------------------------------------------
    // minus(long), minus(long,unit)
    //-----------------------------------------------------------------------
    @DataProvider(name="minus")
    Object[][] data_minus() {
        return new Object[][] {
            {1, -5, 2},
            {1, -4, 1},
            {1, -3, 4},
            {1, -2, 3},
            {1, -1, 2},
            {1, 0, 1},
            {1, 1, 4},
            {1, 2, 3},
            {1, 3, 2},
            {1, 4, 1},
            {1, 5, 4},
        };
    }

    @Test(dataProvider="minus")
    public void test_minus_long(int base, long amount, int expected) {
        assertEquals(Quarter.of(base).minus(amount), Quarter.of(expected));
    }

    //-----------------------------------------------------------------------
    // firstMonth()
    //-----------------------------------------------------------------------
    @Test
    public void test_firstMonth() {
        assertEquals(Quarter.Q1.firstMonth(), Month.JANUARY);
        assertEquals(Quarter.Q2.firstMonth(), Month.APRIL);
        assertEquals(Quarter.Q3.firstMonth(), Month.JULY);
        assertEquals(Quarter.Q4.firstMonth(), Month.OCTOBER);
    }

    //-----------------------------------------------------------------------
    // query()
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertEquals(Quarter.Q1.query(TemporalQueries.chronology()), IsoChronology.INSTANCE);
        assertEquals(Quarter.Q1.query(TemporalQueries.localDate()), null);
        assertEquals(Quarter.Q1.query(TemporalQueries.localTime()), null);
        assertEquals(Quarter.Q1.query(TemporalQueries.offset()), null);
        assertEquals(Quarter.Q1.query(TemporalQueries.precision()), QUARTER_YEARS);
        assertEquals(Quarter.Q1.query(TemporalQueries.zone()), null);
        assertEquals(Quarter.Q1.query(TemporalQueries.zoneId()), null);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals(Quarter.Q1.toString(), "Q1");
        assertEquals(Quarter.Q2.toString(), "Q2");
        assertEquals(Quarter.Q3.toString(), "Q3");
        assertEquals(Quarter.Q4.toString(), "Q4");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test
    public void test_enum() {
        assertEquals(Quarter.valueOf("Q4"), Quarter.Q4);
        assertEquals(Quarter.values()[0], Quarter.Q1);
    }

}
