# CHANGELOG

## 0.7.0 - 2021-12-17
   - Introduce decycle-maven-plugin for executing decycle checks within a maven build
   - decycle-gradle-plugin: deprecate slicing configuration namedPattern(name, pattern),
     use 'pattern=name' instead
   - The generated HTML report got a minor face-lift and doesn't load any longer stylesheets and JS libraries from 
     public CDN sites, all necessary resources will now be created locally

## 0.6.0 - 2021-09-29
   - Add arc tooltips for displaying the class dependencies for this arc in the dependency image
     (and hide other arcs when hovering over an arc)

## 0.5.0 - 2021-07-18
   - Hide other dependency arcs when hovering over the nodes in a dependency image
   - Add a title to the dependency image  
   - Add configuration option 'ignoreFailures' to the gradle plugin

## 0.4.0 - 2021-07-05
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
