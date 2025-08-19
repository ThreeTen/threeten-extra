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

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.IsoFields.QUARTER_OF_YEAR;
import static java.time.temporal.IsoFields.QUARTER_YEARS;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Locale;

/**
 * A quarter-of-year, such as 'Q2'.
 * <p>
 * {@code Quarter} is an enum representing the 4 quarters of the year - Q1, Q2, Q3 and Q4.
 * These are defined as January to March, April to June, July to September and October to December.
 * <p>
 * The {@code int} value follows the quarter, from 1 (Q1) to 4 (Q4).
 * It is recommended that applications use the enum rather than the {@code int} value
 * to ensure code clarity.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code Quarter}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * This enum represents a common concept that is found in many calendar systems.
 * As such, this enum may be used by any calendar system that has the quarter-of-year
 * concept defined exactly equivalent to the ISO calendar system.
 *
 * <h3>Implementation Requirements:</h3>
 * This is an immutable and thread-safe enum.
 */
public enum Quarter implements TemporalAccessor, TemporalAdjuster {

    /**
     * The singleton instance for the first quarter-of-year, from January to March.
     * This has the numeric value of {@code 1}.
     */
    Q1,
    /**
     * The singleton instance for the second quarter-of-year, from April to June.
     * This has the numeric value of {@code 2}.
     */
    Q2,
    /**
     * The singleton instance for the third quarter-of-year, from July to September.
     * This has the numeric value of {@code 3}.
     */
    Q3,
    /**
     * The singleton instance for the fourth quarter-of-year, from October to December.
     * This has the numeric value of {@code 4}.
     */
    Q4;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Quarter} from an {@code int} value.
     * <p>
     * {@code Quarter} is an enum representing the 4 quarters of the year.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows the quarter, from 1 (Q1) to 4 (Q4).
     *
     * @param quarterOfYear  the quarter-of-year to represent, from 1 (Q1) to 4 (Q4)
     * @return the quarter-of-year, not null
     * @throws DateTimeException if the quarter-of-year is invalid
     */
    public static Quarter of(int quarterOfYear) {
        switch (quarterOfYear) {
            case 1:
                return Q1;
            case 2:
                return Q2;
            case 3:
                return Q3;
            case 4:
                return Q4;
            default:
                throw new DateTimeException("Invalid value for Quarter: " + quarterOfYear);
        }
    }

    /**
     * Obtains an instance of {@code Quarter} from a month-of-year.
     * <p>
     * {@code Quarter} is an enum representing the 4 quarters of the year.
     * This factory allows the enum to be obtained from the {@code Month} value.
     * <p>
     * January to March are Q1, April to June are Q2, July to September are Q3
     * and October to December are Q4.
     *
     * @param monthOfYear  the month-of-year to convert from, from 1 to 12
     * @return the quarter-of-year, not null
     * @throws DateTimeException if the month-of-year is invalid
     */
    public static Quarter ofMonth(int monthOfYear) {
        MONTH_OF_YEAR.range().checkValidValue(monthOfYear, MONTH_OF_YEAR);
        return of((monthOfYear - 1) / 3 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Quarter} from a temporal object.
     * <p>
     * This obtains a quarter based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code Quarter}.
     * <p>
     * The conversion extracts the {@link IsoFields#QUARTER_OF_YEAR QUARTER_OF_YEAR} field.
     * The extraction is only permitted if the temporal object has an ISO
     * chronology, or can be converted to a {@code LocalDate}.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used in queries via method reference, {@code Quarter::from}.
     *
     * @param temporal  the temporal-time object to convert, not null
     * @return the quarter-of-year, not null
     * @throws DateTimeException if unable to convert to a {@code Quarter}
     */
    public static Quarter from(TemporalAccessor temporal) {
        if (temporal instanceof Quarter) {
            return (Quarter) temporal;
        } else if (temporal instanceof Month) {
            Month month = (Month) temporal;
            return of(month.ordinal() / 3 + 1);
        }
        try {
            TemporalAccessor adjusted =
                    !IsoChronology.INSTANCE.equals(Chronology.from(temporal)) ? LocalDate.from(temporal) : temporal;
            // need to use getLong() as JDK Parsed class get() doesn't work properly
            int qoy = Math.toIntExact(adjusted.getLong(QUARTER_OF_YEAR));
            return of(qoy);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain Quarter from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the quarter-of-year {@code int} value.
     * <p>
     * The values are numbered following the ISO-8601 standard,
     * from 1 (Q1) to 4 (Q4).
     *
     * @return the quarter-of-year, from 1 (Q1) to 4 (Q4)
     */
    public int getValue() {
        return ordinal() + 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation, such as 'Q1' or '4th quarter'.
     * <p>
     * This returns the textual name used to identify the quarter-of-year,
     * suitable for presentation to the user.
     * The parameters control the style of the returned text and the locale.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param style  the length of the text required, not null
     * @param locale  the locale to use, not null
     * @return the text value of the quarter-of-year, not null
     */
    public String getDisplayName(TextStyle style, Locale locale) {
        return new DateTimeFormatterBuilder().appendText(QUARTER_OF_YEAR, style).toFormatter(locale).format(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified field is supported.
     * <p>
     * This checks if this quarter-of-year can be queried for the specified field.
     * If false, then calling the {@link #range(TemporalField) range} and
     * {@link #get(TemporalField) get} methods will throw an exception.
     * <p>
     * If the field is {@link IsoFields#QUARTER_OF_YEAR QUARTER_OF_YEAR} then
     * this method returns true.
     * All {@code ChronoField} instances will return false.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.isSupportedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the field is supported is determined by the field.
     *
     * @param field  the field to check, null returns false
     * @return true if the field is supported on this quarter-of-year, false if not
     */
    @Override
    public boolean isSupported(TemporalField field) {
        if (field == QUARTER_OF_YEAR) {
            return true;
        } else if (field instanceof ChronoField) {
            return false;
        }
        return field != null && field.isSupportedBy(this);
    }

    /**
     * Gets the range of valid values for the specified field.
     * <p>
     * The range object expresses the minimum and maximum valid values for a field.
     * This quarter is used to enhance the accuracy of the returned range.
     * If it is not possible to return the range, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is {@link IsoFields#QUARTER_OF_YEAR QUARTER_OF_YEAR} then the
     * range of the quarter-of-year, from 1 to 4, will be returned.
     * All {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.rangeRefinedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the range can be obtained is determined by the field.
     *
     * @param field  the field to query the range for, not null
     * @return the range of valid values for the field, not null
     * @throws DateTimeException if the range for the field cannot be obtained
     * @throws UnsupportedTemporalTypeException if the field is not supported
     */
    @Override
    public ValueRange range(TemporalField field) {
        if (field == QUARTER_OF_YEAR) {
            return field.range();
        } else if (field instanceof ChronoField) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return TemporalAccessor.super.range(field);
    }

    /**
     * Gets the value of the specified field from this quarter-of-year as an {@code int}.
     * <p>
     * This queries this quarter for the value for the specified field.
     * The returned value will always be within the valid range of values for the field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is {@link IsoFields#QUARTER_OF_YEAR QUARTER_OF_YEAR} then the
     * value of the quarter-of-year, from 1 to 4, will be returned.
     * All {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
     * passing {@code this} as the argument. Whether the value can be obtained,
     * and what the value represents, is determined by the field.
     *
     * @param field  the field to get, not null
     * @return the value for the field, within the valid range of values
     * @throws DateTimeException if a value for the field cannot be obtained or
     *         the value is outside the range of valid values for the field
     * @throws UnsupportedTemporalTypeException if the field is not supported or
     *         the range of values exceeds an {@code int}
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public int get(TemporalField field) {
        if (field == QUARTER_OF_YEAR) {
            return getValue();
        } else if (field instanceof ChronoField) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return TemporalAccessor.super.get(field);
    }

    /**
     * Gets the value of the specified field from this quarter-of-year as a {@code long}.
     * <p>
     * This queries this quarter for the value for the specified field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is {@link IsoFields#QUARTER_OF_YEAR QUARTER_OF_YEAR} then the
     * value of the quarter-of-year, from 1 to 4, will be returned.
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
     * passing {@code this} as the argument. Whether the value can be obtained,
     * and what the value represents, is determined by the field.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws DateTimeException if a value for the field cannot be obtained
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public long getLong(TemporalField field) {
        if (field == QUARTER_OF_YEAR) {
            return getValue();
        } else if (field instanceof ChronoField) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.getFrom(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the quarter that is the specified number of quarters after this one.
     * <p>
     * The calculation rolls around the end of the year from Q4 to Q1.
     * The specified period may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param quarters  the quarters to add, positive or negative
     * @return the resulting quarter, not null
     */
    public Quarter plus(long quarters) {
        int amount = (int) quarters % 4;
        return values()[(ordinal() + (amount + 4)) % 4];
    }

    /**
     * Returns the quarter that is the specified number of quarters before this one.
     * <p>
     * The calculation rolls around the start of the year from Q1 to Q4.
     * The specified period may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param quarters  the quarters to subtract, positive or negative
     * @return the resulting quarter, not null
     */
    public Quarter minus(long quarters) {
        return plus(-(quarters % 4));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this quarter in days.
     * <p>
     * This takes a flag to determine whether to return the length for a leap year or not.
     * <p>
     * Q1 has 90 in a standard year and 91 days in a leap year.
     * Q2 has 91 days.
     * Q3 and Q4 have 92 days.
     *
     * @param leapYear  true if the length is required for a leap year
     * @return the length of this quarter in days, from 90 to 92
     */
    public int length(boolean leapYear) {
        switch (this) {
            case Q1:
                return (leapYear ? 91 : 90);
            case Q2:
                return 91;
            default:
                return 92;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the first of the three months that this quarter refers to.
     * <p>
     * Q1 will return January.<br>
     * Q2 will return April.<br>
     * Q3 will return July.<br>
     * Q4 will return October.
     * <p>
     * To obtain the other two months of the quarter, simply use {@link Month#plus(long)}
     * on the returned month.
     *
     * @return the first month in the quarter, not null
     */
    public Month firstMonth() {
        switch (this) {
            case Q1:
                return Month.JANUARY;
            case Q2:
                return Month.APRIL;
            case Q3:
                return Month.JULY;
            case Q4:
                return Month.OCTOBER;
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Queries this quarter-of-year using the specified query.
     * <p>
     * {@link TemporalQueries#chronology()} and {@link TemporalQueries#precision()} are directly supported.
     * Otherwise, the result of this method is obtained by invoking
     * {@link TemporalAccessor#query(TemporalQuery)} on the parent interface.
     *
     * @param <R> the type of the result
     * @param query  the query to invoke, not null
     * @return the query result, null may be returned (defined by the query)
     * @throws DateTimeException if unable to query (defined by the query)
     * @throws ArithmeticException if numeric overflow occurs (defined by the query)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.chronology()) {
            return (R) IsoChronology.INSTANCE;
        } else if (query == TemporalQueries.precision()) {
            return (R) QUARTER_YEARS;
        }
        return TemporalAccessor.super.query(query);
    }

    /**
     * Adjusts the specified temporal object to have this quarter-of-year.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with the quarter-of-year changed to be the same as this.
     * <p>
     * The adjustment is equivalent to using {@link Temporal#with(TemporalField, long)}
     * passing {@link IsoFields#QUARTER_OF_YEAR} as the field.
     * If the specified temporal object does not use the ISO calendar system then
     * a {@code DateTimeException} is thrown.
     * <p>
     * In most cases, it is clearer to reverse the calling pattern by using
     * {@link Temporal#with(TemporalAdjuster)}:
     * <pre>
     *   // these two lines are equivalent, but the second approach is recommended
     *   temporal = thisQuarter.adjustInto(temporal);
     *   temporal = temporal.with(thisQuarter);
     * </pre>
     * <p>
     * For example, given a date in May, the following are output:
     * <pre>
     *   dateInMay.with(Q1);    // three months earlier
     *   dateInMay.with(Q2);    // no change
     *   dateInMay.with(Q3);    // three months later
     *   dateInMay.with(Q4);    // six months later
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param temporal  the target object to be adjusted, not null
     * @return the adjusted object, not null
     * @throws DateTimeException if unable to make the adjustment
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal adjustInto(Temporal temporal) {
        if (Chronology.from(temporal).equals(IsoChronology.INSTANCE) == false) {
            throw new DateTimeException("Adjustment only supported on ISO date-time");
        }
        return temporal.with(QUARTER_OF_YEAR, getValue());
    }

}
