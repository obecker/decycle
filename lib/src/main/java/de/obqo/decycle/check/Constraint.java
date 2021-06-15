package de.obqo.decycle.check;

import static java.util.stream.Collectors.joining;

import de.obqo.decycle.graph.SlicingSource;
import de.obqo.decycle.model.Edge;

import java.util.List;
import java.util.Set;

import lombok.Value;

/**
 * A {@link Constraint} can check whether all {@link de.obqo.decycle.graph.Slicing slicings} of a {@link SlicingSource}
 * follow its rules. All detected rule violations can be retrieved with {@link #violations(SlicingSource)}.
 */
public interface Constraint {

    @Value
    class Violation {

        private String sliceType;
        private String name;
        private Set<Edge> dependencies;

        public String displayString() {
            return String.format(
                    "Violation(slicing=%s, name=%s, dependencies=[%s])",
                    this.sliceType,
                    this.name,
                    this.dependencies.stream().map(Edge::displayString).collect(joining(", ")));
        }
    }

    String getShortString();

    List<Violation> violations(SlicingSource slicingSource);
}
