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
import static java.time.temporal.ChronoUnit.ERAS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

/**
 * Test class.
 */
public class TestPeriodDuration {

    private static final Period P1Y2M3D = Period.of(1, 2, 3);
    private static final Duration DUR_5 = Duration.ofSeconds(5);
    private static final Duration DUR_6 = Duration.ofSeconds(6);

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(PeriodDuration.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_serialization() throws Exception {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(test, ois.readObject());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ZERO() {
        assertEquals(PeriodDuration.ZERO, PeriodDuration.of(Period.ZERO, Duration.ZERO));
        assertEquals(PeriodDuration.ZERO, PeriodDuration.of(Period.ZERO));
        assertEquals(PeriodDuration.ZERO, PeriodDuration.of(Duration.ZERO));
        assertEquals(Period.ZERO, PeriodDuration.ZERO.getPeriod());
        assertEquals(Duration.ZERO, PeriodDuration.ZERO.getDuration());
        assertEquals(true, PeriodDuration.ZERO.isZero());
        assertEquals(Arrays.asList(YEARS, MONTHS, DAYS, SECONDS, NANOS), PeriodDuration.ZERO.getUnits());
        assertEquals(0, PeriodDuration.ZERO.get(YEARS));
        assertEquals(0, PeriodDuration.ZERO.get(MONTHS));
        assertEquals(0, PeriodDuration.ZERO.get(DAYS));
        assertEquals(0, PeriodDuration.ZERO.get(SECONDS));
        assertEquals(0, PeriodDuration.ZERO.get(NANOS));
    }

    @Test
    public void test_ZERO_getEra() {
        assertThrows(DateTimeException.class, () -> PeriodDuration.ZERO.get(ERAS));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of_both() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(4));
        assertEquals(P1Y2M3D, test.getPeriod());
        assertEquals(Duration.ofSeconds(4), test.getDuration());
        assertEquals(false, test.isZero());
        assertEquals(1, test.get(YEARS));
        assertEquals(2, test.get(MONTHS));
        assertEquals(3, test.get(DAYS));
        assertEquals(4, test.get(SECONDS));
        assertEquals(0, test.get(NANOS));
    }

    @Test
    public void test_of_period() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D);
        assertEquals(P1Y2M3D, test.getPeriod());
        assertEquals(Duration.ZERO, test.getDuration());
        assertEquals(false, test.isZero());
        assertEquals(1, test.get(YEARS));
        assertEquals(2, test.get(MONTHS));
        assertEquals(3, test.get(DAYS));
        assertEquals(0, test.get(SECONDS));
        assertEquals(0, test.get(NANOS));
    }

    @Test
    public void test_of_duration() {
        PeriodDuration test = PeriodDuration.of(Duration.ofSeconds(4));
        assertEquals(Period.ZERO, test.getPeriod());
        assertEquals(Duration.ofSeconds(4), test.getDuration());
        assertEquals(false, test.isZero());
        assertEquals(0, test.get(YEARS));
        assertEquals(0, test.get(MONTHS));
        assertEquals(0, test.get(DAYS));
        assertEquals(4, test.get(SECONDS));
        assertEquals(0, test.get(NANOS));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_between_borrowsDayToAvoidMixedSigns() {
        LocalDateTime a = LocalDateTime.of(2012, 1, 31, 10, 0);
        LocalDateTime b = LocalDateTime.of(2012, 3, 1, 9, 0);
        PeriodDuration forward = PeriodDuration.between(a, b);
        assertEquals(PeriodDuration.of(Period.ofDays(29), Duration.ofHours(23)), forward);
        assertEquals(b, a.plus(forward));
        PeriodDuration backward = PeriodDuration.between(b, a);
        assertEquals(a, b.plus(backward));
        assertFalse(backward.getPeriod().isNegative() && backward.getDuration().compareTo(Duration.ZERO) > 0);
    }

    @Test
    public void test_between_zoned_acrossDst() {
        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime start = ZonedDateTime.of(2012, 3, 24, 12, 0, 0, 0, london);
        Instant end = ZonedDateTime.of(2012, 3, 25, 12, 0, 0, 0, london).toInstant();
        assertEquals(PeriodDuration.of(Period.ofDays(1)), PeriodDuration.between(start, end));
        Instant end2 = end.plus(Duration.ofMinutes(90));
        assertEquals(PeriodDuration.of(Period.ofDays(1), Duration.ofMinutes(90)), PeriodDuration.between(start, end2));
    }

    @Test
    public void test_between_zoned_borrowsDayToAvoidMixedSigns() {
        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime a = ZonedDateTime.of(2012, 3, 24, 12, 0, 0, 0, london);
        ZonedDateTime b = ZonedDateTime.of(2012, 3, 25, 11, 0, 0, 0, london);
        assertEquals(PeriodDuration.of(Duration.ofHours(22)), PeriodDuration.between(a, b.toInstant()));
        assertEquals(PeriodDuration.of(Duration.ofHours(-22)), PeriodDuration.between(b, a.toInstant()));

        ZoneId paris = ZoneId.of("Europe/Paris");
        ZonedDateTime c = ZonedDateTime.of(2012, 1, 31, 10, 0, 0, 0, paris);
        ZonedDateTime d = ZonedDateTime.of(2012, 3, 1, 9, 0, 0, 0, paris);
        assertEquals(PeriodDuration.of(Period.ofDays(29), Duration.ofHours(23)), PeriodDuration.between(c, d.toInstant()));
        PeriodDuration backward = PeriodDuration.between(d, c.toInstant());
        assertEquals(c, d.plus(backward.getPeriod()).plus(backward.getDuration()));
    }

    @Test
    public void test_between_zoned_javadocExample() {
        ZonedDateTime start = ZonedDateTime.parse("2012-03-24T12:00+01:00[Europe/Paris]");
        ZonedDateTime endZoned = ZonedDateTime.parse("2012-03-27T12:00+02:00[Europe/Paris]");
        Instant end = Instant.parse("2012-03-27T10:00:00Z");
        assertEquals(endZoned.toInstant(), end);
        assertEquals(71, Duration.between(start.toInstant(), end).toHours());
        assertEquals(PeriodDuration.of(Period.ofDays(3)), PeriodDuration.between(start, end));
    }

    @Test
    public void test_between_zoned_afterDst() {
        ZonedDateTime original = ZonedDateTime.parse("2012-03-30T00:00+02:00[Europe/Paris]");
        assertEquals(
                PeriodDuration.of(Period.ofDays(3), Duration.ofHours(2)),
                PeriodDuration.between(original, Instant.parse("2012-04-02T00:00:00Z")));
    }

    @Test
    public void test_between_zoned_endInGap() {
        // 2012-03-25T01:30 does not exist in London, local time jumps from 01:00 to 02:00
        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime start = ZonedDateTime.of(2012, 3, 24, 1, 30, 0, 0, london);
        Instant end = Instant.parse("2012-03-25T01:15:00Z");
        PeriodDuration test = PeriodDuration.between(start, end);
        assertEquals(end, start.plus(test.getPeriod()).plus(test.getDuration()).toInstant());
        assertFalse(test.getPeriod().isNegative() || test.getDuration().isNegative());
    }

    @Test
    public void test_between_unzonedEnd_startInLaterOverlap() {
        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime start = ZonedDateTime.of(2012, 10, 28, 1, 30, 0, 0, london).withLaterOffsetAtOverlap();
        assertEquals(PeriodDuration.ZERO,
                PeriodDuration.between(start, java.time.LocalDateTime.of(2012, 10, 28, 1, 30)));
    }

    @Test
    public void test_between_unzonedEnd_startInOtherYearsOverlap() {
        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime start = ZonedDateTime.of(2011, 10, 30, 1, 30, 0, 0, london).withLaterOffsetAtOverlap();
        ZonedDateTime earlier = ZonedDateTime.of(2012, 10, 28, 1, 30, 0, 0, london);
        PeriodDuration test = PeriodDuration.between(start, java.time.LocalDateTime.of(2012, 10, 28, 1, 30));
        assertEquals(earlier.toInstant(), start.plus(test.getPeriod()).plus(test.getDuration()).toInstant());
    }

    @Test
    public void test_between_zoned_endInOverlap() {
        // 2012-10-28T01:30 occurs twice in London
        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime start = ZonedDateTime.of(2012, 10, 27, 1, 30, 0, 0, london);
        for (Instant end : Arrays.asList(Instant.parse("2012-10-28T00:30:00Z"), Instant.parse("2012-10-28T01:30:00Z"))) {
            PeriodDuration test = PeriodDuration.between(start, end);
            assertEquals(end, start.plus(test.getPeriod()).plus(test.getDuration()).toInstant());
            assertFalse(test.getPeriod().isNegative() || test.getDuration().isNegative());
        }
    }

    public static Object[][] data_betweenZonedZones() {
        return new Object[][]{
                {"Europe/London"}, {"America/New_York"}, {"Australia/Lord_Howe"}, {"Asia/Kolkata"}, {"Pacific/Apia"}};
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data_betweenZonedZones")
    public void test_between_zoned_randomRoundTripAndSigns(String zoneId) {
        ZoneId zone = ZoneId.of(zoneId);
        Random random = new Random(zoneId.hashCode());
        long from = Instant.parse("2010-01-01T00:00:00Z").getEpochSecond();
        long range = Instant.parse("2020-01-01T00:00:00Z").getEpochSecond() - from;
        for (int i = 0; i < 2000; i++) {
            ZonedDateTime start = Instant.ofEpochSecond(from + (long) (random.nextDouble() * range)).atZone(zone);
            // bias towards nearby values, where gaps, overlaps and month-ends matter
            long delta = (long) ((random.nextDouble() - 0.5) * (i % 2 == 0 ? 3 * 86400 : 120 * 86400));
            Instant end = start.toInstant().plusSeconds(delta);
            PeriodDuration test = PeriodDuration.between(start, end);
            String msg = start + " -> " + end + " = " + test;
            assertEquals(end, start.plus(test.getPeriod()).plus(test.getDuration()).toInstant(), msg);
            assertFalse(test.getPeriod().isNegative() && test.getDuration().compareTo(Duration.ZERO) > 0, msg);
            assertFalse(test.getDuration().isNegative() && !test.getPeriod().isNegative() && !test.getPeriod().isZero(), msg);
        }
    }

    @Test
    public void test_between_randomRoundTripAndSigns() {
        Random random = new Random(12345);
        ZoneOffset[] offsets = {ZoneOffset.UTC, ZoneOffset.ofHours(1), ZoneOffset.ofHoursMinutes(-9, -30), ZoneOffset.ofHoursMinutes(5, 45)};
        for (int i = 0; i < 5000; i++) {
            OffsetDateTime start = OffsetDateTime.of(
                    LocalDateTime.of(2012, 1, 1, 0, 0).plusSeconds(random.nextInt(2 * 365 * 86400)).plusNanos(random.nextInt(1_000_000_000)),
                    offsets[random.nextInt(offsets.length)]);
            OffsetDateTime end = OffsetDateTime.of(
                    start.toLocalDateTime().plusSeconds((long) ((random.nextDouble() - 0.5) * 200 * 86400)),
                    offsets[random.nextInt(offsets.length)]);
            PeriodDuration test = PeriodDuration.between(start, end);
            String msg = start + " -> " + end + " = " + test;
            assertEquals(end.toInstant(), start.plus(test).toInstant(), msg);
            assertFalse(test.getPeriod().isNegative() && test.getDuration().compareTo(Duration.ZERO) > 0, msg);
            assertFalse(test.getDuration().isNegative() && !test.getPeriod().isNegative() && !test.getPeriod().isZero(), msg);
        }
    }

    @Test
    public void test_between_zoned_skippedDay() {
        // Pacific/Apia skipped 2011-12-30 entirely, so the end is only 13 hours after the start
        ZoneId apia = ZoneId.of("Pacific/Apia");
        ZonedDateTime a = ZonedDateTime.of(2011, 12, 29, 12, 0, 0, 0, apia);
        ZonedDateTime b = ZonedDateTime.of(2011, 12, 31, 1, 0, 0, 0, apia);
        for (ZonedDateTime[] pair : new ZonedDateTime[][] {{a, b}, {b, a}}) {
            PeriodDuration test = PeriodDuration.between(pair[0], pair[1]);
            assertEquals(pair[1].toInstant(), pair[0].plus(test.getPeriod()).plus(test.getDuration()).toInstant(), test.toString());
            assertFalse(test.getPeriod().isNegative() && test.getDuration().compareTo(Duration.ZERO) > 0, test.toString());
            assertFalse(test.getDuration().isNegative() && !test.getPeriod().isNegative() && !test.getPeriod().isZero(), test.toString());
        }
    }

    @Test
    public void test_between_zoned_rollbackAcrossMidnightReversesDateOrder() {
        ZoneId stJohns = ZoneId.of("America/St_Johns");
        ZonedDateTime start = ZonedDateTime.of(2009, 11, 1, 0, 0, 0, 0, stJohns);
        ZonedDateTime end = start.plusMinutes(30);
        assertTrue(end.toLocalDate().isBefore(start.toLocalDate()));
        assertEquals(PeriodDuration.of(Duration.ofMinutes(30)), PeriodDuration.between(start, end));
        assertEquals(PeriodDuration.of(Duration.ofMinutes(-30)), PeriodDuration.between(end, start));
    }

    @Test
    public void test_between_zoned_backwardsAndSame() {
        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime start = ZonedDateTime.of(2012, 3, 25, 12, 0, 0, 0, london);
        Instant end = ZonedDateTime.of(2012, 3, 24, 11, 0, 0, 0, london).toInstant();
        assertEquals(PeriodDuration.of(Period.ofDays(-1), Duration.ofHours(-1)), PeriodDuration.between(start, end));
        assertEquals(PeriodDuration.ZERO, PeriodDuration.between(start, start.toInstant()));
    }

    public static Object[][] data_between() {
        ZoneOffset plus1 = ZoneOffset.ofHours(1);
        Instant instant = Instant.parse("2012-06-20T10:00:00Z");
        OffsetDateTime odt = OffsetDateTime.of(2012, 6, 20, 10, 0, 0, 0, plus1);
        OffsetDate offsetDate = OffsetDate.of(LocalDate.of(2012, 6, 20), plus1);
        return new Object[][]{
                {"dates", LocalDate.of(2012, 6, 20), LocalDate.of(2012, 8, 25), PeriodDuration.of(Period.of(0, 2, 5))},
                {"times", LocalTime.of(11, 20), LocalTime.of(12, 25), PeriodDuration.of(Duration.ofMinutes(65))},
                {"localDate-localTime", LocalDate.of(2012, 6, 20), LocalTime.of(11, 25),
                        PeriodDuration.of(Duration.ofMinutes(685))},
                {"localDate-localDateTime", LocalDate.of(2012, 6, 20), LocalDateTime.of(2012, 7, 22, 11, 25),
                        PeriodDuration.of(Period.of(0, 1, 2), Duration.ofMinutes(685))},
                {"offsetDate-offsetDate", offsetDate, OffsetDate.of(LocalDate.of(2012, 6, 20), ZoneOffset.UTC),
                        PeriodDuration.of(Duration.ofHours(1))},
                {"instant-instant", instant, Instant.parse("2012-06-22T11:00:00.5Z"),
                        PeriodDuration.of(Period.ofDays(2), Duration.ofHours(1).plusMillis(500))},
                {"negativeOffsets",
                        OffsetDateTime.of(2012, 6, 20, 22, 0, 0, 0, ZoneOffset.ofHours(-5)),
                        OffsetDateTime.of(2012, 6, 21, 2, 0, 0, 0, ZoneOffset.ofHours(-3)),
                        PeriodDuration.of(Duration.ofHours(2))},
                {"nanos",
                        LocalDateTime.of(2012, 6, 20, 10, 0, 0, 900_000_000),
                        LocalDateTime.of(2012, 6, 21, 10, 0, 0, 100_000_000),
                        PeriodDuration.of(Duration.ofHours(23).plusMinutes(59).plusSeconds(59).plusMillis(200))},
                {"differentZones",
                        ZonedDateTime.of(2012, 6, 20, 10, 0, 0, 0, ZoneId.of("Europe/London")),
                        ZonedDateTime.of(2012, 6, 20, 6, 0, 0, 0, ZoneId.of("America/New_York")),
                        PeriodDuration.of(Duration.ofHours(1))},
                {"instant-offsetDateTime", instant, odt, PeriodDuration.of(Duration.ofHours(-1))},
                {"instant-localDateTime", instant, LocalDateTime.of(2012, 6, 22, 11, 0),
                        PeriodDuration.of(Period.ofDays(2), Duration.ofHours(1))},
                {"offsetTime-offsetTime", OffsetTime.of(10, 0, 0, 0, plus1), OffsetTime.of(9, 30, 0, 0, ZoneOffset.UTC),
                        PeriodDuration.of(Duration.ofMinutes(30))},
                {"offsetTime-offsetTime-sameDate", OffsetTime.of(23, 0, 0, 0, plus1), OffsetTime.of(23, 30, 0, 0, ZoneOffset.UTC),
                        PeriodDuration.of(Duration.ofMinutes(90))},
                {"offsetTime-offsetTime-sameDateAcrossOffsets", OffsetTime.of(0, 30, 0, 0, plus1), OffsetTime.of(23, 30, 0, 0, ZoneOffset.UTC),
                        PeriodDuration.of(Duration.ofHours(24))},
                {"offsetDate-localDate", offsetDate, LocalDate.of(2012, 6, 25), PeriodDuration.of(Period.ofDays(5))},
                {"offsetDate-localTime", offsetDate, LocalTime.of(11, 30), PeriodDuration.of(Duration.ofMinutes(690))},
                {"offsetDateTime-localDate", odt, LocalDate.of(2012, 6, 25),
                        PeriodDuration.of(Period.ofDays(4), Duration.ofHours(14))},
                {"offsetDateTime-localTime", odt, LocalTime.of(11, 30), PeriodDuration.of(Duration.ofMinutes(90))},
                {"year-year", Year.of(2020), Year.of(2025), PeriodDuration.of(Period.ofYears(5))},
                {"yearMonth-yearMonth", YearMonth.of(2020, 1), YearMonth.of(2021, 3), PeriodDuration.of(Period.of(1, 2, 0))},
                {"yearMonth-localDate", YearMonth.of(2020, 1), LocalDate.of(2020, 1, 11), PeriodDuration.of(Period.ofDays(10))},
        };
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data_between")
    public void test_between(String name, Temporal start, Temporal end, PeriodDuration expected) {
        assertEquals(expected, PeriodDuration.between(start, end));
        assertEquals(expected.negated(), PeriodDuration.between(end, start));
    }

    @Test
    public void test_between_zonedStart_localEndUsesZone() {
        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime start = ZonedDateTime.of(2012, 3, 24, 12, 0, 0, 0, london);
        LocalDateTime end = LocalDateTime.of(2012, 3, 25, 12, 0);
        PeriodDuration test = PeriodDuration.between(start, end);
        assertEquals(PeriodDuration.of(Period.ofDays(1)), test);
        assertEquals(ZonedDateTime.of(end, london), start.plus(test));
    }

    @Test
    public void test_between_dates_addableToStart() {
        // backwards month-end cases where Period.between does not add back to the end date
        LocalDate[] dates = {
            LocalDate.of(2013, 3, 29), LocalDate.of(2013, 2, 1), LocalDate.of(2012, 12, 31), LocalDate.of(2012, 4, 3),
            LocalDate.of(2015, 7, 31), LocalDate.of(2014, 11, 30), LocalDate.of(2012, 2, 29), LocalDate.of(2011, 2, 28)};
        for (LocalDate a : dates) {
            for (LocalDate b : dates) {
                PeriodDuration test = PeriodDuration.between(a, b);
                assertEquals(b, a.plus(test.getPeriod()), a + " " + b);
                assertEquals(test, PeriodDuration.between(a.atStartOfDay(), b.atStartOfDay()), a + " " + b);
                if (!b.isBefore(a)) {
                    assertEquals(PeriodDuration.of(Period.between(a, b)), test, a + " " + b);
                }
            }
        }
    }

    public static Object[][] data_betweenAllTypes() {
        ZoneOffset plus1 = ZoneOffset.ofHours(1);
        ZoneId london = ZoneId.of("Europe/London");
        Temporal[] temporals = {
            LocalDate.of(2012, 1, 31), LocalTime.of(10, 30), LocalDateTime.of(2012, 3, 1, 9, 0),
            OffsetDate.of(LocalDate.of(2012, 3, 25), plus1), OffsetTime.of(23, 30, 0, 0, plus1),
            OffsetDateTime.of(2012, 3, 25, 0, 30, 0, 0, ZoneOffset.ofHoursMinutes(-9, -30)),
            ZonedDateTime.of(2012, 3, 25, 12, 0, 0, 0, london), Instant.parse("2012-03-24T23:45:00.5Z"),
            Year.of(2011), YearMonth.of(2012, 2), YearQuarter.of(2012, Quarter.Q3), YearHalf.of(2012, 2),
            YearWeek.of(2012, 10)};
        Object[][] data = new Object[temporals.length * temporals.length][];
        int i = 0;
        for (Temporal start : temporals) {
            for (Temporal end : temporals) {
                data[i++] = new Object[] {start, end};
            }
        }
        return data;
    }

    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("data_betweenAllTypes")
    public void test_between_allTypeCombinations(Temporal start, Temporal end) {
        PeriodDuration test = PeriodDuration.between(start, end);
        String msg = start + " -> " + end + " = " + test;
        assertFalse(test.getPeriod().isNegative() && test.getDuration().compareTo(Duration.ZERO) > 0, msg);
        assertFalse(test.getDuration().isNegative() && !test.getPeriod().isNegative() && !test.getPeriod().isZero(), msg);
        // same instance must always give zero
        assertEquals(PeriodDuration.ZERO, PeriodDuration.between(start, start), msg);
        // if the start is a full date-time that can accept the result, the end is reached
        if (start instanceof LocalDateTime && end instanceof LocalDateTime) {
            assertEquals(end, start.plus(test), msg);
        }
        if (start instanceof ZonedDateTime || start instanceof OffsetDateTime) {
            Instant expected = end.query(Temporals.instant());
            if (expected != null) {
                assertEquals(expected, start.plus(test).query(Temporals.instant()), msg);
            }
        }
    }

    @Test
    public void test_between_overflowOnOffsetConversion() {
        OffsetDateTime start = OffsetDateTime.of(LocalDateTime.MAX, ZoneOffset.ofHours(18));
        OffsetDateTime end = OffsetDateTime.of(LocalDateTime.MAX, ZoneOffset.ofHours(-18));
        assertThrows(DateTimeException.class, () -> PeriodDuration.between(start, end));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_from() {
        assertEquals(PeriodDuration.from(PeriodDuration.of(P1Y2M3D)), PeriodDuration.from(PeriodDuration.of(P1Y2M3D)));
        assertEquals(PeriodDuration.of(Period.ofYears(2)), PeriodDuration.from(Period.ofYears(2)));
        assertEquals(PeriodDuration.of(Duration.ofSeconds(2)), PeriodDuration.from(Duration.ofSeconds(2)));
        assertEquals(PeriodDuration.of(Period.ofYears(2)), PeriodDuration.from(Years.of(2)));
        assertEquals(PeriodDuration.of(Period.ofMonths(2)), PeriodDuration.from(Months.of(2)));
        assertEquals(PeriodDuration.of(Period.ofWeeks(2)), PeriodDuration.from(Weeks.of(2)));
        assertEquals(PeriodDuration.of(Period.ofDays(2)), PeriodDuration.from(Days.of(2)));
        assertEquals(PeriodDuration.of(Duration.ofHours(2)), PeriodDuration.from(Hours.of(2)));
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_valid() {
        return new Object[][] {
                {"P1Y2M3W4DT5H6M7S", Period.of(1, 2, 3 * 7 + 4), Duration.ofHours(5).plusMinutes(6).plusSeconds(7)},
                {"P3Y", Period.ofYears(3), Duration.ZERO},
                {"P3M", Period.ofMonths(3), Duration.ZERO},
                {"P3W", Period.ofWeeks(3), Duration.ZERO},
                {"P3D", Period.ofDays(3), Duration.ZERO},

                {"PT0S", Period.of(0, 0, 0), Duration.ofSeconds(0)},
                {"PT1S", Period.of(0, 0, 0), Duration.ofSeconds(1)},
                {"PT2S", Period.of(0, 0, 0), Duration.ofSeconds(2)},
                {"PT123456789S", Period.of(0, 0, 0), Duration.ofSeconds(123456789)},
                {"PT+0S", Period.of(0, 0, 0), Duration.ofSeconds(0)},
                {"PT+2S", Period.of(0, 0, 0), Duration.ofSeconds(2)},
                {"PT-0S", Period.of(0, 0, 0), Duration.ofSeconds(0)},
                {"PT-2S", Period.of(0, 0, 0), Duration.ofSeconds(-2)},

                {"P+0M", Period.of(0, 0, 0), Duration.ZERO},
                {"P+2M", Period.of(0, 2, 0), Duration.ZERO},
                {"P-0M", Period.of(0, 0, 0), Duration.ZERO},
                {"P-2M", Period.of(0, -2, 0), Duration.ZERO},
        };
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid(String str, Period period, Duration duration) {
        assertEquals(PeriodDuration.of(period, duration), PeriodDuration.parse(str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialPlus(String str, Period period, Duration duration) {
        assertEquals(PeriodDuration.of(period, duration), PeriodDuration.parse("+" + str));
    }

    @ParameterizedTest
    @MethodSource("data_valid")
    public void test_parse_CharSequence_valid_initialMinus(String str, Period period, Duration duration) {
        assertEquals(PeriodDuration.of(period, duration).negated(), PeriodDuration.parse("-" + str));
    }

    public static Object[][] data_invalid() {
        return new Object[][] {
                {"P3Q"},
                {"P1M2Y"},

                {"3"},
                {"-3"},
                {"3H"},
                {"-3H"},
                {"P3"},
                {"P-3"},
                {"PH"},
                {"T"},
                {"T3H"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_invalid")
    public void test_parse_CharSequence_invalid(String str) {
        assertThrows(DateTimeParseException.class, () -> PeriodDuration.parse(str));
    }

    @Test
    public void test_parse_CharSequence_null() {
        //noinspection DataFlowIssue - testing nulls
        assertThrows(NullPointerException.class, () -> PeriodDuration.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount_PeriodDuration() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(PeriodDuration.of(Period.of(4, 4, 4), DUR_5), test.plus(Period.of(3, 2, 1)));
        assertEquals(PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(9)), test.plus(Duration.ofSeconds(4)));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.of(Integer.MAX_VALUE - 1, 0, 0)).plus(PeriodDuration.of(Period.ofYears(2))));
    }

    @Test
    public void test_plus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.of(Integer.MIN_VALUE + 1, 0, 0)).plus(PeriodDuration.of(Period.ofYears(-2))));
    }

    @Test
    public void test_plus_TemporalAmount_null() {
        //noinspection DataFlowIssue - testing nulls
        assertThrows(NullPointerException.class, () -> P1Y2M3D.plus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount_PeriodDuration() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(PeriodDuration.of(Period.of(0, 1, 2), DUR_5), test.minus(Period.of(1, 1, 1)));
        assertEquals(PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(1)), test.minus(Duration.ofSeconds(4)));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.of(Integer.MAX_VALUE - 1, 0, 0)).minus(PeriodDuration.of(Period.ofYears(-2))));
    }

    @Test
    public void test_minus_TemporalAmount_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.of(Integer.MIN_VALUE + 1, 0, 0)).minus(PeriodDuration.of(Period.ofYears(2))));
    }

    @Test
    public void test_minus_TemporalAmount_null() {
        //noinspection DataFlowIssue - testing nulls
        assertThrows(NullPointerException.class, () -> P1Y2M3D.minus(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy() {
        PeriodDuration test = PeriodDuration.of(P1Y2M3D, DUR_5);
        assertEquals(PeriodDuration.ZERO, test.multipliedBy(0));
        assertEquals(test, test.multipliedBy(1));
        assertEquals(PeriodDuration.of(Period.of(5,  10, 15), Duration.ofSeconds(25)), test.multipliedBy(5));
        assertEquals(PeriodDuration.of(Period.of(-3,  -6, -9), Duration.ofSeconds(-15)), test.multipliedBy(-3));
    }

    @Test
    public void test_multipliedBy_overflowTooBig() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.ofYears(Integer.MAX_VALUE / 2 + 1)).multipliedBy(2));
    }

    @Test
    public void test_multipliedBy_overflowTooSmall() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.ofYears(Integer.MIN_VALUE / 2 - 1)).multipliedBy(2));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_negated() {
        assertEquals(PeriodDuration.of(P1Y2M3D.negated(), DUR_5.negated()), PeriodDuration.of(P1Y2M3D, DUR_5).negated());
    }

    @Test
    public void test_negated_overflow() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Duration.ofSeconds(Long.MIN_VALUE)).negated());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_normalizedYears() {
        assertEquals(PeriodDuration.of(P1Y2M3D.normalized(), DUR_5), PeriodDuration.of(P1Y2M3D, DUR_5).normalizedYears());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_normalizedStandardDays() {
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(5)),
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(5)).normalizedStandardDays());
        assertEquals(
                PeriodDuration.of(P1Y2M3D.plusDays(1), Duration.ofHours(1)),
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(25)).normalizedStandardDays());
        assertEquals(
                PeriodDuration.of(P1Y2M3D.plusDays(-3), Duration.ofHours(-1)),
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(-73)).normalizedStandardDays());
    }

    @Test
    public void test_normalizedStandardDays_nanosSignAgrees() {
        assertEquals(
                PeriodDuration.of(Period.ofDays(-1), Duration.ofSeconds(-86399, -876543211)),
                PeriodDuration.of(Period.ofDays(-1), Duration.ofSeconds(-86400, 123456789)).normalizedStandardDays());
        assertEquals(
                PeriodDuration.of(Period.ofDays(-2), Duration.ofNanos(-876543211)),
                PeriodDuration.of(Period.ofDays(-2), Duration.ofNanos(-876543211)).normalizedStandardDays());
        assertEquals(
                PeriodDuration.of(Period.ofDays(1), Duration.ofNanos(500_000_000)),
                PeriodDuration.of(Period.ofDays(2), Duration.ofSeconds(-86400, 500_000_000)).normalizedStandardDays());
    }

    @Test
    public void test_normalizedStandardDays_secondsOverflow() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Period.ofDays(1), Duration.ofSeconds(Long.MAX_VALUE)).normalizedStandardDays());
    }

    @Test
    public void test_parse_CharSequence_overflow() {
        assertThrows(DateTimeParseException.class, () -> PeriodDuration.parse("P-2147483648W"));
        assertThrows(DateTimeParseException.class, () -> PeriodDuration.parse("P-2147483648WT1H"));
    }

    @Test
    public void test_normalizedStandardDaysn_overflow() {
        assertThrows(ArithmeticException.class, () -> PeriodDuration.of(Duration.ofSeconds(Long.MIN_VALUE)).normalizedStandardDays());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_addTo() {
        LocalDateTime base = LocalDateTime.of(2012, 6, 20, 11, 30, 0);
        assertEquals(LocalDateTime.of(2013, 8, 23, 11, 30, 5), PeriodDuration.of(P1Y2M3D, DUR_5).addTo(base));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_subtractFrom() {
        LocalDateTime base = LocalDateTime.of(2012, 6, 20, 11, 30, 0);
        assertEquals(LocalDateTime.of(2011, 4, 17, 11, 29, 55), PeriodDuration.of(P1Y2M3D, DUR_5).subtractFrom(base));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_truncatedTo() {
        assertEquals(
                PeriodDuration.of(P1Y2M3D, DUR_5), 
                PeriodDuration.of(P1Y2M3D, DUR_5).truncatedTo(NANOS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofNanos(-2_000_000)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofNanos(-2_000_456)).truncatedTo(MILLIS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, DUR_5), 
                PeriodDuration.of(P1Y2M3D, DUR_5).truncatedTo(SECONDS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ZERO), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(0)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ZERO), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(1)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ZERO), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(59)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(1)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(60)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(1)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(61)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(1)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(119)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(2)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(120)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofDays(0)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofMinutes(24 * 60 - 1)).truncatedTo(DAYS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofDays(1)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(24)).truncatedTo(DAYS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofDays(2)), 
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(51)).truncatedTo(DAYS));
        assertThrows(DateTimeException.class, () ->  PeriodDuration.of(P1Y2M3D, Duration.ofHours(51)).truncatedTo(MONTHS));
    }

    @Test
    public void test_truncatedTo_negativeTowardsZero() {
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(-86399)),
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(-86400, 1)).truncatedTo(SECONDS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofHours(-23).minusMinutes(59)),
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(-86400, 1)).truncatedTo(MINUTES));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ZERO),
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(-86400, 1)).truncatedTo(DAYS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ZERO),
                PeriodDuration.of(P1Y2M3D, Duration.ofMillis(-500)).truncatedTo(SECONDS));
        assertEquals(
                PeriodDuration.of(P1Y2M3D, Duration.ofSeconds(-1)),
                PeriodDuration.of(P1Y2M3D, Duration.ofMillis(-1500)).truncatedTo(SECONDS));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(PeriodDuration.of(P1Y2M3D, DUR_5), PeriodDuration.of(P1Y2M3D, DUR_5))
            .addEqualityGroup(PeriodDuration.of(P1Y2M3D, DUR_6), PeriodDuration.of(P1Y2M3D, DUR_6))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals("P1Y2M3DT5S", PeriodDuration.of(P1Y2M3D, DUR_5).toString());
        assertEquals("P1Y2M3D", PeriodDuration.of(P1Y2M3D, Duration.ZERO).toString());
        assertEquals("PT5S", PeriodDuration.of(Period.ZERO, DUR_5).toString());
    }

}
