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
import java.time.ZoneId;

import java.time.chrono.AbstractChronology;
import java.time.chrono.Era;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.ValueRange;

import java.util.Arrays;
import java.util.List;

/**
 * The International Fixed calendar system.
 * <p>
 * This chronology defines the rules of the International Fixed calendar system.
 * It shares the leap year rule with the Gregorian calendar.
 * Dates are aligned such that {@code 0001-01-01 (International Fixed)} is {@code 0001-01-01 (ISO)}.
 * <p>
 * This class is proleptic.  It implements only years greater or equal to 1.
 * <p>
 * This class implements a calendar where January 1st is the start of the year.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There is only one era, the current 'Common Era' (CE).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year 1.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the current era.
 * <li>month-of-year - There are 13 months in an International Fixed year, numbered from 1 to 13.
 * <li>day-of-month - There are 28 days in an International Fixed month, numbered from 1 to 28.
 * <li>day-of-year - There are 365 days in a standard International Fixed year and 366 days in a leap year.
 *  The days are numbered accordingly.
 * <li>leap-year - Leap years occur every 4 years, but skips 3 out of four centuries, i.e. when the century is not divisible by 400.
 *  This is the same rule in use for the Gregorian calendar.
 * <li>Week day - every month starts on a Sunday.  Leap-day and year-day are neither part of a week, nor of any month.
 * </ul>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 */
