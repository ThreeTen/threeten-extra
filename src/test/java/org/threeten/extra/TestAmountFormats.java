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

import java.time.Duration;
import java.time.Period;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Test AmountFormats.
 */
@RunWith(DataProviderRunner.class)
public class TestAmountFormats {

    private static final Locale PL = new Locale("pl");
    private static final Locale RU = new Locale("ru");

    //-----------------------------------------------------------------------
    @Test
    public void test_iso8601() {
        assertEquals("P12M6DT8H30M", AmountFormats.iso8601(Period.of(0, 12, 6), Duration.ofMinutes(8 * 60 + 30)));
        assertEquals("PT8H30M", AmountFormats.iso8601(Period.ZERO, Duration.ofMinutes(8 * 60 + 30)));
        assertEquals("P12M6D", AmountFormats.iso8601(Period.of(0, 12, 6), Duration.ZERO));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_wordBased() {
        return new Object[][] {
            {Period.ofYears(0), Locale.ROOT, "0 days"},
            {Period.ofYears(1), Locale.ROOT, "1 year"},
            {Period.ofYears(2), Locale.ROOT, "2 years"},
            {Period.ofYears(12), Locale.ROOT, "12 years"},
            {Period.ofYears(-1), Locale.ROOT, "-1 year"},

            {Period.ofWeeks(0), Locale.ENGLISH, "0 days"},
            {Period.ofWeeks(1), Locale.ENGLISH, "1 week"},
            {Period.ofWeeks(4), Locale.ENGLISH, "4 weeks"},

            {Period.ofMonths(0), Locale.ENGLISH, "0 days"},
            {Period.ofMonths(1), Locale.ENGLISH, "1 month"},
            {Period.ofMonths(4), Locale.ENGLISH, "4 months"},
            {Period.ofMonths(14), Locale.ENGLISH, "1 year and 2 months"},

            {Period.ofDays(1), Locale.ENGLISH, "1 day"},
            {Period.ofDays(2), Locale.ENGLISH, "2 days"},
            {Period.ofDays(5), Locale.ENGLISH, "5 days"},
            {Period.ofDays(7), Locale.ENGLISH, "1 week"},
            {Period.ofDays(-1), Locale.ENGLISH, "-1 day"},

            {Period.ofDays(1), new Locale("ro"), "1 zi"},
            {Period.ofDays(2), new Locale("ro"), "2 zile"},
            {Period.ofDays(5), new Locale("ro"), "5 zile"},
            {Period.ofDays(7), new Locale("ro"), "1 săptămână"},
            {Period.ofWeeks(3), new Locale("ro"), "3 săptămâni"},
            {Period.ofMonths(14), new Locale("ro"), "1 an și 2 luni"},
            {Period.ofMonths(1), new Locale("ro"), "1 lună"},
            {Period.ofYears(2), new Locale("ro"), "2 ani"},
        };
    }

    @Test
    @UseDataProvider("data_wordBased")
    public void test_wordBased(Period period, Locale locale, String expected) {
        assertEquals(expected, AmountFormats.wordBased(period, locale));
    }

    @DataProvider
    public static Object[][] duration_wordBased() {
        return new Object[][] {
            {Duration.ofMinutes(180 + 2), Locale.ENGLISH, "3 hours and 2 minutes"},
            {Duration.ofMinutes(-60 - 40), Locale.ENGLISH, "-1 hour and -40 minutes"},
            {Duration.ofSeconds(180), Locale.ENGLISH, "3 minutes"},
            {Duration.ofSeconds(100), Locale.ENGLISH, "1 minute and 40 seconds"},
            {Duration.ofSeconds(-140), Locale.ENGLISH, "-2 minutes and -20 seconds"},
            {Duration.ofSeconds(-90), Locale.ENGLISH, "-1 minute and -30 seconds"},
            {Duration.ofSeconds(-40), Locale.ENGLISH, "-40 seconds"},
            {Duration.ofMillis(1_000), Locale.ENGLISH, "1 second"},
            {Duration.ofMillis(3_000), Locale.ENGLISH, "3 seconds"},
            {Duration.ofNanos(1_000_000), Locale.ENGLISH, "1 millisecond"},
            {Duration.ofNanos(1000_000_000 + 2_000_000), Locale.ENGLISH, "1 second and 2 milliseconds"},

            {Duration.ofMinutes(60 + 1), new Locale("ro"), "1 oră și 1 minut"},
            {Duration.ofMinutes(180 + 2), new Locale("ro"), "3 ore și 2 minute"},
            {Duration.ofMinutes(-60 - 40), new Locale("ro"), "-1 oră și -40 minute"},
            {Duration.ofSeconds(-90), new Locale("ro"), "-1 minut și -30 secunde"},
            {Duration.ofNanos(1_000_000), new Locale("ro"), "1 milisecundă"},
            {Duration.ofNanos(1000_000_000 + 2_000_000), new Locale("ro"), "1 secundă și 2 milisecunde"},

            {Duration.ofHours(5).plusMinutes(6).plusSeconds(7).plusNanos(8_000_000L), PL,
                "5 godzin, 6 minut, 7 sekund i 8 milisekund"},
        };
    }

    @Test
    @UseDataProvider("duration_wordBased")
    public void test_wordBased(Duration duration, Locale locale, String expected) {
        assertEquals(expected, AmountFormats.wordBased(duration, locale));
    }

    @DataProvider
    public static Object[][] period_duration_wordBased() {
        return new Object[][] {
            {Period.ofDays(1), Duration.ofMinutes(180 + 2), Locale.ROOT, "1 day, 3 hours and 2 minutes"},
            {Period.ofDays(2), Duration.ofSeconds(180), Locale.ROOT, "2 days and 3 minutes"},
            {Period.ofDays(7), Duration.ofMinutes(80), Locale.ROOT, "1 week, 1 hour and 20 minutes"},
            {Period.ZERO, Duration.ofMillis(1_000), Locale.ROOT, "1 second"},

            {Period.ofMonths(0), Duration.ofSeconds(0), Locale.ENGLISH, "0 milliseconds"},
            {Period.ofMonths(0), Duration.ofHours(9), Locale.ENGLISH, "9 hours"},
            {Period.ofMonths(1), Duration.ZERO, Locale.ENGLISH, "1 month"},
            {Period.ofMonths(4), Duration.ZERO, Locale.ENGLISH, "4 months"},
            {Period.of(1, 2, 5), Duration.ofHours(4), Locale.ENGLISH, "1 year, 2 months, 5 days and 4 hours"},
            {Period.ofDays(5), Duration.ofDays(2).plusHours(6), Locale.ENGLISH, "7 days and 6 hours"},
            {Period.ofDays(5), Duration.ofDays(-2).plusHours(-6), Locale.ENGLISH, "3 days and -6 hours"},

            {Period.ofDays(1), Duration.ofHours(5).plusMinutes(6).plusSeconds(7).plusNanos(8_000_000L), PL,
                "1 dzie\u0144, 5 godzin, 6 minut, 7 sekund i 8 milisekund"},
        };
    }

    @Test
    @UseDataProvider("period_duration_wordBased")
    public void test_wordBased(Period period, Duration duration, Locale locale, String expected) {
        assertEquals(expected, AmountFormats.wordBased(period, duration, locale));
    }

    //-----------------------------------------------------------------------
    public void test_wordBased_pl_formatStandard() {
        Duration p = Duration.ofDays(1).plusHours(5).plusMinutes(6).plusSeconds(7).plusNanos(8_000_000L);
        assertEquals("1 dzie\u0144, 5 godzin, 6 minut, 7 sekund i 8 milisekund", AmountFormats.wordBased(p, PL));
    }

    public void test_wordBased_pl_predicatex() {
        assertEquals("1 rok", AmountFormats.wordBased(Period.ofYears(1), PL));
        assertEquals("2 lata", AmountFormats.wordBased(Period.ofYears(2), PL));
        assertEquals("5 lat", AmountFormats.wordBased(Period.ofYears(5), PL));
        assertEquals("12 lat", AmountFormats.wordBased(Period.ofYears(12), PL));
        assertEquals("15 lat", AmountFormats.wordBased(Period.ofYears(15), PL));
        assertEquals("1112 lat", AmountFormats.wordBased(Period.ofYears(1112), PL));
        assertEquals("1115 lat", AmountFormats.wordBased(Period.ofYears(1115), PL));
        assertEquals("2112 lat", AmountFormats.wordBased(Period.ofYears(2112), PL));
        assertEquals("2115 lat", AmountFormats.wordBased(Period.ofYears(2115), PL));
        assertEquals("2212 lat", AmountFormats.wordBased(Period.ofYears(2212), PL));
        assertEquals("2215 lat", AmountFormats.wordBased(Period.ofYears(2215), PL));
        assertEquals("22 lata", AmountFormats.wordBased(Period.ofYears(22), PL));
        assertEquals("25 lat", AmountFormats.wordBased(Period.ofYears(25), PL));
        assertEquals("1122 lata", AmountFormats.wordBased(Period.ofYears(1122), PL));
        assertEquals("1125 lat", AmountFormats.wordBased(Period.ofYears(1125), PL));
        assertEquals("2122 lata", AmountFormats.wordBased(Period.ofYears(2122), PL));
        assertEquals("2125 lat", AmountFormats.wordBased(Period.ofYears(2125), PL));
        assertEquals("2222 lata", AmountFormats.wordBased(Period.ofYears(2222), PL));
        assertEquals("2225 lat", AmountFormats.wordBased(Period.ofYears(2225), PL));

        assertEquals("1 miesi\u0105c", AmountFormats.wordBased(Period.ofMonths(1), PL));
        assertEquals("2 miesi\u0105ce", AmountFormats.wordBased(Period.ofMonths(2), PL));
        assertEquals("5 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(5), PL));
        assertEquals("12 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(12), PL));
        assertEquals("15 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(15), PL));
        assertEquals("1112 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(1112), PL));
        assertEquals("1115 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(1115), PL));
        assertEquals("2112 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(2112), PL));
        assertEquals("2115 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(2115), PL));
        assertEquals("2212 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(2212), PL));
        assertEquals("2215 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(2215), PL));
        assertEquals("22 miesi\u0105ce", AmountFormats.wordBased(Period.ofMonths(22), PL));
        assertEquals("25 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(25), PL));
        assertEquals("1122 miesi\u0105ce", AmountFormats.wordBased(Period.ofMonths(1122), PL));
        assertEquals("1125 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(1125), PL));
        assertEquals("2122 miesi\u0105ce", AmountFormats.wordBased(Period.ofMonths(2122), PL));
        assertEquals("2125 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(2125), PL));
        assertEquals("2222 miesi\u0105ce", AmountFormats.wordBased(Period.ofMonths(2222), PL));
        assertEquals("2225 miesi\u0119cy", AmountFormats.wordBased(Period.ofMonths(2225), PL));

        assertEquals("1 tydzie\u0144", AmountFormats.wordBased(Period.ofWeeks(1), PL));
        assertEquals("2 tygodnie", AmountFormats.wordBased(Period.ofWeeks(2), PL));
        assertEquals("5 tygodni", AmountFormats.wordBased(Period.ofWeeks(5), PL));
        assertEquals("12 tygodni", AmountFormats.wordBased(Period.ofWeeks(12), PL));
        assertEquals("15 tygodni", AmountFormats.wordBased(Period.ofWeeks(15), PL));
        assertEquals("1112 tygodni", AmountFormats.wordBased(Period.ofWeeks(1112), PL));
        assertEquals("1115 tygodni", AmountFormats.wordBased(Period.ofWeeks(1115), PL));
        assertEquals("2112 tygodni", AmountFormats.wordBased(Period.ofWeeks(2112), PL));
        assertEquals("2115 tygodni", AmountFormats.wordBased(Period.ofWeeks(2115), PL));
        assertEquals("2212 tygodni", AmountFormats.wordBased(Period.ofWeeks(2212), PL));
        assertEquals("2215 tygodni", AmountFormats.wordBased(Period.ofWeeks(2215), PL));
        assertEquals("22 tygodnie", AmountFormats.wordBased(Period.ofWeeks(22), PL));
        assertEquals("25 tygodni", AmountFormats.wordBased(Period.ofWeeks(25), PL));
        assertEquals("1122 tygodnie", AmountFormats.wordBased(Period.ofWeeks(1122), PL));
        assertEquals("1125 tygodni", AmountFormats.wordBased(Period.ofWeeks(1125), PL));
        assertEquals("2122 tygodnie", AmountFormats.wordBased(Period.ofWeeks(2122), PL));
        assertEquals("2125 tygodni", AmountFormats.wordBased(Period.ofWeeks(2125), PL));
        assertEquals("2222 tygodnie", AmountFormats.wordBased(Period.ofWeeks(2222), PL));
        assertEquals("2225 tygodni", AmountFormats.wordBased(Period.ofWeeks(2225), PL));

        assertEquals("1 dzie\u0144", AmountFormats.wordBased(Period.ofDays(1), PL));
        assertEquals("2 dni", AmountFormats.wordBased(Period.ofDays(2), PL));
        assertEquals("5 dni", AmountFormats.wordBased(Period.ofDays(5), PL));
        assertEquals("12 dni", AmountFormats.wordBased(Period.ofDays(12), PL));
        assertEquals("15 dni", AmountFormats.wordBased(Period.ofDays(15), PL));
        assertEquals("22 dni", AmountFormats.wordBased(Period.ofDays(22), PL));
        assertEquals("25 dni", AmountFormats.wordBased(Period.ofDays(25), PL));

        assertEquals("1 godzina", AmountFormats.wordBased(Duration.ofHours(1), PL));
        assertEquals("2 godziny", AmountFormats.wordBased(Duration.ofHours(2), PL));
        assertEquals("5 godzin", AmountFormats.wordBased(Duration.ofHours(5), PL));
        assertEquals("12 godzin", AmountFormats.wordBased(Duration.ofHours(12), PL));
        assertEquals("15 godzin", AmountFormats.wordBased(Duration.ofHours(15), PL));
        assertEquals("1112 godzin", AmountFormats.wordBased(Duration.ofHours(1112), PL));
        assertEquals("1115 godzin", AmountFormats.wordBased(Duration.ofHours(1115), PL));
        assertEquals("2112 godzin", AmountFormats.wordBased(Duration.ofHours(2112), PL));
        assertEquals("2115 godzin", AmountFormats.wordBased(Duration.ofHours(2115), PL));
        assertEquals("2212 godzin", AmountFormats.wordBased(Duration.ofHours(2212), PL));
        assertEquals("2215 godzin", AmountFormats.wordBased(Duration.ofHours(2215), PL));
        assertEquals("22 godziny", AmountFormats.wordBased(Duration.ofHours(22), PL));
        assertEquals("25 godzin", AmountFormats.wordBased(Duration.ofHours(25), PL));
        assertEquals("1122 godziny", AmountFormats.wordBased(Duration.ofHours(1122), PL));
        assertEquals("1125 godzin", AmountFormats.wordBased(Duration.ofHours(1125), PL));
        assertEquals("2122 godziny", AmountFormats.wordBased(Duration.ofHours(2122), PL));
        assertEquals("2125 godzin", AmountFormats.wordBased(Duration.ofHours(2125), PL));
        assertEquals("2222 godziny", AmountFormats.wordBased(Duration.ofHours(2222), PL));
        assertEquals("2225 godzin", AmountFormats.wordBased(Duration.ofHours(2225), PL));

        assertEquals("1 minuta", AmountFormats.wordBased(Duration.ofMinutes(1), PL));
        assertEquals("2 minuty", AmountFormats.wordBased(Duration.ofMinutes(2), PL));
        assertEquals("5 minut", AmountFormats.wordBased(Duration.ofMinutes(5), PL));
        assertEquals("12 minut", AmountFormats.wordBased(Duration.ofMinutes(12), PL));
        assertEquals("15 minut", AmountFormats.wordBased(Duration.ofMinutes(15), PL));
        assertEquals("1112 minut", AmountFormats.wordBased(Duration.ofMinutes(1112), PL));
        assertEquals("1115 minut", AmountFormats.wordBased(Duration.ofMinutes(1115), PL));
        assertEquals("2112 minut", AmountFormats.wordBased(Duration.ofMinutes(2112), PL));
        assertEquals("2115 minut", AmountFormats.wordBased(Duration.ofMinutes(2115), PL));
        assertEquals("2212 minut", AmountFormats.wordBased(Duration.ofMinutes(2212), PL));
        assertEquals("2215 minut", AmountFormats.wordBased(Duration.ofMinutes(2215), PL));
        assertEquals("22 minuty", AmountFormats.wordBased(Duration.ofMinutes(22), PL));
        assertEquals("25 minut", AmountFormats.wordBased(Duration.ofMinutes(25), PL));
        assertEquals("1122 minuty", AmountFormats.wordBased(Duration.ofMinutes(1122), PL));
        assertEquals("1125 minut", AmountFormats.wordBased(Duration.ofMinutes(1125), PL));
        assertEquals("2122 minuty", AmountFormats.wordBased(Duration.ofMinutes(2122), PL));
        assertEquals("2125 minut", AmountFormats.wordBased(Duration.ofMinutes(2125), PL));
        assertEquals("2222 minuty", AmountFormats.wordBased(Duration.ofMinutes(2222), PL));
        assertEquals("2225 minut", AmountFormats.wordBased(Duration.ofMinutes(2225), PL));

        assertEquals("1 sekunda", AmountFormats.wordBased(Duration.ofSeconds(1), PL));
        assertEquals("2 sekundy", AmountFormats.wordBased(Duration.ofSeconds(2), PL));
        assertEquals("5 sekund", AmountFormats.wordBased(Duration.ofSeconds(5), PL));
        assertEquals("12 sekund", AmountFormats.wordBased(Duration.ofSeconds(12), PL));
        assertEquals("15 sekund", AmountFormats.wordBased(Duration.ofSeconds(15), PL));
        assertEquals("1112 sekund", AmountFormats.wordBased(Duration.ofSeconds(1112), PL));
        assertEquals("1115 sekund", AmountFormats.wordBased(Duration.ofSeconds(1115), PL));
        assertEquals("2112 sekund", AmountFormats.wordBased(Duration.ofSeconds(2112), PL));
        assertEquals("2115 sekund", AmountFormats.wordBased(Duration.ofSeconds(2115), PL));
        assertEquals("2212 sekund", AmountFormats.wordBased(Duration.ofSeconds(2212), PL));
        assertEquals("2215 sekund", AmountFormats.wordBased(Duration.ofSeconds(2215), PL));
        assertEquals("22 sekundy", AmountFormats.wordBased(Duration.ofSeconds(22), PL));
        assertEquals("25 sekund", AmountFormats.wordBased(Duration.ofSeconds(25), PL));
        assertEquals("1122 sekundy", AmountFormats.wordBased(Duration.ofSeconds(1122), PL));
        assertEquals("1125 sekund", AmountFormats.wordBased(Duration.ofSeconds(1125), PL));
        assertEquals("2122 sekundy", AmountFormats.wordBased(Duration.ofSeconds(2122), PL));
        assertEquals("2125 sekund", AmountFormats.wordBased(Duration.ofSeconds(2125), PL));
        assertEquals("2222 sekundy", AmountFormats.wordBased(Duration.ofSeconds(2222), PL));
        assertEquals("2225 sekund", AmountFormats.wordBased(Duration.ofSeconds(2225), PL));

        assertEquals("1 milisekunda", AmountFormats.wordBased(Duration.ofMillis(1), PL));
        assertEquals("2 milisekundy", AmountFormats.wordBased(Duration.ofMillis(2), PL));
        assertEquals("5 milisekund", AmountFormats.wordBased(Duration.ofMillis(5), PL));
        assertEquals("12 milisekund", AmountFormats.wordBased(Duration.ofMillis(12), PL));
        assertEquals("15 milisekund", AmountFormats.wordBased(Duration.ofMillis(15), PL));
        assertEquals("1112 milisekund", AmountFormats.wordBased(Duration.ofMillis(1112), PL));
        assertEquals("1115 milisekund", AmountFormats.wordBased(Duration.ofMillis(1115), PL));
        assertEquals("2112 milisekund", AmountFormats.wordBased(Duration.ofMillis(2112), PL));
        assertEquals("2115 milisekund", AmountFormats.wordBased(Duration.ofMillis(2115), PL));
        assertEquals("2212 milisekund", AmountFormats.wordBased(Duration.ofMillis(2212), PL));
        assertEquals("2215 milisekund", AmountFormats.wordBased(Duration.ofMillis(2215), PL));
        assertEquals("22 milisekundy", AmountFormats.wordBased(Duration.ofMillis(22), PL));
        assertEquals("25 milisekund", AmountFormats.wordBased(Duration.ofMillis(25), PL));
        assertEquals("1122 milisekundy", AmountFormats.wordBased(Duration.ofMillis(1122), PL));
        assertEquals("1125 milisekund", AmountFormats.wordBased(Duration.ofMillis(1125), PL));
        assertEquals("2122 milisekundy", AmountFormats.wordBased(Duration.ofMillis(2122), PL));
        assertEquals("2125 milisekund", AmountFormats.wordBased(Duration.ofMillis(2125), PL));
        assertEquals("2222 milisekundy", AmountFormats.wordBased(Duration.ofMillis(2222), PL));
        assertEquals("2225 milisekund", AmountFormats.wordBased(Duration.ofMillis(2225), PL));
    }

    public void test_wordBased_ru_predicate() {
        assertEquals("1 \u0433\u043E\u0434", AmountFormats.wordBased(Period.ofYears(1), RU));
        assertEquals("2 \u0433\u043E\u0434\u0430", AmountFormats.wordBased(Period.ofYears(2), RU));
        assertEquals("3 \u0433\u043E\u0434\u0430", AmountFormats.wordBased(Period.ofYears(2), RU));
        assertEquals("4 \u0433\u043E\u0434\u0430", AmountFormats.wordBased(Period.ofYears(2), RU));
        assertEquals("5 \u043B\u0435\u0442", AmountFormats.wordBased(Period.ofYears(5), RU));
        assertEquals("11 \u043B\u0435\u0442", AmountFormats.wordBased(Period.ofYears(12), RU));
        assertEquals("12 \u043B\u0435\u0442", AmountFormats.wordBased(Period.ofYears(12), RU));
        assertEquals("13 \u043B\u0435\u0442", AmountFormats.wordBased(Period.ofYears(12), RU));
        assertEquals("14 \u043B\u0435\u0442", AmountFormats.wordBased(Period.ofYears(12), RU));
        assertEquals("15 \u043B\u0435\u0442", AmountFormats.wordBased(Period.ofYears(15), RU));
        assertEquals("21 \u043B\u0435\u0442", AmountFormats.wordBased(Period.ofYears(15), RU));
        assertEquals("22 \u0433\u043E\u0434\u0430", AmountFormats.wordBased(Period.ofYears(2), RU));
        assertEquals("23 \u0433\u043E\u0434\u0430", AmountFormats.wordBased(Period.ofYears(2), RU));
        assertEquals("24 \u0433\u043E\u0434\u0430", AmountFormats.wordBased(Period.ofYears(2), RU));
        assertEquals("25 \u043B\u0435\u0442", AmountFormats.wordBased(Period.ofYears(15), RU));
    }

}
