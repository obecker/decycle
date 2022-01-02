package de.obqo.decycle.gradle;

import java.io.Serializable;

/**
 * Configuration model for {@code allow} constraints
 *
 * @author Oliver Becker
 */
class AllowConfiguration implements Serializable {

    private static final long serialVersionUID = 10L;

    private final boolean direct;
    private final Object[] layers;

    AllowConfiguration(final boolean direct, final Object[] layers) {
        this.direct = direct;
        this.layers = layers;
    }

    boolean isDirect() {
        return this.direct;
    }

    Object[] getLayers() {
        return this.layers;
    }
}
