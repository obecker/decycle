package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;

public class PatternMatchingFilter implements NodeFilter {

    private final PatternMatcher matcher;

    public PatternMatchingFilter(final String pattern) {
        this.matcher = new PatternMatcher(pattern);
    }

    @Override
    public boolean test(final Node node) {
        return !(node instanceof SimpleNode) || this.matcher.matches(node.getName()).isPresent();
    }
}
