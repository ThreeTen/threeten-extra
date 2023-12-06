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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

/**
 * Test class.
 */
public class TestSeconds {

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Seconds.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_deserializationSingleton() throws Exception {
        Seconds test = Seconds.ZERO;
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
        assertSame(Seconds.ZERO, Seconds.of(0));
        assertEquals(Seconds.ZERO, Seconds.of(0));
        assertEquals(0, Seconds.ZERO.getAmount());
        assertFalse(Seconds.ZERO.isNegative());
        assertTrue(Seconds.ZERO.isZero());
        assertFalse(Seconds.ZERO.isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of() {
        assertEquals(0, Seconds.of(0).getAmount());
        assertEquals(1, Seconds.of(1).getAmount());
        assertEquals(2, Seconds.of(2).getAmount());
        assertEquals(Integer.MAX_VALUE, Seconds.of(Integer.MAX_VALUE).getAmount());
        assertEquals(-1, Seconds.of(-1).getAmount());
        assertEquals(-2, Seconds.of(-2).getAmount());
        assertEquals(Integer.MIN_VALUE, Seconds.of(Integer.MIN_VALUE).getAmount());
    }

    @Test
    public void test_ofMinusOne() {
        assertEquals(-1, Seconds.of(-1).getAmount());
        assertTrue(Seconds.of(-1).isNegative());
        assertFalse(Seconds.of(-1).isZero());
        assertFalse(Seconds.of(-1).isPositive());
    }

    @Test
    public void test_ofPlusOne() {
        assertEquals(1, Seconds.of(1).getAmount());
        assertFalse(Seconds.of(1).isNegative());
        assertFalse(Seconds.of(1).isZero());
        assertTrue(Seconds.of(1).isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofHours() {
        assertEquals(0, Seconds.ofHours(0).getAmount());
        assertEquals(3600, Seconds.ofHours(1).getAmount());
        assertEquals(7200, Seconds.ofHours(2).getAmount());
        assertEquals((Integer.MAX_VALUE / 3600) * 3600, Seconds.ofHours(Integer.MAX_VALUE / 3600).getAmount());
        assertEquals(-3600, Seconds.ofHours(-1).getAmount());
        assertEquals(-7200, Seconds.ofHours(-2).getAmount());
        assertEquals((Integer.MIN_VALUE / 3600) * 3600, Seconds.ofHours(Integer.MIN_VALUE / 3600).getAmount());
    }

    @Test
    public void test_ofHours_overflow() {
        assertThrows(ArithmeticException.class, () -> Seconds.ofHours((Integer.MAX_VALUE / 3600) + 3600));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofMinutes() {
        assertEquals(0, Seconds.ofMinutes(0).getAmount());
        assertEquals(60, Seconds.ofMinutes(1).getAmount());
        assertEquals(120, Seconds.ofMinutes(2).getAmount());
        assertEquals((Integer.MAX_VALUE / 60) * 60, Seconds.ofMinutes(Integer.MAX_VALUE / 60).getAmount());
        assertEquals(-60, Seconds.ofMinutes(-1).getAmount());
        assertEquals(-120, Seconds.ofMinutes(-2).getAmount());
        assertEquals((Integer.MIN_VALUE / 60) * 60, Seconds.ofMinutes(Integer.MIN_VALUE / 60).getAmount());
    }

    @Test
    public void test_ofMinutes_overflow() {
        assertThrows(ArithmeticException.class, () -> Seconds.ofMinutes((Integer.MAX_VALUE / 60) + 60));
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_valid() {
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

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid(String str, int expectedSeconds) {
        assertEquals(Seconds.of(expectedSeconds), Seconds.parse(str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedSeconds) {
        assertEquals(Seconds.of(expectedSeconds), Seconds.parse("+" + str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedSeconds) {
        assertEquals(Seconds.of(-expectedSeconds), Seconds.parse("-" + str));
    }

    public static Object[][] data_invalid() {
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

    @ParameterizedTest
    @MethodSource("data_invalid")
    public void test_parse_CharSequence_invalid(String str) {
        assertThrows(DateTimeParseException.class, () -> Seconds.parse(str));
    }

    @Test
    public void test_parse_CharSequence_null() {
        assertThrows(NullPointerException.class, () -> Seconds.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount_Seconds() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(5), test5.plus(Seconds.of(0)));
        assertEquals(Seconds.of(7), test5.plus(Seconds.of(2)));
        assertEquals(Seconds.of(3), test5.plus(Seconds.of(-2)));
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE - 1).plus(Seconds.of(1)));
        assertEquals(Seconds.of(Integer.MIN_VALUE), Seconds.of(Integer.MIN_VALUE + 1).plus(Seconds.of(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MAX_VALUE - 1).plus(Seconds.of(2)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MIN_VALUE + 1).plus(Seconds.of(-2)));
    }

    @Test
    public void test_plus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Seconds.of(Integer.MIN_VALUE + 1).plus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_int() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(5), test5.plus(0));
        assertEquals(Seconds.of(7), test5.plus(2));
        assertEquals(Seconds.of(3), test5.plus(-2));
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Seconds.of(Integer.MIN_VALUE), Seconds.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test
    public void test_plus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MAX_VALUE - 1).plus(2));
    }

    @Test
    public void test_plus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MIN_VALUE + 1).plus(-2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount_Seconds() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(5), test5.minus(Seconds.of(0)));
        assertEquals(Seconds.of(3), test5.minus(Seconds.of(2)));
        assertEquals(Seconds.of(7), test5.minus(Seconds.of(-2)));
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE - 1).minus(Seconds.of(-1)));
        assertEquals(Seconds.of(Integer.MIN_VALUE), Seconds.of(Integer.MIN_VALUE + 1).minus(Seconds.of(1)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MAX_VALUE - 1).minus(Seconds.of(-2)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MIN_VALUE + 1).minus(Seconds.of(2)));
    }

    @Test
    public void test_minus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Seconds.of(Integer.MIN_VALUE + 1).minus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_int() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(5), test5.minus(0));
        assertEquals(Seconds.of(3), test5.minus(2));
        assertEquals(Seconds.of(7), test5.minus(-2));
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Seconds.of(Integer.MIN_VALUE), Seconds.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test
    public void test_minus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MAX_VALUE - 1).minus(-2));
    }

    @Test
    public void test_minus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MIN_VALUE + 1).minus(2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(0), test5.multipliedBy(0));
        assertEquals(Seconds.of(5), test5.multipliedBy(1));
        assertEquals(Seconds.of(10), test5.multipliedBy(2));
        assertEquals(Seconds.of(15), test5.multipliedBy(3));
        assertEquals(Seconds.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_negate() {
        Seconds test5 = Seconds.of(5);
        assertEquals(Seconds.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2));
    }

    @Test
    public void test_multipliedBy_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2));
    }

    //-----------------------------------------------------------------------
    @Test
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

    @Test
    public void test_dividedBy_negate() {
        Seconds test12 = Seconds.of(12);
        assertEquals(Seconds.of(-4), test12.dividedBy(-3));
    }

    @Test
    public void test_dividedBy_divideByZero() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(1).dividedBy(0));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_negated() {
        assertEquals(Seconds.of(0), Seconds.of(0).negated());
        assertEquals(Seconds.of(-12), Seconds.of(12).negated());
        assertEquals(Seconds.of(12), Seconds.of(-12).negated());
        assertEquals(Seconds.of(-Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE).negated());
    }

    @Test
    public void test_negated_overflow() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MIN_VALUE).negated());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_abs() {
        assertEquals(Seconds.of(0), Seconds.of(0).abs());
        assertEquals(Seconds.of(12), Seconds.of(12).abs());
        assertEquals(Seconds.of(12), Seconds.of(-12).abs());
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(Integer.MAX_VALUE).abs());
        assertEquals(Seconds.of(Integer.MAX_VALUE), Seconds.of(-Integer.MAX_VALUE).abs());
    }

    @Test
    public void test_abs_overflow() {
        assertThrows(ArithmeticException.class, () -> Seconds.of(Integer.MIN_VALUE).abs());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_addTo() {
        LocalTime base = LocalTime.of(11, 30);
        assertEquals(LocalTime.of(11, 30), Seconds.of(0).addTo(base));
        assertEquals(LocalTime.of(11, 30, 6), Seconds.of(6).addTo(base));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_subtractFrom() {
        LocalTime base = LocalTime.of(11, 30);
        assertEquals(LocalTime.of(11, 30), Seconds.of(0).subtractFrom(base));
        assertEquals(LocalTime.of(11, 29, 54), Seconds.of(6).subtractFrom(base));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toDuration() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Duration.ofSeconds(i), Seconds.of(i).toDuration());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        Seconds test5 = Seconds.of(5);
        Seconds test6 = Seconds.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test
    public void test_compareTo_null() {
        Seconds test5 = Seconds.of(5);
        assertThrows(NullPointerException.class, () -> test5.compareTo(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(Seconds.of(5), Seconds.of(5))
            .addEqualityGroup(Seconds.of(6), Seconds.of(6))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Seconds test5 = Seconds.of(5);
        assertEquals("PT5S", test5.toString());
        Seconds testM1 = Seconds.of(-1);
        assertEquals("PT-1S", testM1.toString());
    }

}
