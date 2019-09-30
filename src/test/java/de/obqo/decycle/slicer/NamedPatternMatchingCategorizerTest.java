package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.SimpleNode.classNode;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.obqo.decycle.model.SimpleNode;

class NamedPatternMatchingCategorizerTest {

    @Test
    void shouldReturnNodeOfTypeWithNameOfMatch() {
        final var categorizer = new NamedPatternMatchingCategorizer("type", "(some.package.Class)", "name");

        assertThat(categorizer.apply(classNode("some.package.Class"))).isEqualTo(SimpleNode.simpleNode("type", "name"));
    }

    @Test
    void shouldReturnInputIfNotMatched() {
        final var categorizer = new NamedPatternMatchingCategorizer("type", "(y)", "name");
        final var x = classNode("x");

        assertThat(categorizer.apply(x)).isEqualTo(x);
    }
}
