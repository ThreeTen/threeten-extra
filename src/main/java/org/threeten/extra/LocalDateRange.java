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

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjuster;
import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * A range of local dates.
 * <p>
 * A {@code LocalDateRange} represents a range of dates, from a start date to an end date.
 * Instances can be constructed from either a half-open or a closed range of dates.
 * Internally, the class stores the start and end dates, with the start inclusive and the end exclusive.
 * The end date is always greater than or equal to the start date.
 * <p>
 * The constants {@code LocalDate.MIN} and {@code LocalDate.MAX} can be used
 * to indicate an unbounded far-past or far-future. Note that there is no difference
 * between a half-open and a closed range when the end is {@code LocalDate.MAX}.
 * Empty ranges are allowed.
 * <p>
 * No range can end at {@code LocalDate.MIN} or {@code LocalDate.MIN.plusDays(1)}.
 * No range can start at {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)}.
 * No empty range can exist at {@code LocalDate.MIN} or {@code LocalDate.MAX}.
 * <p>
 * Date ranges are not comparable. To compare the length of two ranges, it is
 * generally recommended to compare the number of days they contain.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class LocalDateRange
        implements Serializable {

    /**
     * The day after the MIN date.
     */
    private static final LocalDate MINP1 = LocalDate.MIN.plusDays(1);
    /**
     * The day before the MAX date.
     */
    private static final LocalDate MAXM1 = LocalDate.MAX.minusDays(1);
    /**
     * A range over the whole time-line.
     */
    public static final LocalDateRange ALL = new LocalDateRange(LocalDate.MIN, LocalDate.MAX);

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 3358656715467L;

    /**
     * The start date (inclusive).
     */
    private final LocalDate start;
    /**
     * The end date (exclusive).
     */
    private final LocalDate end;

    //-----------------------------------------------------------------------
    /**
     * Obtains a half-open range of dates, including the start and excluding the end.
     * <p>
     * The range includes the start date and excludes the end date, unless the end is {@code LocalDate.MAX}.
     * The end date must be equal to or after the start date.
     * This definition permits an empty range located at a specific date.
     * <p>
     * The constants {@code LocalDate.MIN} and {@code LocalDate.MAX} can be used
     * to indicate an unbounded far-past or far-future.
     * <p>
     * The start inclusive date must not be {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)}.
     * The end inclusive date must not be {@code LocalDate.MIN} or {@code LocalDate.MIN.plusDays(1)}.
     * No empty range can exist at {@code LocalDate.MIN} or {@code LocalDate.MAX}.
     *
     * @param startInclusive  the inclusive start date, not null
     * @param endExclusive  the exclusive end date, not null
     * @return the half-open range, not null
     * @throws DateTimeException if the end is before the start,
     *   or the start date is {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)},
     *   or the end date is {@code LocalDate.MIN} or {@code LocalDate.MIN.plusDays(1)}
     */
    public static LocalDateRange of(LocalDate startInclusive, LocalDate endExclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endExclusive, "endExclusive");
        return new LocalDateRange(startInclusive, endExclusive);
    }

    /**
     * Obtains a closed range of dates, including the start and end.
     * <p>
     * The range includes the start date and the end date.
     * The end date must be equal to or after the start date.
     * <p>
     * The constants {@code LocalDate.MIN} and {@code LocalDate.MAX} can be used
     * to indicate an unbounded far-past or far-future. In addition, an end date of
     * {@code LocalDate.MAX.minusDays(1)} will also create an unbounded far-future range.
     * <p>
     * The start inclusive date must not be {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)}.
     * The end inclusive date must not be {@code LocalDate.MIN}.
     * 
     * @param startInclusive  the inclusive start date, not null
     * @param endInclusive  the inclusive end date, not null
     * @return the closed range
     * @throws DateTimeException if the end is before the start,
     *   or the start date is {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)},
     *   or the end date is {@code LocalDate.MIN}
     */
    public static LocalDateRange ofClosed(LocalDate startInclusive, LocalDate endInclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endInclusive, "endInclusive");
        if (endInclusive.isBefore(startInclusive)) {
            throw new DateTimeException("Start date must be on or before end date");
        }
        LocalDate end = (endInclusive.equals(LocalDate.MAX) ? LocalDate.MAX : endInclusive.plusDays(1));
        return new LocalDateRange(startInclusive, end);
    }

    /**
     * Obtains an instance of {@code LocalDateRange} from the start and a period.
     * <p>
     * The end date is calculated as the start plus the duration.
     * The period must not be negative.
     * <p>
     * The constant {@code LocalDate.MIN} can be used to indicate an unbounded far-past.
     * <p>
     * The period must not be zero or one day when the start date is {@code LocalDate.MIN}.
     *
     * @param startInclusive  the inclusive start date, not null
     * @param period  the period from the start to the end, not null
     * @return the range, not null
     * @throws DateTimeException if the end is before the start,
     *  or if the period addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs when adding the period
     */
    public static LocalDateRange of(LocalDate startInclusive, Period period) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(period, "period");
        if (period.isNegative()) {
            throw new DateTimeException("Period must not be zero or negative");
        }
        return new LocalDateRange(startInclusive, startInclusive.plus(period));
    }

    /**
     * Obtains an empty date range located at the specified date.
     * <p>
     * The empty range has zero length and contains no other dates or ranges.
     * An empty range cannot be located at {@code LocalDate.MIN}, {@code LocalDate.MIN.plusDays(1)},
     * {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)}.
     *
     * @param date  the date where the empty range is located, not null
     * @return the empty range, not null
     * @throws DateTimeException if the date is {@code LocalDate.MIN}, {@code LocalDate.MIN.plusDays(1)},
     *   {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)}
     */
    public static LocalDateRange ofEmpty(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return new LocalDateRange(date, date);
    }

    /**
     * Obtains a range that is unbounded at the start and end.
     * 
     * @return the range, with an unbounded start and unbounded end
     */
    public static LocalDateRange ofUnbounded() {
        return ALL;
    }

    /**
     * Obtains a range up to, but not including, the specified end date.
     * <p>
     * The range includes all dates from the unbounded start, denoted by {@code LocalDate.MIN}, to the end date.
     * The end date is exclusive and cannot be {@code LocalDate.MIN} or {@code LocalDate.MIN.plusDays(1)}.
     * 
     * @param endExclusive  the exclusive end date, {@code LocalDate.MAX} treated as unbounded, not null
     * @return the range, with an unbounded start
     * @throws DateTimeException if the end date is {@code LocalDate.MIN} or  {@code LocalDate.MIN.plusDays(1)}
     */
    public static LocalDateRange ofUnboundedStart(LocalDate endExclusive) {
        return LocalDateRange.of(LocalDate.MIN, endExclusive);
    }

    /**
     * Obtains a range from and including the specified start date.
     * <p>
     * The range includes all dates from the start date to the unbounded end, denoted by {@code LocalDate.MAX}.
     * The start date is inclusive and cannot be {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)}.
     * 
     * @param startInclusive  the inclusive start date, {@code LocalDate.MIN} treated as unbounded, not null
     * @return the range, with an unbounded end
     * @throws DateTimeException if the start date is {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)}
     */
    public static LocalDateRange ofUnboundedEnd(LocalDate startInclusive) {
        return LocalDateRange.of(startInclusive, LocalDate.MAX);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDateRange} from a text string such as
     * {@code 2007-12-03/2007-12-04}, where the end date is exclusive.
     * <p>
     * The string must consist of one of the following three formats:
     * <ul>
     * <li>a representations of an {@link LocalDate}, followed by a forward slash,
     *  followed by a representation of a {@link LocalDate}
     * <li>a representation of an {@link LocalDate}, followed by a forward slash,
     *  followed by a representation of a {@link Period}
     * <li>a representation of a {@link Period}, followed by a forward slash,
     *  followed by a representation of an {@link LocalDate}
     * </ul>
     *
     * @param text  the text to parse, not null
     * @return the parsed range, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    @FromString
    public static LocalDateRange parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '/') {
                char firstChar = text.charAt(0);
                if (firstChar == 'P' || firstChar == 'p') {
                    // period followed by date
                    Period duration = Period.parse(text.subSequence(0, i));
                    LocalDate end = LocalDate.parse(text.subSequence(i + 1, text.length()));
                    return LocalDateRange.of(end.minus(duration), end);
                } else {
                    // date followed by date or period
                    LocalDate start = LocalDate.parse(text.subSequence(0, i));
                    if (i + 1 < text.length()) {
                        char c = text.charAt(i + 1);
                        if (c == 'P' || c == 'p') {
                            Period duration = Period.parse(text.subSequence(i + 1, text.length()));
                            return LocalDateRange.of(start, start.plus(duration));
                        }
                    }
                    LocalDate end = LocalDate.parse(text.subSequence(i + 1, text.length()));
                    return LocalDateRange.of(start, end);
                }
            }
        }
        throw new DateTimeParseException("LocalDateRange cannot be parsed, no forward slash found", text, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param startInclusive  the start date, inclusive, validated not null
     * @param endExclusive  the end date, exclusive, validated not null
     */
    private LocalDateRange(LocalDate startInclusive, LocalDate endExclusive) {
        if (endExclusive.isBefore(startInclusive)) {
            throw new DateTimeException("End date must be on or after start date");
        }
        if (startInclusive.equals(MAXM1)) {
            throw new DateTimeException("Range must not start at LocalDate.MAX.minusDays(1)");
        }
        if (endExclusive.equals(MINP1)) {
            throw new DateTimeException("Range must not end at LocalDate.MIN.plusDays(1)");
        }
        if (endExclusive.equals(LocalDate.MIN) || startInclusive.equals(LocalDate.MAX)) {
            throw new DateTimeException("Empty range must not be at LocalDate.MIN or LocalDate.MAX");
        }
        this.start = startInclusive;
        this.end = endExclusive;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the start date of this range, inclusive.
     * <p>
     * This will return {@code LocalDate#MIN} if the range is unbounded at the start.
     * In this case, the range includes all dates into the far-past.
     * <p>
     * This never returns {@code LocalDate.MAX} or {@code LocalDate.MAX.minusDays(1)}.
     *
     * @return the start date
     */
    public LocalDate getStart() {
        return start;
    }

    /**
     * Gets the end date of this range, exclusive.
     * <p>
     * This will return {@code LocalDate.MAX} if the range is unbounded at the end.
     * In this case, the range includes all dates into the far-future.
     * <p>
     * This never returns {@code LocalDate.MIN} or {@code LocalDate.MIN.plusDays(1)}.
     *
     * @return the end date, exclusive
     */
    public LocalDate getEnd() {
        return end;
    }

    /**
     * Gets the end date of this range, inclusive.
     * <p>
     * This will return {@code LocalDate.MAX} if the range is unbounded at the end.
     * In this case, the range includes all dates into the far-future.
     * <p>
     * This returns the date before the end date.
     * <p>
     * This never returns {@code LocalDate.MIN}.
     * 
     * @return the end date, inclusive
     */
    public LocalDate getEndInclusive() {
        if (isUnboundedEnd()) {
            return LocalDate.MAX;
        }
        return end.minusDays(1);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the range is empty.
     * <p>
     * An empty range occurs when the start date equals the end date.
     * <p>
     * An empty range is never unbounded.
     * 
     * @return true if the range is empty
     */
    public boolean isEmpty() {
        return start.equals(end);
    }

    /**
     * Checks if the start of the range is unbounded.
     * <p>
     * An unbounded range is never empty.
     * 
     * @return true if start is unbounded
     */
    public boolean isUnboundedStart() {
        return start.equals(LocalDate.MIN);
    }

    /**
     * Checks if the end of the range is unbounded.
     * <p>
     * An unbounded range is never empty.
     * 
     * @return true if end is unbounded
     */
    public boolean isUnboundedEnd() {
        return end.equals(LocalDate.MAX);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this range with the start date adjusted.
     * <p>
     * This returns a new instance with the start date altered.
     * Since {@code LocalDate} implements {@code TemporalAdjuster} any
     * local date can simply be passed in.
     * <p>
     * For example, to adjust the start to one week earlier:
     * <pre>
     *  range = range.withStart(date -&gt; date.minus(1, ChronoUnit.WEEKS));
     * </pre>
     * 
     * @param adjuster  the adjuster to use, not null
     * @return a copy of this range with the start date adjusted
     * @throws DateTimeException if the new start date is after the current end date
     */
    public LocalDateRange withStart(TemporalAdjuster adjuster) {
        return LocalDateRange.of(start.with(adjuster), end);
    }

    /**
     * Returns a copy of this range with the end date adjusted.
     * <p>
     * This returns a new instance with the exclusive end date altered.
     * Since {@code LocalDate} implements {@code TemporalAdjuster} any
     * local date can simply be passed in.
     * <p>
     * For example, to adjust the end to one week later:
     * <pre>
     *  range = range.withEnd(date -&gt; date.plus(1, ChronoUnit.WEEKS));
     * </pre>
     * 
     * @param adjuster  the adjuster to use, not null
     * @return a copy of this range with the end date adjusted
     * @throws DateTimeException if the new end date is before the current start date
     */
    public LocalDateRange withEnd(TemporalAdjuster adjuster) {
        return LocalDateRange.of(start, end.with(adjuster));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this range contains the specified date.
     * <p>
     * This checks if the specified date is within the bounds of this range.
     * If this range is empty then this method always returns false.
     * Else if this range has an unbounded start then {@code contains(LocalDate#MIN)} returns true.
     * Else if this range has an unbounded end then {@code contains(LocalDate#MAX)} returns true.
     * 
     * @param date  the date to check for, not null
     * @return true if this range contains the date
     */
    public boolean contains(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return start.compareTo(date) <= 0 && (date.compareTo(end) < 0 || isUnboundedEnd());
    }

    /**
     * Checks if this range encloses the specified range.
     * <p>
     * This checks if the bounds of the specified range are within the bounds of this range.
     * An empty range encloses itself.
     * 
     * @param other  the other range to check for, not null
     * @return true if this range contains all dates in the other range
     */
    public boolean encloses(LocalDateRange other) {
        Objects.requireNonNull(other, "other");
        return start.compareTo(other.start) <= 0 && other.end.compareTo(end) <= 0;
    }

    /**
     * Checks if this range abuts the specified range.
     * <p>
     * The result is true if the end of this range is the start of the other, or vice versa.
     * An empty range does not abut itself.
     *
     * @param other  the other range, not null
     * @return true if this range abuts the other range
     */
    public boolean abuts(LocalDateRange other) {
        Objects.requireNonNull(other, "other");
        return end.equals(other.start) ^ start.equals(other.end);
    }

    /**
     * Checks if this range is connected to the specified range.
     * <p>
     * The result is true if the two ranges have an enclosed range in common, even if that range is empty.
     * An empty range is connected to itself.
     * <p>
     * This is equivalent to {@code (overlaps(other) || abuts(other))}.
     *
     * @param other  the other range, not null
     * @return true if this range is connected to the other range
     */
    public boolean isConnected(LocalDateRange other) {
        Objects.requireNonNull(other, "other");
        return this.equals(other) || (start.compareTo(other.end) <= 0 && other.start.compareTo(end) <= 0);
    }

    /**
     * Checks if this range overlaps the specified range.
     * <p>
     * The result is true if the two ranges share some part of the time-line.
     * An empty range overlaps itself.
     * <p>
     * This is equivalent to {@code (isConnected(other) && !abuts(other))}.
     *
     * @param other  the time range to compare to, null means a zero length range now
     * @return true if the time ranges overlap
     */
    public boolean overlaps(LocalDateRange other) {
        Objects.requireNonNull(other, "other");
        return other.equals(this) || (start.compareTo(other.end) < 0 && other.start.compareTo(end) < 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the range that is the intersection of this range and the specified range.
     * <p>
     * This finds the intersection of two ranges.
     * This throws an exception if the two ranges are not {@linkplain #isConnected(LocalDateRange) connected}.
     * 
     * @param other  the other range to check for, not null
     * @return the range that is the intersection of the two ranges
     * @throws DateTimeException if the ranges do not connect
     */
    public LocalDateRange intersection(LocalDateRange other) {
        Objects.requireNonNull(other, "other");
        if (isConnected(other) == false) {
            throw new DateTimeException("Ranges do not connect: " + this + " and " + other);
        }
        int cmpStart = start.compareTo(other.start);
        int cmpEnd = end.compareTo(other.end);
        if (cmpStart >= 0 && cmpEnd <= 0) {
            return this;
        } else if (cmpStart <= 0 && cmpEnd >= 0) {
            return other;
        } else {
            LocalDate newStart = (cmpStart >= 0 ? start : other.start);
            LocalDate newEnd = (cmpEnd <= 0 ? end : other.end);
            return LocalDateRange.of(newStart, newEnd);
        }
    }

    /**
     * Calculates the range that is the union of this range and the specified range.
     * <p>
     * This finds the union of two ranges.
     * This throws an exception if the two ranges are not {@linkplain #isConnected(LocalDateRange) connected}.
     * 
     * @param other  the other range to check for, not null
     * @return the range that is the union of the two ranges
     * @throws DateTimeException if the ranges do not connect
     */
    public LocalDateRange union(LocalDateRange other) {
        Objects.requireNonNull(other, "other");
        if (isConnected(other) == false) {
            throw new DateTimeException("Ranges do not connect: " + this + " and " + other);
        }
        int cmpStart = start.compareTo(other.start);
        int cmpEnd = end.compareTo(other.end);
        if (cmpStart >= 0 && cmpEnd <= 0) {
            return other;
        } else if (cmpStart <= 0 && cmpEnd >= 0) {
            return this;
        } else {
            LocalDate newStart = (cmpStart >= 0 ? other.start : start);
            LocalDate newEnd = (cmpEnd <= 0 ? other.end : end);
            return LocalDateRange.of(newStart, newEnd);
        }
    }

    /**
     * Calculates the smallest range that encloses this range and the specified range.
     * <p>
     * The result of this method will {@linkplain #encloses(LocalDateRange) enclose}
     * this range and the specified range.
     * 
     * @param other  the other range to check for, not null
     * @return the range that spans the two ranges
     */
    public LocalDateRange span(LocalDateRange other) {
        Objects.requireNonNull(other, "other");
        int cmpStart = start.compareTo(other.start);
        int cmpEnd = end.compareTo(other.end);
        LocalDate newStart = (cmpStart >= 0 ? other.start : start);
        LocalDate newEnd = (cmpEnd <= 0 ? other.end : end);
        return LocalDateRange.of(newStart, newEnd);
    }

    //-----------------------------------------------------------------------
    /**
     * Streams the set of dates included in the range.
     * <p>
     * This returns a stream consisting of each date in the range.
     * The stream is ordered.
     * 
     * @return the stream of dates from the start to the end
     */
    public Stream<LocalDate> stream() {
        long count = end.toEpochDay() - start.toEpochDay() + (isUnboundedEnd() ? 1 : 0);
        Spliterator<LocalDate> spliterator = new Spliterators.AbstractSpliterator<LocalDate>(
                count,
                Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.DISTINCT | Spliterator.ORDERED |
                        Spliterator.SORTED | Spliterator.SIZED | Spliterator.SUBSIZED) {

            private LocalDate current = start;
            
            @Override
            public boolean tryAdvance(Consumer<? super LocalDate> action) {
                if (current != null) {
                    if (current.isBefore(end)) {
                        action.accept(current);
                        current = current.plusDays(1);
                        return true;
                    }
                    if (current.equals(LocalDate.MAX)) {
                        action.accept(LocalDate.MAX);
                        current = null;
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public Comparator<? super LocalDate> getComparator() {
                return null;
            }
        };
        return StreamSupport.stream(spliterator, false);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this range is after the specified date.
     * <p>
     * The result is true if every date in this range is after the specified date.
     * An empty range behaves as though it is a date for comparison purposes.
     *
     * @param date  the other date to compare to, not null
     * @return true if the start of this range is after the specified date
     */
    public boolean isAfter(LocalDate date) {
        return start.compareTo(date) > 0;
    }

    /**
     * Checks if this range is before the specified date.
     * <p>
     * The result is true if every date in this range is before the specified date.
     * An empty range behaves as though it is a date for comparison purposes.
     *
     * @param date  the other date to compare to, not null
     * @return true if the start of this range is before the specified date
     */
    public boolean isBefore(LocalDate date) {
        return end.compareTo(date) <= 0 && start.compareTo(date) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this range is after the specified range.
     * <p>
     * The result is true if every date in this range is after every date in the specified range.
     * An empty range behaves as though it is a date for comparison purposes.
     *
     * @param other  the other range to compare to, not null
     * @return true if every date in this range is after every date in the other range
     */
    public boolean isAfter(LocalDateRange other) {
        return start.compareTo(other.end) >= 0 && !other.equals(this);
    }

    /**
     * Checks if this range is before the specified range.
     * <p>
     * The result is true if every date in this range is before every date in the specified range.
     * An empty range behaves as though it is a date for comparison purposes.
     *
     * @param range  the other range to compare to, not null
     * @return true if every date in this range is before every date in the other range
     */
    public boolean isBefore(LocalDateRange range) {
        return end.compareTo(range.start) <= 0 && !range.equals(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the length of this range in days.
     * <p>
     * This returns the number of days between the start and end dates.
     * If the range is too large, the length will be {@code Integer.MAX_VALUE}.
     * Unbounded ranges return {@code Integer.MAX_VALUE}.
     *
     * @return the length in days, Integer.MAX_VALUE if unbounded or too large
     */
    public int lengthInDays() {
        if (isUnboundedStart() || isUnboundedEnd()) {
            return Integer.MAX_VALUE;
        }
        long length = end.toEpochDay() - start.toEpochDay();
        return length > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) length;
    }

    /**
     * Obtains the length of this range as a period.
     * <p>
     * This returns the {@link Period} between the start and end dates.
     * Unbounded ranges throw {@link ArithmeticException}.
     *
     * @return the period of the range
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Period},
     *   or the range is unbounded
     */
    public Period toPeriod() {
        if (isUnboundedStart() || isUnboundedEnd()) {
            throw new ArithmeticException("Unbounded range cannot be converted to a Period");
        }
        return Period.between(start, end);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this range is equal to another range.
     * <p>
     * Compares this {@code LocalDateRange} with another ensuring that the two dates are the same.
     * Only objects of type {@code LocalDateRange} are compared, other types return false.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other range
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LocalDateRange) {
            LocalDateRange other = (LocalDateRange) obj;
            return start.equals(other.start) && end.equals(other.end);
        }
        return false;
    }

    /**
     * A hash code for this range.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return start.hashCode() ^ end.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this range as a {@code String}, such as {@code 2007-12-03/2007-12-04}.
     * <p>
     * The output will be the ISO-8601 format formed by combining the
     * {@code toString()} methods of the two dates, separated by a forward slash.
     *
     * @return a string representation of this date, not null
     */
    @Override
    @ToString
    public String toString() {
        return start.toString() + '/' + end.toString();
    }

}
