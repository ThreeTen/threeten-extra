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

import java.time.temporal.ValueRange;

/**
 * A date in one of the Nile river calendar systems.
 *
 * <h3>Implementation Requirements</h3>
 * Implementations must be immutable and thread-safe.
 */
abstract class AbstractNileDate
        extends AbstractDate {

    /**
     * Creates an instance.
     */
    AbstractNileDate() {
    }

    //-----------------------------------------------------------------------
    abstract int getEpochDayDifference();

    @Override
    int getDayOfYear() {
        return (getMonth() - 1) * 30 + getDayOfMonth();
    }

    @Override
    AbstractDate withDayOfYear(int value) {
        return resolvePrevious(getProlepticYear(), ((value - 1) / 30) + 1, ((value - 1) % 30) + 1);
    }

    @Override
    int lengthOfYearInMonths() {
        return 13;
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        return ValueRange.of(1, getMonth() == 13 ? 1 : 5);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Months 1 to 12 have 30 days. Month 13 has 5 or 6 days.
     *
     * @return the length of the month in days, from 5 to 30
     */
    @Override
    public int lengthOfMonth() {
        if (getMonth() == 13) {
            return (isLeapYear() ? 6 : 5);
        }
        return 30;
    }

    @Override
    public long toEpochDay() {
        long year = (long) getProlepticYear();
        long calendarEpochDay = ((year - 1) * 365) + Math.floorDiv(year, 4) + (getDayOfYear() - 1);
        return calendarEpochDay - getEpochDayDifference();
    }

}
