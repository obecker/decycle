package de.obqo.decycle.analysis;

import static de.obqo.decycle.model.SliceType.classType;
import static de.obqo.decycle.model.SliceType.packageType;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.EdgeFilter;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.NodeFilter;
import de.obqo.decycle.slicer.PackageCategorizer;

import org.junit.jupiter.api.Test;

class AnalyzerTest {

    @Test
    void shouldAnalyze() {
        // given
        final Analyzer analyzer = new Analyzer();

        // when
        final Graph graph = analyzer.analyze("build", new PackageCategorizer(), NodeFilter.ALL, EdgeFilter.NONE);

        // then
        assertThat(graph.sliceTypes()).containsOnly(classType(), packageType());
        assertThat(graph.allNodes()).filteredOn(node -> node.hasType(packageType()))
                .extracting(Node::getName)
                .isNotEmpty()
                .allMatch(name -> name.startsWith("de.obqo.decycle"));
    }
}
