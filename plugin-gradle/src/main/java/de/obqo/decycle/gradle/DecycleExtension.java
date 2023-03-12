package de.obqo.decycle.gradle;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

import kotlin.Pair;

/**
 * Extension class for the configuration of decycle
 *
 * @author Oliver Becker
 */
public class DecycleExtension {

    private final NamedDomainObjectContainer<SlicingExtension> slicings;
    private final DecycleConfiguration configuration;

    public DecycleExtension(final Project project, final DecycleConfiguration configuration) {
        this.slicings = project.container(
                SlicingExtension.class,
                sliceType -> new SlicingExtension(project, configuration.addSlicing(sliceType)));
        this.configuration = configuration;
    }

    /**
     * Declare the source sets to checked by decycle. Will be used by the Groovy Gradle DSL.
     *
     * @param sourceSets the source sets to be checked
     */
    public void sourceSets(final SourceSet... sourceSets) {
        for (final SourceSet sourceSet : sourceSets) {
            this.configuration.addSourceSet(sourceSet);
        }
    }

    /**
     * Declare the source sets to checked by decycle. Will be used by the Kotlin Gradle DSL.
     *
     * @param sourceSets the providers for the source sets to be checked
     */
    public void sourceSets(final NamedDomainObjectProvider<?>... sourceSets) {
        // don't use NamedDomainObjectProvider<SourceSet> as the compiler can't check the generic type anyway
        for (final NamedDomainObjectProvider<?> sourceSet : sourceSets) {
            final Object value = sourceSet.get();
            if (!(value instanceof SourceSet)) {
                throw new GradleException(String.format(
                        "decycle: passed value to sourceSets is not a SourceSet, encountered %s",
                        value));
            }
            this.configuration.addSourceSet((SourceSet) value);
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

    @Deprecated
    public void ignoring(final String... ignoreSpec) {
        throw new GradleException(String.format(
                "decycle: ignoring must be used with from: and to: values, found %s",
                String.join(", ", ignoreSpec)));
    }

    /**
     * Add a dependency that should be ignored. Preferably to be used by the Groovy Gradle DSL:
     * <pre>
     *     ignoring from: '...', to: '...'
     * </pre>
     *
     * @param ignoreSpec map containing only "from" and "to" keys for the dependency
     * @see de.obqo.decycle.slicer.IgnoredDependency#create(String, String)
     */
    public void ignoring(final Map<String, String> ignoreSpec) {
        final Set<String> ignoreKeys = Set.of("from", "to");
        if (!ignoreKeys.containsAll(ignoreSpec.keySet())) {
            throw new GradleException(String.format(
                    "decycle: ignoring must only have from: and to: values, found %s",
                    ignoreSpec.keySet().stream().filter(key -> !ignoreKeys.contains(key))
                            .map(key -> key + ":")
                            .sorted()
                            .collect(Collectors.joining(", "))));
        }
        this.configuration.addIgnoredDep(new IgnoreConfig(ignoreSpec.get("from"), ignoreSpec.get("to")));
    }

    /**
     * Add a dependency that should be ignored. Preferably to be used by the Kotlin Gradle DSL:
     * <pre>
     *     ignoring("..." to "...")
     * </pre>
     *
     * @param ignoreSpec a pair representing the dependency
     * @see de.obqo.decycle.slicer.IgnoredDependency#create(String, String)
     */
    public void ignoring(final Pair<String, String> ignoreSpec) {
        this.configuration.addIgnoredDep(new IgnoreConfig(ignoreSpec.getFirst(), ignoreSpec.getSecond()));
    }

    public void slicings(final Action<NamedDomainObjectContainer<SlicingExtension>> action) {
        action.execute(this.slicings);
    }

    public void ignoreFailures(final boolean ignoreFailures) {
        this.configuration.setIgnoreFailures(ignoreFailures);
    }

    public void reportsEnabled(final boolean reportsEnabled) {
        this.configuration.setReportsEnabled(reportsEnabled);
    }
}
