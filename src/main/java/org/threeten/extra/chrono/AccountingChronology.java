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

import static org.threeten.extra.chrono.AccountingPeriod.THIRTEEN_EVEN_PERIODS_OF_4_WEEKS;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.chrono.AbstractChronology;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.Era;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongFunction;

/**
 * An Accounting calendar system.
 * <p>
 * This chronology defines the rules of a proleptic 52/53-week Accounting calendar system.
 * This calendar system follows the rules as laid down in <a href="http://www.irs.gov/publications/p538/ar02.html">IRS Publication 538</a>
 * and the <a href="?">International Financial Reporting Standards</a>.
 * The start of the Accounting calendar will vary against the ISO calendar.
 * Depending on options chosen, it can start as early as {@code 0000-12-26 (ISO)} or as late as {@code 0001-01-04 (ISO)}.
 * <p>
 * This class is proleptic. It implements Accounting chronology rules for the entire time-line.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, the current 'Current Era' (CE) and the previous era 'Before Current Era' (BCE).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year one.
 *  For the previous era the year increases from one as time goes backwards.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current era. For the previous era, years have zero, then negative values.
 * <li>month-of-year - There are 12 or 13 months (periods) in an Accouting year, numbered from 1 to 12 or 13.
 * <li>day-of-month - There are 28 or 35 days in each Accounting month (period), numbered from 1 to 35.
 *  Month (period) length depends on how the year has been divided.
 *  When the Accounting leap year occurs, a week (7 days) is added to a specific month (period);
 *  this increases to maximum day-of-month numbering to 35 or 42.
 * <li>day-of-year - There are 364 days in a standard Accounting year and 371 in a leap year.
 *  The days are numbered from 1 to 364 or 1 to 371.
 * <li>leap-year - Leap years usually occur every 5 or 6 years.  Timing depends on settings chosen.
 * </ul>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 */
