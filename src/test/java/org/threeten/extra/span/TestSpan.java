package org.threeten.extra.span;

import org.testng.annotations.Test;
import org.threeten.extra.Temporals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class TestSpan {

    @Test
    public void should_split_span_in_three_ranges() {
        //GIVEN
        Instant start = LocalDateTime.of(2014, 10, 4, 21, 3).toInstant(ZoneOffset.UTC);
        Instant stop = LocalDateTime.of(2014, 10, 7, 20, 1).toInstant(ZoneOffset.UTC);

        //WHEN
        List<ValueRange> valueRanges = Temporals.span(start, stop).split(1, ChronoUnit.DAYS);

        //THEN
        assertThat(valueRanges).hasSize(3);
        assertThat(valueRanges.get(0).getMinimum()).isEqualTo(start.getEpochSecond());
        assertThat(valueRanges.get(0).getMaximum()).isEqualTo(start.plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS).getEpochSecond());
        assertThat(valueRanges.get(1).getMinimum()).isEqualTo(start.plus(1, ChronoUnit.DAYS).getEpochSecond());
        assertThat(valueRanges.get(1).getMaximum()).isEqualTo(start.plus(2, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS).getEpochSecond());
        assertThat(valueRanges.get(2).getMinimum()).isEqualTo(start.plus(2, ChronoUnit.DAYS).getEpochSecond());
        assertThat(valueRanges.get(2).getMaximum()).isEqualTo(stop.getEpochSecond());
    }

    @Test
    public void should_split_span_in_two_ranges() {
        //GIVEN
        Instant start = LocalDateTime.of(2014, 10, 4, 7, 3).toInstant(ZoneOffset.UTC);
        Instant stop = LocalDateTime.of(2014, 10, 6, 8, 1).toInstant(ZoneOffset.UTC);

        //WHEN
        List<ValueRange> valueRanges = Temporals.span(start, stop).split(2, ChronoUnit.DAYS);

        //THEN
        assertThat(valueRanges).hasSize(2);
        assertThat(valueRanges.get(0).getMinimum()).isEqualTo(start.getEpochSecond());
        assertThat(valueRanges.get(0).getMaximum()).isEqualTo(start.plus(2, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS).getEpochSecond());
        assertThat(valueRanges.get(1).getMinimum()).isEqualTo(start.plus(2, ChronoUnit.DAYS).getEpochSecond());
        assertThat(valueRanges.get(1).getMaximum()).isEqualTo(stop.getEpochSecond());
    }
}
