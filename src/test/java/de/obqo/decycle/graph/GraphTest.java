package de.obqo.decycle.graph;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;
import de.obqo.decycle.slicer.ListCategory;

class GraphTest {

    private Node n(String name) {
        return SimpleNode.classNode(name);
    }

    @Test
    void newGraphShouldContainNoTopNodes() {
        var g = new Graph();

        assertThat(g.topNodes()).isEmpty();
    }

    @Test
    void topNodesShouldContainAddedNodes() {
        var g = new Graph();
        var node = n("a");
        g.add(node);

        assertThat(g.topNodes()).contains(node);
    }

    @Test
    void simpleNodeShouldHaveNoContent() {
        var g = new Graph();
        var node = n("a");
        g.add(node);

        assertThat(g.contentsOf(node)).isEmpty();
    }

    @Test
    void shouldAddCategoryForANode() {
        var category = n("cat");
        var node = n("n");
        var g = new Graph(__ -> category, null, null);
        g.add(node);

        assertThat(g.topNodes()).contains(category);
    }

    @Test
    void categoryShouldContainNodesOfThatCategory() {
        var category = n("cat");
        var node = n("n");
        var g = new Graph(__ -> category, null, null);
        g.add(node);

        assertThat(g.contentsOf(category)).contains(node);
    }

    @Test
    void contentsOfNonExistingCategoryShouldBeEmpty() {
        var g = new Graph();
        var category = n("cat");

        assertThat(g.contentsOf(category)).isEmpty();
    }

    @Test
    void categoriesThatArePartOfOtherCategoriesShouldContainEachOther() {
        var topCategory = n("top");
        var subCategory = n("sub");
        var node = n("a");
        var g = new Graph(ListCategory.of(node, subCategory, topCategory));
        g.add(node);

        assertThat(g.topNodes()).containsOnly(topCategory);
        assertThat(g.contentsOf(topCategory)).containsOnly(subCategory);
        assertThat(g.contentsOf(subCategory)).containsOnly(node);
    }

    @Test
    void shouldContainNodesAfterEdgeWasAdded() {
        var g = new Graph();
        var a = n("a");
        var b = n("b");
        g.connect(a, b);

        assertThat(g.topNodes()).containsOnly(a, b);
        assertThat(g.connectionsOf(a)).containsOnly(b);
    }

    @Test
    void connectionsOfShouldReturnAllConnectedNodes() {
        var g = new Graph();
        var a = n("a");
        var b = n("b");
        var c = n("c");
        g.connect(a, b);
        g.connect(a, c);

        assertThat(g.connectionsOf(a)).containsOnly(b, c);
        assertThat(g.topNodes()).containsOnly(a, b, c);
    }

    @Test
    void simpleNodesShouldHaveNoConnections() {
        var g = new Graph();
        var a = n("a");
        g.add(a);

        assertThat(g.connectionsOf(a)).isEmpty();
    }

    @Test
    void allNodesOfAnEmptyGraphShouldBeEmpty() {
        var g = new Graph();

        assertThat(g.allNodes()).isEmpty();
    }

    @Test
    void allNodesInAGraphWithoutCategoriesShouldBeTheTopNodes() {
        var g = new Graph();
        g.add(n("a"));
        g.add(n("23"));

        assertThat(g.allNodes()).isEqualTo(g.topNodes());
        assertThat(g.allNodes()).isEqualTo(Set.of(n("a"), n("23")));
    }

    @Test
    void allNodesInAGraphWithCategoriesShouldContainTheNodesAndAllCategories() {
        var g = new Graph(ListCategory.of(n("a"), n("b"), n("c")).compose(ListCategory.of(n("23"), n("42"), n("c"))));
        g.add(n("a"));
        g.add(n("23"));

        assertThat(g.allNodes()).isEqualTo(Set.of(n("a"), n("b"), n("c"), n("23"), n("42")));
        assertThat(g.topNodes()).containsOnly(n("c"));
    }

    @Test
    void categoriesShouldNotBeFiltered() {
        var g = new Graph(ListCategory.of(n("a"), n("b")), x -> x.equals(n("a")));
        g.add(n("a"));

        assertThat(g.topNodes()).containsOnly(n("b"));
    }
}
