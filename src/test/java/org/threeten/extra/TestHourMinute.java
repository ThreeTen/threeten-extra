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
import static java.time.temporal.ChronoUnit.CENTURIES;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.HALF_DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLENNIA;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.time.temporal.IsoFields.DAY_OF_QUARTER;
import static java.time.temporal.IsoFields.QUARTER_OF_YEAR;
import static java.time.temporal.IsoFields.QUARTER_YEARS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.threeten.extra.TemporalFields.DAY_OF_HALF;
import static org.threeten.extra.TemporalFields.HALF_OF_YEAR;
import static org.threeten.extra.TemporalFields.HALF_YEARS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

/**
 * Test HourMinute.
 */
public class TestHourMinute {

    private static final HourMinute TEST = HourMinute.of(12, 31);

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertThat(HourMinute.class)
                .isAssignableTo(Serializable.class)
                .isAssignableTo(Comparable.class)
                .isAssignableTo(TemporalAdjuster.class)
                .isAssignableTo(TemporalAccessor.class);
    }

    @Test
    public void test_serialization() throws IOException, ClassNotFoundException {
        HourMinute test = HourMinute.of(12, 31);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertThat(ois.readObject()).isEqualTo(test);
        }
    }

    //-----------------------------------------------------------------------
    // of(int,int) / getters
    //-----------------------------------------------------------------------
    @Test
    public void test_of_int_int() {
        for (int hour = 0; hour <= 23; hour++) {
            for (int minute = 0; minute <= 59; minute++) {
                HourMinute test = HourMinute.of(hour, minute);
                assertThat(test.getHour()).isEqualTo(hour);
                assertThat(test.getMinute()).isEqualTo(minute);
                assertThat(test)
                        .isEqualTo(HourMinute.of(hour, minute))
                        .hasSameHashCodeAs(HourMinute.of(hour, minute));
            }
        }
    }

    @Test
    public void test_of_int_int_invalid() {
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> HourMinute.of(-1, 0));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> HourMinute.of(24, 0));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> HourMinute.of(1, -1));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> HourMinute.of(1, 60));
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor() {
        for (int hour = 0; hour <= 23; hour++) {
            for (int minute = 0; minute <= 59; minute++) {
                HourMinute expected = HourMinute.of(hour, minute);
                assertThat(HourMinute.from(expected)).isEqualTo(expected);
                assertThat(HourMinute.from(LocalTime.of(hour, minute))).isEqualTo(expected);
                assertThat(HourMinute.from(LocalDateTime.of(2020, 6, 3, hour, minute))).isEqualTo(expected);
            }
        }
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> HourMinute.from(LocalDate.of(2020, 6, 3)));
        assertThatNullPointerException().isThrownBy(() -> HourMinute.from((TemporalAccessor) null));
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence)
    //-----------------------------------------------------------------------
    static Object[][] data_parse_CharSequence() {
        return new Object[][] {
                {HourMinute.of(0, 0), "00:00"},
                {HourMinute.of(9, 9), "09:09"},
                {HourMinute.of(10, 10), "10:10"},
                {HourMinute.of(19, 59), "19:59"},
                {HourMinute.of(23, 59), "23:59"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_parse_CharSequence")
    public void test_parse_CharSequence(HourMinute hourMin, String str) {
        assertThat(HourMinute.parse(str)).isEqualTo(hourMin);
        assertThat(hourMin).hasToString(str);
    }

    @Test
    public void test_parse_CharSequence_invalid() {
        assertThatNullPointerException().isThrownBy(() -> HourMinute.parse((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequenceDateTimeFormatter() {
        assertThat(HourMinute.parse("23:30+01:00", DateTimeFormatter.ISO_OFFSET_TIME)).isEqualTo(HourMinute.of(23, 30));
    }

    @Test
    public void test_parse_CharSequenceDateTimeFormatter_invalid() {
        assertThatNullPointerException().isThrownBy(() -> HourMinute.parse((CharSequence) null, DateTimeFormatter.ISO_OFFSET_TIME));
        assertThatNullPointerException().isThrownBy(() -> HourMinute.parse("23:59", (DateTimeFormatter) null));
    }

    //-----------------------------------------------------------------------
    // isSupported(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_isSupported_TemporalField() {
        assertThat(TEST.isSupported((TemporalField) null)).isFalse();
        assertThat(TEST.isSupported(NANO_OF_SECOND)).isFalse();
        assertThat(TEST.isSupported(NANO_OF_DAY)).isFalse();
        assertThat(TEST.isSupported(MICRO_OF_SECOND)).isFalse();
        assertThat(TEST.isSupported(MICRO_OF_DAY)).isFalse();
        assertThat(TEST.isSupported(MILLI_OF_SECOND)).isFalse();
        assertThat(TEST.isSupported(MILLI_OF_DAY)).isFalse();
        assertThat(TEST.isSupported(SECOND_OF_MINUTE)).isFalse();
        assertThat(TEST.isSupported(SECOND_OF_DAY)).isFalse();
        assertThat(TEST.isSupported(MINUTE_OF_HOUR)).isTrue();
        assertThat(TEST.isSupported(MINUTE_OF_DAY)).isTrue();
        assertThat(TEST.isSupported(HOUR_OF_AMPM)).isTrue();
        assertThat(TEST.isSupported(CLOCK_HOUR_OF_AMPM)).isTrue();
        assertThat(TEST.isSupported(HOUR_OF_DAY)).isTrue();
        assertThat(TEST.isSupported(CLOCK_HOUR_OF_DAY)).isTrue();
        assertThat(TEST.isSupported(AMPM_OF_DAY)).isTrue();
        assertThat(TEST.isSupported(DAY_OF_WEEK)).isFalse();
        assertThat(TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_MONTH)).isFalse();
        assertThat(TEST.isSupported(ALIGNED_DAY_OF_WEEK_IN_YEAR)).isFalse();
        assertThat(TEST.isSupported(DAY_OF_MONTH)).isFalse();
        assertThat(TEST.isSupported(DAY_OF_YEAR)).isFalse();
        assertThat(TEST.isSupported(EPOCH_DAY)).isFalse();
        assertThat(TEST.isSupported(ALIGNED_WEEK_OF_MONTH)).isFalse();
        assertThat(TEST.isSupported(ALIGNED_WEEK_OF_YEAR)).isFalse();
        assertThat(TEST.isSupported(MONTH_OF_YEAR)).isFalse();
        assertThat(TEST.isSupported(PROLEPTIC_MONTH)).isFalse();
        assertThat(TEST.isSupported(YEAR_OF_ERA)).isFalse();
        assertThat(TEST.isSupported(YEAR)).isFalse();
        assertThat(TEST.isSupported(ERA)).isFalse();
        assertThat(TEST.isSupported(INSTANT_SECONDS)).isFalse();
        assertThat(TEST.isSupported(OFFSET_SECONDS)).isFalse();
        assertThat(TEST.isSupported(QUARTER_OF_YEAR)).isFalse();
        assertThat(TEST.isSupported(DAY_OF_QUARTER)).isFalse();
        assertThat(TEST.isSupported(HALF_OF_YEAR)).isFalse();
        assertThat(TEST.isSupported(DAY_OF_HALF)).isFalse();
    }

    //-----------------------------------------------------------------------
    // isSupported(TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_isSupported_TemporalUnit() {
        assertThat(TEST.isSupported((TemporalUnit) null)).isFalse();
        assertThat(TEST.isSupported(NANOS)).isFalse();
        assertThat(TEST.isSupported(MICROS)).isFalse();
        assertThat(TEST.isSupported(MILLIS)).isFalse();
        assertThat(TEST.isSupported(SECONDS)).isFalse();
        assertThat(TEST.isSupported(MINUTES)).isTrue();
        assertThat(TEST.isSupported(HOURS)).isTrue();
        assertThat(TEST.isSupported(HALF_DAYS)).isTrue();
        assertThat(TEST.isSupported(DAYS)).isFalse();
        assertThat(TEST.isSupported(WEEKS)).isFalse();
        assertThat(TEST.isSupported(MONTHS)).isFalse();
        assertThat(TEST.isSupported(YEARS)).isFalse();
        assertThat(TEST.isSupported(DECADES)).isFalse();
        assertThat(TEST.isSupported(CENTURIES)).isFalse();
        assertThat(TEST.isSupported(MILLENNIA)).isFalse();
        assertThat(TEST.isSupported(ERA)).isFalse();
        assertThat(TEST.isSupported(FOREVER)).isFalse();
        assertThat(TEST.isSupported(QUARTER_YEARS)).isFalse();
        assertThat(TEST.isSupported(HALF_YEARS)).isFalse();
    }

    //-----------------------------------------------------------------------
    // range(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_range() {
        assertThat(TEST.range(MINUTE_OF_HOUR)).isEqualTo(MINUTE_OF_HOUR.range());
        assertThat(TEST.range(MINUTE_OF_DAY)).isEqualTo(MINUTE_OF_DAY.range());
        assertThat(TEST.range(HOUR_OF_DAY)).isEqualTo(HOUR_OF_DAY.range());
        assertThat(TEST.range(CLOCK_HOUR_OF_DAY)).isEqualTo(CLOCK_HOUR_OF_DAY.range());
        assertThat(TEST.range(HOUR_OF_AMPM)).isEqualTo(HOUR_OF_AMPM.range());
        assertThat(TEST.range(CLOCK_HOUR_OF_AMPM)).isEqualTo(CLOCK_HOUR_OF_AMPM.range());
        assertThat(TEST.range(AMPM_OF_DAY)).isEqualTo(AMPM_OF_DAY.range());
    }

    @Test
    public void test_range_invalid() {
        assertThatExceptionOfType(UnsupportedTemporalTypeException.class).isThrownBy(() -> TEST.range(SECOND_OF_MINUTE));
        assertThatExceptionOfType(UnsupportedTemporalTypeException.class).isThrownBy(() -> TEST.range(NANO_OF_SECOND));
        assertThatNullPointerException().isThrownBy(() -> TEST.range((TemporalField) null));
    }

    //-----------------------------------------------------------------------
    // get(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_get() {
        assertThat(TEST.get(MINUTE_OF_HOUR)).isEqualTo(31);
        assertThat(TEST.get(MINUTE_OF_DAY)).isEqualTo(12 * 60 + 31);
        assertThat(TEST.get(HOUR_OF_DAY)).isEqualTo(12);
        assertThat(TEST.get(CLOCK_HOUR_OF_DAY)).isEqualTo(12);
        assertThat(TEST.get(HOUR_OF_AMPM)).isEqualTo(0);
        assertThat(TEST.get(CLOCK_HOUR_OF_AMPM)).isEqualTo(12);
        assertThat(TEST.get(AMPM_OF_DAY)).isEqualTo(1);
    }

    @Test
    public void test_get_invalid() {
        assertThatExceptionOfType(UnsupportedTemporalTypeException.class).isThrownBy(() -> TEST.get(SECOND_OF_MINUTE));
        assertThatExceptionOfType(UnsupportedTemporalTypeException.class).isThrownBy(() -> TEST.get(NANO_OF_SECOND));
        assertThatNullPointerException().isThrownBy(() -> TEST.get((TemporalField) null));
    }

    //-----------------------------------------------------------------------
    // getLong(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_getLong() {
        assertThat(TEST.getLong(MINUTE_OF_HOUR)).isEqualTo(31);
        assertThat(TEST.getLong(MINUTE_OF_DAY)).isEqualTo(12 * 60 + 31);
        assertThat(TEST.getLong(HOUR_OF_DAY)).isEqualTo(12);
        assertThat(TEST.getLong(CLOCK_HOUR_OF_DAY)).isEqualTo(12);
        assertThat(TEST.getLong(HOUR_OF_AMPM)).isEqualTo(0);
        assertThat(TEST.getLong(CLOCK_HOUR_OF_AMPM)).isEqualTo(12);
        assertThat(TEST.getLong(AMPM_OF_DAY)).isEqualTo(1);
    }

    @Test
    public void test_getLong_invalid() {
        assertThatExceptionOfType(UnsupportedTemporalTypeException.class).isThrownBy(() -> TEST.getLong(SECOND_OF_MINUTE));
        assertThatExceptionOfType(UnsupportedTemporalTypeException.class).isThrownBy(() -> TEST.getLong(NANO_OF_SECOND));
        assertThatNullPointerException().isThrownBy(() -> TEST.getLong((TemporalField) null));
    }

    //-----------------------------------------------------------------------
    // with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_TemporalAdjuster() {
        assertThat(TEST.with(HourMinute.of(9, 10))).isEqualTo(HourMinute.of(9, 10));
        assertThat(TEST.with(AmPm.AM)).isEqualTo(HourMinute.of(0, 31));
    }

    @Test
    public void test_with_TemporalAdjuster_invalid() {
        assertThatExceptionOfType(UnsupportedTemporalTypeException.class).isThrownBy(() -> TEST.with(LocalTime.of(9, 10, 11)));
        assertThatNullPointerException().isThrownBy(() -> TEST.with((TemporalAdjuster) null));
    }

    //-----------------------------------------------------------------------
    // with(TemporalField, long)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_TemporalFieldlong() {
        assertThat(TEST.with(MINUTE_OF_HOUR, 2)).isEqualTo(HourMinute.of(12, 2));
        assertThat(TEST.with(MINUTE_OF_DAY, 2)).isEqualTo(HourMinute.of(0, 2));
        assertThat(TEST.with(HOUR_OF_DAY, 2)).isEqualTo(HourMinute.of(2, 31));
        assertThat(TEST.with(CLOCK_HOUR_OF_DAY, 2)).isEqualTo(HourMinute.of(2, 31));
        assertThat(TEST.with(HOUR_OF_AMPM, 2)).isEqualTo(HourMinute.of(14, 31));
        assertThat(TEST.with(CLOCK_HOUR_OF_AMPM, 2)).isEqualTo(HourMinute.of(14, 31));
        assertThat(TEST.with(AMPM_OF_DAY, 0)).isEqualTo(HourMinute.of(0, 31));
    }

    @Test
    public void test_with_TemporalFieldlong_invalid() {
        assertThatExceptionOfType(UnsupportedTemporalTypeException.class).isThrownBy(() -> TEST.with(SECOND_OF_MINUTE, 1));
        assertThatExceptionOfType(UnsupportedTemporalTypeException.class).isThrownBy(() -> TEST.with(NANO_OF_SECOND, 1));
        assertThatNullPointerException().isThrownBy(() -> TEST.with((TemporalField) null, 1));
    }

    //-----------------------------------------------------------------------
    // withHour(int) / withMinute(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_int() {
        assertThat(TEST.withHour(9)).isEqualTo(HourMinute.of(9, 31));
        assertThat(TEST.withMinute(9)).isEqualTo(HourMinute.of(12, 9));
    }

    @Test
    public void test_with_int_invalid() {
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.withHour(-1));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.withHour(24));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.withMinute(-1));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.withMinute(60));
    }

    //-----------------------------------------------------------------------
    // plus(TemporalAmount)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_TemporalAmount() {
        assertThat(TEST.plus(Hours.of(3))).isEqualTo(HourMinute.of(15, 31));
        assertThat(TEST.plus(Minutes.of(3))).isEqualTo(HourMinute.of(12, 34));
    }

    @Test
    public void test_plus_TemporalAmount_invalid() {
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.plus(Days.of(1)));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.plus(Seconds.of(3)));
        assertThatNullPointerException().isThrownBy(() -> TEST.plus(null));
    }

    //-----------------------------------------------------------------------
    // plus(long,TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_longTemporalUnit() {
        assertThat(TEST.plus(3, HOURS)).isEqualTo(HourMinute.of(15, 31));
        assertThat(TEST.plus(3, MINUTES)).isEqualTo(HourMinute.of(12, 34));
        assertThat(TEST.plus(1, HALF_DAYS)).isEqualTo(HourMinute.of(0, 31));
    }

    @Test
    public void test_plus_longTemporalUnit_invalid() {
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.plus(1, DAYS));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.plus(1, SECONDS));
        assertThatNullPointerException().isThrownBy(() -> TEST.plus(1, null));
    }

    //-----------------------------------------------------------------------
    // plusHours(int) / plusMinutes(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_int() {
        assertThat(TEST.plusHours(3)).isEqualTo(HourMinute.of(15, 31));
        assertThat(TEST.plusMinutes(3)).isEqualTo(HourMinute.of(12, 34));
        assertThat(TEST.plusHours(-15)).isEqualTo(HourMinute.of(21, 31));
        assertThat(TEST.plusHours(25)).isEqualTo(HourMinute.of(13, 31));
    }

    //-----------------------------------------------------------------------
    // minus(TemporalAmount)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_TemporalAmount() {
        assertThat(TEST.minus(Hours.of(3))).isEqualTo(HourMinute.of(9, 31));
        assertThat(TEST.minus(Minutes.of(3))).isEqualTo(HourMinute.of(12, 28));
    }

    @Test
    public void test_minus_TemporalAmount_invalid() {
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.minus(Days.of(1)));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.minus(Seconds.of(3)));
        assertThatNullPointerException().isThrownBy(() -> TEST.minus(null));
    }

    //-----------------------------------------------------------------------
    // minus(long,TemporalUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_longTemporalUnit() {
        assertThat(TEST.minus(3, HOURS)).isEqualTo(HourMinute.of(9, 31));
        assertThat(TEST.minus(3, MINUTES)).isEqualTo(HourMinute.of(12, 28));
        assertThat(TEST.minus(1, HALF_DAYS)).isEqualTo(HourMinute.of(0, 31));
    }

    @Test
    public void test_minus_longTemporalUnit_invalid() {
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.minus(1, DAYS));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.minus(1, SECONDS));
        assertThatNullPointerException().isThrownBy(() -> TEST.minus(1, null));
    }

    //-----------------------------------------------------------------------
    // minusHours(int) / minusMinutes(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_int() {
        assertThat(TEST.minusHours(3)).isEqualTo(HourMinute.of(9, 31));
        assertThat(TEST.minusMinutes(3)).isEqualTo(HourMinute.of(12, 28));
        assertThat(TEST.minusHours(-15)).isEqualTo(HourMinute.of(3, 31));
        assertThat(TEST.minusHours(25)).isEqualTo(HourMinute.of(11, 31));
    }

    //-----------------------------------------------------------------------
    // query(TemporalQuery)
    //-----------------------------------------------------------------------
    @Test
    public void test_query() {
        assertThat(TEST.query(TemporalQueries.chronology())).isNull();
        assertThat(TEST.query(TemporalQueries.localDate())).isNull();
        assertThat(TEST.query(TemporalQueries.localTime())).isEqualTo(LocalTime.of(12, 31));
        assertThat(TEST.query(TemporalQueries.offset())).isNull();
        assertThat(TEST.query(TemporalQueries.precision())).isEqualTo(MINUTES);
        assertThat(TEST.query(TemporalQueries.zone())).isNull();
        assertThat(TEST.query(TemporalQueries.zoneId())).isNull();
    }

    //-----------------------------------------------------------------------
    // adjustInto(Temporal)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjustInto_Temporal() {
        assertThat(TEST.adjustInto(LocalDateTime.of(2020, 6, 3, 2, 4, 6))).isEqualTo(LocalDateTime.of(2020, 6, 3, 12, 31, 6));
    }

    @Test
    public void test_adjustInto_Temporal_invalid() {
        assertThatNullPointerException().isThrownBy(() -> TEST.adjustInto((Temporal) null));
    }

    //-----------------------------------------------------------------------
    // until(Temporal,TemporalUnit)
    //-----------------------------------------------------------------------
    static Object[][] data_until_TemporalTemporalUnit() {
        return new Object[][] {
                {HourMinute.of(12, 31), 0, 0},
                {HourMinute.of(12, 32), 0, 1},
                {HourMinute.of(13, 30), 0, 59},
                {HourMinute.of(13, 31), 1, 60},
                {HourMinute.of(13, 32), 1, 61},
                {HourMinute.of(12, 30), 0, -1},
                {HourMinute.of(11, 31), -1, -60},
        };
    }

    @ParameterizedTest
    @MethodSource("data_until_TemporalTemporalUnit")
    public void test_until_TemporalTemporalUnit(HourMinute target, int expectedHours, int expectedMins) {
        assertThat(TEST.until(target, HOURS)).isEqualTo(expectedHours);
        assertThat(TEST.until(target, MINUTES)).isEqualTo(expectedMins);
    }

    @Test
    public void test_until_TemporalTemporalUnit_invalid() {
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.until(HourMinute.of(0, 0), DAYS));
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> TEST.until(HourMinute.of(0, 0), SECONDS));
        assertThatNullPointerException().isThrownBy(() -> TEST.until(null, HOURS));
        assertThatNullPointerException().isThrownBy(() -> TEST.until(HourMinute.of(0, 0), null));
    }

    //-----------------------------------------------------------------------
    // format(DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void test_format() {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
                .appendLiteral("Hour ")
                .appendValue(HOUR_OF_DAY)
                .appendLiteral(" Minute ")
                .appendValue(MINUTE_OF_HOUR)
                .toFormatter();
        assertThat(TEST.format(f)).isEqualTo("Hour 12 Minute 31");
    }

    @Test
    public void test_format_null() {
        assertThatNullPointerException().isThrownBy(() -> TEST.format(null));
    }

    //-----------------------------------------------------------------------
    // atDate(LocalDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_atDate_LocalDate() {
        LocalDate date = LocalDate.of(2020, 6, 3);
        assertThat(TEST.atDate(date)).isEqualTo(LocalDateTime.of(date, LocalTime.of(12, 31)));
    }

    @Test
    public void test_atDate_LocalDate_invalid() {
        assertThatNullPointerException().isThrownBy(() -> TEST.atDate(null));
    }

    //-----------------------------------------------------------------------
    // atOffset(ZoneOffset)
    //-----------------------------------------------------------------------
    @Test
    public void test_atOffset_ZoneOffset() {
        ZoneOffset offset = ZoneOffset.ofHours(2);
        assertThat(TEST.atOffset(offset)).isEqualTo(OffsetTime.of(LocalTime.of(12, 31), offset));
    }

    @Test
    public void test_atOffset_ZoneOffset_invalid() {
        assertThatNullPointerException().isThrownBy(() -> TEST.atOffset(null));
    }

    //-----------------------------------------------------------------------
    // toLocalTime()
    //-----------------------------------------------------------------------
    @Test
    public void test_toLocalTime() {
        assertThat(TEST.toLocalTime()).isEqualTo(LocalTime.of(12, 31));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo() {
        for (int hour1 = 0; hour1 <= 23; hour1++) {
            for (int minute1 = 0; minute1 <= 59; minute1++) {
                HourMinute a = HourMinute.of(hour1, minute1);
                for (int hour2 = 0; hour2 <= 23; hour2++) {
                    for (int minute2 = 0; minute2 <= 59; minute2++) {
                        HourMinute b = HourMinute.of(hour2, minute2);
                        if (hour1 < hour2 || (hour1 == hour2 && minute1 < minute2)) {
                            assertThat(a).isLessThan(b);
                            assertThat(b).isGreaterThan(a);
                            assertThat(a.isAfter(b)).isFalse();
                            assertThat(a.isBefore(b)).isTrue();
                            assertThat(b.isAfter(a)).isTrue();
                            assertThat(b.isBefore(a)).isFalse();
                        } else if (hour1 > hour2 || (hour1 == hour2 && minute1 > minute2)) {
                            assertThat(a).isGreaterThan(b);
                            assertThat(b).isLessThan(a);
                            assertThat(a.isAfter(b)).isTrue();
                            assertThat(a.isBefore(b)).isFalse();
                            assertThat(b.isAfter(a)).isFalse();
                            assertThat(b.isBefore(a)).isTrue();
                        } else {
                            assertThat(a).isEqualByComparingTo(b);
                            assertThat(b).isEqualByComparingTo(a);
                            assertThat(a.isAfter(b)).isFalse();
                            assertThat(a.isBefore(b)).isFalse();
                            assertThat(b.isAfter(a)).isFalse();
                            assertThat(b.isBefore(a)).isFalse();
                        }
                    }
                }
            }
        }
    }

    @Test
    public void test_compareTo_nullHourMinute() {
        assertThatNullPointerException().isThrownBy(() -> TEST.compareTo(null));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
                .addEqualityGroup(HourMinute.of(1, 5), HourMinute.of(1, 5))
                .addEqualityGroup(HourMinute.of(14, 43), HourMinute.of(14, 43))
                .testEquals();
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertThat(HourMinute.of(0, 0)).hasToString("00:00");
        assertThat(HourMinute.of(0, 5)).hasToString("00:05");
        assertThat(HourMinute.of(9, 0)).hasToString("09:00");
        assertThat(HourMinute.of(23, 59)).hasToString("23:59");
    }

}
