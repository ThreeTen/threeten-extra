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

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

import org.testng.annotations.Test;

/**
 * Test Temporals.
 */
@Test
public class TestTemporals {

    //-----------------------------------------------------------------------
    // nextWorkingDay()
    //-----------------------------------------------------------------------
    public void test_nextWorkingDay_serialization() throws IOException, ClassNotFoundException {
        TemporalAdjuster nextWorkingDay = Temporals.nextWorkingDay();
        assertTrue(nextWorkingDay instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(nextWorkingDay);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), nextWorkingDay);
    }

    public void test_nextWorkingDay() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = LocalDate.of(2007, month, i);
                LocalDate test = (LocalDate) Temporals.nextWorkingDay().adjustInto(date);
                assertTrue(test.isAfter(date));
                assertFalse(test.getDayOfWeek().equals(SATURDAY));
                assertFalse(test.getDayOfWeek().equals(SUNDAY));

                switch (date.getDayOfWeek()) {
                    case FRIDAY:
                    case SATURDAY:
                        assertEquals(test.getDayOfWeek(), MONDAY);
                        break;
                    default:
                        assertEquals(date.getDayOfWeek().plus(1), test.getDayOfWeek());
                }

                if (test.getYear() == 2007) {
                    int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                    switch (date.getDayOfWeek()) {
                        case FRIDAY:
                            assertEquals(dayDiff, 3);
                            break;
                        case SATURDAY:
                            assertEquals(dayDiff, 2);
                            break;
                        default:
                            assertEquals(dayDiff, 1);
                    }
                } else {
                    assertEquals(test.getYear(), 2008);
                    assertEquals(test.getMonth(), JANUARY);
                    assertEquals(test.getDayOfMonth(), 1);
                }
            }
        }
    }

    public void test_nextWorkingDay_yearChange() {
        LocalDate friday = LocalDate.of(2010, DECEMBER, 31);
        Temporal test = Temporals.nextWorkingDay().adjustInto(friday);
        assertEquals(LocalDate.of(2011, JANUARY, 3), test);

        LocalDate saturday = LocalDate.of(2011, DECEMBER, 31);
        test = Temporals.nextWorkingDay().adjustInto(saturday);
        assertEquals(LocalDate.of(2012, JANUARY, 2), test);
    }

    //-----------------------------------------------------------------------
    // previousWorkingDay()
    //-----------------------------------------------------------------------
    public void test_previousWorkingDay_serialization() throws IOException, ClassNotFoundException {
        TemporalAdjuster previousWorkingDay = Temporals.previousWorkingDay();
        assertTrue(previousWorkingDay instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(previousWorkingDay);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), previousWorkingDay);
    }

    public void test_previousWorkingDay() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.length(false); i++) {
                LocalDate date = LocalDate.of(2007, month, i);
                LocalDate test = (LocalDate) Temporals.previousWorkingDay().adjustInto(date);
                assertTrue(test.isBefore(date));
                assertFalse(test.getDayOfWeek().equals(SATURDAY));
                assertFalse(test.getDayOfWeek().equals(SUNDAY));

                switch (date.getDayOfWeek()) {
                    case MONDAY:
                    case SUNDAY:
                        assertEquals(test.getDayOfWeek(), FRIDAY);
                        break;
                    default:
                        assertEquals(date.getDayOfWeek().minus(1), test.getDayOfWeek());
                }

                if (test.getYear() == 2007) {
                    int dayDiff = test.getDayOfYear() - date.getDayOfYear();
                    switch (date.getDayOfWeek()) {
                        case MONDAY:
                            assertEquals(dayDiff, -3);
                            break;
                        case SUNDAY:
                            assertEquals(dayDiff, -2);
                            break;
                        default:
                            assertEquals(dayDiff, -1);
                    }
                } else {
                    assertEquals(test.getYear(), 2006);
                    assertEquals(test.getMonth(), DECEMBER);
                    assertEquals(test.getDayOfMonth(), 29);
                }
            }
        }
    }

    public void test_previousWorkingDay_yearChange() {
        LocalDate monday = LocalDate.of(2011, JANUARY, 3);
        Temporal test = Temporals.previousWorkingDay().adjustInto(monday);
        assertEquals(LocalDate.of(2010, DECEMBER, 31), test);

        LocalDate sunday = LocalDate.of(2011, JANUARY, 2);
        test = Temporals.previousWorkingDay().adjustInto(sunday);
        assertEquals(LocalDate.of(2010, DECEMBER, 31), test);
    }

}
