package de.obqo.decycle.analysis;

import org.junit.jupiter.api.Test;

import de.obqo.decycle.graph.Graph;

class AnalyzerTest {

    @Test
    void shouldAnalyze() {

        final Analyzer analyzer = new Analyzer();

        final Graph graph = analyzer.analyze("build", null, null);

        System.out.println("=========================");
        graph.topNodes().forEach(n -> System.out.println(n));

    }
}
