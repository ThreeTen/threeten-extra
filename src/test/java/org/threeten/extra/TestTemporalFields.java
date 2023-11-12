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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.threeten.extra.TemporalFields.DAY_OF_HALF;
import static org.threeten.extra.TemporalFields.HALF_OF_YEAR;
import static org.threeten.extra.TemporalFields.HALF_YEARS;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

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
    @Test
    public void test_HALF_OF_YEAR() {
        assertEquals(HALF_YEARS, HALF_OF_YEAR.getBaseUnit());
        assertEquals(YEARS, HALF_OF_YEAR.getRangeUnit());
        assertEquals(true, HALF_OF_YEAR.isDateBased());
        assertEquals(false, HALF_OF_YEAR.isTimeBased());
        assertEquals("HalfOfYear", HALF_OF_YEAR.toString());

        assertEquals(true, HALF_OF_YEAR.isSupportedBy(LocalDate.of(2023, 6, 30)));
        assertEquals(true, HALF_OF_YEAR.isSupportedBy(Quarter.Q2));
        assertEquals(false, HALF_OF_YEAR.isSupportedBy(DayOfYear.of(32)));
        assertEquals(true, HALF_OF_YEAR.isSupportedBy(YearMonth.of(2023, 6)));
        assertEquals(1, HALF_OF_YEAR.getFrom(LocalDate.of(2023, 6, 30)));
        assertEquals(2, HALF_OF_YEAR.getFrom(LocalDate.of(2023, 12, 31)));

        assertEquals(ValueRange.of(1, 2), HALF_OF_YEAR.range());
        assertEquals(ValueRange.of(1, 2), HALF_OF_YEAR.rangeRefinedBy(LocalDate.of(2023, 6, 30)));
        assertEquals(ValueRange.of(1, 2), HALF_OF_YEAR.rangeRefinedBy(LocalDate.of(2024, 6, 30)));
        assertEquals(ValueRange.of(1, 2), HALF_OF_YEAR.rangeRefinedBy(LocalDate.of(2023, 7, 30)));

        assertEquals(LocalDate.of(2023, 12, 30), HALF_OF_YEAR.adjustInto(LocalDate.of(2023, 6, 30), 2));
        assertEquals(LocalDate.of(2023, 7, 1), HALF_OF_YEAR.adjustInto(LocalDate.of(2023, 7, 1), 2));

        assertEquals(YearMonth.of(2023, 12), HALF_OF_YEAR.adjustInto(YearMonth.of(2023, 6), 2));
    }

    //-----------------------------------------------------------------------
    // HALF_YEARS
    //-----------------------------------------------------------------------
    @Test
    public void test_HALF_YEARS() {
        assertEquals(true, HALF_YEARS.isDateBased());
        assertEquals(false, HALF_YEARS.isTimeBased());
        assertEquals("HalfYears", HALF_YEARS.toString());

        assertEquals(true, HALF_YEARS.isSupportedBy(LocalDate.of(2023, 6, 30)));
        assertEquals(true, HALF_YEARS.isSupportedBy(YearQuarter.of(2023, 2)));
        assertEquals(true, HALF_YEARS.isSupportedBy(YearMonth.of(2023, 6)));

        assertEquals(LocalDate.of(2023, 12, 30), HALF_YEARS.addTo(LocalDate.of(2023, 6, 30), 1));
        assertEquals(YearQuarter.of(2023, 4), HALF_YEARS.addTo(YearQuarter.of(2023, 2), 1));
        assertEquals(YearMonth.of(2023, 12), HALF_YEARS.addTo(YearMonth.of(2023, 6), 1));

        assertEquals(0, HALF_YEARS.between(LocalDate.of(2023, 6, 30), LocalDate.of(2023, 12, 29)));
        assertEquals(1, HALF_YEARS.between(LocalDate.of(2023, 6, 30), LocalDate.of(2023, 12, 30)));

        assertEquals(0, HALF_YEARS.between(YearQuarter.of(2023, 2), YearQuarter.of(2023, 3)));
        assertEquals(1, HALF_YEARS.between(YearQuarter.of(2023, 2), YearQuarter.of(2023, 4)));

        assertEquals(0, HALF_YEARS.between(YearMonth.of(2023, 2), YearMonth.of(2023, 7)));
        assertEquals(1, HALF_YEARS.between(YearMonth.of(2023, 2), YearMonth.of(2023, 8)));
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
