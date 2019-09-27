package de.obqo.decycle.analyze;

import org.junit.jupiter.api.Test;

class AnalyzerTest {

    @Test
    void shouldAnalyze() {

        Analyzer analyzer = new Analyzer();

        analyzer.analyze("build");

    }
}
