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

import static java.time.temporal.ChronoUnit.DAYS;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Period;
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
 * A day-based amount of time, such as '12 days'.
 * <p>
 * This class models a quantity or amount of time in terms of days.
 * It is a type-safe way of representing a number of days in an application.
 * <p>
 * The model is of a directed amount, meaning that the amount may be negative.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class Days
        implements TemporalAmount, Comparable<Days>, Serializable {

    /**
     * A constant for zero days.
     */
    public static final Days ZERO = new Days(0);
    /**
     * A constant for one day.
     */
    public static final Days ONE = new Days(1);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -8903767091325669093L;
    /**
     * The number of days per week.
     */
    private static final int DAYS_PER_WEEK = 7;
    /**
     * The pattern for parsing.
     */
    private static final Pattern PATTERN =
            Pattern.compile("([-+]?)P"
                    + "(?:([-+]?[0-9]+)W)?"
                    + "(?:([-+]?[0-9]+)D)?", Pattern.CASE_INSENSITIVE);

    /**
     * The number of days.
     */
    private final int days;

    /**
     * Obtains a {@code Days} representing a number of days.
     * <p>
     * The resulting amount will have the specified days.
     *
     * @param days  the number of days, positive or negative
     * @return the number of days, not null
     */
    public static Days of(int days) {
        if (days == 0) {
            return ZERO;
        } else if (days == 1) {
            return ONE;
        }
        return new Days(days);
    }

    /**
     * Obtains a {@code Days} representing the number of days
     * equivalent to a number of weeks.
     * <p>
     * The resulting amount will be day-based, with the number of days
     * equal to the number of weeks multiplied by 7.
     *
     * @param weeks  the number of weeks, positive or negative
     * @return the amount with the input weeks converted to days, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Days ofWeeks(int weeks) {
        if (weeks == 0) {
            return ZERO;
        }
        return new Days(Math.multiplyExact(weeks, DAYS_PER_WEEK));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Days} from a temporal amount.
     * <p>
     * This obtains an instance based on the specified amount.
     * A {@code TemporalAmount} represents an amount of time, which may be
     * date-based or time-based, which this factory extracts to a {@code Days}.
     * <p>
     * The result is calculated by looping around each unit in the specified amount.
     * Each amount is converted to days using {@link Temporals#convertAmount}.
     * If the conversion yields a remainder, an exception is thrown.
     * If the amount is zero, the unit is ignored.
     *
     * @param amount  the temporal amount to convert, not null
     * @return the equivalent amount, not null
     * @throws DateTimeException if unable to convert to a {@code Days}
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Days from(TemporalAmount amount) {
        if (amount instanceof Days) {
            return (Days) amount;
        }
        Objects.requireNonNull(amount, "amount");
        int days = 0;
        for (TemporalUnit unit : amount.getUnits()) {
            long value = amount.get(unit);
            if (value != 0) {
                long[] converted = Temporals.convertAmount(value, unit, DAYS);
                if (converted[1] != 0) {
                    throw new DateTimeException(
                            "Amount could not be converted to a whole number of days: " + value + " " + unit);
                }
                days = Math.addExact(days, Math.toIntExact(converted[0]));
            }
        }
        return of(days);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Days} from a text string such as {@code PnD}.
     * <p>
     * This will parse the string produced by {@code toString()} which is
     * based on the ISO-8601 period formats {@code PnD} and {@code PnW}.
     * <p>
     * The string starts with an optional sign, denoted by the ASCII negative
     * or positive symbol. If negative, the whole amount is negated.
     * The ASCII letter "P" is next in upper or lower case.
     * There are then two sections, each consisting of a number and a suffix.
     * At least one of the two sections must be present.
     * The sections have suffixes in ASCII of "W" and "D" for weeks and days,
     * accepted in upper or lower case. The suffixes must occur in order.
     * The number part of each section must consist of ASCII digits.
     * The number may be prefixed by the ASCII negative or positive symbol.
     * The number must parse to an {@code int}.
     * <p>
     * The leading plus/minus sign, and negative values for weeks and days are
     * not part of the ISO-8601 standard.
     * <p>
     * For example, the following are valid inputs:
     * <pre>
     *   "P2D"             -- Days.of(2)
     *   "P-2D"            -- Days.of(-2)
     *   "-P2D"            -- Days.of(-2)
     *   "-P-2D"           -- Days.of(2)
     *   "P3W"             -- Days.of(3 * 7)
     *   "P3W-2D"          -- Days.of(3 * 7 - 2)
     * </pre>
     *
     * @param text  the text to parse, not null
     * @return the parsed period, not null
     * @throws DateTimeParseException if the text cannot be parsed to a period
     */
    @FromString
    public static Days parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches()) {
            int negate = "-".equals(matcher.group(1)) ? -1 : 1;
            String weeksStr = matcher.group(2);
            String daysStr = matcher.group(3);
            if (weeksStr != null || daysStr != null) {
                int days = 0;
                if (daysStr != null) {
                    try {
                        days = Integer.parseInt(daysStr);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to a Days, non-numeric days", text, 0, ex);
                    }
                }
                if (weeksStr != null) {
                    try {
                        int weeks = Math.multiplyExact(Integer.parseInt(weeksStr), DAYS_PER_WEEK);
                        days = Math.addExact(days, weeks);
                    } catch (NumberFormatException ex) {
                        throw new DateTimeParseException("Text cannot be parsed to a Days, non-numeric weeks", text, 0, ex);
                    }
                }
                return of(Math.multiplyExact(days, negate));
            }
        }
        throw new DateTimeParseException("Text cannot be parsed to a Days", text, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Days} consisting of the number of days between two dates.
     * <p>
     * The start date is included, but the end date is not.
     * The result of this method can be negative if the end is before the start.
     *
     * @param startDateInclusive  the start date, inclusive, not null
     * @param endDateExclusive  the end date, exclusive, not null
     * @return the number of days between this date and the end date, not null
     */
    public static Days between(Temporal startDateInclusive, Temporal endDateExclusive) {
        return of(Math.toIntExact(DAYS.between(startDateInclusive, endDateExclusive)));
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific number of days.
     *
     * @param days  the days to use
     */
    private Days(int days) {
        super();
        this.days = days;
    }

    /**
     * Resolves singletons.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return Days.of(days);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the requested unit.
     * <p>
     * This returns a value for the supported unit - {@link ChronoUnit#DAYS DAYS}.
     * All other units throw an exception.
     *
     * @param unit  the {@code TemporalUnit} for which to return the value
     * @return the long value of the unit
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     */
    @Override
    public long get(TemporalUnit unit) {
        if (unit == DAYS) {
            return days;
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    /**
     * Gets the set of units supported by this amount.
     * <p>
     * The single supported unit is {@link ChronoUnit#DAYS DAYS}.
     * <p>
     * This set can be used in conjunction with {@link #get(TemporalUnit)}
     * to access the entire state of the amount.
     *
     * @return a list containing the days unit, not null
     */
    @Override
    public List<TemporalUnit> getUnits() {
        return Collections.singletonList(DAYS);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of days in this amount.
     *
     * @return the number of days
     */
    public int getAmount() {
        return days;
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
     * The parameter is converted using {@link Days#from(TemporalAmount)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, not null
     * @return a {@code Days} based on this instance with the requested amount added, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Days plus(TemporalAmount amountToAdd) {
        return plus(Days.from(amountToAdd).getAmount());
    }

    /**
     * Returns a copy of this amount with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the amount of days to add, may be negative
     * @return a {@code Days} based on this instance with the requested amount added, not null
     * @throws ArithmeticException if the result overflows an int
     */
    public Days plus(int days) {
        if (days == 0) {
            return this;
        }
        return of(Math.addExact(this.days, days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this amount with the specified amount subtracted.
     * <p>
     * The parameter is converted using {@link Days#from(TemporalAmount)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount to subtract, not null
     * @return a {@code Days} based on this instance with the requested amount subtracted, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Days minus(TemporalAmount amountToSubtract) {
        return minus(Days.from(amountToSubtract).getAmount());
    }

    /**
     * Returns a copy of this amount with the specified number of days subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the amount of days to add, may be negative
     * @return a {@code Days} based on this instance with the requested amount subtracted, not null
     * @throws ArithmeticException if the result overflows an int
     */
    public Days minus(int days) {
        if (days == 0) {
            return this;
        }
        return of(Math.subtractExact(this.days, days));
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
    public Days multipliedBy(int scalar) {
        if (scalar == 1) {
            return this;
        }
        return of(Math.multiplyExact(days, scalar));
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
    public Days dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return of(days / divisor);
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
    public Days negated() {
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
    public Days abs() {
        return days < 0 ? negated() : this;
    }

    //-------------------------------------------------------------------------
    /**
     * Gets the number of days as a {@code Period}.
     * <p>
     * This returns a period with the same number of days.
     *
     * @return the equivalent period, not null
     */
    public Period toPeriod() {
        return Period.ofDays(days);
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
     * @throws UnsupportedTemporalTypeException if the DAYS unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal addTo(Temporal temporal) {
        if (days != 0) {
            temporal = temporal.plus(days, DAYS);
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
     * @throws UnsupportedTemporalTypeException if the DAYS unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal subtractFrom(Temporal temporal) {
        if (days != 0) {
            temporal = temporal.minus(days, DAYS);
        }
        return temporal;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this amount to the specified {@code Days}.
     * <p>
     * The comparison is based on the total length of the amounts.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param otherAmount  the other amount, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(Days otherAmount) {
        int thisValue = this.days;
        int otherValue = otherAmount.days;
        return Integer.compare(thisValue, otherValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this amount is equal to the specified {@code Days}.
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
        if (otherAmount instanceof Days) {
            Days other = (Days) otherAmount;
            return this.days == other.days;
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
        return days;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of days.
     * This will be in the format 'PnD' where n is the number of days.
     *
     * @return the number of days in ISO-8601 string format
     */
    @Override
    @ToString
    public String toString() {
        return "P" + days + "D";
    }

}
