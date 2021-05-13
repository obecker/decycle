package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

public interface MutableSlicing extends Slicing {

    static MutableSlicing create(final String sliceType) {
        return new DefaultMutableSlicing(sliceType);
    }

    void addNode(final Node node);

    void addEdge(final Edge edge);
}
