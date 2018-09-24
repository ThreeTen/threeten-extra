package org.threeten.extra;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.DayTime;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;

import static org.junit.Assert.*;

public class DayTimeTest {

    private static final DayOfWeek DAY_OF_WEEK = DayOfWeek.WEDNESDAY;

    private static final int HOUR = 13;
    private static final int MINUTE = 10;
    private static final int SECOND = 22;
    private static final LocalTime LOCAL_TIME = LocalTime.of(HOUR, MINUTE, SECOND);

    private DayTime dayTime;

    @Before
    public void setUp() {
        dayTime = DayTime.of(DAY_OF_WEEK, LOCAL_TIME);
    }

    @Test
    public void testGetDayOfWeek() {
        assertEquals(DAY_OF_WEEK, dayTime.getDayOfWeek());
    }

    @Test
    public void testGetHour() {
        assertEquals(LOCAL_TIME.getHour(), dayTime.getHour());
    }

    @Test
    public void testGetMinute() {
        assertEquals(LOCAL_TIME.getMinute(), dayTime.getMinute());
    }

    @Test
    public void testGetSecond() {
        assertEquals(LOCAL_TIME.getSecond(), dayTime.getSecond());
    }

    @Test
    public void testGetNano() {
        assertEquals(LOCAL_TIME.getNano(), dayTime.getNano());
    }

    @Test
    public void testFrom() {
        final DayTime dayTime = DayTime.from(LocalDateTime.parse("2017-11-17T10:15:30"));

        assertEquals(DayOfWeek.FRIDAY, dayTime.getDayOfWeek());
        assertEquals(10, dayTime.getHour());
        assertEquals(15, dayTime.getMinute());
        assertEquals(30, dayTime.getSecond());
    }

    @Test
    public void testWith() {
        assertEquals(DayTime.of(DAY_OF_WEEK.plus(1), LOCAL_TIME),
                dayTime.with(ChronoField.DAY_OF_WEEK, DAY_OF_WEEK.plus(1).getValue()));

        assertEquals(DayTime.of(DAY_OF_WEEK, LocalTime.of(HOUR + 1, MINUTE, SECOND)),
                dayTime.with(ChronoField.HOUR_OF_DAY, HOUR + 1));

        assertEquals(DayTime.of(DAY_OF_WEEK, LocalTime.of(HOUR, MINUTE + 1, SECOND)),
                dayTime.with(ChronoField.MINUTE_OF_HOUR, MINUTE + 1));

        assertEquals(DayTime.of(DAY_OF_WEEK, LocalTime.of(HOUR, MINUTE, SECOND + 1)),
                dayTime.with(ChronoField.SECOND_OF_MINUTE, SECOND + 1));
    }
    
    @Test
    public void testPlusTemporalUnit() {
        final DayTime earlyTuesdayMorning = DayTime.of(DayOfWeek.TUESDAY, LocalTime.of(1, 0));
        final DayTime lateMondayEvening = DayTime.of(DayOfWeek.MONDAY, LocalTime.of(23, 0));

        assertEquals(DayOfWeek.WEDNESDAY, earlyTuesdayMorning.plus(1, ChronoUnit.DAYS).getDayOfWeek());
        assertEquals(1, earlyTuesdayMorning.plus(1, ChronoUnit.DAYS).getHour());
        assertEquals(0, earlyTuesdayMorning.plus(1, ChronoUnit.DAYS).getMinute());

        assertEquals(DayOfWeek.TUESDAY, earlyTuesdayMorning.plus(77, ChronoUnit.MINUTES).getDayOfWeek());
        assertEquals(2, earlyTuesdayMorning.plus(77, ChronoUnit.MINUTES).getHour());
        assertEquals(17, earlyTuesdayMorning.plus(77, ChronoUnit.MINUTES).getMinute());

        assertEquals(DayOfWeek.TUESDAY, lateMondayEvening.plus(97, ChronoUnit.MINUTES).getDayOfWeek());
    }

