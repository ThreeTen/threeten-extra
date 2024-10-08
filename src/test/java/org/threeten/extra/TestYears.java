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
public class TestYears {

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Years.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_deserializationSingleton() throws Exception {
        Years test = Years.ZERO;
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
        assertSame(Years.ZERO, Years.of(0));
        assertEquals(Years.ZERO, Years.of(0));
        assertEquals(0, Years.ZERO.getAmount());
        assertFalse(Years.ZERO.isNegative());
        assertTrue(Years.ZERO.isZero());
        assertFalse(Years.ZERO.isPositive());
    }

    @Test
    public void test_ONE() {
        assertSame(Years.ONE, Years.of(1));
        assertEquals(Years.ONE, Years.of(1));
        assertEquals(1, Years.ONE.getAmount());
        assertFalse(Years.ONE.isNegative());
        assertFalse(Years.ONE.isZero());
        assertTrue(Years.ONE.isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of() {
        assertEquals(1, Years.of(1).getAmount());
        assertEquals(2, Years.of(2).getAmount());
        assertEquals(Integer.MAX_VALUE, Years.of(Integer.MAX_VALUE).getAmount());
        assertEquals(-1, Years.of(-1).getAmount());
        assertEquals(-2, Years.of(-2).getAmount());
        assertEquals(Integer.MIN_VALUE, Years.of(Integer.MIN_VALUE).getAmount());
    }

    @Test
    public void test_ofMinusOne() {
        assertEquals(-1, Years.of(-1).getAmount());
        assertTrue(Years.of(-1).isNegative());
        assertFalse(Years.of(-1).isZero());
        assertFalse(Years.of(-1).isPositive());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_from_P0Y() {
        assertEquals(Years.of(0), Years.from(Period.ofYears(0)));
    }

    @Test
    public void test_from_P2Y() {
        assertEquals(Years.of(2), Years.from(Period.ofYears(2)));
    }

    @Test
    public void test_from_P24M() {
        assertEquals(Years.of(2), Years.from(Period.ofMonths(24)));
    }

    @Test
    public void test_from_yearsAndMonths() {
        assertEquals(Years.of(5), Years.from(Period.of(3, 24, 0)));
    }

    @Test
    public void test_from_decadesAndMonths() {
        assertEquals(Years.of(19), Years.from(new MockDecadesMonths(2, -12)));
    }

    @Test
    public void test_from_wrongUnit_remainder() {
        assertThrows(DateTimeException.class, () -> Years.from(Period.ofMonths(3)));
    }

    @Test
    public void test_from_wrongUnit_noConversion() {
        assertThrows(DateTimeException.class, () -> Years.from(Period.ofDays(2)));
    }

    @Test
    public void test_from_null() {
        assertThrows(NullPointerException.class, () -> Years.from((TemporalAmount) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequence() {
        assertEquals(Years.of(0), Years.parse("P0Y"));
        assertEquals(Years.of(1), Years.parse("P1Y"));
        assertEquals(Years.of(2), Years.parse("P2Y"));
        assertEquals(Years.of(123456789), Years.parse("P123456789Y"));
        assertEquals(Years.of(-2), Years.parse("P-2Y"));
        assertEquals(Years.of(-2), Years.parse("-P2Y"));
        assertEquals(Years.of(2), Years.parse("-P-2Y"));
    }

    public static Object[][] data_invalid() {
        return new Object[][] {
            {"P3M"},
            {"P3W"},
            {"P3D"},

            {"3"},
            {"-3"},
            {"3Y"},
            {"-3Y"},
            {"P3"},
            {"P-3"},
            {"PY"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_invalid")
    public void test_parse_CharSequence_invalid(String str) {
        assertThrows(DateTimeParseException.class, () -> Years.parse(str));
    }

    @Test
    public void test_parse_CharSequence_null() {
        assertThrows(NullPointerException.class, () -> Years.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_between() {
        assertEquals(Years.of(2), Years.between(LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1)));
    }

    @Test
    public void test_between_date_null() {
        assertThrows(NullPointerException.class, () -> Years.between(LocalDate.now(), (Temporal) null));
    }

    @Test
    public void test_between_null_date() {
        assertThrows(NullPointerException.class, () -> Years.between((Temporal) null, LocalDate.now()));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(6, Years.of(6).get(ChronoUnit.YEARS));
    }

    @Test
    public void test_get_invalidType() {
        assertThrows(DateTimeException.class, () -> Years.of(6).get(IsoFields.QUARTER_YEARS));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount_Years() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.plus(Years.of(0)));
        assertEquals(Years.of(7), test5.plus(Years.of(2)));
        assertEquals(Years.of(3), test5.plus(Years.of(-2)));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).plus(Years.of(1)));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).plus(Years.of(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_Period() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.plus(Period.ofYears(0)));
        assertEquals(Years.of(7), test5.plus(Period.ofYears(2)));
        assertEquals(Years.of(3), test5.plus(Period.ofYears(-2)));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).plus(Period.ofYears(1)));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).plus(Period.ofYears(-1)));
    }

    @Test
    public void test_plus_TemporalAmount_PeriodMonths() {
        assertThrows(DateTimeException.class, () -> Years.of(1).plus(Period.ofMonths(2)));
    }

    @Test
    public void test_plus_TemporalAmount_Duration() {
        assertThrows(DateTimeException.class, () -> Years.of(1).plus(Duration.ofHours(2)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MAX_VALUE - 1).plus(Years.of(2)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MIN_VALUE + 1).plus(Years.of(-2)));
    }

    @Test
    public void test_plus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Years.of(Integer.MIN_VALUE + 1).plus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_int() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.plus(0));
        assertEquals(Years.of(7), test5.plus(2));
        assertEquals(Years.of(3), test5.plus(-2));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test
    public void test_plus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MAX_VALUE - 1).plus(2));
    }

    @Test
    public void test_plus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MIN_VALUE + 1).plus(-2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount_Years() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.minus(Years.of(0)));
        assertEquals(Years.of(3), test5.minus(Years.of(2)));
        assertEquals(Years.of(7), test5.minus(Years.of(-2)));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).minus(Years.of(-1)));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).minus(Years.of(1)));
    }

    @Test
    public void test_minus_TemporalAmount_Period() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.minus(Period.ofYears(0)));
        assertEquals(Years.of(3), test5.minus(Period.ofYears(2)));
        assertEquals(Years.of(7), test5.minus(Period.ofYears(-2)));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).minus(Period.ofYears(-1)));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).minus(Period.ofYears(1)));
    }

    @Test
    public void test_minus_TemporalAmount_PeriodMonths() {
        assertThrows(DateTimeException.class, () -> Years.of(1).minus(Period.ofMonths(2)));
    }

    @Test
    public void test_minus_TemporalAmount_Duration() {
        assertThrows(DateTimeException.class, () -> Years.of(1).minus(Duration.ofHours(2)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MAX_VALUE - 1).minus(Years.of(-2)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MIN_VALUE + 1).minus(Years.of(2)));
    }

    @Test
    public void test_minus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> Years.of(Integer.MIN_VALUE + 1).minus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_int() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(5), test5.minus(0));
        assertEquals(Years.of(3), test5.minus(2));
        assertEquals(Years.of(7), test5.minus(-2));
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Years.of(Integer.MIN_VALUE), Years.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test
    public void test_minus_int_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MAX_VALUE - 1).minus(-2));
    }

    @Test
    public void test_minus_int_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MIN_VALUE + 1).minus(2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(0), test5.multipliedBy(0));
        assertEquals(Years.of(5), test5.multipliedBy(1));
        assertEquals(Years.of(10), test5.multipliedBy(2));
        assertEquals(Years.of(15), test5.multipliedBy(3));
        assertEquals(Years.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_negate() {
        Years test5 = Years.of(5);
        assertEquals(Years.of(-15), test5.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2));
    }

    @Test
    public void test_multipliedBy_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_dividedBy() {
        Years test12 = Years.of(12);
        assertEquals(Years.of(12), test12.dividedBy(1));
        assertEquals(Years.of(6), test12.dividedBy(2));
        assertEquals(Years.of(4), test12.dividedBy(3));
        assertEquals(Years.of(3), test12.dividedBy(4));
        assertEquals(Years.of(2), test12.dividedBy(5));
        assertEquals(Years.of(2), test12.dividedBy(6));
        assertEquals(Years.of(-4), test12.dividedBy(-3));
    }

    @Test
    public void test_dividedBy_negate() {
        Years test12 = Years.of(12);
        assertEquals(Years.of(-4), test12.dividedBy(-3));
    }

    @Test
    public void test_dividedBy_divideByZero() {
        assertThrows(ArithmeticException.class, () -> Years.of(1).dividedBy(0));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_negated() {
        assertEquals(Years.of(0), Years.of(0).negated());
        assertEquals(Years.of(-12), Years.of(12).negated());
        assertEquals(Years.of(12), Years.of(-12).negated());
        assertEquals(Years.of(-Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE).negated());
    }

    @Test
    public void test_negated_overflow() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MIN_VALUE).negated());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_abs() {
        assertEquals(Years.of(0), Years.of(0).abs());
        assertEquals(Years.of(12), Years.of(12).abs());
        assertEquals(Years.of(12), Years.of(-12).abs());
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(Integer.MAX_VALUE).abs());
        assertEquals(Years.of(Integer.MAX_VALUE), Years.of(-Integer.MAX_VALUE).abs());
    }

    @Test
    public void test_abs_overflow() {
        assertThrows(ArithmeticException.class, () -> Years.of(Integer.MIN_VALUE).abs());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_addTo() {
        assertEquals(LocalDate.of(2019, 1, 10), Years.of(0).addTo(LocalDate.of(2019, 1, 10)));
        assertEquals(LocalDate.of(2024, 1, 10), Years.of(5).addTo(LocalDate.of(2019, 1, 10)));
    }

    @Test
    public void test_subtractFrom() {
        assertEquals(LocalDate.of(2019, 1, 10), Years.of(0).subtractFrom(LocalDate.of(2019, 1, 10)));
        assertEquals(LocalDate.of(2014, 1, 10), Years.of(5).subtractFrom(LocalDate.of(2019, 1, 10)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toPeriod() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Period.ofYears(i), Years.of(i).toPeriod());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        Years test5 = Years.of(5);
        Years test6 = Years.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test
    public void test_compareTo_null() {
        Years test5 = Years.of(5);
        assertThrows(NullPointerException.class, () -> test5.compareTo(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(Years.of(0), Years.of(0))
            .addEqualityGroup(Years.of(1), Years.of(1))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Years test5 = Years.of(5);
        assertEquals("P5Y", test5.toString());
        Years testM1 = Years.of(-1);
        assertEquals("P-1Y", testM1.toString());
    }

}
