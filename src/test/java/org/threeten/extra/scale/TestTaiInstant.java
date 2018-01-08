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
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test TaiInstant.
 */
@Test
public class TestTaiInstant {

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(TaiInstant.class));
        assertTrue(Comparable.class.isAssignableFrom(TaiInstant.class));
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test
    public void test_serialization() throws Exception {
        TaiInstant test = TaiInstant.ofTaiSeconds(2, 3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(ois.readObject(), test);
        }
    }

    //-----------------------------------------------------------------------
    // ofTaiSeconds(long,long)
    //-----------------------------------------------------------------------
    @Test
    public void factory_ofTaiSecondslong_long() {
        for (long i = -2; i <= 2; i++) {
            for (int j = 0; j < 10; j++) {
                TaiInstant t = TaiInstant.ofTaiSeconds(i, j);
                assertEquals(t.getTaiSeconds(), i);
                assertEquals(t.getNano(), j);
            }
            for (int j = -10; j < 0; j++) {
                TaiInstant t = TaiInstant.ofTaiSeconds(i, j);
                assertEquals(t.getTaiSeconds(), i - 1);
                assertEquals(t.getNano(), j + 1000000000);
            }
            for (int j = 999999990; j < 1000000000; j++) {
                TaiInstant t = TaiInstant.ofTaiSeconds(i, j);
                assertEquals(t.getTaiSeconds(), i);
                assertEquals(t.getNano(), j);
            }
        }
    }

    @Test
    public void factory_ofTaiSeconds_long_long_nanosNegativeAdjusted() {
        TaiInstant test = TaiInstant.ofTaiSeconds(2L, -1);
        assertEquals(test.getTaiSeconds(), 1);
        assertEquals(test.getNano(), 999999999);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void factory_ofTaiSeconds_long_long_tooBig() {
        TaiInstant.ofTaiSeconds(Long.MAX_VALUE, 1000000000);
    }

    //-----------------------------------------------------------------------
    // of(Instant)
    //-----------------------------------------------------------------------
    @Test
    public void factory_of_Instant() {
        TaiInstant test = TaiInstant.of(Instant.ofEpochSecond(0, 2));
        assertEquals(test.getTaiSeconds(), (40587L - 36204) * 24 * 60 * 60 + 10); //((1970 - 1958) * 365 + 3) * 24 * 60 * 60 + 10);
        assertEquals(test.getNano(), 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_of_Instant_null() {
        TaiInstant.of((Instant) null);
    }

    //-----------------------------------------------------------------------
    // of(UtcInstant)
    //-----------------------------------------------------------------------
    @Test
    public void factory_of_UtcInstant() {
        for (int i = -1000; i < 1000; i++) {
            for (int j = 0; j < 10; j++) {
                TaiInstant test = TaiInstant.of(UtcInstant.ofModifiedJulianDay(36204 + i, j * 1000000000L + 2L));
                assertEquals(test.getTaiSeconds(), i * 24 * 60 * 60 + j + 10);
                assertEquals(test.getNano(), 2);
            }
        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_of_UtcInstant_null() {
        TaiInstant.of((UtcInstant) null);
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    public void factory_parse_CharSequence() {
        for (int i = -1000; i < 1000; i++) {
            for (int j = 900000000; j < 990000000; j += 10000000) {
                String str = i + "." + j + "s(TAI)";
                TaiInstant test = TaiInstant.parse(str);
                assertEquals(test.getTaiSeconds(), i);
                assertEquals(test.getNano(), j);
            }
        }
    }

    @DataProvider(name = "BadParse")
    Object[][] provider_badParse() {
        return new Object[][] {
            {"A.123456789s(TAI)"},
            {"123.12345678As(TAI)"},
            {"123.123456789"},
            {"123.123456789s"},
            {"+123.123456789s(TAI)"},
            {"-123.123s(TAI)"},
        };
    }

    @Test(dataProvider = "BadParse", expectedExceptions = DateTimeParseException.class)
    public void factory_parse_CharSequence_invalid(String str) {
        TaiInstant.parse(str);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void factory_parse_CharSequence_null() {
        TaiInstant.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // withTAISeconds()
    //-----------------------------------------------------------------------
    @DataProvider(name = "withTAISeconds")
    Object[][] provider_withTAISeconds() {
        return new Object[][] {
            {0L, 12345L, 1L, 1L, 12345L},
            {0L, 12345L, -1L, -1L, 12345L},
            {7L, 12345L, 2L, 2L, 12345L},
            {7L, 12345L, -2L, -2L, 12345L},
            {-99L, 12345L, 3L, 3L, 12345L},
            {-99L, 12345L, -3L, -3L, 12345L},
        };
    }

    @Test(dataProvider = "withTAISeconds")
    public void test_withTAISeconds(long tai, long nanos, long newTai, Long expectedTai, Long expectedNanos) {
        TaiInstant i = TaiInstant.ofTaiSeconds(tai, nanos).withTaiSeconds(newTai);
        assertEquals(i.getTaiSeconds(), expectedTai.longValue());
        assertEquals(i.getNano(), expectedNanos.longValue());
    }

    //-----------------------------------------------------------------------
    // withNano()
    //-----------------------------------------------------------------------
    @DataProvider(name = "withNanoOfSecond")
    Object[][] provider_withNano() {
        return new Object[][] {
            {0L, 12345L, 1, 0L, 1L},
            {7L, 12345L, 2, 7L, 2L},
            {-99L, 12345L, 3, -99L, 3L},
            {-99L, 12345L, 999999999, -99L, 999999999L},
            {-99L, 12345L, -1, null, null},
            {-99L, 12345L, 1000000000, null, null},
        };
    }

    @Test(dataProvider = "withNanoOfSecond")
    public void test_withNano(long tai, long nanos, int newNano, Long expectedTai, Long expectedNanos) {
        TaiInstant i = TaiInstant.ofTaiSeconds(tai, nanos);
        if (expectedTai != null) {
            i = i.withNano(newNano);
            assertEquals(i.getTaiSeconds(), expectedTai.longValue());
            assertEquals(i.getNano(), expectedNanos.longValue());
        } else {
            try {
                i = i.withNano(newNano);
                fail();
            } catch (IllegalArgumentException ex) {
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
            {Long.MIN_VALUE, 0, Long.MAX_VALUE, 0, -1, 0},

            {-4, 666666667, -4, 666666667, -7, 333333334},
            {-4, 666666667, -3,         0, -7, 666666667},
            {-4, 666666667, -2,         0, -6, 666666667},
            {-4, 666666667, -1,         0, -5, 666666667},
            {-4, 666666667, -1, 333333334, -4,         1},
            {-4, 666666667, -1, 666666667, -4, 333333334},
            {-4, 666666667, -1, 999999999, -4, 666666666},
            {-4, 666666667,  0,         0, -4, 666666667},
            {-4, 666666667,  0,         1, -4, 666666668},
            {-4, 666666667,  0, 333333333, -3,         0},
            {-4, 666666667,  0, 666666666, -3, 333333333},
            {-4, 666666667,  1,         0, -3, 666666667},
            {-4, 666666667,  2,         0, -2, 666666667},
            {-4, 666666667,  3,         0, -1, 666666667},
            {-4, 666666667,  3, 333333333,  0,         0},

            {-3, 0, -4, 666666667, -7, 666666667},
            {-3, 0, -3,         0, -6,         0},
            {-3, 0, -2,         0, -5,         0},
            {-3, 0, -1,         0, -4,         0},
            {-3, 0, -1, 333333334, -4, 333333334},
            {-3, 0, -1, 666666667, -4, 666666667},
            {-3, 0, -1, 999999999, -4, 999999999},
            {-3, 0,  0,         0, -3,         0},
            {-3, 0,  0,         1, -3,         1},
            {-3, 0,  0, 333333333, -3, 333333333},
            {-3, 0,  0, 666666666, -3, 666666666},
            {-3, 0,  1,         0, -2,         0},
            {-3, 0,  2,         0, -1,         0},
            {-3, 0,  3,         0,  0,         0},
            {-3, 0,  3, 333333333,  0, 333333333},

            {-2, 0, -4, 666666667, -6, 666666667},
            {-2, 0, -3,         0, -5,         0},
            {-2, 0, -2,         0, -4,         0},
            {-2, 0, -1,         0, -3,         0},
            {-2, 0, -1, 333333334, -3, 333333334},
            {-2, 0, -1, 666666667, -3, 666666667},
            {-2, 0, -1, 999999999, -3, 999999999},
            {-2, 0,  0,         0, -2,         0},
            {-2, 0,  0,         1, -2,         1},
            {-2, 0,  0, 333333333, -2, 333333333},
            {-2, 0,  0, 666666666, -2, 666666666},
            {-2, 0,  1,         0, -1,         0},
            {-2, 0,  2,         0,  0,         0},
            {-2, 0,  3,         0,  1,         0},
            {-2, 0,  3, 333333333,  1, 333333333},

            {-1, 0, -4, 666666667, -5, 666666667},
            {-1, 0, -3,         0, -4,         0},
            {-1, 0, -2,         0, -3,         0},
            {-1, 0, -1,         0, -2,         0},
            {-1, 0, -1, 333333334, -2, 333333334},
            {-1, 0, -1, 666666667, -2, 666666667},
            {-1, 0, -1, 999999999, -2, 999999999},
            {-1, 0,  0,         0, -1,         0},
            {-1, 0,  0,         1, -1,         1},
            {-1, 0,  0, 333333333, -1, 333333333},
            {-1, 0,  0, 666666666, -1, 666666666},
            {-1, 0,  1,         0,  0,         0},
            {-1, 0,  2,         0,  1,         0},
            {-1, 0,  3,         0,  2,         0},
            {-1, 0,  3, 333333333,  2, 333333333},

            {-1, 666666667, -4, 666666667, -4, 333333334},
            {-1, 666666667, -3,         0, -4, 666666667},
            {-1, 666666667, -2,         0, -3, 666666667},
            {-1, 666666667, -1,         0, -2, 666666667},
            {-1, 666666667, -1, 333333334, -1,         1},
            {-1, 666666667, -1, 666666667, -1, 333333334},
            {-1, 666666667, -1, 999999999, -1, 666666666},
            {-1, 666666667,  0,         0, -1, 666666667},
            {-1, 666666667,  0,         1, -1, 666666668},
            {-1, 666666667,  0, 333333333,  0,         0},
            {-1, 666666667,  0, 666666666,  0, 333333333},
            {-1, 666666667,  1,         0,  0, 666666667},
            {-1, 666666667,  2,         0,  1, 666666667},
            {-1, 666666667,  3,         0,  2, 666666667},
            {-1, 666666667,  3, 333333333,  3,         0},

            {0, 0, -4, 666666667, -4, 666666667},
            {0, 0, -3,         0, -3,         0},
            {0, 0, -2,         0, -2,         0},
            {0, 0, -1,         0, -1,         0},
            {0, 0, -1, 333333334, -1, 333333334},
            {0, 0, -1, 666666667, -1, 666666667},
            {0, 0, -1, 999999999, -1, 999999999},
            {0, 0,  0,         0,  0,         0},
            {0, 0,  0,         1,  0,         1},
            {0, 0,  0, 333333333,  0, 333333333},
            {0, 0,  0, 666666666,  0, 666666666},
            {0, 0,  1,         0,  1,         0},
            {0, 0,  2,         0,  2,         0},
            {0, 0,  3,         0,  3,         0},
            {0, 0,  3, 333333333,  3, 333333333},

            {0, 333333333, -4, 666666667, -3,         0},
            {0, 333333333, -3,         0, -3, 333333333},
            {0, 333333333, -2,         0, -2, 333333333},
            {0, 333333333, -1,         0, -1, 333333333},
            {0, 333333333, -1, 333333334, -1, 666666667},
            {0, 333333333, -1, 666666667,  0,         0},
            {0, 333333333, -1, 999999999,  0, 333333332},
            {0, 333333333,  0,         0,  0, 333333333},
            {0, 333333333,  0,         1,  0, 333333334},
            {0, 333333333,  0, 333333333,  0, 666666666},
            {0, 333333333,  0, 666666666,  0, 999999999},
            {0, 333333333,  1,         0,  1, 333333333},
            {0, 333333333,  2,         0,  2, 333333333},
            {0, 333333333,  3,         0,  3, 333333333},
            {0, 333333333,  3, 333333333,  3, 666666666},

            {1, 0, -4, 666666667, -3, 666666667},
            {1, 0, -3,         0, -2,         0},
            {1, 0, -2,         0, -1,         0},
            {1, 0, -1,         0,  0,         0},
            {1, 0, -1, 333333334,  0, 333333334},
            {1, 0, -1, 666666667,  0, 666666667},
            {1, 0, -1, 999999999,  0, 999999999},
            {1, 0,  0,         0,  1,         0},
            {1, 0,  0,         1,  1,         1},
            {1, 0,  0, 333333333,  1, 333333333},
            {1, 0,  0, 666666666,  1, 666666666},
            {1, 0,  1,         0,  2,         0},
            {1, 0,  2,         0,  3,         0},
            {1, 0,  3,         0,  4,         0},
            {1, 0,  3, 333333333,  4, 333333333},

            {2, 0, -4, 666666667, -2, 666666667},
            {2, 0, -3,         0, -1,         0},
            {2, 0, -2,         0,  0,         0},
            {2, 0, -1,         0,  1,         0},
            {2, 0, -1, 333333334,  1, 333333334},
            {2, 0, -1, 666666667,  1, 666666667},
            {2, 0, -1, 999999999,  1, 999999999},
            {2, 0,  0,         0,  2,         0},
            {2, 0,  0,         1,  2,         1},
            {2, 0,  0, 333333333,  2, 333333333},
            {2, 0,  0, 666666666,  2, 666666666},
            {2, 0,  1,         0,  3,         0},
            {2, 0,  2,         0,  4,         0},
            {2, 0,  3,         0,  5,         0},
            {2, 0,  3, 333333333,  5, 333333333},

            {3, 0, -4, 666666667, -1, 666666667},
            {3, 0, -3,         0,  0,         0},
            {3, 0, -2,         0,  1,         0},
            {3, 0, -1,         0,  2,         0},
            {3, 0, -1, 333333334,  2, 333333334},
            {3, 0, -1, 666666667,  2, 666666667},
            {3, 0, -1, 999999999,  2, 999999999},
            {3, 0,  0,         0,  3,         0},
            {3, 0,  0,         1,  3,         1},
            {3, 0,  0, 333333333,  3, 333333333},
            {3, 0,  0, 666666666,  3, 666666666},
            {3, 0,  1,         0,  4,         0},
            {3, 0,  2,         0,  5,         0},
            {3, 0,  3,         0,  6,         0},
            {3, 0,  3, 333333333,  6, 333333333},

            {3, 333333333, -4, 666666667,  0,         0},
            {3, 333333333, -3,         0,  0, 333333333},
            {3, 333333333, -2,         0,  1, 333333333},
            {3, 333333333, -1,         0,  2, 333333333},
            {3, 333333333, -1, 333333334,  2, 666666667},
            {3, 333333333, -1, 666666667,  3,         0},
            {3, 333333333, -1, 999999999,  3, 333333332},
            {3, 333333333,  0,         0,  3, 333333333},
            {3, 333333333,  0,         1,  3, 333333334},
            {3, 333333333,  0, 333333333,  3, 666666666},
            {3, 333333333,  0, 666666666,  3, 999999999},
            {3, 333333333,  1,         0,  4, 333333333},
            {3, 333333333,  2,         0,  5, 333333333},
            {3, 333333333,  3,         0,  6, 333333333},
            {3, 333333333,  3, 333333333,  6, 666666666},

            {Long.MAX_VALUE, 0, Long.MIN_VALUE, 0, -1, 0},
        };
    }

    @Test(dataProvider = "Plus")
    public void test_plus(long seconds, int nanos, long plusSeconds, int plusNanos, long expectedSeconds, int expectedNanoOfSecond) {
        TaiInstant i = TaiInstant.ofTaiSeconds(seconds, nanos).plus(Duration.ofSeconds(plusSeconds, plusNanos));
        assertEquals(i.getTaiSeconds(), expectedSeconds);
        assertEquals(i.getNano(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_overflowTooBig() {
        TaiInstant i = TaiInstant.ofTaiSeconds(Long.MAX_VALUE, 999999999);
        i.plus(Duration.ofSeconds(0, 1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_overflowTooSmall() {
        TaiInstant i = TaiInstant.ofTaiSeconds(Long.MIN_VALUE, 0);
        i.plus(Duration.ofSeconds(-1, 999999999));
    }

    //-----------------------------------------------------------------------
    // minus(Duration)
    //-----------------------------------------------------------------------
    @DataProvider(name = "Minus")
    Object[][] provider_minus() {
        return new Object[][] {
            {Long.MIN_VALUE, 0, Long.MIN_VALUE + 1, 0, -1, 0},

            {-4, 666666667, -4, 666666667,  0,         0},
            {-4, 666666667, -3,         0, -1, 666666667},
            {-4, 666666667, -2,         0, -2, 666666667},
            {-4, 666666667, -1,         0, -3, 666666667},
            {-4, 666666667, -1, 333333334, -3, 333333333},
            {-4, 666666667, -1, 666666667, -3,         0},
            {-4, 666666667, -1, 999999999, -4, 666666668},
            {-4, 666666667,  0,         0, -4, 666666667},
            {-4, 666666667,  0,         1, -4, 666666666},
            {-4, 666666667,  0, 333333333, -4, 333333334},
            {-4, 666666667,  0, 666666666, -4,         1},
            {-4, 666666667,  1,         0, -5, 666666667},
            {-4, 666666667,  2,         0, -6, 666666667},
            {-4, 666666667,  3,         0, -7, 666666667},
            {-4, 666666667,  3, 333333333, -7, 333333334},

            {-3, 0, -4, 666666667,  0, 333333333},
            {-3, 0, -3,         0,  0,         0},
            {-3, 0, -2,         0, -1,         0},
            {-3, 0, -1,         0, -2,         0},
            {-3, 0, -1, 333333334, -3, 666666666},
            {-3, 0, -1, 666666667, -3, 333333333},
            {-3, 0, -1, 999999999, -3,         1},
            {-3, 0,  0,         0, -3,         0},
            {-3, 0,  0,         1, -4, 999999999},
            {-3, 0,  0, 333333333, -4, 666666667},
            {-3, 0,  0, 666666666, -4, 333333334},
            {-3, 0,  1,         0, -4,         0},
            {-3, 0,  2,         0, -5,         0},
            {-3, 0,  3,         0, -6,         0},
            {-3, 0,  3, 333333333, -7, 666666667},

            {-2, 0, -4, 666666667,  1, 333333333},
            {-2, 0, -3,         0,  1,         0},
            {-2, 0, -2,         0,  0,         0},
            {-2, 0, -1,         0, -1,         0},
            {-2, 0, -1, 333333334, -2, 666666666},
            {-2, 0, -1, 666666667, -2, 333333333},
            {-2, 0, -1, 999999999, -2,         1},
            {-2, 0,  0,         0, -2,         0},
            {-2, 0,  0,         1, -3, 999999999},
            {-2, 0,  0, 333333333, -3, 666666667},
            {-2, 0,  0, 666666666, -3, 333333334},
            {-2, 0,  1,         0, -3,         0},
            {-2, 0,  2,         0, -4,         0},
            {-2, 0,  3,         0, -5,         0},
            {-2, 0,  3, 333333333, -6, 666666667},

            {-1, 0, -4, 666666667,  2, 333333333},
            {-1, 0, -3,         0,  2,         0},
            {-1, 0, -2,         0,  1,         0},
            {-1, 0, -1,         0,  0,         0},
            {-1, 0, -1, 333333334, -1, 666666666},
            {-1, 0, -1, 666666667, -1, 333333333},
            {-1, 0, -1, 999999999, -1,         1},
            {-1, 0,  0,         0, -1,         0},
            {-1, 0,  0,         1, -2, 999999999},
            {-1, 0,  0, 333333333, -2, 666666667},
            {-1, 0,  0, 666666666, -2, 333333334},
            {-1, 0,  1,         0, -2,         0},
            {-1, 0,  2,         0, -3,         0},
            {-1, 0,  3,         0, -4,         0},
            {-1, 0,  3, 333333333, -5, 666666667},

            {-1, 666666667, -4, 666666667,  3,         0},
            {-1, 666666667, -3,         0,  2, 666666667},
            {-1, 666666667, -2,         0,  1, 666666667},
            {-1, 666666667, -1,         0,  0, 666666667},
            {-1, 666666667, -1, 333333334,  0, 333333333},
            {-1, 666666667, -1, 666666667,  0,         0},
            {-1, 666666667, -1, 999999999, -1, 666666668},
            {-1, 666666667,  0,         0, -1, 666666667},
            {-1, 666666667,  0,         1, -1, 666666666},
            {-1, 666666667,  0, 333333333, -1, 333333334},
            {-1, 666666667,  0, 666666666, -1,         1},
            {-1, 666666667,  1,         0, -2, 666666667},
            {-1, 666666667,  2,         0, -3, 666666667},
            {-1, 666666667,  3,         0, -4, 666666667},
            {-1, 666666667,  3, 333333333, -4, 333333334},

            {0, 0, -4, 666666667,  3, 333333333},
            {0, 0, -3,         0,  3,         0},
            {0, 0, -2,         0,  2,         0},
            {0, 0, -1,         0,  1,         0},
            {0, 0, -1, 333333334,  0, 666666666},
            {0, 0, -1, 666666667,  0, 333333333},
            {0, 0, -1, 999999999,  0,         1},
            {0, 0,  0,         0,  0,         0},
            {0, 0,  0,         1, -1, 999999999},
            {0, 0,  0, 333333333, -1, 666666667},
            {0, 0,  0, 666666666, -1, 333333334},
            {0, 0,  1,         0, -1,         0},
            {0, 0,  2,         0, -2,         0},
            {0, 0,  3,         0, -3,         0},
            {0, 0,  3, 333333333, -4, 666666667},

            {0, 333333333, -4, 666666667,  3, 666666666},
            {0, 333333333, -3,         0,  3, 333333333},
            {0, 333333333, -2,         0,  2, 333333333},
            {0, 333333333, -1,         0,  1, 333333333},
            {0, 333333333, -1, 333333334,  0, 999999999},
            {0, 333333333, -1, 666666667,  0, 666666666},
            {0, 333333333, -1, 999999999,  0, 333333334},
            {0, 333333333,  0,         0,  0, 333333333},
            {0, 333333333,  0,         1,  0, 333333332},
            {0, 333333333,  0, 333333333,  0,         0},
            {0, 333333333,  0, 666666666, -1, 666666667},
            {0, 333333333,  1,         0, -1, 333333333},
            {0, 333333333,  2,         0, -2, 333333333},
            {0, 333333333,  3,         0, -3, 333333333},
            {0, 333333333,  3, 333333333, -3,         0},

            {1, 0, -4, 666666667,  4, 333333333},
            {1, 0, -3,         0,  4,         0},
            {1, 0, -2,         0,  3,         0},
            {1, 0, -1,         0,  2,         0},
            {1, 0, -1, 333333334,  1, 666666666},
            {1, 0, -1, 666666667,  1, 333333333},
            {1, 0, -1, 999999999,  1,         1},
            {1, 0,  0,         0,  1,         0},
            {1, 0,  0,         1,  0, 999999999},
            {1, 0,  0, 333333333,  0, 666666667},
            {1, 0,  0, 666666666,  0, 333333334},
            {1, 0,  1,         0,  0,         0},
            {1, 0,  2,         0, -1,         0},
            {1, 0,  3,         0, -2,         0},
            {1, 0,  3, 333333333, -3, 666666667},

            {2, 0, -4, 666666667,  5, 333333333},
            {2, 0, -3,         0,  5,         0},
            {2, 0, -2,         0,  4,         0},
            {2, 0, -1,         0,  3,         0},
            {2, 0, -1, 333333334,  2, 666666666},
            {2, 0, -1, 666666667,  2, 333333333},
            {2, 0, -1, 999999999,  2,         1},
            {2, 0,  0,         0,  2,         0},
            {2, 0,  0,         1,  1, 999999999},
            {2, 0,  0, 333333333,  1, 666666667},
            {2, 0,  0, 666666666,  1, 333333334},
            {2, 0,  1,         0,  1,         0},
            {2, 0,  2,         0,  0,         0},
            {2, 0,  3,         0, -1,         0},
            {2, 0,  3, 333333333, -2, 666666667},

            {3, 0, -4, 666666667,  6, 333333333},
            {3, 0, -3,         0,  6,         0},
            {3, 0, -2,         0,  5,         0},
            {3, 0, -1,         0,  4,         0},
            {3, 0, -1, 333333334,  3, 666666666},
            {3, 0, -1, 666666667,  3, 333333333},
            {3, 0, -1, 999999999,  3,         1},
            {3, 0,  0,         0,  3,         0},
            {3, 0,  0,         1,  2, 999999999},
            {3, 0,  0, 333333333,  2, 666666667},
            {3, 0,  0, 666666666,  2, 333333334},
            {3, 0,  1,         0,  2,         0},
            {3, 0,  2,         0,  1,         0},
            {3, 0,  3,         0,  0,         0},
            {3, 0,  3, 333333333, -1, 666666667},

            {3, 333333333, -4, 666666667,  6, 666666666},
            {3, 333333333, -3,         0,  6, 333333333},
            {3, 333333333, -2,         0,  5, 333333333},
            {3, 333333333, -1,         0,  4, 333333333},
            {3, 333333333, -1, 333333334,  3, 999999999},
            {3, 333333333, -1, 666666667,  3, 666666666},
            {3, 333333333, -1, 999999999,  3, 333333334},
            {3, 333333333,  0,         0,  3, 333333333},
            {3, 333333333,  0,         1,  3, 333333332},
            {3, 333333333,  0, 333333333,  3,         0},
            {3, 333333333,  0, 666666666,  2, 666666667},
            {3, 333333333,  1,         0,  2, 333333333},
            {3, 333333333,  2,         0,  1, 333333333},
            {3, 333333333,  3,         0,  0, 333333333},
            {3, 333333333,  3, 333333333,  0,         0},

            {Long.MAX_VALUE, 0, Long.MAX_VALUE, 0, 0, 0},
        };
    }

    @Test(dataProvider = "Minus")
    public void test_minus(long seconds, int nanos, long minusSeconds, int minusNanos, long expectedSeconds, int expectedNanoOfSecond) {
        TaiInstant i = TaiInstant.ofTaiSeconds(seconds, nanos).minus(Duration.ofSeconds(minusSeconds, minusNanos));
        assertEquals(i.getTaiSeconds(), expectedSeconds);
        assertEquals(i.getNano(), expectedNanoOfSecond);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_overflowTooSmall() {
        TaiInstant i = TaiInstant.ofTaiSeconds(Long.MIN_VALUE, 0);
        i.minus(Duration.ofSeconds(0, 1));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_overflowTooBig() {
        TaiInstant i = TaiInstant.ofTaiSeconds(Long.MAX_VALUE, 999999999);
        i.minus(Duration.ofSeconds(-1, 999999999));
    }

    //-----------------------------------------------------------------------
    // durationUntil()
    //-----------------------------------------------------------------------
    @Test
    public void test_durationUntil_fifteenSeconds() {
        TaiInstant tai1 = TaiInstant.ofTaiSeconds(10, 0);
        TaiInstant tai2 = TaiInstant.ofTaiSeconds(25, 0);
        Duration test = tai1.durationUntil(tai2);
        assertEquals(test.getSeconds(), 15);
        assertEquals(test.getNano(), 0);
    }

    @Test
    public void test_durationUntil_twoNanos() {
        TaiInstant tai1 = TaiInstant.ofTaiSeconds(4, 5);
        TaiInstant tai2 = TaiInstant.ofTaiSeconds(4, 7);
        Duration test = tai1.durationUntil(tai2);
        assertEquals(test.getSeconds(), 0);
        assertEquals(test.getNano(), 2);
    }

    @Test
    public void test_durationUntil_twoNanosNegative() {
        TaiInstant tai1 = TaiInstant.ofTaiSeconds(4, 9);
        TaiInstant tai2 = TaiInstant.ofTaiSeconds(4, 7);
        Duration test = tai1.durationUntil(tai2);
        assertEquals(test.getSeconds(), -1);
        assertEquals(test.getNano(), 999999998);
    }

    //-----------------------------------------------------------------------
    // toUtcInstant()
    //-----------------------------------------------------------------------
    @Test
    public void test_toUtcInstant() {
        for (int i = -1000; i < 1000; i++) {
            for (int j = 0; j < 10; j++) {
                UtcInstant expected = UtcInstant.ofModifiedJulianDay(36204 + i, j * 1000000000L + 2L);
                TaiInstant test = TaiInstant.ofTaiSeconds(i * 24 * 60 * 60 + j + 10, 2);
                assertEquals(test.toUtcInstant(), expected);
            }
        }
    }

    //-----------------------------------------------------------------------
    // toInstant()
    //-----------------------------------------------------------------------
    @Test
    public void test_toInstant() {
        for (int i = -1000; i < 1000; i++) {
            for (int j = 0; j < 10; j++) {
                Instant expected = Instant.ofEpochSecond(-378691200L + i * 24 * 60 * 60 + j).plusNanos(2);
                TaiInstant test = TaiInstant.ofTaiSeconds(i * 24 * 60 * 60 + j + 10, 2);
                assertEquals(test.toInstant(), expected, "Loop " + i + " " + j);
            }
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_comparisons() {
        doTest_comparisons_TaiInstant(
            TaiInstant.ofTaiSeconds(-2L, 0),
            TaiInstant.ofTaiSeconds(-2L, 999999998),
            TaiInstant.ofTaiSeconds(-2L, 999999999),
            TaiInstant.ofTaiSeconds(-1L, 0),
            TaiInstant.ofTaiSeconds(-1L, 1),
            TaiInstant.ofTaiSeconds(-1L, 999999998),
            TaiInstant.ofTaiSeconds(-1L, 999999999),
            TaiInstant.ofTaiSeconds(0L, 0),
            TaiInstant.ofTaiSeconds(0L, 1),
            TaiInstant.ofTaiSeconds(0L, 2),
            TaiInstant.ofTaiSeconds(0L, 999999999),
            TaiInstant.ofTaiSeconds(1L, 0),
            TaiInstant.ofTaiSeconds(2L, 0)
        );
    }

    void doTest_comparisons_TaiInstant(TaiInstant... instants) {
        for (int i = 0; i < instants.length; i++) {
            TaiInstant a = instants[i];
            for (int j = 0; j < instants.length; j++) {
                TaiInstant b = instants[j];
                if (i < j) {
                    assertEquals(a.compareTo(b) < 0, true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertEquals(a.compareTo(b) > 0, true, a + " <=> " + b);
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
        TaiInstant a = TaiInstant.ofTaiSeconds(0L, 0);
        a.compareTo(null);
    }

    @Test(expectedExceptions = ClassCastException.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void test_compareToNonTaiInstant() {
        Comparable c = TaiInstant.ofTaiSeconds(0L, 2);
        c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals() {
        TaiInstant test5a = TaiInstant.ofTaiSeconds(5L, 20);
        TaiInstant test5b = TaiInstant.ofTaiSeconds(5L, 20);
        TaiInstant test5n = TaiInstant.ofTaiSeconds(5L, 30);
        TaiInstant test6 = TaiInstant.ofTaiSeconds(6L, 20);

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
        TaiInstant test5 = TaiInstant.ofTaiSeconds(5L, 20);
        assertEquals(test5.equals(null), false);
    }

    @Test
    public void test_equals_otherClass() {
        TaiInstant test5 = TaiInstant.ofTaiSeconds(5L, 20);
        assertEquals(test5.equals(""), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_hashCode() {
        TaiInstant test5a = TaiInstant.ofTaiSeconds(5L, 20);
        TaiInstant test5b = TaiInstant.ofTaiSeconds(5L, 20);
        TaiInstant test5n = TaiInstant.ofTaiSeconds(5L, 30);
        TaiInstant test6 = TaiInstant.ofTaiSeconds(6L, 20);

        assertEquals(test5a.hashCode() == test5a.hashCode(), true);
        assertEquals(test5a.hashCode() == test5b.hashCode(), true);
        assertEquals(test5b.hashCode() == test5b.hashCode(), true);

        assertEquals(test5a.hashCode() == test5n.hashCode(), false);
        assertEquals(test5a.hashCode() == test6.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString_standard() {
        TaiInstant t = TaiInstant.ofTaiSeconds(123L, 123456789);
        assertEquals(t.toString(), "123.123456789s(TAI)");
    }

    @Test
    public void test_toString_negative() {
        TaiInstant t = TaiInstant.ofTaiSeconds(-123L, 123456789);
        assertEquals(t.toString(), "-123.123456789s(TAI)");
    }

    @Test
    public void test_toString_zeroDecimal() {
        TaiInstant t = TaiInstant.ofTaiSeconds(0L, 567);
        assertEquals(t.toString(), "0.000000567s(TAI)");
    }

}
