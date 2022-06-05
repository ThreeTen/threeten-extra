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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.JulianFields;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test SystemLeapSecondRules.
 */
public class TestUtcRules {

    SystemUtcRules rules;

    @BeforeEach
    public void setUp() throws Exception {
        Constructor<SystemUtcRules> con = SystemUtcRules.class.getDeclaredConstructor();
        con.setAccessible(true);
        rules = con.newInstance();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(Duration.class));
    }

    //-----------------------------------------------------------------------
    // serialize
    //-----------------------------------------------------------------------
    @Test
    public void test_serialization() throws Exception {
        SystemUtcRules test = SystemUtcRules.INSTANCE;  // use real rules, not our hacked copy
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertSame(test, ois.readObject());
        }
    }

    //-----------------------------------------------------------------------
    // getName()
    //-----------------------------------------------------------------------
    @Test
    public void test_getName() {
        assertEquals("System", rules.getName());
    }

    //-----------------------------------------------------------------------
    // getLeapSecond()
    //-----------------------------------------------------------------------
    public static Object[][] data_leapSeconds() {
        return new Object[][] {
            {-1, 0, 10, "1858-11-16"},
            {0, 0, 10, "1858-11-17"},
            {1, 0, 10, "1858-11-18"},

            {41316, 0, 10, "1971-12-31"},
            {41317, 0, 10, "1972-01-01"},
            {41318, 0, 10, "1972-01-02"},

            {41497, 0, 10, "1972-06-29"},
            {41498, 1, 10, "1972-06-30"},
            {41499, 0, 11, "1972-07-01"},
            {41500, 0, 11, "1972-07-02"},

            {41681, 0, 11, "1972-12-30"},
            {41682, 1, 11, "1972-12-31"},
            {41683, 0, 12, "1973-01-01"},
            {41684, 0, 12, "1973-01-02"},

            {42046, 0, 12, "1973-12-30"},
            {42047, 1, 12, "1973-12-31"},
            {42048, 0, 13, "1974-01-01"},
            {42049, 0, 13, "1974-01-02"},

            {42411, 0, 13, "1974-12-30"},
            {42412, 1, 13, "1974-12-31"},
            {42413, 0, 14, "1975-01-01"},
            {42414, 0, 14, "1975-01-02"},

            {42776, 0, 14, "1975-12-30"},
            {42777, 1, 14, "1975-12-31"},
            {42778, 0, 15, "1976-01-01"},
            {42779, 0, 15, "1976-01-02"},

            {43142, 0, 15, "1976-12-30"},
            {43143, 1, 15, "1976-12-31"},
            {43144, 0, 16, "1977-01-01"},
            {43145, 0, 16, "1977-01-02"},

            {43507, 0, 16, "1977-12-30"},
            {43508, 1, 16, "1977-12-31"},
            {43509, 0, 17, "1978-01-01"},
            {43510, 0, 17, "1978-01-02"},

            {43872, 0, 17, "1978-12-30"},
            {43873, 1, 17, "1978-12-31"},
            {43874, 0, 18, "1979-01-01"},
            {43875, 0, 18, "1979-01-02"},

            {44237, 0, 18, "1979-12-30"},
            {44238, 1, 18, "1979-12-31"},
            {44239, 0, 19, "1980-01-01"},
            {44240, 0, 19, "1980-01-02"},

            {44784, 0, 19, "1981-06-29"},
            {44785, 1, 19, "1981-06-30"},
            {44786, 0, 20, "1981-07-01"},
            {44787, 0, 20, "1981-07-02"},

            {45149, 0, 20, "1982-06-29"},
            {45150, 1, 20, "1982-06-30"},
            {45151, 0, 21, "1982-07-01"},
            {45152, 0, 21, "1982-07-02"},

            {45514, 0, 21, "1983-06-29"},
            {45515, 1, 21, "1983-06-30"},
            {45516, 0, 22, "1983-07-01"},
            {45517, 0, 22, "1983-07-02"},

            {46245, 0, 22, "1985-06-29"},
            {46246, 1, 22, "1985-06-30"},
            {46247, 0, 23, "1985-07-01"},
            {46248, 0, 23, "1985-07-02"},

            {47159, 0, 23, "1987-12-30"},
            {47160, 1, 23, "1987-12-31"},
            {47161, 0, 24, "1988-01-01"},
            {47162, 0, 24, "1988-01-02"},

            {47890, 0, 24, "1989-12-30"},
            {47891, 1, 24, "1989-12-31"},
            {47892, 0, 25, "1990-01-01"},
            {47893, 0, 25, "1990-01-02"},

            {48255, 0, 25, "1990-12-30"},
            {48256, 1, 25, "1990-12-31"},
            {48257, 0, 26, "1991-01-01"},
            {48258, 0, 26, "1991-01-02"},

            {48802, 0, 26, "1992-06-29"},
            {48803, 1, 26, "1992-06-30"},
            {48804, 0, 27, "1992-07-01"},
            {48805, 0, 27, "1992-07-02"},

            {49167, 0, 27, "1993-06-29"},
            {49168, 1, 27, "1993-06-30"},
            {49169, 0, 28, "1993-07-01"},
            {49170, 0, 28, "1993-07-02"},

            {49532, 0, 28, "1994-06-29"},
            {49533, 1, 28, "1994-06-30"},
            {49534, 0, 29, "1994-07-01"},
            {49535, 0, 29, "1994-07-02"},

            {50081, 0, 29, "1995-12-30"},
            {50082, 1, 29, "1995-12-31"},
            {50083, 0, 30, "1996-01-01"},
            {50084, 0, 30, "1996-01-02"},

            {50628, 0, 30, "1997-06-29"},
            {50629, 1, 30, "1997-06-30"},
            {50630, 0, 31, "1997-07-01"},
            {50631, 0, 31, "1997-07-02"},

            {51177, 0, 31, "1998-12-30"},
            {51178, 1, 31, "1998-12-31"},
            {51179, 0, 32, "1999-01-01"},
            {51180, 0, 32, "1999-01-02"},

            {53734, 0, 32, "2005-12-30"},
            {53735, 1, 32, "2005-12-31"},
            {53736, 0, 33, "2006-01-01"},
            {53737, 0, 33, "2006-01-02"},

            {54830, 0, 33, "2008-12-30"},
            {54831, 1, 33, "2008-12-31"},
            {54832, 0, 34, "2009-01-01"},
            {54833, 0, 34, "2009-01-02"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_leapSeconds")
    public void test_leapSeconds(long mjd, int adjust, int offset, String checkDate) {
        assertEquals(LocalDate.parse(checkDate).getLong(JulianFields.MODIFIED_JULIAN_DAY), mjd);

        assertEquals(adjust, rules.getLeapSecondAdjustment(mjd));
        assertEquals(offset, rules.getTaiOffset(mjd));
        if (adjust != 0) {
            long[] leaps = rules.getLeapSecondDates();
            Arrays.sort(leaps);
            assertEquals(true, Arrays.binarySearch(leaps, mjd) >= 0);
        }
    }

    //-----------------------------------------------------------------------
    // convertToUtc(TaiInstant)/convertToTai(UtcInstant)
    //-----------------------------------------------------------------------
    private static final int CURRENT_TAI_OFFSET = 37;  // change this as leap secs added
    private static final long SECS_PER_DAY = 24 * 60 * 60L;
    private static final long NANOS_PER_SEC = 1000000000L;
    private static final long MJD_1800 = -21504L;
    private static final long MJD_1900 = 15020L;
    private static final long MJD_1958 = 36204L;
    private static final long MJD_1980 = 44239L;
    private static final long MJD_2100 = 88069L;
    private static final long TAI_SECS_UTC1800 = (MJD_1800 - MJD_1958) * SECS_PER_DAY + 10;
    private static final long TAI_SECS_UTC1900 = (MJD_1900 - MJD_1958) * SECS_PER_DAY + 10;
    private static final long TAI_SECS_UTC1958 = 10;
    private static final long TAI_SECS_UTC1980 = (MJD_1980 - MJD_1958) * SECS_PER_DAY + 19;
    private static final long TAI_SECS_UTC2100 = (MJD_2100 - MJD_1958) * SECS_PER_DAY + CURRENT_TAI_OFFSET;
    private static final long TAI_SECS_UTC2100_EXTRA_NEGATIVE_LEAP = (MJD_2100 - MJD_1958) * SECS_PER_DAY + CURRENT_TAI_OFFSET - 1;

    @Test
    public void test_convertToUtc_TaiInstant_startUtcPeriod() {
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC1980, 0);  // 1980-01-01 (19 leap secs added)
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_1980, 0);
        for (int i = -10; i < 10; i++) {
            Duration duration = Duration.ofNanos(i);
            assertEquals(expected.plus(duration), rules.convertToUtc(tai.plus(duration)));
            assertEquals(tai.plus(duration), rules.convertToTai(expected.plus(duration))); // check reverse
        }
        for (int i = -10; i < 10; i++) {
            Duration duration = Duration.ofSeconds(i);
            assertEquals(expected.plus(duration), rules.convertToUtc(tai.plus(duration)));
            assertEquals(tai.plus(duration), rules.convertToTai(expected.plus(duration))); // check reverse
        }
    }

    @Test
    public void test_convertToUtc_TaiInstant_furtherAfterLeap() {
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC1980 + 1, 0);  // 1980-01-01 (19 leap secs added)
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_1980, NANOS_PER_SEC);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    @Test
    public void test_convertToUtc_TaiInstant_justAfterLeap() {
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC1980, 0);  // 1980-01-01 (19 leap secs added)
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_1980, 0);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    @Test
    public void test_convertToUtc_TaiInstant_inLeap() {
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC1980 - 1, 0);  // 1980-01-01 (1 second before 1980)
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_1980 - 1, SECS_PER_DAY * NANOS_PER_SEC);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    @Test
    public void test_convertToUtc_TaiInstant_justBeforeLeap() {
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC1980 - 2, 0);  // 1980-01-01 (2 seconds before 1980)
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_1980 - 1, (SECS_PER_DAY - 1) * NANOS_PER_SEC);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    @Test
    public void test_convertToUtc_TaiInstant_1800() {
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC1800, 0);  // 1800-01-01
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_1800, 0);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    @Test
    public void test_convertToUtc_TaiInstant_1900() {
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC1900, 0);  // 1900-01-01
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_1900, 0);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    @Test
    public void test_convertToUtc_TaiInstant_1958() {
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC1958, 0);  // 1958-01-01
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_1958, 0);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    @Test
    public void test_convertToUtc_TaiInstant_2100() {
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC2100, 0);  // 2100-01-01
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_2100, 0);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    @Test
    public void test_convertToUtc_TaiInstant_null() {
        assertThrows(NullPointerException.class, () -> rules.convertToUtc((TaiInstant) null));
    }

    @Test
    public void test_convertToTai_UtcInstant_null() {
        assertThrows(NullPointerException.class, () -> rules.convertToTai((UtcInstant) null));
    }

    //-------------------------------------------------------------------------
    @Test
    public void test_negativeLeap_justBeforeLeap() {
        rules.register(MJD_2100 - 1, -1);
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC2100_EXTRA_NEGATIVE_LEAP - 1, 0);  // 2100-01-01 (1 second before 2100)
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_2100 - 1, (SECS_PER_DAY - 2) * NANOS_PER_SEC);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    @Test
    public void test_negativeLeap_justAfterLeap() {
        rules.register(MJD_2100 - 1, -1);
        TaiInstant tai = TaiInstant.ofTaiSeconds(TAI_SECS_UTC2100_EXTRA_NEGATIVE_LEAP, 0);  // 2100-01-01
        UtcInstant expected = UtcInstant.ofModifiedJulianDay(MJD_2100, 0);
        assertEquals(expected, rules.convertToUtc(tai));
        assertEquals(tai, rules.convertToTai(expected)); // check reverse
    }

    //-----------------------------------------------------------------------
    // convertToUtc(Instant)/convertToInstant(UtcInstant)
    //-----------------------------------------------------------------------
    @Test
    public void test_convertToInstant_justBeforeLeap() {
        OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 23, 43, 21, 0, ZoneOffset.UTC);
        Instant instant = odt.toInstant();
        UtcInstant utc = UtcInstant.ofModifiedJulianDay(MJD_1980 - 1, (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC);
        assertEquals(instant, rules.convertToInstant(utc));
        assertEquals(utc, rules.convertToUtc(instant));
    }

    @Test
    public void test_convertToInstant_sls() {
        for (int i = 1; i < 1000; i++) {
            long utcNanos = (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000000000L;
            long startSls = (86401 - 1000) * NANOS_PER_SEC;
            long slsNanos = (utcNanos - (utcNanos - startSls) / 1000);
            OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 0, 0, 0,
                    (int) (slsNanos % NANOS_PER_SEC), ZoneOffset.UTC)
                    .plusSeconds((int) (slsNanos / NANOS_PER_SEC));
            Instant instant = odt.toInstant();
            UtcInstant utc = UtcInstant.ofModifiedJulianDay(
                    MJD_1980 - 1, (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * NANOS_PER_SEC);
            assertEquals(instant, rules.convertToInstant(utc));
            assertEquals(utc, rules.convertToUtc(instant));
        }
    }

    @Test
    public void test_convertToInstant_slsMillis() {
        for (int i = 1; i < 1000; i++) {
            long utcNanos = (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000000;
            long startSls = (86401 - 1000) * NANOS_PER_SEC;
            long slsNanos = (utcNanos - (utcNanos - startSls) / 1000);
            OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 23, 43, 21, (int) (slsNanos % NANOS_PER_SEC), ZoneOffset.UTC);
            Instant instant = odt.toInstant();
            UtcInstant utc = UtcInstant.ofModifiedJulianDay(
                    MJD_1980 - 1, (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000000);
            assertEquals(instant, rules.convertToInstant(utc));
            assertEquals(utc, rules.convertToUtc(instant));
        }
    }

    @Test
    public void test_convertToInstant_slsMicros() {
        for (int i = 1; i < 1000; i++) {
            long utcNanos = (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000;
            long startSls = (86401 - 1000) * NANOS_PER_SEC;
            long slsNanos = (utcNanos - (utcNanos - startSls) / 1000);
            OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 23, 43, 21, (int) (slsNanos % NANOS_PER_SEC), ZoneOffset.UTC);
            Instant instant = odt.toInstant();
            UtcInstant utc = UtcInstant.ofModifiedJulianDay(
                    MJD_1980 - 1, (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000);
            assertEquals(instant, rules.convertToInstant(utc));
            assertEquals(utc, rules.convertToUtc(instant));
        }
    }

    @Test
    public void test_convertToInstant_slsNanos() {
        for (int i = 1; i < 5005; i++) {
            long utcNanos = (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i;
            long startSls = (86401 - 1000) * NANOS_PER_SEC;
            long slsNanos = (utcNanos - (utcNanos - startSls) / 1000);
            OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 23, 43, 21, (int) (slsNanos % NANOS_PER_SEC), ZoneOffset.UTC);
            Instant instant = odt.toInstant();
            UtcInstant utc = UtcInstant.ofModifiedJulianDay(MJD_1980 - 1, utcNanos);
            assertEquals(instant, rules.convertToInstant(utc));
            // not all instants can map back to the correct UTC value
            long reverseUtcNanos = startSls + ((slsNanos - startSls) * 1000L) / (1000L - 1);
            assertEquals(UtcInstant.ofModifiedJulianDay(MJD_1980 - 1, reverseUtcNanos), rules.convertToUtc(instant));
        }
    }

    @Test
    public void test_convertToInstant_justAfterLeap() {
        OffsetDateTime odt = OffsetDateTime.of(1980, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        Instant instant = odt.toInstant();
        UtcInstant utc = UtcInstant.ofModifiedJulianDay(MJD_1980, 0);
        assertEquals(instant, rules.convertToInstant(utc));
        assertEquals(utc, rules.convertToUtc(instant));
    }

    @Test
    public void test_convertToInstant_furtherAfterLeap() {
        OffsetDateTime odt = OffsetDateTime.of(1980, 1, 1, 0, 0, 1, 0, ZoneOffset.UTC);
        Instant instant = odt.toInstant();
        UtcInstant utc = UtcInstant.ofModifiedJulianDay(MJD_1980, NANOS_PER_SEC);
        assertEquals(instant, rules.convertToInstant(utc));
        assertEquals(utc, rules.convertToUtc(instant));
    }

    //-----------------------------------------------------------------------
    // registerLeapSecond()
    //-----------------------------------------------------------------------
    @Test
    public void test_registerLeapSecond_justAfterLastDate_plusOne() {
        long[] dates = rules.getLeapSecondDates();
        long mjd = dates[dates.length - 1] + 1;
        rules.register(mjd, 1);
        long[] test = rules.getLeapSecondDates();
        assertEquals(dates.length + 1, test.length);
        assertEquals(mjd, test[test.length - 1]);
        assertEquals(1, rules.getLeapSecondAdjustment(mjd));
    }

    @Test
    public void test_registerLeapSecond_justAfterLastDate_minusOne() {
        long[] dates = rules.getLeapSecondDates();
        long mjd = dates[dates.length - 1] + 1;
        rules.register(mjd, -1);
        long[] test = rules.getLeapSecondDates();
        assertEquals(dates.length + 1, test.length);
        assertEquals(mjd, test[test.length - 1]);
        assertEquals(-1, rules.getLeapSecondAdjustment(mjd));
    }

    @Test
    public void test_registerLeapSecond_equalLastDate_sameLeap() {
        long[] dates = rules.getLeapSecondDates();
        long mjd = dates[dates.length - 1];
        int adj = rules.getLeapSecondAdjustment(mjd);
        rules.register(mjd, adj);
        long[] test = rules.getLeapSecondDates();
        assertEquals(true, Arrays.equals(test, dates));
        assertEquals(adj, rules.getLeapSecondAdjustment(mjd));
    }

    @Test
    public void test_registerLeapSecond_equalEarlierDate_sameLeap() {
        long[] dates = rules.getLeapSecondDates();
        long mjd = dates[dates.length - 2];
        int adj = rules.getLeapSecondAdjustment(mjd);
        rules.register(mjd, adj);
        long[] test = rules.getLeapSecondDates();
        assertEquals(true, Arrays.equals(test, dates));
        assertEquals(adj, rules.getLeapSecondAdjustment(mjd));
    }

    @Test
    public void test_registerLeapSecond_equalLastDate_differentLeap() {
        long[] dates = rules.getLeapSecondDates();
        long mjd = dates[dates.length - 1];
        int adj = rules.getLeapSecondAdjustment(mjd);
        assertThrows(IllegalArgumentException.class, () -> rules.register(mjd, -adj));
    }

    @Test
    public void test_registerLeapSecond_equalEarlierDate_differentLeap() {
        long[] dates = rules.getLeapSecondDates();
        long mjd = dates[dates.length - 2];
        int adj = rules.getLeapSecondAdjustment(mjd);
        assertThrows(IllegalArgumentException.class, () -> rules.register(mjd, -adj));
    }

    @Test
    public void test_registerLeapSecond_beforeLastDate() {
        long[] dates = rules.getLeapSecondDates();
        long mjd = dates[dates.length - 1] - 1;
        assertThrows(IllegalArgumentException.class, () -> rules.register(mjd, 1));
    }

    @Test
    public void test_registerLeapSecond_invalidAdjustment_zero() {
        assertThrows(IllegalArgumentException.class, () -> rules.register(MJD_2100, 0));
    }

    @Test
    public void test_registerLeapSecond_invalidAdjustment_minusTwo() {
        assertThrows(IllegalArgumentException.class, () -> rules.register(MJD_2100, -2));
    }

    @Test
    public void test_registerLeapSecond_invalidAdjustment_three() {
        assertThrows(IllegalArgumentException.class, () -> rules.register(MJD_2100, 3));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals("UtcRules[System]", rules.toString());
    }

}
