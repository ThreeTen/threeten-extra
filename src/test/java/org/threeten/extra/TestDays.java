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
public class TestDays {

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Days.class));
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Days orginal = Days.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Days ser = (Days) in.readObject();
        assertSame(Days.ZERO, ser);
    }

    //-----------------------------------------------------------------------
    public void test_ZERO() {
        assertSame(Days.of(0), Days.ZERO);
        assertSame(Days.of(0), Days.ZERO);
        assertEquals(Days.ZERO.getAmount(), 0);
    }

    public void test_ONE() {
        assertSame(Days.of(1), Days.ONE);
        assertSame(Days.of(1), Days.ONE);
        assertEquals(Days.ONE.getAmount(), 1);
    }

    //-----------------------------------------------------------------------
    public void test_of() {
        assertEquals(Days.of(0).getAmount(), 0);
        assertEquals(Days.of(1).getAmount(), 1);
        assertEquals(Days.of(2).getAmount(), 2);
        assertEquals(Days.of(Integer.MAX_VALUE).getAmount(), Integer.MAX_VALUE);
        assertEquals(Days.of(-1).getAmount(), -1);
        assertEquals(Days.of(-2).getAmount(), -2);
        assertEquals(Days.of(Integer.MIN_VALUE).getAmount(), Integer.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    public void test_ofWeeks() {
        assertEquals(Days.ofWeeks(0).getAmount(), 0);
        assertEquals(Days.ofWeeks(1).getAmount(), 7);
        assertEquals(Days.ofWeeks(2).getAmount(), 14);
        assertEquals(Days.ofWeeks(Integer.MAX_VALUE / 7).getAmount(), (Integer.MAX_VALUE / 7) * 7);
        assertEquals(Days.ofWeeks(-1).getAmount(), -7);
        assertEquals(Days.ofWeeks(-2).getAmount(), -14);
        assertEquals(Days.ofWeeks(Integer.MIN_VALUE / 7).getAmount(), (Integer.MIN_VALUE / 7) * 7);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_ofWeeks_overflow() {
        Days.ofWeeks((Integer.MAX_VALUE / 7) + 7);
    }

    //-----------------------------------------------------------------------
    public void test_from_Period_P0D() {
        assertEquals(Days.from(Period.ofDays(0)), Days.of(0));
    }

    public void test_from_Period_P2D() {
        assertEquals(Days.from(Period.ofDays(2)), Days.of(2));
    }

    public void test_from_P2W() {
        assertEquals(Days.from(new MockWeeksDays(2, 0)), Days.of(14));
    }

    public void test_from_P2W3D() {
        assertEquals(Days.from(new MockWeeksDays(2, 3)), Days.of(17));
    }

    public void test_from_Duration() {
        assertEquals(Days.from(Duration.ofDays(2)), Days.of(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_from_wrongUnit_remainder() {
        Days.from(Duration.ofHours(3));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_from_wrongUnit_noConversion() {
        Days.from(Period.ofMonths(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_from_null() {
        Days.from((TemporalAmount) null);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "parseValid")
    Object[][] data_valid() {
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

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid(String str, int expectedDays) {
        assertEquals(Days.parse(str), Days.of(expectedDays));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialPlus(String str, int expectedDays) {
        assertEquals(Days.parse("+" + str), Days.of(expectedDays));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialMinus(String str, int expectedDays) {
        assertEquals(Days.parse("-" + str), Days.of(-expectedDays));
    }

    @DataProvider(name = "parseInvalid")
    Object[][] data_invalid() {
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

    @Test(expectedExceptions = DateTimeParseException.class, dataProvider = "parseInvalid")
    public void test_parse_CharSequence_invalid(String str) {
        Days.parse(str);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Days.parse((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_TemporalAmount_Days() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.plus(Days.of(0)));
        assertEquals(Days.of(7), test5.plus(Days.of(2)));
        assertEquals(Days.of(3), test5.plus(Days.of(-2)));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).plus(Days.of(1)));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).plus(Days.of(-1)));
    }

    public void test_plus_TemporalAmount_Period() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.plus(Period.ofDays(0)));
        assertEquals(Days.of(7), test5.plus(Period.ofDays(2)));
        assertEquals(Days.of(3), test5.plus(Period.ofDays(-2)));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).plus(Period.ofDays(1)));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).plus(Period.ofDays(-1)));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_TemporalAmount_PeriodYears() {
        Days.of(1).plus(Period.ofYears(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_plus_TemporalAmount_Duration() {
        Days.of(1).plus(Duration.ofHours(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        Days.of(Integer.MAX_VALUE - 1).plus(Days.of(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        Days.of(Integer.MIN_VALUE + 1).plus(Days.of(-2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        Days.of(Integer.MIN_VALUE + 1).plus(null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_int() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.plus(0));
        assertEquals(Days.of(7), test5.plus(2));
        assertEquals(Days.of(3), test5.plus(-2));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooBig() {
        Days.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_int_overflowTooSmall() {
        Days.of(Integer.MIN_VALUE + 1).plus(-2);
    }

    //-----------------------------------------------------------------------
    public void test_minus_TemporalAmount_Days() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.minus(Days.of(0)));
        assertEquals(Days.of(3), test5.minus(Days.of(2)));
        assertEquals(Days.of(7), test5.minus(Days.of(-2)));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).minus(Days.of(-1)));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).minus(Days.of(1)));
    }

    public void test_minus_TemporalAmount_Period() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.minus(Period.ofDays(0)));
        assertEquals(Days.of(3), test5.minus(Period.ofDays(2)));
        assertEquals(Days.of(7), test5.minus(Period.ofDays(-2)));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).minus(Period.ofDays(-1)));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).minus(Period.ofDays(1)));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_TemporalAmount_PeriodYears() {
        Days.of(1).minus(Period.ofYears(2));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_minus_TemporalAmount_Duration() {
        Days.of(1).minus(Duration.ofHours(2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        Days.of(Integer.MAX_VALUE - 1).minus(Days.of(-2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        Days.of(Integer.MIN_VALUE + 1).minus(Days.of(2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        Days.of(Integer.MIN_VALUE + 1).minus(null);
    }

    //-----------------------------------------------------------------------
    public void test_minus_int() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(5), test5.minus(0));
        assertEquals(Days.of(3), test5.minus(2));
        assertEquals(Days.of(7), test5.minus(-2));
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Days.of(Integer.MIN_VALUE), Days.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooBig() {
        Days.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_int_overflowTooSmall() {
        Days.of(Integer.MIN_VALUE + 1).minus(2);
    }

    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(0), test5.multipliedBy(0));
        assertEquals(Days.of(5), test5.multipliedBy(1));
        assertEquals(Days.of(10), test5.multipliedBy(2));
        assertEquals(Days.of(15), test5.multipliedBy(3));
        assertEquals(Days.of(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Days test5 = Days.of(5);
        assertEquals(Days.of(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Days.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Days.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
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

    public void test_dividedBy_negate() {
        Days test12 = Days.of(12);
        assertEquals(Days.of(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        Days.of(1).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Days.of(0), Days.of(0).negated());
        assertEquals(Days.of(-12), Days.of(12).negated());
        assertEquals(Days.of(12), Days.of(-12).negated());
        assertEquals(Days.of(-Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_negated_overflow() {
        Days.of(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    public void test_abs() {
        assertEquals(Days.of(0), Days.of(0).abs());
        assertEquals(Days.of(12), Days.of(12).abs());
        assertEquals(Days.of(12), Days.of(-12).abs());
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(Integer.MAX_VALUE).abs());
        assertEquals(Days.of(Integer.MAX_VALUE), Days.of(-Integer.MAX_VALUE).abs());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_abs_overflow() {
        Days.of(Integer.MIN_VALUE).abs();
    }

    //-----------------------------------------------------------------------
    public void test_toPeriod() {
        for (int i = -20; i < 20; i++) {
            assertEquals(Days.of(i).toPeriod(), Period.ofDays(i));
        }
    }

    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Days test5 = Days.of(5);
        Days test6 = Days.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_null() {
        Days test5 = Days.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Days test5 = Days.of(5);
        Days test6 = Days.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Days test5 = Days.of(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Days test5 = Days.of(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "isLessThan")
    Object[][] isLessThan() {
        return new Object[][] {
                {Days.of(0), Days.of(1), true},
                {Days.of(1), Days.of(1), false},
                {Days.of(0), null, false},
                {Days.of(-1), null, true},
                {Days.of(3), Days.of(1), false},
        };
    }

    @Test(dataProvider = "isLessThan")
    public void test_isLessThan(Days d1,Days d2,boolean expectedResult) {
        assertEquals(expectedResult, d1.isLessThan(d2));
    }

    @DataProvider(name = "isLessThanOrEqualsTo")
    Object[][] isLessThanOrEqualsTo() {
        return new Object[][] {
                {Days.of(0), Days.of(1), true},
                {Days.of(1), Days.of(1), true},
                {Days.of(0), null, true},
                {Days.of(-1), null, true},
                {Days.of(3), Days.of(1), false},
        };
    }

    @Test(dataProvider = "isLessThanOrEqualsTo")
    public void test_isLessThanOrEqualsTo(Days d1,Days d2,boolean expectedResult) {
        assertEquals(expectedResult, d1.isLessThanOrEqualsTo(d2));
    }

    @DataProvider(name = "isGreaterThan")
    Object[][] isGreaterThan() {
        return new Object[][] {
                {Days.of(1), Days.of(0), true},
                {Days.of(1), Days.of(1), false},
                {Days.of(0), null, false},
                {Days.of(1), null, true},
                {Days.of(1), Days.of(3), false},
        };
    }

    @Test(dataProvider = "isGreaterThan")
    public void test_isGreaterThan(Days d1,Days d2,boolean expectedResult) {
        assertEquals(expectedResult, d1.isGreaterThan(d2));
    }

    @DataProvider(name = "isGreaterThanOrEqualsTo")
    Object[][] isGreaterThanOrEqualsTo() {
        return new Object[][] {
                {Days.of(1), Days.of(0), true},
                {Days.of(1), Days.of(1), true},
                {Days.of(0), null, true},
                {Days.of(1), null, true},
                {Days.of(1), Days.of(3), false},
        };
    }

    @Test(dataProvider = "isGreaterThanOrEqualsTo")
    public void test_isGreaterThanOrEqualsTo(Days d1,Days d2,boolean expectedResult) {
        assertEquals(expectedResult, d1.isGreaterThanOrEqualsTo(d2));
    }
    
    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Days test5 = Days.of(5);
        Days test6 = Days.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Days test5 = Days.of(5);
        assertEquals("P5D", test5.toString());
        Days testM1 = Days.of(-1);
        assertEquals("P-1D", testM1.toString());
    }

}
