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

import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;

/**
 * How an Accounting year is divided into smaller periods.
 * <p>
 * The Accounting calendar system generally divides a year into smaller periods, similar in length to regular calendar months.
 * The most common divisions either use 12 such periods (requiring 1 period every quarter to be 5 weeks instead of 4),
 * or use 13 periods of 4 weeks each (making one quarter have an extra period).
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code AccountingPeriod}.
 * Use {@code getValue()} instead.</b>
 *
 * <h3>Implementation Requirements:</h3>
 * This is an immutable and thread-safe enum.
 */
public enum AccountingPeriod {

    /**
     * The singleton instance for a year divided into 4 quarters, 
     * each having 3 periods with lengths of 4, 4, and 5 weeks, respectively.
     */
    QUARTERS_OF_PATTERN_4_4_5_WEEKS(new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}),
    /**
     * The singleton instance for a year divided into 4 quarters, 
     * each having 3 periods with lengths of 4, 5, and 4 weeks, respectively.
     */
    QUARTERS_OF_PATTERN_4_5_4_WEEKS(new int[] {4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4}),
    /**
     * The singleton instance for a year divided into 4 quarters, 
     * each having 3 periods with lengths of 5, 4, and 4 weeks, respectively.
     */
    QUARTERS_OF_PATTERN_5_4_4_WEEKS(new int[] {5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4}),
    /**
     * The singleton instance for a year divided into 13 even periods,
     * each having 4 weeks.
     */
    THIRTEEN_EVEN_PERIODS_OF_4_WEEKS(new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4});

    /**
     * The number of weeks in each month.
     */
    private final int[] weeksInMonths;
    /**
     * The range of months in each year.
     */
    private final ValueRange monthsInYearRange;

    //-----------------------------------------------------------------------
    /**
     * Private constructor for enum, for creating cached info.
     * 
     * @param weeksInMonths The number of weeks in each month (period).
     */
    private AccountingPeriod(int[] weeksInMonths) {
        this.weeksInMonths = weeksInMonths;

        this.monthsInYearRange = ValueRange.of(1, weeksInMonths.length);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code AccountingPeriod} from an {@code int} value.
     * <p>
     * {@code AccountingPeriod} is an enum representing how an Accounting year is divided into smaller periods.
     * This factory allows the enum to be obtained from the {@code int} value.
     *
     * @param period  the AccountingPeriod division to represent, from 0 to 3
     * @return the AccountingPeriod singleton, not null
     * @throws DateTimeException if the value is invalid
     */
    public static AccountingPeriod of(int period) {
        switch (period) {
            case 0:
                return QUARTERS_OF_PATTERN_4_4_5_WEEKS;
            case 1:
                return QUARTERS_OF_PATTERN_4_5_4_WEEKS;
            case 2:
                return QUARTERS_OF_PATTERN_5_4_4_WEEKS;
            case 3:
                return THIRTEEN_EVEN_PERIODS_OF_4_WEEKS;
            default:
                throw new DateTimeException("Invalid AccountingPeriod: " + period);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the numeric AccountingPeriod {@code int} value.
     * <p>
     * <ul>
     * <li>The AccountingPeriod {@code QUARTERS_OF_PATTERN_4_4_5_WEEKS} has the value 0.
     * <li>The AccountingPeriod {@code QUARTERS_OF_PATTERN_4_5_4_WEEKS} has the value 1.
     * <li>The AccountingPeriod {@code QUARTERS_OF_PATTERN_5_4_4_WEEKS} has the value 2.
     * <li>The AccountingPeriod {@code THIRTEEN_EVEN_PERIODS_OF_4_WEEKS} has the value 3.
     * </ul>
     *
     * @return the AccountingPeriod value, from 0 to 3.
     */
    public int getValue() {
        return ordinal();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the range of months in a year.
     * <p>
     * <ul>
     * <li>The AccountingPeriod {@code QUARTERS_OF_PATTERN_4_4_5_WEEKS} range is [1, 12].
     * <li>The AccountingPeriod {@code QUARTERS_OF_PATTERN_4_5_4_WEEKS} range is [1, 12].
     * <li>The AccountingPeriod {@code QUARTERS_OF_PATTERN_5_4_4_WEEKS} range is [1, 12].
     * <li>The AccountingPeriod {@code THIRTEEN_EVEN_PERIODS_OF_4_WEEKS} range is [1, 13].
     * </ul>
     *
     * @return the range of months (periods) in a year.
     */
    ValueRange getMonthsInYearRange() {
        return monthsInYearRange;
    }

    /**
     * Gets the length of the year in months.
     * 
     * @return The length of the year in months.
     */
    int lengthOfYearInMonths() {
        return weeksInMonths.length;
    }

    /**
     * Get the number of weeks in the given month (period). 
     */
    int getWeeksInMonth(int month) {
        return weeksInMonths[monthsInYearRange.checkValidIntValue(month, ChronoField.MONTH_OF_YEAR) - 1];
    }

}
