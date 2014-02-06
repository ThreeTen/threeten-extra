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
 * A date in the Coptic calendar system.
 * <p>
 * This date operates using the {@linkplain CopticChronology Coptic calendar}.
 * This calendar system is primarily used in Christian Egypt.
 * Dates are aligned such that {@code 0001-01-01 (Coptic)} is {@code 0284-08-29 (ISO)}.
 * <p>
 * NOTE: Treat this class as a value type.
 * Do not synchronize, rely on the identity hash code or use the distinction between equals() and ==.
 *
 * @implSpec
 * This class is immutable and thread-safe.
 */
public final class CopticDate
        implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -7920528871688876868L;
    /**
     * The difference between the ISO and Coptic epoch day count.
     */
    private static final int EPOCH_DAY_DIFFERENCE = 574971 + 40587;

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
     * Obtains the current {@code CopticDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static CopticDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current {@code CopticDate} from the system clock in the specified time-zone.
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
    public static CopticDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current {@code CopticDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static CopticDate now(Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return CopticDate.ofEpochDay(now.toEpochDay());
    }

    /**
     * Obtains a {@code CopticDate} representing a date in the Coptic calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code CopticDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Coptic proleptic-year
     * @param month  the Coptic month-of-year, from 1 to 12
     * @param dayOfMonth  the Coptic day-of-month, from 1 to 31
     * @return the date in Coptic calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static CopticDate of(int prolepticYear, int month, int dayOfMonth) {
        return CopticDate.create(prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a {@code CopticDate} from a temporal object.
     * <p>
     * This obtains a date in the Coptic calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code CopticDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code CopticDate::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in Coptic calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code CopticDate}
     */
    public static CopticDate from(TemporalAccessor temporal) {
        if (temporal instanceof CopticDate) {
            return (CopticDate) temporal;
        }
        return CopticDate.ofEpochDay(temporal.getLong(EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code CopticDate} representing a date in the Coptic calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code CopticDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Coptic proleptic-year
     * @param dayOfYear  the Coptic day-of-year, from 1 to 366
     * @return the date in Coptic calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the month-year
     */
    static CopticDate ofYearDay(int prolepticYear, int dayOfYear) {
        CopticChronology.YEAR_RANGE.checkValidValue(prolepticYear, YEAR);
        DAY_OF_YEAR.range().checkValidValue(dayOfYear, DAY_OF_YEAR);
        if (dayOfYear == 366 && CopticChronology.INSTANCE.isLeapYear(prolepticYear) == false) {
            throw new DateTimeException("Invalid date 'Nasi 6' as '" + prolepticYear + "' is not a leap year");
        }
        return new CopticDate(prolepticYear, (dayOfYear - 1) / 30 + 1, (dayOfYear - 1) % 30 + 1);
    }

    /**
     * Obtains a {@code CopticDate} representing a date in the Coptic calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in Coptic calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static CopticDate ofEpochDay(final long epochDay) {
        EPOCH_DAY.range().checkValidValue(epochDay, EPOCH_DAY);  // validate outer bounds
        long copticED = epochDay + EPOCH_DAY_DIFFERENCE;
        int adjustment = 0;
        if (copticED < 0) {
            copticED = copticED + (1461L * (1_000_000L / 4));
            adjustment = -1_000_000;
        }
        int prolepticYear = (int) (((copticED * 4) + 1463) / 1461);
        int startYearEpochDay = (prolepticYear - 1) * 365 + (prolepticYear / 4);
        int doy0 = (int) (copticED - startYearEpochDay);
        int month = doy0 / 30 + 1;
        int dom = doy0 % 30 + 1;
        return new CopticDate(prolepticYear + adjustment, month, dom);
    }

    private static CopticDate resolvePreviousValid(int prolepticYear, int month, int day) {
        if (month == 13 && day > 5) {
            day = CopticChronology.INSTANCE.isLeapYear(prolepticYear) ? 6 : 5;
        }
        return new CopticDate(prolepticYear, month, day);
    }

    /**
     * Creates a {@code CopticDate} validating the input.
     *
     * @param prolepticYear  the Coptic proleptic-year
     * @param dayOfYear  the Coptic day-of-year, from 1 to 366
     * @return the date in Coptic calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the month-year
     */
    static CopticDate create(int prolepticYear, int month, int dayOfMonth) {
        CopticChronology.YEAR_RANGE.checkValidValue(prolepticYear, YEAR);
        CopticChronology.MOY_RANGE.checkValidValue(month, MONTH_OF_YEAR);
        CopticChronology.DOM_RANGE.checkValidValue(dayOfMonth, DAY_OF_MONTH);
        if (month == 13 && dayOfMonth > 5) {
            if (CopticChronology.INSTANCE.isLeapYear(prolepticYear)) {
                if (dayOfMonth > 6) {
                    throw new DateTimeException("Invalid date 'Nasi " + dayOfMonth + "', valid range from 1 to 5, or 1 to 6 in a leap year");
                }
            } else {
                if (dayOfMonth == 6) {
                    throw new DateTimeException("Invalid date 'Nasi 6' as '" + prolepticYear + "' is not a leap year");
                } else {
                    throw new DateTimeException("Invalid date 'Nasi " + dayOfMonth + "', valid range from 1 to 5, or 1 to 6 in a leap year");
                }
            }
        }
        return new CopticDate(prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the Coptic proleptic-year
     * @param month  the Coptic month, from 1 to 13
     * @param dayOfMonth  the Coptic day-of-month, from 1 to 30
     */
    private CopticDate(int prolepticYear, int month, int dayOfMonth) {
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
        return CopticDate.create(prolepticYear, month, day);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the Coptic calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the Coptic chronology, not null
     */
    @Override
    public CopticChronology getChronology() {
        return CopticChronology.INSTANCE;
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The Coptic calendar system has two eras, 'AM' and 'BEFORE_AM',
     * defined by {@link CopticEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public CopticEra getEra() {
        return (prolepticYear >= 1 ? CopticEra.AM : CopticEra.BEFORE_AM);
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Month lengths match those of the ISO calendar system.
     *
     * @return the length of the month in days
     */
    @Override
    public int lengthOfMonth() {
        if (month == 13) {
            return (isLeapYear() ? 6 : 5);
        }
        return 30;
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
                        return ValueRange.of(1, month == 13 ? 1 : 5);
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
        return (month - 1) * 30 + day;
    }

    private long getProlepticMonth() {
        return prolepticYear * 13L + month - 1;
    }

    private int getYearOfEra() {
        return prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear;
    }

    //-------------------------------------------------------------------------
    @Override
    public CopticDate with(TemporalAdjuster adjuster) {
        return (CopticDate) adjuster.adjustInto(this);
    }

    @Override
    public CopticDate with(TemporalField field, long newValue) {
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
                    return resolvePreviousValid(prolepticYear, ((nvalue - 1) / 30) + 1, ((nvalue - 1) % 30) + 1);
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
    public CopticDate plus(TemporalAmount amount) {
        return (CopticDate) amount.addTo(this);
    }

    @Override
    public CopticDate plus(long amountToAdd, TemporalUnit unit) {
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

    private CopticDate plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = YEAR.checkValidIntValue(Math.addExact(prolepticYear, yearsToAdd));
        return resolvePreviousValid(newYear, month, day);
    }

    private CopticDate plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long curEm = prolepticYear * 13L + (month - 1);
        long calcEm = Math.addExact(curEm, months);
        int newYear = Math.toIntExact(Math.floorDiv(calcEm, 13));
        int newMonth = (int) (Math.floorMod(calcEm, 13) + 1);
        return resolvePreviousValid(newYear, newMonth, day);
    }

    private CopticDate plusDays(long days) {
        if (days == 0) {
            return this;
        }
        return CopticDate.ofEpochDay(Math.addExact(toEpochDay(), days));
    }

    @Override
    public CopticDate minus(TemporalAmount amount) {
        return (CopticDate) amount.subtractFrom(this);
    }

    @Override
    public CopticDate minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override        // for javadoc and covariant return type
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<CopticDate> atTime(LocalTime localTime) {
        return (ChronoLocalDateTime<CopticDate>) ChronoLocalDate.super.atTime(localTime);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        CopticDate end = CopticDate.from(endExclusive);
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case DAYS:
                    return daysUntil(end);
                case WEEKS:
                    return daysUntil(end) / 7;
                case MONTHS:
                    return monthsUntil(end);
                case YEARS:
                    return monthsUntil(end) / 13;
                case DECADES:
                    return monthsUntil(end) / 130;
                case CENTURIES:
                    return monthsUntil(end) / 1300;
                case MILLENNIA:
                    return monthsUntil(end) / 13000;
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

    private long monthsUntil(CopticDate end) {
        long packed1 = getProlepticMonth() * 32L + day;  // no overflow
        long packed2 = end.getProlepticMonth() * 32L + end.day;  // no overflow
        return (packed2 - packed1) / 32;
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        CopticDate end = CopticDate.from(endDateExclusive);
        long totalMonths = end.getProlepticMonth() - this.getProlepticMonth();  // safe
        int days = end.day - this.day;
        if (totalMonths > 0 && days < 0) {
            totalMonths--;
            CopticDate calcDate = this.plusMonths(totalMonths);
            days = (int) (end.toEpochDay() - calcDate.toEpochDay());  // safe
        } else if (totalMonths < 0 && days > 0) {
            totalMonths++;
            days -= end.lengthOfMonth();
        }
        long years = totalMonths / 13;  // safe
        int months = (int) (totalMonths % 13);  // safe
        return getChronology().period(Math.toIntExact(years), months, days);
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long year = (long) prolepticYear;
        long copticEpochDay = ((year - 1) * 365) + Math.floorDiv(year, 4) + (getDayOfYear() - 1);
        return copticEpochDay - EPOCH_DAY_DIFFERENCE;
    }

    //-------------------------------------------------------------------------
    /**
     * Compares this date to another date, including the chronology.
     * <p>
     * Compares this {@code CopticDate} with another ensuring that the date is the same.
     * <p>
     * Only objects of type {@code CopticDate} are compared, other types return false.
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
        if (obj instanceof CopticDate) {
            CopticDate otherDate = (CopticDate) obj;
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
