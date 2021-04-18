package de.obqo.decycle.configuration;

import static de.obqo.decycle.slicer.MultiCategorizer.combine;
import static java.util.Objects.requireNonNullElse;

import de.obqo.decycle.analysis.Analyzer;
import de.obqo.decycle.analysis.IncludeExcludeFilter;
import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.Constraint.Violation;
import de.obqo.decycle.check.CycleFree;
import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.report.HtmlReport;
import de.obqo.decycle.slicer.Categorizer;
import de.obqo.decycle.slicer.InternalClassCategorizer;
import de.obqo.decycle.slicer.NodeFilter;
import de.obqo.decycle.slicer.PackageCategorizer;
import de.obqo.decycle.slicer.ParallelCategorizer;
import de.obqo.decycle.slicer.PatternMatchingFilter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.NonNull;

public class Configuration {

    @NonNull
    private final String classpath;

    private final List<String> includes;

    private final List<String> excludes;

    private final Map<String, List<Pattern>> categories;

    private final Set<Constraint> constraints;

    private final Appendable report;

    private final boolean minifyReport;

    private final Graph graph;

    @Builder
    public Configuration(
            final String classpath,
            final List<String> includes,
            final List<String> excludes,
            final Map<String, List<Pattern>> categories,
            final Set<Constraint> constraints,
            final Appendable report,
            final Boolean minifyReport) {
        this.classpath = classpath;
        this.includes = requireNonNullElse(includes, List.of());
        this.excludes = requireNonNullElse(excludes, List.of());
        this.categories = requireNonNullElse(categories, Map.of());
        this.constraints = requireNonNullElse(constraints, Set.of());
        this.report = report;
        this.minifyReport = !Boolean.FALSE.equals(minifyReport); // null -> true

        this.graph = createGraph();
    }

    private Graph createGraph() {
        return new Analyzer().analyze(this.classpath, buildCategorizer(), buildFilter());
    }

    private Categorizer buildCategorizer() {
        final var slicers =
                this.categories.entrySet().stream().map(entry -> buildCategorizer(entry.getKey(), entry.getValue()));
        final var slicersWithPackages = Stream.concat(Stream.of(new PackageCategorizer()), slicers);
        final var cat = new ParallelCategorizer(slicersWithPackages.toArray(Categorizer[]::new));
        return combine(new InternalClassCategorizer(), cat);
    }

    private Categorizer buildCategorizer(final String slicing, final List<Pattern> groupings) {
        return combine(groupings.stream().map(p -> p.toCategorizer(slicing)).toArray(Categorizer[]::new));
    }

    private NodeFilter buildFilter() {
        return new IncludeExcludeFilter(
                this.includes.stream().map(PatternMatchingFilter::new).collect(Collectors.toSet()),
                this.excludes.stream().map(PatternMatchingFilter::new).collect(Collectors.toSet()));
    }

    public List<Violation> check() {
        final var allConstraints = Stream.concat(Stream.of(new CycleFree()), this.constraints.stream());
        final var violations = allConstraints.flatMap(c -> c.violations(this.graph).stream())
                .sorted(Comparator.comparing(Violation::getSliceType).thenComparing(Violation::getName))
                .collect(Collectors.toList());
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
        builder.append("  slicings: ").append(this.categories).append("\n");
        builder.append("}");
        return builder.toString();
    }
}
