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

import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * System default UTC rules.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 */
final class SystemUtcRules extends UtcRules implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 7594178360693417218L;
    /**
     * The known leap second definitions.
     * Consists of the Modified Julian Day of the leap second and the TAI offset after that date.
     */
    private static final Data BUILT_IN_DATA = new Data(
            new long[]{
                    41317, 41498, 41682, 42047, 42412, 42777, 43143, 43508, 43873, 44238,
                    44785, 45150, 45515, 46246, 47160, 47891, 48256, 48803, 49168, 49533,
                    50082, 50629, 51178, 53735, 54831, 56108, 57203, 57753
            },
            new int[]{
                    10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
                    24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37
            });
    /**
     * Singleton.
     */
    static final SystemUtcRules INSTANCE = new SystemUtcRules();

    /**
     * The table of leap second dates.
     */
    private final AtomicReference<Data> dataRef = new AtomicReference<>(BUILT_IN_DATA);

    /** Data holder. */
    private static final class Data implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = -3655687912882817265L;
        /** Constructor. */
        private Data(long[] dates, int[] offsets) {
            super();
            this.dates = dates;
            this.offsets = offsets;
            this.taiSeconds = new long[dates.length];
            for (int i = 0; i < dates.length; i++) {
                this.taiSeconds[i] = tai(dates[i], offsets[i]);
            }
        }
        /** The table of leap second date when the leap second occurs. */
        private final long[] dates;
        /** The table of TAI offset after the leap second. */
        private final int[] offsets;
        /** The table of TAI second when the new offset starts. */
        private final long[] taiSeconds;
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
     * <p>
     * This class includes all known leap seconds.
     * Since leap seconds are now discontinued, there is no good reason to add new leap seconds.
     * However, this method is retained for compatibility, and for unusual use cases.
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
        int offset = offsets[offsets.length - 2] + leapAdjustment;
        dates[dates.length - 1] = mjDay;
        offsets[offsets.length - 1] = offset;
        Data newData = new Data(dates, offsets);
        if (!dataRef.compareAndSet(data, newData)) {
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