    @Test
    public void testMinus() {
        final DayTime earlyTuesdayMorning = DayTime.of(DayOfWeek.TUESDAY, LocalTime.of(1, 0));

        assertEquals(DayOfWeek.MONDAY, earlyTuesdayMorning.minus(1, ChronoUnit.DAYS).getDayOfWeek());
        assertEquals(1, earlyTuesdayMorning.minus(1, ChronoUnit.DAYS).getHour());
        assertEquals(0, earlyTuesdayMorning.minus(1, ChronoUnit.DAYS).getMinute());

        assertEquals(DayOfWeek.MONDAY, earlyTuesdayMorning.minus(77, ChronoUnit.MINUTES).getDayOfWeek());
        assertEquals(23, earlyTuesdayMorning.minus(77, ChronoUnit.MINUTES).getHour());
        assertEquals(43, earlyTuesdayMorning.minus(77, ChronoUnit.MINUTES).getMinute());
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void testPlusUnsupportedUnit() {
        dayTime.plus(1, ChronoUnit.DECADES);
    }

    @Test
    public void testUntil() {
        final DayTime wednesdayAtNoon = DayTime.of(DayOfWeek.WEDNESDAY, LocalTime.NOON);
        final DayTime fridayAtNoon = DayTime.of(DayOfWeek.FRIDAY, LocalTime.NOON);
        final DayTime fridayMorning = DayTime.of(DayOfWeek.FRIDAY, LocalTime.of(9, 0));

        final DayTime lateMondayNight = DayTime.of(DayOfWeek.MONDAY, LocalTime.of(23, 0));
        final DayTime earlyTuesdayMorning = DayTime.of(DayOfWeek.TUESDAY, LocalTime.of(1, 0));

        assertEquals(0, wednesdayAtNoon.until(wednesdayAtNoon, ChronoUnit.DAYS));
        assertEquals(0, wednesdayAtNoon.until(wednesdayAtNoon, ChronoUnit.HOURS));

        assertEquals(2, wednesdayAtNoon.until(fridayAtNoon, ChronoUnit.DAYS));
        assertEquals(1, wednesdayAtNoon.until(fridayMorning, ChronoUnit.DAYS));
        assertEquals(5, fridayAtNoon.until(wednesdayAtNoon, ChronoUnit.DAYS));
        assertEquals(5, fridayMorning.until(wednesdayAtNoon, ChronoUnit.DAYS));
        assertEquals(0, lateMondayNight.until(earlyTuesdayMorning, ChronoUnit.DAYS));
        assertEquals(6, earlyTuesdayMorning.until(lateMondayNight, ChronoUnit.DAYS));

        assertEquals(48, wednesdayAtNoon.until(fridayAtNoon, ChronoUnit.HOURS));
        assertEquals(120, fridayAtNoon.until(wednesdayAtNoon, ChronoUnit.HOURS));
        assertEquals(3, fridayMorning.until(fridayAtNoon, ChronoUnit.HOURS));
        assertEquals(165, fridayAtNoon.until(fridayMorning, ChronoUnit.HOURS));
        assertEquals(2, lateMondayNight.until(earlyTuesdayMorning, ChronoUnit.HOURS));
        assertEquals(166, earlyTuesdayMorning.until(lateMondayNight, ChronoUnit.HOURS));
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void testUntilUnsupportedUnit() {
        dayTime.until(dayTime, ChronoUnit.DECADES);
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void testWithUnsupportedField() {
        dayTime.with(ChronoField.ALIGNED_WEEK_OF_MONTH, 1);
    }

    @Test
    public void testIsSupported() {
        for (final ChronoField chronoField : ChronoField.values()) {
            final boolean expectSupport = (chronoField == ChronoField.DAY_OF_WEEK || chronoField.isTimeBased());
            assertEquals(expectSupport, dayTime.isSupported(chronoField));
        }
    }

    @Test
    public void testGetLong() {
        assertEquals(DAY_OF_WEEK.getValue(), dayTime.getLong(ChronoField.DAY_OF_WEEK));
        assertEquals(LOCAL_TIME.toNanoOfDay(), dayTime.getLong(ChronoField.NANO_OF_DAY));
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void testGetLongUnsupportedField() {
        dayTime.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
    }

    @Test
    public void testAdjustInto() {
        final LocalDateTime localDateTime = LocalDateTime.of(2017, 11, 14, 9, 14);
        final LocalDateTime adjustedLocalDateTime = (LocalDateTime) dayTime.adjustInto(localDateTime);

        final ChronoField[] chronoFields = new ChronoField[] {
                ChronoField.DAY_OF_WEEK,
                ChronoField.HOUR_OF_DAY,
                ChronoField.MINUTE_OF_HOUR
        };

        for (final ChronoField chronoField : chronoFields) {
            assertEquals(dayTime.get(chronoField), adjustedLocalDateTime.get(chronoField));
        }
    }

    @Test
    public void testCompareTo() {
        final DayTime tuesdayAtNoon = DayTime.of(DayOfWeek.TUESDAY, LocalTime.NOON);
        final DayTime tuesdayAfternoon = DayTime.of(DayOfWeek.TUESDAY, LocalTime.of(15, 0));

        assertEquals(0, tuesdayAtNoon.compareTo(tuesdayAtNoon));
        assertTrue(tuesdayAtNoon.compareTo(tuesdayAfternoon) < 0);
        assertTrue(tuesdayAfternoon.compareTo(tuesdayAtNoon) > 0);

        final DayTime wednesdayAtNoon = DayTime.of(DayOfWeek.WEDNESDAY, LocalTime.NOON);

        assertTrue(tuesdayAtNoon.compareTo(wednesdayAtNoon) < 0);
        assertTrue(wednesdayAtNoon.compareTo(tuesdayAtNoon) > 0);
    }

    @Test
    public void testEquals() {
        final DayTime tuesdayAtNoon = DayTime.of(DayOfWeek.TUESDAY, LocalTime.NOON);
        final DayTime tuesdayAfternoon = DayTime.of(DayOfWeek.TUESDAY, LocalTime.of(15, 0));

        assertEquals(tuesdayAtNoon, tuesdayAtNoon);
        assertFalse((tuesdayAtNoon.equals(tuesdayAfternoon)));

        final DayTime wednesdayAtNoon = DayTime.of(DayOfWeek.WEDNESDAY, LocalTime.NOON);

        assertNotEquals(tuesdayAtNoon, wednesdayAtNoon);

        assertFalse(tuesdayAtNoon.equals(4));
        assertFalse(tuesdayAtNoon.equals(null));
    }
}
