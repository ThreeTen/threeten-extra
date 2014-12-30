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
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.*;

import static java.time.temporal.ChronoField.*;
//import static org.threeten.extra.chrono.InternationalFixedChronology.*;

/**
 * A date in the International fixed calendar system.
 * <p>
 * Implements a pure International Fixed calendar (also known as the Cotsworth plan, the Eastman plan,
 * the 13 Month calendar or the Equal Month calendar) a solar calendar proposal for calendar reform designed by
 * Moses B. Cotsworth, who presented it in 1902.</p>
 * <p>
 * It provides for a year of 13 months of 28 days each, with one or two days a year belonging to no month or week.
 * It is therefore a perennial calendar, with every date fixed always on the same weekday.
 * Though it was never officially adopted in any country, it was the official calendar of the Eastman Kodak Company
 * from 1928 to 1989.</p>
 * <p>
 * This date operates using the {@linkplain InternationalFixedChronology International fixed calendar}.
 * This calendar system is a proposed reform calendar system, and is not in common use.
 * The International fixed differs from the Gregorian in terms of month count and length, and the leap year rule.
 * Dates are aligned such that {@code 0001-01-01 (International fixed)} is {@code 0000-12-31 (ISO)}.</p>
 * <p>
 * More information is available in the <a href='https://en.wikipedia.org/wiki/International_Fixed_Calendar'>International fixed Calendar</a> Wikipedia article.</p>
 * <p>
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.</p>
 */
