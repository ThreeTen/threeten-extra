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

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.ERAS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestPeriodDuration {

    private static final Period P1Y2M3D = Period.of(1, 2, 3);
    private static final Duration DUR_5 = Duration.ofSeconds(5);
    private static final Duration DUR_6 = Duration.ofSeconds(6);

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(PeriodDuration.class));
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        PeriodDuration orginal = PeriodDuration.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        PeriodDuration ser = (PeriodDuration) in.readObject();
        assertEquals(PeriodDuration.ZERO, ser);
    }

    //-----------------------------------------------------------------------
    public void test_ZERO() {
        assertEquals(PeriodDuration.of(Period.ZERO, Duration.ZERO), PeriodDuration.ZERO);
        assertEquals(PeriodDuration.of(Period.ZERO), PeriodDuration.ZERO);
        assertEquals(PeriodDuration.of(Duration.ZERO), PeriodDuration.ZERO);
        assertEquals(PeriodDuration.ZERO.getPeriod(), Period.ZERO);
        assertEquals(PeriodDuration.ZERO.getDuration(), Duration.ZERO);
        assertEquals(PeriodDuration.ZERO.isZero(), true);
        assertEquals(PeriodDuration.ZERO.getUnits(), Arrays.asList(YEARS, MONTHS, DAYS, SECONDS, NANOS));
        assertEquals(PeriodDuration.ZERO.get(YEARS), 0);
        assertEquals(PeriodDuration.ZERO.get(MONTHS), 0);
        assertEquals(PeriodDuration.ZERO.get(DAYS), 0);
        assertEquals(PeriodDuration.ZERO.get(SECONDS), 0);
        assertEquals(PeriodDuration.ZERO.get(NANOS), 0);
        assertThrows(() -> PeriodDuration.ZERO.get(ERAS));
    }

    //-----------------------------------------------------------------------
    public void test_of_both() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(4));
        assertEquals(test.getPeriod(), P1Y2M3D);
        assertEquals(test.getDuration(), Duration.ofSeconds(4));
        assertEquals(test.isZero(), false);
        assertEquals(test.get(YEARS), 1);
        assertEquals(test.get(MONTHS), 2);
        assertEquals(test.get(DAYS), 3);
        assertEquals(test.get(SECONDS), 4);
        assertEquals(test.get(NANOS), 0);
    }

    public void test_of_period() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D);
        assertEquals(test.getPeriod(), P1Y2M3D);
        assertEquals(test.getDuration(), Duration.ZERO);
        assertEquals(test.isZero(), false);
        assertEquals(test.get(YEARS), 1);
        assertEquals(test.get(MONTHS), 2);
        assertEquals(test.get(DAYS), 3);
        assertEquals(test.get(SECONDS), 0);
        assertEquals(test.get(NANOS), 0);
    }

    public void test_of_duration() {
        PeriodDuration test = PeriodDuration.of(Duration.ofSeconds(4));
        assertEquals(test.getPeriod(), Period.ZERO);
        assertEquals(test.getDuration(), Duration.ofSeconds(4));
        assertEquals(test.isZero(), false);
        assertEquals(test.get(YEARS), 0);
        assertEquals(test.get(MONTHS), 0);
        assertEquals(test.get(DAYS), 0);
        assertEquals(test.get(SECONDS), 4);
        assertEquals(test.get(NANOS), 0);
    }

    //-----------------------------------------------------------------------
    public void test_between_dates() {
        PeriodDuration test = PeriodDuration.between(LocalDate.of(2012, 6, 20), LocalDate.of(2012, 8, 25));
        assertEquals(test.getPeriod(), Period.between(LocalDate.of(2012, 6, 20), LocalDate.of(2012, 8, 25)));
        assertEquals(test.getDuration(), Duration.ZERO);
    }

    public void test_between_times() {
        PeriodDuration test = PeriodDuration.between(LocalTime.of(11, 20), LocalTime.of(12, 25));
        assertEquals(test.getPeriod(), Period.ZERO);
        assertEquals(test.getDuration(), Duration.between(LocalTime.of(11, 20), LocalTime.of(12, 25)));
    }

    public void test_between_mixed1() {
        PeriodDuration test = PeriodDuration.between(LocalDate.of(2012, 6, 20), LocalTime.of(11, 25));
        assertEquals(test.getPeriod(), Period.ZERO);
        assertEquals(test.getDuration(), Duration.ofHours(11).plusMinutes(25));
    }

    public void test_between_mixed2() {
        PeriodDuration test = PeriodDuration.between(LocalDate.of(2012, 6, 20), LocalDateTime.of(2012, 7, 22, 11, 25));
        assertEquals(test.getPeriod(), Period.of(0, 1, 2));
        assertEquals(test.getDuration(), Duration.ofHours(11).plusMinutes(25));
    }

    //-----------------------------------------------------------------------
    public void test_from() {
        assertEquals(PeriodDuration.from(PeriodDuration.of(P1Y2M3D)), PeriodDuration.from(PeriodDuration.of(P1Y2M3D)));
        assertEquals(PeriodDuration.from(Period.ofYears(2)), PeriodDuration.of(Period.ofYears(2)));
        assertEquals(PeriodDuration.from(Duration.ofSeconds(2)), PeriodDuration.of(Duration.ofSeconds(2)));
        assertEquals(PeriodDuration.from(Years.of(2)), PeriodDuration.of(Period.ofYears(2)));
        assertEquals(PeriodDuration.from(Months.of(2)), PeriodDuration.of(Period.ofMonths(2)));
        assertEquals(PeriodDuration.from(Weeks.of(2)), PeriodDuration.of(Period.ofWeeks(2)));
        assertEquals(PeriodDuration.from(Days.of(2)), PeriodDuration.of(Period.ofDays(2)));
        assertEquals(PeriodDuration.from(Hours.of(2)), PeriodDuration.of(Duration.ofHours(2)));
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "parseValid")
    Object[][] data_valid() {
        return new Object[][] {
                {"P1Y2M3W4DT5H6M7S", Period.of(1, 2, 3 * 7 + 4), Duration.ofHours(5).plusMinutes(6).plusSeconds(7)},
                {"P3Y", Period.ofYears(3), Duration.ZERO},
                {"P3M", Period.ofMonths(3), Duration.ZERO},
                {"P3W", Period.ofWeeks(3), Duration.ZERO},
                {"P3D", Period.ofDays(3), Duration.ZERO},

                {"PT0S", Period.of(0, 0, 0), Duration.ofSeconds(0)},
                {"PT1S", Period.of(0, 0, 0), Duration.ofSeconds(1)},
                {"PT2S", Period.of(0, 0, 0), Duration.ofSeconds(2)},
                {"PT123456789S", Period.of(0, 0, 0), Duration.ofSeconds(123456789)},
                {"PT+0S", Period.of(0, 0, 0), Duration.ofSeconds(0)},
                {"PT+2S", Period.of(0, 0, 0), Duration.ofSeconds(2)},
                {"PT-0S", Period.of(0, 0, 0), Duration.ofSeconds(0)},
                {"PT-2S", Period.of(0, 0, 0), Duration.ofSeconds(-2)},

                {"P+0M", Period.of(0, 0, 0), Duration.ZERO},
                {"P+2M", Period.of(0, 2, 0), Duration.ZERO},
                {"P-0M", Period.of(0, 0, 0), Duration.ZERO},
                {"P-2M", Period.of(0, -2, 0), Duration.ZERO},
        };
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid(String str, Period period, Duration duration) {
        assertEquals(PeriodDuration.parse(str), PeriodDuration.of(period, duration));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialPlus(String str, Period period, Duration duration) {
        assertEquals(PeriodDuration.parse("+" + str), PeriodDuration.of(period, duration));
    }

    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence_valid_initialMinus(String str, Period period, Duration duration) {
        assertEquals(PeriodDuration.parse("-" + str), PeriodDuration.of(period, duration).negated());
    }

    @DataProvider(name = "parseInvalid")
    Object[][] data_invalid() {
        return new Object[][] {
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
        PeriodDuration.parse(str);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        PeriodDuration.parse((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    public void test_plus_TemporalAmount_PeriodDuration() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(test.plus(Period.of(3, 2, 1)), PeriodDuration.of(Period.of(4, 4, 4), DUR_5));
        assertEquals(test.plus(Duration.ofSeconds(4)), PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(9)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooBig() {
        PeriodDuration.of(Period.of(Integer.MAX_VALUE - 1, 0, 0)).plus(PeriodDuration.of(Period.ofYears(2)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_TemporalAmount_overflowTooSmall() {
        PeriodDuration.of(Period.of(Integer.MIN_VALUE + 1, 0, 0)).plus(PeriodDuration.of(Period.ofYears(-2)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_TemporalAmount_null() {
        P1Y2M3D.plus(null);
    }

    //-----------------------------------------------------------------------
    public void test_minus_TemporalAmount_PeriodDuration() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(test.minus(Period.of(1, 1, 1)), PeriodDuration.of(Period.of(0, 1, 2), DUR_5));
        assertEquals(test.minus(Duration.ofSeconds(4)), PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(1)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooBig() {
        PeriodDuration.of(Period.of(Integer.MAX_VALUE - 1, 0, 0)).minus(PeriodDuration.of(Period.ofYears(-2)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_TemporalAmount_overflowTooSmall() {
        PeriodDuration.of(Period.of(Integer.MIN_VALUE + 1, 0, 0)).minus(PeriodDuration.of(Period.ofYears(2)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_TemporalAmount_null() {
        P1Y2M3D.minus(null);
    }

    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(test.multipliedBy(0), PeriodDuration.ZERO);
        assertEquals(test.multipliedBy(1), test);
        assertEquals(test.multipliedBy(5), PeriodDuration.of(Period.of(5,  10, 15), Duration.ofSeconds(25)));
        assertEquals(test.multipliedBy(-3), PeriodDuration.of(Period.of(-3,  -6, -9), Duration.ofSeconds(-15)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        PeriodDuration.of(Period.ofYears(Integer.MAX_VALUE / 2 + 1)).multipliedBy(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        PeriodDuration.of(Period.ofYears(Integer.MIN_VALUE / 2 - 1)).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(PeriodDuration.of(P1Y2M3D, DUR_5).negated(), PeriodDuration.of(P1Y2M3D.negated(), DUR_5.negated()));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_negated_overflow() {
        PeriodDuration.of(Duration.ofSeconds(Long.MIN_VALUE)).negated();
    }

    //-----------------------------------------------------------------------
    public void test_normalized() {
        assertEquals(PeriodDuration.of(P1Y2M3D, DUR_5).normalized(), PeriodDuration.of(P1Y2M3D.normalized(), DUR_5));
    }

    //-----------------------------------------------------------------------
    public void test_normalizedInexactDuration() {
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(5)).normalizedInexactDuration(),
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(5)));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(25)).normalizedInexactDuration(),
                PeriodDuration.of(P1Y2M3D.plusDays(1), Duration.ofHours(1)));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(-73)).normalizedInexactDuration(),
                PeriodDuration.of(P1Y2M3D.plusDays(-3), Duration.ofHours(-1)));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_normalizedInexactDuration_overflow() {
        PeriodDuration.of(Duration.ofSeconds(Long.MIN_VALUE)).normalizedInexactDuration();
    }

    //-----------------------------------------------------------------------
    public void test_addTo() {
        LocalDateTime base = LocalDateTime.of(2012, 6, 20, 11, 30, 0);
        assertEquals(PeriodDuration.of(P1Y2M3D, DUR_5).addTo(base), LocalDateTime.of(2013, 8, 23, 11, 30, 5));
    }

    //-----------------------------------------------------------------------
    public void test_subtractFrom() {
        LocalDateTime base = LocalDateTime.of(2012, 6, 20, 11, 30, 0);
        assertEquals(PeriodDuration.of(P1Y2M3D, DUR_5).subtractFrom(base), LocalDateTime.of(2011, 4, 17, 11, 29, 55));
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        PeriodDuration test5 = PeriodDuration.of(P1Y2M3D, DUR_5);
        PeriodDuration test6 = PeriodDuration.of(P1Y2M3D, DUR_6);
        assertEquals(test5.equals(test5), true);
        assertEquals(test5.equals(test6), false);
        assertEquals(test6.equals(test5), false);
    }

    public void test_equals_null() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(test.equals(null), false);
    }

    public void test_equals_otherClass() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(test.equals(""), false);
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        PeriodDuration test5 = PeriodDuration.of(P1Y2M3D, DUR_5);
        PeriodDuration test6 = PeriodDuration.of(P1Y2M3D, DUR_6);
        assertEquals(test5.hashCode() == test5.hashCode(), true);
        assertEquals(test5.hashCode() == test6.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals("P1Y2M3DT5S", PeriodDuration.of(P1Y2M3D, DUR_5).toString());
        assertEquals("P1Y2M3D", PeriodDuration.of(P1Y2M3D, Duration.ZERO).toString());
        assertEquals("PT5S", PeriodDuration.of(Period.ZERO, DUR_5).toString());
    }

}
