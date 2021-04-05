package de.obqo.decycle.graph;

import static de.obqo.decycle.model.Node.packageNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;

import java.util.Set;

import org.junit.jupiter.api.Test;

class StronglyConnectedComponentsFinderTest {

    private Node n(final String name) {
        return packageNode(name);
    }

    private Edge e(final String from, final String to) {
        return e(n(to), n(from));
    }

    private Edge e(final Node from, final Node to) {
        return new Edge(from, to, Edge.EdgeLabel.REFERENCES);
    }

    @Test
    void shouldFindNoComponentsForUnconnectedNodes() {
        final var g = new Graph();
        g.add(n("a"));
        g.add(n("b"));
        g.add(n("c"));

        final var components = StronglyConnectedComponentsFinder.findComponents(g.slice(Node.PACKAGE));

        assertThat(components).isEmpty();
    }

    @Test
    void shouldFindNoComponentsForACycleFreeGraph() {
        final var g = new Graph();
        g.connect(n("a"), n("b"));
        g.connect(n("b"), n("c"));
        g.connect(n("c"), n("d"));

        final var components = StronglyConnectedComponentsFinder.findComponents(g.slice(Node.PACKAGE));

        assertThat(components).isEmpty();
    }

    @Test
    void shouldFindCycleOfTwoNodes() {
        final var g = new Graph();
        g.connect(n("a"), n("b"));
        g.connect(n("b"), n("a"));

        final var components = StronglyConnectedComponentsFinder.findComponents(g.slice(Node.PACKAGE));

        assertThat(components).containsOnly(Set.of(e("a", "b"), e("b", "a")));
    }

    @Test
    void shouldFindMultipleComponents() {
        final Node a = n("a");
        final Node b = n("b");
        final Node c = n("c");
        final Node d = n("d");
        final Node x = n("x");
        final Node y = n("y");
        final Node z = n("z");

        final var g = new Graph();
        g.connect(a, b);
        g.connect(b, c);
        g.connect(c, d);
        g.connect(a, d);
        g.connect(c, a);
        g.connect(x, y);
        g.connect(y, z);
        g.connect(z, y);
        g.connect(b, z);

        final var components = StronglyConnectedComponentsFinder.findComponents(g.slice(Node.PACKAGE));

        assertThat(components).containsOnly(Set.of(e(a, b), e(b, c), e(c, a)), Set.of(e(y, z), e(z, y)));
    }

    @Test
    void shouldFindAllEdgesOfConnectedComponents() {
        final Node a = n("a");
        final Node b = n("b");
        final Node c = n("c");
        final Node d = n("d");
        final Node x = n("x");
        final Node y = n("y");
        final Node z = n("z");

        final var g = new Graph();
        g.connect(a, b);
        g.connect(b, c);
        g.connect(c, d);
        g.connect(d, a);
        g.connect(a, c);
        g.connect(d, b);

        g.connect(x, y);
        g.connect(y, z);
        g.connect(z, x);
        g.connect(y, x);

        final var components = StronglyConnectedComponentsFinder.findComponents(g.slice(Node.PACKAGE));

        assertThat(components).containsOnly(
                Set.of(e(a, b), e(b, c), e(c, d), e(d, a), e(a, c), e(d, b)),
                Set.of(e(x, y), e(y, z), e(z, x), e(y, x)));
    }
}
