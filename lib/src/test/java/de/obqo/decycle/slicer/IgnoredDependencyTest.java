package de.obqo.decycle.slicer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IgnoredDependencyTest {

    @Test
    void shouldTrimAndFallbackToMatchAllPattern() {
        assertThat(new IgnoredDependency("a", "b")).asString().isEqualTo("a → b");
        assertThat(new IgnoredDependency("  a   ", "   b\n")).asString().isEqualTo("a → b");
        assertThat(new IgnoredDependency("   ", "b")).asString().isEqualTo("** → b");
        assertThat(new IgnoredDependency(null, "b")).asString().isEqualTo("** → b");
        assertThat(new IgnoredDependency("  a  ", "   ")).asString().isEqualTo("a → **");
        assertThat(new IgnoredDependency("a", null)).asString().isEqualTo("a → **");
        assertThat(new IgnoredDependency(" ", null)).asString().isEqualTo("** → **");
        assertThat(new IgnoredDependency(null, null)).asString().isEqualTo("** → **");
    }
}
