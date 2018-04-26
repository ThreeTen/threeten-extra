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

import java.time.DateTimeException;
import java.time.Instant;
import java.util.ConcurrentModificationException;

/**
 * Rules defining the UTC time-scale, notably when leap seconds occur.
 * <p>
 * This class defines the UTC time-scale including when leap seconds occur.
 * Subclasses obtain the data from a suitable source, such as a file.
 * <p>
 * The static methods on this class provide access to the system leap second rules.
 * These are used by default in {@code UtcInstant} and {@code TaiInstant}.
 * Using other rules requires manual use of this class.
 * <p>
 * The system rules can be updated using a {@code LeapSeconds.txt}} file.
 * You can create your own version of this file and place it in on the classpath
 * and it will be used. Due to Java 9 module restrictions, the file is located
 * under META-INF to avoid module encapsulation problems -
 * {@code META-INF/org/threeten/extra/scale/LeapSeconds.txt}.
 *
 * <h3>Implementation Requirements:</h3>
 * This is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All implementations must be final, immutable and thread-safe.
 * Subclasses should be {@code Serializable} wherever possible.
 */
public abstract class UtcRules {

    /**
     * Constant for the offset from MJD day 0 to the Java Epoch of 1970-01-01: 40587.
     */
    static final int OFFSET_MJD_EPOCH = 40587;
    /**
     * Constant for the offset from MJD day 0 to TAI day 0 of 1958-01-01: 36204.
     */
    static final int OFFSET_MJD_TAI = 36204;
    /**
     * Constant for number of seconds per standard day: 86,400.
     */
    static final long SECS_PER_DAY = 24L * 60L * 60L;
    /**
     * Constant for nanos per standard second: 1,000,000,000.
     */
    static final long NANOS_PER_SECOND = 1000000000L;

    /**
     * Gets the system default leap second rules.
     * <p>
     * The system default rules are serializable, immutable and thread-safe.
     * They will remain up to date as new leap seconds are added.
     *
     * @return the system rules, not null
     */
    public static UtcRules system() {
        return SystemUtcRules.INSTANCE;
    }

