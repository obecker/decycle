package de.obqo.decycle.check;

import static de.obqo.decycle.check.Layer.anyOf;
import static de.obqo.decycle.check.Layer.oneOf;
import static de.obqo.decycle.check.SimpleDependency.d;
import static de.obqo.decycle.check.SimpleDependency.dependenciesIn;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class LayeringConstraintTest {

    private final LayeringConstraint c = new LayeringConstraint("t", List.of(anyOf("a"), anyOf("b"), anyOf("c")));

    private List<Constraint.Violation> violations(final String sliceType, final SimpleDependency... deps) {
        return this.c.violations(new MockSlicingSource(sliceType, deps));
    }

    @Test
    void violationFreeGraphShouldResultInEmptySetOfViolations() {
        assertThat(violations("t", d("a", "b"), d("b", "c"))).isEmpty();
    }

    @Test
    void skippingLayersShouldBeOk() {
        assertThat(violations("t", d("a", "c"))).isEmpty();
    }

    @Test
    void inverseDependencyShouldBeReported() {
        assertThat(dependenciesIn(violations("t", d("b", "a")))).containsExactly(d("b", "a"));
    }

    @Test
    void dependenciesInOtherLayersShouldBeIgnored() {
        assertThat(violations("x", d("b", "a"))).isEmpty();
    }

    @Test
    void dependencyFromLastToUnknownShouldBeOk() {
        assertThat(violations("t", d("c", "x"))).isEmpty();
    }

    @Test
    void dependencyFromUnknownToFirstShouldBeOk() {
        assertThat(violations("t", d("x", "a"))).isEmpty();
    }

    @Test
    void dependencyToUnknownInTheMiddleShouldBeOk() {
        assertThat(violations("t", d("b", "x"))).isEmpty();
    }

    @Test
    void dependencyFromUnknownInTheMiddleShouldBeOk() {
        assertThat(violations("t", d("x", "b"))).isEmpty();
    }

    @Test
    void shouldProvideSimpleShortStringForSingleLayers() {
        assertThat(new LayeringConstraint("type", List.of(oneOf("a"), anyOf("b"))).getShortString())
                .isEqualTo("a -> b");
    }

    @Test
    void shouldProvideShortStringForMultipleLayers() {
        assertThat(new LayeringConstraint("type", List.of(oneOf("a", "x"), anyOf("b", "y"))).getShortString())
                .isEqualTo("[a, x] -> (b, y)");
    }
}
