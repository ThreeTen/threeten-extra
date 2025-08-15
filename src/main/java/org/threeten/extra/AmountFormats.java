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
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
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
     * The predicate that matches numbers ending 1 but not ending 11.
     */
    private static final IntPredicate PREDICATE_END1_NOT11 = value -> {
        int abs = Math.abs(value);
        int last = abs % 10;
        int secondLast = (abs % 100) / 10;
        return (last == 1 && secondLast != 1);
    };
    /**
     * The predicate that matches numbers ending 2, 3 or 4, but not ending 12, 13 or 14.
     */
    private static final IntPredicate PREDICATE_END234_NOTTEENS = value -> {
        int abs = Math.abs(value);
        int last = abs % 10;
        int secondLast = (abs % 100) / 10;
        return (last >= 2 && last <= 4 && secondLast != 1);
    };
    /**
     * List of DurationUnit values ordered by longest suffix first.
     */
    private static final List<DurationUnit> DURATION_UNITS =
            Arrays.asList(new DurationUnit("ns", Duration.ofNanos(1)),
                    new DurationUnit("µs", Duration.ofNanos(1000)), // U+00B5 = micro symbol
                    new DurationUnit("μs", Duration.ofNanos(1000)), // U+03BC = Greek letter mu
                    new DurationUnit("us", Duration.ofNanos(1000)),
                    new DurationUnit("ms", Duration.ofMillis(1)),
                    new DurationUnit("s", Duration.ofSeconds(1)),
                    new DurationUnit("m", Duration.ofMinutes(1)),
                    new DurationUnit("h", Duration.ofHours(1)));
    /**
     * Zero value for an absent fractional component of a numeric duration string.
     */
    private static final FractionScalarPart EMPTY_FRACTION = new FractionScalarPart(0, 0);

    //-----------------------------------------------------------------------
    /**
     * Formats a period and duration to a string in ISO-8601 format.
     * <p>
     * To obtain the ISO-8601 format of a {@link Period}, {@link Duration} or {@link PeriodDuration}
     * simply call {@code toString()}.
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
     * The year and month are printed as supplied unless the signs differ, in which case they are normalized.
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

        Period normPeriod = oppositeSigns(period.getMonths(), period.getYears()) ? period.normalized() : period;
        int weeks = 0;
        int days = 0;
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
     * The year and month are printed as supplied unless the signs differ, in which case they are normalized.
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

        Period normPeriod = oppositeSigns(period.getMonths(), period.getYears()) ? period.normalized() : period;
        int weeks = 0;
        int days = 0;
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

    // are the signs opposite
    private static boolean oppositeSigns(int a, int b) {
        return a < 0 ? (b >= 0) : (b < 0);
    }

    /**
     * Formats a period-duration to a string in a localized word-based format.
     * <p>
     * This returns a word-based format for the period-duration.
     * The year and month are printed as supplied unless the signs differ, in which case they are normalized.
     * The words are configured in a resource bundle text file -
     * {@code org.threeten.extra.wordbased.properties} - with overrides per language.
     *
     * @param periodDuration  the period-duration to format
     * @param locale  the locale to use
     * @return the localized word-based format for the period-duration
     */
    public static String wordBased(PeriodDuration periodDuration, Locale locale) {
        Objects.requireNonNull(periodDuration, "periodDuration must not be null");
        Objects.requireNonNull(locale, "locale must not be null");
        return wordBased(periodDuration.getPeriod(), periodDuration.getDuration(), locale);
    }

    // -------------------------------------------------------------------------
    /**
     * Parses formatted durations based on units.
     * <p>
     * The behaviour matches the <a href="https://golang.org/pkg/time/#ParseDuration">Golang</a>
     * duration parser, however, infinite durations are not supported.
     * <p>
     * The duration format is a possibly signed sequence of decimal numbers, each with optional
     * fraction and a unit suffix, such as "300ms", "-1.5h" or "2h45m". Valid time units are
     * "ns", "us" (or "µs"), "ms", "s", "m", "h".
     * <p>
     * Note, the value "0" is specially supported as {@code Duration.ZERO}.
     *
     * @param durationText the formatted unit-based duration string.
     * @return the {@code Duration} value represented by the string, if possible.
     */
    public static Duration parseUnitBasedDuration(CharSequence durationText) {
        Objects.requireNonNull(durationText, "durationText must not be null");

        // variables for tracking error positions during parsing.
        int offset = 0;
        CharSequence original = durationText;

        // consume the leading sign - or + if one is present.
        int sign = 1;
        Optional<CharSequence> updatedText = consumePrefix(durationText, '-');
        if (updatedText.isPresent()) {
            sign = -1;
            offset += 1;
            durationText = updatedText.get();
        } else {
            updatedText = consumePrefix(durationText, '+');
            if (updatedText.isPresent()) {
                offset += 1;
            }
            durationText = updatedText.orElse(durationText);
        }
        // special case for a string of "0"
        if (durationText.equals("0")) {
            return Duration.ZERO;
        }
        // special case, empty string as an invalid duration.
        if (durationText.length() == 0) {
            throw new DateTimeParseException("Not a numeric value", original, 0);
        }

        Duration value = Duration.ZERO;
        int durationTextLength = durationText.length();
        while (durationTextLength > 0) {
            ParsedUnitPart integerPart =
                consumeDurationLeadingInt(durationText, original, offset);
            offset += (durationText.length() - integerPart.remainingText().length());
            durationText = integerPart.remainingText();
            DurationScalar leadingInt = integerPart;
            DurationScalar fraction = EMPTY_FRACTION;
            Optional<CharSequence> dot = consumePrefix(durationText, '.');
            if (dot.isPresent()) {
                offset += 1;
                durationText = dot.get();
                ParsedUnitPart fractionPart =
                    consumeDurationFraction(durationText, original, offset);
                // update the remaining string and fraction.
                offset += (durationText.length() - fractionPart.remainingText().length());
                durationText = fractionPart.remainingText();
                fraction = fractionPart;
            }

            Optional<DurationUnit> optUnit = findUnit(durationText);
            if (!optUnit.isPresent()) {
                throw new DateTimeParseException(
                    "Invalid duration unit", original, offset);
            }
            DurationUnit unit = optUnit.get();
            try {
                Duration unitValue = leadingInt.applyTo(unit);
                Duration fractionValue = fraction.applyTo(unit);
                unitValue = unitValue.plus(fractionValue);
                value = value.plus(unitValue);
            } catch (ArithmeticException e) {
                throw new DateTimeParseException(
                    "Duration string exceeds valid numeric range",
                    original, offset, e);
            }
            // update the remaining text and text length.
            CharSequence remainingText = unit.consumeDurationUnit(durationText);
            offset += (durationText.length() - remainingText.length());
            durationText = remainingText;
            durationTextLength = durationText.length();
        }
        return sign < 0 ? value.negated() : value;
    }

    // consume the fractional part of a unit-based duration, e.g.
    // <int>.<fraction><unit>.
    private static ParsedUnitPart consumeDurationLeadingInt(CharSequence text,
        CharSequence original, int offset) {
        long integerPart = 0;
        int i = 0;
        int valueLength = text.length();
        for ( ; i < valueLength; i++) {
            char c = text.charAt(i);
            if (c < '0' || c > '9') {
                break;
            }
            // overflow of a single numeric specifier for a duration.
            if (integerPart > Long.MAX_VALUE / 10) {
                throw new DateTimeParseException(
                    "Duration string exceeds valid numeric range",
                    original, i + offset);
            }
            integerPart *= 10;
            integerPart += (long) (c - '0');
            // overflow of a single numeric specifier for a duration.
            if (integerPart < 0) {
                throw new DateTimeParseException(
                    "Duration string exceeds valid numeric range",
                    original, i + offset);
            }
        }
        // if no text was consumed, return empty.
        if (i == 0) {
            throw new DateTimeParseException("Missing leading integer", original, offset);
        }
        return new ParsedUnitPart(text.subSequence(i, text.length()),
            new IntegerScalarPart(integerPart));
    }

    // consume the fractional part of a unit-based duration, e.g.
    // <int>.<fraction><unit>.
    private static ParsedUnitPart consumeDurationFraction(CharSequence text,
        CharSequence original, int offset) {
        int i = 0;
        long fraction = 0;
        long scale = 1;
        boolean overflow = false;
        for ( ; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < '0' || c > '9') {
                break;
            }
            // for the fractional part, it's possible to overflow; however,
            // this does not invalidate the duration, but rather it means that
            // the precision of the fractional part is truncated to 999,999,999.
            if (overflow || fraction > Long.MAX_VALUE / 10) {
                continue;
            }
            long tmp = fraction * 10 + (long) (c - '0');
            if (tmp < 0) {
                overflow = true;
                continue;
            }
            fraction = tmp;
            scale *= 10;
        }
        if (i == 0) {
            throw new DateTimeParseException(
                "Missing numeric fraction after '.'", original, offset);
        }
        return new ParsedUnitPart(text.subSequence(i, text.length()),
            new FractionScalarPart(fraction, scale));
    }

    // find the duration unit at the beginning of the input text, if present.
    private static Optional<DurationUnit> findUnit(CharSequence text) {
        return DURATION_UNITS.stream()
            .sequential()
            .filter(du -> du.prefixMatchesUnit(text))
            .findFirst();
    }

    // consume the indicated {@code prefix} if it exists at the beginning of the
    // text, returning the
    // remaining string if the prefix was consumed.
    private static Optional<CharSequence> consumePrefix(CharSequence text, char prefix) {
        if (text.length() > 0 && text.charAt(0) == prefix) {
            return Optional.of(text.subSequence(1, text.length()));
        }
        return Optional.empty();
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
                case "End1Not11": return PREDICATE_END1_NOT11;
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
        }
    }

     // -------------------------------------------------------------------------
    // data holder for a duration unit string and its associated Duration value.
    static final class DurationUnit {
        private final String abbrev;
        private final Duration value;

        private DurationUnit(String abbrev, Duration value) {
            this.abbrev = abbrev;
            this.value = value;
        }

        // whether the input text starts with the unit abbreviation.
        boolean prefixMatchesUnit(CharSequence text) {
            return text.length() >= abbrev.length()
                    && abbrev.equals(text.subSequence(0, abbrev.length()));
        }

        // consume the duration unit and returning the remaining text.
        CharSequence consumeDurationUnit(CharSequence text) {
            return text.subSequence(abbrev.length(), text.length());
        }

        // scale the unit by the input scalingFunction, returning a value if
        // one is produced, or an empty result when the operation results in an
        // arithmetic overflow.
        Duration scaleBy(Function<Duration, Duration> scaleFunc) {
            return scaleFunc.apply(value);
        }
    }

    // interface for computing a duration from a duration unit and a scalar.
    static interface DurationScalar {
        // returns a duration value on a successful computation, and an empty
        // result otherwise.
        Duration applyTo(DurationUnit unit);
    }

    // data holder for parsed fragments of a floating point duration scalar.
    static final class ParsedUnitPart implements DurationScalar {
        private final CharSequence remainingText;
        private final DurationScalar scalar;

        private ParsedUnitPart(CharSequence remainingText, DurationScalar scalar) {
            this.remainingText = remainingText;
            this.scalar = scalar;
        }

        @Override
        public Duration applyTo(DurationUnit unit) {
            return scalar.applyTo(unit);
        }

        CharSequence remainingText() {
            return remainingText;
        }
    }

    // data holder for the leading integer value of a duration scalar.
    static final class IntegerScalarPart implements DurationScalar {
        private final long value;

        private IntegerScalarPart(long value) {
            this.value = value;
        }

        @Override
        public Duration applyTo(DurationUnit unit) {
            return unit.scaleBy(d -> d.multipliedBy(value));
        }
    }

    // data holder for the fractional floating point value of a duration
    // scalar.
    static final class FractionScalarPart implements DurationScalar {
        private final long value;
        private final long scale;

        private FractionScalarPart(long value, long scale) {
            this.value = value;
            this.scale = scale;
        }

        @Override
        public Duration applyTo(DurationUnit unit) {
            if (value == 0) {
                return Duration.ZERO;
            }
            return unit.scaleBy(d -> d.multipliedBy(value).dividedBy(scale));
        }
    }
}
