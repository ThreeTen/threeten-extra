ThreeTen-Extra
==============

ThreeTen-Extra provides additional date-time classes that complement those in JDK 8.

Not every piece of date/time logic is destined for the JDK.
Some concepts are too specialized or too bulky to make it in.
This project provides some of those additional classes as a well-tested and reliable jar.


### Documentation
Various documentation is available:

* The [home page](http://www.threeten.org/threeten-extra/)
* The [user guide](http://www.threeten.org/threeten-extra/userguide.html)
* The [Javadoc](http://www.threeten.org/threeten-extra/apidocs/index.html)


### Releases
Release 1.3.2 is the current release.
This release is considered stable and worthy of the 1.x tag.

ThreeTen-Extra requires Java SE 8 or later and has no dependencies.

Available in the [Maven Central repository](https://search.maven.org/search?q=g:org.threeten%20AND%20a:threeten-extra&core=gav)


### Support
Please use GitHub [issues](https://github.com/ThreeTen/threeten-extra/issues) and Pull Requests for support.


### Release process

* Update version (README.md, index.md, changes.xml)
* Commit and push
* `mvn clean release:clean release:prepare release:perform`
* Website will be built and released by Travis
