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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test AmountFormats.
 */
public class TestAmountFormats {

    private static final Locale BG = new Locale("bg");
    private static final Locale CA = new Locale("ca");
    private static final Locale CS = new Locale("cs");
    private static final Locale DA = new Locale("da");
    private static final Locale ES = new Locale("es");
    private static final Locale FA = new Locale("fa");
    private static final Locale FI = new Locale("fi");
    private static final Locale NB = new Locale("nb");
    private static final Locale NL = new Locale("nl");
    private static final Locale NN = new Locale("nn");
    private static final Locale PL = new Locale("pl");
    private static final Locale PT = new Locale("pt");
    private static final Locale RO = new Locale("ro");
    private static final Locale RU = new Locale("ru");
    private static final Locale SV = new Locale("sv");
    private static final Locale TR = new Locale("tr");

    /** Every language with word-based resources, as used by the style tests. */
    private static final Locale[] SUPPORTED = {
        BG, CA, CS, DA, Locale.GERMAN, Locale.ENGLISH, ES, FA, FI, Locale.FRENCH,
        Locale.ITALIAN, Locale.JAPANESE, NB, NL, NN, PL, PT, RO, RU, SV, TR};

    /** A duration exercising every duration unit, including milliseconds. */
    private static final Duration D_HMSM = Duration.ofHours(5).plusMinutes(6).plusSeconds(7).plusMillis(8);

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

    public static Object[][] period_duration_wordBased_style() {
        return new Object[][] {
            {Period.ofDays(1), Duration.ofMinutes(180 + 2), Locale.ROOT, "1 day, 3 hr and 2 min", "1 day, 3 hr, 2 min", "1d 3h 2m"},
            {Period.ofDays(2), Duration.ofSeconds(180), Locale.ROOT, "2 days and 3 min", "2 days, 3 min", "2d 3m"},
            {Period.ofDays(7), Duration.ofMinutes(80), Locale.ROOT, "1 wk, 1 hr and 20 min", "1 wk, 1 hr, 20 min", "1w 1h 20m"},
            {Period.ZERO, Duration.ofMillis(1_000), Locale.ROOT, "1 sec", "1 sec", "1s"},

            {Period.ofMonths(0), Duration.ofSeconds(0), Locale.ENGLISH, "0 ms", "0 ms", "0ms"},
            {Period.ofMonths(0), Duration.ofHours(9), Locale.ENGLISH, "9 hr", "9 hr", "9h"},
            {Period.ofMonths(1), Duration.ZERO, Locale.ENGLISH, "1 mth", "1 mth", "1m"},
            {Period.ofMonths(4), Duration.ZERO, Locale.ENGLISH, "4 mths", "4 mths", "4m"},
            {Period.of(1, 2, 5), Duration.ofHours(4), Locale.ENGLISH, "1 yr, 2 mths, 5 days and 4 hr", "1 yr, 2 mths, 5 days, 4 hr", "1y 2m 5d 4h"},
            {Period.ofDays(5), Duration.ofDays(2).plusHours(6), Locale.ENGLISH, "7 days and 6 hr", "7 days, 6 hr", "7d 6h"},
            {Period.ofDays(5), Duration.ofDays(-2).plusHours(-6), Locale.ENGLISH, "3 days and -6 hr", "3 days, -6 hr", "3d -6h"},
        };
    }

    @ParameterizedTest
    @MethodSource("period_duration_wordBased_style")
    public void test_wordBased_style(Period period, Duration duration, Locale locale, String expectedLong, String expectedMedium, String expectedShort) {
        assertEquals(expectedLong, AmountFormats.wordBased(period, duration, locale, FormatStyle.LONG), "LONG style");
        assertEquals(expectedLong, AmountFormats.wordBased(PeriodDuration.of(period, duration), locale, FormatStyle.LONG), "LONG style");
        assertEquals(expectedMedium, AmountFormats.wordBased(period, duration, locale, FormatStyle.MEDIUM), "MEDIUM style");
        assertEquals(expectedMedium, AmountFormats.wordBased(PeriodDuration.of(period, duration), locale, FormatStyle.MEDIUM), "MEDIUM style");
        assertEquals(expectedShort, AmountFormats.wordBased(period, duration, locale, FormatStyle.SHORT), "SHORT style");
        assertEquals(expectedShort, AmountFormats.wordBased(PeriodDuration.of(period, duration), locale, FormatStyle.SHORT), "SHORT style");
    }

