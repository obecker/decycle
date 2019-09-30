package de.obqo.decycle.graph;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;
import de.obqo.decycle.slicer.ListCategorizer;

class GraphTest {

    private Node n(final String name) {
        return SimpleNode.classNode(name);
    }

    @Test
    void newGraphShouldContainNoTopNodes() {
        final var g = new Graph();

        assertThat(g.topNodes()).isEmpty();
    }

    @Test
    void topNodesShouldContainAddedNodes() {
        final var g = new Graph();
        final var node = n("a");
        g.add(node);

        assertThat(g.topNodes()).contains(node);
    }

    @Test
    void simpleNodeShouldHaveNoContent() {
        final var g = new Graph();
        final var node = n("a");
        g.add(node);

        assertThat(g.contentsOf(node)).isEmpty();
    }

    @Test
    void shouldAddCategoryForANode() {
        final var category = n("cat");
        final var node = n("n");
        final var g = new Graph(__ -> category, null, null);
        g.add(node);

        assertThat(g.topNodes()).contains(category);
    }

    @Test
    void categoryShouldContainNodesOfThatCategory() {
        final var category = n("cat");
        final var node = n("n");
        final var g = new Graph(__ -> category, null, null);
        g.add(node);

        assertThat(g.contentsOf(category)).contains(node);
    }

    @Test
    void contentsOfNonExistingCategoryShouldBeEmpty() {
        final var g = new Graph();
        final var category = n("cat");

        assertThat(g.contentsOf(category)).isEmpty();
    }

    @Test
    void categoriesThatArePartOfOtherCategoriesShouldContainEachOther() {
        final var topCategory = n("top");
        final var subCategory = n("sub");
        final var node = n("a");
        final var g = new Graph(ListCategorizer.of(node, subCategory, topCategory));
        g.add(node);

        assertThat(g.topNodes()).containsOnly(topCategory);
        assertThat(g.contentsOf(topCategory)).containsOnly(subCategory);
        assertThat(g.contentsOf(subCategory)).containsOnly(node);
    }

    @Test
    void shouldContainNodesAfterEdgeWasAdded() {
        final var g = new Graph();
        final var a = n("a");
        final var b = n("b");
        g.connect(a, b);

        assertThat(g.topNodes()).containsOnly(a, b);
        assertThat(g.connectionsOf(a)).containsOnly(b);
    }

    @Test
    void connectionsOfShouldReturnAllConnectedNodes() {
        final var g = new Graph();
        final var a = n("a");
        final var b = n("b");
        final var c = n("c");
        g.connect(a, b);
        g.connect(a, c);

        assertThat(g.connectionsOf(a)).containsOnly(b, c);
        assertThat(g.topNodes()).containsOnly(a, b, c);
    }

    @Test
    void simpleNodesShouldHaveNoConnections() {
        final var g = new Graph();
        final var a = n("a");
        g.add(a);

        assertThat(g.connectionsOf(a)).isEmpty();
    }

    @Test
    void allNodesOfAnEmptyGraphShouldBeEmpty() {
        final var g = new Graph();

        assertThat(g.allNodes()).isEmpty();
    }

    @Test
    void allNodesInAGraphWithoutCategoriesShouldBeTheTopNodes() {
        final var g = new Graph();
        g.add(n("a"));
        g.add(n("23"));

        assertThat(g.allNodes()).isEqualTo(g.topNodes());
        assertThat(g.allNodes()).isEqualTo(Set.of(n("a"), n("23")));
    }

    @Test
    void allNodesInAGraphWithCategoriesShouldContainTheNodesAndAllCategories() {
        final var g = new Graph(ListCategorizer.of(n("a"), n("b"), n("c")).combine(ListCategorizer.of(n("23"), n("42"), n("c"))));
        g.add(n("a"));
        g.add(n("23"));

        assertThat(g.allNodes()).isEqualTo(Set.of(n("a"), n("b"), n("c"), n("23"), n("42")));
        assertThat(g.topNodes()).containsOnly(n("c"));
    }

    @Test
    void categoriesShouldNotBeFiltered() {
        final var g = new Graph(ListCategorizer.of(n("a"), n("b")), x -> x.equals(n("a")));
        g.add(n("a"));

        assertThat(g.topNodes()).containsOnly(n("b"));
    }
}
