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
import java.util.Arrays;

/**
 * How an Accounting year is divided.
 * <p>
 * An Accounting calendar system generally divides a year into smaller periods, similar in length to regular calendar months.
 * The most common divisions either use 12 such 'months' (requiring one every quarter to be 5 weeks instead of 4),
 * or use 13 of 4 weeks each (making one quarter have an extra month, or each quarter have partial months).
 *
 * <h3>Implementation Requirements:</h3>
 * This is an immutable and thread-safe enum.
 */
public enum AccountingYearDivision {

    /**
     * The singleton instance for a year divided into 4 quarters, 
     * each having 3 months with lengths of 4, 4, and 5 weeks, respectively.
     */
    QUARTERS_OF_PATTERN_4_4_5_WEEKS(new int[] {4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5}),
    /**
     * The singleton instance for a year divided into 4 quarters, 
     * each having 3 months with lengths of 4, 5, and 4 weeks, respectively.
     */
    QUARTERS_OF_PATTERN_4_5_4_WEEKS(new int[] {4, 5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4}),
    /**
     * The singleton instance for a year divided into 4 quarters, 
     * each having 3 months with lengths of 5, 4, and 4 weeks, respectively.
     */
    QUARTERS_OF_PATTERN_5_4_4_WEEKS(new int[] {5, 4, 4, 5, 4, 4, 5, 4, 4, 5, 4, 4}),
    /**
     * The singleton instance for a year divided into 13 even months,
     * each having 4 weeks.
     */
    THIRTEEN_EVEN_MONTHS_OF_4_WEEKS(new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4});

    /**
     * The number of weeks in each month.
     */
    private final int[] weeksInMonths;
    /**
     * The range of months in each year.
     */
    private final ValueRange monthsInYearRange;
    /**
     * The elapsed number of weeks at the start of each month.
     */
    private final int[] elapsedWeeks;

    //-----------------------------------------------------------------------
    /**
     * Private constructor for enum, for creating cached info.
     * 
     * @param weeksInMonths The number of weeks in each month (period).
     */
    private AccountingYearDivision(int[] weeksInMonths) {
        this.weeksInMonths = weeksInMonths;

        this.monthsInYearRange = ValueRange.of(1, weeksInMonths.length);

        this.elapsedWeeks = new int[weeksInMonths.length];
        for (int i = 1; i < weeksInMonths.length; i++) {
            elapsedWeeks[i] = elapsedWeeks[i - 1] + weeksInMonths[i - 1];
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the range of months in a year.
     * <p>
     * <ul>
     * <li>The AccountingYearDivision {@code QUARTERS_OF_PATTERN_4_4_5_WEEKS} range is [1, 12].
     * <li>The AccountingYearDivision {@code QUARTERS_OF_PATTERN_4_5_4_WEEKS} range is [1, 12].
     * <li>The AccountingYearDivision {@code QUARTERS_OF_PATTERN_5_4_4_WEEKS} range is [1, 12].
     * <li>The AccountingYearDivision {@code THIRTEEN_EVEN_MONTHS_OF_4_WEEKS} range is [1, 13].
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

    //-----------------------------------------------------------------------
    /**
     * Get the number of weeks in the given month (period). 
     * 
     * @param month  The month for which to get the count of weeks.
     * @return The count of weeks in the given month.
     * @throws DateTimeException if the month isn't in the valid range of months.
     */
    int getWeeksInMonth(int month) {
        return getWeeksInMonth(month, 0);
    }

    /**
     * Get the number of weeks in the given month (period), with the leap year in the indicated month.
     * 
     * @param month  The month for which to get the count of weeks.
     * @param leapWeekInMonth  The month in which the leap-week resides
     * @return The count of weeks in the given month, including any leap week.
     * @throws DateTimeException if the month isn't in the valid range of months.
     */
    int getWeeksInMonth(int month, int leapWeekInMonth) {
        month = monthsInYearRange.checkValidIntValue(month, ChronoField.MONTH_OF_YEAR);
        leapWeekInMonth = (leapWeekInMonth == 0 ? 0 : monthsInYearRange.checkValidIntValue(leapWeekInMonth, ChronoField.MONTH_OF_YEAR));
        return weeksInMonths[month - 1] + (month == leapWeekInMonth ? 1 : 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Get the number of weeks elapsed before the start of the month.
     * 
     * @param month The month
     * @return The number of weeks elapsed before the start of the month.
     * @throws DateTimeException if the month isn't in the valid range of months.
     */
    int getWeeksAtStartOfMonth(int month) {
        return getWeeksAtStartOfMonth(month, 0);
    }

    /**
     * Get the number of weeks elapsed before the start of the month.
     * 
     * @param month The month
     * @param leapWeekInMonth  The month in which the leap-week resides
     * @return The number of weeks elapsed before the start of the month, including any leap week.
     * @throws DateTimeException if the month isn't in the valid range of months.
     */
    int getWeeksAtStartOfMonth(int month, int leapWeekInMonth) {
        month = monthsInYearRange.checkValidIntValue(month, ChronoField.MONTH_OF_YEAR);
        leapWeekInMonth = (leapWeekInMonth == 0 ? 0 : monthsInYearRange.checkValidIntValue(leapWeekInMonth, ChronoField.MONTH_OF_YEAR));
        return elapsedWeeks[month - 1] + (leapWeekInMonth != 0 && month > leapWeekInMonth ? 1 : 0);
    }

    /**
     * Get the month from a count of elapsed weeks.
     * 
     * @param weeksElapsed The weeks elapsed since the start of the year.
     * @return the month
     * @throws DateTimeException if the month isn't in the valid range of months, 
     *   or the week isn't in the valid range.
     */
    int getMonthFromElapsedWeeks(int weeksElapsed) {
        return getMonthFromElapsedWeeks(weeksElapsed, 0);
    }

    /**
     * Get the month from a count of elapsed weeks.
     * 
     * @param weeksElapsed The weeks elapsed since the start of the year.
     * @param leapWeekInMonth  The month in which the leap-week resides
     * @return the month
     * @throws DateTimeException if the month isn't in the valid range of months, 
     *   or the week isn't in the valid range.
     */
    int getMonthFromElapsedWeeks(int weeksElapsed, int leapWeekInMonth) {
        if (weeksElapsed < 0 || weeksElapsed >= (leapWeekInMonth == 0 ? 52 : 53)) {
            throw new DateTimeException("Count of '" + elapsedWeeks.length + "' elapsed weeks not valid,"
                    + " should be in the range [0, " + (leapWeekInMonth == 0 ? 52 : 53) + ")");
        }
        leapWeekInMonth = (leapWeekInMonth == 0 ? 0 : monthsInYearRange.checkValidIntValue(leapWeekInMonth, ChronoField.MONTH_OF_YEAR));

        int month = Arrays.binarySearch(elapsedWeeks, weeksElapsed);
        // Binary search returns 0-indexed if found, negative - 1 for insert position if not.
        month = (month >= 0 ? month + 1 : 0 - month - 1);
        // Need to move to previous month if there was a leap week and in the first week.
        return leapWeekInMonth == 0 || month <= leapWeekInMonth || weeksElapsed > elapsedWeeks[month - 1] ? month : month - 1;
    }
}
