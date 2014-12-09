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

import org.threeten.extra.instant.Span;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.ERAS;
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.WEEKS;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Additional utilities for working with temporal classes.
 * <p>
 * This currently contains adjusters that ignore Saturday/Sunday weekends.
 *
 * <h3>Implementation Requirements:</h3>
 * This is a thread-safe utility class.
 * All returned classes are immutable and thread-safe.
 */
public final class Temporals {

    /**
     * Restricted constructor.
     */
    private Temporals() {
    }

    //-------------------------------------------------------------------------
    /**
     * Returns an adjuster that returns the next working day, ignoring Saturday and Sunday.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however an adjuster
     * can be easily written to do so.
     *
     * @return the next working day adjuster, not null
     */
    public static TemporalAdjuster nextWorkingDay() {
        return Adjuster.NEXT_WORKING;
    }

    /**
     * Returns an adjuster that returns the previous working day, ignoring Saturday and Sunday.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however an adjuster
     * can be easily written to do so.
     *
     * @return the previous working day adjuster, not null
     */
    public static TemporalAdjuster previousWorkingDay() {
        return Adjuster.PREVIOUS_WORKING;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the adjusters.
     */
    private static enum Adjuster implements TemporalAdjuster {
        /** Next working day adjuster. */
        NEXT_WORKING {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                int dow = temporal.get(DAY_OF_WEEK);
                switch (dow) {
                    case 6:  // Saturday
                        return temporal.plus(2, DAYS);
                    case 5:  // Friday
                        return temporal.plus(3, DAYS);
                    default:
                        return temporal.plus(1, DAYS);
                }
            }
        },
        /** Previous working day adjuster. */
        PREVIOUS_WORKING {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                int dow = temporal.get(DAY_OF_WEEK);
                switch (dow) {
                    case 1:  // Monday
                        return temporal.minus(3, DAYS);
                    case 7:  // Sunday
                        return temporal.minus(2, DAYS);
                    default:
                        return temporal.minus(1, DAYS);
                }
            }
        },
    }

    //-------------------------------------------------------------------------
    /**
     * Converts a {@code TimeUnit} to a {@code ChronoUnit}.
     * <p>
     * This handles the seven units declared in {@code TimeUnit}.
     * 
     * @param unit  the unit to convert, not null
     * @return the converted unit, not null
     */
    public static ChronoUnit chronoUnit(TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        switch (unit) {
            case NANOSECONDS:
                return ChronoUnit.NANOS;
            case MICROSECONDS:
                return ChronoUnit.MICROS;
            case MILLISECONDS:
                return ChronoUnit.MILLIS;
            case SECONDS:
                return ChronoUnit.SECONDS;
            case MINUTES:
                return ChronoUnit.MINUTES;
            case HOURS:
                return ChronoUnit.HOURS;
            case DAYS:
                return ChronoUnit.DAYS;
            default:
                throw new IllegalArgumentException("Unknown TimeUnit constant");
        }
    }

    /**
     * Converts a {@code ChronoUnit} to a {@code TimeUnit}.
     * <p>
     * This handles the seven units declared in {@code TimeUnit}.
     * 
     * @param unit  the unit to convert, not null
     * @return the converted unit, not null
     * @throws IllegalArgumentException if the unit cannot be converted
     */
    public static TimeUnit timeUnit(ChronoUnit unit) {
        Objects.requireNonNull(unit, "unit");
        switch (unit) {
            case NANOS:
                return TimeUnit.NANOSECONDS;
            case MICROS:
                return TimeUnit.MICROSECONDS;
            case MILLIS:
                return TimeUnit.MILLISECONDS;
            case SECONDS:
                return TimeUnit.SECONDS;
            case MINUTES:
                return TimeUnit.MINUTES;
            case HOURS:
                return TimeUnit.HOURS;
            case DAYS:
                return TimeUnit.DAYS;
            default:
                throw new IllegalArgumentException("ChronoUnit cannot be converted to TimeUnit: " + unit);
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Converts an amount from one unit to another.
     * <p>
     * This works on the units in {@code ChronoUnit} and {@code IsoFields}.
     * The {@code DAYS} and {@code WEEKS} units are handled as exact multiple of 24 hours.
     * The {@code ERAS} and {@code FOREVER} units are not supported.
     *
     * @param amount  the input amount in terms of the {@code fromUnit}
     * @param fromUnit  the unit to convert from, not null
     * @param toUnit  the unit to convert to, not null
     * @return the conversion array,
     *  element 0 is the signed whole number,
     *  element 1 is the signed remainder in terms of the input unit,
     *  not null
     * @throws DateTimeException if the units cannot be converted
     * @throws UnsupportedTemporalTypeException if the units are not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static long[] convertAmount(long amount, TemporalUnit fromUnit, TemporalUnit toUnit) {
        Objects.requireNonNull(fromUnit, "fromUnit");
        Objects.requireNonNull(toUnit, "toUnit");
        validateUnit(fromUnit);
        validateUnit(toUnit);
        if (fromUnit.equals(toUnit)) {
            return new long[] {amount, 0};
        }
        // precise-based
        if (isPrecise(fromUnit) && isPrecise(toUnit)) {
            long fromNanos = fromUnit.getDuration().toNanos();
            long toNanos = toUnit.getDuration().toNanos();
            if (fromNanos > toNanos) {
                long multiple = fromNanos / toNanos;
                return new long[] {Math.multiplyExact(amount, multiple), 0};
            } else {
                long multiple = toNanos / fromNanos;
                return new long[] {amount / multiple, amount % multiple};
            }
        }
        // month-based
        int fromMonthFactor = monthMonthFactor(fromUnit, fromUnit, toUnit);
        int toMonthFactor = monthMonthFactor(toUnit, fromUnit, toUnit);
        if (fromMonthFactor > toMonthFactor) {
            long multiple = fromMonthFactor / toMonthFactor;
            return new long[] {Math.multiplyExact(amount, multiple), 0};
        } else {
            long multiple = toMonthFactor / fromMonthFactor;
            return new long[] {amount / multiple, amount % multiple};
        }
    }

    private static void validateUnit(TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            if (unit.equals(ERAS) || unit.equals(FOREVER)) {
                throw new UnsupportedTemporalTypeException("Unsupported TemporalUnit: " + unit);
            }
        } else if (unit.equals(IsoFields.QUARTER_YEARS) == false) {
            throw new UnsupportedTemporalTypeException("Unsupported TemporalUnit: " + unit);
        }
    }

    private static boolean isPrecise(TemporalUnit unit) {
        return unit instanceof ChronoUnit && ((ChronoUnit) unit).compareTo(WEEKS) <= 0;
    }

    private static int monthMonthFactor(TemporalUnit unit, TemporalUnit fromUnit, TemporalUnit toUnit) {
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case MONTHS:
                    return 1;
                case YEARS:
                    return 12;
                case DECADES:
                    return 120;
                case CENTURIES:
                    return 1200;
                case MILLENNIA:
                    return 12000;
                default:
                    throw new DateTimeException(
                            String.format("Unable to convert between units: %s to %s", fromUnit, toUnit));
            }
        }
        return 3;  // quarters
    }

    /**
     * Creates a {@link Span} between two {@link Instant} values
     * @param start
     * @param stop
     * @return
     */
    public static Span span(Instant start, Instant stop) {
        return Span.between(start, stop);
    }
}
