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
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;

/**
 * The Symmetry454 calendar system.
 * <p>
 * This chronology defines the rules of the Symmetry454 calendar system.
 * Dates are aligned such that {@code 0001/01/01 (Sym454)} is {@code 0001-01-01 (ISO)}.
 * <p>
 * The calendar implemented by this class is proleptic, with January 1st as the start of the year.
 * Each month either has 28 days or 35 days, in an alternating pattern; January has 28 days,
 * February has 35 days and March again has 28 days. Due to this, each quarter consists of 13 weeks.
 * <p>
 * Normal years thus have 364 days, whereas leap years have an extra week, aptly called leap week,
 * added to the end, extending the year to 371 days.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - Same eras as used in the Gregorian calendar: 'Before Common Era' (BCE) and 'Common Era' (CE).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year 1.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the current era.
 * <li>month-of-year - There are 12 months in an Symmetry454 year, numbered from 1 to 12.
 * <li>day-of-month - There are 28 days in a standard Symmetry454 month, numbered from 1 to 28, except for the middle-month
 * in each quarter, which spans 35 days: February, May, August, November; and December in leap years.
 * <li>day-of-year - There are 364 days in a standard Symmetry454 year and 371 days in a leap year.
 *  The days are numbered accordingly.
 * <li>leap-year - Leap years occur every 5 or 6 years, evenly spread over 293 years according the formula:
 *     (52 &gt; ((52 * year + 146) % 293)).
 * <li>Week day - every month starts on a Monday.  There are no days outside of the week or month.
 * </ul>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 */
