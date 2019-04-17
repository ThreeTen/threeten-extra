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

import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.threeten.extra.chrono.DiscordianChronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.DiscordianChronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.DiscordianChronology.MONTHS_IN_YEAR;
import static org.threeten.extra.chrono.DiscordianChronology.OFFSET_FROM_ISO_0000;
import static org.threeten.extra.chrono.DiscordianChronology.WEEKS_IN_YEAR;

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
 * A date in the Discordian calendar system.
 * <p>
 * This date operates using the {@linkplain DiscordianChronology Discordian calendar}.
 * This calendar system is used by some adherents to Discordianism.
 * The Discordian differs from the Gregorian in terms of the length of the week and month, and uses an offset year.
 * Dates are aligned such that {@code 0001-01-01 (Discordian)} is {@code -1165-01-01 (ISO)}.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class DiscordianDate
        extends AbstractDate
        implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -4340508226506164852L;
    /**
     * The difference between the Discordian and ISO epoch day count (Discordian 1167-01-01 to ISO 1970-01-01).
     */
    private static final int DISCORDIAN_1167_TO_ISO_1970 = 719162;
    /**
     * The days per short 4 year cycle.
     */
    private static final int DAYS_PER_SHORT_CYCLE = (365 * 4) + 1;
    /**
     * The days per 100 year cycle.
     */
    private static final int DAYS_PER_CYCLE = (DAYS_PER_SHORT_CYCLE * 25) - 1;
    /**
     * The days per 400 year long cycle.
     */
    private static final int DAYS_PER_LONG_CYCLE = (DAYS_PER_CYCLE * 4) + 1;
    /**
     * Offset in days from start of year to St. Tib's Day.
     */
    private static final int ST_TIBS_OFFSET = 60;

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
     * Obtains the current {@code DiscordianDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static DiscordianDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current {@code DiscordianDate} from the system clock in the specified time-zone.
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
    public static DiscordianDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current {@code DiscordianDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static DiscordianDate now(Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return DiscordianDate.ofEpochDay(now.toEpochDay());
    }

    /**
     * Obtains a {@code DiscordianDate} representing a date in the Discordian calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code DiscordianDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * <p>
     * St. Tib's Day is indicated by specifying 0 for both month and day-of-month.
     *
     * @param prolepticYear  the Discordian proleptic-year
     * @param month  the Discordian month-of-year, from 1 to 5
     * @param dayOfMonth  the Discordian day-of-month, from 1 to 73
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static DiscordianDate of(int prolepticYear, int month, int dayOfMonth) {
        return DiscordianDate.create(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a {@code DiscordianDate} from a temporal object.
     * <p>
     * This obtains a date in the Discordian calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code DiscordianDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code DiscordianDate::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code DiscordianDate}
     */
    public static DiscordianDate from(TemporalAccessor temporal) {
        if (temporal instanceof DiscordianDate) {
            return (DiscordianDate) temporal;
        }
        return DiscordianDate.ofEpochDay(temporal.getLong(EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code DiscordianDate} representing a date in the Discordian calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code DiscordianDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Discordian proleptic-year
     * @param dayOfYear  the Discordian day-of-year, from 1 to 366
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static DiscordianDate ofYearDay(int prolepticYear, int dayOfYear) {
        DiscordianChronology.YEAR_RANGE.checkValidValue(prolepticYear, YEAR);
        DAY_OF_YEAR.checkValidValue(dayOfYear);
        boolean leap = DiscordianChronology.INSTANCE.isLeapYear(prolepticYear);
        if (dayOfYear == 366 && !leap) {
            throw new DateTimeException("Invalid date 'DayOfYear 366' as '" + prolepticYear + "' is not a leap year");
        }

        if (leap) {
            if (dayOfYear == ST_TIBS_OFFSET) {
                // Take care of special case of St Tib's Day.
                return new DiscordianDate(prolepticYear, 0, 0);
            } else if (dayOfYear > ST_TIBS_OFFSET) {
                // Offset dayOfYear to account for added day.
                dayOfYear--;
            }
        }

        int month = (dayOfYear - 1) / DAYS_IN_MONTH + 1;
        int dayOfMonth = (dayOfYear - 1) % DAYS_IN_MONTH + 1;

        return new DiscordianDate(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a {@code DiscordianDate} representing a date in the Discordian calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static DiscordianDate ofEpochDay(final long epochDay) {
        DiscordianChronology.EPOCH_DAY_RANGE.checkValidValue(epochDay, EPOCH_DAY);

        // use of Discordian 1167 makes leap year at end of long cycle
        long discordianEpochDay = epochDay + DISCORDIAN_1167_TO_ISO_1970;

        long longCycle = Math.floorDiv(discordianEpochDay, DAYS_PER_LONG_CYCLE);
        long daysInLongCycle = Math.floorMod(discordianEpochDay, DAYS_PER_LONG_CYCLE);
        if (daysInLongCycle == DAYS_PER_LONG_CYCLE - 1) {
            int year = (int) (longCycle * 400) + 400;
            return ofYearDay(year + OFFSET_FROM_ISO_0000, 366);
        }

        int cycle = (int) daysInLongCycle / DAYS_PER_CYCLE;
        int dayInCycle = (int) daysInLongCycle % DAYS_PER_CYCLE;
        int shortCycle = dayInCycle / DAYS_PER_SHORT_CYCLE;
        int dayInShortCycle = dayInCycle % DAYS_PER_SHORT_CYCLE;

        if (dayInShortCycle == DAYS_PER_SHORT_CYCLE - 1) {
            int year = (int) (longCycle * 400) + (cycle * 100) + (shortCycle * 4) + 4;
            return ofYearDay(year + OFFSET_FROM_ISO_0000, 366);
        }

        int year = (int) (longCycle * 400) + (cycle * 100) + (shortCycle * 4) + (dayInShortCycle / 365) + 1;
        int dayOfYear = (dayInShortCycle % 365) + 1;

        return ofYearDay(year + OFFSET_FROM_ISO_0000, dayOfYear);
    }

    private static DiscordianDate resolvePreviousValid(int prolepticYear, int month, int day) {
        switch (month) {
            case 0:
                day = 0;
                if (DiscordianChronology.INSTANCE.isLeapYear(prolepticYear)) {
                    break;
                }
                month = 1;
                // fall through
            default:
                if (day == 0) {
                    day = ST_TIBS_OFFSET;
                }
        }
        return new DiscordianDate(prolepticYear, month, day);
    }

    private static long getLeapYearsBefore(long year) {
        long offsetYear = year - OFFSET_FROM_ISO_0000 - 1;
        return Math.floorDiv(offsetYear, 4) - Math.floorDiv(offsetYear, 100) + Math.floorDiv(offsetYear, 400);
    }

    /**
     * Creates a {@code DiscordianDate} validating the input.
     *
     * @param prolepticYear  the Discordian proleptic-year
     * @param month  the Discordian month-of-year, from 1 to 5
     * @param dayOfMonth  the Discordian day-of-month, from 1 to 73
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the month-year
     */
    static DiscordianDate create(int prolepticYear, int month, int dayOfMonth) {
        DiscordianChronology.YEAR_RANGE.checkValidValue(prolepticYear, YEAR);
        DiscordianChronology.MONTH_OF_YEAR_RANGE.checkValidValue(month, MONTH_OF_YEAR);
        DiscordianChronology.DAY_OF_MONTH_RANGE.checkValidValue(dayOfMonth, DAY_OF_MONTH);

        if (month == 0 || dayOfMonth == 0) {
            if (month != 0 || dayOfMonth != 0) {
                throw new DateTimeException("Invalid date '" + month + " " + dayOfMonth + "' as St. Tib's Day is the only special day inserted in a non-existent month.");
            } else if (!DiscordianChronology.INSTANCE.isLeapYear(prolepticYear)) {
                throw new DateTimeException("Invalid date 'St. Tibs Day' as '" + prolepticYear + "' is not a leap year");
            }
        }

        return new DiscordianDate(prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the Discordian proleptic-year
     * @param month  the Discordian month, from 1 to 5
     * @param dayOfMonth  the Discordian day-of-month, from 1 to 73
     */
    private DiscordianDate(int prolepticYear, int month, int dayOfMonth) {
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
        return DiscordianDate.create(prolepticYear, month, day);
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
        // St. Tib's Day isn't part of any month, but would be the 60th day of the year.
        if (month == 0 && day == 0) {
            return ST_TIBS_OFFSET;
        }
        int dayOfYear = (month - 1) * DAYS_IN_MONTH + day;
        // If after St. Tib's day, need to offset to account for it.
        return dayOfYear + (dayOfYear >= ST_TIBS_OFFSET && isLeapYear() ? 1 : 0);
    }

    @Override
    AbstractDate withDayOfYear(int value) {
        return plusDays(value - getDayOfYear());
    }

    @Override
    int lengthOfWeek() {
        return DAYS_IN_WEEK;
    }

    @Override
    int lengthOfYearInMonths() {
        return MONTHS_IN_YEAR;
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        return month == 0 ? ValueRange.of(0, 0) : ValueRange.of(1, 15);
    }

    @Override
    DiscordianDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        return resolvePreviousValid(newYear, newMonth, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                ChronoField f = (ChronoField) field;
                switch (f) {
                    case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                        return month == 0 ? ValueRange.of(0, 0) : ValueRange.of(1, DAYS_IN_WEEK);
                    case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                        return ValueRange.of(isLeapYear() ? 0 : 1, DAYS_IN_WEEK);
                    case ALIGNED_WEEK_OF_YEAR:
                        return ValueRange.of(isLeapYear() ? 0 : 1, WEEKS_IN_YEAR);
                    case DAY_OF_MONTH:
                        return month == 0 ? ValueRange.of(0, 0) : ValueRange.of(1, DAYS_IN_MONTH);
                    case DAY_OF_WEEK:
                        return month == 0 ? ValueRange.of(0, 0) : ValueRange.of(1, DAYS_IN_WEEK);
                    case MONTH_OF_YEAR:
                        return ValueRange.of(isLeapYear() ? 0 : 1, MONTHS_IN_YEAR);
                    default:
                        break;
                }
            }
        }
        return super.range(field);
    }

    //-----------------------------------------------------------------------
    @Override
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                    return month == 0 ? 0 : super.getLong(field);
                case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    return getDayOfWeek();
                case ALIGNED_WEEK_OF_MONTH:
                    return month == 0 ? 0 : super.getLong(field);
                case ALIGNED_WEEK_OF_YEAR:
                    if (month == 0) {
                        return 0;
                    } else {
                        return ((getDayOfYear() - (getDayOfYear() >= ST_TIBS_OFFSET && isLeapYear() ? 1 : 0) - 1) / DAYS_IN_WEEK) + 1;
                    }
                default:
                    break;
            }
        }
        return super.getLong(field);
    }

    @Override
    int getDayOfWeek() {
        if (month == 0) {
            return 0;
        }
        // Need to offset to account for added day.
        int dayOfYear = getDayOfYear() - (getDayOfYear() >= ST_TIBS_OFFSET && isLeapYear() ? 1 : 0);
        return (dayOfYear - 1) % DAYS_IN_WEEK + 1;
    }

    @Override
    long getProlepticMonth() {
        // Consider St. Tib's day to be part of the 1st month for this count.
        return prolepticYear * MONTHS_IN_YEAR + (month == 0 ? 1 : month) - 1;
    }

    long getProlepticWeek() {
        // Consider St. Tib's day to be part of the 12th week for this count.
        return ((long) prolepticYear) * WEEKS_IN_YEAR + (month == 0 ? ST_TIBS_OFFSET / DAYS_IN_WEEK : getLong(ALIGNED_WEEK_OF_YEAR)) - 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the Discordian calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the Discordian chronology, not null
     */
    @Override
    public DiscordianChronology getChronology() {
        return DiscordianChronology.INSTANCE;
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The Discordian calendar system has one era, 'YOLD',
     * defined by {@link DiscordianEra}.
     *
     * @return the era YOLD, not null
     */
    @Override
    public DiscordianEra getEra() {
        return DiscordianEra.YOLD;
    }

    @Override
    public int lengthOfMonth() {
        return month == 0 ? 1 : DAYS_IN_MONTH;
    }

    //-------------------------------------------------------------------------
    @Override
    public DiscordianDate with(TemporalAdjuster adjuster) {
        return (DiscordianDate) adjuster.adjustInto(this);
    }

    @Override
    public DiscordianDate with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            DiscordianChronology.INSTANCE.range(f).checkValidValue(newValue, f);
            int nvalue = (int) newValue;
            // trying to move to St Tibs
            if (nvalue == 0 && isLeapYear()) {
                switch (f) {
                    case DAY_OF_WEEK:
                    case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                    case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    case ALIGNED_WEEK_OF_MONTH:
                    case ALIGNED_WEEK_OF_YEAR:
                    case DAY_OF_MONTH:
                    case MONTH_OF_YEAR:
                        if (month == 0) {
                            return this;
                        }
                        return DiscordianDate.create(prolepticYear, 0, 0);
                    default:
                        break;
                }
            }
            // currently on St Tibs
            if (month == 0) {
                switch (f) {
                    case YEAR:
                    case YEAR_OF_ERA:
                        if (DiscordianChronology.INSTANCE.isLeapYear(nvalue)) {
                            return DiscordianDate.create(nvalue, 0, 0);
                        }
                        // fall through
                    default:
                        return DiscordianDate.create(prolepticYear, 1, 60).with(field, newValue);
                }
            }
            // validate range (generally excluding zero value)
            range(f).checkValidValue(newValue, f);
            switch (f) {
                case DAY_OF_WEEK:
                case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    if (month == 1
                            && day >= ST_TIBS_OFFSET - DAYS_IN_WEEK + 1 && day < ST_TIBS_OFFSET + 1
                            && isLeapYear()) {
                        int currentDayOfWeek = getDayOfWeek();
                        // St. Tib's Day is between the 4th and 5th days of the week...
                        if (currentDayOfWeek < DAYS_IN_WEEK && nvalue == DAYS_IN_WEEK) {
                            return (DiscordianDate) plusDays(nvalue - currentDayOfWeek + 1);
                        } else if (currentDayOfWeek == DAYS_IN_WEEK && nvalue < DAYS_IN_WEEK) {
                            return (DiscordianDate) plusDays(nvalue - currentDayOfWeek - 1);
                        }
                    }
                    break;
                case ALIGNED_WEEK_OF_MONTH:
                case ALIGNED_WEEK_OF_YEAR:
                    if ((month == 1 || field == ALIGNED_WEEK_OF_YEAR) && isLeapYear()) {
                        // St. Tib's Day is in the middle of the 12th week...
                        int alignedWeek = (int) getLong(field);
                        int currentDayOfWeek = getDayOfWeek();
                        if ((alignedWeek > ST_TIBS_OFFSET / DAYS_IN_WEEK || (alignedWeek == ST_TIBS_OFFSET / DAYS_IN_WEEK && currentDayOfWeek == DAYS_IN_WEEK))
                                && (nvalue < ST_TIBS_OFFSET / DAYS_IN_WEEK || (nvalue == ST_TIBS_OFFSET / DAYS_IN_WEEK && currentDayOfWeek < DAYS_IN_WEEK))) {
                            return (DiscordianDate) plusDays((newValue - alignedWeek) * DAYS_IN_WEEK - 1);
                        } else if ((nvalue > ST_TIBS_OFFSET / DAYS_IN_WEEK || (nvalue == ST_TIBS_OFFSET / DAYS_IN_WEEK && currentDayOfWeek == DAYS_IN_WEEK))
                                && (alignedWeek < ST_TIBS_OFFSET / DAYS_IN_WEEK || (alignedWeek == ST_TIBS_OFFSET / DAYS_IN_WEEK && currentDayOfWeek < DAYS_IN_WEEK))) {
                            return (DiscordianDate) plusDays((newValue - alignedWeek) * lengthOfWeek() + 1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return (DiscordianDate) super.with(field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public DiscordianDate plus(TemporalAmount amount) {
        return (DiscordianDate) amount.addTo(this);
    }

    @Override
    public DiscordianDate plus(long amountToAdd, TemporalUnit unit) {
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
        return (DiscordianDate) super.plus(amountToAdd, unit);
    }

    @Override
    DiscordianDate plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long calcEm = Math.addExact(getProlepticMonth(), months);
        int newYear = Math.toIntExact(Math.floorDiv(calcEm, MONTHS_IN_YEAR));
        int newMonth = (int) (Math.floorMod(calcEm, MONTHS_IN_YEAR) + 1);
        // If the starting date was St. Tib's Day, attempt to retain that status.
        if (month == 0 && newMonth == 1) {
            newMonth = 0;
        }
        return resolvePrevious(newYear, newMonth, day);
    }

    @Override
    DiscordianDate plusWeeks(long weeks) {
        if (weeks == 0) {
            return this;
        }
        long calcEm = Math.addExact(getProlepticWeek(), weeks);
        int newYear = Math.toIntExact(Math.floorDiv(calcEm, WEEKS_IN_YEAR));
        // Give St. Tib's Day the same day-of-week as the day after it.
        int newDayOfYear = (int) (Math.floorMod(calcEm, WEEKS_IN_YEAR)) * DAYS_IN_WEEK + (month == 0 ? DAYS_IN_WEEK : get(DAY_OF_WEEK));
        // Need to offset day-of-year if leap year, and not heading to St. Tib's Day again.
        if (DiscordianChronology.INSTANCE.isLeapYear(newYear)
                && (newDayOfYear > ST_TIBS_OFFSET || (newDayOfYear == ST_TIBS_OFFSET && month != 0))) {
            newDayOfYear++;
        }
        return ofYearDay(newYear, newDayOfYear);
    }

    @Override
    public DiscordianDate minus(TemporalAmount amount) {
        return (DiscordianDate) amount.subtractFrom(this);
    }

    @Override
    public DiscordianDate minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override  // for covariant return type
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<DiscordianDate> atTime(LocalTime localTime) {
        return (ChronoLocalDateTime<DiscordianDate>) super.atTime(localTime);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return until(DiscordianDate.from(endExclusive), unit);
    }

    @Override
    long until(AbstractDate end, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case WEEKS:
                    return weeksUntil(DiscordianDate.from(end));
                default:
                    break;
            }
        }
        return super.until(end, unit);
    }

    long weeksUntil(DiscordianDate end) {
        long weekStart = this.getProlepticWeek() * 8L;
        long weekEnd = end.getProlepticWeek() * 8L;
        // Toggle offset for St. Tib's Day based on the direction traveled.
        long packed1 = weekStart + (this.month == 0 && end.month != 0 ? (weekEnd > weekStart ? DAYS_IN_WEEK : DAYS_IN_WEEK - 1) : this.getDayOfWeek());  // no overflow
        long packed2 = weekEnd + (end.month == 0 && this.month != 0 ? (weekStart > weekEnd ? DAYS_IN_WEEK : DAYS_IN_WEEK - 1) : end.getDayOfWeek());  // no overflow
        return (packed2 - packed1) / 8L;
    }

    @Override
    long monthsUntil(AbstractDate end) {
        DiscordianDate discordianEnd = DiscordianDate.from(end);
        long monthStart = this.getProlepticMonth() * 128L;
        long monthEnd = discordianEnd.getProlepticMonth() * 128L;
        // Toggle offset for St. Tib's Day based on the direction traveled.
        long packed1 = monthStart + (this.month == 0 && discordianEnd.month != 0 ? (monthEnd > monthStart ? ST_TIBS_OFFSET : ST_TIBS_OFFSET - 1) : this.getDayOfMonth());  // no overflow
        long packed2 = monthEnd + (discordianEnd.month == 0 && this.month != 0 ? (monthStart > monthEnd ? ST_TIBS_OFFSET : ST_TIBS_OFFSET - 1) : end.getDayOfMonth());  // no overflow
        return (packed2 - packed1) / 128L;
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        long monthsUntil = monthsUntil(DiscordianDate.from(endDateExclusive));

        int years = Math.toIntExact(monthsUntil / MONTHS_IN_YEAR);
        int months = (int) (monthsUntil % MONTHS_IN_YEAR);
        int days = (int) this.plusMonths(monthsUntil).daysUntil(endDateExclusive);

        return DiscordianChronology.INSTANCE.period(years, months, days);
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long year = prolepticYear;
        long discordianEpochDay = ((year - OFFSET_FROM_ISO_0000 - 1) * 365) + getLeapYearsBefore(year) + (getDayOfYear() - 1);
        return discordianEpochDay - DISCORDIAN_1167_TO_ISO_1970;
    }

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append(DiscordianChronology.INSTANCE.toString())
                .append(" ")
                .append(DiscordianEra.YOLD)
                .append(" ")
                .append(prolepticYear);
        if (month == 0) {
            buf.append(" St. Tib's Day");
        } else {
            buf.append("-").append(month)
                    .append(day < 10 ? "-0" : "-").append(day);
        }
        return buf.toString();
    }

}
