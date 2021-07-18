package de.obqo.decycle.check;

import static de.obqo.decycle.graph.MutableSlicing.create;
import static de.obqo.decycle.graph.StronglyConnectedComponentsFinder.findComponents;

import de.obqo.decycle.graph.Slicing;
import de.obqo.decycle.graph.SlicingSource;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.SliceType;

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
        return "cycle";
    }

    @Override
    public List<Violation> violations(final SlicingSource slicingSource) {
        final List<Slicing> violatingSubgraphs = new ArrayList<>();
        for (final SliceType sliceType : slicingSource.sliceTypes()) {
            if (!sliceType.isClassType()) {
                for (final Set<Edge> comp : findComponents(slicingSource.slicing(sliceType))) {
                    final Slicing violatingSubgraph = create(sliceType, comp);
                    identifyViolations(violatingSubgraph);
                    violatingSubgraphs.add(violatingSubgraph);
                }
            }
        }

        final List<Violation> violations = new ArrayList<>();
        final boolean multiple = violatingSubgraphs.size() > 1;
        for (int i = 0; i < violatingSubgraphs.size(); i++) {
            violations.add(new Violation(
                    multiple ? String.format("%s (%d)", getShortString(), i + 1) : getShortString(),
                    violatingSubgraphs.get(i)));
        }
        return violations;
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
