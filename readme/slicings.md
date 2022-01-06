# Slicings

In Decycle a _slicing_ is a grouping of the analyzed classes. Each resulting group is a _slice_ in this grouping.

In this terminology the _packages_ establish a special slicing of the classes 
(it may be called the _package slicing_, in which every package is a slice). 

Decycle allows the definition of custom slicings. The idea is that the resulting slices of a slicing also should not
contain circular dependencies.

Let's look at an example. Assuming we have the following packages in our project:

```
com.example.app
com.example.app.customer
com.example.app.customer.api
com.example.app.customer.repo
com.example.app.customer.service
com.example.app.invoice
com.example.app.invoice.api
com.example.app.invoice.repo
com.example.app.invoice.service
com.example.app.order
com.example.app.order.api
com.example.app.order.repo
com.example.app.order.service
```

By default, Decycle will analyze the dependencies between these packages and will report any existing package cycle. 
However, it may also be reasonable to check that there are no circular dependencies between the three main package branches 
`customer`, `invoice`, `order`, and in particular the base package `com.example.app`. (A similar idea is used for the
[Dependency Structure Matrix](https://blog.jetbrains.com/idea/2020/01/dsm-prepare-your-application-for-modularity/) 
in IntelliJ IDEA.)

That means a corresponding slicing for these main branches would look like this with the following 4 slices (separated 
by horizontal lines):
```
com.example.app
––––––––––––––––––––––––––––––––
com.example.app.customer
com.example.app.customer.api
com.example.app.customer.repo
com.example.app.customer.service
––––––––––––––––––––––––––––––––
com.example.app.invoice
com.example.app.invoice.api
com.example.app.invoice.repo
com.example.app.invoice.service
––––––––––––––––––––––––––––––––
com.example.app.order
com.example.app.order.api
com.example.app.order.repo
com.example.app.order.service
```

A slicing in Decycle needs a name, so we will call this example slicing "branches".
Now we also need names for the slices. 

## Slicing patterns

The name of a slice will be derived from the pattern that matches the slice. These patterns come in two flavors:
* named patterns
* unnamed patterns

A **named pattern** has the form `pattern=name` in which the first part is a normal [pattern](patterns.md)
and the part after the equal sign `=` is the assigned name.
Example: `com.example.app.order.**=order` defines that all classes under `com.example.app.order` belong to the slice `order`. 
Note, that it is possible to assign the same name to multiple patterns.

An **unnamed pattern** is a [pattern](patterns.md) with additional curly braces. 
The substring that matches the part in the curly braces defines the slice name. 
(Note: If there are no curly braces present, then the entire matched class name becomes the slice name, 
which is usually not what you want.) 
Example: `com.example.app.{*}.**` puts all classes under `com.example.app.order` into the slice `order`,
and all classes under `com.example.app.invoice` into the slice `invoice`, etc.

Below are two versions for defining the 4 slices of our example:

### Version 1:
The slices should be named `com.example.app`, `com.example.app.customer`, `com.example.app.invoice`, and 
`com.example.app.order`. The patterns for these slices are `{com.example.app}.*` and `{com.example.app.*}.**`.
For the complete slicing configuration click the links below:

<details>
<summary>Gradle (click to expand)</summary>

```groovy
// Gradle
slicings {
    branches {
        patterns '{com.example.app}.*', '{com.example.app.*}.**'
    }
}
```
</details>
<details>
<summary>Maven (click to expand)</summary>

```xml
<!-- Maven -->
<slicings>
  <slicing>
    <name>branches</name>
    <patterns>{com.example.app}.*, {com.example.app.*}.**</patterns>
  </slicing>
</slicings>
```
</details>

Here the first unnamed pattern `{com.example.app}.*` matches all classes from the base package.
The second unnamed pattern `{com.example.app.*}.**` matches all classes in the three main branches, and the resulting 
slices are `com.example.app.customer`, `com.example.app.invoice`, and `com.example.app.order`.

### Version 2:
The slices should be named `base` (for `com.example.app`), `customer`, `invoice`, and `order`.
The patterns for these slices are `com.example.app.*=base` and `com.example.app.{*}.**`.
For the complete slicing configuration click the links below:

<details>
<summary>Gradle (click to expand)</summary>

```groovy
// Gradle
slicings {
    branches {
        patterns 'com.example.app.*=base', 'com.example.app.{*}.**'
    }
}
```
</details>
<details>
<summary>Maven (click to expand)</summary>

```xml
<!-- Maven -->
<slicings>
  <slicing>
    <name>branches</name>
    <patterns>com.example.app.*=base, com.example.app.{*}.**</patterns>
  </slicing>
</slicings>
```
</details>

Here the first pattern `com.example.app.*=base` is a _named pattern_ that assigns the slice name `base` to the classes
of the package `com.example.app`.
The other three patterns are again _unnamed patterns_ like in version 1, but in this case the derived name from the `{*}`
part results in `customer`, `invoice`, and `order`.

Another example: We may also create a different, orthogonal slicing using the pattern
```
com.example.app.*.{*}.**
```
This will result in the three slices `api`, `repo`, and `service`, which also should have no circular dependencies.
Classes that don't match the pattern will be ignored - in other words: a slicing may easily cover only a subset of all 
classes.

<details>
<summary>Gradle (click to expand)</summary>

```groovy
// Gradle
slicings {
    branches {
        patterns 'com.example.app.*=base', 'com.example.app.{*}.**'
    }
    layers {
        patterns 'com.example.app.*.{*}.**'
    }
}
```
</details>
<details>
<summary>Maven (click to expand)</summary>

```xml
<!-- Maven -->
<slicings>
  <slicing>
    <name>branches</name>
    <patterns>com.example.app.*=base, com.example.app.{*}.**</patterns>
  </slicing>
  <slicing>
    <name>layers</name>
    <patterns>com.example.app.*.{*}.**</patterns>
  </slicing>
</slicings>
```
</details>

**Note**: Slicing patterns will be matched from left to right, and the first matching pattern wins.
So if you have two patterns in the same slicing that overlap (i.e. there are classes that match both patterns), 
then the matching classes will be put into the slice defined by the first pattern.
However, overlapping patterns in _different_ slicings will of course put each class into one slice _per slicing_.

## Constraints on slices

Decycle allows the definition of additional constraints for a slicing.

### Simple order constraints

For defining the order of the dependencies in a slicing use `allow` with a list of slices.
For example: `invoice` may depend on `order` and `customer`, `order` may depend on `customer`,
and all three of them may depend on `base`. This would be specified by

```groovy
// Gradle
allow 'invoice', 'order', 'customer', 'base'
```
```xml
<!-- Maven -->
<allow>invoice, order, customer, base</allow>
```


<details>
<summary>Complete Gradle slicing configuration (click to expand)</summary>

```groovy
// Gradle
slicings {
    branches {
        patterns 'com.example.app.*=base', 'com.example.app.{*}.**'
        allow 'invoice', 'order', 'customer', 'base'
    }
}
```
</details>
<details>
<summary>Complete Maven slicing configuration (click to expand)</summary>

```xml
<!-- Maven -->
<slicings>
  <slicing>
    <name>branches</name>
    <patterns>com.example.app.*=base, com.example.app.{*}.**</patterns>
    <constraints>
      <allow>invoice, order, customer, base</allow>
    </constraints>
  </slicing>
</slicings>
```
</details>

In other words: dependencies between slices from left to right are OK.
Slices that are not part of the `allow` specification, are not constrained.

### Strict order constraints

If you don't want to allow dependencies to skip slices, you can use `allowDirect` (Gradle) or `<allow-direct>` (Maven) 
instead of `allow`.
So in the following (probably unrealistic) example `invoice` must depend only on `order`, but not on `customer`:

```groovy
// Gradle
allowDirect 'invoice', 'order', 'customer'
```
```xml
<!-- Maven -->
<allow-direct>invoice, order, customer</allow-direct>
```

Moreover, slices not part of the specified list may only depend on the first element in the list, or only the last 
element in the list may depend on such unspecified slices.

Example: if there would be another slice `product`, then all of the slices in
`allow 'invoice', 'order', 'customer', 'base'` might depend on `product`. However, in
`allowDirect 'invoice', 'order', 'customer'` only `customer` is allowed to depend on `product`.

### Unspecified order of slices

If there is a group of slices for which you don’t care about the order, you can specify them using `anyOf(...)` like in
the following example:

```groovy
// Gradle
allow anyOf('invoice', 'order'), 'customer', 'base'
```

Here we don't force a dependency ordering for `invoice` and `order` (however, it is still not allowed to create a cycle
between these two slices).

