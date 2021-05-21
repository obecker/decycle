package de.obqo.decycle.slicer;

import static de.obqo.decycle.slicer.MultiCategorizer.combine;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;

import org.junit.jupiter.api.Test;

class MultiCategorizerTest {

    private Node n(final String s) {
        return Node.sliceNode(s, s);
    }

    @Test
    void combineWithoutCategorizersShouldReturnNothing() {
        final Categorizer cat = combine();

        assertThat(cat.apply(n("x"))).isEmpty();
    }

    @Test
    void combineWithASingleCategorizerShouldReturnCategoryOfTheArgument() {
        final Categorizer cat = combine(ListCategorizer.of(n("a"), n("b"), n("c")));

        assertThat(cat.apply(n("b"))).containsOnly(n("c"));
    }

    @Test
    void shouldApplyOtherCategorizerIfTheFirstOneDoesntMatch() {
        final Categorizer cat =
                combine(ListCategorizer.of(n("a"), n("b")), ListCategorizer.of(n("b"), n("c")));

        assertThat(cat.apply(n("b"))).containsOnly(n("c"));
    }

    @Test
    void shouldIgnoreOtherCategorizerIfTheFirstOneIsApplied() {
        final Categorizer cat =
                combine(ListCategorizer.of(n("a"), n("b")), ListCategorizer.of(n("a"), n("c")));

        assertThat(cat.apply(n("a"))).containsOnly(n("b"));
    }
}
