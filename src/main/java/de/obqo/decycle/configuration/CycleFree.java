package de.obqo.decycle.configuration;

import static de.obqo.decycle.graph.StronglyConnectedComponentsFinder.findComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.obqo.decycle.graph.Edge;
import de.obqo.decycle.graph.SliceSource;
import de.obqo.decycle.model.SimpleNode;

public class CycleFree implements Constraint {

    @Override
    public String getShortString() {
        return "no cycles";
    }

    @Override
    public List<Violation> violations(final SliceSource sliceSource) {
        final List<Violation> list = new ArrayList<>();
        for (final String sliceType : sliceSource.slices()) {
            if (!SimpleNode.CLASS.equals(sliceType)) {
                for (final Set<Edge> comp : findComponents(sliceSource.slice(sliceType))) {
                    list.add(new Violation(sliceType, getShortString(), comp));
                }
            }
        }
        return list;
    }
}
