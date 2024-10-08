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

import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.time.temporal.IsoFields.QUARTER_OF_YEAR;
import static java.time.temporal.IsoFields.QUARTER_YEARS;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Map;

/**
 * Additional Temporal fields.
 * <p>
 * This provides additional fields and units not in the JDK.
 */
public final class TemporalFields {

    /**
     * The field that represents the day-of-half.
     * <p>
     * This field allows the day-of-half value to be queried and set.
     * The day-of-half has values from 1 to 181 in H1 of a standard year, from 1 to 182
     * in H1 of a leap year and from 1 to 184 in H2.
     * <p>
     * The day-of-half can only be calculated if the day-of-year, month-of-year and year
     * are available.
     * <p>
     * When setting this field, the value is allowed to be partially lenient, taking any
     * value from 1 to 184. If the half has less than 184 days, then the day will end up
     * in the following half-year.
     * <p>
     * In the resolving phase of parsing, a date can be created from a year,
     * half-of-year and day-of-half.
     * <p>
     * In {@linkplain ResolverStyle#STRICT strict mode}, all three fields are
     * validated against their range of valid values. The day-of-half field
     * is validated from 1 to 181, 182 or 184 depending on the year and half.
     * <p>
     * In {@linkplain ResolverStyle#SMART smart mode}, all three fields are
     * validated against their range of valid values. The day-of-half field is
     * validated between 1 and 184, ignoring the actual range based on the year and half.
     * If the day-of-half exceeds the actual range, then the resulting date is in the next half-year.
     * <p>
     * In {@linkplain ResolverStyle#LENIENT lenient mode}, only the year is validated
     * against the range of valid values. The resulting date is calculated equivalent to
     * the following three stage approach. First, create a date on the first of January
     * in the requested year. Then take the half-of-year, subtract one, and add the
     * amount in halves to the date. Finally, take the day-of-half, subtract one,
     * and add the amount in days to the date.
     * <p>
     * This unit is an immutable and thread-safe singleton.
     */
    public static final TemporalField DAY_OF_HALF = DayOfHalfField.INSTANCE;
    /**
     * The field that represents the half-of-year.
     * <p>
     * This field allows the half-of-year value to be queried and set.
     * The half-of-year has values from 1 to 2.
     * <p>
     * The half-of-year can only be calculated if the month-of-year is available.
     * <p>
     * In the resolving phase of parsing, a date can be created from a year,
     * half-of-year and day-of-half.
     * See {@link #DAY_OF_HALF} for details.
     * <p>
     * This unit is an immutable and thread-safe singleton.
     */
    public static final TemporalField HALF_OF_YEAR = HalfOfYearField.INSTANCE;
    /**
     * Unit that represents the concept of a half-year.
     * For the ISO calendar system, it is equal to 6 months.
     * The estimated duration of a half-year is one half of {@code 365.2425 Days}.
     * <p>
     * This unit is an immutable and thread-safe singleton.
     */
    public static final TemporalUnit HALF_YEARS = HalfUnit.INSTANCE;

    /**
     * Restricted constructor.
     */
    private TemporalFields() {
    }

    //-------------------------------------------------------------------------
    /**
     * Implementation of day-of-half.
     */
    private static enum DayOfHalfField implements TemporalField {
        INSTANCE;

        private static final ValueRange RANGE = ValueRange.of(1, 181, 184);
        private static final long serialVersionUID = 262362728L;

        //-----------------------------------------------------------------------
        @Override
        public TemporalUnit getBaseUnit() {
            return DAYS;
        }

        @Override
        public TemporalUnit getRangeUnit() {
            return HALF_YEARS;
        }

        @Override
        public boolean isDateBased() {
            return true;
        }

        @Override
        public boolean isTimeBased() {
            return false;
        }

        @Override
        public ValueRange range() {
            return RANGE;
        }

        //-----------------------------------------------------------------------
        @Override
        public boolean isSupportedBy(TemporalAccessor temporal) {
            return temporal.isSupported(DAY_OF_YEAR) &&
                    temporal.isSupported(MONTH_OF_YEAR) &&
                    temporal.isSupported(YEAR);
        }

        @Override
        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            if (!temporal.isSupported(this)) {
                throw new DateTimeException("Unsupported field: DayOfHalf");
            }
            long hoy = temporal.getLong(HALF_OF_YEAR);
            if (hoy == 1) {
                long year = temporal.getLong(YEAR);
                return (IsoChronology.INSTANCE.isLeapYear(year) ? ValueRange.of(1, 182) : ValueRange.of(1, 181));
            } else if (hoy == 2) {
                return ValueRange.of(1, 184);
            } // else value not from 1 to 2, so drop through
            return range();
        }

