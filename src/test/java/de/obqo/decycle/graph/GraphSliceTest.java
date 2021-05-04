package de.obqo.decycle.graph;

import static de.obqo.decycle.model.Node.CLASS;
import static de.obqo.decycle.model.Node.PACKAGE;
import static de.obqo.decycle.model.Node.classNode;
import static de.obqo.decycle.model.Node.packageNode;
import static de.obqo.decycle.model.Node.sliceNode;
import static de.obqo.decycle.slicer.MultiCategorizer.combine;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.EdgeFilter;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.NodeFilter;
import de.obqo.decycle.slicer.IgnoredDependenciesFilter;
import de.obqo.decycle.slicer.IgnoredDependency;
import de.obqo.decycle.slicer.InternalClassCategorizer;
import de.obqo.decycle.slicer.PackageCategorizer;
import de.obqo.decycle.slicer.ParallelCategorizer;
import de.obqo.decycle.slicer.PatternMatchingCategorizer;

import java.util.Set;

import com.google.common.graph.Network;

import org.junit.jupiter.api.Test;

class GraphSliceTest {

    @Test
    void packageSliceOfAGraphWithoutPackageShouldBeEmpty() {
        final var g = new Graph();
        g.add(sliceNode("x", "x"));

        assertThat(g.slice(PACKAGE).nodes()).isEmpty();
    }

    @Test
    void packageSliceOfAGraphWithSomeNodesInASinglePackageShouldBeThatPackage() {
        final var g = new Graph(new PackageCategorizer());
        g.add(classNode("p.C"));

        assertThat(g.slice(PACKAGE).nodes()).containsOnly(packageNode("p"));
    }

    @Test
    void packageSliceOfAGraphWithTwoConnectedClassNodesShouldBeAGraphWithTwoConnectedPackages() {
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("p.one.Class"), classNode("p.two.Class"));

        assertThat(g.slice(PACKAGE).edges()).containsOnly(
                Edge.references(packageNode("p.one"), packageNode("p.two")));
    }

    @Test
    void graphForNonExistingSliceShouldBeEmpty() {
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("p.one.Class"), classNode("p.two.Class"));

        assertThat(g.slice("no such type").nodes()).isEmpty();
    }

    @Test
    void packageSliceOfAnInnerClassShouldBeItsPackage() {
        // since the slice node will appear anyway we use an edge between to inner classes, to test that they get
        // projected on the correct slice
        final var g = new Graph(combine(new InternalClassCategorizer(), new PackageCategorizer()));
        g.connect(classNode("p.one.Class$Inner"), classNode("p.two.Class$Inner"));

        assertThat(g.slice(PACKAGE).edges()).containsOnly(
                Edge.references(packageNode("p.one"), packageNode("p.two")));
    }

    @Test
    void shouldReturnSetOfContainedNodeTypesAsSlices() {
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("package.one.class"), classNode("package.two.class"));
        g.add(sliceNode("x", "x"));

        assertThat(g.slices()).containsOnly(CLASS, PACKAGE, "x");
    }

    @Test
    void shouldFindContainingClassEdges() {
        // given
        final var SLICE = "Slice";
        final var cat = combine(new InternalClassCategorizer(),
                new ParallelCategorizer(new PackageCategorizer(),
                        new PatternMatchingCategorizer(SLICE, "package.(*).**")));
        final var g = new Graph(cat);
        final var classOne = classNode("package.one.class");
        final var classTwo = classNode("package.two.class");
        final var classThreeA = classNode("package.three.classA");
        final var classThreeB = classNode("package.three.classB");
        final var classOneInner = classNode("package.one.class$Inner");
        g.connect(classOne, classTwo);
        g.connect(classTwo, classThreeA);
        g.connect(classTwo, classThreeB);
        g.connect(classOneInner, classThreeB);

        // given PACKAGE edge
        final Network<Node, Edge> packages = g.slice(PACKAGE);

        // when
        final Set<Edge> packageEdgesOneTwo = g.containingClassEdges(
                packages.edgeConnectingOrNull(packageNode("package.one"), packageNode("package.two")));

        // then
        assertThat(packageEdgesOneTwo).containsOnly(Edge.references(classOne, classTwo));

        // when
        final Set<Edge> packageEdgesOneThree = g.containingClassEdges(
                packages.edgeConnectingOrNull(packageNode("package.one"), packageNode("package.three")));

        // then
        assertThat(packageEdgesOneThree).containsOnly(Edge.references(classOneInner, classThreeB));

        // given SLICE edge
        final Network<Node, Edge> slices = g.slice(SLICE);
        final Edge sliceEdge = slices.edgeConnectingOrNull(sliceNode(SLICE, "two"), sliceNode(SLICE, "three"));
        assertThat(sliceEdge).isNotNull();

        // when
        final Set<Edge> classEdgesFromSlice = g.containingClassEdges(sliceEdge);

        // then
        assertThat(classEdgesFromSlice)
                .containsOnly(Edge.references(classTwo, classThreeA), Edge.references(classTwo, classThreeB));
    }

    @Test
    void sliceEdgeShouldBeIgnoredIfAllClassEdgesAreIgnored() {
        // given
        final var SLICE = "Slice";
        final var cat = new ParallelCategorizer(new PackageCategorizer(),
                new PatternMatchingCategorizer(SLICE, "package.(*).**"));
        final var ignoredEdgesFilter = new IgnoredDependenciesFilter(
                Set.of(new IgnoredDependency("package.a.A1", "package.b.B1"),
                        new IgnoredDependency("package.a.**", "package.c.**")));
        final var g = new Graph(cat, NodeFilter.ALL, EdgeFilter.ALL, ignoredEdgesFilter);

        final var a1 = classNode("package.a.A1");
        final var a2 = classNode("package.a.A2");
        final var b1 = classNode("package.b.B1");
        final var b2 = classNode("package.b.B2");
        final var c1 = classNode("package.c.C1");
        final var c2 = classNode("package.c.C2");
        g.connect(a1, b1);
        g.connect(a2, b2);
        g.connect(a1, c1);
        g.connect(a2, c2);

        // when
        final Network<Node, Edge> classes = g.slice(CLASS);

        // then
        assertThat(classes.edgeConnecting(a1, b1)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isTrue());
        assertThat(classes.edgeConnecting(a2, b2)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isFalse());
        assertThat(classes.edgeConnecting(a1, c1)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isTrue());
        assertThat(classes.edgeConnecting(a2, c2)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isTrue());

        // when
        final Network<Node, Edge> slices = g.slice(SLICE);
        final Node a = sliceNode(SLICE, "a");
        final Node b = sliceNode(SLICE, "b");
        final Node c = sliceNode(SLICE, "c");

        // then
        assertThat(slices.edgeConnecting(a, b)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isFalse());
        assertThat(slices.edgeConnecting(a, c)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isTrue());
    }
}
