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

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.temporal.ChronoUnit.CENTURIES;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.ERAS;
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.HALF_DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLENNIA;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.time.temporal.IsoFields.QUARTER_YEARS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test Temporals.
 */
@Test
public class TestTemporals {

    //-----------------------------------------------------------------------
    // nextWorkingDay()
    //-----------------------------------------------------------------------
    @Test
    public void test_nextWorkingDay_serialization() throws IOException, ClassNotFoundException {
        TemporalAdjuster nextWorkingDay = Temporals.nextWorkingDay();
        assertTrue(nextWorkingDay instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(nextWorkingDay);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), nextWorkingDay);
    }

    @Test
    public void test_nextWorkingDay() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = LocalDate.of(2007, month, i);
                LocalDate test = (LocalDate) Temporals.nextWorkingDay().adjustInto(date);
                assertTrue(test.isAfter(date));
                assertFalse(test.getDayOfWeek().equals(SATURDAY));
                assertFalse(test.getDayOfWeek().equals(SUNDAY));

                switch (date.getDayOfWeek()) {
                    case FRIDAY:
                    case SATURDAY:
                        assertEquals(test.getDayOfWeek(), MONDAY);
                        break;
                    default:
                        assertEquals(date.getDayOfWeek().plus(1), test.getDayOfWeek());
                }

                if (test.getYear() == 2007) {
                    int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                    switch (date.getDayOfWeek()) {
                        case FRIDAY:
                            assertEquals(dayDiff, 3);
                            break;
                        case SATURDAY:
                            assertEquals(dayDiff, 2);
                            break;
                        default:
                            assertEquals(dayDiff, 1);
                    }
                } else {
                    assertEquals(test.getYear(), 2008);
                    assertEquals(test.getMonth(), JANUARY);
                    assertEquals(test.getDayOfMonth(), 1);
                }
            }
        }
    }

    @Test
    public void test_nextWorkingDay_yearChange() {
        LocalDate friday = LocalDate.of(2010, DECEMBER, 31);
        Temporal test = Temporals.nextWorkingDay().adjustInto(friday);
        assertEquals(test, LocalDate.of(2011, JANUARY, 3));

        LocalDate saturday = LocalDate.of(2011, DECEMBER, 31);
        test = Temporals.nextWorkingDay().adjustInto(saturday);
        assertEquals(test, LocalDate.of(2012, JANUARY, 2));
    }

    //-----------------------------------------------------------------------
    // previousWorkingDay()
    //-----------------------------------------------------------------------
    @Test
    public void test_previousWorkingDay_serialization() throws IOException, ClassNotFoundException {
        TemporalAdjuster previousWorkingDay = Temporals.previousWorkingDay();
        assertTrue(previousWorkingDay instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(previousWorkingDay);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), previousWorkingDay);
    }

    @Test
    public void test_previousWorkingDay() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = LocalDate.of(2007, month, i);
                LocalDate test = (LocalDate) Temporals.previousWorkingDay().adjustInto(date);
                assertTrue(test.isBefore(date));
                assertFalse(test.getDayOfWeek().equals(SATURDAY));
                assertFalse(test.getDayOfWeek().equals(SUNDAY));

                switch (date.getDayOfWeek()) {
                    case MONDAY:
                    case SUNDAY:
                        assertEquals(test.getDayOfWeek(), FRIDAY);
                        break;
                    default:
                        assertEquals(date.getDayOfWeek().minus(1), test.getDayOfWeek());
                }

                if (test.getYear() == 2007) {
                    int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                    switch (date.getDayOfWeek()) {
                        case MONDAY:
                            assertEquals(dayDiff, -3);
                            break;
                        case SUNDAY:
                            assertEquals(dayDiff, -2);
                            break;
                        default:
                            assertEquals(dayDiff, -1);
                    }
                } else {
                    assertEquals(test.getYear(), 2006);
                    assertEquals(test.getMonth(), DECEMBER);
                    assertEquals(test.getDayOfMonth(), 29);
                }
            }
        }
    }

    @Test
    public void test_previousWorkingDay_yearChange() {
        LocalDate monday = LocalDate.of(2011, JANUARY, 3);
        Temporal test = Temporals.previousWorkingDay().adjustInto(monday);
        assertEquals(test, LocalDate.of(2010, DECEMBER, 31));

        LocalDate sunday = LocalDate.of(2011, JANUARY, 2);
        test = Temporals.previousWorkingDay().adjustInto(sunday);
        assertEquals(test, LocalDate.of(2010, DECEMBER, 31));
    }

    //-----------------------------------------------------------------------
    // parseFirstMatching()
    //-----------------------------------------------------------------------
    @DataProvider(name = "parseFirstMatching")
    Object[][] data_parseFirstMatching() {
        return new Object[][] {
            {"2016-09-06", DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.BASIC_ISO_DATE},
            {"20160906", DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.BASIC_ISO_DATE},
        };
    }

    @Test(dataProvider = "parseFirstMatching")
    public void test_parseFirstMatching(String text, DateTimeFormatter fmt1, DateTimeFormatter fmt2) {
        assertEquals(Temporals.parseFirstMatching(text, LocalDate::from, fmt1, fmt2), LocalDate.of(2016, 9, 6));
    }

    public void test_parseFirstMatching_zero() {
        assertThrows(DateTimeParseException.class, () -> Temporals.parseFirstMatching("2016-09-06", LocalDate::from));
    }

    public void test_parseFirstMatching_one() {
        assertEquals(Temporals.parseFirstMatching("2016-09-06", LocalDate::from, DateTimeFormatter.ISO_LOCAL_DATE), LocalDate.of(2016, 9, 6));
    }

    public void test_parseFirstMatching_twoNoMatch() {
        assertThrows(
            DateTimeParseException.class,
            () -> Temporals.parseFirstMatching("2016", LocalDate::from, DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.BASIC_ISO_DATE));
    }

    //-----------------------------------------------------------------------
    // chronoUnit() / timeUnit()
    //-----------------------------------------------------------------------
    @DataProvider(name = "timeUnitConversion")
    Object[][] data_timeUnitConversion() {
        return new Object[][] {
            {ChronoUnit.NANOS, TimeUnit.NANOSECONDS},
            {ChronoUnit.MICROS, TimeUnit.MICROSECONDS},
            {ChronoUnit.MILLIS, TimeUnit.MILLISECONDS},
            {ChronoUnit.SECONDS, TimeUnit.SECONDS},
            {ChronoUnit.MINUTES, TimeUnit.MINUTES},
            {ChronoUnit.HOURS, TimeUnit.HOURS},
            {ChronoUnit.DAYS, TimeUnit.DAYS},
        };
    }

    @Test(dataProvider = "timeUnitConversion")
    public void test_timeUnit(ChronoUnit chronoUnit, TimeUnit timeUnit) {
        assertEquals(Temporals.timeUnit(chronoUnit), timeUnit);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_timeUnit_unknown() {
        Temporals.timeUnit(ChronoUnit.MONTHS);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_timeUnit_null() {
        Temporals.timeUnit(null);
    }

    @Test(dataProvider = "timeUnitConversion")
    public void test_chronoUnit(ChronoUnit chronoUnit, TimeUnit timeUnit) {
        assertEquals(Temporals.chronoUnit(timeUnit), chronoUnit);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_chronoUnit_null() {
        Temporals.chronoUnit(null);
    }

    //-----------------------------------------------------------------------
    // convertAmount()
    //-------------------------------------------------------------------------
    @DataProvider(name = "convertAmount")
    Object[][] data_convertAmount() {
        return new Object[][] {
            {2L, NANOS, SECONDS, 0L, 2L},
            {999_999_999L, NANOS, SECONDS, 0L, 999_999_999L},
            {1_000_000_000L, NANOS, SECONDS, 1L, 0L},
            {1_000_000_001L, NANOS, SECONDS, 1L, 1L},

            {2L, NANOS, MINUTES, 0L, 2L},
            {59_999_999_999L, NANOS, MINUTES, 0L, 59_999_999_999L},
            {60_000_000_000L, NANOS, MINUTES, 1L, 0L},
            {60_000_000_001L, NANOS, MINUTES, 1L, 1L},

            {2L, NANOS, HOURS, 0L, 2L},
            {3599_999_999_999L, NANOS, HOURS, 0L, 3599_999_999_999L},
            {3600_000_000_000L, NANOS, HOURS, 1L, 0L},
            {3600_000_000_001L, NANOS, HOURS, 1L, 1L},

            {2L, NANOS, HALF_DAYS, 0L, 2L},
            {3600_000_000_000L * 12 * 3, NANOS, HALF_DAYS, 3L, 0L},

            {2L, NANOS, DAYS, 0L, 2L},
            {3600_000_000_000L * 24 * 3, NANOS, DAYS, 3L, 0L},

            {2L, NANOS, WEEKS, 0L, 2L},
            {3600_000_000_000L * 24 * 7 * 3, NANOS, WEEKS, 3L, 0L},

            {2L, SECONDS, MINUTES, 0L, 2L},
            {59L, SECONDS, MINUTES, 0L, 59L},
            {60L, SECONDS, MINUTES, 1L, 0L},
            {61L, SECONDS, MINUTES, 1L, 1L},

            {2L, SECONDS, HOURS, 0L, 2L},
            {3599L, SECONDS, HOURS, 0L, 3599L},
            {3600L, SECONDS, HOURS, 1L, 0L},
            {3601L, SECONDS, HOURS, 1L, 1L},

            {2L, SECONDS, HALF_DAYS, 0L, 2L},
            {3600L * 12 * 3, SECONDS, HALF_DAYS, 3L, 0L},

            {2L, SECONDS, DAYS, 0L, 2L},
            {3600L * 24 * 3, SECONDS, DAYS, 3L, 0L},

            {2L, SECONDS, WEEKS, 0L, 2L},
            {3600L * 24 * 7 * 3, SECONDS, WEEKS, 3L, 0L},

            {2L, MINUTES, HOURS, 0L, 2L},
            {59L, MINUTES, HOURS, 0L, 59L},
            {60L, MINUTES, HOURS, 1L, 0L},
            {61L, MINUTES, HOURS, 1L, 1L},

            {2L, MINUTES, HALF_DAYS, 0L, 2L},
            {60L * 12 * 3 + 1, MINUTES, HALF_DAYS, 3L, 1L},

            {2L, MINUTES, DAYS, 0L, 2L},
            {60L * 24 * 3 + 1, MINUTES, DAYS, 3L, 1L},

            {2L, MINUTES, WEEKS, 0L, 2L},
            {60L * 24 * 7 * 3 + 1, MINUTES, WEEKS, 3L, 1L},

            {2L, HOURS, HALF_DAYS, 0L, 2L},
            {12L * 3 + 1, HOURS, HALF_DAYS, 3L, 1L},

            {2L, HOURS, DAYS, 0L, 2L},
            {24L * 3 + 1, HOURS, DAYS, 3L, 1L},

            {2L, HOURS, WEEKS, 0L, 2L},
            {24L * 7 * 3 + 1, HOURS, WEEKS, 3L, 1L},

            {1L, HALF_DAYS, DAYS, 0L, 1L},
            {2L * 3 + 1, HALF_DAYS, DAYS, 3L, 1L},

            {1L, HALF_DAYS, WEEKS, 0L, 1L},
            {2L * 7 * 3 + 1, HALF_DAYS, WEEKS, 3L, 1L},

            {1L, DAYS, WEEKS, 0L, 1L},
            {7L * 3 + 1, DAYS, WEEKS, 3L, 1L},

            {2L, SECONDS, NANOS, 2_000_000_000L, 0L},
            {2L, MINUTES, NANOS, 2_000_000_000L * 60, 0L},
            {2L, HOURS, NANOS, 2_000_000_000L * 3600, 0L},
            {2L, HALF_DAYS, NANOS, 2_000_000_000L * 3600 * 12, 0L},
            {2L, DAYS, NANOS, 2_000_000_000L * 3600 * 24, 0L},
            {2L, WEEKS, NANOS, 2_000_000_000L * 3600 * 24 * 7, 0L},

            {2L, MINUTES, SECONDS, 2L * 60, 0L},
            {2L, HOURS, SECONDS, 2L * 3600, 0L},
            {2L, HALF_DAYS, SECONDS, 2L * 3600 * 12, 0L},
            {2L, DAYS, SECONDS, 2L * 3600 * 24, 0L},
            {2L, WEEKS, SECONDS, 2L * 3600 * 24 * 7, 0L},

            {2L, HOURS, MINUTES, 2L * 60, 0L},
            {2L, HALF_DAYS, MINUTES, 2L * 60 * 12, 0L},
            {2L, DAYS, MINUTES, 2L * 60 * 24, 0L},
            {2L, WEEKS, MINUTES, 2L * 60 * 24 * 7, 0L},

            {2L, HALF_DAYS, HOURS, 2L * 12, 0L},
            {2L, DAYS, HOURS, 2L * 24, 0L},
            {2L, WEEKS, HOURS, 2L * 24 * 7, 0L},

            {2L, DAYS, HALF_DAYS, 2L * 2, 0L},
            {2L, WEEKS, HALF_DAYS, 2L * 2 * 7, 0L},

            {2L, WEEKS, DAYS, 2L * 7, 0L},

            {2L * 3 + 1, MONTHS, QUARTER_YEARS, 2L, 1L},
            {2L * 12 + 1, MONTHS, YEARS, 2L, 1L},
            {2L * 120 + 1, MONTHS, DECADES, 2L, 1L},
            {2L * 1200 + 1, MONTHS, CENTURIES, 2L, 1L},
            {2L * 12000 + 1, MONTHS, MILLENNIA, 2L, 1L},

            {2L * 4 + 1, QUARTER_YEARS, YEARS, 2L, 1L},
            {2L * 40 + 1, QUARTER_YEARS, DECADES, 2L, 1L},
            {2L * 400 + 1, QUARTER_YEARS, CENTURIES, 2L, 1L},
            {2L * 4000 + 1, QUARTER_YEARS, MILLENNIA, 2L, 1L},

            {2L * 10 + 1, YEARS, DECADES, 2L, 1L},
            {2L * 100 + 1, YEARS, CENTURIES, 2L, 1L},
            {2L * 1000 + 1, YEARS, MILLENNIA, 2L, 1L},

            {2L * 10 + 1, DECADES, CENTURIES, 2L, 1L},
            {2L * 100 + 1, DECADES, MILLENNIA, 2L, 1L},

            {2L * 10 + 1, CENTURIES, MILLENNIA, 2L, 1L},

            {2L, QUARTER_YEARS, MONTHS, 2L * 3, 0L},
            {2L, YEARS, MONTHS, 2L * 12, 0L},
            {2L, DECADES, MONTHS, 2L * 120, 0L},
            {2L, CENTURIES, MONTHS, 2L * 1200, 0L},
            {2L, MILLENNIA, MONTHS, 2L * 12000, 0L},

            {2L, YEARS, QUARTER_YEARS, 2L * 4, 0L},
            {2L, DECADES, QUARTER_YEARS, 2L * 40, 0L},
            {2L, CENTURIES, QUARTER_YEARS, 2L * 400, 0L},
            {2L, MILLENNIA, QUARTER_YEARS, 2L * 4000, 0L},

            {2L, DECADES, YEARS, 2L * 10, 0L},
            {2L, CENTURIES, YEARS, 2L * 100, 0L},
            {2L, MILLENNIA, YEARS, 2L * 1000, 0L},

            {2L, CENTURIES, DECADES, 2L * 10, 0L},
            {2L, MILLENNIA, DECADES, 2L * 100, 0L},

            {2L, MILLENNIA, CENTURIES, 2L * 10, 0L},
        };
    }

    @Test(dataProvider = "convertAmount")
    public void test_convertAmount(
            long fromAmount, TemporalUnit fromUnit, TemporalUnit resultUnit,
            long resultWhole, long resultRemainder) {
        long[] result = Temporals.convertAmount(fromAmount, fromUnit, resultUnit);
        assertEquals(result[0], resultWhole);
        assertEquals(result[1], resultRemainder);
    }

    @Test(dataProvider = "convertAmount")
    public void test_convertAmount_negative(
            long fromAmount, TemporalUnit fromUnit, TemporalUnit resultUnit,
            long resultWhole, long resultRemainder) {
        long[] result = Temporals.convertAmount(-fromAmount, fromUnit, resultUnit);
        assertEquals(result[0], -resultWhole);
        assertEquals(result[1], -resultRemainder);
    }

    @Test
    public void test_convertAmountSameUnit_zero() {
        for (ChronoUnit unit : ChronoUnit.values()) {
            if (unit != ERAS && unit != FOREVER) {
                long[] result = Temporals.convertAmount(0, unit, unit);
                assertEquals(result[0], 0);
                assertEquals(result[1], 0);
            }
        }
    }

    @Test
    public void test_convertAmountSameUnit_nonZero() {
        for (ChronoUnit unit : ChronoUnit.values()) {
            if (unit != ERAS && unit != FOREVER) {
                long[] result = Temporals.convertAmount(2, unit, unit);
                assertEquals(result[0], 2);
                assertEquals(result[1], 0);
            }
        }
    }

    @DataProvider(name = "convertAmountInvalid")
    Object[][] data_convertAmountInvalid() {
        return new Object[][] {
            {SECONDS, MONTHS},
            {SECONDS, QUARTER_YEARS},
            {SECONDS, YEARS},
            {SECONDS, DECADES},
            {SECONDS, CENTURIES},
            {SECONDS, MILLENNIA},

            {MONTHS, SECONDS},
            {QUARTER_YEARS, SECONDS},
            {YEARS, SECONDS},
            {DECADES, SECONDS},
            {CENTURIES, SECONDS},
            {MILLENNIA, SECONDS},
        };
    }

    @Test(dataProvider = "convertAmountInvalid", expectedExceptions = DateTimeException.class)
    public void test_convertAmountInvalid(TemporalUnit fromUnit, TemporalUnit resultUnit) {
        Temporals.convertAmount(1, fromUnit, resultUnit);
    }

    @DataProvider(name = "convertAmountInvalidUnsupported")
    Object[][] data_convertAmountInvalidUnsupported() {
        return new Object[][] {
            {SECONDS, ERAS},
            {ERAS, SECONDS},
            {YEARS, ERAS},
            {ERAS, YEARS},

            {SECONDS, FOREVER},
            {FOREVER, SECONDS},
            {YEARS, FOREVER},
            {FOREVER, YEARS},

            {FOREVER, ERAS},
            {ERAS, FOREVER},
        };
    }

    @Test(dataProvider = "convertAmountInvalidUnsupported", expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_convertAmountInvalidUnsupported(TemporalUnit fromUnit, TemporalUnit resultUnit) {
        Temporals.convertAmount(1, fromUnit, resultUnit);
    }

}
