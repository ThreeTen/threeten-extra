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

import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_0001_TO_1970;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_MONTH_LONG;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_QUARTER;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_YEAR;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_YEAR_LONG;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_PER_CYCLE;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAY_OF_MONTH_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAY_OF_YEAR_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.EPOCH_DAY_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.ERA_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.INSTANCE;
import static org.threeten.extra.chrono.Symmetry454Chronology.MONTHS_IN_YEAR;
import static org.threeten.extra.chrono.Symmetry454Chronology.MONTH_OF_YEAR_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.WEEKS_IN_MONTH;
import static org.threeten.extra.chrono.Symmetry454Chronology.WEEKS_IN_YEAR;
import static org.threeten.extra.chrono.Symmetry454Chronology.YEAR_RANGE;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoField;
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
 * A date in the Symmetry454 calendar system.
 * <p>
 * This date operates using the {@linkplain Symmetry454Chronology Symmetry454 calendar}.
 * This calendar system is a proposed reform calendar system, and is not in common use.
 * The Symmetry454 calendar differs from the Gregorian in terms of month length, and the leap year rule.
 * Dates are aligned such that {@code 0001/01/01 (Sym454)} is {@code 0001-01-01 (ISO)}.
 * The alignment of January 1st happens 40 times within a 293 years cycle, skipping 5, 6, 11 or 12 years in between:
 *   1,   7,  18,  24,  29,  35,  46,  52,  57,  63,  74,  80,  85,  91, 103, 114, 120, 125, 131, 142,
 * 148, 153, 159, 170, 176, 181, 187, 198, 210, 216, 221, 227, 238, 244, 249, 255, 266, 272, 277, 283.
 * <p>
 * The implementation is a pure Symmetry454 calendar, as proposed by Dr. Irv Bromberg.
 * The year shares the 12 months with the Gregorian calendar.
 * The months February, May, August, November span 35 days, all other months consist of 28 days.
 * In leap years, December is extended with a full week, the so-called "leap week".
 * Since each month is made of full weeks, the calendar is perennial, with every date fixed always on the same weekday.
 * Each month starts on a Monday and ends on a Sunday; so does each year.
 * The 13th day of a month is always a Saturday.
 * <p>
 * More information is available on Wikipedia at
 * <a href='https://en.wikipedia.org/wiki/Symmetry454'>Symmetry454</a> or on the calendar's
 * <a href='https://individual.utoronto.ca/kalendis/symmetry.htm'>home page</a>.
 * <p>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class Symmetry454Date
        extends AbstractDate
        implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -3540913335234762448L;
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

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code Symmetry454Date} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static Symmetry454Date now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current {@code Symmetry454Date} from the system clock in the specified time-zone.
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
    public static Symmetry454Date now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current {@code Symmetry454Date} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static Symmetry454Date now(Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return Symmetry454Date.ofEpochDay(now.toEpochDay());
    }

    /**
     * Obtains a {@code Symmetry454Date} representing a date in the Symmetry454 calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code Symmetry454Date} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param month  the Symmetry454 month-of-year, from 1 to 12
     * @param dayOfMonth  the Symmetry454 day-of-month, from 1 to 28, or 1 to 35 in February, May, August, November
     *   and December in a Leap Year
     * @return the date in Symmetry454 calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static Symmetry454Date of(int prolepticYear, int month, int dayOfMonth) {
        return create(prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Symmetry454Date} from a temporal object.
     * <p>
     * This obtains a date in the Symmetry454 calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code Symmetry454Date}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code Symmetry454Date::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in the Symmetry454 calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code Symmetry454Date}
     */
    public static Symmetry454Date from(TemporalAccessor temporal) {
        if (temporal instanceof Symmetry454Date) {
            return (Symmetry454Date) temporal;
        }
        return Symmetry454Date.ofEpochDay(temporal.getLong(ChronoField.EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Symmetry454Date} representing a date in the Symmetry454 calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code Symmetry454Date} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param dayOfYear  the Symmetry454 day-of-year, from 1 to 364/371
     * @return the date in Symmetry454 calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static Symmetry454Date ofYearDay(int prolepticYear, int dayOfYear) {
        YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
        DAY_OF_YEAR_RANGE.checkValidValue(dayOfYear, ChronoField.DAY_OF_YEAR);
        boolean leap = INSTANCE.isLeapYear(prolepticYear);
        if (dayOfYear > DAYS_IN_YEAR && !leap) {
            throw new DateTimeException("Invalid date 'DayOfYear " + dayOfYear + "' as '" + prolepticYear + "' is not a leap year");
        }

        int offset = Math.min(dayOfYear, DAYS_IN_YEAR) - 1;
        int quarter = offset / DAYS_IN_QUARTER;
        int day = dayOfYear - quarter * DAYS_IN_QUARTER;
        int month = 1 + quarter * 3;

        if (day > DAYS_IN_MONTH + DAYS_IN_MONTH + DAYS_IN_WEEK) {
            month += 2;
            day -= DAYS_IN_MONTH + DAYS_IN_MONTH + DAYS_IN_WEEK;
        } else if (day > DAYS_IN_MONTH) {
            month += 1;
            day -= DAYS_IN_MONTH;
        }
        return new Symmetry454Date(prolepticYear, month, day);
    }

    /**
     * Obtains a {@code Symmetry454Date} representing a date in the Symmetry454 calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO), corresponds to 1970-01-04 (Sym454)
     * @return the date in Symmetry454 calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static Symmetry454Date ofEpochDay(long epochDay) {
        EPOCH_DAY_RANGE.checkValidValue(epochDay + 3, ChronoField.EPOCH_DAY);
        long zeroDay = epochDay + DAYS_0001_TO_1970 + 1;
        long year = 1 + ((293 * zeroDay) / DAYS_PER_CYCLE);
        long doy = zeroDay - (DAYS_IN_YEAR * (year - 1) + Symmetry454Chronology.getLeapYearsBefore(year) * DAYS_IN_WEEK);

        if (doy < 1) {
            year--;
            doy += INSTANCE.isLeapYear(year) ? DAYS_IN_YEAR_LONG : DAYS_IN_YEAR;
        }

        int diy = INSTANCE.isLeapYear(year) ? DAYS_IN_YEAR_LONG : DAYS_IN_YEAR;
        if (doy > diy) {
            doy -= diy;
            year++;
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
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param month  the Symmetry454 month, from 1 to 12
     * @return the resolved date
     */
    private static Symmetry454Date resolvePreviousValid(int prolepticYear, int month, int day) {
        int monthR = Math.min(month, MONTHS_IN_YEAR);
        int dayR = Math.min(day,
            (monthR % 3 == 2) || (monthR == 12 && INSTANCE.isLeapYear(prolepticYear)) ? DAYS_IN_MONTH_LONG : DAYS_IN_MONTH);

        return create(prolepticYear, monthR, dayR);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method, validates the given triplet year, month and dayOfMonth.
     *
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param month  the Symmetry454 month, from 1 to 12
     * @param dayOfMonth  the Symmetry454 day-of-month, from 1 to 28, or 1 to 35 in February, May, August, November
     *   and December in a Leap Year
     * @return the Symmetry454 date
     * @throws DateTimeException if the date is invalid
     */
    static Symmetry454Date create(int prolepticYear, int month, int dayOfMonth) {
        YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
        MONTH_OF_YEAR_RANGE.checkValidValue(month, ChronoField.MONTH_OF_YEAR);
        DAY_OF_MONTH_RANGE.checkValidValue(dayOfMonth, ChronoField.DAY_OF_MONTH);

        if (dayOfMonth > DAYS_IN_MONTH) {
            if (month == MONTHS_IN_YEAR) {
                if (!INSTANCE.isLeapYear(prolepticYear)) {
                    throw new DateTimeException("Invalid Leap Day as '" + prolepticYear + "' is not a leap year");
                }
            } else if (month % 3 != 2) {
                throw new DateTimeException("Invalid date: " + prolepticYear + '/' + month + '/' + dayOfMonth);
            }
        }
        return new Symmetry454Date(prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param month  the Symmetry454 month, from 1 to 12
     * @param dayOfMonth  the Symmetry454 day-of-month, from 1 to 28, or 1 to 35 in February, May, August, November
     *   and December in a Leap Year
     */
    private Symmetry454Date(int prolepticYear, int month, int dayOfMonth) {
        this.prolepticYear = prolepticYear;
        this.month = month;
        this.day = dayOfMonth;
        this.dayOfYear = DAYS_IN_MONTH * (month - 1) + DAYS_IN_WEEK * (month / 3) + dayOfMonth;
    }

    /**
     * Validates the object.
     *
     * @return Symmetry454Date the resolved date, not null
     */
    private Object readResolve() {
        return Symmetry454Date.of(prolepticYear, month, day);
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
        return dayOfYear;
    }

    @Override
    int lengthOfYearInMonths() {
        return MONTHS_IN_YEAR;
    }

    @Override
    int getAlignedDayOfWeekInMonth() {
        return getDayOfWeek();
    }

    @Override
    int getAlignedDayOfWeekInYear() {
        return getDayOfWeek();
    }

    @Override
    int getAlignedWeekOfMonth() {
        return ((day - 1) / DAYS_IN_WEEK) + 1;
    }

    @Override
    int getAlignedWeekOfYear() {
        return ((dayOfYear - 1) / DAYS_IN_WEEK) + 1;
    }

    @Override
    int getDayOfWeek() {
        return ((day - 1) % DAYS_IN_WEEK) + 1;
    }

    long getProlepticWeek() {
        return getProlepticMonth() * WEEKS_IN_MONTH + ((getDayOfMonth() - 1) / DAYS_IN_WEEK) - 1;
    }

    /**
     * Checks if the date is within the leap week.
     * 
     * @return true if this date is in the leap week
     */
    public boolean isLeapWeek() {
        return isLeapYear() && this.dayOfYear > DAYS_IN_YEAR;
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                ChronoField f = (ChronoField) field;
                switch (f) {
                    case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                    case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    case DAY_OF_WEEK:
                        return ValueRange.of(1, DAYS_IN_WEEK);
                    case ALIGNED_WEEK_OF_MONTH:
                        return ValueRange.of(1, WEEKS_IN_MONTH + (isLongMonth() ? 1 : 0));
                    case ALIGNED_WEEK_OF_YEAR:
                        return ValueRange.of(1, WEEKS_IN_YEAR + (isLeapYear() ? 1 : 0));
                    case DAY_OF_MONTH:
                        return ValueRange.of(1, lengthOfMonth());
                    case DAY_OF_YEAR:
                        return ValueRange.of(1, lengthOfYear());
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

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        // never invoked
        return ValueRange.of(1, WEEKS_IN_MONTH);
    }

    @Override
    Symmetry454Date resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        return resolvePreviousValid(newYear, newMonth, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the Symmetry454 calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the Symmetry454 chronology, not null
     */
    @Override
    public Symmetry454Chronology getChronology() {
        return INSTANCE;
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The Symmetry454 calendar system uses {@link IsoEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public IsoEra getEra() {
        return (prolepticYear >= 1 ? IsoEra.CE : IsoEra.BCE);
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Month lengths do not match those of the ISO calendar system.
     * <p>
     * Most months have 28 days, except for February, May, August, November, and
     * December in a leap year, each has 35 days.
     *
     * @return the length of the month in days
     */
    @Override
    public int lengthOfMonth() {
        return isLongMonth() ? DAYS_IN_MONTH_LONG : DAYS_IN_MONTH;
    }

    private boolean isLongMonth() {
        return this.month % 3 == 2 || (isLeapYear() && this.month == MONTHS_IN_YEAR);
    }

    /**
     * Returns the length of the year represented by this date.
     * <p>
     * This returns the length of the year in days.
     * Year lengths do NOT match those of the ISO calendar system.
     *
     * @return the length of the year in days: 364 or 371
     */
    @Override
    public int lengthOfYear() {
        return DAYS_IN_YEAR + (isLeapYear() ? DAYS_IN_WEEK : 0);
    }

    //-------------------------------------------------------------------------
    @Override
    public Symmetry454Date with(TemporalAdjuster adjuster) {
        return (Symmetry454Date) adjuster.adjustInto(this);
    }

    @Override
    public Symmetry454Date with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            if (newValue == 0) {
                return this;
            }
            ChronoField f = (ChronoField) field;
            getChronology().range(f).checkValidValue(newValue, f);
            int nval = (int) newValue;
            switch (f) {
                case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                case DAY_OF_WEEK:
                    range(f).checkValidValue(newValue, field);
                    int dom = ((getDayOfMonth() - 1) / DAYS_IN_WEEK) * DAYS_IN_WEEK;
                    return resolvePreviousValid(prolepticYear, month, dom + nval);
                case ALIGNED_WEEK_OF_MONTH:
                    range(f).checkValidValue(newValue, field);
                    int d = day % DAYS_IN_WEEK;
                    return resolvePreviousValid(prolepticYear, month, (nval - 1) * DAYS_IN_WEEK + d);
                case ALIGNED_WEEK_OF_YEAR:
                    range(f).checkValidValue(newValue, field);
                    int newMonth = 1 + ((nval - 1) / WEEKS_IN_MONTH);
                    int newDay = ((nval - 1) % WEEKS_IN_MONTH) * DAYS_IN_WEEK + 1 + ((day - 1) % DAYS_IN_WEEK);
                    return resolvePreviousValid(prolepticYear, newMonth, newDay);
                case DAY_OF_MONTH:
                    return create(prolepticYear, month, nval);
                default:
                    break;
            }
        }
        return (Symmetry454Date) super.with(field, newValue);
    }

    @Override
    Symmetry454Date withDayOfYear(int value) {
        return ofYearDay(prolepticYear, value);
    }

    //-----------------------------------------------------------------------
    @Override
    public Symmetry454Date plus(TemporalAmount amount) {
        return (Symmetry454Date) amount.addTo(this);
    }

    @Override
    public Symmetry454Date plus(long amountToAdd, TemporalUnit unit) {
        return (Symmetry454Date) super.plus(amountToAdd, unit);
    }

    @Override
    public Symmetry454Date minus(TemporalAmount amount) {
        return (Symmetry454Date) amount.subtractFrom(this);
    }

    @Override
    public Symmetry454Date minus(long amountToSubtract, TemporalUnit unit) {
        return (Symmetry454Date) super.minus(amountToSubtract, unit);
    }

    //-------------------------------------------------------------------------
    @Override  // for covariant return type
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<Symmetry454Date> atTime(LocalTime localTime) {
        return (ChronoLocalDateTime<Symmetry454Date>) super.atTime(localTime);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return until(Symmetry454Date.from(endExclusive), unit);
    }

    /**
     * Get the number of years from this date to the given day.
     *
     * @param end The end date.
     * @return The number of years from this date to the given day.
     */
    long yearsUntil(Symmetry454Date end) {
        long startYear = this.prolepticYear * 512L + this.getDayOfYear();
        long endYear = end.prolepticYear * 512L + end.getDayOfYear();
        return (endYear - startYear) / 512L;
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        Symmetry454Date end = Symmetry454Date.from(endDateExclusive);
        int years = Math.toIntExact(yearsUntil(end));
        // Get to the same "whole" year.
        Symmetry454Date sameYearEnd = (Symmetry454Date) plusYears(years);
        int months = (int) sameYearEnd.monthsUntil(end);
        int days = (int) sameYearEnd.plusMonths(months).daysUntil(end);
        return getChronology().period(years, months, days);
    }

    @Override
    long weeksUntil(AbstractDate end) {
        Symmetry454Date endDate = Symmetry454Date.from(end);
        long startWeek = this.getProlepticWeek() * 8L + this.getDayOfWeek();
        long endWeek = endDate.getProlepticWeek() * 8L + endDate.getDayOfWeek();
        return (endWeek - startWeek) / 8L;
    }

    @Override
    long monthsUntil(AbstractDate end) {
        Symmetry454Date date = Symmetry454Date.from(end);
        long monthStart = this.getProlepticMonth() * 64L + this.getDayOfMonth();
        long monthEnd = date.getProlepticMonth() * 64L + date.getDayOfMonth();
        return (monthEnd - monthStart) / 64L;
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long epochDay =
                (long) (this.prolepticYear - 1) * DAYS_IN_YEAR +
                Symmetry454Chronology.getLeapYearsBefore(this.prolepticYear) * DAYS_IN_WEEK +
                this.dayOfYear -
                DAYS_0001_TO_1970 - 1;

        return epochDay;
    }

    /**
     * Display the date in human-readable format.
     *
     * @return the string representation
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
                .append(this.day < 10 ? "/0" : '/')
                .append(this.day)
                .toString();
    }

}
