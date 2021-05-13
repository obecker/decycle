package de.obqo.decycle.check;

import static de.obqo.decycle.check.Layer.anyOf;
import static de.obqo.decycle.check.MockSliceSource.d;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MultiElementLenientLayerConstraintTest {

    private static Stream<Constraint> constraints() {
        final List<Layer> layers = List.of(anyOf("a"), anyOf("b", "c", "d"), anyOf("e"));
        return Stream.of(new LayeringConstraint("t", layers), new DirectLayeringConstraint("t", layers));
    }

    @ParameterizedTest
    @MethodSource("constraints")
    void dependenciesIntoAMultiElementLayerShouldBeOk(final Constraint constraint) {
        assertThat(constraint.violations(new MockSliceSource("t", d("a", "b"), d("a", "d")))).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("constraints")
    void dependenciesFromAMultiElementLayerShouldBeOk(final Constraint constraint) {
        assertThat(constraint.violations(new MockSliceSource("t", d("b", "e"), d("d", "e")))).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("constraints")
    void dependenciesWithinAMultiElementLayerShouldBeOk(final Constraint constraint) {
        assertThat(constraint.violations(new MockSliceSource("t", d("b", "c"), d("b", "d")))).isEmpty();
    }
}
