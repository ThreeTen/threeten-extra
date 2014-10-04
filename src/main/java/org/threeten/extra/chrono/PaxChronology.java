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
 * /** The ISO calendar system.
 * <p>
 * This chronology defines the rules of the Pax calendar system.
 * <p>
 * The fields are defined as follows:
 * <p>
 * <ul>
 * <li>era - There are two eras, 'Current Era' (CE) and 'Before Current Era' (BCE).
 * <li>year-of-era - The year-of-era is the same as the proleptic-year for the current CE era. For the BCE era before the Pax epoch the year increases from 1 upwards as time goes backwards.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the current era. For the previous era, years have zero, then negative values.
 * <li>month-of-year - There are 13 or 14 months in a Pax year, numbered from 1 to 13 (or 14).
 * <li>day-of-month - There are 28 days in each Pax month, numbered from 1 to 28. In a leap year a one-week month ('Pax') is inserted between months 12 and 13.
 * <li>day-of-year - There are 364 days in a standard Pax year and 371 in a leap year. The days are numbered from 1 to 364 or 1 to 371.
 * <li>leap-year - Leap years occur in every year whose last two digits are divisible by {@code 6}, are {@code 99}, or are {@code 00} and the year is <b>not</b> divisible by 400
 * </ul>
 * <p>
 * For more information, please read the <a href="http://en.wikipedia.org/wiki/Pax_Calendar">Pax Calendar</a> Wikipedia article.
 * <p>
 * <h3>Specification for implementors</h3> This class is immutable and thread-safe.
 *
 * @author Clockwork-Muse
 */
public final class PaxChronology extends AbstractChronology implements Serializable {

    /**
     * Singleton instance of the Pax chronology.
     */
    public static final PaxChronology INSTANCE = new PaxChronology();

    /**
     * The leap-month of Pax is only one week long.
     */
    static final int WEEKS_IN_LEAP_MONTH = 1;

    /**
     * Standard 7-day week.
     */
    static final int DAYS_IN_WEEK = 7;

    /**
     * In all months (except Pax), there are 4 complete weeks.
     */
    static final int WEEKS_IN_MONTH = 4;

    /**
     * There are 13 months in a (non-leap) year.
     */
    static final int MONTHS_IN_YEAR = 13;

    /**
     * There are 4 weeks of 7 days, or 28 total days in a month.
     */
    static final int DAYS_IN_MONTH = WEEKS_IN_MONTH * DAYS_IN_WEEK;

    /**
     * There are 13 months of 28 days, or 364 days in a (non-leap) year.
     */
    static final int DAYS_IN_YEAR = MONTHS_IN_YEAR * DAYS_IN_MONTH;

    /**
     * There are 52 weeks in a (non-leap) year.
     */
    static final int WEEKS_IN_YEAR = DAYS_IN_YEAR / DAYS_IN_WEEK;

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -7021464635577802085L;

    /**
     * Restricted constructor.
     */
    private PaxChronology() {
    }

