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

import static java.time.temporal.ChronoField.DAY_OF_YEAR;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

/**
 * A day-of-year in the ISO-8601 calendar system.
 * <p>
 * {@code DayOfYear} is an immutable date-time object that represents a day-of-year.
 * It is a type-safe way of representing a day-of-year in an application.
 * Any field that can be derived from a day-of-year can be obtained.
 * <p>
 * This class does not store or represent a year, month, time or time-zone.
 * For example, the value "51" can be stored in a {@code DayOfYear} and
 * would represent the 51st day of any year.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class DayOfYear
        implements TemporalAccessor, TemporalAdjuster, Comparable<DayOfYear>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -8789692114017384034L;
    /**
     * Cache of singleton instances.
     */
    private static final DayOfYear[] VALUES = new DayOfYear[366];
    static {
        for (int i = 0; i < 366; i++) {
            VALUES[i] = new DayOfYear(i + 1);
        }
    }

    /**
     * The day-of-year being represented, from 1 to 366.
     */
    private final int day;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current day-of-year from the system clock in the default time-zone.
     * <p>
     * This will query the {@link java.time.Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current day-of-year.
     * The zone and offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current day-of-year using the system clock and default time-zone, not null
     */
    public static DayOfYear now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current day-of-year from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(java.time.ZoneId) system clock} to obtain the current day-of-year.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone  the zone ID to use, not null
     * @return the current day-of-year using the system clock, not null
     */
    public static DayOfYear now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current day-of-year from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current day-of-year.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current day-of-year, not null
     */
    public static DayOfYear now(Clock clock) {
        final LocalDate now = LocalDate.now(clock);  // called once
        return DayOfYear.of(now.getDayOfYear());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code DayOfYear}.
     * <p>
     * A day-of-year object represents one of the 366 days of the year, from 1 to 366.
     *
     * @param dayOfYear  the day-of-year to represent, from 1 to 366
     * @return the day-of-year, not null
     * @throws DateTimeException if the day-of-year is invalid
     */
    public static DayOfYear of(int dayOfYear) {
        try {
            return VALUES[dayOfYear - 1];
        } catch (IndexOutOfBoundsException ex) {
            throw new DateTimeException("Invalid value for DayOfYear: " + dayOfYear);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code DayOfYear} from a date-time object.
     * <p>
     * This obtains a day-of-year based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code DayOfYear}.
     * <p>
     * The conversion extracts the {@link ChronoField#DAY_OF_YEAR day-of-year} field.
     * The extraction is only permitted if the temporal object has an ISO
     * chronology, or can be converted to a {@code LocalDate}.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used in queries via method reference, {@code DayOfYear::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the day-of-year, not null
     * @throws DateTimeException if unable to convert to a {@code DayOfYear}
     */
    public static DayOfYear from(TemporalAccessor temporal) {
        if (temporal instanceof DayOfYear) {
            return (DayOfYear) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            if (IsoChronology.INSTANCE.equals(Chronology.from(temporal)) == false) {
                temporal = LocalDate.from(temporal);
            }
            return of(temporal.get(DAY_OF_YEAR));
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain DayOfYear from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param dayOfYear  the day-of-year to represent
     */
    private DayOfYear(int dayOfYear) {
        this.day = dayOfYear;
    }

    /**
     * Validates the input.
     *
     * @return the valid object, not null
     */
    private Object readResolve() {
        return of(day);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day-of-year value.
     *
     * @return the day-of-year, from 1 to 366
     */
    public int getValue() {
        return day;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified field is supported.
     * <p>
     * This checks if this day-of-year can be queried for the specified field.
     * If false, then calling the {@link #range(TemporalField) range},
     * {@link #get(TemporalField) get} and {@link #getLong(TemporalField) getLong}
     * methods will throw an exception.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The supported fields are:
     * <ul>
     * <li>{@code DAY_OF_YEAR}
     * </ul>
     * All other {@code ChronoField} instances will return false.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.isSupportedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the field is supported is determined by the field.
     *
     * @param field  the field to check, null returns false
     * @return true if the field is supported on this day-of-year, false if not
     */
    @Override
    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            return field == DAY_OF_YEAR;
        }
        return field != null && field.isSupportedBy(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the range of valid values for the specified field.
     * <p>
     * The range object expresses the minimum and maximum valid values for a field.
     * This day-of-year is used to enhance the accuracy of the returned range.
     * If it is not possible to return the range, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return
     * appropriate range instances.
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
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
        return TemporalAccessor.super.range(field);
    }

    /**
     * Gets the value of the specified field from this day-of-year as an {@code int}.
     * <p>
     * This queries this day-of-year for the value for the specified field.
     * The returned value will always be within the valid range of values for the field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this day-of-year.
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
     * passing {@code this} as the argument. Whether the value can be obtained,
     * and what the value represents, is determined by the field.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws DateTimeException if a value for the field cannot be obtained or
     *  the value is outside the range of valid values for the field
     * @throws UnsupportedTemporalTypeException if the field is not supported or
     *  the range of values exceeds an {@code int}
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public int get(TemporalField field) {
        return TemporalAccessor.super.get(field);
    }

    /**
     * Gets the value of the specified field from this day-of-year as a {@code long}.
     * <p>
     * This queries this day-of-year for the value for the specified field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this day-of-year.
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
        if (field == DAY_OF_YEAR) {
            return day;
        } else if (field instanceof ChronoField) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.getFrom(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is valid for this day-of-year.
     * <p>
     * This method checks whether this day-of-year and the input year form
     * a valid date. This can only return false for day-of-year 366.
     *
     * @param year  the year to validate
     * @return true if the year is valid for this day-of-year
     */
    public boolean isValidYear(int year) {
        return (day < 366 || Year.isLeap(year));
    }

    //-----------------------------------------------------------------------
    /**
     * Queries this day-of-year using the specified query.
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
            return (R) ChronoUnit.DAYS;
        }
        return TemporalAccessor.super.query(query);
    }

    /**
     * Adjusts the specified temporal object to have this day-of-year.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with the day-of-year changed to be the same as this.
     * <p>
     * The adjustment is equivalent to using {@link Temporal#with(TemporalField, long)}
     * passing {@link ChronoField#DAY_OF_YEAR} as the field.
     * If the specified temporal object does not use the ISO calendar system then
     * a {@code DateTimeException} is thrown.
     * <p>
     * In most cases, it is clearer to reverse the calling pattern by using
     * {@link Temporal#with(TemporalAdjuster)}:
     * <pre>
     *   // these two lines are equivalent, but the second approach is recommended
     *   temporal = thisDay.adjustInto(temporal);
     *   temporal = temporal.with(thisDay);
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
        return temporal.with(DAY_OF_YEAR, day);
    }

    //-----------------------------------------------------------------------
    /**
     * Combines this day-of-year with a year to create a {@code LocalDate}.
     * <p>
     * This returns a {@code LocalDate} formed from this day and the specified year.
     * <p>
     * This method can be used as part of a chain to produce a date:
     * <pre>
     *  LocalDate date = day.atYear(year);
     * </pre>
     * <p>
     * The day-of-year value 366 is only valid in a leap year.
     *
     * @param year  the year to use, not null
     * @return the local date formed from this day and the specified year, not null
     * @throws DateTimeException if the year is invalid or this is day 366 and the year is not a leap year
     */
    public LocalDate atYear(Year year) {
        Objects.requireNonNull(year, "year");
        return year.atDay(day);
    }

    /**
     * Combines this day-of-year with a year to create a {@code LocalDate}.
     * <p>
     * This returns a {@code LocalDate} formed from this day and the specified year.
     * <p>
     * This method can be used as part of a chain to produce a date:
     * <pre>
     *  LocalDate date = day.atYear(year);
     * </pre>
     * <p>
     * The day-of-year value 366 is only valid in a leap year.
     *
     * @param year  the year to use, from MIN_YEAR to MAX_YEAR
     * @return the local date formed from this day and the specified year, not null
     * @throws DateTimeException if the year is invalid or this is day 366 and the year is not a leap year
     */
    public LocalDate atYear(int year) {
        return LocalDate.ofYearDay(year, day);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this day-of-year to another.
     * <p>
     * The comparison is based on the value of the day.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param other  the other day-of-year instance, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(DayOfYear other) {
        return day - other.day;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this day-of-year is equal to another day-of-year.
     *
     * @param obj  the other day-of-year instance, null returns false
     * @return true if the day-of-year is the same
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DayOfYear) {
            return day == ((DayOfYear) obj).day;
        }
        return false;
    }

    /**
     * A hash code for this day-of-year.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return day;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this day-of-year as a {@code String}.
     *
     * @return a string representation of this day-of-year, not null
     */
    @Override
    public String toString() {
        return "DayOfYear:" + day;
    }

}