    // one case per supported language, exercising every unit and both separators
    // expected values checked against CLDR 48.2.1
    public static Object[][] period_duration_wordBased_style_languages() {
        return new Object[][] {
            {BG,
                "3 год., 4 мес., 3 седм., 5 ч, 6 мин, 7 сек и 8 мсек",
                "3 год., 4 мес., 3 седм., 5 ч, 6 мин, 7 сек и 8 мсек",
                "3 г., 4 мес., 3 седм., 5 ч, 6 мин, 7 с и 8 мсек"},
            {CA,
                "3 anys, 4 m, 3 setm., 5 h, 6 min, 7 s i 8 ms",
                "3 anys, 4 m, 3 setm., 5 h, 6 min, 7 s i 8 ms",
                "3 anys, 4 m, 3 setm., 5 h, 6 min, 7 s i 8 ms"},
            {CS,
                "3 roky, 4 měs., 3 týd., 5 h, 6 min, 7 s a 8 ms",
                "3 roky, 4 měs., 3 týd., 5 h, 6 min, 7 s, 8 ms",
                "3 r. 4 m. 3 t. 5 h 6 m 7 s 8 ms"},
            {DA,
                "3 år, 4 mdr., 3 uger, 5 t., 6 min., 7 sek. og 8 ms",
                "3 år, 4 mdr., 3 uger, 5 t., 6 min., 7 sek. og 8 ms",
                "3 år, 4 m, 3 u, 5 t, 6 m, 7 s og 8 ms"},
            {Locale.GERMAN,
                "3 J, 4 Mon., 3 Wo., 5 Std., 6 Min., 7 Sek. und 8 ms",
                "3 J, 4 Mon., 3 Wo., 5 Std., 6 Min., 7 Sek., 8 ms",
                "3 J, 4 M, 3 W, 5h, 6 Min., 7 Sek., 8 ms"},
            {Locale.ENGLISH,
                "3 yrs, 4 mths, 3 wks, 5 hr, 6 min, 7 sec and 8 ms",
                "3 yrs, 4 mths, 3 wks, 5 hr, 6 min, 7 sec, 8 ms",
                "3y 4m 3w 5h 6m 7s 8ms"},
            {ES,
                "3 a, 4 m., 3 sem., 5 h, 6 min, 7 s y 8 ms",
                "3 a, 4 m., 3 sem., 5 h, 6 min, 7 s y 8 ms",
                "3a 4m 3sem 5h 6min 7s 8ms"},
            {FA,
                "3 سال،‏ 4 ماه،‏ 3 هفته،‏ 5 ساعت،‏ 6 دقیقه،‏ 7 ثانیه و 8 میلی‌ثانیه",
                "3 سال،‏ 4 ماه،‏ 3 هفته،‏ 5 ساعت،‏ 6 دقیقه،‏ 7 ثانیه،‏ 8 میلی‌ثانیه",
                "3 سال 4 ماه 3 هفته 5h 6m 7s 8ms"},
            {FI,
                "3 v, 4 kk, 3 vk, 5 t, 6 min, 7 s ja 8 ms",
                "3 v, 4 kk, 3 vk, 5 t, 6 min, 7 s, 8 ms",
                "3v 4kk 3vk 5t 6min 7s 8ms"},
            {Locale.FRENCH,
                "3 ans, 4 m., 3 sem., 5 h, 6 min, 7 s et 8 ms",
                "3 ans, 4 m., 3 sem., 5 h, 6 min, 7 s et 8 ms",
                "3a 4m. 3sem. 5h 6min 7s 8ms"},
            {Locale.ITALIAN,
                "3 anni, 4 mesi, 3 sett., 5 h, 6 min, 7 s e 8 ms",
                "3 anni, 4 mesi, 3 sett., 5 h, 6 min, 7 s e 8 ms",
                "3anni 4 mesi 3sett. 5h 6min 7s 8ms"},
            {Locale.JAPANESE,
                "3 年、4 か月、3 週間、5 時間、6 分、7 秒、8 ms",
                "3 年 4 か月 3 週間 5 時間 6 分 7 秒 8 ms",
                "3y4m3w5h6m7s8ms"},
            {NB,
                "3 år, 4 md., 3 u, 5 t, 6 min, 7 sek og 8 ms",
                "3 år, 4 md., 3 u, 5 t, 6 min, 7 sek, 8 ms",
                "3å, 4 m, 3u, 5t, 6m, 7s, 8ms"},
            {NL,
                "3 jr, 4 mnd, 3 wkn, 5 uur, 6 min, 7 sec en 8 ms",
                "3 jr, 4 mnd, 3 wkn, 5 uur, 6 min, 7 sec, 8 ms",
                "3 jr, 4 m, 3 w, 5 u, 6 m, 7 s, 8 ms"},
            {NN,
                "3 år, 4 md., 3 v, 5 t, 6 min, 7 s og 8 ms",
                "3 år, 4 md., 3 v, 5 t, 6 min, 7 s, 8 ms",
                "3å 4m 3v 5t 6m 7s 8ms"},
            {PL,
                "3 lata, 4 mies., 3 tyg., 5 godz., 6 min, 7 sek. i 8 ms",
                "3 lata, 4 mies., 3 tyg., 5 godz., 6 min, 7 sek. i 8 ms",
                "3 l., 4 m-ce, 3 t., 5 h, 6 min, 7 s i 8 ms"},
            {PT,
                "3 anos, 4 meses, 3 sem., 5 h, 6 min, 7 s e 8 ms",
                "3 anos, 4 meses, 3 sem., 5 h, 6 min, 7 s e 8 ms",
                "3 anos 4 meses 3 sem. 5 h 6 min 7 s 8 ms"},
            {RO,
                "3 ani, 4 luni, 3 săpt., 5 ore, 6 min., 7 s și 8 ms",
                "3 ani, 4 luni, 3 săpt., 5 ore, 6 min., 7 s, 8 ms",
                "3 a, 4 l, 3 săpt., 5 h, 6 m, 7 s, 8 ms"},
            {RU,
                "3 г., 4 мес., 3 нед., 5 ч, 6 мин, 7 с и 8 мс",
                "3 г. 4 мес. 3 нед. 5 ч 6 мин 7 с 8 мс",
                "3 г. 4 м. 3 н. 5 ч 6 мин 7 с 8 мс"},
            {SV,
                "3 år, 4 mån, 3 v, 5 tim, 6 min, 7 s och 8 ms",
                "3 år, 4 mån, 3 v, 5 tim, 6 min, 7 s, 8 ms",
                "3å 4m 3v 5h 6m 7s 8ms"},
            {TR,
                "3 yıl, 4 ay, 3 hf., 5 sa., 6 dk., 7 sn. ve 8 msn",
                "3 yıl 4 ay 3 hf. 5 sa. 6 dk. 7 sn. 8 msn",
                "3y 4a 3h 5s 6d 7sn 8msn"},
        };
    }