        @Override
        public long getFrom(TemporalAccessor temporal) {
            if (isSupportedBy(temporal) == false) {
                throw new UnsupportedTemporalTypeException("Unsupported field: DayOfHalf");
            }
            int doy = temporal.get(DAY_OF_YEAR);
            int moy = temporal.get(MONTH_OF_YEAR);
            long year = temporal.getLong(YEAR);
            return moy <= 6 ? doy : doy - 181 - (IsoChronology.INSTANCE.isLeapYear(year) ? 1 : 0);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R adjustInto(R temporal, long newValue) {
            // calls getFrom() to check if supported
            long curValue = getFrom(temporal);
            range().checkValidValue(newValue, this);  // leniently check from 1 to 184
            return (R) temporal.with(DAY_OF_YEAR, temporal.getLong(DAY_OF_YEAR) + (newValue - curValue));
        }

        //-----------------------------------------------------------------------
        @Override
        public ChronoLocalDate resolve(
                Map<TemporalField, Long> fieldValues,
                TemporalAccessor partialTemporal,
                ResolverStyle resolverStyle) {

            Long yearLong = fieldValues.get(YEAR);
            Long hoyLong = fieldValues.get(HALF_OF_YEAR);
            if (yearLong == null || hoyLong == null) {
                return null;
            }
            int y = YEAR.checkValidIntValue(yearLong);  // always validate
            long doh = fieldValues.get(DAY_OF_HALF);
            LocalDate date;
            if (resolverStyle == ResolverStyle.LENIENT) {
                date = LocalDate.of(y, 1, 1).plusMonths(Math.multiplyExact(Math.subtractExact(hoyLong, 1), 6));
                doh = Math.subtractExact(doh, 1);
            } else {
                int qoy = HALF_OF_YEAR.range().checkValidIntValue(hoyLong, HALF_OF_YEAR);  // validated
                date = LocalDate.of(y, ((qoy - 1) * 6) + 1, 1);
                if (doh < 1 || doh > 181) {
                    if (resolverStyle == ResolverStyle.STRICT) {
                        rangeRefinedBy(date).checkValidValue(doh, this);  // only allow exact range
                    } else {  // SMART
                        range().checkValidValue(doh, this);  // allow 1-184 rolling into next quarter
                    }
                }
                doh--;
            }
            fieldValues.remove(this);
            fieldValues.remove(YEAR);
            fieldValues.remove(HALF_OF_YEAR);
            return date.plusDays(doh);
        }

        //-----------------------------------------------------------------------
        @Override
        public String toString() {
            return "DayOfHalf";
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Implementation of half-of-year.
     */
    private static enum HalfOfYearField implements TemporalField {
        INSTANCE;

        private static final long serialVersionUID = -29115701L;

        //-----------------------------------------------------------------------
        @Override
        public TemporalUnit getBaseUnit() {
            return HALF_YEARS;
        }

        @Override
        public TemporalUnit getRangeUnit() {
            return YEARS;
        }

        @Override
        public boolean isDateBased() {
            return true;
        }

        @Override
        public boolean isTimeBased() {
            return false;
        }

        @Override
        public ValueRange range() {
            return ValueRange.of(1, 2);
        }

        @Override
        public boolean isSupportedBy(TemporalAccessor temporal) {
            return temporal.isSupported(QUARTER_OF_YEAR);
        }

        @Override
        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            return range();
        }

        @Override
        public long getFrom(TemporalAccessor temporal) {
            if (isSupportedBy(temporal) == false) {
                throw new UnsupportedTemporalTypeException("Unsupported field: HalfOfYear");
            }
            long qoy = temporal.get(QUARTER_OF_YEAR);
            return qoy <= 2 ? 1 : 2;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R adjustInto(R temporal, long newValue) {
            // calls getFrom() to check if supported
            long curValue = getFrom(temporal);
            range().checkValidValue(newValue, this);  // strictly check from 1 to 2
            return (R) temporal.with(MONTH_OF_YEAR, temporal.getLong(MONTH_OF_YEAR) + (newValue - curValue) * 6);
        }

        @Override
        public String toString() {
            return "HalfOfYear";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the half-year unit.
     */
    private static enum HalfUnit implements TemporalUnit {

        /**
         * Unit that represents the concept of a week-based-year.
         */
        INSTANCE;

        @Override
        public Duration getDuration() {
            return Duration.ofSeconds(31556952L / 2);
        }

        @Override
        public boolean isDurationEstimated() {
            return true;
        }

        @Override
        public boolean isDateBased() {
            return true;
        }

        @Override
        public boolean isTimeBased() {
            return false;
        }

        @Override
        public boolean isSupportedBy(Temporal temporal) {
            return temporal.isSupported(QUARTER_OF_YEAR);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R addTo(R temporal, long amount) {
            return (R) temporal.plus(Math.multiplyExact(amount, 2), QUARTER_YEARS);
        }

        @Override
        public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
            if (temporal1Inclusive.getClass() != temporal2Exclusive.getClass()) {
                return temporal1Inclusive.until(temporal2Exclusive, this);
            }
            return temporal1Inclusive.until(temporal2Exclusive, QUARTER_YEARS) / 2;
        }

        @Override
        public String toString() {
            return "HalfYears";
        }
    }

}
