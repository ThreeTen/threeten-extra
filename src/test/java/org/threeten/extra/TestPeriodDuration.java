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
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

/**
 * Test class.
 */
public class TestPeriodDuration {

    private static final Period P1Y2M3D = Period.of(1, 2, 3);
    private static final Duration DUR_5 = Duration.ofSeconds(5);
    private static final Duration DUR_6 = Duration.ofSeconds(6);

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(PeriodDuration.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_serialization() throws Exception {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(test, ois.readObject());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ZERO() {
        assertEquals(PeriodDuration.ZERO, PeriodDuration.of(Period.ZERO, Duration.ZERO));
        assertEquals(PeriodDuration.ZERO, PeriodDuration.of(Period.ZERO));
        assertEquals(PeriodDuration.ZERO, PeriodDuration.of(Duration.ZERO));
        assertEquals(Period.ZERO, PeriodDuration.ZERO.getPeriod());
        assertEquals(Duration.ZERO, PeriodDuration.ZERO.getDuration());
        assertEquals(true, PeriodDuration.ZERO.isZero());
        assertEquals(Arrays.asList(YEARS, MONTHS, DAYS, SECONDS, NANOS), PeriodDuration.ZERO.getUnits());
        assertEquals(0, PeriodDuration.ZERO.get(YEARS));
        assertEquals(0, PeriodDuration.ZERO.get(MONTHS));
        assertEquals(0, PeriodDuration.ZERO.get(DAYS));
        assertEquals(0, PeriodDuration.ZERO.get(SECONDS));
        assertEquals(0, PeriodDuration.ZERO.get(NANOS));
    }

    @Test
    public void test_ZERO_getEra() {
        assertThrows(DateTimeException.class, () -> PeriodDuration.ZERO.get(ERAS));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of_both() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(4));
        assertEquals(P1Y2M3D, test.getPeriod());
        assertEquals(Duration.ofSeconds(4), test.getDuration());
        assertEquals(false, test.isZero());
        assertEquals(1, test.get(YEARS));
        assertEquals(2, test.get(MONTHS));
        assertEquals(3, test.get(DAYS));
        assertEquals(4, test.get(SECONDS));
        assertEquals(0, test.get(NANOS));
    }

    @Test
    public void test_of_period() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D);
        assertEquals(P1Y2M3D, test.getPeriod());
        assertEquals(Duration.ZERO, test.getDuration());
        assertEquals(false, test.isZero());
        assertEquals(1, test.get(YEARS));
        assertEquals(2, test.get(MONTHS));
        assertEquals(3, test.get(DAYS));
        assertEquals(0, test.get(SECONDS));
        assertEquals(0, test.get(NANOS));
    }

    @Test
    public void test_of_duration() {
        PeriodDuration test = PeriodDuration.of(Duration.ofSeconds(4));
        assertEquals(Period.ZERO, test.getPeriod());
        assertEquals(Duration.ofSeconds(4), test.getDuration());
        assertEquals(false, test.isZero());
        assertEquals(0, test.get(YEARS));
        assertEquals(0, test.get(MONTHS));
        assertEquals(0, test.get(DAYS));
        assertEquals(4, test.get(SECONDS));
        assertEquals(0, test.get(NANOS));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_between_dates() {
        PeriodDuration test = PeriodDuration.between(LocalDate.of(2012, 6, 20), LocalDate.of(2012, 8, 25));
        assertEquals(Period.between(LocalDate.of(2012, 6, 20), LocalDate.of(2012, 8, 25)), test.getPeriod());
        assertEquals(Duration.ZERO, test.getDuration());
    }

    @Test
    public void test_between_times() {
        PeriodDuration test = PeriodDuration.between(LocalTime.of(11, 20), LocalTime.of(12, 25));
        assertEquals(Period.ZERO, test.getPeriod());
        assertEquals(Duration.between(LocalTime.of(11, 20), LocalTime.of(12, 25)), test.getDuration());
    }

    @Test
    public void test_between_mixed1() {
        PeriodDuration test = PeriodDuration.between(LocalDate.of(2012, 6, 20), LocalTime.of(11, 25));
        assertEquals(Period.ZERO, test.getPeriod());
        assertEquals(Duration.ofHours(11).plusMinutes(25), test.getDuration());
    }

    @Test
    public void test_between_mixed2() {
        PeriodDuration test = PeriodDuration.between(LocalDate.of(2012, 6, 20), LocalDateTime.of(2012, 7, 22, 11, 25));
        assertEquals(Period.of(0, 1, 2), test.getPeriod());
        assertEquals(Duration.ofHours(11).plusMinutes(25), test.getDuration());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_from() {
        assertEquals(PeriodDuration.from(PeriodDuration.of(P1Y2M3D)), PeriodDuration.from(PeriodDuration.of(P1Y2M3D)));
        assertEquals(PeriodDuration.of(Period.ofYears(2)), PeriodDuration.from(Period.ofYears(2)));
        assertEquals(PeriodDuration.of(Duration.ofSeconds(2)), PeriodDuration.from(Duration.ofSeconds(2)));
        assertEquals(PeriodDuration.of(Period.ofYears(2)), PeriodDuration.from(Years.of(2)));
        assertEquals(PeriodDuration.of(Period.ofMonths(2)), PeriodDuration.from(Months.of(2)));
        assertEquals(PeriodDuration.of(Period.ofWeeks(2)), PeriodDuration.from(Weeks.of(2)));
        assertEquals(PeriodDuration.of(Period.ofDays(2)), PeriodDuration.from(Days.of(2)));
        assertEquals(PeriodDuration.of(Duration.ofHours(2)), PeriodDuration.from(Hours.of(2)));
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_valid() {
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

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid(String str, Period period, Duration duration) {
        assertEquals(PeriodDuration.of(period, duration), PeriodDuration.parse(str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialPlus(String str, Period period, Duration duration) {
        assertEquals(PeriodDuration.of(period, duration), PeriodDuration.parse("+" + str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialMinus(String str, Period period, Duration duration) {
        assertEquals(PeriodDuration.of(period, duration).negated(), PeriodDuration.parse("-" + str));
    }

    public static Object[][] data_invalid() {
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

    @ParameterizedTest
    @MethodSource("data_invalid")
    public void test_parse_CharSequence_invalid(String str) {
        assertThrows(DateTimeParseException.class, () -> PeriodDuration.parse(str));
    }

    @Test
    public void test_parse_CharSequence_null() {
        assertThrows(NullPointerException.class, () -> PeriodDuration.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount_PeriodDuration() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(PeriodDuration.of(Period.of(4, 4, 4), DUR_5), test.plus(Period.of(3, 2, 1)));
        assertEquals(PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(9)), test.plus(Duration.ofSeconds(4)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.of(Integer.MAX_VALUE - 1, 0, 0)).plus(PeriodDuration.of(Period.ofYears(2))));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.of(Integer.MIN_VALUE + 1, 0, 0)).plus(PeriodDuration.of(Period.ofYears(-2))));
    }

    @Test
    public void test_plus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> P1Y2M3D.plus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount_PeriodDuration() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(PeriodDuration.of(Period.of(0, 1, 2), DUR_5), test.minus(Period.of(1, 1, 1)));
        assertEquals(PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(1)), test.minus(Duration.ofSeconds(4)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.of(Integer.MAX_VALUE - 1, 0, 0)).minus(PeriodDuration.of(Period.ofYears(-2))));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.of(Integer.MIN_VALUE + 1, 0, 0)).minus(PeriodDuration.of(Period.ofYears(2))));
    }

    @Test
    public void test_minus_TemporalAmount_null() {
        assertThrows(NullPointerException.class, () -> P1Y2M3D.minus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(PeriodDuration.ZERO, test.multipliedBy(0));
        assertEquals(test, test.multipliedBy(1));
        assertEquals(PeriodDuration.of(Period.of(5,  10, 15), Duration.ofSeconds(25)), test.multipliedBy(5));
        assertEquals(PeriodDuration.of(Period.of(-3,  -6, -9), Duration.ofSeconds(-15)), test.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.ofYears(Integer.MAX_VALUE / 2 + 1)).multipliedBy(2));
    }

    @Test
    public void test_multipliedBy_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.ofYears(Integer.MIN_VALUE / 2 - 1)).multipliedBy(2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_negated() {
        assertEquals(PeriodDuration.of(P1Y2M3D.negated(), DUR_5.negated()), PeriodDuration.of(P1Y2M3D, DUR_5).negated());
    }

    @Test
    public void test_negated_overflow() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Duration.ofSeconds(Long.MIN_VALUE)).negated());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_normalizedYears() {
        assertEquals(PeriodDuration.of(P1Y2M3D.normalized(), DUR_5), PeriodDuration.of(P1Y2M3D, DUR_5).normalizedYears());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_normalizedStandardDays() {
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(5)),
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(5)).normalizedStandardDays());
        assertEquals(
                PeriodDuration.of(P1Y2M3D.plusDays(1), Duration.ofHours(1)),
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(25)).normalizedStandardDays());
        assertEquals(
                PeriodDuration.of(P1Y2M3D.plusDays(-3), Duration.ofHours(-1)),
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(-73)).normalizedStandardDays());
    }

    @Test
    public void test_normalizedStandardDaysn_overflow() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Duration.ofSeconds(Long.MIN_VALUE)).normalizedStandardDays());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_addTo() {
        LocalDateTime base = LocalDateTime.of(2012, 6, 20, 11, 30, 0);
        assertEquals(LocalDateTime.of(2013, 8, 23, 11, 30, 5), PeriodDuration.of(P1Y2M3D, DUR_5).addTo(base));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_subtractFrom() {
        LocalDateTime base = LocalDateTime.of(2012, 6, 20, 11, 30, 0);
        assertEquals(LocalDateTime.of(2011, 4, 17, 11, 29, 55), PeriodDuration.of(P1Y2M3D, DUR_5).subtractFrom(base));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_truncatedTo() {
        assertEquals(
                PeriodDuration.of(P1Y2M3D, DUR_5), 
                PeriodDuration.of(P1Y2M3D, DUR_5).truncatedTo(NANOS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofNanos(-2_000_000)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofNanos(-2_000_456)).truncatedTo(MILLIS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, DUR_5), 
                PeriodDuration.of(P1Y2M3D, DUR_5).truncatedTo(SECONDS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ZERO), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(0)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ZERO), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(1)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ZERO), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(59)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(1)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(60)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(1)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(61)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(1)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(119)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(2)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(120)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofDays(0)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(24 * 60 - 1)).truncatedTo(DAYS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofDays(1)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(24)).truncatedTo(DAYS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofDays(2)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(51)).truncatedTo(DAYS));
        assertThrows(DateTimeException.class, () ->  PeriodDuration.of(P1Y2M3D, Duration.ofHours(51)).truncatedTo(MONTHS));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(PeriodDuration.of(P1Y2M3D, DUR_5), PeriodDuration.of(P1Y2M3D, DUR_5))
            .addEqualityGroup(PeriodDuration.of(P1Y2M3D, DUR_6), PeriodDuration.of(P1Y2M3D, DUR_6))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals("P1Y2M3DT5S", PeriodDuration.of(P1Y2M3D, DUR_5).toString());
        assertEquals("P1Y2M3D", PeriodDuration.of(P1Y2M3D, Duration.ZERO).toString());
        assertEquals("PT5S", PeriodDuration.of(Period.ZERO, DUR_5).toString());
    }

}
