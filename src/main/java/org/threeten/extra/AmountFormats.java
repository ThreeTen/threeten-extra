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
     * The number of minutes per hour.
     */
    private static final int MINUTES_PER_HOUR = 60;
    
    /**
     * The number of seconds per minute.
     */
    private static final int SECONDS_PER_MINUTE = 60;
    
    /**
     * The resource bundle name.
     */
    private static final String BUNDLE_NAME = "org.threeten.extra.wordbased";
   
    /**
     * the property file key for the separator " "
     */
    private static final String WORDBASED_SPACE = "WordBased.space";
    
    /**
     * the property file key for the separator ", "
     */
    private static final String WORDBASED_COMMASPACE = "WordBased.commaspace";
    
    /**
     * the property file key for the separator " and "
     */
    private static final String WORDBASED_SPACEANDSPACE = "WordBased.spaceandspace";
    
    /**
     * the property file key for the word "year"
     */
    private static final String WORDBASED_YEAR = "WordBased.year";
    
    /**
     * the property file key for the word "years"
     */
    private static final String WORDBASED_YEARS = "WordBased.years";
    
    /**
     * the property file key for the word "month"
     */
    private static final String WORDBASED_MONTH = "WordBased.month";
    
    /**
     * the property file key for the word "months"
     */
    private static final String WORDBASED_MONTHS = "WordBased.months";
    
    /**
     * the property file key for the word "day"
     */
    private static final String WORDBASED_DAY = "WordBased.day";
    
    /**
     * the property file key for the word "days"
     */
    private static final String WORDBASED_DAYS = "WordBased.days";
    
    /**
     * the property file key for the word "hour"
     */
    private static final String WORDBASED_HOUR = "WordBased.hour";
    
    /**
     * the property file key for the word "hours"
     */
    private static final String WORDBASED_HOURS = "WordBased.hours";
    
    /**
     * the property file key for the word "minute"
     */
    private static final String WORDBASED_MINUTE  = "WordBased.minute";
    
    /**
     * the property file key for the word "minutes"
     */
    private static final String WORDBASED_MINUTES  = "WordBased.minutes";
    
    /**
     * the property file key for the word "second"
     */
    private static final String WORDBASED_SECOND = "WordBased.second";
    
    /**
     * the property file key for the word "seconds"
     */
    private static final String WORDBASED_SECONDS = "WordBased.seconds";
    
    /**
     * the property file key for the word "millisecond"
     */
    private static final String WORDBASED_MILLISECOND = "WordBased.millisecond";
    
    /**
     * the property file key for the word "milliseconds"
     */
    private static final String WORDBASED_MILLISECONDS = "WordBased.milliseconds";
    
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
     * Formats a period and duration to a string in a localized word-based format.
     * <p>
     * This calls {@link #wordBased(Period, Locale)} using the {@link Locale#getDefault() default locale}.
     *
     * @param period  the period to format
     * @return the localized word-based format for the period and duration
     */
    public static String wordBased(Period period) {
        Objects.requireNonNull(period, "period must not be null");
        return wordBased(period, Locale.getDefault());
    }

    /**
     * Formats a period and duration to a string in a localized word-based format.
     * <p>
     * This returns a word-based format for the period.
     * The words are configured in a resource bundle text file -
     * {@code org.threeten.extra.wordbased.properties} - with overrides per language.
     *
     * @param period  the period to format
     * @param locale  the locale to use
     * @return the localized word-based format for the period and duration
     */
    public static String wordBased(Period period, Locale locale) {
        Objects.requireNonNull(period, "period must not be null");
        Objects.requireNonNull(locale, "locale must not be null");
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        UnitFormat[] formats = {
            new UnitFormat(bundle.getString(WORDBASED_YEAR), bundle.getString(WORDBASED_YEARS)),
            new UnitFormat(bundle.getString(WORDBASED_MONTH), bundle.getString(WORDBASED_MONTHS)),
            new UnitFormat(bundle.getString(WORDBASED_DAY), bundle.getString(WORDBASED_DAYS))};
        WordBased wb = new WordBased(formats, bundle.getString(WORDBASED_COMMASPACE), bundle.getString(WORDBASED_SPACEANDSPACE));
        Period normPeriod = period.normalized();
        int[] values = {normPeriod.getYears(), normPeriod.getMonths(), normPeriod.getDays()};
        return wb.format(values);
        //        if (bundle.containsKey("WordBased.regex.separator")) {
        //            return buildRegExFormatter(bundle, locale);
        //        } else {
        //        }
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
        	new UnitFormat(bundle.getString(WORDBASED_HOUR), bundle.getString(WORDBASED_HOURS)),
        	new UnitFormat(bundle.getString(WORDBASED_MINUTE), bundle.getString(WORDBASED_MINUTES)),
            new UnitFormat(bundle.getString(WORDBASED_SECOND), bundle.getString(WORDBASED_SECONDS)) };
        WordBased wb = new WordBased(formats, bundle.getString(WORDBASED_SPACE), bundle.getString(WORDBASED_SPACE));
        long hours = duration.toHours()<0?0:duration.toHours();
        long mins = duration.toMinutes() % MINUTES_PER_HOUR;
        long secs = duration.getSeconds() % SECONDS_PER_MINUTE;
        int[] values = {(int)hours, (int)mins, (int)secs};
        return wb.format(values);
    }
    
    private AmountFormats() {
    }

    //-------------------------------------------------------------------------
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

    static final class UnitFormat {
        private final String single;
        private final String plural;

        public UnitFormat(String single, String plural) {
            this.single = single;
            this.plural = plural;
        }

        void formatTo(int value, StringBuilder buf) {
            buf.append(value).append(value == 1 || value == -1 ? single : plural);
        }
    }

    //    /**
    //     * Returns a word based formatter for the specified locale.
    //     * <p>
    //     * The words are configured in a resource bundle text file -
    //     * {@code org.joda.time.format.messages}.
    //     * This can be added to via the normal classpath resource bundle mechanisms.
    //     * <p>
    //     * You can add your own translation by creating messages_<locale>.properties file
    //     * and adding it to the {@code org.joda.time.format.messages} path.
    //     * <p>
    //     * Simple example (1 -> singular suffix, not 1 -> plural suffix):
    //     * 
    //     * <pre>
    //     * PeriodFormat.space=\ 
    //     * PeriodFormat.comma=,
    //     * PeriodFormat.commandand=,and 
    //     * PeriodFormat.commaspaceand=, and 
    //     * PeriodFormat.commaspace=, 
    //     * PeriodFormat.spaceandspace=\ and 
    //     * PeriodFormat.year=\ year
    //     * PeriodFormat.years=\ years
    //     * PeriodFormat.month=\ month
    //     * PeriodFormat.months=\ months
    //     * PeriodFormat.week=\ week
    //     * PeriodFormat.weeks=\ weeks
    //     * PeriodFormat.day=\ day
    //     * PeriodFormat.days=\ days
    //     * PeriodFormat.hour=\ hour
    //     * PeriodFormat.hours=\ hours
    //     * PeriodFormat.minute=\ minute
    //     * PeriodFormat.minutes=\ minutes
    //     * PeriodFormat.second=\ second
    //     * PeriodFormat.seconds=\ seconds
    //     * PeriodFormat.millisecond=\ millisecond
    //     * PeriodFormat.milliseconds=\ milliseconds
    //     * </pre>
    //     * 
    //     * <p>
    //     * Some languages contain more than two suffixes. You can use regular expressions
    //     * for them. Here's an example using regular expression for English:
    //     * 
    //     * <pre>
    //     * PeriodFormat.space=\ 
    //     * PeriodFormat.comma=,
    //     * PeriodFormat.commandand=,and 
    //     * PeriodFormat.commaspaceand=, and 
    //     * PeriodFormat.commaspace=, 
    //     * PeriodFormat.spaceandspace=\ and 
    //     * PeriodFormat.regex.separator=%
    //     * PeriodFormat.years.regex=1$%.*
    //     * PeriodFormat.years.list=\ year%\ years
    //     * PeriodFormat.months.regex=1$%.*
    //     * PeriodFormat.months.list=\ month%\ months
    //     * PeriodFormat.weeks.regex=1$%.*
    //     * PeriodFormat.weeks.list=\ week%\ weeks
    //     * PeriodFormat.days.regex=1$%.*
    //     * PeriodFormat.days.list=\ day%\ days
    //     * PeriodFormat.hours.regex=1$%.*
    //     * PeriodFormat.hours.list=\ hour%\ hours
    //     * PeriodFormat.minutes.regex=1$%.*
    //     * PeriodFormat.minutes.list=\ minute%\ minutes
    //     * PeriodFormat.seconds.regex=1$%.*
    //     * PeriodFormat.seconds.list=\ second%\ seconds
    //     * PeriodFormat.milliseconds.regex=1$%.*
    //     * PeriodFormat.milliseconds.list=\ millisecond%\ milliseconds
    //     * </pre>
    //     * 
    //     * <p>
    //     * You can mix both approaches. Here's example for Polish (
    //     * "1 year, 2 years, 5 years, 12 years, 15 years, 21 years, 22 years, 25 years"
    //     * translates to
    //     * "1 rok, 2 lata, 5 lat, 12 lat, 15 lat, 21 lat, 22 lata, 25 lat"). Notice that
    //     * PeriodFormat.day and PeriodFormat.days is used for day suffixes as there is no
    //     * need for regular expressions:
    //     * 
    //     * <pre>
    //     * PeriodFormat.space=\ 
    //     * PeriodFormat.comma=,
    //     * PeriodFormat.commandand=,i 
    //     * PeriodFormat.commaspaceand=, i 
    //     * PeriodFormat.commaspace=, 
    //     * PeriodFormat.spaceandspace=\ i 
    //     * PeriodFormat.regex.separator=%
    //     * PeriodFormat.years.regex=^1$%[0-9]*(?&lt;!1)[2-4]$%[0-9]*
    //     * PeriodFormat.years.list=\ rok%\ lata%\ lat
    //     * PeriodFormat.months.regex=^1$%[0-9]*(?&lt;!1)[2-4]$%[0-9]*
    //     * PeriodFormat.months.list=\ miesi\u0105c%\ miesi\u0105ce%\ miesi\u0119cy
    //     * PeriodFormat.weeks.regex=^1$%[0-9]*(?&lt;!1)[2-4]$%[0-9]*
    //     * PeriodFormat.weeks.list=\ tydzie\u0144%\ tygodnie%\ tygodni
    //     * PeriodFormat.day=\ dzie\u0144
    //     * PeriodFormat.days=\ dni
    //     * PeriodFormat.hours.regex=^1$%[0-9]*(?&lt;!1)[2-4]$%[0-9]*
    //     * PeriodFormat.hours.list=\ godzina%\ godziny%\ godzin
    //     * PeriodFormat.minutes.regex=^1$%[0-9]*(?&lt;!1)[2-4]$%[0-9]*
    //     * PeriodFormat.minutes.list=\ minuta%\ minuty%\ minut
    //     * PeriodFormat.seconds.regex=^1$%[0-9]*(?&lt;!1)[2-4]$%[0-9]*
    //     * PeriodFormat.seconds.list=\ sekunda%\ sekundy%\ sekund
    //     * PeriodFormat.milliseconds.regex=^1$%[0-9]*(?&lt;!1)[2-4]$%[0-9]*
    //     * PeriodFormat.milliseconds.list=\ milisekunda%\ milisekundy%\ milisekund
    //     * </pre>
    //     * 
    //     * <p>
    //     * Each PeriodFormat.&lt;duration_field_type&gt;.regex property stands for an array of
    //     * regular expressions and is followed by a property
    //     * PeriodFormat.&lt;duration_field_type&gt;.list holding an array of suffixes.
    //     * PeriodFormat.regex.separator is used for splitting. See
    //     * {@link PeriodFormatterBuilder#appendSuffix(String[], String[])} for details.
    //     * <p>
    //     * Available languages are English, Danish, Dutch, French, German, Japanese,
    //     * Polish, Portuguese and Spanish.
    //     * 
    //     * @return the formatter, not null
    //     * @since 2.0, regex since 2.5
    //     */
    //    public static PeriodFormatter wordBased(Locale locale) {
    //        PeriodFormatter pf = FORMATTERS.get(locale);
    //        if (pf == null) {
    //            DynamicWordBased dynamic = new DynamicWordBased(buildWordBased(locale));
    //            pf = new PeriodFormatter(dynamic, dynamic, locale, null);
    //            PeriodFormatter existing = FORMATTERS.putIfAbsent(locale, pf);
    //            if (existing != null) {
    //                pf = existing;
    //            }
    //        }
    //        return pf;
    //    }
    //
    //    //-----------------------------------------------------------------------
    //    private static PeriodFormatter buildWordBased(Locale locale) {
    //        ResourceBundle b = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    //        if (containsKey(b, "PeriodFormat.regex.separator")) {
    //            return buildRegExFormatter(b, locale);
    //        } else {
    //            return buildNonRegExFormatter(b, locale);
    //        }
    //    }
    //
    //    private static PeriodFormatter buildRegExFormatter(ResourceBundle b, Locale locale) {
    //        String[] variants = retrieveVariants(b);
    //        String regExSeparator = b.getString("PeriodFormat.regex.separator");
    //        
    //        PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
    //        builder.appendYears();
    //        if (containsKey(b, "PeriodFormat.years.regex")) {
    //            builder.appendSuffix(
    //                b.getString("PeriodFormat.years.regex").split(regExSeparator),
    //                b.getString("PeriodFormat.years.list").split(regExSeparator));
    //        } else {
    //            builder.appendSuffix(b.getString("PeriodFormat.year"), b.getString("PeriodFormat.years"));
    //        }
    //
    //        builder.appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants);
    //        builder.appendMonths();
    //        if (containsKey(b, "PeriodFormat.months.regex")) {
    //            builder.appendSuffix(
    //                    b.getString("PeriodFormat.months.regex").split(regExSeparator),
    //                    b.getString("PeriodFormat.months.list").split(regExSeparator));
    //        } else {
    //            builder.appendSuffix(b.getString("PeriodFormat.month"), b.getString("PeriodFormat.months"));
    //        }        
    //
    //        builder.appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants);
    //        builder.appendWeeks();
    //        if (containsKey(b, "PeriodFormat.weeks.regex")) {
    //            builder.appendSuffix(
    //                    b.getString("PeriodFormat.weeks.regex").split(regExSeparator),
    //                    b.getString("PeriodFormat.weeks.list").split(regExSeparator));
    //        } else {
    //            builder.appendSuffix(b.getString("PeriodFormat.week"), b.getString("PeriodFormat.weeks"));
    //        }            
    //
    //        builder.appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants);
    //        builder.appendDays();
    //        if (containsKey(b, "PeriodFormat.days.regex")) {
    //            builder.appendSuffix(
    //                    b.getString("PeriodFormat.days.regex").split(regExSeparator),
    //                    b.getString("PeriodFormat.days.list").split(regExSeparator));
    //        } else {
    //            builder.appendSuffix(b.getString("PeriodFormat.day"), b.getString("PeriodFormat.days"));
    //        }            
    //
    //        builder.appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants);
    //        builder.appendHours();
    //        if (containsKey(b, "PeriodFormat.hours.regex")) {
    //            builder.appendSuffix(
    //                    b.getString("PeriodFormat.hours.regex").split(regExSeparator),
    //                    b.getString("PeriodFormat.hours.list").split(regExSeparator));
    //        } else {
    //            builder.appendSuffix(b.getString("PeriodFormat.hour"), b.getString("PeriodFormat.hours"));
    //        }            
    //
    //        builder.appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants);
    //        builder.appendMinutes();
    //        if (containsKey(b, "PeriodFormat.minutes.regex")) {
    //            builder.appendSuffix(
    //                    b.getString("PeriodFormat.minutes.regex").split(regExSeparator),
    //                    b.getString("PeriodFormat.minutes.list").split(regExSeparator));
    //        } else {
    //            builder.appendSuffix(b.getString("PeriodFormat.minute"), b.getString("PeriodFormat.minutes"));
    //        }    
    //
    //        builder.appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants);
    //        builder.appendSeconds();
    //        if (containsKey(b, "PeriodFormat.seconds.regex")) {
    //            builder.appendSuffix(
    //                    b.getString("PeriodFormat.seconds.regex").split(regExSeparator),
    //                    b.getString("PeriodFormat.seconds.list").split(regExSeparator));
    //        } else {
    //            builder.appendSuffix(b.getString("PeriodFormat.second"), b.getString("PeriodFormat.seconds"));
    //        }
    //
    //        builder.appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants);
    //        builder.appendMillis();
    //        if (containsKey(b, "PeriodFormat.milliseconds.regex")) {
    //            builder.appendSuffix(
    //                    b.getString("PeriodFormat.milliseconds.regex").split(regExSeparator),
    //                    b.getString("PeriodFormat.milliseconds.list").split(regExSeparator));
    //        } else {
    //            builder.appendSuffix(b.getString("PeriodFormat.millisecond"), b.getString("PeriodFormat.milliseconds"));
    //        }
    //        return builder.toFormatter().withLocale(locale);
    //    }
    //
    //    private static PeriodFormatter buildNonRegExFormatter(ResourceBundle b, Locale locale) {
    //        String[] variants = retrieveVariants(b);
    //        return new PeriodFormatterBuilder()
    //            .appendYears()
    //            .appendSuffix(b.getString("PeriodFormat.year"), b.getString("PeriodFormat.years"))
    //            .appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants)
    //            .appendMonths()
    //            .appendSuffix(b.getString("PeriodFormat.month"), b.getString("PeriodFormat.months"))
    //            .appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants)
    //            .appendWeeks()
    //            .appendSuffix(b.getString("PeriodFormat.week"), b.getString("PeriodFormat.weeks"))
    //            .appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants)
    //            .appendDays()
    //            .appendSuffix(b.getString("PeriodFormat.day"), b.getString("PeriodFormat.days"))
    //            .appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants)
    //            .appendHours()
    //            .appendSuffix(b.getString("PeriodFormat.hour"), b.getString("PeriodFormat.hours"))
    //            .appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants)
    //            .appendMinutes()
    //            .appendSuffix(b.getString("PeriodFormat.minute"), b.getString("PeriodFormat.minutes"))
    //            .appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants)
    //            .appendSeconds()
    //            .appendSuffix(b.getString("PeriodFormat.second"), b.getString("PeriodFormat.seconds"))
    //            .appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace"), variants)
    //            .appendMillis()
    //            .appendSuffix(b.getString("PeriodFormat.millisecond"), b.getString("PeriodFormat.milliseconds"))
    //            .toFormatter().withLocale(locale);
    //    }
    //
    //    private static String[] retrieveVariants(ResourceBundle b) {
    //        return new String[] { b.getString("PeriodFormat.space"), b.getString("PeriodFormat.comma"),
    //                b.getString("PeriodFormat.commandand"), b.getString("PeriodFormat.commaspaceand") };
    //    }
    //
    //    // simulate ResourceBundle.containsKey()
    //    private static boolean containsKey(ResourceBundle bundle, String key) {
    //        for (Enumeration<String> en = bundle.getKeys(); en.hasMoreElements(); ) {
    //            if (en.nextElement().equals(key)) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }
    //
    //    //-----------------------------------------------------------------------
    //    /**
    //     * Printer/parser that reacts to the locale and changes the word-based
    //     * pattern if necessary.
    //     */
    //    static class DynamicWordBased
    //            implements PeriodPrinter, PeriodParser {
    //
    //        /** The formatter with the locale selected at construction time. */
    //        private final PeriodFormatter iFormatter;
    //
    //        DynamicWordBased(PeriodFormatter formatter) {
    //            iFormatter = formatter;
    //        }
    //
    //        public int countFieldsToPrint(ReadablePeriod period, int stopAt, Locale locale) {
    //            return getPrinter(locale).countFieldsToPrint(period, stopAt, locale);
    //        }
    //
    //        public int calculatePrintedLength(ReadablePeriod period, Locale locale) {
    //            return getPrinter(locale).calculatePrintedLength(period, locale);
    //        }
    //
    //        public void printTo(StringBuffer buf, ReadablePeriod period, Locale locale) {
    //            getPrinter(locale).printTo(buf, period, locale);
    //        }
    //
    //        public void printTo(Writer out, ReadablePeriod period, Locale locale) throws IOException {
    //            getPrinter(locale).printTo(out, period, locale);
    //        }
    //
    //        private PeriodPrinter getPrinter(Locale locale) {
    //            if (locale != null && !locale.equals(iFormatter.getLocale())) {
    //                return wordBased(locale).getPrinter();
    //            }
    //            return iFormatter.getPrinter();
    //        }
    //
    //        public int parseInto(
    //                ReadWritablePeriod period, String periodStr,
    //                int position, Locale locale) {
    //            return getParser(locale).parseInto(period, periodStr, position, locale);
    //        }
    //
    //        private PeriodParser getParser(Locale locale) {
    //            if (locale != null && !locale.equals(iFormatter.getLocale())) {
    //                return wordBased(locale).getParser();
    //            }
    //            return iFormatter.getParser();
    //        }
    //    }
}
