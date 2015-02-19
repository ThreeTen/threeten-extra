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

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.AMPM_OF_DAY;
import static java.time.temporal.ChronoField.CLOCK_HOUR_OF_AMPM;
import static java.time.temporal.ChronoField.CLOCK_HOUR_OF_DAY;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.HOUR_OF_AMPM;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.MICRO_OF_DAY;
import static java.time.temporal.ChronoField.MICRO_OF_SECOND;
import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.SECOND_OF_DAY;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.IsoFields.DAY_OF_QUARTER;
import static java.time.temporal.IsoFields.QUARTER_OF_YEAR;
import static java.time.temporal.IsoFields.WEEK_BASED_YEAR;
import static java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class TestYearWeek {

    private static final YearWeek TEST = YearWeek.of(2015, 1);

    @DataProvider(name = "sampleYearWeeks")
    Object[][] provider_sampleYearWeeks() {
        return new Object[][]{
            {2015, 1},
            {2015, 2},
            {2015, 3},
            {2015, 4},
            {2015, 5},
            {2015, 6},
            {2015, 7},
            {2015, 8},
            {2015, 9},
            {2015, 10},
            {2015, 11},
            {2015, 12},
            {2015, 13},
            {2015, 14},
            {2015, 15},
            {2015, 16},
            {2015, 17},
            {2015, 18},
            {2015, 19},
            {2015, 20},
            {2015, 21},
            {2015, 22},
            {2015, 21},
            {2015, 22},
            {2015, 23},
            {2015, 23},
            {2015, 24},
            {2015, 25},
            {2015, 26},
            {2015, 27},
            {2015, 28},
            {2015, 29},
            {2015, 30},
            {2015, 31},
            {2015, 32},
            {2015, 33},
            {2015, 34},
            {2015, 35},
            {2015, 36},
            {2015, 37},
            {2015, 38},
            {2015, 39},
            {2015, 40},
            {2015, 41},
            {2015, 42},
            {2015, 43},
            {2015, 44},
            {2015, 45},
            {2015, 46},
            {2015, 47},
            {2015, 48},
            {2015, 49},
            {2015, 50},
            {2015, 51},
            {2015, 52},
            {2015, 53}
        };
    }

    @DataProvider(name = "53WeekYear")
    Object[][] provider_53WeekYear() {
        return new Object[][]{
            {4},
            {9},
            {15},
            {20},
            {26},
            {32},
            {37},
            {43},
            {48},
            {54},
            {60},
            {65},
            {71},
            {76},
            {82},
            {88},
            {93},
            {99},
            {105},
            {111},
            {116},
            {122},
            {128},
            {133},
            {139},
            {144},
            {150},
            {156},
            {161},
            {167},
            {172},
            {178},
            {184},
            {189},
            {195},
            {201},
            {207},
            {212},
            {218},
            {224},
            {229},
            {235},
            {240},
            {246},
            {252},
            {257},
            {263},
            {268},
            {274},
            {280},
            {285},
            {291},
            {296},
            {303},
            {308},
            {314},
            {320},
            {325},
            {331},
            {336},
            {342},
            {348},
            {353},
            {359},
            {364},
            {370},
            {376},
            {381},
            {387},
            {392},
            {398}
        };
    }

    @DataProvider(name = "sampleAtDay")
    Object[][] provider_sampleAtDay() {
        return new Object[][]{
            {2014, 52, MONDAY, 2014, 12, 22},
            {2014, 52, TUESDAY, 2014, 12, 23},
            {2014, 52, WEDNESDAY, 2014, 12, 24},
            {2014, 52, THURSDAY, 2014, 12, 25}, 
            {2014, 52, FRIDAY, 2014, 12, 26},
            {2014, 52, SATURDAY, 2014, 12, 27}, 
            {2014, 52, SUNDAY, 2014, 12, 28},
            {2015,  1, MONDAY, 2014, 12, 29},
            {2015,  1, TUESDAY, 2014, 12, 30},
            {2015,  1, WEDNESDAY, 2014, 12, 31},
            {2015,  1, THURSDAY, 2015, 1, 1}, 
            {2015,  1, FRIDAY, 2015, 1, 2},
            {2015,  1, SATURDAY, 2015, 1, 3}, 
            {2015,  1, SUNDAY, 2015, 1, 4},
            {2017,  1, MONDAY, 2017, 1, 2},
            {2017,  1, TUESDAY, 2017, 1, 3},
            {2017,  1, WEDNESDAY, 2017, 1, 4},
            {2017,  1, THURSDAY, 2017, 1, 5}, 
            {2017,  1, FRIDAY, 2017, 1, 6},
            {2017,  1, SATURDAY, 2017, 1, 7}, 
            {2017,  1, SUNDAY, 2017, 1, 8}
        };
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(YearWeek.class));
        assertTrue(Comparable.class.isAssignableFrom(YearWeek.class));
        assertTrue(TemporalAdjuster.class.isAssignableFrom(YearWeek.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(YearWeek.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        YearWeek test = YearWeek.of(2015, 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @Test
    public void test_now() {
        YearWeek expected = YearWeek.now(Clock.systemDefaultZone());
        YearWeek test = YearWeek.now();
        for (int i = 0; i < 100; i++) {
            if (expected.equals(test)) {
                return;
            }
            expected = YearWeek.now(Clock.systemDefaultZone());
            test = YearWeek.now();
        }
        assertEquals(test, expected);
    }

    //-----------------------------------------------------------------------
    // now(ZoneId)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions = NullPointerException.class)
    public void now_ZoneId_nullZoneId() {
        YearWeek.now((ZoneId) null);
    }

    @Test
    public void now_ZoneId() {
        ZoneId zone = ZoneId.of("UTC+01:02:03");
        YearWeek expected = YearWeek.now(Clock.system(zone));
        YearWeek test = YearWeek.now(zone);
        for (int i = 0; i < 100; i++) {
            if (expected.equals(test)) {
                return;
            }
            expected = YearWeek.now(Clock.system(zone));
            test = YearWeek.now(zone);
        }
        assertEquals(test, expected);
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test
    public void now_Clock() {
        Instant instant = LocalDateTime.of(2010, 12, 31, 0, 0).toInstant(ZoneOffset.UTC);
        Clock clock = Clock.fixed(instant, ZoneOffset.UTC);
        YearWeek test = YearWeek.now(clock);
        assertEquals(test.getYear(), 2010);
        assertEquals(test.getWeek(), 52);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void now_Clock_nullClock() {
        YearWeek.now((Clock) null);
    }

    //-----------------------------------------------------------------------
    // of(int, int)
    //-----------------------------------------------------------------------
    @Test(dataProvider = "sampleYearWeeks")
    public void test_of(int year, int week) {
        YearWeek yearWeek = YearWeek.of(year, week);
        assertEquals(yearWeek.getYear(), year);
        assertEquals(yearWeek.getWeek(), week);
    }

    public void test_carry() {
        assertTrue(YearWeek.of(2014, 53).equals(TEST));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_year_toLow() {
        YearWeek.of(Integer.MIN_VALUE, 1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_year_toHigh() {
        YearWeek.of(Integer.MAX_VALUE, 1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_invalidWeekValue() {
        YearWeek.of(2015, 54);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_invalidWeekValue0() {
        YearWeek.of(2015, 0);
    }

    //-----------------------------------------------------------------------
    // isSupported(TemporalField)
    //-----------------------------------------------------------------------
    public void test_isSupported_TemporalField() {
        assertEquals(TEST.isSupported((TemporalField) null), false);
        assertEquals(TEST.isSupported(NANO_OF_SECOND), false);
        assertEquals(TEST.isSupported(NANO_OF_DAY), false);
        assertEquals(TEST.isSupported(MICRO_OF_SECOND), false);
        assertEquals(TEST.isSupported(MICRO_OF_DAY), false);
        assertEquals(TEST.isSupported(MILLI_OF_SECOND), false);
        assertEquals(TEST.isSupported(MILLI_OF_DAY), false);
        assertEquals(TEST.isSupported(SECOND_OF_MINUTE), false);
        assertEquals(TEST.isSupported(SECOND_OF_DAY), false);
        assertEquals(TEST.isSupported(MINUTE_OF_HOUR), false);
        assertEquals(TEST.isSupported(MINUTE_OF_DAY), false);
        assertEquals(TEST.isSupported(HOUR_OF_AMPM), false);
        assertEquals(TEST.isSupported(CLOCK_HOUR_OF_AMPM), false);
        assertEquals(TEST.isSupported(HOUR_OF_DAY), false);
        assertEquals(TEST.isSupported(CLOCK_HOUR_OF_DAY), false);
        assertEquals(TEST.isSupported(AMPM_OF_DAY), false);
        assertEquals(TEST.isSupported(DAY_OF_WEEK), false);
        assertEquals(TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_MONTH), false);
        assertEquals(TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_YEAR), false);
        assertEquals(TEST.isSupported(DAY_OF_MONTH), false);
        assertEquals(TEST.isSupported(DAY_OF_YEAR), false);
        assertEquals(TEST.isSupported(EPOCH_DAY), false);
        assertEquals(TEST.isSupported(ALIGNED_WEEK_OF_MONTH), false);
        assertEquals(TEST.isSupported(ALIGNED_WEEK_OF_YEAR), false);
        assertEquals(TEST.isSupported(MONTH_OF_YEAR), false);
        assertEquals(TEST.isSupported(PROLEPTIC_MONTH), false);
        assertEquals(TEST.isSupported(YEAR_OF_ERA), false);
        assertEquals(TEST.isSupported(YEAR), false);
        assertEquals(TEST.isSupported(ERA), false);
        assertEquals(TEST.isSupported(INSTANT_SECONDS), false);
        assertEquals(TEST.isSupported(OFFSET_SECONDS), false);
        assertEquals(TEST.isSupported(QUARTER_OF_YEAR), false);
        assertEquals(TEST.isSupported(DAY_OF_QUARTER), false);
        assertEquals(TEST.isSupported(WEEK_BASED_YEAR), true);
        assertEquals(TEST.isSupported(WEEK_OF_WEEK_BASED_YEAR), true);
    }

    //-----------------------------------------------------------------------
    // atDay(DayOfWeek)
    //-----------------------------------------------------------------------
    @Test(dataProvider = "sampleAtDay")
    public void test_atDay(int weekBasedYear, int weekOfWeekBasedYear, DayOfWeek dayOfWeek, int year, int month, int dayOfMonth) {
        YearWeek yearWeek = YearWeek.of(weekBasedYear, weekOfWeekBasedYear);
        LocalDate expected = LocalDate.of(year, month, dayOfMonth);
        LocalDate actual = yearWeek.atDay(dayOfWeek);
        assertEquals(actual, expected);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_AtDay_null() {
        TEST.atDay(null);
    }

    //-----------------------------------------------------------------------
    // is53WeekYear()
    //-----------------------------------------------------------------------
    @Test(dataProvider = "53WeekYear")
    public void test_is53WeekYear(int year) {
        YearWeek yearWeek = YearWeek.of(year, 1);
        assertTrue(yearWeek.is53WeekYear());
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int year1 = -100; year1 < 100; year1++) {
            for (int week1 = 1; week1 < 53; week1++) {
                YearWeek a = YearWeek.of(year1, week1);
                for (int year2 = -100; year2 < 100; year2++) {
                    for (int week2 = 1; week2 < 53; week2++) {
                        YearWeek b = YearWeek.of(year2, week2);
                        if (year1 < year2) {
                            assertEquals(a.compareTo(b) < 0, true);
                            assertEquals(b.compareTo(a) > 0, true);
                            assertEquals(a.isAfter(b), false);
                            assertEquals(b.isBefore(a), false);
                            assertEquals(b.isAfter(a), true);
                            assertEquals(a.isBefore(b), true);
                        } else if (year1 > year2) {
                            assertEquals(a.compareTo(b) > 0, true);
                            assertEquals(b.compareTo(a) < 0, true);
                            assertEquals(a.isAfter(b), true);
                            assertEquals(b.isBefore(a), true);
                            assertEquals(b.isAfter(a), false);
                            assertEquals(a.isBefore(b), false);
                        } else {
                            if (week1 < week2) {
                                assertEquals(a.compareTo(b) < 0, true);
                                assertEquals(b.compareTo(a) > 0, true);
                                assertEquals(a.isAfter(b), false);
                                assertEquals(b.isBefore(a), false);
                                assertEquals(b.isAfter(a), true);
                                assertEquals(a.isBefore(b), true);
                            } else if (week1 > week2) {
                                assertEquals(a.compareTo(b) > 0, true);
                                assertEquals(b.compareTo(a) < 0, true);
                                assertEquals(a.isAfter(b), true);
                                assertEquals(b.isBefore(a), true);
                                assertEquals(b.isAfter(a), false);
                                assertEquals(a.isBefore(b), false);
                            } else {
                                assertEquals(a.compareTo(b), 0);
                                assertEquals(b.compareTo(a), 0);
                                assertEquals(a.isAfter(b), false);
                                assertEquals(b.isBefore(a), false);
                                assertEquals(b.isAfter(a), false);
                                assertEquals(a.isBefore(b), false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_nullYearWeek() {
        TEST.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test(dataProvider = "sampleAtDay")
    public void test_from(int weekBasedYear, int weekOfWeekBasedYear, DayOfWeek dayOfWeek, int year, int month, int dayOfMonth) {
        YearWeek actual = YearWeek.of(weekBasedYear, weekOfWeekBasedYear);
        LocalDate ld = LocalDate.of(year, month, dayOfMonth);
        assertEquals(actual, YearWeek.from(ld));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_from_TemporalAccessor_noDerive() {
        YearWeek.from(LocalTime.NOON);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_from_TemporalAccessor_null() {
        YearWeek.from((TemporalAccessor) null);
    }

    //-----------------------------------------------------------------------
    // get(TemporalField)
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(TEST.get(WEEK_BASED_YEAR), 2015);
        assertEquals(TEST.get(WEEK_OF_WEEK_BASED_YEAR), 1);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_get_invalidField() {
        TEST.get(YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_get_null() {
        TEST.get((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // getLong(TemporalField)
    //-----------------------------------------------------------------------
    public void test_getLong() {
        assertEquals(TEST.getLong(WEEK_BASED_YEAR), 2015L);
        assertEquals(TEST.getLong(WEEK_OF_WEEK_BASED_YEAR), 1L);
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_getLong_invalidField() {
        TEST.getLong(YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getLong_null() {
        TEST.getLong((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // lengthOfYear()
    //-----------------------------------------------------------------------
    public void test_lengthOfYear() {
        assertEquals(YearWeek.of(2014, 1).lengthOfYear(), 364);
        assertEquals(YearWeek.of(2015, 1).lengthOfYear(), 371);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(TEST.toString(), "2015-W01");
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence)
    //-----------------------------------------------------------------------
    public void test_parse_CharSequence() {
        assertEquals(YearWeek.parse("2015-W01"), TEST);
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequenceDate_invalidYear() {
        YearWeek.parse("12345-W7");
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequenceDate_invalidWeek() {
        YearWeek.parse("2015-W54");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceDate_nullCharSequence() {
        YearWeek.parse((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void test_parse_CharSequenceDateTimeFormatter() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E 'W'w YYYY");
        assertEquals(YearWeek.parse("Mon W1 2015", f), TEST);
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequenceDateDateTimeFormatter_invalidWeek() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E 'W'w YYYY");
        YearWeek.parse("Mon W99 2015", f);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceDateTimeFormatter_nullCharSequence() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E 'W'w YYYY");
        YearWeek.parse((CharSequence) null, f);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceDateTimeFormatter_nullDateTimeFormatter() {
        YearWeek.parse("", (DateTimeFormatter) null);
    }

    //-----------------------------------------------------------------------
    // range(TemporalField)
    //-----------------------------------------------------------------------
    public void test_range() {
        assertEquals(TEST.range(WEEK_OF_WEEK_BASED_YEAR), WEEK_OF_WEEK_BASED_YEAR.range());
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_invalidField() {
        TEST.range(YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_range_null() {
        TEST.range((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // withYear(int)
    //-----------------------------------------------------------------------
    public void test_withYear() {
        assertEquals(YearWeek.of(2015, 1).withYear(2014), YearWeek.of(2014, 1));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withYear_int_max() {
        TEST.withYear(Integer.MAX_VALUE);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withYear_int_min() {
        TEST.withYear(Integer.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // withWeek(int)
    //-----------------------------------------------------------------------
    public void test_WithWeek() {
        assertEquals(TEST.withWeek(52), YearWeek.of(2015, 52));
        assertEquals(YearWeek.of(2014, 1).withWeek(53), TEST);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withWeek_int_max() {
        TEST.withWeek(Integer.MAX_VALUE);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withWeek_int_min() {
        TEST.withWeek(Integer.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // query(TemporalQuery)
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertEquals(TEST.query(TemporalQueries.chronology()), IsoChronology.INSTANCE);
        assertEquals(TEST.query(TemporalQueries.localDate()), null);
        assertEquals(TEST.query(TemporalQueries.localTime()), null);
        assertEquals(TEST.query(TemporalQueries.offset()), null);
        assertEquals(TEST.query(TemporalQueries.precision()), null);
        assertEquals(TEST.query(TemporalQueries.zone()), null);
        assertEquals(TEST.query(TemporalQueries.zoneId()), null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider = "sampleYearWeeks")
    public void test_equalsAndHashCodeContract(int year, int week) {
        YearWeek a = YearWeek.of(year, week);
        YearWeek b = YearWeek.of(year, week);
        assertTrue(a.equals(null) == false);
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertTrue(a.hashCode() == b.hashCode());
    }

    public void test_equals_incorrectType() {
        assertEquals(TEST.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name = "sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][]{
            {2015, 1, "2015-W01"},
            {2015, 10, "2015-W10"},
            {999, 1, "0999-W01"},
            {-999, 1, "-0999-W01"},
            {10000, 1, "+10000-W01"},
            {-10000, 1, "-10000-W01"},};
    }

    @Test(dataProvider = "sampleToString")
    public void test_toString(int year, int week, String expected) {
        YearWeek yearWeek = YearWeek.of(year, week);
        String s = yearWeek.toString();
        assertEquals(s, expected);
    }

}
