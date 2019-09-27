package de.obqo.decycle.graph;

import static de.obqo.decycle.model.SimpleNode.classNode;
import static de.obqo.decycle.model.SimpleNode.packageNode;
import static de.obqo.decycle.model.SimpleNode.simpleNode;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.ParentAwareNode;
import de.obqo.decycle.model.SimpleNode;

class SliceNodeFinderTest {

    private Node n(final String s) {
        return simpleNode(s, s);
    }

    private MutableNetwork<Node, Graph.Edge> graph() {
        return NetworkBuilder.directed().build();
    }

    private MutableNetwork<Node, Graph.Edge> graph(final Node n) {
        final MutableNetwork<Node, Graph.Edge> g = graph();
        g.addNode(n);
        return g;
    }

    private MutableNetwork<Node, Graph.Edge> graph(final Node from, final Node to, final Graph.EdgeLabel label) {
        final MutableNetwork<Node, Graph.Edge> g = graph();
        final Graph.Edge edge = new Graph.Edge(from, to, label);
        g.addEdge(from, to, edge);
        return g;
    }

    @Test
    void shouldNotBeDefinedForAnEmptyGraph() {
        final var finder = new SliceNodeFinder("x", graph());

        assertThat(finder.isDefinedAt(n("z"))).isFalse();
    }

    @Test
    void shouldReturnTheNodeForASliceNode() {
        final var p = packageNode("p");
        final var g = graph(p);
        final var finder = new SliceNodeFinder(SimpleNode.PACKAGE, g);

        assertThat(finder.isDefinedAt(p)).isTrue();
        assertThat(finder.apply(p)).isEqualTo(p);
    }

    @Test
    void shouldNotBeDefinedIfNodeIsOfADifferentSlice() {
        final var p = packageNode("p");
        final var g = graph(p);
        final var finder = new SliceNodeFinder("does not exist", g);

        assertThat(finder.isDefinedAt(p)).isFalse();
    }

    @Test
    void shouldReturnTheContentOfAParentAwareNode() {
        final var p = packageNode("p");
        final var n = new ParentAwareNode(p);
        final var g = graph(n);
        final var finder = new SliceNodeFinder(SimpleNode.PACKAGE, g);

        assertThat(finder.isDefinedAt(n)).isTrue();
        assertThat(finder.apply(n)).isEqualTo(p);
    }

    @Test
    void shouldNotBeDefinedIfParentAwareNodeDoesNotContainTheCorrectSlice() {
        final var p = packageNode("p");
        final var n = new ParentAwareNode(p);
        final var g = graph(n);
        final var finder = new SliceNodeFinder("does not exist", g);

        assertThat(finder.isDefinedAt(n)).isFalse();
    }

    @Test
    void shouldReturnTheMatchingSliceFromTheContentOfAParentAwareNode() {
        final var p = packageNode("p");
        final var n = new ParentAwareNode(n("x"), p, n("y"));
        final var g = graph(n);
        final var finder = new SliceNodeFinder(SimpleNode.PACKAGE, g);

        assertThat(finder.isDefinedAt(n)).isTrue();
        assertThat(finder.apply(n)).isEqualTo(p);
    }

    @Test
    void traversesContainsRelationship() {
        final var p = packageNode("p");
        final var g = graph(p, n("x"), Graph.EdgeLabel.CONTAINS);
        final var finder = new SliceNodeFinder(SimpleNode.PACKAGE, g);

        assertThat(finder.isDefinedAt(n("x"))).isTrue();
        assertThat(finder.apply(n("x"))).isEqualTo(p);
    }

    @Test
    void shouldReturnCorrectElementFromSlicesRelationship() {
        final var p = packageNode("p");
        final var c = classNode("p.c");
        final var g = graph(p, c, Graph.EdgeLabel.CONTAINS);
        final var finder = new SliceNodeFinder(SimpleNode.PACKAGE, g);

        assertThat(finder.isDefinedAt(c)).isTrue();
        assertThat(finder.apply(c)).isEqualTo(p);
    }
}
