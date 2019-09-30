package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.SimpleNode.classNode;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InternalClassCategorizerTest {

    private final Categorizer categorizer = new InternalClassCategorizer();

    @Test
    void shouldCategorizeASimpleClassAsItself() {
        final var node = classNode("org.junit.Test");

        assertThat(this.categorizer.apply(node)).isEqualTo(node);
    }

    @Test
    void shouldCategorizeAnInnerClassAsTheOuterClass() {
        final var innerClassNode = classNode("java.util.Map$Entry");
        final var outerClassNode = classNode("java.util.Map");

        assertThat(this.categorizer.apply(innerClassNode)).isEqualTo(outerClassNode);
    }

    @Test
    void shouldCategorizeANestedInnerClassAsTheOutermostClass() {
        final var innerClassNode = classNode("de.obqo.decycle.Outer$Inner$Even$More$Inside");
        final var outerClassNode = classNode("de.obqo.decycle.Outer");

        assertThat(this.categorizer.apply(innerClassNode)).isEqualTo(outerClassNode);
    }
}