public final class InternationalFixedDate
        extends AbstractDate
        implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -2229133057743750072L;

    /**
     * The days per 400 year cycle.
     */
    private static final int DAYS_PER_CYCLE = 146097;

    /**
     * Number of years in a decade.
     */
    private static final int YEARS_IN_DECADE = 10;

    /**
     * Number of years in a century.
     */
    private static final int YEARS_IN_CENTURY = 100;

    /**
     * Number of years in a millennium.
     */
    private static final int YEARS_IN_MILLENNIUM = 1000;

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

    /**
     * In a leap year, is it the day between June 28th and Sol 1st ?
     */
    private final boolean isLeapDay;

    /**
     * Is is the last day of the year ?
     */
    private final boolean isYearDay;

    //-----------------------------------------------------------------------

    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month, from 1 to 13
     * @param dayOfMonth    the International fixed day-of-month, from 1 to 28, the 29th is only legal for leap day and year day
     */
    private InternationalFixedDate (final int prolepticYear, final int month, final int dayOfMonth) {
        YEAR.checkValidValue (prolepticYear);
        InternationalFixedChronology.MONTH_OF_YEAR_RANGE.checkValidValue (month, MONTH_OF_YEAR);
        InternationalFixedChronology.DAY_OF_MONTH_RANGE.checkValidValue (dayOfMonth, DAY_OF_MONTH);

        if (0 > prolepticYear) {
            throw new DateTimeException ("Invalid date, year must be positive: " + prolepticYear + '-' + month + '-' + dayOfMonth);
        }

        if (dayOfMonth > 28 && month != 6 && month != 13) {
            throw new DateTimeException ("Invalid date: " + prolepticYear + '-' + month + '-' + dayOfMonth);
        }

        this.prolepticYear = prolepticYear;
        this.month = (short) month;
        this.day = (short) dayOfMonth;
        this.isLeapDay = dayOfMonth == 29 && month == 6;
        this.isYearDay = dayOfMonth == 29 && month == 13;

        if (this.isLeapDay && !isLeapYear ()) {
            throw new DateTimeException ("Invalid leap date: " + prolepticYear + '-' + month + '-' + dayOfMonth);
        }
    }

    /**
     * Obtains the current {@code InternationalFixedDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static InternationalFixedDate now () {
        return now (Clock.systemDefaultZone ());
    }

    /**
     * Obtains the current {@code InternationalFixedDate} from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     * @return the current date using the system clock, not null
     */
    public static InternationalFixedDate now (final ZoneId zone) {
        return now (Clock.system (zone));
    }

    /**
     * Obtains the current {@code InternationalFixedDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static InternationalFixedDate now (final Clock clock) {
        LocalDate now = LocalDate.now (clock);

        return InternationalFixedDate.ofEpochDay (now.toEpochDay ());
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month-of-year, from 1 to 13
     * @param dayOfMonth    the International fixed day-of-month, from 1 to 28
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    public static InternationalFixedDate of (final int prolepticYear, final int month, final int dayOfMonth) {
        return new InternationalFixedDate (prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------

    /**
     * Obtains a {@code InternationalFixedDate} from a temporal object.
     * <p>
     * This obtains a date in the International fixed calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code InternationalFixedDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code InternationalFixedDate::from}.
     *
     * @param temporal the temporal object to convert, not null
     * @return the date in the International fixed calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code InternationalFixedDate}
     */
    public static InternationalFixedDate from (final TemporalAccessor temporal) {
        if (temporal instanceof InternationalFixedDate) {
            return (InternationalFixedDate) temporal;
        }

        return InternationalFixedDate.ofEpochDay (temporal.getLong (EPOCH_DAY));
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param dayOfYear     the International fixed day-of-year, from 1 to 371
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-year is invalid for the year
     */
    static InternationalFixedDate ofYearDay (final int prolepticYear, final int dayOfYear) {
        YEAR.checkValidValue (prolepticYear);
        InternationalFixedChronology.DAY_OF_YEAR_RANGE.checkValidValue (dayOfYear, DAY_OF_YEAR);
        boolean leap = InternationalFixedChronology.INSTANCE.isLeapYear (prolepticYear);

        if (dayOfYear > (InternationalFixedChronology.DAYS_IN_YEAR + 1) && !leap) {
            throw new DateTimeException ("Invalid date 'DayOfYear " + dayOfYear + "' as '" + prolepticYear + "' is not a leap year");
        }

        int month = ((dayOfYear - 1) / InternationalFixedChronology.DAYS_IN_MONTH) + 1;

        // In leap years, the leap-month is shorter than the following month, so needs to be adjusted.
        if (leap && month == InternationalFixedChronology.MONTHS_IN_YEAR && dayOfYear >= InternationalFixedChronology.DAYS_IN_YEAR - InternationalFixedChronology.DAYS_IN_MONTH + 1) {
            month++;
        }

        // Subtract days-at-start-of-month from days in year
        int dayOfMonth = dayOfYear - (month - 1) * InternationalFixedChronology.DAYS_IN_MONTH;

        // Adjust for shorter inserted leap-month.
        if (month == InternationalFixedChronology.MONTHS_IN_YEAR + 1) {
            dayOfMonth += InternationalFixedChronology.DAYS_IN_MONTH;
        }

        return of (prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the epoch-day.
     *
     * @param epochDay the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    public static InternationalFixedDate ofEpochDay (final long epochDay) {
        EPOCH_DAY.range ().checkValidValue (epochDay, EPOCH_DAY);
        long zeroDay = epochDay + InternationalFixedChronology.DAYS_0000_TO_1970;

        if (zeroDay < 0) {
            throw new DateTimeException ("Negative epoch invalid: " + epochDay);
        }

        long yearEst = (400 * zeroDay) / DAYS_PER_CYCLE;
        long doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        int month = 1 + (int) ((doyEst - 1 ) / 28);
        int dom = 1 + (int) ((doyEst - 1) % 28);

        if (month == 14) {
            month = 13;
            dom += 28;
        }

        // check year now we are certain it is correct
        int year = YEAR.checkValidIntValue (yearEst);

        return new InternationalFixedDate (year, month, dom);
    }

    private static InternationalFixedDate resolvePreviousValid (final int prolepticYear, final int month, final int day) {
        int monthR = Math.min (month, InternationalFixedChronology.MONTHS_IN_YEAR);
        int dayR = Math.min (day, InternationalFixedChronology.DAYS_IN_MONTH + (month == 6 || month == 13 ? 1 : 0));

        return InternationalFixedDate.of (prolepticYear, monthR, dayR);
    }

    //-----------------------------------------------------------------------

    /**
     * Get the count of leap years since International fixed year 0.
     * <p>
     * This number is negative if the year is prior to International fixed year 0.
     *
     * @param prolepticYear The year.
     * @return The number of leap years since International fixed year 0.
     */
    private static long getLeapYearsBefore (final long prolepticYear) {
        return (prolepticYear / 4) - (prolepticYear / 100) + (prolepticYear / 400);
    }

    /**
     * Validates the object.
     *
     * @return the resolved date, not null
     */
    private Object readResolve () {
        return InternationalFixedDate.of (prolepticYear, month, day);
    }

    //-----------------------------------------------------------------------
    @Override
    int getProlepticYear () {
        return prolepticYear;
    }

    @Override
    int getMonth () {
        return month;
    }

    @Override
    int getDayOfMonth () {
        return day;
    }

    @Override
    int getDayOfYear () {
        return (month - 1) * InternationalFixedChronology.DAYS_IN_MONTH + getDayOfMonth ();
    }

    @Override
    AbstractDate withDayOfYear (final int value) {
        return plusDays (value - getDayOfYear ());
    }

    @Override
    int lengthOfYearInMonths () {
        return InternationalFixedChronology.MONTHS_IN_YEAR;
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth () {
        return ValueRange.of (1, InternationalFixedChronology.WEEKS_IN_MONTH);
    }

    @Override
    InternationalFixedDate resolvePrevious (final int newYear, final int newMonth, final int dayOfMonth) {
        return resolvePreviousValid (newYear, newMonth, dayOfMonth);
    }

    @Override
    public ValueRange range (final TemporalField field) {
        if (field == ChronoField.ALIGNED_WEEK_OF_YEAR) {
            return ValueRange.of (1, InternationalFixedChronology.WEEKS_IN_YEAR);
        } else if (field == ChronoField.MONTH_OF_YEAR) {
            return ValueRange.of (1, InternationalFixedChronology.MONTHS_IN_YEAR);
        }

        return super.range (field);
    }

    //-----------------------------------------------------------------------

    /**
     * Gets the chronology of this date, which is the International fixed calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the International fixed chronology, not null
     */
    @Override
    public InternationalFixedChronology getChronology () {
        return InternationalFixedChronology.INSTANCE;
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The International fixed calendar system only has one era, 'CE',
     * defined by {@link InternationalFixedEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public InternationalFixedEra getEra () {
        return InternationalFixedEra.CE;
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Month lengths do not match those of the ISO calendar system.
     *
     * @return the length of the month in days, 28 or 29
     */
    @Override
    public int lengthOfMonth () {
        return InternationalFixedChronology.DAYS_IN_MONTH + ((this.month == 6 && this.isLeapYear ()) || this.month == 13 || this.isLeapDay || this.isYearDay ? 1 : 0);
    }

    @Override
    public int lengthOfYear () {
        return InternationalFixedChronology.DAYS_IN_YEAR + (isLeapYear () ? 1 : 0);
    }

    //-------------------------------------------------------------------------
    @Override
    public InternationalFixedDate with (final TemporalAdjuster adjuster) {
        return (InternationalFixedDate) adjuster.adjustInto (this);
    }

    @Override
    public InternationalFixedDate with (final TemporalField field, final long newValue) {
        // Evaluate years as a special case, to deal with inserted leap months.
        if (field == ChronoField.YEAR) {
            return plusYears (Math.subtractExact (newValue, getProlepticYear ()));
        }

        return (InternationalFixedDate) super.with (field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public InternationalFixedDate plus (final TemporalAmount amount) {
        return (InternationalFixedDate) amount.addTo (this);
    }

    @Override
    public InternationalFixedDate plus (final long amountToAdd, final TemporalUnit unit) {
        return (InternationalFixedDate) super.plus (amountToAdd, unit);
    }

    /**
     * Returns a copy of this {@code InternationalFixedDate} with the specified period in years added.
     * <p>
     * This method adds the specified amount to the years field in two steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>If necessary, shift the index to account for the inserted/deleted leap-month.</li>
     * </ol>
     * <p>
     * In the International fixed Calendar, the month of December is 13 in non-leap-years, and 14 in leap years.
     * Shifting the index of the month thus means the month would still be the same.
     * <p>
     * In the case of moving from the inserted leap-month (destination year is non-leap), the month index is retained.
     * This has the effect of retaining the same day-of-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearsToAdd the years to add, may be negative
     * @return a {@code InternationalFixedDate} based on this date with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    InternationalFixedDate plusYears (final long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }

        int newYear = YEAR.checkValidIntValue (getProlepticYear () + yearsToAdd);

        // Otherwise, one of the following is true:
        // 1 - Before the leap month, nothing to do (most common)
        // 2 - Both source and destination in leap-month, nothing to do
        // 3 - Both source and destination after leap month in leap year, nothing to do
        // 4 - Source in leap month, but destination year not leap. Retain month index, preserving day-of-year.
        // 5 - Source after leap month, but destination year not leap. Move month index back.
        return resolvePreviousValid (newYear, month, day);
    }

    /**
     * Returns a copy of this {@code InternationalFixedDate} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2006-12-13 plus one month would result in the invalid date 2006-13-13.
     * Instead of returning an invalid result, the last valid day of the month, 2006-13-07, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthsToAdd the months to add, may be negative
     * @return a {@code InternationalFixedDate} based on this date with the months added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    InternationalFixedDate plusMonths (final long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }

        long calcMonths = Math.addExact (getProlepticMonth (), monthsToAdd);
        int newYear = YEAR.checkValidIntValue (Math.floorDiv (calcMonths, InternationalFixedChronology.MONTHS_IN_YEAR));
        int newMonth = Math.toIntExact (calcMonths % InternationalFixedChronology.MONTHS_IN_YEAR) + 1;

        return resolvePreviousValid (newYear, newMonth, getDayOfMonth ());
    }

    @Override
    public InternationalFixedDate minus (final TemporalAmount amount) {
        return (InternationalFixedDate) amount.subtractFrom (this);
    }

    @Override
    public InternationalFixedDate minus (final long amountToSubtract, final TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus (Long.MAX_VALUE, unit).plus (1, unit) : plus (-amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override  // for covariant return type
    @SuppressWarnings ("unchecked")
    public ChronoLocalDateTime<InternationalFixedDate> atTime (final LocalTime localTime) {
        return (ChronoLocalDateTime<InternationalFixedDate>) ChronoLocalDate.super.atTime (localTime);
    }

    @Override
    public long until (final Temporal endExclusive, final TemporalUnit unit) {
        return until (InternationalFixedDate.from (endExclusive), unit);
    }

    @Override
    long until (final AbstractDate end, final TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            InternationalFixedDate ifxEnd = InternationalFixedDate.from (end);
            switch ((ChronoUnit) unit) {
                case YEARS:
                    return yearsUntil (ifxEnd);
                case DECADES:
                    return yearsUntil (ifxEnd) / YEARS_IN_DECADE;
                case CENTURIES:
                    return yearsUntil (ifxEnd) / YEARS_IN_CENTURY;
                case MILLENNIA:
                    return yearsUntil (ifxEnd) / YEARS_IN_MILLENNIUM;
                default:
                    break;
            }
        }

        return super.until (end, unit);
    }

    /**
     * Get the number of years from this date to the given day.
     *
     * @param end The end date.
     * @return The number of years from this date to the given day.
     */
    long yearsUntil (final InternationalFixedDate end) {
        // If either date is after the inserted leap month, and the other year isn't leap, simulate the effect of the inserted month.
        long startYear = getProlepticYear () * 512L + getDayOfYear ();
        long endYear = end.getProlepticYear () * 512L + end.getDayOfYear ();

        return (endYear - startYear) / 512L;
    }

    @Override
    public ChronoPeriod until (final ChronoLocalDate endDateExclusive) {
        InternationalFixedDate end = InternationalFixedDate.from (endDateExclusive);
        int years = Math.toIntExact (yearsUntil (end));
        // Get to the same "whole" year.
        InternationalFixedDate sameYearEnd = end.plusYears (years);
        int months = (int) monthsUntil (sameYearEnd);
        int days = (int) daysUntil (sameYearEnd.plusMonths (months));

        return getChronology ().period (years, months, days);
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay () {
        long epochDay = ((long) getProlepticYear ()) * (InternationalFixedChronology.DAYS_IN_YEAR + 1) + getLeapYearsBefore (getProlepticYear ()) + getDayOfYear ();

        return epochDay - InternationalFixedChronology.DAYS_0000_TO_1970;
    }

    @Override
    int getYearOfEra () {
        return getProlepticYear ();
    }

    @Override
    public String toString () {
        StringBuilder buf = new StringBuilder (getChronology ().toString ());
        buf.append (' ')
           .append (getEra ())
           .append (' ')
           .append (getYearOfEra ())
           .append (getMonth () < 10 ? "-0" : '-').append (getMonth ())
           .append (getDayOfMonth () < 10 ? "-0" : '-').append (getDayOfMonth ());

        return buf.toString ();
    }
}
