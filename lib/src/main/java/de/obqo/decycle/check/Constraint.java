package de.obqo.decycle.check;

import static java.util.stream.Collectors.joining;

import de.obqo.decycle.graph.Slicing;
import de.obqo.decycle.graph.SlicingSource;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.SliceType;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lombok.Value;

/**
 * A {@link Constraint} can check whether all {@link de.obqo.decycle.graph.Slicing slicings} of a {@link SlicingSource}
 * follow its rules. All detected rule violations can be retrieved with {@link #violations(SlicingSource)}.
 */
public interface Constraint {

    @Value
    class Violation {

        String name;
        Slicing violatingSubgraph;

        public SliceType getSliceType() {
            return this.violatingSubgraph.getSliceType();
        }

        public Set<Edge> getDependencies() {
            return new TreeSet<>(this.violatingSubgraph.edges());
        }

        public String displayString() {
            return String.format(
                    "Violation(slicing=%s, name=%s, dependencies=[%s])",
                    getSliceType().displayString(),
                    this.name,
                    getDependencies().stream().map(Edge::displayString).collect(joining(", "))
            );
        }
    }

    String getShortString();

    List<Violation> violations(SlicingSource slicingSource);
}
