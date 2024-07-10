# CHANGELOG

## [1.2.1](https://github.com/obecker/decycle/compare/v1.2.0...v1.2.1) – 2024-07-10
   - Several dependency updates
     * guava 33.1.0 → 33.2.1
     * maven 3.9.6 → 3.9.8
     * maven-plugin-annotations 3.12.0 → 3.13.1
     * webjars-locator-core 0.58 → 0.59

## [1.2.0](https://github.com/obecker/decycle/compare/v1.1.1...v1.2.0) – 2024-04-15
   - Several dependency updates
     * asm 9.6 → 9.7 (supports Java 23)
     * guava 33.0.0 → 33.1.0
     * maven-plugin-annotations 3.11.0 → 3.12.0
     * slf4j 2.0.11 → 2.0.13
     * webjars-locator-core 0.55 → 0.58
     * bootstrap-icons 1.11.2 → 1.11.3
   - Fix jQuery CVE warning from tooltipster webjar (fixes [#79](https://github.com/obecker/decycle/issues/79))
   - Add html DOCTYPE to generated reports

## [1.1.1](https://github.com/obecker/decycle/compare/v1.1.0...v1.1.1) – 2024-01-15
   - Several dependency updates
     * guava 32.1.3 → 33.0.0
     * maven 3.9.5 → 3.9.6
     * maven-plugin-annotations 3.9.0 → 3.11.0
     * slf4j 2.0.9 → 2.0.11
     * webjars-locator-core 0.53 → 0.55
     * bootstrap-icons 1.11.1 → 1.11.2

## [1.1.0](https://github.com/obecker/decycle/compare/v1.0.0...v1.1.0) – 2023-10-11
   - Several dependency updates
     * asm 9.5 → 9.6 (supports Java 22)
     * guava 32.1.2 → 32.1.3
     * maven 3.9.4 → 3.9.5
     * slf4j 2.0.7 → 2.0.9
     * jquery 3.7.0 → 3.7.1
     * bootstrap-icons 1.10.5 → 1.11.1

## [1.0.0](https://github.com/obecker/decycle/compare/v0.11.0...v1.0.0) – 2023-08-09
   - Support for Java 21 (by upgrading to [asm 9.5](https://asm.ow2.io/versions.html))

## [0.11.0](https://github.com/obecker/decycle/compare/v0.10.0...v0.11.0) – 2023-03-12
   - Remove anonymous classes from the HTML report. Dependencies from anonymous classes to other classes will be treated
     as dependencies from the parent class.
   - Add support for Java Record Component annotations (annotations with target `RECORD_COMPONENT`)
   - Bugfix: Prevent `FileAlreadyExistsException` when creating HTML reports 
     (fixes [#25](https://github.com/obecker/decycle/issues/25))
   - Add configuration option `reportsEnabled` to the Decycle Gradle Plugin
   - Add configuration option `skipReports` to the Decycle Maven Plugin

## [0.10.0](https://github.com/obecker/decycle/compare/v0.9.0...v0.10.0) – 2022-12-14
   - Gradle Plugin: Better support for the Kotlin Gradle DSL (for `sourceSets` and `ignoring`)
   - Bugfix: Load specific asset versions from webjars when generating the report
     (fixes [#16](https://github.com/obecker/decycle/issues/16))

## [0.9.0](https://github.com/obecker/decycle/compare/v0.8.0...v0.9.0) – 2022-01-08
   - Breaking changes:
     - Slicing patterns will now only match classes (and no longer packages).
       This removes the ambiguity for patterns of the form `com.example.*`.
       Simple package patterns, e.g. `com.example.foo` now need to be changed to `com.example.foo.*`.
   - Patterns now support the `?` character for matching one single character
   - The decycle-maven-plugin now also supports the configuration of custom slicing constraints
   - Decycle now validates custom slicing constraints and logs a warning if a constraint contains slices that don't
     exist in the analyzed sources

## [0.8.0](https://github.com/obecker/decycle/compare/v0.7.0...v0.8.0) – 2021-12-27
   - Breaking changes: 
     - Classes that are not itself visited will no longer be considered for packages and slices.
       This will automatically limit the report to the classes of the project. Classes and packages from the java
       standard library or from any third-party libraries will no longer appear as dependencies in the report.
       The typical configuration that contains an `including` for your base package is no longer necessary.
       (Side effect: the performance of the dependency analysis has been improved.) 
     - Slicing patterns will no longer match slices, i.e. they will now match only *classes* and *packages*.
     - Unnamed slicing patterns now use curly braces `{}` to specify the resulting name.
       Previously parentheses `()` were used. All existing slicing configurations of the form
       `com.example.(*).**` need to be rewritten to `com.example.{*}.**`. Parentheses may now be freely used in
       an unnamed pattern, for example to enclose alternatives as in `com.(foo|bar).{*}.**`.

## [0.7.0](https://github.com/obecker/decycle/compare/v0.6.0...v0.7.0) – 2021-12-17
   - Introduce decycle-maven-plugin for executing decycle checks within a maven build
   - decycle-gradle-plugin: deprecate slicing configuration namedPattern(name, pattern),
     use 'pattern=name' instead
   - The generated HTML report got a minor face-lift and doesn't load any longer stylesheets and JS libraries from 
     public CDN sites, all necessary resources will now be created locally

## [0.6.0](https://github.com/obecker/decycle/compare/v0.5.0...v0.6.0) – 2021-09-29
   - Add arc tooltips for displaying the class dependencies for this arc in the dependency image
     (and hide other arcs when hovering over an arc)

## [0.5.0](https://github.com/obecker/decycle/compare/v0.4.0...v0.5.0) – 2021-07-18
   - Hide other dependency arcs when hovering over the nodes in a dependency image
   - Add a title to the dependency image  
   - Add configuration option 'ignoreFailures' to the gradle plugin

## [0.4.0](https://github.com/obecker/decycle/compare/v0.3.0...v0.4.0) – 2021-07-05
   - Add links between packages/slices in the dependency tables
   - Add image(s) depicting the dependency graph and cycles

## 0.3.1 – 2021-06-07
   - Republication of 0.3.0 due to a wrong dependency in the gradle plugin 

## [0.3.0](https://github.com/obecker/decycle/compare/v0.2.0...v0.3.0) – 2021-06-07
   - Add project / source set name to generated HTML report
   - Add favicon
   - Gradle Plugin: rename configuration option `ignore` to `ignoring` 

## 0.2.0 – 2021-05-27
 - Initial Release