public final class AccountingChronology extends AbstractChronology implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 7291205177830286973L;
    /**
     * Range of proleptic month for 12-month (period) year.
     */
    private static final ValueRange PROLEPTIC_MONTH_RANGE_12 = ValueRange.of(-999_999 * 12L, 999_999 * 12L + 11);
    /**
     * Range of proleptic month for 13-month (period) year.
     */
    private static final ValueRange PROLEPTIC_MONTH_RANGE_13 = ValueRange.of(-999_999 * 13L, 999_999 * 13L + 12);
    /**
     * Range of days in year.
     */
    private static final ValueRange DAY_OF_YEAR_RANGE = ValueRange.of(1, 364, 371);

    /**
     * The day of the week on which a given Accounting year ends.
     */
    private final DayOfWeek endsOn;
    /**
     * Whether the calendar ends in the last week of a given Gregorian/ISO month, 
     * or nearest to the last day of the month (will sometimes be in the next month).
     */
    private final boolean inLastWeek;
    /**
     * Which Gregorian/ISO end-of-month the year ends in/is nearest to.
     */
    private final Month end;
    /**
     * How to divide an accounting year.
     */
    final AccountingPeriod division;
    /**
     * The period which will have the leap-week added.
     */
    private final int leapWeekInPeriod;

    /**
     * Difference in days between accounting year end and ISO month end, in ISO year 0.
     */
    private transient final int yearZeroDifference;
    /** 
     * Number of weeks in a month range.
     */
    private transient final ValueRange alignedWeekOfMonthRange;
    /**
     * Number of days in a month range.
     */
    private transient final ValueRange dayOfMonthRange;

    //-----------------------------------------------------------------------
    /**
     * Creates an {@code AccountingChronology} validating the input.
     * Package private as only meant to be called from the builder.
     * 
     * @param endsOn  The day-of-week a given year ends on.
     * @param end The  month-end the year is based on.
     * @param inLastWeek  Whether the year ends in the last week of the month, or nearest the end-of-month.
     * @param division  How the year is divided.
     * @param leapWeekInPeriod  The period in which the leap-week resides.
     * @return The created Chronology, not null.
     * @throws DateTimeException if the chronology cannot be built.
     */
    static AccountingChronology create(DayOfWeek endsOn, Month end, boolean inLastWeek, AccountingPeriod division, int leapWeekInPeriod) {
        if (endsOn == null || end == null || division == null || leapWeekInPeriod == 0) {
            throw new IllegalStateException("AccountingCronology cannot be built: "
                    + (endsOn == null ? "| ending day-of-week |" : "")
                    + (end == null ? "| month ending in/nearest to |" : "")
                    + (division == null ? "| how year divided |" : "")
                    + (leapWeekInPeriod == 0 ? "| leap-week period |" : "")
                    + " not set.");
        }
        if (!division.getMonthsInYearRange().isValidValue(leapWeekInPeriod)) {
            throw new IllegalStateException("Leap week cannot not be placed in non-existant period " + leapWeekInPeriod
                    + ", range is [" + division.getMonthsInYearRange() + "].");
        }

        // Derive cached information.
        LocalDate endingLimit = inLastWeek ? LocalDate.of(0, end, 1).with(TemporalAdjusters.lastDayOfMonth()) :
                LocalDate.of(0, end, 1).with(TemporalAdjusters.lastDayOfMonth()).plusDays(3);
        int yearZeroDifference = (int) endingLimit.with(TemporalAdjusters.previousOrSame(endsOn)).until(endingLimit, ChronoUnit.DAYS);
        // Longest/shortest month lengths and related
        int longestMonthLength = 0;
        int shortestMonthLength = Integer.MAX_VALUE;
        for (int month = 1; month <= division.getMonthsInYearRange().getMaximum(); month++) {
            int monthLength = division.getWeeksInMonth(month);
            shortestMonthLength = Math.min(shortestMonthLength, monthLength);
            longestMonthLength = Math.max(longestMonthLength, monthLength + (month == leapWeekInPeriod ? 1 : 0));
        }
        ValueRange alignedWeekOfMonthRange = ValueRange.of(1, shortestMonthLength, longestMonthLength);
        ValueRange dayOfMonthRange = ValueRange.of(1, shortestMonthLength * 7, longestMonthLength * 7);

        return new AccountingChronology(endsOn, end, inLastWeek, division, leapWeekInPeriod, yearZeroDifference, alignedWeekOfMonthRange, dayOfMonthRange);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data, and cached data.
     * 
     * @param endsOn  The day-of-week a given year ends on.
     * @param end  The month-end the year is based on.
     * @param inLastWeek  Whether the year ends in the last week of the month, or nearest the end-of-month.
     * @param division  How the year is divided.
     * @param leapWeekInPeriod  The period in which the leap-week resides.
     * @param yearZeroDifference  Difference in days between accounting year end and ISO month end, in ISO year 0.
     * @param alignedWeekOfMonthRange  Range of weeks in month.
     * @param dayOfMonthRange  Range of days in month.
     */
    private AccountingChronology(DayOfWeek endsOn, Month end, boolean inLastWeek, AccountingPeriod division, int leapWeekInPeriod, int yearZeroDifference, ValueRange alignedWeekOfMonthRange,
            ValueRange dayOfMonthRange) {
        this.endsOn = endsOn;
        this.end = end;
        this.inLastWeek = inLastWeek;
        this.division = division;
        this.leapWeekInPeriod = leapWeekInPeriod;
        this.yearZeroDifference = yearZeroDifference;
        this.alignedWeekOfMonthRange = alignedWeekOfMonthRange;
        this.dayOfMonthRange = dayOfMonthRange;
    }

    /**
     * Resolve stored instances.
     *
     * @return a built, validated instance.
     */
    private Object readResolve() {
        return AccountingChronology.create(endsOn, end, inLastWeek, division, leapWeekInPeriod);
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCalendarType() {
        // TODO Auto-generated method stub
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a local date in Accounting calendar system from the
     * era, year-of-era, month-of-year and day-of-month fields.
     *
     * @param era  the Accounting era, not null
     * @param yearOfEra  the year-of-era
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Accounting local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code AccountingEra}
     */
    @Override
    public AccountingDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Obtains a local date in Accounting calendar system from the
     * proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param month  the month-of-year
     * @param dayOfMonth  the day-of-month
     * @return the Accounting local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public AccountingDate date(int prolepticYear, int month, int dayOfMonth) {
        return AccountingDate.of(this, prolepticYear, month, dayOfMonth);
    }

    /**
     * Obtains a local date in Accounting calendar system from the
     * era, year-of-era and day-of-year fields.
     *
     * @param era  the Accounting era, not null
     * @param yearOfEra  the year-of-era
     * @param dayOfYear  the day-of-year
     * @return the Accounting local date, not null
     * @throws DateTimeException if unable to create the date
     * @throws ClassCastException if the {@code era} is not a {@code AccountingEra}
     */
    @Override
    public AccountingDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Obtains a local date in Accounting calendar system from the
     * proleptic-year and day-of-year fields.
     *
     * @param prolepticYear  the proleptic-year
     * @param dayOfYear  the day-of-year
     * @return the Accounting local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public AccountingDate dateYearDay(int prolepticYear, int dayOfYear) {
        return AccountingDate.ofYearDay(this, prolepticYear, dayOfYear);
    }

    /**
     * Obtains a local date in the Accounting calendar system from the epoch-day.
     *
     * @param epochDay  the epoch day
     * @return the Accounting local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public AccountingDate dateEpochDay(long epochDay) {
        return AccountingDate.ofEpochDay(this, epochDay);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains the current Accounting local date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current Accounting local date using the system clock and default time-zone, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public AccountingDate dateNow() {
        return AccountingDate.now(this);
    }

    /**
     * Obtains the current Accounting local date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     * @return the current Accounting local date using the system clock, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public AccountingDate dateNow(ZoneId zone) {
        return AccountingDate.now(this, zone);
    }

    /**
     * Obtains the current Accounting local date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current Accounting local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override  // override with covariant return type
    public AccountingDate dateNow(Clock clock) {
        return AccountingDate.now(this, clock);
    }

    //-------------------------------------------------------------------------
    /**
     * Obtains a Accounting local date from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Accounting local date, not null
     * @throws DateTimeException if unable to create the date
     */
    @Override
    public AccountingDate date(TemporalAccessor temporal) {
        return AccountingDate.from(this, temporal);
    }

    /**
     * Obtains a Accounting local date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Accounting local date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<AccountingDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<AccountingDate>) super.localDateTime(temporal);
    }

    /**
     * Obtains a Accounting zoned date-time from another date-time object.
     *
     * @param temporal  the date-time object to convert, not null
     * @return the Accounting zoned date-time, not null
     * @throws DateTimeException if unable to create the date-time
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<AccountingDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<AccountingDate>) super.zonedDateTime(temporal);
    }

    /**
     * Obtains a Accounting zoned date-time in this chronology from an {@code Instant}.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param zone  the time-zone, not null
     * @return the Accounting zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<AccountingDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<AccountingDate>) super.zonedDateTime(instant, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * An Accounting proleptic-year is leap if the time between the end of the previous year 
     * and the end of the current year is 371 days.
     * This method does not validate the year passed in, and only has a
     * well-defined result for years in the supported range.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        LongFunction<Long> getISOleapYearCount = year -> {
            long offsetYear = year - (end == Month.JANUARY ? 1 : 0);
            return Math.floorDiv(offsetYear, 4) - Math.floorDiv(offsetYear, 100) + Math.floorDiv(offsetYear, 400) + (end == Month.JANUARY ? 1 : 0);
        };

        return Math.floorMod(prolepticYear + getISOleapYearCount.apply(prolepticYear) + yearZeroDifference, 7) == 0
                || Math.floorMod(prolepticYear + getISOleapYearCount.apply(prolepticYear - 1) + yearZeroDifference, 7) == 0;
    }

    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (!(era instanceof AccountingEra)) {
            throw new ClassCastException("Era must be AccountingEra");
        }
        return (era == AccountingEra.CE ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public AccountingEra eraOf(int era) {
        return AccountingEra.of(era);
    }

    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(AccountingEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(ChronoField field) {
        switch (field) {
            case ALIGNED_WEEK_OF_MONTH:
                return alignedWeekOfMonthRange;
            case DAY_OF_MONTH:
                return dayOfMonthRange;
            case DAY_OF_YEAR:
                return DAY_OF_YEAR_RANGE;
            case MONTH_OF_YEAR:
                return division.getMonthsInYearRange();
            case PROLEPTIC_MONTH:
                return division == THIRTEEN_EVEN_PERIODS_OF_4_WEEKS ? PROLEPTIC_MONTH_RANGE_13 : PROLEPTIC_MONTH_RANGE_12;
            default:
                break;
        }
        return field.range();
    }

}
