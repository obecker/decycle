package de.obqo.decycle.slicer;

import static de.obqo.decycle.slicer.MultiCategorizer.combine;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;

import org.junit.jupiter.api.Test;

class MultiCategorizerTest {

    private Node n(final String s) {
        return SimpleNode.simpleNode(s, s);
    }

    private final Categorizer id = x -> x;

    @Test
    void combineWithASingleIdentityCategorizerShouldReturnTheArgument() {
        final Categorizer cat = combine(this.id);
        final Node x = n("x");

        assertThat(cat.apply(x)).isEqualTo(x);
    }

    @Test
    void combineWithASingleCategorizerShouldReturnCategoryOfTheArgument() {
        final Categorizer cat = combine(ListCategorizer.of(n("a"), n("b"), n("c")));

        assertThat(cat.apply(n("b"))).isEqualTo(n("c"));
    }

    @Test
    void shouldCombine() {
        final Categorizer cat =
                combine(ListCategorizer.of(n("a"), n("b")), ListCategorizer.of(n("b"), n("c")));

        assertThat(cat.apply(n("b"))).isEqualTo(n("c"));
    }
}
