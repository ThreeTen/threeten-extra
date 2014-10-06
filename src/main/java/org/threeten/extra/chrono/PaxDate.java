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

import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.YEAR;
import static org.threeten.extra.chrono.PaxChronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.PaxChronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.PaxChronology.DAYS_IN_YEAR;
import static org.threeten.extra.chrono.PaxChronology.MONTHS_IN_YEAR;
import static org.threeten.extra.chrono.PaxChronology.WEEKS_IN_LEAP_MONTH;
import static org.threeten.extra.chrono.PaxChronology.WEEKS_IN_MONTH;
import static org.threeten.extra.chrono.PaxChronology.WEEKS_IN_YEAR;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
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
    private static final int DAYS_PAX_0001_TO_ISO_1970 = 719163;

    /**
     * Count of days in a long cycle (400 years).
     */
    private static final long DAYS_PER_LONG_CYCLE = 400 * DAYS_IN_YEAR + getLeapYearsBefore(400 + 1) * DAYS_IN_WEEK;

    /**
     * Count of days in a cycle (100 years).
     */
    private static final long DAYS_PER_CYCLE = 100 * DAYS_IN_YEAR + getLeapYearsBefore(100 + 1) * DAYS_IN_WEEK;

    /**
     * Count of days in the six-year cycle.
     */
    private static final long DAYS_PER_SIX_CYCLE = 6 * DAYS_IN_YEAR + getLeapYearsBefore(6 + 1) * DAYS_IN_WEEK;

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
        if (temporal instanceof PaxDate) {
            return (PaxDate) temporal;
        }
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

    /**
     * Obtains a {@code PaxDate} representing a date in the Pax calendar system from the epoch-day.
     *
     * @param epochDay the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in Pax calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static PaxDate ofEpochDay(final long epochDay) {
        EPOCH_DAY.range().checkValidValue(epochDay, EPOCH_DAY);
        // use of Pax 0001 makes non-leap century at end of (long) cycle.
        long paxEpochDay = epochDay + DAYS_PAX_0001_TO_ISO_1970;
        long longCycle = Math.floorDiv(paxEpochDay, DAYS_PER_LONG_CYCLE);
        long cycle = (paxEpochDay - longCycle * DAYS_PER_LONG_CYCLE) / DAYS_PER_CYCLE;
        long dayOfCycle = Math.floorMod(paxEpochDay - longCycle * DAYS_PER_LONG_CYCLE, DAYS_PER_CYCLE);
        if (dayOfCycle >= DAYS_PER_CYCLE - DAYS_IN_YEAR - DAYS_IN_WEEK) {
            // Is in the century year
            int dayOfYear = Math.toIntExact(dayOfCycle - (DAYS_PER_CYCLE - DAYS_IN_YEAR - DAYS_IN_WEEK) + 1);
            return ofYearDay(Math.toIntExact(longCycle * 400 + cycle * 100) + 100, dayOfYear);
        }

        // For negative years, the cycle of leap years runs the other direction for 99s and 6s.
        if (paxEpochDay >= 0) {
            if (dayOfCycle >= DAYS_PER_CYCLE - 2 * DAYS_IN_YEAR - 2 * DAYS_IN_WEEK) {
                // Is in the '99 year
                int dayOfYear = Math.toIntExact(dayOfCycle - 2 * DAYS_IN_YEAR - 2 * DAYS_IN_WEEK + 1);
                return ofYearDay(Math.toIntExact(longCycle * 400 + cycle * 100) + 99, dayOfYear);
            }
            // Otherwise, part of the regular 6-year cycle.
            long sixCycle = dayOfCycle / DAYS_PER_SIX_CYCLE;
            long dayOfSixCycle = dayOfCycle % DAYS_PER_SIX_CYCLE;
            int year = (int) (dayOfSixCycle / DAYS_IN_YEAR) + 1;
            int dayOfYear = (int) (dayOfSixCycle % DAYS_IN_YEAR) + 1;
            if (year == 7) {
                year--;
                dayOfYear += DAYS_IN_YEAR;
            }
            return ofYearDay(Math.toIntExact(longCycle * 400 + cycle * 100 + sixCycle * 6) + year, dayOfYear);
        } else {
            if (dayOfCycle < DAYS_IN_YEAR + DAYS_IN_WEEK) {
                // -'99 year is at _start_ of cycle (first year encountered).
                return ofYearDay(Math.toIntExact(longCycle * 400 + cycle * 100) + (100 - 99), Math.toIntExact(dayOfCycle) + 1);
            }
            // Otherwise, part of the regular 6-year cycle, but offset to -'101 first.
            long offsetCycle = dayOfCycle + 2 * DAYS_IN_YEAR;
            long sixCycle = offsetCycle / DAYS_PER_SIX_CYCLE;
            long dayOfSixCycle = offsetCycle % DAYS_PER_SIX_CYCLE;
            int year = (int) (sixCycle / DAYS_IN_YEAR) + 1;
            int dayOfYear = (int) (dayOfSixCycle % DAYS_IN_YEAR) + 1;
            if (year == 7) {
                year--;
                dayOfYear += DAYS_IN_YEAR;
            }
            return ofYearDay(Math.toIntExact(longCycle * 400 + cycle * 100 + (100 - (99 + 2 - (sixCycle * 6 + year)))), dayOfYear);

        }
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
    static PaxDate ofYearDay(final int prolepticYear, final int dayOfYear) {
        YEAR.checkValidValue(prolepticYear);
        if (dayOfYear < 1 || dayOfYear > DAYS_IN_YEAR + DAYS_IN_WEEK) {
            throw new DateTimeException("Inavlid date 'DayOfYear " + dayOfYear + "'");
        }
        final boolean leap = PaxChronology.INSTANCE.isLeapYear(prolepticYear);
        if (dayOfYear > DAYS_IN_YEAR && !leap) {
            throw new DateTimeException("Invalid date 'DayOfYear " + dayOfYear + "' as '" + prolepticYear + "' is not a leap year");
        }

        int month = ((dayOfYear - 1) / DAYS_IN_MONTH) + 1;

        // In leap years, the leap-month is shorter than the following month, so needs to be adjusted.
        if (leap && month == MONTHS_IN_YEAR && dayOfYear >= (DAYS_IN_YEAR + DAYS_IN_WEEK) - DAYS_IN_MONTH + 1) {
            month++;
        }

        // Subtract days-at-start-of-month from days in year
        int dayOfMonth = dayOfYear - (month - 1) * DAYS_IN_MONTH;

        // Adjust for shorter inserted leap-month.
        if (month == MONTHS_IN_YEAR + 1) {
            dayOfMonth += (DAYS_IN_MONTH - DAYS_IN_WEEK);
        }

        return of(prolepticYear, month, dayOfMonth);
    }

    /**
     * Get the count of leap months since proleptic month 0.
     * <p>
     * This number is negative if the month is prior to Pax year 0.
     ** <p>
     * Remember that if using this for things like turning months into days, you must first subtract this number from the proleptic month count.
     *
     * @param prolepticMonth The month.
     * @return The number of leap months since proleptic month 0.
     */
    private static long getLeapMonthsBefore(final long prolepticMonth) {
        final long offsetMonth = prolepticMonth - (prolepticMonth <= 0 ? 13 : 13 - 1);
        // First, see getLeapYearsBefore(...) for explanations.
        // return 18 * Math.floorDiv(offsetMonth, 100 * MONTHS_IN_YEAR + (getLeapYearsBefore(100) + 1))
        // - Math.floorDiv(offsetMonth, 400 * MONTHS_IN_YEAR + (getLeapYearsBefore(400) + 1))
        // + (((Math.floorMod(offsetMonth, 100 * MONTHS_IN_YEAR + (getLeapYearsBefore(100) + 1)) - (offsetMonth <= 0 ? 100 * MONTHS_IN_YEAR + getLeapYearsBefore(100) : 0))
        // / (99 * MONTHS_IN_YEAR + (getLeapYearsBefore(99) + 1))) + (offsetMonth <= 0 ? 1 : 0))
        // + (Math.floorMod(offsetMonth, 100 * MONTHS_IN_YEAR + (getLeapYearsBefore(100) + 1)) + (offsetMonth <= 0 ? 2 * MONTHS_IN_YEAR - 1 : 0)) / (6 * MONTHS_IN_YEAR + 1);
        return 18 * Math.floorDiv(offsetMonth, 1318)
                - Math.floorDiv(offsetMonth, 5272)
                + (((Math.floorMod(offsetMonth, 1318) - (offsetMonth <= 0 ? 1317 : 0)) / 1304) + (offsetMonth <= 0 ? 1 : 0))
                + (Math.floorMod(offsetMonth, 1318) + (offsetMonth <= 0 ? 25 : 0)) / 79;
    }

    /**
     * Get the count of leap years since Pax year 0.
     * <p>
     * This number is negative if the year is prior to Pax year 0.
     *
     * @param prolepticYear The year.
     * @return The number of leap years since Pax year 0.
     */
    private static long getLeapYearsBefore(final long prolepticYear) {
        // Some explanations are in order:
        // - First, because this calculates "leap years from 0 to the start of the year",
        // The 'current' year must be counted when the year is negative (hence Math.floorDiv(...)).
        // - However, this means that there's a negative count which must be offset for the in-century years,
        // which still occur at the "last two digits divisible by 6" (or 99) point.
        // - Math.floorMod(...) returns a nicely positive result for negative years, counting 'down', but
        // thus needs to be offset to make sure the first leap year is -6, and not -4...
        // The second line, which calculates the '99 occurrences, runs "backwards", so must first be reversed, then the results flipped.
        return 18 * Math.floorDiv(prolepticYear - 1, 100) - Math.floorDiv(prolepticYear - 1, 400) +
                (Math.floorMod(prolepticYear - 1, 100) - (prolepticYear <= 0 ? 99 : 0)) / 99 + (prolepticYear <= 0 ? 1 : 0) +
                ((Math.floorMod(prolepticYear - 1, 100) + (prolepticYear <= 0 ? 2 : 0)) / 6);
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
    int getDayOfMonth() {
        return day;
    }

    /**
     * Get the day of the year.
     *
     * @return The day of the year, from 1 to 371.
     */
    @Override
    int getDayOfYear() {
        return (getMonth() - 1) * DAYS_IN_MONTH
                - (getMonth() == MONTHS_IN_YEAR + 1 ? DAYS_IN_MONTH - DAYS_IN_WEEK : 0) + getDayOfMonth();
    }

    /**
     * Gets the month-of-year field from 1 to 14.
     * <p>
     * This method returns the month as an {@code int} from 1 to 14.
     *
     * @return the month-of-year, from 1 to 14
     */
    @Override
    int getMonth() {
        return month;
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
    PaxDate plusMonths(final long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        final long calcMonths = Math.addExact(getProlepticMonth(), monthsToAdd);
        // "Regularize" the month count, as if years were all 13 months long.
        final long monthsRegularized = calcMonths - getLeapMonthsBefore(calcMonths);
        final int newYear = YEAR.checkValidIntValue(Math.floorDiv(monthsRegularized, MONTHS_IN_YEAR));
        final int newMonth = Math.toIntExact(calcMonths - (newYear * MONTHS_IN_YEAR + getLeapYearsBefore(newYear)) + 1);
        return resolvePreviousValid(newYear, newMonth, getDayOfMonth());
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
    PaxDate plusYears(final long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        final int newYear = YEAR.checkValidIntValue(getProlepticYear() + yearsToAdd);
        // Retain actual month (not index) in the case where a leap month is to be inserted.
        if (getMonth() == MONTHS_IN_YEAR && !isLeapYear() && PaxChronology.INSTANCE.isLeapYear(newYear)) {
            return of(newYear, MONTHS_IN_YEAR + 1, getDayOfMonth());
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
        if (field == ChronoField.ALIGNED_WEEK_OF_YEAR) {
            return ValueRange.of(1, WEEKS_IN_YEAR + (isLeapYear() ? 1 : 0));
        } else if (field == ChronoField.MONTH_OF_YEAR) {
            return ValueRange.of(1, MONTHS_IN_YEAR + (isLeapYear() ? 1 : 0));
        }
        return super.range(field);
    }

    @Override
    public long toEpochDay() {
        final long days = ((long) getProlepticYear() - 1) * DAYS_IN_YEAR + getLeapYearsBefore(getProlepticYear()) * DAYS_IN_WEEK + getDayOfYear() - 1;
        // Rebase to ISO 1970.
        return days - DAYS_PAX_0001_TO_ISO_1970;
    }

    @Override
    public ChronoPeriod until(final ChronoLocalDate endDate) {
        final PaxDate end = PaxDate.from(endDate);
        final long years = yearsUntil(end);
        // Get to the same "whole" year.
        final PaxDate sameYearEnd = end.plusYears(years);
        final int months = (int) monthsUntil(sameYearEnd);
        final int days = (int) daysUntil(sameYearEnd.plusMonths(months));
        return getChronology().period(Math.toIntExact(years), months, days);
    }

    @Override
    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        final PaxDate end = PaxDate.from(endExclusive);
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case YEARS:
                    return yearsUntil(end);
                case DECADES:
                    return yearsUntil(end) / YEARS_IN_DECADE;
                case CENTURIES:
                    return yearsUntil(end) / YEARS_IN_CENTURY;
                case MILLENNIA:
                    return yearsUntil(end) / YEARS_IN_MILLENNIUM;
                default:
                    break;
            }
        }
        return super.until(end, unit);
    }

    @Override
    public PaxDate with(TemporalAdjuster adjuster) {
        return (PaxDate) adjuster.adjustInto(this);
    }

    @Override
    public PaxDate with(final TemporalField field, final long newValue) {
        // Evaluate years as a special case, to deal with inserted leap months.
        if (field == ChronoField.YEAR) {
            return plusYears(newValue - getProlepticYear());
        }
        return (PaxDate) super.with(field, newValue);
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
     * Get the proleptic month from the start of the epoch (Pax 0000).
     *
     * @return The proleptic month.
     */
    @Override
    long getProlepticMonth() {
        return ((long) getProlepticYear()) * MONTHS_IN_YEAR + getLeapYearsBefore(getProlepticYear()) + getMonth() - 1;
    }

    /**
     * Get the number of years from this date to the given day.
     *
     * @param end The end date.
     * @return The number of years from this date to the given day.
     */
    long yearsUntil(final PaxDate end) {
        // // If either date is after the inserted leap month, and the other year isn't leap, remove the effect of the inserted month.
        final long startYear = getProlepticYear() * 512L + getDayOfYear() - (getMonth() == MONTHS_IN_YEAR + 1 && JulianChronology.INSTANCE.isLeapYear(end.getProlepticYear()) ? DAYS_IN_WEEK : 0);
        final long endYear = end.getProlepticYear() * 512L + end.getDayOfYear() - (end.getMonth() == MONTHS_IN_YEAR + 1 && JulianChronology.INSTANCE.isLeapYear(getProlepticYear()) ? DAYS_IN_WEEK : 0);
        return (endYear - startYear) / 512L;
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
    @Override
    int getProlepticYear() {
        return year;
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        return ValueRange.of(1, getMonth() == MONTHS_IN_YEAR && isLeapYear() ? WEEKS_IN_LEAP_MONTH : WEEKS_IN_MONTH);
    }

    @Override
    PaxDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        return resolvePreviousValid(newYear, newMonth, dayOfMonth);
    }

    @Override
    int lengthOfYearInMonths() {
        return MONTHS_IN_YEAR + (isLeapYear() ? 1 : 0);
    }
}
