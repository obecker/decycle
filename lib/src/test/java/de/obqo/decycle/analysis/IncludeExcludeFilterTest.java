package de.obqo.decycle.analysis;

import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.NodeFilter;

import java.util.Set;

import org.junit.jupiter.api.Test;

class IncludeExcludeFilterTest {

    private Node n(final String s) {
        return Node.sliceNode(s, s);
    }

    private NodeFilter f(final boolean result) {
        return __ -> result;
    }

    @Test
    void shouldBeTrueWithoutIncludesAndExcludes() {
        assertThat(new IncludeExcludeFilter(Set.of(), Set.of()).test(n("x"))).isTrue();
    }

    @Test
    void shouldBeTrueIfOneIncludeIsTrue() {
        assertThat(new IncludeExcludeFilter(Set.of(f(true), f(false), f(true)), Set.of())
                .test(n("x"))).isTrue();
    }

    @Test
    void shouldBeFalseIfAllIncludesAreFalse() {
        assertThat(new IncludeExcludeFilter(Set.of(f(false), f(false), f(false)), Set.of())
                .test(n("x"))).isFalse();
    }

    @Test
    void shouldBeFalseIfOneExcludeIsTrue() {
        assertThat(new IncludeExcludeFilter(Set.of(), Set.of(f(false), f(true), f(false)))
                .test(n("x"))).isFalse();
    }

    @Test
    void shouldBeTrueIfAllExcludesAreFalse() {
        assertThat(new IncludeExcludeFilter(Set.of(), Set.of(f(false), f(false), f(false)))
                .test(n("x"))).isTrue();
    }

    @Test
    void shouldOnlyBeTrueIfIncludeIsTrueAndExcludeIsFalse() {
        assertThat(new IncludeExcludeFilter(Set.of(f(true)), Set.of(f(false))).test(n("y"))).isTrue();
        assertThat(new IncludeExcludeFilter(Set.of(f(false)), Set.of(f(false))).test(n("y"))).isFalse();
        assertThat(new IncludeExcludeFilter(Set.of(f(true)), Set.of(f(true))).test(n("y"))).isFalse();
    }
}
