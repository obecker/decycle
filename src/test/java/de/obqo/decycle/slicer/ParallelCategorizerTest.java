package de.obqo.decycle.slicer;

import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;

import org.junit.jupiter.api.Test;

class ParallelCategorizerTest {

    private Node n(final String s) {
        return Node.sliceNode(s, s);
    }

    @Test
    void parallelCombinationOfZeroCategorizersShouldReturnNothing() {
        assertThat(new ParallelCategorizer().apply(n("x"))).isEmpty();
    }

    @Test
    void parallelCombinationOfSingleCategorizerShouldApplyThisCategorizer() {
        assertThat(new ParallelCategorizer(ListCategorizer.of(n("a"), n("b"), n("c"))).apply(n("b")))
                .containsOnly(n("c"));
    }

    @Test
    void parallelCombinationOfMultipleCategorizersShouldReturnTheResultOfAllCategorizers() {
        assertThat(new ParallelCategorizer(
                ListCategorizer.of(n("a"), n("b")),
                ListCategorizer.of(n("a"), n("c")),
                ListCategorizer.of(n("a"), n("d"))
        ).apply(n("a")))
                .containsOnly(n("b"), n("c"), n("d"));
    }
}
