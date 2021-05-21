package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.Node.classNode;
import static de.obqo.decycle.model.Node.packageNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;

import org.junit.jupiter.api.Test;

class PackageCategorizerTest {

    private final Categorizer categorizer = new PackageCategorizer();

    @Test
    void shouldNotCategorizeArbitraryTypes() {
        final Node n = Node.sliceNode("x", "alpha.beta");

        assertThat(this.categorizer.apply(n)).isEmpty();
    }

    @Test
    void shouldCategorizeAClassNodeAsItsPackageNode() {
        assertThat(this.categorizer.apply(classNode("some.package.Class"))).containsOnly(packageNode("some.package"));
    }
}
