package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.Node.classNode;
import static de.obqo.decycle.model.Node.sliceNode;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PatternMatchingCategorizerTest {

    @Test
    void shouldNotMatchArbitraryNode() {
        final var categorizer = new PatternMatchingCategorizer("type", "some.package.Class");
        final var node = classNode("x");

        assertThat(categorizer.apply(node)).isEmpty();
    }

    @Test
    void shouldCategorizeMatchedGroup() {
        final var categorizer = new PatternMatchingCategorizer("type", "some.{*}.Class");

        assertThat(categorizer.apply(classNode("some.package.Class")))
                .containsOnly(sliceNode("type", "package"));
    }
}
