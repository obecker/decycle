package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.SimpleNode.classNode;
import static de.obqo.decycle.model.SimpleNode.packageNode;
import static de.obqo.decycle.model.SimpleNode.simpleNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.ParentAwareNode;

import org.junit.jupiter.api.Test;

class PatternMatchingFilterTest {

    @Test
    void shouldMatchTheNameOfASimpleNode() {
        final var matcher = new PatternMatchingFilter("x.*y.abc.**");

        assertThat(matcher.test(packageNode("x.y.abc.x"))).isTrue();
        assertThat(matcher.test(classNode("x.blay.abc.x.yz.D"))).isTrue();
        assertThat(matcher.test(simpleNode("other", "x.x.y.abc.x"))).isFalse();
    }

    @Test
    void shouldMatchParentAwareNode() {
        final var matcher = new PatternMatchingFilter("x.*y.abc.**");

        assertThat(matcher.test(new ParentAwareNode(simpleNode("x", "y"), simpleNode("a", "b"))))
                .isTrue();
    }
}
