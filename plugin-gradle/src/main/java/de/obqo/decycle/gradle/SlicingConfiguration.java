package de.obqo.decycle.gradle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration model for {@code slicings}
 *
 * @author Oliver Becker
 */
class SlicingConfiguration implements Serializable {

    private static final long serialVersionUID = 10L;

    private final String sliceType;
    private final List<String> patterns;
    private final List<AllowConfiguration> allows;

    SlicingConfiguration(final String sliceType) {
        this.sliceType = sliceType;
        this.patterns = new ArrayList<>();
        this.allows = new ArrayList<>();
    }

    String getSliceType() {
        return this.sliceType;
    }

    List<String> getPatterns() {
        return this.patterns;
    }

    void addPattern(final String pattern) {
        this.patterns.add(pattern);
    }

    List<AllowConfiguration> getAllows() {
        return this.allows;
    }

    void addAllow(final boolean direct, final Object[] layers) {
        this.allows.add(new AllowConfiguration(direct, layers));
    }
}
