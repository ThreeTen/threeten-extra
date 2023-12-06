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
public class TestHours {

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Hours.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_deserializationSingleton() throws Exception {
        Hours test = Hours.ZERO;
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
        assertSame(Hours.ZERO, Hours.of(0));
        assertEquals(Hours.ZERO, Hours.of(0));
        assertEquals(0, Hours.ZERO.getAmount());
        assertFalse(Hours.ZERO.isNegative());
        assertTrue(Hours.ZERO.isZero());
        assertFalse(Hours.ZERO.isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of() {
        assertEquals(0, Hours.of(0).getAmount());
        assertEquals(1, Hours.of(1).getAmount());
        assertEquals(2, Hours.of(2).getAmount());
        assertEquals(Integer.MAX_VALUE, Hours.of(Integer.MAX_VALUE).getAmount());
        assertEquals(-1, Hours.of(-1).getAmount());
        assertEquals(-2, Hours.of(-2).getAmount());
        assertEquals(Integer.MIN_VALUE, Hours.of(Integer.MIN_VALUE).getAmount());
    }

    @Test
    public void test_ofMinusOne() {
        assertEquals(-1, Hours.of(-1).getAmount());
        assertTrue(Hours.of(-1).isNegative());
        assertFalse(Hours.of(-1).isZero());
        assertFalse(Hours.of(-1).isPositive());
    }

    @Test
    public void test_ofPlusOne() {
        assertEquals(1, Hours.of(1).getAmount());
        assertFalse(Hours.of(1).isNegative());
        assertFalse(Hours.of(1).isZero());
        assertTrue(Hours.of(1).isPositive());
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_valid() {
        return new Object[][] {
            {"PT0H", 0},
            {"PT1H", 1},
            {"PT2H", 2},
            {"PT123456789H", 123456789},
            {"PT+0H", 0},
            {"PT+2H", 2},
            {"PT-0H", 0},
            {"PT-2H", -2},

            {"P0D", 0 * 24},
            {"P1D", 1 * 24},
            {"P2D", 2 * 24},
            {"P1234567D", 1234567 * 24},
            {"P+0D", 0 * 24},
            {"P+2D", 2 * 24},
            {"P-0D", 0 * 24},
            {"P-2D", -2 * 24},

            {"P0DT0H", 0},
            {"P1DT2H", 1 * 24+ 2},
        };
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid(String str, int expectedDays) {
        assertEquals(Hours.of(expectedDays), Hours.parse(str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedDays) {
        assertEquals(Hours.of(expectedDays), Hours.parse("+" + str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedDays) {
        assertEquals(Hours.of(-expectedDays), Hours.parse("-" + str));
    }

    public static Object[][] data_invalid() {
        return new Object[][] {
            {"P3W"},
            {"P3Q"},
            {"P1M2Y"},

            {"3"},
            {"-3"},
            {"3H"},
            {"-3H"},
            {"P3H"},
            {"P3"},
            {"P-3"},
            {"PH"},
            {"T"},
            {"T3H"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_invalid")
    public void test_parse_CharSequence_invalid(String str) {
        assertThrows(DateTimeParseException.class, () -> Hours.parse(str));
    }

    @Test
    public void test_parse_CharSequence_null() {
        assertThrows(NullPointerException.class, () -> Hours.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount_Hours() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.plus(Hours.of(0)));
        assertEquals(Hours.of(7), test5.plus(Hours.of(2)));
        assertEquals(Hours.of(3), test5.plus(Hours.of(-2)));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).plus(Hours.of(1)));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).plus(Hours.of(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_Period() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.plus(Duration.ofHours(0)));
        assertEquals(Hours.of(7), test5.plus(Duration.ofHours(2)));
        assertEquals(Hours.of(3), test5.plus(Duration.ofHours(-2)));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).plus(Duration.ofHours(1)));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).plus(Duration.ofHours(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MAX_VALUE - 1).plus(Hours.of(2)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MIN_VALUE + 1).plus(Hours.of(-2)));
    }

    @Test
    public void test_plus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Hours.of(Integer.MIN_VALUE + 1).plus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_int() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.plus(0));
        assertEquals(Hours.of(7), test5.plus(2));
        assertEquals(Hours.of(3), test5.plus(-2));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test
    public void test_plus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MAX_VALUE - 1).plus(2));
    }

    @Test
    public void test_plus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MIN_VALUE + 1).plus(-2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount_Hours() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.minus(Hours.of(0)));
        assertEquals(Hours.of(3), test5.minus(Hours.of(2)));
        assertEquals(Hours.of(7), test5.minus(Hours.of(-2)));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).minus(Hours.of(-1)));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).minus(Hours.of(1)));
    }

    @Test
    public void test_minus_TemporalAmount_Duration() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.minus(Duration.ofHours(0)));
        assertEquals(Hours.of(3), test5.minus(Duration.ofHours(2)));
        assertEquals(Hours.of(7), test5.minus(Duration.ofHours(-2)));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).minus(Duration.ofHours(-1)));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).minus(Duration.ofHours(1)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MAX_VALUE - 1).minus(Hours.of(-2)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MIN_VALUE + 1).minus(Hours.of(2)));
    }

    @Test
    public void test_minus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Hours.of(Integer.MIN_VALUE + 1).minus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_int() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(5), test5.minus(0));
        assertEquals(Hours.of(3), test5.minus(2));
        assertEquals(Hours.of(7), test5.minus(-2));
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Hours.of(Integer.MIN_VALUE), Hours.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test
    public void test_minus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MAX_VALUE - 1).minus(-2));
    }

    @Test
    public void test_minus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MIN_VALUE + 1).minus(2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(0), test5.multipliedBy(0));
        assertEquals(Hours.of(5), test5.multipliedBy(1));
        assertEquals(Hours.of(10), test5.multipliedBy(2));
        assertEquals(Hours.of(15), test5.multipliedBy(3));
        assertEquals(Hours.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_negate() {
        Hours test5 = Hours.of(5);
        assertEquals(Hours.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2));
    }

    @Test
    public void test_multipliedBy_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2));
    }

    //-----------------------------------------------------------------------
    @Test
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

    @Test
    public void test_dividedBy_negate() {
        Hours test12 = Hours.of(12);
        assertEquals(Hours.of(-4), test12.dividedBy(-3));
    }

    @Test
    public void test_dividedBy_divideByZero() {
        assertThrows(ArithmeticException.class, () -> Hours.of(1).dividedBy(0));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_negated() {
        assertEquals(Hours.of(0), Hours.of(0).negated());
        assertEquals(Hours.of(-12), Hours.of(12).negated());
        assertEquals(Hours.of(12), Hours.of(-12).negated());
        assertEquals(Hours.of(-Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE).negated());
    }

    @Test
    public void test_negated_overflow() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MIN_VALUE).negated());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_abs() {
        assertEquals(Hours.of(0), Hours.of(0).abs());
        assertEquals(Hours.of(12), Hours.of(12).abs());
        assertEquals(Hours.of(12), Hours.of(-12).abs());
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(Integer.MAX_VALUE).abs());
        assertEquals(Hours.of(Integer.MAX_VALUE), Hours.of(-Integer.MAX_VALUE).abs());
    }

    @Test
    public void test_abs_overflow() {
        assertThrows(ArithmeticException.class, () -> Hours.of(Integer.MIN_VALUE).abs());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_addTo() {
        LocalTime base = LocalTime.of(11, 30);
        assertEquals(LocalTime.of(11, 30), Hours.of(0).addTo(base));
        assertEquals(LocalTime.of(17, 30), Hours.of(6).addTo(base));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_subtractFrom() {
        LocalTime base = LocalTime.of(11, 30);
        assertEquals(LocalTime.of(11, 30), Hours.of(0).subtractFrom(base));
        assertEquals(LocalTime.of(5, 30), Hours.of(6).subtractFrom(base));
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("deprecation")
    @Test
    public void test_toDuration() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Duration.ofHours(i), Hours.of(i).toPeriod());
            assertEquals(Duration.ofHours(i), Hours.of(i).toDuration());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        Hours test5 = Hours.of(5);
        Hours test6 = Hours.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test
    public void test_compareTo_null() {
        Hours test5 = Hours.of(5);
        assertThrows(NullPointerException.class, () -> test5.compareTo(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(Hours.of(5), Hours.of(5))
            .addEqualityGroup(Hours.of(6), Hours.of(6))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Hours test5 = Hours.of(5);
        assertEquals("PT5H", test5.toString());
        Hours testM1 = Hours.of(-1);
        assertEquals("PT-1H", testM1.toString());
    }

}
