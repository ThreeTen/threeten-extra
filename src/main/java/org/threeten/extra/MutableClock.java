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

import static java.time.Instant.EPOCH;
import static java.time.ZoneOffset.UTC;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Objects;

/**
 * A clock that does not advance on its own and that must be updated manually.
 * <p>
 * This class is designed for testing clock-sensitive components by simulating
 * the passage of time. This class differs from {@link
 * Clock#fixed(Instant, ZoneId)} and {@link Clock#offset(Clock, Duration)} in
 * that it permits arbitrary, unrestricted updates to its instant. This allows
 * for testing patterns that are not well-supported by the {@code fixed} and
 * {@code offset} clocks such as the following pattern:
 * <ol>
 * <li>Create the clock-sensitive component to be tested
 * <li>Verify some behavior of the component in the initial state
 * <li>Advance the clock <em>without recreating the component</em>
 * <li>Verify that the component behaves as expected given the (artificial)
 *     delta in clock time since the initial state
 * </ol>
 * <p>
 * This class is mutable. The time-zone of the clock is fixed, but the instant
 * may be updated at will.
 * <p>
 * The instant may be set to any value even if that new value is less than the
 * previous value. Caution should be exercised when moving the clock backwards,
 * since clock-sensitive components are likely to assume that time is
 * monotonically increasing.
 * <p>
 * Update semantics are expressed in terms of {@link ZonedDateTime}. The steps
 * of each update are as follows:
 * <ol>
 * <li>The clock captures its own state in a {@code ZonedDateTime} via {@link
 *     ZonedDateTime#now(Clock)} (or the equivalent thereof)
 * <li>The update operation is applied to that {@code ZonedDateTime}, producing
 *     a new {@code ZonedDateTime}
 * <li>The resulting {@code ZonedDateTime} is converted to an instant via {@link
 *     ZonedDateTime#toInstant()} (or the equivalent thereof)
 * <li>The clock's instant is set to that new instant
 * </ol>
 * <p>
 * Therefore, whenever there is a question about what argument types, units,
 * fields, or values an update operation supports, or what the result will be,
 * refer to the corresponding method of {@code ZonedDateTime}. Links are
 * provided from the documentation of each update operation of this class to the
 * corresponding method of {@code ZonedDateTime}.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is thread-safe. Updates are atomic and synchronized.
 * <p>
 * While update semantics are expressed in terms of {@code ZonedDateTime}, that
 * imposes no requirements on implementation details. The implementation may
 * avoid using {@code ZonedDateTime} completely or only sometimes, for
 * convenience, efficiency, or any other reason.
 *
 * @serial exclude
 */
