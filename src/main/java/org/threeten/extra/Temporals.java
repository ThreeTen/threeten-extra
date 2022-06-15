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

import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.ERAS;
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.WEEKS;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Additional utilities for working with temporal classes.
 * <p>
 * This includes:
 * <ul>
 * <li>adjusters that ignore Saturday/Sunday weekends
 * <li>conversion between {@code TimeUnit} and {@code ChronoUnit}
 * <li>converting an amount to another unit
 * </ul>
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
     * Returns an adjuster that returns the next working day or same day if already working day, ignoring Saturday and Sunday.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however an adjuster
     * can be easily written to do so.
     * 
     * @return the next working day or same adjuster, not null
     */
    public static TemporalAdjuster nextWorkingDayOrSame() {
        return Adjuster.NEXT_WORKING_OR_SAME;
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

    /**
     * Returns an adjuster that returns the previous working day or same day if already working day, ignoring Saturday and Sunday.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however an adjuster
     * can be easily written to do so.
     * 
     * @return the previous working day or same adjuster, not null
     */
    public static TemporalAdjuster previousWorkingDayOrSame() {
        return Adjuster.PREVIOUS_WORKING_OR_SAME;
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
        /** Next working day or same adjuster. */
        NEXT_WORKING_OR_SAME {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                int dow = temporal.get(DAY_OF_WEEK);
                switch (dow) {
                    case 6: // Saturday
                        return temporal.plus(2, DAYS);
                    case 7: // Sunday
                        return temporal.plus(1, DAYS);
                    default:
                        return temporal;
                }
            }
        },
        /** Previous working day or same adjuster. */
        PREVIOUS_WORKING_OR_SAME {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                int dow = temporal.get(DAY_OF_WEEK);
                switch (dow) {
                    case 6: //Saturday
                        return temporal.minus(1, DAYS);
                    case 7:  // Sunday
                        return temporal.minus(2, DAYS);
                    default:
                        return temporal;
                }
            }
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Parses the text using one of the formatters.
     * <p>
     * This will try each formatter in turn, attempting to fully parse the specified text.
     * The temporal query is typically a method reference to a {@code from(TemporalAccessor)} method.
     * For example:
     * <pre>
     *  LocalDateTime dt = Temporals.parseFirstMatching(str, LocalDateTime::from, fmt1, fm2, fm3);
     * </pre>
     * If the parse completes without reading the entire length of the text,
     * or a problem occurs during parsing or merging, then an exception is thrown.
     *
     * @param <T> the type of the parsed date-time
     * @param text  the text to parse, not null
     * @param query  the query defining the type to parse to, not null
     * @param formatters  the formatters to try, not null
     * @return the parsed date-time, not null
     * @throws DateTimeParseException if unable to parse the requested result
     */
    public static <T> T parseFirstMatching(CharSequence text, TemporalQuery<T> query, DateTimeFormatter... formatters) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(formatters, "formatters");
        if (formatters.length == 0) {
            throw new DateTimeParseException("No formatters specified", text, 0);
        }
        if (formatters.length == 1) {
            return formatters[0].parse(text, query);
        }
        for (DateTimeFormatter formatter : formatters) {
            try {
                ParsePosition pp = new ParsePosition(0);
                formatter.parseUnresolved(text, pp);
                int len = text.length();
                if (pp.getErrorIndex() == -1 && pp.getIndex() == len) {
                    return formatter.parse(text, query);
                }
            } catch (RuntimeException ex) {
                // should not happen, but ignore if it does
            }
        }
        throw new DateTimeParseException("Text '" + text + "' could not be parsed", text, 0);
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

    //-------------------------------------------------------------------------
    /**
     * Converts a duration to a {@code BigDecimal} with a scale of 9.
     *
     * @param duration  the duration to convert, not null
     * @return the {@code BigDecimal} equivalent of the duration, in seconds with a scale of 9
     */
    public static BigDecimal durationToBigDecimalSeconds(Duration duration) {
        return BigDecimal.valueOf(duration.getSeconds()).add(BigDecimal.valueOf(duration.getNano(), 9));
    }

    /**
     * Converts a {@code BigDecimal} representing seconds to a duration, saturating if necessary.
     * <p>
     * No exception is thrown by this method.
     * Numbers are rounded up to the nearest nanosecond (away from zero).
     * The duration will saturate at the biggest positive or negative {@code Duration}.
     *
     * @param seconds  the number of seconds to convert, positive or negative
     * @return a {@code Duration}, not null
     */
    public static Duration durationFromBigDecimalSeconds(BigDecimal seconds) {
        BigInteger nanos = seconds.setScale(9, RoundingMode.UP).max(BigDecimalSeconds.MIN).min(BigDecimalSeconds.MAX).unscaledValue();
        BigInteger[] secondsNanos = nanos.divideAndRemainder(BigInteger.valueOf(1_000_000_000));
        return Duration.ofSeconds(secondsNanos[0].longValue(), secondsNanos[1].intValue());
    }

    /**
     * Converts a duration to a {@code double}.
     *
     * @param duration  the duration to convert, not null
     * @return the {@code double} equivalent of the duration, in seconds
     */
    public static double durationToDoubleSeconds(Duration duration) {
        if (duration.getSeconds() < 1_000_000_000) {
            return duration.toNanos() / 1_000_000_000d;
        }
        return durationToBigDecimalSeconds(duration).doubleValue();
    }

    /**
     * Converts a {@code double} representing seconds to a duration, saturating if necessary.
     * <p>
     * No exception is thrown by this method.
     * Numbers are rounded up to the nearest nanosecond (away from zero).
     * The duration will saturate at the biggest positive or negative {@code Duration}.
     *
     * @param seconds  the number of seconds to convert, positive or negative
     * @return a {@code Duration}, not null
     */
    public static Duration durationFromDoubleSeconds(double seconds) {
        return durationFromBigDecimalSeconds(BigDecimal.valueOf(seconds));
    }

    /**
     * Multiplies a duration by a {@code double}.
     * <p>
     * The amount is rounded away from zero, thus the result is only zero if zero is passed in.
     * See {@link #durationToBigDecimalSeconds(Duration)} and {@link #durationFromBigDecimalSeconds(BigDecimal)}.
     * Note that due to the rounding up, 1 nanosecond multiplied by any number smaller than 1 will still be 1 nanosecond.
     * 
     * @param duration  the duration to multiply, not null
     * @param multiplicand  the multiplication factor
     * @return the multiplied duration, not null
     */
    public static Duration multiply(Duration duration, double multiplicand) {
        if (multiplicand == 0d || duration.isZero()) {
            return Duration.ZERO;
        }
        if (multiplicand == 1d) {
            return duration;
        }
        BigDecimal amount = durationToBigDecimalSeconds(duration);
        amount = amount.multiply(BigDecimal.valueOf(multiplicand));
        return durationFromBigDecimalSeconds(amount);
    }

    /**
     * Useful Duration constants expressed as BigDecimal seconds with a scale of 9.
     */
    private static final class BigDecimalSeconds {
        public static final BigDecimal MIN = BigDecimal.valueOf(Long.MIN_VALUE).add(BigDecimal.valueOf(0, 9));
        public static final BigDecimal MAX = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(999_999_999, 9));

        private BigDecimalSeconds() {
        }
    }
}
