package de.obqo.decycle.slicer;

import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;

import org.junit.jupiter.api.Test;

class CombinedSlicerTest {

    private Node n(final String s) {
        return SimpleNode.simpleNode(s, s);
    }

    @Test
    void shouldApplySingleSlicer() {
        final var slicer = new CombinedSlicer(ListCategorizer.of(n("a"), n("b")));

        assertThat(slicer.apply(n("a"))).isEqualTo(n("b"));
        assertThat(slicer.apply(n("c"))).isEqualTo(n("c"));
    }

    @Test
    void shouldIgnoreOtherSlicersIfTheFirstOneIsApplied() {
        final var slicer = new CombinedSlicer(ListCategorizer.of(n("a"), n("b")),
                ListCategorizer.of(n("a"), n("c"), n("b"), n("c")));

        assertThat(slicer.apply(n("a"))).isEqualTo(n("b"));
    }

    @Test
    void shouldApplyOtherSlicerIfTheFirstOneDoesntMatch() {
        final var slicer = new CombinedSlicer(ListCategorizer.of(n("a"), n("b")), ListCategorizer.of(n("c"), n("d")));

        assertThat(slicer.apply(n("c"))).isEqualTo(n("d"));
    }
}
