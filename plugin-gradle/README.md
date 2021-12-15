[![](https://img.shields.io/maven-metadata/v?label=Plugin&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fde%2Fobqo%2Fdecycle%2Fde.obqo.decycle.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/de.obqo.decycle)

![decycle](../readme/logo-gradle-plugin.svg?raw=true)

The **Decycle Gradle Plugin** adds verification tasks to the Gradle build that check for package and slice cycles 
on the sources of the Gradle project.

## Installation

Add the plugin to your build gradle file as described on the 
[Plugin page](https://plugins.gradle.org/plugin/de.obqo.decycle).

## Running

The plugin creates for each source set a corresponding <code>decycle<i>SourceSetName</i></code> task.
Additionally there is one `decycle` task that runs all these source set specific tasks.

You can run a single decycle check (for example for the `main` source set) with

```
gradle decycleMain
```

You can run all decycle checks with

```
gradle decycle
```

The `decycle` task has a dependency on the `check` task, so it will be executed together with other verification tasks if you run

```
gradle check
```

## Configuration

The plugin adds a `decycle` configuration object to the build, that offers the following optional configuration settings:

<pre>
<b>decycle</b> {
    <b>sourceSets</b> sourceSets.main, sourceSets.test, ...
    <b>including</b> 'org.example.includes.**', ...
    <b>excluding</b> 'org.example.excludes.**', ...
    <b>ignoring</b> from: 'org.examples.from.Example', to: 'org.examples.to.**'
    <b>slicings</b> {
        <i>name1</i> {
            <b>patterns</b> 'org.example.(*).**', ...
            <b>allow</b> 'a', 'b', ...
            <b>allowDirect</b> 'x', 'y', ...
        }
        <i>name2</i> {
            ...
        }
    }
    <b>ignoreFailures</b> false
}
</pre>

(_Note_: technically all configuration settings are method calls and no property assignments.
So you have to use `sourceSets ...` or even `sourceSets(...)` instead of `sourceSets = ...`.
Also, when the settings for `sourceSets`, `including`, `excluding`, and `ignoring` are applied multiple times,
they will be added to the existing configuration.)

* `sourceSets`
  defines the source sets that should be analyzed.
  By default all source sets defined in the gradle build file are considered.
  Use this option if you only want a subset of the source sets to be checked.

* `including`
  defines ant style string patterns for the classes that should be included (default: all).
  It is recommended to configure an include pattern for your base package (e.g. `com.company.project.**`),
  otherwise the report will also contain all dependencies to JDK and external packages.

* `excluding`
  defines ant style string patterns for the classes that should be excluded (default: none).

* `ignoring`
  defines a dependency (or an ant style string pattern for a set of dependencies) that should be ignored
  when checking cycle (and other) constraints on the analyzed classes (default none).
  This setting differs from `excluding` as the ignored dependency is not excluded from the dependency graph
  (i.e. it is present in the report). Multiple ignored dependencies can be configured by using `ignoring` multiple times. 
  Ignored dependencies might be useful if you introduce decycle to an existing project and don't want to resolve all 
  existing cyclic dependencies at once.
  Technically the parameter for `ignoring` is a map with the following two keys,
  both are optional:
    * `from:` defines the source of the dependency (default: all)
    * `to`: defines the target of the dependency (default: all)

* `slicings`
  starts the slicings block, each slicing
  is defined by its name (also known as slicing type). A slicing configuration contains:
    * `patterns`
      a list containing patterns (strings), either named or unnamed.
      A named pattern is defined using <code><i>pattern</i>=<i>name</i></code>,
      in an unnamed pattern the name is derived from the matched part in parentheses in the pattern, for example  
      `org.example.(*).**`. 
    * `allow`
      defines a simple constraint on the defined slices
    * `allowDirect`
      defines a strict constraint on the defined slices. As constraints (both simple and strict) you can use
        * a string (referencing the name of the pattern/slice)
        * <code>anyOf(<i>slice, ...</i>)</code>
        * <code>oneOf(<i>slice, ...</i>)</code>
    
* `ignoreFailures` whether to allow the build to continue if there are constraint violations (default: `false`).

For the time being, until I have created a complete documentation, please have a look at the 
orignal documentation for [degraph](http://riy.github.io/degraph/documentation.html) for understanding the concepts of
[slicings](http://riy.github.io/degraph/documentation.html#adding-slicings) and constraints on slices.

