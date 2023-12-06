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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

/**
 * Test class.
 */
public class TestMutableClock {

    @Test
    public void test_of() {
        assertEquals(
                Instant.EPOCH,
                MutableClock.of(Instant.EPOCH, ZoneOffset.UTC).instant());
        assertEquals(
                Instant.MIN,
                MutableClock.of(Instant.MIN, ZoneOffset.UTC).instant());
        assertEquals(
                Instant.MAX,
                MutableClock.of(Instant.MAX, ZoneOffset.UTC).instant());
        assertEquals(
                ZoneOffset.UTC,
                MutableClock.of(Instant.EPOCH, ZoneOffset.UTC).getZone());
        assertEquals(
                ZoneOffset.MIN,
                MutableClock.of(Instant.EPOCH, ZoneOffset.MIN).getZone());
        assertEquals(
                ZoneOffset.MAX,
                MutableClock.of(Instant.EPOCH, ZoneOffset.MAX).getZone());
    }

    @Test
    public void test_of_nullInstant() {
        assertThrows(NullPointerException.class, () -> MutableClock.of(null, ZoneOffset.UTC));
    }

    @Test
    public void test_of_nullZone() {
        assertThrows(NullPointerException.class, () -> MutableClock.of(Instant.EPOCH, null));
    }

    @Test
    public void test_epochUTC() {
        assertEquals(Instant.EPOCH, MutableClock.epochUTC().instant());
        assertEquals(ZoneOffset.UTC, MutableClock.epochUTC().getZone());
    }

    @Test
    public void test_setInstant() {
        MutableClock clock = MutableClock.epochUTC();
        assertEquals(Instant.EPOCH, clock.instant());
        clock.setInstant(Instant.MIN);
        assertEquals(Instant.MIN, clock.instant());
        clock.setInstant(Instant.MAX);
        assertEquals(Instant.MAX, clock.instant());
        clock.setInstant(Instant.EPOCH.plusSeconds(10));
        assertEquals(Instant.EPOCH.plusSeconds(10), clock.instant());
    }

    @Test
    public void test_setInstant_null() {
        assertThrows(NullPointerException.class, () -> MutableClock.epochUTC().setInstant(null));
    }

    @Test
    public void test_add_amountOnly() {
        MutableClock clock = MutableClock.epochUTC();
        clock.add(Duration.ofNanos(3));
        clock.add(Period.ofMonths(2));
        clock.add(Duration.ofSeconds(-5));
        clock.add(Period.ofWeeks(-7));
        clock.add(Duration.ZERO);
        clock.add(Period.ZERO);
        assertEquals(
                ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
                        .plusNanos(3)
                        .plusMonths(2)
                        .minusSeconds(5)
                        .minusWeeks(7)
                        .toInstant(),
                clock.instant());
    }

    @Test
    public void test_add_amountOnly_null() {
        assertThrows(NullPointerException.class, () -> MutableClock.epochUTC().add(null));
    }

    @Test
    public void test_add_amountAndUnit() {
        MutableClock clock = MutableClock.epochUTC();
        clock.add(3, ChronoUnit.NANOS);
        clock.add(2, ChronoUnit.MONTHS);
        clock.add(-5, ChronoUnit.SECONDS);
        clock.add(-7, ChronoUnit.WEEKS);
        clock.add(0, ChronoUnit.MILLIS);
        clock.add(0, ChronoUnit.YEARS);
        assertEquals(
                ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
                        .plusNanos(3)
                        .plusMonths(2)
                        .minusSeconds(5)
                        .minusWeeks(7)
                        .toInstant(),
                clock.instant());
    }

    @Test
    public void test_add_amountAndUnit_nullUnit() {
        assertThrows(NullPointerException.class, () -> MutableClock.epochUTC().add(0, null));
    }

