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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

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

import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestMutableClock {

    public void test_of() {
        assertEquals(
                MutableClock.of(Instant.EPOCH, ZoneOffset.UTC).instant(),
                Instant.EPOCH);
        assertEquals(
                MutableClock.of(Instant.MIN, ZoneOffset.UTC).instant(),
                Instant.MIN);
        assertEquals(
                MutableClock.of(Instant.MAX, ZoneOffset.UTC).instant(),
                Instant.MAX);
        assertEquals(
                MutableClock.of(Instant.EPOCH, ZoneOffset.UTC).getZone(),
                ZoneOffset.UTC);
        assertEquals(
                MutableClock.of(Instant.EPOCH, ZoneOffset.MIN).getZone(),
                ZoneOffset.MIN);
        assertEquals(
                MutableClock.of(Instant.EPOCH, ZoneOffset.MAX).getZone(),
                ZoneOffset.MAX);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_of_nullInstant() {
        MutableClock.of(null, ZoneOffset.UTC);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_of_nullZone() {
        MutableClock.of(Instant.EPOCH, null);
    }

    public void test_epochUTC() {
        assertEquals(MutableClock.epochUTC().instant(), Instant.EPOCH);
        assertEquals(MutableClock.epochUTC().getZone(), ZoneOffset.UTC);
    }

    public void test_setInstant() {
        MutableClock clock = MutableClock.epochUTC();
        assertEquals(clock.instant(), Instant.EPOCH);
        clock.setInstant(Instant.MIN);
        assertEquals(clock.instant(), Instant.MIN);
        clock.setInstant(Instant.MAX);
        assertEquals(clock.instant(), Instant.MAX);
        clock.setInstant(Instant.EPOCH.plusSeconds(10));
        assertEquals(clock.instant(), Instant.EPOCH.plusSeconds(10));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_setInstant_null() {
        MutableClock.epochUTC().setInstant(null);
    }

    public void test_add_amountOnly() {
        MutableClock clock = MutableClock.epochUTC();
        clock.add(Duration.ofNanos(3));
        clock.add(Period.ofMonths(2));
        clock.add(Duration.ofSeconds(-5));
        clock.add(Period.ofWeeks(-7));
        clock.add(Duration.ZERO);
        clock.add(Period.ZERO);
        assertEquals(
                clock.instant(),
                ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
                        .plusNanos(3)
                        .plusMonths(2)
                        .minusSeconds(5)
                        .minusWeeks(7)
                        .toInstant());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_add_amountOnly_null() {
        MutableClock.epochUTC().add(null);
    }

    public void test_add_amountAndUnit() {
        MutableClock clock = MutableClock.epochUTC();
        clock.add(3, ChronoUnit.NANOS);
        clock.add(2, ChronoUnit.MONTHS);
        clock.add(-5, ChronoUnit.SECONDS);
        clock.add(-7, ChronoUnit.WEEKS);
        clock.add(0, ChronoUnit.MILLIS);
        clock.add(0, ChronoUnit.YEARS);
        assertEquals(
                clock.instant(),
                ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
                        .plusNanos(3)
                        .plusMonths(2)
                        .minusSeconds(5)
                        .minusWeeks(7)
                        .toInstant());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_add_amountAndUnit_nullUnit() {
        MutableClock.epochUTC().add(0, null);
    }

    public void test_set_adjuster() {
        MutableClock clock = MutableClock.epochUTC();
        clock.set(LocalDate.of(0, 1, 2));
        clock.set(LocalTime.of(3, 4, 5));
        clock.set(TemporalAdjusters.firstDayOfNextMonth());
        clock.set(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
        assertEquals(
                clock.instant(),
                LocalDateTime.of(0, 1, 2, 3, 4, 5)
                        .with(TemporalAdjusters.firstDayOfNextMonth())
                        .with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY))
                        .atZone(ZoneOffset.UTC)
                        .toInstant());
        clock.set(Instant.EPOCH);
        assertEquals(clock.instant(), Instant.EPOCH);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_set_adjuster_null() {
        MutableClock.epochUTC().set(null);
    }

    public void test_set_fieldAndValue() {
        MutableClock clock = MutableClock.epochUTC();
        clock.set(ChronoField.YEAR, 0);
        clock.set(ChronoField.MONTH_OF_YEAR, 1);
        clock.set(ChronoField.DAY_OF_MONTH, 2);
        clock.set(ChronoField.HOUR_OF_DAY, 3);
        clock.set(ChronoField.MINUTE_OF_HOUR, 4);
        clock.set(ChronoField.SECOND_OF_MINUTE, 5);
        assertEquals(
                clock.instant(),
                LocalDateTime.of(0, 1, 2, 3, 4, 5)
                        .atZone(ZoneOffset.UTC)
                        .toInstant());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_set_fieldAndValue_nullField() {
        MutableClock.epochUTC().set(null, 0);
    }

    public void test_getZone() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        MutableClock ofOtherZone = MutableClock.of(Instant.EPOCH, ZoneOffset.MAX);
        assertEquals(clock.getZone(), ZoneOffset.UTC);
        assertEquals(withOtherZone.getZone(), ZoneOffset.MIN);
        assertEquals(ofOtherZone.getZone(), ZoneOffset.MAX);
    }

    public void test_withZone() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        MutableClock withSameZone = withOtherZone.withZone(ZoneOffset.UTC);
        clock.setInstant(Instant.MIN);
        assertEquals(withOtherZone.instant(), Instant.MIN);
        assertEquals(withSameZone.instant(), Instant.MIN);
        assertEquals(withOtherZone.getZone(), ZoneOffset.MIN);
        assertEquals(withSameZone.getZone(), ZoneOffset.UTC);
        assertNotEquals(withOtherZone, clock);
        assertEquals(withSameZone, clock);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withZone_null() {
        MutableClock.epochUTC().withZone(null);
    }

    public void test_instant() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        assertEquals(clock.instant(), Instant.EPOCH);
        clock.add(Duration.ofSeconds(5));
        assertEquals(clock.instant(), Instant.EPOCH.plusSeconds(5));
        clock.setInstant(Instant.MIN);
        assertEquals(clock.instant(), Instant.MIN);
        assertEquals(withOtherZone.instant(), Instant.MIN);
    }

    public void test_equals() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        MutableClock withSameZone = withOtherZone.withZone(ZoneOffset.UTC);
        MutableClock independent = MutableClock.epochUTC();
        assertEquals(clock, clock);
        assertNotEquals(null, clock);
        assertNotEquals("", clock);
        assertNotEquals(withOtherZone, clock);
        assertEquals(withSameZone, clock);
        assertNotEquals(independent, clock);
    }

    public void test_hashCode_isConstant() {
        MutableClock clock = MutableClock.epochUTC();
        int hash = clock.hashCode();
        clock.add(Period.ofMonths(1));
        assertEquals(clock.hashCode(), hash);
        clock.add(1, ChronoUnit.DAYS);
        assertEquals(clock.hashCode(), hash);
        clock.set(Year.of(2000));
        assertEquals(clock.hashCode(), hash);
        clock.set(ChronoField.INSTANT_SECONDS, -1);
        assertEquals(clock.hashCode(), hash);
    }

    public void test_hashCode_sameWhenSharedUpdates() {
        MutableClock clock = MutableClock.epochUTC();
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        MutableClock withSameZone = withOtherZone.withZone(ZoneOffset.UTC);
        assertEquals(clock.hashCode(), withSameZone.hashCode());
    }

    public void test_toString() {
        MutableClock clock = MutableClock.epochUTC();
        assertEquals(
                clock.toString(),
                "MutableClock[1970-01-01T00:00:00Z,Z]");
        clock.add(Period.ofYears(30));
        assertEquals(
                clock.toString(),
                "MutableClock[2000-01-01T00:00:00Z,Z]");
        MutableClock withOtherZone = clock.withZone(ZoneOffset.MIN);
        assertEquals(
                withOtherZone.toString(),
                "MutableClock[2000-01-01T00:00:00Z,-18:00]");
    }

    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(MutableClock.class));
    }

    public void test_serialization() throws Exception {
        MutableClock original = MutableClock.epochUTC();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(original);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        MutableClock ser = (MutableClock) in.readObject();
        assertEquals(ser.instant(), original.instant());
        assertEquals(ser.getZone(), original.getZone());
        // no shared updates
        assertNotEquals(ser, original);
        original.add(Duration.ofSeconds(1));
        assertNotEquals(ser.instant(), original.instant());
    }

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
                clock.instant(),
                Instant.EPOCH.plus(increment.multipliedBy(updateCount)));
    }
}
