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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Range;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Test date range.
 */
@RunWith(DataProviderRunner.class)
public class TestLocalDateRange {

    private static final LocalDate DATE_2012_07_01 = LocalDate.of(2012, 7, 1);
    private static final LocalDate DATE_2012_07_27 = LocalDate.of(2012, 7, 27);
    private static final LocalDate DATE_2012_07_28 = LocalDate.of(2012, 7, 28);
    private static final LocalDate DATE_2012_07_29 = LocalDate.of(2012, 7, 29);
    private static final LocalDate DATE_2012_07_30 = LocalDate.of(2012, 7, 30);
    private static final LocalDate DATE_2012_07_31 = LocalDate.of(2012, 7, 31);
    private static final LocalDate DATE_2012_08_01 = LocalDate.of(2012, 8, 1);
    private static final LocalDate DATE_2012_08_31 = LocalDate.of(2012, 8, 31);

    //-----------------------------------------------------------------------
    @Test
    public void test_ALL() {
        LocalDateRange test = LocalDateRange.ALL;
        assertEquals(LocalDate.MIN, test.getStart());
        assertEquals(LocalDate.MAX, test.getEndInclusive());
        assertEquals(LocalDate.MAX, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals("-999999999-01-01/+999999999-12-31", test.toString());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(DATE_2012_07_28, test.getStart());
        assertEquals(DATE_2012_07_30, test.getEndInclusive());
        assertEquals(DATE_2012_07_31, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("2012-07-28/2012-07-31", test.toString());
    }

    @Test
    public void test_of_MIN() {
        LocalDateRange test = LocalDateRange.of(LocalDate.MIN, DATE_2012_07_31);
        assertEquals(LocalDate.MIN, test.getStart());
        assertEquals(DATE_2012_07_30, test.getEndInclusive());
        assertEquals(DATE_2012_07_31, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("-999999999-01-01/2012-07-31", test.toString());
    }

    @Test
    public void test_of_MAX() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, LocalDate.MAX);
        assertEquals(DATE_2012_07_28, test.getStart());
        assertEquals(LocalDate.MAX, test.getEndInclusive());
        assertEquals(LocalDate.MAX, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals("2012-07-28/+999999999-12-31", test.toString());
    }

    @Test
    public void test_of_MIN_MAX() {
        LocalDateRange test = LocalDateRange.of(LocalDate.MIN, LocalDate.MAX);
        assertEquals(LocalDate.MIN, test.getStart());
        assertEquals(LocalDate.MAX, test.getEndInclusive());
        assertEquals(LocalDate.MAX, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals("-999999999-01-01/+999999999-12-31", test.toString());
    }

    @Test
    public void test_of_MIN_MIN() {
        LocalDateRange test = LocalDateRange.of(LocalDate.MIN, LocalDate.MIN);
        assertEquals(LocalDate.MIN, test.getStart());
        assertEquals(LocalDate.MIN, test.getEndInclusive());
        assertEquals(LocalDate.MIN, test.getEnd());
        assertEquals(true, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("-999999999-01-01/-999999999-01-01", test.toString());
    }

    @Test
    public void test_of_empty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_30, DATE_2012_07_30);
        assertEquals(DATE_2012_07_30, test.getStart());
        assertEquals(DATE_2012_07_29, test.getEndInclusive());
        assertEquals(DATE_2012_07_30, test.getEnd());
        assertEquals(true, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("2012-07-30/2012-07-30", test.toString());
    }

    @Test(expected = DateTimeException.class)
    public void test_of_badOrder() {
        LocalDateRange.of(DATE_2012_07_31, DATE_2012_07_30);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofClosed() {
        LocalDateRange test = LocalDateRange.ofClosed(DATE_2012_07_28, DATE_2012_07_30);
        assertEquals(DATE_2012_07_28, test.getStart());
        assertEquals(DATE_2012_07_30, test.getEndInclusive());
        assertEquals(DATE_2012_07_31, test.getEnd());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("2012-07-28/2012-07-31", test.toString());
    }

    @Test
    public void test_ofClosed_MIN() {
        LocalDateRange test = LocalDateRange.ofClosed(LocalDate.MIN, DATE_2012_07_30);
        assertEquals(LocalDate.MIN, test.getStart());
        assertEquals(DATE_2012_07_30, test.getEndInclusive());
        assertEquals(DATE_2012_07_31, test.getEnd());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("-999999999-01-01/2012-07-31", test.toString());
    }

    @Test
    public void test_ofClosed_MAX() {
        LocalDateRange test = LocalDateRange.ofClosed(DATE_2012_07_28, LocalDate.MAX);
        assertEquals(DATE_2012_07_28, test.getStart());
        assertEquals(LocalDate.MAX, test.getEndInclusive());
        assertEquals(LocalDate.MAX, test.getEnd());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals("2012-07-28/+999999999-12-31", test.toString());
    }

    @Test
    public void test_ofClosed_MIN_MAX() {
        LocalDateRange test = LocalDateRange.ofClosed(LocalDate.MIN, LocalDate.MAX);
        assertEquals(LocalDate.MIN, test.getStart());
        assertEquals(LocalDate.MAX, test.getEndInclusive());
        assertEquals(LocalDate.MAX, test.getEnd());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals("-999999999-01-01/+999999999-12-31", test.toString());
    }

    @Test(expected = DateTimeException.class)
    public void test_ofClosed_badOrder() {
        LocalDateRange.ofClosed(DATE_2012_07_31, DATE_2012_07_30);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of_period() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, Period.ofDays(3));
        assertEquals(DATE_2012_07_28, test.getStart());
        assertEquals(DATE_2012_07_30, test.getEndInclusive());
        assertEquals(DATE_2012_07_31, test.getEnd());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("2012-07-28/2012-07-31", test.toString());
    }

    @Test(expected = DateTimeException.class)
    public void test_of_period_negative() {
        LocalDateRange.of(DATE_2012_07_31, Period.ofDays(-1));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequence() {
        LocalDateRange test = LocalDateRange.parse(DATE_2012_07_27 + "/" + DATE_2012_07_29);
        assertEquals(DATE_2012_07_27, test.getStart());
        assertEquals(DATE_2012_07_29, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_PeriodLocalDate() {
        LocalDateRange test = LocalDateRange.parse("P2D/" + DATE_2012_07_29);
        assertEquals(DATE_2012_07_27, test.getStart());
        assertEquals(DATE_2012_07_29, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_PeriodLocalDate_case() {
        LocalDateRange test = LocalDateRange.parse("p2d/" + DATE_2012_07_29);
        assertEquals(DATE_2012_07_27, test.getStart());
        assertEquals(DATE_2012_07_29, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_LocalDatePeriod() {
        LocalDateRange test = LocalDateRange.parse(DATE_2012_07_27 + "/P2D");
        assertEquals(DATE_2012_07_27, test.getStart());
        assertEquals(DATE_2012_07_29, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_LocalDatePeriod_case() {
        LocalDateRange test = LocalDateRange.parse(DATE_2012_07_27 + "/p2d");
        assertEquals(DATE_2012_07_27, test.getStart());
        assertEquals(DATE_2012_07_29, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_empty() {
        LocalDateRange test = LocalDateRange.parse(DATE_2012_07_27 + "/" + DATE_2012_07_27);
        assertEquals(DATE_2012_07_27, test.getStart());
        assertEquals(DATE_2012_07_27, test.getEnd());
    }

    @Test(expected = DateTimeException.class)
    public void test_parse_CharSequence_badOrder() {
        LocalDateRange.parse(DATE_2012_07_29 + "/" + DATE_2012_07_27);
    }

    @Test(expected = DateTimeParseException.class)
    public void test_parse_CharSequence_badFormat() {
        LocalDateRange.parse(DATE_2012_07_29 + "-" + DATE_2012_07_27);
    }

    @Test(expected = NullPointerException.class)
    public void test_parse_CharSequence_null() {
        LocalDateRange.parse(null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(LocalDateRange.class));
    }

    @Test
    public void test_serialization() throws Exception {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
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
    public void test_withStart() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        LocalDateRange test = base.withStart(DATE_2012_07_27);
        assertEquals(DATE_2012_07_27, test.getStart());
        assertEquals(DATE_2012_07_30, test.getEndInclusive());
        assertEquals(DATE_2012_07_31, test.getEnd());
    }

    @Test
    public void test_withStart_adjuster() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        LocalDateRange test = base.withStart(date -> date.minus(1, ChronoUnit.WEEKS));
        assertEquals(DATE_2012_07_28.minusWeeks(1), test.getStart());
        assertEquals(DATE_2012_07_30, test.getEndInclusive());
        assertEquals(DATE_2012_07_31, test.getEnd());
    }

    @Test
    public void test_withStart_min() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        LocalDateRange test = base.withStart(LocalDate.MIN);
        assertEquals(LocalDate.MIN, test.getStart());
        assertEquals(DATE_2012_07_30, test.getEndInclusive());
        assertEquals(DATE_2012_07_31, test.getEnd());
    }

    @Test
    public void test_withStart_empty() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        LocalDateRange test = base.withStart(DATE_2012_07_31);
        assertEquals(DATE_2012_07_31, test.getStart());
        assertEquals(DATE_2012_07_30, test.getEndInclusive());
        assertEquals(DATE_2012_07_31, test.getEnd());
    }

    @Test(expected = DateTimeException.class)
    public void test_withStart_invalid() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_30);
        base.withStart(DATE_2012_07_31);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withEnd() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        LocalDateRange test = base.withEnd(DATE_2012_07_30);
        assertEquals(DATE_2012_07_28, test.getStart());
        assertEquals(DATE_2012_07_29, test.getEndInclusive());
        assertEquals(DATE_2012_07_30, test.getEnd());
    }

    @Test
    public void test_withEnd_adjuster() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        LocalDateRange test = base.withEnd(date -> date.plus(1, ChronoUnit.WEEKS));
        assertEquals(DATE_2012_07_28, test.getStart());
        assertEquals(DATE_2012_07_30.plusWeeks(1), test.getEndInclusive());
        assertEquals(DATE_2012_07_31.plusWeeks(1), test.getEnd());
    }

    @Test
    public void test_withEnd_max() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        LocalDateRange test = base.withEnd(LocalDate.MAX);
        assertEquals(DATE_2012_07_28, test.getStart());
        assertEquals(LocalDate.MAX, test.getEndInclusive());
        assertEquals(LocalDate.MAX, test.getEnd());
    }

    @Test
    public void test_withEnd_empty() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_30, DATE_2012_07_31);
        LocalDateRange test = base.withEnd(DATE_2012_07_30);
        assertEquals(DATE_2012_07_30, test.getStart());
        assertEquals(DATE_2012_07_29, test.getEndInclusive());
        assertEquals(DATE_2012_07_30, test.getEnd());
    }

    @Test(expected = DateTimeException.class)
    public void test_withEnd_invalid() {
        LocalDateRange base = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        base.withEnd(DATE_2012_07_27);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_contains() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(false, test.contains(LocalDate.MIN));
        assertEquals(false, test.contains(DATE_2012_07_27));
        assertEquals(true, test.contains(DATE_2012_07_28));
        assertEquals(true, test.contains(DATE_2012_07_29));
        assertEquals(true, test.contains(DATE_2012_07_30));
        assertEquals(false, test.contains(DATE_2012_07_31));
        assertEquals(false, test.contains(DATE_2012_08_01));
        assertEquals(false, test.contains(LocalDate.MAX));
    }

    @Test
    public void test_contains_baseEmpty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28);
        assertEquals(false, test.contains(LocalDate.MIN));
        assertEquals(false, test.contains(DATE_2012_07_27));
        assertEquals(false, test.contains(DATE_2012_07_28));
        assertEquals(false, test.contains(DATE_2012_07_29));
        assertEquals(false, test.contains(LocalDate.MAX));
    }

    @Test
    public void test_contains_max() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, LocalDate.MAX);
        assertEquals(false, test.contains(LocalDate.MIN));
        assertEquals(false, test.contains(DATE_2012_07_27));
        assertEquals(true, test.contains(DATE_2012_07_28));
        assertEquals(true, test.contains(DATE_2012_07_29));
        assertEquals(true, test.contains(DATE_2012_07_30));
        assertEquals(true, test.contains(DATE_2012_07_31));
        assertEquals(true, test.contains(DATE_2012_08_01));
        assertEquals(true, test.contains(LocalDate.MAX));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_queries() {
        return new Object[][] {
            // before start
            { DATE_2012_07_01, DATE_2012_07_27, false, false, false, false },
            { DATE_2012_07_01, DATE_2012_07_28, false, true, true, false },
            // before end
            { DATE_2012_07_27, DATE_2012_07_30, false, false, true, true },
            { DATE_2012_07_28, DATE_2012_07_30, true, false, true, true },
            { DATE_2012_07_29, DATE_2012_07_30, true, false, true, true },
            // same end
            { DATE_2012_07_27, DATE_2012_07_31, false, false, true, true },
            { DATE_2012_07_28, DATE_2012_07_31, true, false, true, true },
            { DATE_2012_07_29, DATE_2012_07_31, true, false, true, true },
            { DATE_2012_07_30, DATE_2012_07_31, true, false, true , true},
            // past end
            { DATE_2012_07_27, DATE_2012_08_01, false, false, true, true },
            { DATE_2012_07_28, DATE_2012_08_01, false, false, true, true },
            { DATE_2012_07_29, DATE_2012_08_01, false, false, true, true },
            { DATE_2012_07_30, DATE_2012_08_01, false, false, true, true },
            // start past end
            { DATE_2012_07_31, DATE_2012_08_01, false, true, true, false },
            { DATE_2012_07_31, DATE_2012_08_31, false, true, true, false },
            { DATE_2012_08_01, DATE_2012_08_31, false, false, false, false },
            // empty
            { DATE_2012_07_27, DATE_2012_07_27, false, false, false, false },
            { DATE_2012_07_28, DATE_2012_07_28, true, true, true, false },
            { DATE_2012_07_29, DATE_2012_07_29, true, false, true, true },
            { DATE_2012_07_30, DATE_2012_07_30, true, false, true, true },
            { DATE_2012_07_31, DATE_2012_07_31, true, true, true, false },
            { DATE_2012_08_31, DATE_2012_08_31, false, false, false, false },
            // min
            { LocalDate.MIN, DATE_2012_07_27, false, false, false, false },
            { LocalDate.MIN, DATE_2012_07_28, false, true, true, false },
            { LocalDate.MIN, DATE_2012_07_29, false, false, true, true },
            { LocalDate.MIN, DATE_2012_07_30, false, false, true, true },
            { LocalDate.MIN, DATE_2012_07_31, false, false, true, true },
            { LocalDate.MIN, DATE_2012_08_01, false, false, true, true },
            { LocalDate.MIN, LocalDate.MAX, false, false, true, true },
            // max
            { DATE_2012_07_27, LocalDate.MAX, false, false, true, true },
            { DATE_2012_07_28, LocalDate.MAX, false, false, true, true },
            { DATE_2012_07_29, LocalDate.MAX, false, false, true, true },
            { DATE_2012_07_30, LocalDate.MAX, false, false, true, true },
            { DATE_2012_07_31, LocalDate.MAX, false, true, true, false },
            { DATE_2012_08_01, LocalDate.MAX, false, false, false, false },
        };
    }

    @Test
    @UseDataProvider("data_queries")
    public void test_encloses(
            LocalDate start, LocalDate end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(isEnclosedBy, test.encloses(LocalDateRange.of(start, end)));
    }

    @Test
    @UseDataProvider("data_queries")
    public void test_abuts(
            LocalDate start, LocalDate end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(abuts, test.abuts(LocalDateRange.of(start, end)));
    }

    @Test
    @UseDataProvider("data_queries")
    public void test_isConnected(
            LocalDate start, LocalDate end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(isConnected, test.isConnected(LocalDateRange.of(start, end)));
    }

    @Test
    @UseDataProvider("data_queries")
    public void test_overlaps(
            LocalDate start, LocalDate end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(overlaps, test.overlaps(LocalDateRange.of(start, end)));
    }

    @Test
    @UseDataProvider("data_queries")
    public void test_crossCheck(
            LocalDate start, LocalDate end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        LocalDateRange input = LocalDateRange.of(start, end);
        assertEquals(test.overlaps(input) || test.abuts(input), test.isConnected(input));
        assertEquals(test.isConnected(input) && !test.abuts(input), test.overlaps(input));
    }

    @Test
    public void test_encloses_max() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, LocalDate.MAX);
        assertEquals(true, test.encloses(LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28)));
        assertEquals(true, test.encloses(LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_29)));
        assertEquals(true, test.encloses(LocalDateRange.of(DATE_2012_07_28, LocalDate.MAX)));
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_01, DATE_2012_07_27)));
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_29)));
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_27, LocalDate.MAX)));
    }

    @Test
    public void test_encloses_baseEmpty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28);
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_27)));
        assertEquals(true, test.encloses(LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28)));
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29)));
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_27)));
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_28)));
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_29)));
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_27, LocalDate.MAX)));
        assertEquals(false, test.encloses(LocalDateRange.of(DATE_2012_07_28, LocalDate.MAX)));
    }

    @Test
    public void test_encloses_baseEmptyMax() {
        assertEquals(true, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .encloses(LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .encloses(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX)));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .encloses(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(1))));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX.minusDays(1), LocalDate.MAX.minusDays(1))
				        .encloses(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(1))));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX.minusDays(1), LocalDate.MAX.minusDays(1))
				        .encloses(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(2))));
    }

    @Test
    public void test_abuts_baseEmpty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28);
        assertEquals(false, test.abuts(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_27)));
        assertEquals(false, test.abuts(LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28)));
        assertEquals(false, test.abuts(LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29)));
        assertEquals(true, test.abuts(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_28)));
        assertEquals(true, test.abuts(LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_29)));
    }

    @Test
    public void test_abuts_baseEmptyMax() {
        assertEquals(false, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .abuts(LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)));
        assertEquals(true, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .abuts(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX)));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .abuts(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(1))));
        assertEquals(true, LocalDateRange.of(LocalDate.MAX.minusDays(1), LocalDate.MAX.minusDays(1))
				        .abuts(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(1))));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX.minusDays(1), LocalDate.MAX.minusDays(1))
				        .abuts(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(2))));
    }

    @Test
    public void test_isConnected_baseEmpty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28);
        assertEquals(false, test.isConnected(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_27)));
        assertEquals(true, test.isConnected(LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28)));
        assertEquals(false, test.isConnected(LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29)));
    }

    @Test
    public void test_isConnected_baseEmptyMax() {
        assertEquals(true, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .isConnected(LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)));
        assertEquals(true, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .isConnected(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX)));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .isConnected(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(1))));
        assertEquals(true, LocalDateRange.of(LocalDate.MAX.minusDays(1), LocalDate.MAX.minusDays(1))
				        .isConnected(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(1))));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX.minusDays(1), LocalDate.MAX.minusDays(1))
				        .isConnected(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(2))));
    }

    @Test
    public void test_overlaps_baseEmpty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28);
        assertEquals(false, test.overlaps(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_27)));
        assertEquals(true, test.overlaps(LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28)));
        assertEquals(false, test.overlaps(LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29)));
    }

    @Test
    public void test_overlaps_baseEmptyMax() {
        assertEquals(true, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .overlaps(LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .overlaps(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX)));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX, LocalDate.MAX)
				        .overlaps(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(1))));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX.minusDays(1), LocalDate.MAX.minusDays(1))
				        .overlaps(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(1))));
        assertEquals(false, LocalDateRange.of(LocalDate.MAX.minusDays(1), LocalDate.MAX.minusDays(1))
				        .overlaps(LocalDateRange.of(DATE_2012_07_01, LocalDate.MAX.minusDays(2))));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_intersection() {
        return new Object[][] {
            // adjacent
            { DATE_2012_07_01, DATE_2012_07_28, DATE_2012_07_28, DATE_2012_07_30, DATE_2012_07_28, DATE_2012_07_28 },
            // adjacent empty
            { DATE_2012_07_01, DATE_2012_07_30, DATE_2012_07_30, DATE_2012_07_30, DATE_2012_07_30, DATE_2012_07_30 },
            // overlap
            { DATE_2012_07_01, DATE_2012_07_29, DATE_2012_07_28, DATE_2012_07_30, DATE_2012_07_28, DATE_2012_07_29 },
            // encloses
            { DATE_2012_07_01, DATE_2012_07_30, DATE_2012_07_28, DATE_2012_07_29, DATE_2012_07_28, DATE_2012_07_29 },
            // encloses empty
            { DATE_2012_07_01, DATE_2012_07_30, DATE_2012_07_28, DATE_2012_07_28, DATE_2012_07_28, DATE_2012_07_28 },
        };
    }

    @Test
    @UseDataProvider("data_intersection")
    public void test_intersection(
            LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2, LocalDate expStart, LocalDate expEnd) {

        LocalDateRange test1 = LocalDateRange.of(start1, end1);
        LocalDateRange test2 = LocalDateRange.of(start2, end2);
        LocalDateRange expected = LocalDateRange.of(expStart, expEnd);
        assertTrue(test1.isConnected(test2));
        assertEquals(expected, test1.intersection(test2));
    }

    @Test
    @UseDataProvider("data_intersection")
    public void test_intersection_reverse(
            LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2, LocalDate expStart, LocalDate expEnd) {

        LocalDateRange test1 = LocalDateRange.of(start1, end1);
        LocalDateRange test2 = LocalDateRange.of(start2, end2);
        LocalDateRange expected = LocalDateRange.of(expStart, expEnd);
        assertTrue(test2.isConnected(test1));
        assertEquals(expected, test2.intersection(test1));
    }

    @Test(expected = DateTimeException.class)
    public void test_intersectionBad() {
        LocalDateRange test1 = LocalDateRange.of(DATE_2012_07_01, DATE_2012_07_28);
        LocalDateRange test2 = LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_30);
        assertEquals(false, test1.isConnected(test2));
        test1.intersection(test2);
    }

    @Test
    public void test_intersection_same() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(test, test.intersection(test));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_union() {
        return new Object[][] {
            // adjacent
            { DATE_2012_07_01, DATE_2012_07_28, DATE_2012_07_28, DATE_2012_07_30, DATE_2012_07_01, DATE_2012_07_30 },
            // adjacent empty
            { DATE_2012_07_01, DATE_2012_07_30, DATE_2012_07_30, DATE_2012_07_30, DATE_2012_07_01, DATE_2012_07_30 },
            // overlap
            { DATE_2012_07_01, DATE_2012_07_29, DATE_2012_07_28, DATE_2012_07_30, DATE_2012_07_01, DATE_2012_07_30 },
            // encloses
            { DATE_2012_07_01, DATE_2012_07_30, DATE_2012_07_28, DATE_2012_07_29, DATE_2012_07_01, DATE_2012_07_30 },
            // encloses empty
            { DATE_2012_07_01, DATE_2012_07_30, DATE_2012_07_28, DATE_2012_07_28, DATE_2012_07_01, DATE_2012_07_30 },
        };
    }

    @Test
    @UseDataProvider("data_union")
    public void test_unionAndSpan(
            LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2, LocalDate expStart, LocalDate expEnd) {

        LocalDateRange test1 = LocalDateRange.of(start1, end1);
        LocalDateRange test2 = LocalDateRange.of(start2, end2);
        LocalDateRange expected = LocalDateRange.of(expStart, expEnd);
        assertTrue(test1.isConnected(test2));
        assertEquals(expected, test1.union(test2));
        assertEquals(expected, test1.span(test2));
    }

    @Test
    @UseDataProvider("data_union")
    public void test_unionAndSpan_reverse(
            LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2, LocalDate expStart, LocalDate expEnd) {

        LocalDateRange test1 = LocalDateRange.of(start1, end1);
        LocalDateRange test2 = LocalDateRange.of(start2, end2);
        LocalDateRange expected = LocalDateRange.of(expStart, expEnd);
        assertTrue(test2.isConnected(test1));
        assertEquals(expected, test2.union(test1));
        assertEquals(expected, test2.span(test1));
    }

    @Test
    @UseDataProvider("data_union")
    public void test_span_enclosesInputs(
            LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2, LocalDate expStart, LocalDate expEnd) {

        LocalDateRange test1 = LocalDateRange.of(start1, end1);
        LocalDateRange test2 = LocalDateRange.of(start2, end2);
        LocalDateRange expected = LocalDateRange.of(expStart, expEnd);
        assertEquals(true, expected.encloses(test1));
        assertEquals(true, expected.encloses(test2));
    }

    @Test(expected = DateTimeException.class)
    public void test_union_disconnected() {
        LocalDateRange test1 = LocalDateRange.of(DATE_2012_07_01, DATE_2012_07_28);
        LocalDateRange test2 = LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_30);
        assertFalse(test1.isConnected(test2));
        test1.union(test2);
    }

    @Test
    public void test_span_disconnected() {
        LocalDateRange test1 = LocalDateRange.of(DATE_2012_07_01, DATE_2012_07_28);
        LocalDateRange test2 = LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_30);
        assertFalse(test1.isConnected(test2));
        assertEquals(LocalDateRange.of(DATE_2012_07_01, DATE_2012_07_30), test1.span(test2));
    }

    @Test
    public void test_unionAndSpan_same() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(test, test.union(test));
        assertEquals(test, test.span(test));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_stream() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        List<LocalDate> result = test.stream().collect(Collectors.toList());
        assertEquals(3, result.size());
        assertEquals(DATE_2012_07_28, result.get(0));
        assertEquals(DATE_2012_07_29, result.get(1));
        assertEquals(DATE_2012_07_30, result.get(2));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_isBefore() {
        return new Object[][] {
            // before start
            { DATE_2012_07_01, DATE_2012_07_27, false },
            // before end
            { DATE_2012_07_27, DATE_2012_07_30, false },
            { DATE_2012_07_28, DATE_2012_07_30, false },
            { DATE_2012_07_29, DATE_2012_07_30, false },
            // same end
            { DATE_2012_07_27, DATE_2012_07_31, false },
            { DATE_2012_07_28, DATE_2012_07_31, false },
            { DATE_2012_07_29, DATE_2012_07_31, false },
            { DATE_2012_07_30, DATE_2012_07_31, false },
            // past end
            { DATE_2012_07_27, DATE_2012_08_01, false },
            { DATE_2012_07_28, DATE_2012_08_01, false },
            { DATE_2012_07_29, DATE_2012_08_01, false },
            { DATE_2012_07_30, DATE_2012_08_01, false },
            // start past end
            { DATE_2012_07_31, DATE_2012_08_01, true },
            { DATE_2012_07_31, DATE_2012_08_31, true },
            // empty
            { DATE_2012_07_30, DATE_2012_07_30, false },
            { DATE_2012_07_31, DATE_2012_07_31, true },
            // min
            { LocalDate.MIN, DATE_2012_07_27, false },
            { LocalDate.MIN, DATE_2012_07_28, false },
            { LocalDate.MIN, DATE_2012_07_29, false },
            { LocalDate.MIN, DATE_2012_07_30, false },
            { LocalDate.MIN, DATE_2012_07_31, false },
            { LocalDate.MIN, DATE_2012_08_01, false },
            { LocalDate.MIN, LocalDate.MAX, false },
            // max
            { DATE_2012_07_27, LocalDate.MAX, false },
            { DATE_2012_07_28, LocalDate.MAX, false },
            { DATE_2012_07_29, LocalDate.MAX, false },
            { DATE_2012_07_30, LocalDate.MAX, false },
            { DATE_2012_07_31, LocalDate.MAX, true },
            { DATE_2012_08_01, LocalDate.MAX, true },
        };
    }

    @Test
    @UseDataProvider("data_isBefore")
    public void test_isBefore_range(LocalDate start, LocalDate end, boolean before) {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(before, test.isBefore(LocalDateRange.of(start, end)));
    }

    @Test
    @UseDataProvider("data_isBefore")
    public void test_isBefore_date(LocalDate start, LocalDate end, boolean before) {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(before, test.isBefore(start));
    }

    @Test
    public void test_isBefore_range_empty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29);
        assertEquals(false, test.isBefore(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_28)));
        assertEquals(false, test.isBefore(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_29)));
        assertEquals(false, test.isBefore(LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29)));
        assertEquals(true, test.isBefore(LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_30)));
        assertEquals(true, test.isBefore(LocalDateRange.of(DATE_2012_07_30, DATE_2012_07_30)));
        assertEquals(true, test.isBefore(LocalDateRange.of(DATE_2012_07_30, DATE_2012_07_31)));
    }

    @Test
    public void test_isBefore_date_empty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29);
        assertEquals(false, test.isBefore(DATE_2012_07_28));
        assertEquals(false, test.isBefore(DATE_2012_07_29));
        assertEquals(true, test.isBefore(DATE_2012_07_30));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_isAfter() {
        return new Object[][] {
            // before start
            { DATE_2012_07_01, DATE_2012_07_27, true },
            // to start
            { DATE_2012_07_01, DATE_2012_07_28, true },
            // before end
            { DATE_2012_07_01, DATE_2012_07_29, false },
            { DATE_2012_07_27, DATE_2012_07_30, false },
            { DATE_2012_07_28, DATE_2012_07_30, false },
            { DATE_2012_07_29, DATE_2012_07_30, false },
            // same end
            { DATE_2012_07_27, DATE_2012_07_31, false },
            { DATE_2012_07_28, DATE_2012_07_31, false },
            { DATE_2012_07_29, DATE_2012_07_31, false },
            { DATE_2012_07_30, DATE_2012_07_31, false },
            // past end
            { DATE_2012_07_27, DATE_2012_08_01, false },
            { DATE_2012_07_28, DATE_2012_08_01, false },
            { DATE_2012_07_29, DATE_2012_08_01, false },
            { DATE_2012_07_30, DATE_2012_08_01, false },
            // start past end
            { DATE_2012_07_31, DATE_2012_08_01, false },
            { DATE_2012_07_31, DATE_2012_08_31, false },
            // empty
            { DATE_2012_07_28, DATE_2012_07_28, true },
            { DATE_2012_07_29, DATE_2012_07_29, false },
            // min
            { LocalDate.MIN, DATE_2012_07_27, true },
            { LocalDate.MIN, DATE_2012_07_28, true },
            { LocalDate.MIN, DATE_2012_07_29, false },
            { LocalDate.MIN, DATE_2012_07_30, false },
            { LocalDate.MIN, DATE_2012_07_31, false },
            { LocalDate.MIN, DATE_2012_08_01, false },
            { LocalDate.MIN, LocalDate.MAX, false },
            // max
            { DATE_2012_07_27, LocalDate.MAX, false },
            { DATE_2012_07_28, LocalDate.MAX, false },
            { DATE_2012_07_29, LocalDate.MAX, false },
            { DATE_2012_07_30, LocalDate.MAX, false },
            { DATE_2012_07_31, LocalDate.MAX, false },
            { DATE_2012_08_01, LocalDate.MAX, false },
        };
    }

    @Test
    @UseDataProvider("data_isAfter")
    public void test_isAfter_range(LocalDate start, LocalDate end, boolean before) {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(before, test.isAfter(LocalDateRange.of(start, end)));
    }

    @Test
    @UseDataProvider("data_isAfter")
    public void test_isAfter_date(LocalDate start, LocalDate end, boolean before) {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_31);
        assertEquals(before, test.isAfter(end.minusDays(1)));
    }

    @Test
    public void test_isAfter_range_empty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29);
        assertEquals(true, test.isAfter(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_28)));
        assertEquals(true, test.isAfter(LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_29)));
        assertEquals(true, test.isAfter(LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_28)));
        assertEquals(false, test.isAfter(LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29)));
        assertEquals(false, test.isAfter(LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_30)));
        assertEquals(false, test.isAfter(LocalDateRange.of(DATE_2012_07_30, DATE_2012_07_30)));
        assertEquals(false, test.isAfter(LocalDateRange.of(DATE_2012_07_30, DATE_2012_07_31)));
    }

    @Test
    public void test_isAfter_date_empty() {
        LocalDateRange test = LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29);
        assertEquals(true, test.isAfter(DATE_2012_07_28));
        assertEquals(false, test.isAfter(DATE_2012_07_29));
        assertEquals(false, test.isAfter(DATE_2012_07_30));
    }

  //-----------------------------------------------------------------------
    @Test
    public void test_lengthInDays() {
        assertEquals(2, LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_29).lengthInDays());
        assertEquals(1, LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_29).lengthInDays());
        assertEquals(0, LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29).lengthInDays());
    }

    @Test
    public void test_toPeriod() {
        assertEquals(Period.ofDays(2), LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_29).toPeriod());
        assertEquals(Period.ofDays(1), LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_29).toPeriod());
        assertEquals(Period.ofDays(0), LocalDateRange.of(DATE_2012_07_29, DATE_2012_07_29).toPeriod());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals() {
        LocalDateRange a = LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_29);
        LocalDateRange a2 = LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_29);
        LocalDateRange b = LocalDateRange.of(DATE_2012_07_27, DATE_2012_07_30);
        LocalDateRange c = LocalDateRange.of(DATE_2012_07_28, DATE_2012_07_29);
        assertEquals(true, a.equals(a));
        assertEquals(true, a.equals(a2));
        assertEquals(false, a.equals(b));
        assertEquals(false, a.equals(c));
        assertEquals(false, a.equals(null));
        assertEquals(false, a.equals(""));
        assertEquals(true, a.hashCode() == a2.hashCode());
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static List<List<Object>> data_crossCheckGuava() {
        List<List<Object>> list = new ArrayList<>();
        for (int i1 = 1; i1 < 5; i1++) {
            for (int j1 = i1; j1 < 5; j1++) {
                LocalDate date11 = LocalDate.of(2016, 1, i1);
                LocalDate date12 = LocalDate.of(2016, 1, j1);
                LocalDateRange extraRange1 = LocalDateRange.of(date11, date12);
                Range<LocalDate> guavaRange1 = Range.closedOpen(date11, date12);
                for (int i2 = 1; i2 < 5; i2++) {
                    for (int j2 = i2; j2 < 5; j2++) {
                        LocalDate date21 = LocalDate.of(2016, 1, i2);
                        LocalDate date22 = LocalDate.of(2016, 1, j2);
                        LocalDateRange extraRange2 = LocalDateRange.of(date21, date22);
                        Range<LocalDate> guavaRange2 = Range.closedOpen(date21, date22);
                        list.add(Arrays.asList(extraRange1, extraRange2, guavaRange1, guavaRange2));
                    }
                }
            }
        }
        return list;
    }

    @Test
    @UseDataProvider("data_crossCheckGuava")
    public void crossCheckGuava_encloses(
            LocalDateRange extraRange1,
            LocalDateRange extraRange2, 
            Range<LocalDate> guavaRange1, 
            Range<LocalDate> guavaRange2) {

        boolean extra = extraRange1.encloses(extraRange2);
        boolean guava = guavaRange1.encloses(guavaRange2);
        assertEquals(guava, extra);
    }

    @Test
    @UseDataProvider("data_crossCheckGuava")
    public void crossCheckGuava_isConnected(
            LocalDateRange extraRange1,
            LocalDateRange extraRange2, 
            Range<LocalDate> guavaRange1, 
            Range<LocalDate> guavaRange2) {

        boolean extra = extraRange1.isConnected(extraRange2);
        boolean guava = guavaRange1.isConnected(guavaRange2);
        assertEquals(guava, extra);
    }

    @Test
    @UseDataProvider("data_crossCheckGuava")
    public void crossCheckGuava_intersection(
            LocalDateRange extraRange1,
            LocalDateRange extraRange2, 
            Range<LocalDate> guavaRange1, 
            Range<LocalDate> guavaRange2) {

        LocalDateRange extra = null;
        try {
            extra = extraRange1.intersection(extraRange2);
        } catch (DateTimeException ex) {
            // continue
        }
        Range<LocalDate> guava = null;
        try {
            guava = guavaRange1.intersection(guavaRange2);
        } catch (IllegalArgumentException ex) {
            // continue
        }
        if (extra == null) {
            assertEquals(guava, extra);
        } else if (guava != null) {
            assertEquals(guava.lowerEndpoint(), extra.getStart());
            assertEquals(guava.upperEndpoint(), extra.getEnd());
        }
    }

    @Test
    @UseDataProvider("data_crossCheckGuava")
    public void crossCheckGuava_span(
            LocalDateRange extraRange1,
            LocalDateRange extraRange2, 
            Range<LocalDate> guavaRange1, 
            Range<LocalDate> guavaRange2) {

        LocalDateRange extra = extraRange1.span(extraRange2);
        Range<LocalDate> guava = guavaRange1.span(guavaRange2);
        assertEquals(guava.lowerEndpoint(), extra.getStart());
        assertEquals(guava.upperEndpoint(), extra.getEnd());
    }

}