    /**
     * Adds a new leap second to the system default leap second rules.
     * <p>
     * This method registers a new leap second with the system leap second rules.
     * Once registered, there is no way to deregister the leap second.
     * <p>
     * Calling this method is thread-safe.
     * Its effects are immediately visible in all threads.
     * Where possible, only call this method from a single thread to avoid the possibility of
     * a {@code ConcurrentModificationException}.
     * <p>
     * If the leap second being added matches a previous definition, then the method returns normally.
     * If the date is before the last registered date and does not match a previous definition,
     * then an exception is thrown.
     *
     * @param mjDay  the Modified Julian Day that the leap second occurs at the end of
     * @param leapAdjustment  the leap seconds to add/remove at the end of the day, either -1 or 1
     * @throws IllegalArgumentException if the leap adjustment is invalid
     * @throws IllegalArgumentException if the day is before or equal the last known leap second day
     *  and the definition does not match a previously registered leap
     * @throws ConcurrentModificationException if another thread updates the rules at the same time
     */
    public static void registerLeapSecond(long mjDay, int leapAdjustment) {
        SystemUtcRules.INSTANCE.register(mjDay, leapAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance of the rules.
     */
    protected UtcRules() {
    }

    //-----------------------------------------------------------------------
    /**
     * The name of these rules.
     *
     * @return the name, not null
     */
    public abstract String getName();

    /**
     * Gets the leap second adjustment on the specified date.
     * <p>
     * The UTC standard restricts the adjustment on any day to {@code -1} or {@code 1}.
     * <p>
     * Any leap seconds are added to, or removed from, the end of the specified date.
     * <p>
     * If the UTC specification is altered to allow multiple leap seconds at once, then
     * the result of this method would return a number with an absolute value greater than one.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @return the number of seconds added, or removed, from the date, either -1 or 1
     */
    public abstract int getLeapSecondAdjustment(long mjDay);

    /**
     * Gets the offset to TAI on the specified date.
     * <p>
     * The TAI offset starts at 10 in 1972 and varies from then on based on the
     * dates of leap seconds.
     * The offset will apply for the whole of the date.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @return the TAI offset in seconds
     */
    public abstract int getTaiOffset(long mjDay);

    /**
     * Gets all known leap second dates.
     * <p>
     * The dates are returned as Modified Julian Day values.
     * The leap second is added to, or removed from, the <i>end</i> of the specified dates.
     * The dates will be sorted from earliest to latest.
     *
     * @return an array of leap second dates expressed as Modified Julian Day values, not null
     */
    public abstract long[] getLeapSecondDates();

    //-----------------------------------------------------------------------
    /**
     * Validates combination of Modified Julian Day and nanosecond-of-day.
     * <p>
     * Modified Julian Day is a simple incrementing count of days where day 0 is 1858-11-17.
     * Nanosecond-of-day is a simple count of nanoseconds from the start of the day
     * including any additional leap-second.
     * This method validates the nanosecond-of-day value against the Modified Julian Day.
     * <p>
     * The nanosecond-of-day value has a valid range from {@code 0} to
     * {@code 86,400,000,000,000 - 1} on most days, and a larger or smaller range
     * on leap-second days.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @param nanoOfDay  the nanoseconds within the day, including leap seconds
     * @throws DateTimeException if nanoOfDay is out of range
     */
    public void validateModifiedJulianDay(long mjDay, long nanoOfDay) {
        long leapSecs = getLeapSecondAdjustment(mjDay);
        long maxNanos = (SECS_PER_DAY + leapSecs) * NANOS_PER_SECOND;
        if (nanoOfDay < 0 || nanoOfDay >= maxNanos) {
            throw new DateTimeException("Nanosecond-of-day must be between 0 and " + maxNanos + " on date " + mjDay);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Converts a {@code UtcInstant} to a {@code TaiInstant}.
     * <p>
     * This method converts from the UTC to the TAI time-scale using the
     * leap-second rules of the implementation.
     *
     * @param utcInstant  the UTC instant to convert, not null
     * @return the converted TAI instant, not null
     * @throws DateTimeException if the valid range is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public TaiInstant convertToTai(UtcInstant utcInstant) {
        long mjd = utcInstant.getModifiedJulianDay();
        long nod = utcInstant.getNanoOfDay();
        long taiUtcDaySeconds = Math.multiplyExact(Math.subtractExact(mjd, OFFSET_MJD_TAI), SECS_PER_DAY);
        long taiSecs = Math.addExact(taiUtcDaySeconds, nod / NANOS_PER_SECOND + getTaiOffset(mjd));
        int nos = (int) (nod % NANOS_PER_SECOND);
        return TaiInstant.ofTaiSeconds(taiSecs, nos);
    }

    /**
     * Converts a {@code TaiInstant} to a {@code UtcInstant}.
     * <p>
     * This method converts from the TAI to the UTC time-scale using the
     * leap-second rules of the implementation.
     *
     * @param taiInstant  the TAI instant to convert, not null
     * @return the converted UTC instant, not null
     * @throws DateTimeException if the valid range is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public abstract UtcInstant convertToUtc(TaiInstant taiInstant);

    //-----------------------------------------------------------------------
    /**
     * Converts a {@code UtcInstant} to an {@code Instant}.
     * <p>
     * This method converts from the UTC time-scale to one with 86400 subdivisions
     * per day using the leap-second rules of the implementation.
     * <p>
     * The standard implementation uses the UTC-SLS algorithm.
     * Overriding this algorithm is possible, however doing so will conflict other parts
     * of the specification.
     * <p>
     * The algorithm calculates the UTC-SLS nanos-of-day {@code US} from the UTC nanos-of day {@code U}.<br>
     * Let {@code L = getLeapAdjustment(mjd)}.<br>
     * Let {@code B = 86400 + L - 1000}.<br>
     * Let {@code US = U - L * (U - B) / 1000}.<br>
     * Where the algorithm is applied while {@code U >= B}.
     *
     * @param utcInstant  the UTC instant to convert, not null
     * @return the converted instant, not null
     * @throws DateTimeException if the valid range is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Instant convertToInstant(UtcInstant utcInstant) {
        long mjd = utcInstant.getModifiedJulianDay();
        long utcNanos = utcInstant.getNanoOfDay();
        long epochDay = Math.subtractExact(mjd, OFFSET_MJD_EPOCH);
        long epochSec = Math.multiplyExact(epochDay, SECS_PER_DAY);
        int leapAdj = getLeapSecondAdjustment(mjd);
        long startSlsNanos = (SECS_PER_DAY + leapAdj - 1000) * NANOS_PER_SECOND;
        long slsNanos = utcNanos;
        if (leapAdj != 0 && utcNanos >= startSlsNanos) {
            slsNanos = utcNanos - leapAdj * (utcNanos - startSlsNanos) / 1000;  // apply UTC-SLS mapping
        }
        return Instant.ofEpochSecond(epochSec + slsNanos / NANOS_PER_SECOND, slsNanos % NANOS_PER_SECOND);
    }

    /**
     * Converts an {@code Instant} to a {@code UtcInstant}.
     * <p>
     * This method converts from an instant with 86400 subdivisions per day
     * to the UTC time-scale using the leap-second rules of the implementation.
     * <p>
     * The standard implementation uses the UTC-SLS algorithm.
     * Overriding this algorithm is possible, however doing so will conflict other parts
     * of the specification.
     * <p>
     * The algorithm calculates the UTC nanos-of-day {@code U} from the UTC-SLS nanos-of day {@code US}.<br>
     * Let {@code L = getLeapAdjustment(mjd)}.<br>
     * Let {@code B = 86400 + L - 1000}.<br>
     * Let {@code U = B + ((US - B) * 1000) / (1000 - L)}.<br>
     * Where the algorithm is applied while {@code US >= B}.<br>
     * (This algorithm has been tuned for integer arithmetic from the UTC-SLS specification.)
     *
     * @param instant  the instant to convert, not null
     * @return the converted UTC instant, not null
     * @throws DateTimeException if the valid range is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public UtcInstant convertToUtc(Instant instant) {
        long epochDay = Math.floorDiv(instant.getEpochSecond(), SECS_PER_DAY);
        long mjd = epochDay + OFFSET_MJD_EPOCH;
        long slsNanos = Math.floorMod(instant.getEpochSecond(), SECS_PER_DAY) * NANOS_PER_SECOND + instant.getNano();
        int leapAdj = getLeapSecondAdjustment(mjd);
        long startSlsNanos = (SECS_PER_DAY + leapAdj - 1000) * NANOS_PER_SECOND;
        long utcNanos = slsNanos;
        if (leapAdj != 0 && slsNanos >= startSlsNanos) {
            utcNanos = startSlsNanos + ((slsNanos - startSlsNanos) * 1000) / (1000 - leapAdj);  // apply UTC-SLS mapping
        }
        return UtcInstant.ofModifiedJulianDay(mjd, utcNanos);
    }

    //-------------------------------------------------------------------------
    /**
     * Converts a {@code TaiInstant} to an {@code Instant}.
     * <p>
     * This method converts from the TAI time-scale to one with 86400 subdivisions
     * per day using the leap-second rules of the implementation.
     * <p>
     * The standard implementation uses UTC-SLS. It uses
     * {@link #convertToUtc(TaiInstant)} and {@link #convertToInstant(UtcInstant)}.
     *
     * @param taiInstant  the TAI instant to convert, not null
     * @return the converted instant, not null
     * @throws DateTimeException if the valid range is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Instant convertToInstant(TaiInstant taiInstant) {
        return convertToInstant(convertToUtc(taiInstant));
    }

    /**
     * Converts an {@code Instant} to a {@code TaiInstant}.
     * <p>
     * This method converts from an instant with 86400 subdivisions per day
     * to the TAI time-scale using the leap-second rules of the implementation.
     * <p>
     * The standard implementation uses the UTC-SLS algorithm. It uses
     * {@link #convertToUtc(TaiInstant)} and {@link #convertToInstant(UtcInstant)}.
     *
     * @param instant  the instant to convert, not null
     * @return the converted instant, not null
     * @throws DateTimeException if the valid range is exceeded
     * @throws ArithmeticException if numeric overflow occurs
     */
    public TaiInstant convertToTai(Instant instant) {
        return convertToTai(convertToUtc(instant));
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of these rules.
     *
     * @return the string representation, not null
     */
    @Override
    public String toString() {
        return "UtcRules[" + getName() + ']';
    }

}
