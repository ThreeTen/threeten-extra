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
package org.threeten.extra.scale;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test UtcInstant.
 */
@Test
public class TestUtcInstant {

    private static final long MJD_1972_12_30 = 41681;
    private static final long MJD_1972_12_31_LEAP = 41682;
    private static final long MJD_1973_01_01 = 41683;
    private static final long MJD_1973_12_31_LEAP = MJD_1972_12_31_LEAP + 365;
    private static final long SECS_PER_DAY = 24L * 60 * 60;
    private static final long NANOS_PER_SEC = 1000000000L;
    private static final long NANOS_PER_DAY = SECS_PER_DAY * NANOS_PER_SEC;
    private static final long NANOS_PER_LEAP_DAY = (SECS_PER_DAY + 1) * NANOS_PER_SEC;

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(UtcInstant.class));
        assertTrue(Comparable.class.isAssignableFrom(UtcInstant.class));
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test
    public void test_serialization() throws Exception {
        UtcInstant test = UtcInstant.ofModifiedJulianDay(2, 3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(ois.readObject(), test);
        }
    }

    //-----------------------------------------------------------------------
    // ofModififiedJulianDay(long,long)
    //-----------------------------------------------------------------------
    @Test
    public void factory_ofModifiedJulianDay_long_long() {
        for (long i = -2; i <= 2; i++) {
            for (int j = 0; j < 10; j++) {
                UtcInstant t = UtcInstant.ofModifiedJulianDay(i, j);
                assertEquals(t.getModifiedJulianDay(), i);
                assertEquals(t.getNanoOfDay(), j);
                assertEquals(t.isLeapSecond(), false);
            }
        }
    }

    @Test
    public void factory_ofModifiedJulianDay_long_long_startLeap() {
        UtcInstant t = UtcInstant.ofModifiedJulianDay(MJD_1972_12_31_LEAP, NANOS_PER_DAY);
        assertEquals(t.getModifiedJulianDay(), MJD_1972_12_31_LEAP);
        assertEquals(t.getNanoOfDay(), NANOS_PER_DAY);
    }

    @Test
    public void factory_ofModifiedJulianDay_long_long_endLeap() {
        UtcInstant t = UtcInstant.ofModifiedJulianDay(MJD_1972_12_31_LEAP, NANOS_PER_LEAP_DAY - 1);
        assertEquals(t.getModifiedJulianDay(), MJD_1972_12_31_LEAP);
        assertEquals(t.getNanoOfDay(), NANOS_PER_LEAP_DAY - 1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void factory_ofModifiedJulianDay_long_long_nanosNegative() {
        UtcInstant.ofModifiedJulianDay(MJD_1973_01_01, -1);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void factory_ofModifiedJulianDay_long_long_nanosTooBig_notLeap() {
        UtcInstant.ofModifiedJulianDay(MJD_1973_01_01, NANOS_PER_DAY);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void factory_ofModifiedJulianDay_long_long_nanosTooBig_leap() {
        UtcInstant.ofModifiedJulianDay(MJD_1972_12_31_LEAP, NANOS_PER_LEAP_DAY);
    }

    //-----------------------------------------------------------------------
    // of(Instant)
    //-----------------------------------------------------------------------
    @Test
    public void factory_of_Instant() {
        UtcInstant test = UtcInstant.of(Instant.ofEpochSecond(0, 2));  // 1970-01-01
        assertEquals(test.getModifiedJulianDay(), 40587);
        assertEquals(test.getNanoOfDay(), 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_of_Instant_null() {
        UtcInstant.of((Instant) null);
    }

    //-----------------------------------------------------------------------
    // of(TaiInstant)
    //-----------------------------------------------------------------------
    @Test
    public void factory_of_TaiInstant() {
        for (int i = -1000; i < 1000; i++) {
            for (int j = 0; j < 10; j++) {
                UtcInstant expected = UtcInstant.ofModifiedJulianDay(36204 + i, j * NANOS_PER_SEC + 2L);
                TaiInstant tai = TaiInstant.ofTaiSeconds(i * SECS_PER_DAY + j + 10, 2);
                assertEquals(UtcInstant.of(tai), expected);
            }
        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_of_TaiInstant_null() {
        UtcInstant.of((TaiInstant) null);
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    public void factory_parse_CharSequence() {
        assertEquals(UtcInstant.parse("1972-12-31T23:59:59Z"), UtcInstant.ofModifiedJulianDay(MJD_1972_12_31_LEAP, NANOS_PER_DAY - NANOS_PER_SEC));
        assertEquals(UtcInstant.parse("1972-12-31T23:59:60Z"), UtcInstant.ofModifiedJulianDay(MJD_1972_12_31_LEAP, NANOS_PER_DAY));
    }

    @DataProvider(name = "BadParse")
    Object[][] provider_badParse() {
        return new Object[][] {
            {""},
            {"A"},
            {"2012-13-01T00:00:00Z"},  // bad month
        };
    }

    @Test(dataProvider = "BadParse", expectedExceptions = DateTimeParseException.class)
    public void factory_parse_CharSequence_invalid(String str) {
        UtcInstant.parse(str);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void factory_parse_CharSequence_invalidLeapSecond() {
        UtcInstant.parse("1972-11-11T23:59:60Z");  // leap second but not leap day
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_parse_CharSequence_null() {
        UtcInstant.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // withModifiedJulianDay()
    //-----------------------------------------------------------------------
    @DataProvider(name = "WithModifiedJulianDay")
    Object[][] provider_withModifiedJulianDay() {
        return new Object[][] {
            {0L, 12345L, 1L, 1L, 12345L},
            {0L, 12345L, -1L, -1L, 12345L},
            {7L, 12345L, 2L, 2L, 12345L},
            {7L, 12345L, -2L, -2L, 12345L},
            {-99L, 12345L, 3L, 3L, 12345L},
            {-99L, 12345L, -3L, -3L, 12345L},
            {MJD_1972_12_31_LEAP, NANOS_PER_DAY, MJD_1972_12_30, null, null},
            {MJD_1972_12_31_LEAP, NANOS_PER_DAY, MJD_1972_12_31_LEAP, MJD_1972_12_31_LEAP, NANOS_PER_DAY},
            {MJD_1972_12_31_LEAP, NANOS_PER_DAY, MJD_1973_01_01, null, null},
            {MJD_1972_12_31_LEAP, NANOS_PER_DAY, MJD_1973_12_31_LEAP, MJD_1973_12_31_LEAP, NANOS_PER_DAY},
        };
    }

    @Test(dataProvider = "WithModifiedJulianDay")
    public void test_withModifiedJulianDay(long mjd, long nanos, long newMjd, Long expectedMjd, Long expectedNanos) {
        UtcInstant i = UtcInstant.ofModifiedJulianDay(mjd, nanos);
        if (expectedMjd != null) {
            i = i.withModifiedJulianDay(newMjd);
            assertEquals(i.getModifiedJulianDay(), expectedMjd.longValue());
            assertEquals(i.getNanoOfDay(), expectedNanos.longValue());
        } else {
            try {
                i = i.withModifiedJulianDay(newMjd);
                fail();
            } catch (DateTimeException ex) {
                // expected
            }
        }
    }

    //-----------------------------------------------------------------------
    // withNanoOfDay()
    //-----------------------------------------------------------------------
    @DataProvider(name = "WithNanoOfDay")
    Object[][] provider_withNanoOfDay() {
        return new Object[][] {
            {0L, 12345L, 1L, 0L, 1L},
            {0L, 12345L, -1L, null, null},
            {7L, 12345L, 2L, 7L, 2L},
            {-99L, 12345L, 3L, -99L, 3L},
            {MJD_1972_12_30, NANOS_PER_DAY - 1, NANOS_PER_DAY - 1, MJD_1972_12_30, NANOS_PER_DAY - 1},
            {MJD_1972_12_31_LEAP, NANOS_PER_DAY - 1, NANOS_PER_DAY - 1, MJD_1972_12_31_LEAP, NANOS_PER_DAY - 1},
            {MJD_1973_01_01, NANOS_PER_DAY - 1, NANOS_PER_DAY - 1, MJD_1973_01_01, NANOS_PER_DAY - 1},
            {MJD_1972_12_30, NANOS_PER_DAY - 1, NANOS_PER_DAY, null, null},
            {MJD_1972_12_31_LEAP, NANOS_PER_DAY - 1, NANOS_PER_DAY, MJD_1972_12_31_LEAP, NANOS_PER_DAY},
            {MJD_1973_01_01, NANOS_PER_DAY - 1, NANOS_PER_DAY, null, null},
            {MJD_1972_12_30, NANOS_PER_DAY - 1, NANOS_PER_LEAP_DAY - 1, null, null},
            {MJD_1972_12_31_LEAP, NANOS_PER_DAY - 1, NANOS_PER_LEAP_DAY - 1, MJD_1972_12_31_LEAP, NANOS_PER_LEAP_DAY - 1},
            {MJD_1973_01_01, NANOS_PER_DAY - 1, NANOS_PER_LEAP_DAY - 1, null, null},
            {MJD_1972_12_30, NANOS_PER_DAY - 1, NANOS_PER_LEAP_DAY, null, null},
            {MJD_1972_12_31_LEAP, NANOS_PER_DAY - 1, NANOS_PER_LEAP_DAY, null, null},
            {MJD_1973_01_01, NANOS_PER_DAY - 1, NANOS_PER_LEAP_DAY, null, null},
        };
    }

    @Test(dataProvider = "WithNanoOfDay")
    public void test_withNanoOfDay(long mjd, long nanos, long newNanoOfDay, Long expectedMjd, Long expectedNanos) {
        UtcInstant i = UtcInstant.ofModifiedJulianDay(mjd, nanos);
        if (expectedMjd != null) {
            i = i.withNanoOfDay(newNanoOfDay);
            assertEquals(i.getModifiedJulianDay(), expectedMjd.longValue());
            assertEquals(i.getNanoOfDay(), expectedNanos.longValue());
        } else {
            try {
                i = i.withNanoOfDay(newNanoOfDay);
                fail();
            } catch (DateTimeException ex) {
                // expected
            }
        }
    }

    //-----------------------------------------------------------------------
    // plus(Duration)
    //-----------------------------------------------------------------------
    @DataProvider(name = "Plus")
    Object[][] provider_plus() {
        return new Object[][] {
            {0, 0,  -2 * SECS_PER_DAY, 5, -2, 5},
            {0, 0,  -1 * SECS_PER_DAY, 1, -1, 1},
            {0, 0,  -1 * SECS_PER_DAY, 0, -1, 0},
            {0, 0,  0,        -2, -1,  NANOS_PER_DAY - 2},
            {0, 0,  0,        -1, -1,  NANOS_PER_DAY - 1},
            {0, 0,  0,         0,  0,  0},
            {0, 0,  0,         1,  0,  1},
            {0, 0,  0,         2,  0,  2},
            {0, 0,  1,         0,  0,  1 * NANOS_PER_SEC},
            {0, 0,  2,         0,  0,  2 * NANOS_PER_SEC},
            {0, 0,  3, 333333333,  0,  3 * NANOS_PER_SEC + 333333333},
            {0, 0,  1 * SECS_PER_DAY, 0,  1, 0},
            {0, 0,  1 * SECS_PER_DAY, 1,  1, 1},
            {0, 0,  2 * SECS_PER_DAY, 5,  2, 5},

            {1, 0,  -2 * SECS_PER_DAY, 5, -1, 5},
            {1, 0,  -1 * SECS_PER_DAY, 1, 0, 1},
            {1, 0,  -1 * SECS_PER_DAY, 0, 0, 0},
            {1, 0,  0,        -2,  0,  NANOS_PER_DAY - 2},
            {1, 0,  0,        -1,  0,  NANOS_PER_DAY - 1},
            {1, 0,  0,         0,  1,  0},
            {1, 0,  0,         1,  1,  1},
            {1, 0,  0,         2,  1,  2},
            {1, 0,  1,         0,  1,  1 * NANOS_PER_SEC},
            {1, 0,  2,         0,  1,  2 * NANOS_PER_SEC},
            {1, 0,  3, 333333333,  1,  3 * NANOS_PER_SEC + 333333333},
            {1, 0,  1 * SECS_PER_DAY, 0,  2, 0},
            {1, 0,  1 * SECS_PER_DAY, 1,  2, 1},
            {1, 0,  2 * SECS_PER_DAY, 5,  3, 5},
        };
    }

    @Test(dataProvider = "Plus")
    public void test_plus(long mjd, long nanos, long plusSeconds, int plusNanos, long expectedMjd, long expectedNanos) {
        UtcInstant i = UtcInstant.ofModifiedJulianDay(mjd, nanos).plus(Duration.ofSeconds(plusSeconds, plusNanos));
        assertEquals(i.getModifiedJulianDay(), expectedMjd);
        assertEquals(i.getNanoOfDay(), expectedNanos);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_overflowTooBig() {
        UtcInstant i = UtcInstant.ofModifiedJulianDay(Long.MAX_VALUE, NANOS_PER_DAY - 1);
        i.plus(Duration.ofNanos(1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_overflowTooSmall() {
        UtcInstant i = UtcInstant.ofModifiedJulianDay(Long.MIN_VALUE, 0);
        i.plus(Duration.ofNanos(-1));
    }

    //-----------------------------------------------------------------------
    // minus(Duration)
    //-----------------------------------------------------------------------
    @DataProvider(name = "Minus")
    Object[][] provider_minus() {
        return new Object[][] {
            {0, 0,  2 * SECS_PER_DAY, -5, -2, 5},
            {0, 0,  1 * SECS_PER_DAY, -1, -1, 1},
            {0, 0,  1 * SECS_PER_DAY, 0, -1, 0},
            {0, 0,  0,          2, -1,  NANOS_PER_DAY - 2},
            {0, 0,  0,          1, -1,  NANOS_PER_DAY - 1},
            {0, 0,  0,          0,  0,  0},
            {0, 0,  0,         -1,  0,  1},
            {0, 0,  0,         -2,  0,  2},
            {0, 0,  -1,         0,  0,  1 * NANOS_PER_SEC},
            {0, 0,  -2,         0,  0,  2 * NANOS_PER_SEC},
            {0, 0,  -3, -333333333,  0,  3 * NANOS_PER_SEC + 333333333},
            {0, 0,  -1 * SECS_PER_DAY, 0,  1, 0},
            {0, 0,  -1 * SECS_PER_DAY, -1,  1, 1},
            {0, 0,  -2 * SECS_PER_DAY, -5,  2, 5},

            {1, 0,  2 * SECS_PER_DAY, -5, -1, 5},
            {1, 0,  1 * SECS_PER_DAY, -1, 0, 1},
            {1, 0,  1 * SECS_PER_DAY, 0, 0, 0},
            {1, 0,  0,          2,  0,  NANOS_PER_DAY - 2},
            {1, 0,  0,          1,  0,  NANOS_PER_DAY - 1},
            {1, 0,  0,          0,  1,  0},
            {1, 0,  0,         -1,  1,  1},
            {1, 0,  0,         -2,  1,  2},
            {1, 0,  -1,         0,  1,  1 * NANOS_PER_SEC},
            {1, 0,  -2,         0,  1,  2 * NANOS_PER_SEC},
            {1, 0,  -3, -333333333,  1,  3 * NANOS_PER_SEC + 333333333},
            {1, 0,  -1 * SECS_PER_DAY, 0,  2, 0},
            {1, 0,  -1 * SECS_PER_DAY, -1,  2, 1},
            {1, 0,  -2 * SECS_PER_DAY, -5,  3, 5},
        };
    }

    @Test(dataProvider = "Minus")
    public void test_minus(long mjd, long nanos, long minusSeconds, int minusNanos, long expectedMjd, long expectedNanos) {
        UtcInstant i = UtcInstant.ofModifiedJulianDay(mjd, nanos).minus(Duration.ofSeconds(minusSeconds, minusNanos));
        assertEquals(i.getModifiedJulianDay(), expectedMjd);
        assertEquals(i.getNanoOfDay(), expectedNanos);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_overflowTooSmall() {
        UtcInstant i = UtcInstant.ofModifiedJulianDay(Long.MIN_VALUE, 0);
        i.minus(Duration.ofNanos(1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_overflowTooBig() {
        UtcInstant i = UtcInstant.ofModifiedJulianDay(Long.MAX_VALUE, NANOS_PER_DAY - 1);
        i.minus(Duration.ofNanos(-1));
    }

    //-----------------------------------------------------------------------
    // durationUntil()
    //-----------------------------------------------------------------------
    @Test
    public void test_durationUntil_oneDayNoLeap() {
        UtcInstant utc1 = UtcInstant.ofModifiedJulianDay(MJD_1972_12_30, 0);
        UtcInstant utc2 = UtcInstant.ofModifiedJulianDay(MJD_1972_12_31_LEAP, 0);
        Duration test = utc1.durationUntil(utc2);
        assertEquals(test.getSeconds(), 86400);
        assertEquals(test.getNano(), 0);
    }

    @Test
    public void test_durationUntil_oneDayLeap() {
        UtcInstant utc1 = UtcInstant.ofModifiedJulianDay(MJD_1972_12_31_LEAP, 0);
        UtcInstant utc2 = UtcInstant.ofModifiedJulianDay(MJD_1973_01_01, 0);
        Duration test = utc1.durationUntil(utc2);
        assertEquals(test.getSeconds(), 86401);
        assertEquals(test.getNano(), 0);
    }

    @Test
    public void test_durationUntil_oneDayLeapNegative() {
        UtcInstant utc1 = UtcInstant.ofModifiedJulianDay(MJD_1973_01_01, 0);
        UtcInstant utc2 = UtcInstant.ofModifiedJulianDay(MJD_1972_12_31_LEAP, 0);
        Duration test = utc1.durationUntil(utc2);
        assertEquals(test.getSeconds(), -86401);
        assertEquals(test.getNano(), 0);
    }

    //-----------------------------------------------------------------------
    // toTaiInstant()
    //-----------------------------------------------------------------------
    @Test
    public void test_toTaiInstant() {
        for (int i = -1000; i < 1000; i++) {
            for (int j = 0; j < 10; j++) {
                UtcInstant utc = UtcInstant.ofModifiedJulianDay(36204 + i, j * NANOS_PER_SEC + 2L);
                TaiInstant test = utc.toTaiInstant();
                assertEquals(test.getTaiSeconds(), i * SECS_PER_DAY + j + 10);
                assertEquals(test.getNano(), 2);
            }
        }
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_toTaiInstant_maxInvalid() {
        UtcInstant utc = UtcInstant.ofModifiedJulianDay(Long.MAX_VALUE, 0);
        utc.toTaiInstant();
    }

    //-----------------------------------------------------------------------
    // toInstant()
    //-----------------------------------------------------------------------
    @Test
    public void test_toInstant() {
        for (int i = -1000; i < 1000; i++) {
            for (int j = 0; j < 10; j++) {
                Instant expected = Instant.ofEpochSecond(315532800 + i * SECS_PER_DAY + j).plusNanos(2);
                UtcInstant test = UtcInstant.ofModifiedJulianDay(44239 + i, j * NANOS_PER_SEC + 2);
                assertEquals(test.toInstant(), expected, "Loop " + i + " " + j);
            }
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_comparisons() {
        doTest_comparisons_UtcInstant(
            UtcInstant.ofModifiedJulianDay(-2L, 0),
            UtcInstant.ofModifiedJulianDay(-2L, NANOS_PER_DAY - 2),
            UtcInstant.ofModifiedJulianDay(-2L, NANOS_PER_DAY - 1),
            UtcInstant.ofModifiedJulianDay(-1L, 0),
            UtcInstant.ofModifiedJulianDay(-1L, 1),
            UtcInstant.ofModifiedJulianDay(-1L, NANOS_PER_DAY - 2),
            UtcInstant.ofModifiedJulianDay(-1L, NANOS_PER_DAY - 1),
            UtcInstant.ofModifiedJulianDay(0L, 0),
            UtcInstant.ofModifiedJulianDay(0L, 1),
            UtcInstant.ofModifiedJulianDay(0L, 2),
            UtcInstant.ofModifiedJulianDay(0L, NANOS_PER_DAY - 1),
            UtcInstant.ofModifiedJulianDay(1L, 0),
            UtcInstant.ofModifiedJulianDay(2L, 0)
        );
    }

    void doTest_comparisons_UtcInstant(UtcInstant... instants) {
        for (int i = 0; i < instants.length; i++) {
            UtcInstant a = instants[i];
            for (int j = 0; j < instants.length; j++) {
                UtcInstant b = instants[j];
                if (i < j) {
                    assertEquals(a.compareTo(b), -1, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertEquals(a.compareTo(b), 1, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_ObjectNull() {
        UtcInstant a = UtcInstant.ofModifiedJulianDay(0L, 0);
        a.compareTo(null);
    }

    @Test(expectedExceptions = ClassCastException.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void test_compareToNonUtcInstant() {
        Comparable c = UtcInstant.ofModifiedJulianDay(0L, 2);
        c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals() {
        UtcInstant test5a = UtcInstant.ofModifiedJulianDay(5L, 20);
        UtcInstant test5b = UtcInstant.ofModifiedJulianDay(5L, 20);
        UtcInstant test5n = UtcInstant.ofModifiedJulianDay(5L, 30);
        UtcInstant test6 = UtcInstant.ofModifiedJulianDay(6L, 20);

        assertEquals(test5a.equals(test5a), true);
        assertEquals(test5a.equals(test5b), true);
        assertEquals(test5a.equals(test5n), false);
        assertEquals(test5a.equals(test6), false);

        assertEquals(test5b.equals(test5a), true);
        assertEquals(test5b.equals(test5b), true);
        assertEquals(test5b.equals(test5n), false);
        assertEquals(test5b.equals(test6), false);

        assertEquals(test5n.equals(test5a), false);
        assertEquals(test5n.equals(test5b), false);
        assertEquals(test5n.equals(test5n), true);
        assertEquals(test5n.equals(test6), false);

        assertEquals(test6.equals(test5a), false);
        assertEquals(test6.equals(test5b), false);
        assertEquals(test6.equals(test5n), false);
        assertEquals(test6.equals(test6), true);
    }

    @Test
    public void test_equals_null() {
        UtcInstant test5 = UtcInstant.ofModifiedJulianDay(5L, 20);
        assertEquals(test5.equals(null), false);
    }

    @Test
    public void test_equals_otherClass() {
        UtcInstant test5 = UtcInstant.ofModifiedJulianDay(5L, 20);
        assertEquals(test5.equals(""), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_hashCode() {
        UtcInstant test5a = UtcInstant.ofModifiedJulianDay(5L, 20);
        UtcInstant test5b = UtcInstant.ofModifiedJulianDay(5L, 20);
        UtcInstant test5n = UtcInstant.ofModifiedJulianDay(5L, 30);
        UtcInstant test6 = UtcInstant.ofModifiedJulianDay(6L, 20);

        assertEquals(test5a.hashCode() == test5a.hashCode(), true);
        assertEquals(test5a.hashCode() == test5b.hashCode(), true);
        assertEquals(test5b.hashCode() == test5b.hashCode(), true);

        assertEquals(test5a.hashCode() == test5n.hashCode(), false);
        assertEquals(test5a.hashCode() == test6.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name = "ToString")
    Object[][] provider_toString() {
        return new Object[][] {
            {40587, 0, "1970-01-01T00:00:00Z"},
            {40588, 1, "1970-01-02T00:00:00.000000001Z"},
            {40588, 999, "1970-01-02T00:00:00.000000999Z"},
            {40588, 1000, "1970-01-02T00:00:00.000001Z"},
            {40588, 999000, "1970-01-02T00:00:00.000999Z"},
            {40588, 1000000, "1970-01-02T00:00:00.001Z"},
            {40618, 999999999, "1970-02-01T00:00:00.999999999Z"},
            {40619, 1000000000, "1970-02-02T00:00:01Z"},
            {40620, 60L * 1000000000L, "1970-02-03T00:01:00Z"},
            {40621, 60L * 60L * 1000000000L, "1970-02-04T01:00:00Z"},
            {MJD_1972_12_31_LEAP, 24L * 60L * 60L * 1000000000L - 1000000000L, "1972-12-31T23:59:59Z"},
            {MJD_1972_12_31_LEAP, NANOS_PER_DAY, "1972-12-31T23:59:60Z"},
            {MJD_1973_01_01, 0, "1973-01-01T00:00:00Z"},
        };
    }

    @Test(dataProvider = "ToString")
    public void test_toString(long mjd, long nod, String expected) {
        assertEquals(UtcInstant.ofModifiedJulianDay(mjd, nod).toString(), expected);
    }

    @Test(dataProvider = "ToString")
    public void test_toString_parse(long mjd, long nod, String str) {
        assertEquals(UtcInstant.parse(str), UtcInstant.ofModifiedJulianDay(mjd, nod));
    }

}
