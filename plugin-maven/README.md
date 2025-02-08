[![Version](https://img.shields.io/maven-central/v/de.obqo.decycle/decycle-maven-plugin?logo=apachemaven)](https://search.maven.org/artifact/de.obqo.decycle/decycle-maven-plugin)

![Decycle](../readme/images/logo-maven-plugin.svg#gh-light-mode-only)
![Decycle](../readme/images/logo-maven-plugin-dm.svg#gh-dark-mode-only)

The **Decycle Maven Plugin** adds verification goals to the Maven build that check for package and slice cycles on the classes of a Maven project.

**Requirements:** Maven ≥ 3.3.1, Java ≥ 11

## Installation

Add the decycle plugin to the `build` section of your `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
        <groupId>de.obqo.decycle</groupId>
        <artifactId>decycle-maven-plugin</artifactId>
        <version>...</version>
        <executions>
          <execution>
            <goals>
              <goal>check</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <!-- see below -->
        </configuration>
    </plugin>
  </plugins>
</build>
```

The decycle plugin provides the goals `decycle:check`, `decycle:checkMain`, and `decycle:checkTest`.

## Running

The above base configuration will execute the goal `decycle:check` automatically in the `verify` phase of the maven lifecycle.
Decycle will check the `main` and the `test` classes separately, and it will create a dependency report for each check.
These reports can be found in the reporting directory,
i.e. typically the two reports are `target/site/decycle/main.html` and `target/site/decycle/test.html`.
If it detects violations (i.e. package cycles) then the build will fail by default 
(however, this behavior can be adjusted, see `ignoreFailures` below in [Configuration](#configuration)).

The two goals `decycle:checkMain` and `decycle:checkTest` are intended to be invoked as single goals
(e.g. via `mvn decycle:checkMain`). They will first compile the sources and perform the decycle check afterward.
They may be executed after fixing violations detected by decycle.
(Note: running `decycle:check` in contrast will *not* automatically recompile the sources.)

The execution of the decycle goals might be skipped by passing the following properties to maven
(e.g. via `mvn verify -Ddecycle.skip`):

 * `decycle.skip` will skip decycle checks completely
 * `decycle.skipMain` will skip the decycle check for the `main` classes 
 * `decycle.skipTest` will skip the decycle check for the `test` classes

Setting the property `decycle.ignoreFailures` will ignore any failures detected by decycle
(but it will still generate the reports).

## Configuration

Within the `configuration` element of the plugin (see [Installation](#installation) above) the following parameters can be defined:

```xml
<configuration>
  <including>org.example.includes.**, ...</including>
  <excluding>org.example.excludes.**, ...</excluding>
  <ignoring>
    <dependency>
      <from>org.examples.from.Example</from>
      <to>org.examples.to.**</to>
    </dependency>
  </ignoring>
  <slicings>
    <slicing>
      <name>module</name>
      <patterns>org.example.{*}.**, ...</patterns>
      <constraints>
        <allow>a, b, ...</allow>
        <allow-direct>
          <any-of>x</any-of>
          <any-of>y, z</any-of>
        </allow-direct>
      </constraints>
    </slicing>
  </slicings>
  <ignoreFailures>false</ignoreFailures>
  <skip>false</skip>
  <skipMain>false</skipMain>
  <skipTest>false</skipTest>
  <skipReports>false</skipReports>
</configuration>
```

 * `including` defines a comma separated list of [patterns](../readme/patterns.md) for the classes that should be included (default: all).
  
 * `excluding` defines a comma separated list of [patterns](../readme/patterns.md) for the classes that should be excluded (default: none).

 * `ignoring` defines a list of dependencies that should be ignored when checking cycle constraints on the analyzed classes
   (default none). This setting differs from `excluding` as the ignored dependency is not excluded from the dependency graph 
   (i.e. it is present in the report).
   Each ignored dependency is represented by a `dependency` element containing `from` and `to` [patterns](../readme/patterns.md), both are optional:
    * `from` defines the source of the dependency (default: all) 
    * `to` defines the target of the dependency (default: all) 

 * `slicings` defines a list of [slicings](../readme/slicings.md) for the packages. 
   Each slicing is represented by a `slicing` element containing `name` and `patterns` elements, both are required:
   * `name` defines the name of the slicing
   * `patterns` defines a comma separated list of [patterns](../readme/slicings.md#slicing-patterns), in which each 
     pattern is either an unnamed pattern (containing curly braces for determining the slice) or a named pattern of 
     the form _pattern=slice_ (in which _slice_ is the name of the slice and _pattern_ is a simple pattern)
   * `constraints` enables the configuration of [additional constraints](../readme/slicings.md#constraints-on-slices) 
     on the defined slices
     * `allow` defines a [simple order constraint](../readme/slicings.md#simple-order-constraints) 
     * `allow-direct` defines a [strict order constraint](../readme/slicings.md#strict-order-constraints) 
     * both `allow` and `allow-direct` may either contain a comma separated list of slice names, or they may contain
       only `any-of` and `one-of` elements (and no direct text content)
     * `any-of` contains comma separated slices names with an [unspecified slice order](../readme/slicings.md#unspecified-order-of-slices)
     * `one-of` contains comma separated slices names that [must not depend on each other](../readme/slicings.md#forbidden-dependencies-between-slices)

 * `ignoreFailures` whether to allow the build to continue if there are constraint violations (default: `false`).
   This parameter can also be specified by defining the property `decycle.ignoreFailures`

 * `skip` whether to skip the execution of decycle (default: `false`). 
   This parameter can also be specified by defining the property `decycle.skip`  

 * `skipMain` whether to skip the execution of decycle on the `main` classes (default: `false`). 
   This parameter can also be specified by defining the property `decycle.skipMain`  

 * `skipTest` whether to skip the execution of decycle on the `test` classes (default: `false`). 
   This parameter can also be specified by defining the property `decycle.skipTest`  
 
 * `skipReports` whether to skip the creation of the HTML reports (default: `false`). 
   This parameter can also be specified by defining the property `decycle.skipReports`  
