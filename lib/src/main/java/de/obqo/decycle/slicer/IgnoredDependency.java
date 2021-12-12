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

    public IgnoredDependency(final String fromPattern, final String toPattern) {
        this.fromPattern = adjustPattern(fromPattern);
        this.toPattern = adjustPattern(toPattern);
        validate();
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

    private void validate() {
        if (MATCH_ALL.equals(this.fromPattern) && MATCH_ALL.equals(this.toPattern)) {
            log.warn("Ignoring all dependencies ({}), is this really intended?", this);
        }
    }

    @Override
    public String toString() {
        return this.fromPattern + " → " + this.toPattern;
    }
}
