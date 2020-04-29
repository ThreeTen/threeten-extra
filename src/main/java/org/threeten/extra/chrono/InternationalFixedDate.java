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

import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_0000_TO_1970;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_LONG_MONTH;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_YEAR;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_PER_CYCLE;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAY_OF_MONTH_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAY_OF_YEAR_LEAP_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAY_OF_YEAR_NORMAL_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.EMPTY_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.EPOCH_DAY_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.ERA_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.INSTANCE;
import static org.threeten.extra.chrono.InternationalFixedChronology.MONTHS_IN_YEAR;
import static org.threeten.extra.chrono.InternationalFixedChronology.MONTH_OF_YEAR_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.WEEKS_IN_MONTH;
import static org.threeten.extra.chrono.InternationalFixedChronology.WEEKS_IN_YEAR;
import static org.threeten.extra.chrono.InternationalFixedChronology.YEAR_RANGE;

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
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

/**
 * A date in the International fixed calendar system.
 * <p>
 * Implements a pure International Fixed calendar (also known as the Cotsworth plan, the Eastman plan,
 * the 13 Month calendar or the Equal Month calendar) a solar calendar proposal for calendar reform designed by
 * Moses B. Cotsworth, who presented it in 1902.
 * <p>
 * It provides for a year of 13 months of 28 days each.
 * Month 6 has 29 days in a leap year, but the additional day is not part of any week.
 * Month 12 always has 29 days, but the additional day is not part of any week.
 * It is therefore a perennial calendar, with every date fixed always on the same weekday.
 * Though it was never officially adopted in any country, it was the official calendar of the Eastman Kodak Company
 * from 1928 to 1989.
 * <p>
 * This date operates using the {@linkplain InternationalFixedChronology International fixed calendar}.
 * This calendar system is a proposed reform calendar system, and is not in common use.
 * The International fixed differs from the Gregorian in terms of month count and length, and the leap year rule.
 * Dates are aligned such that {@code 0001/01/01 (International fixed)} is {@code 0001-01-01 (ISO)}.
 * <p>
 * More information is available in the
 * <a href='https://en.wikipedia.org/wiki/International_Fixed_Calendar'>International Fixed Calendar</a>
 * Wikipedia article.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class InternationalFixedDate
        extends AbstractDate
        implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
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

    //-----------------------------------------------------------------------
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
    public static InternationalFixedDate now() {
        return now(Clock.systemDefaultZone());
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
     * @param zone  the zone ID to use, not null
     * @return the current date using the system clock, not null
     */
    public static InternationalFixedDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current {@code InternationalFixedDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static InternationalFixedDate now(Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return InternationalFixedDate.ofEpochDay(now.toEpochDay());
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the International fixed proleptic-year
     * @param month  the International fixed month-of-year, from 1 to 13
     * @param dayOfMonth  the International fixed day-of-month, from 1 to 28 (29 for Leap Day or Year Day)
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static InternationalFixedDate of(int prolepticYear, int month, int dayOfMonth) {
        return create(prolepticYear, month, dayOfMonth);
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
     * @param temporal  the temporal object to convert, not null
     * @return the date in the International fixed calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code InternationalFixedDate}
     */
    public static InternationalFixedDate from(TemporalAccessor temporal) {
        if (temporal instanceof InternationalFixedDate) {
            return (InternationalFixedDate) temporal;
        }
        return InternationalFixedDate.ofEpochDay(temporal.getLong(ChronoField.EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the International fixed proleptic-year
     * @param dayOfYear  the International fixed day-of-year, from 1 to 366
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static InternationalFixedDate ofYearDay(int prolepticYear, int dayOfYear) {
        YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
        ChronoField.DAY_OF_YEAR.checkValidValue(dayOfYear);

        boolean isLeapYear = INSTANCE.isLeapYear(prolepticYear);
        int lastDoy = (DAYS_IN_YEAR + (isLeapYear ? 1 : 0));
        if (dayOfYear > lastDoy) {
            throw new DateTimeException("Invalid date 'DayOfYear 366' as '" + prolepticYear + "' is not a leap year");
        }
        if (dayOfYear == lastDoy) {
            return new InternationalFixedDate(prolepticYear, 13, 29);
        }
        if (dayOfYear == LEAP_DAY_AS_DAY_OF_YEAR && isLeapYear) {
            return new InternationalFixedDate(prolepticYear, 6, 29);
        }
        int doy0 = dayOfYear - 1;
        if (dayOfYear >= LEAP_DAY_AS_DAY_OF_YEAR && isLeapYear) {
            doy0--;
        }
        int month = (doy0 / DAYS_IN_MONTH) + 1;
        int day = (doy0 % DAYS_IN_MONTH) + 1;
        return new InternationalFixedDate(prolepticYear, month, day);
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static InternationalFixedDate ofEpochDay(long epochDay) {
        EPOCH_DAY_RANGE.checkValidValue(epochDay, ChronoField.EPOCH_DAY);
        long zeroDay = epochDay + DAYS_0000_TO_1970;

        // The two values work great for any dates, just not the first (N/01/01) or the last of the year (N/0/0).
        long year = (400 * zeroDay) / DAYS_PER_CYCLE;
        long doy = zeroDay - (DAYS_IN_YEAR * year + InternationalFixedChronology.getLeapYearsBefore(year));

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
     * @param prolepticYear  the International fixed proleptic-year
     * @param month  the International fixed month, from 1 to 13
     * @param day  the International fixed day-of-month, from 1 to 28 (29 for Leap Day or Year Day)
     * @return the resolved date
     */
    private static InternationalFixedDate resolvePreviousValid(int prolepticYear, int month, int day) {
        int monthR = Math.min(month, MONTHS_IN_YEAR);
        int dayR = Math.min(day,
            (monthR == 13 || (monthR == 6 && INSTANCE.isLeapYear(prolepticYear)) ? DAYS_IN_LONG_MONTH : DAYS_IN_MONTH));

        return create(prolepticYear, monthR, dayR);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method, validates the given triplet year, month and dayOfMonth.
     *
     * @param prolepticYear  the International fixed proleptic-year
     * @param month  the International fixed month, from 1 to 13
     * @param dayOfMonth  the International fixed day-of-month, from 1 to 28 (29 for Leap Day or Year Day)
     * @return the International fixed date
     * @throws DateTimeException if the date is invalid
     */
    static InternationalFixedDate create(int prolepticYear, int month, int dayOfMonth) {
        YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
        MONTH_OF_YEAR_RANGE.checkValidValue(month, ChronoField.MONTH_OF_YEAR);
        DAY_OF_MONTH_RANGE.checkValidValue(dayOfMonth, ChronoField.DAY_OF_MONTH);

        if (dayOfMonth == DAYS_IN_LONG_MONTH && month != 6 && month != MONTHS_IN_YEAR) {
            throw new DateTimeException("Invalid date: " + prolepticYear + '/' + month + '/' + dayOfMonth);
        }
        if (month == 6 && dayOfMonth == DAYS_IN_LONG_MONTH && !INSTANCE.isLeapYear(prolepticYear)) {
            throw new DateTimeException("Invalid Leap Day as '" + prolepticYear + "' is not a leap year");
        }
        return new InternationalFixedDate(prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the International fixed proleptic-year
     * @param month  the International fixed month, from 1 to 13
     * @param dayOfMonth  the International fixed day-of-month, from 1 to 28 (29 for Leap Day or Year Day)
     */
    private InternationalFixedDate(int prolepticYear, int month, int dayOfMonth) {
        this.prolepticYear = prolepticYear;
        this.month = month;
        this.day = dayOfMonth;
        this.isLeapYear = INSTANCE.isLeapYear(prolepticYear);
        this.isLeapDay = this.month == 6 && this.day == 29;
        this.isYearDay = this.month == 13 && this.day == 29;
        this.dayOfYear = ((month - 1) * DAYS_IN_MONTH + day) + (month > 6 && isLeapYear ? 1 : 0);
    }

    /**
     *
     * Validates the object.
     *
     * @return InternationalFixedDate the resolved date, not null
     */
    private Object readResolve() {
        return InternationalFixedDate.of(prolepticYear, month, day);
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
        if (isSpecialDay()) {
            return 0;
        }
        return ((day - 1) / DAYS_IN_WEEK) + 1;
    }

    @Override
    int getAlignedWeekOfYear() {
        if (isSpecialDay()) {
            return 0;
        }
        return (month - 1) * WEEKS_IN_MONTH + ((day - 1) / DAYS_IN_WEEK) + 1;
    }

    /**
     * Returns the day of the week represented by this date.
     * <p>
     * Leap Day and Year Day are not considered week-days, thus return 0.
     *
     * @return the day of the week: between 1 and 7, or 0 (Leap Day, Year Day)
     */
    @Override
    int getDayOfWeek() {
        if (isSpecialDay()) {
            return 0;
        }
        return ((day - 1) % DAYS_IN_WEEK) + 1;
    }

    long getProlepticWeek() {
        return getProlepticMonth() * WEEKS_IN_MONTH + ((getDayOfMonth() - 1) / DAYS_IN_WEEK) - 1;
    }

    private boolean isSpecialDay() {
        return day == DAYS_IN_LONG_MONTH;
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                // day 29 is treated as being outside the normal week
                ChronoField f = (ChronoField) field;
                switch (f) {
                    case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                    case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    case DAY_OF_WEEK:
                        return isSpecialDay() ? EMPTY_RANGE : ValueRange.of(1, DAYS_IN_WEEK);
                    case ALIGNED_WEEK_OF_MONTH:
                        return isSpecialDay() ? EMPTY_RANGE : ValueRange.of(1, WEEKS_IN_MONTH);
                    case ALIGNED_WEEK_OF_YEAR:
                        return isSpecialDay() ? EMPTY_RANGE : ValueRange.of(1, WEEKS_IN_YEAR);
                    case DAY_OF_MONTH:
                        return ValueRange.of(1, lengthOfMonth());
                    case DAY_OF_YEAR:
                        return isLeapYear ? DAY_OF_YEAR_LEAP_RANGE : DAY_OF_YEAR_NORMAL_RANGE;
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
        return isSpecialDay() ? EMPTY_RANGE : ValueRange.of(1, WEEKS_IN_MONTH);
    }

    @Override
    InternationalFixedDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        return resolvePreviousValid(newYear, newMonth, dayOfMonth);
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
    public InternationalFixedChronology getChronology() {
        return INSTANCE;
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
    public InternationalFixedEra getEra() {
        return InternationalFixedEra.CE;
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Month lengths do not match those of the ISO calendar system.
     * <p>
     * Months have 28 days, except June which has 29 in leap years
     * and December (month 13) which always has 29 days.
     *
     * @return the length of the month in days
     */
    @Override
    public int lengthOfMonth() {
        return (isLongMonth() ? DAYS_IN_LONG_MONTH : DAYS_IN_MONTH);
    }

    private boolean isLongMonth() {
        return month == 13 || (month == 6 && isLeapYear);
    }

    /**
     * Returns the length of the year represented by this date.
     * <p>
     * This returns the length of the year in days.
     * Year lengths match those of the ISO calendar system.
     *
     * @return the length of the year in days: 365 or 366
     */
    @Override
    public int lengthOfYear() {
        return DAYS_IN_YEAR + (isLeapYear ? 1 : 0);
    }

    //-------------------------------------------------------------------------
    @Override
    public InternationalFixedDate with(TemporalAdjuster adjuster) {
        return (InternationalFixedDate) adjuster.adjustInto(this);
    }

    @Override
    public InternationalFixedDate with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            if (newValue == 0 && isSpecialDay()) {
                return this;
            }

            ChronoField f = (ChronoField) field;
            getChronology().range(f).checkValidValue(newValue, f);
            int nval = (int) newValue;

            switch (f) {
                case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                case DAY_OF_WEEK:
                    if (newValue == 0 && !isSpecialDay()) {
                        range(f).checkValidValue(newValue, field);
                    }
                    int dom = isSpecialDay() ? 21 : ((getDayOfMonth() - 1) / DAYS_IN_WEEK) * DAYS_IN_WEEK;
                    return resolvePreviousValid(prolepticYear, month, dom + nval);
                case ALIGNED_WEEK_OF_MONTH:
                    if (newValue == 0 && !isSpecialDay()) {
                        range(f).checkValidValue(newValue, field);
                    }
                    int d = isSpecialDay() ? 1 : day % DAYS_IN_WEEK;
                    return resolvePreviousValid(prolepticYear, month, (nval - 1) * DAYS_IN_WEEK + d);
                case ALIGNED_WEEK_OF_YEAR:
                    if (newValue == 0 && !isSpecialDay()) {
                        range(f).checkValidValue(newValue, field);
                    }
                    int newMonth = 1 + ((nval - 1) / WEEKS_IN_MONTH);
                    int newDay = ((nval - 1) % WEEKS_IN_MONTH) * DAYS_IN_WEEK + 1 + ((day - 1) % DAYS_IN_WEEK);
                    return resolvePreviousValid(prolepticYear, newMonth, newDay);
                case DAY_OF_MONTH:
                    return create(prolepticYear, month, nval);
                default:
                    break;
            }
        }

        return (InternationalFixedDate) super.with(field, newValue);
    }

    @Override
    InternationalFixedDate withDayOfYear(int value) {
        return ofYearDay(prolepticYear, value);
    }

    //-----------------------------------------------------------------------
    @Override
    public InternationalFixedDate plus(TemporalAmount amount) {
        return (InternationalFixedDate) amount.addTo(this);
    }

    @Override
    public InternationalFixedDate plus(long amountToAdd, TemporalUnit unit) {
        return (InternationalFixedDate) super.plus(amountToAdd, unit);
    }


    @Override
    InternationalFixedDate plusWeeks(long weeks) {
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
        int newDay = 1 + ((newWeek * DAYS_IN_WEEK + 8 +
                (isLeapDay ? 0 : isYearDay ? -1 : (day - 1) % DAYS_IN_WEEK) - 1) % DAYS_IN_MONTH);
        return create(newYear, newMonth, newDay);
    }

    @Override
    InternationalFixedDate plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        if (months % MONTHS_IN_YEAR == 0) {
            return plusYears(months / MONTHS_IN_YEAR);
        }
        int newMonth = (int) Math.addExact(getProlepticMonth(), months);
        int newYear = newMonth / MONTHS_IN_YEAR;
        newMonth = 1 + (newMonth % MONTHS_IN_YEAR);
        return resolvePreviousValid(newYear, newMonth, day);
    }

    @Override
    InternationalFixedDate plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = YEAR_RANGE.checkValidIntValue(Math.addExact(prolepticYear, yearsToAdd), ChronoField.YEAR);
        return resolvePreviousValid(newYear, month, day);
    }

    @Override
    public InternationalFixedDate minus(TemporalAmount amount) {
        return (InternationalFixedDate) amount.subtractFrom(this);
    }

    @Override
    public InternationalFixedDate minus(long amountToSubtract, TemporalUnit unit) {
        return (InternationalFixedDate) super.minus(amountToSubtract, unit);
    }

    //-------------------------------------------------------------------------
    @Override  // for covariant return type
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<InternationalFixedDate> atTime(LocalTime localTime) {
        return (ChronoLocalDateTime<InternationalFixedDate>) super.atTime(localTime);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return until(InternationalFixedDate.from(endExclusive), unit);
    }

    /**
     * Get the number of years from this date to the given day.
     *
     * @param end The end date.
     * @return The number of years from this date to the given day.
     */
    long yearsUntil(InternationalFixedDate end) {
        long startYear = this.prolepticYear * 512L + this.getInternalDayOfYear();
        long endYear = end.prolepticYear * 512L + end.getInternalDayOfYear();
        return (endYear - startYear) / 512L;
    }

    /**
     * For calculation purposes in a leap year, decrement the day of the year for months 7 and higher - including Year Day.
     * Leave out Leap Day though!
     *
     * @return int day of the year for calculations
     */
    private int getInternalDayOfYear() {
        return isLeapYear && (month > 6) ? dayOfYear - 1 : dayOfYear;
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        InternationalFixedDate end = InternationalFixedDate.from(endDateExclusive);
        int years = Math.toIntExact(yearsUntil(end));
        // Get to the same "whole" year.
        InternationalFixedDate sameYearEnd = plusYears(years);
        int months = (int) sameYearEnd.monthsUntil(end);
        int days = (int) sameYearEnd.plusMonths(months).daysUntil(end);

        // When both Leap Day and Year Day start / end the period, the intra-month difference can be +- 28 days,
        // because internally day-of-month as 1 (Leap Day) or 29 (Year Day) for calculations.
        // Thus we have to compensate the difference accordingly.
        if ((!isYearDay && !isLeapDay) && !(end.isYearDay && !end.isLeapDay)) {
            if (days == DAYS_IN_MONTH) {
                days = 0;
                months += 1;
            }

            if (days == -DAYS_IN_MONTH) {
                days = 0;
                months -= 1;
            }
        }

        return getChronology().period(years, months, days);
    }

    @Override
    long weeksUntil(AbstractDate end) {
        InternationalFixedDate endDate = InternationalFixedDate.from(end);
        int offset = (this.day < 1 || endDate.day < 1) && (this.day != endDate.day) &&
                this.isLeapYear && endDate.isLeapYear ? (this.isBefore(endDate) ? 1 : -1) : 0;
        long startWeek = this.getProlepticWeek() * 8L + this.getDayOfWeek();
        long endWeek = endDate.getProlepticWeek() * 8L + end.getDayOfWeek();

        return (endWeek - startWeek - offset) / 8L;
    }

    @Override
    long monthsUntil(AbstractDate end) {
        InternationalFixedDate date = InternationalFixedDate.from(end);
        long monthStart = this.getProlepticMonth() * 32L + this.getDayOfMonth();
        long monthEnd = date.getProlepticMonth() * 32L + date.getDayOfMonth();

        return (monthEnd - monthStart) / 32L;
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long epochDay = ((long) this.prolepticYear) * DAYS_IN_YEAR +
                InternationalFixedChronology.getLeapYearsBefore(this.prolepticYear) + this.dayOfYear;
        return epochDay - DAYS_0000_TO_1970;
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
