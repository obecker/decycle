package de.obqo.gradle.decycle;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Oliver Becker
 */
public class DecyclePluginFunctionalTest {

    private List<File> pluginClasspath;

    @BeforeEach
    void setUp() throws Exception {
        final URL pluginClasspathResource = getClass().getClassLoader().getResource("plugin-classpath.txt");
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.");
        }

        try (final InputStream is = pluginClasspathResource.openStream()) {
            this.pluginClasspath = IOUtils.readLines(is, Charset.defaultCharset())
                    .stream()
                    .map(File::new)
                    .collect(Collectors.toList());
        }
    }

    @Test
    void shouldSucceed() {
        BuildResult result = build("success.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
    }

    @Test
    void shouldSucceedWithIgnoredDependenciesAsList() {
        BuildResult result = build("ignore-list.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
    }

    @Test
    void shouldSucceedWithIgnoredDependenciesAsMap() {
        BuildResult result = build("ignore-map.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
    }

    @Test
    void shouldFailBecauseOfCycles() {
        BuildResult result = buildAndFail("cycle.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("demo.cycle.a → demo.cycle.b, demo.cycle.b → demo.cycle.a");
    }

    @Test
    void shouldFailBecauseOfCyclesWithDefaultConfiguration() {
        BuildResult result = buildAndFail("default.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("demo.cycle.a → demo.cycle.b, demo.cycle.b → demo.cycle.a");
    }

    @Test
    void shouldSucceedWithSlicings() {
        BuildResult result = build("allowed.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
    }

    @Test
    void shouldFailBecauseOfDisallowedSlices() {
        BuildResult result = buildAndFail("disallowed.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("x → z");
    }

    @Test
    void shouldFailWithSliceCycles() {
        BuildResult result = buildAndFail("slices-cycle.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("a → b, b → a");
    }

    @Test
    void shouldFailWithMultipleViolations() {
        BuildResult result = buildAndFail("multiple.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("more")
                .contains("demo.cycle.a → demo.cycle.b, demo.cycle.b → demo.cycle.a");
    }

    @Test
    void shouldSucceedOnTestSources() {
        BuildResult result = build("test-sources.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");

        assertThat(result.task(":decycleMain")).isNull();
    }

    @Test
    void shouldSucceedWithAdditionalSourceSets() {
        BuildResult result = build("source-sets.gradle");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleMain");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleTest");
        assertBuildResult(result, TaskOutcome.SUCCESS, "decycleShared");
    }

    @Test
    void shouldFailBecauseOfIncompleteSlicingConfiguration() {
        BuildResult result = buildAndFail("error-missing-pattern.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, "decycleMain")
                .contains("Slicing 'module' has no pattern definition");
    }

    @Test
    void shouldFailBecauseOfWrongIgnoreListConfiguration() {
        BuildResult result = buildAndFail("error-ignore-list.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, null)
                .contains("ignore list must consist of string pairs, found 3 values");
    }

    @Test
    void shouldFailBecauseOfWrongIgnoreMapConfiguration() {
        BuildResult result = buildAndFail("error-ignore-map.gradle");
        assertBuildResult(result, TaskOutcome.FAILED, null)
                .contains("ignore must have from: and to: values, found {from=demo.module.b.**}");
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
        return GradleRunner.create().withProjectDir(new File("demo")).withPluginClasspath(this.pluginClasspath);
    }

    private AbstractStringAssert<?> assertBuildResult(final BuildResult buildResult, final TaskOutcome expectedOutcome, final String taskName) {
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
