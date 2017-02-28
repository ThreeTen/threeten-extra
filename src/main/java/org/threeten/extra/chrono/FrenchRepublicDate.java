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

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import static org.threeten.extra.chrono.FrenchRepublicChronology.DOW_RANGE;

/**
 * A date in the FrenchRepublic calendar system.
 * <p>
 * This date operates using the {@linkplain FrenchRepublicChronology FrenchRepublic calendar}.
 * This calendar was used in France after the revolution, in the years 1793-1805 and shortly
 * for 18 days in 1871 (see https://en.wikipedia.org/wiki/French_Republican_Calendar)
 * Dates are aligned such that {@code 0001-01-01 (FrenchRepublic)} is {@code 1972-09-22 (ISO)}.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class FrenchRepublicDate
        extends AbstractNileDate
        implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -7920528871688876868L;
    /**
     * The difference between the ISO and FrenchRepublic epoch day count.
     */
    private static final int EPOCH_DAY_DIFFERENCE = 64748;  // MJD values
    /**
     * The days per 4 year cycle.
     */
    private static final int DAYS_PER_CYCLE = (365 * 4) + 1;
    
    
    /**
     * The proleptic year.
     */
    private final int prolepticYear;
    /**
     * The month.
     */
    private final short month;
    /**
     * The day.
     */
    private final short day;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code FrenchRepublicDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static FrenchRepublicDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current {@code FrenchRepublicDate} from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone  the zone ID to use, not null
     * @return the current date using the system clock, not null
     */
    public static FrenchRepublicDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current {@code FrenchRepublicDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static FrenchRepublicDate now(Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return FrenchRepublicDate.ofEpochDay(now.toEpochDay());
    }

    /**
     * Obtains a {@code FrenchRepublicDate} representing a date in the FrenchRepublic calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code FrenchRepublicDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the FrenchRepublic proleptic-year
     * @param month  the FrenchRepublic month-of-year, from 1 to 13
     * @param dayOfMonth  the FrenchRepublic day-of-month, from 1 to 30
     * @return the date in FrenchRepublic calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static FrenchRepublicDate of(int prolepticYear, int month, int dayOfMonth) {
        return FrenchRepublicDate.create(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a {@code FrenchRepublicDate} from a temporal object.
     * <p>
     * This obtains a date in the FrenchRepublic calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code FrenchRepublicDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code FrenchRepublicDate::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in FrenchRepublic calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code FrenchRepublicDate}
     */
    public static FrenchRepublicDate from(TemporalAccessor temporal) {
        if (temporal instanceof FrenchRepublicDate) {
            return (FrenchRepublicDate) temporal;
        }
        return FrenchRepublicDate.ofEpochDay(temporal.getLong(EPOCH_DAY));
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        return ValueRange.of(1, getMonth() == 13 ? 1 : 3);
    }

    @Override
    public ValueRange range(TemporalField field) {
        if (WeekFields.ISO.dayOfWeek().equals(field)) {
            return DOW_RANGE;
        }

        return super.range(field);
    }    

    @Override
    public long getLong(TemporalField field) {
        if (WeekFields.ISO.dayOfWeek().equals(field)) {
            return day;
        }
        return super.getLong(field);
    }
    
    @Override
    int getDayOfWeek() {
        return (int) Math.floorMod(day, 10);
    }

    @Override
    int lengthOfWeek() {
        return 10;
    }

    
    
    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code FrenchRepublicDate} representing a date in the FrenchRepublic calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code FrenchRepublicDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the FrenchRepublic proleptic-year
     * @param dayOfYear  the FrenchRepublic day-of-year, from 1 to 366
     * @return the date in FrenchRepublic calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static FrenchRepublicDate ofYearDay(int prolepticYear, int dayOfYear) {
        FrenchRepublicChronology.YEAR_RANGE.checkValidValue(prolepticYear, YEAR);
        DAY_OF_YEAR.range().checkValidValue(dayOfYear, DAY_OF_YEAR);
        if (dayOfYear == 366 && !FrenchRepublicChronology.INSTANCE.isLeapYear(prolepticYear)) {
            throw new DateTimeException("Invalid date 'Complementary day 6' as '" + prolepticYear + "' is not a leap year");
        }
        return new FrenchRepublicDate(prolepticYear, (dayOfYear - 1) / 30 + 1, (dayOfYear - 1) % 30 + 1);
    }

    /**
     * Obtains a {@code FrenchRepublicDate} representing a date in the FrenchRepublic calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in FrenchRepublic calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static FrenchRepublicDate ofEpochDay(final long epochDay) {
        EPOCH_DAY.range().checkValidValue(epochDay, EPOCH_DAY);  // validate outer bounds
        // use of French Rev. -1 makes leap year at the end of cycle
        long frenchRevEpochDayPlus365 = epochDay + EPOCH_DAY_DIFFERENCE + 365;
        long cycle = Math.floorDiv(frenchRevEpochDayPlus365, DAYS_PER_CYCLE);
        long daysInCycle = Math.floorMod(frenchRevEpochDayPlus365, DAYS_PER_CYCLE);
        if (daysInCycle == DAYS_PER_CYCLE - 1) {
            int year = (int) (cycle * 4 + 3);
            return ofYearDay(year, 366);
        }
        int year = (int) (cycle * 4 + daysInCycle / 365);
        int doy = (int) ((daysInCycle % 365) + 1);
        return ofYearDay(year, doy);
    }

    private static FrenchRepublicDate resolvePreviousValid(int prolepticYear, int month, int day) {
        if (month == 13 && day > 5) {
            day = FrenchRepublicChronology.INSTANCE.isLeapYear(prolepticYear) ? 6 : 5;
        }
        return new FrenchRepublicDate(prolepticYear, month, day);
    }

    /**
     * Creates a {@code FrenchRepublicDate} validating the input.
     *
     * @param prolepticYear  the FrenchRepublic proleptic-year
     * @param month  the FrenchRepublic month-of-year, from 1 to 13
     * @param dayOfMonth  the FrenchRepublic day-of-month, from 1 to 30
     * @return the date in FrenchRepublic calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    static FrenchRepublicDate create(int prolepticYear, int month, int dayOfMonth) {
        FrenchRepublicChronology.YEAR_RANGE.checkValidValue(prolepticYear, YEAR);
        FrenchRepublicChronology.MOY_RANGE.checkValidValue(month, MONTH_OF_YEAR);
        FrenchRepublicChronology.DOM_RANGE.checkValidValue(dayOfMonth, DAY_OF_MONTH);
        if (month == 13 && dayOfMonth > 5) {
            if (FrenchRepublicChronology.INSTANCE.isLeapYear(prolepticYear)) {
                if (dayOfMonth > 6) {
                    throw new DateTimeException("Invalid date 'Complementary day " + dayOfMonth + "', valid range from 1 to 5, or 1 to 6 in a leap year");
                }
            } else {
                if (dayOfMonth == 6) {
                    throw new DateTimeException("Invalid date 'Complementary day 6' as '" + prolepticYear + "' is not a leap year");
                } else {
                    throw new DateTimeException("Invalid date 'Complementary day " + dayOfMonth + "', valid range from 1 to 5, or 1 to 6 in a leap year");
                }
            }
        }
        return new FrenchRepublicDate(prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the FrenchRepublic proleptic-year
     * @param month  the FrenchRepublic month, from 1 to 13
     * @param dayOfMonth  the FrenchRepublic day-of-month, from 1 to 30
     */
    private FrenchRepublicDate(int prolepticYear, int month, int dayOfMonth) {
        this.prolepticYear = prolepticYear;
        this.month = (short) month;
        this.day = (short) dayOfMonth;
    }

    /**
     * Validates the object.
     *
     * @return the resolved date, not null
     */
    private Object readResolve() {
        return FrenchRepublicDate.create(prolepticYear, month, day);
    }

    //-----------------------------------------------------------------------
    @Override
    int getEpochDayDifference() {
        return EPOCH_DAY_DIFFERENCE;
    }

    @Override
    int getProlepticYear() {
        return prolepticYear;
    }

    @Override
    int getMonth() {
        return month;
    }

    @Override
    int getDayOfMonth() {
        return day;
    }

    @Override
    FrenchRepublicDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        return resolvePreviousValid(newYear, newMonth, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the FrenchRepublic calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the FrenchRepublic chronology, not null
     */
    @Override
    public FrenchRepublicChronology getChronology() {
        return FrenchRepublicChronology.INSTANCE;
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The FrenchRepublic calendar system has two eras, 'AM' and 'BEFORE_AM',
     * defined by {@link FrenchRepublicEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public FrenchRepublicEra getEra() {
        return (prolepticYear >= 1 ? FrenchRepublicEra.REPUBLICAN : FrenchRepublicEra.BEFORE_REPUBLICAN);
    }

    //-------------------------------------------------------------------------
    @Override
    public FrenchRepublicDate with(TemporalAdjuster adjuster) {
        return (FrenchRepublicDate) adjuster.adjustInto(this);
    }

    @Override
    public FrenchRepublicDate with(TemporalField field, long newValue) {
        return (FrenchRepublicDate) super.with(field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public FrenchRepublicDate plus(TemporalAmount amount) {
        return (FrenchRepublicDate) amount.addTo(this);
    }

    @Override
    public FrenchRepublicDate plus(long amountToAdd, TemporalUnit unit) {
        return (FrenchRepublicDate) super.plus(amountToAdd, unit);
    }

    @Override
    public FrenchRepublicDate minus(TemporalAmount amount) {
        return (FrenchRepublicDate) amount.subtractFrom(this);
    }

    @Override
    public FrenchRepublicDate minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override  // for covariant return type
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<FrenchRepublicDate> atTime(LocalTime localTime) {
        return (ChronoLocalDateTime<FrenchRepublicDate>) ChronoLocalDate.super.atTime(localTime);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return super.until(FrenchRepublicDate.from(endExclusive), unit);
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        return super.doUntil(FrenchRepublicDate.from(endDateExclusive));
    }

    @Override
    public long toEpochDay() {
        long year = (long) getProlepticYear();
        long calendarEpochDay = (year * 365) + Math.floorDiv(year, 4) + (getDayOfYear() - 1);
        return calendarEpochDay - 365 - getEpochDayDifference();
    }    
}
