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
public class TestDays {

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Days.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_deserializationSingleton() throws Exception {
        Days test = Days.ZERO;
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
        assertSame(Days.ZERO, Days.of(0));
        assertEquals(Days.ZERO, Days.of(0));
        assertEquals(0, Days.ZERO.getAmount());
        assertFalse(Days.ZERO.isNegative());
        assertTrue(Days.ZERO.isZero());
        assertFalse(Days.ZERO.isPositive());
    }

    @Test
    public void test_ONE() {
        assertSame(Days.ONE, Days.of(1));
        assertEquals(Days.ONE, Days.of(1));
        assertEquals(1, Days.ONE.getAmount());
        assertFalse(Days.ONE.isNegative());
        assertFalse(Days.ONE.isZero());
        assertTrue(Days.ONE.isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of() {
        assertEquals(0, Days.of(0).getAmount());
        assertEquals(1, Days.of(1).getAmount());
        assertEquals(2, Days.of(2).getAmount());
        assertEquals(Integer.MAX_VALUE, Days.of(Integer.MAX_VALUE).getAmount());
        assertEquals(-1, Days.of(-1).getAmount());
        assertEquals(-2, Days.of(-2).getAmount());
        assertEquals(Integer.MIN_VALUE, Days.of(Integer.MIN_VALUE).getAmount());
    }

    @Test
    public void test_ofMinusOne() {
        assertEquals(-1, Days.of(-1).getAmount());
        assertTrue(Days.of(-1).isNegative());
        assertFalse(Days.of(-1).isZero());
        assertFalse(Days.of(-1).isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofWeeks() {
        assertEquals(0, Days.ofWeeks(0).getAmount());
        assertEquals(7, Days.ofWeeks(1).getAmount());
        assertEquals(14, Days.ofWeeks(2).getAmount());
        assertEquals((Integer.MAX_VALUE / 7) * 7, Days.ofWeeks(Integer.MAX_VALUE / 7).getAmount());
        assertEquals(-7, Days.ofWeeks(-1).getAmount());
        assertEquals(-14, Days.ofWeeks(-2).getAmount());
        assertEquals((Integer.MIN_VALUE / 7) * 7, Days.ofWeeks(Integer.MIN_VALUE / 7).getAmount());
    }

    @Test
    public void test_ofWeeks_overflow() {
        assertThrows(ArithmeticException.class, () -> Days.ofWeeks((Integer.MAX_VALUE / 7) + 7));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_from_Period_P0D() {
        assertEquals(Days.of(0), Days.from(Period.ofDays(0)));
    }

    @Test
    public void test_from_Period_P2D() {
        assertEquals(Days.of(2), Days.from(Period.ofDays(2)));
    }

    @Test
    public void test_from_P2W() {
        assertEquals(Days.of(14), Days.from(new MockWeeksDays(2, 0)));
    }

    @Test
    public void test_from_P2W3D() {
        assertEquals(Days.of(17), Days.from(new MockWeeksDays(2, 3)));
    }

    @Test
    public void test_from_Duration() {
        assertEquals(Days.of(2), Days.from(Duration.ofDays(2)));
    }

    @Test
    public void test_from_wrongUnit_remainder() {
        assertThrows(DateTimeException.class, () -> Days.from(Duration.ofHours(3)));
    }

    @Test
    public void test_from_wrongUnit_noConversion() {
        assertThrows(DateTimeException.class, () -> Days.from(Period.ofMonths(2)));
    }

    @Test
    public void test_from_null() {
        assertThrows(NullPointerException.class, () -> Days.from((TemporalAmount) null));
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_valid() {
        return new Object[][] {
            {"P0D", 0},
            {"P1D", 1},
            {"P2D", 2},
            {"P123456789D", 123456789},
            {"P+0D", 0},
            {"P+2D", 2},
            {"P-0D", 0},
            {"P-2D", -2},

            {"P0W", 0},
            {"P1W", 7},
            {"P2W", 14},
            {"P12345678W", 12345678 * 7},
            {"P+0W", 0},
            {"P+2W", 14},
            {"P-0W", 0},
            {"P-2W", -14},

            {"P0W0D", 0},
            {"P2W3D", 17},
            {"P+2W3D", 17},
            {"P2W+3D", 17},
            {"P-2W3D", -11},
            {"P2W-3D", 11},
            {"P-2W-3D", -17},
        };
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid(String str, int expectedDays) {
        assertEquals(Days.of(expectedDays), Days.parse(str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedDays) {
        assertEquals(Days.of(expectedDays), Days.parse("+" + str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedDays) {
        assertEquals(Days.of(-expectedDays), Days.parse("-" + str));
    }

    public static Object[][] data_invalid() {
        return new Object[][] {
            {"P3Y"},
            {"P3M"},
            {"P3Q"},
            {"P1D2W"},

            {"3"},
            {"-3"},
            {"3D"},
            {"-3D"},
            {"P3"},
            {"P-3"},
            {"P"},
            {"PD"},
            {"PW"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_invalid")
    public void test_parse_CharSequence_invalid(String str) {
        assertThrows(DateTimeParseException.class, () -> Days.parse(str));
    }

    @Test
    public void test_parse_CharSequence_null() {
        assertThrows(NullPointerException.class, () -> Days.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(6, Days.of(6).get(ChronoUnit.DAYS));
    }

    @Test
    public void test_get_invalidType() {
        assertThrows(DateTimeException.class, () -> Days.of(6).get(IsoFields.QUARTER_YEARS));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_between() {
        assertEquals(Days.of(365 + 366), Days.between(LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1)));
    }

    @Test
    public void test_between_date_null() {
        assertThrows(NullPointerException.class, () -> Days.between(LocalDate.now(), (Temporal) null));
    }

    @Test
    public void test_between_null_date() {
        assertThrows(NullPointerException.class, () -> Days.between((Temporal) null, LocalDate.now()));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount_Days() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.plus(Days.of(0)));
        assertEquals(Days.of(7), test5.plus(Days.of(2)));
        assertEquals(Days.of(3), test5.plus(Days.of(-2)));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).plus(Days.of(1)));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).plus(Days.of(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_Period() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.plus(Period.ofDays(0)));
        assertEquals(Days.of(7), test5.plus(Period.ofDays(2)));
        assertEquals(Days.of(3), test5.plus(Period.ofDays(-2)));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).plus(Period.ofDays(1)));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).plus(Period.ofDays(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_PeriodYears() {
        assertThrows(DateTimeException.class, () -> Days.of(1).plus(Period.ofYears(2)));
    }

    @Test
    public void test_plus_TemporalAmount_Duration() {
        assertThrows(DateTimeException.class, () -> Days.of(1).plus(Duration.ofHours(2)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MAX_VALUE - 1).plus(Days.of(2)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MIN_VALUE + 1).plus(Days.of(-2)));
    }

    @Test
    public void test_plus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Days.of(Integer.MIN_VALUE + 1).plus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_int() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.plus(0));
        assertEquals(Days.of(7), test5.plus(2));
        assertEquals(Days.of(3), test5.plus(-2));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test
    public void test_plus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MAX_VALUE - 1).plus(2));
    }

    @Test
    public void test_plus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MIN_VALUE + 1).plus(-2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount_Days() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.minus(Days.of(0)));
        assertEquals(Days.of(3), test5.minus(Days.of(2)));
        assertEquals(Days.of(7), test5.minus(Days.of(-2)));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).minus(Days.of(-1)));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).minus(Days.of(1)));
    }

    @Test
    public void test_minus_TemporalAmount_Period() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.minus(Period.ofDays(0)));
        assertEquals(Days.of(3), test5.minus(Period.ofDays(2)));
        assertEquals(Days.of(7), test5.minus(Period.ofDays(-2)));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).minus(Period.ofDays(-1)));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).minus(Period.ofDays(1)));
    }

    @Test
    public void test_minus_TemporalAmount_PeriodYears() {
        assertThrows(DateTimeException.class, () -> Days.of(1).minus(Period.ofYears(2)));
    }

    @Test
    public void test_minus_TemporalAmount_Duration() {
        assertThrows(DateTimeException.class, () -> Days.of(1).minus(Duration.ofHours(2)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MAX_VALUE - 1).minus(Days.of(-2)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MIN_VALUE + 1).minus(Days.of(2)));
    }

    @Test
    public void test_minus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Days.of(Integer.MIN_VALUE + 1).minus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_int() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.minus(0));
        assertEquals(Days.of(3), test5.minus(2));
        assertEquals(Days.of(7), test5.minus(-2));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test
    public void test_minus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MAX_VALUE - 1).minus(-2));
    }

    @Test
    public void test_minus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MIN_VALUE + 1).minus(2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(0), test5.multipliedBy(0));
        assertEquals(Days.of(5), test5.multipliedBy(1));
        assertEquals(Days.of(10), test5.multipliedBy(2));
        assertEquals(Days.of(15), test5.multipliedBy(3));
        assertEquals(Days.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_negate() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2));
    }

    @Test
    public void test_multipliedBy_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_dividedBy() {
        Days test12 = Days.of(12);
        assertEquals(Days.of(12), test12.dividedBy(1));
        assertEquals(Days.of(6), test12.dividedBy(2));
        assertEquals(Days.of(4), test12.dividedBy(3));
        assertEquals(Days.of(3), test12.dividedBy(4));
        assertEquals(Days.of(2), test12.dividedBy(5));
        assertEquals(Days.of(2), test12.dividedBy(6));
        assertEquals(Days.of(-4), test12.dividedBy(-3));
    }

    @Test
    public void test_dividedBy_negate() {
        Days test12 = Days.of(12);
        assertEquals(Days.of(-4), test12.dividedBy(-3));
    }

    @Test
    public void test_dividedBy_divideByZero() {
        assertThrows(ArithmeticException.class, () -> Days.of(1).dividedBy(0));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_negated() {
        assertEquals(Days.of(0), Days.of(0).negated());
        assertEquals(Days.of(-12), Days.of(12).negated());
        assertEquals(Days.of(12), Days.of(-12).negated());
        assertEquals(Days.of(-Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE).negated());
    }

    @Test
    public void test_negated_overflow() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MIN_VALUE).negated());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_abs() {
        assertEquals(Days.of(0), Days.of(0).abs());
        assertEquals(Days.of(12), Days.of(12).abs());
        assertEquals(Days.of(12), Days.of(-12).abs());
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE).abs());
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(-Integer.MAX_VALUE).abs());
    }

    @Test
    public void test_abs_overflow() {
        assertThrows(ArithmeticException.class, () -> Days.of(Integer.MIN_VALUE).abs());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_addTo() {
        assertEquals(LocalDate.of(2019, 1, 10), Days.of(0).addTo(LocalDate.of(2019, 1, 10)));
        assertEquals(LocalDate.of(2019, 1, 15), Days.of(5).addTo(LocalDate.of(2019, 1, 10)));
    }

    @Test
    public void test_subtractFrom() {
        assertEquals(LocalDate.of(2019, 1, 10), Days.of(0).subtractFrom(LocalDate.of(2019, 1, 10)));
        assertEquals(LocalDate.of(2019, 1, 5), Days.of(5).subtractFrom(LocalDate.of(2019, 1, 10)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toPeriod() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Period.ofDays(i), Days.of(i).toPeriod());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        Days test5 = Days.of(5);
        Days test6 = Days.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test
    public void test_compareTo_null() {
        Days test5 = Days.of(5);
        assertThrows(NullPointerException.class, () -> test5.compareTo(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(Days.of(5), Days.of(5))
            .addEqualityGroup(Days.of(6), Days.of(6))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Days test5 = Days.of(5);
        assertEquals("P5D", test5.toString());
        Days testM1 = Days.of(-1);
        assertEquals("P-1D", testM1.toString());
    }

}
