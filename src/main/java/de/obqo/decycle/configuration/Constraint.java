package de.obqo.decycle.configuration;

import java.util.List;
import java.util.Set;

import de.obqo.decycle.graph.Edge;
import de.obqo.decycle.graph.SliceSource;
import lombok.Value;

public interface Constraint {

    @Value
    class Violation {
        private String sliceType;
        private String name;
        private Set<Edge> dependencies;
    }

    String getShortString();

    List<Violation> violations(SliceSource sliceSource);
}
