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
import static org.threeten.extra.chrono.DiscordianChronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.DiscordianChronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.DiscordianChronology.OFFSET_FROM_ISO_0000;

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
            if (dayOfYear == 60) {
                // Take care of special case of St Tib's Day.
                return new DiscordianDate(prolepticYear, 0, 0);
            } else if (dayOfYear > 60) {
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

        // use of Discordian 1267 makes leap year at end of long cycle
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
                if (DiscordianChronology.INSTANCE.isLeapYear(prolepticYear)) {
                    day = 0;
                    break;
                } else {
                    month = 1;
                }
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                if (day == 0) {
                    day = 60;
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
                throw new DateTimeException("Invalid date '" + month + " " + dayOfMonth + "' as St. Tib's Day is the only special day inserted in a nonexistant month.");
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
            return 60;
        }
        int dayOfYear = (month - 1) * DAYS_IN_MONTH + day;
        // If after St. Tib's day, need to offset to account for it.
        return dayOfYear + (dayOfYear >= 60 && isLeapYear() ? 1 : 0);
    }

    @Override
    AbstractDate withDayOfYear(int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    int lengthOfWeek() {
        return DAYS_IN_WEEK;
    }

    @Override
    int lengthOfYearInMonths() {
        return DAYS_IN_MONTH;
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        return month == 0 ? ValueRange.of(0, 0) : ValueRange.of(1, 15);
    }

    @Override
    AbstractDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        return resolvePreviousValid(newYear, newMonth, dayOfMonth);
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
     * @return the era YOLD
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
        return (DiscordianDate) super.with(field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public DiscordianDate plus(TemporalAmount amount) {
        return (DiscordianDate) amount.addTo(this);
    }

    @Override
    public DiscordianDate plus(long amountToAdd, TemporalUnit unit) {
        return (DiscordianDate) super.plus(amountToAdd, unit);
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
    @Override // for covariant return type
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<DiscordianDate> atTime(LocalTime localTime) {
        return (ChronoLocalDateTime<DiscordianDate>) ChronoLocalDate.super.atTime(localTime);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return super.until(DiscordianDate.from(endExclusive), unit);
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        return super.until(DiscordianDate.from(endDateExclusive));
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long year = prolepticYear;
        long discordianEpochDay = ((year - OFFSET_FROM_ISO_0000 - 1) * 365) + getLeapYearsBefore(year) + (getDayOfYear() - 1);
        return discordianEpochDay - DISCORDIAN_1167_TO_ISO_1970;
    }

}
