package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;

import java.util.Optional;
import java.util.Set;

/**
 * Categorizes class nodes by matching them against <em>named patterns</em>.
 *
 * <p>
 * The category of a matched node is defined by {@link #name}.
 * <p>
 * Example:
 * <code>de.*.test=foo</code> matches <code>de.some.test</code> and creates a slice node with the name <code>foo</code>
 *
 * @see PatternMatcher
 */
public class NamedPatternMatchingCategorizer implements Categorizer {

    private final SliceType sliceType;
    private final PatternMatcher matcher;
    private final String name;

    public NamedPatternMatchingCategorizer(final String sliceType, final String pattern, final String name) {
        this.sliceType = SliceType.customType(sliceType);
        this.matcher = new PatternMatcher(pattern);
        this.name = name;
    }

    @Override
    public Set<Node> apply(final Node node) {
        return Optional.of(node)
                .flatMap(n -> this.matcher.matches(n.getName()))
                .map(__ -> Node.sliceNode(this.sliceType, this.name))
                .map(Set::of)
                .orElse(Set.of());
    }
}
