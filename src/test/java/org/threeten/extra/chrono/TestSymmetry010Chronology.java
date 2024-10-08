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
package org.threeten.extra.chrono;


import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.MINUTE_OF_DAY;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.ChronoUnit.CENTURIES;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.ERAS;
import static java.time.temporal.ChronoUnit.MILLENNIA;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.HijrahEra;
import java.time.chrono.IsoEra;
import java.time.chrono.JapaneseEra;
import java.time.chrono.MinguoEra;
import java.time.chrono.ThaiBuddhistEra;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

/**
 * Test.
 */
@SuppressWarnings({"static-method", "javadoc"})
public class TestSymmetry010Chronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology() {
        Chronology chrono = Chronology.of("Sym010");
        assertNotNull(chrono);
        assertEquals(Symmetry010Chronology.INSTANCE, chrono);
        assertEquals("Sym010", chrono.getId());
        assertEquals(null, chrono.getCalendarType());
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.of
    //-----------------------------------------------------------------------
    public static Object[][] data_samples() {
        return new Object[][] {
            { Symmetry010Date.of(   1,  1,  1), LocalDate.of(   1,  1,  1) },
            { Symmetry010Date.of( 272,  2, 28), LocalDate.of( 272,  2, 27) }, // Constantine the Great, Roman emperor (d. 337)
            { Symmetry010Date.of( 272,  2, 27), LocalDate.of( 272,  2, 26) },
            { Symmetry010Date.of( 742,  3, 27), LocalDate.of( 742,  4,  2) }, // Charlemagne, Frankish king (d. 814)
            { Symmetry010Date.of( 742,  4,  2), LocalDate.of( 742,  4,  7) },
            { Symmetry010Date.of(1066, 10, 14), LocalDate.of(1066, 10, 14) }, // Norman Conquest: Battle of Hastings
            { Symmetry010Date.of(1304,  7, 21), LocalDate.of(1304,  7, 20) }, // Francesco Petrarca - Petrarch, Italian scholar and poet in Renaissance Italy, "Father of Humanism" (d. 1374)
            { Symmetry010Date.of(1304,  7, 20), LocalDate.of(1304,  7, 19) },
            { Symmetry010Date.of(1433, 11, 12), LocalDate.of(1433, 11, 10) }, // Charles the Bold, French son of Isabella of Portugal, Duchess of Burgundy (d. 1477)
            { Symmetry010Date.of(1433, 11, 10), LocalDate.of(1433, 11,  8) },
            { Symmetry010Date.of(1452,  4, 11), LocalDate.of(1452,  4, 15) }, // Leonardo da Vinci, Italian painter, sculptor, and architect (d. 1519)
            { Symmetry010Date.of(1452,  4, 15), LocalDate.of(1452,  4, 19) },
            { Symmetry010Date.of(1492, 10, 10), LocalDate.of(1492, 10, 12) }, // Christopher Columbus's expedition makes landfall in the Caribbean.
            { Symmetry010Date.of(1492, 10, 12), LocalDate.of(1492, 10, 14) },
            { Symmetry010Date.of(1564,  2, 18), LocalDate.of(1564,  2, 15) }, // Galileo Galilei, Italian astronomer and physicist (d. 1642)
            { Symmetry010Date.of(1564,  2, 15), LocalDate.of(1564,  2, 12) },
            { Symmetry010Date.of(1564,  4, 28), LocalDate.of(1564,  4, 26) }, // William Shakespeare is baptized in Stratford-upon-Avon, Warwickshire, England (date of actual birth is unknown, d. 1616).
            { Symmetry010Date.of(1564,  4, 26), LocalDate.of(1564,  4, 24) },
            { Symmetry010Date.of(1643,  1 , 7), LocalDate.of(1643,  1,  4) }, // Sir Isaac Newton, English physicist and mathematician (d. 1727)
            { Symmetry010Date.of(1643,  1,  4), LocalDate.of(1643,  1,  1) },
            { Symmetry010Date.of(1707,  4, 12), LocalDate.of(1707,  4, 15) }, // Leonhard Euler, Swiss mathematician and physicist (d. 1783)
            { Symmetry010Date.of(1707,  4, 15), LocalDate.of(1707,  4, 18) },
            { Symmetry010Date.of(1789,  7, 16), LocalDate.of(1789,  7, 14) }, // French Revolution: Citizens of Paris storm the Bastille.
            { Symmetry010Date.of(1789,  7, 14), LocalDate.of(1789,  7, 12) },
            { Symmetry010Date.of(1879,  3, 14), LocalDate.of(1879,  3, 14) }, // Albert Einstein, German theoretical physicist (d. 1955)
            { Symmetry010Date.of(1941,  9, 11), LocalDate.of(1941,  9,  9) }, // Dennis MacAlistair Ritchie, American computer scientist (d. 2011)
            { Symmetry010Date.of(1941,  9,  9), LocalDate.of(1941,  9,  7) },
            { Symmetry010Date.of(1970,  1,  4), LocalDate.of(1970,  1,  1) }, // Unix time begins at 00:00:00 UTC/GMT.
            { Symmetry010Date.of(1970,  1,  1), LocalDate.of(1969, 12, 29) },
            { Symmetry010Date.of(1999, 12, 29), LocalDate.of(2000,  1,  1) }, // Start of the 21st century / 3rd millennium
            { Symmetry010Date.of(2000,  1,  1), LocalDate.of(2000,  1,  3) },
        };
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_from_Symmetry010Date(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(iso, LocalDate.from(sym010));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Symmetry010Date_from_LocalDate(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(sym010, Symmetry010Date.from(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Symmetry010Date_chronology_dateEpochDay(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(sym010, Symmetry010Chronology.INSTANCE.dateEpochDay(iso.toEpochDay()));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Symmetry010Date_toEpochDay(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(iso.toEpochDay(), sym010.toEpochDay());
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Symmetry010Date_until_Symmetry010Date(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(Symmetry010Chronology.INSTANCE.period(0, 0, 0), sym010.until(sym010));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Symmetry010Date_until_LocalDate(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(Symmetry010Chronology.INSTANCE.period(0, 0, 0), sym010.until(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_Chronology_date_Temporal(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(sym010, Symmetry010Chronology.INSTANCE.date(iso));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_LocalDate_until_Symmetry010Date(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(Period.ZERO, iso.until(sym010));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_plusDays(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(iso, LocalDate.from(sym010.plus(0, DAYS)));
        assertEquals(iso.plusDays(1), LocalDate.from(sym010.plus(1, DAYS)));
        assertEquals(iso.plusDays(35), LocalDate.from(sym010.plus(35, DAYS)));
        assertEquals(iso.plusDays(-1), LocalDate.from(sym010.plus(-1, DAYS)));
        assertEquals(iso.plusDays(-60), LocalDate.from(sym010.plus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_minusDays(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(iso, LocalDate.from(sym010.minus(0, DAYS)));
        assertEquals(iso.minusDays(1), LocalDate.from(sym010.minus(1, DAYS)));
        assertEquals(iso.minusDays(35), LocalDate.from(sym010.minus(35, DAYS)));
        assertEquals(iso.minusDays(-1), LocalDate.from(sym010.minus(-1, DAYS)));
        assertEquals(iso.minusDays(-60), LocalDate.from(sym010.minus(-60, DAYS)));
    }

    @ParameterizedTest
    @MethodSource("data_samples")
    public void test_until_DAYS(Symmetry010Date sym010, LocalDate iso) {
        assertEquals(0, sym010.until(iso.plusDays(0), DAYS));
        assertEquals(1, sym010.until(iso.plusDays(1), DAYS));
        assertEquals(35, sym010.until(iso.plusDays(35), DAYS));
        assertEquals(-40, sym010.until(iso.minusDays(40), DAYS));
    }

    public static Object[][] data_badDates() {
        return new Object[][] {
            {-1, 13, 28},
            {-1, 13, 29},

            {2000, -2, 1},
            {2000, 13, 1},
            {2000, 15, 1},

            {2000, 1, -1},
            {2000, 1, 0},

            {2000, 0, 1},
            {2000, -1, 0},
            {2000, -1, 1},

            {2000, 1, 31},
            {2000, 2, 32},
            {2000, 3, 31},
            {2000, 4, 31},
            {2000, 5, 32},
            {2000, 6, 31},
            {2000, 7, 31},
            {2000, 8, 32},
            {2000, 9, 31},
            {2000, 10, 31},
            {2000, 11, 32},
            {2000, 12, 31},
            {2004, 12, 38},
        };
    }

    @ParameterizedTest
    @MethodSource("data_badDates")
    public void test_badDates(int year, int month, int dom) {
        assertThrows(DateTimeException.class, () -> Symmetry010Date.of(year, month, dom));
    }

    public static Object[][] data_badLeapDates() {
        return new Object[][] {
            {1},
            {100},
            {200},
            {2000}
        };
    }

    @ParameterizedTest
    @MethodSource("data_badLeapDates")
    public void badLeapDayDates(int year) {
        assertThrows(DateTimeException.class, () -> Symmetry010Date.of(year, 12, 37));
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.isLeapYear
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_specific() {
        assertTrue(Symmetry010Chronology.INSTANCE.isLeapYear(3));
        assertFalse(Symmetry010Chronology.INSTANCE.isLeapYear(6));
        assertTrue(Symmetry010Chronology.INSTANCE.isLeapYear(9));
        assertFalse(Symmetry010Chronology.INSTANCE.isLeapYear(2000));
        assertTrue(Symmetry010Chronology.INSTANCE.isLeapYear(2004));
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.isLeapWeek
    //-----------------------------------------------------------------------
    @Test
    public void test_leapWeek() {
        assertTrue(Symmetry010Date.of (2015, 12, 31).isLeapWeek());
        assertTrue(Symmetry010Date.of (2015, 12, 32).isLeapWeek());
        assertTrue(Symmetry010Date.of (2015, 12, 33).isLeapWeek());
        assertTrue(Symmetry010Date.of (2015, 12, 34).isLeapWeek());
        assertTrue(Symmetry010Date.of (2015, 12, 35).isLeapWeek());
        assertTrue(Symmetry010Date.of (2015, 12, 36).isLeapWeek());
        assertTrue(Symmetry010Date.of (2015, 12, 37).isLeapWeek());
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.lengthOfMonth
    //-----------------------------------------------------------------------
    public static Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {2000, 1, 28, 30},
            {2000, 2, 28, 31},
            {2000, 3, 28, 30},
            {2000, 4, 28, 30},
            {2000, 5, 28, 31},
            {2000, 6, 28, 30},
            {2000, 7, 28, 30},
            {2000, 8, 28, 31},
            {2000, 9, 28, 30},
            {2000, 10, 28, 30},
            {2000, 11, 28, 31},
            {2000, 12, 28, 30},
            {2004, 12, 20, 37},
        };
    }

    @ParameterizedTest
    @MethodSource("data_lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int day, int length) {
        assertEquals(length, Symmetry010Date.of(year, month, day).lengthOfMonth());
    }

    @ParameterizedTest
    @MethodSource("data_lengthOfMonth")
    public void test_lengthOfMonthFirst(int year, int month, int day, int length) {
        assertEquals(length, Symmetry010Date.of(year, month, 1).lengthOfMonth());
    }

    @Test
    public void test_lengthOfMonth_specific() {
        assertEquals(30, Symmetry010Date.of(2000, 12, 1).lengthOfMonth());
        assertEquals(37, Symmetry010Date.of(2004, 12, 1).lengthOfMonth());
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.era
    // Symmetry010Date.prolepticYear
    // Symmetry010Date.dateYearDay
    //-----------------------------------------------------------------------
    @Test
    public void test_era_loop() {
        for (int year = 1; year < 200; year++) {
            Symmetry010Date base = Symmetry010Chronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            IsoEra era = IsoEra.CE;
            assertEquals(era, base.getEra());
            assertEquals(year, base.get(YEAR_OF_ERA));
            Symmetry010Date eraBased = Symmetry010Chronology.INSTANCE.date(era, year, 1, 1);
            assertEquals(base, eraBased);
        }

        for (int year = -200; year < 0; year++) {
            Symmetry010Date base = Symmetry010Chronology.INSTANCE.date(year, 1, 1);
            assertEquals(year, base.get(YEAR));
            IsoEra era = IsoEra.BCE;
            assertEquals(era, base.getEra());
            assertEquals(1 - year, base.get(YEAR_OF_ERA));
            Symmetry010Date eraBased = Symmetry010Chronology.INSTANCE.date(era, year, 1, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_era_yearDay_loop() {
        for (int year = 1; year < 200; year++) {
            Symmetry010Date base = Symmetry010Chronology.INSTANCE.dateYearDay(year, 1);
            assertEquals(year, base.get(YEAR));
            IsoEra era = IsoEra.CE;
            assertEquals(era, base.getEra());
            assertEquals(year, base.get(YEAR_OF_ERA));
            Symmetry010Date eraBased = Symmetry010Chronology.INSTANCE.dateYearDay(era, year, 1);
            assertEquals(base, eraBased);
        }
    }

    @Test
    public void test_prolepticYear_specific() {
        assertEquals(4, Symmetry010Chronology.INSTANCE.prolepticYear(IsoEra.CE, 4));
        assertEquals(3, Symmetry010Chronology.INSTANCE.prolepticYear(IsoEra.CE, 3));
        assertEquals(2, Symmetry010Chronology.INSTANCE.prolepticYear(IsoEra.CE, 2));
        assertEquals(1, Symmetry010Chronology.INSTANCE.prolepticYear(IsoEra.CE, 1));
        assertEquals(2000, Symmetry010Chronology.INSTANCE.prolepticYear(IsoEra.CE, 2000));
        assertEquals(1582, Symmetry010Chronology.INSTANCE.prolepticYear(IsoEra.CE, 1582));
    }

    public static Object[][] data_prolepticYear_badEra() {
        return new Era[][] {
            { AccountingEra.BCE },
            { AccountingEra.CE },
            { CopticEra.BEFORE_AM },
            { CopticEra.AM },
            { DiscordianEra.YOLD },
            { EthiopicEra.BEFORE_INCARNATION },
            { EthiopicEra.INCARNATION },
            { HijrahEra.AH },
            { InternationalFixedEra.CE },
            { JapaneseEra.MEIJI },
            { JapaneseEra.TAISHO },
            { JapaneseEra.SHOWA },
            { JapaneseEra.HEISEI },
            { JulianEra.BC },
            { JulianEra.AD },
            { MinguoEra.BEFORE_ROC },
            { MinguoEra.ROC },
            { PaxEra.BCE },
            { PaxEra.CE },
            { ThaiBuddhistEra.BEFORE_BE },
            { ThaiBuddhistEra.BE },
        };
    }

    @ParameterizedTest
    @MethodSource("data_prolepticYear_badEra")
    public void test_prolepticYear_badEra(Era era) {
        assertThrows(ClassCastException.class, () -> Symmetry010Chronology.INSTANCE.prolepticYear(era, 4));
    }

    @Test
    public void test_Chronology_eraOf() {
        assertEquals(IsoEra.BCE, Symmetry010Chronology.INSTANCE.eraOf(0));
        assertEquals(IsoEra.CE, Symmetry010Chronology.INSTANCE.eraOf(1));
    }

    @Test
    public void test_Chronology_eraOf_invalid() {
        assertThrows(DateTimeException.class, () -> Symmetry010Chronology.INSTANCE.eraOf(2));
    }

    @Test
    public void test_Chronology_eras() {
        List<Era> eras = Symmetry010Chronology.INSTANCE.eras();
        assertEquals(2, eras.size());
        assertTrue(eras.contains(IsoEra.BCE));
        assertTrue(eras.contains(IsoEra.CE));
    }

    //-----------------------------------------------------------------------
    // Chronology.range
    //-----------------------------------------------------------------------
    @Test
    public void test_Chronology_range() {
        assertEquals(ValueRange.of(1, 7), Symmetry010Chronology.INSTANCE.range(ALIGNED_DAY_OF_WEEK_IN_MONTH));
        assertEquals(ValueRange.of(1, 7), Symmetry010Chronology.INSTANCE.range(ALIGNED_DAY_OF_WEEK_IN_YEAR));
        assertEquals(ValueRange.of(1, 4, 5), Symmetry010Chronology.INSTANCE.range(ALIGNED_WEEK_OF_MONTH));
        assertEquals(ValueRange.of(1, 52, 53), Symmetry010Chronology.INSTANCE.range(ALIGNED_WEEK_OF_YEAR));
        assertEquals(ValueRange.of(1, 7), Symmetry010Chronology.INSTANCE.range(DAY_OF_WEEK));
        assertEquals(ValueRange.of(1, 30, 37), Symmetry010Chronology.INSTANCE.range(DAY_OF_MONTH));
        assertEquals(ValueRange.of(1, 364, 371), Symmetry010Chronology.INSTANCE.range(DAY_OF_YEAR));
        assertEquals(ValueRange.of(0, 1), Symmetry010Chronology.INSTANCE.range(ERA));
        assertEquals(ValueRange.of(-1_000_000 * 364L - 177_474 * 7 - 719_162, 1_000_000 * 364L + 177_474 * 7 - 719_162), Symmetry010Chronology.INSTANCE.range(EPOCH_DAY));
        assertEquals(ValueRange.of(1, 12), Symmetry010Chronology.INSTANCE.range(MONTH_OF_YEAR));
        assertEquals(ValueRange.of(-12_000_000L, 11_999_999L), Symmetry010Chronology.INSTANCE.range(PROLEPTIC_MONTH));
        assertEquals(ValueRange.of(-1_000_000L, 1_000_000), Symmetry010Chronology.INSTANCE.range(YEAR));
        assertEquals(ValueRange.of(-1_000_000, 1_000_000), Symmetry010Chronology.INSTANCE.range(YEAR_OF_ERA));
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.range
    //-----------------------------------------------------------------------
    public static Object[][] data_ranges() {
        return new Object[][] {
            // Leap Day and Year Day are members of months
            {2012, 1, 23, DAY_OF_MONTH, ValueRange.of(1, 30)},
            {2012, 2, 23, DAY_OF_MONTH, ValueRange.of(1, 31)},
            {2012, 3, 23, DAY_OF_MONTH, ValueRange.of(1, 30)},
            {2012, 4, 23, DAY_OF_MONTH, ValueRange.of(1, 30)},
            {2012, 5, 23, DAY_OF_MONTH, ValueRange.of(1, 31)},
            {2012, 6, 23, DAY_OF_MONTH, ValueRange.of(1, 30)},
            {2012, 7, 23, DAY_OF_MONTH, ValueRange.of(1, 30)},
            {2012, 8, 23, DAY_OF_MONTH, ValueRange.of(1, 31)},
            {2012, 9, 23, DAY_OF_MONTH, ValueRange.of(1, 30)},
            {2012, 10, 23, DAY_OF_MONTH, ValueRange.of(1, 30)},
            {2012, 11, 23, DAY_OF_MONTH, ValueRange.of(1, 31)},
            {2012, 12, 23, DAY_OF_MONTH, ValueRange.of(1, 30)},
            {2015, 12, 23, DAY_OF_MONTH, ValueRange.of(1, 37)},

            {2012, 1, 23, DAY_OF_WEEK, ValueRange.of(1, 7)},
            {2012, 6, 23, DAY_OF_WEEK, ValueRange.of(1, 7)},
            {2012, 12, 23, DAY_OF_WEEK, ValueRange.of(1, 7)},

            {2012, 1, 23, DAY_OF_YEAR, ValueRange.of(1, 364)},
            {2015, 1, 23, DAY_OF_YEAR, ValueRange.of(1, 371)},

            {2012, 1, 23, MONTH_OF_YEAR, ValueRange.of(1, 12)},

            {2012, 1, 23, ALIGNED_DAY_OF_WEEK_IN_MONTH, ValueRange.of(1, 7)},
            {2012, 6, 23, ALIGNED_DAY_OF_WEEK_IN_MONTH, ValueRange.of(1, 7)},
            {2012, 12, 23, ALIGNED_DAY_OF_WEEK_IN_MONTH, ValueRange.of(1, 7)},

            {2012, 1, 23, ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {2012, 2, 23, ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 4)},
            {2015, 12, 23, ALIGNED_WEEK_OF_MONTH, ValueRange.of(1, 5)},

            {2012, 1, 23, ALIGNED_DAY_OF_WEEK_IN_YEAR, ValueRange.of(1, 7)},
            {2012, 6, 23, ALIGNED_DAY_OF_WEEK_IN_YEAR, ValueRange.of(1, 7)},
            {2012, 12, 23, ALIGNED_DAY_OF_WEEK_IN_YEAR, ValueRange.of(1, 7)},

            {2012, 1, 23, ALIGNED_WEEK_OF_YEAR, ValueRange.of(1, 52)},
            {2012, 6, 23, ALIGNED_WEEK_OF_YEAR, ValueRange.of(1, 52)},
            {2012, 12, 23, ALIGNED_WEEK_OF_YEAR, ValueRange.of(1, 52)},
            {2015, 12, 30, ALIGNED_WEEK_OF_YEAR, ValueRange.of(1, 53)},
        };
    }

    @ParameterizedTest
    @MethodSource("data_ranges")
    public void test_range(int year, int month, int dom, TemporalField field, ValueRange range) {
        assertEquals(range, Symmetry010Date.of(year, month, dom).range(field));
    }

    @Test
    public void test_range_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Symmetry010Date.of(2012, 6, 28).range(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.getLong
    //-----------------------------------------------------------------------
    public static Object[][] data_getLong() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 2},
            {2014, 5, 26, DAY_OF_MONTH, 26},
            {2014, 5, 26, DAY_OF_YEAR, 30 + 31 + 30 + 30 + 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 7},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 4 + 5 + 4 + 4 + 4},
            {2014, 5, 26, MONTH_OF_YEAR, 5},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 12 + 5 - 1},
            {2014, 5, 26, YEAR, 2014},
            {2014, 5, 26, ERA, 1},
            {1, 5, 8, ERA, 1},

            {2012, 9, 26, DAY_OF_WEEK, 1},
            {2012, 9, 26, DAY_OF_YEAR, 3 * (4 + 5 + 4) * 7 - 4},
            {2012, 9, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5},
            {2012, 9, 26, ALIGNED_WEEK_OF_MONTH, 4},
            {2012, 9, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3},
            {2012, 9, 26, ALIGNED_WEEK_OF_YEAR, 3 * (4 + 5 + 4)},

            {2015, 12, 37, DAY_OF_WEEK, 5},
            {2015, 12, 37, DAY_OF_MONTH, 37},
            {2015, 12, 37, DAY_OF_YEAR, 4 * (4 + 5 + 4) * 7 + 7},
            {2015, 12, 37, ALIGNED_DAY_OF_WEEK_IN_MONTH, 2},
            {2015, 12, 37, ALIGNED_WEEK_OF_MONTH, 6},
            {2015, 12, 37, ALIGNED_DAY_OF_WEEK_IN_YEAR, 7},
            {2015, 12, 37, ALIGNED_WEEK_OF_YEAR, 53},
            {2015, 12, 37, MONTH_OF_YEAR, 12},
            {2015, 12, 37, PROLEPTIC_MONTH, 2016 * 12 - 1},
        };
    }

    @ParameterizedTest
    @MethodSource("data_getLong")
    public void test_getLong(int year, int month, int dom, TemporalField field, long expected) {
        assertEquals(expected, Symmetry010Date.of(year, month, dom).getLong(field));
    }

    @Test
    public void test_getLong_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Symmetry010Date.of(2012, 6, 28).getLong(MINUTE_OF_DAY));
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.with
    //-----------------------------------------------------------------------
    public static Object[][] data_with() {
        return new Object[][] {
            {2014, 5, 26, DAY_OF_WEEK, 1, 2014, 5, 20},
            {2014, 5, 26, DAY_OF_WEEK, 5, 2014, 5, 24},
            {2014, 5, 26, DAY_OF_MONTH, 28, 2014, 5, 28},
            {2014, 5, 26, DAY_OF_MONTH, 26, 2014, 5, 26},
            {2014, 5, 26, DAY_OF_YEAR, 364, 2014, 12, 30},
            {2014, 5, 26, DAY_OF_YEAR, 138, 2014, 5, 17},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2014, 5, 24},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 1, 2014, 5, 5},
            {2014, 5, 26, ALIGNED_WEEK_OF_MONTH, 4, 2014, 5, 26},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2014, 5, 21},
            {2014, 5, 26, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5, 2014, 5, 24},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 23, 2014, 6,  9},
            {2014, 5, 26, ALIGNED_WEEK_OF_YEAR, 20, 2014, 5, 19},
            {2014, 5, 26, MONTH_OF_YEAR, 4, 2014, 4, 26},
            {2014, 5, 26, MONTH_OF_YEAR, 5, 2014, 5, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2013 * 12 + 3 - 1, 2013, 3, 26},
            {2014, 5, 26, PROLEPTIC_MONTH, 2014 * 12 + 5 - 1, 2014, 5, 26},
            {2014, 5, 26, YEAR, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR, 2014, 2014, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2012, 2012, 5, 26},
            {2014, 5, 26, YEAR_OF_ERA, 2014, 2014, 5, 26},
            {2014, 5, 26, ERA, 1, 2014, 5, 26},

            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_MONTH, 1, 2015, 12, 22},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_MONTH, 2, 2015, 12, 23},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, 2015, 12, 24},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_MONTH, 4, 2015, 12, 25},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_MONTH, 5, 2015, 12, 26},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_MONTH, 6, 2015, 12, 27},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_MONTH, 7, 2015, 12, 28},

            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_YEAR, 1, 2015, 12, 17},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_YEAR, 2, 2015, 12, 18},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_YEAR, 3, 2015, 12, 19},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_YEAR, 4, 2015, 12, 20},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_YEAR, 5, 2015, 12, 21},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_YEAR, 6, 2015, 12, 22},
            {2015, 12, 22, ALIGNED_DAY_OF_WEEK_IN_YEAR, 7, 2015, 12, 23},

            {2015, 12, 29, ALIGNED_WEEK_OF_MONTH, 0, 2015, 12, 29},
            {2015, 12, 29, ALIGNED_WEEK_OF_MONTH, 3, 2015, 12, 15},

            {2015, 12, 29, ALIGNED_WEEK_OF_YEAR, 0, 2015, 12, 29},
            {2015, 12, 29, ALIGNED_WEEK_OF_YEAR, 3, 2015,  1, 20},

            {2015, 12, 29, DAY_OF_WEEK, 0, 2015, 12, 29},
            {2015, 12, 28, DAY_OF_WEEK, 1, 2015, 12, 24},
            {2015, 12, 28, DAY_OF_WEEK, 2, 2015, 12, 25},
            {2015, 12, 28, DAY_OF_WEEK, 3, 2015, 12, 26},
            {2015, 12, 28, DAY_OF_WEEK, 4, 2015, 12, 27},
            {2015, 12, 28, DAY_OF_WEEK, 5, 2015, 12, 28},
            {2015, 12, 28, DAY_OF_WEEK, 6, 2015, 12, 29},
            {2015, 12, 28, DAY_OF_WEEK, 7, 2015, 12, 30},

            {2015, 12, 29, DAY_OF_MONTH, 1, 2015, 12, 1},
            {2015, 12, 29, DAY_OF_MONTH, 3, 2015, 12, 3},

            {2015, 12, 29, MONTH_OF_YEAR, 1, 2015, 1, 29},
            {2015, 12, 29, MONTH_OF_YEAR, 12, 2015, 12, 29},
            {2015, 12, 29, MONTH_OF_YEAR, 2, 2015, 2, 29},

            {2015, 12, 37, YEAR, 2004, 2004, 12, 37},
            {2015, 12, 37, YEAR, 2013, 2013, 12, 30},

            {2014, 3, 28, DAY_OF_MONTH, 1, 2014, 3, 1},
            {2014, 1, 28, DAY_OF_MONTH, 1, 2014, 1, 1},
            {2014, 3, 28, MONTH_OF_YEAR, 1, 2014, 1, 28},
            {2015, 3, 28, DAY_OF_YEAR, 371, 2015, 12, 37},
            {2012, 3, 28, DAY_OF_YEAR, 364, 2012, 12, 30},
        };
    }

