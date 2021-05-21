package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.Node.classNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;

import org.junit.jupiter.api.Test;

class PatternMatchingCategorizerTest {

    private Node n(final String s) {
        return Node.sliceNode(s, s);
    }

    @Test
    void shouldNotMatchArbitraryNode() {
        final var categorizer = new PatternMatchingCategorizer("type", "(some.package.Class)");
        final var node = n("x");

        assertThat(categorizer.apply(node)).isEmpty();
    }

    @Test
    void shouldCategorizeMatchedGroup() {
        final var categorizer = new PatternMatchingCategorizer("type", "some.(*).Class");

        assertThat(categorizer.apply(classNode("some.package.Class")))
                .containsOnly(Node.sliceNode("type", "package"));
    }
}
