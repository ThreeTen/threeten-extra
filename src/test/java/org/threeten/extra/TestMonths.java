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
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

/**
 * Test class.
 */
public class TestMonths {

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Months.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_deserializationSingleton() throws Exception {
        Months test = Months.ZERO;
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
        assertSame(Months.ZERO, Months.of(0));
        assertEquals(Months.ZERO, Months.of(0));
        assertEquals(0, Months.ZERO.getAmount());
        assertFalse(Months.ZERO.isNegative());
        assertTrue(Months.ZERO.isZero());
        assertFalse(Months.ZERO.isPositive());
    }

    @Test
    public void test_ONE() {
        assertSame(Months.ONE, Months.of(1));
        assertEquals(Months.ONE, Months.of(1));
        assertEquals(1, Months.ONE.getAmount());
        assertFalse(Months.ONE.isNegative());
        assertFalse(Months.ONE.isZero());
        assertTrue(Months.ONE.isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of() {
        assertEquals(0, Months.of(0).getAmount());
        assertEquals(1, Months.of(1).getAmount());
        assertEquals(2, Months.of(2).getAmount());
        assertEquals(Integer.MAX_VALUE, Months.of(Integer.MAX_VALUE).getAmount());
        assertEquals(-1, Months.of(-1).getAmount());
        assertEquals(-2, Months.of(-2).getAmount());
        assertEquals(Integer.MIN_VALUE, Months.of(Integer.MIN_VALUE).getAmount());
    }

    @Test
    public void test_ofMinusOne() {
        assertEquals(-1, Months.of(-1).getAmount());
        assertTrue(Months.of(-1).isNegative());
        assertFalse(Months.of(-1).isZero());
        assertFalse(Months.of(-1).isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofYears() {
        assertEquals(0, Months.ofYears(0).getAmount());
        assertEquals(12, Months.ofYears(1).getAmount());
        assertEquals(24, Months.ofYears(2).getAmount());
        assertEquals((Integer.MAX_VALUE / 12) * 12, Months.ofYears(Integer.MAX_VALUE / 12).getAmount());
        assertEquals(-12, Months.ofYears(-1).getAmount());
        assertEquals(-24, Months.ofYears(-2).getAmount());
        assertEquals((Integer.MIN_VALUE / 12) * 12, Months.ofYears(Integer.MIN_VALUE / 12).getAmount());
    }

    @Test
    public void test_ofYears_overflow() {
        assertThrows(ArithmeticException.class, () -> Months.ofYears((Integer.MAX_VALUE / 12) + 12));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_from_Period_P0M() {
        assertEquals(Months.of(0), Months.from(Period.ofMonths(0)));
    }

    @Test
    public void test_from_Period_P2M() {
        assertEquals(Months.of(2), Months.from(Period.ofMonths(2)));
    }

    @Test
    public void test_from_P2Y() {
        assertEquals(Months.of(24), Months.from(new MockYearsMonths(2, 0)));
    }

    @Test
    public void test_from_P2Y3M() {
        assertEquals(Months.of(27), Months.from(new MockYearsMonths(2, 3)));
    }

    @Test
    public void test_from_yearsAndMonths() {
        assertEquals(Months.of(41), Months.from(Period.of(3, 5, 0)));
    }

    @Test
    public void test_from_wrongUnit_noConversion() {
        assertThrows(DateTimeException.class, () -> Months.from(Period.ofDays(2)));
    }

    @Test
    public void test_from_null() {
        assertThrows(NullPointerException.class, () -> Months.from((TemporalAmount) null));
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_valid() {
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

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid(String str, int expectedDays) {
        assertEquals(Months.of(expectedDays), Months.parse(str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedDays) {
        assertEquals(Months.of(expectedDays), Months.parse("+" + str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedDays) {
        assertEquals(Months.of(-expectedDays), Months.parse("-" + str));
    }

    public static Object[][] data_invalid() {
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

    @ParameterizedTest
    @MethodSource("data_invalid")
    public void test_parse_CharSequence_invalid(String str) {
        assertThrows(DateTimeParseException.class, () -> Months.parse(str));
    }

    @Test
    public void test_parse_CharSequence_null() {
        assertThrows(NullPointerException.class, () -> Months.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_between() {
        assertEquals(Months.of(24), Months.between(LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1)));
    }

    @Test
    public void test_between_date_null() {
        assertThrows(NullPointerException.class, () -> Months.between(LocalDate.now(), (Temporal) null));
    }

    @Test
    public void test_between_null_date() {
        assertThrows(NullPointerException.class, () -> Months.between((Temporal) null, LocalDate.now()));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(6, Months.of(6).get(ChronoUnit.MONTHS));
    }

    @Test
    public void test_get_invalidType() {
        assertThrows(DateTimeException.class, () -> Months.of(6).get(IsoFields.QUARTER_YEARS));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount_Months() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.plus(Months.of(0)));
        assertEquals(Months.of(7), test5.plus(Months.of(2)));
        assertEquals(Months.of(3), test5.plus(Months.of(-2)));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).plus(Months.of(1)));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).plus(Months.of(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_Period() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.plus(Period.ofMonths(0)));
        assertEquals(Months.of(7), test5.plus(Period.ofMonths(2)));
        assertEquals(Months.of(3), test5.plus(Period.ofMonths(-2)));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).plus(Period.ofMonths(1)));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).plus(Period.ofMonths(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_PeriodDays() {
        assertThrows(DateTimeException.class, () -> Months.of(1).plus(Period.ofDays(2)));
    }

    @Test
    public void test_plus_TemporalAmount_Duration() {
        assertThrows(DateTimeException.class, () -> Months.of(1).plus(Duration.ofHours(2)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MAX_VALUE - 1).plus(Months.of(2)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MIN_VALUE + 1).plus(Months.of(-2)));
    }

    @Test
    public void test_plus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Months.of(Integer.MIN_VALUE + 1).plus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_int() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.plus(0));
        assertEquals(Months.of(7), test5.plus(2));
        assertEquals(Months.of(3), test5.plus(-2));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test
    public void test_plus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MAX_VALUE - 1).plus(2));
    }

    @Test
    public void test_plus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MIN_VALUE + 1).plus(-2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount_Months() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.minus(Months.of(0)));
        assertEquals(Months.of(3), test5.minus(Months.of(2)));
        assertEquals(Months.of(7), test5.minus(Months.of(-2)));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).minus(Months.of(-1)));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).minus(Months.of(1)));
    }