public final class InternationalFixedChronology extends AbstractChronology implements Serializable {
    /**
     * Singleton instance for the International fixed chronology.
     */
    public static final InternationalFixedChronology INSTANCE = new InternationalFixedChronology();
    /**
     * Serialization version UID.
     */
    private static final long serialVersionUID = -2670617783459258438L;
    /**
     * Standard 7-day week.
     */
    static final int DAYS_IN_WEEK = 7;
    /**
     * In all months, there are 4 complete weeks.
     */
    static final int WEEKS_IN_MONTH = 4;
    /**
     * There are 13 months in a year.
     */
    static final int MONTHS_IN_YEAR = 13;
    /**
     * There are 4 weeks of 7 days, or 28 total days in a month.
     */
    static final int DAYS_IN_MONTH = WEEKS_IN_MONTH * DAYS_IN_WEEK;
    /**
     * Range of aligned day-of-week.
     */
    static final ValueRange ALIGNED_DAY_OF_WEEK_RANGE = ValueRange.of(0, DAYS_IN_WEEK);
    /**
     * Range of day-of-week.
     */
    static final ValueRange DAY_OF_WEEK_RANGE = ValueRange.of(0, 1, 0, DAYS_IN_WEEK);
    /**
     * There are 13 months of 28 days, or 365 days in a (non-leap) year.
     */
    static final int DAYS_IN_YEAR = MONTHS_IN_YEAR * DAYS_IN_MONTH + 1;
    /**
     * There are 52 weeks in a year.
     */
    static final int WEEKS_IN_YEAR = DAYS_IN_YEAR / DAYS_IN_WEEK;
    /**
     * The number of days in a 400 year cycle.
     */
    static final int DAYS_PER_CYCLE = 146097;
    /**
     * The number of days from year zero to year 1970, still the era only allows year 1 and higher.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     */
    static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);
    /**
     * Range of year.
     */
    static final ValueRange YEAR_RANGE = ValueRange.of(1, 1_000_000L);
    /**
     * Epoch day range.
     */
    static final ValueRange EPOCH_DAY_RANGE = ValueRange.of(-DAYS_0000_TO_1970, 1_000_000L * DAYS_IN_YEAR + getLeapYearsBefore(1_000_000L) - DAYS_0000_TO_1970);
    /**
     * Range of proleptic month.
     */
    private static final ValueRange PROLEPTIC_MONTH_RANGE = ValueRange.of(13, 1_000_000 * 13L - 1);
    /**
     * Range of day of month.
     */
    static final ValueRange DAY_OF_MONTH_RANGE = ValueRange.of(-1, 0, -1, DAYS_IN_MONTH);
    /**
     * Range of week of year.
     */
    static final ValueRange WEEK_OF_YEAR_RANGE = ValueRange.of(0, WEEKS_IN_YEAR);
    /**
     * Range of aligned week of month, the weeks are always perfectly aligned.
     */
    static final ValueRange WEEK_OF_MONTH_RANGE = ValueRange.of(0, 1, 0, WEEKS_IN_MONTH);
    /**
     * Range of day of year.
     */
    static final ValueRange DAY_OF_YEAR_NORMAL_RANGE = ValueRange.of(1, DAYS_IN_YEAR);
    /**
     * Range of day of leap year.
     */
    static final ValueRange DAY_OF_YEAR_LEAP_RANGE = ValueRange.of(1, DAYS_IN_YEAR + 1);
    /**
     * Range of day of year, inclusive
     */
    static final ValueRange DAY_OF_YEAR_RANGE = ValueRange.of(1, DAYS_IN_YEAR, DAYS_IN_YEAR + 1);
    /**
     * Range of month of year.
     */
    static final ValueRange MONTH_OF_YEAR_RANGE = ValueRange.of(-1, 0, -1, MONTHS_IN_YEAR);
    /**
     * Range of eras.
     */
    static final ValueRange ERA_RANGE = ValueRange.of(1, 1);
    /**
     * Empty range: [0, 0].
     */
    static final ValueRange EMPTY_RANGE = ValueRange.of(0, 0);

    /**
     * Public constructor, in order to satisfy the {@code ServiceLoader}.
     * @deprecated  Use the singleton {@link #INSTANCE} instead.
     */
    @Deprecated
    public InternationalFixedChronology() {
    }

    /**
     * Resolve singleton.
     *
     * @return the singleton instance, not null
     */
    @SuppressWarnings("static-method")
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the chronology - 'Ifc'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     *
     * @return the chronology ID - 'Ifc'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Ifc";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'ifc'.
     * <p>
     * The <em>Unicode Locale Data Markup Language (LDML)</em> specification
     * does not define an identifier for the International Fixed calendar, but were it to
     * do so, 'ifc' is highly likely to be chosen.
     *
     * @return the calendar system type - 'ifc'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "ifc";
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a local date in International Fixed calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era        the International Fixed era, not null
     * @param yearOfEra  the year-of-era
     * @param month      the month-of-year
     * @param dayOfMonth the day-of-month
     * @return the International Fixed local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code InternationalFixedEra}
     */
    @Override
    public InternationalFixedDate date(final Era era, final int yearOfEra, final int month, final int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in International Fixed calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear the proleptic-year
     * @param month         the month-of-year
     * @param dayOfMonth    the day-of-month
     * @return the International Fixed local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public InternationalFixedDate date(final int prolepticYear, final int month, final int dayOfMonth) {
        return InternationalFixedDate.of(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a local date in International Fixed calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era       the International Fixed era, not null
     * @param yearOfEra the year-of-era
     * @param dayOfYear the day-of-year
     * @return the International Fixed local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code InternationalFixedEra}
     */
    @Override
    public InternationalFixedDate dateYearDay(final Era era, final int yearOfEra, final int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in International Fixed calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear the proleptic-year
     * @param dayOfYear     the day-of-year
     * @return the International Fixed local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public InternationalFixedDate dateYearDay(final int prolepticYear, final int dayOfYear) {
        return InternationalFixedDate.ofYearDay(prolepticYear, dayOfYear);
    }

    /**
     * Obtains a local date in the International Fixed calendar system from the epoch-day.
     *
     * @param epochDay the epoch day
     * @return the International Fixed local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public InternationalFixedDate dateEpochDay(final long epochDay) {
        return InternationalFixedDate.ofEpochDay(epochDay);
    }

    //-------------------------------------------------------------------------

    /**
     * Obtains the current International Fixed local date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current International Fixed local date using the system clock and default time-zone, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public InternationalFixedDate dateNow() {
        return InternationalFixedDate.now();
    }

    /**
     * Obtains the current International Fixed local date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     * @return the current International Fixed local date using the system clock, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public InternationalFixedDate dateNow(final ZoneId zone) {
        return InternationalFixedDate.now(zone);
    }

    /**
     * Obtains the current International Fixed local date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock the clock to use, not null
     * @return the current International Fixed local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public InternationalFixedDate dateNow(final Clock clock) {
        return InternationalFixedDate.now(clock);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains a International Fixed local date from another date-time object.
     *
     * @param temporal the date-time object to convert, not null
     * @return the International Fixed local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public InternationalFixedDate date(final TemporalAccessor temporal) {
        return InternationalFixedDate.from(temporal);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A leap-year is a year of a longer length than normal.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <ul>
     * <li>a leap-year must imply a year-length longer than a non leap-year.
     * <li>a chronology that does not support the concept of a year must return false.
     * </ul>
     *
     * @param year the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(final long year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates the chronology era object from the numeric value.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The era in use at 1970-01-01 must have the value 1.
     * Later eras must have sequentially higher values.
     * Earlier eras must have sequentially lower values.
     * Each chronology must refer to an enum or similar singleton to provide the era values.
     * <p>
     * This method returns the singleton era of the correct type for the specified era value.
     *
     * @param eraValue the era value
     * @return the calendar system era, not null
     * @throws DateTimeException if unable to create the era
     */
    @Override
    public InternationalFixedEra eraOf(final int eraValue) {
        return InternationalFixedEra.of(eraValue);
    }

    /**
     * Gets the list of eras for the chronology.
     * <p>
     * Most calendar systems have an era, within which the year has meaning.
     * If the calendar system does not support the concept of eras, an empty
     * list must be returned.
     *
     * @return the list of eras for the chronology, may be immutable, not null
     */
    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(InternationalFixedEra.values());
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public ValueRange range(final ChronoField field) {
        switch (field) {
            case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                return ALIGNED_DAY_OF_WEEK_RANGE;
            case ALIGNED_DAY_OF_WEEK_IN_MONTH:
            case DAY_OF_WEEK:
                return DAY_OF_WEEK_RANGE;
            case ALIGNED_WEEK_OF_MONTH:
                return WEEK_OF_MONTH_RANGE;
            case ALIGNED_WEEK_OF_YEAR:
                return WEEK_OF_YEAR_RANGE;
            case DAY_OF_MONTH:
                return DAY_OF_MONTH_RANGE;
            case DAY_OF_YEAR:
                return DAY_OF_YEAR_RANGE;
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
                return field.range();
        }
    }

    /**
     * Calculates the proleptic-year given the era and year-of-era.
     * <p>
     * The International Fixed calendar only knows common era years, thus negative years are invalid.
     * <p>
     * If the chronology makes active use of eras, such as {@code JapaneseChronology}
     * then the year-of-era will be validated against the era.
     * For other chronologies, validation is optional.
     *
     * @param era       the era of the correct type for the chronology, not null
     * @param yearOfEra the chronology year-of-era
     * @return the proleptic-year
     * @throws DateTimeException  if unable to convert to a proleptic-year, such as if the year is invalid for the era
     */
    @Override
    public int prolepticYear(final Era era, final int yearOfEra) {
        if (!(era instanceof InternationalFixedEra)) {
            throw new DateTimeException("Invalid era: " + era);
        }

        if (yearOfEra < 1) {
            throw new DateTimeException("Invalid year of era: " + yearOfEra);
        }

        return yearOfEra;
    }

    /**
     * Get the count of leap years since International fixed year 1.
     * <p/>
     *
     * @param prolepticYear The year.
     * @return The number of leap years since International fixed year 1.
     */
    public static long getLeapYearsBefore(final long prolepticYear) {
        long yearBefore = prolepticYear - 1;

        return (yearBefore / 4) - (yearBefore / 100) + (yearBefore / 400);
    }
}
