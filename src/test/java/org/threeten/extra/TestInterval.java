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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestInterval {

    Instant NOW1 = ZonedDateTime.of(2014, 12, 1, 1, 0, 0, 0, ZoneOffset.UTC).toInstant();
    Instant NOW2 = NOW1.plusSeconds(60);
    Instant NOW3 = NOW2.plusSeconds(60);

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Interval.class));
    }

    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        Interval orginal = Interval.of(Instant.EPOCH, NOW1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Interval ser = (Interval) in.readObject();
        assertEquals(ser, orginal);
    }

    //-----------------------------------------------------------------------
    public void test_of_Instant_Instant() {
        Interval test = Interval.of(NOW1, NOW2);
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW2);
    }

    public void test_of_Instant_Instant_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW1);
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

    //-----------------------------------------------------------------------
    public void test_parse_CharSequence() {
        Interval test = Interval.parse(NOW1 + "/" + NOW2);
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW2);
    }

    public void test_parse_CharSequence_DurationInstant() {
        Interval test = Interval.parse(Duration.ofHours(6) + "/" + NOW2);
        assertEquals(test.getStart(), NOW2.minus(6, HOURS));
        assertEquals(test.getEnd(), NOW2);
    }

    public void test_parse_CharSequence_InstantDuration() {
        Interval test = Interval.parse(NOW1 + "/" + Duration.ofHours(6));
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW1.plus(6, HOURS));
    }

    public void test_parse_CharSequence_empty() {
        Interval test = Interval.parse(NOW1 + "/" + NOW1);
        assertEquals(test.getStart(), NOW1);
        assertEquals(test.getEnd(), NOW1);
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

    public void test_contains_Instant_empty() {
        Interval test = Interval.of(NOW1, NOW1);
        assertEquals(test.contains(NOW1.minusSeconds(1)), false);
        assertEquals(test.contains(NOW1), false);
        assertEquals(test.contains(NOW1.plusSeconds(1)), false);
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
