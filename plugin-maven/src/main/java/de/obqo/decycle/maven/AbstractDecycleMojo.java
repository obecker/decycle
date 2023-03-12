package de.obqo.decycle.maven;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.DirectLayeringConstraint;
import de.obqo.decycle.check.Layer;
import de.obqo.decycle.check.LayeringConstraint;
import de.obqo.decycle.configuration.Configuration;
import de.obqo.decycle.configuration.Pattern;
import de.obqo.decycle.report.ResourcesExtractor;
import de.obqo.decycle.slicer.IgnoredDependency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
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
    @Parameter(property = "decycle.ignoreFailures", defaultValue = "false")
    private boolean ignoreFailures;

    /**
     * List of ignored dependencies. Every element has a 'from' and a 'to' pattern describing the two sides of the
     * dependency. Omitting one of them is equivalent of specifying '**', i.e. dependencies from any or to any class
     * will be ignored. Example element:
     * <p>
     * &lt;value>&lt;from>org.company.model.**&lt;/from>&lt;to>org.company.service.Locator&lt;/to>&lt;/value>
     */
    @Parameter
    private Dependency[] ignoring;

    /**
     * List of slicing definitions. Each slicing has a name and a comma separated list of patterns. Example element:
     * &lt;value>&lt;name>module&lt;/name>&lt;patterns>org.company.(*).**&lt;/patterns>&lt;/value>. Each pattern is
     * either an unnamed pattern (like in the example above) or a named pattern having the form 'pattern=name'
     */
    @Parameter
    private Slicing[] slicings;

    /**
     * If set to true, then the decycle checks will be skipped. Default is false.
     */
    @Parameter(property = "decycle.skip", defaultValue = "false")
    private boolean skip;

    /**
     * If set to true, then the decycle check for the main classes will be skipped. Default is false.
     */
    @Parameter(property = "decycle.skipMain", defaultValue = "false")
    private boolean skipMain;

    /**
     * If set to true, then the decycle check for the test classes will be skipped. Default is false.
     */
    @Parameter(property = "decycle.skipTest", defaultValue = "false")
    private boolean skipTest;

    /**
     * If set to true, then no report is created after executing the decycle checks. Default is false.
     */
    @Parameter(property = "decycle.skipReports", defaultValue = "false")
    private boolean skipReports;

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
            log.warn("Decycle: " + classpath + " is missing - skipped decycle check for " + sourceSet + " classes");
            return List.of();
        }

        if (this.skipReports) {
            final Configuration config = buildConfiguration(classpath, sourceSet, null, null);
            return check(config, null);
        } else {
            final File reportDir = getDecycleReportDir();
            final String resourcesDirName = ResourcesExtractor.createResourcesIfRequired(reportDir);

            final File report = new File(reportDir, sourceSet + ".html");
            try (final FileWriter reportWriter = new FileWriter(report)) {
                final Configuration config = buildConfiguration(classpath, sourceSet, resourcesDirName, reportWriter);
                return check(config, report);
            }
        }
    }

    // visible for testing
    Configuration buildConfiguration(final String classpath, final String sourceSet,
            final String resourcesDirName, final FileWriter reportWriter) {
        return Configuration.builder()
                .classpath(classpath)
                .including(tokenizeToList(this.including))
                .excluding(tokenizeToList(this.excluding))
                .ignoring(getIgnoredDependencies())
                .slicings(getSlicings())
                .constraints(getConstraints())
                .report(reportWriter)
                .reportResourcesPrefix(resourcesDirName)
                .reportTitle(this.project.getName() + " | " + sourceSet)
                .build();
    }

    private List<Constraint.Violation> check(final Configuration config, final File report) {
        final Log log = getLog();
        log.debug("Decycle configuration: " + config);

        final Consumer<String> logHandler = this.ignoreFailures ? log::warn : log::error;
        final List<Constraint.Violation> violations = config.check();
        if (!violations.isEmpty()) {
            logHandler.accept("Violations detected: " + Constraint.Violation.displayString(violations));
            if (report != null) {
                logHandler.accept("See the report at: " + report);
            }
        }
        return violations;
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

    private Stream<String> tokenize(final String value) {
        return Optional.ofNullable(value).map(v -> v.split(",")).stream()
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(not(String::isEmpty));
    }

    private List<String> tokenizeToList(final String value) {
        return tokenize(value).collect(toList());
    }

    private String[] tokenizeToArray(final String value) {
        return tokenize(value).toArray(String[]::new);
    }

    private List<IgnoredDependency> getIgnoredDependencies() {
        return stream(this.ignoring)
                .map(dependency -> IgnoredDependency.create(dependency.getFrom(), dependency.getTo()))
                .collect(toList());
    }

    private Map<String, List<Pattern>> getSlicings() {
        return stream(this.slicings).collect(Collectors.toMap(
                Slicing::getName,
                slicing -> tokenize(slicing.getPatterns()).map(Pattern::parse).collect(toList())));
    }

    private Set<Constraint> getConstraints() {
        return stream(this.slicings).flatMap(this::mapConstraints).collect(toCollection(LinkedHashSet::new));
    }

    private Stream<Constraint> mapConstraints(final Slicing slicing) {
        return stream(slicing.getConstraints()).map(constraint -> mapConstraint(slicing, constraint));
    }

    private Constraint mapConstraint(final Slicing slicing, final AllowConstraint constraint) {
        final List<Layer> layers;
        // The constraint configuration is either simple or complex (never mixed)
        if (constraint.get() != null) {
            // this is a simple configuration using <allow>slice1, slice2, ...</allow>
            layers = tokenize(constraint.get()).map(Layer::anyOf).collect(toList());
        } else {
            // this is a complex configuration using <allow><any-of>slice1, slice2</any-of>...</allow>
            layers = constraint.getLayers().stream().map(this::mapLayers).collect(toList());
        }
        return constraint.isDirect()
                ? new DirectLayeringConstraint(slicing.getName(), layers)
                : new LayeringConstraint(slicing.getName(), layers);
    }

    private Layer mapLayers(final de.obqo.decycle.maven.Layer layer) {
        final String[] slices = tokenizeToArray(layer.getSlices());
        return layer.isStrict() ? Layer.oneOf(slices) : Layer.anyOf(slices);
    }

    private <T> Stream<T> stream(final T[] array) {
        return Optional.ofNullable(array).stream().flatMap(Arrays::stream);
    }
}
