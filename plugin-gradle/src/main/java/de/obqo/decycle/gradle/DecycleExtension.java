package de.obqo.decycle.gradle;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.SourceSet;

/**
 * Extension class for the configuration of decycle
 *
 * @author Oliver Becker
 */
public class DecycleExtension {

    private final NamedDomainObjectContainer<SlicingExtension> slicings;
    private final DecycleConfiguration configuration;
    private final Logger logger;

    public DecycleExtension(final Project project, final DecycleConfiguration configuration) {
        this.slicings = project.container(
                SlicingExtension.class,
                sliceType -> new SlicingExtension(configuration.addSlicing(sliceType)));
        this.configuration = configuration;
        this.logger = project.getLogger();
    }

    public void sourceSets(final SourceSet... sourceSets) {
        for (final SourceSet sourceSet : sourceSets) {
            this.configuration.addSourceSet(sourceSet);
        }
    }

    public void including(final String... includings) {
        for (final String including : includings) {
            this.configuration.addIncluding(including);
        }
    }

    public void excluding(final String... excludings) {
        for (final String excluding : excludings) {
            this.configuration.addExcluding(excluding);
        }
    }

    @Deprecated(forRemoval = true, since = "0.3.0")
    public void ignore(final Map<String, String> ignoreSpec) {
        this.logger.warn("Decycle: Configuration 'ignore ...' has been deprecated, please use 'ignoring ...' instead");
        ignoring(ignoreSpec);
    }

    public void ignoring(final String... ignoreSpec) {
        throw new GradleException(String.format(
                "decycle: ignore must be used with from: and to: values, found %s",
                String.join(", ", ignoreSpec)));
    }

    public void ignoring(final Map<String, String> ignoreSpec) {
        final Set<String> ignoreKeys = Set.of("from", "to");
        if (!ignoreKeys.containsAll(ignoreSpec.keySet())) {
            throw new GradleException(String.format(
                    "decycle: ignore must only have from: and to: values, found %s",
                    ignoreSpec.keySet().stream().filter(key -> !ignoreKeys.contains(key))
                            .map(key -> key + ":")
                            .sorted()
                            .collect(Collectors.joining(", "))));
        }
        this.configuration.addIgnoredDep(
                new IgnoreConfig(ignoreSpec.getOrDefault("from", "**"), ignoreSpec.getOrDefault("to", "**")));
    }

    public void slicings(final Action<NamedDomainObjectContainer<SlicingExtension>> action) {
        action.execute(this.slicings);
    }
}
