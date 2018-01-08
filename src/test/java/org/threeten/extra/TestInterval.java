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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestInterval {

    Instant NOW1 = ZonedDateTime.of(2014, 12, 1, 1, 0, 0, 0, ZoneOffset.UTC).toInstant();
    Instant NOW2 = NOW1.plusSeconds(60);
    Instant NOW3 = NOW2.plusSeconds(60);
    Instant NOW4 = NOW3.plusSeconds(60);

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Interval.class));
    }

    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        Interval test = Interval.of(Instant.EPOCH, NOW1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(ois.readObject(), test);
        }
    }

    //-----------------------------------------------------------------------
    public void test_ALL() {
        Interval test = Interval.ALL;
        assertEquals(test.getStart(), Instant.MIN);
        assertEquals(test.getEnd(), Instant.MAX);
        assertEquals(test.isEmpty(), false);
        assertEquals(test.isUnboundedStart(), true);
        assertEquals(test.isUnboundedEnd(), true);
    }

    //-----------------------------------------------------------------------
    public void test_of_Instant_Instant() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW2);
        assertEquals(test.isEmpty(), false);
        assertEquals(test.isUnboundedStart(), false);
        assertEquals(test.isUnboundedEnd(), false);
    }

    public void test_of_Instant_Instant_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW1);
        assertEquals(test.isEmpty(), true);
        assertEquals(test.isUnboundedStart(), false);
        assertEquals(test.isUnboundedEnd(), false);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_Instant_Instant_badOrder() {
        Interval.of(NOW2, NOW1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_of_Instant_Instant_nullStart() {
        Interval.of(null, NOW2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_of_Instant_Instant_nullEnd() {
        Interval.of(NOW1, (Instant) null);
    }

    //-----------------------------------------------------------------------
    public void test_of_Instant_Duration() {
        Interval test = Interval.of(NOW1, Duration.ofSeconds(60));
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW2);
    }

    public void test_of_Instant_Duration_zero() {
        Interval test = Interval.of(NOW1, Duration.ZERO);
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_Instant_Duration_negative() {
        Interval.of(NOW2, Duration.ofSeconds(-1));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_of_Instant_Duration_nullInstant() {
        Interval.of(null, Duration.ZERO);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_of_Instant_Duration_nullDuration() {
        Interval.of(NOW1, (Duration) null);
    }

    /* Lower and upper bound for Intervals */
    private static final Instant MIN_OFFSET_DATE_TIME = OffsetDateTime.MIN.plusDays(1L).toInstant();
    private static final Instant MAX_OFFSET_DATE_TIME = OffsetDateTime.MAX.minusDays(1L).toInstant();

    //-----------------------------------------------------------------------
    @DataProvider(name = "parseValid")
    Object[][] data_parseValid() {
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
    
    @Test(dataProvider = "parseValid")
    public void test_parse_CharSequence(String input, Instant start, Instant end) {
        Interval test = Interval.parse(input);
        assertEquals(test.getStart(), start);
        assertEquals(test.getEnd(), end);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_parse_CharSequence_badOrder() {
        Interval.parse(NOW2 + "/" + NOW1);
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequence_badFormat() {
        Interval.parse(NOW2 + "-" + NOW1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        Interval.parse(null);
    }

    //-----------------------------------------------------------------------
    public void test_withStart() {
        Interval base = Interval.of(NOW1, NOW3);
        Interval test = base.withStart(NOW2);
        assertEquals(test.getStart(), NOW2);
        assertEquals(test.getEnd(), NOW3);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withStart_badOrder() {
        Interval base = Interval.of(NOW1, NOW2);
        base.withStart(NOW3);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withStart_null() {
        Interval base = Interval.of(NOW1, NOW2);
        base.withStart(null);
    }

    //-----------------------------------------------------------------------
    public void test_withEnd() {
        Interval base = Interval.of(NOW1, NOW3);
        Interval test = base.withEnd(NOW2);
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW2);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withEnd_badOrder() {
        Interval base = Interval.of(NOW2, NOW3);
        base.withEnd(NOW1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withEnd_null() {
        Interval base = Interval.of(NOW1, NOW2);
        base.withEnd(null);
    }

    //-----------------------------------------------------------------------
    public void test_contains_Instant() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(test.contains(NOW1.minusSeconds(1)), false);
        assertEquals(test.contains(NOW1), true);
        assertEquals(test.contains(NOW1.plusSeconds(1)), true);
        assertEquals(test.contains(NOW2.minusSeconds(1)), true);
        assertEquals(test.contains(NOW2), false);
    }

    public void test_contains_Instant_baseEmpty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(test.contains(NOW1.minusSeconds(1)), false);
        assertEquals(test.contains(NOW1), false);
        assertEquals(test.contains(NOW1.plusSeconds(1)), false);
    }

    public void test_contains_max() {
        Interval test = Interval.of(NOW2, Instant.MAX);
        assertEquals(test.contains(Instant.MIN), false);
        assertEquals(test.contains(NOW1), false);
        assertEquals(test.contains(NOW2), true);
        assertEquals(test.contains(NOW3), true);
        assertEquals(test.contains(Instant.MAX), true);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_contains_Instant_null() {
        Interval base = Interval.of(NOW1, NOW2);
        base.contains((Instant) null);
    }

    //-----------------------------------------------------------------------
    public void test_encloses_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(test.encloses(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        assertEquals(test.encloses(Interval.of(NOW1.minusSeconds(1), NOW1)), false);
        // partly before
        assertEquals(test.encloses(Interval.of(NOW1.minusSeconds(1), NOW2)), false);
        assertEquals(test.encloses(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))), false);
        // contained
        assertEquals(test.encloses(Interval.of(NOW1, NOW2.minusSeconds(1))), true);
        assertEquals(test.encloses(Interval.of(NOW1, NOW2)), true);
        assertEquals(test.encloses(Interval.of(NOW1.plusSeconds(1), NOW2)), true);
        // partly after
        assertEquals(test.encloses(Interval.of(NOW1, NOW2.plusSeconds(1))), false);
        assertEquals(test.encloses(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))), false);
        // completely after
        assertEquals(test.encloses(Interval.of(NOW2, NOW2.plusSeconds(1))), false);
        assertEquals(test.encloses(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))), false);
    }

    public void test_encloses_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(test.encloses(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        // partly before
        assertEquals(test.encloses(Interval.of(NOW1.minusSeconds(1), NOW1)), false);
        // equal
        assertEquals(test.encloses(Interval.of(NOW1, NOW1)), true);
        // completely after
        assertEquals(test.encloses(Interval.of(NOW1, NOW1.plusSeconds(1))), false);
        assertEquals(test.encloses(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_encloses_Interval_null() {
        Interval base = Interval.of(NOW1, NOW2);
        base.encloses((Interval) null);
    }

    //-----------------------------------------------------------------------
    public void test_abuts_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(test.abuts(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        assertEquals(test.abuts(Interval.of(NOW1.minusSeconds(1), NOW1)), true);
        // partly before
        assertEquals(test.abuts(Interval.of(NOW1.minusSeconds(1), NOW2)), false);
        assertEquals(test.abuts(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))), false);
        // contained
        assertEquals(test.abuts(Interval.of(NOW1, NOW2.minusSeconds(1))), false);
        assertEquals(test.abuts(Interval.of(NOW1, NOW2)), false);
        assertEquals(test.abuts(Interval.of(NOW1.plusSeconds(1), NOW2)), false);
        // partly after
        assertEquals(test.abuts(Interval.of(NOW1, NOW2.plusSeconds(1))), false);
        assertEquals(test.abuts(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))), false);
        // completely after
        assertEquals(test.abuts(Interval.of(NOW2, NOW2.plusSeconds(1))), true);
        assertEquals(test.abuts(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))), false);
    }

    public void test_abuts_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(test.abuts(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        assertEquals(test.abuts(Interval.of(NOW1.minusSeconds(1), NOW1)), true);
        // equal
        assertEquals(test.abuts(Interval.of(NOW1, NOW1)), false);
        // completely after
        assertEquals(test.abuts(Interval.of(NOW1, NOW1.plusSeconds(1))), true);
        assertEquals(test.abuts(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_abuts_Interval_null() {
        Interval base = Interval.of(NOW1, NOW2);
        base.abuts((Interval) null);
    }

    //-----------------------------------------------------------------------
    public void test_isConnected_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(test.isConnected(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        assertEquals(test.isConnected(Interval.of(NOW1.minusSeconds(1), NOW1)), true);
        // partly before
        assertEquals(test.isConnected(Interval.of(NOW1.minusSeconds(1), NOW2)), true);
        assertEquals(test.isConnected(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))), true);
        // contained
        assertEquals(test.isConnected(Interval.of(NOW1, NOW2.minusSeconds(1))), true);
        assertEquals(test.isConnected(Interval.of(NOW1, NOW2)), true);
        assertEquals(test.isConnected(Interval.of(NOW1.plusSeconds(1), NOW2)), true);
        // partly after
        assertEquals(test.isConnected(Interval.of(NOW1, NOW2.plusSeconds(1))), true);
        assertEquals(test.isConnected(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))), true);
        // completely after
        assertEquals(test.isConnected(Interval.of(NOW2, NOW2.plusSeconds(1))), true);
        assertEquals(test.isConnected(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))), false);
    }

    public void test_isConnected_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(test.isConnected(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        assertEquals(test.isConnected(Interval.of(NOW1.minusSeconds(1), NOW1)), true);
        // equal
        assertEquals(test.isConnected(Interval.of(NOW1, NOW1)), true);
        // completely after
        assertEquals(test.isConnected(Interval.of(NOW1, NOW1.plusSeconds(1))), true);
        assertEquals(test.isConnected(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_isConnected_Interval_null() {
        Interval base = Interval.of(NOW1, NOW2);
        base.isConnected((Interval) null);
    }

    //-----------------------------------------------------------------------
    public void test_overlaps_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(test.overlaps(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        assertEquals(test.overlaps(Interval.of(NOW1.minusSeconds(1), NOW1)), false);
        // partly before
        assertEquals(test.overlaps(Interval.of(NOW1.minusSeconds(1), NOW2)), true);
        assertEquals(test.overlaps(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))), true);
        // contained
        assertEquals(test.overlaps(Interval.of(NOW1, NOW2.minusSeconds(1))), true);
        assertEquals(test.overlaps(Interval.of(NOW1, NOW2)), true);
        assertEquals(test.overlaps(Interval.of(NOW1.plusSeconds(1), NOW2)), true);
        // partly after
        assertEquals(test.overlaps(Interval.of(NOW1, NOW2.plusSeconds(1))), true);
        assertEquals(test.overlaps(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))), true);
        // completely after
        assertEquals(test.overlaps(Interval.of(NOW2, NOW2.plusSeconds(1))), false);
        assertEquals(test.overlaps(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))), false);
    }

    public void test_overlaps_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(test.overlaps(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        assertEquals(test.overlaps(Interval.of(NOW1.minusSeconds(1), NOW1)), false);
        // equal
        assertEquals(test.overlaps(Interval.of(NOW1, NOW1)), true);
        // completely after
        assertEquals(test.overlaps(Interval.of(NOW1, NOW1.plusSeconds(1))), false);
        assertEquals(test.overlaps(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_overlaps_Interval_null() {
        Interval base = Interval.of(NOW1, NOW2);
        base.overlaps((Interval) null);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "intersection")
    Object[][] data_intersection() {
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

    @Test(dataProvider = "intersection")
    public void test_intersection(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertTrue(test1.isConnected(test2));
        assertEquals(test1.intersection(test2), expected);
    }

    @Test(dataProvider = "intersection")
    public void test_intersection_reverse(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertTrue(test2.isConnected(test1));
        assertEquals(test2.intersection(test1), expected);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_intersectionBad() {
        Interval test1 = Interval.of(NOW1, NOW2);
        Interval test2 = Interval.of(NOW3, NOW4);
        assertEquals(test1.isConnected(test2), false);
        test1.intersection(test2);
    }

    public void test_intersection_same() {
        Interval test = Interval.of(NOW2, NOW4);
        assertEquals(test.intersection(test), test);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "union")
    Object[][] data_union() {
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

    @Test(dataProvider = "union")
    public void test_unionAndSpan(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertTrue(test1.isConnected(test2));
        assertEquals(test1.union(test2), expected);
        assertEquals(test1.span(test2), expected);
    }

    @Test(dataProvider = "union")
    public void test_unionAndSpan_reverse(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertTrue(test2.isConnected(test1));
        assertEquals(test2.union(test1), expected);
        assertEquals(test2.span(test1), expected);
    }

    @Test(dataProvider = "union")
    public void test_span_enclosesInputs(
            Instant start1, Instant end1, Instant start2, Instant end2, Instant expStart, Instant expEnd) {

        Interval test1 = Interval.of(start1, end1);
        Interval test2 = Interval.of(start2, end2);
        Interval expected = Interval.of(expStart, expEnd);
        assertEquals(expected.encloses(test1), true);
        assertEquals(expected.encloses(test2), true);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_union_disconnected() {
        Interval test1 = Interval.of(NOW1, NOW2);
        Interval test2 = Interval.of(NOW3, NOW4);
        assertFalse(test1.isConnected(test2));
        test1.union(test2);
    }

    public void test_span_disconnected() {
        Interval test1 = Interval.of(NOW1, NOW2);
        Interval test2 = Interval.of(NOW3, NOW4);
        assertFalse(test1.isConnected(test2));
        assertEquals(test1.span(test2), Interval.of(NOW1, NOW4));
    }

    public void test_unionAndSpan_same() {
        Interval test = Interval.of(NOW2, NOW4);
        assertEquals(test.union(test), test);
        assertEquals(test.span(test), test);
    }

    //-----------------------------------------------------------------------
    public void test_isAfter_Instant() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(test.isAfter(NOW1.minusSeconds(2)), true);
        assertEquals(test.isAfter(NOW1.minusSeconds(1)), true);
        assertEquals(test.isAfter(NOW1), false);
        assertEquals(test.isAfter(NOW2), false);
        assertEquals(test.isAfter(NOW2.plusSeconds(1)), false);
    }

    public void test_isAfter_Instant_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(test.isAfter(NOW1.minusSeconds(2)), true);
        assertEquals(test.isAfter(NOW1.minusSeconds(1)), true);
        assertEquals(test.isAfter(NOW1), false);
        assertEquals(test.isAfter(NOW1.plusSeconds(1)), false);
    }

    //-----------------------------------------------------------------------
    public void test_isBefore_Instant() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(test.isBefore(NOW1.minusSeconds(1)), false);
        assertEquals(test.isBefore(NOW1), false);
        assertEquals(test.isBefore(NOW2), true);
        assertEquals(test.isBefore(NOW2.plusSeconds(1)), true);
    }

    public void test_isBefore_Instant_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(test.isBefore(NOW1.minusSeconds(1)), false);
        assertEquals(test.isBefore(NOW1), false);
        assertEquals(test.isBefore(NOW1.plusSeconds(1)), true);
    }

    //-----------------------------------------------------------------------
    public void test_isAfter_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(test.isAfter(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), true);
        assertEquals(test.isAfter(Interval.of(NOW1.minusSeconds(1), NOW1)), true);
        // partly before
        assertEquals(test.isAfter(Interval.of(NOW1.minusSeconds(1), NOW2)), false);
        assertEquals(test.isAfter(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))), false);
        // contained
        assertEquals(test.isAfter(Interval.of(NOW1, NOW2.minusSeconds(1))), false);
        assertEquals(test.isAfter(Interval.of(NOW1, NOW2)), false);
        assertEquals(test.isAfter(Interval.of(NOW1.plusSeconds(1), NOW2)), false);
        // partly after
        assertEquals(test.isAfter(Interval.of(NOW1, NOW2.plusSeconds(1))), false);
        assertEquals(test.isAfter(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))), false);
        // completely after
        assertEquals(test.isAfter(Interval.of(NOW2, NOW2.plusSeconds(1))), false);
        assertEquals(test.isAfter(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))), false);
    }

    public void test_isAfter_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(test.isAfter(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), true);
        assertEquals(test.isAfter(Interval.of(NOW1.minusSeconds(1), NOW1)), true);
        // equal
        assertEquals(test.isAfter(Interval.of(NOW1, NOW1)), false);
        // completely after
        assertEquals(test.isAfter(Interval.of(NOW1, NOW1.plusSeconds(1))), false);
        assertEquals(test.isAfter(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))), false);
    }

    //-----------------------------------------------------------------------
    public void test_isBefore_Interval() {
        Interval test = Interval.of(NOW1, NOW2);
        // completely before
        assertEquals(test.isBefore(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        assertEquals(test.isBefore(Interval.of(NOW1.minusSeconds(1), NOW1)), false);
        // partly before
        assertEquals(test.isBefore(Interval.of(NOW1.minusSeconds(1), NOW2)), false);
        assertEquals(test.isBefore(Interval.of(NOW1.minusSeconds(1), NOW2.minusSeconds(1))), false);
        // contained
        assertEquals(test.isBefore(Interval.of(NOW1, NOW2.minusSeconds(1))), false);
        assertEquals(test.isBefore(Interval.of(NOW1, NOW2)), false);
        assertEquals(test.isBefore(Interval.of(NOW1.plusSeconds(1), NOW2)), false);
        // partly after
        assertEquals(test.isBefore(Interval.of(NOW1, NOW2.plusSeconds(1))), false);
        assertEquals(test.isBefore(Interval.of(NOW1.plusSeconds(1), NOW2.plusSeconds(1))), false);
        // completely after
        assertEquals(test.isBefore(Interval.of(NOW2, NOW2.plusSeconds(1))), true);
        assertEquals(test.isBefore(Interval.of(NOW2.plusSeconds(1), NOW2.plusSeconds(2))), true);
    }

    public void test_isBefore_Interval_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        // completely before
        assertEquals(test.isBefore(Interval.of(NOW1.minusSeconds(2), NOW1.minusSeconds(1))), false);
        assertEquals(test.isBefore(Interval.of(NOW1.minusSeconds(1), NOW1)), false);
        // equal
        assertEquals(test.isBefore(Interval.of(NOW1, NOW1)), false);
        // completely after
        assertEquals(test.isBefore(Interval.of(NOW1, NOW1.plusSeconds(1))), true);
        assertEquals(test.isBefore(Interval.of(NOW1.plusSeconds(1), NOW1.plusSeconds(2))), true);
    }

    //-----------------------------------------------------------------------
    public void test_toDuration() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(test.toDuration(), Duration.between(NOW1, NOW2));
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Interval a = Interval.of(NOW1, NOW2);
        Interval a2 = Interval.of(NOW1, NOW2);
        Interval b = Interval.of(NOW1, NOW3);
        Interval c = Interval.of(NOW2, NOW2);
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(a2), true);
        assertEquals(a.equals(b), false);
        assertEquals(a.equals(c), false);
        assertEquals(a.equals(null), false);
        assertEquals(a.equals(""), false);
        assertEquals(a.hashCode() == a2.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(test.toString(), NOW1 + "/" + NOW2);
    }

}
