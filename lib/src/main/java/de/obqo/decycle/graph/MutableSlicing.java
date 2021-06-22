package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.Collection;

public interface MutableSlicing extends Slicing {

    static MutableSlicing create(final String sliceType) {
        return new DefaultMutableSlicing(sliceType);
    }

    static MutableSlicing create(final String sliceType, final Collection<Edge> edges) {
        final DefaultMutableSlicing slicing = new DefaultMutableSlicing(sliceType);
        edges.forEach(slicing::addEdge);
        return slicing;
    }

    void addNode(final Node node);

    void addEdge(final Edge edge);
}
