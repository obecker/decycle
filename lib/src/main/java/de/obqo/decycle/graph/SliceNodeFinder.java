package de.obqo.decycle.graph;

import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.graph.Network;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class SliceNodeFinder {

    private final SliceType sliceType;
    private final Network<Node, Edge> internalGraph;

    Optional<Node> find(final Node node) {
        return allInNodesOfType(node).findFirst();
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

    private Set<Edge> inEdges(final Node node) {
        return this.internalGraph.nodes().contains(node) ? this.internalGraph.inEdges(node) : Set.of();
    }
}
