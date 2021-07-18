package de.obqo.decycle.check;

import static de.obqo.decycle.check.SimpleDependency.d;
import static de.obqo.decycle.check.SimpleDependency.dependenciesIn;
import static de.obqo.decycle.model.Node.classNode;
import static de.obqo.decycle.slicer.ParallelCategorizer.parallel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.iterable;

import de.obqo.decycle.check.Constraint.Violation;
import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.NodeFilter;
import de.obqo.decycle.slicer.IgnoredDependenciesFilter;
import de.obqo.decycle.slicer.IgnoredDependency;
import de.obqo.decycle.slicer.PackageCategorizer;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class CycleFreeTest {

    private Node n(final String s) {
        return Node.sliceNode(s, s);
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

        final List<Violation> violations = this.cycleFree.violations(g);
        assertThat(violations).hasSize(1).extracting(Violation::getName).containsOnly("cycle");
        assertThat(dependenciesIn(violations)).containsOnly(
                d("de.p1", "de.p2"),
                d("de.p2", "de.p3"),
                d("de.p3", "de.p1")
        );
    }

    @Test
    void shouldDetectMultipleCycles() {
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("de.p1.A1"), classNode("de.p2.B2"));
        g.connect(classNode("de.p2.B1"), classNode("de.p1.A2"));
        g.connect(classNode("de.p3.C1"), classNode("de.p4.D2"));
        g.connect(classNode("de.p4.D1"), classNode("de.p3.C2"));

        final List<Violation> violations = this.cycleFree.violations(g);
        assertThat(violations).hasSize(2).extracting(Violation::getName)
                .containsExactly("cycle (1)", "cycle (2)");
        assertThat(dependenciesIn(violations)).containsOnly(
                d("de.p1", "de.p2"),
                d("de.p2", "de.p1"),
                d("de.p3", "de.p4"),
                d("de.p4", "de.p3")
        );
    }

    @Test
    void shouldDetectCycleWithCombinedSlices() {
        final var g = new Graph(parallel(new PackageCategorizer(), __ -> Set.of(Node.sliceNode("tld", "de"))));
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
        final var g = new Graph(new PackageCategorizer(), NodeFilter.ALL,
                new IgnoredDependenciesFilter(Set.of(new IgnoredDependency("de.p3.*", "de.p1.*"))));
        g.connect(classNode("de.p1.A1"), classNode("de.p2.B2"));
        g.connect(classNode("de.p2.B1"), classNode("de.p3.C2"));
        g.connect(classNode("de.p3.C1"), classNode("de.p1.A2"));

        assertThat(dependenciesIn(this.cycleFree.violations(g))).isEmpty();
    }

    @Test
    void shouldDetectViolatingEdgesWithinACycle() {
        final var g = new Graph(new PackageCategorizer());
        // three dependencies from de.p1 to de.p2
        g.connect(classNode("de.p1.A1"), classNode("de.p2.B1"));
        g.connect(classNode("de.p1.A1"), classNode("de.p2.B2"));
        g.connect(classNode("de.p1.A2"), classNode("de.p2.B2"));
        // one dependency from de.p2 to de.p3 -> will be considered violating
        g.connect(classNode("de.p2.B1"), classNode("de.p3.C2"));
        // two dependencies from de.p3 to de.p1
        g.connect(classNode("de.p3.C1"), classNode("de.p1.A2"));
        g.connect(classNode("de.p3.C2"), classNode("de.p1.A1"));

        final List<Violation> violations = this.cycleFree.violations(g);
        assertThat(violations).singleElement().extracting(Violation::getDependencies, iterable(Edge.class))
                .hasSize(3)
                .anySatisfy(edge -> {
                    assertThat(new SimpleDependency(edge)).isEqualTo(d("de.p1", "de.p2"));
                    assertThat(edge.isViolating()).isFalse();
                })
                .anySatisfy(e -> {
                    assertThat(new SimpleDependency(e)).isEqualTo(d("de.p2", "de.p3"));
                    assertThat(e.isViolating()).isTrue();
                })
                .anySatisfy(e -> {
                    assertThat(new SimpleDependency(e)).isEqualTo(d("de.p3", "de.p1"));
                    assertThat(e.isViolating()).isFalse();
                });
    }
}
