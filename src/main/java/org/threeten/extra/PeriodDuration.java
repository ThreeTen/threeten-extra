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

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.joda.convert.FromString;
import org.joda.convert.ToString;
import org.jspecify.annotations.Nullable;

/**
 * An amount of time in the ISO-8601 calendar system that combines a period and a duration.
 * <p>
 * This class models a quantity or amount of time in terms of a {@code Period} and {@code Duration}.
 * A period is a date-based amount of time, consisting of years, months and days.
 * A duration is a time-based amount of time, consisting of seconds and nanoseconds.
 * See the {@link Period} and {@link Duration} classes for more details.
 * <p>
 * The days in a period take account of daylight saving changes (23 or 25-hour days).
 * When performing calculations, the period is added first, then the duration.
 * <p>
 * The model is of a directed amount, meaning that the amount may be negative.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class PeriodDuration
        implements TemporalAmount, Serializable {

    /**
     * A constant for a duration of zero.
     */
    public static final PeriodDuration ZERO = new PeriodDuration(Period.ZERO, Duration.ZERO);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 8815521625671589L;
    /**
     * The supported units.
     */
    private static final List<TemporalUnit> SUPPORTED_UNITS =
            Collections.unmodifiableList(Arrays.<TemporalUnit>asList(YEARS, MONTHS, DAYS, SECONDS, NANOS));
    /**
     * The number of seconds per day.
     */
    private static final long SECONDS_PER_DAY = 86400;
    /**
     * The number of nanoseconds per second.
     */
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    /**
     * The number of nanoseconds per day.
     */
    private static final long NANOS_PER_DAY = SECONDS_PER_DAY * NANOS_PER_SECOND;

    /**
     * The period.
     */
    private final Period period;
    /**
     * The duration.
     */
    private final Duration duration;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance based on a period and duration.
     * <p>
     * The total amount of time of the resulting instance is the period plus the duration.
     *
     * @param period  the period, not null
     * @param duration  the duration, not null
     * @return the combined period-duration, not null
     */
    public static PeriodDuration of(Period period, Duration duration) {
        Objects.requireNonNull(period, "The period must not be null");
        Objects.requireNonNull(duration, "The duration must not be null");
        return new PeriodDuration(period, duration);
    }

    /**
     * Obtains an instance based on a period.
     * <p>
     * The duration will be zero.
     *
     * @param period  the period, not null
     * @return the combined period-duration, not null
     */
    public static PeriodDuration of(Period period) {
        Objects.requireNonNull(period, "The period must not be null");
        return new PeriodDuration(period, Duration.ZERO);
    }

    /**
     * Obtains an instance based on a duration.
     * <p>
     * The period will be zero.
     *
     * @param duration  the duration, not null
     * @return the combined period-duration, not null
     */
    public static PeriodDuration of(Duration duration) {
        Objects.requireNonNull(duration, "The duration must not be null");
        return new PeriodDuration(Period.ZERO, duration);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance from a temporal amount.
     * <p>
     * This obtains an instance based on the specified amount.
     * A {@code TemporalAmount} represents an amount of time which this factory
     * extracts to a {@code PeriodDuration}.
     * <p>
     * The result is calculated by looping around each unit in the specified amount.
     * Any amount that is zero is ignore.
     * If a unit has an exact duration, it will be totalled using {@link Duration#plus(Duration)}.
     * If the unit is days or weeks, it will be totalled into the days part of the period.
     * If the unit is months or quarters, it will be totalled into the months part of the period.
     * If the unit is years, decades, centuries or millennia, it will be totalled into the years part of the period.
     *
     * @param amount  the temporal amount to convert, not null
     * @return the equivalent duration, not null
     * @throws DateTimeException if unable to convert to a {@code Duration}
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static PeriodDuration from(TemporalAmount amount) {
        if (amount instanceof PeriodDuration) {
            return (PeriodDuration) amount;
        }
        if (amount instanceof Period) {
            return PeriodDuration.of((Period) amount);
        }
        if (amount instanceof Duration) {
            return PeriodDuration.of((Duration) amount);
        }
        if (amount instanceof ChronoPeriod) {
            if (!IsoChronology.INSTANCE.equals(((ChronoPeriod) amount).getChronology())) {
                throw new DateTimeException("Period requires ISO chronology: " + amount);
            }
        }
        Objects.requireNonNull(amount, "amount");
        int years = 0;
        int months = 0;
        int days = 0;
        Duration duration = Duration.ZERO;
        for (TemporalUnit unit : amount.getUnits()) {
            long value = amount.get(unit);
            if (value != 0) {
                // ignore unless non-zero
                if (unit.isDurationEstimated()) {
                    if (unit == ChronoUnit.DAYS) {
                        days = Math.addExact(days, Math.toIntExact(value));
                    } else if (unit == ChronoUnit.WEEKS) {
                        days = Math.addExact(days, Math.toIntExact(Math.multiplyExact(value, 7)));
                    } else if (unit == ChronoUnit.MONTHS) {
                        months = Math.addExact(months, Math.toIntExact(value));
                    } else if (unit == IsoFields.QUARTER_YEARS) {
                        months = Math.addExact(months, Math.toIntExact(Math.multiplyExact(value, 3)));
                    } else if (unit == ChronoUnit.YEARS) {
                        years = Math.addExact(years, Math.toIntExact(value));
                    } else if (unit == ChronoUnit.DECADES) {
                        years = Math.addExact(years, Math.toIntExact(Math.multiplyExact(value, 10)));
                    } else if (unit == ChronoUnit.CENTURIES) {
                        years = Math.addExact(years, Math.toIntExact(Math.multiplyExact(value, 100)));
                    } else if (unit == ChronoUnit.MILLENNIA) {
                        years = Math.addExact(years, Math.toIntExact(Math.multiplyExact(value, 1000)));
                    } else {
                        throw new DateTimeException("Unknown unit: " + unit);
                    }
                } else {
                    // total of exact durations
                    duration = duration.plus(amount.get(unit), unit);
                }
            }
        }
        return PeriodDuration.of(Period.of(years, months, days), duration);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance from a text string such as {@code PnYnMnDTnHnMnS}.
     * <p>
     * This will parse the string produced by {@code toString()} which is
     * based on the ISO-8601 period formats {@code PnYnMnDTnHnMnS} and {@code PnW}.
     * <p>
     * The string starts with an optional sign, denoted by the ASCII negative
     * or positive symbol. If negative, the whole amount is negated.
     * The ASCII letter "P" is next in upper or lower case.
     * There are then a number of sections, each consisting of a number and a suffix.
     * At least one of the sections must be present.
     * The sections have suffixes in ASCII of "Y" for years, "M" for months,
     * "W" for weeks, "D" for days, "H" for hours, "M" for minutes, "S" for seconds,
     * accepted in upper or lower case. Note that the ASCII letter "T" separates
     * the date and time parts and must be present if any time part is present.
     * The suffixes must occur in order.
     * The number part of each section must consist of ASCII digits.
     * The number may be prefixed by the ASCII negative or positive symbol.
     * The number must parse to an {@code int}.
     * Any week-based input is multiplied by 7 and treated as a number of days.
     * <p>
     * The leading plus/minus sign, and negative values for weeks and days are
     * not part of the ISO-8601 standard.
     * <p>
     * Note that the date style format {@code PYYYY-MM-DDTHH:MM:SS} is not supported.
     * <p>
     * For example, the following are valid inputs:
     * <pre>
     *   "P2Y"             -- PeriodDuration.of(Period.ofYears(2))
     *   "P3M"             -- PeriodDuration.of(Period.ofMonths(3))
     *   "P4W"             -- PeriodDuration.of(Period.ofWeeks(4))
     *   "P5D"             -- PeriodDuration.of(Period.ofDays(5))
     *   "PT6H"            -- PeriodDuration.of(Duration.ofHours(6))
     *   "P1Y2M3D"         -- PeriodDuration.of(Period.of(1, 2, 3))
     *   "P1Y2M3W4DT8H"    -- PeriodDuration.of(Period.of(1, 2, 25), Duration.ofHours(8))
     *   "P-1Y2M"          -- PeriodDuration.of(Period.of(-1, 2, 0))
     *   "-P1Y2M"          -- PeriodDuration.of(Period.of(-1, -2, 0))
     * </pre>
     *
     * @param text  the text to parse, not null
     * @return the parsed period, not null
     * @throws DateTimeParseException if the text cannot be parsed to a period
     */
    @FromString
    public static PeriodDuration parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        String upper = text.toString().toUpperCase(Locale.ENGLISH);
        String negate = "";
        if (upper.startsWith("+")) {
            upper = upper.substring(1);
        } else if (upper.startsWith("-")) {
            upper = upper.substring(1);
            negate = "-";
        }
        // duration only, parse original text so it does negation
        if (upper.startsWith("PT")) {
            return PeriodDuration.of(Duration.parse(text));
        }
        // period only, parse original text so it does negation
        int tpos = upper.indexOf('T');
        try {
            if (tpos < 0) {
                return PeriodDuration.of(Period.parse(text));
            }
            // period and duration
            Period period = Period.parse(negate + upper.substring(0, tpos));
            Duration duration = Duration.parse(negate + "P" + upper.substring(tpos));
            return PeriodDuration.of(period, duration);
        } catch (ArithmeticException ex) {
            throw new DateTimeParseException("Text cannot be parsed to a PeriodDuration", text, 0, ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance consisting of the amount of time between two temporals.
     * <p>
     * The start is included, but the end is not.
     * The result can be negative if the end is before the start.
     * <p>
     * The result is logically equivalent to the difference between the start and end.
     * Once any missing information has been defaulted (see below), adding the result to the start
     * yields the end, including when the end is before the start.
     * As such, for backwards periods over a month-end boundary the result may differ from
     * {@link Period#between(LocalDate, LocalDate)}, which does not have this property.
     * <p>
     * The inputs do not need to be the same type, with missing information being defaulted:
     * <ul>
     * <li>An {@link Instant} is treated as being an {@code OffsetDateTime} at UTC.
     * <li>A date is extracted using {@link Temporals#localDateAtStart()}, so a {@code Year} or {@code YearMonth}
     * is treated as being the first day of the year or month.
     * <li>If the time is missing, it is defaulted to midnight.
     * <li>If one date is missing, it is defaulted to the other date.
     * If both dates are missing, the result is a duration only.
     * <li>If one offset is missing, it is defaulted to the other offset, or UTC if both are missing.
     * </ul>
     * <p>
     * If the start has a time-zone, a day may be longer or shorter than 24 hours.
     * For example, if the start is "2012-03-24T12:00+01:00[Europe/Paris]" and the end is
     * "2012-03-27T12:00+02:00[Europe/Paris]", the result is "3 days",
     * even though the elapsed time is only 71 hours due to the daylight saving time change.
     * If the start only has an offset, days are always 24 hours.
     * Any time-zone of the end is ignored, only its offset is used.
     * <p>
     * Only the start determines the time-zone, so swapping the arguments may not give the negated result.
     * <p>
     * The output is adjusted so that the period and duration have the same sign.
     * For example, the value "1 day and -23 hours" is adjusted to "0 days and 1 hour".
     *
     * @param startInclusive  the start, inclusive, not null
     * @param endExclusive  the end, exclusive, not null
     * @return the period-duration between the start and end, not null
     * @throws DateTimeException if the temporals cannot be converted, such as when
     *  the end cannot be represented as an instant relative to the start
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static PeriodDuration between(Temporal startInclusive, Temporal endExclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endExclusive, "endExclusive");

        // instants and offset date-times are treated as zoned date-times, with the zone being the offset
        if (startInclusive instanceof Instant) {
            startInclusive = ((Instant) startInclusive).atZone(ZoneOffset.UTC);
        } else if (startInclusive instanceof OffsetDateTime) {
            startInclusive = ((OffsetDateTime) startInclusive).toZonedDateTime();
        }
        if (endExclusive instanceof Instant) {
            endExclusive = ((Instant) endExclusive).atZone(ZoneOffset.UTC);
        } else if (endExclusive instanceof OffsetDateTime) {
            endExclusive = ((OffsetDateTime) endExclusive).toZonedDateTime();
        }

        // short circuit if both are zoned date-times
        if (startInclusive instanceof ZonedDateTime && endExclusive instanceof ZonedDateTime) {
            return periodZoned((ZonedDateTime) startInclusive, (ZonedDateTime) endExclusive);
        }

        return betweenDefaulted(startInclusive, endExclusive);
    }

    // handles temporals that are not both zoned, defaulting any missing offset, time or date
    private static PeriodDuration betweenDefaulted(Temporal startInclusive, Temporal endExclusive) {
        // default the start offset to the end offset or UTC
        ZoneOffset startOffset = startInclusive.query(TemporalQueries.offset());
        ZoneOffset endOffset = endExclusive.query(TemporalQueries.offset());
        if (startOffset == null) {
            startOffset = endOffset != null ? endOffset : ZoneOffset.UTC;
        }

        // default the time to midnight if missing
        LocalTime startTime = startInclusive.query(TemporalQueries.localTime());
        startTime = startTime != null ? startTime : LocalTime.MIDNIGHT;
        LocalTime endTime = endExclusive.query(TemporalQueries.localTime());
        endTime = endTime != null ? endTime : LocalTime.MIDNIGHT;

        // default the date to the other date
        LocalDate startDate = startInclusive.query(Temporals.localDateAtStart());
        LocalDate endDate = endExclusive.query(Temporals.localDateAtStart());
        if (startDate == null && endDate == null) {
            // no dates, so apply the same date to both and return only a duration
            LocalDate date = LocalDate.of(1970, 1, 1);
            return of(Duration.between(
                    OffsetDateTime.of(date, startTime, startOffset),
                    // a null end offset means the end is local, so it shares the start offset
                    OffsetDateTime.of(date, endTime, endOffset != null ? endOffset : startOffset)));
        } else if (startDate == null) {
            startDate = endDate;
        } else if (endDate == null) {
            endDate = startDate;
        }

        // the start keeps its time-zone if it has one
        ZoneId startZone = startInclusive.query(TemporalQueries.zoneId());
        LocalDateTime startLdt = LocalDateTime.of(startDate, startTime);
        ZonedDateTime start = startZone != null ?
                ZonedDateTime.ofLocal(startLdt, startZone, startOffset) :
                ZonedDateTime.of(startLdt, startOffset);
        // the end is an exact instant if it has an offset (any zone is ignored), otherwise it is local to the start
        LocalDateTime endLdt = LocalDateTime.of(endDate, endTime);
        ZonedDateTime end = endOffset != null ?
                OffsetDateTime.of(endLdt, endOffset).toZonedDateTime() :
                localEnd(start, endLdt);
        return periodZoned(start, end);
    }

    // resolves a local end in the start's zone, using the start offset only if both are in the same overlap
    private static ZonedDateTime localEnd(ZonedDateTime start, LocalDateTime endLdt) {
        ZoneRules rules = start.getZone().getRules();
        ZoneOffsetTransition transition = rules.getTransition(endLdt);
        if (transition != null && transition.isOverlap() &&
                transition.equals(rules.getTransition(start.toLocalDateTime()))) {
            return ZonedDateTime.ofLocal(endLdt, start.getZone(), start.getOffset());
        }
        return ZonedDateTime.of(endLdt, start.getZone());
    }

    // calculates the period-duration that can be added to the start to give the end
    private static PeriodDuration periodZoned(ZonedDateTime startInclusive, ZonedDateTime endExclusive) {
        ZonedDateTime end = endExclusive.withZoneSameInstant(startInclusive.getZone());
        LocalDate startDate = startInclusive.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        // a zone rollback can reverse the date order relative to the instant order,
        // in which case only a duration gives components of the same sign
        if ((end.isAfter(startInclusive) && endDate.isBefore(startDate)) ||
                (end.isBefore(startInclusive) && endDate.isAfter(startDate))) {
            return of(Duration.between(startInclusive, end));
        }
        if (end.isBefore(startInclusive)) {
            // Period.between() does not add back to the start when counting backwards,
            // so use a calculation that truncates towards the start
            Period period = periodBackwards(startDate, endDate);
            ZonedDateTime adjusted = startInclusive.plus(period);
            // the local times may mean the period overshoots the end, so borrow days
            // (more than one is possible if the zone skips a whole day)
            while (adjusted.isBefore(end)) {
                endDate = endDate.plusDays(1);
                period = periodBackwards(startDate, endDate);
                adjusted = startInclusive.plus(period);
            }
            return of(period, Duration.between(adjusted, end));
        }
        Period period = Period.between(startDate, endDate);
        ZonedDateTime adjusted = startInclusive.plus(period);
        // the local times may mean the period overshoots the end, so borrow days
        // (more than one is possible if the zone skips a whole day)
        while (adjusted.isAfter(end)) {
            endDate = endDate.minusDays(1);
            period = Period.between(startDate, endDate);
            adjusted = startInclusive.plus(period);
        }
        return of(period, Duration.between(adjusted, end));
    }

    // calculates a period where adding it to the start date gives the end date, with end before start
    private static Period periodBackwards(LocalDate startDate, LocalDate endDate) {
        long totalMonths = MONTHS.between(startDate, endDate);
        long days = DAYS.between(startDate.plusMonths(totalMonths), endDate);
        return Period.of(Math.toIntExact(totalMonths / 12), (int) (totalMonths % 12), Math.toIntExact(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance.
     *
     * @param period  the period
     * @param duration  the duration
     */
    private PeriodDuration(Period period, Duration duration) {
        this.period = period;
        this.duration = duration;
    }

    /**
     * Resolves singletons.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        Objects.requireNonNull(period, "period");
        Objects.requireNonNull(duration, "duration");
        return PeriodDuration.of(period, duration);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the requested unit.
     * <p>
     * This returns a value for the supported units - {@link ChronoUnit#YEARS},
     * {@link ChronoUnit#MONTHS}, {@link ChronoUnit#DAYS}, {@link ChronoUnit#SECONDS}
     * and {@link ChronoUnit#NANOS}.
     * All other units throw an exception.
     * Note that hours and minutes throw an exception.
     *
     * @param unit  the {@code TemporalUnit} for which to return the value
     * @return the long value of the unit
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     */
    @Override
    public long get(TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case YEARS:
                    return period.getYears();
                case MONTHS:
                    return period.getMonths();
                case DAYS:
                    return period.getDays();
                case SECONDS:
                    return duration.getSeconds();
                case NANOS:
                    return duration.getNano();
                default:
                    break;
            }
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    /**
     * Gets the set of units supported by this amount.
     * <p>
     * This returns the list {@link ChronoUnit#YEARS}, {@link ChronoUnit#MONTHS},
     * {@link ChronoUnit#DAYS}, {@link ChronoUnit#SECONDS} and {@link ChronoUnit#NANOS}.
     * <p>
     * This set can be used in conjunction with {@link #get(TemporalUnit)}
     * to access the entire state of the amount.
     *
     * @return a list containing the days unit, not null
     */
    @Override
    public List<TemporalUnit> getUnits() {
        return SUPPORTED_UNITS;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period part.
     *
     * @return the period part
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Returns a copy of this period-duration with a different period.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the new period
     * @return the updated period-duration
     */
    public PeriodDuration withPeriod(Period period) {
        return PeriodDuration.of(period, duration);
    }

    /**
     * Gets the duration part.
     *
     * @return the duration part
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Returns a copy of this period-duration with a different duration.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the new duration
     * @return the updated period-duration
     */
    public PeriodDuration withDuration(Duration duration) {
        return PeriodDuration.of(period, duration);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if all parts of this amount are zero.
     * <p>
     * This returns true if both {@link Period#isZero()} and {@link Duration#isZero()}
     * return true.
     *
     * @return true if this period is zero-length
     */
    public boolean isZero() {
        return period.isZero() && duration.isZero();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this amount with the specified amount added.
     * <p>
     * The parameter is converted using {@link PeriodDuration#from(TemporalAmount)}.
     * The period and duration are combined separately.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, not null
     * @return a {@code Days} based on this instance with the requested amount added, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public PeriodDuration plus(TemporalAmount amountToAdd) {
        PeriodDuration other = PeriodDuration.from(amountToAdd);
        return of(period.plus(other.period), duration.plus(other.duration));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this amount with the specified amount subtracted.
     * <p>
     * The parameter is converted using {@link PeriodDuration#from(TemporalAmount)}.
     * The period and duration are combined separately.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, not null
     * @return a {@code Days} based on this instance with the requested amount subtracted, not null
     * @throws DateTimeException if the specified amount contains an invalid unit
     * @throws ArithmeticException if numeric overflow occurs
     */
    public PeriodDuration minus(TemporalAmount amountToAdd) {
        PeriodDuration other = PeriodDuration.from(amountToAdd);
        return of(period.minus(other.period), duration.minus(other.duration));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an instance with the amount multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return the amount multiplied by the specified scalar, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public PeriodDuration multipliedBy(int scalar) {
        if (scalar == 1) {
            return this;
        }
        return of(period.multipliedBy(scalar), duration.multipliedBy(scalar));
    }

    /**
     * Returns an instance with the amount negated.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return the negated amount, not null
     * @throws ArithmeticException if numeric overflow occurs, which only happens if
     *  the amount is {@code Long.MIN_VALUE}
     */
    public PeriodDuration negated() {
        return multipliedBy(-1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instance with the years and months exactly normalized.
     * <p>
     * This normalizes the years and months units, leaving the days unit unchanged.
     * The result is exact, always representing the same amount of time.
     * <p>
     * The months unit is adjusted to have an absolute value less than 11,
     * with the years unit being adjusted to compensate. For example, a period of
     * "1 year and 15 months" will be normalized to "2 years and 3 months".
     * <p>
     * The sign of the years and months units will be the same after normalization.
     * For example, a period of "1 year and -25 months" will be normalized to
     * "-1 year and -1 month".
     * <p>
     * Note that no normalization is performed on the days or duration.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code PeriodDuration} based on this one with excess months normalized to years, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public PeriodDuration normalizedYears() {
        return withPeriod(period.normalized());
    }

    /**
     * Returns a copy of this instance with the days and duration normalized using the standard day of 24 hours.
     * <p>
     * This normalizes the days and duration, leaving the years and months unchanged.
     * The result uses a standard day length of 24 hours.
     * <p>
     * This combines the duration seconds with the number of days and shares the total
     * seconds between the two fields. For example, a period of
     * "2 days and 86401 seconds" will be normalized to "3 days and 1 second".
     * <p>
     * The sign of the days and duration will be the same after normalization.
     * For example, a period of "1 day and -172801 seconds" will be normalized to
     * "-1 day and -1 second".
     * <p>
     * Note that no normalization is performed on the years or months.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code PeriodDuration} based on this one with excess duration normalized to days, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public PeriodDuration normalizedStandardDays() {
        long totalSecs = Math.addExact(period.getDays() * SECONDS_PER_DAY, duration.getSeconds());
        long nanos = duration.getNano();
        if (totalSecs < 0 && nanos > 0) {
            // view as negative seconds plus negative nanos so that the signs agree
            totalSecs++;
            nanos -= NANOS_PER_SECOND;
        }
        int splitDays = Math.toIntExact(totalSecs / SECONDS_PER_DAY);
        long splitSecs = totalSecs % SECONDS_PER_DAY;
        Duration splitDuration = Duration.ofSeconds(splitSecs, nanos);
        if (splitDays == period.getDays() && splitDuration.equals(duration)) {
            return this;
        }
        return PeriodDuration.of(period.withDays(splitDays), splitDuration);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds this amount to the specified temporal object.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with this amount added. This simply adds the period and duration to the temporal.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param temporal  the temporal object to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws DateTimeException if unable to add
     * @throws UnsupportedTemporalTypeException if the DAYS unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal addTo(Temporal temporal) {
        return temporal.plus(period).plus(duration);
    }

    /**
     * Subtracts this amount from the specified temporal object.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with this amount subtracted. This simply subtracts the period and duration from the temporal.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param temporal  the temporal object to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws DateTimeException if unable to subtract
     * @throws UnsupportedTemporalTypeException if the DAYS unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public Temporal subtractFrom(Temporal temporal) {
        return temporal.minus(period).minus(duration);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period-duration truncated to the specified unit.
     * <p>
     * This method truncates (rounds towards zero) the duration part of this period-duration
     * to the specified unit. The period part is unaffected.
     * For example, truncating to {@link ChronoUnit#MINUTES MINUTES} will drop the seconds and nanoseconds.
     * See {@link Duration#truncatedTo(TemporalUnit)} for more details.
     * <p>
     * Note that the unit must be a {@linkplain ChronoUnit#isTimeBased() time-based unit}
     * that divides evenly into the length of a standard day.
     * The unit {@link ChronoUnit#DAYS DAYS} may also be used to truncate the duration to whole days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param unit the unit to use for truncation, not null
     * @return a {@code PeriodDuration} based on this one with the duration truncated, not null
     * @throws DateTimeException if the unit is invalid for truncation
     * @since 1.9.0
     */
    public PeriodDuration truncatedTo(TemporalUnit unit) {
        // Duration.truncatedTo(TemporalUnit) was only added in Java 9, so we implement it here
        Objects.requireNonNull(unit, "unit");
        if (unit == ChronoUnit.NANOS) {
            return this;
        }
        Duration unitDuration = unit.getDuration();
        if (unitDuration.getSeconds() > SECONDS_PER_DAY) {
            throw new UnsupportedTemporalTypeException("Unit duration exceeds one day and cannot be used for truncation");
        }
        long unitNanos = unitDuration.toNanos();
        if ((NANOS_PER_DAY % unitNanos) != 0) {
            throw new UnsupportedTemporalTypeException("Unit must divide into a standard day without remainder");
        }
        long seconds = duration.getSeconds();
        long nanos = duration.getNano();
        if (seconds < 0 && nanos > 0) {
            // view as negative seconds plus negative nanos so that truncation is towards zero
            seconds++;
            nanos -= NANOS_PER_SECOND;
        }
        long nod = ((seconds % SECONDS_PER_DAY) * NANOS_PER_SECOND) + nanos;
        long result = (nod / unitNanos) * unitNanos;
        Duration adjusted = duration.plusNanos(result - nod);
        return new PeriodDuration(period, adjusted);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this amount is equal to the specified {@code PeriodDuration}.
     * <p>
     * The comparison is based on the underlying period and duration.
     *
     * @param otherAmount  the other amount, null returns false
     * @return true if the other amount is equal to this one
     */
    @Override
    public boolean equals(@Nullable Object otherAmount) {
        if (this == otherAmount) {
            return true;
        }
        if (otherAmount instanceof PeriodDuration) {
            PeriodDuration other = (PeriodDuration) otherAmount;
            return this.period.equals(other.period) && this.duration.equals(other.duration);
        }
        return false;
    }

    /**
     * A hash code for this amount.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        int h = period.hashCode() * 0x9E3779B1 + duration.hashCode();
        return h ^ (h >>> 15);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the amount.
     * This will be in the format 'PnYnMnDTnHnMnS', with sections omitted as necessary.
     * An empty amount will return "PT0S".
     *
     * @return the period in ISO-8601 string format
     */
    @Override
    @ToString
    public String toString() {
        if (period.isZero()) {
            return duration.toString();
        }
        if (duration.isZero()) {
            return period.toString();
        }
        return period.toString() + duration.toString().substring(1);
    }

}
