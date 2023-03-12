# CHANGELOG

## 0.11.0 - 2023-03-12
   - Remove anonymous classes from the HTML report. Dependencies from anonymous classes to other classes will be treated
     as dependencies from the parent class.
   - Add support for Java Record Component annotations (annotations with target `RECORD_COMPONENT`)
   - Bugfix: Prevent `FileAlreadyExistsException` when creating HTML reports 
     (fixes [#25](https://github.com/obecker/decycle/issues/25))
   - Add configuration option `reportsEnabled` to the Decycle Gradle Plugin
   - Add configuration option `skipReports` to the Decycle Maven Plugin

## 0.10.0 - 2022-12-14
   - Gradle Plugin: Better support for the Kotlin Gradle DSL (for `sourceSets` and `ingnoring`)
   - Bugfix: Load specific asset versions from webjars when generating the report
     (fixes [#16](https://github.com/obecker/decycle/issues/16))

## 0.9.0 – 2022-01-08
   - Breaking changes:
     - Slicing patterns will now only match classes (and no longer packages).
       This removes the ambiguity for patterns of the form `com.example.*`.
       Simple package patterns, e.g. `com.example.foo` now need to be changed to `com.example.foo.*`.
   - Patterns now support the `?` character for matching one single character
   - The decycle-maven-plugin now also supports the configuration of custom slicing constraints
   - Decycle now validates custom slicing constraints and logs a warning if a constraint contains slices that don't
     exist in the analyzed sources

## 0.8.0 – 2021-12-27
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

## 0.7.0 – 2021-12-17
   - Introduce decycle-maven-plugin for executing decycle checks within a maven build
   - decycle-gradle-plugin: deprecate slicing configuration namedPattern(name, pattern),
     use 'pattern=name' instead
   - The generated HTML report got a minor face-lift and doesn't load any longer stylesheets and JS libraries from 
     public CDN sites, all necessary resources will now be created locally

## 0.6.0 – 2021-09-29
   - Add arc tooltips for displaying the class dependencies for this arc in the dependency image
     (and hide other arcs when hovering over an arc)

## 0.5.0 – 2021-07-18
   - Hide other dependency arcs when hovering over the nodes in a dependency image
   - Add a title to the dependency image  
   - Add configuration option 'ignoreFailures' to the gradle plugin

## 0.4.0 – 2021-07-05
   - Add links between packages/slices in the dependency tables
   - Add image(s) depicting the dependency graph and cycles

## 0.3.1 – 2021-06-07
   - Republication of 0.3.0 due to a wrong dependency in the gradle plugin 

## 0.3.0 – 2021-06-07
   - Add project / source set name to generated HTML report
   - Add favicon
   - Gradle Plugin: rename configuration option `ignore` to `ignoring` 

## 0.2.0 – 2021-05-27
 - Initial Release
