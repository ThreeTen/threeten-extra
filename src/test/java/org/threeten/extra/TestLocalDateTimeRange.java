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
import java.time.LocalDateTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestLocalDateTimeRange {

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(LocalDateTimeRange.class));
    }

    //-----------------------------------------------------------------------
    // of()
    //-----------------------------------------------------------------------
    @DataProvider
    public Object[][] data_test_of_npe() {
        LocalDateTime now = LocalDateTime.now();
        return new Object[][]{
            {null, null},
            {now, null},
            {null, now}
        };
    }

    @Test(dataProvider = "data_test_of_npe", expectedExceptions = NullPointerException.class)
    public void test_of_npe(LocalDateTime start, LocalDateTime end) {
        LocalDateTimeRange.of(start, end);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_of_iae() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.plusNanos(1);
        LocalDateTimeRange.of(start, end);
    }

    @DataProvider
    public Object[][] data_test_of() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusNanos(1);
        return new Object[][]{
            {start, start},
            {end, end},
            {start, end}
        };
    }

    @Test(dataProvider = "data_test_of")
    public void test_of(LocalDateTime start, LocalDateTime end) {
        LocalDateTimeRange.of(start, end);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @DataProvider
    Object[][] data_test_hashCode() {
        LocalDateTime a = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime b = LocalDateTime.of(2014, 12, 31, 0, 0);
        LocalDateTime c = a;

        return new Object[][]{
            {LocalDateTimeRange.of(a, b), LocalDateTimeRange.of(a, b), true},
            {LocalDateTimeRange.of(c, b), LocalDateTimeRange.of(a, b), true},
            {LocalDateTimeRange.of(a, a), LocalDateTimeRange.of(a, b), false}
        };

    }

    @Test(dataProvider = "data_test_hashCode")
    public void test_hashCode(LocalDateTimeRange a, LocalDateTimeRange b, boolean expected) {
        assertEquals(a.hashCode() == b.hashCode(), expected);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @DataProvider
    Object[][] data_test_equals() {
        LocalDateTime a = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime b = LocalDateTime.of(2014, 12, 31, 0, 0);
        LocalDateTime c = a;

        return new Object[][]{
            {LocalDateTimeRange.of(a, b), LocalDateTimeRange.of(a, b), true},
            {LocalDateTimeRange.of(c, b), LocalDateTimeRange.of(a, b), true},
            {LocalDateTimeRange.of(a, a), LocalDateTimeRange.of(a, b), false},
            {LocalDateTimeRange.of(a, b), null, false}
        };

    }

    @Test(dataProvider = "data_test_equals")
    public void test_equals(LocalDateTimeRange a, LocalDateTimeRange b, boolean expected) {
        assertEquals(a.equals(b), expected);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider
    public Object[][] data_test_toString() {
        LocalDateTime a = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime b = LocalDateTime.of(2014, 1, 1, 12, 30);

        return new Object[][]{
            {LocalDateTimeRange.of(a, b), "2014-01-01T00:00:00/2014-01-01T12:30:00"}
        };
    }

    @Test(dataProvider = "data_test_toString")
    public void test_toString(LocalDateTimeRange i, String expected) {
        assertEquals(i.toString(), expected);
    }

    //-----------------------------------------------------------------------
    // intersects()
    //-----------------------------------------------------------------------
    @DataProvider
    public Object[][] data_test_intersects() {
        LocalDateTime a = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime b = LocalDateTime.of(2014, 1, 1, 12, 0);
        LocalDateTime c = LocalDateTime.of(2014, 1, 2, 0, 0);
        LocalDateTime d = LocalDateTime.of(2014, 1, 2, 12, 0);

        return new Object[][]{
            {LocalDateTimeRange.of(a, a), LocalDateTimeRange.of(a, a), true},
            {LocalDateTimeRange.of(a, a), LocalDateTimeRange.of(b, b), false},
            {LocalDateTimeRange.of(a, b), LocalDateTimeRange.of(c, d), false},
            {LocalDateTimeRange.of(a, c), LocalDateTimeRange.of(b, d), true},
            {LocalDateTimeRange.of(a, b), LocalDateTimeRange.of(b, c), true}
        };
    }

    @Test(dataProvider = "data_test_intersects")
    public void test_overlaps(LocalDateTimeRange a, LocalDateTimeRange b, boolean expected) {
        assertEquals(a.intersects(b), expected);
    }

    //-----------------------------------------------------------------------
    // intersection()
    //-----------------------------------------------------------------------
    @DataProvider
    public Object[][] data_test_intersection() {
        LocalDateTime a = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime b = LocalDateTime.of(2014, 1, 1, 12, 0);
        LocalDateTime c = LocalDateTime.of(2014, 1, 2, 0, 0);
        LocalDateTime d = LocalDateTime.of(2014, 1, 2, 12, 0);

        return new Object[][]{
            {LocalDateTimeRange.of(a, a), LocalDateTimeRange.of(a, a), LocalDateTimeRange.of(a, a)},
            {LocalDateTimeRange.of(a, b), LocalDateTimeRange.of(b, c), LocalDateTimeRange.of(b, b)},
            {LocalDateTimeRange.of(a, c), LocalDateTimeRange.of(b, d), LocalDateTimeRange.of(b, c)}
        };
    }

    @Test(dataProvider = "data_test_intersection", dependsOnMethods = { "test_equals", "test_intersects"})
    public void test_intersection(LocalDateTimeRange a, LocalDateTimeRange b, LocalDateTimeRange c) {
        assertEquals(a.intersection(b).get(), c);
    }
    
    //-----------------------------------------------------------------------
    // isBefore()
    //-----------------------------------------------------------------------
    @DataProvider
    public Object[][] data_test_isBefore() {
        LocalDateTime a = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime b = LocalDateTime.of(2014, 1, 1, 12, 0);
        LocalDateTimeRange range = LocalDateTimeRange.of(a, b);

        return new Object[][]{
            {range, a, false},
            {range, b, false},
            {range, b.plusNanos(1), true}
        };
    }
    
    @Test(dataProvider = "data_test_isBefore")
    public void test_isBefore(LocalDateTimeRange range, LocalDateTime localDateTime, boolean expected) {
        assertEquals(range.isBefore(localDateTime), expected); 
    }
    
    //-----------------------------------------------------------------------
    // isAfter()
    //-----------------------------------------------------------------------
    @DataProvider
    public Object[][] data_test_isAfter() {
        LocalDateTime a = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime b = LocalDateTime.of(2014, 1, 1, 12, 0);
        LocalDateTimeRange range = LocalDateTimeRange.of(a, b);

        return new Object[][]{
            {range, a, false},
            {range, b, false},
            {range, a.minusNanos(1), true}
        };
    }
    
    @Test(dataProvider = "data_test_isAfter")
    public void test_isAfter(LocalDateTimeRange range, LocalDateTime localDateTime, boolean expected) {
        assertEquals(range.isAfter(localDateTime), expected); 
    }

}
