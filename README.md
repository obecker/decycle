[![Build](https://github.com/obecker/decycle/actions/workflows/gradle.yml/badge.svg)](https://github.com/obecker/decycle/actions/workflows/gradle.yml)
[![License](https://img.shields.io/github/license/obecker/decycle)](https://github.com/obecker/decycle/blob/master/LICENSE)

![Decycle](readme/images/logo.svg#gh-light-mode-only)
![Decycle](readme/images/logo-dm.svg#gh-dark-mode-only)

Decycle detects [circular dependencies](https://en.wikipedia.org/wiki/Circular_dependency) within packages or 
[slices](readme/slicings.md) in a Java (or JVM language) project.
Having Decycle as a guard in your project prevents cycles and will help keeping 
your code base [clean](https://wiki.sei.cmu.edu/confluence/display/java/DCL60-J.+Avoid+cyclic+dependencies+between+packages) 
and [modular](https://www.infoq.com/articles/modular-java-what-is-it/).

Decycle is based on the ideas of [Degraph](http://riy.github.io/degraph/index.html) that was created by 
[Jens Schauder](https://github.com/schauder).

Decycle provides the following core features:

* it works with a **minimal configuration** – using just the default settings will find cycles between
  the packages of your project
* it provides a **visualization** of the detected package (and slice) dependencies that helps to understand, which 
  classes are responsible for a certain dependency
* it allows the definition of **custom slicings** and additional dependency constraints

The following example shows a package dependency graph created by Decycle. 
It is cycle free (all dependency arcs are on the right side and go downwards).
Hovering over a package will display all incoming and outgoing dependencies.
The width of each arc corresponds to the number of the underlying class dependencies.
Hovering over a dependency arc will show these class dependencies.

Dependencies creating cycles would be displayed as arcs going upwards on the left side of the package blocks.

<img src="https://user-images.githubusercontent.com/197628/148555788-0acb50d1-01b6-4bcb-8559-571c218baa0a.gif" alt="Example report" width="400">

Decycle requires Java 11 or above.
The recommended way of integrating Decycle is by using the [Gradle](plugin-gradle) or [Maven](plugin-maven) plugins. 

## Documentation

There are specific sections in the [Gradle](plugin-gradle/README.md#configuration) and
[Maven](plugin-maven/README.md#configuration) plugin README files for configuring Decycle.

Moreover, there are separate pages about [slicings](readme/slicings.md),
the [pattern syntax](readme/patterns.md) that is used heavily in the configuration,
and the [limitations](readme/limitations.md) of Decycle. 


## Subprojects

* [decycle-gradle-plugin](plugin-gradle) is a Gradle plugin that performs Decycle checks of the project sources within
  a gradle build.
* [decycle-maven-plugin](plugin-maven) is a Maven plugin that performs Decycle checks of the project sources within 
  the maven verify phase.
* [decycle-lib](lib) is the core library used by both plugins that might also be used within other JVM projects, 
  however currently it is not recommended doing so as the API is not stable yet.

## Building

Compile and test the Decycle project

<pre>
gradlew build
</pre>

Publish local versions of the current build to maven local

<pre>
gradlew publishToMavenLocal
</pre>
