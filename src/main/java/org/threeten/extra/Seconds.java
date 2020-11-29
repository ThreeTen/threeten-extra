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

import static java.time.temporal.ChronoUnit.SECONDS;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * A second-based amount of time, such as '8 seconds'.
 * <p>
 * This class models a quantity or amount of time in terms of seconds.
 * It is a type-safe way of representing a number of seconds in an application.
 * Note that {@link Duration} also models time in terms of seconds, but that
 * class allows nanoseconds, which this class does not.
 * <p>
 * The model is of a directed amount, meaning that the amount may be negative.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class Seconds
        implements TemporalAmount, Comparable<Seconds>, Serializable {

    /**
     * A constant for zero seconds.
     */
    public static final Seconds ZERO = new Seconds(0);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 2602801843170589407L;

    /**
     * The number of seconds per day.
     */
    private static final int SECONDS_PER_DAY = 86400;
    /**
     * The number of seconds per hour.
     */
    private static final int SECONDS_PER_HOUR = 3600;
    /**
     * The number of seconds per minute.
     */
    private static final int SECONDS_PER_MINUTE = 60;

    /**
     * The pattern for parsing.
     */
    private static final Pattern PATTERN =
            Pattern.compile("([-+]?)P"
                    + "(?:([-+]?[0-9]+)D)?"
                    + "(?:T"
                    + "(?:([-+]?[0-9]+)H)?"
                    + "(?:([-+]?[0-9]+)M)?"
                    + "(?:([-+]?[0-9]+)S)?)?", Pattern.CASE_INSENSITIVE);

    /**
     * The number of seconds.
     */
    private final int seconds;

    /**
     * Obtains a {@code Seconds} representing a number of seconds.
     * <p>
     * The resulting amount will have the specified seconds.
     *
     * @param seconds  the number of seconds, positive or negative
     * @return the number of seconds, not null
     */
    public static Seconds of(int seconds) {
        if (seconds == 0) {
            return ZERO;
        }
        return new Seconds(seconds);
    }

    /**
     * Obtains a {@code Seconds} representing the number of seconds
     * equivalent to a number of hours.
     * <p>
     * The resulting amount will be second-based, with the number of seconds
     * equal to the number of hours multiplied by 3600.
     *
     * @param hours  the number of hours, positive or negative
     * @return the amount with the input hours converted to seconds, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Seconds ofHours(int hours) {
        if (hours == 0) {
            return ZERO;
        }
        return new Seconds(Math.multiplyExact(hours, SECONDS_PER_HOUR));
    }

    /**
     * Obtains a {@code Seconds} representing the number of seconds
     * equivalent to a number of hours.
     * <p>
     * The resulting amount will be second-based, with the number of seconds
     * equal to the number of minutes multiplied by 60.
     *
     * @param minutes  the number of minutes, positive or negative
     * @return the amount with the input minutes converted to seconds, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Seconds ofMinutes(int minutes) {
        if (minutes == 0) {
            return ZERO;
        }
        return new Seconds(Math.multiplyExact(minutes, SECONDS_PER_MINUTE));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Seconds} from a temporal amount.
     * <p>
     * This obtains an instance based on the specified amount.
     * A {@code TemporalAmount} represents an amount of time, which may be
     * date-based or time-based, which this factory extracts to a {@code Seconds}.
     * <p>
     * The result is calculated by looping around each unit in the specified amount.
     * Each amount is converted to seconds using {@link Temporals#convertAmount}.
     * If the conversion yields a remainder, an exception is thrown.
     * If the amount is zero, the unit is ignored.
     *
     * @param amount  the temporal amount to convert, not null
     * @return the equivalent amount, not null
     * @throws DateTimeException if unable to convert to a {@code Seconds}
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Seconds from(TemporalAmount amount) {
        if (amount instanceof Seconds) {
            return (Seconds) amount;
        }
        Objects.requireNonNull(amount, "amount");
        int seconds = 0;
        for (TemporalUnit unit : amount.getUnits()) {
            long value = amount.get(unit);
            if (value != 0) {
                long[] converted = Temporals.convertAmount(value, unit, SECONDS);
                if (converted[1] != 0) {
                    throw new DateTimeException(
                            "Amount could not be converted to a whole number of seconds: " + value + " " + unit);
                }
                seconds = Math.addExact(seconds, Math.toIntExact(converted[0]));
            }
        }
        return of(seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Seconds} from a text string such as {@code PTnS}.
     * <p>
     * This will parse the string produced by {@code toString()} and other
     * related formats based on ISO-8601 {@code PnDTnHnMnS}.
     * <p>
     * The string starts with an optional sign, denoted by the ASCII negative
     * or positive symbol. If negative, the whole amount is negated.
     * The ASCII letter "P" is next in upper or lower case.
     * There are four sections consisting of a number and a suffix.
     * There is one section for days suffixed by "D",
     * followed by one section for hours suffixed by "H",
     * followed by one section for minutes suffixed by "M",
     * followed by one section for seconds suffixed by "S".
     * At least one section must be present.
     * If the hours, minutes or seconds section is present it must be prefixed by "T".
     * If the hours, minutes or seconds section is omitted the "T" must be omitted.
     * Letters must be in ASCII upper or lower case.
     * The number part of each section must consist of ASCII digits.
     * The number may be prefixed by the ASCII negative or positive symbol.
     * The number must parse to an {@code int}.
     * <p>
     * The leading plus/minus sign, and negative values for days, hours, minutes
     * and seconds are not part of the ISO-8601 standard.
     * <p>
     * For example, the following are valid inputs:
     * <pre>
     *   "PT2S"            -- Seconds.of(2)
     *   "PT-2S"           -- Seconds.of(-2)
     *   "-PT2S"           -- Seconds.of(-2)
     *   "-PT-2S"          -- Seconds.of(2)
     *   "PT3S"            -- Seconds.of(3 * 60)
     *   "PT3H-2M7S"       -- Seconds.of(3 * 3600 - 2 * 60 + 7)
     *   "P2D"             -- Seconds.of(2 * 86400)
     *   "P2DT3H"          -- Seconds.of(2 * 86400 + 3 * 3600)
     * </pre>
     *
     * @param text  the text to parse, not null
     * @return the parsed period, not null
     * @throws DateTimeParseException if the text cannot be parsed to a period
     */
    @FromString
    public static Seconds parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches()) {
            int negate = "-".equals(matcher.group(1)) ? -1 : 1;
            String daysStr = matcher.group(2);
            String hoursStr = matcher.group(3);
            String minutesStr = matcher.group(4);
            String secondsStr = matcher.group(5);
            if (daysStr != null || hoursStr != null || minutesStr != null || secondsStr != null) {
                int seconds = 0;
                if (secondsStr != null) {
                    try {
                        seconds = Integer.parseInt(secondsStr);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to Seconds, non-numeric seconds", text, 0, ex);
                    }
                }
                if (minutesStr != null) {
                    try {
                        int minutesAsSecs = Math.multiplyExact(Integer.parseInt(minutesStr), SECONDS_PER_MINUTE);
                        seconds = Math.addExact(seconds, minutesAsSecs);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to Seconds, non-numeric minutes", text, 0, ex);
                    }
                }
                if (hoursStr != null) {
                    try {
                        int hoursAsSecs = Math.multiplyExact(Integer.parseInt(hoursStr), SECONDS_PER_HOUR);
                        seconds = Math.addExact(seconds, hoursAsSecs);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to Seconds, non-numeric hours", text, 0, ex);
                    }
                }
                if (daysStr != null) {
                    try {
                        int daysAsSecs = Math.multiplyExact(Integer.parseInt(daysStr), SECONDS_PER_DAY);
                        seconds = Math.addExact(seconds, daysAsSecs);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to Seconds, non-numeric days", text, 0, ex);
                    }
                }
                return of(Math.multiplyExact(seconds, negate));
            }
        }
        throw new DateTimeParseException("Text cannot be parsed to Seconds", text, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Seconds} consisting of the number of seconds between two temporals.
     * <p>
     * The start temporal is included, but the end temporal is not.
     * The result of this method can be negative if the end is before the start.
     *
     * @param startInclusive  the start temporal, inclusive, not null
     * @param endExclusive  the end temporal, exclusive, not null
     * @return the number of seconds between the start and end temporals, not null
     */
    public static Seconds between(Temporal startInclusive, Temporal endExclusive) {
        return of(Math.toIntExact(SECONDS.between(startInclusive, endExclusive)));
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific number of seconds.
     *
     * @param seconds  the amount of seconds
     */
    private Seconds(int seconds) {
        this.seconds = seconds;
    }

    /**
     * Resolves singletons.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return Seconds.of(seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the requested unit.
     * <p>
     * This returns a value for the supported unit - {@link ChronoUnit#SECONDS SECONDS}.
     * All other units throw an exception.
     *
     * @param unit  the {@code TemporalUnit} for which to return the value
     * @return the long value of the unit
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     */
    @Override
    public long get(TemporalUnit unit) {
        if (unit == SECONDS) {
            return seconds;
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    /**
     * Gets the set of units supported by this amount.
     * <p>
     * The single supported unit is {@link ChronoUnit#SECONDS SECONDS}.
     * <p>
     * This set can be used in conjunction with {@link #get(TemporalUnit)} to
     * access the entire state of the amount.
     *
     * @return a list containing the seconds unit, not null
     */
    @Override
    public List<TemporalUnit> getUnits() {
        return Collections.singletonList(SECONDS);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds in this amount.
     *
     * @return the number of seconds
     */
    public int getAmount() {
        return seconds;
    }

    /**
     * Checks if the amount is negative.
     *
     * @return true if the amount is negative, false if the amount is zero or positive
     */
    public boolean isNegative() {
        return getAmount() < 0;
    }

    /**
     * Checks if the amount is zero.
     *
     * @return true if the amount is zero, false if not
     */
    public boolean isZero() {
        return getAmount() == 0;
    }

    /**
     * Checks if the amount is positive.
     *
     * @return true if the amount is positive, false if the amount is zero or negative
     */
    public boolean isPositive() {
        return getAmount() > 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this amount with the specified amount added.
     * <p>
     * The parameter is converted using {@link Seconds#from(TemporalAmount)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, not null
     * @return a {@code Seconds} based on this instance with the requested amount added, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Seconds plus(TemporalAmount amountToAdd) {
        return plus(Seconds.from(amountToAdd).getAmount());
    }

    /**
     * Returns a copy of this amount with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the amount of seconds to add, may be negative
     * @return a {@code Seconds} based on this instance with the requested amount added, not null
     * @throws ArithmeticException if the result overflows an int
     */
    public Seconds plus(int seconds) {
        if (seconds == 0) {
            return this;
        }
        return of(Math.addExact(this.seconds, seconds));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this amount with the specified amount subtracted.
     * <p>
     * The parameter is converted using {@link Seconds#from(TemporalAmount)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount to subtract, not null
     * @return a {@code Seconds} based on this instance with the requested amount subtracted, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Seconds minus(TemporalAmount amountToSubtract) {
        return minus(Seconds.from(amountToSubtract).getAmount());
    }

    /**
     * Returns a copy of this amount with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the amount of seconds to add, may be negative
     * @return a {@code Seconds} based on this instance with the requested amount subtracted, not null
     * @throws ArithmeticException if the result overflows an int
     */
    public Seconds minus(int seconds) {
        if (seconds == 0) {
            return this;
        }
        return of(Math.subtractExact(this.seconds, seconds));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an instance with the amount multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return the amount multiplied by the specified scalar, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Seconds multipliedBy(int scalar) {
        if (scalar == 1) {
            return this;
        }
        return of(Math.multiplyExact(seconds, scalar));
    }

    /**
     * Returns an instance with the amount divided by the specified divisor.
     * <p>
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the amount to divide by, may be negative
     * @return the amount divided by the specified divisor, not null
     * @throws ArithmeticException if the divisor is zero
     */
    public Seconds dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return of(seconds / divisor);
    }

    /**
     * Returns an instance with the amount negated.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return the negated amount, not null
     * @throws ArithmeticException if numeric overflow occurs, which only happens if
     *  the amount is {@code Long.MIN_VALUE}
     */
    public Seconds negated() {
        return multipliedBy(-1);
    }

    /**
     * Returns a copy of this duration with a positive length.
     * <p>
     * This method returns a positive duration by effectively removing the sign from any negative total length.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return the absolute amount, not null
     * @throws ArithmeticException if numeric overflow occurs, which only happens if
     *  the amount is {@code Long.MIN_VALUE}
     */
    public Seconds abs() {
        return seconds < 0 ? negated() : this;
    }

    //-------------------------------------------------------------------------
    /**
     * Gets the number of seconds as a {@code Duration}.
     * <p>
     * This returns a duration with the same number of seconds.
     *
     * @return the equivalent duration, not null
     */
    public Duration toDuration() {
        return Duration.ofSeconds(seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds this amount to the specified temporal object.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with this amount added.
     * <p>
     * In most cases, it is clearer to reverse the calling pattern by using
     * {@link Temporal#plus(TemporalAmount)}.
     * <pre>
     *   // these two lines are equivalent, but the second approach is recommended
     *   dateTime = thisAmount.addTo(dateTime);
     *   dateTime = dateTime.plus(thisAmount);
     * </pre>
     * <p>
     * Only non-zero amounts will be added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param temporal  the temporal object to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws DateTimeException if unable to add
     * @throws UnsupportedTemporalTypeException if the SECONDS unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal addTo(Temporal temporal) {
        if (seconds != 0) {
            temporal = temporal.plus(seconds, SECONDS);
        }
        return temporal;
    }

    /**
     * Subtracts this amount from the specified temporal object.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with this amount subtracted.
     * <p>
     * In most cases, it is clearer to reverse the calling pattern by using
     * {@link Temporal#minus(TemporalAmount)}.
     * <pre>
     *   // these two lines are equivalent, but the second approach is recommended
     *   dateTime = thisAmount.subtractFrom(dateTime);
     *   dateTime = dateTime.minus(thisAmount);
     * </pre>
     * <p>
     * Only non-zero amounts will be subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param temporal  the temporal object to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws DateTimeException if unable to subtract
     * @throws UnsupportedTemporalTypeException if the SECONDS unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal subtractFrom(Temporal temporal) {
        if (seconds != 0) {
            temporal = temporal.minus(seconds, SECONDS);
        }
        return temporal;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this amount to the specified {@code Seconds}.
     * <p>
     * The comparison is based on the total length of the amounts.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param otherAmount  the other amount, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(Seconds otherAmount) {
        int thisValue = this.seconds;
        int otherValue = otherAmount.seconds;
        return Integer.compare(thisValue, otherValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this amount is equal to the specified {@code Seconds}.
     * <p>
     * The comparison is based on the total length of the durations.
     *
     * @param otherAmount  the other amount, null returns false
     * @return true if the other amount is equal to this one
     */
    @Override
    public boolean equals(Object otherAmount) {
        if (this == otherAmount) {
            return true;
        }
        if (otherAmount instanceof Seconds) {
            Seconds other = (Seconds) otherAmount;
            return this.seconds == other.seconds;
        }
        return false;
    }

    /**
     * A hash code for this amount.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return seconds;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of seconds.
     * This will be in the format 'PTnS' where n is the number of seconds.
     *
     * @return the number of seconds in ISO-8601 string format
     */
    @Override
    @ToString
    public String toString() {
        return "PT" + seconds + "S";
    }

}
