package de.obqo.decycle.gradle;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

/**
 * Extension class for the configuration of slicings
 *
 * @author Oliver Becker
 */
public class SlicingExtension {

    // a NamedDomainObjectContainer can only contain objects having a name property - the name will be used as sliceType
    private final String name;
    private final SlicingConfiguration configuration;
    private final Logger logger;

    SlicingExtension(final Project project, final SlicingConfiguration configuration) {
        this.name = configuration.getSliceType();
        this.configuration = configuration;
        this.logger = project.getLogger();
    }

    public void patterns(String... patterns) {
        for (final String pattern : patterns) {
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
        for (final Object slice : slices) {
            if (!(slice instanceof String || slice instanceof LayerConfig)) {
                throw new GradleException(String.format(
                        "decycle: slices after %s must be strings, oneOf(strings), or anyOf(strings), found '%s'",
                        prop,
                        slice));
            }
        }
    }

    @Deprecated(forRemoval = true, since = "0.7.0")
    public String namedPattern(final String name, final String pattern) {
        this.logger.warn("namedPattern(name, pattern) is deprecated, use the string '{}={}' instead", pattern, name);
        if (name.contains("*") || name.contains(".")) {
            throw new GradleException(String.format("decycle: illegal pattern name '%s' - must contain neither * nor .", name));
        }
        return pattern + '=' + name;
    }

    public Object oneOf(final String... slices) {
        return new LayerConfig(true, slices);
    }

    public Object anyOf(final String... slices) {
        return new LayerConfig(false, slices);
    }
}
