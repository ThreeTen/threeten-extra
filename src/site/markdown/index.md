## <i></i> About

**ThreeTen-Extra** provides additional date-time classes that complement those in
[Java SE 8](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html).

Not every piece of date/time logic is destined for the JDK.
Some concepts are too specialized or too bulky to make it in.
This project provides some of those additional classes as a well-tested and reliable jar.
It is curated by the primary author of the Java 8 date and time library, [Stephen Colebourne](http://www.joda.org/).

ThreeTen-Extra is licensed under the business-friendly [BSD 3-clause license](license.html).


## <i></i> Features

The following features are included:

* [`DayOfMonth`](apidocs/org/threeten/extra/DayOfMonth.html) - a day-of-month without month or year
* [`DayOfYear`](apidocs/org/threeten/extra/DayOfYear.html) - a day-of-year without year
* [`AmPm`](apidocs/org/threeten/extra/AmPm.html) - before or after midday
* [`Quarter`](apidocs/org/threeten/extra/Quarter.html) - the four quarters, Q1, Q2, Q3 and Q4
* [`YearQuarter`](apidocs/org/threeten/extra/YearQuarter.html) - combines a year and quarter, 2014Q4
* [`Days`](apidocs/org/threeten/extra/Days.html),
[`Weeks`](apidocs/org/threeten/extra/Weeks.html),
[`Months`](apidocs/org/threeten/extra/Months.html) and
[`Years`](apidocs/org/threeten/extra/Years.html) - amounts of time
* [`Interval`](apidocs/org/threeten/extra/Interval.html) - an interval between two instants
* Weekend adjusters
* [Coptic](apidocs/org/threeten/extra/chrono/CopticChronology.html) calendar system
* [Ethiopic](apidocs/org/threeten/extra/chrono/EthiopicChronology.html) calendar system
* [Julian](apidocs/org/threeten/extra/chrono/JulianChronology.html) calendar system
* Support for the TAI and UTC [time-scales](apidocs/org/threeten/extra/scale/package-summary.html)


## <i></i> Documentation

Various documentation is available:

* The helpful [user guide](userguide.html)
* The [Javadoc](apidocs/index.html)
* The [change notes](changes-report.html) for each release
* The [GitHub](https://github.com/ThreeTen/threeten-extra) source repository

---

## <i></i> Releases

There are no full releases yet!
Release 0.8 is the current development release intended for feedback.
The code is fully tested, but there may yet be bugs and the API may yet change.
There should be no great reason why it cannot be used in production if you can cope with future API change.

The project runs on Java SE 8 and has no [dependencies](dependencies.html).

Available in [Maven Central](http://search.maven.org/#artifactdetails%7Corg.threeten%7Cthreeten-extra%7C0.8%7Cjar).

```xml
<dependency>
  <groupId>org.threeten</groupId>
  <artifactId>threeten-extra</artifactId>
  <version>0.8</version>
</dependency>
```

---

### Support

Support on bugs, library usage or enhancement requests is available on a best efforts basis.

To suggest enhancements or contribute, please [fork the source code](https://github.com/ThreeTen/threeten-extra)
on GitHub and send a Pull Request.

Alternatively, use GitHub [issues](https://github.com/ThreeTen/threeten-extra/issues).

