package org.threeten.extra.chrono;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.chrono.*;
import java.time.temporal.*;
import java.util.Arrays;
import java.util.List;

/**
 * The International Fixed calendar system.
 * <p>
 * This chronology defines the rules of the International Fixed calendar system.
 * It shares the leap year rule with the Gregorian calendar.
 * Dates are aligned such that {@code 0-13-28 (International Fixed)} is {@code 0000-12-30 (ISO)}.
 * <p>
 * This class is not proleptic. It implements only year starting from year 0 onwards.
 * <p>
 * This class implements a calendar where January 1st is the start of the year.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There is only one era, the current 'Common Era' (CE).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year zero.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the current era.
 * <li>month-of-year - There are 13 months in an International Fixed year, numbered from 1 to 13.
 * <li>day-of-month - There are 28 or 29 days in an International Fixed month, numbered from 1 to 28 / 29.
 *  Month 6 may have 29 days - for leap day.
 *  Month 13 has 29 days - for year day.
 *  All other months have 28 days.
 * <li>day-of-year - There are 365 days in a standard International Fixed year and 366 days in a leap year.
 *  The days are numbered from 1 to 365 / 366 accordingly.
 * <li>leap-year - Leap years occur every 4 years, but skips 3 out of four centuries, i.e. when the century is not divisible by 400.
 *  This is the same rule in use for the Gregorian calendar.
 * </ul>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 */
public class InternationalFixedChronology extends AbstractChronology implements Serializable {
    /**
     * Singleton instance for the International fixed chronology.
     */
    public static final InternationalFixedChronology INSTANCE = new InternationalFixedChronology ();

    /**
     * Serialization version.
     */
    //private static final long serialVersionUID = -7021464635577802085L;

    /**
     * Standard 7-day week.
     */
    private static final int DAYS_IN_WEEK = 7;

    /**
     * In all months, there are 4 complete weeks.
     */
    static final int WEEKS_IN_MONTH = 4;

    /**
     * There are 13 months in a (non-leap) year.
     */
    static final int MONTHS_IN_YEAR = 13;

    /**
     * There are 4 weeks of 7 days, or 28 total days in a month.
     */
    static final int DAYS_IN_MONTH = WEEKS_IN_MONTH * DAYS_IN_WEEK;

    /**
     * There are 13 months of 28 days, or 364 days in a (non-leap) year.
     */
    static final int DAYS_IN_YEAR = MONTHS_IN_YEAR * DAYS_IN_MONTH;

    /**
     * There are 52 weeks in a (non-leap) year.
     */
    static final int WEEKS_IN_YEAR = DAYS_IN_YEAR / DAYS_IN_WEEK;

    /**
     * Range of year.
     */
    private static final ValueRange YEAR_RANGE = ValueRange.of (0, 999_999);

    /**
     * Range of proleptic month.
     */
    private static final ValueRange PROLEPTIC_MONTH_RANGE = ValueRange.of (0, 1_000_000 * 13L - 1);

    /**
     * Range of day of month.
     */
    static final ValueRange DAY_OF_MONTH_RANGE = ValueRange.of (1, DAYS_IN_MONTH, DAYS_IN_MONTH + 1);

    /**
     * Range of day of year.
     */
    static final ValueRange DAY_OF_YEAR_RANGE = ValueRange.of (1, DAYS_IN_YEAR, DAYS_IN_YEAR + 1);

    /**
     * Range of month of year.
     */
    static final ValueRange MONTH_OF_YEAR_RANGE = ValueRange.of (1, MONTHS_IN_YEAR);

    /**
     * The number of days in a 400 year cycle.
     */
    private static final int DAYS_PER_CYCLE = 146097;

