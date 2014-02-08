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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestYears {

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Years.class));
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Years orginal = Years.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Years ser = (Years) in.readObject();
        assertSame(Years.ZERO, ser);
    }

    //-----------------------------------------------------------------------
    public void test_ZERO() {
        assertSame(Years.of(0), Years.ZERO);
        assertSame(Years.of(0), Years.ZERO);
        assertEquals(Years.ZERO.getYears(), 0);
    }

    public void test_ONE() {
        assertSame(Years.of(1), Years.ONE);
        assertSame(Years.of(1), Years.ONE);
        assertEquals(Years.ONE.getYears(), 1);
    }

    //-----------------------------------------------------------------------
    public void test_of() {
        assertEquals(Years.of(1).getYears(), 1);
        assertEquals(Years.of(2).getYears(), 2);
        assertEquals(Years.of(Integer.MAX_VALUE).getYears(), Integer.MAX_VALUE);
        assertEquals(Years.of(-1).getYears(), -1);
        assertEquals(Years.of(-2).getYears(), -2);
        assertEquals(Years.of(Integer.MIN_VALUE).getYears(), Integer.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    public void test_parse_CharSequence() {
        assertEquals(Years.parse("P0Y"), Years.of(0));
        assertEquals(Years.parse("P1Y"), Years.of(1));
        assertEquals(Years.parse("P2Y"), Years.of(2));
        assertEquals(Years.parse("P123456789Y"), Years.of(123456789));
        assertEquals(Years.parse("P-2Y"), Years.of(-2));
        assertEquals(Years.parse("-P2Y"), Years.of(-2));
        assertEquals(Years.parse("-P-2Y"), Years.of(2));
    }

    @DataProvider(name = "parseInvalid")
    Object[][] data_invalid() {
        return new Object[][] {
                {"P3M"},
                {"P3W"},
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

    @Test(expectedExceptions = DateTimeParseException.class, dataProvider = "parseInvalid")
    public void test_parse_CharSequence_invalid(String str) {
        Years.parse(str);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Years.parse((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_TemporalAmount_Years() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.plus(Years.of(0)));
        assertEquals(Years.of(7), test5.plus(Years.of(2)));
        assertEquals(Years.of(3), test5.plus(Years.of(-2)));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).plus(Years.of(1)));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).plus(Years.of(-1)));
    }

    public void test_plus_TemporalAmount_Period() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.plus(Period.ofYears(0)));
        assertEquals(Years.of(7), test5.plus(Period.ofYears(2)));
        assertEquals(Years.of(3), test5.plus(Period.ofYears(-2)));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).plus(Period.ofYears(1)));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).plus(Period.ofYears(-1)));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_TemporalAmount_PeriodMonths() {
        Years.of(1).plus(Period.ofMonths(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_TemporalAmount_Duration() {
        Years.of(1).plus(Duration.ofHours(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        Years.of(Integer.MAX_VALUE - 1).plus(Years.of(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        Years.of(Integer.MIN_VALUE + 1).plus(Years.of(-2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        Years.of(Integer.MIN_VALUE + 1).plus(null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_int() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.plus(0));
        assertEquals(Years.of(7), test5.plus(2));
        assertEquals(Years.of(3), test5.plus(-2));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooBig() {
        Years.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooSmall() {
        Years.of(Integer.MIN_VALUE + 1).plus(-2);
    }

    //-----------------------------------------------------------------------
    public void test_minus_TemporalAmount_Years() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.minus(Years.of(0)));
        assertEquals(Years.of(3), test5.minus(Years.of(2)));
        assertEquals(Years.of(7), test5.minus(Years.of(-2)));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).minus(Years.of(-1)));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).minus(Years.of(1)));
    }

    public void test_minus_TemporalAmount_Period() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.minus(Period.ofYears(0)));
        assertEquals(Years.of(3), test5.minus(Period.ofYears(2)));
        assertEquals(Years.of(7), test5.minus(Period.ofYears(-2)));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).minus(Period.ofYears(-1)));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).minus(Period.ofYears(1)));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_TemporalAmount_PeriodMonths() {
        Years.of(1).minus(Period.ofMonths(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_TemporalAmount_Duration() {
        Years.of(1).minus(Duration.ofHours(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        Years.of(Integer.MAX_VALUE - 1).minus(Years.of(-2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        Years.of(Integer.MIN_VALUE + 1).minus(Years.of(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        Years.of(Integer.MIN_VALUE + 1).minus(null);
    }

    //-----------------------------------------------------------------------
    public void test_minus_int() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.minus(0));
        assertEquals(Years.of(3), test5.minus(2));
        assertEquals(Years.of(7), test5.minus(-2));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooBig() {
        Years.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooSmall() {
        Years.of(Integer.MIN_VALUE + 1).minus(2);
    }

    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(0), test5.multipliedBy(0));
        assertEquals(Years.of(5), test5.multipliedBy(1));
        assertEquals(Years.of(10), test5.multipliedBy(2));
        assertEquals(Years.of(15), test5.multipliedBy(3));
        assertEquals(Years.of(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Years.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Years.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Years test12 = Years.of(12);
        assertEquals(Years.of(12), test12.dividedBy(1));
        assertEquals(Years.of(6), test12.dividedBy(2));
        assertEquals(Years.of(4), test12.dividedBy(3));
        assertEquals(Years.of(3), test12.dividedBy(4));
        assertEquals(Years.of(2), test12.dividedBy(5));
        assertEquals(Years.of(2), test12.dividedBy(6));
        assertEquals(Years.of(-4), test12.dividedBy(-3));
    }

    public void test_dividedBy_negate() {
        Years test12 = Years.of(12);
        assertEquals(Years.of(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        Years.of(1).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Years.of(0), Years.of(0).negated());
        assertEquals(Years.of(-12), Years.of(12).negated());
        assertEquals(Years.of(12), Years.of(-12).negated());
        assertEquals(Years.of(-Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_negated_overflow() {
        Years.of(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    public void test_abs() {
        assertEquals(Years.of(0), Years.of(0).abs());
        assertEquals(Years.of(12), Years.of(12).abs());
        assertEquals(Years.of(12), Years.of(-12).abs());
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE).abs());
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(-Integer.MAX_VALUE).abs());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_abs_overflow() {
        Years.of(Integer.MIN_VALUE).abs();
    }

    //-----------------------------------------------------------------------
    public void test_toPeriod() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Years.of(i).toPeriod(), Period.ofYears(i));
        }
    }

    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Years test5 = Years.of(5);
        Years test6 = Years.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_null() {
        Years test5 = Years.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Years test5 = Years.of(5);
        Years test6 = Years.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Years test5 = Years.of(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Years test5 = Years.of(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Years test5 = Years.of(5);
        Years test6 = Years.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Years test5 = Years.of(5);
        assertEquals("P5Y", test5.toString());
        Years testM1 = Years.of(-1);
        assertEquals("P-1Y", testM1.toString());
    }

}
