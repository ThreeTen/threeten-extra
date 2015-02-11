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

import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_0000_TO_1970;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_YEAR;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAY_OF_MONTH_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAY_OF_YEAR_LEAP_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAY_OF_YEAR_NORMAL_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_PER_CYCLE;
import static org.threeten.extra.chrono.InternationalFixedChronology.EMPTY_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.EPOCH_DAY_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.ERA_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.INSTANCE;
import static org.threeten.extra.chrono.InternationalFixedChronology.MONTH_OF_YEAR_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.MONTHS_IN_YEAR;
import static org.threeten.extra.chrono.InternationalFixedChronology.WEEK_OF_MONTH_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.WEEKS_IN_MONTH;
import static org.threeten.extra.chrono.InternationalFixedChronology.WEEKS_IN_YEAR;
import static org.threeten.extra.chrono.InternationalFixedChronology.YEAR_RANGE;

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
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

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
 * Dates are aligned such that {@code 0001/01/01 (International fixed)} is {@code 0001-01-01 (ISO)}.</p>
 * <p>
 * More information is available in the <a href='https://en.wikipedia.org/wiki/International_Fixed_Calendar'>International Fixed Calendar</a> Wikipedia article.</p>
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
    private static final long serialVersionUID = -5501342824322148215L;
    /**
     * Leap Day as day-of-year
     */
    private static final int LEAP_DAY_AS_DAY_OF_YEAR = 6 * DAYS_IN_MONTH + 1;
    /**
     * The proleptic year.
     */
    private final int prolepticYear;
    /**
     * The month of the year.
     */
    private final int month;
    /**
     * The day of the month.
     */
    private final int day;
    /**
     * The day of year.
     */
    private final transient int dayOfYear;
    /**
     * Is the proleptic year a Leap year ?
     */
    private final transient boolean isLeapYear;
    /**
     * Is the day-of-year a Leap Day ?
     */
    private final transient boolean isLeapDay;
    /**
     * Is the day-of-year a Year Day ?
     */
    private final transient boolean isYearDay;

    /**
     * For calculation purposes, internally Leap Day is treated as day-of-year 169,
     * squeezed between end of month 6 and beginning of month 7.
     * Similarly, Year Day is treated as day-of-year 365 - or 366 in a leap year.
     */

    //-----------------------------------------------------------------------

    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month, from -1 to 13 (-1 for Leap Day, 0 for Year Day)
     * @param dayOfMonth    the International fixed day-of-month, from -1 to 28 (-1 for Leap Day, 0 for Year Day)
     * @return the International fixed date
     */
    private InternationalFixedDate(final int prolepticYear, final int month, final int dayOfMonth) {
        this.prolepticYear = prolepticYear;
        this.month = month;
        this.day = dayOfMonth;
        this.isLeapYear = isLeapYear();
        this.isLeapDay = this.day == -1;
        this.isYearDay = this.day == 0;
        this.dayOfYear = this.isLeapDay ? LEAP_DAY_AS_DAY_OF_YEAR : this.isYearDay ? DAYS_IN_YEAR + (this.isLeapYear ? 1 : 0) :
                (month - 1) * DAYS_IN_MONTH + this.day + (this.isLeapYear && this.month > 6 ? 1 : 0);
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
     * @param month         the International fixed month-of-year, from -1 to 13 (-1 for Leap Day, 0 for Year Day)
     * @param dayOfMonth    the International fixed day-of-month, from -1 to 28 (-1 for Leap Day, 0 for Year Day)
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range, or if the day-of-month is invalid for the month-year
     */
    public static InternationalFixedDate of(final int prolepticYear, final int month, final int dayOfMonth) {
        return create(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, for the out-of-month day of Leap Day, which follows the last day in June and precedes Sol 1.
     * <p/>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    public static InternationalFixedDate leapDay(final int prolepticYear) {
        return create(prolepticYear, -1, -1);
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, for the out-of-month day of Year Day, which follows the last day in December.
     * <p/>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    public static InternationalFixedDate yearDay(final int prolepticYear) {
        return create(prolepticYear, 0, 0);
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
        YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
        ChronoField.DAY_OF_YEAR.checkValidValue(dayOfYear);

        if (dayOfYear == DAYS_IN_YEAR + 1 && !INSTANCE.isLeapYear(prolepticYear)) {
            throw new DateTimeException("Invalid date 'DayOfYear 366' as '" + prolepticYear + "' is not a leap year");
        }

        boolean isLeapYear = INSTANCE.isLeapYear(prolepticYear);
        boolean isYearDay = dayOfYear == DAYS_IN_YEAR + (isLeapYear ? 1 : 0);
        boolean isLeapDay = isLeapYear && dayOfYear == LEAP_DAY_AS_DAY_OF_YEAR;
        int doy = isLeapYear && dayOfYear > LEAP_DAY_AS_DAY_OF_YEAR ? dayOfYear - 1 : dayOfYear;
        int month = isYearDay ? 0 : isLeapDay ? -1 : 1 + ((doy - 1) / DAYS_IN_MONTH);
        int day = isYearDay ? 0 : isLeapDay ? -1 : 1 + ((doy - 1) % DAYS_IN_MONTH);

        return new InternationalFixedDate(prolepticYear, month, day);
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
        EPOCH_DAY_RANGE.checkValidValue(epochDay, ChronoField.EPOCH_DAY);
        long zeroDay = epochDay + DAYS_0000_TO_1970;

        // The two values work great for any dates, just not the first (N/01/01) or the last of the year (N/0/0).
        long year = (400 * zeroDay) / DAYS_PER_CYCLE;
        long doy = zeroDay - (DAYS_IN_YEAR * year + INSTANCE.getLeapYearsBefore(year));

        boolean isLeapYear = INSTANCE.isLeapYear(year);

        // In some cases, N/01/01 (January 1st) results in (N-1)/0/0, i.e. -1 day off.
        if (doy == (DAYS_IN_YEAR + 1) && !isLeapYear) {
            year += 1;
            doy = 1;
        }

        // In some cases, N/0/0 results in (N+1)/0/0 (rubbish), in a way +1 year off.
        if (doy == 0) {
            year -= 1;
            doy = DAYS_IN_YEAR + (isLeapYear ? 1 : 0);
        }

        return ofYearDay((int) year, (int) doy);
    }

    /**
     * Consistency check for dates manipulations after calls to
     *   {@link #plus(long, TemporalUnit)},
     *   {@link #minus(long, TemporalUnit)},
     *   {@link #until(AbstractDate, TemporalUnit)} or
     *   {@link #with(TemporalField, long)}.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month, from -1 to 13 (-1 for Leap Day, 0 for Year Day)
     * @param day           the International fixed day-of-month, from -1 to 28 (-1 for Leap Day, 0 for Year Day)
     * @return
     */
    private static InternationalFixedDate resolvePreviousValid(final int prolepticYear, final int month, final int day) {
        if ((month == 0 && day == 0) || (month == -1 && day == -1 && INSTANCE.isLeapYear(prolepticYear))) {
            // create valid Year Day or Leap Day
            return create(prolepticYear, month, day);
        }

        int monthR = month == -1 ? 7 : month == 0 ? MONTHS_IN_YEAR : Math.min(month, MONTHS_IN_YEAR);
        int dayR = day == -1 ? 1 : day == 0 ? DAYS_IN_MONTH : Math.min(day, DAYS_IN_MONTH);

        return create(prolepticYear, monthR, dayR);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method, validates the given triplet year, month and dayOfMonth.
     * Special values are required for Year Day (N/0/0) and Leap Day (N/-1/-1).
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month, from -1 to 13 (-1 for Leap Day, 0 for Year Day)
     * @param dayOfMonth    the International fixed day-of-month, from -1 to 28 (-1 for Leap Day, 0 for Year Day)
     * @return the International fixed date
     * @throws DateTimeException if the date is invalid
     */
    static InternationalFixedDate create(final int prolepticYear, final int month, final int dayOfMonth) {
        YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
        MONTH_OF_YEAR_RANGE.checkValidValue(month, ChronoField.MONTH_OF_YEAR);
        DAY_OF_MONTH_RANGE.checkValidValue(dayOfMonth, ChronoField.DAY_OF_MONTH);

        if ((month < 1 || dayOfMonth < 1) && (dayOfMonth != month)) {
            throw new DateTimeException("Ambiguous Year or Leap Day: " + prolepticYear + '/' + month + '/' + dayOfMonth);
        }

        if ((month == -1) && (dayOfMonth == -1) && !INSTANCE.isLeapYear(prolepticYear)) {
            throw new DateTimeException("Invalid Leap Day as '" + prolepticYear + "' is not a leap year");
        }

        return new InternationalFixedDate(prolepticYear, month, dayOfMonth);
    }

    /**
     *
     * Validates the object.
     *
     * @return InternationalFixedDate the resolved date, not null
     */
    private Object readResolve() {
        return InternationalFixedDate.of(this.prolepticYear, this.month, this.day);
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    int getProlepticYear() {
        return this.prolepticYear;
    }

    /**
     * Leap Day is neither part of a week nor a month.
     *
     * @return boolean whether the date is Leap Day
     */
    boolean isLeapDay() {
        return this.isLeapDay;
    }

    /**
     * The last day of the year is Year Day, it is neither part of a week nor a month.
     *
     * @return boolean whether the date is Year Day
     */
    boolean isYearDay() {
        return this.isYearDay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getMonth() {
        return this.month;
    }

    /**
     * For calculation purposes, consider Leap Day to be associated with month Sol.
     * In the same spirit, associate Year Day with the last month of the year.
     *
     * @return int month for calculations
     */
    private int getInternalMonth() {
        return this.isYearDay ? MONTHS_IN_YEAR : this.isLeapDay ? 7 : this.month;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getDayOfMonth() {
        return this.day;
    }

    /**
     * For calculation purposes, consider Leap Day to be Sol 1st.
     * In the same spirit, treat Year Day December 29th.
     *
     * @return int day of the month for calculations
     */
    private int getInternalDayOfMonth() {
        return this.isYearDay ? DAYS_IN_MONTH + 1 : this.isLeapDay ? 0 : this.day;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDayOfYear() {
        return this.dayOfYear;
    }

    /**
     * For calculation purposes in a leap year, decrement the day of the year for months 7 and higher - including Year Day.
     * Leave out Leap Day though!
     *
     * @return int day of the year for calculations
     */
    private int getInternalDayOfYear() {
        return this.isLeapYear && (this.isYearDay || this.month > 6) ? this.dayOfYear - 1 : this.dayOfYear;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    InternationalFixedDate withDayOfYear(final int value) {
        return ofYearDay(this.prolepticYear, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int lengthOfYearInMonths() {
        return MONTHS_IN_YEAR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        return this.month > 0 ? WEEK_OF_MONTH_RANGE : EMPTY_RANGE;
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
        return INSTANCE;
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
     * Since Leap Day / Year Day are not part of any month, their 'imaginary' month is of length 1.
     *
     * @return the length of the month in days: 28
     */
    @Override
    public int lengthOfMonth() {
        return this.month > 0 ? DAYS_IN_MONTH : 1;
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
        return DAYS_IN_YEAR + (this.isLeapYear ? 1 : 0);
    }

    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public InternationalFixedDate with(final TemporalAdjuster adjuster) {
        return (InternationalFixedDate) adjuster.adjustInto(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternationalFixedDate with(final TemporalField field, final long newValue) {
        if (field instanceof ChronoField) {
            if (newValue == 0 && this.day < 1) {
                return this;
            }

            ChronoField f = (ChronoField) field;

            if (f == ChronoField.DAY_OF_MONTH || f == ChronoField.MONTH_OF_YEAR) {
                if (newValue == 0) {
                    return create(this.prolepticYear, 0, 0);
                }

                if (newValue == -1) {
                    return create(this.prolepticYear, -1, -1);
                }
            }

            getChronology().range(f).checkValidValue(newValue, f);
            int nval = (int) newValue;

            switch (f) {
                case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                case DAY_OF_WEEK:
                    int dom = this.isYearDay ? 21 : (getInternalDayOfMonth() / DAYS_IN_WEEK) * DAYS_IN_WEEK;
                    return resolvePreviousValid(this.prolepticYear, this.month, dom + nval);
                case ALIGNED_WEEK_OF_MONTH:
                    int d = this.day < 1 ? 1 : this.day % DAYS_IN_WEEK;
                    return resolvePreviousValid(this.prolepticYear, this.month, (nval - 1) * DAYS_IN_WEEK + d);
                case ALIGNED_WEEK_OF_YEAR:
                    int newMonth = 1 + ((nval - 1) / WEEKS_IN_MONTH);
                    int newDay = ((nval - 1) % WEEKS_IN_MONTH) * DAYS_IN_WEEK + 1 + ((this.day < 1) ? 0 : (this.day - 1) % DAYS_IN_WEEK);
                    return resolvePreviousValid(this.prolepticYear, newMonth, newDay);
                default:
                    break;
            }
        }

        return (InternationalFixedDate) super.with(field, newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternationalFixedDate plus(final TemporalAmount amount) {
        return (InternationalFixedDate) amount.addTo(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternationalFixedDate plus(final long amountToAdd, final TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;

            switch (f) {
                case WEEKS:
                    return plusWeeks(amountToAdd);
                case MONTHS:
                    return plusMonths(amountToAdd);
                default:
                    break;
            }
        }

        return (InternationalFixedDate) super.plus(amountToAdd, unit);
    }


    private InternationalFixedDate plusWeeks(final long weeks) {
        if (weeks == 0) {
            return this;
        }

        if (weeks % WEEKS_IN_MONTH == 0) {
            return plusMonths(weeks / WEEKS_IN_MONTH);
        }

        long calcEm = Math.addExact(getProlepticWeek(), weeks);
        int newYear = Math.toIntExact(Math.floorDiv(calcEm, WEEKS_IN_YEAR));
        int newWeek = Math.toIntExact(Math.floorMod(calcEm, WEEKS_IN_YEAR));
        int newMonth = 1 + Math.floorDiv(newWeek, WEEKS_IN_MONTH);
        int newDay = 1 + ((newWeek * DAYS_IN_WEEK + 8 + (this.isLeapDay ? 0 : this.isYearDay ? -1 : (this.day - 1) % DAYS_IN_WEEK) - 1) % DAYS_IN_MONTH);

        return create(newYear, newMonth, newDay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternationalFixedDate plusMonths(final long months) {
        if (months == 0) {
            return this;
        }

        if (months % MONTHS_IN_YEAR == 0) {
            return plusYears(months / MONTHS_IN_YEAR);
        }

        int newMonth = (int) Math.addExact(getProlepticMonth(), months);
        int newYear = newMonth / MONTHS_IN_YEAR;
        newMonth = 1 + (newMonth % MONTHS_IN_YEAR);

        return resolvePreviousValid(newYear, newMonth, this.day);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    InternationalFixedDate plusYears(final long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }

        int newYear = YEAR_RANGE.checkValidIntValue(Math.addExact(this.prolepticYear, yearsToAdd), ChronoField.YEAR);

        return resolvePreviousValid(newYear, this.month, this.day);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueRange range(final TemporalField field) {
        boolean special = this.day < 1;

        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                ChronoField f = (ChronoField) field;

                switch (f) {
                    case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                    case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    case DAY_OF_WEEK:
                        return special ? EMPTY_RANGE : ValueRange.of(1, DAYS_IN_WEEK);
                    case ALIGNED_WEEK_OF_MONTH:
                        return special ? EMPTY_RANGE : ValueRange.of(1, WEEKS_IN_MONTH);
                    case ALIGNED_WEEK_OF_YEAR:
                        return special ? EMPTY_RANGE : ValueRange.of(1, WEEKS_IN_YEAR);
                    case DAY_OF_MONTH:
                        return this.isYearDay ? EMPTY_RANGE : this.isLeapDay ? ValueRange.of(-1, -1) : ValueRange.of(1, DAYS_IN_MONTH);
                    case DAY_OF_YEAR:
                        return this.isLeapYear ? DAY_OF_YEAR_LEAP_RANGE : DAY_OF_YEAR_NORMAL_RANGE;
                    case EPOCH_DAY:
                        return EPOCH_DAY_RANGE;
                    case ERA:
                        return ERA_RANGE;
                    case MONTH_OF_YEAR:
                        return MONTH_OF_YEAR_RANGE;
                    default:
                        break;
                }
            } else {
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }

        return super.range(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAlignedDayOfWeekInMonth() {
        if (this.day < 1) {
            return 0;
        }

        return ((this.day - 1) % lengthOfWeek()) + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getAlignedDayOfWeekInYear() {
        if (this.day < 1) {
            return 0;
        }

        return ((this.day - 1) % lengthOfWeek()) + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getAlignedWeekOfMonth() {
        if (this.day < 1) {
            return 0;
        }

        return ((this.day - 1) / lengthOfWeek()) + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getAlignedWeekOfYear() {
        if (this.day < 1) {
            return 0;
        }

        return 1 + (this.month - 1) * WEEKS_IN_MONTH + ((this.day - 1) / DAYS_IN_WEEK);
    }

    /**
     * Returns the day of the week represented by this date.
     * <p/>
     * Leap Day and Year Day are not considered week-days, thus return 0.
     *
     * @return the day of the week: between 1 and 7, or 0 (Leap Day, Year Day)
     */
    @Override
    public int getDayOfWeek() {
        if (this.day < 1) {
            return 0;
        }

        return 1 + ((this.day - 1) % DAYS_IN_WEEK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long getProlepticMonth() {
        return this.prolepticYear * lengthOfYearInMonths() + getInternalMonth() - 1;
    }

    long getProlepticWeek() {
        return getProlepticMonth() * WEEKS_IN_MONTH + ((getInternalDayOfMonth() - 1) / DAYS_IN_WEEK) - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternationalFixedDate minus(final TemporalAmount amount) {
        return (InternationalFixedDate) amount.subtractFrom(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternationalFixedDate minus(final long amountToSubtract, final TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override  // for covariant return type
    @SuppressWarnings ("unchecked")
    public ChronoLocalDateTime<InternationalFixedDate> atTime(final LocalTime localTime) {
        return (ChronoLocalDateTime<InternationalFixedDate>) ChronoLocalDate.super.atTime(localTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        return until(InternationalFixedDate.from(endExclusive), unit);
    }

    long until(final InternationalFixedDate end, final TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case WEEKS:
                    return weeksUntil(end);
                case MONTHS:
                    return monthsUntil(end);
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
    long yearsUntil(final InternationalFixedDate end) {
        long startYear = this.prolepticYear * 512L + this.getInternalDayOfYear();
        long endYear = end.prolepticYear * 512L + end.getInternalDayOfYear();
        return (endYear - startYear) / 512L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChronoPeriod until(final ChronoLocalDate endDateExclusive) {
        InternationalFixedDate end = InternationalFixedDate.from(endDateExclusive);
        int years = Math.toIntExact(yearsUntil(end));
        // Get to the same "whole" year.
        InternationalFixedDate sameYearEnd = plusYears(years);
        int months = (int) sameYearEnd.monthsUntil(end);
        int days = (int) sameYearEnd.plusMonths(months).daysUntil(end);

        // When both Leap Day and Year Day start / end the period, the intra-month difference can be +- 28 days,
        // because internally day-of-month as 1 (Leap Day) or 29 (Year Day) for calculations.
        // Thus we have to compensate the difference accordingly.
        if (days == DAYS_IN_MONTH) {
            days = 0;
            months += 1;
        }

        if (days == -DAYS_IN_MONTH) {
            days = 0;
            months -= 1;
        }

        return getChronology().period(years, months, days);
    }

    private long weeksUntil(final InternationalFixedDate end) {
        int offset = (this.day < 1 || end.day < 1) && (this.day != end.day) && this.isLeapYear && end.isLeapYear ? (this.isBefore(end) ? 1 : -1) : 0;
        long startWeek = this.getProlepticWeek() * 8L + this.getDayOfWeek();
        long endWeek = end.getProlepticWeek() * 8L + end.getDayOfWeek();

        return (endWeek - startWeek - offset) / 8L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long monthsUntil(final AbstractDate end) {
        InternationalFixedDate date = InternationalFixedDate.from(end);
        long monthStart = this.getProlepticMonth() * 32L + this.getInternalDayOfMonth();
        long monthEnd = date.getProlepticMonth() * 32L + date.getInternalDayOfMonth();

        return (monthEnd - monthStart) / 32L;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public long toEpochDay() {
        long epochDay = ((long) this.prolepticYear) * DAYS_IN_YEAR + INSTANCE.getLeapYearsBefore(this.prolepticYear) + this.dayOfYear;

        return epochDay - DAYS_0000_TO_1970;
    }

    /**
     * Display the date in human-readable format.
     * Note: Leap Day and Year Day are not part of any month; Leap Day is displayed as "N/-1/-1", Year Day as "N/0/0".
     *
     * @return The number of years from this date to the given day.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(30);

        return buf.append(getChronology().toString())
                .append(' ')
                .append(getEra())
                .append(' ')
                .append(getYearOfEra())
                .append(this.month < 10 && this.month > 0 ? "/0" : '/')
                .append(this.month)
                .append(this.day < 10 && this.day > 0 ? "/0" : '/')
                .append(this.day)
                .toString();
    }
}
