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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * System default UTC rules.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 */
final class SystemUtcRules extends UtcRules implements Serializable {

    /**
     * The leap seconds config file.
     */
    private static final String LEAP_SECONDS_TXT = "org/threeten/extra/scale/LeapSeconds.txt";
    /**
     * Leap second file format.
     */
    private static final Pattern LEAP_FILE_FORMAT = Pattern.compile("([0-9-]{10})[ ]+([0-9]+)");
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 7594178360693417218L;
    /**
     * Singleton.
     */
    static final SystemUtcRules INSTANCE = new SystemUtcRules();

    /**
     * The table of leap second dates.
     */
    private AtomicReference<Data> dataRef = new AtomicReference<Data>(loadLeapSeconds());

    /** Data holder. */
    private static final class Data implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = -3655687912882817265L;
        /** Constructor. */
        private Data(long[] dates, int[] offsets, long[] taiSeconds) {
            super();
            this.dates = dates;
            this.offsets = offsets;
            this.taiSeconds = taiSeconds;
        }
        /** The table of leap second date when the leap second occurs. */
        private final long[] dates;
        /** The table of TAI offset after the leap second. */
        private final int[] offsets;
        /** The table of TAI second when the new offset starts. */
        private final long[] taiSeconds;

        /**
         * @return The modified Julian Date of the newest leap second
         */
        public long getNewestDate() {
            return dates[dates.length - 1];
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Restricted constructor.
     */
    private SystemUtcRules() {
    }

    /**
     * Resolves singleton.
     *
     * @return the resolved instance, not null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new leap second to these rules.
     *
     * @param mjDay  the Modified Julian Day that the leap second occurs at the end of
     * @param leapAdjustment  the leap seconds to add/remove at the end of the day, either -1 or 1
     * @throws IllegalArgumentException if the leap adjustment is invalid
     * @throws IllegalArgumentException if the day is before or equal the last known leap second day
     *  and the definition does not match a previously registered leap
     * @throws ConcurrentModificationException if another thread updates the rules at the same time
     */
    void register(long mjDay, int leapAdjustment) {
        if (leapAdjustment != -1 && leapAdjustment != 1) {
            throw new IllegalArgumentException("Leap adjustment must be -1 or 1");
        }
        Data data = dataRef.get();
        int pos = Arrays.binarySearch(data.dates, mjDay);
        int currentAdj = pos > 0 ? data.offsets[pos] - data.offsets[pos - 1] : 0;
        if (currentAdj == leapAdjustment) {
            return;  // matches previous definition
        }
        if (mjDay <= data.dates[data.dates.length - 1]) {
            throw new IllegalArgumentException("Date must be after the last configured leap second date");
        }
        long[] dates = Arrays.copyOf(data.dates, data.dates.length + 1);
        int[] offsets = Arrays.copyOf(data.offsets, data.offsets.length + 1);
        long[] taiSeconds = Arrays.copyOf(data.taiSeconds, data.taiSeconds.length + 1);
        int offset = offsets[offsets.length - 2] + leapAdjustment;
        dates[dates.length - 1] = mjDay;
        offsets[offsets.length - 1] = offset;
        taiSeconds[taiSeconds.length - 1] = tai(mjDay, offset);
        Data newData = new Data(dates, offsets, taiSeconds);
        if (dataRef.compareAndSet(data, newData) == false) {
            throw new ConcurrentModificationException("Unable to update leap second rules as they have already been updated");
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return "System";
    }

    @Override
    public int getLeapSecondAdjustment(long mjDay) {
        Data data = dataRef.get();
        int pos = Arrays.binarySearch(data.dates, mjDay);
        return pos > 0 ? data.offsets[pos] - data.offsets[pos - 1] : 0;
    }

    @Override
    public int getTaiOffset(long mjDay) {
        Data data = dataRef.get();
        int pos = Arrays.binarySearch(data.dates, mjDay);
        pos = (pos < 0 ? ~pos : pos);
        return pos > 0 ? data.offsets[pos - 1] : 10;
    }

    @Override
    public long[] getLeapSecondDates() {
        Data data = dataRef.get();
        return data.dates.clone();
    }

    //-----------------------------------------------------------------------
    @Override
    public UtcInstant convertToUtc(TaiInstant taiInstant) {
        Data data = dataRef.get();
        long[] mjds = data.dates;
        long[] tais = data.taiSeconds;
        int pos = Arrays.binarySearch(tais, taiInstant.getTaiSeconds());
        pos = (pos >= 0 ? pos : ~pos - 1);
        int taiOffset = (pos >= 0 ? data.offsets[pos] : 10);
        long adjustedTaiSecs = taiInstant.getTaiSeconds() - taiOffset;
        long mjd = Math.floorDiv(adjustedTaiSecs, SECS_PER_DAY) + OFFSET_MJD_TAI;
        long nod = Math.floorMod(adjustedTaiSecs, SECS_PER_DAY) * NANOS_PER_SECOND + taiInstant.getNano();
        long mjdNextRegionStart = (pos + 1 < mjds.length ? mjds[pos + 1] + 1 : Long.MAX_VALUE);
        if (mjd == mjdNextRegionStart) {  // in leap second
            mjd--;
            nod = SECS_PER_DAY * NANOS_PER_SECOND + (nod / NANOS_PER_SECOND) * NANOS_PER_SECOND + nod % NANOS_PER_SECOND;
        }
        return UtcInstant.ofModifiedJulianDay(mjd, nod);
    }

    //-----------------------------------------------------------------------
    /**
     * Loads the rules from files in the class loader, often jar files.
     *
     * @return the list of loaded rules, not null
     * @throws Exception if an error occurs
     */
    private static Data loadLeapSeconds() {
        Data bestData = null;
        URL url = null;
        try {
            // this is the new location of the file, working on Java 8, Java 9 class path and Java 9 module path
            Enumeration<URL> en = Thread.currentThread().getContextClassLoader().getResources("META-INF/" + LEAP_SECONDS_TXT);
            while (en.hasMoreElements()) {
                url = en.nextElement();
                Data candidate = loadLeapSeconds(url);
                if (bestData == null || candidate.getNewestDate() > bestData.getNewestDate()) {
                    bestData = candidate;
                }
            }
            // this location does not work on Java 9 module path because the resource is encapsulated
            en = Thread.currentThread().getContextClassLoader().getResources(LEAP_SECONDS_TXT);
            while (en.hasMoreElements()) {
                url = en.nextElement();
                Data candidate = loadLeapSeconds(url);
                if (bestData == null || candidate.getNewestDate() > bestData.getNewestDate()) {
                    bestData = candidate;
                }
            }
            // this location is the canonical one, and class-based loading works on Java 9 module path
            url = SystemUtcRules.class.getResource("/" + LEAP_SECONDS_TXT);
            if (url != null) {
                Data candidate = loadLeapSeconds(url);
                if (bestData == null || candidate.getNewestDate() > bestData.getNewestDate()) {
                    bestData = candidate;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load time-zone rule data: " + url, ex);
        }
        if (bestData == null) {
            // no data on classpath, but we allow manual registration of leap seconds
            // setup basic known data - MJD 1972-01-01 is 41317L, where offset was 10
            bestData = new Data(new long[] {41317L}, new int[] {10}, new long[] {tai(41317L, 10)});
        }
        return bestData;
    }

    /**
     * Loads the leap second rules from a URL, often in a jar file.
     *
     * @param url  the jar file to load, not null
     * @throws Exception if an error occurs
     */
    private static Data loadLeapSeconds(URL url) throws ClassNotFoundException, IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            lines = reader.lines().collect(Collectors.toList());
        }
        List<Long> dates = new ArrayList<>();
        List<Integer> offsets = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            Matcher matcher = LEAP_FILE_FORMAT.matcher(line);
            if (matcher.matches() == false) {
                throw new StreamCorruptedException("Invalid leap second file");
            }
            dates.add(LocalDate.parse(matcher.group(1)).getLong(JulianFields.MODIFIED_JULIAN_DAY));
            offsets.add(Integer.valueOf(matcher.group(2)));
        }
        long[] datesData = new long[dates.size()];
        int[] offsetsData = new int[dates.size()];
        long[] taiData = new long[dates.size()];
        for (int i = 0; i < datesData.length; i++) {
            datesData[i] = dates.get(i);
            offsetsData[i] = offsets.get(i);
            taiData[i] = tai(datesData[i], offsetsData[i]);
        }
        return new Data(datesData, offsetsData, taiData);
    }

    /**
     * Gets the TAI seconds for the start of the day following the day passed in.
     *
     * @param changeMjd  the MJD that the leap second is added to
     * @param offset  the new offset after the leap
     * @return the TAI seconds
     */
    private static long tai(long changeMjd, int offset) {
        return (changeMjd + 1 - OFFSET_MJD_TAI) * SECS_PER_DAY + offset;
    }

}
