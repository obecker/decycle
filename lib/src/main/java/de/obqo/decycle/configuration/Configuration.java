package de.obqo.decycle.configuration;

import static de.obqo.decycle.slicer.MultiCategorizer.combine;
import static de.obqo.decycle.slicer.ParallelCategorizer.parallel;
import static java.util.Objects.requireNonNullElse;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import de.obqo.decycle.analysis.Analyzer;
import de.obqo.decycle.analysis.IncludeExcludeFilter;
import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.Constraint.Violation;
import de.obqo.decycle.check.CycleFree;
import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.EdgeFilter;
import de.obqo.decycle.model.NodeFilter;
import de.obqo.decycle.report.HtmlReport;
import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.IgnoredDependenciesFilter;
import de.obqo.decycle.slicer.IgnoredDependency;
import de.obqo.decycle.slicer.PackageCategorizer;
import de.obqo.decycle.slicer.PatternMatchingNodeFilter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.NonNull;

/**
 * Central configuration class for using the Decycle API. A full configuration check would look like this
 * <pre>
 *     List&lt;Constraint.Violation&gt; violations = Configuration.builder()
 *             .classpath("...") // required
 *             .including(...)
 *             .excluding(...)
 *             .ignoring(...)
 *             .slicings(...)
 *             .constraints(...)
 *             .report(...)
 *             .reportResourcesPrefix(...)
 *             .reportTitle(...)
 *             .minifyReport(...)
 *             .build()
 *             .check();
 * </pre>
 * However, typically use cases don't need all settings. The minimal configuration is
 * <pre>
 *     List&lt;Constraint.Violation&gt; violations = Configuration.builder()
 *             .classpath("...")
 *             .build()
 *             .check();
 * </pre>
 * and will return all package cycles in the given classpath.
 */
public class Configuration {

    @NonNull
    private final String classpath;

    private final List<String> including;

    private final List<String> excluding;

    private final List<IgnoredDependency> ignoring;

    private final Map<String, List<Pattern>> slicings;

    private final Set<Constraint> constraints;

    private final Appendable report;

    private final String reportResourcesPrefix;

    private final String reportTitle;

    private final boolean minifyReport;

    private final Graph graph;

    /**
     * Constructor for the Lombok builder. Will not be visible in the generated javadoc, but the param comments will be
     * copied to their corresponding builder methods.
     *
     * @param classpath    The classpath the be analyzed - this cannot be {@code null} or omitted. The classpath is a
     *                     string of directories or jar files that are separated by the OS specific path separator
     *                     (typically a semicolon ';' or colon ':').
     * @param including    Include only classes in the analysis whose fully qualified class names match the given
     *                     patterns (if null or empty: include all classes) - see {@link PatternMatchingNodeFilter}
     * @param excluding    Exclude classes from the analysis whose fully qualified class names match the given patterns
     *                     (if null or empty: exclude no classes) - see {@link PatternMatchingNodeFilter}
     * @param ignoring     List of class dependencies that should be ignored when checking cycles and other constraints
     * @param slicings     Definition of slicings (map key = slicing type, map value = list of patterns that create the
     *                     slices)
     * @param constraints  Set of additional constraints to be checked (Note: {@link CycleFree} is automatically
     *                     included)
     * @param report       Target of the HTML report (if {@code null}, then no report is written)
     * @param reportResourcesPrefix Relative path that is prepended to linked CSS and JS resources in the HTML report.
     *                              These resources must be created independently using
     *                              {@link de.obqo.decycle.report.ResourcesExtractor#copyResources(java.io.File)}
     * @param reportTitle  HTML Title to be used in the generated report. Has no effect if no {@code report} was
     *                     configured.
     * @param minifyReport Whether the HTML report should be minified (default is {@code true}). Has no effect if no
     *                     {@code report} was configured.
     * @since dummy javadoc tag - prevents a lombok bug that discards the last param comment in the builder methods
     */
    @Builder
    private Configuration(
            final String classpath,
            final List<String> including,
            final List<String> excluding,
            final List<IgnoredDependency> ignoring,
            final Map<String, List<Pattern>> slicings,
            final Set<Constraint> constraints,
            final Appendable report,
            final String reportResourcesPrefix,
            final String reportTitle,
            final Boolean minifyReport) {
        this.classpath = classpath;
        this.including = requireNonNullElse(including, List.of());
        this.excluding = requireNonNullElse(excluding, List.of());
        this.slicings = requireNonNullElse(slicings, Map.of());
        this.ignoring = requireNonNullElse(ignoring, List.of());
        this.constraints = requireNonNullElse(constraints, Set.of());
        this.report = report;
        this.reportResourcesPrefix = requireNonNullElse(reportResourcesPrefix, ".");
        this.reportTitle = reportTitle;
        this.minifyReport = !Boolean.FALSE.equals(minifyReport); // null -> true

        validate();

        this.graph = createGraph();
    }

    private void validate() {
        this.slicings.forEach((sliceType, patterns) -> {
            if (patterns.isEmpty()) {
                throw new IllegalArgumentException("Slicing '" + sliceType + "' has no pattern definition");
            }
        });
    }

    private Graph createGraph() {
        return new Analyzer().analyze(this.classpath, buildCategorizer(), buildNodeFilter(), buildEdgeFilters());
    }

    private Categorizer buildCategorizer() {
        final var slicers =
                this.slicings.entrySet().stream().map(entry -> buildSlicing(entry.getKey(), entry.getValue()));
        final var slicersWithPackages = Stream.concat(Stream.of(new PackageCategorizer()), slicers);
        return parallel(slicersWithPackages.toArray(Categorizer[]::new));
    }

    private Categorizer buildSlicing(final String sliceType, final List<Pattern> patterns) {
        return combine(patterns.stream().map(p -> p.toCategorizer(sliceType)).toArray(Categorizer[]::new));
    }

    private NodeFilter buildNodeFilter() {
        return new IncludeExcludeFilter(
                this.including.stream().map(PatternMatchingNodeFilter::new).collect(toSet()),
                this.excluding.stream().map(PatternMatchingNodeFilter::new).collect(toSet()));
    }

    private EdgeFilter buildEdgeFilters() {
        return new IgnoredDependenciesFilter(this.ignoring);
    }

    /**
     * Perform a check of the classes in the given {@code classpath} against the configured {@code constraints} and
     * writes a report if requested.
     *
     * @return the list of all detected constraint violations (empty if no violations have been detected).
     */
    public List<Violation> check() {
        final var allConstraints = Stream.concat(Stream.of(new CycleFree()), this.constraints.stream());
        final var violations = allConstraints.flatMap(c -> c.violations(this.graph).stream())
                .sorted(Comparator.comparing(Violation::getSliceType).thenComparing(Violation::getName))
                .collect(toList());
        if (this.report != null) {
            new HtmlReport(this.minifyReport).writeReport(this.graph, violations, this.report,
                    this.reportResourcesPrefix, this.reportTitle);
        }
        return violations;
    }

    /**
     * @return a string representation of this configuration
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Decycle {\n");
        builder.append("  classpath: ").append(this.classpath).append("\n");
        builder.append("  including: ").append(this.including).append("\n");
        builder.append("  excluding: ").append(this.excluding).append("\n");
        builder.append("  ignoring: ").append(this.ignoring).append("\n");
        builder.append("  slicings: ").append(this.slicings).append("\n");
        builder.append("  constraints: ").append(this.constraints).append("\n");
        builder.append("}");
        return builder.toString();
    }
}
