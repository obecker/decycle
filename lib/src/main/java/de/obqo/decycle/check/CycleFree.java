package de.obqo.decycle.check;

import static de.obqo.decycle.graph.StronglyConnectedComponentsFinder.findComponents;

import de.obqo.decycle.graph.SlicingSource;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
                    list.add(new Violation(sliceType, getShortString(),
                            comp.stream().map(Dependency::of).collect(Collectors.toCollection(TreeSet::new))));
                }
            }
        }
        return list;
    }
}
