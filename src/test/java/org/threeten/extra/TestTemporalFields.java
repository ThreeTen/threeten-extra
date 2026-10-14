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

import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.threeten.extra.TemporalFields.DAY_OF_HALF;
import static org.threeten.extra.TemporalFields.HALF_OF_YEAR;
import static org.threeten.extra.TemporalFields.HALF_YEARS;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test TemporalFields.
 */
public class TestTemporalFields {

    //-----------------------------------------------------------------------
    // DAY_OF_HALF
    //-----------------------------------------------------------------------
    @Test
    public void test_DAY_OF_HALF() {
        assertEquals(DAYS, DAY_OF_HALF.getBaseUnit());
        assertEquals(HALF_YEARS, DAY_OF_HALF.getRangeUnit());
        assertEquals(true, DAY_OF_HALF.isDateBased());
        assertEquals(false, DAY_OF_HALF.isTimeBased());
        assertEquals("DayOfHalf", DAY_OF_HALF.toString());

        assertEquals(true, DAY_OF_HALF.isSupportedBy(LocalDate.of(2023, 6, 30)));
        assertEquals(false, DAY_OF_HALF.isSupportedBy(DayOfYear.of(32)));
        assertEquals(false, DAY_OF_HALF.isSupportedBy(YearMonth.of(2023, 6)));
        assertEquals(181, DAY_OF_HALF.getFrom(LocalDate.of(2023, 6, 30)));
        assertEquals(184, DAY_OF_HALF.getFrom(LocalDate.of(2023, 12, 31)));

        assertEquals(ValueRange.of(1, 181, 184), DAY_OF_HALF.range());
        assertEquals(ValueRange.of(1, 181), DAY_OF_HALF.rangeRefinedBy(LocalDate.of(2023, 6, 30)));
        assertEquals(ValueRange.of(1, 182), DAY_OF_HALF.rangeRefinedBy(LocalDate.of(2024, 6, 30)));
        assertEquals(ValueRange.of(1, 184), DAY_OF_HALF.rangeRefinedBy(LocalDate.of(2023, 7, 30)));

        assertEquals(LocalDate.of(2023, 1, 20), DAY_OF_HALF.adjustInto(LocalDate.of(2023, 6, 30), 20));
        assertEquals(LocalDate.of(2023, 7, 20), DAY_OF_HALF.adjustInto(LocalDate.of(2023, 7, 1), 20));
    }

    //-----------------------------------------------------------------------
    // HALF_OF_YEAR
    //-----------------------------------------------------------------------
    static Object[][] data_HALF_OF_YEAR_get() {
        return new Object[][]{
                {LocalDate.of(2023, 1, 1), 1},
                {LocalDate.of(2023, 6, 30), 1},
                {LocalDate.of(2023, 7, 1), 2},
                {LocalDate.of(2023, 12, 31), 2},
                {Month.JANUARY, 1},
                {Month.JUNE, 1},
                {Month.JULY, 2},
                {Month.DECEMBER, 2},
                {Quarter.Q1, 1},
                {Quarter.Q2, 1},
                {Quarter.Q3, 2},
                {Quarter.Q4, 2},
                {Half.H1, 1},
                {Half.H2, 2},
                {YearMonth.of(2023, 1), 1},
                {YearMonth.of(2023, 6), 1},
                {YearMonth.of(2023, 7), 2},
                {YearMonth.of(2023, 12), 2},
                {YearQuarter.of(2023, 1), 1},
                {YearQuarter.of(2023, 2), 1},
                {YearQuarter.of(2023, 3), 2},
                {YearQuarter.of(2023, 4), 2},
                {YearHalf.of(2023, 1), 1},
                {YearHalf.of(2023, 2), 2},
        };
    }

    @ParameterizedTest
    @MethodSource("data_HALF_OF_YEAR_get")
    public void test_HALF_OF_YEAR_get(TemporalAccessor base, int expected) {
        // calling HALF_OF_YEAR.isSupportedBy(base) or HALF_OF_YEAR.getFrom(base) will fail
        // always access via the primary entry point on the temporal object which will
        // call HALF_OF_YEAR.isSupportedBy(base) and HALF_OF_YEAR.getFrom(base) internally
        assertTrue(base.isSupported(HALF_OF_YEAR));
        assertEquals(expected, base.get(HALF_OF_YEAR));

        // range works in both directions because the range is fixed and does not depend on the base object
        assertEquals(ValueRange.of(1, 2), base.range(HALF_OF_YEAR));
        assertEquals(ValueRange.of(1, 2), HALF_OF_YEAR.rangeRefinedBy(base));
    }

