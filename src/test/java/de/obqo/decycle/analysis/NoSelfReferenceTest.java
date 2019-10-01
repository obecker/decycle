package de.obqo.decycle.analysis;

import static de.obqo.decycle.model.SimpleNode.simpleNode;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.slicer.ListCategorizer;

class NoSelfReferenceTest {

    private Node n(final String s) {
        return simpleNode(s, s);
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
        assertThat(new NoSelfReference(ListCategorizer.of(n("a"), n("b"), n("c"), n("d"))).test(n("a"), n("c"))).isFalse();
    }

    @Test
    void shouldBeFalseFirstNodeBelongsToTheCategoriesOfTheSecondOne() {
        assertThat(new NoSelfReference(ListCategorizer.of(n("a"), n("b"), n("c"), n("d"))).test(n("c"), n("a"))).isFalse();
    }

    @Test
    void shouldBeTrueForUnrelatedNodesWithCategories() {
        assertThat(new NoSelfReference(ListCategorizer.of(n("a"), n("b"), n("c"), n("d"))).test(n("a"), n("x"))).isTrue();
    }

    @Test
    void shouldBeTrueForUnrelatedNodesWithCommonCategory() {
        assertThat(new NoSelfReference(ListCategorizer.of(n("a"), n("x")).combine(ListCategorizer.of(n("b"), n("x")))).test(n("a"), n("b"))).isTrue();
    }
}