    @ParameterizedTest
    @MethodSource("data_with")
    public void test_with_TemporalField(int year, int month, int dom,
            TemporalField field, long value,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(Symmetry010Date.of(expectedYear, expectedMonth, expectedDom), Symmetry010Date.of(year, month, dom).with(field, value));
    }

    public static Object[][] data_with_bad() {
        return new Object[][] {
            {2013,  1,  1, ALIGNED_DAY_OF_WEEK_IN_MONTH, -1},
            {2013,  1,  1, ALIGNED_DAY_OF_WEEK_IN_MONTH,  8},

            {2013,  1,  1, ALIGNED_DAY_OF_WEEK_IN_YEAR, -1},
            {2013,  1,  1, ALIGNED_DAY_OF_WEEK_IN_YEAR,  8},

            {2013,  1,  1, ALIGNED_WEEK_OF_MONTH, -1},
            {2013,  2,  1, ALIGNED_WEEK_OF_MONTH,  6},

            {2013,  1,  1, ALIGNED_WEEK_OF_YEAR, -1},
            {2015,  1,  1, ALIGNED_WEEK_OF_YEAR, 54},

            {2013,  1,  1, DAY_OF_WEEK, -1},
            {2013,  1,  1, DAY_OF_WEEK,  8},
            {2013,  1,  1, DAY_OF_MONTH, -1},
            {2013,  1,  1, DAY_OF_MONTH, 31},
            {2013,  6,  1, DAY_OF_MONTH, 31},
            {2013, 12,  1, DAY_OF_MONTH, 31},
            {2015, 12,  1, DAY_OF_MONTH, 38},

            {2013,  1,  1, DAY_OF_YEAR,  -1},
            {2013,  1,  1, DAY_OF_YEAR, 365},
            {2015,  1,  1, DAY_OF_YEAR, 372},

            {2013,  1,  1, MONTH_OF_YEAR, -1},
            {2013,  1,  1, MONTH_OF_YEAR, 14},
            {2013,  1,  1, MONTH_OF_YEAR, -2},
            {2015,  1,  1, MONTH_OF_YEAR, 14},

            {2013,  1,  1, EPOCH_DAY, -365_961_481},
            {2013,  1,  1, EPOCH_DAY,  364_523_156},
            {2013,  1,  1, YEAR, -1_000_001},
            {2013,  1,  1, YEAR,  1_000_001},
        };
    }

