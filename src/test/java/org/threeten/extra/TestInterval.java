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
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

/**
 * Test class.
 */
public class TestInterval {

    static Instant NOW1 = ZonedDateTime.of(2014, 12, 1, 1, 0, 0, 0, ZoneOffset.UTC).toInstant();
    static Instant NOW2 = NOW1.plusSeconds(60);
    static Instant NOW3 = NOW2.plusSeconds(60);
    static Instant NOW4 = NOW3.plusSeconds(60);
    static Instant NOW11 = NOW1.plusSeconds(11);
    static Instant NOW12 = NOW1.plusSeconds(12);
    static Instant NOW13 = NOW1.plusSeconds(13);
    static Instant NOW14 = NOW1.plusSeconds(14);
    static Instant NOW15 = NOW1.plusSeconds(15);
    static Instant NOW16 = NOW1.plusSeconds(16);

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
        assertThrows(NullPointerException.class, () -> Interval.of((Instant) null, NOW2));
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

    //-----------------------------------------------------------------------
    @Test
    public void test_of_Duration_Instant() {
        Interval test = Interval.of(Duration.ofSeconds(60), NOW2);
        assertEquals(NOW1, test.getStart());
        assertEquals(NOW2, test.getEnd());
    }

    @Test
    public void test_of_Duration_Instant_zero() {
        Interval test = Interval.of(Duration.ZERO, NOW1);
        assertEquals(NOW1, test.getStart());
        assertEquals(NOW1, test.getEnd());
    }

    @Test
    public void test_of_Duration_Instant_negative() {
        assertThrows(DateTimeException.class, () -> Interval.of(Duration.ofSeconds(-1), NOW2));
    }

    @Test
    public void test_of_Duration_Instant_nullInstant() {
        assertThrows(NullPointerException.class, () -> Interval.of(Duration.ZERO, null));
    }

