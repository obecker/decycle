package de.obqo.decycle.analyze;

import org.junit.jupiter.api.Test;

/**
 * @author Oliver Becker
 * @since 25.09.19
 */
class AnalyzerTest {

    @Test
    void shouldAnalyze() {

        Analyzer analyzer = new Analyzer();

        analyzer.analyze("build");

    }
}
