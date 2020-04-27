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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

/**
 * A date in the Pax calendar system.
 * <p>
 * This date operates using the {@linkplain PaxChronology Pax calendar}.
 * This calendar system is a proposed reform calendar system, and is not in common use.
 * The Pax differs from the Gregorian in terms of month count and length, and the leap year rule.
 * Dates are aligned such that {@code 0001-01-01 (Pax)} is {@code 0000-12-31 (ISO)}.
 * <p>
 * More information is available in the <a href="https://en.wikipedia.org/wiki/Pax_Calendar">Pax Calendar</a> Wikipedia article.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class PaxDate
        extends AbstractDate
        implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -2229133057743750072L;
    /**
     * The difference between the ISO and Pax epoch day count (Pax 0001-01-01 to ISO 1970-01-01).
     */
    private static final int PAX_0001_TO_ISO_1970 = 719163;
    /**
     * The days per 400 year cycle.
     */
    private static final int DAYS_PER_LONG_CYCLE = (DAYS_IN_YEAR * 400) + (DAYS_IN_WEEK * 71);
    /**
     * The days per 100 year cycle.
     */
    private static final int DAYS_PER_CYCLE = (DAYS_IN_YEAR * 100) + (DAYS_IN_WEEK * 18);
    /**
     * The days per 6 year cycle.
     */
    private static final int DAYS_PER_SIX_CYCLE = (DAYS_IN_YEAR * 6) + (DAYS_IN_WEEK * 1);
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

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code PaxDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static PaxDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current {@code PaxDate} from the system clock in the specified time-zone.
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
    public static PaxDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current {@code PaxDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static PaxDate now(Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return PaxDate.ofEpochDay(now.toEpochDay());
    }

    /**
     * Obtains a {@code PaxDate} representing a date in the Pax calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code PaxDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Pax proleptic-year
     * @param month  the Pax month-of-year, from 1 to 14
     * @param dayOfMonth  the Pax day-of-month, from 1 to 28
     * @return the date in Pax calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static PaxDate of(int prolepticYear, int month, int dayOfMonth) {
        YEAR.checkValidValue(prolepticYear);
        PaxChronology.MONTH_OF_YEAR_RANGE.checkValidValue(month, MONTH_OF_YEAR);
        PaxChronology.DAY_OF_MONTH_RANGE.checkValidValue(dayOfMonth, DAY_OF_MONTH);
        if (month == MONTHS_IN_YEAR + 1 && !PaxChronology.INSTANCE.isLeapYear(prolepticYear)) {
            throw new DateTimeException("Invalid month 14 as " + prolepticYear + "is not a leap year");
        }

        if (dayOfMonth > DAYS_IN_WEEK && month == MONTHS_IN_YEAR && PaxChronology.INSTANCE.isLeapYear(prolepticYear)) {
            throw new DateTimeException("Invalid date during Pax as " + prolepticYear + " is a leap year");
        }

        return new PaxDate(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a {@code PaxDate} from a temporal object.
     * <p>
     * This obtains a date in the Pax calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code PaxDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code PaxDate::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in the Pax calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code PaxDate}
     */
    public static PaxDate from(TemporalAccessor temporal) {
        if (temporal instanceof PaxDate) {
            return (PaxDate) temporal;
        }
        return PaxDate.ofEpochDay(temporal.getLong(EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code PaxDate} representing a date in the Pax calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code PaxDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Pax proleptic-year
     * @param dayOfYear  the Pax day-of-year, from 1 to 371
     * @return the date in Pax calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static PaxDate ofYearDay(int prolepticYear, int dayOfYear) {
        YEAR.checkValidValue(prolepticYear);
        PaxChronology.DAY_OF_YEAR_RANGE.checkValidValue(dayOfYear, DAY_OF_YEAR);
        boolean leap = PaxChronology.INSTANCE.isLeapYear(prolepticYear);
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
     * Obtains a {@code PaxDate} representing a date in the Pax calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in Pax calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static PaxDate ofEpochDay(long epochDay) {
        EPOCH_DAY.range().checkValidValue(epochDay, EPOCH_DAY);
        // use of Pax 0001 makes non-leap century at end of (long) cycle.
        long paxEpochDay = epochDay + PAX_0001_TO_ISO_1970;
        int longCycle = (int) Math.floorDiv(paxEpochDay, DAYS_PER_LONG_CYCLE);
        int cycle = (int) (paxEpochDay - longCycle * DAYS_PER_LONG_CYCLE) / DAYS_PER_CYCLE;
        int dayOfCycle = (int) Math.floorMod(paxEpochDay - longCycle * DAYS_PER_LONG_CYCLE, DAYS_PER_CYCLE);
        if (dayOfCycle >= DAYS_PER_CYCLE - DAYS_IN_YEAR - DAYS_IN_WEEK) {
            // Is in the century year
            int dayOfYear = dayOfCycle - (DAYS_PER_CYCLE - DAYS_IN_YEAR - DAYS_IN_WEEK) + 1;
            return ofYearDay(longCycle * (4 * YEARS_IN_CENTURY) + cycle * YEARS_IN_CENTURY + YEARS_IN_CENTURY, dayOfYear);
        }

        // For negative years, the cycle of leap years runs the other direction for 99s and 6s.
        if (paxEpochDay >= 0) {
            if (dayOfCycle >= DAYS_PER_CYCLE - 2 * DAYS_IN_YEAR - 2 * DAYS_IN_WEEK) {
                // Is in the '99 year
                int dayOfYear = dayOfCycle - (DAYS_PER_CYCLE - 2 * DAYS_IN_YEAR - 2 * DAYS_IN_WEEK) + 1;
                return ofYearDay(longCycle * (4 * YEARS_IN_CENTURY) + cycle * YEARS_IN_CENTURY + (YEARS_IN_CENTURY - 1), dayOfYear);
            }
            // Otherwise, part of the regular 6-year cycle.
            int sixCycle = dayOfCycle / DAYS_PER_SIX_CYCLE;
            int dayOfSixCycle = dayOfCycle % DAYS_PER_SIX_CYCLE;
            int year = dayOfSixCycle / DAYS_IN_YEAR + 1;
            int dayOfYear = dayOfSixCycle % DAYS_IN_YEAR + 1;
            if (year == 7) {
                year--;
                dayOfYear += DAYS_IN_YEAR;
            }
            return ofYearDay(longCycle * (4 * YEARS_IN_CENTURY) + cycle * YEARS_IN_CENTURY + sixCycle * 6 + year, dayOfYear);
        } else {
            if (dayOfCycle < DAYS_IN_YEAR + DAYS_IN_WEEK) {
                // -'99 year is at _start_ of cycle (first year encountered).
                return ofYearDay(longCycle * (4 * YEARS_IN_CENTURY) + cycle * YEARS_IN_CENTURY + 1, dayOfCycle + 1);
            }
            // Otherwise, part of the regular 6-year cycle, but offset -'96 to be end of six-year-cycle first.
            int offsetCycle = dayOfCycle + 2 * DAYS_IN_YEAR - DAYS_IN_WEEK;
            int sixCycle = offsetCycle / DAYS_PER_SIX_CYCLE;
            int dayOfSixCycle = offsetCycle % DAYS_PER_SIX_CYCLE;
            int year = dayOfSixCycle / DAYS_IN_YEAR + 1;
            int dayOfYear = dayOfSixCycle % DAYS_IN_YEAR + 1;
            if (year == 7) {
                year--;
                dayOfYear += DAYS_IN_YEAR;
            }
            return ofYearDay(longCycle * (4 * YEARS_IN_CENTURY) + cycle * YEARS_IN_CENTURY - 2 + (sixCycle * 6 + year), dayOfYear);
        }
    }

    private static PaxDate resolvePreviousValid(int prolepticYear, int month, int day) {
        int monthR = Math.min(month, MONTHS_IN_YEAR + (PaxChronology.INSTANCE.isLeapYear(prolepticYear) ? 1 : 0));
        int dayR = Math.min(day, month == MONTHS_IN_YEAR && PaxChronology.INSTANCE.isLeapYear(prolepticYear) ? DAYS_IN_WEEK : DAYS_IN_MONTH);
        return PaxDate.of(prolepticYear, monthR, dayR);
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
    private static long getLeapMonthsBefore(long prolepticMonth) {
        long offsetMonth = prolepticMonth - (prolepticMonth <= 0 ? 13 : 13 - 1);
        // First, see getLeapYearsBefore(...) for explanations.
        return 18L * Math.floorDiv(offsetMonth, 1318)
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
    private static long getLeapYearsBefore(long prolepticYear) {
        // Some explanations are in order:
        // - First, because this calculates "leap years from 0 to the start of the year",
        // The 'current' year must be counted when the year is negative (hence Math.floorDiv(...)).
        // - However, this means that there's a negative count which must be offset for the in-century years,
        // which still occur at the "last two digits divisible by 6" (or 99) point.
        // - Math.floorMod(...) returns a nicely positive result for negative years, counting 'down', but
        // thus needs to be offset to make sure the first leap year is -6, and not -4...
        // The second line, which calculates the '99 occurrences, runs "backwards", so must first be reversed, then the results flipped.
        return 18L * Math.floorDiv(prolepticYear - 1, YEARS_IN_CENTURY) - Math.floorDiv(prolepticYear - 1, 4 * YEARS_IN_CENTURY) +
                (Math.floorMod(prolepticYear - 1, 100) - (prolepticYear <= 0 ? 99 : 0)) / 99 + (prolepticYear <= 0 ? 1 : 0) +
                ((Math.floorMod(prolepticYear - 1, 100) + (prolepticYear <= 0 ? 2 : 0)) / 6);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the Pax proleptic-year
     * @param month  the Pax month, from 1 to 14
     * @param dayOfMonth  the Pax day-of-month, from 1 to 28
     */
    private PaxDate(int prolepticYear, int month, int dayOfMonth) {
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
        return PaxDate.of(prolepticYear, month, day);
    }

    //-----------------------------------------------------------------------
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
    int getDayOfYear() {
        return (month - 1) * DAYS_IN_MONTH
                - (month == MONTHS_IN_YEAR + 1 ? DAYS_IN_MONTH - DAYS_IN_WEEK : 0) + getDayOfMonth();
    }

    @Override
    int lengthOfYearInMonths() {
        return MONTHS_IN_YEAR + (isLeapYear() ? 1 : 0);
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        return ValueRange.of(1, month == MONTHS_IN_YEAR && isLeapYear() ? WEEKS_IN_LEAP_MONTH : WEEKS_IN_MONTH);
    }

    @Override
    PaxDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        return resolvePreviousValid(newYear, newMonth, dayOfMonth);
    }

    @Override
    public ValueRange range(TemporalField field) {
        if (field == ChronoField.ALIGNED_WEEK_OF_YEAR) {
            return ValueRange.of(1, WEEKS_IN_YEAR + (isLeapYear() ? 1 : 0));
        } else if (field == ChronoField.MONTH_OF_YEAR) {
            return ValueRange.of(1, MONTHS_IN_YEAR + (isLeapYear() ? 1 : 0));
        }
        return super.range(field);
    }

    /**
     * Get the proleptic month from the start of the epoch (Pax 0000).
     *
     * @return The proleptic month.
     */
    @Override
    long getProlepticMonth() {
        return ((long) getProlepticYear()) * MONTHS_IN_YEAR + getLeapYearsBefore(getProlepticYear()) + month - 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the Pax calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the Pax chronology, not null
     */
    @Override
    public PaxChronology getChronology() {
        return PaxChronology.INSTANCE;
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The Pax calendar system has two eras, 'CE' and 'BCE',
     * defined by {@link PaxEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public PaxEra getEra() {
        return (prolepticYear >= 1 ? PaxEra.CE : PaxEra.BCE);
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Month lengths do not match those of the ISO calendar system.
     *
     * @return the length of the month in days, from 7 to 28
     */
    @Override
    public int lengthOfMonth() {
        switch (month) {
            case 13:
                return (isLeapYear() ? DAYS_IN_WEEK : DAYS_IN_MONTH);
            default:
                return DAYS_IN_MONTH;
        }
    }

    @Override
    public int lengthOfYear() {
        return DAYS_IN_YEAR + (isLeapYear() ? DAYS_IN_WEEK : 0);
    }

    //-------------------------------------------------------------------------
    @Override
    public PaxDate with(TemporalAdjuster adjuster) {
        return (PaxDate) adjuster.adjustInto(this);
    }

    @Override
    public PaxDate with(TemporalField field, long newValue) {
        // Evaluate years as a special case, to deal with inserted leap months.
        if (field == ChronoField.YEAR) {
            return plusYears(Math.subtractExact(newValue, getProlepticYear()));
        }
        return (PaxDate) super.with(field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public PaxDate plus(TemporalAmount amount) {
        return (PaxDate) amount.addTo(this);
    }

    @Override
    public PaxDate plus(long amountToAdd, TemporalUnit unit) {
        return (PaxDate) super.plus(amountToAdd, unit);
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
     * In the Pax Calendar, the month of December is 13 in non-leap-years, and 14 in leap years.
     * Shifting the index of the month thus means the month would still be the same.
     * <p>
     * In the case of moving from the inserted leap-month (destination year is non-leap), the month index is retained.
     * This has the effect of retaining the same day-of-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearsToAdd  the years to add, may be negative
     * @return a {@code PaxDate} based on this date with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    PaxDate plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = YEAR.checkValidIntValue(getProlepticYear() + yearsToAdd);
        // Retain actual month (not index) in the case where a leap month is to be inserted.
        if (month == MONTHS_IN_YEAR && !isLeapYear() && PaxChronology.INSTANCE.isLeapYear(newYear)) {
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
     * For example, 2006-12-13 plus one month would result in the invalid date 2006-13-13.
     * Instead of returning an invalid result, the last valid day of the month, 2006-13-07, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthsToAdd  the months to add, may be negative
     * @return a {@code PaxDate} based on this date with the months added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    PaxDate plusMonths(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        long calcMonths = Math.addExact(getProlepticMonth(), monthsToAdd);
        // "Regularize" the month count, as if years were all 13 months long.
        long monthsRegularized = calcMonths - getLeapMonthsBefore(calcMonths);
        int newYear = YEAR.checkValidIntValue(Math.floorDiv(monthsRegularized, MONTHS_IN_YEAR));
        int newMonth = Math.toIntExact(calcMonths - ((long) newYear * MONTHS_IN_YEAR + getLeapYearsBefore(newYear)) + 1);
        return resolvePreviousValid(newYear, newMonth, getDayOfMonth());
    }

    @Override
    public PaxDate minus(TemporalAmount amount) {
        return (PaxDate) amount.subtractFrom(this);
    }

    @Override
    public PaxDate minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override  // for covariant return type
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<PaxDate> atTime(LocalTime localTime) {
        return (ChronoLocalDateTime<PaxDate>) super.atTime(localTime);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return until(PaxDate.from(endExclusive), unit);
    }

    @Override
    long until(AbstractDate end, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            PaxDate paxEnd = PaxDate.from(end);
            switch ((ChronoUnit) unit) {
                case YEARS:
                    return yearsUntil(paxEnd);
                case DECADES:
                    return yearsUntil(paxEnd) / YEARS_IN_DECADE;
                case CENTURIES:
                    return yearsUntil(paxEnd) / YEARS_IN_CENTURY;
                case MILLENNIA:
                    return yearsUntil(paxEnd) / YEARS_IN_MILLENNIUM;
                default:
                    break;
            }
        }
        return super.until(end, unit);
    }

    /**
     * Get the number of years from this date to the given day.
     *
     * @param end The end date.
     * @return The number of years from this date to the given day.
     */
    long yearsUntil(PaxDate end) {
        // If either date is after the inserted leap month, and the other year isn't leap, simulate the effect of the inserted month.
        long startYear = getProlepticYear() * 512L + getDayOfYear() + (this.month == MONTHS_IN_YEAR && !this.isLeapYear() && end.isLeapYear() ? DAYS_IN_WEEK : 0);
        long endYear = end.getProlepticYear() * 512L + end.getDayOfYear() + (end.month == MONTHS_IN_YEAR && !end.isLeapYear() && this.isLeapYear() ? DAYS_IN_WEEK : 0);
        return (endYear - startYear) / 512L;
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        PaxDate end = PaxDate.from(endDateExclusive);
        int years = Math.toIntExact(yearsUntil(end));
        // Get to the same "whole" year.
        PaxDate sameYearEnd = this.plusYears(years);
        int months = (int) sameYearEnd.monthsUntil(end);
        int days = (int) sameYearEnd.plusMonths(months).daysUntil(end);
        return getChronology().period(years, months, days);
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long paxEpochDay = ((long) getProlepticYear() - 1) * DAYS_IN_YEAR + getLeapYearsBefore(getProlepticYear()) * DAYS_IN_WEEK + getDayOfYear() - 1;
        return paxEpochDay - PAX_0001_TO_ISO_1970;
    }

}
