package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.Node.classNode;
import static de.obqo.decycle.model.Node.packageNode;
import static de.obqo.decycle.model.Node.sliceNode;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PatternMatchingNodeFilterTest {

    @Test
    void shouldMatchTheNameOfASimpleNode() {
        final var matcher = new PatternMatchingNodeFilter("x.*y.abc.**");

        assertThat(matcher.test(packageNode("x.y.abc.x"))).isTrue();
        assertThat(matcher.test(classNode("x.blay.abc.x.yz.D"))).isTrue();
        assertThat(matcher.test(sliceNode("other", "x.x.y.abc.x"))).isFalse();
    }
}
