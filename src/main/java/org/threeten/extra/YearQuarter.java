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

import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.ChronoUnit.CENTURIES;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.ERAS;
import static java.time.temporal.ChronoUnit.MILLENNIA;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.time.temporal.IsoFields.DAY_OF_QUARTER;
import static java.time.temporal.IsoFields.QUARTER_OF_YEAR;
import static java.time.temporal.IsoFields.QUARTER_YEARS;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
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
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * A year-quarter in the ISO-8601 calendar system, such as {@code 2007-Q2}.
 * <p>
 * {@code YearQuarter} is an immutable date-time object that represents the combination
 * of a year and quarter. Any field that can be derived from a year and quarter can be obtained.
 * A quarter is defined by {@link Quarter} and {@link Month#firstMonthOfQuarter()} - Q1, Q2, Q3 and Q4.
 * Q1 is January to March, Q2 is April to June, Q3 is July to September and Q4 is October to December.
 * <p>
 * This class does not store or represent a day, time or time-zone.
 * For example, the value "2nd quarter 2007" can be stored in a {@code YearQuarter}.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which today's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * However, any application that makes use of historical dates, and requires them
 * to be accurate will find the ISO-8601 approach unsuitable.
 * Note that the ISO-8601 standard does not define or refer to quarters.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class YearQuarter
        implements Temporal, TemporalAdjuster, Comparable<YearQuarter>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 4183400860270640070L;
    /**
     * Parser.
     */
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendLiteral('Q')
            .appendValue(QUARTER_OF_YEAR, 1)
            .toFormatter();

    /**
     * The year.
     */
    private final int year;
    /**
     * The quarter-of-year, not null.
     */
    private final Quarter quarter;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current year-quarter from the system clock in the default time-zone.
     * <p>
     * This will query the {@link java.time.Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current year-quarter.
     * The zone and offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current year-quarter using the system clock and default time-zone, not null
     */
    public static YearQuarter now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current year-quarter from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(java.time.ZoneId) system clock} to obtain the current year-quarter.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone  the zone ID to use, not null
     * @return the current year-quarter using the system clock, not null
     */
    public static YearQuarter now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current year-quarter from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current year-quarter.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current year-quarter, not null
     */
    public static YearQuarter now(Clock clock) {
        final LocalDate now = LocalDate.now(clock);  // called once
        return YearQuarter.of(now.getYear(), Quarter.from(now.getMonth()));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearQuarter} from a year and quarter.
     *
     * @param year  the year to represent, not null
     * @param quarter  the quarter-of-year to represent, not null
     * @return the year-quarter, not null
     */
    public static YearQuarter of(Year year, Quarter quarter) {
        return of(year.getValue(), quarter);
    }

    /**
     * Obtains an instance of {@code YearQuarter} from a year and quarter.
     *
     * @param year  the year to represent, not null
     * @param quarter  the quarter-of-year to represent, from 1 to 4
     * @return the year-quarter, not null
     * @throws DateTimeException if the quarter value is invalid
     */
    public static YearQuarter of(Year year, int quarter) {
        return of(year.getValue(), Quarter.of(quarter));
    }

    /**
     * Obtains an instance of {@code YearQuarter} from a year and quarter.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param quarter  the quarter-of-year to represent, not null
     * @return the year-quarter, not null
     * @throws DateTimeException if the year value is invalid
     */
    public static YearQuarter of(int year, Quarter quarter) {
        YEAR.checkValidValue(year);
        Objects.requireNonNull(quarter, "quarter");
        return new YearQuarter(year, quarter);
    }

    /**
     * Obtains an instance of {@code YearQuarter} from a year and quarter.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param quarter  the quarter-of-year to represent, from 1 to 4
     * @return the year-quarter, not null
     * @throws DateTimeException if either field value is invalid
     */
    public static YearQuarter of(int year, int quarter) {
        YEAR.checkValidValue(year);
        return new YearQuarter(year, Quarter.of(quarter));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearQuarter} from a temporal object.
     * <p>
     * This obtains a year-quarter based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code YearQuarter}.
     * <p>
     * The conversion extracts the {@link ChronoField#YEAR YEAR} and
     * {@link IsoFields#QUARTER_OF_YEAR QUARTER_OF_YEAR} fields.
     * The extraction is only permitted if the temporal object has an ISO
     * chronology, or can be converted to a {@code LocalDate}.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used in queries via method reference, {@code YearQuarter::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the year-quarter, not null
     * @throws DateTimeException if unable to convert to a {@code YearQuarter}
     */
    public static YearQuarter from(TemporalAccessor temporal) {
        if (temporal instanceof YearQuarter) {
            return (YearQuarter) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            TemporalAccessor adjusted =
                    !IsoChronology.INSTANCE.equals(Chronology.from(temporal)) ? LocalDate.from(temporal) : temporal;
            // need to use getLong() as JDK Parsed class get() doesn't work properly
            int year = Math.toIntExact(adjusted.getLong(YEAR));
            int qoy = Math.toIntExact(adjusted.getLong(QUARTER_OF_YEAR));
            return of(year, qoy);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain YearQuarter from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearQuarter} from a text string such as {@code 2007-Q2}.
     * <p>
     * The string must represent a valid year-quarter.
     * The format must be {@code uuuu-'Q'Q} where the 'Q' is case insensitive.
     * Years outside the range 0000 to 9999 must be prefixed by the plus or minus symbol.
     *
     * @param text  the text to parse such as "2007-Q2", not null
     * @return the parsed year-quarter, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    @FromString
    public static YearQuarter parse(CharSequence text) {
        return parse(text, PARSER);
    }

    /**
     * Obtains an instance of {@code YearQuarter} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a year-quarter.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed year-quarter, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static YearQuarter parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.parse(text, YearQuarter::from);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param quarter  the quarter-of-year to represent, validated not null
     */
    private YearQuarter(int year, Quarter quarter) {
        this.year = year;
        this.quarter = quarter;
    }

    /**
     * Validates the input.
     *
     * @return the valid object, not null
     */
    private Object readResolve() {
        return of(year, quarter);
    }

    /**
     * Returns a copy of this year-quarter with the new year and quarter, checking
     * to see if a new object is in fact required.
     *
     * @param newYear  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param newQuarter  the quarter-of-year to represent, validated not null
     * @return the year-quarter, not null
     */
    private YearQuarter with(int newYear, Quarter newQuarter) {
        if (year == newYear && quarter == newQuarter) {
            return this;
        }
        return new YearQuarter(newYear, newQuarter);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified field is supported.
     * <p>
     * This checks if this year-quarter can be queried for the specified field.
     * If false, then calling the {@link #range(TemporalField) range},
     * {@link #get(TemporalField) get} and {@link #with(TemporalField, long)}
     * methods will throw an exception.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The supported fields are:
     * <ul>
     * <li>{@code QUARTER_OF_YEAR}
     * <li>{@code YEAR_OF_ERA}
     * <li>{@code YEAR}
     * <li>{@code ERA}
     * </ul>
     * All other {@code ChronoField} instances will return false.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.isSupportedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the field is supported is determined by the field.
     *
     * @param field  the field to check, null returns false
     * @return true if the field is supported on this year-quarter, false if not
     */
    @Override
    public boolean isSupported(TemporalField field) {
        if (field == QUARTER_OF_YEAR) {
            return true;
        } else if (field instanceof ChronoField) {
            return field == YEAR || field == YEAR_OF_ERA || field == ERA;
        }
        return field != null && field.isSupportedBy(this);
    }

    /**
     * Checks if the specified unit is supported.
     * <p>
     * This checks if the specified unit can be added to, or subtracted from, this year-quarter.
     * If false, then calling the {@link #plus(long, TemporalUnit)} and
     * {@link #minus(long, TemporalUnit) minus} methods will throw an exception.
     * <p>
     * If the unit is a {@link ChronoUnit} then the query is implemented here.
     * The supported units are:
     * <ul>
     * <li>{@code QUARTER_YEARS}
     * <li>{@code YEARS}
     * <li>{@code DECADES}
     * <li>{@code CENTURIES}
     * <li>{@code MILLENNIA}
     * <li>{@code ERAS}
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
        if (unit == QUARTER_YEARS) {
            return true;
        } else if (unit instanceof ChronoUnit) {
            return unit == YEARS || unit == DECADES || unit == CENTURIES || unit == MILLENNIA || unit == ERAS;
        }
        return unit != null && unit.isSupportedBy(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the range of valid values for the specified field.
     * <p>
     * The range object expresses the minimum and maximum valid values for a field.
     * This year-quarter is used to enhance the accuracy of the returned range.
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
        if (field == QUARTER_OF_YEAR) {
            return QUARTER_OF_YEAR.range();
        }
        if (field == YEAR_OF_ERA) {
            return (getYear() <= 0 ? ValueRange.of(1, Year.MAX_VALUE + 1) : ValueRange.of(1, Year.MAX_VALUE));
        }
        return Temporal.super.range(field);
    }

    /**
     * Gets the value of the specified field from this year-quarter as an {@code int}.
     * <p>
     * This queries this year-quarter for the value for the specified field.
     * The returned value will always be within the valid range of values for the field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this year-quarter.
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
        if (field == QUARTER_OF_YEAR) {
            return quarter.getValue();
        } else if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case YEAR_OF_ERA:
                    return (year < 1 ? 1 - year : year);
                case YEAR:
                    return year;
                case ERA:
                    return (year < 1 ? 0 : 1);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return Temporal.super.get(field);
    }

    /**
     * Gets the value of the specified field from this year-quarter as a {@code long}.
     * <p>
     * This queries this year-quarter for the value for the specified field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this year-quarter.
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
            return quarter.getValue();
        } else if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case YEAR_OF_ERA:
                    return (year < 1 ? 1 - year : year);
                case YEAR:
                    return year;
                case ERA:
                    return (year < 1 ? 0 : 1);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return field.getFrom(this);
    }

    private long getProlepticQuarter() {
        return year * 4L + (quarter.getValue() - 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     * <p>
     * The year returned by this method is proleptic as per {@code get(YEAR)}.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the quarter-of-year field from 1 to 4.
     * <p>
     * This method returns the quarter as an {@code int} from 1 to 4.
     * Application code is frequently clearer if the enum {@link Quarter}
     * is used by calling {@link #getQuarter()}.
     *
     * @return the quarter-of-year, from 1 to 4
     * @see #getQuarter()
     */
    public int getQuarterValue() {
        return quarter.getValue();
    }

    /**
     * Gets the quarter-of-year field using the {@code Quarter} enum.
     * <p>
     * This method returns the enum {@link Quarter} for the quarter.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link Quarter#getValue() int value}.
     *
     * @return the quarter-of-year, not null
     * @see #getQuarterValue()
     */
    public Quarter getQuarter() {
        return quarter;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the ISO proleptic
     * calendar system rules.
     * <p>
     * This method applies the current rules for leap years across the whole time-line.
     * In general, a year is a leap year if it is divisible by four without
     * remainder. However, years divisible by 100, are not leap years, with
     * the exception of years divisible by 400 which are.
     * <p>
     * For example, 1904 is a leap year it is divisible by 4.
     * 1900 was not a leap year as it is divisible by 100, however 2000 was a
     * leap year as it is divisible by 400.
     * <p>
     * The calculation is proleptic - applying the same rules into the far future and far past.
     * This is historically inaccurate, but is correct for the ISO-8601 standard.
     *
     * @return true if the year is leap, false otherwise
     */
    public boolean isLeapYear() {
        return IsoChronology.INSTANCE.isLeapYear(year);
    }

    /**
     * Checks if the day-of-quarter is valid for this year-quarter.
     * <p>
     * This method checks whether this year and quarter and the input day form
     * a valid date.
     *
     * @param dayOfQuarter  the day-of-quarter to validate, from 1 to 92, invalid value returns false
     * @return true if the day is valid for this year-quarter
     */
    public boolean isValidDay(int dayOfQuarter) {
        return dayOfQuarter >= 1 && dayOfQuarter <= lengthOfQuarter();
    }

    /**
     * Returns the length of the quarter, taking account of the year.
     * <p>
     * This returns the length of the quarter in days.
     *
     * @return the length of the quarter in days, from 90 to 92
     */
    public int lengthOfQuarter() {
        return quarter.length(isLeapYear());
    }

    /**
     * Returns the length of the year.
     * <p>
     * This returns the length of the year in days, either 365 or 366.
     *
     * @return 366 if the year is leap, 365 otherwise
     */
    public int lengthOfYear() {
        return (isLeapYear() ? 366 : 365);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted copy of this year-quarter.
     * <p>
     * This returns a {@code YearQuarter} based on this one, with the year-quarter adjusted.
     * The adjustment takes place using the specified adjuster strategy object.
     * Read the documentation of the adjuster to understand what adjustment will be made.
     * <p>
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the year-quarter to the next quarter that
     * Halley's comet will pass the Earth.
     * <p>
     * The result of this method is obtained by invoking the
     * {@link TemporalAdjuster#adjustInto(Temporal)} method on the
     * specified adjuster passing {@code this} as the argument.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return a {@code YearQuarter} based on {@code this} with the adjustment made, not null
     * @throws DateTimeException if the adjustment cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearQuarter with(TemporalAdjuster adjuster) {
        return (YearQuarter) adjuster.adjustInto(this);
    }

    /**
     * Returns a copy of this year-quarter with the specified field set to a new value.
     * <p>
     * This returns a {@code YearQuarter} based on this one, with the value
     * for the specified field changed.
     * This can be used to change any supported field, such as the year or quarter.
     * If it is not possible to set the value, because the field is not supported or for
     * some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the adjustment is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code QUARTER_OF_YEAR} -
     *  Returns a {@code YearQuarter} with the specified quarter-of-year.
     *  The year will be unchanged.
     * <li>{@code YEAR_OF_ERA} -
     *  Returns a {@code YearQuarter} with the specified year-of-era
     *  The quarter and era will be unchanged.
     * <li>{@code YEAR} -
     *  Returns a {@code YearQuarter} with the specified year.
     *  The quarter will be unchanged.
     * <li>{@code ERA} -
     *  Returns a {@code YearQuarter} with the specified era.
     *  The quarter and year-of-era will be unchanged.
     * </ul>
     * <p>
     * In all cases, if the new value is outside the valid range of values for the field
     * then a {@code DateTimeException} will be thrown.
     * <p>
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
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
     * @return a {@code YearQuarter} based on {@code this} with the specified field set, not null
     * @throws DateTimeException if the field cannot be set
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearQuarter with(TemporalField field, long newValue) {
        if (field == QUARTER_OF_YEAR) {
            return withQuarter(QUARTER_OF_YEAR.range().checkValidIntValue(newValue, QUARTER_OF_YEAR));
        } else if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case YEAR_OF_ERA:
                    return withYear((int) (year < 1 ? 1 - newValue : newValue));
                case YEAR:
                    return withYear((int) newValue);
                case ERA:
                    return (getLong(ERA) == newValue ? this : withYear(1 - year));
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return field.adjustInto(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code YearQuarter} with the year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned year-quarter, from MIN_YEAR to MAX_YEAR
     * @return a {@code YearQuarter} based on this year-quarter with the requested year, not null
     * @throws DateTimeException if the year value is invalid
     */
    public YearQuarter withYear(int year) {
        YEAR.checkValidValue(year);
        return with(year, quarter);
    }

    /**
     * Returns a copy of this {@code YearQuarter} with the quarter-of-year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param quarter  the quarter-of-year to set in the returned year-quarter, from 1 to 4
     * @return a {@code YearQuarter} based on this year-quarter with the requested quarter, not null
     * @throws DateTimeException if the quarter-of-year value is invalid
     */
    public YearQuarter withQuarter(int quarter) {
        QUARTER_OF_YEAR.range().checkValidValue(quarter, QUARTER_OF_YEAR);
        return with(year, Quarter.of(quarter));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this year-quarter with the specified amount added.
     * <p>
     * This returns a {@code YearQuarter} based on this one, with the specified amount added.
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
     * @return a {@code YearQuarter} based on this year-quarter with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearQuarter plus(TemporalAmount amountToAdd) {
        return (YearQuarter) amountToAdd.addTo(this);
    }

    /**
     * Returns a copy of this year-quarter with the specified amount added.
     * <p>
     * This returns a {@code YearQuarter} based on this one, with the amount
     * in terms of the unit added. If it is not possible to add the amount, because the
     * unit is not supported or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoUnit} then the addition is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code QUARTER_YEARS} -
     *  Returns a {@code YearQuarter} with the specified number of quarters added.
     *  This is equivalent to {@link #plusQuarters(long)}.
     * <li>{@code YEARS} -
     *  Returns a {@code YearQuarter} with the specified number of years added.
     *  This is equivalent to {@link #plusYears(long)}.
     * <li>{@code DECADES} -
     *  Returns a {@code YearQuarter} with the specified number of decades added.
     *  This is equivalent to calling {@link #plusYears(long)} with the amount
     *  multiplied by 10.
     * <li>{@code CENTURIES} -
     *  Returns a {@code YearQuarter} with the specified number of centuries added.
     *  This is equivalent to calling {@link #plusYears(long)} with the amount
     *  multiplied by 100.
     * <li>{@code MILLENNIA} -
     *  Returns a {@code YearQuarter} with the specified number of millennia added.
     *  This is equivalent to calling {@link #plusYears(long)} with the amount
     *  multiplied by 1,000.
     * <li>{@code ERAS} -
     *  Returns a {@code YearQuarter} with the specified number of eras added.
     *  Only two eras are supported so the amount must be one, zero or minus one.
     *  If the amount is non-zero then the year is changed such that the year-of-era
     *  is unchanged.
     * </ul>
     * <p>
     * All other {@code ChronoUnit} instances will throw an {@code UnsupportedTemporalTypeException}.
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
     * @return a {@code YearQuarter} based on this year-quarter with the specified amount added, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearQuarter plus(long amountToAdd, TemporalUnit unit) {
        if (unit == QUARTER_YEARS) {
            return plusQuarters(amountToAdd);
        } else if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case YEARS:
                    return plusYears(amountToAdd);
                case DECADES:
                    return plusYears(Math.multiplyExact(amountToAdd, 10));
                case CENTURIES:
                    return plusYears(Math.multiplyExact(amountToAdd, 100));
                case MILLENNIA:
                    return plusYears(Math.multiplyExact(amountToAdd, 1000));
                case ERAS:
                    return with(ERA, Math.addExact(getLong(ERA), amountToAdd));
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.addTo(this, amountToAdd);
    }

    /**
     * Returns a copy of this year-quarter with the specified period in years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearsToAdd  the years to add, may be negative
     * @return a {@code YearQuarter} based on this year-quarter with the years added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public YearQuarter plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = YEAR.checkValidIntValue(year + yearsToAdd);  // safe overflow
        return with(newYear, quarter);
    }

    /**
     * Returns a copy of this year-quarter with the specified period in quarters added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param quartersToAdd  the quarters to add, may be negative
     * @return a {@code YearQuarter} based on this year-quarter with the quarters added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public YearQuarter plusQuarters(long quartersToAdd) {
        if (quartersToAdd == 0) {
            return this;
        }
        long quarterCount = year * 4L + (quarter.getValue() - 1);
        long calcQuarters = quarterCount + quartersToAdd;  // safe overflow
        int newYear = YEAR.checkValidIntValue(Math.floorDiv(calcQuarters, 4));
        int newQuarter = (int) Math.floorMod(calcQuarters, 4L) + 1;
        return with(newYear, Quarter.of(newQuarter));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this year-quarter with the specified amount subtracted.
     * <p>
     * This returns a {@code YearQuarter} based on this one, with the specified amount subtracted.
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
     * @return a {@code YearQuarter} based on this year-quarter with the subtraction made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearQuarter minus(TemporalAmount amountToSubtract) {
        return (YearQuarter) amountToSubtract.subtractFrom(this);
    }

    /**
     * Returns a copy of this year-quarter with the specified amount subtracted.
     * <p>
     * This returns a {@code YearQuarter} based on this one, with the amount
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
     * @return a {@code YearQuarter} based on this year-quarter with the specified amount subtracted, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearQuarter minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    /**
     * Returns a copy of this year-quarter with the specified period in years subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearsToSubtract  the years to subtract, may be negative
     * @return a {@code YearQuarter} based on this year-quarter with the years subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public YearQuarter minusYears(long yearsToSubtract) {
        return (yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract));
    }

    /**
     * Returns a copy of this year-quarter with the specified period in quarters subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param quartersToSubtract  the quarters to subtract, may be negative
     * @return a {@code YearQuarter} based on this year-quarter with the quarters subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public YearQuarter minusQuarters(long quartersToSubtract) {
        return (quartersToSubtract == Long.MIN_VALUE ? plusQuarters(Long.MAX_VALUE).plusQuarters(1) : plusQuarters(-quartersToSubtract));
    }

    //-----------------------------------------------------------------------
    /**
     * Queries this year-quarter using the specified query.
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
        return Temporal.super.query(query);
    }

    /**
     * Adjusts the specified temporal object to have this year-quarter.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with the year and quarter changed to be the same as this.
     * <p>
     * The adjustment is equivalent to using {@link Temporal#plus(long, TemporalUnit)}
     * passing the number of quarters to adjust by.
     * If the specified temporal object does not use the ISO calendar system then
     * a {@code DateTimeException} is thrown.
     * <p>
     * In most cases, it is clearer to reverse the calling pattern by using
     * {@link Temporal#with(TemporalAdjuster)}:
     * <pre>
     *   // these two lines are equivalent, but the second approach is recommended
     *   temporal = thisYearQuarter.adjustInto(temporal);
     *   temporal = temporal.with(thisYearQuarter);
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
        long newProlepticQuarter = getProlepticQuarter();
        long oldProlepticQuarter = temporal.get(YEAR) * 4L + (temporal.get(QUARTER_OF_YEAR) - 1);
        return temporal.plus(newProlepticQuarter - oldProlepticQuarter, QUARTER_YEARS);
    }

    /**
     * Calculates the amount of time until another year-quarter in terms of the specified unit.
     * <p>
     * This calculates the amount of time between two {@code YearQuarter}
     * objects in terms of a single {@code TemporalUnit}.
     * The start and end points are {@code this} and the specified year-quarter.
     * The result will be negative if the end is before the start.
     * The {@code Temporal} passed to this method is converted to a
     * {@code YearQuarter} using {@link #from(TemporalAccessor)}.
     * For example, the period in years between two year-quarters can be calculated
     * using {@code startYearQuarter.until(endYearQuarter, YEARS)}.
     * <p>
     * The calculation returns a whole number, representing the number of
     * complete units between the two year-quarters.
     * For example, the period in decades between 2012-Q3 and 2032-Q2
     * will only be one decade as it is one quarter short of two decades.
     * <p>
     * There are two equivalent ways of using this method.
     * The first is to invoke this method.
     * The second is to use {@link TemporalUnit#between(Temporal, Temporal)}:
     * <pre>
     *   // these two lines are equivalent
     *   amount = start.until(end, QUARTER_YEARS);
     *   amount = QUARTER_YEARS.between(start, end);
     * </pre>
     * The choice should be made based on which makes the code more readable.
     * <p>
     * The calculation is implemented in this method for {@link ChronoUnit}.
     * The units {@code QUARTER_YEARS}, {@code YEARS}, {@code DECADES},
     * {@code CENTURIES}, {@code MILLENNIA} and {@code ERAS} are supported.
     * Other {@code ChronoUnit} values will throw an exception.
     * <p>
     * If the unit is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.between(Temporal, Temporal)}
     * passing {@code this} as the first argument and the converted input temporal
     * as the second argument.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param endExclusive  the end date, exclusive, which is converted to a {@code YearQuarter}, not null
     * @param unit  the unit to measure the amount in, not null
     * @return the amount of time between this year-quarter and the end year-quarter
     * @throws DateTimeException if the amount cannot be calculated, or the end
     *  temporal cannot be converted to a {@code YearQuarter}
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        YearQuarter end = YearQuarter.from(endExclusive);
        long quartersUntil = end.getProlepticQuarter() - getProlepticQuarter();  // no overflow
        if (unit == QUARTER_YEARS) {
            return quartersUntil;
        } else if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case YEARS:
                    return quartersUntil / 4;
                case DECADES:
                    return quartersUntil / 40;
                case CENTURIES:
                    return quartersUntil / 400;
                case MILLENNIA:
                    return quartersUntil / 4000;
                case ERAS:
                    return end.getLong(ERA) - getLong(ERA);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.between(this, end);
    }

    /**
     * Returns a sequential ordered stream of year-quarter. The returned stream starts from this year-quarter
     * (inclusive) and goes to {@code endExclusive} (exclusive) by an incremental step of 1 {@code QUARTER_YEARS}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param endExclusive  the end year-quarter, exclusive, not null
     * @return a sequential {@code Stream} for the range of {@code YearQuarter} values
     * @throws IllegalArgumentException if end year-quarter is before this year-quarter
     */
    public Stream<YearQuarter> quartersUntil(YearQuarter endExclusive) {
        if (endExclusive.isBefore(this)) {
            throw new IllegalArgumentException(endExclusive + " < " + this);
        }
        long intervalLength = until(endExclusive, QUARTER_YEARS);
        return LongStream.range(0, intervalLength).mapToObj(n -> plusQuarters(n));
    }

    /**
     * Formats this year-quarter using the specified formatter.
     * <p>
     * This year-quarter will be passed to the formatter to produce a string.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted year-quarter string, not null
     * @throws DateTimeException if an error occurs during printing
     */
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Combines this year-quarter with a day-of-quarter to create a {@code LocalDate}.
     * <p>
     * This returns a {@code LocalDate} formed from this year-quarter and the specified day-of-quarter.
     * <p>
     * The day-of-quarter value must be valid for the year-quarter.
     * <p>
     * This method can be used as part of a chain to produce a date:
     * <pre>
     *  LocalDate date = yearQuarter.atDay(day);
     * </pre>
     *
     * @param dayOfQuarter  the day-of-quarter to use, from 1 to 92
     * @return the date formed from this year-quarter and the specified day, not null
     * @throws DateTimeException if the day is invalid for the year-quarter
     * @see #isValidDay(int)
     */
    public LocalDate atDay(int dayOfQuarter) {
        ValueRange.of(1, lengthOfQuarter()).checkValidValue(dayOfQuarter, DAY_OF_QUARTER);
        boolean leap = Year.isLeap(year);
        Month month = quarter.firstMonth();
        int dom = dayOfQuarter;
        while (dom > month.length(leap)) {
            dom -= month.length(leap);
            month = month.plus(1);
        }
        return LocalDate.of(year, month, dom);
    }

    /**
     * Returns a {@code LocalDate} at the end of the quarter.
     * <p>
     * This returns a {@code LocalDate} based on this year-quarter.
     * The day-of-quarter is set to the last valid day of the quarter, taking
     * into account leap years.
     * <p>
     * This method can be used as part of a chain to produce a date:
     * <pre>
     *  LocalDate date = year.atQuarter(quarter).atEndOfQuarter();
     * </pre>
     *
     * @return the last valid date of this year-quarter, not null
     */
    public LocalDate atEndOfQuarter() {
        Month month = quarter.firstMonth().plus(2);
        return LocalDate.of(year, month, month.maxLength());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year-quarter to another
     * <p>
     * The comparison is based first on the value of the year, then on the value of the quarter.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param other  the other year-quarter to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(YearQuarter other) {
        int cmp = (year - other.year);
        if (cmp == 0) {
            cmp = quarter.compareTo(other.quarter);
        }
        return cmp;
    }

    /**
     * Is this year-quarter after the specified year-quarter.
     *
     * @param other  the other year-quarter to compare to, not null
     * @return true if this is after the specified year-quarter
     */
    public boolean isAfter(YearQuarter other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this year-quarter before the specified year-quarter.
     *
     * @param other  the other year-quarter to compare to, not null
     * @return true if this point is before the specified year-quarter
     */
    public boolean isBefore(YearQuarter other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this year-quarter is equal to another year-quarter.
     * <p>
     * The comparison is based on the time-line position of the year-quarters.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other year-quarter
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof YearQuarter) {
            YearQuarter other = (YearQuarter) obj;
            return year == other.year && quarter == other.quarter;
        }
        return false;
    }

    /**
     * A hash code for this year-quarter.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return year ^ (quarter.getValue() << 27);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this year-quarter as a {@code String}, such as {@code 2007-Q2}.
     * <p>
     * The output will be in the format {@code uuuu-'Q'Q}:
     *
     * @return a string representation of this year-quarter, not null
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
        return buf.append('-').append(quarter).toString();
    }

}
