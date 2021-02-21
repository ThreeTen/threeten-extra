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

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

/**
 * Test class.
 */
public class TestInterval {

    static Instant NOW1 = ZonedDateTime.of(2014, 12, 1, 1, 0, 0, 0, ZoneOffset.UTC).toInstant();
    static Instant NOW2 = NOW1.plusSeconds(60);
    static Instant NOW3 = NOW2.plusSeconds(60);
    static Instant NOW4 = NOW3.plusSeconds(60);

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Interval.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_serialization() throws Exception {
        Interval test = Interval.of(Instant.EPOCH, NOW1);
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
    public void test_ALL() {
        Interval test = Interval.ALL;
        assertEquals(Instant.MIN, test.getStart());
        assertEquals(Instant.MAX, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of_Instant_Instant() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(NOW1, test.getStart());
        assertEquals(NOW2, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
    }

    @Test
    public void test_of_Instant_Instant_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(NOW1, test.getStart());
        assertEquals(NOW1, test.getEnd());
        assertEquals(true, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
    }

    @Test
    public void test_of_Instant_Instant_badOrder() {
        assertThrows(DateTimeException.class, () -> Interval.of(NOW2, NOW1));
    }

    @Test
    public void test_of_Instant_Instant_nullStart() {
        assertThrows(NullPointerException.class, () -> Interval.of(null, NOW2));
    }

    @Test
    public void test_of_Instant_Instant_nullEnd() {
        assertThrows(NullPointerException.class, () -> Interval.of(NOW1, (Instant) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of_Instant_Duration() {
        Interval test = Interval.of(NOW1, Duration.ofSeconds(60));
        assertEquals(NOW1, test.getStart());
        assertEquals(NOW2, test.getEnd());
    }

    @Test
    public void test_of_Instant_Duration_zero() {
        Interval test = Interval.of(NOW1, Duration.ZERO);
        assertEquals(NOW1, test.getStart());
        assertEquals(NOW1, test.getEnd());
    }

    @Test
    public void test_of_Instant_Duration_negative() {
        assertThrows(DateTimeException.class, () -> Interval.of(NOW2, Duration.ofSeconds(-1)));
    }

    @Test
    public void test_of_Instant_Duration_nullInstant() {
        assertThrows(NullPointerException.class, () -> Interval.of(null, Duration.ZERO));
    }

    @Test
    public void test_of_Instant_Duration_nullDuration() {
        assertThrows(NullPointerException.class, () -> Interval.of(NOW1, (Duration) null));
    }

    /* Lower and upper bound for Intervals */
    private static final Instant MIN_OFFSET_DATE_TIME = OffsetDateTime.MIN.plusDays(1L).toInstant();
    private static final Instant MAX_OFFSET_DATE_TIME = OffsetDateTime.MAX.minusDays(1L).toInstant();

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_parseValid() {
        Instant minPlusOneDay = Instant.MIN.plus(Duration.ofDays(1));
        Instant maxMinusOneDay = Instant.MAX.minus(Duration.ofDays(1));
        return new Object[][] {
            {NOW1 + "/" + NOW2, NOW1, NOW2},
            {Duration.ofHours(6) + "/" + NOW2, NOW2.minus(6, HOURS), NOW2},
            {"P6MT5H/" + NOW2, NOW2.atZone(ZoneOffset.UTC).minus(6, MONTHS).minus(5, HOURS).toInstant(), NOW2},
            {"pt6h/" + NOW2, NOW2.minus(6, HOURS), NOW2},
            {"pt6h/" + Instant.MAX, Instant.MAX.minus(6, HOURS), Instant.MAX},
            {"pt6h/" + minPlusOneDay, minPlusOneDay.minus(6, HOURS), minPlusOneDay},
            {NOW1 + "/" + Duration.ofHours(6), NOW1, NOW1.plus(6, HOURS)},
            {NOW1 + "/pt6h", NOW1, NOW1.plus(6, HOURS)},
            {Instant.MIN + "/pt6h", Instant.MIN, Instant.MIN.plus(6, HOURS)},
            {maxMinusOneDay + "/Pt6h", maxMinusOneDay, maxMinusOneDay.plus(6, HOURS)},
            {NOW1 + "/" + NOW1, NOW1, NOW1},
            {NOW1.atOffset(ZoneOffset.ofHours(2)) + "/" + NOW2.atOffset(ZoneOffset.ofHours(2)), NOW1, NOW2},
            {NOW1.atOffset(ZoneOffset.ofHours(2)) + "/" + NOW2.atOffset(ZoneOffset.ofHours(3)), NOW1, NOW2},
            {NOW1.atOffset(ZoneOffset.ofHours(2)) + "/" + NOW2.atOffset(ZoneOffset.ofHours(2)).toLocalDateTime(), NOW1, NOW2},
            {MIN_OFFSET_DATE_TIME.toString() + "/" + MAX_OFFSET_DATE_TIME, MIN_OFFSET_DATE_TIME, MAX_OFFSET_DATE_TIME},
            {NOW1 + "/" + Instant.MAX, NOW1, Instant.MAX},
            {Instant.MIN.toString() + "/" + NOW2, Instant.MIN, NOW2},
            {Instant.MIN.toString() + "/" + Instant.MAX, Instant.MIN, Instant.MAX}
        };
    }

    @ParameterizedTest
    @UseDataProvider("data_parseValid")
    public void test_parse_CharSequence(String input, Instant start, Instant end) {
        Interval test = Interval.parse(input);
        assertEquals(start, test.getStart());
        assertEquals(end, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_badOrder() {
        assertThrows(DateTimeException.class, () -> Interval.parse(NOW2 + "/" + NOW1));
    }

    @Test
    public void test_parse_CharSequence_badFormat() {
        assertThrows(DateTimeParseException.class, () -> Interval.parse(NOW2 + "-" + NOW1));
    }

    @Test
    public void test_parse_CharSequence_null() {
        assertThrows(NullPointerException.class, () -> Interval.parse(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withStart() {
        Interval base = Interval.of(NOW1, NOW3);
        Interval test = base.withStart(NOW2);
        assertEquals(NOW2, test.getStart());
        assertEquals(NOW3, test.getEnd());
    }

    @Test
    public void test_withStart_badOrder() {
        Interval base = Interval.of(NOW1, NOW2);
        assertThrows(DateTimeException.class, () -> base.withStart(NOW3));
    }

    @Test
    public void test_withStart_null() {
        Interval base = Interval.of(NOW1, NOW2);
        assertThrows(NullPointerException.class, () -> base.withStart(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withEnd() {
        Interval base = Interval.of(NOW1, NOW3);
        Interval test = base.withEnd(NOW2);
        assertEquals(NOW1, test.getStart());
        assertEquals(NOW2, test.getEnd());
    }

    @Test
    public void test_withEnd_badOrder() {
        Interval base = Interval.of(NOW2, NOW3);
        assertThrows(DateTimeException.class, () -> base.withEnd(NOW1));
    }

    @Test
    public void test_withEnd_null() {
        Interval base = Interval.of(NOW1, NOW2);
        assertThrows(NullPointerException.class, () -> base.withEnd(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_contains_Instant() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(false, test.contains(NOW1.minusSeconds(1)));
        assertEquals(true, test.contains(NOW1));
        assertEquals(true, test.contains(NOW1.plusSeconds(1)));
        assertEquals(true, test.contains(NOW2.minusSeconds(1)));
        assertEquals(false, test.contains(NOW2));
    }

    @Test
    public void test_contains_Instant_baseEmpty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(false, test.contains(NOW1.minusSeconds(1)));
        assertEquals(false, test.contains(NOW1));
        assertEquals(false, test.contains(NOW1.plusSeconds(1)));
    }

    @Test
    public void test_contains_max() {
        Interval test = Interval.of(NOW2, Instant.MAX);
        assertEquals(false, test.contains(Instant.MIN));
        assertEquals(false, test.contains(NOW1));
        assertEquals(true, test.contains(NOW2));
        assertEquals(true, test.contains(NOW3));
        assertEquals(true, test.contains(Instant.MAX));
    }

    @Test
    public void test_contains_Instant_null() {
        Interval base = Interval.of(NOW1, NOW2);
        assertThrows(NullPointerException.class, () -> base.contains((Instant) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_encloses_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(false, test.encloses(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(false, test.encloses(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // partly before
        assertEquals(false, test.encloses(Interval.of(NOW1.minusSeconds(1), NOW2)));
        assertEquals(false, test.encloses(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))));
        // contained
        assertEquals(true, test.encloses(Interval.of(NOW1, NOW2.minusSeconds(1))));
        assertEquals(true, test.encloses(Interval.of(NOW1, NOW2)));
        assertEquals(true, test.encloses(Interval.of(NOW1.plusSeconds(1), NOW2)));
        // partly after
        assertEquals(false, test.encloses(Interval.of(NOW1, NOW2.plusSeconds(1))));
        assertEquals(false, test.encloses(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))));
        // completely after
        assertEquals(false, test.encloses(Interval.of(NOW2, NOW2.plusSeconds(1))));
        assertEquals(false, test.encloses(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))));
    }

    @Test
    public void test_encloses_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(false, test.encloses(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        // partly before
        assertEquals(false, test.encloses(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // equal
        assertEquals(true, test.encloses(Interval.of(NOW1, NOW1)));
        // completely after
        assertEquals(false, test.encloses(Interval.of(NOW1, NOW1.plusSeconds(1))));
        assertEquals(false, test.encloses(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))));
    }

    @Test
    public void test_encloses_Interval_null() {
        Interval base = Interval.of(NOW1, NOW2);
        assertThrows(NullPointerException.class, () -> base.encloses((Interval) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_abuts_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(false, test.abuts(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(true, test.abuts(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // partly before
        assertEquals(false, test.abuts(Interval.of(NOW1.minusSeconds(1), NOW2)));
        assertEquals(false, test.abuts(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))));
        // contained
        assertEquals(false, test.abuts(Interval.of(NOW1, NOW2.minusSeconds(1))));
        assertEquals(false, test.abuts(Interval.of(NOW1, NOW2)));
        assertEquals(false, test.abuts(Interval.of(NOW1.plusSeconds(1), NOW2)));
        // partly after
        assertEquals(false, test.abuts(Interval.of(NOW1, NOW2.plusSeconds(1))));
        assertEquals(false, test.abuts(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))));
        // completely after
        assertEquals(true, test.abuts(Interval.of(NOW2, NOW2.plusSeconds(1))));
        assertEquals(false, test.abuts(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))));
    }

    @Test
    public void test_abuts_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(false, test.abuts(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(true, test.abuts(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // equal
        assertEquals(false, test.abuts(Interval.of(NOW1, NOW1)));
        // completely after
        assertEquals(true, test.abuts(Interval.of(NOW1, NOW1.plusSeconds(1))));
        assertEquals(false, test.abuts(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))));
    }

    @Test
    public void test_abuts_Interval_null() {
        Interval base = Interval.of(NOW1, NOW2);
        assertThrows(NullPointerException.class, () -> base.abuts((Interval) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_isConnected_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(false, test.isConnected(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(true, test.isConnected(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // partly before
        assertEquals(true, test.isConnected(Interval.of(NOW1.minusSeconds(1), NOW2)));
        assertEquals(true, test.isConnected(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))));
        // contained
        assertEquals(true, test.isConnected(Interval.of(NOW1, NOW2.minusSeconds(1))));
        assertEquals(true, test.isConnected(Interval.of(NOW1, NOW2)));
        assertEquals(true, test.isConnected(Interval.of(NOW1.plusSeconds(1), NOW2)));
        // partly after
        assertEquals(true, test.isConnected(Interval.of(NOW1, NOW2.plusSeconds(1))));
        assertEquals(true, test.isConnected(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))));
        // completely after
        assertEquals(true, test.isConnected(Interval.of(NOW2, NOW2.plusSeconds(1))));
        assertEquals(false, test.isConnected(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))));
    }

    @Test
    public void test_isConnected_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(false, test.isConnected(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(true, test.isConnected(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // equal
        assertEquals(true, test.isConnected(Interval.of(NOW1, NOW1)));
        // completely after
        assertEquals(true, test.isConnected(Interval.of(NOW1, NOW1.plusSeconds(1))));
        assertEquals(false, test.isConnected(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))));
    }

    @Test
    public void test_isConnected_Interval_null() {
        Interval base = Interval.of(NOW1, NOW2);
        assertThrows(NullPointerException.class, () -> base.isConnected((Interval) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_overlaps_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(false, test.overlaps(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(false, test.overlaps(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // partly before
        assertEquals(true, test.overlaps(Interval.of(NOW1.minusSeconds(1), NOW2)));
        assertEquals(true, test.overlaps(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))));
        // contained
        assertEquals(true, test.overlaps(Interval.of(NOW1, NOW2.minusSeconds(1))));
        assertEquals(true, test.overlaps(Interval.of(NOW1, NOW2)));
        assertEquals(true, test.overlaps(Interval.of(NOW1.plusSeconds(1), NOW2)));
        // partly after
        assertEquals(true, test.overlaps(Interval.of(NOW1, NOW2.plusSeconds(1))));
        assertEquals(true, test.overlaps(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))));
        // completely after
        assertEquals(false, test.overlaps(Interval.of(NOW2, NOW2.plusSeconds(1))));
        assertEquals(false, test.overlaps(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))));
    }

    @Test
    public void test_overlaps_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(false, test.overlaps(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(false, test.overlaps(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // equal
        assertEquals(true, test.overlaps(Interval.of(NOW1, NOW1)));
        // completely after
        assertEquals(false, test.overlaps(Interval.of(NOW1, NOW1.plusSeconds(1))));
        assertEquals(false, test.overlaps(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))));
    }

    @Test
    public void test_overlaps_Interval_null() {
        Interval base = Interval.of(NOW1, NOW2);
        assertThrows(NullPointerException.class, () -> base.overlaps((Interval) null));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_intersection() {
        return new Object[][] {
            // adjacent
            { NOW1, NOW2, NOW2, NOW4, NOW2, NOW2 },
            // adjacent empty
            { NOW1, NOW4, NOW4, NOW4, NOW4, NOW4 },
            // overlap
            { NOW1, NOW3, NOW2, NOW4, NOW2, NOW3 },
            // encloses
            { NOW1, NOW4, NOW2, NOW3, NOW2, NOW3 },
            // encloses empty
            { NOW1, NOW4, NOW2, NOW2, NOW2, NOW2 },
        };
    }

    @ParameterizedTest
    @UseDataProvider("data_intersection")
    public void test_intersection(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertTrue(test1.isConnected(test2));
        assertEquals(expected, test1.intersection(test2));
    }

    @ParameterizedTest
    @UseDataProvider("data_intersection")
    public void test_intersection_reverse(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertTrue(test2.isConnected(test1));
        assertEquals(expected, test2.intersection(test1));
    }

    @Test
    public void test_intersectionBad() {
        Interval test1 = Interval.of(NOW1, NOW2);
        Interval test2 = Interval.of(NOW3, NOW4);
        assertEquals(false, test1.isConnected(test2));
        assertThrows(DateTimeException.class, () -> test1.intersection(test2));
    }

    @Test
    public void test_intersection_same() {
        Interval test = Interval.of(NOW2, NOW4);
        assertEquals(test, test.intersection(test));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_union() {
        return new Object[][] {
            // adjacent
            { NOW1, NOW2, NOW2, NOW4, NOW1, NOW4 },
            // adjacent empty
            { NOW1, NOW4, NOW4, NOW4, NOW1, NOW4 },
            // overlap
            { NOW1, NOW3, NOW2, NOW4, NOW1, NOW4 },
            // encloses
            { NOW1, NOW4, NOW2, NOW3, NOW1, NOW4 },
            // encloses empty
            { NOW1, NOW4, NOW2, NOW2, NOW1, NOW4 },
        };
    }

    @ParameterizedTest
    @UseDataProvider("data_union")
    public void test_unionAndSpan(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertTrue(test1.isConnected(test2));
        assertEquals(expected, test1.union(test2));
        assertEquals(expected, test1.span(test2));
    }

    @ParameterizedTest
    @UseDataProvider("data_union")
    public void test_unionAndSpan_reverse(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertTrue(test2.isConnected(test1));
        assertEquals(expected, test2.union(test1));
        assertEquals(expected, test2.span(test1));
    }

    @ParameterizedTest
    @UseDataProvider("data_union")
    public void test_span_enclosesInputs(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertEquals(true, expected.encloses(test1));
        assertEquals(true, expected.encloses(test2));
    }

    @Test
    public void test_union_disconnected() {
        Interval test1 = Interval.of(NOW1, NOW2);
        Interval test2 = Interval.of(NOW3, NOW4);
        assertFalse(test1.isConnected(test2));
        assertThrows(DateTimeException.class, () -> test1.union(test2));
    }

    @Test
    public void test_span_disconnected() {
        Interval test1 = Interval.of(NOW1, NOW2);
        Interval test2 = Interval.of(NOW3, NOW4);
        assertFalse(test1.isConnected(test2));
        assertEquals(Interval.of(NOW1, NOW4), test1.span(test2));
    }

    @Test
    public void test_unionAndSpan_same() {
        Interval test = Interval.of(NOW2, NOW4);
        assertEquals(test, test.union(test));
        assertEquals(test, test.span(test));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_isAfter_Instant() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(true, test.isAfter(NOW1.minusSeconds(2)));
        assertEquals(true, test.isAfter(NOW1.minusSeconds(1)));
        assertEquals(false, test.isAfter(NOW1));
        assertEquals(false, test.isAfter(NOW2));
        assertEquals(false, test.isAfter(NOW2.plusSeconds(1)));
    }

    @Test
    public void test_isAfter_Instant_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(true, test.isAfter(NOW1.minusSeconds(2)));
        assertEquals(true, test.isAfter(NOW1.minusSeconds(1)));
        assertEquals(false, test.isAfter(NOW1));
        assertEquals(false, test.isAfter(NOW1.plusSeconds(1)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_isBefore_Instant() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(false, test.isBefore(NOW1.minusSeconds(1)));
        assertEquals(false, test.isBefore(NOW1));
        assertEquals(true, test.isBefore(NOW2));
        assertEquals(true, test.isBefore(NOW2.plusSeconds(1)));
    }

    @Test
    public void test_isBefore_Instant_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(false, test.isBefore(NOW1.minusSeconds(1)));
        assertEquals(false, test.isBefore(NOW1));
        assertEquals(true, test.isBefore(NOW1.plusSeconds(1)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_isAfter_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(true, test.isAfter(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(true, test.isAfter(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // partly before
        assertEquals(false, test.isAfter(Interval.of(NOW1.minusSeconds(1), NOW2)));
        assertEquals(false, test.isAfter(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))));
        // contained
        assertEquals(false, test.isAfter(Interval.of(NOW1, NOW2.minusSeconds(1))));
        assertEquals(false, test.isAfter(Interval.of(NOW1, NOW2)));
        assertEquals(false, test.isAfter(Interval.of(NOW1.plusSeconds(1), NOW2)));
        // partly after
        assertEquals(false, test.isAfter(Interval.of(NOW1, NOW2.plusSeconds(1))));
        assertEquals(false, test.isAfter(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))));
        // completely after
        assertEquals(false, test.isAfter(Interval.of(NOW2, NOW2.plusSeconds(1))));
        assertEquals(false, test.isAfter(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))));
    }

    @Test
    public void test_isAfter_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(true, test.isAfter(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(true, test.isAfter(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // equal
        assertEquals(false, test.isAfter(Interval.of(NOW1, NOW1)));
        // completely after
        assertEquals(false, test.isAfter(Interval.of(NOW1, NOW1.plusSeconds(1))));
        assertEquals(false, test.isAfter(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_isBefore_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(false, test.isBefore(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(false, test.isBefore(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // partly before
        assertEquals(false, test.isBefore(Interval.of(NOW1.minusSeconds(1), NOW2)));
        assertEquals(false, test.isBefore(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))));
        // contained
        assertEquals(false, test.isBefore(Interval.of(NOW1, NOW2.minusSeconds(1))));
        assertEquals(false, test.isBefore(Interval.of(NOW1, NOW2)));
        assertEquals(false, test.isBefore(Interval.of(NOW1.plusSeconds(1), NOW2)));
        // partly after
        assertEquals(false, test.isBefore(Interval.of(NOW1, NOW2.plusSeconds(1))));
        assertEquals(false, test.isBefore(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))));
        // completely after
        assertEquals(true, test.isBefore(Interval.of(NOW2, NOW2.plusSeconds(1))));
        assertEquals(true, test.isBefore(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))));
    }

    @Test
    public void test_isBefore_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(false, test.isBefore(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))));
        assertEquals(false, test.isBefore(Interval.of(NOW1.minusSeconds(1), NOW1)));
        // equal
        assertEquals(false, test.isBefore(Interval.of(NOW1, NOW1)));
        // completely after
        assertEquals(true, test.isBefore(Interval.of(NOW1, NOW1.plusSeconds(1))));
        assertEquals(true, test.isBefore(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toDuration() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(Duration.between(NOW1, NOW2), test.toDuration());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals() {
        Interval a = Interval.of(NOW1, NOW2);
        Interval a2 = Interval.of(NOW1, NOW2);
        Interval b = Interval.of(NOW1, NOW3);
        Interval c = Interval.of(NOW2, NOW2);
        assertEquals(true, a.equals(a));
        assertEquals(true, a.equals(a2));
        assertEquals(false, a.equals(b));
        assertEquals(false, a.equals(c));
        assertEquals(false, a.equals(null));
        assertEquals(false, a.equals((Object) ""));
        assertEquals(true, a.hashCode() == a2.hashCode());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(NOW1 + "/" + NOW2, test.toString());
    }

}
