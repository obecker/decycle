package de.obqo.decycle.analysis;

import static de.obqo.decycle.model.Node.sliceNode;
import static de.obqo.decycle.slicer.MultiCategorizer.combine;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.slicer.ListCategorizer;

import org.junit.jupiter.api.Test;

class NoSelfReferenceTest {

    private Node n(final String s) {
        return sliceNode(s, s);
    }

    @Test
    void shouldBeTrueForUnrelatedObjects() {
        assertThat(new NoSelfReference().test(n("a"), n("b"))).isTrue();
    }

    @Test
    void shouldBeFalseForEqualObjects() {
        assertThat(new NoSelfReference().test(n("a"), n("a"))).isFalse();
    }

    @Test
    void shouldBeFalseSecondNodeBelongsToTheCategoriesOfTheFirstOne() {
        assertThat(new NoSelfReference(ListCategorizer.of(n("a"), n("b"), n("c"), n("d"))).test(n("a"), n("c")))
                .isFalse();
    }

    @Test
    void shouldBeFalseFirstNodeBelongsToTheCategoriesOfTheSecondOne() {
        assertThat(new NoSelfReference(ListCategorizer.of(n("a"), n("b"), n("c"), n("d"))).test(n("c"), n("a")))
                .isFalse();
    }

    @Test
    void shouldBeTrueForUnrelatedNodesWithCategories() {
        assertThat(new NoSelfReference(ListCategorizer.of(n("a"), n("b"), n("c"), n("d"))).test(n("a"), n("x")))
                .isTrue();
    }

    @Test
    void shouldBeTrueForUnrelatedNodesWithCommonCategory() {
        assertThat(new NoSelfReference(combine(ListCategorizer.of(n("a"), n("x")), ListCategorizer.of(n("b"), n("x"))))
                .test(n("a"), n("b"))).isTrue();
    }
}
