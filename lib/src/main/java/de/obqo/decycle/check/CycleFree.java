package de.obqo.decycle.check;

import static de.obqo.decycle.graph.MutableSlicing.create;
import static de.obqo.decycle.graph.StronglyConnectedComponentsFinder.findComponents;

import de.obqo.decycle.graph.Slicing;
import de.obqo.decycle.graph.SlicingSource;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;

/**
 * The {@link CycleFree} {@link Constraint constraint} checks that there are no cyclic dependencies between the slices
 * of each {@link de.obqo.decycle.graph.Slicing} of {@link SlicingSource}.
 */
public class CycleFree implements Constraint {

    @Override
    public String getShortString() {
        return "no cycles";
    }

    @Override
    public List<Violation> violations(final SlicingSource slicingSource) {
        final List<Violation> list = new ArrayList<>();
        for (final String sliceType : slicingSource.sliceTypes()) {
            if (!Node.CLASS.equals(sliceType)) {
                for (final Set<Edge> comp : findComponents(slicingSource.slicing(sliceType))) {
                    final Slicing violatingSubgraph = create(sliceType, comp);
                    identifyViolations(violatingSubgraph);
                    list.add(new Violation(getShortString(), violatingSubgraph));
                }
            }
        }
        return list;
    }

    private void identifyViolations(final Slicing subgraph) {
        // Try to identify those edges that are the "wrong" ones, i.e. the violating edges.
        // This is done by finding the edge with the lowest weight in the subgraph, marking it as "violating" (which
        // removes it effectively from the cycle check), and running the findComponents() logic recursively on the
        // remaining subgraph until all cycles have disappeared.
        Preconditions.checkState(!subgraph.edges().isEmpty());
        subgraph.edges().stream()
                .min(Comparator.comparing(Edge::getWeight).thenComparing(Edge::displayString))
                .ifPresent(Edge::setViolating);
        findComponents(subgraph).forEach(edges -> identifyViolations(create(subgraph.getSliceType(), edges)));
    }
}
