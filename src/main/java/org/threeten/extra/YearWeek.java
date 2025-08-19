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

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.IsoFields.WEEK_BASED_YEAR;
import static java.time.temporal.IsoFields.WEEK_BASED_YEARS;
import static java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

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
        implements Temporal, TemporalAdjuster, Comparable<YearWeek>, Serializable {

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
     * Obtains an instance of {@code YearWeek} from a year and week.
     * <p>
     * If the week is 53 and the year does not have 53 weeks, week one of the following
     * year is selected.
     * <p>
     * Note that this class is based on the week-based-year which aligns to Monday to Sunday weeks,
     * whereas {@code Year} is intended to represent standard years aligned from January to December.
     * This difference may be seen at the start and/or end of the year.
     * This method treats the standard year as though it is the week-based-year.
     * Thus, {@code YearWeek.of(Year.of(2020), 1)} creates an object where Monday and Tuesday of the week
     * are actually the last two days of 2019.
     *
     * @param year  the year to represent, not null
     * @param week  the week-of-week-based-year to represent, from 1 to 53
     * @return the year-week, not null
     * @throws DateTimeException if the week value is invalid
     */
    public static YearWeek of(Year year, int week) {
        return of(year.getValue(), week);
    }

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
        // 53 weeks if year starts on Thursday, or Wed in a leap year
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
            if (!IsoChronology.INSTANCE.equals(Chronology.from(temporal))) {
                temporal = LocalDate.from(temporal);
            }
            // need to use getLong() as JDK Parsed class get() doesn't work properly
            int year = Math.toIntExact(temporal.getLong(WEEK_BASED_YEAR));
            int week = Math.toIntExact(temporal.getLong(WEEK_OF_WEEK_BASED_YEAR));
            return of(year, week);
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
    @FromString
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
     * If false, then calling the {@link #range(TemporalField) range} and
     * {@link #get(TemporalField) get} methods will throw an exception.
     * <p>
     * The supported fields are:
     * <ul>
     * <li>{@code WEEK_OF_WEEK_BASED_YEAR}
     * <li>{@code WEEK_BASED_YEAR}
     * </ul>
     * All other {@code ChronoField} instances will return false.
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

    /**
     * Checks if the specified unit is supported.
     * <p>
     * This checks if the specified unit can be added to, or subtracted from, this date-time.
     * If false, then calling the {@link #plus(long, TemporalUnit)} and
     * {@link #minus(long, TemporalUnit) minus} methods will throw an exception.
     * <p>
     * The supported units are:
     * <ul>
     * <li>{@code WEEKS}
     * <li>{@code WEEK_BASED_YEARS}
     * </ul>
     * All other {@code ChronoUnit} instances will return false.
     * <p>
     * If the unit is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.isSupportedBy(Temporal)}
     * passing {@code this} as the argument.
     * Whether the unit is supported is determined by the unit.
     *
     * @param unit  the unit to check, null returns false
     * @return true if the unit can be added/subtracted, false if not
     */
    @Override
    public boolean isSupported(TemporalUnit unit) {
        if (unit == WEEKS || unit == WEEK_BASED_YEARS) {
            return true;
        } else if (unit instanceof ChronoUnit) {
            return false;
        }
        return unit != null && unit.isSupportedBy(this);
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
        return Temporal.super.range(field);
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
        return Temporal.super.get(field);
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
     * <p>
     * Note that the ISO week-based-year does not align with the standard Gregorian/ISO calendar year.
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
     * Returns an adjusted copy of this year-week.
     * <p>
     * This returns a {@code YearWeek}, based on this one, with the year-week adjusted.
     * The adjustment takes place using the specified adjuster strategy object.
     * Read the documentation of the adjuster to understand what adjustment will be made.
     * <p>
     * The result of this method is obtained by invoking the
     * {@link TemporalAdjuster#adjustInto(Temporal)} method on the
     * specified adjuster passing {@code this} as the argument.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return a {@code YearWeek} based on {@code this} with the adjustment made, not null
     * @throws DateTimeException if the adjustment cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearWeek with(TemporalAdjuster adjuster) {
        return (YearWeek) adjuster.adjustInto(this);
    }

    /**
     * Returns a copy of this year-week with the specified field set to a new value.
     * <p>
     * This returns a {@code YearWeek}, based on this one, with the value
     * for the specified field changed.
     * This can be used to change any supported field, such as the year or week.
     * If it is not possible to set the value, because the field is not supported or for
     * some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the adjustment is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code WEEK_OF_WEEK_BASED_YEAR} -
     *  Returns a {@code YearWeek} with the specified week-of-year set as per {@link #withWeek(int)}.
     * <li>{@code WEEK_BASED_YEAR} -
     *  Returns a {@code YearWeek} with the specified year set as per {@link #withYear(int)}.
     * </ul>
     * <p>
     * All {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.adjustInto(Temporal, long)}
     * passing {@code this} as the argument. In this case, the field determines
     * whether and how to adjust the instant.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the result, not null
     * @param newValue  the new value of the field in the result
     * @return a {@code YearWeek} based on {@code this} with the specified field set, not null
     * @throws DateTimeException if the field cannot be set
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearWeek with(TemporalField field, long newValue) {
        if (field == WEEK_OF_WEEK_BASED_YEAR) {
            return withWeek(WEEK_OF_WEEK_BASED_YEAR.range().checkValidIntValue(newValue, WEEK_OF_WEEK_BASED_YEAR));
        } else if (field == WEEK_BASED_YEAR) {
            return withYear(WEEK_BASED_YEAR.range().checkValidIntValue(newValue, WEEK_BASED_YEAR));
        } else if (field instanceof ChronoField) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.adjustInto(this, newValue);
    }

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
     * Returns a copy of this year-week with the specified amount added.
     * <p>
     * This returns a {@code YearWeek}, based on this one, with the specified amount added.
     * The amount is typically {@link Period} but may be any other type implementing
     * the {@link TemporalAmount} interface.
     * <p>
     * The calculation is delegated to the amount object by calling
     * {@link TemporalAmount#addTo(Temporal)}. The amount implementation is free
     * to implement the addition in any way it wishes, however it typically
     * calls back to {@link #plus(long, TemporalUnit)}. Consult the documentation
     * of the amount implementation to determine if it can be successfully added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, not null
     * @return a {@code YearWeek} based on this year-week with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearWeek plus(TemporalAmount amountToAdd) {
        return (YearWeek) amountToAdd.addTo(this);
    }

    /**
     * Returns a copy of this year-week with the specified amount added.
     * <p>
     * This returns a {@code YearWeek}, based on this one, with the amount
     * in terms of the unit added. If it is not possible to add the amount, because the
     * unit is not supported or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoUnit} then the addition is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code WEEKS} -
     *  Returns a {@code YearWeek} with the weeks added as per {@link #plusWeeks(long)}.
     * <li>{@code WEEK_BASED_YEARS} -
     *  Returns a {@code YearWeek} with the years added as per {@link #plusYears(long)}.
     * </ul>
     * <p>
     * All {@code ChronoUnit} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.addTo(Temporal, long)}
     * passing {@code this} as the argument. In this case, the unit determines
     * whether and how to perform the addition.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount of the unit to add to the result, may be negative
     * @param unit  the unit of the amount to add, not null
     * @return a {@code YearWeek} based on this year-week with the specified amount added, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearWeek plus(long amountToAdd, TemporalUnit unit) {
        if (unit == WEEKS) {
            return plusWeeks(amountToAdd);
        } else if (unit == WEEK_BASED_YEARS) {
            return plusYears(amountToAdd);
        } else if (unit instanceof ChronoUnit) {
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
        return unit.addTo(this, amountToAdd);
    }

    /**
     * Returns a copy of this year-week with the specified number of years added.
     * <p>
     * If the week of this instance is 53 and the new year does not have 53 weeks,
     * the week will be adjusted to be 52.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param yearsToAdd  the years to add, may be negative
     * @return the year-week with the years added, not null
     */
    public YearWeek plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = Math.toIntExact(Math.addExact(year, yearsToAdd));
        return withYear(newYear);
    }

    /**
     * Returns a copy of this year-week with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param weeksToAdd  the weeks to add, may be negative
     * @return the year-week with the weeks added, not null
     */
    public YearWeek plusWeeks(long weeksToAdd) {
        if (weeksToAdd == 0) {
            return this;
        }
        LocalDate mondayOfWeek = atDay(MONDAY).plusWeeks(weeksToAdd);
        return YearWeek.from(mondayOfWeek);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this year-week with the specified amount subtracted.
     * <p>
     * This returns a {@code YearWeek}, based on this one, with the specified amount subtracted.
     * The amount is typically {@link Period} but may be any other type implementing
     * the {@link TemporalAmount} interface.
     * <p>
     * The calculation is delegated to the amount object by calling
     * {@link TemporalAmount#subtractFrom(Temporal)}. The amount implementation is free
     * to implement the subtraction in any way it wishes, however it typically
     * calls back to {@link #minus(long, TemporalUnit)}. Consult the documentation
     * of the amount implementation to determine if it can be successfully subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount to subtract, not null
     * @return a {@code YearWeek} based on this year-week with the subtraction made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearWeek minus(TemporalAmount amountToSubtract) {
        return (YearWeek) amountToSubtract.subtractFrom(this);
    }

    /**
     * Returns a copy of this year-week with the specified amount subtracted.
     * <p>
     * This returns a {@code YearWeek}, based on this one, with the amount
     * in terms of the unit subtracted. If it is not possible to subtract the amount,
     * because the unit is not supported or for some other reason, an exception is thrown.
     * <p>
     * This method is equivalent to {@link #plus(long, TemporalUnit)} with the amount negated.
     * See that method for a full description of how addition, and thus subtraction, works.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount of the unit to subtract from the result, may be negative
     * @param unit  the unit of the amount to subtract, not null
     * @return a {@code YearWeek} based on this year-week with the specified amount subtracted, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearWeek minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    /**
     * Returns a copy of this year-week with the specified number of years subtracted.
     * <p>
     * If the week of this instance is 53 and the new year does not have 53 weeks,
     * the week will be adjusted to be 52.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param yearsToSubtract  the years to subtract, may be negative
     * @return the year-week with the years subtracted, not null
     */
    public YearWeek minusYears(long yearsToSubtract) {
        if (yearsToSubtract == 0) {
            return this;
        }
        int newYear = Math.toIntExact(Math.subtractExact(year, yearsToSubtract));
        return withYear(newYear);
    }

    /**
     * Returns a copy of this year-week with the specified number of weeks subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param weeksToSubtract  the weeks to subtract, may be negative
     * @return the year-week with the weeks subtracted, not null
     */
    public YearWeek minusWeeks(long weeksToSubtract) {
        if (weeksToSubtract == 0) {
            return this;
        }
        LocalDate mondayOfWeek = atDay(MONDAY).minusWeeks(weeksToSubtract);
        return YearWeek.from(mondayOfWeek);
    }

    //-----------------------------------------------------------------------
    /**
     * Queries this year-week using the specified query.
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
            return (R) WEEKS;
        }
        return Temporal.super.query(query);
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
        if (!Chronology.from(temporal).equals(IsoChronology.INSTANCE)) {
            throw new DateTimeException("Adjustment only supported on ISO date-time");
        }
        return temporal.with(WEEK_BASED_YEAR, year).with(WEEK_OF_WEEK_BASED_YEAR, week);
    }

    /**
     * Calculates the amount of time until another year-week in terms of the specified unit.
     * <p>
     * This calculates the amount of time between two {@code YearWeek}
     * objects in terms of a single {@code TemporalUnit}.
     * The start and end points are {@code this} and the specified year-week.
     * The result will be negative if the end is before the start.
     * The {@code Temporal} passed to this method is converted to a
     * {@code YearWeek} using {@link #from(TemporalAccessor)}.
     * For example, the period in years between two year-weeks can be calculated
     * using {@code startYearWeek.until(endYearWeek, YEARS)}.
     * <p>
     * The calculation returns a whole number, representing the number of
     * complete units between the two year-weeks.
     * For example, the period in years between 2012-W23 and 2032-W22
     * will only be 9 years as it is one week short of 10 years.
     * <p>
     * There are two equivalent ways of using this method.
     * The first is to invoke this method.
     * The second is to use {@link TemporalUnit#between(Temporal, Temporal)}:
     * <pre>
     *   // these two lines are equivalent
     *   amount = start.until(end, WEEKS);
     *   amount = WEEKS.between(start, end);
     * </pre>
     * The choice should be made based on which makes the code more readable.
     * <p>
     * The calculation is implemented in this method for units {@code WEEKS}
     * and {@code WEEK_BASED_YEARS}.
     * Other {@code ChronoUnit} values will throw an exception.
     * <p>
     * If the unit is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.between(Temporal, Temporal)}
     * passing {@code this} as the first argument and the converted input temporal
     * as the second argument.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param endExclusive  the end date, exclusive, which is converted to a {@code YearWeek}, not null
     * @param unit  the unit to measure the amount in, not null
     * @return the amount of time between this year-week and the end year-week
     * @throws DateTimeException if the amount cannot be calculated, or the end
     *  temporal cannot be converted to a {@code YearWeek}
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        YearWeek end = YearWeek.from(endExclusive);
        if (unit == WEEKS) {
            return daysUntil(end);
        } else if (unit == WEEK_BASED_YEARS) {
            return yearsUntil(end);
        } else if (unit instanceof ChronoUnit) {
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
        return unit.between(this, end);
    }

    private long daysUntil(YearWeek end) {
        LocalDate startDate = this.atDay(MONDAY);
        LocalDate endDate = end.atDay(MONDAY);
        long days = endDate.toEpochDay() - startDate.toEpochDay();
        return days / 7;
    }

    private long yearsUntil(YearWeek end) {
        long yearsDiff = end.year - this.year;
        if (yearsDiff > 0 && end.week < this.week) {
            return yearsDiff - 1;
        }
        if (yearsDiff < 0 && end.week > this.week) {
            return yearsDiff + 1;
        }
        return yearsDiff;
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
        int maxDaysOfYear = Year.isLeap(year) ? 366 : 365;
        if (dayOfYear > maxDaysOfYear) {
            return LocalDate.ofYearDay(year + 1, dayOfYear - maxDaysOfYear);
        }
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
    @ToString
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
