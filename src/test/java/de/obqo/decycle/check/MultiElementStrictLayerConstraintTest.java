package de.obqo.decycle.check;

import static de.obqo.decycle.check.MockSliceSource.d;
import static de.obqo.decycle.check.MockSliceSource.dependenciesIn;
import static de.obqo.decycle.model.SimpleNode.simpleNode;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MultiElementStrictLayerConstraintTest {

    private static Stream<Constraint> constraints() {
        final List<Layer> layers =
                List.of(new StrictLayer("a"), new StrictLayer("b", "c", "d"), new StrictLayer("e"));
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
    void dependenciesWithinAMultiElementLayerShouldNotBeOk(final Constraint constraint) {
        assertThat(dependenciesIn(constraint.violations(new MockSliceSource("t", d("b", "c"), d("b", "d")))))
                .containsOnly(d(simpleNode("b", "t"), simpleNode("c", "t")),
                        d(simpleNode("b", "t"), simpleNode("d", "t"))
                );
    }

    @ParameterizedTest
    @MethodSource("constraints")
    void inverseDependenciesWithinAMultiElementLayerShouldNotBeOk(final Constraint constraint) {
        assertThat(dependenciesIn(constraint.violations(new MockSliceSource("t", d("c", "b"), d("d", "b")))))
                .containsOnly(d(simpleNode("c", "t"), simpleNode("b", "t")),
                        d(simpleNode("d", "t"), simpleNode("b", "t"))
                );
    }

    @ParameterizedTest
    @MethodSource("constraints")
    void selfDependenciesWithinAMultiElementLayerShouldBeOk(final Constraint constraint) {
        // TODO requires self-loop in MockSliceSource
        assertThat(constraint.violations(new MockSliceSource("t", d("b", "b")))).isEmpty();
    }
}
