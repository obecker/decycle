package de.obqo.decycle.configuration;

import static de.obqo.decycle.check.Layer.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.DirectLayeringConstraint;
import de.obqo.decycle.check.LayeringConstraint;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ConfigurationTest {

    @Test
    void projectConfigurationShouldHaveNoConstraintViolations() {
        assertThat(Configuration.builder().classpath("build").includes(List.of("de.obqo.decycle.**")).build().check())
                .isEmpty();
    }

    @Test
    void violatedLayeringConstraintsShouldBeReported() throws IOException {
        final StringWriter writer = new StringWriter();

        final List<Constraint.Violation> violations = Configuration.builder()
                .classpath("build")
                .includes(List.of("de.obqo.decycle.**"))
                .categories(Map.of("subpackage", List.of(new UnnamedPattern("de.obqo.decycle.(*).**"))))
                .constraints(Set.of(
                        new LayeringConstraint("subpackage", List.of(anyOf("util"), anyOf("graph", "slicer"))),
                        new DirectLayeringConstraint("subpackage", List.of(anyOf("model"), anyOf("util")))
                ))
                .report(writer)
                .build()
                .check();

        assertThat(violations).hasSize(2).extracting(Constraint.Violation::getSliceType).allMatch("subpackage"::equals);

        final var violation1 = violations.get(0);
        assertThat(violation1.getName()).isEqualTo("model => util");
        assertThat(violation1.getDependencies()).containsOnly(
                new Constraint.Dependency("analysis", "util"),
                new Constraint.Dependency("graph", "util"),
                new Constraint.Dependency("slicer", "util")
        );

        final var violation2 = violations.get(1);
        assertThat(violation2.getName()).isEqualTo("util -> (graph, slicer)");
        assertThat(violation2.getDependencies()).containsOnly(
                new Constraint.Dependency("graph", "util"),
                new Constraint.Dependency("slicer", "util")
        );

        new FileWriter("build/output.html").append(writer.toString()).close();
    }

    @Test
    void shouldWriteReport() throws IOException {
        final StringWriter writer = new StringWriter();

        final List<Constraint.Violation> violations = Configuration.builder()
                .classpath(System.getProperty("java.class.path"))
                .includes(List.of("j2html.**"))
                .report(writer)
                .minifyReport(false)
                .build()
                .check();

        new FileWriter("build/test.html").append(writer.toString()).close();

        final String expectedReport = readResource("ConfigurationTest-shouldWriteReport.html");
        assertThat(writer.toString()).isEqualTo(expectedReport);
    }

    private String readResource(final String filename) {
        final Scanner s = new Scanner(ConfigurationTest.class.getResourceAsStream(filename)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
