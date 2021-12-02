package de.obqo.decycle.maven;

import static java.util.stream.Collectors.toList;

import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.configuration.Configuration;
import de.obqo.decycle.configuration.NamedPattern;
import de.obqo.decycle.configuration.Pattern;
import de.obqo.decycle.configuration.UnnamedPattern;
import de.obqo.decycle.report.ResourcesExtractor;
import de.obqo.decycle.slicer.IgnoredDependency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import lombok.AccessLevel;
import lombok.Setter;

@Setter(AccessLevel.PACKAGE)
abstract class AbstractDecycleMojo extends AbstractMojo {

    private static final java.util.regex.Pattern NAMED_PATTERN = java.util.regex.Pattern.compile("(\\w+)=(.+)");

    protected static final String MAIN = "main";
    protected static final String TEST = "test";


    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Comma separated list of inclusion patterns, for example org.company.package.**
     */
    @Parameter
    private String including;

    /**
     * Comma separated list of exclusion patterns, for example org.company.package.**
     */
    @Parameter
    private String excluding;

    /**
     * If set to true, then violations detected by decycle will not cause the build to fail. Default is false.
     */
    @Parameter(property = "decycle.ignore.failures", defaultValue = "false")
    private boolean ignoreFailures;

    /**
     * List of ignored dependencies. Every element has a 'from' and a 'to' pattern describing the two sides of the
     * dependency. Omitting one of them is equivalent of specifying '**', i.e. dependencies from any or to any class
     * will be ignored. Example element:
     * <p>
     * &lt;value>&lt;from>org.company.model.**&lt;/from>&lt;to>org.company.service.Locator&lt;/to>&lt;/value>
     */
    @Parameter
    private IgnoringConfig[] ignoring;

    /**
     * List of slicing definitions. Each slicing has a name and a comma separated list of patterns. Example element:
     * &lt;value>&lt;name>module&lt;/name>&lt;patterns>org.company.(*).**&lt;/patterns>&lt;/value>. Each pattern is
     * either an unnamed pattern (like in the example above) or a named pattern having the form 'name=pattern'
     */
    @Parameter
    private SlicingConfig[] slicings;

    /**
     * If set to true, then the decycle checks will be skipped. Default is false.
     */
    @Parameter(property = "skip.decycle", defaultValue = "false")
    private boolean skip;

    /**
     * If set to true, then the decycle check for the main classes will be skipped. Default is false.
     */
    @Parameter(property = "skip.decycle.main", defaultValue = "false")
    private boolean skipMain;

    /**
     * If set to true, then the decycle check for the test classes will be skipped. Default is false.
     */
    @Parameter(property = "skip.decycle.test", defaultValue = "false")
    private boolean skipTest;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final List<Constraint.Violation> violations = executeCheck();
            if (!this.ignoreFailures && !violations.isEmpty()) {
                throw new MojoFailureException("Decycle check failed");
            }
        } catch (final IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected abstract List<Constraint.Violation> executeCheck() throws IOException;

    protected List<Constraint.Violation> checkMain() throws IOException {
        if (this.skip || this.skipMain) {
            getLog().info("Skipped decycle check for main classes");
            return List.of();
        }
        return check(getMainClasses(), MAIN);
    }

    protected List<Constraint.Violation> checkTest() throws IOException {
        if (this.skip || this.skipTest) {
            getLog().info("Skipped decycle check for test classes");
            return List.of();
        }
        return check(getTestClasses(), TEST);
    }

    protected List<Constraint.Violation> check(final String classpath, final String sourceSet) throws IOException {
        final Log log = getLog();
        if (!new File(classpath).exists()) {
            log.warn(classpath + " is missing - skipped decycle check for " + sourceSet + " classes");
            return List.of();
        }

        final File reportDir = getDecycleReportDir();
        final String resourcesDirName = createResourcesIfRequired(reportDir);

        final File report = new File(reportDir, sourceSet + ".html");
        try (final FileWriter reportWriter = new FileWriter(report)) {
            final Configuration configuration = Configuration.builder()
                    .classpath(classpath)
                    .including(tokenize(this.including))
                    .excluding(tokenize(this.excluding))
                    .ignoring(getIgnoredDependencies())
                    .slicings(getSlicings())
                    // TODO constraints(...)
                    .report(reportWriter)
                    .reportResourcesPrefix(resourcesDirName)
                    .reportTitle(this.project.getName() + " | " + sourceSet)
                    .build();

            log.debug("decycle configuration: " + configuration);

            final Consumer<String> logHandler = this.ignoreFailures ? log::warn : log::error;
            final List<Constraint.Violation> violations = configuration.check();
            if (!violations.isEmpty()) {
                logHandler.accept("Violations detected: " + Constraint.Violation.displayString(violations));
                logHandler.accept("See the report at: " + report);
            }
            return violations;
        }
    }

    protected String getMainClasses() {
        return this.project.getBuild().getOutputDirectory();
    }

    protected String getTestClasses() {
        return this.project.getBuild().getTestOutputDirectory();
    }

    private File getDecycleReportDir() {
        return new File(this.project.getModel().getReporting().getOutputDirectory(), "decycle");
    }

    private String createResourcesIfRequired(final File reportDir) throws IOException {
        final String resourcesDirName = "resources-" + Configuration.class.getPackage().getImplementationVersion();
        final File resourcesDir = new File(reportDir, resourcesDirName);
        if (!resourcesDir.exists()) {
            reportDir.mkdirs();
            ResourcesExtractor.copyResources(resourcesDir);
        }
        return resourcesDirName;
    }

    private List<String> tokenize(final String value) {
        return Optional.ofNullable(value).map(v -> v.split("\\s*,\\s*")).map(List::of).orElse(List.of());
    }

    private List<IgnoredDependency> getIgnoredDependencies() {
        return stream(this.ignoring)
                .map(config -> new IgnoredDependency(config.getFrom(), config.getTo()))
                .collect(toList());
    }

    private Map<String, List<Pattern>> getSlicings() {
        return stream(this.slicings).collect(Collectors.toMap(
                SlicingConfig::getName,
                config -> tokenize(config.getPatterns()).stream().map(this::toPattern).collect(toList())));
    }

    private Pattern toPattern(final String string) {
        final Matcher matcher = NAMED_PATTERN.matcher(string);
        return matcher.matches() ? new NamedPattern(matcher.group(1), matcher.group(2)) : new UnnamedPattern(string);
    }

    private <T> Stream<T> stream(final T[] array) {
        return Optional.ofNullable(array).stream().flatMap(Arrays::stream);
    }
}
