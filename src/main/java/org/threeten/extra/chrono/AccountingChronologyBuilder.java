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
import java.time.DayOfWeek;
import java.time.Month;

/**
 * Builder to create Accounting calendars.
 * <p>
 * Accounting calendars require setup before use, given how they are used.
 * The following information is required:
 * <ul>
 * <li>ending day-of-week - The day-of-week on which a given accounting year ends.
 * <li>last-in-month vs. nearest-end-of-month - Whether the ending day-of-week is the last in the month,
 * or the nearest to the end of the month (will sometimes be in the <i>next</i> month.
 * <li>month end - Which Gregorian/ISO end-of-month the year ends in/is nearest to.
 * <li>year division - How many 'months' (periods) to divide the accounting year into,
 * and how many weeks are in each.
 * <li>leap-week month - Which month will have the leap 'week' added to it.
 * In practice this is probably the last one, but this does not seem to be required.
 * <li>year start/end offset - Whether the fiscal year starts or ends in the similarly numbered ISO year.
 * If nearest-end-of-month is set and the ending month is December, the effective offset will shift over time.
 * </ul>
 * <p>
 * There are approximately 7 x 2 x 12 x 4 x 12/13 x 2 = 8064 combinations.
 *
 * <h3>Implementation Requirements</h3>
 * This class is a mutable builder suitable for use from a single thread.
 */
public final class AccountingChronologyBuilder {

    /**
     * The day of the week on which a given Accounting year ends.
     */
    private DayOfWeek endsOn;
    /**
     * Whether the calendar ends in the last week of a given Gregorian/ISO month,
     * or nearest to the last day of the month (will sometimes be in the next month).
     */
    private boolean inLastWeek;
    /**
     * Which Gregorian/ISO end-of-month the year ends in/is nearest to.
     */
    private Month end;
    /**
     * How to divide an accounting year.
     */
    private AccountingYearDivision division;
    /**
     * The month which will have the leap-week added.
     */
    private int leapWeekInMonth;

    /**
     * The offset to apply to the year.
     */
    private int yearOffset;

    /**
     * Constructs a new instance of the builder.
     */
    public AccountingChronologyBuilder() {
        // Nothing to setup in the constructor.
    }

    /**
     * Sets the day-of-week on which a given Accounting calendar year ends.
     *
     * @param endsOn The day-of-week on which a given Accounting calendar year ends.
     *
     * @return this, for chaining, not null.
     */
    public AccountingChronologyBuilder endsOn(DayOfWeek endsOn) {
        this.endsOn = endsOn;
        return this;
    }

    /**
     * Sets the Gregorian/ISO month-end which a given Accounting calendar year ends nearest to.
     * Calendars setup this way will occasionally end in the start of the <i>next</i> month.
     * For example, for July, the month ends on any day from July 28th to August 3rd.
     *
     * @param end The Gregorian/ISO month-end a given Accounting calendar year ends nearest to.
     *
     * @return this, for chaining, not null.
     */
    public AccountingChronologyBuilder nearestEndOf(Month end) {
        this.inLastWeek = false;
        this.end = end;
        return this;
    }

    /**
     * Sets the Gregorian/ISO month-end in which a given Accounting calendar year ends.
     * Calendars setup this way will always end in the last week of the given month.
     * For example, for July, the month ends on any day from July 25th to July 31st.
     *
     * @param end The Gregorian/ISO month-end a given Accounting calendar year ends in the last week of.
     *
     * @return this, for chaining, not null.
     */
    public AccountingChronologyBuilder inLastWeekOf(Month end) {
        this.inLastWeek = true;
        this.end = end;
        return this;
    }

    /**
     * Sets how a given Accounting year will be divided.
     *
     * @param division How to divide a given calendar year.
     *
     * @return this, for chaining, not null.
     */
    public AccountingChronologyBuilder withDivision(AccountingYearDivision division) {
        this.division = division;
        return this;
    }

    /**
     * Sets the month in which the leap-week occurs.
     *
     * @param leapWeekInMonth The month in which the leap-week occurs.
     *
     * @return this, for chaining, not null.
     */
    public AccountingChronologyBuilder leapWeekInMonth(int leapWeekInMonth) {
        this.leapWeekInMonth = leapWeekInMonth;
        return this;
    }

    /**
     * Sets the proleptic accounting year to end in the matching Iso year.
     *
     * @return this, for chaining, not null.
     */
    public AccountingChronologyBuilder accountingYearEndsInIsoYear() {
        this.yearOffset = 0;
        return this;
    }


    /**
     * Sets the proleptic accounting year to start in the matching Iso year.
     *
     * @return this, for chaining, not null.
     */
    public AccountingChronologyBuilder accountingYearStartsInIsoYear() {
        this.yearOffset = 1;
        return this;
    }

    /**
     * Completes this builder by creating the {@code AccountingChronology}.
     *
     * @return the created chronology, not null.
     * @throws DateTimeException if the chronology cannot be built.
     */
    public AccountingChronology toChronology() {
        return AccountingChronology.create(endsOn, end, inLastWeek, division, leapWeekInMonth, yearOffset);
    }

}
