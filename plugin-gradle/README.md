[![](https://img.shields.io/maven-metadata/v?label=Plugin&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fde%2Fobqo%2Fdecycle%2Fde.obqo.decycle.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/de.obqo.decycle)

![Decycle](../readme/images/logo-gradle-plugin.svg#gh-light-mode-only)
![Decycle](../readme/images/logo-gradle-plugin-dm.svg#gh-dark-mode-only)

The **Decycle Gradle Plugin** adds verification tasks to the Gradle build that check for package and slice cycles 
on the sources of the Gradle project.

**Requirements:** Gradle ≥ 5.6, Java ≥ 11 

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

<details open>
<summary><b>Groovy Gradle DSL</b></summary>
<pre>
<b>decycle</b> {
    <b>sourceSets</b> sourceSets.main, sourceSets.test, ...
    <b>including</b> 'org.example.includes.**', ...
    <b>excluding</b> 'org.example.excludes.**', ...
    <b>ignoring</b> from: 'org.examples.from.Example', to: 'org.examples.to.**'
    <b>slicings</b> {
        <i>name1</i> {
            <b>patterns</b> 'org.example.{*}.**', ...
            <b>allow</b> 'a', 'b', ...
            <b>allowDirect</b> 'x', <b>anyOf</b>('y', 'z'), ...
        }
        <i>name2</i> {
            ...
        }
    }
    <b>ignoreFailures</b> false
}
</pre>
</details>
<details>
<summary><b>Kotlin Gradle DSL</b> (click to expand)</summary>
<pre>
<b>decycle</b> {
    <b>sourceSets</b>(sourceSets.main, sourceSets.test, ...)
    <b>including</b>("org.example.includes.**", ...)
    <b>excluding</b>("org.example.excludes.**", ...)
    <b>ignoring</b>("org.examples.from.Example" to "org.examples.to.**")
    <b>slicings</b> {
        create("name1") {
            <b>patterns</b>("org.example.{*}.**", ...)
            <b>allow</b>("a", "b", ...)
            <b>allowDirect</b>("x", <b>anyOf</b>("y", "z"), ...)
        }
        create("name2") {
            ...
        }
    }
    <b>ignoreFailures</b>(false)
}
</pre>
</details>

(_Note_: technically all configuration settings are method calls and no property assignments.
So you have to use `sourceSets ...` or even `sourceSets(...)` instead of `sourceSets = ...`.
Also, when the settings for `sourceSets`, `including`, `excluding`, and `ignoring` are applied multiple times,
they will be added to the existing configuration.)

* `sourceSets`
  defines the source sets that should be analyzed.
  By default, all source sets defined in the gradle build file are considered.
  Use this option if you only want a subset of the source sets to be checked.

* `including`
  defines [patterns](../readme/patterns.md) for the classes that should be included (default: all).

* `excluding`
  defines [patterns](../readme/patterns.md) for the classes that should be excluded (default: none).

* `ignoring`
  defines a dependency (or a [pattern](../readme/patterns.md) for a set of dependencies) that should be ignored
  when checking cycle (and other) constraints on the analyzed classes (default none).
  This setting differs from `excluding` as the ignored dependency is not excluded from the dependency graph
  (i.e. it is present in the report). Multiple ignored dependencies can be configured by using `ignoring` multiple times. 
  Ignored dependencies might be useful if you introduce decycle to an existing project and don't want to resolve all 
  existing cyclic dependencies at once.
  Technically (using the Groovy DSL) the parameter for `ignoring` is a map with the following two keys,
  both are optional:
    * `from:` defines the source of the dependency (default: '**')
    * `to`: defines the target of the dependency (default: '**')
  
  Using the Kotlin DLS, the parameter for `ignoring` is typically a pair `"from" to "to"`.

* `slicings`
  starts the slicings block, each [slicing](../readme/slicings.md) has a name (also known as slicing type). 
  A slicing configuration contains:
    * `patterns`
      a list containing [patterns](../readme/patterns.md) (strings), either named or unnamed.
      A named pattern is defined using <code><i>pattern</i>=<i>name</i></code>,
      in an unnamed pattern the name is derived from the matched part in curly braces in the pattern, for example  
      `org.example.{*}.**`. 
    * `allow`
      defines a [simple order constraint](../readme/slicings.md#simple-order-constraints) on the defined slices
    * `allowDirect`
      defines a [strict order constraint](../readme/slicings.md#strict-order-constraints) on the defined slices. 
      As constraints (both simple and strict) you can use
        * a string (the name of a slice)
        * <code>anyOf(<i>slice, ...</i>)</code> for an [unspecified slice order](../readme/slicings.md#unspecified-order-of-slices)
        * <code>oneOf(<i>slice, ...</i>)</code> for [forbidden dependencies between slices](../readme/slicings.md#forbidden-dependencies-between-slices)
    
* `ignoreFailures` whether to allow the build to continue if there are constraint violations (default: `false`).
