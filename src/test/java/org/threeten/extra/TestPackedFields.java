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
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.testng.Assert.assertEquals;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import org.testng.annotations.Test;

/**
 * Test PackedFields.
 */
@Test
public class TestPackedFields {

    //-----------------------------------------------------------------------
    // packedDate()
    //-----------------------------------------------------------------------
    public void test_date_basics() {
        assertEquals(PackedFields.PACKED_DATE.toString(), "PackedDate");
        assertEquals(PackedFields.PACKED_DATE.getBaseUnit(), DAYS);
        assertEquals(PackedFields.PACKED_DATE.getRangeUnit(), FOREVER);
        assertEquals(PackedFields.PACKED_DATE.isDateBased(), true);
        assertEquals(PackedFields.PACKED_DATE.isTimeBased(), false);
        assertEquals(PackedFields.PACKED_DATE.isSupportedBy(LocalDate.of(2015, 3, 12)), true);
        assertEquals(PackedFields.PACKED_DATE.isSupportedBy(LocalTime.of(11, 30)), false);
        assertEquals(PackedFields.PACKED_DATE.range().getMinimum(), 10000101);
        assertEquals(PackedFields.PACKED_DATE.range().getMaximum(), 99991231);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_date_rangeRefinedBy_time() {
        PackedFields.PACKED_DATE.rangeRefinedBy(LocalTime.of(11, 30));
    }

    public void test_date_getFrom() {
        assertEquals(LocalDate.of(2015, 12, 3).get(PackedFields.PACKED_DATE), 20151203);
        assertEquals(LocalDate.of(1000, 1, 1).get(PackedFields.PACKED_DATE), 10000101);
        assertEquals(LocalDate.of(9999, 12, 31).get(PackedFields.PACKED_DATE), 99991231);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_date_getFrom_rangeLow() {
        PackedFields.PACKED_DATE.getFrom(LocalDate.of(999, 12, 31));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_date_getFrom_rangeHigh() {
        PackedFields.PACKED_DATE.getFrom(LocalDate.of(10000, 1, 1));
    }

    public void test_date_adjustInto() {
        assertEquals(LocalDate.MIN.with(PackedFields.PACKED_DATE, 20151203), LocalDate.of(2015, 12, 3));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_date_adjustInto_range() {
        LocalDate.MIN.with(PackedFields.PACKED_DATE, 1230101);
    }

    public void test_date_resolve() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendValue(PackedFields.PACKED_DATE).toFormatter();
        assertEquals(LocalDate.parse("20151203", f), LocalDate.of(2015, 12, 3));
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_date_resolve_invalid_smart() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendValue(PackedFields.PACKED_DATE).toFormatter();
        LocalDate.parse("20151403", f.withResolverStyle(ResolverStyle.SMART));
    }

    public void test_date_resolve_invalid_lenient() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendValue(PackedFields.PACKED_DATE).toFormatter();
        assertEquals(LocalDate.parse("20151403", f.withResolverStyle(ResolverStyle.LENIENT)), LocalDate.of(2016, 2, 3));
    }

    //-----------------------------------------------------------------------
    // packedHourMin()
    //-----------------------------------------------------------------------
    public void test_hourMin_basics() {
        assertEquals(PackedFields.PACKED_HOUR_MIN.toString(), "PackedHourMin");
        assertEquals(PackedFields.PACKED_HOUR_MIN.getBaseUnit(), MINUTES);
        assertEquals(PackedFields.PACKED_HOUR_MIN.getRangeUnit(), DAYS);
        assertEquals(PackedFields.PACKED_HOUR_MIN.isDateBased(), false);
        assertEquals(PackedFields.PACKED_HOUR_MIN.isTimeBased(), true);
        assertEquals(PackedFields.PACKED_HOUR_MIN.isSupportedBy(LocalTime.of(11, 30)), true);
        assertEquals(PackedFields.PACKED_HOUR_MIN.isSupportedBy(LocalDate.of(2015, 3, 12)), false);
        assertEquals(PackedFields.PACKED_HOUR_MIN.range().getMinimum(), 0);
        assertEquals(PackedFields.PACKED_HOUR_MIN.range().getMaximum(), 2359);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_hourMin_rangeRefinedBy_time() {
        PackedFields.PACKED_HOUR_MIN.rangeRefinedBy(LocalDate.of(2015, 12, 3));
    }

    public void test_hourMin_getFrom() {
        assertEquals(LocalTime.of(11, 30).get(PackedFields.PACKED_HOUR_MIN), 1130);
        assertEquals(LocalTime.of(1, 21).get(PackedFields.PACKED_HOUR_MIN), 121);
    }

    public void test_hourMin_adjustInto() {
        assertEquals(LocalTime.MIDNIGHT.with(PackedFields.PACKED_HOUR_MIN, 1130), LocalTime.of(11, 30));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_hourMin_adjustInto_value() {
        LocalDate.MIN.with(PackedFields.PACKED_HOUR_MIN, 1273);
    }

    public void test_hourMin_resolve() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendValue(PackedFields.PACKED_HOUR_MIN).toFormatter();
        assertEquals(LocalTime.parse("1130", f), LocalTime.of(11, 30));
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_hourMin_resolve_invalid_smart() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendValue(PackedFields.PACKED_HOUR_MIN).toFormatter();
        LocalTime.parse("1173", f.withResolverStyle(ResolverStyle.SMART));
    }

    public void test_hourMin_resolve_invalid_lenient() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendValue(PackedFields.PACKED_HOUR_MIN).toFormatter();
        assertEquals(LocalTime.parse("1173", f.withResolverStyle(ResolverStyle.LENIENT)), LocalTime.of(12, 13));
    }

    //-----------------------------------------------------------------------
    // packedTime()
    //-----------------------------------------------------------------------
    public void test_time_basics() {
        assertEquals(PackedFields.PACKED_TIME.toString(), "PackedTime");
        assertEquals(PackedFields.PACKED_TIME.getBaseUnit(), SECONDS);
        assertEquals(PackedFields.PACKED_TIME.getRangeUnit(), DAYS);
        assertEquals(PackedFields.PACKED_TIME.isDateBased(), false);
        assertEquals(PackedFields.PACKED_TIME.isTimeBased(), true);
        assertEquals(PackedFields.PACKED_TIME.isSupportedBy(LocalTime.of(11, 30)), true);
        assertEquals(PackedFields.PACKED_TIME.isSupportedBy(LocalDate.of(2015, 3, 12)), false);
        assertEquals(PackedFields.PACKED_TIME.range().getMinimum(), 0);
        assertEquals(PackedFields.PACKED_TIME.range().getMaximum(), 235959);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_time_rangeRefinedBy_time() {
        PackedFields.PACKED_TIME.rangeRefinedBy(LocalDate.of(2015, 12, 3));
    }

    public void test_time_getFrom() {
        assertEquals(LocalTime.of(11, 30, 52).get(PackedFields.PACKED_TIME), 113052);
        assertEquals(LocalTime.of(11, 30).get(PackedFields.PACKED_TIME), 113000);
        assertEquals(LocalTime.of(1, 21).get(PackedFields.PACKED_TIME), 12100);
    }

    public void test_time_adjustInto() {
        assertEquals(LocalTime.MIDNIGHT.with(PackedFields.PACKED_TIME, 113052), LocalTime.of(11, 30, 52));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_time_adjustInto_value() {
        LocalDate.MIN.with(PackedFields.PACKED_TIME, 127310);
    }

    public void test_time_resolve() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendValue(PackedFields.PACKED_TIME).toFormatter();
        assertEquals(LocalTime.parse("113052", f), LocalTime.of(11, 30, 52));
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_time_resolve_invalid_smart() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendValue(PackedFields.PACKED_TIME).toFormatter();
        LocalTime.parse("117361", f.withResolverStyle(ResolverStyle.SMART));
    }

    public void test_time_resolve_invalid_lenient() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendValue(PackedFields.PACKED_TIME).toFormatter();
        assertEquals(LocalTime.parse("117361", f.withResolverStyle(ResolverStyle.LENIENT)), LocalTime.of(12, 14, 1));
    }

}
