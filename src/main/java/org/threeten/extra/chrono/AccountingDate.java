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

import static java.time.temporal.ChronoField.EPOCH_DAY;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

/**
 * A date in an Accounting calendar system.
 * <p>
 * This date operates using a given {@linkplain AccountingChronology Accounting calendar}.
 * An Accounting calendar differs greatly from the ISO calendar.
 * The start of the Accounting calendar will vary against the ISO calendar.
 * Depending on options chosen, it can start as early as {@code 0000-12-26 (ISO)} or as late as {@code 0001-01-04 (ISO)}.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class AccountingDate extends AbstractDate implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -126140328940081914L;

    /**
     * The chronology for manipulating this date.
     */
    private final AccountingChronology chronology;
    /**
     * The proleptic year.
     */
    private final int prolepticYear;
    /**
     * The month (period).
     */
    private final short month;
    /**
     * The day.
     */
    private final short day;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code AccountingDate} from the system clock in the default time-zone,
     * translated with the given AccountingChronology.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     * 
     * @param chronology  the accounting chronology to use, not null
     * @return the current date using the system clock and default time-zone, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static AccountingDate now(AccountingChronology chronology) {
        return now(chronology, Clock.systemDefaultZone());
    }

    /**
     * Obtains the current {@code AccountingDate} from the system clock in the specified time-zone,
     * translated with the given AccountingChronology.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     * 
     * @param chronology  the accounting chronology to use, not null
     * @param zone  the zone ID to use, not null
     * @return the current date using the system clock, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static AccountingDate now(AccountingChronology chronology, ZoneId zone) {
        return now(chronology, Clock.system(zone));
    }

    /**
     * Obtains the current {@code AccountingDate} from the specified clock,
     * translated with the given AccountingChronology.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param chronology  the accounting chronology to use, not null
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static AccountingDate now(AccountingChronology chronology, Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return ofEpochDay(chronology, now.toEpochDay());
    }

    public static AccountingDate of(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Obtains an {@code AccountingDate} from a temporal object.
     * <p>
     * This obtains a date in the specified Accounting calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code AccountingDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code AccountingDate::from}.
     * TODO: Figure out how this implementation effects the above.
     *
     * @param chronology  the accounting chronology to use, not null
     * @param temporal  the temporal object to convert, not null
     * @return the date in Accounting calendar system, not null
     * @throws DateTimeException if unable to convert to an {@code AccountingDate}
     */
    public static AccountingDate from(AccountingChronology chronology, TemporalAccessor temporal) {
        if (temporal instanceof AccountingDate && ((AccountingDate) temporal).getChronology().equals(chronology)) {
            return (AccountingDate) temporal;
        }
        return ofEpochDay(chronology, temporal.getLong(EPOCH_DAY));
    }

    static AccountingDate ofYearDay(AccountingChronology chronology, int prolepticYear, int dayOfYear) {
        // TODO Auto-generated method stub
        return null;
    }

    static AccountingDate ofEpochDay(AccountingChronology chronology, long epochDay) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Creates an {@code AccountingDate} validating the input.
     *
     * @param chronology The Accounting chronology to base this date on
     * @param prolepticYear  the Accounting proleptic-year
     * @param dayOfYear  the Accounting day-of-year, from 1 to 371
     * @return the date in Accounting calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  if the day-of-year is invalid for the month-year,
     *  or if a chronology was not provided.
     */
    static AccountingDate create(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        // TODO Auto-generated method stub
        return new AccountingDate(chronology, prolepticYear, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param chronology   The Accounting chronology to base the date on
     * @param prolepticYear  the Accounting proleptic-year
     * @param month  the Accounting month (period), from 1 to 12 or 1 to 13
     * @param dayOfMonth  the Accounting day-of-month, from 1 to 35 or 1 to 42
     */
    private AccountingDate(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        this.chronology = chronology;
        this.prolepticYear = prolepticYear;
        this.month = (short) month;
        this.day = (short) dayOfMonth;
    }

    /**
     * Validates the object.
     *
     * @return the resolved date, not null
     */
    private Object readResolve() {
        return AccountingDate.create(chronology, prolepticYear, month, day);
    }

    @Override
    int getProlepticYear() {
        return prolepticYear;
    }

    @Override
    int getMonth() {
        return month;
    }

    @Override
    int getDayOfMonth() {
        return day;
    }

    @Override
    int getDayOfYear() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    AbstractDate withDayOfYear(int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    AbstractDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Chronology getChronology() {
        return chronology;
    }

    @Override
    public int lengthOfMonth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long until(Temporal arg0, TemporalUnit arg1) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
