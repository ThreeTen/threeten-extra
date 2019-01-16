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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

import org.junit.Test;

/**
 * Test DayOfYear.
 */
public class TestDayOfYear {

    private static final Year YEAR_STANDARD = Year.of(2007);
    private static final Year YEAR_LEAP = Year.of(2008);
    private static final int STANDARD_YEAR_LENGTH = 365;
    private static final int LEAP_YEAR_LENGTH = 366;
    private static final DayOfYear TEST = DayOfYear.of(12);
    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");

    private static class TestingField implements TemporalField {

        public static final TestingField INSTANCE = new TestingField();

        @Override
        public TemporalUnit getBaseUnit() {
            return ChronoUnit.DAYS;
        }

        @Override
        public TemporalUnit getRangeUnit() {
            return ChronoUnit.YEARS;
        }

        @Override
        public ValueRange range() {
            return ValueRange.of(1, 365, 366);
        }

        @Override
        public boolean isDateBased() {
            return true;
        }

        @Override
        public boolean isTimeBased() {
            return false;
        }

        @Override
        public boolean isSupportedBy(TemporalAccessor temporal) {
            return temporal.isSupported(DAY_OF_YEAR);
        }

        @Override
        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            return range();
        }

        @Override
        public long getFrom(TemporalAccessor temporal) {
            return temporal.getLong(DAY_OF_YEAR);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R extends Temporal> R adjustInto(R temporal, long newValue) {
            return (R) temporal.with(DAY_OF_YEAR, newValue);
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(DayOfYear.class));
        assertTrue(Comparable.class.isAssignableFrom(DayOfYear.class));
        assertTrue(TemporalAdjuster.class.isAssignableFrom(DayOfYear.class));
        assertTrue(TemporalAccessor.class.isAssignableFrom(DayOfYear.class));
    }

