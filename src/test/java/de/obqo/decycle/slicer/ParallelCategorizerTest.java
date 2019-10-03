package de.obqo.decycle.slicer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.ParentAwareNode;
import de.obqo.decycle.model.SimpleNode;

class ParallelCategorizerTest {

    private Node n(final String s) {
        return SimpleNode.simpleNode(s, s);
    }

    private final Categorizer id = n -> n;

    @Test
    void parallelCombinationOfZeroFunctionsShouldReturnIdentity() {
        assertThat(new ParallelCategorizer().apply(n("x"))).isEqualTo(n("x"));
    }

    @Test
    void parallelCombinationOfSingleIdentityFunctionShouldReturnTheArgument() {
        assertThat(new ParallelCategorizer(this.id).apply(n("x"))).isEqualTo(n("x"));
    }

    @Test
    void parallelCombinationOfSingleFunctionShouldReturnTheFunctionArgument() {
        assertThat(new ParallelCategorizer(ListCategorizer.of(n("a"), n("b"), n("c"))).apply(n("b")))
                .isEqualTo(n("c"));
    }

    @Test
    void parallelCombinationOfMultipleFunctionsShouldReturnTheResultOfAllFunctions() {
        assertThat(new ParallelCategorizer(ListCategorizer.of(n("a"), n("b")),
                ListCategorizer.of(n("a"), n("c")),
                ListCategorizer.of(n("a"), n("d"))).apply(n("a")))
                .isEqualTo(new ParentAwareNode(n("b"), n("c"), n("d")));
    }

    @Test
    void shouldStepThroughtTheElementsOfAPackageAwareNode() {
        assertThat(new ParallelCategorizer(__ -> n("dummy")).apply(new ParentAwareNode(n("a"), n("b"), n("c"))))
                .isEqualTo(new ParentAwareNode(n("b"), n("c")));
    }

    @Test
    void shouldStepThroughtTheElementsOfAPackageAwareNodeWithSingleNode() {
        assertThat(new ParallelCategorizer(__ -> n("dummy")).apply(new ParentAwareNode(n("a"))))
                .isEqualTo(new ParentAwareNode(n("a")));
    }
}
