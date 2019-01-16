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

import static java.time.temporal.ChronoUnit.MINUTES;

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
 * A minute-based amount of time, such as '8 minutes'.
 * <p>
 * This class models a quantity or amount of time in terms of minutes.
 * It is a type-safe way of representing a number of minutes in an application.
 * <p>
 * The model is of a directed amount, meaning that the amount may be negative.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class Minutes
        implements TemporalAmount, Comparable<Minutes>, Serializable {

    /**
     * A constant for zero minutes.
     */
    public static final Minutes ZERO = new Minutes(0);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 2602801843170589407L;

    /**
     * The number of minutes per day.
     */
    private static final int MINUTES_PER_DAY = 24 * 60;
    /**
     * The number of minutes per hour.
     */
    private static final int MINUTES_PER_HOUR = 60;

    /**
     * The pattern for parsing.
     */
    private static final Pattern PATTERN =
            Pattern.compile("([-+]?)P"
                    + "(?:([-+]?[0-9]+)D)?"
                    + "(?:T"
                    + "(?:([-+]?[0-9]+)H)?"
                    + "(?:([-+]?[0-9]+)M)?)?", Pattern.CASE_INSENSITIVE);

    /**
     * The number of minutes.
     */
    private final int minutes;

    /**
     * Obtains a {@code Minutes} representing a number of minutes.
     * <p>
     * The resulting amount will have the specified minutes.
     *
     * @param minutes  the number of minutes, positive or negative
     * @return the number of minutes, not null
     */
    public static Minutes of(int minutes) {
        if (minutes == 0) {
            return ZERO;
        }
        return new Minutes(minutes);
    }

    /**
     * Obtains a {@code Minutes} representing the number of minutes
     * equivalent to a number of hours.
     * <p>
     * The resulting amount will be minute-based, with the number of minutes
     * equal to the number of hours multiplied by 60.
     *
     * @param hours  the number of hours, positive or negative
     * @return the amount with the input hours converted to minutes, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Minutes ofHours(int hours) {
        if (hours == 0) {
            return ZERO;
        }
        return new Minutes(Math.multiplyExact(hours, MINUTES_PER_HOUR));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Minutes} from a temporal amount.
     * <p>
     * This obtains an instance based on the specified amount.
     * A {@code TemporalAmount} represents an amount of time, which may be
     * date-based or time-based, which this factory extracts to a {@code Minutes}.
     * <p>
     * The result is calculated by looping around each unit in the specified amount.
     * Each amount is converted to minutes using {@link Temporals#convertAmount}.
     * If the conversion yields a remainder, an exception is thrown.
     * If the amount is zero, the unit is ignored.
     *
     * @param amount  the temporal amount to convert, not null
     * @return the equivalent amount, not null
     * @throws DateTimeException if unable to convert to a {@code Minutes}
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Minutes from(TemporalAmount amount) {
        if (amount instanceof Minutes) {
            return (Minutes) amount;
        }
        Objects.requireNonNull(amount, "amount");
        int minutes = 0;
        for (TemporalUnit unit : amount.getUnits()) {
            long value = amount.get(unit);
            if (value != 0) {
                long[] converted = Temporals.convertAmount(value, unit, MINUTES);
                if (converted[1] != 0) {
                    throw new DateTimeException(
                            "Amount could not be converted to a whole number of minutes: " + value + " " + unit);
                }
                minutes = Math.addExact(minutes, Math.toIntExact(converted[0]));
            }
        }
        return of(minutes);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Minutes} from a text string such as {@code PTnM}.
     * <p>
     * This will parse the string produced by {@code toString()} and other
     * related formats based on ISO-8601 {@code PnDTnHnM}.
     * <p>
     * The string starts with an optional sign, denoted by the ASCII negative
     * or positive symbol. If negative, the whole amount is negated.
     * The ASCII letter "P" is next in upper or lower case.
     * There are three sections consisting of a number and a suffix.
     * There is one section for days suffixed by "D",
     * followed by one section for hours suffixed by "H",
     * followed by one section for minutes suffixed by "M".
     * At least one section must be present.
     * If the hours or minutes section is present it must be prefixed by "T".
     * If the hours or minutes section is omitted the "T" must be omitted.
     * Letters must be in ASCII upper or lower case.
     * The number part of each section must consist of ASCII digits.
     * The number may be prefixed by the ASCII negative or positive symbol.
     * The number must parse to an {@code int}.
     * <p>
     * The leading plus/minus sign, and negative values for days, hours and
     * minutes are not part of the ISO-8601 standard.
     * <p>
     * For example, the following are valid inputs:
     * <pre>
     *   "PT2M"            -- Minutes.of(2)
     *   "PT-2M"           -- Minutes.of(-2)
     *   "-PT2M"           -- Minutes.of(-2)
     *   "-PT-2M"          -- Minutes.of(2)
     *   "PT3H"            -- Minutes.of(3 * 60)
     *   "PT3H-2M"         -- Minutes.of(3 * 60 - 2)
     *   "P3D"             -- Minutes.of(3 * 24 * 60)
     *   "P3DT2M"          -- Minutes.of(3 * 24 * 60 + 2)
     * </pre>
     *
     * @param text  the text to parse, not null
     * @return the parsed period, not null
     * @throws DateTimeParseException if the text cannot be parsed to a period
     */
    @FromString
    public static Minutes parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches()) {
            int negate = "-".equals(matcher.group(1)) ? -1 : 1;
            String daysStr = matcher.group(2);
            String hoursStr = matcher.group(3);
            String minutesStr = matcher.group(4);
            if (daysStr != null || hoursStr != null || minutesStr != null) {
                int minutes = 0;
                if (minutesStr != null) {
                    try {
                        minutes = Integer.parseInt(minutesStr);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to Minutes, non-numeric minutes", text, 0, ex);
                    }
                }
                if (hoursStr != null) {
                    try {
                        int hoursAsMins = Math.multiplyExact(Integer.parseInt(hoursStr), MINUTES_PER_HOUR);
                        minutes = Math.addExact(minutes, hoursAsMins);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to Minutes, non-numeric hours", text, 0, ex);
                    }
                }
                if (daysStr != null) {
                    try {
                        int daysAsMins = Math.multiplyExact(Integer.parseInt(daysStr), MINUTES_PER_DAY);
                        minutes = Math.addExact(minutes, daysAsMins);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to Minutes, non-numeric days", text, 0, ex);
                    }
                }
                return of(Math.multiplyExact(minutes, negate));
            }
        }
        throw new DateTimeParseException("Text cannot be parsed to Minutes", text, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Minutes} consisting of the number of minutes between two temporals.
     * <p>
     * The start temporal is included, but the end temporal is not.
     * The result of this method can be negative if the end is before the start.
     *
     * @param startInclusive  the start temporal, inclusive, not null
     * @param endExclusive  the end temporal, exclusive, not null
     * @return the number of minutes between the start and end temporals, not null
     */
    public static Minutes between(Temporal startInclusive, Temporal endExclusive) {
        return of(Math.toIntExact(MINUTES.between(startInclusive, endExclusive)));
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific number of minutes.
     *
     * @param minutes  the amount of minutes
     */
    private Minutes(int minutes) {
        this.minutes = minutes;
    }

    /**
     * Resolves singletons.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return Minutes.of(minutes);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the requested unit.
     * <p>
     * This returns a value for the supported unit - {@link ChronoUnit#MINUTES MINUTES}.
     * All other units throw an exception.
     *
     * @param unit  the {@code TemporalUnit} for which to return the value
     * @return the long value of the unit
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     */
    @Override
    public long get(TemporalUnit unit) {
        if (unit == MINUTES) {
            return minutes;
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    /**
     * Gets the set of units supported by this amount.
     * <p>
     * The single supported unit is {@link ChronoUnit#MINUTES MINUTES}.
     * <p>
     * This set can be used in conjunction with {@link #get(TemporalUnit)} to
     * access the entire state of the amount.
     *
     * @return a list containing the minutes unit, not null
     */
    @Override
    public List<TemporalUnit> getUnits() {
        return Collections.singletonList(MINUTES);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Gets the number of minutes in this amount.
     *
     * @return the number of minutes
     */
    public int getAmount() {
        return minutes;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this amount with the specified amount added.
     * <p>
     * The parameter is converted using {@link Minutes#from(TemporalAmount)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, not null
     * @return a {@code Minutes} based on this instance with the requested amount added, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Minutes plus(TemporalAmount amountToAdd) {
        return plus(Minutes.from(amountToAdd).getAmount());
    }

    /**
     * Returns a copy of this amount with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the amount of minutes to add, may be negative
     * @return a {@code Minutes} based on this instance with the requested amount added, not null
     * @throws ArithmeticException if the result overflows an int
     */
    public Minutes plus(int minutes) {
        if (minutes == 0) {
            return this;
        }
        return of(Math.addExact(this.minutes, minutes));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this amount with the specified amount subtracted.
     * <p>
     * The parameter is converted using {@link Minutes#from(TemporalAmount)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, not null
     * @return a {@code Minutes} based on this instance with the requested amount subtracted, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Minutes minus(TemporalAmount amountToAdd) {
        return minus(Minutes.from(amountToAdd).getAmount());
    }

    /**
     * Returns a copy of this amount with the specified number of minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the amount of minutes to add, may be negative
     * @return a {@code Minutes} based on this instance with the requested amount subtracted, not null
     * @throws ArithmeticException if the result overflows an int
     */
    public Minutes minus(int minutes) {
        if (minutes == 0) {
            return this;
        }
        return of(Math.subtractExact(this.minutes, minutes));
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
    public Minutes multipliedBy(int scalar) {
        if (scalar == 1) {
            return this;
        }
        return of(Math.multiplyExact(minutes, scalar));
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
    public Minutes dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return of(minutes / divisor);
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
    public Minutes negated() {
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
    public Minutes abs() {
        return minutes < 0 ? negated() : this;
    }

    //-------------------------------------------------------------------------
    /**
     * Gets the number of minutes as a {@code Duration}.
     * <p>
     * This returns a duration with the same number of minutes.
     *
     * @return the equivalent duration, not null
     */
    public Duration toDuration() {
        return Duration.ofMinutes(minutes);
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
     * @throws UnsupportedTemporalTypeException if the MINUTES unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal addTo(Temporal temporal) {
        if (minutes != 0) {
            temporal = temporal.plus(minutes, MINUTES);
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
     * @throws UnsupportedTemporalTypeException if the MINUTES unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal subtractFrom(Temporal temporal) {
        if (minutes != 0) {
            temporal = temporal.minus(minutes, MINUTES);
        }
        return temporal;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this amount to the specified {@code Minutes}.
     * <p>
     * The comparison is based on the total length of the amounts.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param otherAmount  the other amount, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(Minutes otherAmount) {
        int thisValue = this.minutes;
        int otherValue = otherAmount.minutes;
        return Integer.compare(thisValue, otherValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this amount is equal to the specified {@code Minutes}.
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
        if (otherAmount instanceof Minutes) {
            Minutes other = (Minutes) otherAmount;
            return this.minutes == other.minutes;
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
        return minutes;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of minutes.
     * This will be in the format 'PTnM' where n is the number of minutes.
     *
     * @return the number of minutes in ISO-8601 string format
     */
    @Override
    @ToString
    public String toString() {
        return "PT" + minutes + "M";
    }

}
