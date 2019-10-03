package de.obqo.decycle.graph;

import static de.obqo.decycle.model.SimpleNode.CLASS;
import static de.obqo.decycle.model.SimpleNode.PACKAGE;
import static de.obqo.decycle.model.SimpleNode.classNode;
import static de.obqo.decycle.model.SimpleNode.packageNode;
import static de.obqo.decycle.model.SimpleNode.simpleNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.slicer.InternalClassCategorizer;
import de.obqo.decycle.slicer.MultiCategorizer;
import de.obqo.decycle.slicer.PackageCategorizer;

import org.junit.jupiter.api.Test;

class GraphSliceTest {

    @Test
    void packageSliceOfAGraphWithoutPackageShouldBeEmpty() {
        final var g = new Graph();
        g.add(simpleNode("x", "x"));

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
                new Edge(packageNode("p.one"), packageNode("p.two"), Edge.EdgeLabel.REFERENCES));
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
        final var g = new Graph(new MultiCategorizer(new InternalClassCategorizer(), new PackageCategorizer()));
        g.connect(classNode("p.one.Class$Inner"), classNode("p.two.Class$Inner"));

        assertThat(g.slice(PACKAGE).edges()).containsOnly(
                new Edge(packageNode("p.one"), packageNode("p.two"), Edge.EdgeLabel.REFERENCES));
    }

    @Test
    void shouldReturnSetOfContainedNodeTypesAsSlices() {
        final var g = new Graph(new PackageCategorizer());
        g.connect(classNode("package.one.class"), classNode("package.two.class"));
        g.add(simpleNode("x", "x"));

        assertThat(g.slices()).containsOnly(CLASS, PACKAGE, "x");
    }
}
