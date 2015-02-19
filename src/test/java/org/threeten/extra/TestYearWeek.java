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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.IsoFields.WEEK_BASED_YEAR;
import static java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

@Test
public class TestYearWeek {
    
    private static final YearWeek TEST = YearWeek.of(2015, 1);
    
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(YearWeek.class));
        assertTrue(Comparable.class.isAssignableFrom(YearWeek.class));
        assertTrue(TemporalAdjuster.class.isAssignableFrom(YearWeek.class));
        assertTrue(Temporal.class.isAssignableFrom(YearWeek.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        YearWeek test = YearWeek.of(2015, 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }
    
    //-----------------------------------------------------------------------
    // of(int, int)
    //-----------------------------------------------------------------------
    public void test_of() {
        YearWeek.of(2015, 1);
    }
    
    @Test(expectedExceptions = DateTimeException.class)
    public void test_of_invalidWeek() {
        YearWeek.of(2014, 53);
    }
    
    
    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(TEST.toString(), "2015-W01");
    }
    
    //-----------------------------------------------------------------------
    // parse(CharSequence)
    //-----------------------------------------------------------------------
    public void test_parse_CharSequence() {
        assertEquals(YearWeek.parse("2015-W01"), TEST);
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequenceDate_invalidYear() {
        YearWeek.parse("12345-W7");
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequenceDate_invalidWeek() {
        YearWeek.parse("2015-W54");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceDate_nullCharSequence() {
        YearWeek.parse((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void test_parse_CharSequenceDateTimeFormatter() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E 'W'w YYYY");
        assertEquals(YearWeek.parse("Mon W1 2015", f), TEST);
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void test_parse_CharSequenceDateDateTimeFormatter_invalidWeek() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E 'W'w YYYY");
        YearWeek yw = YearWeek.parse("Mon W99 2015", f);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceDateTimeFormatter_nullCharSequence() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E 'W'w YYYY");
        YearWeek.parse((CharSequence) null, f);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceDateTimeFormatter_nullDateTimeFormatter() {
        YearWeek.parse("", (DateTimeFormatter) null);
    }
    
    //-----------------------------------------------------------------------
    // range(TemporalField)
    //-----------------------------------------------------------------------
    public void test_range() {
        assertEquals(TEST.range(WEEK_BASED_YEAR), WEEK_BASED_YEAR.range());
        assertEquals(TEST.range(WEEK_OF_WEEK_BASED_YEAR), WEEK_OF_WEEK_BASED_YEAR.range());
    }

    @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
    public void test_range_invalidField() {
        TEST.range(YEAR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_range_null() {
        TEST.range((TemporalField) null);
    }
    
    //-----------------------------------------------------------------------
    // withYear(int)
    //-----------------------------------------------------------------------
    public void test_withYear() {
        assertEquals(YearWeek.of(2015, 1).withYear(2014), YearWeek.of(2014, 1));
    }

    @Test(expectedExceptions = DateTimeException.class)
    public void test_withYear_int_max() {
        TEST.withYear(Integer.MAX_VALUE);
    }
    
    @Test(expectedExceptions = DateTimeException.class)
    public void test_withYear_int_min() {
        TEST.withYear(Integer.MIN_VALUE);
    }
    
    @Test(expectedExceptions = DateTimeException.class)
    public void test_withYear_invalidWeek() {
        YearWeek.of(2015, 53).withYear(2014);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals_nullYearWeek() {
        assertEquals(TEST.equals(null), false);
    }

    public void test_equals_incorrectType() {
        assertEquals(TEST.equals("Incorrect type"), false);
    }
    
}
