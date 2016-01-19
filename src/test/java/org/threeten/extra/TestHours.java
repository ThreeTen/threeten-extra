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
import java.time.Duration;
import java.time.format.DateTimeParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestHours {
    
    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Hours.class));
    }
    
    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Hours orginal = Hours.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Hours ser = (Hours) in.readObject();
        assertSame(Hours.ZERO, ser);
    }
    
    //-----------------------------------------------------------------------
    public void test_ZERO() {
        assertSame(Hours.of(0), Hours.ZERO);
        assertSame(Hours.of(0), Hours.ZERO);
        assertEquals(Hours.ZERO.getAmount(), 0);
    }
    
    //-----------------------------------------------------------------------
    public void test_of() {
        assertEquals(Hours.of(0).getAmount(), 0);
        assertEquals(Hours.of(1).getAmount(), 1);
        assertEquals(Hours.of(2).getAmount(), 2);
        assertEquals(Hours.of(Integer.MAX_VALUE).getAmount(), Integer.MAX_VALUE);
        assertEquals(Hours.of(-1).getAmount(), -1);
        assertEquals(Hours.of(-2).getAmount(), -2);
        assertEquals(Hours.of(Integer.MIN_VALUE).getAmount(), Integer.MIN_VALUE);
    }
    
    //-----------------------------------------------------------------------
    @DataProvider(name = "parseValid")
    Object[][] data_valid() {
        return new Object[][] {
            {"PT0H", 0},
            {"PT1H", 1},
            {"PT2H", 2},
            {"PT123456789H", 123456789},
            {"PT+0H", 0},
            {"PT+2H", 2},
            {"PT-0H", 0},
            {"PT-2H", -2},
        };
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid(String str, int expectedDays) {
        assertEquals(Hours.parse(str), Hours.of(expectedDays));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedDays) {
        assertEquals(Hours.parse("+" + str), Hours.of(expectedDays));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedDays) {
        assertEquals(Hours.parse("-" + str), Hours.of(-expectedDays));
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
            {"3H"},
            {"-3H"},
            {"P3"},
            {"P-3"},
            {"PH"},
            {"T"},
            {"T3H"},
        };
    }

    @Test(expectedExceptions = DateTimeParseException.class, dataProvider = "parseInvalid")
    public void test_parse_CharSequence_invalid(String str) {
        Hours.parse(str);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Hours.parse((CharSequence) null);
    }
    
    //-----------------------------------------------------------------------
    public void test_plus_TemporalAmount_Hours() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.plus(Hours.of(0)));
        assertEquals(Hours.of(7), test5.plus(Hours.of(2)));
        assertEquals(Hours.of(3), test5.plus(Hours.of(-2)));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).plus(Hours.of(1)));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).plus(Hours.of(-1)));
    }

    public void test_plus_TemporalAmount_Period() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.plus(Duration.ofHours(0)));
        assertEquals(Hours.of(7), test5.plus(Duration.ofHours(2)));
        assertEquals(Hours.of(3), test5.plus(Duration.ofHours(-2)));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).plus(Duration.ofHours(1)));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).plus(Duration.ofHours(-1)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        Hours.of(Integer.MAX_VALUE - 1).plus(Hours.of(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        Hours.of(Integer.MIN_VALUE + 1).plus(Hours.of(-2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        Hours.of(Integer.MIN_VALUE + 1).plus(null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_int() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.plus(0));
        assertEquals(Hours.of(7), test5.plus(2));
        assertEquals(Hours.of(3), test5.plus(-2));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooBig() {
        Hours.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooSmall() {
        Hours.of(Integer.MIN_VALUE + 1).plus(-2);
    }

    //-----------------------------------------------------------------------
    public void test_minus_TemporalAmount_Hours() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.minus(Hours.of(0)));
        assertEquals(Hours.of(3), test5.minus(Hours.of(2)));
        assertEquals(Hours.of(7), test5.minus(Hours.of(-2)));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).minus(Hours.of(-1)));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).minus(Hours.of(1)));
    }

    public void test_minus_TemporalAmount_Duration() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.minus(Duration.ofHours(0)));
        assertEquals(Hours.of(3), test5.minus(Duration.ofHours(2)));
        assertEquals(Hours.of(7), test5.minus(Duration.ofHours(-2)));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).minus(Duration.ofHours(-1)));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).minus(Duration.ofHours(1)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        Hours.of(Integer.MAX_VALUE - 1).minus(Hours.of(-2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        Hours.of(Integer.MIN_VALUE + 1).minus(Hours.of(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        Hours.of(Integer.MIN_VALUE + 1).minus(null);
    }

    //-----------------------------------------------------------------------
    public void test_minus_int() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.minus(0));
        assertEquals(Hours.of(3), test5.minus(2));
        assertEquals(Hours.of(7), test5.minus(-2));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooBig() {
        Hours.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooSmall() {
        Hours.of(Integer.MIN_VALUE + 1).minus(2);
    }

    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(0), test5.multipliedBy(0));
        assertEquals(Hours.of(5), test5.multipliedBy(1));
        assertEquals(Hours.of(10), test5.multipliedBy(2));
        assertEquals(Hours.of(15), test5.multipliedBy(3));
        assertEquals(Hours.of(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Hours.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Hours.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Hours test12 = Hours.of(12);
        assertEquals(Hours.of(12), test12.dividedBy(1));
        assertEquals(Hours.of(6), test12.dividedBy(2));
        assertEquals(Hours.of(4), test12.dividedBy(3));
        assertEquals(Hours.of(3), test12.dividedBy(4));
        assertEquals(Hours.of(2), test12.dividedBy(5));
        assertEquals(Hours.of(2), test12.dividedBy(6));
        assertEquals(Hours.of(-4), test12.dividedBy(-3));
    }

    public void test_dividedBy_negate() {
        Hours test12 = Hours.of(12);
        assertEquals(Hours.of(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        Hours.of(1).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Hours.of(0), Hours.of(0).negated());
        assertEquals(Hours.of(-12), Hours.of(12).negated());
        assertEquals(Hours.of(12), Hours.of(-12).negated());
        assertEquals(Hours.of(-Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_negated_overflow() {
        Hours.of(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    public void test_abs() {
        assertEquals(Hours.of(0), Hours.of(0).abs());
        assertEquals(Hours.of(12), Hours.of(12).abs());
        assertEquals(Hours.of(12), Hours.of(-12).abs());
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE).abs());
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(-Integer.MAX_VALUE).abs());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_abs_overflow() {
        Hours.of(Integer.MIN_VALUE).abs();
    }

    //-----------------------------------------------------------------------
    public void test_toDuration() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Hours.of(i).toPeriod(), Duration.ofHours(i));
        }
    }

    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Hours test5 = Hours.of(5);
        Hours test6 = Hours.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_null() {
        Hours test5 = Hours.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Hours test5 = Hours.of(5);
        Hours test6 = Hours.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Hours test5 = Hours.of(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Hours test5 = Hours.of(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Hours test5 = Hours.of(5);
        Hours test6 = Hours.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Hours test5 = Hours.of(5);
        assertEquals("PT5H", test5.toString());
        Hours testM1 = Hours.of(-1);
        assertEquals("PT-1H", testM1.toString());
    }
    
}
