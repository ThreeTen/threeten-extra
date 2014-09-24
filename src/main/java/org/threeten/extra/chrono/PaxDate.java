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

import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.YEAR;
import static org.threeten.extra.chrono.PaxChronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.PaxChronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.PaxChronology.DAYS_IN_YEAR;
import static org.threeten.extra.chrono.PaxChronology.MONTHS_IN_YEAR;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
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

/**
 * /** A date without a time-zone in the Pax calendar system, such as {@code 2007-12-03}.
 * <p>
 * {@code PaxDate} is an immutable date-time object that represents a date, often viewed as year-month-day. Other date fields, such as day-of-year, day-of-week and week-of-year, can also be accessed.
 * <p>
 * This class does not store or represent a time or time-zone. Instead, it is a description of the date, as used for birthdays. It cannot represent an instant on the time-line without additional
 * information such as an offset or time-zone.
 * <p>
 * The Pax calendar system is a proposed reform calendar system, and is not in common use. More information is available in the <a href="http://en.wikipedia.org/wiki/Pax_Calendar">Pax Calendar</a>
 * Wikipedia article.
 * <h3>Implementation Requirements</h3> This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the identity hash code or use the distinction between equals() and ==.
 */
public final class PaxDate extends AbstractDate implements ChronoLocalDate, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2229133057743750072L;

    /**
     * Count of days from the start of the Pax Epoch to ISO 1970-01-01.
     */
    private static final int DAYS_PAX_0000_TO_ISO_1970 = 719527;

    /**
     * Number of seconds in a day.
     */
    private static final long SECONDS_PER_DAY = 86400;

    /**
     * Number of years in a decade.
     */
    private static final long YEARS_IN_DECADE = 10;

    /**
     * Number of years in a century.
     */
    private static final long YEARS_IN_CENTURY = 100;

    /**
     * Number of years in a millennium.
     */
    private static final long YEARS_IN_MILLENNIUM = 1000;

    /**
     * The year.
     */
    private final int year;

    /**
     * The month-of-year.
     */
    private final short month;

    /**
     * The day-of-month.
     */
    private final short day;

    /**
     * Constructor, previously validated.
     *
     * @param year the year to represent
     * @param month the month-of-year to represent, from 1 to 14
     * @param dayOfMonth the day-of-month to represent, valid for year-month, from 1 to 28
     */
    private PaxDate(final int year, final int month, final int dayOfMonth) {
        this.year = year;
        this.month = (short) month;
        this.day = (short) dayOfMonth;
    }

    /**
     * Obtains an instance of {@code PaxDate} from a temporal object.
     * <p>
     * A {@code TemporalAccessor} represents some form of date and time information. This factory converts the arbitrary temporal object to an instance of {@code PaxDate}.
     * <p>
     * The conversion uses the {@link TemporalQueries#localDate()} query, which relies on extracting the {@link ChronoField#EPOCH_DAY EPOCH_DAY} field.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery} allowing it to be used as a query via method reference, {@code PaxDate::from}.
     *
     * @param temporal the temporal object to convert, not null
     * @return the local date, not null
     * @throws DateTimeException if unable to convert to a {@code PaxDate}
     */
    public static PaxDate from(final TemporalAccessor temporal) {
        final LocalDate date = temporal.query(TemporalQueries.localDate());
        if (date == null) {
            throw new DateTimeException("Unable to obtain LocalDate from TemporalAccessor: "
                    + temporal + ", type " + temporal.getClass().getName());
        }
        return ofEpochDay(date.toEpochDay());
    }

    /**
     * Obtains the current date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static PaxDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today. Using this method allows the use of an alternate clock for testing. The alternate clock may be introduced using
     * {@link Clock dependency injection}.
     *
     * @param clock the clock to use, not null
     * @return the current date, not null
     */
    public static PaxDate now(final Clock clock) {
        Objects.requireNonNull(clock, "clock");
        // Called once, so the instant and it's offset will use the same value
        final Instant now = clock.instant();
        final ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        final long epochSec = now.getEpochSecond() + offset.getTotalSeconds();
        final long epochDay = Math.floorDiv(epochSec, SECONDS_PER_DAY);
        return ofEpochDay(epochDay);
    }

    /**
     * Obtains the current date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date. Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     * @return the current date using the system clock, not null
     */
    public static PaxDate now(final ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains an instance of {@code PaxDate} from a year, month and day.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month the month-of-year to represent, from 1 to 14
     * @param dayOfMonth the day-of-month to represent, from 1 to 28
     * @return the local date, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public static PaxDate of(final int prolepticYear, final int month, final int dayOfMonth) {
        YEAR.checkValidValue(prolepticYear);
        if (month < 1 || month > MONTHS_IN_YEAR + 1) {
            throw new DateTimeException("Invalid month " + month);
        } else if (month == MONTHS_IN_YEAR + 1 && !PaxChronology.INSTANCE.isLeapYear(prolepticYear)) {
            throw new DateTimeException("Invalid month 14 as " + prolepticYear + "is not a leap year");
        }

        if (dayOfMonth < 1 || dayOfMonth > DAYS_IN_MONTH) {
            throw new DateTimeException("Invalid day-of-month " + dayOfMonth);
        } else if (dayOfMonth > DAYS_IN_WEEK && month == MONTHS_IN_YEAR && PaxChronology.INSTANCE.isLeapYear(prolepticYear)) {
            throw new DateTimeException("Invalid date during Pax as " + prolepticYear + " is a leap year");
        }

        return new PaxDate(prolepticYear, month, dayOfMonth);
    }

    public static PaxDate ofEpochDay(final long epochDay) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Obtains an instance of {@code PaxDate} from a year and day-of-year.
     * <p>
     * The day-of-year must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear the year to represent, from MIN_YEAR to MAX_YEAR
     * @param dayOfYear the day-of-year to represent, from 1 to 371
     * @return the local date, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-year is invalid for the month-year
     */
    public static PaxDate ofYearDay(final int prolepticYear, final int dayOfYear) {
        YEAR.checkValidValue(prolepticYear);
        if (dayOfYear < 1 || dayOfYear > DAYS_IN_YEAR + DAYS_IN_WEEK) {
            throw new DateTimeException("Inavlid date 'DayOfYear " + dayOfYear + "'");
        }
        final boolean leap = PaxChronology.INSTANCE.isLeapYear(prolepticYear);
        if (dayOfYear > DAYS_IN_YEAR && !leap) {
            throw new DateTimeException("Invalid date 'DayOfYear " + dayOfYear + "' as '" + prolepticYear + "' is not a leap year");
        }

        int month = (dayOfYear - 1) / MONTHS_IN_YEAR;

        // In leap years, the leap-month is shorter than the following month, so needs to be adjusted.
        if (month == MONTHS_IN_YEAR + 1
                && dayOfYear < DAYS_IN_YEAR - DAYS_IN_MONTH + DAYS_IN_WEEK + 1) {
            month--;
        }

        // Subtract days-at-start-of-month from days in year
        int dayOfMonth = dayOfYear - (month - 1) * DAYS_IN_MONTH;

        // Adjust for shorter inserted leap-month.
        if (month == MONTHS_IN_YEAR + 1) {
            dayOfMonth += DAYS_IN_MONTH - DAYS_IN_WEEK;
        }

        return of(prolepticYear, month, dayOfMonth);
    }

    /**
     * The Pax day-of-week is aligned to Sunday, not Monday as in the ISO calendar.
     *
     * @param dayOfWeek The Pax day-of-week, where 1 is Sunday and 7 is Saturday.
     * @return The ISO day-of-week, where 1 is Monday and 7 is Sunday.
     */
    private static long getISODayOfWeek(final int dayOfWeek) {
        return dayOfWeek == 1 ? DAYS_IN_WEEK : dayOfWeek - 1;
    }

    /**
     * Get the count of leap months since proleptic month 0.
     * <p>
     * This number is negative if the month is prior to Pax year 0.
     *
     * @param prolepticMonth The month.
     * @return The number of leap months since proleptic month 0.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private static long getLeapMonthsBefore(final long prolepticMonth) {
        // need to return a non-negative number.
        final long absMonth = Math.abs(prolepticMonth);
        // See getLeapYearsBefore for the reasoning behind the calculation.
        // return Long.signum(prolepticMonth) * (17 * (absMonth / (100 * MONTHS_IN_YEAR + getLeapYearsbefore(100))) + ((absMonth - 1) / (100 * MONTHS_IN_YEAR +
        // getLeapYearsbefore(100))) - ((absMonth - 1) / (400 * MONTHS_IN_YEAR + getLeapYearsbefore(400)))
        // + ((absMonth % (100 * MONTHS_IN_YEAR + getLeapYearsbefore(100))) / (6 * MONTHS_IN_YEAR + 1)));
        return Long.signum(prolepticMonth) * (17 * (absMonth / 1317) + ((absMonth - 1) / 1317) - ((absMonth - 1) / 5271) + ((absMonth % 1317) / 79));
    }

    /**
     * Get the count of leap years since Pax year 0.
     * <p>
     * This number is negative if the year is prior to Pax year 0.
     *
     * @param prolepticYear The year.
     * @return The number of leap years since Pax year 0.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private static int getLeapYearsBefore(final int prolepticYear) {
        // need to return a non-negative number.
        final int absYear = Math.abs(prolepticYear);
        // Calculation is like this:
        // - In every century (years X00 - X99), there are 17 leap years (multiples of 6 and at 99).
        // - Every century is a leap year...
        // - ... except every 400 years.
        // - Count the elapsed multiples of 6 since the start of the century.
        return Integer.signum(prolepticYear) * (17 * (absYear / 100) + ((absYear - 1) / 100) - ((absYear - 1) / 400) + ((absYear % 100) / 6));
    }

    /**
     * Resolves the date, resolving days past the end of month, or non-existent months.
     *
     * @param year the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param month the month-of-year to represent, validated from 1 to 14
     * @param day the day-of-month to represent, validated from 1 to 28
     * @return the resolved date, not null
     */
    private static PaxDate resolvePreviousValid(final int year, final int month, final int day) {
        final int monthR = Math.min(month, MONTHS_IN_YEAR + (PaxChronology.INSTANCE.isLeapYear(year) ? 1 : 0));
        final int dayR = Math.min(day, month == MONTHS_IN_YEAR && PaxChronology.INSTANCE.isLeapYear(year) ? DAYS_IN_WEEK : DAYS_IN_MONTH);
        return PaxDate.of(year, monthR, dayR);
    }

    @Override
    public PaxChronology getChronology() {
        return PaxChronology.INSTANCE;
    }

    /**
     * Gets the day-of-month field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 28
     */
    @Override
    public int getDayOfMonth() {
        return day;
    }

    /**
     * Get the day of the year.
     *
     * @return The day of the year, from 1 to 371.
     */
    @Override
    public int getDayOfYear() {
        return (getMonth() - 1) * DAYS_IN_MONTH
                - (getMonth() == MONTHS_IN_YEAR + 1 ? DAYS_IN_MONTH + DAYS_IN_WEEK : 0) + getDayOfMonth();
    }

    /**
     * Gets the month-of-year field from 1 to 14.
     * <p>
     * This method returns the month as an {@code int} from 1 to 14.
     *
     * @return the month-of-year, from 1 to 14
     */
    @Override
    public int getMonth() {
        return month;
    }

    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     * <p>
     * The year returned by this method is proleptic as per {@code get(YEAR)}. To obtain the year-of-era, use {@code get(YEAR_OF_ERA}.
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    @Override
    public int lengthOfMonth() {
        return month == MONTHS_IN_YEAR && isLeapYear() ? DAYS_IN_WEEK : DAYS_IN_MONTH;
    }

    @Override
    public int lengthOfYear() {
        return DAYS_IN_YEAR + (isLeapYear() ? DAYS_IN_WEEK : 0);
    }

    /**
     * Returns a copy of this {@code PaxDate} with the specified number of days subtracted.
     * <p>
     * This method subtracts the specified amount from the days field decrementing the month and year fields as necessary to ensure the result remains valid. The result is only invalid if the
     * maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-01 minus one day would result in 2008-13-28.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param daysToSubtract the days to subtract, may be negative
     * @return a {@code PaxDate} based on this date with the days subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public PaxDate minusDays(final long daysToSubtract) {
        return (daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-daysToSubtract));
    }

    /**
     * Returns a copy of this {@code PaxDate} with the specified period in months subtracted.
     * <p>
     * This method subtracts the specified amount from the months field in three steps:
     * <ol>
     * <li>Subtract the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2006-14-09 minus one month would result in the invalid date 2006-13-09. Instead of returning an invalid result, the last valid day of the leap week, 2006-13-07, is selected
     * instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthsToSubtract the months to subtract, may be negative
     * @return a {@code PaxDate} based on this date with the months subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public PaxDate minusMonths(final long monthsToSubtract) {
        return (monthsToSubtract == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-monthsToSubtract));
    }

    /**
     * Returns a copy of this {@code PaxDate} with the specified period in weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks from the days field decrementing the month and year fields as necessary to ensure the result remains valid. The result is only invalid if the
     * maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-07 minus one week would result in 2008-13-28.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeksToSubtract the weeks to subtract, may be negative
     * @return a {@code PaxDate} based on this date with the weeks subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public PaxDate minusWeeks(final long weeksToSubtract) {
        return (weeksToSubtract == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeksToSubtract));
    }

    /**
     * Returns a copy of this {@code PaxDate} with the specified period in years subtracted.
     * <p>
     * This method subtracts the specified amount from the years field in two steps:
     * <ol>
     * <li>Subtract the input years to the year field</li>
     * <li>If necessary, shift the index to account for the inserted/deleted leap-month.</li>
     * </ol>
     * <p>
     * In the Pax Calendar, the month of December is 13th in non-leap-years, and 14th in leap years. Shifting the index of the month thus means the month would still be the same.
     * <p>
     * In the case of moving from the inserted leap-month (destination year is non-leap), the month index is retained. This has the effect of retaining the same day-of-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearsToSubtract the years to subtract, may be negative
     * @return a {@code PaxDate} based on this date with the years subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public PaxDate minusYears(final long yearsToSubtract) {
        return (yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract));
    }

    /**
     * Returns a copy of this {@code PaxDate} with the specified number of days added.
     * <p>
     * This method adds the specified amount to the days field incrementing the month and year fields as necessary to ensure the result remains valid. The result is only invalid if the maximum/minimum
     * year is exceeded.
     * <p>
     * For example, 2008-13-28 plus one day would result in 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param daysToAdd the days to add, may be negative
     * @return a {@code PaxDate} based on this date with the days added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    public PaxDate plusDays(final long daysToAdd) {
        if (daysToAdd == 0) {
            return this;
        }
        final long epochOffset = Math.addExact(toEpochDay(), daysToAdd);
        return PaxDate.ofEpochDay(epochOffset);
    }

    /**
     * Returns a copy of this {@code PaxDate} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2006-12-13 plus one month would result in the invalid date 2006-13-13. Instead of returning an invalid result, the last valid day of the month, 2006-13-07, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthsToAdd the months to add, may be negative
     * @return a {@code PaxDate} based on this date with the months added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    public PaxDate plusMonths(final long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        final long calcMonths = getProlepticMonth() + monthsToAdd;
        // "Regularize" the month count, as if years were all 13 months long.
        final long monthsRegularized = calcMonths - getLeapMonthsBefore(calcMonths);
        final int newYear = YEAR.checkValidIntValue(Math.floorDiv(monthsRegularized, MONTHS_IN_YEAR));
        final int newMonth = (int) calcMonths - (newYear * MONTHS_IN_YEAR + getLeapYearsBefore(newYear));
        return resolvePreviousValid(newYear, newMonth, getDayOfMonth());
    }

    /**
     * Returns a copy of this {@code PaxDate} with the specified period in weeks added.
     * <p>
     * This method adds the specified amount in weeks to the days field incrementing the month and year fields as necessary to ensure the result remains valid. The result is only invalid if the
     * maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-28 plus one week would result in 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeksToAdd the weeks to add, may be negative
     * @return a {@code PaxDate} based on this date with the weeks added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public PaxDate plusWeeks(final long weeksToAdd) {
        return plusDays(Math.multiplyExact(weeksToAdd, DAYS_IN_WEEK));
    }

    /**
     * Returns a copy of this {@code PaxDate} with the specified period in years added.
     * <p>
     * This method adds the specified amount to the years field in two steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>If necessary, shift the index to account for the inserted/deleted leap-month.</li>
     * </ol>
     * <p>
     * In the Pax Calendar, the month of December is 13th in non-leap-years, and 14th in leap years. Shifting the index of the month thus means the month would still be the same.
     * <p>
     * In the case of moving from the inserted leap-month (destination year is non-leap), the month index is retained. This has the effect of retaining the same day-of-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearsToAdd the years to add, may be negative
     * @return a {@code PaxDate} based on this date with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    public PaxDate plusYears(final long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        final int newYear = YEAR.checkValidIntValue(getYear() + yearsToAdd);
        // Retain actual month (not index) in the case where a leap month is to be inserted.
        if (getMonth() == MONTHS_IN_YEAR && !isLeapYear() && PaxChronology.INSTANCE.isLeapYear(newYear)) {
            return of(newYear, getMonth() + 1, getDayOfMonth());
        }
        // Otherwise, one of the following is true:
        // 1 - Before the leap month, nothing to do (most common)
        // 2 - Both source and destination in leap-month, nothing to do
        // 3 - Both source and destination after leap month in leap year, nothing to do
        // 4 - Source in leap month, but destination year not leap. Retain month index, preserving day-of-year.
        // 5 - Source after leap month, but destination year not leap. Move month index back.
        return resolvePreviousValid(newYear, month, day);
    }

    @Override
    public PaxDate plus(TemporalAmount amount) {
        return (PaxDate) amount.addTo(this);
    }

    @Override
    public PaxDate plus(long amountToAdd, TemporalUnit unit) {
        return (PaxDate) super.plus(amountToAdd, unit);
    }

    @Override
    public PaxDate minus(TemporalAmount amount) {
        return (PaxDate) amount.subtractFrom(this);
    }

    @Override
    public PaxDate minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    @Override
    public ValueRange range(final TemporalField field) {
        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                switch ((ChronoField) field) {
                    case ALIGNED_WEEK_OF_MONTH:
                        return ValueRange.of(1, (lengthOfMonth() - 1) / DAYS_IN_WEEK);
                    case ALIGNED_WEEK_OF_YEAR:
                        return ValueRange.of(1, (lengthOfYear() - 1) / DAYS_IN_WEEK);
                    case DAY_OF_MONTH:
                        return ValueRange.of(1, lengthOfMonth());
                    case DAY_OF_YEAR:
                        return ValueRange.of(1, lengthOfYear());
                    case MONTH_OF_YEAR:
                        return ValueRange.of(1, MONTHS_IN_YEAR + (isLeapYear() ? 1 : 0));
                    default:
                        return field.range();
                }
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.rangeRefinedBy(this);
    }

    @Override
    public long toEpochDay() {
        final long days = getYear() * DAYS_IN_YEAR + getLeapYearsBefore(getYear()) * DAYS_IN_WEEK + (getMonth() - 1) * DAYS_IN_MONTH + getDayOfMonth() - 1;
        // Adjust for short leap month if after, then rebase to ISO 1970.
        return days - (getMonth() == MONTHS_IN_YEAR + 1 ? DAYS_IN_MONTH - DAYS_IN_WEEK : 0) - DAYS_PAX_0000_TO_ISO_1970;
    }

    @Override
    public ChronoPeriod until(final ChronoLocalDate endDate) {
        final PaxDate end = PaxDate.from(endDate);
        final long years = yearsUntil(end);
        // Get to the same "whole" year.
        final PaxDate sameYearEnd = end.plusYears(years);
        final int months = (int) monthsUntil(sameYearEnd);
        final int days = (int) daysUntil(sameYearEnd.plusMonths(months));
        return Period.of(Math.toIntExact(years), months, days);
    }

    @Override
    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        final PaxDate end = PaxDate.from(endExclusive);
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case DAYS:
                    return daysUntil(end);
                case WEEKS:
                    return daysUntil(end) / DAYS_IN_WEEK;
                case MONTHS:
                    return monthsUntil(end);
                case YEARS:
                    return yearsUntil(end);
                case DECADES:
                    return yearsUntil(end) / YEARS_IN_DECADE;
                case CENTURIES:
                    return yearsUntil(end) / YEARS_IN_CENTURY;
                case MILLENNIA:
                    return yearsUntil(end) / YEARS_IN_MILLENNIUM;
                case ERAS:
                    return end.getLong(ERA) - getLong(ERA);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.between(this, end);
    }

    @Override
    public PaxDate with(TemporalAdjuster adjuster) {
        return (PaxDate) adjuster.adjustInto(this);
    }

    @Override
    public PaxDate with(final TemporalField field, final long newValue) {
        if (field == ChronoField.YEAR) {
            return plusYears(newValue - getYear());
        }
        return (PaxDate) super.with(field, newValue);
    }

    /**
     * Returns a copy of this date with the day-of-month altered. If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param newDayOfMonth the day-of-month to set in the result, from 1 to 7 or 28
     * @return a {@code PaxDate} based on this date with the requested day, not null
     * @throws DateTimeException if the day-of-month value is invalid
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public PaxDate withDayOfMonth(final int newDayOfMonth) {
        if (getDayOfMonth() == newDayOfMonth) {
            return this;
        }
        return of(getYear(), getMonth(), newDayOfMonth);
    }

    /**
     * Returns a copy of this date with the day-of-year altered. If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear the day-of-year to set in the result, from 1 to 364 or 371
     * @return a {@code PaxDate} based on this date with the requested day, not null
     * @throws DateTimeException if the day-of-year value is invalid
     * @throws DateTimeException if the day-of-year is invalid for the year
     */
    @Override
    public PaxDate withDayOfYear(final int dayOfYear) {
        if (this.getDayOfYear() == dayOfYear) {
            return this;
        }
        return ofYearDay(year, dayOfYear);
    }

    /**
     * Get the number of days from this date to the given day.
     *
     * @param end The end date.
     * @return The number of days from this date to the given day.
     */
    private long daysUntil(final PaxDate end) {
        return end.toEpochDay() - toEpochDay();
    }

    /**
     * Get the proleptic month from the start of the epoch (Pax 0000).
     *
     * @return The proleptic month.
     */
    @Override
    long getProlepticMonth() {
        return getYear() * MONTHS_IN_YEAR + getLeapYearsBefore(getYear()) + getMonth() - 1;
    }

    /**
     * Get the number of months from this date to the given day.
     *
     * @param end The end date.
     * @return The number of months from this date to the given day.
     */
    private long monthsUntil(final PaxDate end) {
        // Multiplying by the days-in-month (+1) makes the propleptic count a "place" (ie, the 10's place).
        // This means that if the starting date is before, it moves the count to the prior unit (24 - 8 = 16, and we only care about the 10's place).
        final long startMonth = getProlepticMonth() * (DAYS_IN_MONTH + 1) + getDayOfMonth();
        final long endMonth = end.getProlepticMonth() * (DAYS_IN_MONTH + 1) + end.getDayOfMonth();
        return (endMonth - startMonth) / (DAYS_IN_MONTH + 1);
    }

    /**
     * Get the number of years from this date to the given day.
     *
     * @param end The end date.
     * @return The number of years from this date to the given day.
     */
    private long yearsUntil(final PaxDate end) {
        // TODO: Correct this to deal with moving from/into leap months (!)
        // Multiplying by the (maximum) months-in-year (+1) makes the propleptic count a "place" (ie, the 10's place).
        // This means that if the starting date is before, it moves the count to the prior unit (24 - 8 = 16, and we only care about the 10's place).
        final long startYear = getYear() * (MONTHS_IN_YEAR + 1 + 1) + getMonth();
        final long endYear = end.getYear() * (MONTHS_IN_YEAR + 1 + 1) + end.getMonth();
        return (endYear - startYear) / (MONTHS_IN_YEAR + 1 + 1);
    }

    @Override
    int getProlepticYear() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    AbstractDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        // TODO Auto-generated method stub
        return null;
    }

}