In the Maven plugin the corresponding configuration is a bit more verbose:

```xml
<!-- Maven -->
<allow>
  <any-of>invoice, order</any-of>
  <any-of>customer</any-of>
  <any-of>base</any-of>
</allow>
```

Note, that the Maven XML configuration doesn't support mixing simple slice names with `<any-of>` elements, so we have 
to enclose every single slice name in the `<allow>` list in an `<any-of>` element.

### Forbidden dependencies between slices

If you want to define that some slices must not depend directly on each other, you can put them into an `oneOf` construct.
So, changing our example into

```groovy
// Gradle
allow oneOf('invoice', 'order'), 'customer', 'base'
```
```xml
<!-- Maven -->
<allow>
  <one-of>invoice, order</one-of>
  <any-of>customer</any-of> <!-- could be as well <one-of>customer</one-of> -->
  <any-of>base</any-of>
</allow>
```

means, that `invoice` must not depend directly on `order` and vice versa.
Both still may depend on `customer` and `base`.

In particular (using some other example application with a technical layer slicing), it is possible to add a 
constraint that consists solely of a `oneOf` like this:
```groovy
// Gradle
allow oneOf('api', 'web', 'amqp')
```
```xml
<!-- Maven -->
<allow>
  <one-of>api, web, amqp</one-of>
</allow>
```
Here we don't specify any dependency order. Instead, we forbid (direct) dependencies between the three slices
`api`, `web`, and `amqp`.

Both `anyOf` and `oneOf` may be used with `allow` and `allowDirect`.
Also multiple `allow` / `allowDirect` specifications are possible per slicing.
