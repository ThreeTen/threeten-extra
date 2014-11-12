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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * LocalDateTimeRange is the implementation of an immutable time interval.
 * <p>
 A LocalDateTimeRange represents a period of time between two LocalDateTimes. 
 LocalDateTimeRange are <i>closed</i>.
 LocalDateTimeRange are inclusive of the start and inclusive of the end. 
 The end is always greater than or equal to the begin.
 <p>
 * Methods throw a <tt>NullPointerException</tt> if any null object 
 * parameter is passed.
 * <p>
 LocalDateTimeRange is thread-safe and immutable.
 */
public final class LocalDateTimeRange implements Serializable {
    
    /**
     * Start of the range. The start is inclusive.
     */
    private final LocalDateTime start;
    
    /**
     * End of the range. The end is inclusive.
     */
    private final LocalDateTime end;
    
    /**
     * Constructs an instance of {@code LocalDateTimeRange} using start and end.
     * <p>
     * Ranges are inclusive of the start and inclusive of the end. 
     *
     * @param start  the start of the LocalDateTimeRange
     * @param end  the end of the LocalDateTimeRange
     */
    private LocalDateTimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
    
    /**
     * Gets the start of this LocalDateTimeRange.
     * <p>
     * The start is inclusive.
     * 
     * @return the start of this LocalDateTimeRange
     */
    public LocalDateTime getStart() {
        return start;
    }
    
    /**
     * Is this LocalDateTimeRange before the specified LocalDateTime.
     * 
     * @param localDateTime  the LocalDateTime to compare to
     * @return true if this LocalDateTimeRange is before the LocalDateTime
     */
    public boolean isBefore(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime cannot be null");
        return end.isBefore(localDateTime);
    }
    
    /**
     * Is this LocalDateTimeRange after the specified LocalDateTime.
     * 
     * @param localDateTime  the LocalDateTime to compare to
     * @return true if this LocalDateTimeRange is after the LocalDateTime
     */
    public boolean isAfter(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime cannot be null");
        return start.isAfter(localDateTime);
    }
    
    /**
     * Gets the end of this LocalDateTimeRange.
     * <p>
     * The end is inclusive.
     * 
     * @return the end of this LocalDateTimeRange
     */
    public LocalDateTime getEnd() {
        return end;
    }
    
    /**
     * Checks if this LocalDateTimeRange intersects with another LocalDateTimeRange
     * 
     * @param other the LocalDateTimeRange to compare to
     * @return true if the LocalDateRanges intersects
     */
    public boolean intersects(LocalDateTimeRange other) {
        Objects.requireNonNull(other, "other cannot be null");
        return !(start.isAfter(other.end) || other.start.isAfter(end));
    }
    
    /**
     * Checks if this LocalDateRanges abuts with another LocalDateTimeRange
     * 
     * @param other the LocalDateTimeRange to compare to
     * @return true if the LocalDateRanges abuts
     */
    public boolean abuts(LocalDateTimeRange other) {
        Objects.requireNonNull(other, "other cannot be null");
        return start.equals(other.start) || end.equals(other.end);
    }
    
    /**
     * Checks if this there is a gap
     * 
     * @param other the LocalDateTimeRange to compare to
     * @return true if there is a gaps
     */
    public boolean gaps(LocalDateTimeRange other) {
        return !intersects(other);
    }
    
    /**
     * Obtains the intersection of this LocalDateTimeRange with another LocalDateTimeRange.
     * <p>
     * The LocalDateTimeRange have to overlaps.
     * 
     * @param other the range to compare to
     * @return the LocalDateTimeRange
     */
    public Optional<LocalDateTimeRange> intersection(LocalDateTimeRange other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (intersects(other)) {
            LocalDateTime max = max(start, other.start);
            LocalDateTime min = min(end, other.end);
            return Optional.of(LocalDateTimeRange.of(max, min));
        } else {
            return Optional.empty();
        }
    }
    
    /**
     * Optains a {@code Duration} from this LocalDateTimeRange.
     * 
     * @return the LocalDateTimeRange as a duration
     */
    public Duration toDuration() {
        return Duration.between(start, end);
    }
    
    /**
     * Optains a {@code Period} from this LocalDateTimeRange.
     * 
     * @return the LocalDateTimeRange as a period
     */
    public Period toPeriod() {
        return Period.between(start.toLocalDate(), end.toLocalDate());
    }
    
    /**
     * Checks if this LocalDateTimeRange is equal to the specified {@code LocalDateTimeRange}.
     * 
     * @param obj the other LocalDateTimeRange, null returns false
     * @return true if the other LocalDateTimeRange is equal to this one
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            final LocalDateTimeRange other = (LocalDateTimeRange) obj;
            return Objects.equals(start, other.start)
                    && Objects.equals(end, other.end);
        }
    }
    
    /**
     * A hash code for this LocalDateTimeRange.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    /**
     * Outputs in ISO8601 interval format.
     * 
     * For example, "2014-01-01T00:00:00/2014-01-01T12:30:00".
     * 
     * @return the LocalDateTimeRange in ISO8601 interval format
     */
    @Override
    public String toString() {
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return start.format(df) + "/" + end.format(df);
    }
    
    /**
     * Obtains a {@code LocalDateTimeRange}
     * 
     * @param start the start of the LocalDateTimeRange. The start is inclusive.
     * @param end the end of the LocalDateTimeRange. The interval is inclusive.
     * @return a {@code LocalDateTimeRange}, not null
     */
    public static LocalDateTimeRange of(LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start is after end");
        }
        return new LocalDateTimeRange(start, end);
    }
    
    // -- Implementation helper methods
    
    private LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }
    
    private LocalDateTime min(LocalDateTime a, LocalDateTime b) {
        return a.isBefore(b) ? a : b;
    }
    
}
