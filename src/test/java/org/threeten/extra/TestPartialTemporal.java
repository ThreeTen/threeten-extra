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
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.extra;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.YEAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PartialTemporal}.
 */
public class TestPartialTemporal {

    @Test
    public void test_factoriesAndImmutableUpdates() {
        PartialTemporal empty = PartialTemporal.empty();
        PartialTemporal date = empty.withField(YEAR, 2024).withField(DAY_OF_MONTH, 29);

        assertEquals(0, empty.size());
        assertEquals(2, date.size());
        assertTrue(date.isSupported(YEAR));
        assertEquals(2024, date.getLong(YEAR));
        assertFalse(date.isSupported(NANO_OF_DAY));
        PartialTemporal sameDate = PartialTemporal.of(YEAR, 2024).withField(DAY_OF_MONTH, 29);
        assertEquals(date, sameDate);
        assertEquals(date.hashCode(), sameDate.hashCode());
        // toString has a sorted order of fields, so the output is more predictable (but you should not rely on this!)
        assertEquals("Partial[fieldValues={Year=2024, DayOfMonth=29}, null]", date.toString());
    }

    @Test
    public void test_queries() {
        PartialTemporal partial = PartialTemporal.of(EPOCH_DAY, LocalDate.of(2024, 2, 29).toEpochDay())
                .withField(NANO_OF_DAY, LocalTime.NOON.toNanoOfDay())
                .withField(java.time.temporal.ChronoField.OFFSET_SECONDS, 3600)
                .withZone(ZoneId.of("Europe/London"));

        assertEquals(LocalDate.of(2024, 2, 29), partial.query(java.time.temporal.TemporalQueries.localDate()));
        assertEquals(LocalTime.NOON, partial.query(java.time.temporal.TemporalQueries.localTime()));
        assertEquals(ZoneOffset.ofHours(1), partial.query(java.time.temporal.TemporalQueries.offset()));
        assertEquals(Optional.of(ZoneId.of("Europe/London")), partial.getZone());
    }

    @Test
    public void test_matchesAndFormat() {
        PartialTemporal partial = PartialTemporal.of(YEAR, 2024);
        assertTrue(partial.matches(LocalDate.of(2024, 6, 1)));
        assertFalse(partial.matches(LocalDate.of(2023, 6, 1)));
        assertEquals("2024", partial.format(DateTimeFormatter.ofPattern("u")));
    }

    @Test
    public void test_rangeDoesNotRefineAndUnsupported() {
        PartialTemporal partial = PartialTemporal.of(DAY_OF_MONTH, 1);
        assertEquals(DAY_OF_MONTH.range(), partial.range(DAY_OF_MONTH));
        assertThrows(UnsupportedTemporalTypeException.class, () -> partial.range(YEAR));
        assertThrows(UnsupportedTemporalTypeException.class, () -> partial.getLong(YEAR));
    }

    @Test
    public void test_resolve_validDateAndTime() {
        PartialTemporal partial = PartialTemporal.of(YEAR, 2024)
                .withField(MONTH_OF_YEAR, 2)
                .withField(DAY_OF_MONTH, 29)
                .withField(DAY_OF_WEEK, 4)
                .withField(HOUR_OF_DAY, 12)
                .withField(MINUTE_OF_HOUR, 34);
        TemporalAccessor resolved = partial.resolve(ResolverStyle.STRICT);

        assertEquals(LocalDate.of(2024, 2, 29), LocalDate.from(resolved));
        assertEquals(LocalTime.of(12, 34), LocalTime.from(resolved));
        assertEquals(2024, resolved.getLong(YEAR));
        assertEquals(29, resolved.getLong(DAY_OF_MONTH));
    }

    @Test
    public void test_resolve_resolverStyles() {
        PartialTemporal partial = PartialTemporal.of(YEAR, 2023)
                .withField(MONTH_OF_YEAR, 2)
                .withField(DAY_OF_MONTH, 29);

        assertThrows(DateTimeException.class, () -> partial.resolve(ResolverStyle.STRICT));
        assertEquals(LocalDate.of(2023, 2, 28), LocalDate.from(partial.resolve(ResolverStyle.SMART)));
        assertEquals(LocalDate.of(2023, 3, 1), LocalDate.from(partial.resolve(ResolverStyle.LENIENT)));
    }

    @Test
    public void test_resolve_incompleteAndConflictingFields() {
        PartialTemporal incomplete = PartialTemporal.of(YEAR, 2024).withField(MONTH_OF_YEAR, 2);
        TemporalAccessor resolved = incomplete.resolve(ResolverStyle.STRICT);

        assertEquals(2024, resolved.getLong(YEAR));
        assertEquals(2, resolved.getLong(MONTH_OF_YEAR));
        DateTimeException ex1 = assertThrows(DateTimeException.class, () -> LocalDate.from(resolved));
        assertTrue(ex1.getMessage().contains("Unable to obtain LocalDate from TemporalAccessor"));

        PartialTemporal conflicting = PartialTemporal.of(EPOCH_DAY, 10).withField(YEAR, 2024);
        DateTimeException ex2 = assertThrows(DateTimeException.class, () -> conflicting.resolve(ResolverStyle.STRICT));
        // text could be either way around, as the map has no sort order (for performance reasons)
        assertTrue(ex2.getMessage().contains("Text '2024|10|' could not be parsed:") ||
                ex2.getMessage().contains("Text '10|2024|' could not be parsed:"));
        assertTrue(ex2.getMessage().contains("Conflict found: Field Year 1970 differs from Year 2024 derived from 1970-01-11") ||
                ex2.getMessage().contains("Conflict found: Field Year 2024 differs from Year 1970 derived from 1970-01-11"));
    }

    @Test
    public void test_resolve_zoneIsRetained() {
        ZoneId zone = ZoneId.of("Europe/London");
        PartialTemporal partial = PartialTemporal.of(YEAR, 2024)
                .withField(MONTH_OF_YEAR, 1)
                .withField(DAY_OF_MONTH, 1)
                .withZone(zone);
        TemporalAccessor resolved = partial.resolve(ResolverStyle.STRICT);

        assertEquals(zone, resolved.query(TemporalQueries.zone()));
    }
}