    @ParameterizedTest
    @MethodSource("period_duration_wordBased_style_languages")
    public void test_wordBased_style_languages(Locale locale, String expectedLong, String expectedMedium, String expectedShort) {
        Period period = Period.of(3, 4, 21);
        assertEquals(expectedLong, AmountFormats.wordBased(period, D_HMSM, locale, FormatStyle.LONG), "LONG style");
        assertEquals(expectedMedium, AmountFormats.wordBased(period, D_HMSM, locale, FormatStyle.MEDIUM), "MEDIUM style");
        assertEquals(expectedShort, AmountFormats.wordBased(period, D_HMSM, locale, FormatStyle.SHORT), "SHORT style");
    }

    // the languages that need plural predicates rather than a single/plural pair
    public static Object[][] period_wordBased_style_plurals() {
        return new Object[][] {
            {CS, 1, "1 rok", "1 rok", "1 r."},
            {CS, 2, "2 roky", "2 roky", "2 r."},
            {CS, 5, "5 let", "5 let", "5 l."},
            {CS, 21, "21 let", "21 let", "21 l."},
            {PL, 1, "1 rok", "1 rok", "1 r."},
            {PL, 2, "2 lata", "2 lata", "2 l."},
            {PL, 5, "5 lat", "5 lat", "5 l."},
            {PL, 21, "21 lat", "21 lat", "21 l."},
            {PL, 22, "22 lata", "22 lata", "22 l."},
            {RU, 1, "1 г.", "1 г.", "1 г."},
            {RU, 2, "2 г.", "2 г.", "2 г."},
            {RU, 5, "5 л.", "5 л.", "5 л."},
            {RU, 21, "21 г.", "21 г.", "21 г."},
            {RU, 22, "22 г.", "22 г.", "22 г."},
            {RU, 101, "101 г.", "101 г.", "101 г."},
        };
    }

