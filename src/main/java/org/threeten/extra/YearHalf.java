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
import static org.threeten.extra.TemporalFields.DAY_OF_HALF;
import static org.threeten.extra.TemporalFields.HALF_OF_YEAR;
import static org.threeten.extra.TemporalFields.HALF_YEARS;

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
 * A year-half in the ISO-8601 calendar system, such as {@code 2007-H2}.
 * <p>
 * {@code YearHalf} is an immutable date-time object that represents the combination
 * of a year and a half-year. Any field that can be derived from a year and a half-year can be obtained.
 * A half is defined by {@link Half} - H1 and H2.
 * H1 is January to June, H2 is July to December.
 * <p>
 * This class does not store or represent a day, time or time-zone.
 * For example, the value "2nd half 2007" can be stored in a {@code YearHalf}.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which today's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * However, any application that makes use of historical dates, and requires them
 * to be accurate will find the ISO-8601 approach unsuitable.
 * Note that the ISO-8601 standard does not define or refer to halves.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class YearHalf
        implements Temporal, TemporalAdjuster, Comparable<YearHalf>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 782467825761518L;
    /**
     * Parser.
     */
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendLiteral('H')
            .appendValue(HALF_OF_YEAR, 1)
            .toFormatter();

    /**
     * The year.
     */
    private final int year;
    /**
     * The half-of-year, not null.
     */
    private final Half half;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current year-half from the system clock in the default time-zone.
     * <p>
     * This will query the {@link java.time.Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current year-half.
     * The zone and offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current year-half using the system clock and default time-zone, not null
     */
    public static YearHalf now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current year-half from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(java.time.ZoneId) system clock} to obtain the current year-half.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone  the zone ID to use, not null
     * @return the current year-half using the system clock, not null
     */
    public static YearHalf now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current year-half from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current year-half.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current year-half, not null
     */
    public static YearHalf now(Clock clock) {
        final LocalDate now = LocalDate.now(clock);  // called once
        return YearHalf.of(now.getYear(), Half.from(now.getMonth()));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearHalf} from a year and half.
     *
     * @param year  the year to represent, not null
     * @param half  the half-of-year to represent, not null
     * @return the year-half, not null
     */
    public static YearHalf of(Year year, Half half) {
        return of(year.getValue(), half);
    }

    /**
     * Obtains an instance of {@code YearHalf} from a year and half.
     *
     * @param year  the year to represent, not null
     * @param half  the half-of-year to represent, from 1 to 2
     * @return the year-half, not null
     * @throws DateTimeException if the half value is invalid
     */
    public static YearHalf of(Year year, int half) {
        return of(year.getValue(), Half.of(half));
    }

    /**
     * Obtains an instance of {@code YearHalf} from a year and half.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param half  the half-of-year to represent, not null
     * @return the year-half, not null
     * @throws DateTimeException if the year value is invalid
     */
    public static YearHalf of(int year, Half half) {
        YEAR.checkValidValue(year);
        Objects.requireNonNull(half, "half");
        return new YearHalf(year, half);
    }

    /**
     * Obtains an instance of {@code YearHalf} from a year and half.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param half  the half-of-year to represent, from 1 to 2
     * @return the year-half, not null
     * @throws DateTimeException if either field value is invalid
     */
    public static YearHalf of(int year, int half) {
        YEAR.checkValidValue(year);
        return new YearHalf(year, Half.of(half));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearHalf} from a temporal object.
     * <p>
     * This obtains a year-half based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code YearHalf}.
     * <p>
     * The conversion extracts the {@link ChronoField#YEAR YEAR} and
     * {@link TemporalFields#HALF_OF_YEAR HALF_OF_YEAR} fields.
     * The extraction is only permitted if the temporal object has an ISO
     * chronology, or can be converted to a {@code LocalDate}.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used in queries via method reference, {@code YearHalf::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the year-half, not null
     * @throws DateTimeException if unable to convert to a {@code YearHalf}
     */
    public static YearHalf from(TemporalAccessor temporal) {
        if (temporal instanceof YearHalf) {
            return (YearHalf) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            TemporalAccessor adjusted =
                    !IsoChronology.INSTANCE.equals(Chronology.from(temporal)) ? LocalDate.from(temporal) : temporal;
            // need to use getLong() as JDK Parsed class get() doesn't work properly
            int year = Math.toIntExact(adjusted.getLong(YEAR));
            int hoy = Math.toIntExact(adjusted.getLong(HALF_OF_YEAR));
            return of(year, hoy);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain YearHalf from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearHalf} from a text string such as {@code 2007-H2}.
     * <p>
     * The string must represent a valid year-half.
     * The format must be {@code uuuu-'H'H} where the 'H' is case insensitive.
     * Years outside the range 0000 to 9999 must be prefixed by the plus or minus symbol.
     *
     * @param text  the text to parse such as "2007-H2", not null
     * @return the parsed year-half, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    @FromString
    public static YearHalf parse(CharSequence text) {
        return parse(text, PARSER);
    }

    /**
     * Obtains an instance of {@code YearHalf} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a year-half.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed year-half, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static YearHalf parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.parse(text, YearHalf::from);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param half  the half-of-year to represent, validated not null
     */
    private YearHalf(int year, Half half) {
        this.year = year;
        this.half = half;
    }

    /**
     * Validates the input.
     *
     * @return the valid object, not null
     */
    private Object readResolve() {
        return of(year, half);
    }

    /**
     * Returns a copy of this year-half with the new year and half, checking
     * to see if a new object is in fact required.
     *
     * @param newYear  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param newHalf  the half-of-year to represent, validated not null
     * @return the year-half, not null
     */
    private YearHalf with(int newYear, Half newHalf) {
        if (year == newYear && half == newHalf) {
            return this;
        }
        return new YearHalf(newYear, newHalf);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified field is supported.
     * <p>
     * This checks if this year-half can be queried for the specified field.
     * If false, then calling the {@link #range(TemporalField) range},
     * {@link #get(TemporalField) get} and {@link #with(TemporalField, long)}
     * methods will throw an exception.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The supported fields are:
     * <ul>
     * <li>{@code HALF_OF_YEAR}
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
     * @return true if the field is supported on this year-half, false if not
     */
    @Override
    public boolean isSupported(TemporalField field) {
        if (field == HALF_OF_YEAR) {
            return true;
        } else if (field instanceof ChronoField) {
            return field == YEAR || field == YEAR_OF_ERA || field == ERA;
        }
        return field != null && field.isSupportedBy(this);
    }

    /**
     * Checks if the specified unit is supported.
     * <p>
     * This checks if the specified unit can be added to, or subtracted from, this year-half.
     * If false, then calling the {@link #plus(long, TemporalUnit)} and
     * {@link #minus(long, TemporalUnit) minus} methods will throw an exception.
     * <p>
     * If the unit is a {@link ChronoUnit} then the query is implemented here.
     * The supported units are:
     * <ul>
     * <li>{@code HALF_YEARS}
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
        if (unit == HALF_YEARS) {
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
     * This year-half is used to enhance the accuracy of the returned range.
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
        if (field == HALF_OF_YEAR) {
            return HALF_OF_YEAR.range();
        }
        if (field == YEAR_OF_ERA) {
            return (getYear() <= 0 ? ValueRange.of(1, Year.MAX_VALUE + 1) : ValueRange.of(1, Year.MAX_VALUE));
        }
        return Temporal.super.range(field);
    }

    /**
     * Gets the value of the specified field from this year-half as an {@code int}.
     * <p>
     * This queries this year-half for the value for the specified field.
     * The returned value will always be within the valid range of values for the field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this year-half.
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
        if (field == HALF_OF_YEAR) {
            return half.getValue();
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
     * Gets the value of the specified field from this year-half as a {@code long}.
     * <p>
     * This queries this year-half for the value for the specified field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this year-half.
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
        if (field == HALF_OF_YEAR) {
            return half.getValue();
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

    private long getProlepticHalf() {
        return year * 2L + (half.getValue() - 1);
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
     * Gets the half-of-year field from 1 to 2.
     * <p>
     * This method returns the half as an {@code int} from 1 to 2.
     * Application code is frequently clearer if the enum {@link Half}
     * is used by calling {@link #getHalf()}.
     *
     * @return the half-of-year, from 1 to 2
     * @see #getHalf()
     */
    public int getHalfValue() {
        return half.getValue();
    }

    /**
     * Gets the half-of-year field using the {@code Half} enum.
     * <p>
     * This method returns the enum {@link Half} for the half.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link Half#getValue() int value}.
     *
     * @return the half-of-year, not null
     * @see #getHalfValue()
     */
    public Half getHalf() {
        return half;
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
     * Checks if the day-of-half is valid for this year-half.
     * <p>
     * This method checks whether this year and half and the input day form
     * a valid date.
     *
     * @param dayOfHalf  the day-of-half to validate, from 1 to 181, 182 or 184, invalid value returns false
     * @return true if the day is valid for this year-half
     */
    public boolean isValidDay(int dayOfHalf) {
        return dayOfHalf >= 1 && dayOfHalf <= lengthOfHalf();
    }

    /**
     * Returns the length of the half, taking account of the year.
     * <p>
     * This returns the length of the half in days.
     *
     * @return the length of the half in days, 181, 182 or 184
     */
    public int lengthOfHalf() {
        return half.length(isLeapYear());
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
     * Returns an adjusted copy of this year-half.
     * <p>
     * This returns a {@code YearHalf} based on this one, with the year-half adjusted.
     * The adjustment takes place using the specified adjuster strategy object.
     * Read the documentation of the adjuster to understand what adjustment will be made.
     * <p>
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the year-half to the next half that
     * Halley's comet will pass the Earth.
     * <p>
     * The result of this method is obtained by invoking the
     * {@link TemporalAdjuster#adjustInto(Temporal)} method on the
     * specified adjuster passing {@code this} as the argument.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return a {@code YearHalf} based on {@code this} with the adjustment made, not null
     * @throws DateTimeException if the adjustment cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearHalf with(TemporalAdjuster adjuster) {
        return (YearHalf) adjuster.adjustInto(this);
    }

    /**
     * Returns a copy of this year-half with the specified field set to a new value.
     * <p>
     * This returns a {@code YearHalf} based on this one, with the value
     * for the specified field changed.
     * This can be used to change any supported field, such as the year or half.
     * If it is not possible to set the value, because the field is not supported or for
     * some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the adjustment is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code HALF_OF_YEAR} -
     *  Returns a {@code YearHalf} with the specified half-of-year.
     *  The year will be unchanged.
     * <li>{@code YEAR_OF_ERA} -
     *  Returns a {@code YearHalf} with the specified year-of-era
     *  The half and era will be unchanged.
     * <li>{@code YEAR} -
     *  Returns a {@code YearHalf} with the specified year.
     *  The half will be unchanged.
     * <li>{@code ERA} -
     *  Returns a {@code YearHalf} with the specified era.
     *  The half and year-of-era will be unchanged.
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
     * @return a {@code YearHalf} based on {@code this} with the specified field set, not null
     * @throws DateTimeException if the field cannot be set
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearHalf with(TemporalField field, long newValue) {
        if (field == HALF_OF_YEAR) {
            return withHalf(HALF_OF_YEAR.range().checkValidIntValue(newValue, HALF_OF_YEAR));
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
     * Returns a copy of this {@code YearHalf} with the year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned year-half, from MIN_YEAR to MAX_YEAR
     * @return a {@code YearHalf} based on this year-half with the requested year, not null
     * @throws DateTimeException if the year value is invalid
     */
    public YearHalf withYear(int year) {
        YEAR.checkValidValue(year);
        return with(year, half);
    }

    /**
     * Returns a copy of this {@code YearHalf} with the half-of-year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param half  the half-of-year to set in the returned year-half, from 1 to 2
     * @return a {@code YearHalf} based on this year-half with the requested half, not null
     * @throws DateTimeException if the half-of-year value is invalid
     */
    public YearHalf withHalf(int half) {
        HALF_OF_YEAR.range().checkValidValue(half, HALF_OF_YEAR);
        return with(year, Half.of(half));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this year-half with the specified amount added.
     * <p>
     * This returns a {@code YearHalf} based on this one, with the specified amount added.
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
     * @return a {@code YearHalf} based on this year-half with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearHalf plus(TemporalAmount amountToAdd) {
        return (YearHalf) amountToAdd.addTo(this);
    }

    /**
     * Returns a copy of this year-half with the specified amount added.
     * <p>
     * This returns a {@code YearHalf} based on this one, with the amount
     * in terms of the unit added. If it is not possible to add the amount, because the
     * unit is not supported or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoUnit} then the addition is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code HALF_YEARS} -
     *  Returns a {@code YearHalf} with the specified number of halves added.
     *  This is equivalent to {@link #plusHalves(long)}.
     * <li>{@code YEARS} -
     *  Returns a {@code YearHalf} with the specified number of years added.
     *  This is equivalent to {@link #plusYears(long)}.
     * <li>{@code DECADES} -
     *  Returns a {@code YearHalf} with the specified number of decades added.
     *  This is equivalent to calling {@link #plusYears(long)} with the amount
     *  multiplied by 10.
     * <li>{@code CENTURIES} -
     *  Returns a {@code YearHalf} with the specified number of centuries added.
     *  This is equivalent to calling {@link #plusYears(long)} with the amount
     *  multiplied by 100.
     * <li>{@code MILLENNIA} -
     *  Returns a {@code YearHalf} with the specified number of millennia added.
     *  This is equivalent to calling {@link #plusYears(long)} with the amount
     *  multiplied by 1,000.
     * <li>{@code ERAS} -
     *  Returns a {@code YearHalf} with the specified number of eras added.
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
     * @return a {@code YearHalf} based on this year-half with the specified amount added, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearHalf plus(long amountToAdd, TemporalUnit unit) {
        if (unit == HALF_YEARS) {
            return plusHalves(amountToAdd);
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
     * Returns a copy of this year-half with the specified period in years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearsToAdd  the years to add, may be negative
     * @return a {@code YearHalf} based on this year-half with the years added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public YearHalf plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = YEAR.checkValidIntValue(year + yearsToAdd);  // safe overflow
        return with(newYear, half);
    }

    /**
     * Returns a copy of this year-half with the specified period in halves added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param halvesToAdd  the halves to add, may be negative
     * @return a {@code YearHalf} based on this year-half with the halves added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public YearHalf plusHalves(long halvesToAdd) {
        if (halvesToAdd == 0) {
            return this;
        }
        long halfCount = year * 2L + (half.getValue() - 1);
        long calcHalves = halfCount + halvesToAdd;  // safe overflow
        int newYear = YEAR.checkValidIntValue(Math.floorDiv(calcHalves, 2));
        int newHalf = (int) Math.floorMod(calcHalves, 2L) + 1;
        return with(newYear, Half.of(newHalf));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this year-half with the specified amount subtracted.
     * <p>
     * This returns a {@code YearHalf} based on this one, with the specified amount subtracted.
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
     * @return a {@code YearHalf} based on this year-half with the subtraction made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearHalf minus(TemporalAmount amountToSubtract) {
        return (YearHalf) amountToSubtract.subtractFrom(this);
    }

    /**
     * Returns a copy of this year-half with the specified amount subtracted.
     * <p>
     * This returns a {@code YearHalf} based on this one, with the amount
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
     * @return a {@code YearHalf} based on this year-half with the specified amount subtracted, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public YearHalf minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    /**
     * Returns a copy of this year-half with the specified period in years subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearsToSubtract  the years to subtract, may be negative
     * @return a {@code YearHalf} based on this year-half with the years subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public YearHalf minusYears(long yearsToSubtract) {
        return (yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract));
    }

    /**
     * Returns a copy of this year-half with the specified period in halves subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param halvesToSubtract  the halves to subtract, may be negative
     * @return a {@code YearHalf} based on this year-half with the halves subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public YearHalf minusHalves(long halvesToSubtract) {
        return (halvesToSubtract == Long.MIN_VALUE ? plusHalves(Long.MAX_VALUE).plusHalves(1) : plusHalves(-halvesToSubtract));
    }

    //-----------------------------------------------------------------------
    /**
     * Queries this year-half using the specified query.
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
            return (R) HALF_YEARS;
        }
        return Temporal.super.query(query);
    }

    /**
     * Adjusts the specified temporal object to have this year-half.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with the year and half changed to be the same as this.
     * <p>
     * The adjustment is equivalent to using {@link Temporal#plus(long, TemporalUnit)}
     * passing the number of halves to adjust by.
     * If the specified temporal object does not use the ISO calendar system then
     * a {@code DateTimeException} is thrown.
     * <p>
     * In most cases, it is clearer to reverse the calling pattern by using
     * {@link Temporal#with(TemporalAdjuster)}:
     * <pre>
     *   // these two lines are equivalent, but the second approach is recommended
     *   temporal = thisYearHalf.adjustInto(temporal);
     *   temporal = temporal.with(thisYearHalf);
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
        long newProlepticHalf = getProlepticHalf();
        long oldProlepticHalf = temporal.get(YEAR) * 2L + (temporal.get(HALF_OF_YEAR) - 1);
        return temporal.plus(newProlepticHalf - oldProlepticHalf, HALF_YEARS);
    }

    /**
     * Calculates the amount of time until another year-half in terms of the specified unit.
     * <p>
     * This calculates the amount of time between two {@code YearHalf}
     * objects in terms of a single {@code TemporalUnit}.
     * The start and end points are {@code this} and the specified year-half.
     * The result will be negative if the end is before the start.
     * The {@code Temporal} passed to this method is converted to a
     * {@code YearHalf} using {@link #from(TemporalAccessor)}.
     * For example, the period in years between two year-halves can be calculated
     * using {@code startYearHalf.until(endYearHalf, YEARS)}.
     * <p>
     * The calculation returns a whole number, representing the number of
     * complete units between the two year-halves.
     * For example, the period in decades between 2012-H2 and 2032-H1
     * will only be one decade as it is one half short of two decades.
     * <p>
     * There are two equivalent ways of using this method.
     * The first is to invoke this method.
     * The second is to use {@link TemporalUnit#between(Temporal, Temporal)}:
     * <pre>
     *   // these two lines are equivalent
     *   amount = start.until(end, HALF_YEARS);
     *   amount = HALF_YEARS.between(start, end);
     * </pre>
     * The choice should be made based on which makes the code more readable.
     * <p>
     * The calculation is implemented in this method for {@link ChronoUnit}.
     * The units {@code HALF_YEARS}, {@code YEARS}, {@code DECADES},
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
     * @param endExclusive  the end date, exclusive, which is converted to a {@code YearHalf}, not null
     * @param unit  the unit to measure the amount in, not null
     * @return the amount of time between this year-half and the end year-half
     * @throws DateTimeException if the amount cannot be calculated, or the end
     *  temporal cannot be converted to a {@code YearHalf}
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        YearHalf end = YearHalf.from(endExclusive);
        long halvesUntil = end.getProlepticHalf() - getProlepticHalf();  // no overflow
        if (unit == HALF_YEARS) {
            return halvesUntil;
        } else if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case YEARS:
                    return halvesUntil / 2;
                case DECADES:
                    return halvesUntil / 20;
                case CENTURIES:
                    return halvesUntil / 200;
                case MILLENNIA:
                    return halvesUntil / 2000;
                case ERAS:
                    return end.getLong(ERA) - getLong(ERA);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.between(this, end);
    }

    /**
     * Returns a sequential ordered stream of year-half. The returned stream starts from this year-half
     * (inclusive) and goes to {@code endExclusive} (exclusive) by an incremental step of 1 {@code HALF_YEARS}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param endExclusive  the end year-half, exclusive, not null
     * @return a sequential {@code Stream} for the range of {@code YearHalf} values
     * @throws IllegalArgumentException if end year-half is before this year-half
     */
    public Stream<YearHalf> halvesUntil(YearHalf endExclusive) {
        if (endExclusive.isBefore(this)) {
            throw new IllegalArgumentException(endExclusive + " < " + this);
        }
        long intervalLength = until(endExclusive, HALF_YEARS);
        return LongStream.range(0, intervalLength).mapToObj(n -> plusHalves(n));
    }

    /**
     * Formats this year-half using the specified formatter.
     * <p>
     * This year-half will be passed to the formatter to produce a string.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted year-half string, not null
     * @throws DateTimeException if an error occurs during printing
     */
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Combines this year-half with a day-of-half to create a {@code LocalDate}.
     * <p>
     * This returns a {@code LocalDate} formed from this year-half and the specified day-of-half.
     * <p>
     * The day-of-half value must be valid for the year-half.
     * <p>
     * This method can be used as part of a chain to produce a date:
     * <pre>
     *  LocalDate date = yearHalf.atDay(day);
     * </pre>
     *
     * @param dayOfHalf  the day-of-half to use, from 1 to 184
     * @return the date formed from this year-half and the specified day, not null
     * @throws DateTimeException if the day is invalid for the year-half
     * @see #isValidDay(int)
     */
    public LocalDate atDay(int dayOfHalf) {
        ValueRange.of(1, lengthOfHalf()).checkValidValue(dayOfHalf, DAY_OF_HALF);
        boolean leap = Year.isLeap(year);
        Month month = half.firstMonth();
        int dom = dayOfHalf;
        while (dom > month.length(leap)) {
            dom -= month.length(leap);
            month = month.plus(1);
        }
        return LocalDate.of(year, month, dom);
    }

    /**
     * Returns a {@code LocalDate} at the end of the half.
     * <p>
     * This returns a {@code LocalDate} based on this year-half.
     * The day-of-half is set to the last valid day of the half, taking
     * into account leap years.
     * <p>
     * This method can be used as part of a chain to produce a date:
     * <pre>
     *  LocalDate date = year.atHalf(half).atEndOfHalf();
     * </pre>
     *
     * @return the last valid date of this year-half, not null
     */
    public LocalDate atEndOfHalf() {
        Month month = half.firstMonth().plus(5);
        return LocalDate.of(year, month, month.maxLength());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year-half to another
     * <p>
     * The comparison is based first on the value of the year, then on the value of the half.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param other  the other year-half to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(YearHalf other) {
        int cmp = (year - other.year);
        if (cmp == 0) {
            cmp = half.compareTo(other.half);
        }
        return cmp;
    }

    /**
     * Is this year-half after the specified year-half.
     *
     * @param other  the other year-half to compare to, not null
     * @return true if this is after the specified year-half
     */
    public boolean isAfter(YearHalf other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this year-half before the specified year-half.
     *
     * @param other  the other year-half to compare to, not null
     * @return true if this point is before the specified year-half
     */
    public boolean isBefore(YearHalf other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this year-half is equal to another year-half.
     * <p>
     * The comparison is based on the time-line position of the year-halves.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other year-half
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof YearHalf) {
            YearHalf other = (YearHalf) obj;
            return year == other.year && half == other.half;
        }
        return false;
    }

    /**
     * A hash code for this year-half.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return year ^ (half.getValue() << 28);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this year-half as a {@code String}, such as {@code 2007-H2}.
     *
     * @return a string representation of this year-half, not null
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
        return buf.append('-').append(half).toString();
    }

}
