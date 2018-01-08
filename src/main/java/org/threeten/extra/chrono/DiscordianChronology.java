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
package org.threeten.extra.chrono;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.chrono.AbstractChronology;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The Discordian calendar system.
 * <p>
 * This chronology defines the rules of the proleptic Discordian calendar system.
 * The Discordian differs from the Gregorian in terms of month and week lengths, with an offset year.
 * Dates are aligned such that each Discordian year starts with each ISO year, with an offset index.
 * <p>
 * This class is not proleptic. It implements Discordian rules only since YOLD 1 (ISO BCE 1166).
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There is one era, the current 'Year of Our Lady of Discord' (YOLD).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year one.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the current era.
 * <li>month-of-year - There are 5 months in a Discordian year, numbered from 1 to 5.
 * <li>day-of-week - There are 5 days in a Discordian week, numbered from 1 to 5.
 * <li>day-of-month - There are 73 days in each Discordian month, numbered from 1 to 73.
 * <li>day-of-year - There are 365 days in a standard Discordian year and 366 in a leap year.
 *  The days are numbered from 1 to 365 or 1 to 366.
 * <li>leap-year - Leap years occur in sync with ISO leap-years; that is, they occur every 4 ISO years, excepting when that ISO year is divisible by 100 but not 400.
 * <li>St. Tib's Day - St. Tib's Day occurs each leap-year, and is inserted between the 59th and 60th day of the first month.
 * St. Tib's Day is not part of any month, nor is it part of the Discordian week.  It is aligned with the ISO calendar leap-day (February 29th).
 * </ul>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 */
