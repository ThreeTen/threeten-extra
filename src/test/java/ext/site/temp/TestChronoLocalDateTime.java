/*
 *  Some of this code is under copyright from other authors.
 * 
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
package ext.site.temp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.Chronology;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test assertions that must be true for all built-in chronologies.
 */
@Test
@SuppressWarnings({ "rawtypes", "static-method", "javadoc", "checkstyle:magicnumber", "checkstyle:javadocmethod", "checkstyle:javadocvariable",
        "checkstyle:designforextension",
        "checkstyle:multiplestringliterals", })
public class TestChronoLocalDateTime {
    @Test(dataProvider = "calendars")
    public void testBadDateTimeFieldChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            final TemporalField adjuster = new FixedDateTimeField(cdt2);
            if (chrono != chrono2) {
                try {
                    cdt.with(adjuster, 1);
                    Assert.fail("DateTimeField doSet should have thrown a ClassCastException" + cdt.getClass()
                            + ", can not be cast to " + cdt2.getClass());
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception expected");
                }
            } else {
                // Same chronology,
                final ChronoLocalDateTime<?> result = cdt.with(adjuster, 1);
                assertEquals(result, cdt2, "DateTimeField doSet failed to replace date");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadMinusAdjusterChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            final TemporalAmount adjuster = new FixedAdjuster(cdt2);
            if (chrono != chrono2) {
                try {
                    cdt.minus(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException, "
                            + "required: " + cdt + ", supplied: " + cdt2);
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception expected");
                }
            } else {
                // Same chronology,
                final ChronoLocalDateTime<?> result = cdt.minus(adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadMinusPeriodUnitChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            final TemporalUnit adjuster = new FixedPeriodUnit(cdt2);
            if (chrono != chrono2) {
                try {
                    cdt.minus(1, adjuster);
                    Assert.fail("PeriodUnit.doAdd minus should have thrown a ClassCastException" + cdt.getClass()
                            + ", can not be cast to " + cdt2.getClass());
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception expected");
                }
            } else {
                // Same chronology,
                final ChronoLocalDateTime<?> result = cdt.minus(1, adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadPlusAdjusterChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            final TemporalAmount adjuster = new FixedAdjuster(cdt2);
            if (chrono != chrono2) {
                try {
                    cdt.plus(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException, "
                            + "required: " + cdt + ", supplied: " + cdt2);
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception expected");
                }
            } else {
                // Same chronology,
                final ChronoLocalDateTime<?> result = cdt.plus(adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date time");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadPlusPeriodUnitChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            final TemporalUnit adjuster = new FixedPeriodUnit(cdt2);
            if (chrono != chrono2) {
                try {
                    cdt.plus(1, adjuster);
                    Assert.fail("PeriodUnit.doAdd plus should have thrown a ClassCastException" + cdt
                            + ", can not be cast to " + cdt2);
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception expected");
                }
            } else {
                // Same chronology,
                final ChronoLocalDateTime<?> result = cdt.plus(1, adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadWithAdjusterChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            final TemporalAdjuster adjuster = new FixedAdjuster(cdt2);
            if (chrono != chrono2) {
                try {
                    cdt.with(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException, "
                            + "required: " + cdt + ", supplied: " + cdt2);
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception expected");
                }
            } else {
                // Same chronology,
                final ChronoLocalDateTime<?> result = cdt.with(adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date");
            }
        }
    }

    // -----------------------------------------------------------------------
    // Test Serialization of ISO via chrono API
    // -----------------------------------------------------------------------
    @Test(dataProvider = "calendars")
    public void testChronoLocalDateTimeSerialization(final Chronology chrono) throws Exception {
        final LocalDateTime ref = LocalDate.of(2000, 1, 5).atTime(12, 1, 2, 3);
        final ChronoLocalDateTime<?> orginal = chrono.date(ref).atTime(ref.toLocalTime());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ObjectInputStream in = new ObjectInputStream(bais);
        final ChronoLocalDateTime<?> ser = (ChronoLocalDateTime<?>) in.readObject();
        assertEquals(ser, orginal, "deserialized date is wrong");
    }

    // -----------------------------------------------------------------------
    // isBefore, isAfter, isEqual
    // -----------------------------------------------------------------------
    @Test(dataProvider = "calendars")
    @SuppressWarnings("checkstyle:nestedfordepth")
    public void testDatetimeComparisons(final Chronology chrono) {
        final List<ChronoLocalDateTime<?>> dates = new ArrayList<ChronoLocalDateTime<?>>();

        final ChronoLocalDateTime<?> date = chrono.date(LocalDate.of(1900, 1, 1)).atTime(LocalTime.MIN);

        // Insert dates in order, no duplicates
        dates.add(date.minus(1, ChronoUnit.YEARS));
        dates.add(date.minus(1, ChronoUnit.MONTHS));
        dates.add(date.minus(1, ChronoUnit.WEEKS));
        dates.add(date.minus(1, ChronoUnit.DAYS));
        dates.add(date.minus(1, ChronoUnit.HOURS));
        dates.add(date.minus(1, ChronoUnit.MINUTES));
        dates.add(date.minus(1, ChronoUnit.SECONDS));
        dates.add(date.minus(1, ChronoUnit.NANOS));
        dates.add(date);
        dates.add(date.plus(1, ChronoUnit.NANOS));
        dates.add(date.plus(1, ChronoUnit.SECONDS));
        dates.add(date.plus(1, ChronoUnit.MINUTES));
        dates.add(date.plus(1, ChronoUnit.HOURS));
        dates.add(date.plus(1, ChronoUnit.DAYS));
        dates.add(date.plus(1, ChronoUnit.WEEKS));
        dates.add(date.plus(1, ChronoUnit.MONTHS));
        dates.add(date.plus(1, ChronoUnit.YEARS));
        dates.add(date.plus(100, ChronoUnit.YEARS));

        // Check these dates against the corresponding dates for every calendar
        for (final Chronology[] clist : dataOfCalendars()) {
            final List<ChronoLocalDateTime<?>> otherDates = new ArrayList<ChronoLocalDateTime<?>>();
            final Chronology chrono2 = clist[0];
            for (final ChronoLocalDateTime<?> d : dates) {
                otherDates.add(chrono2.date(d).atTime(d.toLocalTime()));
            }

            // Now compare the sequence of original dates with the sequence of converted dates
            for (int i = 0; i < dates.size(); i++) {
                final ChronoLocalDateTime<?> a = dates.get(i);
                for (int j = 0; j < otherDates.size(); j++) {
                    final ChronoLocalDateTime<?> b = otherDates.get(j);
                    final int cmp = ChronoLocalDateTime.timeLineOrder().compare(a, b);
                    if (i < j) {
                        assertTrue(cmp < 0, a + " compare " + b);
                        assertEquals(a.isBefore(b), true, a + " isBefore " + b);
                        assertEquals(a.isAfter(b), false, a + " isAfter " + b);
                        assertEquals(a.isEqual(b), false, a + " isEqual " + b);
                    } else if (i > j) {
                        assertTrue(cmp > 0, a + " compare " + b);
                        assertEquals(a.isBefore(b), false, a + " isBefore " + b);
                        assertEquals(a.isAfter(b), true, a + " isAfter " + b);
                        assertEquals(a.isEqual(b), false, a + " isEqual " + b);
                    } else {
                        assertTrue(cmp == 0, a + " compare " + b);
                        assertEquals(a.isBefore(b), false, a + " isBefore " + b);
                        assertEquals(a.isAfter(b), false, a + " isAfter " + b);
                        assertEquals(a.isEqual(b), true, a + " isEqual " + b);
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // regular data factory for names and descriptions of available calendars
    // -----------------------------------------------------------------------
    @DataProvider(name = "calendars")
    Chronology[][] dataOfCalendars() {
        return new Chronology[][] { { PaxChronology.INSTANCE } };
    }

    /**
     * FixedAdjusted returns a fixed DateTime in all adjustments. Construct an adjuster with the DateTime that should be returned.
     */
    static class FixedAdjuster implements TemporalAdjuster, TemporalAmount {
        private final Temporal datetime;

        FixedAdjuster(final Temporal datetime) {
            this.datetime = datetime;
        }

        @Override
        public Temporal addTo(final Temporal ignore) {
            return datetime;
        }

        @Override
        public Temporal adjustInto(final Temporal ignore) {
            return datetime;
        }

        @Override
        public long get(final TemporalUnit unit) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<TemporalUnit> getUnits() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Temporal subtractFrom(final Temporal ignore) {
            return datetime;
        }
    }

    /**
     * FixedDateTimeField returns a fixed DateTime in all adjustments. Construct an FixedDateTimeField with the DateTime that should be returned from doSet.
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    static class FixedDateTimeField implements TemporalField {
        private final Temporal dateTime;

        FixedDateTimeField(final Temporal dateTime) {
            this.dateTime = dateTime;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R adjustInto(final R dateTime, final long newValue) {
            return (R) this.dateTime;
        }

        @Override
        public TemporalUnit getBaseUnit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getDisplayName(final Locale locale) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getFrom(final TemporalAccessor dateTime) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TemporalUnit getRangeUnit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isDateBased() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isSupportedBy(final TemporalAccessor dateTime) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isTimeBased() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ValueRange range() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ValueRange rangeRefinedBy(final TemporalAccessor dateTime) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TemporalAccessor resolve(final Map<TemporalField, Long> fieldValues,
                final TemporalAccessor partialTemporal, final ResolverStyle resolverStyle) {
            return null;
        }

        @Override
        public String toString() {
            return "FixedDateTimeField";
        }
    }

    /**
     * FixedPeriodUnit returns a fixed DateTime in all adjustments. Construct an FixedPeriodUnit with the DateTime that should be returned.
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    static class FixedPeriodUnit implements TemporalUnit {
        private final Temporal dateTime;

        FixedPeriodUnit(final Temporal dateTime) {
            this.dateTime = dateTime;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R addTo(final R dateTime, final long periodToAdd) {
            return (R) this.dateTime;
        }

        @Override
        public long between(final Temporal temporal1, final Temporal temporal2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Duration getDuration() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isDateBased() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isDurationEstimated() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isSupportedBy(final Temporal dateTime) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isTimeBased() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String toString() {
            return "FixedPeriodUnit";
        }
    }
}
