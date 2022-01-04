package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.Node.classNode;
import static de.obqo.decycle.model.Node.sliceNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;

import org.junit.jupiter.api.Test;

class NamedPatternMatchingCategorizerTest {

    @Test
    void shouldReturnNodeOfTypeWithNameOfMatch() {
        final var categorizer = new NamedPatternMatchingCategorizer("type", "some.package.*", "name");
        final Node node = classNode("some.package.Class");

        assertThat(categorizer.apply(node)).containsOnly(sliceNode("type", "name"));
    }

    @Test
    void shouldReturnNothingIfNotMatched() {
        final var categorizer = new NamedPatternMatchingCategorizer("type", "y", "name");
        final var node = classNode("x");

        assertThat(categorizer.apply(node)).isEmpty();
    }
}
