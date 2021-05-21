package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.NodeFilter;

public class PatternMatchingNodeFilter implements NodeFilter {

    private final PatternMatcher matcher;

    public PatternMatchingNodeFilter(final String pattern) {
        this.matcher = new PatternMatcher(pattern);
    }

    @Override
    public boolean test(final Node node) {
        return this.matcher.matches(node.getName()).isPresent();
    }
}
