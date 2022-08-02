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
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * An instantaneous point on the time-line measured in the TAI time-scale.
 * <p>
 * The <code>java.time</code> classes use the Java time-scale for simplicity.
 * That scale works on the assumption that the time-line is simple, there are no leap-seconds
 * and there are always 24 * 60 * 60 seconds in a day. Unfortunately, the Earth's rotation
 * is not straightforward, and a solar day does not match this definition.
 * <p>
 * This class is an alternative representation based on the TAI time-scale.
 * TAI is a single incrementing count of SI seconds.
 * There are no leap seconds or other discontinuities.
 * <p>
 * As a result of the simple definition, this time-scale would make an excellent timestamp.
 * However, there are, at the time of writing, few easy ways to obtain an accurate TAI instant,
 * but it is relatively easy to obtain a GPS instant.
 * GPS and TAI differ by the fixed amount of 19 seconds.
 * <p>
 * The duration between two points on the TAI time-scale is calculated solely using this class.
 * Do not use the {@code between} method on {@code Duration} as that will lose information.
 * Instead use {@link #durationUntil(TaiInstant)} on this class.
 * <p>
 * It is intended that most applications will use the {@code Instant} class
 * which uses the UTC-SLS mapping from UTC to guarantee 86400 seconds per day.
 * Specialist applications with access to an accurate time-source may find this class useful.
 *
 * <h3>Time-scale</h3>
 * <p>
 * The TAI time-scale is a very simple well-regarded representation of time.
 * The scale is defined using atomic clocks counting SI seconds.
 * It has proceeded in a continuous uninterrupted manner since the defined
 * epoch of {@code 1958-01-01T00:00:00(TAI)}.
 * There are no leap seconds or other discontinuities.
 * <p>
 * This class may be used for instants in the far past and far future.
 * Since some instants will be prior to 1958, it is not strictly an implementation of TAI.
 * Instead, it is a proleptic time-scale based on TAI and equivalent to it since 1958.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class TaiInstant
        implements Comparable<TaiInstant>, Serializable {
    // does not implement Temporal as that would enable methods like
    // Duration.between which gives the wrong answer due to lossy conversion

    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;
    /**
     * Parse regex.
     */
    private static final Pattern PARSER = Pattern.compile("([-]?[0-9]+)\\.([0-9]{9})s[(]TAI[)]");
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 2133469726395847026L;

    /**
     * The number of seconds from the epoch of 1958-01-01T00:00:00(TAI).
     */
    private final long seconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanos;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code TaiInstant} from the number of seconds from
     * the TAI epoch of 1958-01-01T00:00:00(TAI) with a nanosecond fraction of second.
     * <p>
     * This method allows an arbitrary number of nanoseconds to be passed in.
     * The factory will alter the values of the second and nanosecond in order
     * to ensure that the stored nanosecond is in the range 0 to 999,999,999.
     * For example, the following will result in the exactly the same instant:
     * <pre>
     *  TaiInstant.ofTaiSeconds(3, 1);
     *  TaiInstant.ofTaiSeconds(4, -999999999);
     *  TaiInstant.ofTaiSeconds(2, 1000000001);
     * </pre>
     *
     * @param taiSeconds  the number of seconds from the epoch of 1958-01-01T00:00:00(TAI)
     * @param nanoAdjustment  the nanosecond adjustment to the number of seconds, positive or negative
     * @return the TAI instant, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static TaiInstant ofTaiSeconds(long taiSeconds, long nanoAdjustment) {
        long secs = Math.addExact(taiSeconds, Math.floorDiv(nanoAdjustment, NANOS_PER_SECOND));
        int nos = (int) Math.floorMod(nanoAdjustment, NANOS_PER_SECOND);  // safe cast
        return new TaiInstant(secs, nos);
    }

    /**
     * Obtains an instance of {@code TaiInstant} from an {@code Instant}.
     * <p>
     * Converting a UTC-SLS instant to TAI requires leap second rules.
     * This method uses the latest available system rules.
     * The conversion first maps from UTC-SLS to UTC, then converts to TAI.
     * <p>
     * Conversion from an {@link Instant} will not be completely accurate near
     * a leap second in accordance with UTC-SLS.
     *
     * @param instant  the instant to convert, not null
     * @return the TAI instant, not null
     * @throws DateTimeException if the range of {@code TaiInstant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static TaiInstant of(Instant instant) {
        return UtcRules.system().convertToTai(instant);
    }

    /**
     * Obtains an instance of {@code TaiInstant} from a {@code UtcInstant}.
     * <p>
     * Converting a UTC instant to TAI requires leap second rules.
     * This method uses the latest available system rules.
     * <p>
     * The {@code TaiInstant} will represent exactly the same point on the
     * time-line as per the available leap-second rules.
     * If the leap-second rules change then conversion back to UTC may
     * result in a different instant.
     *
     * @param instant  the instant to convert, not null
     * @return the TAI instant, not null
     * @throws DateTimeException if the range of {@code TaiInstant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static TaiInstant of(UtcInstant instant) {
        return UtcRules.system().convertToTai(instant);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains an instance of {@code TaiInstant} from a text string.
     * <p>
     * The following format is accepted:
     * <ul>
     * <li>{@code {seconds}.{nanosOfSecond}s(TAI)}
     * </ul>
     * <p>
     * The accepted format is strict.
     * The seconds part must contain only numbers and a possible leading negative sign.
     * The nanoseconds part must contain exactly nine digits.
     * The trailing literal must be exactly specified.
     * This format parses the {@link #toString()} format.
     *
     * @param text  the text to parse such as "12345.123456789s(TAI)", not null
     * @return the parsed instant, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    @FromString
    public static TaiInstant parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PARSER.matcher(text);
        if (matcher.matches()) {
            try {
                long seconds = Long.parseLong(matcher.group(1));
                long nanos = Long.parseLong(matcher.group(2));
                return TaiInstant.ofTaiSeconds(seconds, nanos);
            } catch (NumberFormatException ex) {
                throw new DateTimeParseException("The text could not be parsed", text, 0, ex);
            }
        }
        throw new DateTimeParseException("The text could not be parsed", text, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance.
     *
     * @param taiSeconds  the number of TAI seconds from the epoch
     * @param nanoOfSecond  the nanoseconds within the second, from 0 to 999,999,999
     */
    private TaiInstant(long taiSeconds, int nanoOfSecond) {
        super();
        this.seconds = taiSeconds;
        this.nanos = nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds from the TAI epoch of 1958-01-01T00:00:00(TAI).
     * <p>
     * The TAI second count is a simple incrementing count of seconds where
     * second 0 is 1958-01-01T00:00:00(TAI).
     * The nanosecond part of the second is returned by {@link #getNano()}.
     *
     * @return the seconds from the epoch of 1958-01-01T00:00:00(TAI)
     */
    public long getTaiSeconds() {
        return seconds;
    }

    /**
     * Returns a copy of this {@code TaiInstant} with the number of seconds
     * from the TAI epoch of 1958-01-01T00:00:00(TAI).
     * <p>
     * The TAI second count is a simple incrementing count of seconds where
     * second 0 is 1958-01-01T00:00:00(TAI).
     * The nanosecond offset of the second is returned by {@code getNano}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param taiSeconds  the number of seconds from the epoch of 1958-01-01T00:00:00(TAI)
     * @return a {@code TaiInstant} based on this instant with the requested second, not null
     */
    public TaiInstant withTaiSeconds(long taiSeconds) {
        return ofTaiSeconds(taiSeconds, nanos);
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start
     * of the second.
     * <p>
     * The nanosecond-of-second value measures the total number of nanoseconds from
     * the second returned by {@link #getTaiSeconds()}.
     *
     * @return the nanoseconds within the second, from 0 to 999,999,999
     */
    public int getNano() {
        return nanos;
    }

    /**
     * Returns a copy of this {@code TaiInstant} with the nano-of-second value changed.
     * <p>
     * The nanosecond-of-second value measures the total number of nanoseconds from
     * the second returned by {@link #getTaiSeconds()}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second, from 0 to 999,999,999
     * @return a {@code TaiInstant} based on this instant with the requested nano-of-second, not null
     * @throws IllegalArgumentException if nanoOfSecond is out of range
     */
    public TaiInstant withNano(int nanoOfSecond) {
        if (nanoOfSecond < 0 || nanoOfSecond >= NANOS_PER_SECOND) {
            throw new IllegalArgumentException("NanoOfSecond must be from 0 to 999,999,999");
        }
        return ofTaiSeconds(seconds, nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration added.
     * <p>
     * The duration is added using simple addition of the seconds and nanoseconds
     * in the duration to the seconds and nanoseconds of this instant.
     * As a result, the duration is treated as being measured in TAI compatible seconds
     * for the purpose of this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a {@code TaiInstant} based on this instant with the duration added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public TaiInstant plus(Duration duration) {
        long secsToAdd = duration.getSeconds();
        int nanosToAdd = duration.getNano();
        if ((secsToAdd | nanosToAdd) == 0) {
            return this;
        }
        long secs = Math.addExact(seconds, secsToAdd);
        long nanoAdjustment = ((long) nanos) + nanosToAdd;  // safe int+int
        return ofTaiSeconds(secs, nanoAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration subtracted.
     * <p>
     * The duration is subtracted using simple subtraction of the seconds and nanoseconds
     * in the duration from the seconds and nanoseconds of this instant.
     * As a result, the duration is treated as being measured in TAI compatible seconds
     * for the purpose of this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a {@code TaiInstant} based on this instant with the duration subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public TaiInstant minus(Duration duration) {
        long secsToSubtract = duration.getSeconds();
        int nanosToSubtract = duration.getNano();
        if ((secsToSubtract | nanosToSubtract) == 0) {
            return this;
        }
        long secs = Math.subtractExact(seconds, secsToSubtract);
        long nanoAdjustment = ((long) nanos) - nanosToSubtract;  // safe int+int
        return ofTaiSeconds(secs, nanoAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the duration between this instant and the specified instant.
     * <p>
     * This calculates the duration between this instant and another based on
     * the TAI time-scale. Adding the duration to this instant using {@link #plus}
     * will always result in an instant equal to the specified instant.
     *
     * @param otherInstant  the instant to calculate the duration until, not null
     * @return the duration until the specified instant, may be negative, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Duration durationUntil(TaiInstant otherInstant) {
        long durSecs = Math.subtractExact(otherInstant.seconds, seconds);
        long durNanos = otherInstant.nanos - nanos;
        return Duration.ofSeconds(durSecs, durNanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to an {@code Instant}.
     * <p>
     * Converting a TAI instant to UTC-SLS requires leap second rules.
     * This method uses the latest available system rules.
     * The conversion first maps from TAI to UTC, then converts to UTC-SLS.
     * <p>
     * Conversion to an {@link Instant} will not be completely accurate near
     * a leap second in accordance with UTC-SLS.
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
     * Converting a TAI instant to UTC requires leap second rules.
     * This method uses the latest available system rules.
     * <p>
     * The {@link UtcInstant} will represent exactly the same point on the
     * time-line as per the available leap-second rules.
     * If the leap-second rules change then conversion back to TAI may
     * result in a different instant.
     *
     * @return a {@code UtcInstant} representing the same instant, not null
     * @throws DateTimeException if the range of {@code UtcInstant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public UtcInstant toUtcInstant() {
        return UtcRules.system().convertToUtc(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instant to another based on the time-line.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(TaiInstant otherInstant) {
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
     * @param otherInstant  the other instant to compare to, not null
     * @return true if this instant is after the specified instant
     * @throws NullPointerException if otherInstant is null
     */
    public boolean isAfter(TaiInstant otherInstant) {
        return compareTo(otherInstant) > 0;
    }

    /**
     * Checks if this instant is before the specified instant.
     * <p>
     * The comparison is based on the time-line position of the instants.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return true if this instant is before the specified instant
     * @throws NullPointerException if otherInstant is null
     */
    public boolean isBefore(TaiInstant otherInstant) {
        return compareTo(otherInstant) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instant is equal to the specified {@code TaiInstant}.
     *
     * @param otherInstant  the other instant, null returns false
     * @return true if the other instant is equal to this one
     */
    @Override
    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (otherInstant instanceof TaiInstant) {
            TaiInstant other = (TaiInstant) otherInstant;
            return this.seconds == other.seconds &&
                    this.nanos == other.nanos;
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
        // TODO: Evaluate hash code
        return ((int) (seconds ^ (seconds >>> 32))) + 51 * nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this instant.
     * <p>
     * The string is formatted as {@code {seconds).(nanosOfSecond}s(TAI)}.
     * At least one second digit will be present.
     * The nanoseconds will always be nine digits.
     *
     * @return a representation of this instant, not null
     */
    @Override
    @ToString
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(seconds);
        int pos = buf.length();
        buf.append(nanos + NANOS_PER_SECOND);
        buf.setCharAt(pos, '.');
        buf.append("s(TAI)");
        return buf.toString();
    }

}
