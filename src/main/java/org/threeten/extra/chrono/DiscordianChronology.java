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
 * The Discordian calendar system.
 * <p>
 * This chronology defines the rules of the proleptic Discordian calendar system.
 * The Discordian differs from the Gregorian in terms of month and week lengths, with an offset year.
 * Dates are aligned such that each Discordian year starts with each ISO year, with an offset index.
 * <p>
 * This class is not proleptic. It implements Discordian rules only since YOLD 1 (ISO BCE 1166).
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There is one era, the current 'Year of Our Lady of Discord' (YOLD).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year one.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the current era.
 * <li>month-of-year - There are 5 months in a Discordian year, numbered from 1 to 5.
 * <li>day-of-week - There are 5 days in a Discordian week, numbered from 1 to 5.
 * <li>day-of-month - There are 73 days in each Discordian month, numbered from 1 to 73.
 * <li>day-of-year - There are 365 days in a standard Discordian year and 366 in a leap year.
 *  The days are numbered from 1 to 365 or 1 to 366.
 * <li>leap-year - Leap years occur in sync with ISO leap-years; that is, they occur every 4 ISO years, excepting when that ISO year is divisible by 100 but not 400.
 * <li>St. Tib's Day - St. Tib's Day occurs each leap-year, and is inserted between the 59th and 60th day of the first month.
 * St. Tib's Day is not part of any month, nor is it part of the Discordian week.  It is aligned with the ISO calendar leap-day (February 29th).
 * </ul>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 */
public final class DiscordianChronology extends AbstractChronology implements Serializable {

    /**
     * Singleton instance for the Julian chronology.
     */
    public static final DiscordianChronology INSTANCE = new DiscordianChronology();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1075529146344250850L;
    /**
     * Range of proleptic-year.
     */
    static final ValueRange YEAR_RANGE = ValueRange.of(1, 999_999);
    /**
     * Range of month-of-year.
     */
    static final ValueRange MONTH_OF_YEAR_RANGE = ValueRange.of(0, 1, 5, 5);
    /**
     * Range of day-of-month.
     */
    static final ValueRange DAY_OF_MONTH_RANGE = ValueRange.of(0, 1, 73, 73);

    /**
     * Private constructor, that is public to satisfy the {@code ServiceLoader}. 
     * Use the singleton {@link #INSTANCE} instead.
     */
    public DiscordianChronology() {
    }

    @Override
    public ChronoLocalDate date(TemporalAccessor arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDate date(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDate dateEpochDay(long arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDate dateYearDay(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Era eraOf(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Era> eras() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'discordian'.
     * <p>
     * The <em>Unicode Locale Data Markup Language (LDML)</em> specification
     * does not define an identifier for the Discordian calendar, but were it to
     * do so, 'discordian' is highly likely to be chosen.
     *
     * @return the calendar system type - 'discordian'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "discordian";
    }

    /**
     * Gets the ID of the chronology - 'Discordian'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     *
     * @return the chronology ID - 'Discordian'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Discordian";
    }

    @Override
    public boolean isLeapYear(long prolepticYear) {
        long offsetYear = prolepticYear - 1266;
        return (offsetYear % 4 == 0) && ((offsetYear % 400 == 0) || (offsetYear % 100 != 0));
    }

    @Override
    public int prolepticYear(Era arg0, int arg1) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ValueRange range(ChronoField arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}
