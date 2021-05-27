package de.obqo.decycle.slicer;

import lombok.Value;

/**
 * Represents a dependency between two classes that should be ignored when checking the defined constraints.
 */
@Value
public class IgnoredDependency {

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
