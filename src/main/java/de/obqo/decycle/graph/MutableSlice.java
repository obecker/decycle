package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

public interface MutableSlice extends Slice {

    static MutableSlice create(final String sliceType) {
        return new DefaultMutableSlice(sliceType);
    }

    void addNode(final Node node);

    void addEdge(final Edge edge);
}
