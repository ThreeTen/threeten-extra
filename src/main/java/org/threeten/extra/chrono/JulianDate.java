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

import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
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
 * A date in the Julian calendar system.
 * <p>
 * This date operates using the {@linkplain JulianChronology Julian calendar}.
 * This calendar system is the forerunner to the modern Gregorian and ISO calendars.
 * The Julian differs from the Gregorian only in terms of the leap year rule.
 * Dates are aligned such that {@code 0001-01-01 (Julian)} is {@code 0000-12-30 (ISO)}.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class JulianDate
        implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -7920528871688876868L;
    /**
     * The difference between the ISO and Julian epoch day count (Julian 0001-01-01 to ISO 1970-01-01).
     */
    private static final int JULIAN_0001_TO_ISO_1970 = 678577 + 40587;  // MJD values
    /**
     * The days per 4 year cycle.
     */
    private static final int DAYS_PER_CYCLE = (365 * 4) + 1;

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
     * Obtains the current {@code JulianDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static JulianDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current {@code JulianDate} from the system clock in the specified time-zone.
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
    public static JulianDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current {@code JulianDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static JulianDate now(Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return JulianDate.ofEpochDay(now.toEpochDay());
    }

    /**
     * Obtains a {@code JulianDate} representing a date in the Julian calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code JulianDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Julian proleptic-year
     * @param month  the Julian month-of-year, from 1 to 12
     * @param dayOfMonth  the Julian day-of-month, from 1 to 31
     * @return the date in Julian calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static JulianDate of(int prolepticYear, int month, int dayOfMonth) {
        return JulianDate.create(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a {@code JulianDate} from a temporal object.
     * <p>
     * This obtains a date in the Julian calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code JulianDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code JulianDate::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in Julian calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code JulianDate}
     */
    public static JulianDate from(TemporalAccessor temporal) {
        if (temporal instanceof JulianDate) {
            return (JulianDate) temporal;
        }
        return JulianDate.ofEpochDay(temporal.getLong(EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code JulianDate} representing a date in the Julian calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code JulianDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Julian proleptic-year
     * @param dayOfYear  the Julian day-of-year, from 1 to 366
     * @return the date in Julian calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static JulianDate ofYearDay(int prolepticYear, int dayOfYear) {
        JulianChronology.YEAR_RANGE.checkValidValue(prolepticYear, YEAR);
        DAY_OF_YEAR.checkValidValue(dayOfYear);
        boolean leap = JulianChronology.INSTANCE.isLeapYear(prolepticYear);
        if (dayOfYear == 366 && leap == false) {
            throw new DateTimeException("Invalid date 'DayOfYear 366' as '" + prolepticYear + "' is not a leap year");
        }
        Month moy = Month.of((dayOfYear - 1) / 31 + 1);
        int monthEnd = moy.firstDayOfYear(leap) + moy.length(leap) - 1;
        if (dayOfYear > monthEnd) {
            moy = moy.plus(1);
        }
        int dom = dayOfYear - moy.firstDayOfYear(leap) + 1;
        return new JulianDate(prolepticYear, moy.getValue(), dom);
    }

    /**
     * Obtains a {@code JulianDate} representing a date in the Julian calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in Julian calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static JulianDate ofEpochDay(final long epochDay) {
        EPOCH_DAY.range().checkValidValue(epochDay, EPOCH_DAY);  // validate outer bounds
        // use of Julian 0001 makes leap year at end of cycle
        long julianEpochDay = epochDay + JULIAN_0001_TO_ISO_1970;
        long cycle = Math.floorDiv(julianEpochDay, DAYS_PER_CYCLE);
        long daysInCycle = Math.floorMod(julianEpochDay, DAYS_PER_CYCLE);
        if (daysInCycle == DAYS_PER_CYCLE - 1) {
            int year = (int) ((cycle * 4 + 3) + 1);
            return ofYearDay(year, 366);
        }
        int year = (int) ((cycle * 4 + daysInCycle / 365) + 1);
        int doy = (int) ((daysInCycle % 365) + 1);
        return ofYearDay(year, doy);
    }

    private static JulianDate resolvePreviousValid(int prolepticYear, int month, int day) {
        switch (month) {
            case 2:
                day = Math.min(day, JulianChronology.INSTANCE.isLeapYear(prolepticYear) ? 29 : 28);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = Math.min(day, 30);
                break;
        }
        return new JulianDate(prolepticYear, month, day);
    }

    /**
     * Creates a {@code JulianDate} validating the input.
     *
     * @param prolepticYear  the Julian proleptic-year
     * @param dayOfYear  the Julian day-of-year, from 1 to 366
     * @return the date in Julian calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the month-year
     */
    static JulianDate create(int prolepticYear, int month, int dayOfMonth) {
        JulianChronology.YEAR_RANGE.checkValidValue(prolepticYear, YEAR);
        MONTH_OF_YEAR.checkValidValue(month);
        DAY_OF_MONTH.checkValidValue(dayOfMonth);
        if (dayOfMonth > 28) {
            int dom = 31;
            switch (month) {
                case 2:
                    dom = (JulianChronology.INSTANCE.isLeapYear(prolepticYear) ? 29 : 28);
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    dom = 30;
                    break;
            }
            if (dayOfMonth > dom) {
                if (dayOfMonth == 29) {
                    throw new DateTimeException("Invalid date 'February 29' as '" + prolepticYear + "' is not a leap year");
                } else {
                    throw new DateTimeException("Invalid date '" + Month.of(month).name() + " " + dayOfMonth + "'");
                }
            }
        }
        return new JulianDate(prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the Julian proleptic-year
     * @param month  the Julian month, from 1 to 12
     * @param dayOfMonth  the Julian day-of-month, from 1 to 31
     */
    private JulianDate(int prolepticYear, int month, int dayOfMonth) {
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
        return JulianDate.create(prolepticYear, month, day);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the Julian calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the Julian chronology, not null
     */
    @Override
    public JulianChronology getChronology() {
        return JulianChronology.INSTANCE;
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The Julian calendar system has two eras, 'AD' and 'BC',
     * defined by {@link JulianEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public JulianEra getEra() {
        return (prolepticYear >= 1 ? JulianEra.AD : JulianEra.BC);
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Month lengths match those of the ISO calendar system.
     *
     * @return the length of the month in days, from 28 to 31
     */
    @Override
    public int lengthOfMonth() {
        switch (month) {
        case 2:
            return (isLeapYear() ? 29 : 28);
        case 4:
        case 6:
        case 9:
        case 11:
            return 30;
        default:
            return 31;
    }
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                ChronoField f = (ChronoField) field;
                switch (f) {
                    case DAY_OF_MONTH:
                        return ValueRange.of(1, lengthOfMonth());
                    case DAY_OF_YEAR:
                        return ValueRange.of(1, lengthOfYear());
                    case ALIGNED_WEEK_OF_MONTH:
                        return ValueRange.of(1, month == 2 && isLeapYear() == false ? 4 : 5);
                    default:
                        break;
                }
                return getChronology().range(f);
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.rangeRefinedBy(this);
    }

    @Override
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case DAY_OF_WEEK:
                    return getDayOfWeek();
                case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                    return ((day - 1) % 7) + 1;
                case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    return ((getDayOfYear() - 1) % 7) + 1;
                case DAY_OF_MONTH:
                    return day;
                case DAY_OF_YEAR:
                    return getDayOfYear();
                case EPOCH_DAY:
                    return toEpochDay();
                case ALIGNED_WEEK_OF_MONTH:
                    return ((day - 1) / 7) + 1;
                case ALIGNED_WEEK_OF_YEAR:
                    return ((getDayOfYear() - 1) / 7) + 1;
                case MONTH_OF_YEAR:
                    return month;
                case PROLEPTIC_MONTH:
                    return getProlepticMonth();
                case YEAR_OF_ERA:
                    return getYearOfEra();
                case YEAR:
                    return prolepticYear;
                case ERA:
                    return (prolepticYear >= 1 ? 1 : 0);
                default:
                    break;
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.getFrom(this);
    }

    private int getDayOfWeek() {
        return (int) (Math.floorMod(toEpochDay() + 3, 7) + 1);
    }

    private int getDayOfYear() {
        return Month.of(month).firstDayOfYear(isLeapYear()) + day - 1;
    }

    private long getProlepticMonth() {
        return prolepticYear * 12L + month - 1;
    }

    private int getYearOfEra() {
        return prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear;
    }

    //-------------------------------------------------------------------------
    @Override
    public JulianDate with(TemporalAdjuster adjuster) {
        return (JulianDate) adjuster.adjustInto(this);
    }

    @Override
    public JulianDate with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            f.checkValidValue(newValue);
            int nvalue = (int) newValue;
            switch (f) {
                case DAY_OF_WEEK:
                    return plusDays(newValue - getDayOfWeek());
                case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                    return plusDays(newValue - getLong(ALIGNED_DAY_OF_WEEK_IN_MONTH));
                case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    return plusDays(newValue - getLong(ALIGNED_DAY_OF_WEEK_IN_YEAR));
                case DAY_OF_MONTH:
                    return resolvePreviousValid(prolepticYear, month, nvalue);
                case DAY_OF_YEAR:
                    return plusDays(newValue - getDayOfYear());
                case EPOCH_DAY:
                    return ofEpochDay(newValue);
                case ALIGNED_WEEK_OF_MONTH:
                    return plusDays((newValue - getLong(ALIGNED_WEEK_OF_MONTH)) * 7);
                case ALIGNED_WEEK_OF_YEAR:
                    return plusDays((newValue - getLong(ALIGNED_WEEK_OF_YEAR)) * 7);
                case MONTH_OF_YEAR:
                    return resolvePreviousValid(prolepticYear, nvalue, day);
                case PROLEPTIC_MONTH:
                    return plusMonths(newValue - getProlepticMonth());
                case YEAR_OF_ERA:
                    return resolvePreviousValid(prolepticYear >= 1 ? nvalue : 1 - nvalue, month, day);
                case YEAR:
                    return resolvePreviousValid(nvalue, month, day);
                case ERA:
                    return resolvePreviousValid(1 - prolepticYear, month, day);
                default:
                    break;
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.adjustInto(this, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public JulianDate plus(TemporalAmount amount) {
        return (JulianDate) amount.addTo(this);
    }

    @Override
    public JulianDate plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;
            switch (f) {
                case DAYS:
                    return plusDays(amountToAdd);
                case WEEKS:
                    return plusDays(Math.multiplyExact(amountToAdd, 7));
                case MONTHS:
                    return plusMonths(amountToAdd);
                case YEARS:
                    return plusYears(amountToAdd);
                case DECADES:
                    return plusYears(Math.multiplyExact(amountToAdd, 10));
                case CENTURIES:
                    return plusYears(Math.multiplyExact(amountToAdd, 100));
                case MILLENNIA:
                    return plusYears(Math.multiplyExact(amountToAdd, 1000));
                case ERAS:
                    return with(ERA, Math.addExact(getLong(ERA), amountToAdd));
                default:
                    break;
            }
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
        return unit.addTo(this, amountToAdd);
    }

    private JulianDate plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = JulianChronology.YEAR_RANGE.checkValidIntValue(Math.addExact(prolepticYear, yearsToAdd), YEAR);
        return resolvePreviousValid(newYear, month, day);
    }

    private JulianDate plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long monthCount = prolepticYear * 12L + (month - 1);
        long calcMonths = monthCount + months;  // safe overflow
        int newYear = JulianChronology.YEAR_RANGE.checkValidIntValue(Math.floorDiv(calcMonths, 12), YEAR);
        int newMonth = (int)Math.floorMod(calcMonths, 12) + 1;
        return resolvePreviousValid(newYear, newMonth, day);
    }

    private JulianDate plusDays(long days) {
        if (days == 0) {
            return this;
        }
        return JulianDate.ofEpochDay(Math.addExact(toEpochDay(), days));
    }

    @Override
    public JulianDate minus(TemporalAmount amount) {
        return (JulianDate) amount.subtractFrom(this);
    }

    @Override
    public JulianDate minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override  // for covariant return type
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<JulianDate> atTime(LocalTime localTime) {
        return (ChronoLocalDateTime<JulianDate>) ChronoLocalDate.super.atTime(localTime);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        JulianDate end = JulianDate.from(endExclusive);
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case DAYS:
                    return daysUntil(end);
                case WEEKS:
                    return daysUntil(end) / 7;
                case MONTHS:
                    return monthsUntil(end);
                case YEARS:
                    return monthsUntil(end) / 12;
                case DECADES:
                    return monthsUntil(end) / 120;
                case CENTURIES:
                    return monthsUntil(end) / 1200;
                case MILLENNIA:
                    return monthsUntil(end) / 12000;
                case ERAS:
                    return end.getLong(ERA) - getLong(ERA);
                default:
                    break;
            }
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
        return unit.between(this, end);
    }

    private long daysUntil(ChronoLocalDate end) {
        return end.toEpochDay() - toEpochDay();  // no overflow
    }

    private long monthsUntil(JulianDate end) {
        long packed1 = getProlepticMonth() * 32L + day;  // no overflow
        long packed2 = end.getProlepticMonth() * 32L + end.day;  // no overflow
        return (packed2 - packed1) / 32;
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        JulianDate end = JulianDate.from(endDateExclusive);
        long totalMonths = end.getProlepticMonth() - this.getProlepticMonth();  // safe
        int days = end.day - this.day;
        if (totalMonths > 0 && days < 0) {
            totalMonths--;
            JulianDate calcDate = this.plusMonths(totalMonths);
            days = (int) (end.toEpochDay() - calcDate.toEpochDay());  // safe
        } else if (totalMonths < 0 && days > 0) {
            totalMonths++;
            days -= end.lengthOfMonth();
        }
        long years = totalMonths / 12;  // safe
        int months = (int) (totalMonths % 12);  // safe
        return getChronology().period(Math.toIntExact(years), months, days);
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long year = (long) prolepticYear;
        long julianEpochDay = ((year - 1) * 365) + Math.floorDiv((year - 1), 4) + (getDayOfYear() - 1);
        return julianEpochDay - JULIAN_0001_TO_ISO_1970;
    }

    //-------------------------------------------------------------------------
    /**
     * Compares this date to another date, including the chronology.
     * <p>
     * Compares this {@code JulianDate} with another ensuring that the date is the same.
     * <p>
     * Only objects of type {@code JulianDate} are compared, other types return false.
     * To compare the dates of two {@code TemporalAccessor} instances, including dates
     * in two different chronologies, use {@link ChronoField#EPOCH_DAY} as a comparator.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date
     */
    @Override  // override for performance
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JulianDate) {
            JulianDate otherDate = (JulianDate) obj;
            return this.prolepticYear == otherDate.prolepticYear &&
                    this.month == otherDate.month && this.day == otherDate.day;
        }
        return false;
    }

    /**
     * A hash code for this date.
     *
     * @return a suitable hash code based only on the Chronology and the date
     */
    @Override  // override for performance
    public int hashCode() {
        return getChronology().getId().hashCode() ^
                ((prolepticYear & 0xFFFFF800) ^ ((prolepticYear << 11) + (month << 6) + (day)));
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append(getChronology().toString())
                .append(" ")
                .append(getEra())
                .append(" ")
                .append(getYearOfEra())
                .append(month < 10 ? "-0" : "-").append(month)
                .append(day < 10 ? "-0" : "-").append(day);
        return buf.toString();
    }

}
