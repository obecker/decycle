plugins {
    id("de.obqo.decycle")
}

decycle {
    including("demo.module.**")
    ignoring("demo.module.b.**" to "demo.module.a.**")
    slicings {
        create("module") {
            patterns("demo.module.{*}.**")
            allow("a", "b")
        }
    }
}
