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

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * An immutable interval of time between two instants.
 * <p>
 * An interval represents the time on the time-line between two {@link Instant}s.
 * The class stores the start and end instants, with the start inclusive and the end exclusive.
 * The end instant is always greater than or equal to the start instant.
 * <p>
 * The {@link Duration} of an interval can be obtained, but is a separate concept.
 * An interval is connected to the time-line, whereas a duration is not.
 * <p>
 * Intervals are not comparable. To compare the length of two intervals, it is
 * generally recommended to compare their durations.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class Interval
        implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 8375285238652L;

    /**
     * The start instant (inclusive).
     */
    private final Instant start;
    /**
     * The end instant (exclusive).
     */
    private final Instant end;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Interval} from the start and end instant.
     * <p>
     * The end instant must not be before the start instant.
     *
     * @param startInclusive  the start instant, inclusive, not null
     * @param endExclusive  the end instant, exclusive, not null
     * @return the interval, not null
     * @throws DateTimeException if the end is before the start
     */
    public static Interval of(Instant startInclusive, Instant endExclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endExclusive, "endExclusive");
        if (endExclusive.isBefore(startInclusive)) {
            throw new DateTimeException("Start instant must be before end instant");
        }
        return new Interval(startInclusive, endExclusive);
    }

    /**
     * Obtains an instance of {@code Interval} from the start and a duration.
     * <p>
     * The end instant is calculated as the start plus the duration.
     * The duration must not be negative.
     *
     * @param startInclusive  the start instant, inclusive, not null
     * @param duration  the duration from the start to the end, not null
     * @return the interval, not null
     * @throws DateTimeException if the end is before the start,
     *  or if the duration addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs when adding the duration
     */
    public static Interval of(Instant startInclusive, Duration duration) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(duration, "duration");
        if (duration.isNegative()) {
            throw new DateTimeException("Duration must not be zero or negative");
        }
        return new Interval(startInclusive, startInclusive.plus(duration));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Interval} from a text string such as
     * {@code 2007-12-03T10:15:30Z/2007-12-04T10:15:30Z}.
     * <p>
     * The string must consist of one of the following three formats:
     * <ul>
     * <li>a representations of an {@link Instant}, followed by a forward slash, followed by a representation of a {@link Instant}
     * <li>a representation of an {@link Instant}, followed by a forward slash, followed by a representation of a {@link Duration}
     * <li>a representation of a {@link Duration}, followed by a forward slash, followed by a representation of an {@link Instant}
     * </ul>
     * 
     *
     * @param text  the text to parse, not null
     * @return the parsed interval, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static Interval parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '/') {
                char firstChar = text.charAt(0);
                if (firstChar == 'P' || firstChar == 'p') {
                    // duration followed by instant
                    Duration duration = Duration.parse(text.subSequence(0, i));
                    Instant end = Instant.parse(text.subSequence(i + 1, text.length()));
                    return Interval.of(end.minus(duration), end);
                } else {
                    // instant followed by instant or duration
                    Instant start = Instant.parse(text.subSequence(0, i));
                    if (i + 1 < text.length()) {
                        char c = text.charAt(i + 1);
                        if (c == 'P' || c == 'p') {
                            Duration duration = Duration.parse(text.subSequence(i + 1, text.length()));
                            return Interval.of(start, start.plus(duration));
                        }
                    }
                    Instant end = Instant.parse(text.subSequence(i + 1, text.length()));
                    return Interval.of(start, end);
                }
            }
        }
        throw new DateTimeParseException("Interval cannot be parsed, no forward slash found", text, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param startInclusive  the start instant, inclusive, validated not null
     * @param endExclusive  the end instant, exclusive, validated not null
     */
    private Interval(Instant startInclusive, Instant endExclusive) {
        this.start = startInclusive;
        this.end = endExclusive;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the start of this time interval, inclusive.
     *
     * @return the start of the time interval
     */
    public Instant getStart() {
        return start;
    }

    /** 
     * Gets the end of this time interval, exclusive.
     *
     * @return the end of the time interval
     */
    public Instant getEnd() {
        return end;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a new interval with the specified start instant.
     *
     * @param start  the start instant for the new interval, not null
     * @return an interval with the end from this interval and the specified start
     * @throws IllegalArgumentException if the resulting interval has end before start
     */
    public Interval withStart(Instant start) {
        return Interval.of(start, end);
    }

    /**
     * Creates a new interval with the specified end instant.
     *
     * @param end  the end instant for the new interval, not null
     * @return an interval with the start from this interval and the specified end
     * @throws IllegalArgumentException if the resulting interval has end before start
     */
    public Interval withEnd(Instant end) {
        return Interval.of(start, end);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this interval contain the specified instant.
     * <p>
     * The result is true if the instant is equal or after the start and before the end.
     * An empty interval does not contain anything.
     *
     * @param instant  the instant, not null
     * @return true if this interval contains the instant
     */
    public boolean contains(Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return (instant.compareTo(start) >= 0 && instant.compareTo(end) < 0);
    }

    /**
     * Checks if this interval encloses the specified interval.
     * <p>
     * This checks if the specified interval is fully enclosed by this interval.
     * The result is true if the start of the specified interval is contained in this interval,
     * and the end is contained or equal to the end of this interval.
     * An empty interval contains an equal empty interval, but no other intervals.
     *
     * @param interval  the interval, not null
     * @return true if this interval contains the other interval
     */
    public boolean encloses(Interval interval) {
        Objects.requireNonNull(interval, "interval");
        return start.compareTo(interval.start) <= 0 &&
                interval.end.compareTo(end) <= 0;
    }

    /**
     * Checks if this interval overlaps the specified interval.
     * <p>
     * The result is true if the the two intervals share some part of the time-line.
     * An empty interval overlaps an equal empty interval.
     *
     * @param interval  the time interval to compare to, null means a zero length interval now
     * @return true if the time intervals overlap
     */
    public boolean overlaps(Interval interval) {
        Objects.requireNonNull(interval, "interval");
        return interval.equals(this) ||
                (start.compareTo(interval.end) < 0 && interval.start.compareTo(end) < 0);
    }

    /**
     * Checks if this interval abuts the specified interval.
     * <p>
     * The result is true if the the two intervals have one instant in common.
     * An empty interval does not abut an equal empty interval.
     *
     * @param interval  the interval, not null
     * @return true if this interval abuts the other interval
     */
    public boolean abuts(Interval interval) {
        Objects.requireNonNull(interval, "interval");
        return end.equals(interval.start) ^ start.equals(interval.end);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the duration of this interval.
     * <p>
     * An {@code Interval} is associated with two specific instants on the time-line.
     * A {@code Duration} is simply an amount of time, separate from the time-line.
     *
     * @return the duration of the time interval
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration toDuration() {
        return Duration.between(start, end);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this interval is equal to another interval.
     * <p>
     * Compares this {@code Interval} with another ensuring that the two instants are the same.
     * Only objects of type {@code Interval} are compared, other types return false.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other interval
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Interval) {
            Interval other = (Interval) obj;
            return start.equals(other.start) && end.equals(other.end);
        }
        return false;
    }

    /**
     * A hash code for this interval.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return start.hashCode() ^ end.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this interval as a {@code String}, such as {@code 2007-12-03T10:15:30/2007-12-04T10:15:30}.
     * <p>
     * The output will be the ISO-8601 format formed by combining the
     * {@code toString()} methods of the two instants, separated by a forward slash.
     *
     * @return a string representation of this instant, not null
     */
    @Override
    public String toString() {
        return start.toString() + '/' + end.toString();
    }

}
