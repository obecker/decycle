package de.obqo.decycle.graph;

import static de.obqo.decycle.slicer.MultiCategorizer.combine;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.slicer.MockCategorizer;

import java.util.Set;

import org.junit.jupiter.api.Test;

class GraphTest {

    private Node n(final String name) {
        return Node.classNode(name);
    }

    @Test
    void newGraphShouldContainNoNodes() {
        final var g = new Graph();

        assertThat(g.allNodes()).isEmpty();
    }

    @Test
    void addNodeShouldBeContainedInGraph() {
        final var g = new Graph();
        final var node = n("a");
        g.add(node);

        assertThat(g.allNodes()).containsOnly(node);
    }

    @Test
    void simpleNodeShouldHaveNoContent() {
        final var g = new Graph();
        final var node = n("a");
        g.add(node);

        assertThat(g.contentsOf(node)).isEmpty();
    }

    @Test
    void categoryShouldBeAddedToGraph() {
        final var category = n("cat");
        final var node = n("n");
        final var g = new Graph(__ -> Set.of(category));
        g.add(node);

        assertThat(g.allNodes()).contains(node, category);
    }

    @Test
    void categoryShouldContainNodesOfThatCategory() {
        final var category = n("cat");
        final var node = n("n");
        final var g = new Graph(__ -> Set.of(category));
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
    void categoryNodesWillNotBeCategorized() {
        final var topCategory = n("top");
        final var subCategory = n("sub");
        final var node = n("a");
        final var g = new Graph(MockCategorizer.of(node, subCategory).with(subCategory, topCategory));
        g.add(node);

        assertThat(g.contentsOf(topCategory)).isEmpty();
        assertThat(g.contentsOf(subCategory)).containsOnly(node);
    }

    @Test
    void shouldContainNodesAfterEdgeWasAdded() {
        final var g = new Graph();
        final var a = n("a");
        final var b = n("b");
        g.connect(a, b);

        assertThat(g.allNodes()).containsOnly(a, b);
        assertThat(g.connectionsOf(a)).containsOnly(b);
    }

    @Test
    void shouldNotCategorizeNodesOfAddedEdge() {
        final var category = n("cat");
        final var a = n("a");
        final var b = n("b");
        final var g = new Graph(__ -> Set.of(category));
        g.connect(a, b);

        assertThat(g.allNodes()).containsOnly(a, b);
        assertThat(g.connectionsOf(category)).isEmpty();
    }

    @Test
    void connectionsOfShouldReturnAllConnectedNodes() {
        final var g = new Graph();
        final var a = n("a");
        final var b = n("b");
        final var c = n("c");
        g.connect(a, b);
        g.connect(a, c);

        assertThat(g.allNodes()).containsOnly(a, b, c);
        assertThat(g.connectionsOf(a)).containsOnly(b, c);
    }

    @Test
    void simpleNodesShouldHaveNoConnections() {
        final var g = new Graph();
        final var a = n("a");
        g.add(a);

        assertThat(g.connectionsOf(a)).isEmpty();
    }

    @Test
    void allNodesInAGraphWithCategoriesShouldContainTheNodesAndAllCategories() {
        final var g = new Graph(combine(
                MockCategorizer.of(n("a"), n("b"), n("c")), MockCategorizer.of(n("23"), n("42"), n("c"))));
        g.add(n("a"));
        g.add(n("23"));

        assertThat(g.allNodes()).isEqualTo(Set.of(n("a"), n("b"), n("c"), n("23"), n("42")));
    }

    @Test
    void categoriesShouldNotBeFiltered() {
        final var g = new Graph(MockCategorizer.of(n("a"), n("b")), x -> x.equals(n("a")));
        g.add(n("a"));

        assertThat(g.allNodes()).containsOnly(n("a"), n("b"));
    }
}