    @ParameterizedTest
    @MethodSource("data_with_bad")
    public void test_with_TemporalField_badValue(int year, int month, int dom, TemporalField field, long value) {
        assertThrows(DateTimeException.class, () -> Symmetry010Date.of(year, month, dom).with(field, value));
    }

    @Test
    public void test_with_TemporalField_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Symmetry010Date.of(2012, 6, 28).with(MINUTE_OF_DAY, 10));
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.with(TemporalAdjuster)
    //-----------------------------------------------------------------------
    public static Object[][] data_temporalAdjusters_lastDayOfMonth() {
        return new Object[][] {
            {2012, 1, 23, 2012, 1, 30},
            {2012, 2, 23, 2012, 2, 31},
            {2012, 3, 23, 2012, 3, 30},
            {2012, 4, 23, 2012, 4, 30},
            {2012, 5, 23, 2012, 5, 31},
            {2012, 6, 23, 2012, 6, 30},
            {2012, 7, 23, 2012, 7, 30},
            {2012, 8, 23, 2012, 8, 31},
            {2012, 9, 23, 2012, 9, 30},
            {2012, 10, 23, 2012,10, 30},
            {2012, 11, 23, 2012, 11, 31},
            {2012, 12, 23, 2012, 12, 30},
            {2009, 12, 23, 2009, 12, 37},
        };
    }

    @ParameterizedTest
    @MethodSource("data_temporalAdjusters_lastDayOfMonth")
    public void test_temporalAdjusters_LastDayOfMonth(int year, int month, int day, int expectedYear, int expectedMonth, int expectedDay) {
        Symmetry010Date base = Symmetry010Date.of(year, month, day);
        Symmetry010Date expected = Symmetry010Date.of(expectedYear, expectedMonth, expectedDay);
        Symmetry010Date actual = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(expected, actual);
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        Symmetry010Date sym010 = Symmetry010Date.of(2000, 1, 4);
        Symmetry010Date test = sym010.with(LocalDate.of(2012, 7, 6));
        assertEquals(Symmetry010Date.of(2012, 7, 5), test);
    }

    @Test
    public void test_adjust_toMonth() {
        Symmetry010Date sym010 = Symmetry010Date.of(2000, 1, 4);
        assertThrows(DateTimeException.class, () -> sym010.with(Month.APRIL));
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(Symmetry010Date)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToSymmetry010Date() {
        Symmetry010Date sym010 = Symmetry010Date.of(2012, 7, 19);
        LocalDate test = LocalDate.MIN.with(sym010);
        assertEquals(LocalDate.of(2012, 7, 20), test);
    }

    @Test
    public void test_LocalDateTime_adjustToSymmetry010Date() {
        Symmetry010Date sym010 = Symmetry010Date.of(2012, 7, 19);
        LocalDateTime test = LocalDateTime.MIN.with(sym010);
        assertEquals(LocalDateTime.of(2012, 7, 20, 0, 0), test);
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.plus
    // Symmetry010Date.minus
    //-----------------------------------------------------------------------
    public static Object[][] data_plus() {
        return new Object[][] {
            {2014, 5, 26, 0, DAYS, 2014, 5, 26},
            {2014, 5, 26, 8, DAYS, 2014, 6, 3},
            {2014, 5, 26, -3, DAYS, 2014, 5, 23},
            {2014, 5, 26, 0, WEEKS, 2014, 5, 26},
            {2014, 5, 26, 3, WEEKS, 2014, 6, 16},
            {2014, 5, 26, -5, WEEKS, 2014, 4, 21},
            {2014, 5, 26, 0, MONTHS, 2014, 5, 26},
            {2014, 5, 26, 3, MONTHS, 2014, 8, 26},
            {2014, 5, 26, -5, MONTHS, 2013, 12, 26},
            {2014, 5, 26, 0, YEARS, 2014, 5, 26},
            {2014, 5, 26, 3, YEARS, 2017, 5, 26},
            {2014, 5, 26, -5, YEARS, 2009, 5, 26},
            {2014, 5, 26, 0, DECADES, 2014, 5, 26},
            {2014, 5, 26, 3, DECADES, 2044, 5, 26},
            {2014, 5, 26, -5, DECADES, 1964, 5, 26},
            {2014, 5, 26, 0, CENTURIES, 2014, 5, 26},
            {2014, 5, 26, 3, CENTURIES, 2314, 5, 26},
            {2014, 5, 26, -5, CENTURIES, 1514, 5, 26},
            {2014, 5, 26, 0, MILLENNIA, 2014, 5, 26},
            {2014, 5, 26, 3, MILLENNIA, 5014, 5, 26},
            {2014, 5, 26, -1, MILLENNIA, 2014 - 1000, 5, 26},

            {2014, 12, 26, 3, WEEKS, 2015, 1, 17},
            {2014, 1, 26, -5, WEEKS, 2013, 12, 21},

            {2012, 6, 26, 3, WEEKS, 2012, 7, 17},
            {2012, 7, 26, -5, WEEKS, 2012, 6, 21},

            {2012, 6, 21, 52 + 1, WEEKS, 2013, 6, 28},
            {2013, 6, 21, 6 * 52 + 1, WEEKS, 2019, 6, 21},
        };
    }

    public static Object[][] data_plus_leapWeek() {
        return new Object[][] {
            {2015, 12, 28, 0, DAYS, 2015, 12, 28},
            {2015, 12, 28, 8, DAYS, 2015, 12, 36},
            {2015, 12, 28, -3, DAYS, 2015, 12, 25},
            {2015, 12, 28, 0, WEEKS, 2015, 12, 28},
            {2015, 12, 28, 3, WEEKS, 2016,  1, 12},
            {2015, 12, 28, -5, WEEKS, 2015, 11, 24},
            {2015, 12, 28, 52, WEEKS, 2016, 12, 21},
            {2015, 12, 28, 0, MONTHS, 2015, 12, 28},
            {2015, 12, 28, 3, MONTHS, 2016,  3, 28},
            {2015, 12, 28, -5, MONTHS, 2015,  7, 28},
            {2015, 12, 28, 12, MONTHS, 2016, 12, 28},
            {2015, 12, 28, 0, YEARS, 2015, 12, 28},
            {2015, 12, 28, 3, YEARS, 2018, 12, 28},
            {2015, 12, 28, -5, YEARS, 2010, 12, 28},
        };
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_plus_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(Symmetry010Date.of(expectedYear, expectedMonth, expectedDom), Symmetry010Date.of(year, month, dom).plus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_plus_leapWeek")
    public void test_plus_leapWeek_TemporalUnit(int year, int month, int dom,
            long amount, TemporalUnit unit,
            int expectedYear, int expectedMonth, int expectedDom) {
        assertEquals(Symmetry010Date.of(expectedYear, expectedMonth, expectedDom), Symmetry010Date.of(year, month, dom).plus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_plus")
    public void test_minus_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(Symmetry010Date.of(expectedYear, expectedMonth, expectedDom), Symmetry010Date.of(year, month, dom).minus(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("data_plus_leapWeek")
    public void test_minus_leapWeek_TemporalUnit(
            int expectedYear, int expectedMonth, int expectedDom,
            long amount, TemporalUnit unit,
            int year, int month, int dom) {
        assertEquals(Symmetry010Date.of(expectedYear, expectedMonth, expectedDom), Symmetry010Date.of(year, month, dom).minus(amount, unit));
    }

    @Test
    public void test_plus_TemporalUnit_unsupported() {
        assertThrows(UnsupportedTemporalTypeException.class, () -> Symmetry010Date.of(2012, 6, 28).plus(0, MINUTES));
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.until
    //-----------------------------------------------------------------------
    public static Object[][] data_until() {
        return new Object[][] {
            {2014, 5, 26, 2014, 5, 26, DAYS, 0},
            {2014, 5, 26, 2014, 6,  4, DAYS, 9},
            {2014, 5, 26, 2014, 5, 20, DAYS, -6},
            {2014, 5, 26, 2014, 5, 26, WEEKS, 0},
            {2014, 5, 26, 2014, 6,  1, WEEKS, 1},
            {2014, 5, 26, 2014, 6,  5, WEEKS, 1},
            {2014, 5, 26, 2014, 5, 26, MONTHS, 0},
            {2014, 5, 26, 2014, 6, 25, MONTHS, 0},
            {2014, 5, 26, 2014, 6, 26, MONTHS, 1},
            {2014, 5, 26, 2014, 5, 26, YEARS, 0},
            {2014, 5, 26, 2015, 5, 25, YEARS, 0},
            {2014, 5, 26, 2015, 5, 26, YEARS, 1},
            {2014, 5, 26, 2014, 5, 26, DECADES, 0},
            {2014, 5, 26, 2024, 5, 25, DECADES, 0},
            {2014, 5, 26, 2024, 5, 26, DECADES, 1},
            {2014, 5, 26, 2014, 5, 26, CENTURIES, 0},
            {2014, 5, 26, 2114, 5, 25, CENTURIES, 0},
            {2014, 5, 26, 2114, 5, 26, CENTURIES, 1},
            {2014, 5, 26, 2014, 5, 26, MILLENNIA, 0},
            {2014, 5, 26, 3014, 5, 25, MILLENNIA, 0},
            {2014, 5, 26, 3014, 5, 26, MILLENNIA, 1},
            {2014, 5, 26, 3014, 5, 26, ERAS, 0},
        };
    }

    public static Object[][] data_until_period() {
        return new Object[][] {
            {2014, 5, 26, 2014, 5, 26, 0,  0,  0},
            {2014, 5, 26, 2014, 6,  4, 0,  0,  9},
            {2014, 5, 26, 2014, 5, 20, 0,  0, -6},
            {2014, 5, 26, 2014, 6,  5, 0,  0, 10},
            {2014, 5, 26, 2014, 6, 25, 0,  0, 30},
            {2014, 5, 26, 2014, 6, 26, 0,  1,  0},
            {2014, 5, 26, 2015, 5, 25, 0, 11, 29},
            {2014, 5, 26, 2015, 5, 26, 1,  0,  0},
            {2014, 5, 26, 2024, 5, 25, 9, 11, 29},
        };
    }

    @ParameterizedTest
    @MethodSource("data_until")
    public void test_until_TemporalUnit(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            TemporalUnit unit, long expected) {
        Symmetry010Date start = Symmetry010Date.of(year1, month1, dom1);
        Symmetry010Date end = Symmetry010Date.of(year2, month2, dom2);
        assertEquals(expected, start.until(end, unit));
    }

    @ParameterizedTest
    @MethodSource("data_until_period")
    public void test_until_end(
            int year1, int month1, int dom1,
            int year2, int month2, int dom2,
            int yearPeriod, int monthPeriod, int dayPeriod) {
        Symmetry010Date start = Symmetry010Date.of(year1, month1, dom1);
        Symmetry010Date end = Symmetry010Date.of(year2, month2, dom2);
        ChronoPeriod period = Symmetry010Chronology.INSTANCE.period(yearPeriod, monthPeriod, dayPeriod);
        assertEquals(period, start.until(end));
    }

    @Test
    public void test_until_TemporalUnit_unsupported() {
        Symmetry010Date start = Symmetry010Date.of(2012, 6, 28);
        Symmetry010Date end = Symmetry010Date.of(2012, 7, 1);
        assertThrows(UnsupportedTemporalTypeException.class, () -> start.until(end, MINUTES));
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.period
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Period() {
        assertEquals(Symmetry010Date.of(2014, 7, 29),
                Symmetry010Date.of(2014, 5, 21).plus(Symmetry010Chronology.INSTANCE.period(0, 2, 8)));
    }

    @Test
    public void test_plus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> Symmetry010Date.of(2014, 5, 26).plus(Period.ofMonths(2)));
    }

    @Test
    public void test_minus_Period() {
        assertEquals(Symmetry010Date.of(2014, 3, 23),
                Symmetry010Date.of(2014, 5, 26).minus(Symmetry010Chronology.INSTANCE.period(0, 2, 3)));
    }

    @Test
    public void test_minus_Period_ISO() {
        assertThrows(DateTimeException.class, () -> Symmetry010Date.of(2014, 5, 26).minus(Period.ofMonths(2)));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_and_hashCode() {
        new EqualsTester()
            .addEqualityGroup(Symmetry010Date.of(2000,  1,  3), Symmetry010Date.of(2000,  1,  3))
            .addEqualityGroup(Symmetry010Date.of(2000,  1,  4), Symmetry010Date.of(2000,  1,  4))
            .addEqualityGroup(Symmetry010Date.of(2000,  2,  3), Symmetry010Date.of(2000,  2,  3))
            .addEqualityGroup(Symmetry010Date.of(2000,  6, 23), Symmetry010Date.of(2000,  6, 23))
            .addEqualityGroup(Symmetry010Date.of(2000,  6, 28), Symmetry010Date.of(2000,  6, 28))
            .addEqualityGroup(Symmetry010Date.of(2000,  7,  1), Symmetry010Date.of(2000,  7,  1))
            .addEqualityGroup(Symmetry010Date.of(2000, 12, 25), Symmetry010Date.of(2000, 12, 25))
            .addEqualityGroup(Symmetry010Date.of(2000, 12, 28), Symmetry010Date.of(2000, 12, 28))
            .addEqualityGroup(Symmetry010Date.of(2001,  1,  1), Symmetry010Date.of(2001,  1,  1))
            .addEqualityGroup(Symmetry010Date.of(2001,  1,  3), Symmetry010Date.of(2001,  1,  3))
            .addEqualityGroup(Symmetry010Date.of(2001, 12, 28), Symmetry010Date.of(2001, 12, 28))
            .addEqualityGroup(Symmetry010Date.of(2004,  6, 28), Symmetry010Date.of(2004,  6, 28))
            .testEquals();
    }

    //-----------------------------------------------------------------------
    // Symmetry010Date.toString
    //-----------------------------------------------------------------------
    public static Object[][] data_toString() {
        return new Object[][] {
            {Symmetry010Date.of(   1,  1,  1), "Sym010 CE 1/01/01"},
            {Symmetry010Date.of(1970,  2, 31), "Sym010 CE 1970/02/31"},
            {Symmetry010Date.of(2000,  8, 31), "Sym010 CE 2000/08/31"},
            {Symmetry010Date.of(2009, 12, 37), "Sym010 CE 2009/12/37"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_toString")
    public void test_toString(Symmetry010Date date, String expected) {
        assertEquals(expected, date.toString());
    }
}
