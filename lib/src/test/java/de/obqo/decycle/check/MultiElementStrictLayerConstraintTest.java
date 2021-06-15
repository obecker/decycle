package de.obqo.decycle.check;

import static de.obqo.decycle.check.Layer.oneOf;
import static de.obqo.decycle.check.SimpleDependency.d;
import static de.obqo.decycle.check.SimpleDependency.dependenciesIn;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MultiElementStrictLayerConstraintTest {

    private static Stream<Constraint> constraints() {
        final List<Layer> layers = List.of(oneOf("a"), oneOf("b", "c", "d"), oneOf("e"));
        return Stream.of(new LayeringConstraint("t", layers), new DirectLayeringConstraint("t", layers));
    }

    @ParameterizedTest
    @MethodSource("constraints")
    void dependenciesIntoAMultiElementLayerShouldBeOk(final Constraint constraint) {
        assertThat(constraint.violations(new MockSlicingSource("t", d("a", "b"), d("a", "d")))).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("constraints")
    void dependenciesFromAMultiElementLayerShouldBeOk(final Constraint constraint) {
        assertThat(constraint.violations(new MockSlicingSource("t", d("b", "e"), d("d", "e")))).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("constraints")
    void dependenciesWithinAMultiElementLayerShouldNotBeOk(final Constraint constraint) {
        assertThat(dependenciesIn(constraint.violations(new MockSlicingSource("t", d("b", "c"), d("b", "d")))))
                .containsOnly(d("b", "c"), d("b", "d"));
    }

    @ParameterizedTest
    @MethodSource("constraints")
    void inverseDependenciesWithinAMultiElementLayerShouldNotBeOk(final Constraint constraint) {
        assertThat(dependenciesIn(constraint.violations(new MockSlicingSource("t", d("c", "b"), d("d", "b")))))
                .containsOnly(d("c", "b"), d("d", "b"));
    }
}