    static Object[][] data_HALF_OF_YEAR_set() {
        return new Object[][]{
                {LocalDate.of(2023, 1, 1), 1, LocalDate.of(2023, 1, 1)},
                {LocalDate.of(2023, 1, 1), 2, LocalDate.of(2023, 7, 1)},
                {LocalDate.of(2023, 6, 30), 2, LocalDate.of(2023, 12, 30)},
                {LocalDate.of(2023, 7, 1), 1, LocalDate.of(2023, 1, 1)},
                {LocalDate.of(2023, 12, 31), 1, LocalDate.of(2023, 6, 30)},
                {YearMonth.of(2023, 1), 2, YearMonth.of(2023, 7)},
                {YearMonth.of(2023, 3), 2, YearMonth.of(2023, 9)},
                {YearMonth.of(2023, 6), 2, YearMonth.of(2023, 12)},
                {YearMonth.of(2023, 7), 1, YearMonth.of(2023, 1)},
                {YearMonth.of(2023, 12), 1, YearMonth.of(2023, 6)},
                {YearQuarter.of(2023, 1), 2, YearQuarter.of(2023, 3)},
                {YearQuarter.of(2023, 2), 2, YearQuarter.of(2023, 4)},
                {YearQuarter.of(2023, 3), 1, YearQuarter.of(2023, 1)},
                {YearQuarter.of(2023, 4), 1, YearQuarter.of(2023, 2)},
                {YearHalf.of(2023, 1), 2, YearHalf.of(2023, 2)},
                {YearHalf.of(2023, 2), 1, YearHalf.of(2023, 1)}
        };
    }

    @ParameterizedTest
    @MethodSource("data_HALF_OF_YEAR_set")
    public void test_HALF_OF_YEAR_set(Temporal base, int setTo, Temporal expected) {
        // calling HALF_OF_YEAR.adjustInto(base) will fail
        // always access via the primary entry point on the temporal object which will call adjustInto(base) internally
        assertEquals(expected, base.with(HALF_OF_YEAR, setTo));
    }

    @Test
    public void test_HALF_OF_YEAR() {
        assertEquals(HALF_YEARS, HALF_OF_YEAR.getBaseUnit());
        assertEquals(YEARS, HALF_OF_YEAR.getRangeUnit());
        assertTrue(HALF_OF_YEAR.isDateBased());
        assertFalse(HALF_OF_YEAR.isTimeBased());
        assertEquals("HalfOfYear", HALF_OF_YEAR.toString());

        assertFalse(HALF_OF_YEAR.isSupportedBy(DayOfYear.of(32)));

        assertEquals(ValueRange.of(1, 2), HALF_OF_YEAR.range());
    }

    //-----------------------------------------------------------------------
    // HALF_YEARS
    //-----------------------------------------------------------------------
    @Test
    public void test_HALF_YEARS() {
        assertTrue(HALF_YEARS.isDateBased());
        assertFalse(HALF_YEARS.isTimeBased());
        assertEquals("HalfYears", HALF_YEARS.toString());

        assertTrue(LocalDate.of(2023, 6, 30).isSupported(HALF_YEARS));
        assertTrue(YearMonth.of(2023, 6).isSupported(HALF_YEARS));
        assertTrue(YearQuarter.of(2023, 2).isSupported(HALF_YEARS));
        assertTrue(YearHalf.of(2023, 2).isSupported(HALF_YEARS));

        assertTrue(HALF_YEARS.isSupportedBy(LocalDate.of(2023, 6, 30)));
        assertTrue(HALF_YEARS.isSupportedBy(YearMonth.of(2023, 6)));
        assertTrue(HALF_YEARS.isSupportedBy(YearQuarter.of(2023, 2)));
        // this ought to be true, but the ThreeTen design does not allow it
        assertFalse(HALF_YEARS.isSupportedBy(YearHalf.of(2023, 2)));

        assertEquals(LocalDate.of(2023, 6, 30), LocalDate.of(2023, 12, 31).minus(1, HALF_YEARS));
        assertEquals(YearMonth.of(2023, 6), YearMonth.of(2023, 12).minus(1, HALF_YEARS));
        assertEquals(YearQuarter.of(2023, 2), YearQuarter.of(2023, 4).minus(1, HALF_YEARS));
        assertEquals(YearHalf.of(2023, 2), YearHalf.of(2024, 1).minus(1, HALF_YEARS));

        assertEquals(LocalDate.of(2023, 12, 30), HALF_YEARS.addTo(LocalDate.of(2023, 6, 30), 1));
        assertEquals(YearMonth.of(2023, 12), HALF_YEARS.addTo(YearMonth.of(2023, 6), 1));
        assertEquals(YearQuarter.of(2023, 4), HALF_YEARS.addTo(YearQuarter.of(2023, 2), 1));
        // this ought to be supported, but the ThreeTen design does not allow it
        assertThrows(UnsupportedTemporalTypeException.class, () -> HALF_YEARS.addTo(YearHalf.of(2023, 2), 1));

        assertEquals(0, LocalDate.of(2023, 6, 30).until(LocalDate.of(2023, 12, 29), HALF_YEARS));
        assertEquals(1, LocalDate.of(2023, 6, 30).until(LocalDate.of(2023, 12, 30), HALF_YEARS));

        assertEquals(0, YearMonth.of(2023, 2).until(YearMonth.of(2023, 7), HALF_YEARS));
        assertEquals(1, YearMonth.of(2023, 2).until(YearMonth.of(2023, 8), HALF_YEARS));

        assertEquals(0, YearQuarter.of(2023, 2).until(YearQuarter.of(2023, 3), HALF_YEARS));
        assertEquals(1, YearQuarter.of(2023, 2).until(YearQuarter.of(2023, 4), HALF_YEARS));

        assertEquals(2, YearHalf.of(2023, 2).until(YearHalf.of(2024, 2), HALF_YEARS));
        assertEquals(-1, YearHalf.of(2023, 2).until(YearHalf.of(2023, 1), HALF_YEARS));

        assertEquals(0, HALF_YEARS.between(LocalDate.of(2023, 6, 30), LocalDate.of(2023, 12, 29)));
        assertEquals(1, HALF_YEARS.between(LocalDate.of(2023, 6, 30), LocalDate.of(2023, 12, 30)));

        assertEquals(0, HALF_YEARS.between(YearMonth.of(2023, 2), YearMonth.of(2023, 7)));
        assertEquals(1, HALF_YEARS.between(YearMonth.of(2023, 2), YearMonth.of(2023, 8)));

        assertEquals(0, HALF_YEARS.between(YearQuarter.of(2023, 2), YearQuarter.of(2023, 3)));
        assertEquals(1, HALF_YEARS.between(YearQuarter.of(2023, 2), YearQuarter.of(2023, 4)));
    }