    /**
     * The number of days from year zero to year 1970.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     */
    static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);

    /**
     * Private constructor, that is public to satisfy the {@code ServiceLoader}.
     * Use the singleton {@link #INSTANCE} instead.
     */
    public InternationalFixedChronology () {
    }

    /**
     * Creates the chronology era object from the numeric value.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The era in use at 1970-01-01 must have the value 1.
     * Later eras must have sequentially higher values.
     * Earlier eras must have sequentially lower values.
     * Each chronology must refer to an enum or similar singleton to provide the era values.
     * <p>
     * This method returns the singleton era of the correct type for the specified era value.
     *
     * @param eraValue the era value
     * @return the calendar system era, not null
     * @throws DateTimeException if unable to create the era
     */
    @Override
    public Era eraOf (final int eraValue) {
        return InternationalFixedEra.of (eraValue);
    }

    /**
     * Gets the list of eras for the chronology.
     * <p>
     * Most calendar systems have an era, within which the year has meaning.
     * If the calendar system does not support the concept of eras, an empty
     * list must be returned.
     *
     * @return the list of eras for the chronology, may be immutable, not null
     */
    @Override
    public List<Era> eras () {
        return Arrays.<Era> asList (InternationalFixedEra.values ());
    }

    //-----------------------------------------------------------------------

    /**
     * Gets the ID of the chronology - 'Ifc'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     *
     * @return the chronology ID - 'Ifc'
     * @see #getCalendarType()
     */
    @Override
    public String getId () {
        return "Ifc";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'ifc'.
     * <p>
     * The <em>Unicode Locale Data Markup Language (LDML)</em> specification
     * does not define an identifier for the Pax calendar, but were it to
     * do so, 'pax' is highly likely to be chosen.
     *
     * @return the calendar system type - 'ifc'
     * @see #getId()
     */
    @Override
    public String getCalendarType () {
        return "ifc";
    }

    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A leap-year is a year of a longer length than normal.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <ul>
     * <li>a leap-year must imply a year-length longer than a non leap-year.
     * <li>a chronology that does not support the concept of a year must return false.
     * </ul>
     *
     * @param year the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear (final long year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }

    /**
     * Calculates the proleptic-year given the era and year-of-era.
     * <p>
     * The International Fixed calendar only knows common era years, thus negative years are invalid.
     * <p>
     * If the chronology makes active use of eras, such as {@code JapaneseChronology}
     * then the year-of-era will be validated against the era.
     * For other chronologies, validation is optional.
     *
     * @param era       the era of the correct type for the chronology, not null
     * @param yearOfEra the chronology year-of-era
     * @return the proleptic-year
     * @throws DateTimeException  if unable to convert to a proleptic-year,
     *                            such as if the year is invalid for the era
     * @throws ClassCastException if the {@code era} is not of the correct type for the chronology
     */
    @Override
    public int prolepticYear (final Era era, final int yearOfEra) {
        if (!(era instanceof InternationalFixedEra)) {
            throw new ClassCastException ("Era must be InternationalFixedEra");
        }

        if (0 > yearOfEra) {
            throw new DateTimeException ("Year of era MUST not be negative!");
        }

        return yearOfEra;
    }

    /**
     * Gets the range of valid values for the specified field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * <p>
     * Note that the result only describes the minimum and maximum valid values
     * and it is important not to read too much into them. For example, there
     * could be values within the range that are invalid for the field.
     * <p>
     * This method will return a result whether or not the chronology supports the field.
     *
     * @param field the field to get the range for, not null
     * @return the range of valid values for the field, not null
     * @throws DateTimeException if the range for the field cannot be obtained
     */
    @Override
    public ValueRange range (final ChronoField field) {
        switch (field) {
            case PROLEPTIC_MONTH:
                return PROLEPTIC_MONTH_RANGE;
            case YEAR_OF_ERA:
                return YEAR_RANGE;
            case YEAR:
                return YEAR_RANGE;
            case DAY_OF_MONTH:
                return DAY_OF_MONTH_RANGE;
            case DAY_OF_YEAR:
                return DAY_OF_YEAR_RANGE;
            case MONTH_OF_YEAR:
                return MONTH_OF_YEAR_RANGE;
            default:
                return field.range ();
        }
    }

    /**
     * Resolve singleton.
     *
     * @return the singleton instance, not null
     */
    @SuppressWarnings ("static-method")
    private Object readResolve () {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------

    /**
     * Obtains a local date in Julian calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era        the Julian era, not null
     * @param yearOfEra  the year-of-era
     * @param month      the month-of-year
     * @param dayOfMonth the day-of-month
     * @return the Julian local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code JulianEra}
     */
    @Override
    public InternationalFixedDate date (final Era era, final int yearOfEra, final int month, final int dayOfMonth) {
        return date (prolepticYear (era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in Julian calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear the proleptic-year
     * @param month         the month-of-year
     * @param dayOfMonth    the day-of-month
     * @return the Julian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public InternationalFixedDate date (final int prolepticYear, final int month, final int dayOfMonth) {
        return InternationalFixedDate.of (prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a local date in Julian calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era       the Julian era, not null
     * @param yearOfEra the year-of-era
     * @param dayOfYear the day-of-year
     * @return the Julian local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code JulianEra}
     */
    @Override
    public InternationalFixedDate dateYearDay (final Era era, final int yearOfEra, final int dayOfYear) {
        return dateYearDay (prolepticYear (era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in Julian calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear the proleptic-year
     * @param dayOfYear     the day-of-year
     * @return the Julian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public InternationalFixedDate dateYearDay (final int prolepticYear, final int dayOfYear) {
        return InternationalFixedDate.ofYearDay (prolepticYear, dayOfYear);
    }

    /**
     * Obtains a local date in the Julian calendar system from the epoch-day.
     *
     * @param epochDay the epoch day
     * @return the Julian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public InternationalFixedDate dateEpochDay (final long epochDay) {
        return InternationalFixedDate.ofEpochDay (epochDay);
    }

    //-------------------------------------------------------------------------

    /**
     * Obtains the current Julian local date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current Julian local date using the system clock and default time-zone, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public InternationalFixedDate dateNow () {
        return InternationalFixedDate.now ();
    }

    /**
     * Obtains the current Julian local date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     * @return the current Julian local date using the system clock, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public InternationalFixedDate dateNow (final ZoneId zone) {
        return InternationalFixedDate.now (zone);
    }

    /**
     * Obtains the current Julian local date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock the clock to use, not null
     * @return the current Julian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public InternationalFixedDate dateNow (final Clock clock) {
        return InternationalFixedDate.now (clock);
    }

    //-------------------------------------------------------------------------

    /**
     * Obtains a Julian local date from another date-time object.
     *
     * @param temporal the date-time object to convert, not null
     * @return the Julian local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public InternationalFixedDate date (final TemporalAccessor temporal) {
        return InternationalFixedDate.from (temporal);
    }
}
