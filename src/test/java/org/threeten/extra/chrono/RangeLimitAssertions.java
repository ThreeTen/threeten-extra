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
package org.threeten.extra.chrono;

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;

/**
 * Shared assertions for checking the supported date range of a chronology.
 */
final class RangeLimitAssertions {

    private RangeLimitAssertions() {
    }

    /**
     * Checks that the min and max dates are valid and that nothing beyond them can be created.
     *
     * @param chrono  the chronology
     * @param min  the earliest valid date
     * @param max  the latest valid date
     */
    static void assertRangeLimits(Chronology chrono, ChronoLocalDate min, ChronoLocalDate max) {
        assertEquals(min, chrono.dateEpochDay(min.toEpochDay()));
        assertEquals(max, chrono.dateEpochDay(max.toEpochDay()));
        assertThrows(DateTimeException.class, () -> chrono.dateEpochDay(min.toEpochDay() - 1));
        assertThrows(DateTimeException.class, () -> chrono.dateEpochDay(max.toEpochDay() + 1));
        assertThrows(DateTimeException.class, () -> chrono.dateEpochDay(Long.MIN_VALUE));
        assertThrows(DateTimeException.class, () -> chrono.dateEpochDay(Long.MAX_VALUE));
        assertThrows(DateTimeException.class, () -> max.plus(1, DAYS));
        assertThrows(DateTimeException.class, () -> min.minus(1, DAYS));
        assertThrows(DateTimeException.class, () -> max.plus(1, WEEKS));
        assertThrows(DateTimeException.class, () -> min.minus(1, WEEKS));
        assertThrows(DateTimeException.class, () -> max.plus(1, MONTHS));
        assertThrows(DateTimeException.class, () -> min.minus(1, MONTHS));
        assertThrows(DateTimeException.class, () -> max.plus(1, YEARS));
        assertThrows(DateTimeException.class, () -> min.minus(1, YEARS));
        assertThrows(DateTimeException.class, () -> max.plus(1_000_000, YEARS));
        assertThrows(DateTimeException.class, () -> min.minus(1_000_000, YEARS));
        assertThrows(DateTimeException.class, () -> max.plus(13_000_000L, MONTHS));
        assertThrows(DateTimeException.class, () -> min.minus(13_000_000L, MONTHS));
        long minMonth = min.getLong(PROLEPTIC_MONTH);
        long maxMonth = max.getLong(PROLEPTIC_MONTH);
        assertEquals(min.with(PROLEPTIC_MONTH, minMonth), min);
        assertEquals(max.with(PROLEPTIC_MONTH, maxMonth), max);
        assertThrows(DateTimeException.class, () -> min.with(PROLEPTIC_MONTH, minMonth - 1));
        assertThrows(DateTimeException.class, () -> max.with(PROLEPTIC_MONTH, maxMonth + 1));
        assertThrows(DateTimeException.class, () -> min.with(PROLEPTIC_MONTH, Long.MIN_VALUE));
        assertThrows(DateTimeException.class, () -> max.with(PROLEPTIC_MONTH, Long.MAX_VALUE));
        assertFieldRange(chrono, min, max, YEAR);
        assertFieldRange(chrono, min, max, YEAR_OF_ERA);
        assertFieldRange(chrono, min, max, MONTH_OF_YEAR);
    }

    private static void assertFieldRange(Chronology chrono, ChronoLocalDate min, ChronoLocalDate max, ChronoField field) {
        ValueRange range = chrono.range(field);
        for (ChronoLocalDate date : new ChronoLocalDate[] {min, max}) {
            assertTrue(range.isValidValue(date.getLong(field)), field + " " + date.getLong(field) + " of " + date + " not in " + range);
        }
        assertThrows(DateTimeException.class, () -> max.with(field, range.getMaximum() + 1), "max " + field);
        // Symmetry dates treat a value of zero as a no-op in with()
        if (range.getMinimum() - 1 != 0) {
            assertThrows(DateTimeException.class, () -> min.with(field, range.getMinimum() - 1), "min " + field);
        }
    }

    /**
     * Checks that the min and max dates are valid and that nothing beyond them can be created,
     * where the chronology range is narrower than the ISO range.
     *
     * @param chrono  the chronology
     * @param min  the earliest valid date
     * @param max  the latest valid date
     */
    static void assertNarrowRangeLimits(Chronology chrono, ChronoLocalDate min, ChronoLocalDate max) {
        assertRangeLimits(chrono, min, max);
        assertThrows(DateTimeException.class, () -> chrono.date(LocalDate.MAX));
        assertThrows(DateTimeException.class, () -> chrono.date(LocalDate.MIN));
    }

}
