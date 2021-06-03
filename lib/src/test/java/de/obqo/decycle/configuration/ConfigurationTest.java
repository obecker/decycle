package de.obqo.decycle.configuration;

import static de.obqo.decycle.check.Layer.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.DirectLayeringConstraint;
import de.obqo.decycle.check.LayeringConstraint;
import de.obqo.decycle.slicer.IgnoredDependency;

import java.io.FileWriter;
import java.io.IOException;
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
                    .classpath("build/classes/java/main") // for gradle
//                    .classpath("out/production/classes") // for IntelliJ
                    .includes(List.of("de.obqo.decycle.**"))
                    .report(out)
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
                .includes(List.of("de.obqo.decycle.demo.base.**"))
                .report(out)
                .minifyReport(false)
                .build()
                .check())
                .isEmpty();
        new FileWriter("build/demobase.html").append(out.toString()).close();

        final String expectedReport = readResource("ConfigurationTest-shouldReportAllDependencies.html");
        assertThat(out.toString()).isEqualTo(expectedReport);
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
        assertThat(violation1.getDependencies()).containsOnly(new Constraint.Dependency("shared", "common"));

        final var violation2 = violations.get(1);
        assertThat(violation2.getName()).isEqualTo("no cycles");
        assertThat(violation2.getDependencies()).containsOnly(
                new Constraint.Dependency("common", "shared"),
                new Constraint.Dependency("shared", "common")
        );
    }

    @Test
    void shouldWriteReport() throws IOException {
        final StringBuilder out = new StringBuilder();

        Configuration.builder()
                .classpath(System.getProperty("java.class.path"))
                .includes(List.of("j2html.**"))
                .ignoredDependencies(List.of(new IgnoredDependency("j2html.attributes.Attribute", "j2html.**")))
                .report(out)
                .reportTitle("j2html")
                .minifyReport(false)
                .build()
                .check();

        new FileWriter("build/test.html").append(out.toString()).close();

        final String expectedReport = readResource("ConfigurationTest-shouldWriteReport.html");
        assertThat(out.toString()).isEqualTo(expectedReport);
    }

    private String readResource(final String filename) {
        final Scanner s = new Scanner(ConfigurationTest.class.getResourceAsStream(filename)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
