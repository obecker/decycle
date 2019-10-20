package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;

import java.util.Optional;
import java.util.Set;

public class NamedPatternMatchingCategorizer implements Categorizer {

    private final String targetType;
    private final PatternMatcher matcher;
    private final String name;

    public NamedPatternMatchingCategorizer(final String targetType, final String pattern, final String name) {
        this.targetType = targetType;
        this.matcher = new PatternMatcher(pattern);
        this.name = name;
    }

    @Override
    public Set<Node> apply(final Node node) {
        return Optional.of(node)
                .flatMap(n -> this.matcher.matches(n.getName()))
                .map(__ -> Node.sliceNode(this.targetType, this.name))
                .map(Set::of)
                .orElse(Set.of());
    }
}
