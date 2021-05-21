package de.obqo.gradle.decycle;

import java.io.Serializable;

/**
 * Serializable helper class representing a Decycle {@link de.obqo.decycle.check.Layer}
 * 
 * @author Oliver Becker
 */
class LayerConfig implements Serializable {

    private static final long serialVersionUID = 10L;

    private final boolean strict;
    private final String[] slices;

    LayerConfig(final boolean strict, final String[] slices) {
        this.strict = strict;
        this.slices = slices;
    }

    boolean isStrict() {
        return this.strict;
    }

    String[] getSlices() {
        return this.slices;
    }
}