    /**
     * Obtains a local date in Pax calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era the Pax era, not null
     * @param yearOfEra the year-of-era
     * @param month the month-of-year
     * @param dayOfMonth the day-of-month
     * @return the Pax local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code PaxEra}
     */
    @Override
    public PaxDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in Pax calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear the proleptic-year
     * @param month the month-of-year
     * @param dayOfMonth the day-of-month
     * @return the Pax local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public PaxDate date(final int prolepticYear, final int month, final int dayOfMonth) {
        return PaxDate.of(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains the current Pax local date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing because the clock is hard-coded.
     *
     * @return the current Pax local date using the system clock and default time-zone, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public PaxDate dateNow() {
        return PaxDate.now();
    }

    /**
     * Obtains the current Pax local date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date. Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing because the clock is hard-coded.
     *
     * @return the current Pax local date using the system clock, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public PaxDate dateNow(ZoneId zone) {
        return PaxDate.now(zone);
    }

    /**
     * Obtains the current Pax local date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today. Using this method allows the use of an alternate clock for testing. The alternate clock may be introduced using
     * {@link Clock dependency injection}.
     *
     * @param clock the clock to use, not null
     * @return the current Pax local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public PaxDate dateNow(Clock clock) {
        return PaxDate.now(clock);
    }

    /**
     * Obtains a Pax local date from another date-time object.
     *
     * @param temporal the date-time object to convert, not null
     * @return the Pax local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public PaxDate date(final TemporalAccessor temporal) {
        return PaxDate.from(temporal);
    }

    /**
     * Obtains a local date in the Pax calendar system from the epoch-day.
     *
     * @param epochDay the epoch day
     * @return the Pax local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public PaxDate dateEpochDay(final long epochDay) {
        return PaxDate.ofEpochDay(epochDay);
    }

    /**
     * Obtains a local date in Pax calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era the Pax era, not null
     * @param yearOfEra the year-of-era
     * @param dayOfYear the day-of-year
     * @return the Pax local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code PaxEra}
     */
    @Override
    public PaxDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in Pax calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear the proleptic-year
     * @param dayOfYear the day-of-year
     * @return the Pax local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public PaxDate dateYearDay(final int prolepticYear, final int dayOfYear) {
        return PaxDate.ofYearDay(prolepticYear, dayOfYear);
    }

    /**
     * Obtains a Pax local date-time from another date-time object.
     *
     * @param temporal the date-time object to convert, not null
     * @return the Pax local date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<PaxDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<PaxDate>) super.localDateTime(temporal);
    }

    /**
     * Obtains a Pax zoned date-time from another date-time object.
     *
     * @param temporal the date-time object to convert, not null
     * @return the Pax zoned date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<PaxDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<PaxDate>) super.zonedDateTime(temporal);
    }

    /**
     * Obtains a Pax zoned date-time in this chronology from an {@code Instant}.
     *
     * @param instant the instant to create the date-time from, not null
     * @param zone the time-zone, not null
     * @return the Pax zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<PaxDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<PaxDate>) super.zonedDateTime(instant, zone);
    }

    @Override
    public PaxEra eraOf(final int era) {
        return PaxEra.of(era);
    }

    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(PaxEra.values());
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'pax'.
     * <p>
     * The <em>Unicode Locale Data Markup Language (LDML)</em> specification does not define an identifier for the Pax calendar, but were it to do so, 'pax' is highly likely to be chosen.
     *
     * @return the calendar system type - 'pax'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "pax";
    }

    /**
     * Gets the ID of the chronology - 'Pax'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}. It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     *
     * @return the chronology ID - 'Pax'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Pax";
    }

    /**
     * Checks if the year is a leap year, according to the Pax proleptic calendar system rules.
     * <p>
     * This method applies the current rules for leap years across the whole time-line. In general, a year is a leap year if the last two digits are divisible by 6 without remainder, or are 99. Years
     * with the last two digits of 00 are also leap years, with the exception of years divisible by 400 which are not.
     * <p>
     * For example, 2012 is a leap year becuase the last two digits (12) are divisible by 6. 1999 is a leap year as the last two digits are both 9's (99). 1900 is a leap year as the last two digits
     * are both 0's (00), however 2000 was not a leap year as it is divisible by 400. The year 0 is not a leap year.
     * <p>
     * The calculation is proleptic - applying the same rules into the far future and far past.
     *
     * @param prolepticYear the Pax proleptic year to check
     * @return true if the year is leap, false otherwise
     */
    @Override
    public boolean isLeapYear(final long prolepticYear) {
        final long lastTwoDigits = prolepticYear % 100;
        return Math.abs(lastTwoDigits) == 99 || (prolepticYear % 400 != 0 && (lastTwoDigits == 0 || lastTwoDigits % 6 == 0));
    }

    @Override
    public int prolepticYear(final Era era, final int yearOfEra) {
        if (!(era instanceof PaxEra)) {
            throw new ClassCastException("Era must be PaxEra");
        }
        return (era == PaxEra.CE ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public ValueRange range(final ChronoField field) {
        switch (field) {
            case ALIGNED_WEEK_OF_MONTH:
                return ValueRange.of(1, WEEKS_IN_LEAP_MONTH, WEEKS_IN_MONTH);
            case ALIGNED_WEEK_OF_YEAR:
                return ValueRange.of(1, WEEKS_IN_YEAR, WEEKS_IN_YEAR + 1);
            case DAY_OF_MONTH:
                return ValueRange.of(1, DAYS_IN_WEEK, DAYS_IN_MONTH);
            case DAY_OF_YEAR:
                return ValueRange.of(1, DAYS_IN_YEAR, DAYS_IN_YEAR + DAYS_IN_WEEK);
            case MONTH_OF_YEAR:
                return ValueRange.of(1, MONTHS_IN_YEAR, MONTHS_IN_YEAR + 1);
            default:
                return field.range();
        }
    }

    @Override
    public PaxDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (PaxDate) super.resolveDate(fieldValues, resolverStyle);
    }

    /**
     * Resolve singleton.
     *
     * @return the singleton instance, not null
     */
    private Object readResolve() {
        return INSTANCE;
    }

}
