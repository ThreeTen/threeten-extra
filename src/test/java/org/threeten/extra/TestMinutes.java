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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Test class.
 */
@RunWith(DataProviderRunner.class)
public class TestMinutes {
    
    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Minutes.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_deserializationSingleton() throws Exception {
        Minutes test = Minutes.ZERO;
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
        assertSame(Minutes.of(0), Minutes.ZERO);
        assertEquals(Minutes.of(0), Minutes.ZERO);
        assertEquals(0, Minutes.ZERO.getAmount());
        assertFalse(Minutes.ZERO.isNegative());
        assertTrue(Minutes.ZERO.isZero());
        assertFalse(Minutes.ZERO.isPositive());
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_of() {
        assertEquals(0, Minutes.of(0).getAmount());
        assertEquals(1, Minutes.of(1).getAmount());
        assertEquals(2, Minutes.of(2).getAmount());
        assertEquals(Integer.MAX_VALUE, Minutes.of(Integer.MAX_VALUE).getAmount());
        assertEquals(-1, Minutes.of(-1).getAmount());
        assertEquals(-2, Minutes.of(-2).getAmount());
        assertEquals(Integer.MIN_VALUE, Minutes.of(Integer.MIN_VALUE).getAmount());
    }

    @Test
    public void test_ofMinusOne() {
        assertEquals(-1, Hours.of(-1).getAmount());
        assertTrue(Minutes.of(-1).isNegative());
        assertFalse(Minutes.of(-1).isZero());
        assertFalse(Minutes.of(-1).isPositive());
    }

    @Test
    public void test_ofPlusOne() {
        assertEquals(1, Hours.of(1).getAmount());
        assertFalse(Minutes.of(1).isNegative());
        assertFalse(Minutes.of(1).isZero());
        assertTrue(Minutes.of(1).isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofHours() {
        assertEquals(0, Minutes.ofHours(0).getAmount());
        assertEquals(60, Minutes.ofHours(1).getAmount());
        assertEquals(120, Minutes.ofHours(2).getAmount());
        assertEquals((Integer.MAX_VALUE / 60) * 60, Minutes.ofHours(Integer.MAX_VALUE / 60).getAmount());
        assertEquals(-60, Minutes.ofHours(-1).getAmount());
        assertEquals(-120, Minutes.ofHours(-2).getAmount());
        assertEquals((Integer.MIN_VALUE / 60) * 60, Minutes.ofHours(Integer.MIN_VALUE / 60).getAmount());
    }
    
    @Test(expected = ArithmeticException.class)
    public void test_ofHours_overflow() {
        Minutes.ofHours((Integer.MAX_VALUE / 60) + 60);
    }
    
    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_valid() {
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

            {"P0D", 0},
            {"P1D", 1 * 24 * 60},
            {"P2D", 2 * 24 * 60},
            {"P1234D", 1234 * 24 * 60},
            {"P+0D", 0},
            {"P+2D", 2 * 24 * 60},
            {"P-0D", 0},
            {"P-2D", -2 * 24 * 60},

            {"PT0H0M", 0},
            {"PT2H3M", 123},
            {"PT+2H3M", 123},
            {"PT2H+3M", 123},
            {"PT-2H3M", -117},
            {"PT2H-3M", 117},
            {"PT-2H-3M", -123},

            {"P0DT0H0M", 0},
            {"P5DT2H4M", 5 * 24 * 60 + 2 * 60 + 4},
        };
    }

    @Test
    @UseDataProvider("data_valid")
    public void test_parse_CharSequence_valid(String str, int expectedMinutes) {
        assertEquals(Minutes.of(expectedMinutes), Minutes.parse(str));
    }

    @Test
    @UseDataProvider("data_valid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedMinutes) {
        assertEquals(Minutes.of(expectedMinutes), Minutes.parse("+" + str));
    }

    @Test
    @UseDataProvider("data_valid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedMinutes) {
        assertEquals(Minutes.of(-expectedMinutes), Minutes.parse("-" + str));
    }

    @DataProvider
    public static Object[][] data_invalid() {
        return new Object[][] {
            {"P3W"},
            {"P3Q"},
            {"P1M2Y"},

            {"3"},
            {"-3"},
            {"3M"},
            {"-3M"},
            {"P3M"},
            {"P3"},
            {"P-3"},
            {"PM"},
            {"T3"},
            {"P3M"},
            {"PT3S"},
            {"PT3"},
        };
    }

    @Test(expected = DateTimeParseException.class)
    @UseDataProvider("data_invalid")
    public void test_parse_CharSequence_invalid(String str) {
        Minutes.parse(str);
    }

    @Test(expected = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Minutes.parse((CharSequence) null);
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount_Minutes() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(5), test5.plus(Minutes.of(0)));
        assertEquals(Minutes.of(7), test5.plus(Minutes.of(2)));
        assertEquals(Minutes.of(3), test5.plus(Minutes.of(-2)));
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE - 1).plus(Minutes.of(1)));
        assertEquals(Minutes.of(Integer.MIN_VALUE), Minutes.of(Integer.MIN_VALUE + 1).plus(Minutes.of(-1)));
    }
    
    @Test(expected = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE - 1).plus(Minutes.of(2));
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE + 1).plus(Minutes.of(-2));
    }

    @Test(expected = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        Minutes.of(Integer.MIN_VALUE + 1).plus(null);
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_int() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(5), test5.plus(0));
        assertEquals(Minutes.of(7), test5.plus(2));
        assertEquals(Minutes.of(3), test5.plus(-2));
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Minutes.of(Integer.MIN_VALUE), Minutes.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_int_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_int_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE + 1).plus(-2);
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount_Minutes() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(5), test5.minus(Minutes.of(0)));
        assertEquals(Minutes.of(3), test5.minus(Minutes.of(2)));
        assertEquals(Minutes.of(7), test5.minus(Minutes.of(-2)));
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE - 1).minus(Minutes.of(-1)));
        assertEquals(Minutes.of(Integer.MIN_VALUE), Minutes.of(Integer.MIN_VALUE + 1).minus(Minutes.of(1)));
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE - 1).minus(Minutes.of(-2));
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE + 1).minus(Minutes.of(2));
    }

    @Test(expected = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        Minutes.of(Integer.MIN_VALUE + 1).minus(null);
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_int() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(5), test5.minus(0));
        assertEquals(Minutes.of(3), test5.minus(2));
        assertEquals(Minutes.of(7), test5.minus(-2));
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Minutes.of(Integer.MIN_VALUE), Minutes.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_int_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_int_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE + 1).minus(2);
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(0), test5.multipliedBy(0));
        assertEquals(Minutes.of(5), test5.multipliedBy(1));
        assertEquals(Minutes.of(10), test5.multipliedBy(2));
        assertEquals(Minutes.of(15), test5.multipliedBy(3));
        assertEquals(Minutes.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_negate() {
        Minutes test5 = Minutes.of(5);
        assertEquals(Minutes.of(-15), test5.multipliedBy(-3));
    }

    @Test(expected = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Minutes.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expected = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Minutes.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }
    
    //-----------------------------------------------------------------------
    @Test
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

    @Test
    public void test_dividedBy_negate() {
        Minutes test12 = Minutes.of(12);
        assertEquals(Minutes.of(-4), test12.dividedBy(-3));
    }

    @Test(expected = ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        Minutes.of(1).dividedBy(0);
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_negated() {
        assertEquals(Minutes.of(0), Minutes.of(0).negated());
        assertEquals(Minutes.of(-12), Minutes.of(12).negated());
        assertEquals(Minutes.of(12), Minutes.of(-12).negated());
        assertEquals(Minutes.of(-Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE).negated());
    }

    @Test(expected = ArithmeticException.class)
    public void test_negated_overflow() {
        Minutes.of(Integer.MIN_VALUE).negated();
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_abs() {
        assertEquals(Minutes.of(0), Minutes.of(0).abs());
        assertEquals(Minutes.of(12), Minutes.of(12).abs());
        assertEquals(Minutes.of(12), Minutes.of(-12).abs());
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(Integer.MAX_VALUE).abs());
        assertEquals(Minutes.of(Integer.MAX_VALUE), Minutes.of(-Integer.MAX_VALUE).abs());
    }

    @Test(expected = ArithmeticException.class)
    public void test_abs_overflow() {
        Minutes.of(Integer.MIN_VALUE).abs();
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_addTo() {
        LocalTime base = LocalTime.of(11, 30);
        assertEquals(LocalTime.of(11, 30), Minutes.of(0).addTo(base));
        assertEquals(LocalTime.of(11, 36), Minutes.of(6).addTo(base));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_subtractFrom() {
        LocalTime base = LocalTime.of(11, 30);
        assertEquals(LocalTime.of(11, 30), Minutes.of(0).subtractFrom(base));
        assertEquals(LocalTime.of(11, 24), Minutes.of(6).subtractFrom(base));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toDuration() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Duration.ofMinutes(i), Minutes.of(i).toDuration());
        }
    }
    
    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        Minutes test5 = Minutes.of(5);
        Minutes test6 = Minutes.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expected = NullPointerException.class)
    public void test_compareTo_null() {
        Minutes test5 = Minutes.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals() {
        Minutes test5 = Minutes.of(5);
        Minutes test6 = Minutes.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    @Test
    public void test_equals_null() {
        Minutes test5 = Minutes.of(5);
        assertEquals(false, test5.equals(null));
    }

    @Test
    public void test_equals_otherClass() {
        Minutes test5 = Minutes.of(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_hashCode() {
        Minutes test5 = Minutes.of(5);
        Minutes test6 = Minutes.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Minutes test5 = Minutes.of(5);
        assertEquals("PT5M", test5.toString());
        Minutes testM1 = Minutes.of(-1);
        assertEquals("PT-1M", testM1.toString());
    }
    
}
