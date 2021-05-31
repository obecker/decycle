[![Build](https://github.com/obecker/decycle/actions/workflows/gradle.yml/badge.svg)](https://github.com/obecker/decycle/actions/workflows/gradle.yml)
[![License](https://img.shields.io/github/license/obecker/decycle)](https://github.com/obecker/decycle/blob/master/LICENSE)


![decycle](readme/logo.svg?raw=true)

Decycle provides checks for cyclic dependencies between packages and slices of packages in a Java (or JVM language) project.
Decycle is based on the ideas of [Degraph](http://riy.github.io/degraph/index.html) that was created by 
[Jens Schauder](https://github.com/schauder).

The recommended way of integrating Decycle is by using a [Gradle Plugin](plugin-gradle). 

## Documentation

**TODO** (but for the time being you might read the original Degraph [documentation](http://riy.github.io/degraph/documentation.html) to understand the ideas behind Decycle)

## Subprojects

* [decycle-lib](lib) is the core library that might be used within other JVM projects, however currently it is not 
  recommended doing so as the API is not stable yet.
* [decycle-gradle-plugin](plugin-gradle) is a gradle plugin that uses the core library and performs Decycle checks of the
  project sources within a gradle build.

## Building

Compile and test everything

<pre>
gradlew build
</pre>

Publish local versions of the current build to maven local

<pre>
gradlew publishToMavenLocal
</pre>
