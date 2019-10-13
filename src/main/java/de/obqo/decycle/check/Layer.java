package de.obqo.decycle.check;

import java.util.List;

public interface Layer {

    List<String> getSlices();

    boolean contains(String elem);

    boolean denyDependenciesWithinLayer();

    static Layer anyOf(final String... es) {
        return new LenientLayer(es);
    }

    static Layer oneOf(final String... es) {
        return new StrictLayer(es);
    }
}
