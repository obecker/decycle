package de.obqo.decycle.configuration;

import static de.obqo.decycle.model.SimpleNode.classNode;
import static de.obqo.decycle.model.SimpleNode.packageNode;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.obqo.decycle.graph.Edge;
import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;
import de.obqo.decycle.slicer.PackageCategorizer;

class CycleFreeTest {

    private Node n(final String s) {
        return SimpleNode.simpleNode(s, s);
    }

    private Edge e(final String from, final String to) {
        return new Edge(packageNode(from), packageNode(to), Edge.EdgeLabel.REFERENCES);
    }

    private Set<Edge> dependenciesIn(final List<Constraint.Violation> violations) {
        return violations.stream().flatMap(v -> v.getDependencies().stream()).collect(Collectors.toSet());
    }

    private final CycleFree cycleFree = new CycleFree();

    @Test
    void emptyGraphShouldHaveNoCycles() {
        final var g = new Graph();

        assertThat(this.cycleFree.violations(g)).isEmpty();
    }

    @Test
    void graphWithTwoCyclicNodesWithoutSlicesShouldBeReportedCycleFree() {
        final var g = new Graph();
        g.connect(n("a"), n("b"));
        g.connect(n("b"), n("a"));

        assertThat(this.cycleFree.violations(g)).isEmpty();
    }

    @Test
    void graphWithCyclicDependenciesBetweenPackagesShouldBeReportedAsCyclic() {
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("de.p1.A1"), classNode("de.p2.B2"));
        g.connect(classNode("de.p2.B1"), classNode("de.p3.C2"));
        g.connect(classNode("de.p3.C1"), classNode("de.p1.A2"));

        assertThat(dependenciesIn(this.cycleFree.violations(g))).containsOnly(
                e("de.p1", "de.p2"),
                e("de.p2", "de.p3"),
                e("de.p3", "de.p1")
        );
    }
}
