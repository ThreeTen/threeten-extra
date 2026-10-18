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
package org.threeten.extra.scale;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;

/**
 * An instantaneous point on the time-line measured in the MISP time-scale.
 * <p>
 * The <code>java.time</code> classes use the Java time-scale for simplicity.
 * That scale works on the assumption that the time-line is simple, there are no
 * leap-seconds and there are always 24 * 60 * 60 seconds in a day.
 * Unfortunately, the Earth's rotation is not straightforward, and a solar day
 * does not match this definition.
 * <p>
 * This class is an alternative representation based on the MISP time-scale. The
 * MISP time-scale is a single incrementing count of SI seconds. There are no
 * leap seconds or other discontinuities.
 * <p>
 * The duration between two points on the MISP time-scale is calculated solely
 * using this class. Do not use the {@code between} method on {@code Duration}
 * as that will lose information. Instead use
 * {@link #durationUntil(MispInstant)} on this class.
 * <p>
 * It is intended that most applications will use the {@code Instant} class
 * which uses the UTC-SLS mapping from UTC to guarantee 86400 seconds per day.
 *
 * <h3>Time-scale</h3>
 * <p>
 * The scale is explained in Chapter 6 of the MISB Motion Imagery Handbook. The
 * Motion Imagery Handbook can be downloaded from the NSG Registry
 * (https://nsgreg.nga.mil). The MISP Time System clock counts SI-Seconds since
 * the Epoch of 1970-01-01T00:00:00.0Z (UTC). The MISP Time System is locked
 * with TAI with a Leap Second offset of 8.000082 seconds. There are no leap
 * seconds or other discontinuities.
 *
 * <p>
 * This class may be used for instants in the far past and far future. Since
 * some instants will be prior to 1970, it is not strictly an implementation of
 * the MISP Time System. However it is equivalent to it since 1970.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class MispInstant
        implements Comparable<MispInstant>, Serializable {
    // does not implement Temporal as that would enable methods like
    // Duration.between which gives the wrong answer due to lossy conversion

    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 5965510986576614924L;

    /**
     * Seconds part of the difference between TAI and MISP.
     *
     * <p>
     * This is the number of seconds between 1958-01-01T00:00:00(TAI) and
     * 1970-01-01T00:00.00(UTC).
     */
    private static final long TAI_OFFSET_SEC = 378691208;

    /**
     * Nanoseconds part of the difference between TAI and MISP.
     *
     * <p>
     * This is the fractional part of the number of seconds between
     * 1958-01-01T00:00:00(TAI) and 1970-01-01T00:00.00(UTC).
     */
    private static final long TAI_OFFSET_NANOS = 82000;

    /**
     * The number of seconds from the epoch of 1970-01-01T00:00:00(UTC).
     */
    private final long seconds;

    /**
     * The number of nanoseconds, later along the time-line, from the seconds
     * field. This is always positive, and never exceeds 999,999,999.
     */
    private final int nanos;

    /**
     * Obtains an instance of {@code MispInstant} from the number of seconds
     * from the MISP epoch of 1970-01-01T00:00:00(UTC) with a nanosecond
     * fraction of second.
     * <p>
     * This method allows an arbitrary number of nanoseconds to be passed in.
     * The factory will alter the values of the second and nanosecond in order
     * to ensure that the stored nanosecond is in the range 0 to 999,999,999.
     * For example, the following will result in the exactly the same instant:
     * <pre>
     *  MispInstant.ofMispSeconds(3, 1);
     *  MispInstant.ofMispSeconds(4, -999999999);
     *  MispInstant.ofMispSeconds(2, 1000000001);
     * </pre>
     *
     * @param mispSeconds the number of seconds from the epoch of 1970-01-01T00:00:00(UTC)
     * @param nanoAdjustment the nanosecond adjustment to the number of seconds,
     * positive or negative
     * @return the MISP instant, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static MispInstant ofMispSeconds(long mispSeconds, long nanoAdjustment) {
        long secs = Math.addExact(mispSeconds, Math.floorDiv(nanoAdjustment, NANOS_PER_SECOND));
        int nos = (int) Math.floorMod(nanoAdjustment, NANOS_PER_SECOND);  // safe cast
        return new MispInstant(secs, nos);
    }

    /**
     * Obtains an instance of {@code MispInstant} from a {@code TaiInstant}.
     *
     * @param tai the TAI instant to convert, not null
     * @return the MISP instant, not null
     */
    public static MispInstant of(TaiInstant tai) {
        long secs = tai.getTaiSeconds() - TAI_OFFSET_SEC;
        long nos = tai.getNano() - TAI_OFFSET_NANOS;
        return ofMispSeconds(secs, nos);
    }

    /**
     * Obtains an instance of {@code MispInstant} from an {@code Instant}.
     * <p>
     * Converting a UTC-SLS instant to the MISP Time System requires leap second
     * rules. This method uses the latest available system rules.
     * <p>
     * Conversion from an {@link Instant} will not be completely accurate near a
     * leap second in accordance with UTC-SLS.
     *
     * @param instant the instant to convert, not null
     * @return the MISP instant, not null
     * @throws DateTimeException if the range of {@code MispInstant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static MispInstant of(Instant instant) {
        return UtcRules.system().convertToMisp(instant);
    }

    /**
     * Obtains an instance of {@code MispInstant} from a {@code UtcInstant}.
     * <p>
     * Converting a UTC instant to MISP requires leap second rules. This method
     * uses the latest available system rules.
     * <p>
     * The {@code MispInstant} will represent exactly the same point on the
     * time-line as per the available leap-second rules. If the leap-second
     * rules change then conversion back to UTC may result in a different
     * instant.
     *
     * @param instant the instant to convert, not null
     * @return the MISP instant, not null
     * @throws DateTimeException if the range of {@code MispInstant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static MispInstant of(UtcInstant instant) {
        return UtcRules.system().convertToMisp(instant);
    }

    /**
     * Constructs an instance.
     *
     * @param mispSeconds the number of MISP seconds from the epoch
     * @param nanoOfSecond the nanoseconds within the second, from 0 to
     * 999,999,999
     */
    private MispInstant(long mispSeconds, int nanoOfSecond) {
        super();
        this.seconds = mispSeconds;
        this.nanos = nanoOfSecond;
    }

    /**
     * Gets the number of seconds from the MISP epoch of 1970-01-01T00:00:00(UTC).
     * <p>
     * The MISP second count is a simple incrementing count of seconds where
     * second 0 is 1970-01-01T00:00:00(UTC). The nanosecond part of the second
     * is returned by {@link getNano}.
     *
     * @return the seconds from the epoch of 1970-01-01T00:00:00(UTC)
     */
    public long getMispSeconds() {
        return seconds;
    }

    /**
     * Returns a copy of this {@code MispInstant} with the number of seconds from the MISP epoch of 1970-01-01T00:00:00(UTC).
     * <p>
     * The MISP second count is a simple incrementing count of seconds where
     * second 0 is 1970-01-01T00:00:00(UTC). The nanosecond part of the second
     * is returned by {@link getNano}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param mispSeconds the number of seconds from the epoch of
     * 1970-01-01T00:00:00(UTC)
     * @return a {@code MispInstant} based on this instant with the requested
     * second, not null
     */
    public MispInstant withMispSeconds(long mispSeconds) {
        return ofMispSeconds(mispSeconds, nanos);
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start of the second.
     * <p>
     * The nanosecond-of-second value measures the total number of nanoseconds
     * from the second returned by {@link getMispSeconds()}.
     *
     * @return the nanoseconds within the second, from 0 to 999,999,999
     */
    public int getNano() {
        return nanos;
    }

    /**
     * Returns a copy of this {@code MispInstant} with the nano-of-second value changed.
     * <p>
     * The nanosecond-of-second value measures the total number of nanoseconds
     * from the second returned by {@link getMispSeconds()}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond the nano-of-second, from 0 to 999,999,999
     * @return a {@code MispInstant} based on this instant with the requested nano-of-second, not null
     * @throws IllegalArgumentException if nanoOfSecond is out of range
     */
    public MispInstant withNano(int nanoOfSecond) {
        if (nanoOfSecond < 0 || nanoOfSecond >= NANOS_PER_SECOND) {
            throw new IllegalArgumentException("NanoOfSecond must be from 0 to 999,999,999");
        }
        return ofMispSeconds(seconds, nanoOfSecond);
    }

    /**
     * Returns a copy of this instant with the specified duration added.
     * <p>
     * The duration is added using simple addition of the seconds and
     * nanoseconds in the duration to the seconds and nanoseconds of this
     * instant. As a result, the duration is treated as being measured in SI
     * seconds for the purpose of this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration the duration to add, not null
     * @return a {@code MispInstant} based on this instant with the duration added, not null
     * @throws ArithmeticException if the calculation exceeds the supported
     * range
     */
    public MispInstant plus(Duration duration) {
        long secsToAdd = duration.getSeconds();
        int nanosToAdd = duration.getNano();
        if ((secsToAdd | nanosToAdd) == 0) {
            return this;
        }
        long secs = Math.addExact(seconds, secsToAdd);
        long nanoAdjustment = ((long) nanos) + nanosToAdd;  // safe int+int
        return ofMispSeconds(secs, nanoAdjustment);
    }

    /**
     * Returns a copy of this instant with the specified duration subtracted.
     * <p>
     * The duration is subtracted using simple subtraction of the seconds and
     * nanoseconds in the duration from the seconds and nanoseconds of this
     * instant. As a result, the duration is treated as being measured in SI
     * seconds for the purpose of this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration the duration to subtract, not null
     * @return a {@code MispInstant} based on this instant with the duration subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public MispInstant minus(Duration duration) {
        long secsToSubtract = duration.getSeconds();
        int nanosToSubtract = duration.getNano();
        if ((secsToSubtract | nanosToSubtract) == 0) {
            return this;
        }
        long secs = Math.subtractExact(seconds, secsToSubtract);
        long nanoAdjustment = ((long) nanos) - nanosToSubtract;  // safe int+int
        return ofMispSeconds(secs, nanoAdjustment);
    }

    /**
     * Returns the duration between this instant and the specified instant.
     * <p>
     * This calculates the duration between this instant and another based on
     * the MISP time scale. Adding the duration to this instant using
     * {@link #plus} will always result in an instant equal to the specified
     * instant.
     *
     * @param otherInstant the instant to calculate the duration until, not null
     * @return the duration until the specified instant, may be negative, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Duration durationUntil(MispInstant otherInstant) {
        long durSecs = Math.subtractExact(otherInstant.seconds, seconds);
        long durNanos = otherInstant.nanos - nanos;
        return Duration.ofSeconds(durSecs, durNanos);
    }

    /**
     * Converts this instant to an {@code Instant}.
     * <p>
     * Converting a MISP instant to UTC-SLS requires leap second rules. This
     * method uses the latest available system rules. The conversion first maps
     * from the MISP time system to UTC, then converts to UTC-SLS.
     * <p>
     * Conversion to an {@link Instant} will not be completely accurate near a
     * leap second in accordance with UTC-SLS.
     *
     * @return an {@code Instant} representing the best approximation of this instant, not null
     * @throws DateTimeException if the range of {@code Instant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Instant toInstant() {
        return UtcRules.system().convertToInstant(this);
    }

    /**
     * Converts this instant to a {@code UtcInstant}.
     * <p>
     * Converting a MISP instant to UTC requires leap second rules. This method
     * uses the latest available system rules.
     * <p>
     * The {@link UtcInstant} will represent exactly the same point on the
     * time-line as per the available leap-second rules. If the leap-second
     * rules change then conversion back to the MISP time system may result in a
     * different instant.
     *
     * @return a {@code UtcInstant} representing the same instant, not null
     * @throws DateTimeException if the range of {@code UtcInstant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public UtcInstant toUtcInstant() {
        return UtcRules.system().convertToUtc(this);
    }

    /**
     * Converts this instant to a {@code TaiInstant}.
     *
     * @return a {@code TaiInstant} representing the same instant, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public TaiInstant toTaiInstant() {
        long secs = seconds + TAI_OFFSET_SEC;
        long nos = nanos + TAI_OFFSET_NANOS;
        return TaiInstant.ofTaiSeconds(secs, nos);
    }

    /**
     * Compares this instant to another based on the time-line.
     *
     * @param otherInstant the other instant to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(MispInstant otherInstant) {
        int cmp = Long.compare(seconds, otherInstant.seconds);
        if (cmp != 0) {
            return cmp;
        }
        return nanos - otherInstant.nanos;
    }

    /**
     * Checks if this instant is after the specified instant.
     * <p>
     * The comparison is based on the time-line position of the instants.
     *
     * @param otherInstant the other instant to compare to, not null
     * @return true if this instant is after the specified instant
     * @throws NullPointerException if otherInstant is null
     */
    public boolean isAfter(MispInstant otherInstant) {
        return compareTo(otherInstant) > 0;
    }

    /**
     * Checks if this instant is before the specified instant.
     * <p>
     * The comparison is based on the time-line position of the instants.
     *
     * @param otherInstant the other instant to compare to, not null
     * @return true if this instant is before the specified instant
     * @throws NullPointerException if otherInstant is null
     */
    public boolean isBefore(MispInstant otherInstant) {
        return compareTo(otherInstant) < 0;
    }

    /**
     * Checks if this instant is equal to the specified {@code MispInstant}.
     *
     * @param otherInstant the other instant, null returns false
     * @return true if the other instant is equal to this one
     */
    @Override
    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (otherInstant instanceof MispInstant) {
            MispInstant other = (MispInstant) otherInstant;
            return this.seconds == other.seconds
                    && this.nanos == other.nanos;
        }
        return false;
    }

    /**
     * Returns a hash code for this instant.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return ((int) (seconds ^ (seconds >>> 32))) + 51 * nanos;
    }

}