public final class DiscordianChronology
        extends AbstractChronology
        implements Serializable {

    /**
     * Singleton instance for the Discordian chronology.
     */
    public static final DiscordianChronology INSTANCE = new DiscordianChronology();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1075529146344250850L;
    /**
     * Offset from ISO 0
     */
    static final int OFFSET_FROM_ISO_0000 = 1166;
    /**
     * Days in a regular month.
     */
    static final int DAYS_IN_MONTH = 73;
    /**
     * Days in a regular week.
     */
    static final int DAYS_IN_WEEK = 5;
    /**
     * Months in a regular year.
     */
    static final int MONTHS_IN_YEAR = 5;
    /**
     * Weeks in a regular year.
     */
    static final int WEEKS_IN_YEAR = 73;
    /**
     * Range of proleptic-year.
     */
    static final ValueRange YEAR_RANGE = ValueRange.of(1, 999_999);
    /**
     * Range of month-of-year.
     */
    static final ValueRange MONTH_OF_YEAR_RANGE = ValueRange.of(0, 1, MONTHS_IN_YEAR, MONTHS_IN_YEAR);
    /**
     * Range of day-of-month.
     */
    static final ValueRange DAY_OF_MONTH_RANGE = ValueRange.of(0, 1, 0, DAYS_IN_MONTH);
    /**
     * Range of epoch day.
     */
    static final ValueRange EPOCH_DAY_RANGE = ValueRange.of(-1_145_400, 999_999 * 365L + 242_499);
    /**
     * Range of proleptic month.
     */
    private static final ValueRange PROLEPTIC_MONTH_RANGE = ValueRange.of(0, (long) 999_999 * MONTHS_IN_YEAR + MONTHS_IN_YEAR - 1);
    /**
     * Range of day-of-week.
     */
    private static final ValueRange DAY_OF_WEEK_RANGE = ValueRange.of(0, 1, 0, DAYS_IN_WEEK);
    /**
     * Range of aligned day-of-week of year.
     */
    private static final ValueRange ALIGNED_DOW_OF_YEAR_RANGE = ValueRange.of(0, 1, DAYS_IN_WEEK, DAYS_IN_WEEK);
    /**
     * Range of week-of-month.
     */
    private static final ValueRange WEEK_OF_MONTH_RANGE = ValueRange.of(0, 1, 0, 15);
    /**
     * Range of week-of-year.
     */
    private static final ValueRange WEEK_OF_YEAR_RANGE = ValueRange.of(0, 1, WEEKS_IN_YEAR, WEEKS_IN_YEAR);
    /**
     * Range of eras.
     */
    private static final ValueRange ERA_RANGE = ValueRange.of(1, 1);

    /**
     * Private constructor, that is public to satisfy the {@code ServiceLoader}.
     * @deprecated Use the singleton {@link #INSTANCE} instead.
     */
    @Deprecated
    public DiscordianChronology() {
    }

    /**
     * Resolve singleton.
     *
     * @return the singleton instance, not null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the chronology - 'Discordian'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link Chronology#of(String)}.
     *
     * @return the chronology ID - 'Discordian'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Discordian";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'discordian'.
     * <p>
     * The <em>Unicode Locale Data Markup Language (LDML)</em> specification
     * does not define an identifier for the Discordian calendar, but were it to
     * do so, 'discordian' is highly likely to be chosen.
     *
     * @return the calendar system type - 'discordian'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "discordian";
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a local date in Discordian calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era  the Discordian era, not null
     * @param yearOfEra  the year-of-era
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Discordian local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code DiscordianEra}
     */
    @Override
    public DiscordianDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in Discordian calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Discordian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public DiscordianDate date(int prolepticYear, int month, int dayOfMonth) {
        return DiscordianDate.of(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a local date in Discordian calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era  the Discordian era, not null
     * @param yearOfEra  the year-of-era
     * @param dayOfYear  the day-of-year
     * @return the Discordian local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code DiscordianEra}
     */
    @Override
    public DiscordianDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in the Discordian calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param dayOfYear  the day-of-year
     * @return the Discordian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public DiscordianDate dateYearDay(int prolepticYear, int dayOfYear) {
        return DiscordianDate.ofYearDay(prolepticYear, dayOfYear);
    }

    /**
     * Obtains a local date in the Discordian calendar system from the epoch-day.
     *
     * @param epochDay  the epoch day
     * @return the Discordian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public DiscordianDate dateEpochDay(long epochDay) {
        return DiscordianDate.ofEpochDay(epochDay);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains the current Discordian local date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current Discordian local date using the system clock and default time-zone, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public DiscordianDate dateNow() {
        return DiscordianDate.now();
    }

    /**
     * Obtains the current Discordian local date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone  the zone ID to use, not null
     * @return the current Discordian local date using the system clock, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public DiscordianDate dateNow(ZoneId zone) {
        return DiscordianDate.now(zone);
    }

    /**
     * Obtains the current Discordian local date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current Discordian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public DiscordianDate dateNow(Clock clock) {
        return DiscordianDate.now(clock);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains a Discordian local date from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Discordian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public DiscordianDate date(TemporalAccessor temporal) {
        return DiscordianDate.from(temporal);
    }

    /**
     * Obtains a Discordian local date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Discordian local date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<DiscordianDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<DiscordianDate>) super.localDateTime(temporal);
    }

    /**
     * Obtains a Discordian zoned date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Discordian zoned date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<DiscordianDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<DiscordianDate>) super.zonedDateTime(temporal);
    }

    /**
     * Obtains a Discordian zoned date-time in this chronology from an {@code Instant}.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param zone  the time-zone, not null
     * @return the Discordian zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<DiscordianDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<DiscordianDate>) super.zonedDateTime(instant, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A Discordian proleptic-year is leap if the remainder after division by four equals zero.
     * There are two special cases.
     * If the year minus 1166 is divisible by 100 it is not a leap year, unless
     * it is also divisible by 400, when it is a leap year.
     * These rules produce leap days on the same dates as the ISO-8601 calendar system.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        long offsetYear = prolepticYear - OFFSET_FROM_ISO_0000;
        return (offsetYear % 4 == 0) && ((offsetYear % 400 == 0) || (offsetYear % 100 != 0));
    }

    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (!DiscordianEra.YOLD.equals(era)) {
            throw new ClassCastException("Era must be DiscordianEra.YOLD");
        }
        return YEAR_RANGE.checkValidIntValue(yearOfEra, ChronoField.YEAR_OF_ERA);
    }

    @Override
    public DiscordianEra eraOf(int era) {
        return DiscordianEra.of(era);
    }

    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(DiscordianEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(ChronoField field) {
        switch (field) {
            case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                return ALIGNED_DOW_OF_YEAR_RANGE;
            case ALIGNED_DAY_OF_WEEK_IN_MONTH:
            case DAY_OF_WEEK:
                return DAY_OF_WEEK_RANGE;
            case ALIGNED_WEEK_OF_MONTH:
                return WEEK_OF_MONTH_RANGE;
            case ALIGNED_WEEK_OF_YEAR:
                return WEEK_OF_YEAR_RANGE;
            case DAY_OF_MONTH:
                return DAY_OF_MONTH_RANGE;
            case EPOCH_DAY:
                return EPOCH_DAY_RANGE;
            case ERA:
                return ERA_RANGE;
            case MONTH_OF_YEAR:
                return MONTH_OF_YEAR_RANGE;
            case PROLEPTIC_MONTH:
                return PROLEPTIC_MONTH_RANGE;
            case YEAR_OF_ERA:
            case YEAR:
                return YEAR_RANGE;
            default:
                break;
        }
        return field.range();
    }

    //-----------------------------------------------------------------------
    @Override  // override for return type
    public DiscordianDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (DiscordianDate) super.resolveDate(fieldValues, resolverStyle);
    }

}