    @Test
    public void test_serialization() throws IOException, ClassNotFoundException {
        DayOfYear test = DayOfYear.of(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(test, ois.readObject());
        }
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @Test
    public void test_now() {
        DayOfYear test = DayOfYear.now();
        if (LocalDate.now().getDayOfYear() != test.getValue()) {
            test = DayOfYear.now();
        }
        assertEquals(LocalDate.now().getDayOfYear(), test.getValue());
    }

    //-----------------------------------------------------------------------
    // now(ZoneId)
    //-----------------------------------------------------------------------
    @Test
    public void test_now_ZoneId() {
        ZoneId zone = ZoneId.of("Asia/Tokyo");
        DayOfYear test = DayOfYear.now(zone);
        if (LocalDate.now(zone).getDayOfYear() != test.getValue()) {
            test = DayOfYear.now(zone);
        }
        assertEquals(LocalDate.now(zone).getDayOfYear(), test.getValue());
    }

    //-----------------------------------------------------------------------
    // of(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(i, test.getValue());
            assertEquals(test, DayOfYear.of(i));
        }
    }

    @Test(expected = DateTimeException.class)
    public void test_of_int_tooLow() {
        DayOfYear.of(0);
    }

    @Test(expected = DateTimeException.class)
    public void test_of_int_tooHigh() {
        DayOfYear.of(367);
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor_notLeapYear() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.from(date);
            assertEquals(i, test.getValue());
            date = date.plusDays(1);
        }
        DayOfYear test = DayOfYear.from(date);
        assertEquals(1, test.getValue());
    }

    @Test
    public void test_from_TemporalAccessor_leapYear() {
        LocalDate date = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.from(date);
            assertEquals(i, test.getValue());
            date = date.plusDays(1);
        }
    }

    @Test
    public void test_from_TemporalAccessor_DayOfYear() {
        DayOfYear dom = DayOfYear.of(6);
        assertEquals(dom, DayOfYear.from(dom));
    }

    @Test
    public void test_from_TemporalAccessor_nonIso() {
        LocalDate date = LocalDate.now();
        assertEquals(date.getDayOfYear(), DayOfYear.from(JapaneseDate.from(date)).getValue());
    }

    @Test(expected = DateTimeException.class)
    public void test_from_TemporalAccessor_noDerive() {
        DayOfYear.from(LocalTime.NOON);
    }

    @Test(expected = NullPointerException.class)
    public void test_from_TemporalAccessor_null() {
        DayOfYear.from((TemporalAccessor) null);
    }

    @Test
    public void test_from_parse_CharSequence() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("D");
        assertEquals(DayOfYear.of(76), formatter.parse("76", DayOfYear::from));
    }

    //-----------------------------------------------------------------------
    // isSupported(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_isSupported() {
        assertEquals(false, TEST.isSupported((TemporalField) null));
        assertEquals(false, TEST.isSupported(NANO_OF_SECOND));
        assertEquals(false, TEST.isSupported(NANO_OF_DAY));
        assertEquals(false, TEST.isSupported(MICRO_OF_SECOND));
        assertEquals(false, TEST.isSupported(MICRO_OF_DAY));
        assertEquals(false, TEST.isSupported(MILLI_OF_SECOND));
        assertEquals(false, TEST.isSupported(MILLI_OF_DAY));
        assertEquals(false, TEST.isSupported(SECOND_OF_MINUTE));
        assertEquals(false, TEST.isSupported(SECOND_OF_DAY));
        assertEquals(false, TEST.isSupported(MINUTE_OF_HOUR));
        assertEquals(false, TEST.isSupported(MINUTE_OF_DAY));
        assertEquals(false, TEST.isSupported(HOUR_OF_AMPM));
        assertEquals(false, TEST.isSupported(CLOCK_HOUR_OF_AMPM));
        assertEquals(false, TEST.isSupported(HOUR_OF_DAY));
        assertEquals(false, TEST.isSupported(CLOCK_HOUR_OF_DAY));
        assertEquals(false, TEST.isSupported(AMPM_OF_DAY));
        assertEquals(false, TEST.isSupported(DAY_OF_WEEK));
        assertEquals(false, TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_MONTH));
        assertEquals(false, TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_YEAR));
        assertEquals(false, TEST.isSupported(DAY_OF_MONTH));
        assertEquals(true, TEST.isSupported(DAY_OF_YEAR));
        assertEquals(false, TEST.isSupported(EPOCH_DAY));
        assertEquals(false, TEST.isSupported(ALIGNED_WEEK_OF_MONTH));
        assertEquals(false, TEST.isSupported(ALIGNED_WEEK_OF_YEAR));
        assertEquals(false, TEST.isSupported(MONTH_OF_YEAR));
        assertEquals(false, TEST.isSupported(PROLEPTIC_MONTH));
        assertEquals(false, TEST.isSupported(YEAR_OF_ERA));
        assertEquals(false, TEST.isSupported(YEAR));
        assertEquals(false, TEST.isSupported(ERA));
        assertEquals(false, TEST.isSupported(INSTANT_SECONDS));
        assertEquals(false, TEST.isSupported(OFFSET_SECONDS));
        assertEquals(true, TEST.isSupported(TestingField.INSTANCE));
    }

    //-----------------------------------------------------------------------
    // range(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_range() {
        assertEquals(DAY_OF_YEAR.range(), TEST.range(DAY_OF_YEAR));
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void test_range_invalidField() {
        TEST.range(MONTH_OF_YEAR);
    }

    @Test(expected = NullPointerException.class)
    public void test_range_null() {
        TEST.range((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // get(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertEquals(12, TEST.get(DAY_OF_YEAR));
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void test_get_invalidField() {
        TEST.get(MONTH_OF_YEAR);
    }

    @Test(expected = NullPointerException.class)
    public void test_get_null() {
        TEST.get((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // getLong(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_getLong() {
        assertEquals(12L, TEST.getLong(DAY_OF_YEAR));
    }

    @Test
    public void test_getLong_derivedField() {
        assertEquals(12L, TEST.getLong(TestingField.INSTANCE));
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void test_getLong_invalidField() {
        TEST.getLong(MONTH_OF_YEAR);
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void test_getLong_invalidField2() {
        TEST.getLong(IsoFields.DAY_OF_QUARTER);
    }

    @Test(expected = NullPointerException.class)
    public void test_getLong_null() {
        TEST.getLong((TemporalField) null);
    }

    //-----------------------------------------------------------------------
    // isValidYear(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_isValidYear_366() {
        DayOfYear test = DayOfYear.of(366);
        assertEquals(false, test.isValidYear(2011));
        assertEquals(true, test.isValidYear(2012));
        assertEquals(false, test.isValidYear(2013));
    }

    @Test
    public void test_isValidYear_365() {
        DayOfYear test = DayOfYear.of(365);
        assertEquals(true, test.isValidYear(2011));
        assertEquals(true, test.isValidYear(2012));
        assertEquals(true, test.isValidYear(2013));
    }

    //-----------------------------------------------------------------------
    // query(TemporalQuery)
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertEquals(IsoChronology.INSTANCE, TEST.query(TemporalQueries.chronology()));
        assertEquals(null, TEST.query(TemporalQueries.localDate()));
        assertEquals(null, TEST.query(TemporalQueries.localTime()));
        assertEquals(null, TEST.query(TemporalQueries.offset()));
        assertEquals(null, TEST.query(TemporalQueries.precision()));
        assertEquals(null, TEST.query(TemporalQueries.zone()));
        assertEquals(null, TEST.query(TemporalQueries.zoneId()));
    }

    //-----------------------------------------------------------------------
    // adjustInto(Temporal)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjustInto_fromStartOfYear_notLeapYear() {
        LocalDate base = LocalDate.of(2007, 1, 1);
        LocalDate expected = base;
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(expected, test.adjustInto(base));
            expected = expected.plusDays(1);
        }
    }

    @Test
    public void test_adjustInto_fromEndOfYear_notLeapYear() {
        LocalDate base = LocalDate.of(2007, 12, 31);
        LocalDate expected = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(expected, test.adjustInto(base));
            expected = expected.plusDays(1);
        }
    }

    @Test(expected = DateTimeException.class)
    public void test_adjustInto_fromStartOfYear_notLeapYear_day366() {
        LocalDate base = LocalDate.of(2007, 1, 1);
        DayOfYear test = DayOfYear.of(LEAP_YEAR_LENGTH);
        test.adjustInto(base);
    }

    @Test(expected = DateTimeException.class)
    public void test_adjustInto_fromEndOfYear_notLeapYear_day366() {
        LocalDate base = LocalDate.of(2007, 12, 31);
        DayOfYear test = DayOfYear.of(LEAP_YEAR_LENGTH);
        test.adjustInto(base);
    }

    @Test
    public void test_adjustInto_fromStartOfYear_leapYear() {
        LocalDate base = LocalDate.of(2008, 1, 1);
        LocalDate expected = base;
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(expected, test.adjustInto(base));
            expected = expected.plusDays(1);
        }
    }

    @Test
    public void test_adjustInto_fromEndOfYear_leapYear() {
        LocalDate base = LocalDate.of(2008, 12, 31);
        LocalDate expected = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(expected, test.adjustInto(base));
            expected = expected.plusDays(1);
        }
    }

    @Test(expected = DateTimeException.class)
    public void test_adjustInto_nonIso() {
        TEST.adjustInto(JapaneseDate.now());
    }

    @Test(expected = NullPointerException.class)
    public void test_adjustInto_null() {
        TEST.adjustInto((Temporal) null);
    }

    //-----------------------------------------------------------------------
    // atYear(Year)
    //-----------------------------------------------------------------------
    @Test
    public void test_atYear_Year_notLeapYear() {
        LocalDate expected = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(expected, test.atYear(YEAR_STANDARD));
            expected = expected.plusDays(1);
        }
    }

    @Test(expected = DateTimeException.class)
    public void test_atYear_fromStartOfYear_notLeapYear_day366() {
        DayOfYear test = DayOfYear.of(LEAP_YEAR_LENGTH);
        test.atYear(YEAR_STANDARD);
    }

    @Test
    public void test_atYear_Year_leapYear() {
        LocalDate expected = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(expected, test.atYear(YEAR_LEAP));
            expected = expected.plusDays(1);
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_atYear_Year_nullYear() {
        TEST.atYear((Year) null);
    }

    //-----------------------------------------------------------------------
    // atYear(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_atYear_int_notLeapYear() {
        LocalDate expected = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(expected, test.atYear(2007));
            expected = expected.plusDays(1);
        }
    }

    @Test(expected = DateTimeException.class)
    public void test_atYear_int_fromStartOfYear_notLeapYear_day366() {
        DayOfYear test = DayOfYear.of(LEAP_YEAR_LENGTH);
        test.atYear(2007);
    }

    @Test
    public void test_atYear_int_leapYear() {
        LocalDate expected = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear test = DayOfYear.of(i);
            assertEquals(expected, test.atYear(2008));
            expected = expected.plusDays(1);
        }
    }

    @Test(expected = DateTimeException.class)
    public void test_atYear_int_invalidDay() {
        TEST.atYear(Year.MIN_VALUE - 1);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear a = DayOfYear.of(i);
            for (int j = 1; j <= LEAP_YEAR_LENGTH; j++) {
                DayOfYear b = DayOfYear.of(j);
                if (i < j) {
                    assertEquals(true, a.compareTo(b) < 0);
                    assertEquals(true, b.compareTo(a) > 0);
                } else if (i > j) {
                    assertEquals(true, a.compareTo(b) > 0);
                    assertEquals(true, b.compareTo(a) < 0);
                } else {
                    assertEquals(0, a.compareTo(b));
                    assertEquals(0, b.compareTo(a));
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_compareTo_nullDayOfYear() {
        DayOfYear doy = null;
        DayOfYear test = DayOfYear.of(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear a = DayOfYear.of(i);
            for (int j = 1; j <= LEAP_YEAR_LENGTH; j++) {
                DayOfYear b = DayOfYear.of(j);
                assertEquals(i == j, a.equals(b));
                assertEquals(i == j, a.hashCode() == b.hashCode());
            }
        }
    }

    @Test
    public void test_equals_nullDayOfYear() {
        DayOfYear doy = null;
        DayOfYear test = DayOfYear.of(1);
        assertEquals(false, test.equals(doy));
    }

    @Test
    public void test_equals_incorrectType() {
        DayOfYear test = DayOfYear.of(1);
        Object obj = "Incorrect type";
        assertEquals(false, test.equals(obj));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            DayOfYear a = DayOfYear.of(i);
            assertEquals("DayOfYear:" + i, a.toString());
        }
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test
    public void test_now_clock_notLeapYear() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            Instant instant = date.atStartOfDay(PARIS).toInstant();
            Clock clock = Clock.fixed(instant, PARIS);
            DayOfYear test = DayOfYear.now(clock);
            assertEquals(i, test.getValue());
            date = date.plusDays(1);
        }
    }

    @Test
    public void test_now_clock_leapYear() {
        LocalDate date = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            Instant instant = date.atStartOfDay(PARIS).toInstant();
            Clock clock = Clock.fixed(instant, PARIS);
            DayOfYear test = DayOfYear.now(clock);
            assertEquals(i, test.getValue());
            date = date.plusDays(1);
        }
    }

}
