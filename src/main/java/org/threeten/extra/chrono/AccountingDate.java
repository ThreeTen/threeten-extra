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
import java.time.Clock;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

public final class AccountingDate extends AbstractDate implements ChronoLocalDate, Serializable {

    public static AccountingDate now(AccountingChronology chronology) {
        // TODO Auto-generated method stub
        return null;
    }

    public static AccountingDate now(AccountingChronology chronology, ZoneId zone) {
        // TODO Auto-generated method stub
        return null;
    }

    public static AccountingDate now(AccountingChronology chronology, Clock clock) {
        // TODO Auto-generated method stub
        return null;
    }

    public static AccountingDate of(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        // TODO Auto-generated method stub
        return null;
    }

    public static AccountingDate from(AccountingChronology chronology, TemporalAccessor temporal) {
        // TODO Auto-generated method stub
        return null;
    }

    static AccountingDate ofYearDay(AccountingChronology chronology, int prolepticYear, int dayOfYear) {
        // TODO Auto-generated method stub
        return null;
    }

    static AccountingDate ofEpochDay(AccountingChronology chronology, long epochDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    int getProlepticYear() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    int getMonth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    int getDayOfMonth() {
        // TODO Auto-generated method stub
        return 0;
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
        // TODO Auto-generated method stub
        return null;
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
