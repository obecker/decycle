package de.obqo.decycle.graph;

import static de.obqo.decycle.model.Node.CLASS;
import static de.obqo.decycle.model.Node.PACKAGE;
import static de.obqo.decycle.model.Node.classNode;
import static de.obqo.decycle.model.Node.packageNode;
import static de.obqo.decycle.model.Node.sliceNode;
import static de.obqo.decycle.slicer.ParallelCategorizer.parallel;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.NodeFilter;
import de.obqo.decycle.slicer.IgnoredDependenciesFilter;
import de.obqo.decycle.slicer.IgnoredDependency;
import de.obqo.decycle.slicer.PackageCategorizer;
import de.obqo.decycle.slicer.PatternMatchingCategorizer;

import java.util.Set;

import org.junit.jupiter.api.Test;

class GraphSlicingTest {

    @Test
    void packageSliceOfAGraphWithoutPackageShouldBeEmpty() {
        final var g = new Graph();
        g.add(sliceNode("x", "x"));

        assertThat(g.slicing(PACKAGE).nodes()).isEmpty();
    }

    @Test
    void packageSliceOfAGraphWithSomeNodesInASinglePackageShouldBeThatPackage() {
        final var g = new Graph(new PackageCategorizer());
        g.add(classNode("p.C"));

        assertThat(g.slicing(PACKAGE).nodes()).containsOnly(packageNode("p"));
    }

    @Test
    void packageSliceOfAGraphWithTwoConnectedClassNodesShouldBeAGraphWithTwoConnectedPackages() {
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("p.one.Class"), classNode("p.two.Class"));

        assertThat(g.slicing(PACKAGE).edges()).containsOnly(
                Edge.references(packageNode("p.one"), packageNode("p.two")));
    }

    @Test
    void graphForNonExistingSliceShouldBeEmpty() {
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("p.one.Class"), classNode("p.two.Class"));

        assertThat(g.slicing("no such type").nodes()).isEmpty();
    }

    @Test
    void packageSliceOfAnInnerClassShouldBeItsPackage() {
        // since the slice node will appear anyway we use an edge between to inner classes, to test that they get
        // projected on the correct slice
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("p.one.Class$Inner"), classNode("p.two.Class$Inner"));

        assertThat(g.slicing(PACKAGE).edges()).containsOnly(
                Edge.references(packageNode("p.one"), packageNode("p.two")));
    }

    @Test
    void shouldReturnSetOfContainedNodeTypesAsSlices() {
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("package.one.class"), classNode("package.two.class"));
        g.add(sliceNode("x", "x"));

        assertThat(g.sliceTypes()).containsOnly(CLASS, PACKAGE, "x");
    }

    @Test
    void shouldFindContainingClassEdges() {
        // given
        final var SLICE = "Slice";
        final var cat = parallel(new PackageCategorizer(), new PatternMatchingCategorizer(SLICE, "package.(*).**"));
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

        // given PACKAGE slice
        final Slicing packages = g.slicing(PACKAGE);
        final Node packageOne = packageNode("package.one");
        final Node packageTwo = packageNode("package.two");
        final Node packageThree = packageNode("package.three");

        // when
        final Set<Edge> packageEdgesOneTwo = g.containingClassEdges(
                packages.edgeConnecting(packageOne, packageTwo).orElseThrow());

        // then
        assertThat(packageEdgesOneTwo).containsOnly(Edge.references(classOne, classTwo));

        // when
        final Set<Edge> packageEdgesOneThree = g.containingClassEdges(
                packages.edgeConnecting(packageOne, packageThree).orElseThrow());

        // then
        assertThat(packageEdgesOneThree).containsOnly(Edge.references(classOneInner, classThreeB));

        // given SLICE edge
        final Slicing slices = g.slicing(SLICE);
        final Edge sliceEdge = slices.edgeConnecting(sliceNode(SLICE, "two"), sliceNode(SLICE, "three")).orElseThrow();
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
        final var cat = parallel(new PackageCategorizer(), new PatternMatchingCategorizer(SLICE, "package.(*).**"));
        final var ignoredEdgesFilter = new IgnoredDependenciesFilter(
                Set.of(new IgnoredDependency("package.a.A1", "package.b.B1"),
                        new IgnoredDependency("package.a.**", "package.c.**")));
        final var g = new Graph(cat, NodeFilter.ALL, ignoredEdgesFilter);

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
        final Slicing classes = g.slicing(CLASS);

        // then
        assertThat(classes.edgeConnecting(a1, b1)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isTrue());
        assertThat(classes.edgeConnecting(a2, b2)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isFalse());
        assertThat(classes.edgeConnecting(a1, c1)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isTrue());
        assertThat(classes.edgeConnecting(a2, c2)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isTrue());

        // when
        final Slicing slices = g.slicing(SLICE);
        final Node a = sliceNode(SLICE, "a");
        final Node b = sliceNode(SLICE, "b");
        final Node c = sliceNode(SLICE, "c");

        // then
        assertThat(slices.edgeConnecting(a, b)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isFalse());
        assertThat(slices.edgeConnecting(a, c)).hasValueSatisfying(edge -> assertThat(edge.isIgnored()).isTrue());
    }

    @Test
    void shouldComputeWeightForSliceEdges() {
        // given
        final var SLICE = "Slice";
        final var cat = parallel(new PackageCategorizer(), new PatternMatchingCategorizer(SLICE, "package.(*).**"));
        final var g = new Graph(cat, NodeFilter.ALL);

        final var a1 = classNode("package.a.A1");
        final var a2 = classNode("package.a.A2");
        final var b1 = classNode("package.b.B1");
        final var b2 = classNode("package.b.B2");
        final var bsub1 = classNode("package.b.sub.B1");
        final var c1 = classNode("package.c.C1");
        final var c2 = classNode("package.c.C2");
        // a -> b
        g.connect(a1, b1);
        g.connect(a2, b2);
        g.connect(a1, b2);
        g.connect(a1, bsub1);
        g.connect(a2, bsub1);
        // a -> c
        g.connect(a1, c1);
        g.connect(a1, c1); // duplicate, not counted
        g.connect(a2, c2);

        // when
        final Slicing slices = g.slicing(SLICE);
        final Node a = sliceNode(SLICE, "a");
        final Node b = sliceNode(SLICE, "b");
        final Node c = sliceNode(SLICE, "c");

        // then
        assertThat(slices.edgeConnecting(a, b)).hasValueSatisfying(edge -> assertThat(edge.getWeight()).isEqualTo(5));
        assertThat(slices.edgeConnecting(a, c)).hasValueSatisfying(edge -> assertThat(edge.getWeight()).isEqualTo(2));
    }
}
