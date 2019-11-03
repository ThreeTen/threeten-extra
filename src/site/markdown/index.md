## <i></i> About

**ThreeTen-Extra** provides additional date-time classes that complement those in
[Java SE 8](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html).

Not every piece of date/time logic is destined for the JDK.
Some concepts are too specialized or too bulky to make it in.
This project provides some of those additional classes as a well-tested and reliable jar.
It is curated by the primary author of the Java 8 date and time library, [Stephen Colebourne](https://www.joda.org/).

ThreeTen-Extra is licensed under the business-friendly [BSD 3-clause license](license.html).


## <i></i> Features

The following features are included:

* [`DayOfMonth`](apidocs/org.threeten.extra/org/threeten/extra/DayOfMonth.html) - a day-of-month without month or year
* [`DayOfYear`](apidocs/org.threeten.extra/org/threeten/extra/DayOfYear.html) - a day-of-year without year
* [`AmPm`](apidocs/org.threeten.extra/org/threeten/extra/AmPm.html) - before or after midday
* [`Quarter`](apidocs/org.threeten.extra/org/threeten/extra/Quarter.html) - the four quarters, Q1, Q2, Q3 and Q4
* [`YearQuarter`](apidocs/org.threeten.extra/org/threeten/extra/YearQuarter.html) - combines a year and quarter, 2014-Q4
* [`YearWeek`](apidocs/org.threeten.extra/org/threeten/extra/YearWeek.html) - combines a week-based-year and a week, 2014-W06
* [`Seconds`](apidocs/org.threeten.extra/org/threeten/extra/Seconds.html),
[`Minutes`](apidocs/org.threeten.extra/org/threeten/extra/Minutes.html),
[`Hours`](apidocs/org.threeten.extra/org/threeten/extra/Hours.html),
[`Days`](apidocs/org.threeten.extra/org/threeten/extra/Days.html),
[`Weeks`](apidocs/org.threeten.extra/org/threeten/extra/Weeks.html),
[`Months`](apidocs/org.threeten.extra/org/threeten/extra/Months.html) and
[`Years`](apidocs/org.threeten.extra/org/threeten/extra/Years.html) - amounts of time
* [`Interval`](apidocs/org.threeten.extra/org/threeten/extra/Interval.html) - an interval between two instants
* [`LocalDateRange`](apidocs/org.threeten.extra/org/threeten/extra/LocalDateRange.html) - a range between two dates
* [`PeriodDuration`](apidocs/org.threeten.extra/org/threeten/extra/PeriodDuration.html) - combines `Period` and `Duration`
* Weekend adjusters
* [Coptic](apidocs/org.threeten.extra/org/threeten/extra/chrono/CopticChronology.html) calendar system
* [Ethiopic](apidocs/org.threeten.extra/org/threeten/extra/chrono/EthiopicChronology.html) calendar system
* [Julian](apidocs/org.threeten.extra/org/threeten/extra/chrono/JulianChronology.html) calendar system
* [Word-based](apidocs/org.threeten.extra/org/threeten/extra/AmountFormats.html) period and duration formatting
* Support for the TAI and UTC [time-scales](apidocs/org.threeten.extra/org/threeten/extra/scale/package-summary.html)


## <i></i> Documentation

Various documentation is available:

* The helpful [user guide](userguide.html)
* The list of [related projects](related.html)
* The [Javadoc](apidocs/org.threeten.extra/module-summary.html)
* The [change notes](changes-report.html) for each release
* The [GitHub](https://github.com/ThreeTen/threeten-extra) source repository

---

## <i></i> Releases

Release 1.5.0 is the current release.
This release is considered stable and worthy of the 1.x tag.

ThreeTen-Extra requires Java SE 8 or later and has no [dependencies](dependencies.html).

Available in [Maven Central](https://search.maven.org/search?q=g:org.threeten%20AND%20a:threeten-extra&core=gav).

```xml
<dependency>
  <groupId>org.threeten</groupId>
  <artifactId>threeten-extra</artifactId>
  <version>1.5.0</version>
</dependency>
```

---

### Support

Please use [Stack Overflow](https://stackoverflow.com/search?q=threeten-extra) for general usage questions.
GitHub [issues](https://github.com/ThreeTen/threeten-extra/issues) and [pull requests](https://github.com/ThreeTen/threeten-extra/pulls)
should be used when you want to help advance the project.
Commercial support is available via the
[Tidelift subscription](https://tidelift.com/subscription/pkg/maven-org-threeten-threeten-extra?utm_source=maven-org-threeten-threeten-extra&utm_medium=referral&utm_campaign=website).

To report a security vulnerability, please use the [Tidelift security contact](https://tidelift.com/security).
Tidelift will coordinate the fix and disclosure.
