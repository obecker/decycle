package de.obqo.decycle.slicer;

import static java.util.function.Predicate.not;

import java.util.Optional;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a dependency between two classes that should be ignored when checking the defined constraints.
 */
@Value
@Slf4j
public class IgnoredDependency {

    private static final String MATCH_ALL = "**";

    /**
     * Creates a new {@code IgnoredDependency} by adjusting the given patterns. Patterns will be trimmed and a {@code
     * null} or blank pattern will be turned into the <em>match all</em> pattern {@code "**"}.
     *
     * @param fromPattern the pattern for the source of the dependency (or {@code null} for any)
     * @param toPattern   the pattern for the target of the dependency (or {@code null} for any)
     * @return a new {@code IgnoredDependency}
     */
    public static IgnoredDependency create(final String fromPattern, final String toPattern) {
        final String adjustedFrom = adjustPattern(fromPattern);
        final String adjustedTo = adjustPattern(toPattern);
        final IgnoredDependency dependency = new IgnoredDependency(adjustedFrom, adjustedTo);
        if (MATCH_ALL.equals(adjustedFrom) && MATCH_ALL.equals(adjustedTo)) {
            log.warn("Ignoring all dependencies ({}), is this really intended?", dependency);
        }
        return dependency;
    }

    private static String adjustPattern(final String pattern) {
        return Optional.ofNullable(pattern).map(String::trim).filter(not(String::isEmpty)).orElse(MATCH_ALL);
    }

    /**
     * The pattern for the source of the dependency - see {@link PatternMatchingNodeFilter}
     *
     * @return the pattern
     */
    // @return is used for the lombok generated getter
    String fromPattern;

    /**
     * The pattern for the target of the dependency - see {@link PatternMatchingNodeFilter}
     *
     * @return the pattern
     */
    // @return is used for the lombok generated getter
    String toPattern;

    @Override
    public String toString() {
        return this.fromPattern + " → " + this.toPattern;
    }
}