    @Test
    public void test_of_Duration_Instant_nullDuration() {
        assertThrows(NullPointerException.class, () -> Interval.of((Duration) null, NOW1));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_startingAt_Instant_createsUnboundedEnd() {
        Interval interval = Interval.startingAt(NOW1);

        assertEquals(NOW1, interval.getStart());
        assertTrue(interval.isUnboundedEnd());
        assertFalse(interval.isEmpty());
    }

    @Test
    public void test_startingAt_InstantMAX_createsUnboundedEndEmpty() {
        Interval interval = Interval.startingAt(Instant.MAX);

        assertEquals(Instant.MAX, interval.getStart());
        assertTrue(interval.isUnboundedEnd());
        assertTrue(interval.isEmpty());
    }

    @Test
    public void test_startingAt_InstantMIN_isALL() {
        Interval interval = Interval.startingAt(Instant.MIN);

        assertEquals(Interval.ALL, interval);
    }

    @Test
    public void test_startingAt_null() {
        assertThrows(NullPointerException.class, () -> Interval.startingAt(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_endingAt_createsUnboundedStart() {
        Interval interval = Interval.endingAt(NOW1);

        assertEquals(NOW1, interval.getEnd());
        assertTrue(interval.isUnboundedStart());
        assertFalse(interval.isEmpty());
    }

    @Test
    public void test_sendingAt_InstantMIN_createsUnboundedStartEmpty() {
        Interval interval = Interval.endingAt(Instant.MIN);

        assertEquals(Instant.MIN, interval.getEnd());
        assertTrue(interval.isUnboundedStart());
        assertTrue(interval.isEmpty());
    }

    @Test
    public void test_endingAt_InstantMAX_isALL() {
        Interval interval = Interval.endingAt(Instant.MAX);

        assertEquals(Interval.ALL, interval);
    }

    @Test
    public void test_endingAt_null() {
        assertThrows(NullPointerException.class, () -> Interval.endingAt(null));
    }

    /* Lower and upper bound for Intervals */
    private static final Instant MIN_OFFSET_DATE_TIME = OffsetDateTime.MIN.plusDays(1L).toInstant();
    private static final Instant MAX_OFFSET_DATE_TIME = OffsetDateTime.MAX.minusDays(1L).toInstant();

    //-----------------------------------------------------------------------
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
    @MethodSource("data_parseValid")
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
    public void test_contains_min() {
        Interval test = Interval.of(Instant.MIN, NOW2);
        assertEquals(true, test.contains(Instant.MIN));
        assertEquals(true, test.contains(NOW1));
        assertEquals(false, test.contains(NOW2));
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
    @MethodSource("data_intersection")
    public void test_intersection(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertTrue(test1.isConnected(test2));
        assertEquals(expected, test1.intersection(test2));
    }

    @ParameterizedTest
    @MethodSource("data_intersection")
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
    @MethodSource("data_union")
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
    @MethodSource("data_union")
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
    @MethodSource("data_union")
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
    public static Object[][] data_starts() {
        return new Object[][] {
            // normal
            {Interval.of(NOW12, NOW14), NOW11, false, false, true, true},
            {Interval.of(NOW12, NOW14), NOW12, false, true, false, true},
            {Interval.of(NOW12, NOW14), NOW13, true, true, false, false},
            {Interval.of(NOW12, NOW14), NOW14, true, true, false, false},
            {Interval.of(NOW12, NOW14), NOW15, true, true, false, false},
            // empty interval
            {Interval.of(NOW12, NOW12), NOW11, false, false, true, true},
            {Interval.of(NOW12, NOW12), NOW12, false, true, false, true},
            {Interval.of(NOW12, NOW12), NOW13, true, true, false, false},
            // unbounded start
            {Interval.of(Instant.MIN, NOW12), Instant.MIN, false, true, false, true},
            {Interval.of(Instant.MIN, NOW12), NOW11, true, true, false, false},
            {Interval.of(Instant.MIN, NOW12), NOW12, true, true, false, false},
            {Interval.of(Instant.MIN, NOW12), NOW13, true, true, false, false},
            {Interval.of(Instant.MIN, NOW12), Instant.MAX, true, true, false, false},
            // unbounded end
            {Interval.of(NOW12, Instant.MAX), Instant.MIN, false, false, true, true},
            {Interval.of(NOW12, Instant.MAX), NOW11, false, false, true, true},
            {Interval.of(NOW12, Instant.MAX), NOW12, false, true, false, true},
            {Interval.of(NOW12, Instant.MAX), NOW13, true, true, false, false},
            {Interval.of(NOW12, Instant.MAX), Instant.MAX, true, true, false, false},
        };
    }

    @ParameterizedTest
    @MethodSource("data_starts")
    public void test_starts_Instant(
            Interval test,
            Instant instant,
            boolean expectedStartsBefore,
            boolean expectedStartsAtOrBefore,
            boolean expectedStartsAfter,
            boolean expectedStartsAtOrAfter) {

        assertEquals(expectedStartsBefore, test.startsBefore(instant));
        assertEquals(expectedStartsAtOrBefore, test.startsAtOrBefore(instant));
        assertEquals(expectedStartsAfter, test.startsAfter(instant));
        assertEquals(expectedStartsAtOrAfter, test.startsAtOrAfter(instant));
    }

    @Test
    public void test_starts_Instant_null() {
        Interval base = Interval.of(NOW12, NOW14);
        assertThrows(NullPointerException.class, () -> base.startsBefore((Instant) null));
        assertThrows(NullPointerException.class, () -> base.startsAtOrBefore((Instant) null));
        assertThrows(NullPointerException.class, () -> base.startsAfter((Instant) null));
        assertThrows(NullPointerException.class, () -> base.startsAtOrAfter((Instant) null));
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_ends() {
        return new Object[][] {
            // normal
            {Interval.of(NOW12, NOW14), NOW11, false, false, true, true},
            {Interval.of(NOW12, NOW14), NOW12, false, false, true, true},
            {Interval.of(NOW12, NOW14), NOW13, false, false, true, true},
            {Interval.of(NOW12, NOW14), NOW14, false, true, false, true},
            {Interval.of(NOW12, NOW14), NOW15, true, true, false, false},
            // empty interval
            {Interval.of(NOW12, NOW12), NOW11, false, false, true, true},
            {Interval.of(NOW12, NOW12), NOW12, false, true, false, true},
            {Interval.of(NOW12, NOW12), NOW13, true, true, false, false},
            // unbounded start
            {Interval.of(Instant.MIN, NOW12), Instant.MIN, false, false, true, true},
            {Interval.of(Instant.MIN, NOW12), NOW11, false, false, true, true},
            {Interval.of(Instant.MIN, NOW12), NOW12, false, true, false, true},
            {Interval.of(Instant.MIN, NOW12), NOW13, true, true, false, false},
            {Interval.of(Instant.MIN, NOW12), Instant.MAX, true, true, false, false},
            // unbounded end
            {Interval.of(NOW12, Instant.MAX), Instant.MIN, false, false, true, true},
            {Interval.of(NOW12, Instant.MAX), NOW11, false, false, true, true},
            {Interval.of(NOW12, Instant.MAX), NOW12, false, false, true, true},
            {Interval.of(NOW12, Instant.MAX), NOW13, false, false, true, true},
            {Interval.of(NOW12, Instant.MAX), Instant.MAX, false, false, true, true},
        };
    }

    @ParameterizedTest
    @MethodSource("data_ends")
    public void test_ends_Instant(
            Interval test,
            Instant instant,
            boolean expectedEndsBefore,
            boolean expectedEndsAtOrBefore,
            boolean expectedEndsAfter,
            boolean expectedEndsAtOrAfter) {

        assertEquals(expectedEndsBefore, test.endsBefore(instant));
        assertEquals(expectedEndsAtOrBefore, test.endsAtOrBefore(instant));
        assertEquals(expectedEndsAfter, test.endsAfter(instant));
        assertEquals(expectedEndsAtOrAfter, test.endsAtOrAfter(instant));
    }

    @Test
    public void test_ends_Instant_null() {
        Interval base = Interval.of(NOW12, NOW14);
        assertThrows(NullPointerException.class, () -> base.endsBefore((Instant) null));
        assertThrows(NullPointerException.class, () -> base.endsAtOrBefore((Instant) null));
        assertThrows(NullPointerException.class, () -> base.endsAfter((Instant) null));
        assertThrows(NullPointerException.class, () -> base.endsAtOrAfter((Instant) null));
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
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(Interval.of(NOW1, NOW2), Interval.of(NOW1, NOW2))
            .addEqualityGroup(Interval.of(NOW1, NOW3), Interval.of(NOW1, NOW3))
            .addEqualityGroup(Interval.of(NOW2, NOW2), Interval.of(NOW2, NOW2))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(NOW1 + "/" + NOW2, test.toString());
    }

}
