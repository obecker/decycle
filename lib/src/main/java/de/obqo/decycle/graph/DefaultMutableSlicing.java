package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class DefaultMutableSlicing implements MutableSlicing {

    @Getter
    private final SliceType sliceType;

    private final MutableNetwork<Node, Edge> network = NetworkBuilder.directed().build();

    @Override
    public void addNode(final Node node) {
        this.network.addNode(node);
    }

    @Override
    public void addEdge(final Edge edge) {
        this.network.addEdge(edge.getFrom(), edge.getTo(), edge);
    }

    @Override
    public Set<Node> nodes() {
        return this.network.nodes();
    }

    @Override
    public Set<Edge> edges() {
        return this.network.edges();
    }

    @Override
    public Set<Edge> outEdges(final Node node) {
        return this.network.outEdges(node);
    }

    @Override
    public Optional<Edge> edgeConnecting(final Node fromNode, final Node toNode) {
        return this.network.edgeConnecting(fromNode, toNode);
    }

    @Override
    public List<Node> orderedNodes() {
        return Topological.order(this);
    }
}
