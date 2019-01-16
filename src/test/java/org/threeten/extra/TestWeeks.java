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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Test class.
 */
@RunWith(DataProviderRunner.class)
public class TestWeeks {

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Weeks.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_deserializationSingleton() throws Exception {
        Weeks test = Weeks.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertSame(test, ois.readObject());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ZERO() {
        assertSame(Weeks.of(0), Weeks.ZERO);
        assertEquals(Weeks.of(0), Weeks.ZERO);
        assertEquals(0, Weeks.ZERO.getAmount());
    }

    @Test
    public void test_ONE() {
        assertSame(Weeks.of(1), Weeks.ONE);
        assertEquals(Weeks.of(1), Weeks.ONE);
        assertEquals(1, Weeks.ONE.getAmount());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of() {
        assertEquals(1, Weeks.of(1).getAmount());
        assertEquals(2, Weeks.of(2).getAmount());
        assertEquals(Integer.MAX_VALUE, Weeks.of(Integer.MAX_VALUE).getAmount());
        assertEquals(-1, Weeks.of(-1).getAmount());
        assertEquals(-2, Weeks.of(-2).getAmount());
        assertEquals(Integer.MIN_VALUE, Weeks.of(Integer.MIN_VALUE).getAmount());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_from_P0W() {
        assertEquals(Weeks.of(0), Weeks.from(Period.ofWeeks(0)));
    }

    @Test
    public void test_from_P2W() {
        assertEquals(Weeks.of(2), Weeks.from(Period.ofWeeks(2)));
    }

    @Test
    public void test_from_P14D() {
        assertEquals(Weeks.of(2), Weeks.from(Period.ofDays(14)));
    }

    @Test
    public void test_from_Duration() {
        assertEquals(Weeks.of(2), Weeks.from(Duration.ofDays(14)));
    }

    @Test(expected = DateTimeException.class)
    public void test_from_wrongUnit_remainder() {
        Weeks.from(Period.ofDays(3));
    }

    @Test(expected = DateTimeException.class)
    public void test_from_wrongUnit_noConversion() {
        Weeks.from(Period.ofMonths(2));
    }

    @Test(expected = NullPointerException.class)
    public void test_from_null() {
        Weeks.from((TemporalAmount) null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequence() {
        assertEquals(Weeks.of(0), Weeks.parse("P0W"));
        assertEquals(Weeks.of(1), Weeks.parse("P1W"));
        assertEquals(Weeks.of(2), Weeks.parse("P2W"));
        assertEquals(Weeks.of(123456789), Weeks.parse("P123456789W"));
        assertEquals(Weeks.of(-2), Weeks.parse("P-2W"));
        assertEquals(Weeks.of(-2), Weeks.parse("-P2W"));
        assertEquals(Weeks.of(2), Weeks.parse("-P-2W"));
    }

    @DataProvider
    public static Object[][] data_invalid() {
        return new Object[][] {
            {"P3Y"},
            {"P3M"},
            {"P3D"},

            {"3"},
            {"-3"},
            {"3Y"},
            {"-3Y"},
            {"P3"},
            {"P-3"},
            {"PY"},
        };
    }

    @Test(expected = DateTimeParseException.class)
    @UseDataProvider("data_invalid")
    public void test_parse_CharSequence_invalid(String str) {
        Weeks.parse(str);
    }

    @Test(expected = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Weeks.parse((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_between() {
        assertEquals(Weeks.of(104), Weeks.between(LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1)));
    }

    @Test(expected = NullPointerException.class)
    public void test_between_date_null() {
        Weeks.between(LocalDate.now(), (Temporal) null);
    }

    @Test(expected = NullPointerException.class)
    public void test_between_null_date() {
        Weeks.between((Temporal) null, LocalDate.now());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(6, Weeks.of(6).get(ChronoUnit.WEEKS));
    }

    @Test(expected = DateTimeException.class)
    public void test_get_invalidType() {
        Weeks.of(6).get(IsoFields.QUARTER_YEARS);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount_Weeks() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.plus(Weeks.of(0)));
        assertEquals(Weeks.of(7), test5.plus(Weeks.of(2)));
        assertEquals(Weeks.of(3), test5.plus(Weeks.of(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).plus(Weeks.of(1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).plus(Weeks.of(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_Period() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.plus(Period.ofWeeks(0)));
        assertEquals(Weeks.of(7), test5.plus(Period.ofWeeks(2)));
        assertEquals(Weeks.of(3), test5.plus(Period.ofWeeks(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).plus(Period.ofWeeks(1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).plus(Period.ofWeeks(-1)));
    }

    @Test(expected = DateTimeException.class)
    public void test_plus_TemporalAmount_PeriodMonths() {
        Weeks.of(1).plus(Period.ofMonths(2));
    }

    @Test(expected = DateTimeException.class)
    public void test_plus_TemporalAmount_Duration() {
        Weeks.of(1).plus(Duration.ofHours(2));
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).plus(Weeks.of(2));
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).plus(Weeks.of(-2));
    }

    @Test(expected = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        Weeks.of(Integer.MIN_VALUE + 1).plus(null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_int() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.plus(0));
        assertEquals(Weeks.of(7), test5.plus(2));
        assertEquals(Weeks.of(3), test5.plus(-2));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_int_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_int_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).plus(-2);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount_Weeks() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.minus(Weeks.of(0)));
        assertEquals(Weeks.of(3), test5.minus(Weeks.of(2)));
        assertEquals(Weeks.of(7), test5.minus(Weeks.of(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).minus(Weeks.of(-1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).minus(Weeks.of(1)));
    }

    @Test
    public void test_minus_TemporalAmount_Period() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.minus(Period.ofWeeks(0)));
        assertEquals(Weeks.of(3), test5.minus(Period.ofWeeks(2)));
        assertEquals(Weeks.of(7), test5.minus(Period.ofWeeks(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).minus(Period.ofWeeks(-1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).minus(Period.ofWeeks(1)));
    }

    @Test(expected = DateTimeException.class)
    public void test_minus_TemporalAmount_PeriodMonths() {
        Weeks.of(1).minus(Period.ofMonths(2));
    }

    @Test(expected = DateTimeException.class)
    public void test_minus_TemporalAmount_Duration() {
        Weeks.of(1).minus(Duration.ofHours(2));
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).minus(Weeks.of(-2));
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).minus(Weeks.of(2));
    }

    @Test(expected = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        Weeks.of(Integer.MIN_VALUE + 1).minus(null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_int() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.minus(0));
        assertEquals(Weeks.of(3), test5.minus(2));
        assertEquals(Weeks.of(7), test5.minus(-2));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_int_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_int_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).minus(2);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(0), test5.multipliedBy(0));
        assertEquals(Weeks.of(5), test5.multipliedBy(1));
        assertEquals(Weeks.of(10), test5.multipliedBy(2));
        assertEquals(Weeks.of(15), test5.multipliedBy(3));
        assertEquals(Weeks.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_negate() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(-15), test5.multipliedBy(-3));
    }

    @Test(expected = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expected = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_dividedBy() {
        Weeks test12 = Weeks.of(12);
        assertEquals(Weeks.of(12), test12.dividedBy(1));
        assertEquals(Weeks.of(6), test12.dividedBy(2));
        assertEquals(Weeks.of(4), test12.dividedBy(3));
        assertEquals(Weeks.of(3), test12.dividedBy(4));
        assertEquals(Weeks.of(2), test12.dividedBy(5));
        assertEquals(Weeks.of(2), test12.dividedBy(6));
        assertEquals(Weeks.of(-4), test12.dividedBy(-3));
    }

    @Test
    public void test_dividedBy_negate() {
        Weeks test12 = Weeks.of(12);
        assertEquals(Weeks.of(-4), test12.dividedBy(-3));
    }

    @Test(expected = ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        Weeks.of(1).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_negated() {
        assertEquals(Weeks.of(0), Weeks.of(0).negated());
        assertEquals(Weeks.of(-12), Weeks.of(12).negated());
        assertEquals(Weeks.of(12), Weeks.of(-12).negated());
        assertEquals(Weeks.of(-Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE).negated());
    }

    @Test(expected = ArithmeticException.class)
    public void test_negated_overflow() {
        Weeks.of(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_abs() {
        assertEquals(Weeks.of(0), Weeks.of(0).abs());
        assertEquals(Weeks.of(12), Weeks.of(12).abs());
        assertEquals(Weeks.of(12), Weeks.of(-12).abs());
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE).abs());
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(-Integer.MAX_VALUE).abs());
    }

    @Test(expected = ArithmeticException.class)
    public void test_abs_overflow() {
        Weeks.of(Integer.MIN_VALUE).abs();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_addTo() {
        assertEquals(LocalDate.of(2019, 1, 10), Weeks.of(0).addTo(LocalDate.of(2019, 1, 10)));
        assertEquals(LocalDate.of(2019, 2, 14), Weeks.of(5).addTo(LocalDate.of(2019, 1, 10)));
    }

    @Test
    public void test_subtractFrom() {
        assertEquals(LocalDate.of(2019, 1, 10), Weeks.of(0).subtractFrom(LocalDate.of(2019, 1, 10)));
        assertEquals(LocalDate.of(2018, 12, 6), Weeks.of(5).subtractFrom(LocalDate.of(2019, 1, 10)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toPeriod() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Period.ofWeeks(i), Weeks.of(i).toPeriod());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expected = NullPointerException.class)
    public void test_compareTo_null() {
        Weeks test5 = Weeks.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    @Test
    public void test_equals_null() {
        Weeks test5 = Weeks.of(5);
        assertEquals(false, test5.equals(null));
    }

    @Test
    public void test_equals_otherClass() {
        Weeks test5 = Weeks.of(5);
        Object obj = "";
        assertEquals(false, test5.equals(obj));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_hashCode() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Weeks test5 = Weeks.of(5);
        assertEquals("P5W", test5.toString());
        Weeks testM1 = Weeks.of(-1);
        assertEquals("P-1W", testM1.toString());
    }

}
