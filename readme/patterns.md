# Patterns

Decycle uses a special pattern syntax that was inspired by [Apache Ant](https://ant.apache.org/manual/dirtasks.html) 
for matching fully qualified class names. A typical decycle pattern looks like this:
`com.example.app.**`

* `?` matches exactly one name character (but not dots)
* `*` matches zero or more name characters (but not dots)
* `**` matches zero or more dots and name characters
* `.` matches only itself (the dot)
* `|` separates alternatives
* `(` and `)` can be used to surround sub-patterns (for example alternatives)

A _name character_ is a character that may be used in names of Java classes,
i.e. usually letters, digits, the underscore `_`, and in particular the dollar symbol `$`.

**Note**: Decycle looks at the byte code and matches the class names created by the compiler. 
For example, the inner interface `java.util.Map.Entry` becomes `java.util.Map$Entry`.

### Examples

* `com.example.?oo` matches `com.example.Foo` and `com.example.Zoo`
  (it doesn't match `com.example.oo` and `com.example.bar.Taboo`)
* `com.example.*` matches `com.example.Foo` and `com.example.Bar`
  (it doesn't match `com.example.bar.Foo`)
* `com.example.B*` matches `com.example.B`, `com.example.Bar`, and `com.example.Bar$Inner` 
* `com.example.**` matches `com.example.Foo` and `com.example.bar.baz.Qux`
* `com.example.**.Foo` matches `com.example.bar.Foo` and `com.example.bar.baz.Foo`
  (however, it doesn't match `com.example.Foo` – this is different from Ant's `**` handling)
* `com.example.*.**` matches `com.example.bar.Foo` and `com.example.bar.baz.Foo` 
  (but it doesn't match `com.example.Foo`)
* `com.example.(bar|baz).Foo` matches only `com.example.bar.Foo` and `com.example.baz.Foo`

## Slicing patterns

Patterns that are used for the configuration of [slicings](slicings.md#slicing-patterns) have an extended syntax.

A _named pattern_ uses the form <code><em>pattern</em>=<em>name</em></code>. In this case the assigned
_name_ defines the name of the slice.

An _unnamed pattern_ should also contain one pair of curly braces (e.g. `com.example.{*}.**`).
The matched substring within the curly braces defines the name of the slice.