public final class Symmetry454Chronology
        extends AbstractChronology
        implements Serializable {

    /**
     * Singleton instance for the Symmetry454 chronology.
     */
    public static final Symmetry454Chronology INSTANCE = new Symmetry454Chronology();
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -1287766365831162587L;

    /**
     * Standard 7 day weeks.
     */
    static final int DAYS_IN_WEEK = 7;
    /**
     * Standard 12 month years.
     */
    static final int MONTHS_IN_YEAR = 12;
    /**
     * Normal month is 4 weeks.
     */
    static final int WEEKS_IN_MONTH = 4;
    /**
     * Long month is 5 weeks.
     */
    static final int WEEKS_IN_MONTH_LONG = 5;
    /**
     * Days in quarter, (4 + 5 + 4) * 7 = 91
     */
    static final int DAYS_IN_QUARTER = (WEEKS_IN_MONTH + WEEKS_IN_MONTH_LONG + WEEKS_IN_MONTH) * DAYS_IN_WEEK;
    /**
     * Days in year, 8 months of 28 days plus 4 months of 35 days, or 364 days in a normal year.
     */
    static final int DAYS_IN_YEAR = 4 * DAYS_IN_QUARTER;
    /**
     * Leap years are 364 + 7 days.
     */
    static final int DAYS_IN_YEAR_LONG = DAYS_IN_YEAR + DAYS_IN_WEEK;
    /**
     * Days in long month.
     */
    static final int DAYS_IN_MONTH_LONG = WEEKS_IN_MONTH_LONG * DAYS_IN_WEEK;
    /**
     * Days in normal month.
     */
    static final int DAYS_IN_MONTH = WEEKS_IN_MONTH * DAYS_IN_WEEK;
    /**
     * 52 weeks in a normal year.
     */
    static final int WEEKS_IN_YEAR = DAYS_IN_YEAR / DAYS_IN_WEEK;
    /**
     * Number of years in a cycle.
     */
    private static final int YEARS_IN_CYCLE = 293;
    /**
     * Number of days in a cycle.
     */
    static final int DAYS_PER_CYCLE = YEARS_IN_CYCLE * DAYS_IN_YEAR + WEEKS_IN_YEAR * DAYS_IN_WEEK; // == 294 full years!
    /**
     * The number of days from year zero to CE 1970, still the era only allows CE 1 and higher.
     * There are 6 full 293-year cycles from CE 1 to 1758, with 6 * 52 leap years, i.e. 312.
     * There are 37 leap years from CE 1758 to 1970.
     */
    public static final long DAYS_0001_TO_1970 = (146097 * 5L) - (31L * 365L + 7L) - 1;
    /**
     * Highest year in the range.
     */
    private static final long MAX_YEAR = 1_000_000L;
    /**
     * Range of year.
     */
    static final ValueRange YEAR_RANGE = ValueRange.of(-MAX_YEAR, MAX_YEAR);
    /**
     * Epoch day range.
     */
    static final ValueRange EPOCH_DAY_RANGE = ValueRange.of(
            -MAX_YEAR * DAYS_IN_YEAR - getLeapYearsBefore(MAX_YEAR) * DAYS_IN_WEEK - DAYS_0001_TO_1970,
             MAX_YEAR * DAYS_IN_YEAR + getLeapYearsBefore(MAX_YEAR) * DAYS_IN_WEEK - DAYS_0001_TO_1970);
    /**
     * Range of proleptic month.
     */
    private static final ValueRange PROLEPTIC_MONTH_RANGE = ValueRange.of(-MAX_YEAR * MONTHS_IN_YEAR, MAX_YEAR * MONTHS_IN_YEAR - 1);
    /**
     * Range of day of month.
     */
    static final ValueRange DAY_OF_MONTH_RANGE = ValueRange.of(1, DAYS_IN_MONTH, DAYS_IN_MONTH_LONG);
    /**
     * Range of day of year.
     */
    static final ValueRange DAY_OF_YEAR_RANGE = ValueRange.of(1, DAYS_IN_YEAR, DAYS_IN_YEAR + DAYS_IN_WEEK);
    /**
     * Range of month of year.
     */
    static final ValueRange MONTH_OF_YEAR_RANGE = ValueRange.of(1, MONTHS_IN_YEAR);
    /**
     * Range of eras.
     */
    static final ValueRange ERA_RANGE = ValueRange.of(0, 1);
    /**
     * Empty range: [0, 0].
     */
    static final ValueRange EMPTY_RANGE = ValueRange.of(0, 0);

    /**
     * Private constructor, that is public to satisfy the {@code ServiceLoader}.
     * @deprecated Use the singleton {@link #INSTANCE} instead.
     */
    @Deprecated
    public Symmetry454Chronology() {
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
     * Gets the ID of the chronology - 'Sym454'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link Chronology#of(String)}.
     *
     * @return the chronology ID - 'Sym454'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Sym454";
    }

    /**
     * Gets the calendar type of the underlying calendar system, which returns null.
     * <p>
     * The <em>Unicode Locale Data Markup Language (LDML)</em> specification
     * does not define an identifier for this calendar system, thus null is returned.
     *
     * @return the calendar system type, null
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a local date in Symmetry454 calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era  the Symmetry454 era, not null
     * @param yearOfEra  the year-of-era
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Symmetry454 local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code IsoEra}
     */
    @Override
    public Symmetry454Date date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in Symmetry454 calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Symmetry454 local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public Symmetry454Date date(int prolepticYear, int month, int dayOfMonth) {
        return Symmetry454Date.of(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a local date in Symmetry454 calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era  the Symmetry454 era, not null
     * @param yearOfEra  the year-of-era
     * @param dayOfYear  the day-of-year
     * @return the Symmetry454 local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code IsoEra}
     */
    @Override
    public Symmetry454Date dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in Symmetry454 calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param dayOfYear  the day-of-year
     * @return the Symmetry454 local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public Symmetry454Date dateYearDay(int prolepticYear, int dayOfYear) {
        return Symmetry454Date.ofYearDay(prolepticYear, dayOfYear);
    }

    /**
     * Obtains a local date in the Symmetry454 calendar system from the epoch-day.
     *
     * @param epochDay  the epoch day
     * @return the Symmetry454 local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public Symmetry454Date dateEpochDay(long epochDay) {
        return Symmetry454Date.ofEpochDay(epochDay);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains the current Symmetry454 local date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current Symmetry454 local date using the system clock and default time-zone, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public Symmetry454Date dateNow() {
        return Symmetry454Date.now();
    }

    /**
     * Obtains the current Symmetry454 local date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone  the zone ID to use, not null
     * @return the current Symmetry454 local date using the system clock, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public Symmetry454Date dateNow(ZoneId zone) {
        return Symmetry454Date.now(zone);
    }

    /**
     * Obtains the current Symmetry454 local date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current Symmetry454 local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public Symmetry454Date dateNow(Clock clock) {
        return Symmetry454Date.now(clock);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains a Symmetry454 local date from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Symmetry454 local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public Symmetry454Date date(TemporalAccessor temporal) {
        return Symmetry454Date.from(temporal);
    }

    /**
     * Obtains a Symmetry454 local date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Symmetry454 local date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<Symmetry454Date> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<Symmetry454Date>) super.localDateTime(temporal);
    }

    /**
     * Obtains a Symmetry454 zoned date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Symmetry454 zoned date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<Symmetry454Date> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<Symmetry454Date>) super.zonedDateTime(temporal);
    }

    /**
     * Obtains a Symmetry454 zoned date-time in this chronology from an {@code Instant}.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param zone  the time-zone, not null
     * @return the Symmetry454 zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<Symmetry454Date> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<Symmetry454Date>) super.zonedDateTime(instant, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A leap-year is a year of a longer length than normal.
     * Leap years in the calendar system match those of the ISO calendar system.
     *
     * @param year  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(long year) {
        return WEEKS_IN_YEAR > ((WEEKS_IN_YEAR * year + 146) % YEARS_IN_CYCLE);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates the chronology era object from the numeric value.
     * <p>
     * The list of eras is shared with {@link IsoEra}.
     *
     * @param eraValue  the era value
     * @return the calendar system era, not null
     * @throws DateTimeException if unable to create the era
     */
    @Override
    public IsoEra eraOf(int eraValue) {
        return IsoEra.of(eraValue);
    }

    /**
     * Gets the list of eras for the chronology.
     * <p>
     * The list of eras is shared with {@link IsoEra}.
     *
     * @return the list of eras for the chronology, may be immutable, not null
     */
    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(IsoEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(ChronoField field) {
        switch (field) {
            case ALIGNED_DAY_OF_WEEK_IN_YEAR:
            case ALIGNED_DAY_OF_WEEK_IN_MONTH:
            case DAY_OF_WEEK:
                return ValueRange.of(1, DAYS_IN_WEEK);
            case ALIGNED_WEEK_OF_MONTH:
                return ValueRange.of(1, WEEKS_IN_MONTH, WEEKS_IN_MONTH_LONG);
            case ALIGNED_WEEK_OF_YEAR:
                return ValueRange.of(1, WEEKS_IN_YEAR, WEEKS_IN_YEAR + 1);
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

    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (!(era instanceof IsoEra)) {
            throw new ClassCastException("Invalid era: " + era);
        }
        return YEAR_RANGE.checkValidIntValue(yearOfEra, ChronoField.YEAR_OF_ERA);
    }

    /**
     * Get the count of leap years since CE 1.
     *
     * @param prolepticYear  the year
     * @return the number of leap years since CE 1
     */
    public static long getLeapYearsBefore(long prolepticYear) {
        return Math.floorDiv(WEEKS_IN_YEAR * (prolepticYear - 1) + 146, YEARS_IN_CYCLE);
    }
}
