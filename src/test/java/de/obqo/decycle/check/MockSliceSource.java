package de.obqo.decycle.check;

import static de.obqo.decycle.model.Node.sliceNode;

import de.obqo.decycle.check.Constraint.Dependency;
import de.obqo.decycle.graph.MutableSlice;
import de.obqo.decycle.graph.Slice;
import de.obqo.decycle.graph.SliceSource;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class MockSliceSource implements SliceSource {

    static Dependency d(final String from, final String to) {
        return new Dependency(from, to);
    }

    static List<Dependency> dependenciesIn(final List<Constraint.Violation> violations) {
        return violations.stream()
                .flatMap(v -> v.getDependencies().stream())
                .collect(Collectors.toList());
    }

    private final MutableSlice graph;

    MockSliceSource(final String sliceType, final Dependency... deps) {
        this.graph = MutableSlice.create(sliceType);
        for (final Dependency dep : deps) {
            final Node from = sliceNode(sliceType, dep.getFrom());
            final Node to = sliceNode(sliceType, dep.getTo());
            this.graph.addEdge(Edge.references(from, to));
        }
    }

    @Override
    public Set<String> sliceTypes() {
        return Set.of(this.graph.getSliceType());
    }

    @Override
    public Slice slice(final String sliceType) {
        return this.graph.getSliceType().equals(sliceType) ? this.graph : MutableSlice.create(sliceType);
    }
}
