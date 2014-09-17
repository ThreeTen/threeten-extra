package org.threeten.extra.chrono;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;

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
// TODO: Correct data for calendar system.
public class TestPaxDate {

    @Test
    public void testNowClockOffset() {
        final Clock testCLock = Clock.fixed(Instant.parse("2013-02-05T20:00:00"), ZoneId.ofOffset("TYO", ZoneOffset.ofHours(8)));
        Assert.assertEquals(PaxDate.now(testCLock), PaxDate.of(0, 0, 0), "Calendar Date from offset instance not same");
    }

    @Test
    public void testNowClockUTC() {
        final Clock testCLock = Clock.fixed(Instant.parse("2013-02-05"), ZoneId.of("UTC"));
        Assert.assertEquals(PaxDate.now(testCLock), PaxDate.of(0, 0, 0), "Calendar Date from instance not same");
    }

    @Test(dataProvider = "epochPaxDateValues")
    public void testOfEpochDay(final Object[] dataValues) {
        final PaxDate constructed = PaxDate.ofEpochDay((Integer) dataValues[0]);
        Assert.assertNotNull(constructed, "Date not returned");
        Assert.assertEquals(constructed, dataValues[1], "Year not same as expected");
    }

    @Test(dataProvider = "ofYearDayValues")
    public void testOfYearDay(final Object[] dataValues) {
        final PaxDate constructed = PaxDate.ofYearDay((Integer) dataValues[0], (Integer) dataValues[1]);
        Assert.assertNotNull(constructed, "Date not returned");
        Assert.assertEquals(constructed, dataValues[2], "Year not same as expected");
    }

    @Test(dataProvider = "ofYearDayValuesInvalid", expectedExceptions = { DateTimeException.class })
    public void testOfYearDayInvalid(final Integer[] dataValues) {
        final int year = dataValues[0];
        final int dayOfYear = dataValues[2];
        PaxDate.ofYearDay(year, dayOfYear);
    }

    @Test(dataProvider = "ofYearMonthDayValues")
    public void testOfYearMonthDay(final Integer[] dataValues) {
        final int expectedYear = dataValues[0];
        final int expectedMonth = dataValues[1];
        final int expectedDay = dataValues[2];
        final PaxDate constructed = PaxDate.of(expectedYear, expectedMonth, expectedDay);
        Assert.assertNotNull(constructed, "Date not returned");
        Assert.assertEquals(constructed.getYear(), expectedYear, "Year not same as expected");
        Assert.assertEquals(constructed.getMonthValue(), expectedMonth, "Month not same as expected");
        Assert.assertEquals(constructed.getDayOfMonth(), expectedDay, "Day not same as expected");
    }

    @Test(dataProvider = "ofYearMonthDayValuesInvalid", expectedExceptions = { DateTimeException.class })
    public void testOfYearMonthDayInvalid(final Integer[] dataValues) {
        final int expectedYear = dataValues[0];
        final int expectedMonth = dataValues[1];
        final int expectedDay = dataValues[2];
        PaxDate.of(expectedYear, expectedMonth, expectedDay);
    }

    @DataProvider(name = "epochPaxDateValues")
    @SuppressWarnings("checkstyle:indentation")
    Object[][] epochPaxDateValues() {
        // TODO: Fill in with correct epoch values.
        return new Object[][] {
                { 0, PaxDate.of(2014, 1, 1) },
                { 0, PaxDate.of(2013, 5, 9) },
                { 0, PaxDate.of(-1, 6, 4) },
                { 0, PaxDate.of(2012, 13, 7) },
                { 0, PaxDate.of(2012, 14, 1) },
                { 0, PaxDate.of(2011, 13, 28) },
                { 0, PaxDate.of(-6, 13, 5) },
                { 0, PaxDate.of(-6, 14, 8) },
        };
    }

    @DataProvider(name = "ofYearDayValues")
    @SuppressWarnings("checkstyle:indentation")
    Object[][] ofYearDayValues() {
        return new Object[][] {
                { 2014, 1, PaxDate.of(2014, 1, 1) },
                { 2013, 121, PaxDate.of(2013, 5, 9) },
                { -1, 144, PaxDate.of(-1, 6, 4) },
                { 2012, 343, PaxDate.of(2012, 13, 7) },
                { 2012, 344, PaxDate.of(2012, 14, 1) },
                { 2011, 364, PaxDate.of(2011, 13, 28) },
                { -6, 341, PaxDate.of(-6, 13, 5) },
                { -6, 350, PaxDate.of(-6, 14, 8) },
        };
    }

    @DataProvider(name = "ofYearDayValuesInvalid")
    @SuppressWarnings("checkstyle:indentation")
    Integer[][] ofYearDayValuesInvalid() {
        return new Integer[][] {
                { 2014, -1 },
                { 2014, 0 },
                { Year.MAX_VALUE + 1, 1 },
                { 2012, 372 },
                { 2011, 365 },
        };
    }

    @DataProvider(name = "ofYearMonthDayValues")
    @SuppressWarnings("checkstyle:indentation")
    Integer[][] ofYearMonthDayValues() {
        return new Integer[][] {
                { 2014, 1, 1 },
                { 2013, 5, 9 },
                { -1, 6, 4 },
                { 2012, 13, 7 },
                { 2012, 14, 1 },
                { 2011, 13, 28 },
                { -6, 13, 5 },
                { -6, 14, 8 },
        };
    }

    @DataProvider(name = "ofYearMonthDayValuesInvalid")
    @SuppressWarnings("checkstyle:indentation")
    Integer[][] ofYearMonthDayValuesInvalid() {
        return new Integer[][] {
                { 2014, -1, 1 },
                { 2014, 1, -1 },
                { 2014, 0, 1 },
                { 2014, 1, 0 },
                { 2014, 0, 0 },
                { 2014, -1, -1 },
                { Year.MAX_VALUE + 1, 1, 1 },
                { 2012, 13, 8 },
                { 2011, 14, 1 },
                { 2008, 5, 29 },
        };
    }

}
