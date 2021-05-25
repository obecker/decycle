package de.obqo.decycle.gradle;

import java.io.Serializable;

/**
 * Serializable helper class representing a Decycle {@link de.obqo.decycle.configuration.NamedPattern}
 *
 * @author Oliver Becker
 */
class NamedPatternConfig implements Serializable {

    private static final long serialVersionUID = 10L;

    private final String name;
    private final String pattern;

    NamedPatternConfig(final String name, final String pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    String getName() {
        return this.name;
    }

    String getPattern() {
        return this.pattern;
    }
}
