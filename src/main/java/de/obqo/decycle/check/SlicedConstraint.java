package de.obqo.decycle.check;

import static java.util.function.Predicate.not;

import de.obqo.decycle.graph.SlicingSource;
import de.obqo.decycle.model.Edge;
import de.obqo.decycle.model.Node;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

/**
 * A {@link SlicedConstraint} represents some dependency rules for the slices of a {@link
 * de.obqo.decycle.graph.Slicing}. Such constraint is defined by a list of {@link Layer layers}, each containing a set
 * of slices (i.e. a set of slice node names). The actual dependencies in a {@link de.obqo.decycle.graph.Slicing} must
 * match the dependencies defined by this constraint and its layers.
 */
@RequiredArgsConstructor
public abstract class SlicedConstraint implements Constraint {

    private final String sliceType;
    final List<Layer> layers;
    private final String arrow;

    abstract boolean isViolatedBy(Node n1, Node n2);

    int indexOf(final Node node) {
        for (int i = 0; i < this.layers.size(); i++) {
            if (this.layers.get(i).contains(node.getName())) {
                return i;
            }
        }
        return -1;
    }

    boolean constraintContainsBothNodes(final int i, final int j) {
        return i >= 0 && j >= 0;
    }

    @Override
    public List<Violation> violations(final SlicingSource slicingSource) {
        final var sg = slicingSource.slicing(this.sliceType);
        final var deps = sg.edges().stream()
                .filter(not(Edge::isIgnored))
                .filter(e -> isViolatedBy(e.getFrom(), e.getTo()))
                .map(Dependency::of)
                .collect(Collectors.toCollection(TreeSet::new));
        return deps.isEmpty() ? List.of() : List.of(new Violation(this.sliceType, getShortString(), deps));
    }

    @Override
    public String getShortString() {
        return this.layers.stream().map(this::layerToString).collect(Collectors.joining(this.arrow));
    }

    private String layerToString(final Layer layer) {
        final List<String> slices = layer.getSlices();
        if (slices.size() == 1) {
            return slices.get(0);
        }

        return layer.denyDependenciesWithinLayer()
                ? slices.stream().collect(Collectors.joining(", ", "[", "]"))
                : slices.stream().collect(Collectors.joining(", ", "(", ")"));
    }

    @Override
    public String toString() {
        return getShortString();
    }
}
