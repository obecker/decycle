package de.obqo.decycle.graph;

import static de.obqo.decycle.model.Node.PACKAGE;
import static de.obqo.decycle.model.Node.packageNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import org.junit.jupiter.api.Test;

class TopologicalTest {

    private Node n(final String name) {
        return packageNode(name);
    }

    private Edge e(final String from, final String to) {
        return e(n(to), n(from));
    }

    private Edge e(final Node from, final Node to) {
        return Edge.references(from, to);
    }

    @Test
    void shouldFindOrderInLinearGraph() {
        final var g = new Graph();
        g.connect(n("a"), n("b"));
        g.connect(n("c"), n("d"));
        g.connect(n("b"), n("c"));

        final Topological topological = new Topological(g.slicing(PACKAGE));

        assertThat(topological.order()).extracting(Node::getName).containsExactly("a", "b", "c", "d");
    }

    @Test
    void shouldFindOrderInNonLinearGraph() {
        final var g = new Graph();
        g.connect(n("a"), n("c"));
        g.connect(n("c"), n("e"));
        g.connect(n("a"), n("b"));
        g.connect(n("b"), n("e"));
        g.connect(n("b"), n("d"));
        g.connect(n("c"), n("d"));

        final Topological topological = new Topological(g.slicing(PACKAGE));

        assertThat(topological.order()).extracting(Node::getName).containsExactly("a", "b", "c", "d", "e");
    }
}
