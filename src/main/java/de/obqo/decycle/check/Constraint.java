package de.obqo.decycle.check;

import de.obqo.decycle.graph.Edge;
import de.obqo.decycle.graph.SliceSource;

import java.util.List;
import java.util.Set;

import lombok.Value;

public interface Constraint {

    @Value
    class Dependency {

        private String from;
        private String to;

        static Dependency of(final Edge edge) {
            return new Dependency(edge.getFrom().getName(), edge.getTo().getName());
        }

        public String toString() {
            return this.from + " -> " + this.to;
        }
    }

    @Value
    class Violation {

        private String sliceType;
        private String name;
        private Set<Dependency> dependencies;
    }

    String getShortString();

    List<Violation> violations(SliceSource sliceSource);
}
