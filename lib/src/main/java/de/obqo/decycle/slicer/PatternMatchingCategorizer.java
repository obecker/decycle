package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;

import java.util.Optional;
import java.util.Set;

/**
 * Categorizes class nodes by matching them against <em>unnamed patterns</em>.
 *
 * <p>
 * The category of a matched node is that part of the match that is wrapped in curly braces.
 * <p>
 * Example:
 * <code>de.{*}.test</code> matches <code>de.some.test</code> and creates a slice node with the name <code>some</code>
 *
 * @see de.obqo.decycle.slicer.PatternMatcher
 */
public class PatternMatchingCategorizer implements Categorizer {

    private final SliceType sliceType;
    private final PatternMatcher matcher;

    public PatternMatchingCategorizer(final String sliceType, final String pattern) {
        this.sliceType = SliceType.customType(sliceType);
        this.matcher = new PatternMatcher(pattern, true);
    }

    @Override
    public Set<Node> apply(final Node node) {
        return Optional.of(node)
                .flatMap(n -> this.matcher.matches(n.getName()))
                .map(match -> Node.sliceNode(this.sliceType, match))
                .map(Set::of)
                .orElse(Set.of());
    }
}
