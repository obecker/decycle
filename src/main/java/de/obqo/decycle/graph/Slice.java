package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.Optional;
import java.util.Set;

public interface Slice {

    String getSliceType();

    Set<Node> nodes();

    Set<Edge> edges();

    Set<Edge> outEdges(final Node node);

    Optional<Edge> edgeConnecting(final Node fromNode, final Node toNode);
}
