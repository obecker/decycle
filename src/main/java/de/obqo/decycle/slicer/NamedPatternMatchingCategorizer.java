package de.obqo.decycle.slicer;

import java.util.Optional;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;

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
    public Node apply(final Node node) {
        return Optional.of(node)
                       .filter(n -> n instanceof SimpleNode)
                       .flatMap(n -> this.matcher.matches(n.getName()))
                       .map(__ -> (Node) SimpleNode.simpleNode(this.targetType, this.name))
                       .orElse(node);
    }
}
