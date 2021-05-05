package de.obqo.decycle.configuration;

import static de.obqo.decycle.check.Layer.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.DirectLayeringConstraint;
import de.obqo.decycle.check.LayeringConstraint;
import de.obqo.decycle.slicer.IgnoredDependency;

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
    void projectConfigurationShouldHaveNoConstraintViolations() throws IOException {
        try (final FileWriter out = new FileWriter("build/main.html")) {
            assertThat(Configuration.builder()
                    .classpath("build")
                    .includes(List.of("de.obqo.decycle.**"))
                    .excludes(List.of("de.obqo.decycle.demo.**"))
                    .report(out)
                    .build()
                    .check())
                    .isEmpty();
        }
    }

    @Test
    void violatedLayeringConstraintsShouldBeReported() throws IOException {
        final StringBuilder out = new StringBuilder();

        final List<Constraint.Violation> violations = Configuration.builder()
                .classpath("build")
                .includes(List.of("de.obqo.decycle.demo.**"))
                .ignoredDependencies(List.of(
                        new IgnoredDependency("de.obqo.decycle.demo.common.**", "de.obqo.decycle.demo.util.**"),
                        new IgnoredDependency("de.obqo.decycle.demo.common.*", "de.obqo.decycle.demo.common.impl.*")))
                .slicings(Map.of("subpackage", List.of(new UnnamedPattern("de.obqo.decycle.demo.(*).**"))))
                .constraints(Set.of(
                        new LayeringConstraint("subpackage", List.of(anyOf("common"), anyOf("helper", "util"))),
                        new DirectLayeringConstraint("subpackage", List.of(anyOf("common"), anyOf("helper", "shared")))
                ))
                .report(out)
                .build()
                .check();

        new FileWriter("build/demo.html").append(out.toString()).close();

        assertThat(violations).hasSize(2).extracting(Constraint.Violation::getSliceType).allMatch("subpackage"::equals);

        final var violation1 = violations.get(0);
        assertThat(violation1.getName()).isEqualTo("common => (helper, shared)");
        assertThat(violation1.getDependencies()).containsOnly(new Constraint.Dependency("shared", "common")
        );

        final var violation2 = violations.get(1);
        assertThat(violation2.getName()).isEqualTo("no cycles");
        assertThat(violation2.getDependencies()).containsOnly(
                new Constraint.Dependency("common", "shared"),
                new Constraint.Dependency("shared", "common")
        );
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
