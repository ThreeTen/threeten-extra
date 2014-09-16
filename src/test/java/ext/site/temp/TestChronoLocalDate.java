/* 
 * Some of this code is under copyright from other authors.
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
import java.time.chrono.ChronoLocalDate;
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
 * @author Clockwork-Muse
 */
@Test
@SuppressWarnings({ "static-method", "javadoc", "checkstyle:magicnumber", "checkstyle:javadocmethod", "checkstyle:javadocvariable",
        "checkstyle:designforextension",
        "checkstyle:multiplestringliterals", })
public class TestChronoLocalDate {
    @Test(dataProvider = "calendars")
    public void testBadDateTimeFieldChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDate date = chrono.date(refDate);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDate date2 = chrono2.date(refDate);
            final TemporalField adjuster = new FixedDateTimeField(date2);
            if (chrono != chrono2) {
                try {
                    date.with(adjuster, 1);
                    Assert.fail("DateTimeField doSet should have thrown a ClassCastException" + date.getClass()
                            + ", can not be cast to " + date2.getClass());
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception was caught");
                }
            } else {
                // Same chronology,
                final ChronoLocalDate result = date.with(adjuster, 1);
                assertEquals(result, date2, "DateTimeField doSet failed to replace date");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadMinusAdjusterChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDate date = chrono.date(refDate);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDate date2 = chrono2.date(refDate);
            final TemporalAmount adjuster = new FixedAdjuster(date2);
            if (chrono != chrono2) {
                try {
                    date.minus(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException");
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception was caught");
                }
            } else {
                // Same chronology,
                final ChronoLocalDate result = date.minus(adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadMinusPeriodUnitChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDate date = chrono.date(refDate);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDate date2 = chrono2.date(refDate);
            final TemporalUnit adjuster = new FixedPeriodUnit(date2);
            if (chrono != chrono2) {
                try {
                    date.minus(1, adjuster);
                    Assert.fail("PeriodUnit.doAdd minus should have thrown a ClassCastException" + date.getClass()
                            + ", can not be cast to " + date2.getClass());
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception was caught");
                }
            } else {
                // Same chronology,
                final ChronoLocalDate result = date.minus(1, adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadPlusAdjusterChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDate date = chrono.date(refDate);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDate date2 = chrono2.date(refDate);
            final TemporalAmount adjuster = new FixedAdjuster(date2);
            if (chrono != chrono2) {
                try {
                    date.plus(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException");
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception was caught");
                }
            } else {
                // Same chronology,
                final ChronoLocalDate result = date.plus(adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadPlusPeriodUnitChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDate date = chrono.date(refDate);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDate date2 = chrono2.date(refDate);
            final TemporalUnit adjuster = new FixedPeriodUnit(date2);
            if (chrono != chrono2) {
                try {
                    date.plus(1, adjuster);
                    Assert.fail("PeriodUnit.doAdd plus should have thrown a ClassCastException" + date.getClass()
                            + ", can not be cast to " + date2.getClass());
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception was caught");
                }
            } else {
                // Same chronology,
                final ChronoLocalDate result = date.plus(1, adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(dataProvider = "calendars")
    public void testBadWithAdjusterChrono(final Chronology chrono) {
        final LocalDate refDate = LocalDate.of(1900, 1, 1);
        final ChronoLocalDate date = chrono.date(refDate);
        for (final Chronology[] clist : dataOfCalendars()) {
            final Chronology chrono2 = clist[0];
            final ChronoLocalDate date2 = chrono2.date(refDate);
            final TemporalAdjuster adjuster = new FixedAdjuster(date2);
            if (chrono != chrono2) {
                try {
                    date.with(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException");
                } catch (final ClassCastException cce) {
                    // Expected exception; not an error
                    Assert.assertTrue(true, "Exception was caught");
                }
            } else {
                // Same chronology,
                final ChronoLocalDate result = date.with(adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    // -----------------------------------------------------------------------
    // Test Serialization of Calendars
    // -----------------------------------------------------------------------
    @Test(dataProvider = "calendars")
    public void testChronoSerialization(final Chronology chrono) throws Exception {
        final LocalDate ref = LocalDate.of(1900, 1, 5);
        final ChronoLocalDate orginal = chrono.date(ref);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ObjectInputStream in = new ObjectInputStream(bais);
        final ChronoLocalDate ser = (ChronoLocalDate) in.readObject();
        assertEquals(ser, orginal, "deserialized date is wrong");
    }

    // -----------------------------------------------------------------------
    // isBefore, isAfter, isEqual, DATE_COMPARATOR
    // -----------------------------------------------------------------------
    @Test(dataProvider = "calendars")
    @SuppressWarnings("checkstyle:nestedfordepth")
    public void testDateComparisons(final Chronology chrono) {
        final List<ChronoLocalDate> dates = new ArrayList<ChronoLocalDate>();

        final ChronoLocalDate date = chrono.date(LocalDate.of(1900, 1, 1));

        // Insert dates in order, no duplicates
        dates.add(date.minus(1000, ChronoUnit.YEARS));
        dates.add(date.minus(100, ChronoUnit.YEARS));

        dates.add(date.minus(10, ChronoUnit.YEARS));
        dates.add(date.minus(1, ChronoUnit.YEARS));
        dates.add(date.minus(1, ChronoUnit.MONTHS));
        dates.add(date.minus(1, ChronoUnit.WEEKS));
        dates.add(date.minus(1, ChronoUnit.DAYS));
        dates.add(date);
        dates.add(date.plus(1, ChronoUnit.DAYS));
        dates.add(date.plus(1, ChronoUnit.WEEKS));
        dates.add(date.plus(1, ChronoUnit.MONTHS));
        dates.add(date.plus(1, ChronoUnit.YEARS));
        dates.add(date.plus(10, ChronoUnit.YEARS));
        dates.add(date.plus(100, ChronoUnit.YEARS));
        dates.add(date.plus(1000, ChronoUnit.YEARS));

        // Check these dates against the corresponding dates for every calendar
        for (final Chronology[] clist : dataOfCalendars()) {
            final List<ChronoLocalDate> otherDates = new ArrayList<ChronoLocalDate>();
            final Chronology chrono2 = clist[0];
            for (final ChronoLocalDate d : dates) {
                otherDates.add(chrono2.date(d));
            }

            // Now compare the sequence of original dates with the sequence of converted dates
            for (int i = 0; i < dates.size(); i++) {
                final ChronoLocalDate a = dates.get(i);
                for (int j = 0; j < otherDates.size(); j++) {
                    final ChronoLocalDate b = otherDates.get(j);
                    final int cmp = ChronoLocalDate.timeLineOrder().compare(a, b);
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
    @SuppressWarnings({ "checkstyle:hiddenfield" })
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
