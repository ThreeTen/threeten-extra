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

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Provides the ability to format a temporal amount.
 * <p>
 * This allows a {@link TemporalAmount}, such as {@link Duration} or {@link Period},
 * to be formatted. Only selected formatting options are provided.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 */
public final class AmountFormats {

    /**
     * The number of days per week.
     */
    private static final int DAYS_PER_WEEK = 7;
    /**
     * The number of hours per day.
     */
    private static final int HOURS_PER_DAY = 24;
    /**
     * The number of minutes per hour.
     */
    private static final int MINUTES_PER_HOUR = 60;
    /**
     * The number of seconds per minute.
     */
    private static final int SECONDS_PER_MINUTE = 60;
    /**
     * The number of nanosecond per millisecond.
     */
    private static final int NANOS_PER_MILLIS = 1000_000;
    /**
     * The resource bundle name.
     */
    private static final String BUNDLE_NAME = "org.threeten.extra.wordbased";
    /**
     * The pattern to split lists with.
     */
    private static final Pattern SPLITTER = Pattern.compile("[|][|][|]");
    /**
     * The property file key for the separator ", ".
     */
    private static final String WORDBASED_COMMASPACE = "WordBased.commaspace";
    /**
     * The property file key for the separator " and ".
     */
    private static final String WORDBASED_SPACEANDSPACE = "WordBased.spaceandspace";
    /**
     * The property file key for the word "year".
     */
    private static final String WORDBASED_YEAR = "WordBased.year";
    /**
     * The property file key for the word "month".
     */
    private static final String WORDBASED_MONTH = "WordBased.month";
    /**
     * The property file key for the word "week".
     */
    private static final String WORDBASED_WEEK = "WordBased.week";
    /**
     * The property file key for the word "day".
     */
    private static final String WORDBASED_DAY = "WordBased.day";
    /**
     * The property file key for the word "hour".
     */
    private static final String WORDBASED_HOUR = "WordBased.hour";
    /**
     * The property file key for the word "minute".
     */
    private static final String WORDBASED_MINUTE = "WordBased.minute";
    /**
     * The property file key for the word "second".
     */
    private static final String WORDBASED_SECOND = "WordBased.second";
    /**
     * The property file key for the word "millisecond".
     */
    private static final String WORDBASED_MILLISECOND = "WordBased.millisecond";
    /**
     * The predicate that matches 1 or -1.
     */
    private static final IntPredicate PREDICATE_1 = value -> value == 1 || value == -1;
    /**
     * The predicate that matches numbers ending 2, 3 or 4, but not ending 12, 13 or 14.
     */
    private static final IntPredicate PREDICATE_END234_NOTTEENS = value -> {
        int abs = Math.abs(value);
        int last = abs % 10;
        int secondLast = (abs % 100) / 10;
        return (last >= 2 && last <= 4 && secondLast != 1);
    };

