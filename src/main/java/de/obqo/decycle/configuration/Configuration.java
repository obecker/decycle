package de.obqo.decycle.configuration;

import static de.obqo.decycle.slicer.MultiCategorizer.combine;

import de.obqo.decycle.analysis.Analyzer;
import de.obqo.decycle.analysis.IncludeExcludeFilter;
import de.obqo.decycle.check.Constraint;
import de.obqo.decycle.check.Constraint.Violation;
import de.obqo.decycle.check.CycleFree;
import de.obqo.decycle.graph.Graph;
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

@Builder
public class Configuration {

    private final String classpath;

    @Builder.Default
    private final List<String> includes = List.of();

    @Builder.Default
    private final List<String> excludes = List.of();

    @Builder.Default
    private final Map<String, List<Pattern>> categories = Map.of();

    @Builder.Default
    private final Set<Constraint> constraints = Set.of();

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

    private Graph createGraph() {
        return new Analyzer().analyze(this.classpath, buildCategorizer(), buildFilter());
    }

    public List<Violation> check() {
        final var g = createGraph();
        final var allConstraints = Stream.concat(Stream.of(new CycleFree()), this.constraints.stream());
        return allConstraints.flatMap(c -> c.violations(g).stream())
                .sorted(Comparator.comparing(Violation::getSliceType).thenComparing(Violation::getName))
                .collect(Collectors.toList());
    }
}
