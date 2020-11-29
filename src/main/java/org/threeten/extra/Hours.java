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
 * A hour-based amount of time, such as '4 hours'.
 * <p>
 * This class models a quantity or amount of time in terms of hours.
 * It is a type-safe way of representing a number of hours in an application.
 * <p>
 * The model is of a directed amount, meaning that the amount may be negative.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class Hours
        implements TemporalAmount, Comparable<Hours>, Serializable {

    /**
     * A constant for zero hours.
     */
    public static final Hours ZERO = new Hours(0);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -8494096666041369608L;

    /**
     * The number of hours per day.
     */
    private static final int HOURS_PER_DAY = 24;

    /**
     * The pattern for parsing.
     */
    private static final Pattern PATTERN =
            Pattern.compile("([-+]?)P"
                    + "(?:([-+]?[0-9]+)D)?"
                    + "(?:T"
                    + "(?:([-+]?[0-9]+)H)?)?", Pattern.CASE_INSENSITIVE);

    /**
     * The number of hours.
     */
    private final int hours;

    /**
     * Obtains an {@code Hours} representing a number of hours.
     * <p>
     * The resulting amount will have the specified hours.
     *
     * @param hours  the number of hours, positive or negative
     * @return the number of hours, not null
     */
    public static Hours of(int hours) {
        if (hours == 0) {
            return ZERO;
        } else {
            return new Hours(hours);
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Hours} from a temporal amount.
     * <p>
     * This obtains an instance based on the specified amount.
     * A {@code TemporalAmount} represents an amount of time, which may be
     * date-based or time-based, which this factory extracts to a {@code Hours}.
     * <p>
     * The result is calculated by looping around each unit in the specified amount.
     * Each amount is converted to hours using {@link Temporals#convertAmount}.
     * If the conversion yields a remainder, an exception is thrown.
     * If the amount is zero, the unit is ignored.
     *
     * @param amount  the temporal amount to convert, not null
     * @return the equivalent amount, not null
     * @throws DateTimeException if unable to convert to a {@code Hours}
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Hours from(TemporalAmount amount) {
        if (amount instanceof Hours) {
            return (Hours) amount;
        }
        Objects.requireNonNull(amount, "amount");
        int hours = 0;
        for (TemporalUnit unit : amount.getUnits()) {
            long value = amount.get(unit);
            if (value != 0) {
                long[] converted = Temporals.convertAmount(value, unit, HOURS);
                if (converted[1] != 0) {
                    throw new DateTimeException(
                            "Amount could not be converted to a whole number of hours: " + value + " " + unit);
                }
                hours = Math.addExact(hours, Math.toIntExact(converted[0]));
            }
        }
        return of(hours);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Hours} from a text string such as {@code PTnH}.
     * <p>
     * This will parse the string produced by {@code toString()} and other
     * related formats based on ISO-8601 {@code PnDTnH}.
     * <p>
     * The string starts with an optional sign, denoted by the ASCII negative
     * or positive symbol. If negative, the whole amount is negated.
     * The ASCII letter "P" is next in upper or lower case.
     * There are two sections consisting of a number and a suffix.
     * There is one section for days suffixed by "D",
     * followed by one section for hours suffixed by "H".
     * At least one section must be present.
     * If the hours section is present it must be prefixed by "T".
     * If the hours section is omitted the "T" must be omitted.
     * Letters must be in ASCII upper or lower case.
     * The number part of each section must consist of ASCII digits.
     * The number may be prefixed by the ASCII negative or positive symbol.
     * The number must parse to an {@code int}.
     * <p>
     * The leading plus/minus sign, and negative values for days and hours
     * are not part of the ISO-8601 standard.
     * <p>
     * For example, the following are valid inputs:
     * <pre>
     *   "PT2H"            -- Hours.of(2)
     *   "PT-HM"           -- Hours.of(-2)
     *   "-PT2H"           -- Hours.of(-2)
     *   "-PT-2H"          -- Hours.of(2)
     *   "P3D"             -- Hours.of(3 * 24)
     *   "P3DT2H"          -- Hours.of(3 * 24 + 2)
     * </pre>
     *
     * @param text  the text to parse, not null
     * @return the parsed period, not null
     * @throws DateTimeParseException if the text cannot be parsed to a period
     */
    @FromString
    public static Hours parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches()) {
            int negate = "-".equals(matcher.group(1)) ? -1 : 1;
            String daysStr = matcher.group(2);
            String hoursStr = matcher.group(3);
            if (daysStr != null || hoursStr != null) {
                int hours = 0;
                if (hoursStr != null) {
                    try {
                        hours = Integer.parseInt(hoursStr);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to Hours, non-numeric hours", text, 0, ex);
                    }
                }
                if (daysStr != null) {
                    try {
                        int daysAsHours = Math.multiplyExact(Integer.parseInt(daysStr), HOURS_PER_DAY);
                        hours = Math.addExact(hours, daysAsHours);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to Hours, non-numeric days", text, 0, ex);
                    }
                }
                return of(Math.multiplyExact(hours, negate));
            }
        }
        throw new DateTimeParseException("Text cannot be parsed to Hours", text, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Hours} consisting of the number of hours between two temporals.
     * <p>
     * The start temporal is included, but the end temporal is not.
     * The result of this method can be negative if the end is before the start.
     *
     * @param startInclusive  the start temporal, inclusive, not null
     * @param endExclusive  the end temporal, exclusive, not null
     * @return the number of hours between the start and end temporals, not null
     */
    public static Hours between(Temporal startInclusive, Temporal endExclusive) {
        return of(Math.toIntExact(HOURS.between(startInclusive, endExclusive)));
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific number of hours.
     *
     * @param hours  the amount of hours
     */
    private Hours(int hours) {
        this.hours = hours;
    }

    /**
     * Resolves singletons.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return Hours.of(hours);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the requested unit.
     * <p>
     * This returns a value for the supported unit - {@link ChronoUnit#HOURS HOURS}.
     * All other units throw an exception.
     *
     * @param unit  the {@code TemporalUnit} for which to return the value
     * @return the long value of the unit
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     */
    @Override
    public long get(TemporalUnit unit) {
        if (unit == HOURS) {
            return hours;
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    /**
     * Gets the set of units supported by this amount.
     * <p>
     * The single supported unit is {@link ChronoUnit#HOURS HOURS}.
     * <p>
     * This set can be used in conjunction with {@link #get(TemporalUnit)} to
     * access the entire state of the amount.
     *
     * @return a list containing the hours unit, not null
     */
    @Override
    public List<TemporalUnit> getUnits() {
        return Collections.singletonList(HOURS);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of hours in this amount.
     *
     * @return the number of hours
     */
    public int getAmount() {
        return hours;
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
     * The parameter is converted using {@link Hours#from(TemporalAmount)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, not null
     * @return a {@code Hours} based on this instance with the requested amount added, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Hours plus(TemporalAmount amountToAdd) {
        return plus(Hours.from(amountToAdd).getAmount());
    }

    /**
     * Returns a copy of this amount with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the amount of hours to add, may be negative
     * @return a {@code Hours} based on this instance with the requested amount added, not null
     * @throws ArithmeticException if the result overflows an int
     */
    public Hours plus(int hours) {
        if (hours == 0) {
            return this;
        }
        return of(Math.addExact(this.hours, hours));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this amount with the specified amount subtracted.
     * <p>
     * The parameter is converted using {@link Hours#from(TemporalAmount)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount to subtract, not null
     * @return a {@code Hours} based on this instance with the requested amount subtracted, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Hours minus(TemporalAmount amountToSubtract) {
        return minus(Hours.from(amountToSubtract).getAmount());
    }

    /**
     * Returns a copy of this amount with the specified number of hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the amount of hours to add, may be negative
     * @return a {@code Hours} based on this instance with the requested amount subtracted, not null
     * @throws ArithmeticException if the result overflows an int
     */
    public Hours minus(int hours) {
        if (hours == 0) {
            return this;
        }
        return of(Math.subtractExact(this.hours, hours));
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
    public Hours multipliedBy(int scalar) {
        if (scalar == 1) {
            return this;
        }
        return of(Math.multiplyExact(hours, scalar));
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
    public Hours dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return of(hours / divisor);
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
    public Hours negated() {
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
    public Hours abs() {
        return hours < 0 ? negated() : this;
    }

    //-------------------------------------------------------------------------
    /**
     * Gets the number of hours as a {@code Duration}.
     * <p>
     * This returns a duration with the same number of hours.
     *
     * @return the equivalent duration, not null
     * @deprecated Use {@link #toDuration()}
     */
    @Deprecated
    public Duration toPeriod() {
        return Duration.ofHours(hours);
    }

    //-------------------------------------------------------------------------
    /**
     * Gets the number of hours as a {@code Duration}.
     * <p>
     * This returns a duration with the same number of hours.
     *
     * @return the equivalent duration, not null
     */
    public Duration toDuration() {
        return Duration.ofHours(hours);
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
     * @throws UnsupportedTemporalTypeException if the HOURS unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal addTo(Temporal temporal) {
        if (hours != 0) {
            temporal = temporal.plus(hours, HOURS);
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
     * @throws UnsupportedTemporalTypeException if the HOURS unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal subtractFrom(Temporal temporal) {
        if (hours != 0) {
            temporal = temporal.minus(hours, HOURS);
        }
        return temporal;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this amount to the specified {@code Hours}.
     * <p>
     * The comparison is based on the total length of the amounts.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param otherAmount  the other amount, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(Hours otherAmount) {
        int thisValue = this.hours;
        int otherValue = otherAmount.hours;
        return Integer.compare(thisValue, otherValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this amount is equal to the specified {@code Hours}.
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
        if (otherAmount instanceof Hours) {
            Hours other = (Hours) otherAmount;
            return this.hours == other.hours;
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
        return hours;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of hours.
     * This will be in the format 'PTnH' where n is the number of hours.
     *
     * @return the number of hours in ISO-8601 string format
     */
    @Override
    @ToString
    public String toString() {
        return "PT" + hours + "H";
    }

}