    //-----------------------------------------------------------------------
    /**
     * Formats a period and duration to a string in ISO-8601 format.
     * <p>
     * To obtain the ISO-8601 format of a {@code Period} or {@code Duration}
     * individually, simply call {@code toString()}.
     * See also {@link PeriodDuration}.
     * 
     * @param period  the period to format
     * @param duration  the duration to format
     * @return the ISO-8601 format for the period and duration
     */
    public static String iso8601(Period period, Duration duration) {
        Objects.requireNonNull(period, "period must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
        if (period.isZero()) {
            return duration.toString();
        }
        if (duration.isZero()) {
            return period.toString();
        }
        return period.toString() + duration.toString().substring(1);
    }

    //-------------------------------------------------------------------------
    /**
     * Formats a period to a string in a localized word-based format.
     * <p>
     * This returns a word-based format for the period.
     * The words are configured in a resource bundle text file -
     * {@code org.threeten.extra.wordbased.properties} - with overrides per language.
     *
     * @param period  the period to format
     * @param locale  the locale to use
     * @return the localized word-based format for the period
     */
    public static String wordBased(Period period, Locale locale) {
        Objects.requireNonNull(period, "period must not be null");
        Objects.requireNonNull(locale, "locale must not be null");
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        UnitFormat[] formats = {
            UnitFormat.of(bundle, WORDBASED_YEAR),
            UnitFormat.of(bundle, WORDBASED_MONTH),
            UnitFormat.of(bundle, WORDBASED_WEEK),
            UnitFormat.of(bundle, WORDBASED_DAY)};
        WordBased wb = new WordBased(formats, bundle.getString(WORDBASED_COMMASPACE), bundle.getString(WORDBASED_SPACEANDSPACE));
        
        Period normPeriod = period.normalized();
        int weeks = 0, days = 0;
        if (normPeriod.getDays() % DAYS_PER_WEEK == 0) {
            weeks = normPeriod.getDays() / DAYS_PER_WEEK;
        } else {
            days = normPeriod.getDays();
        }
        int[] values = {normPeriod.getYears(), normPeriod.getMonths(), weeks, days};
        return wb.format(values);
    }

    /**
     * Formats a duration to a string in a localized word-based format.
     * <p>
     * This returns a word-based format for the duration.
     * The words are configured in a resource bundle text file -
     * {@code org.threeten.extra.wordbased.properties} - with overrides per language.
     *
     * @param duration  the duration to format
     * @param locale  the locale to use
     * @return the localized word-based format for the duration
     */
    public static String wordBased(Duration duration, Locale locale) {
        Objects.requireNonNull(duration, "duration must not be null");
        Objects.requireNonNull(locale, "locale must not be null");
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        UnitFormat[] formats = {
            UnitFormat.of(bundle, WORDBASED_HOUR),
            UnitFormat.of(bundle, WORDBASED_MINUTE),
            UnitFormat.of(bundle, WORDBASED_SECOND),
            UnitFormat.of(bundle, WORDBASED_MILLISECOND)};
        WordBased wb = new WordBased(formats, bundle.getString(WORDBASED_COMMASPACE), bundle.getString(WORDBASED_SPACEANDSPACE));
        
        long hours = duration.toHours();
        long mins = duration.toMinutes() % MINUTES_PER_HOUR;
        long secs = duration.getSeconds() % SECONDS_PER_MINUTE;
        int millis = duration.getNano() / NANOS_PER_MILLIS;
        int[] values = {(int) hours, (int) mins, (int) secs, millis};
        return wb.format(values);
    }

    /**
     * Formats a period and duration to a string in a localized word-based format.
     * <p>
     * This returns a word-based format for the period.
     * The words are configured in a resource bundle text file -
     * {@code org.threeten.extra.wordbased.properties} - with overrides per language.
     *
     * @param period  the period to format
     * @param duration  the duration to format
     * @param locale  the locale to use
     * @return the localized word-based format for the period and duration
     */
    public static String wordBased(Period period, Duration duration, Locale locale) {
        Objects.requireNonNull(period, "period must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
        Objects.requireNonNull(locale, "locale must not be null");
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        UnitFormat[] formats = {
            UnitFormat.of(bundle, WORDBASED_YEAR),
            UnitFormat.of(bundle, WORDBASED_MONTH),
            UnitFormat.of(bundle, WORDBASED_WEEK),
            UnitFormat.of(bundle, WORDBASED_DAY),
            UnitFormat.of(bundle, WORDBASED_HOUR),
            UnitFormat.of(bundle, WORDBASED_MINUTE),
            UnitFormat.of(bundle, WORDBASED_SECOND),
            UnitFormat.of(bundle, WORDBASED_MILLISECOND)};
        WordBased wb = new WordBased(formats, bundle.getString(WORDBASED_COMMASPACE), bundle.getString(WORDBASED_SPACEANDSPACE));
        
        Period normPeriod = period.normalized();
        int weeks = 0, days = 0;
        if (normPeriod.getDays() % DAYS_PER_WEEK == 0) {
            weeks = normPeriod.getDays() / DAYS_PER_WEEK;
        } else {
            days = normPeriod.getDays();
        }
        long totalHours = duration.toHours();
        days += (int) (totalHours / HOURS_PER_DAY);
        int hours = (int) (totalHours % HOURS_PER_DAY);
        int mins = (int) (duration.toMinutes() % MINUTES_PER_HOUR);
        int secs = (int) (duration.getSeconds() % SECONDS_PER_MINUTE);
        int millis = duration.getNano() / NANOS_PER_MILLIS;
        int[] values = {
            normPeriod.getYears(), normPeriod.getMonths(), weeks, days,
            (int) hours, mins, secs, millis};
        return wb.format(values);
    }

    private AmountFormats() {
    }

    //-------------------------------------------------------------------------
    // data holder for word-based formats
    static final class WordBased {
        private final UnitFormat[] units;
        private final String separator;
        private final String lastSeparator;

        public WordBased(UnitFormat[] units, String separator, String lastSeparator) {
            this.units = units;
            this.separator = separator;
            this.lastSeparator = lastSeparator;
        }

        String format(int[] values) {
            StringBuilder buf = new StringBuilder(32);
            int nonZeroCount = 0;
            for (int i = 0; i < values.length; i++) {
                if (values[i] != 0) {
                    nonZeroCount++;
                }
            }
            int count = 0;
            for (int i = 0; i < values.length; i++) {
                if (values[i] != 0 || (count == 0 && i == values.length - 1)) {
                    units[i].formatTo(values[i], buf);
                    if (count < nonZeroCount - 2) {
                        buf.append(separator);
                    } else if (count == nonZeroCount - 2) {
                        buf.append(lastSeparator);
                    }
                    count++;
                }
            }
            return buf.toString();
        }
    }

    // data holder for single/plural formats
    static interface UnitFormat {
        
        static UnitFormat of(ResourceBundle bundle, String keyStem) {
            if (bundle.containsKey(keyStem + "s.predicates")) {
                String predicateList = bundle.getString(keyStem + "s.predicates");
                String textList = bundle.getString(keyStem + "s.list");
                String[] regexes = SPLITTER.split(predicateList);
                String[] text = SPLITTER.split(textList);
                return new PredicateFormat(regexes, text);
            } else {
                String single = bundle.getString(keyStem);
                String plural = bundle.getString(keyStem + "s");
                return new SinglePluralFormat(single, plural);
            }
        }
        
        void formatTo(int value, StringBuilder buf);
    }

    // data holder for single/plural formats
    static final class SinglePluralFormat implements UnitFormat {
        private final String single;
        private final String plural;

        SinglePluralFormat(String single, String plural) {
            this.single = single;
            this.plural = plural;
        }

        @Override
        public void formatTo(int value, StringBuilder buf) {
            buf.append(value).append(value == 1 || value == -1 ? single : plural);
        }
    }

    // data holder for predicate formats
    static final class PredicateFormat implements UnitFormat {
        private final IntPredicate[] predicates;
        private final String[] text;

        PredicateFormat(String[] predicateStrs, String[] text) {
            if (predicateStrs.length + 1 != text.length) {
                throw new IllegalStateException("Invalid word-based resource");
            }
            this.predicates = Stream.of(predicateStrs)
                    .map(predicateStr -> findPredicate(predicateStr))
                    .toArray(IntPredicate[]::new);
            this.text = text;
        }

        private IntPredicate findPredicate(String predicateStr) {
            switch (predicateStr) {
                case "One": return PREDICATE_1;
                case "End234NotTeens": return PREDICATE_END234_NOTTEENS;
                default: throw new IllegalStateException("Invalid word-based resource");
            }
        }

        @Override
        public void formatTo(int value, StringBuilder buf) {
            for (int i = 0; i < predicates.length; i++) {
                if (predicates[i].test(value)) {
                    buf.append(value).append(text[i]);
                    return;
                }
            }
            buf.append(value).append(text[predicates.length]);
            return;
        }
    }

}
