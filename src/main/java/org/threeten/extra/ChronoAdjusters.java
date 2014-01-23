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
package org.threeten.extra;

import static java.time.temporal.ChronoUnit.MONTHS;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.HijrahChronology;
import java.time.chrono.MinguoChronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

/**
 * Adjusters that allow dates to be adjusted in terms of a calendar system.
 */
public final class ChronoAdjusters {

    /**
     * Restricted constructor.
     */
    private ChronoAdjusters() {
    }

    //-----------------------------------------------------------------------
    public static TemporalAdjuster minguo(final TemporalAdjuster adjuster) {
        return new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                ChronoLocalDate baseDate = MinguoChronology.INSTANCE.date(temporal);
                ChronoLocalDate adjustedDate = (ChronoLocalDate) adjuster.adjustInto(baseDate);
                return temporal.with(adjustedDate);
            }
        };
    }

    public static TemporalAdjuster hijrah(final TemporalAdjuster adjuster) {
        return new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                ChronoLocalDate baseDate = HijrahChronology.INSTANCE.date(temporal);
                ChronoLocalDate adjustedDate = (ChronoLocalDate) adjuster.adjustInto(baseDate);
                return temporal.with(adjustedDate);
            }
        };
    }

    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        System.out.println(date);

//        date = date.with(hijrah(dt -> dt.plus(1, MONTHS)));
//        date = date.with(minguo(dt -> dt.plus(1, MONTHS)));
//        date = date.with(minguo(firstDayOfNextMonth()));
//        date = date.with(hijrah(firstDayOfNextMonth()));

        date = date.with(hijrah(new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                return temporal.plus(1, MONTHS);
            }
        }));
        System.out.println(date);

        date = date.with(minguo(new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                return temporal.plus(1, MONTHS);
            }
        }));
        System.out.println(date);

        date = date.with(minguo(TemporalAdjusters.firstDayOfNextMonth()));
        System.out.println(date);

        date = date.with(hijrah(TemporalAdjusters.firstDayOfNextMonth()));
        System.out.println(date);
    }

}
