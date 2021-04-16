package de.obqo.decycle.graph;

import static de.obqo.decycle.model.Node.classNode;
import static de.obqo.decycle.model.Node.packageNode;
import static de.obqo.decycle.model.Node.sliceNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import org.junit.jupiter.api.Test;

class SliceNodeFinderTest {

    private Node n(final String s) {
        return sliceNode(s, s);
    }

    private MutableNetwork<Node, Edge> graph() {
        return NetworkBuilder.directed().build();
    }

    private MutableNetwork<Node, Edge> graph(final Node n) {
        final MutableNetwork<Node, Edge> g = graph();
        g.addNode(n);
        return g;
    }

    private MutableNetwork<Node, Edge> graph(final Node from, final Node to) {
        final MutableNetwork<Node, Edge> g = graph();
        final Edge edge = Edge.contains(from, to);
        g.addEdge(from, to, edge);
        return g;
    }

    @Test
    void shouldFindNothingForAnEmptyGraph() {
        final var finder = new SliceNodeFinder("x", graph());

        assertThat(finder.find(n("z"))).isEmpty();
    }

    @Test
    void shouldReturnTheNodeForASliceNode() {
        final var p = packageNode("p");
        final var g = graph(p);
        final var finder = new SliceNodeFinder(Node.PACKAGE, g);

        assertThat(finder.find(p)).hasValue(p);
    }

    @Test
    void shouldFindNothingIfNodeIsOfADifferentSlice() {
        final var p = packageNode("p");
        final var g = graph(p);
        final var finder = new SliceNodeFinder("does not exist", g);

        assertThat(finder.find(p)).isEmpty();
    }

    @Test
    void shouldTraverseContainsRelationship() {
        final var p = packageNode("p");
        final var g = graph(p, n("x"));
        final var finder = new SliceNodeFinder(Node.PACKAGE, g);

        assertThat(finder.find(n("x"))).hasValue(p);
    }

    @Test
    void shouldReturnCorrectElementFromSlicesRelationship() {
        final var p = packageNode("p");
        final var c = classNode("p.c");
        final var g = graph(p, c);
        final var finder = new SliceNodeFinder(Node.PACKAGE, g);

        assertThat(finder.find(c)).hasValue(p);
    }
}
