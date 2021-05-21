package de.obqo.decycle.check;

import static de.obqo.decycle.model.Node.classNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.check.Constraint.Dependency;
import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.EdgeFilter;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.NodeFilter;
import de.obqo.decycle.slicer.IgnoredDependenciesFilter;
import de.obqo.decycle.slicer.IgnoredDependency;
import de.obqo.decycle.slicer.PackageCategorizer;
import de.obqo.decycle.slicer.ParallelCategorizer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class CycleFreeTest {

    private Node n(final String s) {
        return Node.sliceNode(s, s);
    }

    private Dependency d(final String from, final String to) {
        return new Dependency(from, to);
    }

    private Set<Dependency> dependenciesIn(final List<Constraint.Violation> violations) {
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
                d("de.p1", "de.p2"),
                d("de.p2", "de.p3"),
                d("de.p3", "de.p1")
        );
    }

    @Test
    void shouldDetectCycleWithCombinedSlices() {
        final var g =
                new Graph(
                        new ParallelCategorizer(new PackageCategorizer(), __ -> Set.of(Node.sliceNode("tld", "de"))));
        g.connect(classNode("de.p1.A1"), classNode("de.p2.B2"));
        g.connect(classNode("de.p2.B1"), classNode("de.p3.C2"));
        g.connect(classNode("de.p3.C1"), classNode("de.p1.A2"));

        assertThat(dependenciesIn(this.cycleFree.violations(g))).containsOnly(
                d("de.p1", "de.p2"),
                d("de.p2", "de.p3"),
                d("de.p3", "de.p1")
        );
    }

    @Test
    void ignoredEdgesInCycleShouldBeReportedCycleFree() {
        final var g = new Graph(new PackageCategorizer(), NodeFilter.ALL, EdgeFilter.ALL,
                new IgnoredDependenciesFilter(Set.of(new IgnoredDependency("de.p3.*", "de.p1.*"))));
        g.connect(classNode("de.p1.A1"), classNode("de.p2.B2"));
        g.connect(classNode("de.p2.B1"), classNode("de.p3.C2"));
        g.connect(classNode("de.p3.C1"), classNode("de.p1.A2"));

        assertThat(dependenciesIn(this.cycleFree.violations(g))).isEmpty();
    }
}
