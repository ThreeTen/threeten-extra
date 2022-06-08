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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

import org.joda.convert.StringConvert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.threeten.extra.scale.TaiInstant;
import org.threeten.extra.scale.UtcInstant;

public class TestConvert {

    public static Object[][] data_inputs() {
        return new Object[][] {
                {Seconds.of(23), "PT23S"},
                {Minutes.of(23), "PT23M"},
                {Hours.of(23), "PT23H"},
                {Days.of(23), "P23D"},
                {Weeks.of(23), "P23W"},
                {Months.of(23), "P23M"},
                {Years.of(23), "P23Y"},
                {YearWeek.of(2019, 3), "2019-W03"},
                {YearQuarter.of(2019, 3), "2019-Q3"},
                {PeriodDuration.of(Period.of(1, 2, 3), Duration.ofHours(6)), "P1Y2M3DT6H"},
                {Interval.of(Instant.ofEpochSecond(60), Duration.ofHours(6)), "1970-01-01T00:01:00Z/1970-01-01T06:01:00Z"},
                {LocalDateRange.of(LocalDate.of(2018, 6, 1), LocalDate.of(2018, 9, 15)), "2018-06-01/2018-09-15"},
                {TaiInstant.ofTaiSeconds(123, 456), "123.000000456s(TAI)"},
                {UtcInstant.ofModifiedJulianDay(0, 1123456789L), "1858-11-17T00:00:01.123456789Z"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_inputs")
    public void test_convertToString(Object obj, String str) {
        assertEquals(str, StringConvert.INSTANCE.convertToString(obj));
    }

    @ParameterizedTest
    @MethodSource("data_inputs")
    public void test_convertFromString(Object obj, String str) {
        assertEquals(obj, StringConvert.INSTANCE.convertFromString(obj.getClass(), str));
    }

}
