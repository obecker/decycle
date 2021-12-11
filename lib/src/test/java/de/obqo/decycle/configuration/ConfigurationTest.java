package de.obqo.decycle.configuration;

import static de.obqo.decycle.check.Layer.anyOf;
import static de.obqo.decycle.check.SimpleDependency.d;
import static de.obqo.decycle.model.SliceType.customType;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.DirectLayeringConstraint;
import de.obqo.decycle.check.LayeringConstraint;
import de.obqo.decycle.check.SimpleDependency;
import de.obqo.decycle.report.ResourcesExtractor;
import de.obqo.decycle.slicer.IgnoredDependency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConfigurationTest {

    private static final String REPORT_DIR = "build/demo/";
    private static final String RESOURCES_DIR = "resources";

    @BeforeAll
    static void beforeAll() throws IOException {
        final File dir = new File(REPORT_DIR + RESOURCES_DIR);
        ResourcesExtractor.copyResources(dir);
    }

    @Test
    void projectConfigurationShouldHaveNoConstraintViolations() throws IOException {
        try (final FileWriter out = new FileWriter(REPORT_DIR + "main.html")) {
            assertThat(Configuration.builder()
                    .classpath("build/classes/java/main") // for gradle
//                    .classpath("out/production/classes") // for IntelliJ
                    .including(List.of("de.obqo.decycle.**"))
                    .report(out)
                    .reportResourcesPrefix(RESOURCES_DIR)
                    .build()
                    .check())
                    .isEmpty();
        }
    }

    @Test
    void shouldReportAllDependencies() throws IOException {
        final StringBuilder out = new StringBuilder();
        assertThat(Configuration.builder()
                .classpath("build/classes/java/test") // for gradle
//                .classpath("out/test/classes") // for IntelliJ
                .including(List.of("de.obqo.decycle.demo.base.**"))
                .report(out)
                .reportResourcesPrefix(RESOURCES_DIR)
                .minifyReport(false)
                .build()
                .check())
                .isEmpty();

        final String report = out.toString();
        new FileWriter(REPORT_DIR + "demobase.html").append(report).close();

        assertReport(report, "ConfigurationTest-shouldReportAllDependencies.html");
    }

    @Test
    void violatedLayeringConstraintsShouldBeReported() throws IOException {
        final StringBuilder out = new StringBuilder();

        final List<Constraint.Violation> violations = Configuration.builder()
                .classpath("build")
                .including(List.of("de.obqo.decycle.demo.**"))
                .ignoring(List.of(
                        new IgnoredDependency("de.obqo.decycle.demo.common.**", "de.obqo.decycle.demo.util.**"),
                        new IgnoredDependency("de.obqo.decycle.demo.common.*", "de.obqo.decycle.demo.common.impl.*")))
                .slicings(Map.of("subpackage", List.of(Pattern.parse("de.obqo.decycle.demo.(*).**"))))
                .constraints(Set.of(
                        new LayeringConstraint("subpackage", List.of(anyOf("common"), anyOf("helper", "util"))),
                        new DirectLayeringConstraint("subpackage", List.of(anyOf("common"), anyOf("helper", "shared")))
                ))
                .report(out)
                .reportResourcesPrefix(RESOURCES_DIR)
                .build()
                .check();

        new FileWriter(REPORT_DIR + "demo.html").append(out.toString()).close();

        assertThat(violations).hasSize(2).extracting(Constraint.Violation::getSliceType).allMatch(customType("subpackage")::equals);

        final var violation1 = violations.get(0);
        assertThat(violation1.getName()).isEqualTo("common => (helper, shared)");
        assertThat(violation1.getDependencies()).map(SimpleDependency::new).containsOnly(d("shared", "common"));

        final var violation2 = violations.get(1);
        assertThat(violation2.getName()).isEqualTo("cycle");
        assertThat(violation2.getDependencies()).map(SimpleDependency::new)
                .containsOnly(d("common", "shared"), d("shared", "common"));
    }

    @Test
    void shouldWriteReport() throws IOException {
        final StringBuilder out = new StringBuilder();

        Configuration.builder()
                .classpath(System.getProperty("java.class.path"))
                .including(List.of("j2html.**"))
                .ignoring(List.of(new IgnoredDependency("j2html.attributes.Attribute", "j2html.**")))
                .report(out)
                .reportResourcesPrefix(RESOURCES_DIR)
                .reportTitle("j2html")
                .minifyReport(false)
                .build()
                .check();

        final String report = out.toString();
        new FileWriter(REPORT_DIR + "test.html").append(report).close();

        assertReport(report, "ConfigurationTest-shouldWriteReport.html");
    }

    private void assertReport(final String actualReport, final String filename) {
        final String expectedReport = readResource(filename);
        if (runsWithinIntelliJ()) {
            // this assertion is more useful locally (in the IDE)
            assertThat(sanitizeLineSeparators(actualReport)).isEqualTo(sanitizeLineSeparators(expectedReport));
        } else {
            // this assertion is more useful on the command line (including CI server / gitlab actions)
            final String[] actualLines = actualReport.split("\\R");
            final String[] expectedLines = expectedReport.split("\\R");
            assertThat(actualLines).containsExactly(expectedLines);
        }
    }

    private boolean runsWithinIntelliJ() {
        final String command = System.getProperty("sun.java.command");
        // com.intellij.rt.junit.JUnitStarter
        return command != null && command.contains("intellij");
    }

    private String readResource(final String filename) {
        final Scanner s = new Scanner(ConfigurationTest.class.getResourceAsStream(filename)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private String sanitizeLineSeparators(final String input) {
        // normalize line separators to cope with different OS settings
        return input.replaceAll("\\R", "\n");
    }
}
