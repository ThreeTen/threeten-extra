ThreeTen-Extra
==============

ThreeTen-Extra provides additional date-time classes that complement those in JDK 8.

Not every piece of date/time logic is destined for the JDK.
Some concepts are too specialized or too bulky to make it in.
This project provides some of those additional classes as a well-tested and reliable jar.


### Documentation
Various documentation is available:

* The [home page](https://www.threeten.org/threeten-extra/)
* The [user guide](https://www.threeten.org/threeten-extra/userguide.html)
* The [Javadoc](https://www.threeten.org/threeten-extra/apidocs/org.threeten.extra/module-summary.html)


### Releases
Release 1.6.0 is the current release.
This release is considered stable and worthy of the 1.x tag as per [SemVer](https://semver.org/spec/v2.0.0.html).

ThreeTen-Extra requires Java SE 8 or later and has no dependencies.

Available in the [Maven Central repository](https://search.maven.org/search?q=g:org.threeten%20AND%20a:threeten-extra&core=gav)

![Tidelift dependency check](https://tidelift.com/badges/github/ThreeTen/threeten-extra)


### Support
Please use [Stack Overflow](https://stackoverflow.com/search?q=threeten-extra) for general usage questions.
GitHub [issues](https://github.com/ThreeTen/threeten-extra/issues) and [pull requests](https://github.com/ThreeTen/threeten-extra/pulls)
should be used when you want to help advance the project.
Commercial support is available via the
[Tidelift subscription](https://tidelift.com/subscription/pkg/maven-org-threeten-threeten-extra?utm_source=maven-org-threeten-threeten-extra&utm_medium=referral&utm_campaign=readme).

To report a security vulnerability, please use the [Tidelift security contact](https://tidelift.com/security).
Tidelift will coordinate the fix and disclosure.


### Release process

* Update version (README.md, index.md, changes.xml)
* Commit and push
* Run `mvn clean release:clean release:prepare release:perform` on Java 11
* Website will be built and released by GitHub Actions
