package de.obqo.decycle.gradle;

import java.io.Serializable;

/**
 * Serializable helper class representing a Decycle {@link de.obqo.decycle.slicer.IgnoredDependency}
 *
 * @author Oliver Becker
 */
public class IgnoreConfig implements Serializable {

    private static final long serialVersionUID = 10L;

    private final String from;
    private final String to;

    public IgnoreConfig(final String from, final String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }
}
