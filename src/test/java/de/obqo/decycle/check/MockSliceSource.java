package de.obqo.decycle.check;

import static de.obqo.decycle.model.SimpleNode.simpleNode;

import de.obqo.decycle.graph.Edge;
import de.obqo.decycle.graph.SliceSource;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SimpleNode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;

import lombok.Value;

class MockSliceSource implements SliceSource {

    @Value
    static class Dependency<T> {

        private final T from;
        private final T to;
    }

    public static <T> Dependency<T> d(final T from, final T to) {
        return new Dependency<>(from, to);
    }

    public static List<Dependency> dependenciesIn(final List<Constraint.Violation> violations) {
        return violations.stream().flatMap(v -> v.getDependencies().stream()).map(e -> d(e.getFrom(), e.getTo()))
                .collect(
                        Collectors.toList());
    }

    private final String slice;
    private final MutableNetwork<Node, Edge> graph;

    public MockSliceSource(final String slice, final Dependency<String>... deps) {
        this.slice = slice;

        this.graph = NetworkBuilder.directed().allowsSelfLoops(true).build();
        for (final Dependency<String> dep : deps) {
            final SimpleNode from = simpleNode(dep.getFrom(), slice);
            final SimpleNode to = simpleNode(dep.getTo(), slice);
            this.graph.addEdge(from, to, new Edge(from, to, Edge.EdgeLabel.REFERENCES));
        }
    }

    @Override
    public Set<String> slices() {
        return Set.of(this.slice);
    }

    @Override
    public Network<Node, Edge> slice(final String name) {
        return this.slice.equals(name) ? this.graph : NetworkBuilder.directed().build();
    }
}
