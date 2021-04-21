package de.obqo.decycle.check;

import java.util.List;

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