public final class MutableClock
        extends Clock
        implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -6152029959790119695L;

    /**
     * The mutable instant of this clock.
     */
    private final transient InstantHolder instantHolder;

    /**
     * The fixed time-zone of this clock.
     */
    private final transient ZoneId zone;

    /**
     * Obtains a new {@code MutableClock} set to the epoch of
     * 1970-01-01T00:00:00Z, converting to date and time using the UTC
     * time-zone.
     * <p>
     * Use this method when a {@code MutableClock} is needed and neither its
     * initial value nor its time-zone are important. This is often true when
     * testing behavior that depends on elapsed <em>relative</em> time rather
     * than <em>absolute</em> time.
     *
     * @return a new {@code MutableClock}, not null
     */
    public static MutableClock epochUTC() {
        return MutableClock.of(EPOCH, UTC);
    }

    /**
     * Obtains a new {@code MutableClock} set to the specified instant,
     * converting to date and time using the specified time-zone.
     *
     * @param instant the initial value for the clock, not null
     * @param zone the time-zone to use, not null
     * @return a new {@code MutableClock}, not null
     */
    public static MutableClock of(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
        return new MutableClock(new InstantHolder(instant), zone);
    }

    /**
     * Constructor.
     *
     * @param instantHolder the mutable instant, validated not null
     * @param zone the fixed time-zone, validated not null
     */
    private MutableClock(InstantHolder instantHolder, ZoneId zone) {
        this.instantHolder = instantHolder;
        this.zone = zone;
    }

    /**
     * Overrides the instant of this clock with the specified value.
     *
     * @param instant the new instant for this clock, not null
     */
    public void setInstant(Instant instant) {
        Objects.requireNonNull(instant, "instant");
        instantHolder.set(instant);
    }

    /**
     * Adds the specified amount to this clock.
     * <p>
     * Atomically updates this clock to the value of the following expression:
     * <pre>
     *   ZonedDateTime.now(thisClock)
     *                .plus(amountToAdd)
     *                .toInstant()
     * </pre>
     *
     * @param amountToAdd the amount to add, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     * @see ZonedDateTime#plus(TemporalAmount)
     */
    public void add(TemporalAmount amountToAdd) {
        Objects.requireNonNull(amountToAdd, "amountToAdd");
        synchronized (instantHolder) {
            ZonedDateTime current = ZonedDateTime.ofInstant(instantHolder.get(), zone);
            ZonedDateTime result = current.plus(amountToAdd);
            instantHolder.set(result.toInstant());
        }
    }

    /**
     * Adds the specified amount to this clock.
     * <p>
     * Atomically updates this clock to the value of the following expression:
     * <pre>
     *   ZonedDateTime.now(thisClock)
     *                .plus(amountToAdd, unit)
     *                .toInstant()
     * </pre>
     *
     * @param amountToAdd the amount of the specified unit to add, may be negative
     * @param unit the unit of the amount to add, not null
     * @throws DateTimeException if the unit cannot be added
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     * @see ZonedDateTime#plus(long, TemporalUnit)
     */
    public void add(long amountToAdd, TemporalUnit unit) {
        Objects.requireNonNull(unit, "unit");
        synchronized (instantHolder) {
            ZonedDateTime current = ZonedDateTime.ofInstant(instantHolder.get(), zone);
            ZonedDateTime result = current.plus(amountToAdd, unit);
            instantHolder.set(result.toInstant());
        }
    }

    /**
     * Adjusts this clock.
     * <p>
     * Atomically updates this clock to the value of the following expression:
     * <pre>
     *   ZonedDateTime.now(thisClock)
     *                .with(adjuster)
     *                .toInstant()
     * </pre>
     *
     * @param adjuster the adjuster to use, not null
     * @throws DateTimeException if the adjustment cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     * @see ZonedDateTime#with(TemporalAdjuster)
     */
    public void set(TemporalAdjuster adjuster) {
        Objects.requireNonNull(adjuster, "adjuster");
        synchronized (instantHolder) {
            ZonedDateTime current = ZonedDateTime.ofInstant(instantHolder.get(), zone);
            ZonedDateTime result = current.with(adjuster);
            instantHolder.set(result.toInstant());
        }
    }

    /**
     * Alters the specified field of this clock.
     * <p>
     * Atomically updates this clock to the value of the following expression:
     * <pre>
     *   ZonedDateTime.now(thisClock)
     *                .with(field, newValue)
     *                .toInstant()
     * </pre>
     *
     * @param field the field to set, not null
     * @param newValue the new value of the field
     * @throws DateTimeException if the field cannot be set
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException if numeric overflow occurs
     * @see ZonedDateTime#with(TemporalField, long)
     */
    public void set(TemporalField field, long newValue) {
        Objects.requireNonNull(field, "field");
        synchronized (instantHolder) {
            ZonedDateTime current = ZonedDateTime.ofInstant(instantHolder.get(), zone);
            ZonedDateTime result = current.with(field, newValue);
            instantHolder.set(result.toInstant());
        }
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    /**
     * Returns a {@code MutableClock} that uses the specified time-zone and that
     * has shared updates with this clock.
     * <p>
     * Two clocks with shared updates always have the same instant, and all
     * updates applied to either clock affect both clocks.
     *
     * @param zone the time-zone to use for the returned clock, not null
     * @return a view of this clock in the specified time-zone, not null
     */
    @Override
    public MutableClock withZone(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        if (zone.equals(this.zone)) {
            return this;
        }
        return new MutableClock(instantHolder, zone);
    }

    @Override
    public Instant instant() {
        return instantHolder.get();
    }

    /**
     * Returns {@code true} if {@code obj} is a {@code MutableClock} that uses
     * the same time-zone as this clock and has shared updates with this clock.
     * <p>
     * Two clocks with shared updates always have the same instant, and all
     * updates applied to either clock affect both clocks.
     * <p>
     * A deserialized {@code MutableClock} is not equal to the original clock
     * that was serialized, since the two clocks do not have shared updates.
     *
     * @param obj the object to check, null returns {@code false}
     * @return {@code true} if this is equal to the other clock
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MutableClock) {
            MutableClock other = (MutableClock) obj;
            return instantHolder == other.instantHolder && zone.equals(other.zone);
        }
        return false;
    }

    /**
     * A hash code for this clock, which is constant for this instance.
     *
     * @return a constant hash code for this instance
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(instantHolder) ^ zone.hashCode();
    }

    @Override
    public String toString() {
        return "MutableClock[" + instant() + "," + getZone() + "]";
    }

    /**
     * Returns the serialization proxy to replace this {@code MutableClock}.
     *
     * @return the serialization proxy, not null
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Throws {@link InvalidObjectException}.
     *
     * @param s ignored
     * @throws InvalidObjectException always
     */
    private void readObject(ObjectInputStream s) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    /**
     * The serialized form of a {@code MutableClock}.
     *
     * @serial include
     */
    private static final class SerializationProxy
            implements Serializable {

        /**
         * Serialization version.
         */
        private static final long serialVersionUID = 8602110640241828260L;

        /**
         * A snapshot of the instant of the {@code MutableClock}, taken when the
         * clock was serialized, not null.
         *
         * @serial
         */
        private final Instant instant;

        /**
         * The time-zone of the {@code MutableClock}, not null.
         *
         * @serial
         */
        private final ZoneId zone;

        /**
         * Constructor.
         *
         * @param clock the {@code MutableClock} to be serialized, not null
         */
        SerializationProxy(MutableClock clock) {
            instant = clock.instant();
            zone = clock.getZone();
        }

        /**
         * Returns the {@code MutableClock} to replace this serialization proxy.
         *
         * @return the {@code MutableClock}, not null
         * @throws InvalidObjectException if the instant or time-zone is null
         */
        private Object readResolve() throws InvalidObjectException {
            if (instant == null) {
                throw new InvalidObjectException("null instant");
            }
            if (zone == null) {
                throw new InvalidObjectException("null zone");
            }
            return MutableClock.of(instant, zone);
        }
    }

    /**
     * An identity-having holder object for a mutable instant value.
     * <p>
     * Clocks have shared updates when they share a holder object. Clocks rely
     * on the identity of the holder object in their {@code equals} and {@code
     * hashCode} methods.
     * <p>
     * Reads of the value are volatile and are never stale. Blind writes to the
     * value are volatile and do not need to synchronize. Atomic read-and-write
     * operations must synchronize on the holder object instance.
     */
    private static final class InstantHolder {
        /**
         * The current value.
         */
        private volatile Instant value;

        /**
         * Constructor.
         *
         * @param value the initial value, validated not null
         */
        InstantHolder(Instant value) {
            this.value = value;
        }

        /**
         * Reads the value.
         *
         * @return the current value, not null
         */
        Instant get() {
            return value;
        }

        /**
         * Writes the value.
         *
         * @param value the new value, validated not null
         */
        void set(Instant value) {
            this.value = value;
        }
    }
}
