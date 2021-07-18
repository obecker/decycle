package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;

import java.util.Optional;
import java.util.Set;

/**
 * Categorizes package nodes by matching them against ant like patterns.
 *
 * <ul>
 *     <li>* matches a node with any name not containing dots</li>
 *     <li>letters and dots match those letters and dots respectively</li>
 *     <li>** matches any combination of letters and dots</li>
 * </ul>
 * <p>
 * The category of a matched node is defined by {@link #name}.
 * <p>
 * Examples:
 * <ul>
 *     <li>de.**.test matches de..test, de.some.test and de.some.other.test</li>
 *     <li>de.*.test matches from the examples given above only de.some.test</li>
 * </ul>
 */
public class NamedPatternMatchingCategorizer implements Categorizer {

    private final SliceType sliceType;
    private final PatternMatcher matcher;
    private final String name;

    public NamedPatternMatchingCategorizer(final String sliceType, final String name, final String pattern) {
        this.sliceType = SliceType.customType(sliceType);
        this.name = name;
        this.matcher = new PatternMatcher(pattern);
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
