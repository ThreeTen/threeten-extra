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
import java.time.LocalDate;
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
import java.util.Locale;
import java.util.Map;

/**
 * The Coptic calendar system.
 * <p>
 * This chronology defines the rules of the Coptic calendar system.
 * This calendar system is primarily used in Christian Egypt.
 * Dates are aligned such that {@code 0001-01-01 (Coptic)} is {@code 0284-08-29 (ISO)}.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, the current 'Era of the Martyrs' (AM) and the previous era (BEFORE_AM).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year one.
 *  For the previous era the year increases from one as time goes backwards.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current era. For the previous era, years have zero, then negative values.
 * <li>month-of-year - There are 13 months in a Coptic year, numbered from 1 to 13.
 * <li>day-of-month - There are 30 days in each of the first 12 Coptic months, numbered 1 to 30.
 *  The 13th month has 5 days, or 6 in a leap year, numbered 1 to 5 or 1 to 6.
 * <li>day-of-year - There are 365 days in a standard Coptic year and 366 in a leap year.
 *  The days are numbered from 1 to 365 or 1 to 366.
 * <li>leap-year - Leap years occur every 4 years.
 * </ul>
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 */
public final class CopticChronology extends AbstractChronology implements Serializable {

    /**
     * Singleton instance for the Coptic chronology.
     */
    public static final CopticChronology INSTANCE = new CopticChronology();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 7291205177830286973L;
    /**
     * Range of proleptic-year.
     */
    static final ValueRange YEAR_RANGE = ValueRange.of(-999_998, 999_999);
    /**
     * Range of year.
     */
    static final ValueRange YOE_RANGE = ValueRange.of(1, 999_999);
    /**
     * Range of proleptic month.
     */
    static final ValueRange PROLEPTIC_MONTH_RANGE = ValueRange.of(-999_998 * 13L, 999_999 * 13L + 12);
    /**
     * Range of months.
     */
    static final ValueRange MOY_RANGE = ValueRange.of(1, 13);
    /**
     * Range of weeks.
     */
    static final ValueRange ALIGNED_WOM_RANGE = ValueRange.of(1,  1, 5);
    /**
     * Range of days.
     */
    static final ValueRange DOM_RANGE = ValueRange.of(1, 5, 30);
    /**
     * Range of days.
     */
    static final ValueRange DOM_RANGE_NONLEAP = ValueRange.of(1, 5);
    /**
     * Range of days.
     */
    static final ValueRange DOM_RANGE_LEAP = ValueRange.of(1, 6);

    /**
     * Creates an instance.
     * <p>
     * This is only public to satisfy the ServiceLoader.
     * Use the singleton {@link #INSTANCE} instead.
     */
    public CopticChronology() {
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
     * Gets the ID of the chronology - 'Coptic'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     *
     * @return the chronology ID - 'Coptic'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Coptic";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'coptic'.
     * <p>
     * The calendar type is an identifier defined by the
     * <em>Unicode Locale Data Markup Language (LDML)</em> specification.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     * It can also be used as part of a locale, accessible via
     * {@link Locale#getUnicodeLocaleType(String)} with the key 'ca'.
     *
     * @return the calendar system type - 'coptic'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "coptic";
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a local date in Coptic calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era  the Coptic era, not null
     * @param yearOfEra  the year-of-era
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Coptic local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code CopticEra}
     */
    @Override
    public CopticDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in Coptic calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Coptic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public CopticDate date(int prolepticYear, int month, int dayOfMonth) {
        return CopticDate.of(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a local date in Coptic calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era  the Coptic era, not null
     * @param yearOfEra  the year-of-era
     * @param dayOfYear  the day-of-year
     * @return the Coptic local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code CopticEra}
     */
    @Override
    public CopticDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in Coptic calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param dayOfYear  the day-of-year
     * @return the Coptic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public CopticDate dateYearDay(int prolepticYear, int dayOfYear) {
        return CopticDate.ofYearDay(prolepticYear, dayOfYear);  // TODO
    }

    /**
     * Obtains a local date in the Coptic calendar system from the epoch-day.
     *
     * @param epochDay  the epoch day
     * @return the Coptic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public CopticDate dateEpochDay(long epochDay) {
        return CopticDate.ofEpochDay(epochDay);
    }

    @Override
    public CopticDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    @Override
    public CopticDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    @Override
    public CopticDate dateNow(Clock clock) {
        return date(LocalDate.now(clock));
    }

    @Override
    public CopticDate date(TemporalAccessor temporal) {
        return CopticDate.from(temporal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<CopticDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<CopticDate>) super.localDateTime(temporal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<CopticDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<CopticDate>) super.zonedDateTime(temporal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<CopticDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<CopticDate>) super.zonedDateTime(instant, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A Coptic proleptic-year is leap if the remainder after division by four equals three.
     * This method does not validate the year passed in, and only has a
     * well-defined result for years in the supported range.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return Math.floorMod(prolepticYear, 4) == 3;
    }

    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof CopticEra == false) {
            throw new ClassCastException("Era must be CopticEra");
        }
        return (era == CopticEra.AM ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public CopticEra eraOf(int eraValue) {
        return CopticEra.of(eraValue);
    }

    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(CopticEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(ChronoField field) {
        switch (field) {
            case DAY_OF_MONTH:
                return DOM_RANGE;
            case ALIGNED_WEEK_OF_MONTH:
                return ALIGNED_WOM_RANGE;
            case MONTH_OF_YEAR:
                return MOY_RANGE;
            case PROLEPTIC_MONTH:
                return PROLEPTIC_MONTH_RANGE;
            case YEAR_OF_ERA:
                return YOE_RANGE;
            case YEAR:
                return YEAR_RANGE;
            default:
                break;
        }
        return field.range();
    }

    //-----------------------------------------------------------------------
    @Override  // override for return type
    public CopticDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (CopticDate) super.resolveDate(fieldValues, resolverStyle);
    }

}
