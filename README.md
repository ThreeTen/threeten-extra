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
Release 1.8.0 is the current release.
This release is considered stable and worthy of the 1.x tag as per [SemVer](https://semver.org/spec/v2.0.0.html).

ThreeTen-Extra requires Java SE 8 or later and has no dependencies.

Available in the [Maven Central repository](https://search.maven.org/search?q=g:org.threeten%20AND%20a:threeten-extra&core=gav)

```
<dependency>
  <groupId>org.threeten</groupId>
  <artifactId>threeten-extra</artifactId>
  <version>1.8.0</version>
</dependency>
```

![Tidelift lifted](https://img.shields.io/badge/-lifted!-2dd160.svg?colorA=58595b&style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABEAAAAOCAYAAADJ7fe0AAAAAXNSR0IArs4c6QAAAAlwSFlzAAAWJQAAFiUBSVIk8AAAAVlpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDUuNC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KTMInWQAAAVhJREFUKBV1kj0vBFEUhmd2sdZHh2IlGhKFQuOviEYiNlFodCqtUqPxA%2FwCjUTnDygkGoVERFQaZFlE9nreO%2BdM5u5wkifvuee892Pu3CyEcA0DeIc%2B9IwftJsR6Cko3uCjguZdjuBZhhwmYDjGrOC96WED41UtsgEdGEAPlmAfpuAbFF%2BFZLfoMfRBGzThDtLgePPwBIpdddGzOArhPHUXowbNptE2www6a%2Fm96Y3pHN7oQ1s%2B13pxt1ENaKzBFWyWzaJ%2BRO0C9Jny6VPSoKjLVbMDC5bn5OPuJF%2BBSe95PVEMuugY5AegS9fCh7BedP45hRnj8TC34QQUe9bTZyh2KgvFk2vc8GIlXyTfsvqr6bPpNgv52ynnlomZJNpB70Xhl%2Bf6Sa02p1bApEfnETwxVa%2Faj%2BW%2FFtHltmxS%2FO3krvpTtTnVgu%2F6gvHRFvG78Ef3kOe5PimJXycY74blT5R%2BAAAAAElFTkSuQmCC)


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
* `git push origin HEAD:refs/tags/release`
* Code and Website will be built and released by GitHub Actions

Release from local:

* Ensure `gpg-agent` is running
* `mvn clean release:clean release:prepare release:perform`
