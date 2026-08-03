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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

/**
 * Test MispInstant.
 */
public class TestMispInstant {

    @Test
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(MispInstant.class));
        assertTrue(Comparable.class.isAssignableFrom(MispInstant.class));
    }

    @Test
    public void test_serialization() throws Exception {
        MispInstant test = MispInstant.ofMispSeconds(2, 3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try ( ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try ( ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(test, ois.readObject());
        }
    }

    @Test
    public void factory_ofMispSecondslong_long() {
        for (long i = -2; i <= 2; i++) {
            for (int j = 0; j < 10; j++) {
                MispInstant t = MispInstant.ofMispSeconds(i, j);
                assertEquals(i, t.getMispSeconds());
                assertEquals(j, t.getNano());
            }
            for (int j = -10; j < 0; j++) {
                MispInstant t = MispInstant.ofMispSeconds(i, j);
                assertEquals(i - 1, t.getMispSeconds());
                assertEquals(j + 1000000000, t.getNano());
            }
            for (int j = 999999990; j < 1000000000; j++) {
                MispInstant t = MispInstant.ofMispSeconds(i, j);
                assertEquals(i, t.getMispSeconds());
                assertEquals(j, t.getNano());
            }
        }
    }

    @Test
    public void factory_ofMispSeconds_long_long_nanosNegativeAdjusted() {
        MispInstant test = MispInstant.ofMispSeconds(2L, -1);
        assertEquals(1, test.getMispSeconds());
        assertEquals(999999999, test.getNano());
    }

    @Test
    public void factory_ofMispSeconds_long_long_tooBig() {
        assertThrows(ArithmeticException.class, () -> MispInstant.ofMispSeconds(Long.MAX_VALUE, 1000000000));
    }

    @Test
    public void factory_of_Instant_null() {
        assertThrows(NullPointerException.class, () -> MispInstant.of((Instant) null));
    }

    @Test
    public void factory_of_UtcInstant_null() {
        assertThrows(NullPointerException.class, () -> MispInstant.of((UtcInstant) null));
    }

    @Test
    public void test_fromTaiInstant() {
        TaiInstant tai = TaiInstant.parse("378691208.000082000s(TAI)");
        MispInstant misp = MispInstant.of(tai);
        assertEquals(0, misp.getMispSeconds());
        assertEquals(0, misp.getNano());
    }

    @Test
    public void test_toTaiInstant() {
        MispInstant misp = MispInstant.ofMispSeconds(0, 0);
        TaiInstant tai = misp.toTaiInstant();
        assertEquals(378691208, tai.getTaiSeconds());
        assertEquals(82000, tai.getNano());
    }

    @Test
    public void test_fromInstant() {
        Instant instant = Instant.parse("2022-03-05T00:00:08.000082Z");
        MispInstant misp = MispInstant.of(instant);
        // 37 seconds for TAI to UTC offset, and 8.000082 for MISP to TAI offset
        // 59643L is the MJD for 2022-03-05
        // 40587L is the MJD for 1970-01-01
        assertEquals((59643L - 40587L) * 24 * 60 * 60 + 37, misp.getMispSeconds());
        assertEquals(0, misp.getNano());
    }

    @Test
    public void test_fromUtcInstant() {
        UtcInstant utcInstant = UtcInstant.parse("2022-03-05T00:00:08.000082Z");
        MispInstant misp = MispInstant.of(utcInstant);
        // 37 seconds for TAI to UTC offset, and 8.000082 for MISP to TAI offset
        // 59643L is the MJD for 2022-03-05
        // 40587L is the MJD for 1970-01-01
        assertEquals((59643L - 40587L) * 24 * 60 * 60 + 37, misp.getMispSeconds());
        assertEquals(0, misp.getNano());
    }

    @Test
    public void test_toUtcInstant() {
        MispInstant misp1 = MispInstant.ofMispSeconds((59643L - 40587L) * 24 * 60 * 60, 0);
        MispInstant misp2 = misp1.minus(Duration.ofSeconds(8, 82000));
        MispInstant misp3 = misp2.plus(Duration.ofSeconds(37, 0));
        UtcInstant utc = misp3.toUtcInstant();
        assertEquals("2022-03-05T00:00:00Z", utc.toString());
    }

    @Test
    public void test_toInstant() {
        MispInstant misp1 = MispInstant.ofMispSeconds((59643L - 40587L) * 24 * 60 * 60, 0);
        MispInstant misp2 = misp1.minus(Duration.ofSeconds(8, 82000));
        MispInstant misp3 = misp2.plus(Duration.ofSeconds(37, 0));
        Instant instant = misp3.toInstant();
        assertEquals("2022-03-05T00:00:00Z", instant.toString());
    }

    @DataProvider
    public static Object[][] data_withMispSeconds() {
        return new Object[][]{
            {0L, 12345L, 1L, 1L, 12345L},
            {0L, 12345L, -1L, -1L, 12345L},
            {7L, 12345L, 2L, 2L, 12345L},
            {7L, 12345L, -2L, -2L, 12345L},
            {-99L, 12345L, 3L, 3L, 12345L},
            {-99L, 12345L, -3L, -3L, 12345L},};
    }

    @ParameterizedTest
    @UseDataProvider("data_withMispSeconds")
    public void test_ofMispSeconds(long secs, long nanos, long newMisp, Long expectedSeconds, Long expectedNanos) {
        MispInstant i = MispInstant.ofMispSeconds(secs, nanos).withMispSeconds(newMisp);
        assertEquals(expectedSeconds.longValue(), i.getMispSeconds());
        assertEquals(expectedNanos.longValue(), i.getNano());
    }

    @DataProvider
    public static Object[][] data_withNano() {
        return new Object[][]{
            {0L, 12345L, 1, 0L, 1L},
            {7L, 12345L, 2, 7L, 2L},
            {-99L, 12345L, 3, -99L, 3L},
            {-99L, 12345L, 999999999, -99L, 999999999L},
            {-99L, 12345L, -1, null, null},
            {-99L, 12345L, 1000000000, null, null},};
    }

    @ParameterizedTest
    @UseDataProvider("data_withNano")
    public void test_withNano(long secs, long nanos, int newNano, Long expectedSeconds, Long expectedNanos) {
        MispInstant i = MispInstant.ofMispSeconds(secs, nanos);
        if (expectedSeconds != null) {
            MispInstant withNano = i.withNano(newNano);
            assertEquals(expectedSeconds.longValue(), withNano.getMispSeconds());
            assertEquals(expectedNanos.longValue(), withNano.getNano());
        } else {
            assertThrows(IllegalArgumentException.class, () -> i.withNano(newNano));
        }
    }

    @DataProvider
    public static Object[][] data_plus() {
        return new Object[][]{
            {Long.MIN_VALUE, 0, Long.MAX_VALUE, 0, -1, 0},
            {-4, 666666667, -4, 666666667, -7, 333333334},
            {-4, 666666667, -3, 0, -7, 666666667},
            {-4, 666666667, -2, 0, -6, 666666667},
            {-4, 666666667, -1, 0, -5, 666666667},
            {-4, 666666667, -1, 333333334, -4, 1},
            {-4, 666666667, -1, 666666667, -4, 333333334},
            {-4, 666666667, -1, 999999999, -4, 666666666},
            {-4, 666666667, 0, 0, -4, 666666667},
            {-4, 666666667, 0, 1, -4, 666666668},
            {-4, 666666667, 0, 333333333, -3, 0},
            {-4, 666666667, 0, 666666666, -3, 333333333},
            {-4, 666666667, 1, 0, -3, 666666667},
            {-4, 666666667, 2, 0, -2, 666666667},
            {-4, 666666667, 3, 0, -1, 666666667},
            {-4, 666666667, 3, 333333333, 0, 0},
            {-3, 0, -4, 666666667, -7, 666666667},
            {-3, 0, -3, 0, -6, 0},
            {-3, 0, -2, 0, -5, 0},
            {-3, 0, -1, 0, -4, 0},
            {-3, 0, -1, 333333334, -4, 333333334},
            {-3, 0, -1, 666666667, -4, 666666667},
            {-3, 0, -1, 999999999, -4, 999999999},
            {-3, 0, 0, 0, -3, 0},
            {-3, 0, 0, 1, -3, 1},
            {-3, 0, 0, 333333333, -3, 333333333},
            {-3, 0, 0, 666666666, -3, 666666666},
            {-3, 0, 1, 0, -2, 0},
            {-3, 0, 2, 0, -1, 0},
            {-3, 0, 3, 0, 0, 0},
            {-3, 0, 3, 333333333, 0, 333333333},
            {-2, 0, -4, 666666667, -6, 666666667},
            {-2, 0, -3, 0, -5, 0},
            {-2, 0, -2, 0, -4, 0},
            {-2, 0, -1, 0, -3, 0},
            {-2, 0, -1, 333333334, -3, 333333334},
            {-2, 0, -1, 666666667, -3, 666666667},
            {-2, 0, -1, 999999999, -3, 999999999},
            {-2, 0, 0, 0, -2, 0},
            {-2, 0, 0, 1, -2, 1},
            {-2, 0, 0, 333333333, -2, 333333333},
            {-2, 0, 0, 666666666, -2, 666666666},
            {-2, 0, 1, 0, -1, 0},
            {-2, 0, 2, 0, 0, 0},
            {-2, 0, 3, 0, 1, 0},
            {-2, 0, 3, 333333333, 1, 333333333},
            {-1, 0, -4, 666666667, -5, 666666667},
            {-1, 0, -3, 0, -4, 0},
            {-1, 0, -2, 0, -3, 0},
            {-1, 0, -1, 0, -2, 0},
            {-1, 0, -1, 333333334, -2, 333333334},
            {-1, 0, -1, 666666667, -2, 666666667},
            {-1, 0, -1, 999999999, -2, 999999999},
            {-1, 0, 0, 0, -1, 0},
            {-1, 0, 0, 1, -1, 1},
            {-1, 0, 0, 333333333, -1, 333333333},
            {-1, 0, 0, 666666666, -1, 666666666},
            {-1, 0, 1, 0, 0, 0},
            {-1, 0, 2, 0, 1, 0},
            {-1, 0, 3, 0, 2, 0},
            {-1, 0, 3, 333333333, 2, 333333333},
            {-1, 666666667, -4, 666666667, -4, 333333334},
            {-1, 666666667, -3, 0, -4, 666666667},
            {-1, 666666667, -2, 0, -3, 666666667},
            {-1, 666666667, -1, 0, -2, 666666667},
            {-1, 666666667, -1, 333333334, -1, 1},
            {-1, 666666667, -1, 666666667, -1, 333333334},
            {-1, 666666667, -1, 999999999, -1, 666666666},
            {-1, 666666667, 0, 0, -1, 666666667},
            {-1, 666666667, 0, 1, -1, 666666668},
            {-1, 666666667, 0, 333333333, 0, 0},
            {-1, 666666667, 0, 666666666, 0, 333333333},
            {-1, 666666667, 1, 0, 0, 666666667},
            {-1, 666666667, 2, 0, 1, 666666667},
            {-1, 666666667, 3, 0, 2, 666666667},
            {-1, 666666667, 3, 333333333, 3, 0},
            {0, 0, -4, 666666667, -4, 666666667},
            {0, 0, -3, 0, -3, 0},
            {0, 0, -2, 0, -2, 0},
            {0, 0, -1, 0, -1, 0},
            {0, 0, -1, 333333334, -1, 333333334},
            {0, 0, -1, 666666667, -1, 666666667},
            {0, 0, -1, 999999999, -1, 999999999},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 1},
            {0, 0, 0, 333333333, 0, 333333333},
            {0, 0, 0, 666666666, 0, 666666666},
            {0, 0, 1, 0, 1, 0},
            {0, 0, 2, 0, 2, 0},
            {0, 0, 3, 0, 3, 0},
            {0, 0, 3, 333333333, 3, 333333333},
            {0, 333333333, -4, 666666667, -3, 0},
            {0, 333333333, -3, 0, -3, 333333333},
            {0, 333333333, -2, 0, -2, 333333333},
            {0, 333333333, -1, 0, -1, 333333333},
            {0, 333333333, -1, 333333334, -1, 666666667},
            {0, 333333333, -1, 666666667, 0, 0},
            {0, 333333333, -1, 999999999, 0, 333333332},
            {0, 333333333, 0, 0, 0, 333333333},
            {0, 333333333, 0, 1, 0, 333333334},
            {0, 333333333, 0, 333333333, 0, 666666666},
            {0, 333333333, 0, 666666666, 0, 999999999},
            {0, 333333333, 1, 0, 1, 333333333},
            {0, 333333333, 2, 0, 2, 333333333},
            {0, 333333333, 3, 0, 3, 333333333},
            {0, 333333333, 3, 333333333, 3, 666666666},
            {1, 0, -4, 666666667, -3, 666666667},
            {1, 0, -3, 0, -2, 0},
            {1, 0, -2, 0, -1, 0},
            {1, 0, -1, 0, 0, 0},
            {1, 0, -1, 333333334, 0, 333333334},
            {1, 0, -1, 666666667, 0, 666666667},
            {1, 0, -1, 999999999, 0, 999999999},
            {1, 0, 0, 0, 1, 0},
            {1, 0, 0, 1, 1, 1},
            {1, 0, 0, 333333333, 1, 333333333},
            {1, 0, 0, 666666666, 1, 666666666},
            {1, 0, 1, 0, 2, 0},
            {1, 0, 2, 0, 3, 0},
            {1, 0, 3, 0, 4, 0},
            {1, 0, 3, 333333333, 4, 333333333},
            {2, 0, -4, 666666667, -2, 666666667},
            {2, 0, -3, 0, -1, 0},
            {2, 0, -2, 0, 0, 0},
            {2, 0, -1, 0, 1, 0},
            {2, 0, -1, 333333334, 1, 333333334},
            {2, 0, -1, 666666667, 1, 666666667},
            {2, 0, -1, 999999999, 1, 999999999},
            {2, 0, 0, 0, 2, 0},
            {2, 0, 0, 1, 2, 1},
            {2, 0, 0, 333333333, 2, 333333333},
            {2, 0, 0, 666666666, 2, 666666666},
            {2, 0, 1, 0, 3, 0},
            {2, 0, 2, 0, 4, 0},
            {2, 0, 3, 0, 5, 0},
            {2, 0, 3, 333333333, 5, 333333333},
            {3, 0, -4, 666666667, -1, 666666667},
            {3, 0, -3, 0, 0, 0},
            {3, 0, -2, 0, 1, 0},
            {3, 0, -1, 0, 2, 0},
            {3, 0, -1, 333333334, 2, 333333334},
            {3, 0, -1, 666666667, 2, 666666667},
            {3, 0, -1, 999999999, 2, 999999999},
            {3, 0, 0, 0, 3, 0},
            {3, 0, 0, 1, 3, 1},
            {3, 0, 0, 333333333, 3, 333333333},
            {3, 0, 0, 666666666, 3, 666666666},
            {3, 0, 1, 0, 4, 0},
            {3, 0, 2, 0, 5, 0},
            {3, 0, 3, 0, 6, 0},
            {3, 0, 3, 333333333, 6, 333333333},
            {3, 333333333, -4, 666666667, 0, 0},
            {3, 333333333, -3, 0, 0, 333333333},
            {3, 333333333, -2, 0, 1, 333333333},
            {3, 333333333, -1, 0, 2, 333333333},
            {3, 333333333, -1, 333333334, 2, 666666667},
            {3, 333333333, -1, 666666667, 3, 0},
            {3, 333333333, -1, 999999999, 3, 333333332},
            {3, 333333333, 0, 0, 3, 333333333},
            {3, 333333333, 0, 1, 3, 333333334},
            {3, 333333333, 0, 333333333, 3, 666666666},
            {3, 333333333, 0, 666666666, 3, 999999999},
            {3, 333333333, 1, 0, 4, 333333333},
            {3, 333333333, 2, 0, 5, 333333333},
            {3, 333333333, 3, 0, 6, 333333333},
            {3, 333333333, 3, 333333333, 6, 666666666},
            {Long.MAX_VALUE, 0, Long.MIN_VALUE, 0, -1, 0},};
    }

    @ParameterizedTest
    @UseDataProvider("data_plus")
    public void test_plus(long seconds, int nanos, long plusSeconds, int plusNanos, long expectedSeconds, int expectedNanoOfSecond) {
        MispInstant i = MispInstant.ofMispSeconds(seconds, nanos).plus(Duration.ofSeconds(plusSeconds, plusNanos));
        assertEquals(expectedSeconds, i.getMispSeconds());
        assertEquals(expectedNanoOfSecond, i.getNano());
    }

    @Test
    public void test_plus_overflowTooBig() {
        MispInstant i = MispInstant.ofMispSeconds(Long.MAX_VALUE, 999999999);
        assertThrows(ArithmeticException.class, () -> i.plus(Duration.ofSeconds(0, 1)));
    }

    @Test
    public void test_plus_overflowTooSmall() {
        MispInstant i = MispInstant.ofMispSeconds(Long.MIN_VALUE, 0);
        assertThrows(ArithmeticException.class, () -> i.plus(Duration.ofSeconds(-1, 999999999)));
    }

    @DataProvider
    public static Object[][] data_minus() {
        return new Object[][]{
            {Long.MIN_VALUE, 0, Long.MIN_VALUE + 1, 0, -1, 0},
            {-4, 666666667, -4, 666666667, 0, 0},
            {-4, 666666667, -3, 0, -1, 666666667},
            {-4, 666666667, -2, 0, -2, 666666667},
            {-4, 666666667, -1, 0, -3, 666666667},
            {-4, 666666667, -1, 333333334, -3, 333333333},
            {-4, 666666667, -1, 666666667, -3, 0},
            {-4, 666666667, -1, 999999999, -4, 666666668},
            {-4, 666666667, 0, 0, -4, 666666667},
            {-4, 666666667, 0, 1, -4, 666666666},
            {-4, 666666667, 0, 333333333, -4, 333333334},
            {-4, 666666667, 0, 666666666, -4, 1},
            {-4, 666666667, 1, 0, -5, 666666667},
            {-4, 666666667, 2, 0, -6, 666666667},
            {-4, 666666667, 3, 0, -7, 666666667},
            {-4, 666666667, 3, 333333333, -7, 333333334},
            {-3, 0, -4, 666666667, 0, 333333333},
            {-3, 0, -3, 0, 0, 0},
            {-3, 0, -2, 0, -1, 0},
            {-3, 0, -1, 0, -2, 0},
            {-3, 0, -1, 333333334, -3, 666666666},
            {-3, 0, -1, 666666667, -3, 333333333},
            {-3, 0, -1, 999999999, -3, 1},
            {-3, 0, 0, 0, -3, 0},
            {-3, 0, 0, 1, -4, 999999999},
            {-3, 0, 0, 333333333, -4, 666666667},
            {-3, 0, 0, 666666666, -4, 333333334},
            {-3, 0, 1, 0, -4, 0},
            {-3, 0, 2, 0, -5, 0},
            {-3, 0, 3, 0, -6, 0},
            {-3, 0, 3, 333333333, -7, 666666667},
            {-2, 0, -4, 666666667, 1, 333333333},
            {-2, 0, -3, 0, 1, 0},
            {-2, 0, -2, 0, 0, 0},
            {-2, 0, -1, 0, -1, 0},
            {-2, 0, -1, 333333334, -2, 666666666},
            {-2, 0, -1, 666666667, -2, 333333333},
            {-2, 0, -1, 999999999, -2, 1},
            {-2, 0, 0, 0, -2, 0},
            {-2, 0, 0, 1, -3, 999999999},
            {-2, 0, 0, 333333333, -3, 666666667},
            {-2, 0, 0, 666666666, -3, 333333334},
            {-2, 0, 1, 0, -3, 0},
            {-2, 0, 2, 0, -4, 0},
            {-2, 0, 3, 0, -5, 0},
            {-2, 0, 3, 333333333, -6, 666666667},
            {-1, 0, -4, 666666667, 2, 333333333},
            {-1, 0, -3, 0, 2, 0},
            {-1, 0, -2, 0, 1, 0},
            {-1, 0, -1, 0, 0, 0},
            {-1, 0, -1, 333333334, -1, 666666666},
            {-1, 0, -1, 666666667, -1, 333333333},
            {-1, 0, -1, 999999999, -1, 1},
            {-1, 0, 0, 0, -1, 0},
            {-1, 0, 0, 1, -2, 999999999},
            {-1, 0, 0, 333333333, -2, 666666667},
            {-1, 0, 0, 666666666, -2, 333333334},
            {-1, 0, 1, 0, -2, 0},
            {-1, 0, 2, 0, -3, 0},
            {-1, 0, 3, 0, -4, 0},
            {-1, 0, 3, 333333333, -5, 666666667},
            {-1, 666666667, -4, 666666667, 3, 0},
            {-1, 666666667, -3, 0, 2, 666666667},
            {-1, 666666667, -2, 0, 1, 666666667},
            {-1, 666666667, -1, 0, 0, 666666667},
            {-1, 666666667, -1, 333333334, 0, 333333333},
            {-1, 666666667, -1, 666666667, 0, 0},
            {-1, 666666667, -1, 999999999, -1, 666666668},
            {-1, 666666667, 0, 0, -1, 666666667},
            {-1, 666666667, 0, 1, -1, 666666666},
            {-1, 666666667, 0, 333333333, -1, 333333334},
            {-1, 666666667, 0, 666666666, -1, 1},
            {-1, 666666667, 1, 0, -2, 666666667},
            {-1, 666666667, 2, 0, -3, 666666667},
            {-1, 666666667, 3, 0, -4, 666666667},
            {-1, 666666667, 3, 333333333, -4, 333333334},
            {0, 0, -4, 666666667, 3, 333333333},
            {0, 0, -3, 0, 3, 0},
            {0, 0, -2, 0, 2, 0},
            {0, 0, -1, 0, 1, 0},
            {0, 0, -1, 333333334, 0, 666666666},
            {0, 0, -1, 666666667, 0, 333333333},
            {0, 0, -1, 999999999, 0, 1},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, -1, 999999999},
            {0, 0, 0, 333333333, -1, 666666667},
            {0, 0, 0, 666666666, -1, 333333334},
            {0, 0, 1, 0, -1, 0},
            {0, 0, 2, 0, -2, 0},
            {0, 0, 3, 0, -3, 0},
            {0, 0, 3, 333333333, -4, 666666667},
            {0, 333333333, -4, 666666667, 3, 666666666},
            {0, 333333333, -3, 0, 3, 333333333},
            {0, 333333333, -2, 0, 2, 333333333},
            {0, 333333333, -1, 0, 1, 333333333},
            {0, 333333333, -1, 333333334, 0, 999999999},
            {0, 333333333, -1, 666666667, 0, 666666666},
            {0, 333333333, -1, 999999999, 0, 333333334},
            {0, 333333333, 0, 0, 0, 333333333},
            {0, 333333333, 0, 1, 0, 333333332},
            {0, 333333333, 0, 333333333, 0, 0},
            {0, 333333333, 0, 666666666, -1, 666666667},
            {0, 333333333, 1, 0, -1, 333333333},
            {0, 333333333, 2, 0, -2, 333333333},
            {0, 333333333, 3, 0, -3, 333333333},
            {0, 333333333, 3, 333333333, -3, 0},
            {1, 0, -4, 666666667, 4, 333333333},
            {1, 0, -3, 0, 4, 0},
            {1, 0, -2, 0, 3, 0},
            {1, 0, -1, 0, 2, 0},
            {1, 0, -1, 333333334, 1, 666666666},
            {1, 0, -1, 666666667, 1, 333333333},
            {1, 0, -1, 999999999, 1, 1},
            {1, 0, 0, 0, 1, 0},
            {1, 0, 0, 1, 0, 999999999},
            {1, 0, 0, 333333333, 0, 666666667},
            {1, 0, 0, 666666666, 0, 333333334},
            {1, 0, 1, 0, 0, 0},
            {1, 0, 2, 0, -1, 0},
            {1, 0, 3, 0, -2, 0},
            {1, 0, 3, 333333333, -3, 666666667},
            {2, 0, -4, 666666667, 5, 333333333},
            {2, 0, -3, 0, 5, 0},
            {2, 0, -2, 0, 4, 0},
            {2, 0, -1, 0, 3, 0},
            {2, 0, -1, 333333334, 2, 666666666},
            {2, 0, -1, 666666667, 2, 333333333},
            {2, 0, -1, 999999999, 2, 1},
            {2, 0, 0, 0, 2, 0},
            {2, 0, 0, 1, 1, 999999999},
            {2, 0, 0, 333333333, 1, 666666667},
            {2, 0, 0, 666666666, 1, 333333334},
            {2, 0, 1, 0, 1, 0},
            {2, 0, 2, 0, 0, 0},
            {2, 0, 3, 0, -1, 0},
            {2, 0, 3, 333333333, -2, 666666667},
            {3, 0, -4, 666666667, 6, 333333333},
            {3, 0, -3, 0, 6, 0},
            {3, 0, -2, 0, 5, 0},
            {3, 0, -1, 0, 4, 0},
            {3, 0, -1, 333333334, 3, 666666666},
            {3, 0, -1, 666666667, 3, 333333333},
            {3, 0, -1, 999999999, 3, 1},
            {3, 0, 0, 0, 3, 0},
            {3, 0, 0, 1, 2, 999999999},
            {3, 0, 0, 333333333, 2, 666666667},
            {3, 0, 0, 666666666, 2, 333333334},
            {3, 0, 1, 0, 2, 0},
            {3, 0, 2, 0, 1, 0},
            {3, 0, 3, 0, 0, 0},
            {3, 0, 3, 333333333, -1, 666666667},
            {3, 333333333, -4, 666666667, 6, 666666666},
            {3, 333333333, -3, 0, 6, 333333333},
            {3, 333333333, -2, 0, 5, 333333333},
            {3, 333333333, -1, 0, 4, 333333333},
            {3, 333333333, -1, 333333334, 3, 999999999},
            {3, 333333333, -1, 666666667, 3, 666666666},
            {3, 333333333, -1, 999999999, 3, 333333334},
            {3, 333333333, 0, 0, 3, 333333333},
            {3, 333333333, 0, 1, 3, 333333332},
            {3, 333333333, 0, 333333333, 3, 0},
            {3, 333333333, 0, 666666666, 2, 666666667},
            {3, 333333333, 1, 0, 2, 333333333},
            {3, 333333333, 2, 0, 1, 333333333},
            {3, 333333333, 3, 0, 0, 333333333},
            {3, 333333333, 3, 333333333, 0, 0},
            {Long.MAX_VALUE, 0, Long.MAX_VALUE, 0, 0, 0},};
    }

    @ParameterizedTest
    @UseDataProvider("data_minus")
    public void test_minus(long seconds, int nanos, long minusSeconds, int minusNanos, long expectedSeconds, int expectedNanoOfSecond) {
        MispInstant i = MispInstant.ofMispSeconds(seconds, nanos).minus(Duration.ofSeconds(minusSeconds, minusNanos));
        assertEquals(expectedSeconds, i.getMispSeconds());
        assertEquals(expectedNanoOfSecond, i.getNano());
    }

    @Test
    public void test_minus_overflowTooSmall() {
        MispInstant i = MispInstant.ofMispSeconds(Long.MIN_VALUE, 0);
        assertThrows(ArithmeticException.class, () -> i.minus(Duration.ofSeconds(0, 1)));
    }

    @Test
    public void test_minus_overflowTooBig() {
        MispInstant i = MispInstant.ofMispSeconds(Long.MAX_VALUE, 999999999);
        assertThrows(ArithmeticException.class, () -> i.minus(Duration.ofSeconds(-1, 999999999)));
    }

    @Test
    public void test_durationUntil_fifteenSeconds() {
        MispInstant misp1 = MispInstant.ofMispSeconds(10, 0);
        MispInstant misp2 = MispInstant.ofMispSeconds(25, 0);
        Duration test = misp1.durationUntil(misp2);
        assertEquals(15, test.getSeconds());
        assertEquals(0, test.getNano());
    }

    @Test
    public void test_durationUntil_twoNanos() {
        MispInstant misp1 = MispInstant.ofMispSeconds(4, 5);
        MispInstant misp2 = MispInstant.ofMispSeconds(4, 7);
        Duration test = misp1.durationUntil(misp2);
        assertEquals(0, test.getSeconds());
        assertEquals(2, test.getNano());
    }

    @Test
    public void test_durationUntil_twoNanosNegative() {
        MispInstant misp1 = MispInstant.ofMispSeconds(4, 9);
        MispInstant misp2 = MispInstant.ofMispSeconds(4, 7);
        Duration test = misp1.durationUntil(misp2);
        assertEquals(-1, test.getSeconds());
        assertEquals(999999998, test.getNano());
    }

    @Test
    public void test_comparisons() {
        doTest_comparisons_MispInstant(
                MispInstant.ofMispSeconds(-2L, 0),
                MispInstant.ofMispSeconds(-2L, 999999998),
                MispInstant.ofMispSeconds(-2L, 999999999),
                MispInstant.ofMispSeconds(-1L, 0),
                MispInstant.ofMispSeconds(-1L, 1),
                MispInstant.ofMispSeconds(-1L, 999999998),
                MispInstant.ofMispSeconds(-1L, 999999999),
                MispInstant.ofMispSeconds(0L, 0),
                MispInstant.ofMispSeconds(0L, 1),
                MispInstant.ofMispSeconds(0L, 2),
                MispInstant.ofMispSeconds(0L, 999999999),
                MispInstant.ofMispSeconds(1L, 0),
                MispInstant.ofMispSeconds(2L, 0)
        );
    }

    void doTest_comparisons_MispInstant(MispInstant... instants) {
        for (int i = 0; i < instants.length; i++) {
            MispInstant a = instants[i];
            for (int j = 0; j < instants.length; j++) {
                MispInstant b = instants[j];
                if (i < j) {
                    assertTrue(a.compareTo(b) < 0);
                    assertFalse(a.equals(b));
                    assertTrue(a.isBefore(b));
                    assertFalse(a.isAfter(b));
                } else if (i > j) {
                    assertTrue(a.compareTo(b) > 0);
                    assertFalse(a.equals(b));
                    assertFalse(a.isBefore(b));
                    assertTrue(a.isAfter(b));
                } else {
                    assertEquals(0, a.compareTo(b));
                    assertTrue(a.equals(b));
                    assertFalse(a.isBefore(b));
                    assertFalse(a.isAfter(b));
                }
            }
        }
    }

    @Test
    public void test_compareTo_ObjectNull() {
        MispInstant a = MispInstant.ofMispSeconds(0L, 0);
        assertThrows(NullPointerException.class, () -> a.compareTo(null));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void test_compareToNonMispInstant() {
        Comparable c = MispInstant.ofMispSeconds(0L, 2);
        assertThrows(ClassCastException.class, () -> c.compareTo(new Object()));
    }

    @Test
    public void test_equals() {
        MispInstant test5a = MispInstant.ofMispSeconds(5L, 20);
        MispInstant test5b = MispInstant.ofMispSeconds(5L, 20);
        MispInstant test5n = MispInstant.ofMispSeconds(5L, 30);
        MispInstant test6 = MispInstant.ofMispSeconds(6L, 20);

        assertEquals(true, test5a.equals(test5a));
        assertEquals(true, test5a.equals(test5b));
        assertEquals(false, test5a.equals(test5n));
        assertEquals(false, test5a.equals(test6));

        assertEquals(true, test5b.equals(test5a));
        assertEquals(true, test5b.equals(test5b));
        assertEquals(false, test5b.equals(test5n));
        assertEquals(false, test5b.equals(test6));

        assertEquals(false, test5n.equals(test5a));
        assertEquals(false, test5n.equals(test5b));
        assertEquals(true, test5n.equals(test5n));
        assertEquals(false, test5n.equals(test6));

        assertEquals(false, test6.equals(test5a));
        assertEquals(false, test6.equals(test5b));
        assertEquals(false, test6.equals(test5n));
        assertEquals(true, test6.equals(test6));
    }

    @Test
    public void test_equals_null() {
        MispInstant test5 = MispInstant.ofMispSeconds(5L, 20);
        assertEquals(false, test5.equals(null));
    }

    @Test
    public void test_equals_otherClass() {
        MispInstant test5 = MispInstant.ofMispSeconds(5L, 20);
        assertEquals(false, test5.equals((Object) ""));
    }

    @Test
    public void test_hashCode() {
        MispInstant test5a = MispInstant.ofMispSeconds(5L, 20);
        MispInstant test5b = MispInstant.ofMispSeconds(5L, 20);
        MispInstant test5n = MispInstant.ofMispSeconds(5L, 30);
        MispInstant test6 = MispInstant.ofMispSeconds(6L, 20);

        assertEquals(true, test5a.hashCode() == test5a.hashCode());
        assertEquals(true, test5a.hashCode() == test5b.hashCode());
        assertEquals(true, test5b.hashCode() == test5b.hashCode());

        assertEquals(false, test5a.hashCode() == test5n.hashCode());
        assertEquals(false, test5a.hashCode() == test6.hashCode());
    }

}
