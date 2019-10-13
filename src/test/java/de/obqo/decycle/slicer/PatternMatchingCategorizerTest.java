package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.SimpleNode.classNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;

import org.junit.jupiter.api.Test;

class PatternMatchingCategorizerTest {

    private Node n(final String s) {
        return SimpleNode.simpleNode(s, s);
    }

    @Test
    void shouldNotMatchArbitraryObject() {
        final var categorizer = new PatternMatchingCategorizer("type", "(some.package.Class)");
        final var node = n("x");

        assertThat(categorizer.apply(node)).isEqualTo(node);
    }

    @Test
    void shouldCategorizeMatchedGroup() {
        final var categorizer = new PatternMatchingCategorizer("type", "some.(package).Class");

        assertThat(categorizer.apply(classNode("some.package.Class")))
                .isEqualTo(SimpleNode.simpleNode("package", "type"));
    }
}
