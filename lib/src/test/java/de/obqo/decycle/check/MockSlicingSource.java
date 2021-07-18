package de.obqo.decycle.check;

import static de.obqo.decycle.model.Node.sliceNode;
import static de.obqo.decycle.model.SliceType.customType;

import de.obqo.decycle.graph.MutableSlicing;
import de.obqo.decycle.graph.Slicing;
import de.obqo.decycle.graph.SlicingSource;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.SliceType;

import java.util.Set;

class MockSlicingSource implements SlicingSource {

    private final MutableSlicing graph;

    MockSlicingSource(final String sliceType, final SimpleDependency... deps) {
        this.graph = MutableSlicing.create(customType(sliceType));
        for (final SimpleDependency dep : deps) {
            final Node from = sliceNode(sliceType, dep.getFrom());
            final Node to = sliceNode(sliceType, dep.getTo());
            this.graph.addEdge(Edge.references(from, to));
        }
    }

    @Override
    public Set<SliceType> sliceTypes() {
        return Set.of(this.graph.getSliceType());
    }

    @Override
    public Slicing slicing(final SliceType sliceType) {
        return this.graph.getSliceType().equals(sliceType) ? this.graph : MutableSlicing.create(sliceType);
    }
}
