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

import static java.time.temporal.ChronoField.AMPM_OF_DAY;
import static java.time.temporal.ChronoField.CLOCK_HOUR_OF_AMPM;
import static java.time.temporal.ChronoField.CLOCK_HOUR_OF_DAY;
import static java.time.temporal.ChronoField.HOUR_OF_AMPM;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoUnit.HALF_DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
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

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * An hour-minute, such as {@code 12:31}.
 * <p>
 * This class is similar to {@link LocalTime} but has a precision of minutes.
 * Seconds and nanoseconds cannot be represented by this class.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class HourMinute
        implements Temporal, TemporalAdjuster, Comparable<HourMinute>, Serializable {

    /**
     * The time of midnight at the start of the day, '00:00'.
     */
    public static final HourMinute MIDNIGHT = new HourMinute(0, 0);

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -2532872925L;
    /**
     * Parser.
     */
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .toFormatter();
    /**
     * Hours per day.
     */
    private static final int HOURS_PER_DAY = 24;
    /**
     * Minutes per hour.
     */
    private static final int MINUTES_PER_HOUR = 60;
    /**
     * Minutes per day.
     */
    private static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;

    /**
     * The hour-of-day.
     */
    private final int hour;
    /**
     * The minute-of-hour.
     */
    private final int minute;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current hour-minute from the system clock in the default time-zone.
     * <p>
     * This will query the {@link java.time.Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current hour-minute.
     * The zone and offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current hour-minute using the system clock and default time-zone, not null
     */
    public static HourMinute now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current hour-minute from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(java.time.ZoneId) system clock} to obtain the current hour-minute.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone  the zone ID to use, not null
     * @return the current hour-minute using the system clock, not null
     */
    public static HourMinute now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current hour-minute from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current hour-minute.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current hour-minute, not null
     */
    public static HourMinute now(Clock clock) {
        final LocalTime now = LocalTime.now(clock);  // called once
        return HourMinute.of(now.getHour(), now.getMinute());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code HourMinute} from a hour and minute.
     *
     * @param hour  the hour to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return the hour-minute, not null
     * @throws DateTimeException if either field value is invalid
     */
    public static HourMinute of(int hour, int minute) {
        HOUR_OF_DAY.checkValidValue(hour);
        MINUTE_OF_HOUR.checkValidValue(minute);
        return new HourMinute(hour, minute);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code HourMinute} from a temporal object.
     * <p>
     * This obtains a hour-minute based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code HourMinute}.
     * <p>
     * The conversion extracts the {@link ChronoField#HOUR_OF_DAY HOUR_OF_DAY} and
     * {@link ChronoField#MINUTE_OF_HOUR MINUTE_OF_HOUR} fields.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used in queries via method reference, {@code HourMinute::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the hour-minute, not null
     * @throws DateTimeException if unable to convert to a {@code HourMinute}
     */
    public static HourMinute from(TemporalAccessor temporal) {
        if (temporal instanceof HourMinute) {
            return (HourMinute) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            // need to use getLong() as JDK Parsed class get() doesn't work properly
            int hour = Math.toIntExact(temporal.getLong(HOUR_OF_DAY));
            int minute = Math.toIntExact(temporal.getLong(MINUTE_OF_HOUR));
            return of(hour, minute);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain HourMinute from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code HourMinute} from a text string such as {@code 12:31}.
     * <p>
     * The string must represent a valid hour-minute.
     * The format must be {@code HH:mm}.
     *
     * @param text  the text to parse such as "12:31", not null
     * @return the parsed hour-minute, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    @FromString
    public static HourMinute parse(CharSequence text) {
        return parse(text, PARSER);
    }

    /**
     * Obtains an instance of {@code HourMinute} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a hour-minute.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed hour-minute, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static HourMinute parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.parse(text, HourMinute::from);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param hour  the hour to represent, validated from 0 to 23
     * @param minute  the minute-of-hour to represent, validated from 0 to 59
     */
    private HourMinute(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Validates the input.
     *
     * @return the valid object, not null
     */
    private Object readResolve() {
        return of(hour, minute);
    }

    /**
     * Returns a copy of this hour-minute with the new hour and minute, checking
     * to see if a new object is in fact required.
     *
     * @param newYear  the hour to represent, validated from 0 to 23
     * @param newMinute  the minute-of-hour to represent, validated from 0 to 59
     * @return the hour-minute, not null
     */
    private HourMinute with(int newYear, int newMinute) {
        if (hour == newYear && minute == newMinute) {
            return this;
        }
        return new HourMinute(newYear, newMinute);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified field is supported.
     * <p>
     * This checks if this hour-minute can be queried for the specified field.
     * If false, then calling the {@link #range(TemporalField) range},
     * {@link #get(TemporalField) get} and {@link #with(TemporalField, long)}
     * methods will throw an exception.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The supported fields are:
     * <ul>
     * <li>{@code MINUTE_OF_HOUR}
     * <li>{@code MINUTE_OF_DAY}
     * <li>{@code HOUR_OF_AMPM}
     * <li>{@code CLOCK_HOUR_OF_AMPM}
     * <li>{@code HOUR_OF_DAY}
     * <li>{@code CLOCK_HOUR_OF_DAY}
     * <li>{@code AMPM_OF_DAY}
     * </ul>
     * All other {@code ChronoField} instances will return false.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.isSupportedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the field is supported is determined by the field.
     *
     * @param field  the field to check, null returns false
     * @return true if the field is supported on this hour-minute, false if not
     */
    @Override
    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            return field == MINUTE_OF_HOUR ||
                    field == MINUTE_OF_DAY ||
                    field == HOUR_OF_AMPM ||
                    field == CLOCK_HOUR_OF_AMPM ||
                    field == HOUR_OF_DAY ||
                    field == CLOCK_HOUR_OF_DAY ||
                    field == AMPM_OF_DAY;
        }
        return field != null && field.isSupportedBy(this);
    }

    /**
     * Checks if the specified unit is supported.
     * <p>
     * This checks if the specified unit can be added to, or subtracted from, this hour-minute.
     * If false, then calling the {@link #plus(long, TemporalUnit)} and
     * {@link #minus(long, TemporalUnit) minus} methods will throw an exception.
     * <p>
     * If the unit is a {@link ChronoUnit} then the query is implemented here.
     * The supported units are:
     * <ul>
     * <li>{@code MINUTES}
     * <li>{@code HOURS}
     * <li>{@code HALF_DAYS}
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
        if (unit instanceof ChronoUnit) {
            return unit == MINUTES || unit == HOURS || unit == HALF_DAYS;
        }
        return unit != null && unit.isSupportedBy(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the range of valid values for the specified field.
     * <p>
     * The range object expresses the minimum and maximum valid values for a field.
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
        return Temporal.super.range(field);
    }

    /**
     * Gets the value of the specified field from this hour-minute as an {@code int}.
     * <p>
     * This queries this hour-minute for the value for the specified field.
     * The returned value will always be within the valid range of values for the field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this hour-minute.
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
        if (field instanceof ChronoField) {
            return get0(field);
        }
        return Temporal.super.get(field);
    }

    /**
     * Gets the value of the specified field from this hour-minute as a {@code long}.
     * <p>
     * This queries this hour-minute for the value for the specified field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this hour-minute.
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
        if (field instanceof ChronoField) {
            return get0(field);
        }
        return field.getFrom(this);
    }

    private int get0(TemporalField field) {
        switch ((ChronoField) field) {
            case MINUTE_OF_HOUR:
                return minute;
            case MINUTE_OF_DAY:
                return hour * 60 + minute;
            case HOUR_OF_AMPM:
                return hour % 12;
            case CLOCK_HOUR_OF_AMPM:
                int ham = hour % 12;
                return (ham % 12 == 0 ? 12 : ham);
            case HOUR_OF_DAY:
                return hour;
            case CLOCK_HOUR_OF_DAY:
                return (hour == 0 ? 24 : hour);
            case AMPM_OF_DAY:
                return hour / 12;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour field, from 0 to 23.
     * <p>
     * This method returns the hour as an {@code int} from 0 to 23.
     *
     * @return the hour, from 0 to 23
     */
    public int getHour() {
        return hour;
    }

    /**
     * Gets the minute-of-hour field from 0 to 59.
     * <p>
     * This method returns the minute as an {@code int} from 0 to 59.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinute() {
        return minute;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted copy of this hour-minute.
     * <p>
     * This returns a {@code HourMinute} based on this one, with the hour-minute adjusted.
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
     * @return a {@code HourMinute} based on {@code this} with the adjustment made, not null
     * @throws DateTimeException if the adjustment cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public HourMinute with(TemporalAdjuster adjuster) {
        return (HourMinute) adjuster.adjustInto(this);
    }

    /**
     * Returns a copy of this hour-minute with the specified field set to a new value.
     * <p>
     * This returns a {@code HourMinute} based on this one, with the value
     * for the specified field changed.
     * This can be used to change any supported field, such as the hour or minute.
     * If it is not possible to set the value, because the field is not supported or for
     * some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the adjustment is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code MINUTE_OF_HOUR} -
     *  Returns a {@code HourMinute} with the specified minute-of-hour.
     *  The hour will be unchanged.
     * <li>{@code MINUTE_OF_DAY} -
     *  Returns a {@code HourMinute} with the specified minute-of-day.
     * <li>{@code HOUR_OF_AMPM} -
     *  Returns a {@code HourMinute} with the specified hour-of-am-pm.
     *  The AM/PM and minute-of-hour will be unchanged.
     * <li>{@code CLOCK_HOUR_OF_AMPM} -
     *  Returns a {@code HourMinute} with the specified clock-hour-of-am-pm.
     *  The AM/PM and minute-of-hour will be unchanged.
     * <li>{@code HOUR_OF_DAY} -
     *  Returns a {@code HourMinute} with the specified hour-of-day.
     *  The minute-of-hour will be unchanged.
     * <li>{@code CLOCK_HOUR_OF_DAY} -
     *  Returns a {@code HourMinute} with the specified clock-hour-of-day.
     *  The minute-of-hour will be unchanged.
     * <li>{@code AMPM_OF_DAY} -
     *  Returns a {@code HourMinute} with the specified AM/PM.
     *  The hour-of-am-pm and minute-of-hour will be unchanged.
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
     * @return a {@code HourMinute} based on {@code this} with the specified field set, not null
     * @throws DateTimeException if the field cannot be set
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public HourMinute with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case MINUTE_OF_HOUR:
                    return withMinute((int) newValue);
                case MINUTE_OF_DAY:
                    return plusMinutes(newValue - (hour * MINUTES_PER_HOUR + minute));
                case HOUR_OF_AMPM:
                    return plusHours(newValue - (hour % 12));
                case CLOCK_HOUR_OF_AMPM:
                    return plusHours((newValue == 12 ? 0 : newValue) - (hour % 12));
                case HOUR_OF_DAY:
                    return withHour((int) newValue);
                case CLOCK_HOUR_OF_DAY:
                    return withHour((int) (newValue == 24 ? 0 : newValue));
                case AMPM_OF_DAY:
                    return plusHours((newValue - (hour / 12)) * 12);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return field.adjustInto(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code HourMinute} with the hour altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour to set in the returned hour-minute, from 0 to 23
     * @return a {@code HourMinute} based on this hour-minute with the requested hour, not null
     * @throws DateTimeException if the hour value is invalid
     */
    public HourMinute withHour(int hour) {
        HOUR_OF_DAY.checkValidValue(hour);
        return with(hour, minute);
    }

    /**
     * Returns a copy of this {@code HourMinute} with the minute-of-hour altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minute  the minute-of-hour to set in the returned hour-minute, from 0 to 59
     * @return a {@code HourMinute} based on this hour-minute with the requested minute, not null
     * @throws DateTimeException if the minute-of-hour value is invalid
     */
    public HourMinute withMinute(int minute) {
        MINUTE_OF_HOUR.checkValidValue(minute);
        return with(hour, minute);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this hour-minute with the specified amount added.
     * <p>
     * This returns a {@code HourMinute} based on this one, with the specified amount added.
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
     * @return a {@code HourMinute} based on this hour-minute with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public HourMinute plus(TemporalAmount amountToAdd) {
        return (HourMinute) amountToAdd.addTo(this);
    }

    /**
     * Returns a copy of this hour-minute with the specified amount added.
     * <p>
     * This returns a {@code HourMinute} based on this one, with the amount
     * in terms of the unit added. If it is not possible to add the amount, because the
     * unit is not supported or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoUnit} then the addition is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code MINUTES} -
     *  Returns an {@code HourMinute} with the specified number of minutes added.
     *  This is equivalent to {@link #plusMinutes(long)}.
     * <li>{@code HOURS} -
     *  Returns an {@code HourMinute} with the specified number of hours added.
     *  This is equivalent to {@link #plusHours(long)}.
     * <li>{@code HALF_DAYS} -
     *  Returns an {@code HourMinute} with the specified number of half-days added.
     *  This is equivalent to {@link #plusHours(long)} with the amount
     *  multiplied by 12.
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
     * @return a {@code HourMinute} based on this hour-minute with the specified amount added, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public HourMinute plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case MINUTES:
                    return plusMinutes(amountToAdd);
                case HOURS:
                    return plusHours(amountToAdd);
                case HALF_DAYS:
                    return plusHours((amountToAdd % 2) * 12);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.addTo(this, amountToAdd);
    }

    /**
     * Returns a copy of this {@code HourMinute} with the specified number of hours added.
     * <p>
     * This adds the specified number of hours to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hoursToAdd  the hours to add, may be negative
     * @return an {@code HourMinute} based on this time with the hours added, not null
     */
    public HourMinute plusHours(long hoursToAdd) {
        if (hoursToAdd == 0) {
            return this;
        }
        int newHour = ((int) (hoursToAdd % HOURS_PER_DAY) + hour + HOURS_PER_DAY) % HOURS_PER_DAY;
        return with(newHour, minute);
    }

    /**
     * Returns a copy of this {@code HourMinute} with the specified number of minutes added.
     * <p>
     * This adds the specified number of minutes to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutesToAdd  the minutes to add, may be negative
     * @return an {@code HourMinute} based on this time with the minutes added, not null
     */
    public HourMinute plusMinutes(long minutesToAdd) {
        if (minutesToAdd == 0) {
            return this;
        }
        int mofd = hour * MINUTES_PER_HOUR + minute;
        int newMofd = ((int) (minutesToAdd % MINUTES_PER_DAY) + mofd + MINUTES_PER_DAY) % MINUTES_PER_DAY;
        if (mofd == newMofd) {
            return this;
        }
        int newHour = newMofd / MINUTES_PER_HOUR;
        int newMinute = newMofd % MINUTES_PER_HOUR;
        return with(newHour, newMinute);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this hour-minute with the specified amount subtracted.
     * <p>
     * This returns a {@code HourMinute} based on this one, with the specified amount subtracted.
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
     * @return a {@code HourMinute} based on this hour-minute with the subtraction made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public HourMinute minus(TemporalAmount amountToSubtract) {
        return (HourMinute) amountToSubtract.subtractFrom(this);
    }

    /**
     * Returns a copy of this hour-minute with the specified amount subtracted.
     * <p>
     * This returns a {@code HourMinute} based on this one, with the amount
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
     * @return a {@code HourMinute} based on this hour-minute with the specified amount subtracted, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public HourMinute minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    /**
     * Returns a copy of this hour-minute with the specified period in hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hoursToSubtract  the hours to subtract, may be negative
     * @return a {@code HourMinute} based on this hour-minute with the hours subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public HourMinute minusHours(long hoursToSubtract) {
        return (hoursToSubtract == Long.MIN_VALUE ? plusHours(Long.MAX_VALUE).plusHours(1) : plusHours(-hoursToSubtract));
    }

    /**
     * Returns a copy of this hour-minute with the specified period in minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutesToSubtract  the minutes to subtract, may be negative
     * @return a {@code HourMinute} based on this hour-minute with the minutes subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public HourMinute minusMinutes(long minutesToSubtract) {
        return (minutesToSubtract == Long.MIN_VALUE ? plusMinutes(Long.MAX_VALUE).plusMinutes(1) : plusMinutes(-minutesToSubtract));
    }

    //-----------------------------------------------------------------------
    /**
     * Queries this hour-minute using the specified query.
     * <p>
     * {@link TemporalQueries#localTime()} and {@link TemporalQueries#precision()} are directly supported.
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
        if (query == TemporalQueries.localTime()) {
            return (R) toLocalTime();
        } else if (query == TemporalQueries.precision()) {
            return (R) MINUTES;
        }
        return Temporal.super.query(query);
    }

    /**
     * Adjusts the specified temporal object to have this hour-minute.
     * Note that if the target has a second or nanosecond field, that is not altered by this method.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with the hour and minute changed to be the same as this.
     * <p>
     * The adjustment is equivalent to using {@link Temporal#with(TemporalField, long)}
     * passing {@link ChronoField#MINUTE_OF_DAY} as the field.
     * Note that this does not affect any second/nanosecond field in the target.
     * <p>
     * In most cases, it is clearer to reverse the calling pattern by using
     * {@link Temporal#with(TemporalAdjuster)}:
     * <pre>
     *   // these two lines are equivalent, but the second approach is recommended
     *   temporal = thisHourMinute.adjustInto(temporal);
     *   temporal = temporal.with(thisHourMinute);
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
        return temporal.with(MINUTE_OF_DAY, hour * MINUTES_PER_HOUR + minute);
    }

    /**
     * Calculates the amount of time until another hour-minute in terms of the specified unit.
     * <p>
     * This calculates the amount of time between two {@code HourMinute}
     * objects in terms of a single {@code TemporalUnit}.
     * The start and end points are {@code this} and the specified hour-minute.
     * The result will be negative if the end is before the start.
     * The {@code Temporal} passed to this method is converted to a
     * {@code HourMinute} using {@link #from(TemporalAccessor)}.
     * For example, the period in hours between two hour-minutes can be calculated
     * using {@code startHourMinute.until(endHourMinute, YEARS)}.
     * <p>
     * The calculation is implemented in this method for {@link ChronoUnit}.
     * The units {@code MINUTES}, {@code HOURS} and {@code HALF_DAYS} are supported.
     * Other {@code ChronoUnit} values will throw an exception.
     * <p>
     * If the unit is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.between(Temporal, Temporal)}
     * passing {@code this} as the first argument and the converted input temporal
     * as the second argument.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param endExclusive  the end date, exclusive, which is converted to a {@code HourMinute}, not null
     * @param unit  the unit to measure the amount in, not null
     * @return the amount of time between this hour-minute and the end hour-minute
     * @throws DateTimeException if the amount cannot be calculated, or the end
     *  temporal cannot be converted to a {@code HourMinute}
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        HourMinute end = HourMinute.from(endExclusive);
        long minutesUntil = (end.hour * MINUTES_PER_HOUR + end.minute) - (hour * MINUTES_PER_HOUR + minute);  // no overflow
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case MINUTES:
                    return minutesUntil;
                case HOURS:
                    return minutesUntil / MINUTES_PER_HOUR;
                case HALF_DAYS:
                    return minutesUntil / (12 * MINUTES_PER_HOUR);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.between(this, end);
    }

    /**
     * Formats this hour-minute using the specified formatter.
     * <p>
     * This hour-minute will be passed to the formatter to produce a string.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted hour-minute string, not null
     * @throws DateTimeException if an error occurs during printing
     */
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Combines this time with a date to create a {@code LocalDateTime}.
     * <p>
     * This returns a {@code LocalDateTime} formed from this time at the specified date.
     * All possible combinations of date and time are valid.
     *
     * @param date  the date to combine with, not null
     * @return the local date-time formed from this time and the specified date, not null
     */
    public LocalDateTime atDate(LocalDate date) {
        return LocalDateTime.of(date, toLocalTime());
    }

    /**
     * Combines this time with an offset to create an {@code OffsetTime}.
     * <p>
     * This returns an {@code OffsetTime} formed from this time at the specified offset.
     * All possible combinations of time and offset are valid.
     *
     * @param offset  the offset to combine with, not null
     * @return the offset time formed from this time and the specified offset, not null
     */
    public OffsetTime atOffset(ZoneOffset offset) {
        return OffsetTime.of(toLocalTime(), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the equivalent {@code LocalTime}.
     * <p>
     * This returns a {@code LocalTime} formed from this hour and minute.
     *
     * @return the equivalent local time, not null
     */
    public LocalTime toLocalTime() {
        return LocalTime.of(hour, minute);
    }

    //-------------------------------------------------------------------------
    /**
     * Compares this hour-minute to another
     * <p>
     * The comparison is based first on the value of the hour, then on the value of the minute.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param other  the other hour-minute to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(HourMinute other) {
        int cmp = (hour - other.hour);
        if (cmp == 0) {
            cmp = (minute - other.minute);
        }
        return cmp;
    }

    /**
     * Is this hour-minute after the specified hour-minute.
     *
     * @param other  the other hour-minute to compare to, not null
     * @return true if this is after the specified hour-minute
     */
    public boolean isAfter(HourMinute other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this hour-minute before the specified hour-minute.
     *
     * @param other  the other hour-minute to compare to, not null
     * @return true if this point is before the specified hour-minute
     */
    public boolean isBefore(HourMinute other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this hour-minute is equal to another hour-minute.
     * <p>
     * The comparison is based on the time-line position of the hour-minute.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other hour-minute
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HourMinute) {
            HourMinute other = (HourMinute) obj;
            return hour == other.hour && minute == other.minute;
        }
        return false;
    }

    /**
     * A hash code for this hour-minute.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return hour * MINUTES_PER_HOUR + minute;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this hour-minute as a {@code String}, such as {@code 12:31}.
     *
     * @return a string representation of this hour-minute, not null
     */
    @Override
    @ToString
    public String toString() {
        return new StringBuilder(5)
                .append(hour < 10 ? "0" : "").append(hour)
                .append(minute < 10 ? ":0" : ":").append(minute)
                .toString();
    }

}
