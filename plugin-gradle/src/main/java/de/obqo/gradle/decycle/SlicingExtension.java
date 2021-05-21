package de.obqo.gradle.decycle;

import org.gradle.api.GradleException;

/**
 * Extension class for the configuration of slicings
 *
 * @author Oliver Becker
 */
public class SlicingExtension {

    // a NamedDomainObjectContainer can only contain objects having a name property - the name will be used as sliceType
    private final String name;
    private final SlicingConfiguration configuration;

    SlicingExtension(final SlicingConfiguration configuration) {
        this.name = configuration.getSliceType();
        this.configuration = configuration;
    }

    public void patterns(Object... patterns) {
        for (Object pattern : patterns) {
            if (!(pattern instanceof String || pattern instanceof NamedPatternConfig)) {
                throw new GradleException(String.format(
                        "decycle: patterns must be strings or namedPattern(string), found '%s'",
                        pattern));
            }
            this.configuration.addPattern(pattern);
        }
    }

    public void allow(final Object... slices) {
        checkSlices("allow", slices);
        this.configuration.addAllow(false, slices);
    }

    public void allowDirect(final Object... slices) {
        checkSlices("allowDirect", slices);
        this.configuration.addAllow(true, slices);
    }

    private void checkSlices(final String prop, final Object[] slices) {
        for (Object slice : slices) {
            if (!(slice instanceof String || slice instanceof LayerConfig)) {
                throw new GradleException(String.format(
                        "decycle: slices after %s must be strings, oneOf(strings), or anyOf(strings), found '%s'",
                        prop,
                        slice));
            }
        }
    }

    public Object namedPattern(final String name, final String pattern) {
        if (name.contains("*") || name.contains(".")) {
            throw new GradleException(String.format("decycle: illegal pattern name '%s' - must contain neither * nor .", name));
        }
        return new NamedPatternConfig(name, pattern);
    }

    public Object oneOf(final String... slices) {
        return new LayerConfig(true, slices);
    }

    public Object anyOf(final String... slices) {
        return new LayerConfig(false, slices);
    }
}
