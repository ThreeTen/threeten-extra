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
package org.threeten.extra;

import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;
import static java.time.temporal.IsoFields.WEEK_BASED_YEARS;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.FRIDAY;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import static java.time.temporal.IsoFields.WEEK_BASED_YEAR;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

/**
 * A year-week in the ISO week date system such as {@code2015-W01}
 * 
 * ISO 8601 defines the week as always starting with Monday. The first week is 
 * the week which contains the first Thursday of the calendar year. 
 */
public final class YearWeek 
        implements Temporal, TemporalAdjuster, Comparable<YearWeek>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 3381384051871883921L;

    /**
     * Parsing year-week, if day of week is omitted, Monday is assumed
     */
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
                .parseStrict()
                .parseCaseInsensitive()
                .appendValue(IsoFields.WEEK_BASED_YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral("-W")
                .appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2)
                .parseDefaulting(DAY_OF_WEEK, 1)
                .toFormatter();
    
    /**
     * The year.
     */
    private final int year;
    
    /**
     * The week of the year
     */
    private final int week;

    public static YearWeek of(int year, int week) {
        return create(year, week);
    }
    
    public static YearWeek from(TemporalAccessor temporal) {
        if (temporal instanceof YearWeek) {
            return (YearWeek) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            if (IsoChronology.INSTANCE.equals(Chronology.from(temporal)) == false) {
                temporal = LocalDate.from(temporal);
            }
            return of(temporal.get(WEEK_BASED_YEAR), temporal.get(WEEK_OF_WEEK_BASED_YEAR));
        } catch (DateTimeException e) {
            throw new DateTimeException("Unable to obtain YearWeek from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), e);
        }
    }

    public static YearWeek parse(CharSequence text) {
        return parse(text, PARSER);
    }

    public static YearWeek parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.parse(text, YearWeek::from);
    }
    
    private static YearWeek create(int year, int week) {
        WEEK_BASED_YEAR.range().checkValidIntValue(year, WEEK_BASED_YEAR);
        WEEK_OF_WEEK_BASED_YEAR.range().checkValidIntValue(week, WEEK_OF_WEEK_BASED_YEAR);
        if (week == 53) {
            LocalDate firstDayOfYear = Year.of(year).atDay(1);
            LocalDate lastDayOfYear = firstDayOfYear.with(TemporalAdjusters.lastDayOfYear());
            if (IsoChronology.INSTANCE.isLeapYear(year)) {
                if (!((firstDayOfYear.getDayOfWeek() == WEDNESDAY && lastDayOfYear.getDayOfWeek() == THURSDAY)
                        || (firstDayOfYear.getDayOfWeek() == THURSDAY && lastDayOfYear.getDayOfWeek() == FRIDAY))) {
                    throw new DateTimeException("Invalid week '53'. '" + year + "' is a leap year and does not start "
                            + "on a wednesday and end on a thursday or does not start on a thursday and end on a friday" );
                }
            } else {
                if (!(firstDayOfYear.getDayOfWeek() == THURSDAY 
                        && lastDayOfYear.getDayOfWeek() == THURSDAY)) {
                    throw new DateTimeException("Invalid week '53'. '" + year + "' does not start and end on a thursday");
                }
            }
        }
        return new YearWeek(year, week);
    }

    private YearWeek(int year, int week) {
        this.year = year;
        this.week = week;
    }

    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the week field.
     * <p>
     * This method returns the primitive {@code int} value for the week of the year.
     *
     * @return the year
     */
    public int getWeek() {
        return week;
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (field == WEEK_OF_WEEK_BASED_YEAR || field == WEEK_BASED_YEAR) {
            return true;
        } else if (field instanceof ChronoField) {
            return false;
        }
        return field != null && field.isSupportedBy(this);
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        if (unit == WEEK_BASED_YEARS) {
            return true;
        } else if (unit instanceof ChronoUnit) {
            return false;
        }
        return unit != null && unit.isSupportedBy(this);
    }

    @Override
    public ValueRange range(TemporalField field) {
        if (field == WEEK_BASED_YEAR) {
            return WEEK_BASED_YEAR.range();
        }
        if (field == WEEK_OF_WEEK_BASED_YEAR) {
            return WEEK_OF_WEEK_BASED_YEAR.range();
        }
        return Temporal.super.range(field);
    }

    @Override
    public int get(TemporalField field) {
        return range(field).checkValidIntValue(getLong(field), field);
    }

    @Override
    public long getLong(TemporalField field) {
        if (field == WEEK_BASED_YEAR) {
            return year;
        }
        if (field == WEEK_OF_WEEK_BASED_YEAR) {
            return week;
        }
        return field.getFrom(this);
    }

    @Override
    public YearWeek with(TemporalAdjuster adjuster) {
        return (YearWeek) adjuster.adjustInto(this);
    }

    @Override
    public YearWeek with(TemporalField field, long newValue) {
        if (field == WEEK_BASED_YEAR) {
            return withYear((int) newValue);
        }
        if (field == WEEK_OF_WEEK_BASED_YEAR) {
            return withWeek((int) newValue);
        }
        return field.adjustInto(this, newValue);
    }

    public YearWeek withYear(int year) {
        return with(year, week);
    }

    public YearWeek withWeek(int week) {
        return with(year, week);
    }
    
    private YearWeek with(int newYear, int newWeek) {
        if (year == newYear && week == newWeek) {
            return this;
        }
        return create(newWeek, newWeek);
    }

    @Override
    public YearWeek plus(TemporalAmount amountToAdd) {
        return (YearWeek) amountToAdd.addTo(this);
    }

    @Override
    public YearWeek plus(long amountToAdd, TemporalUnit unit) {
        if (unit == WEEK_BASED_YEARS) {
            plusYears((int)amountToAdd);
        }
        if (unit instanceof ChronoUnit) {
            if (unit == WEEKS) {
                return plusWeeks((int)amountToAdd);
            }
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
        return unit.addTo(this, amountToAdd);
    }

    public YearWeek plusYears(long yearsToAdd) { 
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = (int) (year + yearsToAdd);
        return with(newYear, week);
    }

    public YearWeek plusWeeks(long weeksToAdd) {
        if (weeksToAdd == 0) {
            return this;
        }
        // Ok, this does not work yet.  If 'weeksToAdd` > 52
        // we need to check how many weeks are left
        // in the current year and how many weeks are in the following years.
        int newWeek = (int) (week + weeksToAdd);
        return with(year, newWeek);
    }

    @Override
    public YearWeek minus(TemporalAmount amountToSubtract) {
        return (YearWeek) amountToSubtract.subtractFrom(this);
    }

    @Override
    public YearWeek minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    public YearWeek minusYears(long yearsToSubtract) {
        return (yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract));
    }

    public YearWeek minusWeeks(long weeksToSubtract) {
        return (weeksToSubtract == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeksToSubtract));
    }

    @Override
    public Temporal adjustInto(Temporal temporal) {
        if (Chronology.from(temporal).equals(IsoChronology.INSTANCE) == false) {
            throw new DateTimeException("Adjustment only supported on ISO date-time");
        }
        return temporal.with(WEEK_BASED_YEAR, year).with(WEEK_OF_WEEK_BASED_YEAR, week);
       
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
//        YearWeek end = YearWeek.from(endExclusive);
//        if (unit instanceof ChronoUnit) {
//            long monthsUntil = end.getProlepticMonth() - getProlepticMonth();  // no overflow
//            switch ((ChronoUnit) unit) {
//                case MONTHS: return monthsUntil;
//                case YEARS: return monthsUntil / 12;
//                case DECADES: return monthsUntil / 120;
//                case CENTURIES: return monthsUntil / 1200;
//                case MILLENNIA: return monthsUntil / 12000;
//                case ERAS: return end.getLong(ERA) - getLong(ERA);
//            }
//            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
//        }
//        return unit.between(this, end);
        return -1;
    }

    /**
     * Formats this year-week using the specified formatter.
     * <p>
     * This year-week will be passed to the formatter to produce a string.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted year-week string, not null
     * @throws DateTimeException if an error occurs during printing
     */
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year-week to another
     * <p>
     * The comparison is based first on the value of the year, then on the value of the week.
     * It is "consistent with equals", as defined by {@link Comparable}.
     *
     * @param other  the other year-week to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(YearWeek other) {
        int cmp = (year - other.year);
        if (cmp == 0) {
            cmp = (week - other.week);
        }
        return cmp;
    }

    /**
     * Is this year-week after the specified year-week.
     *
     * @param other  the other year-week to compare to, not null
     * @return true if this is after the specified year-week
     */
    public boolean isAfter(YearWeek other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this year-week before the specified year-week.
     *
     * @param other  the other year-week to compare to, not null
     * @return true if this point is before the specified year-week
     */
    public boolean isBefore(YearWeek other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this year-week is equal to another year-week.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other year-quarter
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof YearWeek) {
            YearWeek other = (YearWeek) obj;
            return year == other.year && week == other.week;
        }
        return false;
    }

    /**
     * A hash code for this year-week.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(year, week);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this year-week as a {@code String}, such as {@code 2015-W01}.
     * <p>
     * The output will be in the format {@code YYYY-'W'w}:
     *
     * @return a string representation of this year-quarter, not null
     */
    @Override
    public String toString() {
        return String.format("%d-W%02d", year, week);
    }

}
