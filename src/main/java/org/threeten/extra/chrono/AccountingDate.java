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

import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.threeten.extra.chrono.AccountingChronology.DAY_OF_YEAR_RANGE;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

/**
 * A date in an Accounting calendar system.
 * <p>
 * This date operates using a given {@linkplain AccountingChronology Accounting calendar}.
 * An Accounting calendar differs greatly from the ISO calendar.
 * The start of the Accounting calendar will vary against the ISO calendar.
 * Depending on options chosen, it can start as early as {@code 0000-12-26 (ISO)} or as late as {@code 0001-01-04 (ISO)}.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class AccountingDate extends AbstractDate implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -126140328940081914L;
    /**
     * Number of days in a week.
     */
    private static final int DAYS_IN_WEEK = 7;
    /** 
     * Number of weeks in a regular (non-leap) year.
     */
    private static final long WEEKS_IN_YEAR = 52;

    /**
     * The chronology for manipulating this date.
     */
    private final AccountingChronology chronology;
    /**
     * The proleptic year.
     */
    private final int prolepticYear;
    /**
     * The month (period).
     */
    private final short month;
    /**
     * The day.
     */
    private final short day;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code AccountingDate} from the system clock in the default time-zone,
     * translated with the given AccountingChronology.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     * 
     * @param chronology  the accounting chronology to use, not null
     * @return the current date using the system clock and default time-zone, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static AccountingDate now(AccountingChronology chronology) {
        return now(chronology, Clock.systemDefaultZone());
    }

    /**
     * Obtains the current {@code AccountingDate} from the system clock in the specified time-zone,
     * translated with the given AccountingChronology.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     * 
     * @param chronology  the accounting chronology to use, not null
     * @param zone  the zone ID to use, not null
     * @return the current date using the system clock, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static AccountingDate now(AccountingChronology chronology, ZoneId zone) {
        return now(chronology, Clock.system(zone));
    }

    /**
     * Obtains the current {@code AccountingDate} from the specified clock,
     * translated with the given AccountingChronology.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param chronology  the accounting chronology to use, not null
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static AccountingDate now(AccountingChronology chronology, Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return ofEpochDay(chronology, now.toEpochDay());
    }

    /**
     * Obtains a {@code AccountingDate} representing a date in the given accounting calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code AccountingDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * @param chronology  the Accounting chronology to use, not null
     * @param prolepticYear  the Accounting proleptic-year
     * @param month  the Accounting month-of-year, from 1 to 12 or 1 to 13
     * @param dayOfMonth  the Accounting day-of-month, from 1 to 35 or 1 to 42
     * @return the date in the given Accounting calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  if the day-of-month is invalid for the month-year,
     *  or if an AccountingChronology was not provided
     */
    public static AccountingDate of(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        return create(chronology, prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains an {@code AccountingDate} from a temporal object.
     * <p>
     * This obtains a date in the specified Accounting calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code AccountingDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code AccountingDate::from}.
     * TODO: Figure out how this implementation effects the above.
     *
     * @param chronology  the accounting chronology to use, not null
     * @param temporal  the temporal object to convert, not null
     * @return the date in Accounting calendar system, not null
     * @throws DateTimeException if unable to convert to an {@code AccountingDate}
     */
    public static AccountingDate from(AccountingChronology chronology, TemporalAccessor temporal) {
        if (temporal instanceof AccountingDate && ((AccountingDate) temporal).getChronology().equals(chronology)) {
            return (AccountingDate) temporal;
        }
        return ofEpochDay(chronology, temporal.getLong(EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code AccountingDate} representing a date in the given Accounting calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns an {@code AccountingDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param chronology  the chronology to use to evaluate the year and day-of-year
     * @param prolepticYear  the Accounting proleptic-year
     * @param dayOfYear  the Accounting day-of-year, from 1 to 371
     * @return the date in Accounting calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  if the day-of-year is invalid for the year,
     *  or if the chronology was not provided.
     */
    static AccountingDate ofYearDay(AccountingChronology chronology, int prolepticYear, int dayOfYear) {
        if (chronology == null) {
            throw new DateTimeException("A previously setup chronology is required.");
        }
        YEAR.checkValidValue(prolepticYear);
        DAY_OF_YEAR_RANGE.checkValidValue(dayOfYear, DAY_OF_YEAR);
        boolean leap = chronology.isLeapYear(prolepticYear);
        if (dayOfYear > WEEKS_IN_YEAR * DAYS_IN_WEEK && !leap) {
            throw new DateTimeException("Invalid date 'DayOfYear " + dayOfYear + "' as '" + prolepticYear + "' is not a leap year");
        }

        // If the day-of-year would be after the inserted leap-week (in the next month), remove its influence
        if (leap && dayOfYear > (chronology.division.getWeeksAtStartOfMonth(chronology.leapWeekInPeriod) + chronology.division.getWeeksInMonth(chronology.leapWeekInPeriod) + 1) * DAYS_IN_WEEK) {
            dayOfYear -= DAYS_IN_WEEK;
        }

        int month = chronology.division.getMonthFromElapsedWeeks((dayOfYear - 1) / DAYS_IN_WEEK);
        int dayOfMonth = dayOfYear - (chronology.division.getWeeksAtStartOfMonth(month) * DAYS_IN_WEEK);

        // If the day-of-year is in the leap week, the month/day-of-month will be shifted.
        if (leap && month == chronology.leapWeekInPeriod + 1 && dayOfMonth <= DAYS_IN_WEEK) {
            month--;
            dayOfMonth += chronology.division.getWeeksInMonth(month) * DAYS_IN_WEEK;
        }

        return new AccountingDate(chronology, prolepticYear, month, dayOfMonth);
    }

    static AccountingDate ofEpochDay(AccountingChronology chronology, long epochDay) {
        // TODO Auto-generated method stub
        return null;
    }

    private static AccountingDate resolvePreviousValid(AccountingChronology chronology, int prolepticYear, int month, int day) {
        day = Math.min(day, lengthOfMonth(chronology, prolepticYear, month));
        return new AccountingDate(chronology, prolepticYear, month, day);
    }

    private static int lengthOfMonth(AccountingChronology chronology, int prolepticYear, int month) {
        return (chronology.division.getWeeksInMonth(month) +
                (month == chronology.leapWeekInPeriod && chronology.isLeapYear(prolepticYear) ? 1 : 0)) * DAYS_IN_WEEK;
    }

    /**
     * Creates an {@code AccountingDate} validating the input.
     *
     * @param chronology The Accounting chronology to base this date on
     * @param prolepticYear  the Accounting proleptic-year
     * @param dayOfYear  the Accounting day-of-year, from 1 to 371
     * @return the date in Accounting calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  if the day-of-year is invalid for the month-year,
     *  or if a chronology was not provided.
     */
    static AccountingDate create(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        if (chronology == null) {
            throw new DateTimeException("A previously setup chronology is required.");
        }
        YEAR.checkValidValue(prolepticYear);
        chronology.range(MONTH_OF_YEAR).checkValidValue(month, MONTH_OF_YEAR);

        if (dayOfMonth > lengthOfMonth(chronology, prolepticYear, month)) {
            if (month == chronology.leapWeekInPeriod && dayOfMonth < (chronology.division.getWeeksInMonth(month) + 1) * DAYS_IN_WEEK
                    && !chronology.isLeapYear(prolepticYear)) {
                throw new DateTimeException("Invalid date '" + month + "/" + dayOfMonth + "' as '" + prolepticYear + "' is not a leap year");
            } else {
                throw new DateTimeException("Invalid date '" + month + "/" + dayOfMonth + "'");
            }
        }

        return new AccountingDate(chronology, prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param chronology   The Accounting chronology to base the date on
     * @param prolepticYear  the Accounting proleptic-year
     * @param month  the Accounting month (period), from 1 to 12 or 1 to 13
     * @param dayOfMonth  the Accounting day-of-month, from 1 to 35 or 1 to 42
     */
    private AccountingDate(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        this.chronology = chronology;
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
        return AccountingDate.create(chronology, prolepticYear, month, day);
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
        return (chronology.division.getWeeksAtStartOfMonth(month) + (month > chronology.leapWeekInPeriod && isLeapYear() ? 1 : 0)) * DAYS_IN_WEEK + day;
    }

    @Override
    AbstractDate withDayOfYear(int value) {
        return plusDays(value - getDayOfYear());
    }

    @Override
    int lengthOfYearInMonths() {
        return chronology.division.lengthOfYearInMonths();
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        return ValueRange.of(1, (lengthOfMonth() - 1) / DAYS_IN_WEEK + 1);
    }

    @Override
    AccountingDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        return resolvePreviousValid(chronology, newYear, newMonth, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is an Accounting calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the Accounting chronology, not null
     */
    @Override
    public Chronology getChronology() {
        return chronology;
    }

    @Override
    public int lengthOfMonth() {
        return lengthOfMonth(chronology, prolepticYear, month);
    }

    //-------------------------------------------------------------------------
    @Override
    public AccountingDate with(TemporalAdjuster adjuster) {
        return (AccountingDate) adjuster.adjustInto(this);
    }

    @Override
    public AccountingDate with(TemporalField field, long newValue) {
        return (AccountingDate) super.with(field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public AccountingDate plus(TemporalAmount amount) {
        return (AccountingDate) amount.addTo(this);
    }

    @Override
    public AccountingDate plus(long amountToAdd, TemporalUnit unit) {
        return (AccountingDate) super.plus(amountToAdd, unit);
    }

    @Override
    public AccountingDate minus(TemporalAmount amount) {
        return (AccountingDate) amount.subtractFrom(this);
    }

    @Override
    public AccountingDate minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override  // for covariant return type
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<AccountingDate> atTime(LocalTime localTime) {
        return (ChronoLocalDateTime<AccountingDate>) ChronoLocalDate.super.atTime(localTime);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return super.until(AccountingDate.from(chronology, endExclusive), unit);
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        return super.until(AccountingDate.from(chronology, endDateExclusive));
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long year = prolepticYear;
        long accountingEpochDay = ((year - 1) * WEEKS_IN_YEAR + chronology.previousLeapYears(year)) * DAYS_IN_WEEK + (getDayOfYear() - 1);
        return accountingEpochDay - chronology.ACCOUNTING_0001_TO_ISO_1970;
    }
}
