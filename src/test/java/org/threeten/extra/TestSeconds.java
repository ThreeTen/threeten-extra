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
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestSeconds {
    
    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Seconds.class));
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Seconds orginal = Seconds.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Seconds ser = (Seconds) in.readObject();
        assertSame(Seconds.ZERO, ser);
    }
    
    //-----------------------------------------------------------------------
    public void test_ZERO() {
        assertSame(Seconds.of(0), Seconds.ZERO);
        assertSame(Seconds.of(0), Seconds.ZERO);
        assertEquals(Seconds.ZERO.getAmount(), 0);
    }
    
    //-----------------------------------------------------------------------
    public void test_of() {
        assertEquals(Seconds.of(0).getAmount(), 0);
        assertEquals(Seconds.of(1).getAmount(), 1);
        assertEquals(Seconds.of(2).getAmount(), 2);
        assertEquals(Seconds.of(Integer.MAX_VALUE).getAmount(), Integer.MAX_VALUE);
        assertEquals(Seconds.of(-1).getAmount(), -1);
        assertEquals(Seconds.of(-2).getAmount(), -2);
        assertEquals(Seconds.of(Integer.MIN_VALUE).getAmount(), Integer.MIN_VALUE);
    }
    
    //-----------------------------------------------------------------------
    public void test_ofHours() {
        assertEquals(Seconds.ofHours(0).getAmount(), 0);
        assertEquals(Seconds.ofHours(1).getAmount(), 3600);
        assertEquals(Seconds.ofHours(2).getAmount(), 7200);
        assertEquals(Seconds.ofHours(Integer.MAX_VALUE / 3600).getAmount(), (Integer.MAX_VALUE / 3600) * 3600);
        assertEquals(Seconds.ofHours(-1).getAmount(), -3600);
        assertEquals(Seconds.ofHours(-2).getAmount(), -7200);
        assertEquals(Seconds.ofHours(Integer.MIN_VALUE / 3600).getAmount(), (Integer.MIN_VALUE / 3600) * 3600);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void test_ofHours_overflow() {
        Seconds.ofHours((Integer.MAX_VALUE / 3600) + 3600);
    }
    
    //-----------------------------------------------------------------------
    public void test_ofMinutes() {
        assertEquals(Seconds.ofMinutes(0).getAmount(), 0);
        assertEquals(Seconds.ofMinutes(1).getAmount(), 60);
        assertEquals(Seconds.ofMinutes(2).getAmount(), 120);
        assertEquals(Seconds.ofMinutes(Integer.MAX_VALUE / 60).getAmount(), (Integer.MAX_VALUE / 60) * 60);
        assertEquals(Seconds.ofMinutes(-1).getAmount(), -60);
        assertEquals(Seconds.ofMinutes(-2).getAmount(), -120);
        assertEquals(Seconds.ofMinutes(Integer.MIN_VALUE / 60).getAmount(), (Integer.MIN_VALUE / 60) * 60);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void test_ofMinutes_overflow() {
        Seconds.ofMinutes((Integer.MAX_VALUE / 60) + 60);
    }
    
    //-----------------------------------------------------------------------
    @DataProvider(name = "parseValid")
    Object[][] data_valid() {
        return new Object[][] {
            {"PT0S", 0},
            {"PT1S", 1},
            {"PT2S", 2},
            {"PT123456789S", 123456789},
            {"PT+0S", 0},
            {"PT+2S", 2},
            {"PT-0S", 0},
            {"PT-2S", -2},

            {"PT0M", 0},
            {"PT1M", 60},
            {"PT2M", 120},
            {"PT1234M", 1234 * 60},
            {"PT+0M", 0},
            {"PT+2M", 120},
            {"PT-0M", 0},
            {"PT-2M", -120},

            {"PT0H", 0},
            {"PT1H", 60 * 60},
            {"PT2H", 120 * 60},
            {"PT1234H", 1234 * 60 * 60},
            {"PT+0H", 0},
            {"PT+2H", 120 * 60},
            {"PT-0H", 0},
            {"PT-2H", -120 * 60},

            {"P0D", 0},
            {"P1D", 60 * 60 * 24},
            {"P2D", 120 * 60 * 24},
            {"P1234D", 1234 * 60 * 60 * 24},
            {"P+0D", 0},
            {"P+2D", 120 * 60 * 24},
            {"P-0D", 0},
            {"P-2D", -120 * 60 * 24},

            {"PT0M0S", 0},
            {"PT2M3S", 2 * 60 + 3},
            {"PT+2M3S", 2 * 60 + 3},
            {"PT2M+3S", 2 * 60 + 3},
            {"PT-2M3S", -2 * 60 + 3},
            {"PT2M-3S", 2 * 60 - 3},
            {"PT-2M-3S", -2 * 60 - 3},

            {"PT0H0S", 0},
            {"PT2H3S", 2 * 3600 + 3},
            {"PT+2H3S", 2 * 3600 + 3},
            {"PT2H+3S", 2 * 3600 + 3},
            {"PT-2H3S", -2 * 3600 + 3},
            {"PT2H-3S", 2 * 3600 - 3},
            {"PT-2H-3S", -2 * 3600 - 3},

            {"P0DT0H0M0S", 0},
            {"P5DT2H4M3S", 5 * 86400 + 2 * 3600 + 4 * 60 + 3},
        };
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid(String str, int expectedSeconds) {
        assertEquals(Seconds.parse(str), Seconds.of(expectedSeconds));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedSeconds) {
        assertEquals(Seconds.parse("+" + str), Seconds.of(expectedSeconds));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedSeconds) {
        assertEquals(Seconds.parse("-" + str), Seconds.of(-expectedSeconds));
    }

    @DataProvider(name = "parseInvalid")
    Object[][] data_invalid() {
        return new Object[][] {
            {"P3W"},
            {"P3Q"},
            {"P1M2Y"},

            {"3"},
            {"-3"},
            {"3S"},
            {"-3S"},
            {"P3S"},
            {"P3"},
            {"P-3"},
            {"PS"},
            {"T3"},
            {"PT3"},
        };
    }

    @Test(expectedExceptions = DateTimeParseException.class, dataProvider = "parseInvalid")
    public void test_parse_CharSequence_invalid(String str) {
        Seconds.parse(str);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Seconds.parse((CharSequence) null);
    }
    
    //-----------------------------------------------------------------------
    public void test_plus_TemporalAmount_Seconds() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(5), test5.plus(Seconds.of(0)));
        assertEquals(Seconds.of(7), test5.plus(Seconds.of(2)));
        assertEquals(Seconds.of(3), test5.plus(Seconds.of(-2)));
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE - 1).plus(Seconds.of(1)));
        assertEquals(Seconds.of(Integer.MIN_VALUE), Seconds.of(Integer.MIN_VALUE + 1).plus(Seconds.of(-1)));
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        Seconds.of(Integer.MAX_VALUE - 1).plus(Seconds.of(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        Seconds.of(Integer.MIN_VALUE + 1).plus(Seconds.of(-2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        Seconds.of(Integer.MIN_VALUE + 1).plus(null);
    }
    
    //-----------------------------------------------------------------------
    public void test_plus_int() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(5), test5.plus(0));
        assertEquals(Seconds.of(7), test5.plus(2));
        assertEquals(Seconds.of(3), test5.plus(-2));
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Seconds.of(Integer.MIN_VALUE), Seconds.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooBig() {
        Seconds.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooSmall() {
        Seconds.of(Integer.MIN_VALUE + 1).plus(-2);
    }
    
    //-----------------------------------------------------------------------
    public void test_minus_TemporalAmount_Seconds() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(5), test5.minus(Seconds.of(0)));
        assertEquals(Seconds.of(3), test5.minus(Seconds.of(2)));
        assertEquals(Seconds.of(7), test5.minus(Seconds.of(-2)));
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE - 1).minus(Seconds.of(-1)));
        assertEquals(Seconds.of(Integer.MIN_VALUE), Seconds.of(Integer.MIN_VALUE + 1).minus(Seconds.of(1)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        Seconds.of(Integer.MAX_VALUE - 1).minus(Seconds.of(-2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        Seconds.of(Integer.MIN_VALUE + 1).minus(Seconds.of(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        Seconds.of(Integer.MIN_VALUE + 1).minus(null);
    }
    
    //-----------------------------------------------------------------------
    public void test_minus_int() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(5), test5.minus(0));
        assertEquals(Seconds.of(3), test5.minus(2));
        assertEquals(Seconds.of(7), test5.minus(-2));
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Seconds.of(Integer.MIN_VALUE), Seconds.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooBig() {
        Seconds.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooSmall() {
        Seconds.of(Integer.MIN_VALUE + 1).minus(2);
    }
    
    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(0), test5.multipliedBy(0));
        assertEquals(Seconds.of(5), test5.multipliedBy(1));
        assertEquals(Seconds.of(10), test5.multipliedBy(2));
        assertEquals(Seconds.of(15), test5.multipliedBy(3));
        assertEquals(Seconds.of(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Seconds.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Seconds.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }
    
    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Seconds test12 = Seconds.of(12);
        assertEquals(Seconds.of(12), test12.dividedBy(1));
        assertEquals(Seconds.of(6), test12.dividedBy(2));
        assertEquals(Seconds.of(4), test12.dividedBy(3));
        assertEquals(Seconds.of(3), test12.dividedBy(4));
        assertEquals(Seconds.of(2), test12.dividedBy(5));
        assertEquals(Seconds.of(2), test12.dividedBy(6));
        assertEquals(Seconds.of(-4), test12.dividedBy(-3));
    }

    public void test_dividedBy_negate() {
        Seconds test12 = Seconds.of(12);
        assertEquals(Seconds.of(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        Seconds.of(1).dividedBy(0);
    }
    
    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Seconds.of(0), Seconds.of(0).negated());
        assertEquals(Seconds.of(-12), Seconds.of(12).negated());
        assertEquals(Seconds.of(12), Seconds.of(-12).negated());
        assertEquals(Seconds.of(-Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_negated_overflow() {
        Seconds.of(Integer.MIN_VALUE).negated();
    }
    
    //-----------------------------------------------------------------------
    public void test_abs() {
        assertEquals(Seconds.of(0), Seconds.of(0).abs());
        assertEquals(Seconds.of(12), Seconds.of(12).abs());
        assertEquals(Seconds.of(12), Seconds.of(-12).abs());
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE).abs());
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(-Integer.MAX_VALUE).abs());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_abs_overflow() {
        Seconds.of(Integer.MIN_VALUE).abs();
    }
    
    //-----------------------------------------------------------------------
    public void test_addTo() {
        LocalTime base = LocalTime.of(11, 30);
        assertEquals(Seconds.of(0).addTo(base), LocalTime.of(11, 30));
        assertEquals(Seconds.of(6).addTo(base), LocalTime.of(11, 30, 6));
    }

    //-----------------------------------------------------------------------
    public void test_subtractFrom() {
        LocalTime base = LocalTime.of(11, 30);
        assertEquals(Seconds.of(0).subtractFrom(base), LocalTime.of(11, 30));
        assertEquals(Seconds.of(6).subtractFrom(base), LocalTime.of(11, 29, 54));
    }

    //-----------------------------------------------------------------------
    public void test_toDuration() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Seconds.of(i).toDuration(), Duration.ofSeconds(i));
        }
    }
    
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Seconds test5 = Seconds.of(5);
        Seconds test6 = Seconds.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_null() {
        Seconds test5 = Seconds.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Seconds test5 = Seconds.of(5);
        Seconds test6 = Seconds.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Seconds test5 = Seconds.of(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Seconds test5 = Seconds.of(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Seconds test5 = Seconds.of(5);
        Seconds test6 = Seconds.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Seconds test5 = Seconds.of(5);
        assertEquals("PT5S", test5.toString());
        Seconds testM1 = Seconds.of(-1);
        assertEquals("PT-1S", testM1.toString());
    }
    
}
