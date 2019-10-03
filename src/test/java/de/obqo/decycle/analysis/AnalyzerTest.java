package de.obqo.decycle.analysis;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.slicer.PackageCategorizer;

import org.junit.jupiter.api.Test;

class AnalyzerTest {

    @Test
    void shouldAnalyze() {

        final Analyzer analyzer = new Analyzer();

        final Graph graph = analyzer.analyze("build", new PackageCategorizer(), n -> true);

        System.out.println("=========================");
        graph.topNodes().forEach(n -> System.out.println(n));

    }
}
