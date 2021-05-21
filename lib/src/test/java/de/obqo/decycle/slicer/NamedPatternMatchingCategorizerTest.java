package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.Node.classNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;

import org.junit.jupiter.api.Test;

class NamedPatternMatchingCategorizerTest {

    @Test
    void shouldReturnNodeOfTypeWithNameOfMatch() {
        final var categorizer = new NamedPatternMatchingCategorizer("type", "name", "(some.package.Class)");

        assertThat(categorizer.apply(classNode("some.package.Class"))).containsOnly(Node.sliceNode("type", "name"));
    }

    @Test
    void shouldReturnNothingIfNotMatched() {
        final var categorizer = new NamedPatternMatchingCategorizer("type", "name", "(y)");
        final var x = classNode("x");

        assertThat(categorizer.apply(x)).isEmpty();
    }
}
