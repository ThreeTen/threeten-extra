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
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The Ethiopic calendar system.
 * <p>
 * This chronology defines the rules of the Ethiopic calendar system.
 * This calendar system is primarily used in Ethiopia.
 * Dates are aligned such that {@code 0001-01-01 (Ethiopic)} is {@code 0008-08-27 (ISO)}.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, the current 'Incarnation Era' (INCARNATION) and the previous era (BEFORE_INCARNATION).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year one.
 *  For the previous era the year increases from one as time goes backwards.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current era. For the previous era, years have zero, then negative values.
 * <li>month-of-year - There are 13 months in a Ethiopic year, numbered from 1 to 13.
 * <li>day-of-month - There are 30 days in each of the first 12 Ethiopic months, numbered 1 to 30.
 *  The 13th month has 5 days, or 6 in a leap year, numbered 1 to 5 or 1 to 6.
 * <li>day-of-year - There are 365 days in a standard Ethiopic year and 366 in a leap year.
 *  The days are numbered from 1 to 365 or 1 to 366.
 * <li>leap-year - Leap years occur every 4 years.
 * </ul>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 */
public final class EthiopicChronology
        extends AbstractNileChronology
        implements Serializable {

    /**
     * Singleton instance for the Ethiopic chronology.
     */
    public static final EthiopicChronology INSTANCE = new EthiopicChronology();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 53287687268768L;

    /**
     * Private constructor, that is public to satisfy the {@code ServiceLoader}.
     * @deprecated Use the singleton {@link #INSTANCE} instead.
     */
    @Deprecated
    public EthiopicChronology() {
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
     * Gets the ID of the chronology - 'Ethiopic'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link Chronology#of(String)}.
     *
     * @return the chronology ID - 'Ethiopic'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Ethiopic";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'ethiopic'.
     * <p>
     * The calendar type is an identifier defined by the
     * <em>Unicode Locale Data Markup Language (LDML)</em> specification.
     * It can be used to lookup the {@code Chronology} using {@link Chronology#of(String)}.
     * It can also be used as part of a locale, accessible via
     * {@link Locale#getUnicodeLocaleType(String)} with the key 'ca'.
     *
     * @return the calendar system type - 'ethiopic'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "ethiopic";
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a local date in Ethiopic calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era  the Ethiopic era, not null
     * @param yearOfEra  the year-of-era
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Ethiopic local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code EthiopicEra}
     */
    @Override
    public EthiopicDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in Ethiopic calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Ethiopic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public EthiopicDate date(int prolepticYear, int month, int dayOfMonth) {
        return EthiopicDate.of(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a local date in Ethiopic calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era  the Ethiopic era, not null
     * @param yearOfEra  the year-of-era
     * @param dayOfYear  the day-of-year
     * @return the Ethiopic local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code EthiopicEra}
     */
    @Override
    public EthiopicDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in Ethiopic calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param dayOfYear  the day-of-year
     * @return the Ethiopic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public EthiopicDate dateYearDay(int prolepticYear, int dayOfYear) {
        return EthiopicDate.ofYearDay(prolepticYear, dayOfYear);
    }

    /**
     * Obtains a local date in the Ethiopic calendar system from the epoch-day.
     *
     * @param epochDay  the epoch day
     * @return the Ethiopic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public EthiopicDate dateEpochDay(long epochDay) {
        return EthiopicDate.ofEpochDay(epochDay);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains the current Ethiopic local date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current Ethiopic local date using the system clock and default time-zone, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public EthiopicDate dateNow() {
        return EthiopicDate.now();
    }

    /**
     * Obtains the current Ethiopic local date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     * @return the current Ethiopic local date using the system clock, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public EthiopicDate dateNow(ZoneId zone) {
        return EthiopicDate.now(zone);
    }

    /**
     * Obtains the current Ethiopic local date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current Ethiopic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public EthiopicDate dateNow(Clock clock) {
        return EthiopicDate.now(clock);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains a Ethiopic local date from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Ethiopic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public EthiopicDate date(TemporalAccessor temporal) {
        return EthiopicDate.from(temporal);
    }

    /**
     * Obtains a Ethiopic local date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Ethiopic local date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<EthiopicDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<EthiopicDate>) super.localDateTime(temporal);
    }

    /**
     * Obtains a Ethiopic zoned date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Ethiopic zoned date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<EthiopicDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<EthiopicDate>) super.zonedDateTime(temporal);
    }

    /**
     * Obtains a Ethiopic zoned date-time in this chronology from an {@code Instant}.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param zone  the time-zone, not null
     * @return the Ethiopic zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<EthiopicDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<EthiopicDate>) super.zonedDateTime(instant, zone);
    }

    //-----------------------------------------------------------------------
    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof EthiopicEra == false) {
            throw new ClassCastException("Era must be EthiopicEra");
        }
        return (era == EthiopicEra.INCARNATION ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public EthiopicEra eraOf(int eraValue) {
        return EthiopicEra.of(eraValue);
    }

    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(EthiopicEra.values());
    }

    //-----------------------------------------------------------------------
    @Override  // override for return type
    public EthiopicDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (EthiopicDate) super.resolveDate(fieldValues, resolverStyle);
    }

}
