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
import java.time.temporal.TemporalAmount;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestMonths {

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Months.class));
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Months orginal = Months.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Months ser = (Months) in.readObject();
        assertSame(Months.ZERO, ser);
    }

    //-----------------------------------------------------------------------
    public void test_ZERO() {
        assertSame(Months.of(0), Months.ZERO);
        assertSame(Months.of(0), Months.ZERO);
        assertEquals(Months.ZERO.getAmount(), 0);
    }

    public void test_ONE() {
        assertSame(Months.of(1), Months.ONE);
        assertSame(Months.of(1), Months.ONE);
        assertEquals(Months.ONE.getAmount(), 1);
    }

    //-----------------------------------------------------------------------
    public void test_of() {
        assertEquals(Months.of(0).getAmount(), 0);
        assertEquals(Months.of(1).getAmount(), 1);
        assertEquals(Months.of(2).getAmount(), 2);
        assertEquals(Months.of(Integer.MAX_VALUE).getAmount(), Integer.MAX_VALUE);
        assertEquals(Months.of(-1).getAmount(), -1);
        assertEquals(Months.of(-2).getAmount(), -2);
        assertEquals(Months.of(Integer.MIN_VALUE).getAmount(), Integer.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    public void test_ofYears() {
        assertEquals(Months.ofYears(0).getAmount(), 0);
        assertEquals(Months.ofYears(1).getAmount(), 12);
        assertEquals(Months.ofYears(2).getAmount(), 24);
        assertEquals(Months.ofYears(Integer.MAX_VALUE / 12).getAmount(), (Integer.MAX_VALUE / 12) * 12);
        assertEquals(Months.ofYears(-1).getAmount(), -12);
        assertEquals(Months.ofYears(-2).getAmount(), -24);
        assertEquals(Months.ofYears(Integer.MIN_VALUE / 12).getAmount(), (Integer.MIN_VALUE / 12) * 12);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_ofYears_overflow() {
        Months.ofYears((Integer.MAX_VALUE / 12) + 12);
    }

    //-----------------------------------------------------------------------
    public void test_from_Period_P0M() {
        assertEquals(Months.from(Period.ofMonths(0)), Months.of(0));
    }

    public void test_from_Period_P2M() {
        assertEquals(Months.from(Period.ofMonths(2)), Months.of(2));
    }

    public void test_from_P2Y() {
        assertEquals(Months.from(new MockYearsMonths(2, 0)), Months.of(24));
    }

    public void test_from_P2Y3M() {
        assertEquals(Months.from(new MockYearsMonths(2, 3)), Months.of(27));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_from_wrongUnit() {
        Months.from(Period.ofDays(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_from_null() {
        Months.from((TemporalAmount) null);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "parseValid")
    Object[][] data_valid() {
        return new Object[][] {
                {"P0M", 0},
                {"P1M", 1},
                {"P2M", 2},
                {"P123456789M", 123456789},
                {"P+0M", 0},
                {"P+2M", 2},
                {"P-0M", 0},
                {"P-2M", -2},
                
                {"P0Y", 0},
                {"P1Y", 12},
                {"P2Y", 24},
                {"P1234567Y", 1234567 * 12},
                {"P+0Y", 0},
                {"P+2Y", 24},
                {"P-0Y", 0},
                {"P-2Y", -24},
                
                {"P0Y0M", 0},
                {"P2Y3M", 27},
                {"P+2Y3M", 27},
                {"P2Y+3M", 27},
                {"P-2Y3M", -21},
                {"P2Y-3M", 21},
                {"P-2Y-3M", -27},
        };
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid(String str, int expectedDays) {
        assertEquals(Months.parse(str), Months.of(expectedDays));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedDays) {
        assertEquals(Months.parse("+" + str), Months.of(expectedDays));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedDays) {
        assertEquals(Months.parse("-" + str), Months.of(-expectedDays));
    }

    @DataProvider(name = "parseInvalid")
    Object[][] data_invalid() {
        return new Object[][] {
                {"P3W"},
                {"P3D"},
                {"P3Q"},
                {"P1M2Y"},
                
                {"3"},
                {"-3"},
                {"3M"},
                {"-3M"},
                {"P3"},
                {"P-3"},
                {"PM"},
        };
    }

    @Test(expectedExceptions = DateTimeParseException.class, dataProvider = "parseInvalid")
    public void test_parse_CharSequence_invalid(String str) {
        Months.parse(str);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Months.parse((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_TemporalAmount_Months() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.plus(Months.of(0)));
        assertEquals(Months.of(7), test5.plus(Months.of(2)));
        assertEquals(Months.of(3), test5.plus(Months.of(-2)));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).plus(Months.of(1)));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).plus(Months.of(-1)));
    }

    public void test_plus_TemporalAmount_Period() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.plus(Period.ofMonths(0)));
        assertEquals(Months.of(7), test5.plus(Period.ofMonths(2)));
        assertEquals(Months.of(3), test5.plus(Period.ofMonths(-2)));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).plus(Period.ofMonths(1)));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).plus(Period.ofMonths(-1)));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_TemporalAmount_PeriodDays() {
        Months.of(1).plus(Period.ofDays(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_TemporalAmount_Duration() {
        Months.of(1).plus(Duration.ofHours(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        Months.of(Integer.MAX_VALUE - 1).plus(Months.of(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        Months.of(Integer.MIN_VALUE + 1).plus(Months.of(-2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        Months.of(Integer.MIN_VALUE + 1).plus(null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_int() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.plus(0));
        assertEquals(Months.of(7), test5.plus(2));
        assertEquals(Months.of(3), test5.plus(-2));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooBig() {
        Months.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooSmall() {
        Months.of(Integer.MIN_VALUE + 1).plus(-2);
    }

    //-----------------------------------------------------------------------
    public void test_minus_TemporalAmount_Months() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.minus(Months.of(0)));
        assertEquals(Months.of(3), test5.minus(Months.of(2)));
        assertEquals(Months.of(7), test5.minus(Months.of(-2)));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).minus(Months.of(-1)));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).minus(Months.of(1)));
    }

    public void test_minus_TemporalAmount_Period() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.minus(Period.ofMonths(0)));
        assertEquals(Months.of(3), test5.minus(Period.ofMonths(2)));
        assertEquals(Months.of(7), test5.minus(Period.ofMonths(-2)));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).minus(Period.ofMonths(-1)));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).minus(Period.ofMonths(1)));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_TemporalAmount_PeriodDays() {
        Months.of(1).minus(Period.ofDays(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_TemporalAmount_Duration() {
        Months.of(1).minus(Duration.ofHours(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        Months.of(Integer.MAX_VALUE - 1).minus(Months.of(-2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        Months.of(Integer.MIN_VALUE + 1).minus(Months.of(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        Months.of(Integer.MIN_VALUE + 1).minus(null);
    }

    //-----------------------------------------------------------------------
    public void test_minus_int() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.minus(0));
        assertEquals(Months.of(3), test5.minus(2));
        assertEquals(Months.of(7), test5.minus(-2));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooBig() {
        Months.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooSmall() {
        Months.of(Integer.MIN_VALUE + 1).minus(2);
    }

    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(0), test5.multipliedBy(0));
        assertEquals(Months.of(5), test5.multipliedBy(1));
        assertEquals(Months.of(10), test5.multipliedBy(2));
        assertEquals(Months.of(15), test5.multipliedBy(3));
        assertEquals(Months.of(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Months.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Months.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Months test12 = Months.of(12);
        assertEquals(Months.of(12), test12.dividedBy(1));
        assertEquals(Months.of(6), test12.dividedBy(2));
        assertEquals(Months.of(4), test12.dividedBy(3));
        assertEquals(Months.of(3), test12.dividedBy(4));
        assertEquals(Months.of(2), test12.dividedBy(5));
        assertEquals(Months.of(2), test12.dividedBy(6));
        assertEquals(Months.of(-4), test12.dividedBy(-3));
    }

    public void test_dividedBy_negate() {
        Months test12 = Months.of(12);
        assertEquals(Months.of(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        Months.of(1).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Months.of(0), Months.of(0).negated());
        assertEquals(Months.of(-12), Months.of(12).negated());
        assertEquals(Months.of(12), Months.of(-12).negated());
        assertEquals(Months.of(-Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_negated_overflow() {
        Months.of(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    public void test_abs() {
        assertEquals(Months.of(0), Months.of(0).abs());
        assertEquals(Months.of(12), Months.of(12).abs());
        assertEquals(Months.of(12), Months.of(-12).abs());
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE).abs());
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(-Integer.MAX_VALUE).abs());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_abs_overflow() {
        Months.of(Integer.MIN_VALUE).abs();
    }

    //-----------------------------------------------------------------------
    public void test_toPeriod() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Months.of(i).toPeriod(), Period.ofMonths(i));
        }
    }

    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Months test5 = Months.of(5);
        Months test6 = Months.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_null() {
        Months test5 = Months.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Months test5 = Months.of(5);
        Months test6 = Months.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Months test5 = Months.of(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Months test5 = Months.of(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Months test5 = Months.of(5);
        Months test6 = Months.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Months test5 = Months.of(5);
        assertEquals("P5M", test5.toString());
        Months testM1 = Months.of(-1);
        assertEquals("P-1M", testM1.toString());
    }

}
