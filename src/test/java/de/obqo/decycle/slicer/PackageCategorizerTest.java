package de.obqo.decycle.slicer;

import static de.obqo.decycle.model.SimpleNode.classNode;
import static de.obqo.decycle.model.SimpleNode.packageNode;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.ParentAwareNode;
import de.obqo.decycle.model.SimpleNode;

class PackageCategorizerTest {

    private final Categorizer categorizer = new PackageCategorizer();

    @Test
    void shouldNotCategorizeArbitraryNodes() {
        final Node n = new ParentAwareNode();

        assertThat(this.categorizer.apply(n)).isEqualTo(n);
    }

    @Test
    void shouldNotCategorizeArbitraryTypes() {
        final Node n = SimpleNode.simpleNode("alpha.beta", "x");

        assertThat(this.categorizer.apply(n)).isEqualTo(n);
    }

    @Test
    void shouldCategorizeAClassNodeAsItsPackageNode() {
        assertThat(this.categorizer.apply(classNode("some.package.Class"))).isEqualTo(packageNode("some.package"));
    }
}
