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

import static java.time.temporal.ChronoUnit.DAYS;
import static org.testng.Assert.assertEquals;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.Chronology;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestDiscordianChronology {

    //-----------------------------------------------------------------------
    // Chronology.of(String)
    //-----------------------------------------------------------------------
    @Test
    public void test_chronology_of_name() {
        Chronology chrono = Chronology.of("Discordian");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, DiscordianChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Discordian");
        Assert.assertEquals(chrono.getCalendarType(), "discordian");
    }

    @Test
    public void test_chronology_of_name_id() {
        Chronology chrono = Chronology.of("discordian");
        Assert.assertNotNull(chrono);
        Assert.assertEquals(chrono, DiscordianChronology.INSTANCE);
        Assert.assertEquals(chrono.getId(), "Discordian");
        Assert.assertEquals(chrono.getCalendarType(), "discordian");
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
        return new Object[][] {
            {DiscordianDate.of(1167, 1, 1), LocalDate.of(1, 1, 1)},
            {DiscordianDate.of(1167, 1, 2), LocalDate.of(1, 1, 2)},
            {DiscordianDate.of(1167, 1, 3), LocalDate.of(1, 1, 3)},

            {DiscordianDate.of(1167, 1, 57), LocalDate.of(1, 2, 26)},
            {DiscordianDate.of(1167, 1, 58), LocalDate.of(1, 2, 27)},
            {DiscordianDate.of(1167, 1, 59), LocalDate.of(1, 2, 28)},
            {DiscordianDate.of(1167, 1, 60), LocalDate.of(1, 3, 1)},

            {DiscordianDate.of(1170, 1, 57), LocalDate.of(4, 2, 26)},
            {DiscordianDate.of(1170, 1, 58), LocalDate.of(4, 2, 27)},
            {DiscordianDate.of(1170, 1, 59), LocalDate.of(4, 2, 28)},
            {DiscordianDate.of(1170, 0, 0), LocalDate.of(4, 2, 29)},
            {DiscordianDate.of(1170, 1, 60), LocalDate.of(4, 3, 1)},

            {DiscordianDate.of(1266, 1, 57), LocalDate.of(100, 2, 26)},
            {DiscordianDate.of(1266, 1, 58), LocalDate.of(100, 2, 27)},
            {DiscordianDate.of(1266, 1, 59), LocalDate.of(100, 2, 28)},
            {DiscordianDate.of(1266, 1, 60), LocalDate.of(100, 3, 1)},
            {DiscordianDate.of(1266, 1, 61), LocalDate.of(100, 3, 2)},

            {DiscordianDate.of(1166, 5, 73), LocalDate.of(0, 12, 31)},
            {DiscordianDate.of(1166, 5, 72), LocalDate.of(0, 12, 30)},

            {DiscordianDate.of(2748, 4, 68), LocalDate.of(1582, 10, 14)},
            {DiscordianDate.of(2748, 4, 69), LocalDate.of(1582, 10, 15)},
            {DiscordianDate.of(3111, 5, 24), LocalDate.of(1945, 11, 12)},

            {DiscordianDate.of(3178, 3, 40), LocalDate.of(2012, 7, 5)},
            {DiscordianDate.of(3178, 3, 41), LocalDate.of(2012, 7, 6)},
        };
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_from_DiscordianDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(LocalDate.from(discordian), iso);
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_from_LocalDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(DiscordianDate.from(iso), discordian);
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_chronology_dateEpochDay(DiscordianDate discordian, LocalDate iso) {
        assertEquals(DiscordianChronology.INSTANCE.dateEpochDay(iso.toEpochDay()), discordian);
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_toEpochDay(DiscordianDate discordian, LocalDate iso) {
        assertEquals(discordian.toEpochDay(), iso.toEpochDay());
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_until_DiscordianDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(discordian.until(discordian), DiscordianChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_DiscordianDate_until_LocalDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(discordian.until(iso), DiscordianChronology.INSTANCE.period(0, 0, 0));
    }

    @Test(dataProvider = "samples")
    public void test_LocalDate_until_DiscordianDate(DiscordianDate discordian, LocalDate iso) {
        assertEquals(iso.until(discordian), Period.ZERO);
    }

    @Test(dataProvider = "samples")
    public void test_Chronology_date_Temporal(DiscordianDate discordian, LocalDate iso) {
        assertEquals(DiscordianChronology.INSTANCE.date(iso), discordian);
    }

    @Test(dataProvider = "samples")
    public void test_plusDays(DiscordianDate discordian, LocalDate iso) {
        assertEquals(LocalDate.from(discordian.plus(0, DAYS)), iso);
        assertEquals(LocalDate.from(discordian.plus(1, DAYS)), iso.plusDays(1));
        assertEquals(LocalDate.from(discordian.plus(35, DAYS)), iso.plusDays(35));
        assertEquals(LocalDate.from(discordian.plus(-1, DAYS)), iso.plusDays(-1));
        assertEquals(LocalDate.from(discordian.plus(-60, DAYS)), iso.plusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_minusDays(DiscordianDate discordian, LocalDate iso) {
        assertEquals(LocalDate.from(discordian.minus(0, DAYS)), iso);
        assertEquals(LocalDate.from(discordian.minus(1, DAYS)), iso.minusDays(1));
        assertEquals(LocalDate.from(discordian.minus(35, DAYS)), iso.minusDays(35));
        assertEquals(LocalDate.from(discordian.minus(-1, DAYS)), iso.minusDays(-1));
        assertEquals(LocalDate.from(discordian.minus(-60, DAYS)), iso.minusDays(-60));
    }

    @Test(dataProvider = "samples")
    public void test_until_DAYS(DiscordianDate discordian, LocalDate iso) {
        assertEquals(discordian.until(iso.plusDays(0), DAYS), 0);
        assertEquals(discordian.until(iso.plusDays(1), DAYS), 1);
        assertEquals(discordian.until(iso.plusDays(35), DAYS), 35);
        assertEquals(discordian.until(iso.minusDays(40), DAYS), -40);
    }

    @DataProvider(name = "badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {1900, 0, 0},

            {1900, -1, 1},
            {1900, 0, 1},
            {1900, 6, 1},
            {1900, 7, 1},

            {1900, 1, -1},
            {1900, 1, 0},
            {1900, 1, 74},

            {1900, 0, 0},

            {1900, 5, -1},
            {1900, 5, 0},
            {1900, 5, 74},

            {1900, 2, 74},
            {1900, 3, 74},
            {1900, 4, 74},
        };
    }

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates(int year, int month, int dom) {
        DiscordianDate.of(year, month, dom);
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_chronology_dateYearDay_badDate() {
        DiscordianChronology.INSTANCE.dateYearDay(2001, 366);
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLeapYear_loop() {
        Predicate<Integer> isLeapYear = year -> {
            int offsetYear = year - 1166;
            return offsetYear % 4 == 0 && (offsetYear % 400 == 0 || offsetYear % 100 != 0);
        };
        for (int year = 1066; year < 1567; year++) {
            DiscordianDate base = DiscordianDate.of(year, 1, 1);
            assertEquals(base.isLeapYear(), isLeapYear.test(year), "Mismatch on " + year);
            assertEquals(DiscordianChronology.INSTANCE.isLeapYear(year), isLeapYear.test(year), "Mismatch on " + year);
        }
    }

    @Test
    public void test_isLeapYear_specific() {
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1174), true);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1173), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1172), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1171), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1170), true);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1169), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1168), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1167), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1166), true);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1165), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1164), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1163), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1162), true);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1161), false);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(1160), false);
    }

    @DataProvider(name = "lengthOfMonth")
    Object[][] data_lengthOfMonth() {
        return new Object[][] {
            {1900, 1, 73},
            {1900, 2, 73},
            {1900, 3, 73},
            {1900, 4, 73},
            {1900, 5, 73},

            {1901, 1, 73},
            {1902, 1, 73},
            {1903, 1, 73},
            {1904, 1, 73},
            {1966, 1, 73},
            {2066, 1, 73},
        };
    }

    @Test(dataProvider = "lengthOfMonth")
    public void test_lengthOfMonth(int year, int month, int length) {
        assertEquals(DiscordianDate.of(year, month, 1).lengthOfMonth(), length);
    }
}
