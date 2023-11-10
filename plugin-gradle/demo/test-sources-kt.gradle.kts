plugins {
    java
    id("de.obqo.decycle")
}

decycle {
    sourceSets(sourceSets.test)
}