    @Test
    public void test_set_adjuster() {
        MutableClock clock = MutableClock.epochUTC();
        clock.set(LocalDate.of(0, 1, 2));
        clock.set(LocalTime.of(3, 4, 5));
        clock.set(TemporalAdjusters.firstDayOfNextMonth());
        clock.set(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
        assertEquals(
                LocalDateTime.of(0, 1, 2, 3, 4, 5)
                        .with(TemporalAdjusters.firstDayOfNextMonth())
                        .with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY))
                        .atZone(ZoneOffset.UTC)
                        .toInstant(),
                clock.instant());
        clock.set(Instant.EPOCH);
        assertEquals(Instant.EPOCH, clock.instant());
    }

    @Test
    public void test_set_adjuster_null() {
        assertThrows(NullPointerException.class, () -> MutableClock.epochUTC().set(null));
    }

    @Test
    public void test_set_fieldAndValue() {
        MutableClock clock = MutableClock.epochUTC();
        clock.set(ChronoField.YEAR, 0);
        clock.set(ChronoField.MONTH_OF_YEAR, 1);
        clock.set(ChronoField.DAY_OF_MONTH, 2);
        clock.set(ChronoField.HOUR_OF_DAY, 3);
        clock.set(ChronoField.MINUTE_OF_HOUR, 4);
        clock.set(ChronoField.SECOND_OF_MINUTE, 5);
        assertEquals(
                LocalDateTime.of(0, 1, 2, 3, 4, 5)
                        .atZone(ZoneOffset.UTC)
                        .toInstant(),
                clock.instant());
    }

    @Test
    public void test_set_fieldAndValue_nullField() {
        assertThrows(NullPointerException.class, () -> MutableClock.epochUTC().set(null, 0));
    }

    @Test
    public void test_getZone() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        MutableClock ofOtherZone = MutableClock.of(Instant.EPOCH, ZoneOffset.MAX);
        assertEquals(ZoneOffset.UTC, clock.getZone());
        assertEquals(ZoneOffset.MIN, withOtherZone.getZone());
        assertEquals(ZoneOffset.MAX, ofOtherZone.getZone());
    }

    @Test
    public void test_withZone() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        MutableClock withSameZone = withOtherZone.withZone(ZoneOffset.UTC);
        clock.setInstant(Instant.MIN);
        assertEquals(Instant.MIN, withOtherZone.instant());
        assertEquals(Instant.MIN, withSameZone.instant());
        assertEquals(ZoneOffset.MIN, withOtherZone.getZone());
        assertEquals(ZoneOffset.UTC, withSameZone.getZone());
        assertNotEquals(clock, withOtherZone);
        assertEquals(clock, withSameZone);
    }

    @Test
    public void test_withZone_null() {
        assertThrows(NullPointerException.class, () -> MutableClock.epochUTC().withZone(null));
    }

    @Test
    public void test_instant() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        assertEquals(Instant.EPOCH, clock.instant());
        clock.add(Duration.ofSeconds(5));
        assertEquals(Instant.EPOCH.plusSeconds(5), clock.instant());
        clock.setInstant(Instant.MIN);
        assertEquals(Instant.MIN, clock.instant());
        assertEquals(Instant.MIN, withOtherZone.instant());
    }

    @Test
    public void test_equals() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        MutableClock withSameZone = withOtherZone.withZone(ZoneOffset.UTC);
        MutableClock independent = MutableClock.epochUTC();
        new EqualsTester()
            .addEqualityGroup(clock, clock, withSameZone)
            .addEqualityGroup(withOtherZone)
            .addEqualityGroup(independent)
            .testEquals();
    }

    @Test
    public void test_hashCode_isConstant() {
        MutableClock clock = MutableClock.epochUTC();
        int hash = clock.hashCode();
        clock.add(Period.ofMonths(1));
        assertEquals(hash, clock.hashCode());
        clock.add(1, ChronoUnit.DAYS);
        assertEquals(hash, clock.hashCode());
        clock.set(Year.of(2000));
        assertEquals(hash, clock.hashCode());
        clock.set(ChronoField.INSTANT_SECONDS, -1);
        assertEquals(hash, clock.hashCode());
    }

    @Test
    public void test_hashCode_sameWhenSharedUpdates() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        MutableClock withSameZone = withOtherZone.withZone(ZoneOffset.UTC);
        assertEquals(clock.hashCode(), withSameZone.hashCode());
    }

    @Test
    public void test_toString() {
        MutableClock clock = MutableClock.epochUTC();
        assertEquals(
                "MutableClock[1970-01-01T00:00:00Z,Z]",
                clock.toString());
        clock.add(Period.ofYears(30));
        assertEquals(
                "MutableClock[2000-01-01T00:00:00Z,Z]",
                clock.toString());
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        assertEquals(
                "MutableClock[2000-01-01T00:00:00Z,-18:00]",
                withOtherZone.toString());
    }

    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(MutableClock.class));
    }

    @Test
    public void test_serialization() throws Exception {
        MutableClock test = MutableClock.epochUTC();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            MutableClock ser = (MutableClock) ois.readObject();
            assertEquals(test.instant(), ser.instant());
            assertEquals(test.getZone(), ser.getZone());
            // no shared updates
            assertNotEquals(ser, test);
            test.add(Duration.ofSeconds(1));
            assertNotEquals(ser.instant(), test.instant());
        }
    }

    @Test
    public void test_updatesAreAtomic() throws Exception {
        MutableClock clock = MutableClock.epochUTC();
        Duration increment = Duration.ofSeconds(1);
        Callable<Void> applyOneUpdate = () -> {
            clock.add(increment);
            return null;
        };
        int updateCount = 10000;
        List<Callable<Void>> tasks = Collections.nCopies(updateCount, applyOneUpdate);
        int threads = Runtime.getRuntime().availableProcessors() * 4;
        ExecutorService service = Executors.newFixedThreadPool(threads);
        try {
            service.invokeAll(tasks);
            service.shutdown();
            service.awaitTermination(1, TimeUnit.MINUTES);
        } finally {
            if (!service.isTerminated()) {
                service.shutdownNow();
            }
        }
        assertEquals(
                Instant.EPOCH.plus(increment.multipliedBy(updateCount)),
                clock.instant());
    }
}
