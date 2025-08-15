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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test AmountFormats.
 */
public class TestAmountFormats {

    private static final Locale FA = new Locale("fa");
    private static final Locale PL = new Locale("pl");
    private static final Locale RO = new Locale("ro");
    private static final Locale RU = new Locale("ru");

    //-----------------------------------------------------------------------
    @Test
    public void test_iso8601() {
        assertEquals("P12M6DT8H30M", AmountFormats.iso8601(Period.of(0, 12, 6), Duration.ofMinutes(8 * 60 + 30)));
        assertEquals("PT8H30M", AmountFormats.iso8601(Period.ZERO, Duration.ofMinutes(8 * 60 + 30)));
        assertEquals("P12M6D", AmountFormats.iso8601(Period.of(0, 12, 6), Duration.ZERO));
    }

    //-----------------------------------------------------------------------
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
            {Period.ofMonths(14), Locale.ENGLISH, "14 months"},
            {Period.ofMonths(14).normalized(), Locale.ENGLISH, "1 year and 2 months"},
            {Period.ofYears(2).plusMonths(-10).normalized(), Locale.ENGLISH, "1 year and 2 months"},

            {Period.ofDays(1), Locale.ENGLISH, "1 day"},
            {Period.ofDays(2), Locale.ENGLISH, "2 days"},
            {Period.ofDays(5), Locale.ENGLISH, "5 days"},
            {Period.ofDays(7), Locale.ENGLISH, "1 week"},
            {Period.ofDays(-1), Locale.ENGLISH, "-1 day"},

