/*
 * Copyright (c) 2014-present, Philippe Marschall
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
import java.time.temporal.TemporalAmount;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestWeeks {

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Weeks.class));
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Weeks orginal = Weeks.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Weeks ser = (Weeks) in.readObject();
        assertSame(Weeks.ZERO, ser);
    }

    //-----------------------------------------------------------------------
    public void test_ZERO() {
        assertSame(Weeks.of(0), Weeks.ZERO);
        assertSame(Weeks.of(0), Weeks.ZERO);
        assertEquals(Weeks.ZERO.getAmount(), 0);
    }

    public void test_ONE() {
        assertSame(Weeks.of(1), Weeks.ONE);
        assertSame(Weeks.of(1), Weeks.ONE);
        assertEquals(Weeks.ONE.getAmount(), 1);
    }

    //-----------------------------------------------------------------------
    public void test_of() {
        assertEquals(Weeks.of(1).getAmount(), 1);
        assertEquals(Weeks.of(2).getAmount(), 2);
        assertEquals(Weeks.of(Integer.MAX_VALUE).getAmount(), Integer.MAX_VALUE);
        assertEquals(Weeks.of(-1).getAmount(), -1);
        assertEquals(Weeks.of(-2).getAmount(), -2);
        assertEquals(Weeks.of(Integer.MIN_VALUE).getAmount(), Integer.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    public void test_from() {
        assertEquals(Weeks.from(Period.ofWeeks(2)), Weeks.of(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_from_wrongUnit() {
        Weeks.from(Period.ofYears(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_from_null() {
        Weeks.from((TemporalAmount) null);
    }

    //-----------------------------------------------------------------------
    public void test_parse_CharSequence() {
        assertEquals(Weeks.parse("P0W"), Weeks.of(0));
        assertEquals(Weeks.parse("P1W"), Weeks.of(1));
        assertEquals(Weeks.parse("P2W"), Weeks.of(2));
        assertEquals(Weeks.parse("P123456789W"), Weeks.of(123456789));
        assertEquals(Weeks.parse("P-2W"), Weeks.of(-2));
        assertEquals(Weeks.parse("-P2W"), Weeks.of(-2));
        assertEquals(Weeks.parse("-P-2W"), Weeks.of(2));
    }

    @DataProvider(name = "parseInvalid")
    Object[][] data_invalid() {
        return new Object[][] {
                {"P3Y"},
                {"P3M"},
                {"P3D"},
                
                {"3"},
                {"-3"},
                {"3W"},
                {"-3W"},
                {"P3"},
                {"P-3"},
                {"PW"},
        };
    }

    @Test(expectedExceptions = DateTimeParseException.class, dataProvider = "parseInvalid")
    public void test_parse_CharSequence_invalid(String str) {
        Weeks.parse(str);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Weeks.parse((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_TemporalAmount_Weeks() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.plus(Weeks.of(0)));
        assertEquals(Weeks.of(7), test5.plus(Weeks.of(2)));
        assertEquals(Weeks.of(3), test5.plus(Weeks.of(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).plus(Weeks.of(1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).plus(Weeks.of(-1)));
    }

    public void test_plus_TemporalAmount_Period() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.plus(Period.ofWeeks(0)));
        assertEquals(Weeks.of(7), test5.plus(Period.ofWeeks(2)));
        assertEquals(Weeks.of(3), test5.plus(Period.ofWeeks(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).plus(Period.ofWeeks(1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).plus(Period.ofWeeks(-1)));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_TemporalAmount_PeriodYears() {
        Weeks.of(1).plus(Period.ofYears(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_TemporalAmount_Duration() {
        Weeks.of(1).plus(Duration.ofHours(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).plus(Weeks.of(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).plus(Weeks.of(-2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        Weeks.of(Integer.MIN_VALUE + 1).plus(null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_int() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.plus(0));
        assertEquals(Weeks.of(7), test5.plus(2));
        assertEquals(Weeks.of(3), test5.plus(-2));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).plus(-2);
    }

    //-----------------------------------------------------------------------
    public void test_minus_TemporalAmount_Weeks() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.minus(Weeks.of(0)));
        assertEquals(Weeks.of(3), test5.minus(Weeks.of(2)));
        assertEquals(Weeks.of(7), test5.minus(Weeks.of(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).minus(Weeks.of(-1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).minus(Weeks.of(1)));
    }

    public void test_minus_TemporalAmount_Period() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.minus(Period.ofWeeks(0)));
        assertEquals(Weeks.of(3), test5.minus(Period.ofWeeks(2)));
        assertEquals(Weeks.of(7), test5.minus(Period.ofWeeks(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).minus(Period.ofWeeks(-1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).minus(Period.ofWeeks(1)));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_TemporalAmount_PeriodYears() {
        Weeks.of(1).minus(Period.ofYears(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_TemporalAmount_Duration() {
        Weeks.of(1).minus(Duration.ofHours(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).minus(Weeks.of(-2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).minus(Weeks.of(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        Weeks.of(Integer.MIN_VALUE + 1).minus(null);
    }

    //-----------------------------------------------------------------------
    public void test_minus_int() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.minus(0));
        assertEquals(Weeks.of(3), test5.minus(2));
        assertEquals(Weeks.of(7), test5.minus(-2));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).minus(2);
    }

    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(0), test5.multipliedBy(0));
        assertEquals(Weeks.of(5), test5.multipliedBy(1));
        assertEquals(Weeks.of(10), test5.multipliedBy(2));
        assertEquals(Weeks.of(15), test5.multipliedBy(3));
        assertEquals(Weeks.of(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
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

    public void test_dividedBy_negate() {
        Weeks test12 = Weeks.of(12);
        assertEquals(Weeks.of(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        Weeks.of(1).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Weeks.of(0), Weeks.of(0).negated());
        assertEquals(Weeks.of(-12), Weeks.of(12).negated());
        assertEquals(Weeks.of(12), Weeks.of(-12).negated());
        assertEquals(Weeks.of(-Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_negated_overflow() {
        Weeks.of(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    public void test_abs() {
        assertEquals(Weeks.of(0), Weeks.of(0).abs());
        assertEquals(Weeks.of(12), Weeks.of(12).abs());
        assertEquals(Weeks.of(12), Weeks.of(-12).abs());
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE).abs());
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(-Integer.MAX_VALUE).abs());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_abs_overflow() {
        Weeks.of(Integer.MIN_VALUE).abs();
    }

    //-----------------------------------------------------------------------
    public void test_toPeriod() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Weeks.of(i).toPeriod(), Period.ofWeeks(i));
        }
    }

    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_null() {
        Weeks test5 = Weeks.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Weeks test5 = Weeks.of(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Weeks test5 = Weeks.of(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Weeks test5 = Weeks.of(5);
        assertEquals("P5W", test5.toString());
        Weeks testM1 = Weeks.of(-1);
        assertEquals("P-1W", testM1.toString());
    }

}
