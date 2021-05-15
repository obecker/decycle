package de.obqo.decycle.check;

import java.util.List;

/**
 * A {@link Layer} represents a list of slices (names of slice {@link de.obqo.decycle.model.Node nodes}) that
 * will be used in a {@link SlicedConstraint}.
 */
public interface Layer {

    List<String> getSlices();

    boolean contains(String slice);

    boolean denyDependenciesWithinLayer();

    static Layer anyOf(final String... slices) {
        return new LenientLayer(slices);
    }

    static Layer oneOf(final String... slices) {
        return new StrictLayer(slices);
    }
}
