package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;

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
 * The category of a matched node it that part of the match that is wrapped in parenthesis.
 * <p>
 * If no parenthesis are give the full name is returned as a category.
 * <p>
 * Examples:
 * <ul>
 *     <li>de.**.test matches de..test, de.some.test and de.some.other.test</li>
 *     <li>de.*.test matches from the examples given above only de.some.test</li>
 *     <li>de.(*.test) categorizes de.some.test as 'some.test'</li>
 *     <li>de.(*).test categorizes it as 'some'</li>
 * </ul>
 */
public class PatternMatchingCategorizer implements Categorizer {

    private final String targetType;
    private final PatternMatcher matcher;

    public PatternMatchingCategorizer(final String targetType, final String pattern) {
        this.targetType = targetType;
        this.matcher = new PatternMatcher(pattern);
    }

    @Override
    public Set<Node> apply(final Node node) {
        return Optional.of(node)
                .flatMap(n -> this.matcher.matches(n.getName()))
                .map(match -> Node.sliceNode(this.targetType, match))
                .map(Set::of)
                .orElse(Set.of());
    }
}