    @ParameterizedTest
    @MethodSource("period_wordBased_style_plurals")
    public void test_wordBased_style_plurals(Locale locale, int years, String expectedLong, String expectedMedium, String expectedShort) {
        Period period = Period.ofYears(years);
        assertEquals(expectedLong, AmountFormats.wordBased(period, locale, FormatStyle.LONG), "LONG style");
        assertEquals(expectedMedium, AmountFormats.wordBased(period, locale, FormatStyle.MEDIUM), "MEDIUM style");
        assertEquals(expectedShort, AmountFormats.wordBased(period, locale, FormatStyle.SHORT), "SHORT style");
    }

    @Test
    public void test_wordBased_style_allLanguagesHaveAllWords() {
        // the two periods between them use every unit, as a period of 21 days formats as weeks
        Period[] periods = {Period.of(3, 4, 21), Period.of(3, 4, 5)};
        for (Locale locale : SUPPORTED) {
            for (FormatStyle style : FormatStyle.values()) {
                for (Period period : periods) {
                    String text = AmountFormats.wordBased(period, D_HMSM, locale, style);
                    assertFalse(text.isEmpty(), locale + " " + style);
                }
            }
        }
    }

    @Test
    public void test_wordBased_styleFullMatchesNoStyle() {
        Period period = Period.of(3, 4, 5);
        PeriodDuration periodDuration = PeriodDuration.of(period, D_HMSM);
        for (Locale locale : SUPPORTED) {
            String msg = locale.toString();
            assertEquals(AmountFormats.wordBased(period, locale),
                AmountFormats.wordBased(period, locale, FormatStyle.FULL), msg);
            assertEquals(AmountFormats.wordBased(D_HMSM, locale),
                AmountFormats.wordBased(D_HMSM, locale, FormatStyle.FULL), msg);
            assertEquals(AmountFormats.wordBased(period, D_HMSM, locale),
                AmountFormats.wordBased(period, D_HMSM, locale, FormatStyle.FULL), msg);
            assertEquals(AmountFormats.wordBased(periodDuration, locale),
                AmountFormats.wordBased(periodDuration, locale, FormatStyle.FULL), msg);
        }
    }

    @Test
    public void test_wordBased_style_nullStyle() {
        Period period = Period.ofDays(1);
        Duration duration = Duration.ofHours(1);
        PeriodDuration periodDuration = PeriodDuration.of(period, duration);
        assertThrows(NullPointerException.class,
            () -> AmountFormats.wordBased(period, Locale.ENGLISH, (FormatStyle) null));
        assertThrows(NullPointerException.class,
            () -> AmountFormats.wordBased(duration, Locale.ENGLISH, (FormatStyle) null));
        assertThrows(NullPointerException.class,
            () -> AmountFormats.wordBased(period, duration, Locale.ENGLISH, (FormatStyle) null));
        assertThrows(NullPointerException.class,
            () -> AmountFormats.wordBased(periodDuration, Locale.ENGLISH, (FormatStyle) null));
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

    public static @Nullable Object[][] duration_unitBasedErrors() {
        return new @Nullable Object[][] {
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
