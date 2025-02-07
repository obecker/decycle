package de.obqo.decycle.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.AbstractStringAssert;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Oliver Becker
 */
public class DecyclePluginFunctionalTest {

    private static List<File> pluginClasspath;

    @BeforeAll
    static void setUp() throws Exception {
        final URL pluginClasspathResource =
                DecyclePluginFunctionalTest.class.getClassLoader().getResource("plugin-classpath.txt");
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.");
        }

        try (final InputStream is = pluginClasspathResource.openStream()) {
            pluginClasspath = IOUtils.readLines(is, Charset.defaultCharset())
                    .stream()
                    .map(File::new)
                    .collect(Collectors.toList());
        }
    }

    @Test
    void shouldSucceed() {
        final BuildResult result = build("success.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
    }

    @Test
    void shouldSucceedWithIgnoredDependencies() {
        final BuildResult result = build("ignoring.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
    }

    @Test
    void shouldSucceedWithIgnoredFromDependencies() {
        final BuildResult result = build("ignoring-from.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
    }

    @Test
    void shouldSucceedWithIgnoredToDependencies() {
        final BuildResult result = build("ignoring-to.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
    }

    @Test
    void shouldFailBecauseOfCycles() {
        final BuildResult result = buildAndFail("cycle.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("demo.cycle.a → demo.cycle.b, demo.cycle.b → demo.cycle.a")
                .contains("See the report at: ");
    }

    @Test
    void shouldFailBecauseOfCyclesWithDefaultConfiguration() {
        final BuildResult result = buildAndFail("default.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("demo.cycle.a → demo.cycle.b, demo.cycle.b → demo.cycle.a");
    }

    @Test
    void shouldSucceedWithSlicings() {
        final BuildResult result = build("allowed.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
    }

    @Test
    void shouldFailBecauseOfDisallowedSlices() {
        final BuildResult result = buildAndFail("disallowed.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("x → z");
    }

    @Test
    void shouldFailWithSliceCycles() {
        final BuildResult result = buildAndFail("slices-cycle.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("a → b, b → a");
    }

    @Test
    void shouldFailWithMultipleViolations() {
        final BuildResult result = buildAndFail("multiple.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("more")
                .contains("demo.cycle.a → demo.cycle.b, demo.cycle.b → demo.cycle.a");
    }

    @Test
    void shouldSucceedOnTestSources() {
        final BuildResult result = build("test-sources.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");

        assertThat(result.task(":decycleMain")).isNull();
    }

    @Test
    void shouldSucceedWithAdditionalSourceSets() {
        final BuildResult result = build("source-sets.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
        assertBuildResult(result, TaskOutcome.NO_SOURCE, "decycleShared");
    }

    @Test
    void shouldFailBecauseOfIncompleteSlicingConfiguration() {
        final BuildResult result = buildAndFail("error-missing-pattern.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("Slicing 'module' has no pattern definition");
    }

    @Test
    void shouldFailBecauseOfIllegalNamePatterns() {
        final BuildResult result = buildAndFail("error-illegal-pattern.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("Curly braces are not allowed in a named pattern. Encountered 'demo.module.{*}.**'");
    }

    @Test
    void shouldFailBecauseOfWrongIgnoringListConfiguration() {
        final BuildResult result = buildAndFail("error-ignoring-list.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, null)
                .contains("decycle: ignoring must be used with from: and to: values, " +
                        "found demo.module.b.**, demo.module.a.**, c");
    }

    @Test
    void shouldFailBecauseOfWrongIgnoringMapConfiguration() {
        final BuildResult result = buildAndFail("error-ignoring-map.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, null)
                .contains("ignoring must only have from: and to: values, found and:, via:");
    }

    @Test
    void shouldSucceedWithIgnoreFailures() {
        final BuildResult result = build("ignoreFailures.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain")
                .contains("\nViolations detected: Violation(slicing=Package, name=cycle, " +
                        "dependencies=[demo.cycle.a → demo.cycle.b, demo.cycle.b → demo.cycle.a])\n")
                .contains("See the report at: ");
    }

    @Test
    void shouldFailButWithoutReport() {
        final BuildResult result = buildAndFail("reportsDisabled.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("demo.cycle.a → demo.cycle.b, demo.cycle.b → demo.cycle.a")
                .doesNotContain("See the report at: ");
    }

    @Test
    void shouldSucceedWithIgnoredDependenciesAsKotlinPair() {
        final BuildResult result = build("ignoring-kt.gradle.kts");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
    }

    @Test
    void shouldSucceedWithSourceSetsInKotlinDsl() {
        final BuildResult result = build("test-sources-kt.gradle.kts");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
    }

    private BuildResult build(final String buildFile) {
        return execute(buildFile, true);
    }

    private BuildResult buildAndFail(final String buildFile) {
        return execute(buildFile, false);
    }

    private BuildResult execute(final String buildFile, final boolean expectSuccess) {
        final GradleRunner gradleRunner = buildGradleRunner()
                .withArguments("-b", buildFile, "decycle", "--info", "--stacktrace", "--rerun-tasks")
                .withDebug(true);
        return expectSuccess ? gradleRunner.build() : gradleRunner.buildAndFail();
    }

    private GradleRunner buildGradleRunner() {
        return GradleRunner.create().withProjectDir(new File("demo")).withPluginClasspath(pluginClasspath);
    }

    private AbstractStringAssert<?> assertBuildResult(final BuildResult buildResult, final TaskOutcome expectedOutcome,
            final String taskName) {
        System.out.println(buildResult.getOutput());

        if (taskName != null) {
            final BuildTask decycleTask = buildResult.task(":" + taskName);
            assertThat(decycleTask).isNotNull();
            assertThat(decycleTask.getOutcome()).isEqualTo(expectedOutcome);
        }

        return assertThat(buildResult.getOutput());
    }

    @Test
    void shouldCachePreviousRun() {
        // given
        final String buildFile = "allowed.gradle";

        // when first run
        final BuildResult successResult = buildGradleRunner()
                .withArguments("-b", buildFile, "decycle", "--info", "--stacktrace", "--rerun-tasks")
                .withDebug(true)
                .build();
        // then
        assertBuildResult(successResult, TaskOutcome.SUCCESS, "decycleMain");

        // when second run
        final BuildResult upToDateResult = buildGradleRunner()
                .withArguments("-b", buildFile, "decycle", "--info", "--stacktrace")
                .withDebug(true)
                .build();
        // then
        assertBuildResult(upToDateResult, TaskOutcome.UP_TO_DATE, "decycleMain");
    }
}
