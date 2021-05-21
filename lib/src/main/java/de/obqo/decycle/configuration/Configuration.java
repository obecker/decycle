package de.obqo.decycle.configuration;

import static de.obqo.decycle.slicer.MultiCategorizer.combine;
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
import de.obqo.decycle.slicer.InternalClassCategorizer;
import de.obqo.decycle.slicer.PackageCategorizer;
import de.obqo.decycle.slicer.ParallelCategorizer;
import de.obqo.decycle.slicer.PatternMatchingNodeFilter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.NonNull;

public class Configuration {

    @NonNull
    private final String classpath;

    private final List<String> includes;

    private final List<String> excludes;

    private final List<IgnoredDependency> ignoredDependencies;

    private final Map<String, List<Pattern>> slicings;

    private final Set<Constraint> constraints;

    private final Appendable report;

    private final boolean minifyReport;

    private final Graph graph;

    @Builder
    public Configuration(
            final String classpath,
            final List<String> includes,
            final List<String> excludes,
            final List<IgnoredDependency> ignoredDependencies,
            final Map<String, List<Pattern>> slicings,
            final Set<Constraint> constraints,
            final Appendable report,
            final Boolean minifyReport) {
        this.classpath = classpath;
        this.includes = requireNonNullElse(includes, List.of());
        this.excludes = requireNonNullElse(excludes, List.of());
        this.slicings = requireNonNullElse(slicings, Map.of());
        this.ignoredDependencies = requireNonNullElse(ignoredDependencies, List.of());
        this.constraints = requireNonNullElse(constraints, Set.of());
        this.report = report;
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
        final var cat = new ParallelCategorizer(slicersWithPackages.toArray(Categorizer[]::new));
        return combine(new InternalClassCategorizer(), cat);
    }

    private Categorizer buildSlicing(final String sliceType, final List<Pattern> patterns) {
        return combine(patterns.stream().map(p -> p.toCategorizer(sliceType)).toArray(Categorizer[]::new));
    }

    private NodeFilter buildNodeFilter() {
        return new IncludeExcludeFilter(
                this.includes.stream().map(PatternMatchingNodeFilter::new).collect(toSet()),
                this.excludes.stream().map(PatternMatchingNodeFilter::new).collect(toSet()));
    }

    private EdgeFilter buildEdgeFilters() {
        return new IgnoredDependenciesFilter(this.ignoredDependencies);
    }

    public List<Violation> check() {
        final var allConstraints = Stream.concat(Stream.of(new CycleFree()), this.constraints.stream());
        final var violations = allConstraints.flatMap(c -> c.violations(this.graph).stream())
                .sorted(Comparator.comparing(Violation::getSliceType).thenComparing(Violation::getName))
                .collect(toList());
        if (this.report != null) {
            new HtmlReport().writeReport(this.graph, violations, this.report, this.minifyReport);
        }
        return violations;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Decycle {\n");
        builder.append("  classpath: ").append(this.classpath).append("\n");
        builder.append("  including: ").append(this.includes).append("\n");
        builder.append("  excluding: ").append(this.excludes).append("\n");
        builder.append("  ignored: ").append(this.ignoredDependencies).append("\n");
        builder.append("  slicings: ").append(this.slicings).append("\n");
        builder.append("  constraints: ").append(this.constraints).append("\n");
        builder.append("}");
        return builder.toString();
    }
}
