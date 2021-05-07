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

import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static org.threeten.extra.scale.UtcRules.NANOS_PER_SECOND;
import static org.threeten.extra.scale.UtcRules.OFFSET_MJD_EPOCH;
import static org.threeten.extra.scale.UtcRules.SECS_PER_DAY;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalAccessor;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * An instantaneous point on the time-line measured in the UTC time-scale
 * with leap seconds.
 * <p>
 * The <code>java.time</code> classes use the Java time-scale for simplicity.
 * That scale works on the assumption that the time-line is simple, there are no leap-seconds
 * and there are always 24 * 60 * 60 seconds in a day. Unfortunately, the Earth's rotation
 * is not straightforward, and a solar day does not match this definition.
 * <p>
 * This class is an alternative representation based on the UTC time-scale which
 * includes leap-seconds. Leap-seconds are additional seconds that are inserted into the
 * year-month-day-hour-minute-second time-line in order to keep UTC in line with the solar day.
 * When a leap second occurs, an accurate clock will show the time {@code 23:59:60} just before midnight.
 * <p>
 * Leap-seconds are announced in advance, typically at least six months.
 * The {@link UtcRules} class models which dates have leap-seconds.
 * All the methods on this class use the latest available system rules.
 * <p>
 * The system leap-second rules fix the start point of UTC as 1972.
 * This date was chosen as UTC was more complex before 1972.
 * <p>
 * The duration between two points on the UTC time-scale is calculated solely using this class.
 * Do not use the {@code between} method on {@code Duration} as that will lose information.
 * Instead use {@link #durationUntil(UtcInstant)} on this class.
 * <p>
 * It is intended that most applications will use the {@code Instant} class
 * which uses the UTC-SLS mapping from UTC to guarantee 86400 seconds per day.
 * Specialist applications with access to an accurate time-source may find this class useful.
 *
 * <h3>Time-scale</h3>
 * <p>
 * The length of the solar day is the standard way that humans measure time.
 * As the Earth's rotation changes, the length of the day varies.
 * In general, a solar day is slightly longer than 86400 SI seconds.
 * The actual length is not predictable and can only be determined by measurement.
 * The UT1 time-scale captures these measurements.
 * <p>
 * The UTC time-scale is a standard approach to bundle up all the additional fractions
 * of a second from UT1 into whole seconds, known as <i>leap-seconds</i>.
 * A leap-second may be added or removed depending on the Earth's rotational changes.
 * If it is removed, then the relevant date will have no time of {@code 23:59:59}.
 * If it is added, then the relevant date will have an extra second of {@code 23:59:60}.
 * <p>
 * The modern UTC time-scale was introduced in 1972, introducing the concept of whole leap-seconds.
 * Between 1958 and 1972, the definition of UTC was complex, with minor sub-second leaps and
 * alterations to the length of the notional second.
 * <p>
 * This class may be used for instants in the far past and far future.
 * Since some instants will be prior to 1972, it is not strictly an implementation of UTC.
 * Instead, it is a proleptic time-scale based on TAI and equivalent to it since 1972.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class UtcInstant
        implements Comparable<UtcInstant>, Serializable {
    // does not implement Temporal as that would enable methods like
    // Duration.between which gives the wrong answer due to lossy conversion

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 2600294095511836210L;

    /**
     * The Modified Julian Day, from the epoch of 1858-11-17.
     */
    private final long mjDay;
    /**
     * The number of nanoseconds, later along the time-line, from the MJD field.
     * This is always positive and includes leap seconds.
     */
    private final long nanoOfDay;
    /**
     * A cache of the result from {@link #toString()} 
     */
    private transient String toString;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code UtcInstant} from a Modified Julian Day with
     * a nanosecond fraction of day.
     * <p>
     * Modified Julian Day is a simple incrementing count of days where day 0 is 1858-11-17.
     * Nanosecond-of-day is a simple count of nanoseconds from the start of the day
     * including any additional leap-second.
     * This method validates the nanosecond-of-day value against the Modified Julian Day.
     * <p>
     * The nanosecond-of-day value has a valid range from {@code 0} to
     * {@code 86,400,000,000,000 - 1} on most days, and a larger or smaller range
     * on leap-second days.
     * <p>
     * The nanosecond value must be positive even for negative values of Modified
     * Julian Day. One nanosecond before Modified Julian Day zero will be
     * {@code -1} days and the maximum nanosecond value.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @param nanoOfDay  the nanoseconds within the day, including leap seconds
     * @return the UTC instant, not null
     * @throws IllegalArgumentException if nanoOfDay is out of range
     */
    public static UtcInstant ofModifiedJulianDay(long mjDay, long nanoOfDay) {
        UtcRules.system().validateModifiedJulianDay(mjDay, nanoOfDay);
        return new UtcInstant(mjDay, nanoOfDay);
    }

    /**
     * Obtains an instance of {@code UtcInstant} from an {@code Instant}.
     * <p>
     * Converting a UTC-SLS instant to UTC requires leap second rules.
     * This method uses the latest available system rules.
     * <p>
     * Conversion from an {@code Instant} will not be completely accurate near
     * a leap second in accordance with UTC-SLS.
     *
     * @param instant  the instant to convert, not null
     * @return the UTC instant, not null
     * @throws DateTimeException if the range of {@code UtcInstant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static UtcInstant of(Instant instant) {
        return UtcRules.system().convertToUtc(instant);
    }

    /**
     * Obtains an instance of {@code UtcInstant} from a {@code TaiInstant}.
     * <p>
     * Converting a TAI instant to UTC requires leap second rules.
     * This method uses the latest available system rules.
     * <p>
     * The {@code UtcInstant} will represent exactly the same point on the
     * time-line as per the available leap-second rules.
     * If the leap-second rules change then conversion back to TAI may
     * result in a different instant.
     *
     * @param instant  the instant to convert, not null
     * @return the UTC instant, not null
     * @throws DateTimeException if the range of {@code UtcInstant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static UtcInstant of(TaiInstant instant) {
        return UtcRules.system().convertToUtc(instant);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains an instance of {@code UtcInstant} from a text string,
     * such as {@code 2007-12-03T10:15:30.00Z}.
     * <p>
     * The string must represent a valid instant in UTC and is parsed using
     * {@link DateTimeFormatter#ISO_INSTANT} with leap seconds handled.
     *
     * @param text  the text to parse such as "2007-12-03T10:15:30.00Z", not null
     * @return the parsed instant, not null
     * @throws DateTimeParseException if the text cannot be parsed
     * @throws DateTimeException if parsed text represents an invalid leap second
     */
    @FromString
    public static UtcInstant parse(CharSequence text) {
        TemporalAccessor parsed = DateTimeFormatter.ISO_INSTANT.parse(text);
        long epochSecond = parsed.getLong(INSTANT_SECONDS);
        long nanoOfSecond = parsed.getLong(NANO_OF_SECOND);
        boolean leap = parsed.query(DateTimeFormatter.parsedLeapSecond());
        long epochDay = Math.floorDiv(epochSecond, SECS_PER_DAY);
        long mjd = epochDay + OFFSET_MJD_EPOCH;
        long nanoOfDay = Math.floorMod(epochSecond, SECS_PER_DAY) * NANOS_PER_SECOND + nanoOfSecond;
        if (leap) {
            nanoOfDay += NANOS_PER_SECOND;
        }
        return UtcInstant.ofModifiedJulianDay(mjd, nanoOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @param nanoOfDay  the nanoseconds within the day, including leap seconds
     */
    private UtcInstant(long mjDay, long nanoOfDay) {
        super();
        this.mjDay = mjDay;
        this.nanoOfDay = nanoOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the Modified Julian Day (MJD).
     * <p>
     * The Modified Julian Day is a simple incrementing count of days where day 0 is 1858-11-17.
     * The nanosecond part of the day is returned by {@code getNanosOfDay}.
     * The day varies in length, being one second longer on a leap day.
     *
     * @return the Modified Julian Day based on the epoch 1858-11-17
     */
    public long getModifiedJulianDay() {
        return mjDay;
    }

    /**
     * Returns a copy of this {@code UtcInstant} with the Modified Julian Day (MJD) altered.
     * <p>
     * The Modified Julian Day is a simple incrementing count of days where day 0 is 1858-11-17.
     * The nanosecond part of the day is returned by {@code getNanosOfDay}.
     * The day varies in length, being one second longer on a leap day.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @return a {@code UtcInstant} based on this instant with the requested day, not null
     * @throws DateTimeException if nanoOfDay becomes invalid
     */
    public UtcInstant withModifiedJulianDay(long mjDay) {
        return UtcInstant.ofModifiedJulianDay(mjDay, nanoOfDay);
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start
     * of the Modified Julian Day.
     * <p>
     * The nanosecond-of-day value measures the total number of nanoseconds within
     * the day from the start of the day returned by {@code getModifiedJulianDay}.
     * This value will include any additional leap seconds.
     *
     * @return the nanoseconds within the day, including leap seconds
     */
    public long getNanoOfDay() {
        return nanoOfDay;
    }

    /**
     * Returns a copy of this {@code UtcInstant} with the nano-of-day altered.
     * <p>
     * The nanosecond-of-day value measures the total number of nanoseconds within
     * the day from the start of the day returned by {@code getModifiedJulianDay}.
     * This value will include any additional leap seconds.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfDay  the nanoseconds within the day, including leap seconds
     * @return a {@code UtcInstant} based on this instant with the requested nano-of-day, not null
     * @throws DateTimeException if the nanoOfDay value is invalid
     */
    public UtcInstant withNanoOfDay(long nanoOfDay) {
        return UtcInstant.ofModifiedJulianDay(mjDay, nanoOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the instant is within a leap second.
     * <p>
     * This method returns true when an accurate clock would return a seconds
     * field of 60.
     *
     * @return true if this instant is within a leap second
     */
    public boolean isLeapSecond() {
        return nanoOfDay >= SECS_PER_DAY * NANOS_PER_SECOND;
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
     * @return a {@code UtcInstant} with the duration added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public UtcInstant plus(Duration duration) {
        return UtcInstant.of(toTaiInstant().plus(duration));
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
     * @return a {@code UtcInstant} with the duration subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public UtcInstant minus(Duration duration) {
        return UtcInstant.of(toTaiInstant().minus(duration));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the duration between this instant and the specified instant.
     * <p>
     * This calculates the duration between this instant and another based on
     * the UTC time-scale. Any leap seconds that occur will be included in the duration.
     * Adding the duration to this instant using {@link #plus} will always result
     * in an instant equal to the specified instant.
     *
     * @param utcInstant  the instant to calculate the duration until, not null
     * @return the duration until the specified instant, may be negative, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Duration durationUntil(UtcInstant utcInstant) {
        TaiInstant thisTAI = toTaiInstant();
        TaiInstant otherTAI = utcInstant.toTaiInstant();
        return thisTAI.durationUntil(otherTAI);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to an {@code Instant}.
     * <p>
     * Converting a UTC instant to UTC-SLS requires leap second rules.
     * This method uses the latest available system rules.
     * <p>
     * Conversion to an {@code Instant} will not be completely accurate near
     * a leap second in accordance with UTC-SLS.
     *
     * @return an {@code Instant} representing the best approximation of this instant, not null
     * @throws DateTimeException if the range  of {@code Instant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Instant toInstant() {
        return UtcRules.system().convertToInstant(this);
    }

    /**
     * Converts this instant to a {@code TaiInstant}.
     * <p>
     * Converting a UTC instant to TAI requires leap second rules.
     * This method uses the latest available system rules.
     * <p>
     * The {@code TaiInstant} will represent exactly the same point on the
     * time-line as per the available leap-second rules.
     * If the leap-second rules change then conversion back to UTC may
     * result in a different instant.
     *
     * @return a {@code TaiInstant} representing the same instant, not null
     * @throws DateTimeException if the range of {@code TaiInstant} is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public TaiInstant toTaiInstant() {
        return UtcRules.system().convertToTai(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instant to another based on the time-line.
     * <p>
     * The comparison is based first on the Modified Julian Day, then on the nano-of-day.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(UtcInstant otherInstant) {
        int cmp = Long.compare(mjDay, otherInstant.mjDay);
        if (cmp != 0) {
            return cmp;
        }
        return Long.compare(nanoOfDay, otherInstant.nanoOfDay);
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
    public boolean isAfter(UtcInstant otherInstant) {
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
    public boolean isBefore(UtcInstant otherInstant) {
        return compareTo(otherInstant) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instant is equal to the specified {@code UtcInstant}.
     * <p>
     * The comparison is based on the Modified Julian Day, then on the nano-of-day.
     *
     * @param otherInstant  the other instant, null returns false
     * @return true if the other instant is equal to this one
     */
    @Override
    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (otherInstant instanceof UtcInstant) {
            UtcInstant other = (UtcInstant) otherInstant;
            return this.mjDay == other.mjDay &&
                    this.nanoOfDay == other.nanoOfDay;
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
        return ((int) (mjDay ^ (mjDay >>> 32))) + 51 * ((int) (nanoOfDay ^ (nanoOfDay >>> 32)));
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this instant.
     * <p>
     * The string is formatted using ISO-8601.
     * The output includes seconds, 9 nanosecond digits and a trailing 'Z'.
     * The time-of-day will be 23:59:60 during a positive leap second.
     *
     * @return a representation of this instant, not null
     */
    @Override
    @ToString
    public String toString() {
        // racy single-check idiom
        String currentStringValue = toString;
        if (currentStringValue == null) {
            currentStringValue = buildToString();
            toString = currentStringValue;
        }
        return currentStringValue;
    }

    // produces the string representation of this instant
    private String buildToString() {
        LocalDate date = LocalDate.MAX.with(JulianFields.MODIFIED_JULIAN_DAY, mjDay); // TODO: capacity/import issues
        StringBuilder buf = new StringBuilder(30);
        int sod = (int) (nanoOfDay / NANOS_PER_SECOND);
        int hourValue = sod / (60 * 60);
        int minuteValue = (sod / 60) % 60;
        int secondValue = sod % 60;
        int nanoValue = (int) (nanoOfDay % NANOS_PER_SECOND);
        if (hourValue == 24) {
            hourValue = 23;
            minuteValue = 59;
            secondValue = 60;
        }
        buf.append(date).append('T')
                .append(hourValue < 10 ? "0" : "").append(hourValue)
                .append(minuteValue < 10 ? ":0" : ":").append(minuteValue)
                .append(secondValue < 10 ? ":0" : ":").append(secondValue);
        if (nanoValue > 0) {
            buf.append('.');
            if (nanoValue % 1000_000 == 0) {
                buf.append(Integer.toString((nanoValue / 1000_000) + 1000).substring(1));
            } else if (nanoValue % 1000 == 0) {
                buf.append(Integer.toString((nanoValue / 1000) + 1000_000).substring(1));
            } else {
                buf.append(Integer.toString((nanoValue) + 1000_000_000).substring(1));
            }
        }
        buf.append('Z');
        return buf.toString();
    }

}
