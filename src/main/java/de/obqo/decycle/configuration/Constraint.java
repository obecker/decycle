package de.obqo.decycle.configuration;

import java.util.List;

import de.obqo.decycle.graph.SliceSource;
import de.obqo.decycle.model.Node;
import lombok.Value;

public interface Constraint {

    @Value
    class NodePair {
        private Node node1;
        private Node node2;
    }

    @Value
    class Violation {
        private String sliceType;
        private String name;
        private List<NodePair> dependencies;
    }

    String getShortString();

    List<Violation> violations(SliceSource sliceSource);
}
