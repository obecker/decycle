package de.obqo.decycle.check;

import de.obqo.decycle.graph.SlicingSource;
import de.obqo.decycle.model.Edge;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import lombok.Value;

/**
 * A {@link Constraint} can check whether all {@link de.obqo.decycle.graph.Slicing slicings} of a {@link SlicingSource}
 * follow its rules. All detected rule violations can be retrieved with {@link #violations(SlicingSource)}.
 */
public interface Constraint {

    @Value
    class Dependency implements Comparable<Dependency> {

        private static Comparator<Dependency> COMPARATOR =
                Comparator.comparing(Dependency::getFrom).thenComparing(Dependency::getTo);

        private String from;
        private String to;

        static Dependency of(final Edge edge) {
            return new Dependency(edge.getFrom().getName(), edge.getTo().getName());
        }

        public String toString() {
            return this.from + " → " + this.to;
        }

        @Override
        public int compareTo(final Dependency other) {
            return COMPARATOR.compare(this, other);
        }
    }

    @Value
    class Violation {

        private String sliceType;
        private String name;
        private Set<Dependency> dependencies;
    }

    String getShortString();

    List<Violation> violations(SlicingSource slicingSource);
}
