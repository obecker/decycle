package de.obqo.decycle.slicer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IgnoredDependencyTest {

    @Test
    void createShouldTrimAndFallbackToMatchAllPattern() {
        assertThat(IgnoredDependency.create("a", "b")).asString().isEqualTo("a → b");
        assertThat(IgnoredDependency.create("  a   ", "   b\n")).asString().isEqualTo("a → b");
        assertThat(IgnoredDependency.create("   ", "b")).asString().isEqualTo("** → b");
        assertThat(IgnoredDependency.create(null, "b")).asString().isEqualTo("** → b");
        assertThat(IgnoredDependency.create("  a  ", "   ")).asString().isEqualTo("a → **");
        assertThat(IgnoredDependency.create("a", null)).asString().isEqualTo("a → **");
        assertThat(IgnoredDependency.create(" ", null)).asString().isEqualTo("** → **");
        assertThat(IgnoredDependency.create(null, null)).asString().isEqualTo("** → **");
    }
}
