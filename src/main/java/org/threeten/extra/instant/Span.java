package org.threeten.extra.instant;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

/**
 * Span between two {@link Instant} values
 */
public class Span {

    private final Instant start;
    private final Instant stop;

    private Span(Instant start, Instant stop) {
        this.start = start;
        this.stop = stop;
    }

    /**
     * Creates a {@link Span} between two {@link Instant} values
     * @param start
     * @param stop
     * @return
     */
    public static Span between(Instant start, Instant stop) {
        return new Span(start, stop);
    }

    /**
     * Splits the span between {@code start} and {@code stop} in {@link java.util.List} of {@link java.time.temporal.ValueRange}.
     * Ranges are not overlapping. For example
     * @param amount
     * @param unit
     * @return
     */
    public List<ValueRange> split(long amount, TemporalUnit unit) {

        List<ValueRange> ranges = new ArrayList<>();
        Instant tmpStart = start;
        do {
            Instant tmp = tmpStart.plus(amount, unit);
            ranges.add(ValueRange.of(tmpStart.getEpochSecond(),
                    tmp.isAfter(stop) ? stop.getEpochSecond() : tmp.minus(1, ChronoUnit.SECONDS).getEpochSecond()));
            tmpStart = tmp;
        } while(tmpStart.isBefore(stop));

        return ranges;
    }
}
