package de.obqo.decycle.check;

import static de.obqo.decycle.graph.StronglyConnectedComponentsFinder.findComponents;

import de.obqo.decycle.graph.SliceSource;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CycleFree implements Constraint {

    @Override
    public String getShortString() {
        return "no cycles";
    }

    @Override
    public List<Violation> violations(final SliceSource sliceSource) {
        final List<Violation> list = new ArrayList<>();
        for (final String sliceType : sliceSource.slices()) {
            if (!Node.CLASS.equals(sliceType)) {
                for (final Set<Edge> comp : findComponents(sliceSource.slice(sliceType))) {
                    list.add(new Violation(sliceType, getShortString(),
                            comp.stream().map(Dependency::of).collect(Collectors.toCollection(TreeSet::new))));
                }
            }
        }
        return list;
    }
}
