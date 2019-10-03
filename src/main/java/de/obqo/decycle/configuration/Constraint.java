package de.obqo.decycle.configuration;

import de.obqo.decycle.graph.Edge;
import de.obqo.decycle.graph.SliceSource;

import java.util.List;
import java.util.Set;

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