    //-------------------------------------------------------------------------
    @Test
    public void test_resolveStrict() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 20L);
        values.put(HALF_OF_YEAR, 2L);
        values.put(YEAR, 2023L);
        TemporalAccessor resolved = DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.STRICT);
        assertEquals(LocalDate.of(2023, 7, 20), resolved.query(LocalDate::from));
    }

    @Test
    public void test_resolveStrict_noYear() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 20L);
        values.put(HALF_OF_YEAR, 1L);
        assertNull(DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.STRICT));
    }

    @Test
    public void test_resolveStrict_halfOfYear() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 20L);
        values.put(YEAR, 2023L);
        assertNull(DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.STRICT));
    }

    @Test
    public void test_resolveStrict_badHalfOfYear() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 20L);
        values.put(HALF_OF_YEAR, 3L);
        values.put(YEAR, 2023L);
        assertThrows(DateTimeException.class, () -> DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.STRICT));
    }

    @Test
    public void test_resolveStrict_day0() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 0L);
        values.put(HALF_OF_YEAR, 1L);
        values.put(YEAR, 2023L);
        assertThrows(DateTimeException.class, () -> DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.STRICT));
    }

    @Test
    public void test_resolveStrict_day182_notLeap() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 182L);
        values.put(HALF_OF_YEAR, 1L);
        values.put(YEAR, 2023L);
        assertThrows(DateTimeException.class, () -> DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.STRICT));
    }

    @Test
    public void test_resolveStrict_day182_leap() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 182L);
        values.put(HALF_OF_YEAR, 1L);
        values.put(YEAR, 2024L);
        TemporalAccessor resolved = DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.STRICT);
        assertEquals(LocalDate.of(2024, 6, 30), resolved.query(LocalDate::from));
    }

    //-------------------------------------------------------------------------
    @Test
    public void test_resolveSmart_day181_notLeap() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 181L);
        values.put(HALF_OF_YEAR, 1L);
        values.put(YEAR, 2023L);
        TemporalAccessor resolved = DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.SMART);
        assertEquals(LocalDate.of(2023, 6, 30), resolved.query(LocalDate::from));
    }

    @Test
    public void test_resolveSmart_day184() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 184L);
        values.put(HALF_OF_YEAR, 1L);
        values.put(YEAR, 2023L);
        TemporalAccessor resolved = DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.SMART);
        assertEquals(LocalDate.of(2023, 7, 3), resolved.query(LocalDate::from));
    }

    @Test
    public void test_resolve_day185() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 185L);
        values.put(HALF_OF_YEAR, 1L);
        values.put(YEAR, 2023L);
        assertThrows(DateTimeException.class, () -> DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.STRICT));
    }

    //-------------------------------------------------------------------------
    @Test
    public void test_resolveLenient_day181_notLeap() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 181L);
        values.put(HALF_OF_YEAR, 1L);
        values.put(YEAR, 2023L);
        TemporalAccessor resolved = DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.LENIENT);
        assertEquals(LocalDate.of(2023, 6, 30), resolved.query(LocalDate::from));
    }

    @Test
    public void test_resolveLenient_day0() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 0L);
        values.put(HALF_OF_YEAR, 1L);
        values.put(YEAR, 2023L);
        TemporalAccessor resolved = DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.LENIENT);
        assertEquals(LocalDate.of(2022, 12, 31), resolved.query(LocalDate::from));
    }

    @Test
    public void test_resolveLenient_day185() {
        Map<TemporalField, Long> values = new HashMap<>();
        values.put(DAY_OF_HALF, 185L);
        values.put(HALF_OF_YEAR, 1L);
        values.put(YEAR, 2023L);
        TemporalAccessor resolved = DAY_OF_HALF.resolve(values, LocalDate.of(1, 1, 1), ResolverStyle.LENIENT);
        assertEquals(LocalDate.of(2023, 7, 4), resolved.query(LocalDate::from));
    }

}
