package de.obqo.decycle.slicer;

import de.obqo.decycle.model.EdgeFilter;
import de.obqo.decycle.model.Node;

public class PatternMatchingEdgeFilter implements EdgeFilter {

    private final PatternMatcher fromMatcher;
    private final PatternMatcher toMatcher;

    public PatternMatchingEdgeFilter(final String fromPattern, final String toPattern) {
        this.fromMatcher = new PatternMatcher(fromPattern);
        this.toMatcher = new PatternMatcher(toPattern);
    }

    @Override
    public boolean test(final Node node1, final Node node2) {
        return this.fromMatcher.matches(node1.getName()).isPresent() &&
                this.toMatcher.matches(node2.getName()).isPresent();
    }
}
