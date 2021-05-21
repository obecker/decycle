package de.obqo.decycle.check;

import static de.obqo.decycle.check.Layer.anyOf;
import static de.obqo.decycle.check.Layer.oneOf;
import static de.obqo.decycle.check.MockSlicingSource.d;
import static de.obqo.decycle.check.MockSlicingSource.dependenciesIn;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.check.Constraint.Dependency;

import java.util.List;

import org.junit.jupiter.api.Test;

class DirectLayeringConstraintTest {

    private DirectLayeringConstraint c = new DirectLayeringConstraint("t", List.of(anyOf("a"), anyOf("b"), anyOf("c")));

    private List<Constraint.Violation> violations(final String sliceType, final Dependency... deps) {
        return this.c.violations(new MockSlicingSource(sliceType, deps));
    }

    @Test
    void violationFreeGraphShouldResultInEmptySetOfViolations() {
        assertThat(violations("t", d("a", "b"), d("b", "c"))).isEmpty();
    }

    @Test
    void skippingLayersShouldBeReported() {
        assertThat(dependenciesIn(violations("t", d("a", "c")))).containsExactly(d("a", "c"));
    }

    @Test
    void inverseDependencyShouldBeReported() {
        assertThat(dependenciesIn(violations("t", d("b", "a")))).containsExactly(
                d("b", "a"));
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
    void dependencyToUnknownInTheMiddleShouldBeReported() {
        assertThat(dependenciesIn(violations("t", d("b", "x")))).containsExactly(d("b", "x"));
    }

    @Test
    void dependencyFromUnknownInTheMiddleShouldBeReported() {
        assertThat(dependenciesIn(violations("t", d("x", "b")))).containsExactly(d("x", "b"));
    }

    @Test
    void shouldProvideSimpleShortStringForSingleLayers() {
        assertThat(new DirectLayeringConstraint("type", List.of(oneOf("a"), anyOf("b")))
                .getShortString())
                .isEqualTo("a => b");
    }

    @Test
    void shouldProvideShortStringForMultipleLayers() {
        assertThat(new DirectLayeringConstraint("type", List.of(oneOf("a", "x"), anyOf("b", "y")))
                .getShortString())
                .isEqualTo("[a, x] => (b, y)");
    }
}
