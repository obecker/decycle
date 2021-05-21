package de.obqo.decycle.slicer;

import de.obqo.decycle.model.EdgeFilter;
import de.obqo.decycle.model.Node;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IgnoredDependenciesFilter implements EdgeFilter {

    private final Set<EdgeFilter> filters;

    public IgnoredDependenciesFilter(final Collection<IgnoredDependency> ignoredDependencies) {
        this.filters = Optional.ofNullable(ignoredDependencies).stream()
                .flatMap(Collection::stream)
                .map(this::toFilter)
                .collect(Collectors.toSet());
    }

    private PatternMatchingEdgeFilter toFilter(final IgnoredDependency dep) {
        return new PatternMatchingEdgeFilter(dep.getFromPattern(), dep.getToPattern());
    }

    @Override
    public boolean test(final Node node1, final Node node2) {
        return this.filters.stream().anyMatch(filter -> filter.test(node1, node2));
    }
}
