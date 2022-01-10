<img src="images/logo.svg#gh-light-mode-only" alt="Decycle" width="104">
<img src="images/logo-dm.svg#gh-dark-mode-only" alt="Decycle" width="104">

# Limitations

Decycle works on the compiled classes by inspecting the bytecode using
[ASM](https://asm.ow2.io). This results in a few limitations:

### Dependencies on constants for simple types are not reported

Constants (`public static final` attributes) that are declared and initialized with a value of a
primitive type or a String are not reported as dependency. This happens since the compiler 
is inlining those values, so the dependency is not present in the bytecode anymore.

In theory this could be prevented by assigning the constants in a _static initializer_ block, or by computing the constant value
from a method call - however, this would result in rather ugly and unnecessarily complicated source code.

```java
// usages will be inlined by the compiler
public static final String X = "inlined constant";
```
```java
// usages of Y and Z will not be inlined by the compiler
public static final String Y;

static {
    Y = "assigned constant";
}

public static final String Z = "computed constant".toString();
```

### Dependencies on annotations with retention policy `SOURCE` are not reported

Annotations with `@Retention(RetentionPolicy.SOURCE)` will be discarded by the compiler, so they are not visible to ASM
and Decycle. Moreover, annotations on local variables or lambda parameters will never be retained in the bytecode.
See [Retention](https://docs.oracle.com/javase/specs/jls/se17/html/jls-9.html#jls-9.6.4.2) in the Java Language 
Specification.

