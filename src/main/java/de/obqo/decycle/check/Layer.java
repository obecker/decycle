package de.obqo.decycle.check;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link Layer} represents a set of slices (names of slice {@link de.obqo.decycle.model.Node nodes}) that will be
 * used in a {@link SlicedConstraint}.
 */
public class Layer {

    /**
     * Constructs a {@link Layer} without further constraints on its slices.
     */
    public static Layer anyOf(final String... slices) {
        return new Layer(false, slices);
    }

    /**
     * Constructs a {@link Layer} whose slices must not depend on each other.
     */
    public static Layer oneOf(final String... slices) {
        return new Layer(true, slices);
    }

    private final boolean denyDependenciesWithinLayer;
    private final Set<String> slices;

    private Layer(final boolean denyDependenciesWithinLayer, final String... slices) {
        this.denyDependenciesWithinLayer = denyDependenciesWithinLayer;
        this.slices = new LinkedHashSet<>(List.of(slices)); // keep the given order for getShortString()
    }

    public boolean contains(final String slice) {
        return this.slices.contains(slice);
    }

    public boolean denyDependenciesWithinLayer() {
        return this.denyDependenciesWithinLayer;
    }

    public String getShortString() {
        if (this.slices.size() == 1) {
            return this.slices.iterator().next();
        }

        return this.denyDependenciesWithinLayer
                ? this.slices.stream().collect(Collectors.joining(", ", "[", "]"))
                : this.slices.stream().collect(Collectors.joining(", ", "(", ")"));
    }

    @Override
    public String toString() {
        return getShortString();
    }
}