            {Period.ofDays(1), RO, "1 zi"},
            {Period.ofDays(2), RO, "2 zile"},
            {Period.ofDays(5), RO, "5 zile"},
            {Period.ofDays(7), RO, "1 săptămână"},
            {Period.ofWeeks(3), RO, "3 săptămâni"},
            {Period.ofMonths(14).normalized(), RO, "1 an și 2 luni"},
            {Period.ofMonths(1), RO, "1 lună"},
            {Period.ofYears(2), RO, "2 ani"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_wordBased")
    public void test_wordBased(Period period, Locale locale, String expected) {
        assertEquals(expected, AmountFormats.wordBased(period, locale));
    }

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

            {Duration.ofMinutes(60 + 1), RO, "1 oră și 1 minut"},
            {Duration.ofMinutes(180 + 2), RO, "3 ore și 2 minute"},
            {Duration.ofMinutes(-60 - 40), RO, "-1 oră și -40 minute"},
            {Duration.ofSeconds(-90), RO, "-1 minut și -30 secunde"},
            {Duration.ofNanos(1_000_000), RO, "1 milisecundă"},
            {Duration.ofNanos(1000_000_000 + 2_000_000), RO, "1 secundă și 2 milisecunde"},

            {Duration.ofHours(5).plusMinutes(6).plusSeconds(7).plusNanos(8_000_000L), PL,
                "5 godzin, 6 minut, 7 sekund i 8 milisekund"},

            {Duration.ofMinutes(60 + 1), FA, "1 \u0633\u0627\u0639\u062A \u0648 1 \u062f\u0642\u06cc\u0642\u0647"}
        };
    }

    @ParameterizedTest
    @MethodSource("duration_wordBased")
    public void test_wordBased(Duration duration, Locale locale, String expected) {
        assertEquals(expected, AmountFormats.wordBased(duration, locale));
    }

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

    @ParameterizedTest
    @MethodSource("period_duration_wordBased")
    public void test_wordBased(Period period, Duration duration, Locale locale, String expected) {
        assertEquals(expected, AmountFormats.wordBased(period, duration, locale));
        assertEquals(expected, AmountFormats.wordBased(PeriodDuration.of(period, duration), locale));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_wordBased_pl_formatStandard() {
        Period p = Period.ofDays(1);
        Duration d = Duration.ofHours(5).plusMinutes(6).plusSeconds(7).plusNanos(8_000_000L);
        assertEquals("1 dzie\u0144, 5 godzin, 6 minut, 7 sekund i 8 milisekund", AmountFormats.wordBased(p, d, PL));
    }

    @Test
    public void test_wordBased_pl_predicate() {
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
        assertEquals("18 godzin i 32 minuty", AmountFormats.wordBased(Duration.ofMinutes(1112), PL));
        assertEquals("18 godzin i 35 minut", AmountFormats.wordBased(Duration.ofMinutes(1115), PL));
        assertEquals("35 godzin i 12 minut", AmountFormats.wordBased(Duration.ofMinutes(2112), PL));
        assertEquals("35 godzin i 15 minut", AmountFormats.wordBased(Duration.ofMinutes(2115), PL));
        assertEquals("36 godzin i 52 minuty", AmountFormats.wordBased(Duration.ofMinutes(2212), PL));
        assertEquals("36 godzin i 55 minut", AmountFormats.wordBased(Duration.ofMinutes(2215), PL));
    }

    // -----------------------------------------------------------------------
    // wordBased "ru" locale
    // -----------------------------------------------------------------------
    @Test
    public void test_wordBased_ru_formatStandard() {
        Period period = Period.ofYears(1).plusMonths(2).plusDays(3);
        Duration duration = Duration.ofHours(5).plusMinutes(6).plusSeconds(7).plusNanos(8_000_000L);

        String expected = "1 \u0433\u043E\u0434, 2 \u043C\u0435\u0441\u044F\u0446\u0430,"
            + " 3 \u0434\u043D\u044F, 5 \u0447\u0430\u0441\u043e\u0432, 6 \u043c\u0438\u043d\u0443\u0442,"
            + " 7 \u0441\u0435\u043A\u0443\u043D\u0434 \u0438 8 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434";

        assertEquals(expected, AmountFormats.wordBased(period, duration, RU));
    }

    // -----------------------------------------------------------------------
    @ParameterizedTest
    @MethodSource("wordBased_ru_formatSeparator")
    public void test_wordBased_ru_formatSeparator(String expected, Duration duration) {
        assertEquals(expected, AmountFormats.wordBased(duration, RU));
    }

    public static Object[][] wordBased_ru_formatSeparator() {
        return new Object[][]{
            {"18 \u0447\u0430\u0441\u043E\u0432 \u0438 32 \u043C\u0438\u043D\u0443\u0442\u044B", Duration.ofMinutes(1112)},
            {"1 \u0441\u0435\u043A\u0443\u043D\u0434\u0430 \u0438 112 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(1112)},
        };
    }

    // -----------------------------------------------------------------------
    @ParameterizedTest
    @MethodSource("wordBased_ru_period_predicate")
    public void test_wordBased_ru_period_predicate(String expected, Period period) {
        assertEquals(expected, AmountFormats.wordBased(period, RU));
    }

    public static Object[][] wordBased_ru_period_predicate() {
        return new Object[][]{

//       год  \u0433\u043E\u0434
//       года \u0433\u043E\u0434\u0430
//       лет  \u043B\u0435\u0442
            {"1 \u0433\u043E\u0434", Period.ofYears(1)},
            {"11 \u043B\u0435\u0442", Period.ofYears(11)},
            {"101 \u0433\u043E\u0434", Period.ofYears(101)},
            {"111 \u043B\u0435\u0442", Period.ofYears(111)},
            {"121 \u0433\u043E\u0434", Period.ofYears(121)},
            {"2001 \u0433\u043E\u0434", Period.ofYears(2001)},
            {"2 \u0433\u043E\u0434\u0430", Period.ofYears(2)},
            {"3 \u0433\u043E\u0434\u0430", Period.ofYears(3)},
            {"4 \u0433\u043E\u0434\u0430", Period.ofYears(4)},
            {"12 \u043B\u0435\u0442", Period.ofYears(12)},
            {"13 \u043B\u0435\u0442", Period.ofYears(13)},
            {"14 \u043B\u0435\u0442", Period.ofYears(14)},
            {"21 \u0433\u043E\u0434", Period.ofYears(21)},
            {"22 \u0433\u043E\u0434\u0430", Period.ofYears(22)},
            {"23 \u0433\u043E\u0434\u0430", Period.ofYears(23)},
            {"24 \u0433\u043E\u0434\u0430", Period.ofYears(24)},
            {"102 \u0433\u043E\u0434\u0430", Period.ofYears(102)},
            {"105 \u043B\u0435\u0442", Period.ofYears(105)},
            {"112 \u043B\u0435\u0442", Period.ofYears(112)},
            {"113 \u043B\u0435\u0442", Period.ofYears(113)},
            {"124 \u0433\u043E\u0434\u0430", Period.ofYears(124)},
            {"5 \u043B\u0435\u0442", Period.ofYears(5)},
            {"15 \u043B\u0435\u0442", Period.ofYears(15)},
            {"25 \u043B\u0435\u0442", Period.ofYears(25)},
            {"106 \u043B\u0435\u0442", Period.ofYears(106)},
            {"1005 \u043B\u0435\u0442", Period.ofYears(1005)},
            {"31 \u0433\u043E\u0434", Period.ofYears(31)},
            {"32 \u0433\u043E\u0434\u0430", Period.ofYears(32)},

//       месяц   \u043C\u0435\u0441\u044F\u0446
//       месяца  \u043C\u0435\u0441\u044F\u0446\u0430
//       месяцев \u043C\u0435\u0441\u044F\u0446\u0435\u0432
            {"1 \u043C\u0435\u0441\u044F\u0446", Period.ofMonths(1)},
            {"11 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(11)},
            {"21 \u043C\u0435\u0441\u044F\u0446", Period.ofMonths(21)},
            {"101 \u043C\u0435\u0441\u044F\u0446", Period.ofMonths(101)},
            {"111 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(111)},
            {"121 \u043C\u0435\u0441\u044F\u0446", Period.ofMonths(121)},
            {"2001 \u043C\u0435\u0441\u044F\u0446", Period.ofMonths(2001)},
            {"2 \u043C\u0435\u0441\u044F\u0446\u0430", Period.ofMonths(2)},
            {"3 \u043C\u0435\u0441\u044F\u0446\u0430", Period.ofMonths(3)},
            {"4 \u043C\u0435\u0441\u044F\u0446\u0430", Period.ofMonths(4)},
            {"12 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(12)},
            {"13 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(13)},
            {"14 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(14)},
            {"22 \u043C\u0435\u0441\u044F\u0446\u0430", Period.ofMonths(22)},
            {"23 \u043C\u0435\u0441\u044F\u0446\u0430", Period.ofMonths(23)},
            {"24 \u043C\u0435\u0441\u044F\u0446\u0430", Period.ofMonths(24)},
            {"102 \u043C\u0435\u0441\u044F\u0446\u0430", Period.ofMonths(102)},
            {"112 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(112)},
            {"124 \u043C\u0435\u0441\u044F\u0446\u0430", Period.ofMonths(124)},
            {"5 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(5)},
            {"15 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(15)},
            {"25 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(25)},
            {"105 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(105)},
            {"1005 \u043C\u0435\u0441\u044F\u0446\u0435\u0432", Period.ofMonths(1005)},

//       неделя \u043D\u0435\u0434\u0435\u043B\u044F
//       недели \u043D\u0435\u0434\u0435\u043B\u0438
//       недель \u043D\u0435\u0434\u0435\u043B\u044C
            {"1 \u043D\u0435\u0434\u0435\u043B\u044F", Period.ofWeeks(1)},
            {"11 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(11)},
            {"21 \u043D\u0435\u0434\u0435\u043B\u044F", Period.ofWeeks(21)},
            {"101 \u043D\u0435\u0434\u0435\u043B\u044F", Period.ofWeeks(101)},
            {"111 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(111)},
            {"121 \u043D\u0435\u0434\u0435\u043B\u044F", Period.ofWeeks(121)},
            {"2001 \u043D\u0435\u0434\u0435\u043B\u044F", Period.ofWeeks(2001)},
            {"2 \u043D\u0435\u0434\u0435\u043B\u0438", Period.ofWeeks(2)},
            {"3 \u043D\u0435\u0434\u0435\u043B\u0438", Period.ofWeeks(3)},
            {"4 \u043D\u0435\u0434\u0435\u043B\u0438", Period.ofWeeks(4)},
            {"12 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(12)},
            {"13 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(13)},
            {"14 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(14)},
            {"22 \u043D\u0435\u0434\u0435\u043B\u0438", Period.ofWeeks(22)},
            {"23 \u043D\u0435\u0434\u0435\u043B\u0438", Period.ofWeeks(23)},
            {"24 \u043D\u0435\u0434\u0435\u043B\u0438", Period.ofWeeks(24)},
            {"102 \u043D\u0435\u0434\u0435\u043B\u0438", Period.ofWeeks(102)},
            {"112 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(112)},
            {"124 \u043D\u0435\u0434\u0435\u043B\u0438", Period.ofWeeks(124)},
            {"5 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(5)},
            {"15 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(15)},
            {"25 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(25)},
            {"105 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(105)},
            {"1005 \u043D\u0435\u0434\u0435\u043B\u044C", Period.ofWeeks(1005)},

//       день \u0434\u0435\u043D\u044C
//       дня  \u0434\u043D\u044F
//       дней \u0434\u043D\u0435\u0439
            {"1 \u0434\u0435\u043D\u044C", Period.ofDays(1)},
            {"11 \u0434\u043D\u0435\u0439", Period.ofDays(11)},
            {"101 \u0434\u0435\u043D\u044C", Period.ofDays(101)},
            {"111 \u0434\u043D\u0435\u0439", Period.ofDays(111)},
            {"121 \u0434\u0435\u043D\u044C", Period.ofDays(121)},
            {"31 \u0434\u0435\u043D\u044C", Period.ofDays(31)},
            {"2001 \u0434\u0435\u043D\u044C", Period.ofDays(2001)},
            {"2 \u0434\u043D\u044F", Period.ofDays(2)},
            {"3 \u0434\u043D\u044F", Period.ofDays(3)},
            {"4 \u0434\u043D\u044F", Period.ofDays(4)},
            {"12 \u0434\u043D\u0435\u0439", Period.ofDays(12)},
            {"13 \u0434\u043D\u0435\u0439", Period.ofDays(13)},
            {"22 \u0434\u043D\u044F", Period.ofDays(22)},
            {"23 \u0434\u043D\u044F", Period.ofDays(23)},
            {"24 \u0434\u043D\u044F", Period.ofDays(24)},
            {"102 \u0434\u043D\u044F", Period.ofDays(102)},
            {"113 \u0434\u043D\u0435\u0439", Period.ofDays(113)},
            {"124 \u0434\u043D\u044F", Period.ofDays(124)},
            {"5 \u0434\u043D\u0435\u0439", Period.ofDays(5)},
            {"15 \u0434\u043D\u0435\u0439", Period.ofDays(15)},
            {"25 \u0434\u043D\u0435\u0439", Period.ofDays(25)},
            {"106 \u0434\u043D\u0435\u0439", Period.ofDays(106)},
            {"1005 \u0434\u043D\u0435\u0439", Period.ofDays(1005)}
        };
    }

    // -----------------------------------------------------------------------
    @ParameterizedTest
    @MethodSource("wordBased_ru_duration_predicate")
    public void test_wordBased_ru_duration_predicate(String expected, Duration duration) {
        assertEquals(expected, AmountFormats.wordBased(duration, RU));
    }

    public static Object[][] wordBased_ru_duration_predicate() {
        return new Object[][]{

//       час   \u0447\u0430\u0441
//       часа  \u0447\u0430\u0441\u0430
//       часов \u0447\u0430\u0441\u043E\u0432
            {"1 \u0447\u0430\u0441", Duration.ofHours(1)},
            {"11 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(11)},
            {"21 \u0447\u0430\u0441", Duration.ofHours(21)},
            {"101 \u0447\u0430\u0441", Duration.ofHours(101)},
            {"111 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(111)},
            {"121 \u0447\u0430\u0441", Duration.ofHours(121)},
            {"2001 \u0447\u0430\u0441", Duration.ofHours(2001)},
            {"2 \u0447\u0430\u0441\u0430", Duration.ofHours(2)},
            {"3 \u0447\u0430\u0441\u0430", Duration.ofHours(3)},
            {"4 \u0447\u0430\u0441\u0430", Duration.ofHours(4)},
            {"12 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(12)},
            {"13 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(13)},
            {"14 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(14)},
            {"22 \u0447\u0430\u0441\u0430", Duration.ofHours(22)},
            {"23 \u0447\u0430\u0441\u0430", Duration.ofHours(23)},
            {"24 \u0447\u0430\u0441\u0430", Duration.ofHours(24)},
            {"102 \u0447\u0430\u0441\u0430", Duration.ofHours(102)},
            {"112 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(112)},
            {"124 \u0447\u0430\u0441\u0430", Duration.ofHours(124)},
            {"5 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(5)},
            {"15 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(15)},
            {"25 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(25)},
            {"105 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(105)},
            {"1005 \u0447\u0430\u0441\u043e\u0432", Duration.ofHours(1005)},

//       минута \u043C\u0438\u043D\u0443\u0442\u0430
//       минуты \u043C\u0438\u043D\u0443\u0442\u044B
//       минут  \u043C\u0438\u043D\u0443\u0442
            {"1 \u043c\u0438\u043d\u0443\u0442\u0430", Duration.ofMinutes(1)},
            {"11 \u043c\u0438\u043d\u0443\u0442", Duration.ofMinutes(11)},
            {"21 \u043c\u0438\u043d\u0443\u0442\u0430", Duration.ofMinutes(21)},
            {"2 \u043c\u0438\u043d\u0443\u0442\u044b", Duration.ofMinutes(2)},
            {"3 \u043c\u0438\u043d\u0443\u0442\u044b", Duration.ofMinutes(3)},
            {"4 \u043c\u0438\u043d\u0443\u0442\u044b", Duration.ofMinutes(4)},
            {"12 \u043c\u0438\u043d\u0443\u0442", Duration.ofMinutes(12)},
            {"13 \u043c\u0438\u043d\u0443\u0442", Duration.ofMinutes(13)},
            {"14 \u043c\u0438\u043d\u0443\u0442", Duration.ofMinutes(14)},
            {"22 \u043c\u0438\u043d\u0443\u0442\u044b", Duration.ofMinutes(22)},
            {"23 \u043c\u0438\u043d\u0443\u0442\u044b", Duration.ofMinutes(23)},
            {"24 \u043c\u0438\u043d\u0443\u0442\u044b", Duration.ofMinutes(24)},
            {"5 \u043c\u0438\u043d\u0443\u0442", Duration.ofMinutes(5)},
            {"15 \u043c\u0438\u043d\u0443\u0442", Duration.ofMinutes(15)},
            {"25 \u043c\u0438\u043d\u0443\u0442", Duration.ofMinutes(25)},

//       секунда \u0441\u0435\u043A\u0443\u043D\u0434\u0430
//       секунды \u0441\u0435\u043A\u0443\u043D\u0434\u044B
//       секунд  \u0441\u0435\u043A\u0443\u043D\u0434
            {"1 \u0441\u0435\u043A\u0443\u043D\u0434\u0430", Duration.ofSeconds(1)},
            {"11 \u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofSeconds(11)},
            {"21 \u0441\u0435\u043A\u0443\u043D\u0434\u0430", Duration.ofSeconds(21)},
            {"2 \u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofSeconds(2)},
            {"3 \u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofSeconds(3)},
            {"4 \u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofSeconds(4)},
            {"12 \u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofSeconds(12)},
            {"13 \u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofSeconds(13)},
            {"14 \u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofSeconds(14)},
            {"22 \u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofSeconds(22)},
            {"23 \u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofSeconds(23)},
            {"24 \u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofSeconds(24)},
            {"5 \u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofSeconds(5)},
            {"15 \u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofSeconds(15)},
            {"25 \u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofSeconds(25)},

//       миллисекунда \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u0430
//       миллисекунды \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u044B
//       миллисекунд  \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434
            {"1 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u0430", Duration.ofMillis(1)},
            {"11 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(11)},
            {"21 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u0430", Duration.ofMillis(21)},
            {"101 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u0430", Duration.ofMillis(101)},
            {"111 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(111)},
            {"121 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u0430", Duration.ofMillis(121)},
            {"2 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofMillis(2)},
            {"3 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofMillis(3)},
            {"4 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofMillis(4)},
            {"12 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(12)},
            {"13 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(13)},
            {"14 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(14)},
            {"22 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofMillis(22)},
            {"23 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofMillis(23)},
            {"24 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofMillis(24)},
            {"102 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofMillis(102)},
            {"112 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(112)},
            {"124 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434\u044B", Duration.ofMillis(124)},
            {"5 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(5)},
            {"15 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(15)},
            {"25 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(25)},
            {"105 \u043C\u0438\u043B\u043B\u0438\u0441\u0435\u043A\u0443\u043D\u0434", Duration.ofMillis(105)}
        };
    }

    // -----------------------------------------------------------------------
    @ParameterizedTest
    @MethodSource("duration_unitBased")
    public void test_parseUnitBasedDuration(Duration expected, String input) {
        assertEquals(expected, AmountFormats.parseUnitBasedDuration(input));
    }

    public static Object[][] duration_unitBased() {
        return new Object[][] {
            {Duration.ZERO, "0"},
            {Duration.ofHours(1), "+1h"},
            {Duration.ofHours(1).negated(), "-1h"},
            {Duration.ofHours(1).plusMinutes(15).negated(), "-1.25h"},
            {Duration.ofSeconds(15).plusMillis(110), "15.11s"},
            {Duration.ofHours(1).plusMinutes(2).plusSeconds(3).plusMillis(400), "1h2m3.4s"},
            {Duration.ofMinutes(1), "1m"},
            {Duration.ofSeconds(1), "1s"},
            {Duration.ofMillis(1), "1ms"},
            {Duration.ofNanos(1000), "1us"},
            {Duration.ofNanos(1000), "1µs"}, // U+00B5 = micro symbol
            {Duration.ofNanos(1000), "1μs"}, // U+03BC = Greek letter mu
            {Duration.ofNanos(1), "1ns"},
            {Duration.ofHours(1).plusMinutes(1).plusSeconds(1), "1h1m1s"},
            // Loss of precision, but still a valid duration.
            {Duration.ofSeconds(1).plusNanos(999_999_999), "1.9999999999999999999999999999s"},
            // Adding duration values to exactly the max duration.
            {Duration.ofSeconds(Long.MAX_VALUE), String.format("%ds%ds", Long.MAX_VALUE - 2, 2)},
        };
    }

    @ParameterizedTest
    @MethodSource("duration_unitBasedErrors")
    public void test_parseUnitBasedDurationErrors(Exception e, String input) {
        Exception thrown =
            assertThrows(e.getClass(), () -> AmountFormats.parseUnitBasedDuration(input));
        assertEquals(e.getMessage(), thrown.getMessage());
        if (e instanceof DateTimeParseException) {
            DateTimeParseException expected = (DateTimeParseException) e;
            DateTimeParseException actual = (DateTimeParseException) thrown;
            assertEquals(expected.getParsedString(), actual.getParsedString());
            assertEquals(expected.getErrorIndex(), actual.getErrorIndex());
        }
    }

    public static Object[][] duration_unitBasedErrors() {
        return new Object[][] {
            {new NullPointerException("durationText must not be null"), null},
            {new DateTimeParseException("Not a numeric value", "", 0), ""},
            {new DateTimeParseException("Not a numeric value", "+", 0), "+"},
            {new DateTimeParseException("Not a numeric value", "-", 0), "-"},
            {new DateTimeParseException("Missing leading integer", ".", 0), "."},
            {new DateTimeParseException("Missing leading integer", ".1s", 0), ".1s"},
            {new DateTimeParseException("Missing leading integer", "inf", 0), "inf"},
            {new DateTimeParseException("Missing leading integer", "-inf", 1), "-inf"},
            {new DateTimeParseException("Missing numeric fraction after '.'", "1.b", 2), "1.b"},
            {new DateTimeParseException("Invalid duration unit", "1.1ps", 3), "1.1ps"},
            {new DateTimeParseException(
                "Duration string exceeds valid numeric range", "9223372036854775807h", 19),
                String.format("%dh", Long.MAX_VALUE)}, // overflow in create duration
            {new DateTimeParseException(
                "Duration string exceeds valid numeric range", "-9223372036854775808h", 19),
                String.format("%dh", Long.MAX_VALUE + 1)}, // overflow in leading int
            {new DateTimeParseException(
                "Duration string exceeds valid numeric range",
                "9223372036854775806s2s", 21),
                String.format("%ds2s", Long.MAX_VALUE - 1)}, // overflow on int add
            // overflow on fraction add
            {new DateTimeParseException(
                "Duration string exceeds valid numeric range",
                "9223372036854775805.1s2.999999999s", 33),
                String.format("%d.1s2.999999999s", Long.MAX_VALUE - 2)}
        };
    }
}