    @Test
    public void test_minus_TemporalAmount_Period() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.minus(Period.ofMonths(0)));
        assertEquals(Months.of(3), test5.minus(Period.ofMonths(2)));
        assertEquals(Months.of(7), test5.minus(Period.ofMonths(-2)));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).minus(Period.ofMonths(-1)));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).minus(Period.ofMonths(1)));
    }

    @Test
    public void test_minus_TemporalAmount_PeriodDays() {
        assertThrows(DateTimeException.class, () -> Months.of(1).minus(Period.ofDays(2)));
    }

    @Test
    public void test_minus_TemporalAmount_Duration() {
        assertThrows(DateTimeException.class, () -> Months.of(1).minus(Duration.ofHours(2)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MAX_VALUE - 1).minus(Months.of(-2)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MIN_VALUE + 1).minus(Months.of(2)));
    }

    @Test
    public void test_minus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Months.of(Integer.MIN_VALUE + 1).minus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_int() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(5), test5.minus(0));
        assertEquals(Months.of(3), test5.minus(2));
        assertEquals(Months.of(7), test5.minus(-2));
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Months.of(Integer.MIN_VALUE), Months.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test
    public void test_minus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MAX_VALUE - 1).minus(-2));
    }

    @Test
    public void test_minus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MIN_VALUE + 1).minus(2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(0), test5.multipliedBy(0));
        assertEquals(Months.of(5), test5.multipliedBy(1));
        assertEquals(Months.of(10), test5.multipliedBy(2));
        assertEquals(Months.of(15), test5.multipliedBy(3));
        assertEquals(Months.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_negate() {
        Months test5 = Months.of(5);
        assertEquals(Months.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2));
    }

    @Test
    public void test_multipliedBy_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2));
    }

    //-----------------------------------------------------------------------
    @Test
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

    @Test
    public void test_dividedBy_negate() {
        Months test12 = Months.of(12);
        assertEquals(Months.of(-4), test12.dividedBy(-3));
    }

    @Test
    public void test_dividedBy_divideByZero() {
        assertThrows(ArithmeticException.class, () -> Months.of(1).dividedBy(0));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_negated() {
        assertEquals(Months.of(0), Months.of(0).negated());
        assertEquals(Months.of(-12), Months.of(12).negated());
        assertEquals(Months.of(12), Months.of(-12).negated());
        assertEquals(Months.of(-Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE).negated());
    }

    @Test
    public void test_negated_overflow() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MIN_VALUE).negated());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_abs() {
        assertEquals(Months.of(0), Months.of(0).abs());
        assertEquals(Months.of(12), Months.of(12).abs());
        assertEquals(Months.of(12), Months.of(-12).abs());
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(Integer.MAX_VALUE).abs());
        assertEquals(Months.of(Integer.MAX_VALUE), Months.of(-Integer.MAX_VALUE).abs());
    }

    @Test
    public void test_abs_overflow() {
        assertThrows(ArithmeticException.class, () -> Months.of(Integer.MIN_VALUE).abs());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_addTo() {
        assertEquals(LocalDate.of(2019, 1, 10), Months.of(0).addTo(LocalDate.of(2019, 1, 10)));
        assertEquals(LocalDate.of(2019, 6, 10), Months.of(5).addTo(LocalDate.of(2019, 1, 10)));
    }

    @Test
    public void test_subtractFrom() {
        assertEquals(LocalDate.of(2019, 1, 10), Months.of(0).subtractFrom(LocalDate.of(2019, 1, 10)));
        assertEquals(LocalDate.of(2018, 8, 10), Months.of(5).subtractFrom(LocalDate.of(2019, 1, 10)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toPeriod() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Period.ofMonths(i), Months.of(i).toPeriod());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        Months test5 = Months.of(5);
        Months test6 = Months.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test
    public void test_compareTo_null() {
        Months test5 = Months.of(5);
        assertThrows(NullPointerException.class, () -> test5.compareTo(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(Months.of(5), Months.of(5))
            .addEqualityGroup(Months.of(6), Months.of(6))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Months test5 = Months.of(5);
        assertEquals("P5M", test5.toString());
        Months testM1 = Months.of(-1);
        assertEquals("P-1M", testM1.toString());
    }

}
