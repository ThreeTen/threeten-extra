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
import java.time.Period;
import java.time.format.DateTimeParseException;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestMinutes {
    
    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Minutes.class));
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Minutes orginal = Minutes.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Minutes ser = (Minutes) in.readObject();
        assertSame(Minutes.ZERO, ser);
    }
    
    //-----------------------------------------------------------------------
    public void test_ZERO() {
        assertSame(Minutes.of(0), Minutes.ZERO);
        assertSame(Minutes.of(0), Minutes.ZERO);
        assertEquals(Minutes.ZERO.getAmount(), 0);
    }
    
    //-----------------------------------------------------------------------
    public void test_of() {
        assertEquals(Minutes.of(0).getAmount(), 0);
        assertEquals(Minutes.of(1).getAmount(), 1);
        assertEquals(Minutes.of(2).getAmount(), 2);
        assertEquals(Minutes.of(Integer.MAX_VALUE).getAmount(), Integer.MAX_VALUE);
        assertEquals(Minutes.of(-1).getAmount(), -1);
        assertEquals(Minutes.of(-2).getAmount(), -2);
        assertEquals(Minutes.of(Integer.MIN_VALUE).getAmount(), Integer.MIN_VALUE);
    }
    
    //-----------------------------------------------------------------------
    public void test_ofHours() {
        assertEquals(Minutes.ofHours(0).getAmount(), 0);
        assertEquals(Minutes.ofHours(1).getAmount(), 60);
        assertEquals(Minutes.ofHours(2).getAmount(), 120);
        assertEquals(Minutes.ofHours(Integer.MAX_VALUE / 60).getAmount(), (Integer.MAX_VALUE / 60) * 60);
        assertEquals(Minutes.ofHours(-1).getAmount(), -60);
        assertEquals(Minutes.ofHours(-2).getAmount(), -120);
        assertEquals(Minutes.ofHours(Integer.MIN_VALUE / 60).getAmount(), (Integer.MIN_VALUE / 60) * 60);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void test_ofHours_overflow() {
        Minutes.ofHours((Integer.MAX_VALUE / 60) + 60);
    }
    
    //-----------------------------------------------------------------------
    @DataProvider(name = "parseValid")
    Object[][] data_valid() {
        return new Object[][] {
            {"PT0M", 0},
            {"PT1M", 1},
            {"PT2M", 2},
            {"PT123456789M", 123456789},
            {"PT+0M", 0},
            {"PT+2M", 2},
            {"PT-0M", 0},
            {"PT-2M", -2},

            {"PT0H", 0},
            {"PT1H", 60},
            {"PT2H", 120},
            {"PT1234H", 1234 * 60},
            {"PT+0H", 0},
            {"PT+2H", 120},
            {"PT-0H", 0},
            {"PT-2H", -120},

            {"PT0H0M", 0},
            {"PT2H3M", 123},
            {"PT+2H3M", 123},
            {"PT2H+3M", 123},
            {"PT-2H3M", -117},
            {"PT2H-3M", 117},
            {"PT-2H-3M", -123},
        };
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid(String str, int expectedMinutes) {
        assertEquals(Minutes.parse(str), Minutes.of(expectedMinutes));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedMinutes) {
        assertEquals(Minutes.parse("+" + str), Minutes.of(expectedMinutes));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedMinutes) {
        assertEquals(Minutes.parse("-" + str), Minutes.of(-expectedMinutes));
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
            {"T3"},
            {"P3M"},
            {"PT3S"},
            {"PT3"},
        };
    }

    @Test(expectedExceptions = DateTimeParseException.class, dataProvider = "parseInvalid")
    public void test_parse_CharSequence_invalid(String str) {
        Minutes.parse(str);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Minutes.parse((CharSequence) null);
    }
    
    //-----------------------------------------------------------------------
    public void test_plus_TemporalAmount_Minutes() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(5), test5.plus(Minutes.of(0)));
        assertEquals(Minutes.of(7), test5.plus(Minutes.of(2)));
        assertEquals(Minutes.of(3), test5.plus(Minutes.of(-2)));
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE - 1).plus(Minutes.of(1)));
        assertEquals(Minutes.of(Integer.MIN_VALUE), Minutes.of(Integer.MIN_VALUE + 1).plus(Minutes.of(-1)));
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE - 1).plus(Minutes.of(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE + 1).plus(Minutes.of(-2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        Minutes.of(Integer.MIN_VALUE + 1).plus(null);
    }
    
    //-----------------------------------------------------------------------
    public void test_plus_int() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(5), test5.plus(0));
        assertEquals(Minutes.of(7), test5.plus(2));
        assertEquals(Minutes.of(3), test5.plus(-2));
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Minutes.of(Integer.MIN_VALUE), Minutes.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE + 1).plus(-2);
    }
    
    //-----------------------------------------------------------------------
    public void test_minus_TemporalAmount_Minutes() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(5), test5.minus(Minutes.of(0)));
        assertEquals(Minutes.of(3), test5.minus(Minutes.of(2)));
        assertEquals(Minutes.of(7), test5.minus(Minutes.of(-2)));
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE - 1).minus(Minutes.of(-1)));
        assertEquals(Minutes.of(Integer.MIN_VALUE), Minutes.of(Integer.MIN_VALUE + 1).minus(Minutes.of(1)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE - 1).minus(Minutes.of(-2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE + 1).minus(Minutes.of(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        Minutes.of(Integer.MIN_VALUE + 1).minus(null);
    }
    
    //-----------------------------------------------------------------------
    public void test_minus_int() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(5), test5.minus(0));
        assertEquals(Minutes.of(3), test5.minus(2));
        assertEquals(Minutes.of(7), test5.minus(-2));
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Minutes.of(Integer.MIN_VALUE), Minutes.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE + 1).minus(2);
    }
    
    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(0), test5.multipliedBy(0));
        assertEquals(Minutes.of(5), test5.multipliedBy(1));
        assertEquals(Minutes.of(10), test5.multipliedBy(2));
        assertEquals(Minutes.of(15), test5.multipliedBy(3));
        assertEquals(Minutes.of(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }
    
    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Minutes test12 = Minutes.of(12);
        assertEquals(Minutes.of(12), test12.dividedBy(1));
        assertEquals(Minutes.of(6), test12.dividedBy(2));
        assertEquals(Minutes.of(4), test12.dividedBy(3));
        assertEquals(Minutes.of(3), test12.dividedBy(4));
        assertEquals(Minutes.of(2), test12.dividedBy(5));
        assertEquals(Minutes.of(2), test12.dividedBy(6));
        assertEquals(Minutes.of(-4), test12.dividedBy(-3));
    }

    public void test_dividedBy_negate() {
        Minutes test12 = Minutes.of(12);
        assertEquals(Minutes.of(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        Minutes.of(1).dividedBy(0);
    }
    
    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Minutes.of(0), Minutes.of(0).negated());
        assertEquals(Minutes.of(-12), Minutes.of(12).negated());
        assertEquals(Minutes.of(12), Minutes.of(-12).negated());
        assertEquals(Minutes.of(-Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_negated_overflow() {
        Minutes.of(Integer.MIN_VALUE).negated();
    }
    
    //-----------------------------------------------------------------------
    public void test_abs() {
        assertEquals(Minutes.of(0), Minutes.of(0).abs());
        assertEquals(Minutes.of(12), Minutes.of(12).abs());
        assertEquals(Minutes.of(12), Minutes.of(-12).abs());
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE).abs());
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(-Integer.MAX_VALUE).abs());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_abs_overflow() {
        Minutes.of(Integer.MIN_VALUE).abs();
    }
    
    //-----------------------------------------------------------------------
    public void test_toDuration() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Minutes.of(i).toDuration(), Duration.ofMinutes(i));
        }
    }
    
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Minutes test5 = Minutes.of(5);
        Minutes test6 = Minutes.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_null() {
        Minutes test5 = Minutes.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Minutes test5 = Minutes.of(5);
        Minutes test6 = Minutes.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Minutes test5 = Minutes.of(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Minutes test5 = Minutes.of(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Minutes test5 = Minutes.of(5);
        Minutes test6 = Minutes.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Minutes test5 = Minutes.of(5);
        assertEquals("PT5M", test5.toString());
        Minutes testM1 = Minutes.of(-1);
        assertEquals("PT-1M", testM1.toString());
    }
    
}
