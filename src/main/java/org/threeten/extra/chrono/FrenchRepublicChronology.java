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
import java.time.chrono.Era;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**

 */
public final class FrenchRepublicChronology
        extends AbstractNileChronology
        implements Serializable {

    /**
     * Singleton instance for the FrenchRepublic chronology.
     */
    public static final FrenchRepublicChronology INSTANCE = new FrenchRepublicChronology();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 7291205177830286973L;

    /**
     * Range of days of week.
     */
    static final ValueRange DOW_RANGE = ValueRange.of(1, 10);
    /**
     * Range of weeks.
     */
    static final ValueRange ALIGNED_WOM_RANGE = ValueRange.of(1, 1, 3);
    
    /**
     * Private constructor, that is public to satisfy the {@code ServiceLoader}.
     * @deprecated Use the singleton {@link #INSTANCE} instead.
     */
    @Deprecated
    public FrenchRepublicChronology() {
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
     * Gets the ID of the chronology - 'FrenchRepublic'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     *
     * @return the chronology ID - 'FrenchRepublic'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "French Republican";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'frenchrepublican'.
     * <p>
     * The <em>Unicode Locale Data Markup Language (LDML)</em> specification
     * does not define an identifier for the French Revolutionary calendar, but
     * were it to do so, 'frenchrepublican' is likely to be chosen.
     *
     * @return the calendar system type - 'frenchrepublican'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "frenchrepublican";
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a local date in FrenchRepublic calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era  the FrenchRepublic era, not null
     * @param yearOfEra  the year-of-era
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the FrenchRepublic local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code FrenchRepublicEra}
     */
    @Override
    public FrenchRepublicDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in FrenchRepublic calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the FrenchRepublic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public FrenchRepublicDate date(int prolepticYear, int month, int dayOfMonth) {
        return FrenchRepublicDate.of(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a local date in FrenchRepublic calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era  the FrenchRepublic era, not null
     * @param yearOfEra  the year-of-era
     * @param dayOfYear  the day-of-year
     * @return the FrenchRepublic local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code FrenchRepublicEra}
     */
    @Override
    public FrenchRepublicDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in FrenchRepublic calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param dayOfYear  the day-of-year
     * @return the FrenchRepublic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public FrenchRepublicDate dateYearDay(int prolepticYear, int dayOfYear) {
        return FrenchRepublicDate.ofYearDay(prolepticYear, dayOfYear);
    }

    /**
     * Obtains a local date in the FrenchRepublic calendar system from the epoch-day.
     *
     * @param epochDay  the epoch day
     * @return the FrenchRepublic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public FrenchRepublicDate dateEpochDay(long epochDay) {
        return FrenchRepublicDate.ofEpochDay(epochDay);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains the current FrenchRepublic local date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current FrenchRepublic local date using the system clock and default time-zone, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public FrenchRepublicDate dateNow() {
        return FrenchRepublicDate.now();
    }

    /**
     * Obtains the current FrenchRepublic local date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     * @return the current FrenchRepublic local date using the system clock, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public FrenchRepublicDate dateNow(ZoneId zone) {
        return FrenchRepublicDate.now(zone);
    }

    /**
     * Obtains the current FrenchRepublic local date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current FrenchRepublic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public FrenchRepublicDate dateNow(Clock clock) {
        return FrenchRepublicDate.now(clock);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains a FrenchRepublic local date from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the FrenchRepublic local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public FrenchRepublicDate date(TemporalAccessor temporal) {
        return FrenchRepublicDate.from(temporal);
    }

    /**
     * Obtains a FrenchRepublic local date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the FrenchRepublic local date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<FrenchRepublicDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<FrenchRepublicDate>) super.localDateTime(temporal);
    }

    /**
     * Obtains a FrenchRepublic zoned date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the FrenchRepublic zoned date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<FrenchRepublicDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<FrenchRepublicDate>) super.zonedDateTime(temporal);
    }

    /**
     * Obtains a FrenchRepublic zoned date-time in this chronology from an {@code Instant}.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param zone  the time-zone, not null
     * @return the FrenchRepublic zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<FrenchRepublicDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<FrenchRepublicDate>) super.zonedDateTime(instant, zone);
    }

    @Override
    public ValueRange range(ChronoField field) {
        if (field == DAY_OF_WEEK) {
            return DOW_RANGE;
        } else if (WeekFields.ISO.dayOfWeek().equals(field)) {
            return DOW_RANGE;
        } else if (field == ALIGNED_WEEK_OF_MONTH) {
            return ALIGNED_WOM_RANGE;
        }
        return super.range(field);
    }
    
    //-----------------------------------------------------------------------
    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (!(era instanceof FrenchRepublicEra)) {
            throw new ClassCastException("Era must be FrenchRepublicEra");
        }
        return (era == FrenchRepublicEra.REPUBLICAN ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public FrenchRepublicEra eraOf(int eraValue) {
        return FrenchRepublicEra.of(eraValue);
    }

    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(FrenchRepublicEra.values());
    }

    //-----------------------------------------------------------------------
    @Override  // override for return type
    public FrenchRepublicDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (FrenchRepublicDate) super.resolveDate(fieldValues, resolverStyle);
    }

}
