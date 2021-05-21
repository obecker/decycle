package de.obqo.gradle.decycle;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

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
                sliceType -> new SlicingExtension(configuration.addSlicing(sliceType)));
        this.configuration = configuration;
    }

    public void sourceSets(final SourceSet... sourceSets) {
        for (SourceSet sourceSet : sourceSets) {
            this.configuration.addSourceSet(sourceSet);
        }
    }

    public void including(final String... includings) {
        for (String including : includings) {
            this.configuration.addIncluding(including);
        }
    }

    public void excluding(final String... excludings) {
        for (String excluding : excludings) {
            this.configuration.addExcluding(excluding);
        }
    }

    public void ignore(final String... ignore) {
        if (ignore.length % 2 != 0) {
            throw new GradleException(String.format(
                    "decycle: ignore list must consist of string pairs, found %s values",
                    ignore.length));
        }
        for (int i = 0; i < ignore.length; i += 2) {
            this.configuration.addIgnoredDep(List.of(ignore[i], ignore[i + 1]));
        }
    }

    public void ignore(final Map<String, String> args) {
        if (!Set.of("from", "to").equals(args.keySet())) {
            throw new GradleException(String.format(
                    "decycle: ignore must have from: and to: values, found %s",
                    args));
        }
        ignore(args.get("from"), args.get("to"));
    }

    public void slicings(final Action<NamedDomainObjectContainer<SlicingExtension>> action) {
        action.execute(this.slicings);
    }
}
