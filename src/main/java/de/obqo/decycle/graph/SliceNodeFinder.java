package de.obqo.decycle.graph;

import de.obqo.decycle.model.Node;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.graph.Network;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SliceNodeFinder {

    private final String sliceType;
    private final Network<Node, Edge> graph;

    public Optional<Node> find(final Node node) {
        return allInNodesOfType(node).findFirst();
    }

    private Set<Edge> inEdges(final Node node) {
        return this.graph.nodes().contains(node) ? this.graph.inEdges(node) : Set.of();
    }

    private Stream<Node> allInNodesOfType(final Node node) {
        if (node.hasType(this.sliceType)) {
            return Stream.of(node);
        } else {
            return inEdges(node).stream()
                    .filter(Edge::isContaining)
                    .map(Edge::getFrom)
                    .flatMap(this::allInNodesOfType);
        }
    }
}
