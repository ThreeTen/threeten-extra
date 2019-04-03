/*
9 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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

import static java.time.Month.DECEMBER;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.JulianFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Test OffsetDate.
 */
@RunWith(DataProviderRunner.class)
public class TestOffsetDate extends AbstractDateTimeTest {
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);

    private OffsetDate TEST_2007_07_15_PONE;

    @Before
    public void setUp() {
        TEST_2007_07_15_PONE = OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Override
    protected List<TemporalAccessor> samples() {
        TemporalAccessor[] array = {TEST_2007_07_15_PONE, OffsetDate.MIN, OffsetDate.MAX};
        return Arrays.asList(array);
    }

    @Override
    protected List<TemporalField> validFields() {
        TemporalField[] array = {
            DAY_OF_WEEK,
            ALIGNED_DAY_OF_WEEK_IN_MONTH,
            ALIGNED_DAY_OF_WEEK_IN_YEAR,
            DAY_OF_MONTH,
            DAY_OF_YEAR,
            EPOCH_DAY,
            ALIGNED_WEEK_OF_MONTH,
            ALIGNED_WEEK_OF_YEAR,
            MONTH_OF_YEAR,
            PROLEPTIC_MONTH,
            YEAR_OF_ERA,
            YEAR,
            ERA,
            OFFSET_SECONDS,
            JulianFields.JULIAN_DAY,
            JulianFields.MODIFIED_JULIAN_DAY,
            JulianFields.RATA_DIE,
        };
        return Arrays.asList(array);
    }

    @Override
    protected List<TemporalField> invalidFields() {
        List<TemporalField> list = new ArrayList<>(Arrays.<TemporalField>asList(ChronoField.values()));
        list.removeAll(validFields());
        return list;
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_serialization() throws ClassNotFoundException, IOException {
        assertSerializable(TEST_2007_07_15_PONE);
        assertSerializable(OffsetDate.MIN);
        assertSerializable(OffsetDate.MAX);
    }

    @Test
    public void test_serialization_format() throws ClassNotFoundException, IOException {
        assertEqualsSerialisedForm(OffsetDate.of(LocalDate.of(2012, 9, 16), ZoneOffset.of("+01:00")));
    }

    //-----------------------------------------------------------------------
    // constants
    //-----------------------------------------------------------------------
    @Test
    public void constant_MIN() {
        check(OffsetDate.MIN, Year.MIN_VALUE, 1, 1, ZoneOffset.MAX);
    }

    @Test
    public void constant_MAX() {
        check(OffsetDate.MAX, Year.MAX_VALUE, 12, 31, ZoneOffset.MIN);
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @Test
    public void now() {
        OffsetDate expected = OffsetDate.now(Clock.systemDefaultZone());
        OffsetDate test = OffsetDate.now();
        for (int i = 0; i < 100; i++) {
            if (expected.equals(test)) {
                return;
            }
            expected = OffsetDate.now(Clock.systemDefaultZone());
            test = OffsetDate.now();
        }
        assertEquals(expected, test);
    }

    @Test
    public void now_Clock_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.fixed(instant, ZoneOffset.UTC);
            OffsetDate test = OffsetDate.now(clock);
            check(test, 1970, 1, (i < 24 * 60 * 60 ? 1 : 2), ZoneOffset.UTC);
        }
    }

    @Test
    public void now_Clock_allSecsInDay_beforeEpoch() {
        for (int i =-1; i >= -(2 * 24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.fixed(instant, ZoneOffset.UTC);
            OffsetDate test = OffsetDate.now(clock);
            check(test, 1969, 12, (i >= -24 * 60 * 60 ? 31 : 30), ZoneOffset.UTC);
        }
    }

    @Test
    public void now_Clock_offsets() {
        Instant base = LocalDateTime.of(1970, 1, 1, 12, 0).toInstant(ZoneOffset.UTC);
        for (int i = -9; i < 15; i++) {
            ZoneOffset offset = ZoneOffset.ofHours(i);
            Clock clock = Clock.fixed(base, offset);
            OffsetDate test = OffsetDate.now(clock);
            check(test, 1970, 1, (i >= 12 ? 2 : 1), offset);
        }
    }

    @Test(expected=NullPointerException.class)
    public void now_Clock_nullZoneId() {
        OffsetDate.now((ZoneId) null);
    }

    @Test(expected=NullPointerException.class)
    public void now_Clock_nullClock() {
        OffsetDate.now((Clock) null);
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    private void check(OffsetDate test, int y, int mo, int d, ZoneOffset offset) {
        assertEquals(LocalDate.of(y, mo, d), test.getDate());
        assertEquals(offset, test.getOffset());

        assertEquals(y, test.getYear());
        assertEquals(mo, test.getMonth().getValue());
        assertEquals(d, test.getDayOfMonth());

        assertEquals(test, test);
        assertEquals(test.hashCode(), test.hashCode());
        assertEquals(test, OffsetDate.of(LocalDate.of(y, mo, d), offset));
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_intMonthInt() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2007, Month.JULY, 15), OFFSET_PONE);
        check(test, 2007, 7, 15, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_ints() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PONE);
        check(test, 2007, 7, 15, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_intsMonthOffset() {
        assertEquals(OffsetDate.of(LocalDate.of(2007, Month.JULY, 15), OFFSET_PONE), TEST_2007_07_15_PONE);
    }

    @Test(expected=DateTimeException.class)
    public void factory_of_intsMonthOffset_dayTooLow() {
        OffsetDate.of(LocalDate.of(2007, Month.JANUARY, 0), OFFSET_PONE);
    }

    @Test(expected=DateTimeException.class)
    public void factory_of_intsMonthOffset_dayTooHigh() {
        OffsetDate.of(LocalDate.of(2007, Month.JANUARY, 32), OFFSET_PONE);
    }

    @Test(expected=NullPointerException.class)
    public void factory_of_intsMonthOffset_nullMonth() {
        OffsetDate.of(LocalDate.of(2007, null, 30), OFFSET_PONE);
    }

    @Test(expected=DateTimeException.class)
    public void factory_of_intsMonthOffset_yearTooLow() {
        OffsetDate.of(LocalDate.of(Integer.MIN_VALUE, Month.JANUARY, 1), OFFSET_PONE);
    }

    @Test(expected=NullPointerException.class)
    public void factory_of_intsMonthOffset_nullOffset() {
        OffsetDate.of(LocalDate.of(2007, Month.JANUARY, 30), null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_intsOffset() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PONE);
        check(test, 2007, 7, 15, OFFSET_PONE);
    }

    @Test(expected=DateTimeException.class)
    public void factory_of_ints_dayTooLow() {
        OffsetDate.of(LocalDate.of(2007, 1, 0), OFFSET_PONE);
    }

    @Test(expected=DateTimeException.class)
    public void factory_of_ints_dayTooHigh() {
        OffsetDate.of(LocalDate.of(2007, 1, 32), OFFSET_PONE);
    }

    @Test(expected=DateTimeException.class)
    public void factory_of_ints_monthTooLow() {
        OffsetDate.of(LocalDate.of(2007, 0, 1), OFFSET_PONE);
    }

    @Test(expected=DateTimeException.class)
    public void factory_of_ints_monthTooHigh() {
        OffsetDate.of(LocalDate.of(2007, 13, 1), OFFSET_PONE);
    }

    @Test(expected=DateTimeException.class)
    public void factory_of_ints_yearTooLow() {
        OffsetDate.of(LocalDate.of(Integer.MIN_VALUE, 1, 1), OFFSET_PONE);
    }

    @Test(expected=NullPointerException.class)
    public void factory_of_ints_nullOffset() {
        OffsetDate.of(LocalDate.of(2007, 1, 1), (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void factory_of_LocalDateZoneOffset() {
        LocalDate localDate = LocalDate.of(2008, 6, 30);
        OffsetDate test = OffsetDate.of(localDate, OFFSET_PONE);
        check(test, 2008, 6, 30, OFFSET_PONE);
    }

    @Test(expected=NullPointerException.class)
    public void factory_of_LocalDateZoneOffset_nullDate() {
        OffsetDate.of((LocalDate) null, OFFSET_PONE);
    }

    @Test(expected=NullPointerException.class)
    public void factory_of_LocalDateZoneOffset_nullOffset() {
        LocalDate localDate = LocalDate.of(2008, 6, 30);
        OffsetDate.of(localDate, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // from(TemporalAccessor)
    //-----------------------------------------------------------------------
    @Test
    public void test_from_TemporalAccessor_OD() {
        assertEquals(TEST_2007_07_15_PONE, OffsetDate.from(TEST_2007_07_15_PONE));
    }

    @Test
    public void test_from_TemporalAccessor_ZDT() {
        ZonedDateTime base = LocalDateTime.of(2007, 7, 15, 17, 30).atZone(OFFSET_PONE);
        assertEquals(TEST_2007_07_15_PONE, OffsetDate.from(base));
    }

    @Test(expected=DateTimeException.class)
    public void test_from_TemporalAccessor_invalid_noDerive() {
        OffsetDate.from(LocalTime.of(12, 30));
    }

    @Test(expected=NullPointerException.class)
    public void test_from_TemporalAccessor_null() {
        OffsetDate.from((TemporalAccessor) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test
    @UseDataProvider("provider_sampleToString")
    public void factory_parse_validText(int y, int m, int d, String offsetId, String parsable) {
        OffsetDate t = OffsetDate.parse(parsable);
        assertNotNull(parsable, t);
        assertEquals(parsable, y, t.getYear());
        assertEquals(parsable, m, t.getMonth().getValue());
        assertEquals(parsable, d, t.getDayOfMonth());
        assertEquals(ZoneOffset.of(offsetId), t.getOffset());
    }

    @DataProvider
    public static Object[][] provider_sampleBadParse() {
        return new Object[][]{
                {"2008/07/05"},
                {"10000-01-01"},
                {"2008-1-1"},
                {"2008--01"},
                {"ABCD-02-01"},
                {"2008-AB-01"},
                {"2008-02-AB"},
                {"-0000-02-01"},
                {"2008-02-01Y"},
                {"2008-02-01+19:00"},
                {"2008-02-01+01/00"},
                {"2008-02-01+1900"},
                {"2008-02-01+01:60"},
                {"2008-02-01+01:30:123"},
                {"2008-02-01"},
                {"2008-02-01+01:00[Europe/Paris]"},
        };
    }

    @Test(expected=DateTimeParseException.class)
    @UseDataProvider("provider_sampleBadParse")
    public void factory_parse_invalidText(String unparsable) {
        OffsetDate.parse(unparsable);
    }

    @Test(expected=DateTimeParseException.class)
    public void factory_parse_illegalValue() {
        OffsetDate.parse("2008-06-32+01:00");
    }

    @Test(expected=DateTimeParseException.class)
    public void factory_parse_invalidValue() {
        OffsetDate.parse("2008-06-31+01:00");
    }

    @Test(expected=NullPointerException.class)
    public void factory_parse_nullText() {
        OffsetDate.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // parse(DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void factory_parse_formatter() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("y M d XXX");
        OffsetDate test = OffsetDate.parse("2010 12 3 +01:00", f);
        assertEquals(OffsetDate.of(LocalDate.of(2010, 12, 3), ZoneOffset.ofHours(1)), test);
    }

    @Test(expected=NullPointerException.class)
    public void factory_parse_formatter_nullText() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("y M d");
        OffsetDate.parse((String) null, f);
    }

    @Test(expected=NullPointerException.class)
    public void factory_parse_formatter_nullFormatter() {
        OffsetDate.parse("ANY", null);
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    @Test(expected=NullPointerException.class)
    public void constructor_nullDate() throws Throwable  {
        Constructor<OffsetDate> con = OffsetDate.class.getDeclaredConstructor(LocalDate.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(null, OFFSET_PONE);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected=NullPointerException.class)
    public void constructor_nullOffset() throws Throwable  {
        Constructor<OffsetDate> con = OffsetDate.class.getDeclaredConstructor(LocalDate.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(LocalDate.of(2008, 6, 30), null);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] provider_sampleDates() {
        return new Object[][] {
            {2008, 7, 5, OFFSET_PTWO},
            {2007, 7, 5, OFFSET_PONE},
            {2006, 7, 5, OFFSET_PTWO},
            {2005, 7, 5, OFFSET_PONE},
            {2004, 1, 1, OFFSET_PTWO},
            {-1, 1, 2, OFFSET_PONE},
            {999999, 11, 20, ZoneOffset.ofHoursMinutesSeconds(6, 9, 12)},
        };
    }

    @Test
    @UseDataProvider("provider_sampleDates")
    public void test_get_OffsetDate(int y, int m, int d, ZoneOffset offset) {
        LocalDate localDate = LocalDate.of(y, m, d);
        OffsetDate a = OffsetDate.of(localDate, offset);

        assertEquals(localDate, a.getDate());
        assertEquals(offset, a.getOffset());
        assertEquals(localDate.toString() + offset.toString(), a.toString());
        assertEquals(localDate.getYear(), a.getYear());
        assertEquals(localDate.getMonth(), a.getMonth());
        assertEquals(localDate.getDayOfMonth(), a.getDayOfMonth());
        assertEquals(localDate.getDayOfYear(), a.getDayOfYear());
        assertEquals(localDate.getDayOfWeek(), a.getDayOfWeek());
    }

    //-----------------------------------------------------------------------
    // get(TemporalField)
    //-----------------------------------------------------------------------
    @Test
    public void test_get_TemporalField() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        assertEquals(2008, test.get(ChronoField.YEAR));
        assertEquals(6, test.get(ChronoField.MONTH_OF_YEAR));
        assertEquals(30, test.get(ChronoField.DAY_OF_MONTH));
        assertEquals(1, test.get(ChronoField.DAY_OF_WEEK));
        assertEquals(182, test.get(ChronoField.DAY_OF_YEAR));

        assertEquals(3600, test.get(ChronoField.OFFSET_SECONDS));
    }

    @Test
    public void test_getLong_TemporalField() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        assertEquals(2008, test.getLong(ChronoField.YEAR));
        assertEquals(6, test.getLong(ChronoField.MONTH_OF_YEAR));
        assertEquals(30, test.getLong(ChronoField.DAY_OF_MONTH));
        assertEquals(1, test.getLong(ChronoField.DAY_OF_WEEK));
        assertEquals(182, test.getLong(ChronoField.DAY_OF_YEAR));

        assertEquals(3600, test.getLong(ChronoField.OFFSET_SECONDS));
    }

    //-----------------------------------------------------------------------
    // query(TemporalQuery)
    //-----------------------------------------------------------------------
    @Test
    public void test_query_chrono() {
        assertEquals(IsoChronology.INSTANCE, TEST_2007_07_15_PONE.query(TemporalQueries.chronology()));
        assertEquals(IsoChronology.INSTANCE, TemporalQueries.chronology().queryFrom(TEST_2007_07_15_PONE));
    }

    @Test
    public void test_query_zoneId() {
        assertEquals(null, TEST_2007_07_15_PONE.query(TemporalQueries.zoneId()));
        assertEquals(null, TemporalQueries.zoneId().queryFrom(TEST_2007_07_15_PONE));
    }

    @Test
    public void test_query_precision() {
        assertEquals(ChronoUnit.DAYS, TEST_2007_07_15_PONE.query(TemporalQueries.precision()));
        assertEquals(ChronoUnit.DAYS, TemporalQueries.precision().queryFrom(TEST_2007_07_15_PONE));
    }

    @Test
    public void test_query_offset() {
        assertEquals(OFFSET_PONE, TEST_2007_07_15_PONE.query(TemporalQueries.offset()));
        assertEquals(OFFSET_PONE, TemporalQueries.offset().queryFrom(TEST_2007_07_15_PONE));
    }

    @Test
    public void test_query_zone() {
        assertEquals(OFFSET_PONE, TEST_2007_07_15_PONE.query(TemporalQueries.zone()));
        assertEquals(OFFSET_PONE, TemporalQueries.zone().queryFrom(TEST_2007_07_15_PONE));
    }

    @Test(expected=NullPointerException.class)
    public void test_query_null() {
        TEST_2007_07_15_PONE.query(null);
    }

    //-----------------------------------------------------------------------
    // withOffset()
    //-----------------------------------------------------------------------
    @Test
    public void test_withOffset() {
        OffsetDate base = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        OffsetDate test = base.withOffset(OFFSET_PTWO);
        assertEquals(base.getDate(), test.getDate());
        assertEquals(OFFSET_PTWO, test.getOffset());
    }

    @Test
    public void test_withOffset_noChange() {
        OffsetDate base = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        OffsetDate test = base.withOffset(OFFSET_PONE);
        assertEquals(base, test);
    }

    @Test(expected=NullPointerException.class)
    public void test_withOffset_null() {
        TEST_2007_07_15_PONE.withOffset(null);
    }

    //-----------------------------------------------------------------------
    // with(WithAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_adjustment() {
        final OffsetDate sample = OffsetDate.of(LocalDate.of(2012, 3, 4), OFFSET_PONE);
        TemporalAdjuster adjuster = new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal dateTime) {
                return sample;
            }
        };
        assertEquals(sample, TEST_2007_07_15_PONE.with(adjuster));
    }

    @Test
    public void test_with_adjustment_LocalDate() {
        OffsetDate test = TEST_2007_07_15_PONE.with(LocalDate.of(2008, 6, 30));
        assertEquals(OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE), test);
    }

    @Test
    public void test_with_adjustment_OffsetDate() {
        OffsetDate test = TEST_2007_07_15_PONE.with(OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO));
        assertEquals(OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO), test);
    }

    @Test
    public void test_with_adjustment_ZoneOffset() {
        OffsetDate test = TEST_2007_07_15_PONE.with(OFFSET_PTWO);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PTWO), test);
    }

    @Test
    public void test_with_adjustment_Month() {
        OffsetDate test = TEST_2007_07_15_PONE.with(DECEMBER);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 12, 15), OFFSET_PONE), test);
    }

    @Test
    public void test_with_adjustment_offsetUnchanged() {
        OffsetDate base = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        OffsetDate test = base.with(Year.of(2008));
        assertEquals(base, test);
    }

    @Test
    public void test_with_adjustment_noChange() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        OffsetDate base = OffsetDate.of(date, OFFSET_PONE);
        OffsetDate test = base.with(date);
        assertEquals(base, test);
    }

    @Test(expected=NullPointerException.class)
    public void test_with_adjustment_null() {
        TEST_2007_07_15_PONE.with((TemporalAdjuster) null);
    }

    //-----------------------------------------------------------------------
    // with(TemporalField, long)
    //-----------------------------------------------------------------------
    @Test
    public void test_with_TemporalField() {
        OffsetDate test = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        assertEquals(OffsetDate.of(LocalDate.of(2009, 6, 30), OFFSET_PONE), test.with(ChronoField.YEAR, 2009));
        assertEquals(OffsetDate.of(LocalDate.of(2008, 7, 30), OFFSET_PONE), test.with(ChronoField.MONTH_OF_YEAR, 7));
        assertEquals(OffsetDate.of(LocalDate.of(2008, 6, 1), OFFSET_PONE), test.with(ChronoField.DAY_OF_MONTH, 1));
        assertEquals(OffsetDate.of(LocalDate.of(2008, 7, 1), OFFSET_PONE), test.with(ChronoField.DAY_OF_WEEK, 2));
        assertEquals(OffsetDate.of(LocalDate.of(2008, 7, 1), OFFSET_PONE), test.with(ChronoField.DAY_OF_YEAR, 183));

        assertEquals(OffsetDate.of(LocalDate.of(2008, 6, 30), ZoneOffset.ofHoursMinutesSeconds(2, 0, 5)), test.with(ChronoField.OFFSET_SECONDS, 7205));
    }

    @Test(expected=NullPointerException.class)
    public void test_with_TemporalField_null() {
        TEST_2007_07_15_PONE.with((TemporalField) null, 0);
    }

    @Test(expected=DateTimeException.class)
    public void test_with_TemporalField_invalidField() {
        TEST_2007_07_15_PONE.with(ChronoField.AMPM_OF_DAY, 0);
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    @Test
    public void test_withYear_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withYear(2008);
        assertEquals(OffsetDate.of(LocalDate.of(2008, 7, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_withYear_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withYear(2007);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test(expected=DateTimeException.class)
    public void test_withYear_int_invalid() {
        TEST_2007_07_15_PONE.withYear(Year.MIN_VALUE - 1);
    }

    @Test
    public void test_withYear_int_adjustDay() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).withYear(2007);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PONE);
        assertEquals(expected, t);
    }

    //-----------------------------------------------------------------------
    // withMonth()
    //-----------------------------------------------------------------------
    @Test
    public void test_withMonth_int_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withMonth(1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 1, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_withMonth_int_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withMonth(7);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test(expected=DateTimeException.class)
    public void test_withMonth_int_invalid() {
        TEST_2007_07_15_PONE.withMonth(13);
    }

    @Test
    public void test_withMonth_int_adjustDay() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PONE).withMonth(11);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 11, 30), OFFSET_PONE);
        assertEquals(expected, t);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    @Test
    public void test_withDayOfMonth_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth(1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 1), OFFSET_PONE), t);
    }

    @Test
    public void test_withDayOfMonth_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth(15);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 15), OFFSET_PONE), t);
    }

    @Test(expected=DateTimeException.class)
    public void test_withDayOfMonth_invalidForMonth() {
        OffsetDate.of(LocalDate.of(2007, 11, 30), OFFSET_PONE).withDayOfMonth(31);
    }

    @Test(expected=DateTimeException.class)
    public void test_withDayOfMonth_invalidAlways() {
        OffsetDate.of(LocalDate.of(2007, 11, 30), OFFSET_PONE).withDayOfMonth(32);
    }

    //-----------------------------------------------------------------------
    // withDayOfYear(int)
    //-----------------------------------------------------------------------
    @Test
    public void test_withDayOfYear_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfYear(33);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 2, 2), OFFSET_PONE), t);
    }

    @Test
    public void test_withDayOfYear_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.withDayOfYear(31 + 28 + 31 + 30 + 31 + 30 + 15);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test(expected=DateTimeException.class)
    public void test_withDayOfYear_illegal() {
        TEST_2007_07_15_PONE.withDayOfYear(367);
    }

    @Test(expected=DateTimeException.class)
    public void test_withDayOfYear_invalid() {
        TEST_2007_07_15_PONE.withDayOfYear(366);
    }

    //-----------------------------------------------------------------------
    // plus(PlusAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_PlusAdjuster() {
        MockSimplePeriod period = MockSimplePeriod.of(7, ChronoUnit.MONTHS);
        OffsetDate t = TEST_2007_07_15_PONE.plus(period);
        assertEquals(OffsetDate.of(LocalDate.of(2008, 2, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plus_PlusAdjuster_noChange() {
        MockSimplePeriod period = MockSimplePeriod.of(0, ChronoUnit.MONTHS);
        OffsetDate t = TEST_2007_07_15_PONE.plus(period);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_plus_PlusAdjuster_zero() {
        OffsetDate t = TEST_2007_07_15_PONE.plus(Period.ZERO);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test(expected=NullPointerException.class)
    public void test_plus_PlusAdjuster_null() {
        TEST_2007_07_15_PONE.plus((TemporalAmount) null);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test
    public void test_plusYears_long_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(1);
        assertEquals(OffsetDate.of(LocalDate.of(2008, 7, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusYears_long_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1);
        assertEquals(OffsetDate.of(LocalDate.of(2006, 7, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusYears_long_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(0);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_plusYears_long_adjustDay() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).plusYears(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2009, 2, 28), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_plusYears_long_big() {
        long years = 20L + Year.MAX_VALUE;
        OffsetDate test = OffsetDate.of(LocalDate.of(-40, 6, 1), OFFSET_PONE).plusYears(years);
        assertEquals(OffsetDate.of(LocalDate.of((int) (-40L + years), 6, 1), OFFSET_PONE), test);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusYears_long_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 1, 1), OFFSET_PONE).plusYears(1);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusYears_long_invalidTooLargeMaxAddMax() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.plusYears(Long.MAX_VALUE);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusYears_long_invalidTooLargeMaxAddMin() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.plusYears(Long.MIN_VALUE);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusYears_long_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusYears(-1);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    @Test
    public void test_plusMonths_long_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 8, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusMonths_long_overYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(25);
        assertEquals(OffsetDate.of(LocalDate.of(2009, 8, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusMonths_long_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 6, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusMonths_long_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-7);
        assertEquals(OffsetDate.of(LocalDate.of(2006, 12, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusMonths_long_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(-31);
        assertEquals(OffsetDate.of(LocalDate.of(2004, 12, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusMonths_long_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusMonths(0);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_plusMonths_long_adjustDayFromLeapYear() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).plusMonths(12);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2009, 2, 28), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_plusMonths_long_adjustDayFromMonthLength() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2007, 3, 31), OFFSET_PONE).plusMonths(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 4, 30), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_plusMonths_long_big() {
        long months = 20L + Integer.MAX_VALUE;
        OffsetDate test = OffsetDate.of(LocalDate.of(-40, 6, 1), OFFSET_PONE).plusMonths(months);
        assertEquals(OffsetDate.of(LocalDate.of((int) (-40L + months / 12), 6 + (int) (months % 12), 1), OFFSET_PONE), test);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusMonths_long_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE).plusMonths(1);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusMonths_long_invalidTooLargeMaxAddMax() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.plusMonths(Long.MAX_VALUE);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusMonths_long_invalidTooLargeMaxAddMin() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.plusMonths(Long.MIN_VALUE);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusMonths_long_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusMonths(-1);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] provider_samplePlusWeeksSymmetry() {
        return new Object[][] {
            {OffsetDate.of(LocalDate.of(-1, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(-1, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 12, 31), OFFSET_PTWO)},
        };
    }

    @Test
    @UseDataProvider("provider_samplePlusWeeksSymmetry")
    public void test_plusWeeks_symmetry(OffsetDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            OffsetDate t = reference.plusWeeks(weeks).plusWeeks(-weeks);
            assertEquals(reference, t);

            t = reference.plusWeeks(-weeks).plusWeeks(weeks);
            assertEquals(reference, t);
        }
    }

    @Test
    public void test_plusWeeks_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 22), OFFSET_PONE), t);
    }

    @Test
    public void test_plusWeeks_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(9);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 9, 16), OFFSET_PONE), t);
    }

    @Test
    public void test_plusWeeks_overYears() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2006, 7, 16), OFFSET_PONE).plusWeeks(52);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_plusWeeks_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1).plusWeeks(104);
        assertEquals(OffsetDate.of(LocalDate.of(2008, 7, 12), OFFSET_PONE), t);
    }

    @Test
    public void test_plusWeeks_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(-1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 8), OFFSET_PONE), t);
    }

    @Test
    public void test_plusWeeks_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(-28);
        assertEquals(OffsetDate.of(LocalDate.of(2006, 12, 31), OFFSET_PONE), t);
    }

    @Test
    public void test_plusWeeks_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(-104);
        assertEquals(OffsetDate.of(LocalDate.of(2005, 7, 17), OFFSET_PONE), t);
    }

    @Test
    public void test_plusWeeks_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusWeeks(0);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_plusWeeks_maximum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 24), OFFSET_PONE).plusWeeks(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_plusWeeks_minimum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 8), OFFSET_PONE).plusWeeks(-1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusWeeks_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks(1);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusWeeks_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 7), OFFSET_PONE).plusWeeks(-1);
    }

    @Test(expected=ArithmeticException.class)
    public void test_plusWeeks_invalidMaxMinusMax() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks(Long.MAX_VALUE);
    }

    @Test(expected=ArithmeticException.class)
    public void test_plusWeeks_invalidMaxMinusMin() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] provider_samplePlusDaysSymmetry() {
        return new Object[][] {
            {OffsetDate.of(LocalDate.of(-1, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(-1, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 12, 31), OFFSET_PTWO)},
        };
    }

    @Test
    @UseDataProvider("provider_samplePlusDaysSymmetry")
    public void test_plusDays_symmetry(OffsetDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            OffsetDate t = reference.plusDays(days).plusDays(-days);
            assertEquals(reference, t);

            t = reference.plusDays(-days).plusDays(days);
            assertEquals(reference, t);
        }
    }

    @Test
    public void test_plusDays_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 16), OFFSET_PONE), t);
    }

    @Test
    public void test_plusDays_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(62);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 9, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusDays_overYears() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2006, 7, 14), OFFSET_PONE).plusDays(366);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_plusDays_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(-1).plusDays(365 + 366);
        assertEquals(OffsetDate.of(LocalDate.of(2008, 7, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusDays_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(-1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 14), OFFSET_PONE), t);
    }

    @Test
    public void test_plusDays_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(-196);
        assertEquals(OffsetDate.of(LocalDate.of(2006, 12, 31), OFFSET_PONE), t);
    }

    @Test
    public void test_plusDays_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(-730);
        assertEquals(OffsetDate.of(LocalDate.of(2005, 7, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_plusDays_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.plusDays(0);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_plusDays_maximum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 30), OFFSET_PONE).plusDays(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_plusDays_minimum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 2), OFFSET_PONE).plusDays(-1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusDays_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE).plusDays(1);
    }

    @Test(expected=DateTimeException.class)
    public void test_plusDays_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusDays(-1);
    }

    @Test(expected=ArithmeticException.class)
    public void test_plusDays_overflowTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE).plusDays(Long.MAX_VALUE);
    }

    @Test(expected=ArithmeticException.class)
    public void test_plusDays_overflowTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusDays(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minus(MinusAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_MinusAdjuster() {
        MockSimplePeriod period = MockSimplePeriod.of(7, ChronoUnit.MONTHS);
        OffsetDate t = TEST_2007_07_15_PONE.minus(period);
        assertEquals(OffsetDate.of(LocalDate.of(2006, 12, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_minus_MinusAdjuster_noChange() {
        MockSimplePeriod period = MockSimplePeriod.of(0, ChronoUnit.MONTHS);
        OffsetDate t = TEST_2007_07_15_PONE.minus(period);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_minus_MinusAdjuster_zero() {
        OffsetDate t = TEST_2007_07_15_PONE.minus(Period.ZERO);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test(expected=NullPointerException.class)
    public void test_plus_MinusAdjuster_null() {
        TEST_2007_07_15_PONE.minus((TemporalAmount) null);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test
    public void test_minusYears_long_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(1);
        assertEquals(OffsetDate.of(LocalDate.of(2006, 7, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_minusYears_long_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(-1);
        assertEquals(OffsetDate.of(LocalDate.of(2008, 7, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_minusYears_long_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(0);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_minusYears_long_adjustDay() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).minusYears(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_minusYears_long_big() {
        long years = 20L + Year.MAX_VALUE;
        OffsetDate test = OffsetDate.of(LocalDate.of(40, 6, 1), OFFSET_PONE).minusYears(years);
        assertEquals(OffsetDate.of(LocalDate.of((int) (40L - years), 6, 1), OFFSET_PONE), test);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusYears_long_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 1, 1), OFFSET_PONE).minusYears(-1);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusYears_long_invalidTooLargeMaxAddMax() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.minusYears(Long.MAX_VALUE);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusYears_long_invalidTooLargeMaxAddMin() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.minusYears(Long.MIN_VALUE);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusYears_long_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusYears(1);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    @Test
    public void test_minusMonths_long_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 6, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_minusMonths_long_overYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(25);
        assertEquals(OffsetDate.of(LocalDate.of(2005, 6, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_minusMonths_long_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 8, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_minusMonths_long_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-7);
        assertEquals(OffsetDate.of(LocalDate.of(2008, 2, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_minusMonths_long_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(-31);
        assertEquals(OffsetDate.of(LocalDate.of(2010, 2, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_minusMonths_long_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusMonths(0);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_minusMonths_long_adjustDayFromLeapYear() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PONE).minusMonths(12);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_minusMonths_long_adjustDayFromMonthLength() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2007, 3, 31), OFFSET_PONE).minusMonths(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_minusMonths_long_big() {
        long months = 20L + Integer.MAX_VALUE;
        OffsetDate test = OffsetDate.of(LocalDate.of(40, 6, 1), OFFSET_PONE).minusMonths(months);
        assertEquals(OffsetDate.of(LocalDate.of((int) (40L - months / 12), 6 - (int) (months % 12), 1), OFFSET_PONE), test);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusMonths_long_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE).minusMonths(-1);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusMonths_long_invalidTooLargeMaxAddMax() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.minusMonths(Long.MAX_VALUE);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusMonths_long_invalidTooLargeMaxAddMin() {
        OffsetDate test = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 1), OFFSET_PONE);
        test.minusMonths(Long.MIN_VALUE);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusMonths_long_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusMonths(1);
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] provider_sampleMinusWeeksSymmetry() {
        return new Object[][] {
            {OffsetDate.of(LocalDate.of(-1, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(-1, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 12, 31), OFFSET_PTWO)},
        };
    }

    @Test
    @UseDataProvider("provider_sampleMinusWeeksSymmetry")
    public void test_minusWeeks_symmetry(OffsetDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            OffsetDate t = reference.minusWeeks(weeks).minusWeeks(-weeks);
            assertEquals(reference, t);

            t = reference.minusWeeks(-weeks).minusWeeks(weeks);
            assertEquals(reference, t);
        }
    }

    @Test
    public void test_minusWeeks_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 8), OFFSET_PONE), t);
    }

    @Test
    public void test_minusWeeks_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(9);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 5, 13), OFFSET_PONE), t);
    }

    @Test
    public void test_minusWeeks_overYears() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 7, 13), OFFSET_PONE).minusWeeks(52);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_minusWeeks_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusYears(-1).minusWeeks(104);
        assertEquals(OffsetDate.of(LocalDate.of(2006, 7, 18), OFFSET_PONE), t);
    }

    @Test
    public void test_minusWeeks_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(-1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 22), OFFSET_PONE), t);
    }

    @Test
    public void test_minusWeeks_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(-28);
        assertEquals(OffsetDate.of(LocalDate.of(2008, 1, 27), OFFSET_PONE), t);
    }

    @Test
    public void test_minusWeeks_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(-104);
        assertEquals(OffsetDate.of(LocalDate.of(2009, 7, 12), OFFSET_PONE), t);
    }

    @Test
    public void test_minusWeeks_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusWeeks(0);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_minusWeeks_maximum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 24), OFFSET_PONE).minusWeeks(-1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_minusWeeks_minimum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 8), OFFSET_PONE).minusWeeks(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusWeeks_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks(-1);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusWeeks_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 7), OFFSET_PONE).minusWeeks(1);
    }

    @Test(expected=ArithmeticException.class)
    public void test_minusWeeks_invalidMaxMinusMax() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks(Long.MAX_VALUE);
    }

    @Test(expected=ArithmeticException.class)
    public void test_minusWeeks_invalidMaxMinusMin() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] provider_sampleMinusDaysSymmetry() {
        return new Object[][] {
            {OffsetDate.of(LocalDate.of(-1, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(-1, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(-1, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(0, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(0, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2007, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 2, 29), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2008, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2008, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2099, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2099, 12, 31), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 1, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 2, 28), OFFSET_PTWO)},
            {OffsetDate.of(LocalDate.of(2100, 3, 1), OFFSET_PONE)},
            {OffsetDate.of(LocalDate.of(2100, 12, 31), OFFSET_PTWO)},
        };
    }

    @Test
    @UseDataProvider("provider_sampleMinusDaysSymmetry")
    public void test_minusDays_symmetry(OffsetDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            OffsetDate t = reference.minusDays(days).minusDays(-days);
            assertEquals(reference, t);

            t = reference.minusDays(-days).minusDays(days);
            assertEquals(reference, t);
        }
    }

    @Test
    public void test_minusDays_normal() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 14), OFFSET_PONE), t);
    }

    @Test
    public void test_minusDays_overMonths() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(62);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 5, 14), OFFSET_PONE), t);
    }

    @Test
    public void test_minusDays_overYears() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 7, 16), OFFSET_PONE).minusDays(367);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_minusDays_overLeapYears() {
        OffsetDate t = TEST_2007_07_15_PONE.plusYears(2).minusDays(365 + 366);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_minusDays_negative() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(-1);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 7, 16), OFFSET_PONE), t);
    }

    @Test
    public void test_minusDays_negativeAcrossYear() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(-169);
        assertEquals(OffsetDate.of(LocalDate.of(2007, 12, 31), OFFSET_PONE), t);
    }

    @Test
    public void test_minusDays_negativeOverYears() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(-731);
        assertEquals(OffsetDate.of(LocalDate.of(2009, 7, 15), OFFSET_PONE), t);
    }

    @Test
    public void test_minusDays_noChange() {
        OffsetDate t = TEST_2007_07_15_PONE.minusDays(0);
        assertEquals(TEST_2007_07_15_PONE, t);
    }

    @Test
    public void test_minusDays_maximum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 30), OFFSET_PONE).minusDays(-1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test
    public void test_minusDays_minimum() {
        OffsetDate t = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 2), OFFSET_PONE).minusDays(1);
        OffsetDate expected = OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE);
        assertEquals(expected, t);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusDays_invalidTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE).minusDays(-1);
    }

    @Test(expected=DateTimeException.class)
    public void test_minusDays_invalidTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusDays(1);
    }

    @Test(expected=ArithmeticException.class)
    public void test_minusDays_overflowTooLarge() {
        OffsetDate.of(LocalDate.of(Year.MAX_VALUE, 12, 31), OFFSET_PONE).minusDays(Long.MIN_VALUE);
    }

    @Test(expected=ArithmeticException.class)
    public void test_minusDays_overflowTooSmall() {
        OffsetDate.of(LocalDate.of(Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusDays(Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    // atTime()
    //-----------------------------------------------------------------------
    @Test
    public void test_atTime_Local() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO);
        assertEquals(OffsetDateTime.of(LocalDate.of(2008, 6, 30), LocalTime.of(11, 30), OFFSET_PTWO),
                t.atTime(LocalTime.of(11, 30)));
    }

    @Test(expected=NullPointerException.class)
    public void test_atTime_Local_nullLocalTime() {
        OffsetDate t = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO);
        t.atTime((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // getDate()
    //-----------------------------------------------------------------------
    @Test
    @UseDataProvider("provider_sampleDates")
    public void test_getDate(int year, int month, int day, ZoneOffset offset) {
        LocalDate t = LocalDate.of(year, month, day);
        assertEquals(t, OffsetDate.of(LocalDate.of(year, month, day), offset).getDate());
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo_date() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 29), OFFSET_PONE);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b due to date
        assertEquals(true, a.compareTo(b) < 0);
        assertEquals(true, b.compareTo(a) > 0);
        assertEquals(true, a.compareTo(a) == 0);
        assertEquals(true, b.compareTo(b) == 0);
        assertEquals(true, a.atTime(LocalTime.MIDNIGHT).toInstant().compareTo(b.atTime(LocalTime.MIDNIGHT).toInstant()) < 0);
    }

    @Test
    public void test_compareTo_offset() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b due to offset
        assertEquals(true, a.compareTo(b) < 0);
        assertEquals(true, b.compareTo(a) > 0);
        assertEquals(true, a.compareTo(a) == 0);
        assertEquals(true, b.compareTo(b) == 0);
        assertEquals(true, a.atTime(LocalTime.MIDNIGHT).toInstant().compareTo(b.atTime(LocalTime.MIDNIGHT).toInstant()) < 0);
    }

    @Test
    public void test_compareTo_both() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 29), OFFSET_PTWO);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b on instant scale
        assertEquals(true, a.compareTo(b) < 0);
        assertEquals(true, b.compareTo(a) > 0);
        assertEquals(true, a.compareTo(a) == 0);
        assertEquals(true, b.compareTo(b) == 0);
        assertEquals(true, a.atTime(LocalTime.MIDNIGHT).toInstant().compareTo(b.atTime(LocalTime.MIDNIGHT).toInstant()) < 0);
    }

    @Test
    public void test_compareTo_24hourDifference() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 29), ZoneOffset.ofHours(-12));
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), ZoneOffset.ofHours(12));  // a is before b despite being same time-line time
        assertEquals(true, a.compareTo(b) < 0);
        assertEquals(true, b.compareTo(a) > 0);
        assertEquals(true, a.compareTo(a) == 0);
        assertEquals(true, b.compareTo(b) == 0);
        assertEquals(true, a.atTime(LocalTime.MIDNIGHT).toInstant().compareTo(b.atTime(LocalTime.MIDNIGHT).toInstant()) == 0);
    }

    @Test(expected=NullPointerException.class)
    public void test_compareTo_null() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        a.compareTo(null);
    }

    @Test(expected=ClassCastException.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareToNonOffsetDate() {
       Comparable c = TEST_2007_07_15_PONE;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore() / isEqual()
    //-----------------------------------------------------------------------
    @Test
    public void test_isBeforeIsAfterIsEqual1() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 29), OFFSET_PONE);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b due to time
        assertEquals(true, a.isBefore(b));
        assertEquals(false, a.isEqual(b));
        assertEquals(false, a.isAfter(b));

        assertEquals(false, b.isBefore(a));
        assertEquals(false, b.isEqual(a));
        assertEquals(true, b.isAfter(a));

        assertEquals(false, a.isBefore(a));
        assertEquals(false, b.isBefore(b));

        assertEquals(true, a.isEqual(a));
        assertEquals(true, b.isEqual(b));

        assertEquals(false, a.isAfter(a));
        assertEquals(false, b.isAfter(b));
    }

    @Test
    public void test_isBeforeIsAfterIsEqual2() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PTWO);
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);  // a is before b due to offset
        assertEquals(true, a.isBefore(b));
        assertEquals(false, a.isEqual(b));
        assertEquals(false, a.isAfter(b));

        assertEquals(false, b.isBefore(a));
        assertEquals(false, b.isEqual(a));
        assertEquals(true, b.isAfter(a));

        assertEquals(false, a.isBefore(a));
        assertEquals(false, b.isBefore(b));

        assertEquals(true, a.isEqual(a));
        assertEquals(true, b.isEqual(b));

        assertEquals(false, a.isAfter(a));
        assertEquals(false, b.isAfter(b));
    }

    @Test
    public void test_isBeforeIsAfterIsEqual_instantComparison() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), ZoneOffset.ofHours(12));
        OffsetDate b = OffsetDate.of(LocalDate.of(2008, 6, 29), ZoneOffset.ofHours(-12));  // a is same instant as b
        assertEquals(false, a.isBefore(b));
        assertEquals(true, a.isEqual(b));
        assertEquals(false, a.isAfter(b));

        assertEquals(false, b.isBefore(a));
        assertEquals(true, b.isEqual(a));
        assertEquals(false, b.isAfter(a));

        assertEquals(false, a.isBefore(a));
        assertEquals(false, b.isBefore(b));

        assertEquals(true, a.isEqual(a));
        assertEquals(true, b.isEqual(b));

        assertEquals(false, a.isAfter(a));
        assertEquals(false, b.isAfter(b));
    }

    @Test(expected=NullPointerException.class)
    public void test_isBefore_null() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        a.isBefore(null);
    }

    @Test(expected=NullPointerException.class)
    public void test_isAfter_null() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        a.isAfter(null);
    }

    @Test(expected=NullPointerException.class)
    public void test_isEqual_null() {
        OffsetDate a = OffsetDate.of(LocalDate.of(2008, 6, 30), OFFSET_PONE);
        a.isEqual(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test
    @UseDataProvider("provider_sampleDates")
    public void test_equals_true(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), offset);
        OffsetDate b = OffsetDate.of(LocalDate.of(y, m, d), offset);
        assertEquals(true, a.equals(b));
        assertEquals(true, a.hashCode() == b.hashCode());
    }
    @Test
    @UseDataProvider("provider_sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), offset);
        OffsetDate b = OffsetDate.of(LocalDate.of(y + 1, m, d), offset);
        assertEquals(false, a.equals(b));
    }

    @Test
    @UseDataProvider("provider_sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), offset);
        OffsetDate b = OffsetDate.of(LocalDate.of(y, m + 1, d), offset);
        assertEquals(false, a.equals(b));
    }

    @Test
    @UseDataProvider("provider_sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d, ZoneOffset offset) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), offset);
        OffsetDate b = OffsetDate.of(LocalDate.of(y, m, d + 1), offset);
        assertEquals(false, a.equals(b));
    }

    @Test
    @UseDataProvider("provider_sampleDates")
    public void test_equals_false_offset_differs(int y, int m, int d, ZoneOffset ignored) {
        OffsetDate a = OffsetDate.of(LocalDate.of(y, m, d), OFFSET_PONE);
        OffsetDate b = OffsetDate.of(LocalDate.of(y, m, d), OFFSET_PTWO);
        assertEquals(false, a.equals(b));
    }

    @Test
    public void test_equals_itself_true() {
        assertEquals(true, TEST_2007_07_15_PONE.equals(TEST_2007_07_15_PONE));
    }

    @Test
    public void test_equals_string_false() {
        assertEquals(false, TEST_2007_07_15_PONE.equals("2007-07-15"));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, "Z", "2008-07-05Z"},
            {2008, 7, 5, "+00", "2008-07-05Z"},
            {2008, 7, 5, "+0000", "2008-07-05Z"},
            {2008, 7, 5, "+00:00", "2008-07-05Z"},
            {2008, 7, 5, "+000000", "2008-07-05Z"},
            {2008, 7, 5, "+00:00:00", "2008-07-05Z"},
            {2008, 7, 5, "-00", "2008-07-05Z"},
            {2008, 7, 5, "-0000", "2008-07-05Z"},
            {2008, 7, 5, "-00:00", "2008-07-05Z"},
            {2008, 7, 5, "-000000", "2008-07-05Z"},
            {2008, 7, 5, "-00:00:00", "2008-07-05Z"},
            {2008, 7, 5, "+01", "2008-07-05+01:00"},
            {2008, 7, 5, "+0100", "2008-07-05+01:00"},
            {2008, 7, 5, "+01:00", "2008-07-05+01:00"},
            {2008, 7, 5, "+010000", "2008-07-05+01:00"},
            {2008, 7, 5, "+01:00:00", "2008-07-05+01:00"},
            {2008, 7, 5, "+0130", "2008-07-05+01:30"},
            {2008, 7, 5, "+01:30", "2008-07-05+01:30"},
            {2008, 7, 5, "+013000", "2008-07-05+01:30"},
            {2008, 7, 5, "+01:30:00", "2008-07-05+01:30"},
            {2008, 7, 5, "+013040", "2008-07-05+01:30:40"},
            {2008, 7, 5, "+01:30:40", "2008-07-05+01:30:40"},
        };
    }

    @Test
    @UseDataProvider("provider_sampleToString")
    public void test_toString(int y, int m, int d, String offsetId, String expected) {
        OffsetDate t = OffsetDate.of(LocalDate.of(y, m, d), ZoneOffset.of(offsetId));
        String str = t.toString();
        assertEquals(expected, str);
    }

    //-----------------------------------------------------------------------
    // toString(DateTimeFormatter)
    //-----------------------------------------------------------------------
    @Test
    public void test_toString_formatter() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("y M d");
        String t = OffsetDate.of(LocalDate.of(2010, 12, 3), OFFSET_PONE).toString(f);
        assertEquals("2010 12 3", t);
    }

    @Test(expected=NullPointerException.class)
    public void test_toString_formatter_null() {
        OffsetDate.of(LocalDate.of(2010, 12, 3), OFFSET_PONE).toString(null);
    }

}
