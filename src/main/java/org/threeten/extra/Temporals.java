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

import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.Period;
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
     * The number of days per year.
     */
    private static final int DAYS_PER_YEAR = 365;
    /**
     * The number of days per 4 year cycle.
     */
    private static final int DAYS_PER_4YEARS = 365 * 4 + 1;
    /**
     * The number of days per 100 year cycle.
     */
    private static final int DAYS_PER_100YEARS = 365 * 100 + 24;
    /**
     * The number of days per 4 year cycle.
     */
    private static final int DAYS_PER_400YEARS = 365 * 400 + 97;

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
     * Normalizes the years and days in a period.
     * <p>
     * This normalizes the years and days, leaving the months unchanged.
     * The result is typically inexact as it makes assumptions around the length of a year.
     * It is often desirable to normalize the years and months first using {@link Period#normalized()}.
     * <p>
     * If the number of days exceeds 146,097, 400 years will be added.
     * This is an exact transformation, as 400 years always contains 146,097 days in the ISO calendar.
     * <p>
     * If the number of days exceeds 36,524, 100 years will be added.
     * This is an inexact transformation, as not all 100 year periods have 36,524 days.
     * <p>
     * If the number of days exceeds 1461, 4 years will be added.
     * This is an inexact transformation, as not all 4 year periods have 1461 days.
     * <p>
     * If the number of days exceeds 365, 1 year will be added.
     * This is an inexact transformation, as while a year normally contains 365 days,
     * it will contain 366 in a leap year.
     * <p>
     * The sign of the years and days will be the same after normalization.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param input  the period to normalize, not null
     * @return a {@code Period} based on this period with excess duration normalized to days, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Period normalizeStandardYears(Period input) {
        int yearsInput = input.getYears();
        long daysInput = input.getDays();
        daysInput += (yearsInput / 400) * DAYS_PER_400YEARS;
        yearsInput = (yearsInput % 400);
        daysInput += (yearsInput / 100) * DAYS_PER_100YEARS;
        yearsInput = (yearsInput % 100);
        daysInput += (yearsInput / 4) * DAYS_PER_4YEARS;
        yearsInput = (yearsInput % 4);
        daysInput += yearsInput * DAYS_PER_YEAR;
        
//        if ((daysInput > 0 && yearsInput < 0) || (daysInput < 0 && yearsInput > 0)) {
//            daysInput += (yearsInput / 400) * DAYS_PER_400YEARS;
//            yearsInput = (yearsInput % 400);
//            daysInput += (yearsInput / 100) * DAYS_PER_100YEARS;
//            yearsInput = (yearsInput % 100);
//            daysInput += (yearsInput / 4) * DAYS_PER_4YEARS;
//            yearsInput = (yearsInput % 4);
//            daysInput += yearsInput * DAYS_PER_YEAR;
//        }
        long extra400 = daysInput / DAYS_PER_400YEARS;
        int days400 = (int) (daysInput % DAYS_PER_400YEARS);
        
        int extra100 = days400 / DAYS_PER_100YEARS;
        int days100 = days400 % DAYS_PER_100YEARS;
        if (extra100 == 4 && days100 == 0) {
            extra100--;
            days100 = DAYS_PER_100YEARS;
        }
        
        int extra4 = days100 / DAYS_PER_4YEARS;
        int days4 = days100 % DAYS_PER_4YEARS;
        
        int extra1 = days4 / DAYS_PER_YEAR;
        int days1 = days4 % DAYS_PER_YEAR;
        if (extra1 == 4) {
            extra1--;
            days1 += DAYS_PER_YEAR;
        }
        
        long combinedYears = ((long) extra400) * 400 + extra100 * 100 + extra4 * 4 + extra1;
        int splitYears = Math.toIntExact(combinedYears);
        if (splitYears == input.getYears() && days1 == input.getDays()) {
            return input;
        }
        return Period.of(splitYears, input.getMonths(), days1);
        
//        // always 146,007 days per 400 years
//        int cycles400 = input.getYears() / 400;
//        int yearsInCycle400 = input.getYears() % 400;
//        long totalDaysBasedOnCycle400 = ((long) cycles400) * DAYS_PER_400YEARS + input.getDays();
//        int splitCycles400 = (int) (totalDaysBasedOnCycle400 / DAYS_PER_400YEARS);
//        int splitDays400 = (int) (totalDaysBasedOnCycle400 % DAYS_PER_400YEARS);
//        
//        // usually 36,524 days per 4 years
//        int cycles100 = yearsInCycle400 / 100;
//        int yearsInCycle100 = yearsInCycle400 % 100;
//        long totalDaysBasedOnCycle100 = cycles100 * DAYS_PER_100YEARS + splitDays400;
//        long splitCycles100 = totalDaysBasedOnCycle100 / DAYS_PER_100YEARS;
//        int splitDays100 = (int) (totalDaysBasedOnCycle100 % DAYS_PER_100YEARS);
//        if (splitCycles100 == 4) {
//            splitCycles100--;
//            splitDays100 += DAYS_PER_100YEARS;
//        }
//        
//        // usually 1461 days per 4 years
//        int cycles4 = yearsInCycle100 / 4;
//        int yearsInCycle4 = yearsInCycle100 % 4;
//        long totalDaysBasedOnCycle4 = cycles4 * DAYS_PER_4YEARS + splitDays100;
//        long splitCycles4 = totalDaysBasedOnCycle4 / DAYS_PER_4YEARS;
//        int splitDays4 = (int) (totalDaysBasedOnCycle4 % DAYS_PER_4YEARS);
//        if (splitCycles4 == 100) {
//            splitCycles4--;
//            splitDays4 += DAYS_PER_4YEARS;
//        }
//
//        long totalDaysBasedOnCycle1 = yearsInCycle4 * DAYS_PER_YEAR + splitDays4;
//        long splitCycles1 = totalDaysBasedOnCycle1 / DAYS_PER_YEAR;
//        int splitDays1 = (int) (totalDaysBasedOnCycle1 % DAYS_PER_YEAR);
//        if (splitCycles1 == 4) {
//            splitCycles1--;
//            splitDays1 += DAYS_PER_YEAR;
//        }
//
//        int splitYears = Math.toIntExact(
//                splitCycles400 * 400 + splitCycles100 * 100 + splitCycles4 * 4 + splitCycles1);
//        if (splitYears == input.getYears() && splitDays1 == input.getDays()) {
//            return input;
//        }
//        return Period.of(splitYears, input.getMonths(), splitDays1);
        
        
//        int cycles100 = yearsInCycle400 / 100;
//        int yearsInCycle100 = yearsInCycle400 % 100;
//        int cycles4 = yearsInCycle100 / 4;
//        int yearsInCycle4 = yearsInCycle100 % 4;
//        long days = ((long) cycles100) * DAYS_PER_100YEARS +
//                    ((long) cycles4) * DAYS_PER_4YEARS +
//                    yearsInCycle4 * 366 + 
//                    splitDays400;
//        int splitYears = (int) (days / 366);
//        int splitDays = (int) (days % 366);
//        return Period.of((int) splitYears + splitCycles400 * 400, input.getMonths(), splitDays);
        
//        int years = yearsInCycle400;
//        int days = splitDays400;
//        while (days >= DAYS_PER_100YEARS) {
//            years = Math.addExact(years, 100);
//            days = days - DAYS_PER_100YEARS;
//        }
//        while (days >= DAYS_PER_4YEARS) {
//            years = Math.addExact(years, 4);
//            days = days - DAYS_PER_4YEARS;
//        }
//        if (days == DAYS_PER_4YEARS - 1) {
//            years = Math.addExact(years, 3);
//            days = 366;
//        } else {
//            while (days >= DAYS_PER_YEAR) {
//                years = Math.incrementExact(years);
//                days = days - DAYS_PER_YEAR;
//            }
//        }
//        return Period.of((int) years + splitCycles400 * 400, input.getMonths(), (int) days);
    }
     
//    private Object foo() {
//        
//        // usually 36,524 days per 4 years
//        int cycles100 = yearsInCycle400 / 100;
//        int yearsInCycle100 = yearsInCycle400 % 100;
//        long totalDaysBasedOnCycle100 = cycles100 * DAYS_PER_100YEARS + splitDays400;
//        long splitCycles100 = totalDaysBasedOnCycle100 / DAYS_PER_100YEARS;
//        int splitDays100 = (int) (totalDaysBasedOnCycle100 % DAYS_PER_100YEARS);
//        
//        // usually 1461 days per 4 years
//        int cycles4 = yearsInCycle100 / 4;
//        int yearsInCycle4 = yearsInCycle100 % 4;
//        long totalDaysBasedOnCycle4 = cycles4 * DAYS_PER_4YEARS + splitDays100;
//        long splitCycles4 = totalDaysBasedOnCycle4 / DAYS_PER_4YEARS;
//        int splitDays4 = (int) (totalDaysBasedOnCycle4 % DAYS_PER_4YEARS);
//
//        // typically 365 days per year
//        long splitCycles1;
//        int splitDays1;
//        if (yearsInCycle4 == 3 && splitDays4 == 365) {
//            splitCycles1 = 3;
//            splitDays1 = 365;
//        } else if (yearsInCycle4 == 3 && splitDays4 == 366) {
//            splitCycles1 = 4;
//            splitDays1 = 0;
//        } else {
//            long totalDaysBasedOnCycle1 = yearsInCycle4 * DAYS_PER_YEAR + splitDays4;
//            splitCycles1 = totalDaysBasedOnCycle1 / DAYS_PER_YEAR;
//            splitDays1 = (int) (totalDaysBasedOnCycle1 % DAYS_PER_YEAR);
//        }
////        long splitCycles1 = 0;
////        int splitDays1 = (int) totalDaysBasedOnCycle1;
////        if (totalDaysBasedOnCycle1 > (365 * 3)) {
////            splitCycles1 = 3;
////            splitDays1 = (int) (totalDaysBasedOnCycle1 % 365);
////        } else if (totalDaysBasedOnCycle1 > (365 * 2)) {
////            splitCycles1 = 2;
////            splitDays1 = (int) (totalDaysBasedOnCycle1 % 365);
////        } else if (totalDaysBasedOnCycle1 > (365)) {
////            splitCycles1 = 1;
////            splitDays1 = (int) (totalDaysBasedOnCycle1 % 365);
////        }
//        
////        long splitCycles1 = Math.max(totalDaysBasedOnCycle1 / DAYS_PER_YEAR, 3);
////        int splitDays1 = (int) (totalDaysBasedOnCycle1 % DAYS_PER_YEAR);
//
////        long splitCycles1 = totalDaysBasedOnCycle1 / DAYS_PER_YEAR;
////        int splitDays1 = (int) (totalDaysBasedOnCycle1 % DAYS_PER_YEAR);
////        if (totalDaysBasedOnCycle1 == DAYS_PER_4YEARS - 1) {
////            splitCycles1--;
////            splitDays1 += DAYS_PER_YEAR;
////        }
//        
////        
////        
////        int years4 = (years400 % 400) / 4;
////        int years1 = (years4 % 4);
//        
////        long totalDays = (input.getYears() / 400) * DAYS_PER_400YEARS + input.getDays();
////        long split400Years = totalDays / DAYS_PER_400YEARS;
////        int splitDays = (int) (totalDays % DAYS_PER_400YEARS);
////        // usually 1463 days per 4 years
////        long split4Years = splitDays / DAYS_PER_4YEARS;
////        splitDays = (int) (splitDays % DAYS_PER_4YEARS);
////        // typically 365 days per year
////        long split1Years = splitDays / DAYS_PER_YEAR;
////        splitDays = (int) (splitDays % DAYS_PER_YEAR);
//        // avoid object creation if no change
//        int splitYears = Math.toIntExact(
//                splitCycles400 * 400 + splitCycles100 * 100 + splitCycles4 * 4 + splitCycles1);
//        if (splitYears == input.getYears() && splitDays1 == input.getDays()) {
//            return input;
//        }
//        return Period.of(splitYears, input.getMonths(), splitDays1);
//    }

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

}
