## User guide

ThreeTen-Extra is a small library that builds on the Java SE 8
[`java.time`](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html) package.


## Value types

The additional value types operate exactly as per similar classes in Java SE 8.
These include:

* [`DayOfMonth`](apidocs/org.threeten.extra/org/threeten/extra/DayOfMonth.html) - a day-of-month without month or year
* [`DayOfYear`](apidocs/org.threeten.extra/org/threeten/extra/DayOfYear.html) - a day-of-year without year
* [`AmPm`](apidocs/org.threeten.extra/org/threeten/extra/AmPm.html) - before or after midday
* [`Quarter`](apidocs/org.threeten.extra/org/threeten/extra/Quarter.html) - the four quarters, Q1, Q2, Q3 and Q4
* [`YearQuarter`](apidocs/org.threeten.extra/org/threeten/extra/YearQuarter.html) - combines a year and quarter, 2014-Q4
* [`YearWeek`](apidocs/org.threeten.extra/org/threeten/extra/YearWeek.html) - combines a week-based-year and a week, 2014-W06
* [`OffsetDate`](apidocs/org.threeten.extra/org/threeten/extra/OffsetDate.html) - combines `LocalDate` and `ZoneOffset`
* [`Seconds`](apidocs/org.threeten.extra/org/threeten/extra/Seconds.html) - an amount of time measured in seconds
* [`Minutes`](apidocs/org.threeten.extra/org/threeten/extra/Minutes.html) - an amount of time measured in minutes
* [`Hours`](apidocs/org.threeten.extra/org/threeten/extra/Hours.html) - an amount of time measured in hours
* [`Days`](apidocs/org.threeten.extra/org/threeten/extra/Days.html) - an amount of time measured in days
* [`Weeks`](apidocs/org.threeten.extra/org/threeten/extra/Weeks.html) - an amount of time measured in weeks
* [`Months`](apidocs/org.threeten.extra/org/threeten/extra/Months.html) - an amount of time measured in months
* [`Years`](apidocs/org.threeten.extra/org/threeten/extra/Years.html) - an amount of time measured in years
* [`Interval`](apidocs/org.threeten.extra/org/threeten/extra/Interval.html) - an interval between two instants
* [`LocalDateRange`](apidocs/org.threeten.extra/org/threeten/extra/LocalDateRange.html) - a range between two dates
* [`PeriodDuration`](apidocs/org.threeten.extra/org/threeten/extra/PeriodDuration.html) - combines `Period` and `Duration`


## Period/Duration formatting

The JDK does not provide a mechanism to format periods or durations beyond ISO-8601.
A simple mechanism is provided here in [`AmountFormats`](apidocs/org.threeten.extra/org/threeten/extra/AmountFormats.html):

```
 Period period = Period.of(1, 6, 5);
 String str = AmountFormats.wordBased(period, Locale.ENGLISH);
 // output: "1 year, 6 months and 5 days"
```

Translations are provided for bg, ca, cs, da, de, en, es, fa, fi, fr, it, ja, nb, nl, nn, pl, pt, ro, ru, sv, tr.
Feel free to raise PRs for other languages.


## Calendar systems

The additional calendar systems operate exactly as per similar classes in Java SE 8.
These include:

* [Accounting](apidocs/org.threeten.extra/org/threeten/extra/chrono/AccountingChronology.html) calendar system
* [British Cutover](apidocs/org.threeten.extra/org/threeten/extra/chrono/BritishCutoverChronology.html) calendar system
* [Coptic](apidocs/org.threeten.extra/org/threeten/extra/chrono/CopticChronology.html) calendar system
* [Discordian](apidocs/org.threeten.extra/org/threeten/extra/chrono/DiscordianChronology.html) calendar system
* [Ethiopic](apidocs/org.threeten.extra/org/threeten/extra/chrono/EthiopicChronology.html) calendar system
* [International Fixed](apidocs/org.threeten.extra/org/threeten/extra/chrono/InternationalFixedChronology.html) calendar system
* [Julian](apidocs/org.threeten.extra/org/threeten/extra/chrono/JulianChronology.html) calendar system
* [Pax](apidocs/org.threeten.extra/org/threeten/extra/chrono/PaxChronology.html) calendar system
* [Symmetry010](apidocs/org.threeten.extra/org/threeten/extra/chrono/Symmetry010Chronology.html) calendar system
* [Symmetry454](apidocs/org.threeten.extra/org/threeten/extra/chrono/Symmetry454Chronology.html) calendar system


## Time scales

The JDK operates exclusively using the Java time-scale, defined in
[Instant](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html).
That time-scale eliminates leap seconds using UTC-SLS, although the JDK implementation
is typically based on a POSIX-like clock (and thus relatively undefined around leap seconds).

ThreeTen-Extra provides the additional classes that were rejected from JDK 8.
They were not included as they were deemed to be too specialized for the JDK.

Use `TaiInstant` if you need an instant using the TAI time-scale.

Use `UtcInstant` if you need an instant using the UTC time-scale.

The leap second data is provided in a text file loaded from the classpath.
Only whole leap seconds are handled, and data starts from 1972 by default.
To replace the built in leap seconds file, create a file `META-INF/org/threeten/extra/scale/LeapSeconds.txt`.
The content should have two columns as per [this format](https://github.com/ThreeTen/threeten-extra/blob/0cf61e35fc165062eb70a66b026c54c261dce46d/src/main/resources/org/threeten/extra/scale/LeapSeconds.txt).


## Related projects

See also the list of [related projects](related.html).
