package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.SimpleNode.classNode;
import static de.obqo.decycle.model.SimpleNode.packageNode;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.ParentAwareNode;
import de.obqo.decycle.model.SimpleNode;

import org.junit.jupiter.api.Test;

class PackageCategorizerTest {

    private final Categorizer categorizer = new PackageCategorizer();

    @Test
    void shouldNotCategorizeArbitraryNodes() {
        final Node n = new ParentAwareNode();

        assertThat(this.categorizer.apply(n)).isEqualTo(n);
    }

    @Test
    void shouldNotCategorizeArbitraryTypes() {
        final Node n = SimpleNode.simpleNode("x", "alpha.beta");

        assertThat(this.categorizer.apply(n)).isEqualTo(n);
    }

    @Test
    void shouldCategorizeAClassNodeAsItsPackageNode() {
        assertThat(this.categorizer.apply(classNode("some.package.Class"))).isEqualTo(packageNode("some.package"));
    }
}
