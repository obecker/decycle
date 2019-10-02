package de.obqo.decycle.graph;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.google.common.graph.Network;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.ParentAwareNode;
import de.obqo.decycle.model.SimpleNode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SliceNodeFinder implements Function<Node, SimpleNode> {

    private final String slice;
    private final Network<Node, Edge> graph;

    private boolean contains(final ParentAwareNode pan) {
        return pan.getVals().stream().anyMatch(n ->
                n instanceof SimpleNode && n.getTypes().contains(this.slice));
    }

    private SimpleNode findIn(final ParentAwareNode pan) {
        return pan.getVals().stream()
                  .filter(n -> n instanceof SimpleNode && n.getTypes().contains(this.slice))
                  .map(n -> (SimpleNode) n)
                  .findFirst().get();
    }

    private Set<Edge> inEdges(final Node node) {
        return this.graph.nodes().contains(node) ? this.graph.inEdges(node) : Set.of();
    }

    private Optional<Node> container(final Node n) {
        return inEdges(n).stream()
                         .filter(e -> e.getLabel() == Edge.EdgeLabel.CONTAINS)
                         .map(Edge::getFrom)
                         .findFirst();
    }

    public boolean isDefinedAt(final Node n) {
        if (n instanceof SimpleNode && n.getTypes().contains(this.slice)) {
            return true;
        }
        if (n instanceof ParentAwareNode && contains((ParentAwareNode) n)) {
            return true;
        }
        return container(n).map(this::isDefinedAt).orElse(false);
    }


    @Override
    public SimpleNode apply(final Node node) {
        if (node instanceof SimpleNode && node.getTypes().contains(this.slice)) {
            return (SimpleNode) node;
        }
        if (node instanceof ParentAwareNode) {
            return findIn((ParentAwareNode) node);
        }
        return apply(container(node).get());
    }

    public Optional<SimpleNode> lift(final Node node) {
        return isDefinedAt(node) ? Optional.of(apply(node)) : Optional.empty();
    }
}
