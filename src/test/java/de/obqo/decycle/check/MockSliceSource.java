package de.obqo.decycle.check;

import static de.obqo.decycle.model.Node.sliceNode;

import de.obqo.decycle.check.Constraint.Dependency;
import de.obqo.decycle.graph.Edge;
import de.obqo.decycle.graph.SliceSource;
import de.obqo.decycle.model.Node;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;

class MockSliceSource implements SliceSource {

    static Dependency d(final String from, final String to) {
        return new Dependency(from, to);
    }

    static List<Dependency> dependenciesIn(final List<Constraint.Violation> violations) {
        return violations.stream()
                .flatMap(v -> v.getDependencies().stream())
                .collect(Collectors.toList());
    }

    private final String slice;
    private final MutableNetwork<Node, Edge> graph;

    MockSliceSource(final String slice, final Dependency... deps) {
        this.slice = slice;

        this.graph = NetworkBuilder.directed().allowsSelfLoops(true).build();
        for (final Dependency dep : deps) {
            final Node from = sliceNode(slice, dep.getFrom());
            final Node to = sliceNode(slice, dep.getTo());
            this.graph.addEdge(from, to, Edge.references(from, to));
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
