package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link Slicing} is a special graph/network that contains only {@link Node nodes} that have the same {@link
 * Node#type} (see {@link #getSliceType()}). Its contained package or slice {@link #nodes() nodes} are connected among
 * each other by {@link Edge.EdgeLabel#REFERENCES referencing} {@link #edges() edges} that are derived from the {@link
 * Edge.EdgeLabel#REFERENCES referencing} edges of their {@link Edge.EdgeLabel#CONTAINS contained} class nodes in a
 * {@link Graph}.
 */
public interface Slicing {

    SliceType getSliceType();

    Set<Node> nodes();

    Set<Edge> edges();

    Set<Edge> outEdges(final Node node);

    Optional<Edge> edgeConnecting(final Node fromNode, final Node toNode);

    /**
     * @return the nodes of this slicing in topological order (which is undefined if the slicing has cycles)
     */
    List<Node> orderedNodes();
}
