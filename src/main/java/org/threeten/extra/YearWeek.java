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

import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.temporal.IsoFields.WEEK_BASED_YEAR;
import static java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
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
import java.util.Objects;

/**
 * A year-week in the ISO week date system such as {@code 2015-W13}
 * <p>
 * {@code YearWeek} is an immutable date-time object that represents the combination
 * of a week-based-year and week-of-week-based-year.
 * Any field that can be derived from those two fields can be obtained.
 * <p>
 * This class does not store or represent a day, time or time-zone.
 * For example, the value "13th week of 2007" can be stored in a {@code YearWeek}.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which today's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * However, any application that makes use of historical dates, and requires them
 * to be accurate will find the ISO-8601 approach unsuitable.
 * <p>
 * ISO-8601 defines the week as always starting with Monday.
 * The first week is the week which contains the first Thursday of the calendar year.
 * As such, the week-based-year used in this class does not align with the calendar year.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class YearWeek
        implements TemporalAccessor, TemporalAdjuster, Comparable<YearWeek>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 3381384054271883921L;

    /**
     * Parser.
     */
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(WEEK_BASED_YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral("-W")
            .appendValue(WEEK_OF_WEEK_BASED_YEAR, 2)
            .toFormatter();

    /**
     * The week-based-year.
     */
    private final int year;
    /**
     * The week-of-week-based-year
     */
    private final int week;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current year-week from the system clock in the default time-zone.
     * <p>
     * This will query the {@link java.time.Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current year-week.
     * The zone and offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current year-week using the system clock and default time-zone, not null
     */
    public static YearWeek now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current year-week from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(java.time.ZoneId) system clock} to obtain the current year-week.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone  the zone ID to use, not null
     * @return the current year-week using the system clock, not null
     */
    public static YearWeek now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current year-week from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current year-week.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current year-week, not null
     */
    public static YearWeek now(Clock clock) {
        final LocalDate now = LocalDate.now(clock);  // called once
        return YearWeek.of(now.get(WEEK_BASED_YEAR), now.get(WEEK_OF_WEEK_BASED_YEAR));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearWeek} from a week-based-year and week.
     * <p>
     * If the week is 53 and the year does not have 53 weeks, week one of the following
     * year is selected.
     *
     * @param weekBasedYear  the week-based-year to represent, from MIN_YEAR to MAX_YEAR
     * @param week  the week-of-week-based-year to represent, from 1 to 53
     * @return the year-week, not null
     * @throws DateTimeException if either field is invalid
     */
    public static YearWeek of(int weekBasedYear, int week) {
        WEEK_BASED_YEAR.range().checkValidValue(weekBasedYear, WEEK_BASED_YEAR);
        WEEK_OF_WEEK_BASED_YEAR.range().checkValidValue(week, WEEK_OF_WEEK_BASED_YEAR);
        if (week == 53 && weekRange(weekBasedYear) < 53) {
            week = 1;
            weekBasedYear++;
            WEEK_BASED_YEAR.range().checkValidValue(weekBasedYear, WEEK_BASED_YEAR);
        }
        return new YearWeek(weekBasedYear, week);
    }

    // from IsoFields in ThreeTen-Backport
    private static int weekRange(int weekBasedYear) {
        LocalDate date = LocalDate.of(weekBasedYear, 1, 1);
        // 53 weeks if standard year starts on Thursday, or Wed in a leap year
        if (date.getDayOfWeek() == THURSDAY || (date.getDayOfWeek() == WEDNESDAY && date.isLeapYear())) {
            return 53;
        }
        return 52;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearWeek} from a temporal object.
     * <p>
     * This obtains a year-week based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code YearWeek}.
     * <p>
     * The conversion extracts the {@link IsoFields#WEEK_BASED_YEAR WEEK_BASED_YEAR} and
     * {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR WEEK_OF_WEEK_BASED_YEAR} fields.
     * The extraction is only permitted if the temporal object has an ISO
     * chronology, or can be converted to a {@code LocalDate}.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used in queries via method reference, {@code YearWeek::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the year-week, not null
     * @throws DateTimeException if unable to convert to a {@code YearWeek}
     */
    public static YearWeek from(TemporalAccessor temporal) {
        if (temporal instanceof YearWeek) {
            return (YearWeek) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            if (IsoChronology.INSTANCE.equals(Chronology.from(temporal)) == false) {
                temporal = LocalDate.from(temporal);
            }
            return of(temporal.get(WEEK_BASED_YEAR), (int) temporal.getLong(WEEK_OF_WEEK_BASED_YEAR));
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain YearWeek from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearWeek} from a text string such as {@code 2007-W13}.
     * <p>
     * The string must represent a valid year-week.
     * Week 53 will be adjusted to week 1 of the following year if necessary.
     * The format must be {@code YYYY-'W'ww}.
     * Years outside the range 0000 to 9999 must be prefixed by the plus or minus symbol.
     *
     * @param text  the text to parse such as "2007-W13", not null
     * @return the parsed year-week, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static YearWeek parse(CharSequence text) {
        return parse(text, PARSER);
    }

    /**
     * Obtains an instance of {@code YearWeek} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a year-week.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed year-week, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static YearWeek parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.parse(text, YearWeek::from);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param weekBasedYear  the week-based-year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param week  the week to represent, validated
     */
    private YearWeek(int weekBasedYear, int week) {
        this.year = weekBasedYear;
        this.week = week;
    }

    /**
     * Validates the input.
     *
     * @return the valid object, not null
     */
    private Object readResolve() {
        return of(year, week);
    }

    /**
     * Returns a copy of this year-week with the new year and week, checking
     * to see if a new object is in fact required.
     *
     * @param newYear  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param newWeek  the week to represent, validated from 1 to 53
     * @return the year-week, not null
     */
    private YearWeek with(int newYear, int newWeek) {
        if (year == newYear && week == newWeek) {
            return this;
        }
        return of(newYear, newWeek);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified field is supported.
     * <p>
     * This checks if this year-week can be queried for the specified field.
     * If false, then calling the {@link #range(TemporalField) range},
     * {@link #get(TemporalField) get} and {@link #with(TemporalField, long)}
     * methods will throw an exception.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The supported fields are:
     * <ul>
     * <li>{@code WEEK_OF_WEEK_BASED_YEAR}
     * <li>{@code WEEK_BASED_YEAR}
     * </ul>
     * All {@code ChronoField} instances will return false.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.isSupportedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the field is supported is determined by the field.
     *
     * @param field  the field to check, null returns false
     * @return true if the field is supported on this year-week, false if not
     */
    @Override
    public boolean isSupported(TemporalField field) {
        if (field == WEEK_OF_WEEK_BASED_YEAR || field == WEEK_BASED_YEAR) {
            return true;
        } else if (field instanceof ChronoField) {
            return false;
        }
        return field != null && field.isSupportedBy(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the range of valid values for the specified field.
     * <p>
     * The range object expresses the minimum and maximum valid values for a field.
     * This year-week is used to enhance the accuracy of the returned range.
     * If it is not possible to return the range, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * The range for the {@link IsoFields#WEEK_BASED_YEAR WEEK_BASED_YEAR} and
     * {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR WEEK_OF_WEEK_BASED_YEAR} fields is returned.
     * All {@link ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     *
     * @param field  the field to query the range for, not null
     * @return the range of valid values for the field, not null
     * @throws DateTimeException if the range for the field cannot be obtained
     * @throws UnsupportedTemporalTypeException if the field is not supported
     */
    @Override
    public ValueRange range(TemporalField field) {
        if (field == WEEK_BASED_YEAR) {
            return WEEK_BASED_YEAR.range();
        }
        if (field == WEEK_OF_WEEK_BASED_YEAR) {
            return ValueRange.of(1, weekRange(year));
        }
        return TemporalAccessor.super.range(field);
    }

    /**
     * Gets the value of the specified field from this year-week as an {@code int}.
     * <p>
     * This queries this year-week for the value for the specified field.
     * The returned value will always be within the valid range of values for the field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * The value for the {@link IsoFields#WEEK_BASED_YEAR WEEK_BASED_YEAR} and
     * {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR WEEK_OF_WEEK_BASED_YEAR} fields is returned.
     * All {@link ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
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
        if (field == WEEK_BASED_YEAR) {
            return year;
        }
        if (field == WEEK_OF_WEEK_BASED_YEAR) {
            return week;
        }
        return TemporalAccessor.super.get(field);
    }

    /**
     * Gets the value of the specified field from this year-week as a {@code long}.
     * <p>
     * This queries this year-week for the value for the specified field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * The value for the {@link IsoFields#WEEK_BASED_YEAR WEEK_BASED_YEAR} and
     * {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR WEEK_OF_WEEK_BASED_YEAR} fields is returned.
     * All {@link ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws DateTimeException if a value for the field cannot be obtained
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public long getLong(TemporalField field) {
        if (field == WEEK_BASED_YEAR) {
            return year;
        }
        if (field == WEEK_OF_WEEK_BASED_YEAR) {
            return week;
        }
        if (field instanceof ChronoField) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.getFrom(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the week-based-year field.
     * <p>
     * This method returns the primitive {@code int} value for the week-based-year.
     *
     * @return the week-based-year
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the week-of-week-based-year field.
     * <p>
     * This method returns the primitive {@code int} value for the week of the week-based-year.
     *
     * @return the week-of-week-based-year
     */
    public int getWeek() {
        return week;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the week-based-year has 53 weeks.
     * <p>
     * This determines if the year has 53 weeks, returning true.
     * If false, the year has 52 weeks.
     *
     * @return true if the year has 53 weeks, false otherwise
     */
    public boolean is53WeekYear() {
        return weekRange(year) == 53;
    }

    /**
     * Returns the length of the week-based-year.
     * <p>
     * This returns the length of the year in days, either 364 or 371.
     *
     * @return 364 if the year has 52 weeks, 371 if it has 53 weeks
     */
    public int lengthOfYear() {
        return (is53WeekYear() ? 371 : 364);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code YearWeek} with the week-based-year altered.
     * <p>
     * This returns a year-week with the specified week-based-year.
     * If the week of this instance is 53 and the new year does not have 53 weeks,
     * the week will be adjusted to be 52.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weekBasedYear  the week-based-year to set in the returned year-week
     * @return a {@code YearWeek} based on this year-week with the requested year, not null
     * @throws DateTimeException if the week-based-year value is invalid
     */
    public YearWeek withYear(int weekBasedYear) {
        if (week == 53 && weekRange(weekBasedYear) < 53) {
            return YearWeek.of(weekBasedYear, 52);
        }
        return with(weekBasedYear, week);
    }

    /**
     * Returns a copy of this {@code YearWeek} with the week altered.
     * <p>
     * This returns a year-week with the specified week-of-week-based-year.
     * If the new week is 53 and the year does not have 53 weeks, week one of the
     * following year is selected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param week  the week-of-week-based-year to set in the returned year-week
     * @return a {@code YearWeek} based on this year-week with the requested week, not null
     * @throws DateTimeException if the week-of-week-based-year value is invalid
     */
    public YearWeek withWeek(int week) {
        return with(year, week);
    }

    //-----------------------------------------------------------------------
    /**
     * Queries this year-week using the specified query.
     * <p>
     * This queries this year-week using the specified query strategy object.
     * The {@code TemporalQuery} object defines the logic to be used to
     * obtain the result. Read the documentation of the query to understand
     * what the result of this method will be.
     * <p>
     * The result of this method is obtained by invoking the
     * {@link TemporalQuery#queryFrom(TemporalAccessor)} method on the
     * specified query passing {@code this} as the argument.
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
        }
        return TemporalAccessor.super.query(query);
    }

    /**
     * Adjusts the specified temporal object to have this year-week.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with the week-based-year and week changed to be the same as this.
     * <p>
     * The adjustment is equivalent to using {@link Temporal#with(TemporalField, long)}
     * twice, passing {@link IsoFields#WEEK_BASED_YEAR} and
     * {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR} as the fields.
     * If the specified temporal object does not use the ISO calendar system then
     * a {@code DateTimeException} is thrown.
     * <p>
     * In most cases, it is clearer to reverse the calling pattern by using
     * {@link Temporal#with(TemporalAdjuster)}:
     * <pre>
     *   // these two lines are equivalent, but the second approach is recommended
     *   temporal = thisYearWeek.adjustInto(temporal);
     *   temporal = temporal.with(thisYearWeek);
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
        return temporal.with(WEEK_BASED_YEAR, year).with(WEEK_OF_WEEK_BASED_YEAR, week);
    }

    /**
     * Formats this year-week using the specified formatter.
     * <p>
     * This year-week will be passed to the formatter to produce a string.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted year-week string, not null
     * @throws DateTimeException if an error occurs during printing
     */
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Combines this year-week with a day-of-week to create a {@code LocalDate}.
     * <p>
     * This returns a {@code LocalDate} formed from this year-week and the specified day-of-Week.
     * <p>
     * This method can be used as part of a chain to produce a date:
     * <pre>
     *  LocalDate date = yearWeek.atDay(MONDAY);
     * </pre>
     *
     * @param dayOfWeek  the day-of-week to use, not null
     * @return the date formed from this year-week and the specified day, not null
     */
    public LocalDate atDay(DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        int correction = LocalDate.of(year, 1, 4).getDayOfWeek().getValue() + 3;
        int dayOfYear = week * 7 + dayOfWeek.getValue() - correction;
        if (dayOfYear > 0) {
            return LocalDate.ofYearDay(year, dayOfYear);
        } else {
            int daysOfPreviousYear = Year.isLeap(year - 1) ? 366 : 365;
            return LocalDate.ofYearDay(year - 1, daysOfPreviousYear + dayOfYear);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year-week to another
     * <p>
     * The comparison is based first on the value of the year, then on the value of the week.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param other  the other year-week to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(YearWeek other) {
        int cmp = (year - other.year);
        if (cmp == 0) {
            cmp = (week - other.week);
        }
        return cmp;
    }

    /**
     * Is this year-week after the specified year-week.
     *
     * @param other  the other year-week to compare to, not null
     * @return true if this is after the specified year-week
     */
    public boolean isAfter(YearWeek other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this year-week before the specified year-week.
     *
     * @param other  the other year-week to compare to, not null
     * @return true if this point is before the specified year-week
     */
    public boolean isBefore(YearWeek other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this year-week is equal to another year-week.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other year-week
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof YearWeek) {
            YearWeek other = (YearWeek) obj;
            return year == other.year && week == other.week;
        }
        return false;
    }

    /**
     * A hash code for this year-week.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return year ^ (week << 25);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this year-week as a {@code String}, such as {@code 2015-W13}.
     * <p>
     * The output will be in the format {@code YYYY-'W'ww}:
     *
     * @return a string representation of this year-week, not null
     */
    @Override
    public String toString() {
        int absYear = Math.abs(year);
        StringBuilder buf = new StringBuilder(10);
        if (absYear < 1000) {
            if (year < 0) {
                buf.append(year - 10000).deleteCharAt(1);
            } else {
                buf.append(year + 10000).deleteCharAt(0);
            }
        } else {
            if (year > 9999) {
                buf.append('+');
            }
            buf.append(year);
        }
        return buf.append(week < 10 ? "-W0" : "-W").append(week).toString();
    }

}
