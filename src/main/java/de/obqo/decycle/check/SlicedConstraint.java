package de.obqo.decycle.check;

import de.obqo.decycle.graph.SliceSource;
import de.obqo.decycle.model.Node;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SlicedConstraint implements Constraint {

    private final String sliceType;
    final List<Layer> slices;
    private final String arrow;

    SlicedConstraint(final String sliceType, final List<Layer> slices, final String arrow) {
        this.sliceType = sliceType;
        this.slices = slices;
        this.arrow = arrow;
    }

    abstract boolean isViolatedBy(Node n1, Node n2);

    int indexOf(final Node node) {
        for (int i = 0; i < this.slices.size(); i++) {
            if (this.slices.get(i).contains(node.getName())) {
                return i;
            }
        }
        return -1;
    }

    boolean constraintContainsBothNodes(final int i, final int j) {
        return i >= 0 && j >= 0;
    }

    @Override
    public List<Violation> violations(final SliceSource sliceSource) {
        final var sg = sliceSource.slice(this.sliceType);
        final var deps = sg.edges().stream()
                .filter(e -> isViolatedBy(e.getFrom(), e.getTo()))
                .map(Dependency::of)
                .collect(Collectors.toSet());
        return deps.isEmpty() ? List.of() : List.of(new Violation(this.sliceType, getShortString(), deps));
    }

    @Override
    public String getShortString() {
        return mkString(this.slices.stream()
                .map(l -> l.denyDependenciesWithinLayer()
                        ? layersToString(l.getSlices(), "[", "]")
                        : layersToString(l.getSlices(), "(", ")"))
                .collect(Collectors.toList()), "", " " + this.arrow + " ", "");
    }

    private String layersToString(final Collection<String> ls, final String start, final String end) {
        if (ls.size() == 1) {
            return ls.iterator().next();
        } else {
            return mkString(ls, start, ", ", end);
        }
    }

    private String mkString(final Collection<String> strings, final String start, final String delimiter,
            final String end) {
        final StringBuilder builder = new StringBuilder(start);
        boolean added = false;
        for (final String l : strings) {
            if (added) {
                builder.append(delimiter);
            }
            builder.append(l);
            added = true;
        }
        builder.append(end);
        return builder.toString();
    }
}
