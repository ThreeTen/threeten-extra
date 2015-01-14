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
import java.time.DayOfWeek;
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
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

import java.util.Locale;

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
 * Dates are aligned such that {@code 0001-01-01 (International fixed)} is {@code 0001-01-01 (ISO)}.</p>
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
     * Serialization version UID.
     */
    private static final long serialVersionUID = -908631218899744973L;
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
     * The month of the year.
     * Leap-day is encoded as month -1, year-day is encoded as month 0.
     */
    private final int month;
    /**
     * The day of the month.
     * Both year-day and leap-day are encoded as day-of-month 0.
     */
    private final int day;

    /**
     * Dates for both leap day and year day may only be instantiated through {@link InternationalFixedDate#of(int,boolean,boolean)}.
     * It is therefore not possible to instantiated leap day as 2004-07-01 or year day as 2005-13-29.
     *
     * For calculation purposes, internally leap day is treated as day-of-year 169,
     * squeezed between end of month 6 and beginning of month 7.
     * Similarly, year day is treated as day-of-year 365 - or 366 in a leap year.
     */

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month, from 1 to 13
     * @param dayOfMonth    the International fixed day-of-month, from 1 to 28
     * @return the International fixed date
     */
    private InternationalFixedDate(final int prolepticYear, final int month, final int dayOfMonth) {
        this.prolepticYear = prolepticYear;
        this.month = month;
        this.day = dayOfMonth;
    }

    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param dayOfYear     the day of the year
     * @return the International fixed date
     */
    private InternationalFixedDate(final int prolepticYear, final int dayOfYear) {
        boolean isLeapYear = getChronology().isLeapYear(prolepticYear);
        boolean isYearDay = dayOfYear == InternationalFixedChronology.DAYS_IN_YEAR + (isLeapYear ? 1 : 0);
        boolean isLeapDay = isLeapYear && dayOfYear == 169;
        int doy = isLeapYear && dayOfYear > 169 ? dayOfYear - 1 : dayOfYear;
        this.prolepticYear = prolepticYear;
        this.month = isLeapDay ? -1 : isYearDay ? 0 : 1 + ((doy - 1)  / InternationalFixedChronology.DAYS_IN_MONTH);
        this.day = (isYearDay || isLeapDay) ? 0 : 1 + ((doy - 1) % InternationalFixedChronology.DAYS_IN_MONTH);
    }

    /**
     * Obtains the current {@code InternationalFixedDate} from the system clock in the default time-zone.
     * <p/>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p/>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static InternationalFixedDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current {@code InternationalFixedDate} from the system clock in the specified time-zone.
     * <p/>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p/>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     * @return the current date using the system clock, not null
     */
    public static InternationalFixedDate now(final ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current {@code InternationalFixedDate} from the specified clock.
     * <p/>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static InternationalFixedDate now(final Clock clock) {
        LocalDate now = LocalDate.now(clock);

        return InternationalFixedDate.ofEpochDay(now.toEpochDay());
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p/>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month-of-year, from 1 to 13
     * @param dayOfMonth    the International fixed day-of-month, from 1 to 28
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range, or if the day-of-month is invalid for the month-year
     */
    public static InternationalFixedDate of(final int prolepticYear, final int month, final int dayOfMonth) {
        return create(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, for the month-less days of leap-day, which follows the last day in June and precedes Sol 1.
     * <p/>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    public static InternationalFixedDate leapDay(final int prolepticYear) {
        return createLeapDay(prolepticYear);
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, for the month-less days of year-day, which follows the last day in December.
     * <p/>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    public static InternationalFixedDate yearDay(final int prolepticYear) {
        return createYearDay(prolepticYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code InternationalFixedDate} from a temporal object.
     * <p/>
     * This obtains a date in the International fixed calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code InternationalFixedDate}.
     * <p/>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p/>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code InternationalFixedDate::from}.
     *
     * @param temporal the temporal object to convert, not null
     * @return the date in the International fixed calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code InternationalFixedDate}
     */
    public static InternationalFixedDate from(final TemporalAccessor temporal) {
        if (temporal instanceof InternationalFixedDate) {
            return (InternationalFixedDate) temporal;
        }

        return InternationalFixedDate.ofEpochDay(temporal.getLong(ChronoField.EPOCH_DAY));
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year and day-of-year fields.
     * <p/>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param dayOfYear     the International fixed day-of-year, from 1 to 366
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range, or if the day-of-year is invalid for the year
     */
    static InternationalFixedDate ofYearDay(final int prolepticYear, final int dayOfYear) {
        InternationalFixedChronology.YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
        InternationalFixedChronology.DAY_OF_YEAR_RANGE.checkValidValue(dayOfYear, ChronoField.DAY_OF_YEAR);

        return new InternationalFixedDate(prolepticYear, dayOfYear);
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the epoch-day.
     *
     * @param epochDay the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static InternationalFixedDate ofEpochDay(final long epochDay) {
        InternationalFixedChronology.EPOCH_DAY_RANGE.checkValidValue(epochDay, ChronoField.EPOCH_DAY);
        long zeroDay = epochDay + InternationalFixedChronology.DAYS_0000_TO_1970;

        if (zeroDay < 0) {
            throw new DateTimeException("Invalid epoch: " + epochDay);
        }

        // The two values work great for any dates, just not the first (xxxx-01-01 IFC) or the last of the year (xxxx-year-day).
        long year = (400 * zeroDay) / InternationalFixedChronology.DAYS_PER_CYCLE;
        long doy = zeroDay - (InternationalFixedChronology.DAYS_IN_YEAR * year + InternationalFixedChronology.getLeapYearsBefore(year));

        boolean isLeapYear = InternationalFixedChronology.INSTANCE.isLeapYear(year);

        // In some cases, N-01-01 (January 1st) results in (N-1)-year-day, i.e. -1 day off.
        if (doy == 366 && !isLeapYear) {
            year += 1;
            doy = 1;
        }

        // In some cases, N-year-day results in (N+1)-00-00 (rubbish), in a way +1 day off.
        if (doy == 0) {
            year -= 1;
            doy = 365 + (isLeapYear ? 1 : 0);
        }

        return ofYearDay((int) year, (int) doy);
    }

    private static InternationalFixedDate resolvePreviousValid(final int prolepticYear, final int month, final int day) {
        int monthR = Math.min(month, InternationalFixedChronology.MONTHS_IN_YEAR);
        int dayR = Math.min(day, InternationalFixedChronology.DAYS_IN_MONTH);

        return InternationalFixedDate.of(prolepticYear, monthR, dayR);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method, validates the given triplet year, month and dayOfMonth.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month, from 1 to 13
     * @param dayOfMonth    the International fixed day-of-month, from 1 to 28
     * @return the International fixed date
     * @throws DateTimeException if the date is invalid
     */
    static InternationalFixedDate create(final int prolepticYear, final int month, final int dayOfMonth) {
        InternationalFixedChronology.YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
        InternationalFixedChronology.MONTH_OF_YEAR_RANGE.checkValidValue(month, ChronoField.MONTH_OF_YEAR);
        InternationalFixedChronology.DAY_OF_MONTH_RANGE.checkValidValue(dayOfMonth, ChronoField.DAY_OF_MONTH);

        if (month < 1 || dayOfMonth < 1) {
            throw new DateTimeException("Invalid date: " + prolepticYear + '/' + month + '/' + dayOfMonth);
        }

        return new InternationalFixedDate(prolepticYear, month, dayOfMonth);
    }

    /**
     * Factory method, validates the given year, accepts only valid leap-years.
     * Leap-day is a month-less day between end of June and beginning of Sol.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the International fixed date
     * @throws DateTimeException if the date is invalid
     */
    static InternationalFixedDate createLeapDay(final int prolepticYear) {
        InternationalFixedChronology.YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);

        if (!InternationalFixedChronology.INSTANCE.isLeapYear(prolepticYear)) {
            throw new DateTimeException("Invalid leap day for year: " + prolepticYear);
        }

        return ofYearDay(prolepticYear, 169);
    }

    /**
     * Factory method, accepts any year, will be validated further down.
     * Year-day is a month-less day following the last day of December.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the International fixed date
     * @throws DateTimeException if the date is invalid
     */
    static InternationalFixedDate createYearDay(final int prolepticYear) {
        InternationalFixedChronology.YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);

        return ofYearDay(prolepticYear, 365 + (InternationalFixedChronology.INSTANCE.isLeapYear(prolepticYear) ? 1 : 0));
    }

    /**
     *
     * Validates the object.
     *
     * @return the resolved date, not null
     */
    private Object readResolve() {
        if (isYearDay() || isLeapDay()) {
            return new InternationalFixedDate(prolepticYear, getDayOfYear());
        }

        return InternationalFixedDate.of(prolepticYear, getMonth(), getDayOfMonth());
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    int getProlepticYear() {
        return prolepticYear;
    }

    public boolean isLeapDay() {
        return month == -1;
    }

    public boolean isYearDay() {
        return month == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getMonth() {
        return month;
    }

    /**
     * For calculation purposes, consider leap day to be associated with month Sol.
     * In the same spirit, associate year day with the last month of the year.
     * @return
     */
    private int getCalculatedMonth() {
        return isYearDay() ?
            InternationalFixedChronology.MONTHS_IN_YEAR : isLeapDay() ? 7 : getMonth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getDayOfMonth() {
        if (isYearDay() || isLeapDay()) {
            return 0;
        }

        int doy = getDayOfYearAdjusted() - 1;

        return 1 + (doy % InternationalFixedChronology.DAYS_IN_MONTH);
    }

    private int getCalculatedDayOfMonth() {
        return isYearDay() ? InternationalFixedChronology.DAYS_IN_MONTH + 1 : isLeapDay() ? 1 : getDayOfMonth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDayOfYear() {
        return isLeapDay() ? 169 : isYearDay() ? InternationalFixedChronology.DAYS_IN_YEAR + (isLeapYear() ? 1 : 0) :
                (month - 1) * InternationalFixedChronology.DAYS_IN_MONTH + day + (isLeapYear() && month > 6 ? 1 : 0);
    }

    private int getDayOfYearAdjusted() {
        int d = getDayOfYear();

        return isLeapYear() && month > 6 ? d - 1 : d;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int lengthOfYearInMonths() {
        return InternationalFixedChronology.MONTHS_IN_YEAR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        return month > 0 ? InternationalFixedChronology.WEEK_OF_MONTH_RANGE : InternationalFixedChronology.EMPTY_RANGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    InternationalFixedDate resolvePrevious(final int newYear, final int newMonth, final int dayOfMonth) {
        return resolvePreviousValid(newYear, newMonth, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the International fixed calendar system.
     * <p/>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the International fixed chronology, not null
     */
    @Override
    public InternationalFixedChronology getChronology() {
        return InternationalFixedChronology.INSTANCE;
    }

    /**
     * Gets the era applicable at this date.
     * <p/>
     * The International fixed calendar system only has one era, 'CE',
     * defined by {@link InternationalFixedEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public InternationalFixedEra getEra() {
        return InternationalFixedEra.CE;
    }

    /**
     * Returns the length of the month represented by this date.
     * <p/>
     * This returns the length of the month in days.
     * Month lengths do not match those of the ISO calendar system.
     *
     * Since leap-day / year-day are not part of any month, their 'imaginary' month is of length 1.
     *
     * @return the length of the month in days: 28
     */
    @Override
    public int lengthOfMonth() {
        return month > 0 ? InternationalFixedChronology.DAYS_IN_MONTH : 1;
    }

    /**
     * Returns the length of the year represented by this date.
     * <p/>
     * This returns the length of the year in days.
     * Year lengths match those of the ISO calendar system.
     *
     * @return the length of the year in days: 365 or 366
     */
    @Override
    public int lengthOfYear() {
        return InternationalFixedChronology.DAYS_IN_YEAR + (isLeapYear() ? 1 : 0);
    }

    @Override
    public InternationalFixedDate plus(final long amountToAdd, final TemporalUnit unit) {
        if (amountToAdd == 0) {
            return this;
        }

        return (InternationalFixedDate) super.plus(amountToAdd, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    InternationalFixedDate plusYears(final long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }

        int newYear = (int) Math.addExact(getProlepticYear(), yearsToAdd);

        if (isYearDay()) {
            return createYearDay(newYear);
        }

        int day = getDayOfMonth();
        int newMonth = getCalculatedMonth();

        if (isLeapDay()) {
            if ((yearsToAdd & 3) == 0) {
                return createLeapDay(newYear);
            }

            day = 1;
        }

        return create(newYear, newMonth, day);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternationalFixedDate plusMonths(final long months) {
        if (months == 0) {
            return this;
        }

        int day = isYearDay() ? InternationalFixedChronology.DAYS_IN_MONTH : getCalculatedDayOfMonth();
        int newMonth = (int) Math.addExact(getCalculatedProlepticMonth(), months);
        int year = newMonth / InternationalFixedChronology.MONTHS_IN_YEAR;
        newMonth = 1 + (newMonth % InternationalFixedChronology.MONTHS_IN_YEAR);

        return create(year, newMonth, day);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternationalFixedDate plusDays(final long daysToAdd) {
        if (daysToAdd == 0) {
            return this;
        }

        return ofEpochDay(toEpochDay() + daysToAdd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueRange range(final TemporalField field) {
        boolean special = isYearDay() || isLeapDay();

        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                ChronoField f = (ChronoField) field;

                switch (f) {
                    case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                    case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    case DAY_OF_WEEK:
                        return special ? InternationalFixedChronology.EMPTY_RANGE : InternationalFixedChronology.DAY_OF_WEEK_RANGE;
                    case ALIGNED_WEEK_OF_MONTH:
                        return special ? InternationalFixedChronology.EMPTY_RANGE : InternationalFixedChronology.WEEK_OF_MONTH_RANGE;
                    case ALIGNED_WEEK_OF_YEAR:
                        return special ? InternationalFixedChronology.EMPTY_RANGE : InternationalFixedChronology.WEEK_OF_YEAR_RANGE;
                    case DAY_OF_MONTH:
                        return special ? InternationalFixedChronology.EMPTY_RANGE : InternationalFixedChronology.DAY_OF_MONTH_RANGE;
                    case DAY_OF_YEAR:
                        return special ?
                                    ValueRange.of(getDayOfYear(), getDayOfYear()) :
                                    isLeapYear() ?
                                            InternationalFixedChronology.DAY_OF_YEAR_LEAP_RANGE :
                                            InternationalFixedChronology.DAY_OF_YEAR_NORMAL_RANGE;
                    case EPOCH_DAY:
                        return InternationalFixedChronology.EPOCH_DAY_RANGE;
                    case ERA:
                        return InternationalFixedChronology.ERA_RANGE;
                    case MONTH_OF_YEAR:
                        return special ? InternationalFixedChronology.EMPTY_RANGE : InternationalFixedChronology.MONTH_OF_YEAR_RANGE;
                    default:
                        break;
                }
            }
        }

        return super.range(field);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public int getAlignedDayOfWeekInMonth() {
        if (isYearDay() || isLeapDay()) {
            return 0;
        }

        return ((getDayOfMonth() - 1) % lengthOfWeek()) + 1;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    int getAlignedDayOfWeekInYear() {
        if (isYearDay() || isLeapDay()) {
            return 0;
        }

        return ((getDayOfYearAdjusted() - 1) % lengthOfWeek()) + 1;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    int getAlignedWeekOfMonth() {
        if (isYearDay() || isLeapDay()) {
            return 0;
        }

        return ((getDayOfMonth() - 1) / lengthOfWeek()) + 1;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    int getAlignedWeekOfYear() {
        if (isYearDay() || isLeapDay()) {
            return 0;
        }

        return ((getDayOfYear() - 1) / lengthOfWeek()) + 1;
    }

    /**
     * Returns the day of the week represented by this date.
     * <p/>
     * This returns the ordinal of the enum {@link DayOfWeek}.
     * Day-of-week do not match those of the ISO calendar system.
     * In particular, each months starts with Sunday.
     * Leap-day and year-day are not considered week-days, thus return 0.
     *
     * @return the day of the week enumeration: between 1 (Monday) and 7 (Sunday), or 0 (leap-day, year-day)
     */
    @Override
    public int getDayOfWeek() {
        if (isLeapDay() || isYearDay()) {
            return 0;
        }

        int doy = getDayOfYearAdjusted();

        return 1 + ((5 + doy) % 7);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    long getProlepticMonth() {
        return getProlepticYear() * lengthOfYearInMonths() + getMonth() - 1;
    }

    private long getCalculatedProlepticMonth() {
        return getProlepticYear() * lengthOfYearInMonths() + getCalculatedMonth() - 1;
    }

    //-------------------------------------------------------------------------
    @Override  // for covariant return type
    @SuppressWarnings ("unchecked")
    public ChronoLocalDateTime<InternationalFixedDate> atTime(final LocalTime localTime) {
        return (ChronoLocalDateTime<InternationalFixedDate>) ChronoLocalDate.super.atTime(localTime);
    }

    /**
     * {@inheritDoc}
     * @return number of TemporalUnit until given Temporal
     */
    @Override
    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        InternationalFixedDate end = InternationalFixedDate.from(endExclusive);

        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case DAYS:
                    return daysUntil(end);
                case WEEKS:
                    return daysUntil(end) / InternationalFixedChronology.DAYS_IN_WEEK;
                case MONTHS:
                    return monthsUntil(end);
                case YEARS:
                    return monthsUntil(end) / InternationalFixedChronology.MONTHS_IN_YEAR;
                case DECADES:
                    return monthsUntil(end) / (InternationalFixedChronology.MONTHS_IN_YEAR * 10);
                case CENTURIES:
                    return monthsUntil(end) / (InternationalFixedChronology.MONTHS_IN_YEAR * 100);
                case MILLENNIA:
                    return monthsUntil(end) / (InternationalFixedChronology.MONTHS_IN_YEAR * 1000);
                case ERAS:
                    return end.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
            }

            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }

        return unit.between(this, end);
    }

    /**
     * Get the number of years from this date to the given day.
     *
     * @param end The end date.
     * @return The number of years from this date to the given day.
     */
    long yearsUntil(final InternationalFixedDate end) {
        long startYear = getProlepticYear() * 512L + getDayOfYear();
        long endYear = end.getProlepticYear() * 512L + end.getDayOfYear();

        return (endYear - startYear) / 512L;
    }

    /**
     * {@inheritDoc}
     * @param endDateExclusive
     * @return period up to that date
     */
    @Override
    public ChronoPeriod until(final ChronoLocalDate endDateExclusive) {
        InternationalFixedDate end = InternationalFixedDate.from(endDateExclusive);
        int years = Math.toIntExact(yearsUntil(end));
        // Get to the same "whole" year.
        InternationalFixedDate sameYearEnd = end.plusYears(years);
        int months = (int) monthsUntil(sameYearEnd);
        int days = (int) daysUntil(sameYearEnd.plusMonths(months));

        return getChronology().period(years, months, days);
    }

    /**
     * {@inheritDoc}
     * @param end
     * @return period up to that date
     */
    @Override
    long monthsUntil(final AbstractDate end) {
        InternationalFixedDate date = InternationalFixedDate.from(end);
        long monthStart = this.getCalculatedProlepticMonth() * 256L + this.getCalculatedDayOfMonth();
        long monthEnd = date.getCalculatedProlepticMonth() * 256L + date.getCalculatedDayOfMonth();

        return (monthEnd - monthStart) / 256L;
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long epochDay =
                ((long) getProlepticYear()) * InternationalFixedChronology.DAYS_IN_YEAR +
                        InternationalFixedChronology.getLeapYearsBefore(getProlepticYear()) + getDayOfYear();

        return epochDay - InternationalFixedChronology.DAYS_0000_TO_1970;
    }

    /**
     * Display the date in human-readable format.
     * Note: leap-day and year-day are not part of any month, thus they appear as "2008-leap-day" / "2004-year-day".
     *
     * TODO: the string "leap-day" and "year-day" depends on a {@link Locale}, should be loaded from a resource bundle.
     * TODO: Some languages may prefer the wording "leap-day of 2008" rather than "2008-leap-day".
     *
     * @return The number of years from this date to the given day.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append(getChronology().toString())
                .append(' ')
                .append(getEra())
                .append(' ')
                .append(getYearOfEra());

        if (isLeapDay()) {
            buf.append("-leap-day");
        } else if (isYearDay()) {
            buf.append("-year-day");
        } else {
            buf.append(getMonth() < 10 ? "-0" : '-')
                    .append(getMonth())
                    .append(getDayOfMonth() < 10 ? "-0" : '-')
                    .append(getDayOfMonth());
        }

        return buf.toString();
    }
}
