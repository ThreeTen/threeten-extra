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
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.YEAR;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

/**
 * A date in one of the Nile river calendar systems.
 *
 * <h3>Implementation Requirements</h3>
 * Implementations must be immutable and thread-safe.
 */
abstract class AbstractNileDate
        implements ChronoLocalDate {

    /**
     * Creates an instance.
     */
    AbstractNileDate() {
    }

    //-----------------------------------------------------------------------
    abstract int getEpochDayDifference();

    abstract int getProlepticYear();

    abstract int getMonth();

    abstract int getDayOfMonth();

    abstract AbstractNileDate resolvePrevious(int newYear, int newMonth, int dayOfMonth);

    AbstractNileDate resolveEpochDay(long epcohDay) {
        return (AbstractNileDate) getChronology().dateEpochDay(epcohDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Months 1 to 12 have 30 days. Month 13 has 5 or 6 days.
     *
     * @return the length of the month in days, from 5 to 30
     */
    @Override
    public int lengthOfMonth() {
        if (getMonth() == 13) {
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
                        return ValueRange.of(1, getMonth() == 13 ? 1 : 5);
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
                    return ((getDayOfMonth() - 1) % 7) + 1;
                case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                    return ((getDayOfYear() - 1) % 7) + 1;
                case DAY_OF_MONTH:
                    return getDayOfMonth();
                case DAY_OF_YEAR:
                    return getDayOfYear();
                case EPOCH_DAY:
                    return toEpochDay();
                case ALIGNED_WEEK_OF_MONTH:
                    return ((getDayOfMonth() - 1) / 7) + 1;
                case ALIGNED_WEEK_OF_YEAR:
                    return ((getDayOfYear() - 1) / 7) + 1;
                case MONTH_OF_YEAR:
                    return getMonth();
                case PROLEPTIC_MONTH:
                    return getProlepticMonth();
                case YEAR_OF_ERA:
                    return getYearOfEra();
                case YEAR:
                    return getProlepticYear();
                case ERA:
                    return (getProlepticYear() >= 1 ? 1 : 0);
                default:
                    break;
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.getFrom(this);
    }

    int getDayOfWeek() {
        return (int) (Math.floorMod(toEpochDay() + 3, 7) + 1);
    }

    private int getDayOfYear() {
        return (getMonth() - 1) * 30 + getDayOfMonth();
    }

    long getProlepticMonth() {
        return getProlepticYear() * 13L + getMonth() - 1;
    }

    private int getYearOfEra() {
        return getProlepticYear() >= 1 ? getProlepticYear() : 1 - getProlepticYear();
    }

    @Override
    public AbstractNileDate with(TemporalField field, long newValue) {
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
                    return resolvePrevious(getProlepticYear(), getMonth(), nvalue);
                case DAY_OF_YEAR:
                    return resolvePrevious(getProlepticYear(), ((nvalue - 1) / 30) + 1, ((nvalue - 1) % 30) + 1);
                case EPOCH_DAY:
                    return resolveEpochDay(newValue);
                case ALIGNED_WEEK_OF_MONTH:
                    return plusDays((newValue - getLong(ALIGNED_WEEK_OF_MONTH)) * 7);
                case ALIGNED_WEEK_OF_YEAR:
                    return plusDays((newValue - getLong(ALIGNED_WEEK_OF_YEAR)) * 7);
                case MONTH_OF_YEAR:
                    return resolvePrevious(getProlepticYear(), nvalue, getDayOfMonth());
                case PROLEPTIC_MONTH:
                    return plusMonths(newValue - getProlepticMonth());
                case YEAR_OF_ERA:
                    return resolvePrevious(getProlepticYear() >= 1 ? nvalue : 1 - nvalue, getMonth(), getDayOfMonth());
                case YEAR:
                    return resolvePrevious(nvalue, getMonth(), getDayOfMonth());
                case ERA:
                    return resolvePrevious(1 - getProlepticYear(), getMonth(), getDayOfMonth());
                default:
                    break;
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.adjustInto(this, newValue);
    }

    @Override
    public AbstractNileDate plus(long amountToAdd, TemporalUnit unit) {
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

    private AbstractNileDate plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = YEAR.checkValidIntValue(Math.addExact(getProlepticYear(), yearsToAdd));
        return resolvePrevious(newYear, getMonth(), getDayOfMonth());
    }

    AbstractNileDate plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long curEm = getProlepticYear() * 13L + (getMonth() - 1);
        long calcEm = Math.addExact(curEm, months);
        int newYear = Math.toIntExact(Math.floorDiv(calcEm, 13));
        int newMonth = (int) (Math.floorMod(calcEm, 13) + 1);
        return resolvePrevious(newYear, newMonth, getDayOfMonth());
    }

    AbstractNileDate plusDays(long days) {
        if (days == 0) {
            return this;
        }
        return resolveEpochDay(Math.addExact(toEpochDay(), days));
    }

    //-------------------------------------------------------------------------
    long until(AbstractNileDate end, TemporalUnit unit) {
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

    private long monthsUntil(AbstractNileDate end) {
        long packed1 = getProlepticMonth() * 32L + getDayOfMonth();  // no overflow
        long packed2 = end.getProlepticMonth() * 32L + end.getDayOfMonth();  // no overflow
        return (packed2 - packed1) / 32;
    }

    ChronoPeriod until(AbstractNileDate end) {
        long totalMonths = end.getProlepticMonth() - this.getProlepticMonth();  // safe
        int days = end.getDayOfMonth() - this.getDayOfMonth();
        if (totalMonths > 0 && days < 0) {
            totalMonths--;
            AbstractNileDate calcDate = this.plusMonths(totalMonths);
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
        long year = (long) getProlepticYear();
        long calendarEpochDay = ((year - 1) * 365) + Math.floorDiv(year, 4) + (getDayOfYear() - 1);
        return calendarEpochDay - getEpochDayDifference();
    }

    //-------------------------------------------------------------------------
    /**
     * Compares this date to another date, including the chronology.
     * <p>
     * Compares this date with another ensuring that the date is the same.
     * <p>
     * Only objects of this concrete type are compared, other types return false.
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
        if (obj != null && this.getClass() == obj.getClass()) {
            AbstractNileDate otherDate = (AbstractNileDate) obj;
            return this.getProlepticYear() == otherDate.getProlepticYear() &&
                    this.getMonth() == otherDate.getMonth() &&
                    this.getDayOfMonth() == otherDate.getDayOfMonth();
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
                ((getProlepticYear() & 0xFFFFF800) ^ ((getProlepticYear() << 11) +
                        (getMonth() << 6) + (getDayOfMonth())));
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append(getChronology().toString())
                .append(" ")
                .append(getEra())
                .append(" ")
                .append(getYearOfEra())
                .append(getMonth() < 10 ? "-0" : "-").append(getMonth())
                .append(getDayOfMonth() < 10 ? "-0" : "-").append(getDayOfMonth());
        return buf.toString();
    }

}
