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

import java.io.Serializable;
import java.time.chrono.AbstractChronology;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Era;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.ValueRange;
import java.util.List;

/**
 * An Accounting calendar system.
 * <p>
 * This chronology defines the rules of a proleptic 52/53-week Accounting calendar system.
 * This calendar system follows the rules as laid down in TODO: get references as necessary
 * The start of the Accounting calendar will vary against the ISO calendar.
 * Depending on options chosen, it can start as early as {@code 0000-12-26 (ISO)} or as late as {@code 0001-01-04 (ISO)}.
 * <p>
 * This class is proleptic. It implements Accounting chronology rules for the entire time-line.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, the current 'Current Era' (CE) and the previous era 'Before Current Era' (BCE).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year one.
 *  For the previous era the year increases from one as time goes backwards.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current era. For the previous era, years have zero, then negative values.
 * <li>month-of-year - There are 12 or 13 months (periods) in an Accouting year, numbered from 1 to 12 or 13.
 * <li>day-of-month - There are 28 or 35 days in each Accounting month (period), numbered from 1 to 35.
 *  Month (period) length depends on how the year has been divided.
 *  When the Accounting leap year occurs, a week (7 days) is added to a specific month (period).
 * <li>day-of-year - There are 364 days in a standard Accounting year and 371 in a leap year.
 *  The days are numbered from 1 to 364 or 1 to 371.
 * <li>leap-year - Leap years usually occur every 5 or 6 years.  Timing depends on settings chosen.
 * </ul>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 */
public final class AccountingChronology extends AbstractChronology implements Serializable {

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCalendarType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDate date(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDate dateYearDay(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDate dateEpochDay(long arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDate date(TemporalAccessor arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isLeapYear(long arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int prolepticYear(Era arg0, int arg1) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Era> eras() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Era eraOf(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueRange range(ChronoField arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
